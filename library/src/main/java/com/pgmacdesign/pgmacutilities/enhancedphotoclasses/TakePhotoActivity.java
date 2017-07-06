package com.pgmacdesign.pgmacutilities.enhancedphotoclasses;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.SparseArray;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.pgmacdesign.pgmacutilities.R;
import com.pgmacdesign.pgmacutilities.graphicsanddrawing.CircleOverlayView;
import com.pgmacdesign.pgmacutilities.misc.PGMacUtilitiesConstants;
import com.pgmacdesign.pgmacutilities.utilities.AnimationUtilities;
import com.pgmacdesign.pgmacutilities.utilities.CameraMediaUtilities;
import com.pgmacdesign.pgmacutilities.utilities.ColorUtilities;
import com.pgmacdesign.pgmacutilities.utilities.DateUtilities;
import com.pgmacdesign.pgmacutilities.utilities.DisplayManagerUtilities;
import com.pgmacdesign.pgmacutilities.utilities.FileUtilities;
import com.pgmacdesign.pgmacutilities.utilities.ImageUtilities;
import com.pgmacdesign.pgmacutilities.utilities.L;
import com.pgmacdesign.pgmacutilities.utilities.PermissionUtilities;
import com.pgmacdesign.pgmacutilities.utilities.ProgressBarUtilities;
import com.pgmacdesign.pgmacutilities.utilities.StringUtilities;
import com.pgmacdesign.pgmacutilities.utilities.SystemUtilities;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

/**
 * For Pre API21 devices that need to use the old, deprecated camera class
 * Created by pmacdowell on 9/20/2016.
 */
public class TakePhotoActivity extends AppCompatActivity implements View.OnClickListener, CustomPhotoListener {

    //UI
    private ImageView take_photo_activity_shutter_button;
    private CoordinatorLayout take_photo_activity_top_coordinator_layout;
    private TextView take_photo_activity_top_textview,
            take_photo_activity_center_countdown_textview;
    private RelativeLayout take_photo_activity_relative_layout,
            take_photo_activity_top_text_layout;
    private TextureView take_photo_activity_textureview;
    private CircleOverlayView circleOverlayView;

    //Misc
    private static final long TIME_IN_MILLISECONDS_FOR_INITIAL_DELAY =
            (PGMacUtilitiesConstants.ONE_SECOND * 4);
    private boolean okToTake;
    private int displayOrientation;
    private File file;
    private long timeActivityOpened;
    private String userSentPathToFile, userSentNameOfFile, photoExtensionName;
    private boolean useFlash, useFrontFacingCamera, isActuallyUsingFrontCamera;
    private boolean postedSmileText, postedNot1FaceText, postedInitialText, blockPosts;
    private int cameraWidth, cameraHeight;

    //Camera
    private Camera camera;
    private TextureView.SurfaceTextureListener textureListener;
    private SurfaceTexture texture;
    private Camera.Size imageSizes;
    private Camera.Parameters cameraParameters;


    //Custom UI Features and face detection
    private GraphicOverlay graphic_face_overlay;
    private FaceTrackerWithGraphic faceTracker;
    private TakePhotoWithCountdownAsync async;
    private CameraSource mCameraSource = null;
    private FaceDetector detector;

    /*

     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preInit();
        setContentView(R.layout.take_photo_activity);
        initUI();
        initIntentData();
        initVariables();
        initLastCalls();
    }
    /**
     * Called before the UI is set to make it full screen
     */
    private void preInit(){
        //No title here. Full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
                        View.SYSTEM_UI_FLAG_LOW_PROFILE |
                        View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    /**
     * initialize the ui
     */
    private void initUI() {
        take_photo_activity_textureview = (TextureView) this.findViewById(
                R.id.take_photo_activity_textureview);
        take_photo_activity_textureview.setTag("take_photo_activity_textureview");

        take_photo_activity_relative_layout = (RelativeLayout) this.findViewById(
                R.id.take_photo_activity_relative_layout);
        take_photo_activity_relative_layout.setTag("take_photo_activity_relative_layout");

        take_photo_activity_top_text_layout = (RelativeLayout) this.findViewById(
                R.id.take_photo_activity_top_text_layout);

        take_photo_activity_top_textview = (TextView) this.findViewById(
                R.id.take_photo_activity_top_textview);
        take_photo_activity_top_textview.setTag("take_photo_activity_top_textview");

        take_photo_activity_center_countdown_textview = (TextView) this.findViewById(
                R.id.take_photo_activity_center_countdown_textview);
        take_photo_activity_center_countdown_textview.setTag(
                "take_photo_activity_center_countdown_textview");

        take_photo_activity_top_coordinator_layout = (CoordinatorLayout) this.findViewById(
                R.id.take_photo_activity_top_coordinator_layout);
        take_photo_activity_top_coordinator_layout.setTag("take_photo_activity_top_coordinator_layout");

        take_photo_activity_shutter_button = (ImageView) this.findViewById(
                R.id.take_photo_activity_shutter_button);
        take_photo_activity_shutter_button.setTag("take_photo_activity_shutter_button");

        graphic_face_overlay = (GraphicOverlay) this.findViewById(
                R.id.graphic_face_overlay);

        setupOverlay();

    }

    private void setupOverlay(){
        //Local, for use in setting the overlay view
        // TODO: 9/21/2016 refactor this into constructor preferences
        FrameLayout take_photo_activity_overlay_layout = (FrameLayout) this.findViewById(
                R.id.take_photo_activity_overlay_layout);
        take_photo_activity_overlay_layout.setVisibility(View.VISIBLE);
        DisplayManagerUtilities dmu = new DisplayManagerUtilities(this);
        int width = (int) (dmu.getWidthRadius() * 0.83);
        CircleOverlayView.CircleOverlayParams params = new CircleOverlayView.CircleOverlayParams();
        params.setShapeType(CircleOverlayView.CircleOverlayParams.ShapeTypes.OVAL);
        params.setColorToSet(ColorUtilities.parseMyColor(PGMacUtilitiesConstants.COLOR_BLACK));
        params.setAlphaToUse(99);
        params.setShapeRadius(width);
        circleOverlayView = new CircleOverlayView(this, params);
        take_photo_activity_overlay_layout.addView(circleOverlayView);

        take_photo_activity_shutter_button.bringToFront();
    }

    /**
     * Load up the data from the intent
     */
    private void initIntentData(){
        Intent intent = getIntent();
        userSentPathToFile = intent.getStringExtra(CameraMediaUtilities.TAG_FILE_PATH);
        userSentNameOfFile = intent.getStringExtra(CameraMediaUtilities.TAG_FILE_NAME);
        photoExtensionName = intent.getStringExtra(CameraMediaUtilities.TAG_FILE_EXTENSION);
        //Both booleans default to true unless specified otherwise
        useFlash = intent.getBooleanExtra(CameraMediaUtilities.TAG_USE_FLASH, true);
        useFrontFacingCamera = intent.getBooleanExtra(
                CameraMediaUtilities.TAG_USE_FRONT_FACING_CAMERA, true);

        if(StringUtilities.anyNullsOrEmptyInStrings(new String[]{userSentPathToFile,
                userSentNameOfFile, photoExtensionName})){
            L.toast(this, "An error occurred, please try again");
            this.finish();
        } else {
            file = FileUtilities.generateFileForImage(userSentPathToFile,
                    userSentNameOfFile, photoExtensionName);
        }

        //file = new File(Environment.getExternalStorageDirectory()+"/pic.jpg");
    }

    /**
     * Init any Misc variables
     */
    private void initVariables(){
        okToTake = true;
        displayOrientation = 0;
        //Check current API level. Should be only 20 or lower here
        if(SystemUtilities.userHasMarshmallowOrHigher()){
            // TODO: 9/20/2016 re-enable this once pre-activity is set
            //L.toast(this, "Your phone does not support this feature");
            //this.finish();
        }

        //Check camera feature (people have phones without cameras?)
        if(!this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            L.toast(this, "Your phone does not support this feature");
            this.finish();
        }

        //Check camera permissions
        PermissionUtilities perm = PermissionUtilities.getInstance(this);
        if(!perm.startPermissionsRequest(PermissionUtilities.permissionsEnum.CAMERA)){
            //No permissions given
            L.toast(this, "You must enable camera permissions to use this feature");
            this.finish();
        }

        textureListener = new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                //open your camera here
                //TakePhotoActivity.this.texture = texture;
                postAMessage(AlertMessages.INITIAL);
                setupCamera();
                camera.startPreview();

                try {
                    //mCameraSource.start();
                } catch (Exception e){
                    e.printStackTrace();
                }


            }
            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
                // Transform you image captured size according to the texture width and height
            }
            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                camera.stopPreview();
                camera.release();
                return true;
            }
            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surface) {
            }
        };
    }

    /**
     * Enable or disable the camera functionality
     * @param bool true for enable false for disable
     */
    private void enableCamera(boolean bool){
        if(bool){
            okToTake = true;
            take_photo_activity_shutter_button.setImageResource(R.drawable.shutter_blue);
            try {
                camera.unlock();
            } catch (Exception e){
                e.printStackTrace();
            }
        } else {
            stopPhotoCountdown();
            okToTake = false;
            take_photo_activity_shutter_button.setImageResource(R.drawable.shutter_grey);
            try {
                camera.lock();
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    /**
     * Last calls happen here
     */
    private void initLastCalls(){
        timeActivityOpened = DateUtilities.getCurrentDateLong();
        assert take_photo_activity_textureview != null;
        take_photo_activity_shutter_button.setOnClickListener(this);
        take_photo_activity_textureview.setSurfaceTextureListener(textureListener);
        //setupCamera();
    }


    /**
     * On Click
     * @param view
     */
    @Override
    public void onClick(View view) {
        String str = null;
        try {
            str = (String) view.getTag();
        } catch (Exception e){}
        if(str != null){
            if(str.equalsIgnoreCase("take_photo_activity_shutter_button")){
                if(okToTake) {
                    // TODO: 9/20/2016 add code for face recognition to auto take picture
                    takePicture();
                    okToTake = false;
                }
            }
        }
    }

    /**
     * Setup the camera
     */
    private void setupCamera(){
        int cameraId = this.findFrontFacingCamera();
        if(useFrontFacingCamera){
            if(cameraId == -1){
                //No front facing
                cameraId = 0;
            }
        } else {
            cameraId = 0;
        }
        try {
            if(cameraId != 0){
                isActuallyUsingFrontCamera = true;
            } else {
                isActuallyUsingFrontCamera = false;
            }
            camera = Camera.open(cameraId);
        } catch (Exception e){
            e.printStackTrace();
            L.toast(this, "An error occurred trying to open your camera");
            this.finish();
        }
        cameraParameters = camera.getParameters();
        try {
            //Get preview size
            setCameraDisplayOrientation(this, cameraId, camera);
            imageSizes = cameraParameters.getPreviewSize();
            assert texture != null;
            //Flash
            cameraParameters.setFlashMode(Camera.Parameters.FLASH_MODE_ON); //Use torch instead?
            //Sizes
            List<Camera.Size> availSizes = cameraParameters.getSupportedPreviewSizes();
                texture = take_photo_activity_textureview.getSurfaceTexture();
            cameraWidth = availSizes.get(0).width;
            cameraHeight = availSizes.get(0).height;
            cameraParameters.setPreviewSize(cameraWidth, cameraHeight);
            //Set the camera source with data here

            camera.setPreviewTexture(texture);
            camera.setParameters(cameraParameters);
        } catch (Exception e){
            e.printStackTrace();
        }
        try {
            //auto-focus settings
            List<String> supportedFocusModes = cameraParameters.getSupportedFocusModes();
            boolean hasAutoFocus = supportedFocusModes != null && supportedFocusModes.contains(
                    Camera.Parameters.FOCUS_MODE_AUTO);
            if(hasAutoFocus){
                camera.autoFocus(new Camera.AutoFocusCallback() {
                    @Override
                    public void onAutoFocus(boolean focused, Camera camera) {
                        if(focused){
                            enableCamera(true);
                        } else {
                            enableCamera(false);
                        }
                    }
                });
            } else {
                L.m("Phone does not have auto focus");
            }
        } catch (Exception e){}

//Face graphic overlay + Facial tracking

        //builder.setImageData()
        try {

            faceTracker = new FaceTrackerWithGraphic(graphic_face_overlay, this);
            detector = new FaceDetector.Builder(this)
                    .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                    .build();
            detector.setProcessor(faceTracker.buildDetector());
            L.m("face tracker setup");
            if(detector.isOperational()){
                L.m("detector is operational");
            } else {
                L.m("detector is NOT operational");
            }

            CameraSource.Builder myBuilder = new CameraSource.Builder(this, detector);
            myBuilder.setRequestedPreviewSize(cameraWidth, cameraHeight);
            myBuilder.setAutoFocusEnabled(true);
            int facing;
            if(isActuallyUsingFrontCamera){
                facing = CameraSource.CAMERA_FACING_FRONT;
            } else {
                facing = CameraSource.CAMERA_FACING_BACK;
            }
            myBuilder.setFacing(facing);
            myBuilder.setRequestedFps(30.0f);
            mCameraSource = myBuilder.build();
            graphic_face_overlay.setCameraInfo(cameraWidth, cameraHeight, facing);
            graphic_face_overlay.setVisibility(View.INVISIBLE);

        } catch (Exception e){
            detector = null;
            e.printStackTrace();
        }
    }

    //NOTE! can be set to non-static if remove the actual amount rotated (or just return it)
    public void setCameraDisplayOrientation(Activity activity, int cameraId,
                                                   android.hardware.Camera camera) {
        int myOrientation = getCorrectCameraDisplayOrientation(activity, cameraId);
        displayOrientation = myOrientation;
        camera.setDisplayOrientation(myOrientation);
    }

    /**
     * For old, deprecated Camera1
     * @param activity
     * @param cameraId
     */
    public static int getCorrectCameraDisplayOrientation(Activity activity, int cameraId) {
        android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int result;
        //int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        // do something for phones running an SDK before lollipop
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360; // compensate the mirror
        } else { // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        return result;
        //To set it, simply write:
        //camera.setDisplayOrientation(result);
    }
    /**
     * Check for front facing camera. If -1, no front facing camera
     * @return
     */
    private int findFrontFacingCamera() {
        int cameraId = -1;
        // Search for the front facing camera
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                cameraId = i;
                break;
            }
        }
        return cameraId;
    }

    /**
     * Take the actual picture
     */
    private void takePicture(){
        L.m("takepicture");
        stopPhotoCountdown();
        ProgressBarUtilities.showSVGProgressDialog(this);
        camera.takePicture(null, null, new PhotoHandler(this));
    }

    @Override
    public void facesChanged(final int numberOfFaces) {
        L.m("num faces changed = " + numberOfFaces);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(numberOfFaces == 1){
                    enableCamera(true);
                    if(isPastInitialStartupTime()){
                        startPhotoCountdown();
                    }
                } else {
                    enableCamera(false);
                }
            }
        });
    }

    @Override
    public void countdownFinished(final boolean bool) {
        L.m("countdown finished");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(bool){
                    //Photo is ready to go
                    takePicture();
                } else {
                    //Photo was cancelled (maybe they moved?) popup here with text
                }
            }
        });

    }

    /**
     * Checks if the initial time has passed on startup in order to give the user
     * some extra time to get situated before the auto timer starts
     * @return
     */
    private boolean isPastInitialStartupTime(){
        long currentTime = DateUtilities.getCurrentDateLong();
        if(currentTime - timeActivityOpened > TIME_IN_MILLISECONDS_FOR_INITIAL_DELAY){
            return true;
        }
        return false;
    }

    /**
     * Start the auto photo countdown
     */
    private void startPhotoCountdown(){
        L.l(486);
        if(async != null){
            return;
        }
        async = new TakePhotoWithCountdownAsync(this, 4,
                take_photo_activity_center_countdown_textview);
        async.execute();
    }

    /**
     * Stop the auto photo countdown
     */
    private void stopPhotoCountdown(){
        L.l(499);
        if(async != null){
            async.cancel(false);
        }
        async = null;
    }
    /**
     * Class for handling writing
     */
    class PhotoHandler implements Camera.PictureCallback {
        private final Context context;

        public PhotoHandler(Context context) {
            this.context = context;
        }

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {

            File pictureFileDir = getDir();

            if (!pictureFileDir.exists() && !pictureFileDir.mkdirs()) {
                L.m("Can't create directory to save image.");
                Toast.makeText(context, "Can't create directory to save image.",
                        Toast.LENGTH_LONG).show();
                return;

            }

            try {
                //Write file
                FileOutputStream fos = new FileOutputStream(file);

                Bitmap realImage = BitmapFactory.decodeByteArray(data, 0, data.length);

                if(detector != null) {
                    Frame frame = new Frame.Builder().setBitmap(realImage).build();
                    SparseArray<Face> faces = detector.detect(frame);
                    int x = faces.size();
                    L.m("NUMBER OF FACES = " + x);
                }

                int amountToRotate = (360 - displayOrientation) % 360;
                realImage = ImageUtilities.rotate(realImage, amountToRotate);

                //Note, I am not compressing these to full quality on purpose
                boolean bo = realImage.compress(Bitmap.CompressFormat.JPEG, 92, fos);

                fos.close();

                //Success
                successMethod();

            } catch (Exception error) {
                error.printStackTrace();
                L.toast(context, "An error occurred while taking your photo");
            }
        }

        private File getDir() {
            File sdDir = Environment
                    .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            return new File(sdDir, "CameraAPIDemo");
        }
    }

    /**
     * This is called when the camera finishes taking a photo and the file is written
     */
    private void successMethod(){
        android.net.Uri uri = FileUtilities.convertFileToUri(file);
        Intent resultIntent = new Intent();
        resultIntent.putExtra(CameraMediaUtilities.TAG_SELF_PHOTO_URI,
                StringUtilities.convertAndroidUriToString(uri));
        setResult(Activity.RESULT_OK, resultIntent);
        setResult(Activity.RESULT_OK, resultIntent);
        TakePhotoActivity.this.finish();
    }

    /**
     * Post a message on the top section
     * @param whichMessage
     */
    private void postAMessage(AlertMessages whichMessage){
        if(blockPosts){
            return;
        }
        switch (whichMessage){
            case INITIAL:
                if(!postedInitialText){
                    postedInitialText = true;
                    String message = whichMessage.message;
                    int lengthOfDisplay = whichMessage.lengthOfDisplay;
                    new MessageUserTop(message, lengthOfDisplay).execute();
                }
                break;

            case BAD_FACES:
                if(!postedNot1FaceText){
                    postedNot1FaceText = true;
                    String message = whichMessage.message;
                    int lengthOfDisplay = whichMessage.lengthOfDisplay;
                    new MessageUserTop(message, lengthOfDisplay).execute();
                }
                break;
        }
    }

    /**
     * For sending a message up top, fading in and out, and making it (in)visible
     */
    private class MessageUserTop extends AsyncTask<Void, Void, Void> {
        String message;
        long seconds;

        /**
         * Loads a message
         * @param message String message to show
         * @param seconds length of time
         */
        MessageUserTop(String message, long seconds){
            this.seconds = seconds;
            this.message = message;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            try {
                fadeInTextSlow(message);
            } catch (Exception e){e.printStackTrace();}
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Thread.sleep(seconds);
            } catch (InterruptedException e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            blockPosts = false;
            try {
                fadeOutTextSlow();
            } catch (Exception e){e.printStackTrace();}
        }
    }
    private void fadeInTextSlow(String message){
        take_photo_activity_top_textview.setText(Html.fromHtml(message));
        take_photo_activity_relative_layout.setVisibility(View.VISIBLE);
        take_photo_activity_top_text_layout.setVisibility(View.VISIBLE);
        AnimationUtilities.animateMyView(take_photo_activity_top_textview,
                (int)(PGMacUtilitiesConstants.ONE_SECOND * 0.4),
                Techniques.FadeInUp);
        take_photo_activity_top_textview.bringToFront();
    }
    private void fadeOutTextSlow(){
        AnimationUtilities.animateMyView(take_photo_activity_top_text_layout,
                (int)(PGMacUtilitiesConstants.ONE_SECOND * 0.4),
                Techniques.FadeOutDown);
    }

    /**
     * Alert messages to show the user.
     */
    private static enum AlertMessages{
        INITIAL("Center your face in circle and give us your best smile!",
                (int)(PGMacUtilitiesConstants.ONE_SECOND * 3)),
        BAD_FACES("We can't see your face!",
                (int)(PGMacUtilitiesConstants.ONE_SECOND * 2.5));

        String message;
        int lengthOfDisplay;
        AlertMessages(String message, int lengthOfDisplay){
            this.message = message;
            this.lengthOfDisplay = lengthOfDisplay;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (take_photo_activity_textureview.isAvailable()) {
            setupCamera();
            camera.startPreview();
        } else {
            take_photo_activity_textureview.setSurfaceTextureListener(textureListener);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        ProgressBarUtilities.dismissProgressDialog();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /*
    Unused code


     */
}
