package com.pgmacdesign.pgmacutilities.enhancedphotoclasses;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.TextureView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.pgmacdesign.pgmacutilities.R;
import com.pgmacdesign.pgmacutilities.nonutilities.PGMacUtilitiesConstants;
import com.pgmacdesign.pgmacutilities.utilities.CameraMediaUtilities;
import com.pgmacdesign.pgmacutilities.utilities.FileUtilities;
import com.pgmacdesign.pgmacutilities.utilities.L;
import com.pgmacdesign.pgmacutilities.utilities.PermissionUtilities;
import com.pgmacdesign.pgmacutilities.utilities.StringUtilities;
import com.pgmacdesign.pgmacutilities.utilities.SystemUtilities;

import java.io.File;
import java.io.FileOutputStream;

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

    //Misc
    private boolean okToTake;
    private File file;
    private String userSentPathToFile, userSentNameOfFile, photoExtensionName;
    private boolean useFlash, useFrontFacingCamera;

    //Camera
    private Camera camera;
    private TextureView.SurfaceTextureListener textureListener;
    private SurfaceTexture surface;
    private Camera.FaceDetectionListener faceDetectionListener;

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
    }

    /**
     * Init any Misc variables
     */
    private void initVariables(){
        okToTake = true;

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
                TakePhotoActivity.this.surface = surface;
                setupCamera();
            }
            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
                // Transform you image captured size according to the surface width and height
            }
            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                return false;
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
            camera = Camera.open(cameraId);
            camera.setPreviewTexture(surface);
            camera.setFaceDetectionListener(faceDetectionListener);
        } catch (Exception e){
            e.printStackTrace();
            L.toast(this, "An error occurred trying to open your camera");
            this.finish();
        }

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
        L.l(298);
        camera.startPreview();
        L.l(300);
        camera.takePicture(null, null, new PhotoHandler(this));
        L.l(302);
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
                FileOutputStream fos = new FileOutputStream(file);
                fos.write(data);
                fos.close();
                //Success
                // TODO: 9/20/2016 refactor this back in once activity before it
                L.m("SUCCESS!");
                //successMethod();

            } catch (Exception error) {
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
}
