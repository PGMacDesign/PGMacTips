package com.pgmacdesign.pgmacutilities.enhancedphotoclasses;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.ExifInterface;
import android.media.Image;
import android.media.ImageReader;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.provider.MediaStore;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ListViewCompat;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.pgmacdesign.pgmacutilities.R;
import com.pgmacdesign.pgmacutilities.adaptersandlisteners.OnTaskCompleteListener;
import com.pgmacdesign.pgmacutilities.nonutilities.PGMacUtilitiesConstants;
import com.pgmacdesign.pgmacutilities.utilities.AnimationUtilities;
import com.pgmacdesign.pgmacutilities.utilities.DateUtilities;
import com.pgmacdesign.pgmacutilities.utilities.DisplayManagerUtilities;
import com.pgmacdesign.pgmacutilities.utilities.L;
import com.pgmacdesign.pgmacutilities.utilities.ProgressBarUtilities;
import com.pgmacdesign.pgmacutilities.utilities.StringUtilities;
import com.pgmacdesign.pgmacutilities.utilities.SystemUtilities;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

/**
 * todo this is currently under construction and will throw null pointers when attempting to run
 * Created by pmacdowell on 8/31/2016.
 */
@Deprecated
public class AutoPhotoActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "AutoPhotoActivity";

    private AutoPhotoOptions options;

    //Camera Source
    private CameraSource mCameraSource = null;
    private android.hardware.camera2.CameraCaptureSession cameraCaptureSession;
    private CameraManager manager;
    private CameraCharacteristics cameraCharacteristics;
    protected CaptureRequest.Builder captureRequestBuilder;
    protected CameraCaptureSession cameraCaptureSessions;

    //Custom UI
    private CameraSourcePreview camera_source_preview;
    private GraphicOverlay graphic_face_overlay;
    private FilterOverlay filter_overlay;
    //private View filter_overlay2;
    private TextureView auto_photo_activity_textureview;

    //Standard UI
    private TextView auto_photo_activity_center_countdown_textview;
    private ImageView auto_photo_shutter_button;
    private CoordinatorLayout auto_photo_activity_coordinator_layout;
    private RelativeLayout auto_photo_activity_main_layout, auto_photo_activity_message_layout;
    private LinearLayout auto_photo_activity_main_layout2, auto_photo_activity_overlay_layout;
    private FrameLayout auto_photo_activity_frame_layout;

    //For display
    private ListViewCompat auto_photo_listview;
    private DisplayManagerUtilities dmu;
    private int pictureWidth, pictureHeight;

    //For Countdown
    private Timer timer = null;
    private long timeStarted, timeEnding;
    private static final long TIME_BETWEEN_ADJUSTMENTS = (long)(PGMacUtilitiesConstants.ONE_SECOND * 1.5);
    private int secondsLeft, currentNumFaces, previousNumFaces;
    private TakePhotoWithCountdownAsync async;
    private boolean isBlocked, numFacesOk, blockTheBlock, blockAll; //numFacesChanged

    private static final int RC_HANDLE_GMS = 9001;
    // permission request codes need to be < 256
    private static final int RC_HANDLE_CAMERA_PERM = 2;

    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    ///Options and Config //////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private static class AutoPhotoOptions{
        boolean showTrackerCircle, autoTakePhoto, showShutterIcon;
        int maxNumFaces, minNumFaces, countdownTimer, customShutterImageId;
        ShutterColors stockShutterColor;
        enum ShutterColors {BLUE, WHITE, BLACK, GREY};
    }

    public static class AutoPhotoOptionsBuilder{
        private static AutoPhotoOptions options;

        //Defaults
        static {
            options = new AutoPhotoOptions();
            options.autoTakePhoto = true;
            options.showTrackerCircle = true;
            options.showShutterIcon = true;
            options.maxNumFaces = 1;
            options.minNumFaces = 1;
            options.countdownTimer = 3;
            options.stockShutterColor = AutoPhotoOptions.ShutterColors.BLUE;
            options.customShutterImageId = R.drawable.shutter_blue;
        }

        public void setStockShutterColor(AutoPhotoOptions.ShutterColors stockShutterColor) {
            options.stockShutterColor = stockShutterColor;
        }

        public void setShowTrackerCircle(boolean showTrackerCircle) {
            options.showTrackerCircle = showTrackerCircle;
        }

        public void setAutoTakePhoto(boolean autoTakePhoto) {
            options.autoTakePhoto = autoTakePhoto;
        }

        public void setShowShutterIcon(boolean showShutterIcon) {
            options.showShutterIcon = showShutterIcon;
        }

        public void setMaxNumFaces(int maxNumFaces) {
            options.maxNumFaces = maxNumFaces;
        }

        public void setMinNumFaces(int minNumFaces) {
            options.minNumFaces = minNumFaces;
        }

        public void setCustomShutterImageId(int customShutterImageId) {
            options.customShutterImageId = customShutterImageId;
        }

        public void setCountdownTimer(int countdownTimer) {
            options.countdownTimer = countdownTimer;
        }

        public AutoPhotoOptions build(){
            return options;
        }
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    ///Activity Methods// //////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Initializes the UI and initiates the creation of a face detector.
     */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        //No title here. Full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //Set content view after requestWindowFeature is set
        setContentView(R.layout.custom_auto_photo_activity_layout);

        AutoPhotoOptionsBuilder builder = new AutoPhotoOptionsBuilder();
        builder.setMaxNumFaces(2);
        builder.setShowTrackerCircle(false);
        options = builder.build();

        setupUI();
        // Check for the camera permission before accessing the camera.  If the
        // permission is not granted yet, request permission.
        int rc = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (rc == PackageManager.PERMISSION_GRANTED) {
            createCameraSource();
        } else {
            requestCameraPermission();
        }

        dmu = new DisplayManagerUtilities(this);
        L.m("SIZE OF WIDTH = " + dmu.getPixelsWidth());
        L.m("SIZE OF HEIGHT = " + (dmu.getPixelsHeight()   ));//* .85));
    }

    private void setupUI(){
        camera_source_preview = (CameraSourcePreview) this.findViewById(
                R.id.camera_source_preview);
        graphic_face_overlay = (GraphicOverlay) this.findViewById(
                R.id.graphic_face_overlay);
        //filter_overlay = (FilterOverlay) this.findViewById(
                //R.id.filter_overlay);
        //filter_overlay2 = (View) this.findViewById(R.id.filter_overlay2);
        auto_photo_shutter_button = (ImageView) this.findViewById(
                R.id.auto_photo_shutter_button);
        auto_photo_activity_coordinator_layout = (CoordinatorLayout)this.findViewById(
                R.id.auto_photo_activity_coordinator_layout);
        auto_photo_activity_main_layout = (RelativeLayout)this.findViewById(
                R.id.auto_photo_activity_main_layout);
        auto_photo_activity_message_layout = (RelativeLayout)this.findViewById(
                R.id.auto_photo_activity_message_layout);
        auto_photo_activity_main_layout2 = (LinearLayout)this.findViewById(
                R.id.auto_photo_activity_main_layout2);
        auto_photo_activity_overlay_layout = (LinearLayout)this.findViewById(
                R.id.auto_photo_activity_overlay_layout);
        auto_photo_activity_center_countdown_textview = (TextView) this.findViewById(
                R.id.auto_photo_activity_center_countdown_textview);
        auto_photo_activity_textureview = (TextureView) this.findViewById(
                R.id.auto_photo_activity_textureview);
        auto_photo_listview = (ListViewCompat) this.findViewById(R.id.auto_photo_listview);
        auto_photo_activity_frame_layout = (FrameLayout) this.findViewById(
                R.id.auto_photo_activity_frame_layout);
        //Click Listeners
        auto_photo_shutter_button.setTag("auto_photo_shutter_button");
        auto_photo_shutter_button.setOnClickListener(this);
        numFacesOk = isBlocked = blockTheBlock = blockAll = false;
        currentNumFaces = previousNumFaces = 0;
    }

    /**
     * Handles the requesting of the camera permission.  This includes
     * showing a "Snackbar" message of why the permission is needed then
     * sending the request.
     */
    private void requestCameraPermission() {
        Log.w(TAG, "Camera permission is not granted. Requesting permission");

        final String[] permissions = new String[]{Manifest.permission.CAMERA};

        if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(this, permissions, RC_HANDLE_CAMERA_PERM);
            return;
        }

        final Activity thisActivity = this;

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(thisActivity, permissions,
                        RC_HANDLE_CAMERA_PERM);
            }
        };

        Snackbar.make(graphic_face_overlay, "Permission is required to take a photo",
                Snackbar.LENGTH_INDEFINITE)
                .setAction("Ok", listener)
                .show();
    }

    /**
     * Creates and starts the camera.  Note that this uses a higher resolution in comparison
     * to other detection examples to enable the barcode detector to detect small barcodes
     * at long distances.
     */
    private void createCameraSource() {

        Context context = getApplicationContext();
        FaceDetector detector = new FaceDetector.Builder(context)
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .build();

        detector.setProcessor(
                new MultiProcessor.Builder<>(new GraphicFaceTrackerFactory())
                        .build());

        if (!detector.isOperational()) {
            // Note: The first time that an app using face API is installed on a device, GMS will
            // download a native library to the device in order to do detection.  Usually this
            // completes before the app is run for the first time.  But if that download has not yet
            // completed, then the above call will not detect any faces.
            //
            // isOperational() can be used to check if the required native library is currently
            // available.  The detector will automatically become operational once the library
            // download completes on device.
            L.m("Face detector dependencies are not yet available.");
        }

        dmu = new DisplayManagerUtilities(context);
        pictureWidth= (int) dmu.getPixelsWidth();
        pictureHeight = (int) dmu.getPixelsHeight();
        //Adjusting height to account for 15% of screen dedicated to bottom section
        pictureHeight = (int)(pictureHeight * 0.85);

        mCameraSource = new CameraSource.Builder(context, detector)
                .setRequestedPreviewSize(pictureWidth, pictureHeight)
                .setAutoFocusEnabled(true)
                .setFacing(CameraSource.CAMERA_FACING_FRONT)
                .setRequestedFps(30.0f)
                .build();

    }

    //TESTING.//////////////////////////////////////
    /*
    private Handler backgroundHandler;
    private HandlerThread backgroundThread;

    */
    private ImageReader imageReader;
    private String cameraId;
    private CameraDevice cameraDevice;
    private Handler mBackgroundHandler;
    private HandlerThread mBackgroundThread;
    protected void startBackgroundThread() {
        mBackgroundThread = new HandlerThread("Camera Background");
        mBackgroundThread.start();
        mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
    }
    protected void stopBackgroundThread() {
        mBackgroundThread.quitSafely();
        try {
            mBackgroundThread.join();
            mBackgroundThread = null;
            mBackgroundHandler = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    private final CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(CameraDevice camera) {
            //This is called when the camera is open
            L.m("OnOpened");
            cameraDevice = camera;
        }
        @Override
        public void onDisconnected(CameraDevice camera) {
            L.m("onDisconnected");
            cameraDevice.close();
        }
        @Override
        public void onError(CameraDevice camera, int error) {
            L.m("onError");
            cameraDevice.close();
            cameraDevice = null;
        }
    };
    final CameraCaptureSession.CaptureCallback captureCallbackListener = new CameraCaptureSession.CaptureCallback() {
        @Override
        public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result) {
            super.onCaptureCompleted(session, request, result);
            L.m("onCaptureCompleted");
        }
    };
    TextureView.SurfaceTextureListener textureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            //change boolean here
            L.m("onSurfaceTextureAvailable");
        }
        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
            // Transform you image captured size according to the surface width and height
            L.m("onSurfaceTextureSizeChanged");
        }
        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            L.m("onSurfaceTextureDestroyed");
            return false;
        }
        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {
            L.m("onSurfaceTextureUpdated");
        }
    };
    private void openCamera() {
        CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        Log.e(TAG, "is camera open");
        try {
            String[] idList = manager.getCameraIdList();
            for(String str : idList){
                L.m("ID LIST = " + str);
            }
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(idList[0]);
            StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            assert map != null;
            manager.openCamera(cameraId, stateCallback, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        Log.e(TAG, "openCamera X");
    }
    private void startCameraSession(){

        if(SystemUtilities.userHasMarshmallowOrHigher()){
            //User has API 21+, need to use Camera2
            try {
                openCamera();
                manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);

                CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraDevice.getId());
                Size[] jpegSizes = null;
                if (characteristics != null) {
                    jpegSizes = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP).
                            getOutputSizes(ImageFormat.JPEG);
                }
                int width = pictureWidth;
                int height = pictureHeight;
                if (jpegSizes != null && 0 < jpegSizes.length) {
                    width = jpegSizes[0].getWidth();
                    height = jpegSizes[0].getHeight();
                }
                ImageReader reader = ImageReader.newInstance(width, height, ImageFormat.JPEG, 1);
                List<Surface> outputSurfaces = new ArrayList<Surface>(2);
                outputSurfaces.add(reader.getSurface());
                outputSurfaces.add(new Surface(auto_photo_activity_textureview.getSurfaceTexture()));
                final CaptureRequest.Builder captureBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
                captureBuilder.addTarget(reader.getSurface());
                captureBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
                // Orientation
                int rotation = getWindowManager().getDefaultDisplay().getRotation();
                captureBuilder.set(CaptureRequest.JPEG_ORIENTATION, ORIENTATIONS.get(rotation));
                final File file = new File(Environment.getExternalStorageDirectory()+"/pic.jpg");
                ImageReader.OnImageAvailableListener readerListener = new ImageReader.OnImageAvailableListener() {
                    @Override
                    public void onImageAvailable(ImageReader reader) {
                        Image image = null;
                        try {
                            image = reader.acquireLatestImage();
                            ByteBuffer buffer = image.getPlanes()[0].getBuffer();
                            byte[] bytes = new byte[buffer.capacity()];
                            buffer.get(bytes);
                            save(bytes);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            if (image != null) {
                                image.close();
                            }
                        }
                    }
                    private void save(byte[] bytes) throws IOException {
                        OutputStream output = null;
                        try {
                            output = new FileOutputStream(file);
                            output.write(bytes);
                        } finally {
                            if (null != output) {
                                output.close();
                            }
                            L.m("file write completed. File = " + file);
                        }
                    }
                };
                reader.setOnImageAvailableListener(readerListener, mBackgroundHandler);
                final CameraCaptureSession.CaptureCallback captureListener = new CameraCaptureSession.CaptureCallback() {
                    @Override
                    public void onCaptureCompleted(CameraCaptureSession session,
                                                   CaptureRequest request, TotalCaptureResult result) {
                        super.onCaptureCompleted(session, request, result);
                        L.m("File saved = " + file);
                        L.m("onCaptureCompleted");
                    }
                };
                cameraDevice.createCaptureSession(outputSurfaces, new CameraCaptureSession.StateCallback() {
                    @Override
                    public void onConfigured(CameraCaptureSession session) {
                        try {
                            L.m("onConfigured");
                            session.capture(captureBuilder.build(), captureListener, mBackgroundHandler);
                        } catch (CameraAccessException e) {
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onConfigureFailed(CameraCaptureSession session) {
                        L.m("onConfigureFailed");
                    }
                }, mBackgroundHandler);
            } catch (Exception e){
                e.printStackTrace();
            }
            //TESTING

        } else {
            //User has API <21, use old Camera class
            L.m("API < 21");
        }
    }

    private boolean checkIsBlocked(){
        long myTime = DateUtilities.getCurrentDateLong();
        if(myTime <= timeEnding){
            isBlocked = true;
            return isBlocked;
        } else {
            timeEnding = myTime + TIME_BETWEEN_ADJUSTMENTS;
            isBlocked = false;
            return isBlocked;
        }
    }

    /**
     * Restarts the camera.
     */
    @Override
    protected void onResume() {
        super.onResume();
        auto_photo_activity_textureview.setSurfaceTextureListener(textureListener);
        startCameraSource();
    }

    /**
     * Stops the camera.
     */
    @Override
    protected void onPause() {
        super.onPause();
        camera_source_preview.stop();
    }

    /**
     * Releases the resources associated with the camera source, the associated detector, and the
     * rest of the processing pipeline.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCameraSource != null) {
            mCameraSource.release();
        }
    }

    /**
     * Callback for the result from requesting permissions. This method
     * is invoked for every call on {@link #requestPermissions(String[], int)}.
     * <p>
     * <strong>Note:</strong> It is possible that the permissions request interaction
     * with the user is interrupted. In this case you will receive empty permissions
     * and results arrays which should be treated as a cancellation.
     * </p>
     *
     * @param requestCode  The request code passed in {@link #requestPermissions(String[], int)}.
     * @param permissions  The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *                     which is either {@link PackageManager#PERMISSION_GRANTED}
     *                     or {@link PackageManager#PERMISSION_DENIED}. Never null.
     * @see #requestPermissions(String[], int)
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode != RC_HANDLE_CAMERA_PERM) {
            Log.d(TAG, "Got unexpected permission result: " + requestCode);
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Camera permission granted - initialize the camera source");
            // we have permission, so create the camerasource
            createCameraSource();
            return;
        }

        Log.e(TAG, "Permission not granted: results len = " + grantResults.length +
                " Result code = " + (grantResults.length > 0 ? grantResults[0] : "(empty)"));

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Photo")
                .setMessage("Cannot Proceed: Lack of camera permission")
                .setPositiveButton("Ok", listener)
                .show();
    }

    //==============================================================================================
    // Camera Source Preview
    //==============================================================================================

    /**
     * Starts or restarts the camera source, if it exists.  If the camera source doesn't exist yet
     * (e.g., because onResume was called before the camera source was created), this will be called
     * again when the camera source is created.
     */
    private void startCameraSource() {

        // check that the device has play services available.
        int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(
                getApplicationContext());
        if (code != ConnectionResult.SUCCESS) {
            Dialog dlg = GoogleApiAvailability.getInstance().getErrorDialog(
                    this, code, RC_HANDLE_GMS);
            dlg.show();
        }

        if (mCameraSource != null) {
            try {
                camera_source_preview.start(mCameraSource, graphic_face_overlay);
            } catch (IOException e) {
                Log.e(TAG, "Unable to start camera source.", e);
                mCameraSource.release();
                mCameraSource = null;
            }
        }

        // TODO: 8/31/2016 look into why this is not pulling up
        auto_photo_activity_overlay_layout.bringToFront();
        //filter_overlay2.bringToFront();

        checkOptions();
    }

    /**
     * Go through options and make changes here
     */
    private void checkOptions(){
        if(!options.showShutterIcon){
            auto_photo_shutter_button.setVisibility(View.INVISIBLE);
        } else {
            auto_photo_shutter_button.setImageResource(options.customShutterImageId);
            if(options.customShutterImageId == R.drawable.shutter_blue){
                switch (options.stockShutterColor){
                    case BLACK:
                        auto_photo_shutter_button.setImageResource(R.drawable.shutter_black);
                        break;
                    case BLUE:
                        auto_photo_shutter_button.setImageResource(R.drawable.shutter_blue);
                        break;
                    case GREY:
                        auto_photo_shutter_button.setImageResource(R.drawable.shutter_grey);
                        break;
                    default: //White
                        auto_photo_shutter_button.setImageResource(R.drawable.shutter_white);
                        break;
                }
            }
        }

        //options.showTrackerCircle; //Handled elsewhere
        //options.countdownTimer; //Handled elsewhere
    }
    @Override
    public void onClick(View view) {
        String id = null;
        try {
            id = (String) view.getTag();
        } catch (Exception e){}
        if(id.equals("auto_photo_shutter_button")){
            if(blockTheBlock){
                return;
            }
            blockTheBlock = true;
            takePhotoWithCountdown();
        }
    }

    //==============================================================================================
    // Graphic Face Tracker
    //==============================================================================================

    /**
     * Factory for creating a face tracker to be associated with a new face.  The multiprocessor
     * uses this factory to create face trackers as needed -- one for each individual.
     */
    private class GraphicFaceTrackerFactory implements MultiProcessor.Factory<Face> {
        @Override
        public Tracker<Face> create(Face face) {
            return new GraphicFaceTracker(graphic_face_overlay, options);
        }
    }

    /**
     * Face tracker for each detected individual. This maintains a face graphic within the app's
     * associated face overlay.
     */
    private class GraphicFaceTracker extends Tracker<Face> {
        private GraphicOverlay mOverlay;
        private FaceGraphic mFaceGraphic;
        private AutoPhotoOptions localOptions;

        GraphicFaceTracker(GraphicOverlay overlay, AutoPhotoOptions localOptions) {
            this.mOverlay = overlay;
            this.mFaceGraphic = new FaceGraphic(overlay);
            this.localOptions = localOptions;
        }

        /**
         * Start tracking the detected face instance within the face overlay.
         */
        @Override
        public void onNewItem(int faceId, Face face) {
            if(this.localOptions.showTrackerCircle) {
                mFaceGraphic.setId(faceId);
            }


            /*
            L.m("FACE DETECTED");
            L.m("START onNewItem");
            L.m("left Eye open probability = " + face.getIsLeftEyeOpenProbability());
            L.m("right Eye open probability = " + face.getIsRightEyeOpenProbability());
            L.m("is smiling probability = " + face.getIsSmilingProbability());
            L.m("position? = " + face.getPosition());
            L.m("height  = " + face.getHeight());
            L.m("END onNewItem");
            */
        }

        /**
         * Update the position/characteristics of the face within the overlay.
         */
        @Override
        public void onUpdate(FaceDetector.Detections<Face> detectionResults, Face face) {
            if(this.localOptions.showTrackerCircle) {
                mOverlay.add(mFaceGraphic);
                mFaceGraphic.updateFace(face);
            }

            if(face != null) {
                /*
                L.m("EULER Y = " + face.getEulerY());
                L.m("EULER Z = " + face.getEulerZ());
                L.m("HEIGHT = " + face.getHeight());
                L.m("WIDTH = " + face.getWidth());
                */
                PointF pf = face.getPosition();
                float localX = pf.x;
                float localY = pf.y;
                //L.m("AT POSITION " + localX + "," + localY);
            }
            if(!checkIsBlocked()) {
                int x = detectionResults.getDetectedItems().size();
                if (options.minNumFaces > 0 && options.maxNumFaces != 0) {
                    //Ok to use faces for checking
                    /*
                    currentNumFaces = x;
                    if (currentNumFaces != previousNumFaces) {
                        previousNumFaces = currentNumFaces;
                        numFacesChanged = true;
                    } else {
                        numFacesChanged = false;
                    }
                    */
                    if (x >= options.minNumFaces && x <= options.maxNumFaces) {
                        numFacesOk = true;
                    } else {
                        numFacesOk = false;
                    }
                } else {
                    numFacesOk = true;
                }
                if (x <= 0) {
                    numFacesOk = false;
                }
                preCheckOnCamera();
                if (numFacesOk) {
                    //
                }
            }

            //Face enters the picture
            // TODO: 8/30/2016 here is where we check if the # of faces is 1 or more
            //L.m("detectionResults number = " + detectionResults.getDetectedItems().size());
            /*
            L.m("START onUpdate");
            L.m("left Eye open probability = " + face.getIsLeftEyeOpenProbability());
            L.m("right Eye open probability = " + face.getIsRightEyeOpenProbability());
            L.m("is smiling probability = " + face.getIsSmilingProbability());
            L.m("position? = " + face.getPosition());
            L.m("height  = " + face.getHeight());
            L.m("END onUpdate");
            if (face.getIsSmilingProbability() > 0.75) {
                L.m("USER IS SMILING");
            } else {
                L.m("USER IS NOT SMILING");
            }
            */
        }

        /**
         * Hide the graphic when the corresponding face was not detected.  This can happen for
         * intermediate frames temporarily (e.g., if the face was momentarily blocked from
         * view).
         */
        @Override
        public void onMissing(FaceDetector.Detections<Face> detectionResults) {
            //Called when face goes out of picture
            if(this.localOptions.showTrackerCircle) {
                mOverlay.remove(mFaceGraphic);
            }
            int x = detectionResults.getDetectedItems().size();
            if (x <= 0) {
                numFacesOk = false;
            }
            if (!numFacesOk) {
                //preCheckOnCamera();
            }
        }

        /**
         * Called when the face is assumed to be gone for good. Remove the graphic annotation from
         * the overlay.
         */
        @Override
        public void onDone() {
            if(this.localOptions.showTrackerCircle) {
                mOverlay.remove(mFaceGraphic);
            }
        }
    }

    private void preCheckOnCamera(){
        if(!numFacesOk){
            stopPhotoProcess();
        }
        if(options.autoTakePhoto){
            if(numFacesOk){
                takePhotoWithCountdown();
            }
        }
    }

    private void stopPhotoProcess(){
        if(!blockTheBlock) {
            L.m("PHOTO PROCESS STOPPED");
            if (async != null) {
                async.cancel(false);
            }
            async = null;
        }
    }

    private void takePhotoWithCountdown(){

        if(async != null){
            return;
        }
        if(blockAll){
            return;
        }
        /*
        L.m("TRYING LIGHT HERE");
        PackageManager pm = this.getPackageManager();
        final Camera.Parameters p = camera.getParameters();
        if (isFlashSupported(pm)) {
            boolean on = ((ToggleButton) view).isChecked();
            if (on) {
                Log.i("info", "torch is turn on!");
                p.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                camera.setParameters(p);
                camera.startPreview();
            } else {
                Log.i("info", "torch is turn off!");
                p.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                camera.setParameters(p);
                camera.stopPreview();
            }
        }
        L.m("END TRYING LIGHT");
        */

        async = new TakePhotoWithCountdownAsync(this.options.countdownTimer);
        async.execute();

    }

    private void countdownFinished(boolean bool){
        blockTheBlock = false;
        if(bool){
            //Do stuff
            //L.m("STARTING PHOTO CALL");
            //startCameraSession();
            L.m("STARTING PHOTO CALL");
            blockAll = true;
            startPre21Camera();
        }
    }

    /**
     * Countdown timer async class which updates the views. Only param it takes in via the
     * constructor is the number of seconds in the countdown, which CAN be omitted
     */
    private class TakePhotoWithCountdownAsync extends AsyncTask <Void, Integer, Void> {

        private int numSecondsCountdown;
        private boolean bailOut, taskComplete;

        TakePhotoWithCountdownAsync(){
            this.numSecondsCountdown = 0;
            this.bailOut = false;
            this.taskComplete = false;
        }

        TakePhotoWithCountdownAsync(int numSecondsCountdown){
            if(numSecondsCountdown < 0){
                numSecondsCountdown = 0;
            }
            this.numSecondsCountdown = numSecondsCountdown;
            this.bailOut = false;
            this.taskComplete = false;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }


        @Override
        protected void onCancelled() {
            super.onCancelled();
            bailOut = true;
        }

        @Override
        protected void onCancelled(Void aVoid) {
            super.onCancelled(aVoid);
            bailOut = true;
            handleCancel();
        }

        private void handleCancel(){
            this.onPostExecute(null);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if(numSecondsCountdown <= 0 || isCancelled()){
                return null;
            } else {
                for (int i = numSecondsCountdown; i > 0; i--) {
                    //If cancel, bail out here
                    if(isCancelled()){
                        return null;
                    }
                    //Otherwise, continue on
                    publishProgress(i);
                    try {
                        Thread.sleep(PGMacUtilitiesConstants.ONE_SECOND);
                    } catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }
                return null;
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            int x = values[0];
            auto_photo_activity_center_countdown_textview.setText("" + x);
            AnimationUtilities.animateMyView(auto_photo_activity_center_countdown_textview,
                    (int)(PGMacUtilitiesConstants.ONE_SECOND * 0.75),
                    PGMacUtilitiesConstants.OUT_ZOOM);

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if(bailOut || isCancelled()){
                L.m("ABORT!");
                /*
                auto_photo_activity_center_countdown_textview.setText("ABORT!");
                AnimationUtilities.animateMyView(auto_photo_activity_center_countdown_textview,
                        (int)(PGMacUtilitiesConstants.ONE_SECOND * 0.75),
                        PGMacUtilitiesConstants.OUT_ZOOM);
                */
                countdownFinished(false);
            } else {
                L.m("COUNTDOWN FINISHED");
                AnimationUtilities.animateMyView(auto_photo_activity_main_layout2,
                        (int) (PGMacUtilitiesConstants.ONE_SECOND * 0.12), PGMacUtilitiesConstants.IN_FLASH);

                countdownFinished(true);
            }
        }
    }

    public static class PhotoQualifications {
        String str;
        Integer imageIdIfPassing, imageIdIfFailing, stringColorIfPassing, stringColorIfFailing;

        public void setStr(String str) {
            this.str = str;
        }

        public void setImageIdIfPassing(int imageIdIfPassing) {
            this.imageIdIfPassing = imageIdIfPassing;
        }

        public void setImageIdIfFailing(int imageIdIfFailing) {
            this.imageIdIfFailing = imageIdIfFailing;
        }

        public void setStringColorIfPassing(int stringColorIfPassing) {
            this.stringColorIfPassing = stringColorIfPassing;
        }

        public void setStringColorIfFailing(int stringColorIfFailing) {
            this.stringColorIfFailing = stringColorIfFailing;
        }
    }

    private class PhotoQualificationsListAdapter extends ArrayAdapter {

        private Context context;
        private PhotoQualifications[] objects;
        public PhotoQualificationsListAdapter(Context context, int resource, PhotoQualifications[] objects) {
            super(context, resource, objects);
            this.objects = objects;
            this.context = context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(context);
            //View
            View rowView = inflater.inflate(R.layout.generic_string_image_adapter_layout, null,true);

            //UI
            TextView generic_adapter_layout_textview = (TextView) rowView.findViewById(
                    R.id.generic_adapter_layout_textview);
            ImageView generic_adapter_layout_imageview = (ImageView) rowView.findViewById(
                    R.id.generic_adapter_layout_imageview);

            //Object
            PhotoQualifications obj = null;
            try {
                obj = objects[position];
            } catch (ArrayIndexOutOfBoundsException e){
                //In case there is a mismatch in sizes
            }

            if(obj != null){
                if(!StringUtilities.isNullOrEmpty(obj.str)){
                    generic_adapter_layout_textview.setText(obj.str);
                }
                //Set these here
                int imageIdIfPassing, imageIdIfFailing, colorIfPassing, colorIfFailing;
                // TODO: 8/31/2016 decide on usefulness of this
            }

            return rowView;
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////////
    ///Deprecated Code for Pre API 21 Devices/////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////

    //Deprecated stuff for Pre API 21 devices
    private Bitmap pictureTaken,croppedImg;
    private int deprecatedHeight, degree;
    private boolean safeToTakePicture = false;
    private CameraPreview mPreview;
    private OnTaskCompleteListener onTaskCompleteListener = new OnTaskCompleteListener() {
        @Override
        public void onTaskComplete(Object result, int customTag) {
            L.m("Stuff happened");
        }
    };
    /*
    http://stackoverflow.com/questions/21723557/java-lang-runtimeexception-takepicture-failed
    http://stackoverflow.com/questions/20938543/android-java-lang-runtimeexception-takepicture-failed

     */
    private void startPre21Camera(){
        try {
            L.m("startPre21Camera");
            Camera mCamera;
            mCamera = this.getCameraInstance();
            mPreview = new CameraPreview(this, this, mCamera, onTaskCompleteListener);
            auto_photo_activity_frame_layout.addView(mPreview);
            mCamera.takePicture(shutter, picture_raw, picture_jpg);
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            //
        }
    }
    private Camera getCameraInstance() {
        int cameraCount = 0;
        Camera cam = null;
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        cameraCount = Camera.getNumberOfCameras();
        for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
            Camera.getCameraInfo(camIdx, cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                try {
                    cam = Camera.open(camIdx);

                } catch (RuntimeException e) {
                    e.printStackTrace();
                }
            }else if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT){
                cam = Camera.open(camIdx);
            }
        }

        return cam;
    }
    Camera.PictureCallback picture_jpg = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera arg1) {
            L.m("onPictureTaken");
            arg1.startPreview();
            //convert image to bitmap
            ProgressBarUtilities.showSVGProgressDialog(AutoPhotoActivity.this);
            Matrix matrix = new Matrix();
            matrix.postRotate(-90);

            pictureTaken = BitmapFactory.decodeByteArray(data, 0,
                    data.length);
            deprecatedHeight = new Double(pictureTaken.getHeight() * .43).intValue();
            croppedImg = Bitmap.createBitmap(pictureTaken, deprecatedHeight, 0,
                    pictureTaken.getWidth() - deprecatedHeight, pictureTaken.getHeight());
            Uri selectedImageuri = getImageUri(croppedImg);
            String selectedImagePath = getAbsolutePath(selectedImageuri);
            setCapturedImage(selectedImagePath, new OnTaskCompleteListener() {
                @Override
                public void onTaskComplete(Object result, int customTag) {
                    ProgressBarUtilities.dismissProgressDialog();
                    blockAll = false;
                    L.m("result = " + result);
                }
            });


        }
    };
    Camera.ShutterCallback shutter = new Camera.ShutterCallback() {
        @Override
        public void onShutter() {
            L.m("onShutter");
        }
    };
    Camera.PictureCallback picture_raw = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] arg0, Camera arg1) {
            L.m("onPictureTaken");
        }
    };

    public void setCapturedImage(final String imagePath, final OnTaskCompleteListener listener) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {

                try {
                    return getRightAngleImage(imagePath);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                return imagePath;
            }

            private String getRightAngleImage(String photoPath) {
                try {
                    ExifInterface ei = new ExifInterface(photoPath);
                    int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                            ExifInterface.ORIENTATION_NORMAL);

                    switch (orientation) {

                    }

                    return rotateImage(degree, photoPath);

                } catch (Exception e) {
                    e.printStackTrace();
                }

                return photoPath;
            }

            @Override
            protected void onPostExecute(String imagePath) {
                super.onPostExecute(imagePath);
                listener.onTaskComplete(imagePath, -1);
            }
        }.execute();
    }

    public Uri getImageUri(Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(getContentResolver(),
                inImage, "Title", null);
        return Uri.parse(path);
    }

    public String getAbsolutePath(Uri uri) {
        if (Build.VERSION.SDK_INT >= 19) {
            String id = "";
            if (uri.getLastPathSegment().split(":").length > 1)
                id = uri.getLastPathSegment().split(":")[1];
            else if (uri.getLastPathSegment().split(":").length > 0)
                id = uri.getLastPathSegment().split(":")[0];
            if (id.length() > 0) {
                final String[] imageColumns = { MediaStore.Images.Media.DATA };
                final String imageOrderBy = null;
                Uri tempUri = getUri();
                Cursor imageCursor = getContentResolver().query(tempUri,
                        imageColumns, MediaStore.Images.Media._ID + "=" + id,
                        null, imageOrderBy);
                if (imageCursor.moveToFirst()) {
                    return imageCursor.getString(imageCursor
                            .getColumnIndex(MediaStore.Images.Media.DATA));
                } else {
                    return null;
                }
            } else {
                return null;
            }
        } else {
            String[] projection = { MediaStore.MediaColumns.DATA };
            Cursor cursor = getContentResolver().query(uri, projection, null,
                    null, null);
            if (cursor != null) {
                int column_index = cursor
                        .getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                cursor.moveToFirst();
                return cursor.getString(column_index);
            } else
                return null;
        }

    }

    private Uri getUri() {
        String state = Environment.getExternalStorageState();
        if (!state.equalsIgnoreCase(Environment.MEDIA_MOUNTED))
            return MediaStore.Images.Media.INTERNAL_CONTENT_URI;

        return MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
    }

    String rotateImage(int degree, String imagePath) {
        Bitmap b = BitmapFactory.decodeFile(imagePath);
        Matrix matrix = new Matrix();
        if (degree <= 0) {
            matrix.postRotate(-90);
            b = Bitmap.createBitmap(b , 0, 0, b.getWidth(), b.getHeight(), matrix, true);
        }else{




            if (b.getWidth() > b.getHeight()) {
                matrix.setRotate(degree);
                b = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(),
                        matrix, true);
            }
        }

        try {
            FileOutputStream fOut = new FileOutputStream(imagePath);
            String imageName = imagePath
                    .substring(imagePath.lastIndexOf("/") + 1);
            String imageType = imageName
                    .substring(imageName.lastIndexOf(".") + 1);

            FileOutputStream out = new FileOutputStream(imagePath);
            if (imageType.equalsIgnoreCase("png")) {
                b.compress(Bitmap.CompressFormat.PNG, 100, out);
            } else if (imageType.equalsIgnoreCase("jpeg")
                    || imageType.equalsIgnoreCase("jpg")) {
                b.compress(Bitmap.CompressFormat.JPEG, 100, out);
            }
            fOut.flush();
            fOut.close();

            b.recycle();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return imagePath;
    }
}
