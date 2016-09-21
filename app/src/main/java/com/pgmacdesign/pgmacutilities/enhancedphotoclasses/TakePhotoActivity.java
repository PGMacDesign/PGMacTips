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
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
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

import com.pgmacdesign.pgmacutilities.R;
import com.pgmacdesign.pgmacutilities.graphicsanddrawing.CircleOverlayView;
import com.pgmacdesign.pgmacutilities.nonutilities.PGMacUtilitiesConstants;
import com.pgmacdesign.pgmacutilities.utilities.CameraMediaUtilities;
import com.pgmacdesign.pgmacutilities.utilities.ColorUtilities;
import com.pgmacdesign.pgmacutilities.utilities.DisplayManagerUtilities;
import com.pgmacdesign.pgmacutilities.utilities.FileUtilities;
import com.pgmacdesign.pgmacutilities.utilities.ImageUtilities;
import com.pgmacdesign.pgmacutilities.utilities.L;
import com.pgmacdesign.pgmacutilities.utilities.PermissionUtilities;
import com.pgmacdesign.pgmacutilities.utilities.StringUtilities;
import com.pgmacdesign.pgmacutilities.utilities.SystemUtilities;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

/**
 * For Pre API21 devices that need to use the old, deprecated camera class
 * Created by pmacdowell on 9/20/2016.
 */
public class TakePhotoActivity extends AppCompatActivity implements View.OnClickListener {

    //UI
    private ImageView take_photo_activity_shutter_button;
    private CoordinatorLayout take_photo_activity_top_coordinator_layout;
    private TextView take_photo_activity_top_textview;
    private RelativeLayout take_photo_activity_relative_layout;
    private TextureView take_photo_activity_textureview;
    private CircleOverlayView circleOverlayView;

    //Misc
    private boolean okToTake;
    private int displayOrientation;
    private File file;
    private String userSentPathToFile, userSentNameOfFile, photoExtensionName;
    private boolean useFlash, useFrontFacingCamera, isActuallyUsingFrontCamera;

    //Camera
    private Camera camera;
    private TextureView.SurfaceTextureListener textureListener;
    private SurfaceTexture texture;
    private Camera.FaceDetectionListener faceDetectionListener;
    private Camera.Size imageSizes;
    private Camera.Parameters cameraParameters;

    //Custom UI Features
    private GraphicOverlay graphic_face_overlay;

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

        take_photo_activity_top_textview = (TextView) this.findViewById(
                R.id.take_photo_activity_top_textview);
        take_photo_activity_top_textview.setTag("take_photo_activity_top_textview");

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
            // TODO: 9/20/2016 refactor this back in once set to secondary activity
            //L.toast(this, "An error occurred, please try again");
            //this.finish();
        } else {
            file = FileUtilities.generateFileForImage(userSentPathToFile,
                    userSentNameOfFile, photoExtensionName);
        }
        // TODO: 9/21/2016 refactor this out once pre activity is written
        file = new File(Environment.getExternalStorageDirectory()+"/pic.jpg");
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
        if(!PermissionUtilities.getCameraPermissions(this)){
            //No permissions given
            L.toast(this, "You must enable camera permissions to use this feature");
            this.finish();
        }

        textureListener = new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                //open your camera here
                //TakePhotoActivity.this.texture = texture;
                setupCamera();
                camera.startPreview();
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
        faceDetectionListener = new Camera.FaceDetectionListener() {
            @Override
            public void onFaceDetection(Camera.Face[] faces, Camera camera) {
                try {
                    if(faces.length == 1){
                        enableCamera(true);
                    } else {
                        enableCamera(false);
                    }
                } catch (Exception e){}
            }
        };
    }

    /**
     * Enable or disable the camera functionality
     * @param bool true for enable false for disable
     */
    private void enableCamera(boolean bool){
        if(bool){
            okToTake = false;
            take_photo_activity_shutter_button.setImageResource(R.drawable.shutter_blue);
            try {
                camera.unlock();
            } catch (Exception e){
                e.printStackTrace();
            }
        } else {
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
            camera.setFaceDetectionListener(faceDetectionListener);
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
            cameraParameters.setPreviewSize(availSizes.get(0).width, availSizes.get(0).height);
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
        camera.takePicture(null, null, new PhotoHandler(this));
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

                int amountToRotate = (360 - displayOrientation) % 360;
                realImage = ImageUtilities.rotate(realImage, amountToRotate);

                //Note, I am not compressing these to full quality on purpose
                boolean bo = realImage.compress(Bitmap.CompressFormat.JPEG, 92, fos);

                fos.close();

                //Success
                // TODO: 9/20/2016 refactor this back in once activity before it
                //successMethod();

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
        resultIntent.putExtra(PGMacUtilitiesConstants.TAG_SELF_PHOTO_URI,
                StringUtilities.convertAndroidUriToString(uri));
        setResult(Activity.RESULT_OK, resultIntent);
        TakePhotoActivity.this.finish();
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
    }
}
