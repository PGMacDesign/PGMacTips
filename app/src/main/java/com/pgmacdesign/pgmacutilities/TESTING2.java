package com.pgmacdesign.pgmacutilities;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCaptureSession.CaptureCallback;
import android.hardware.camera2.CameraCaptureSession.StateCallback;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureFailure;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.DngCreator;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.media.MediaScannerConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.pgmacdesign.pgmacutilities.utilities.DatabaseUtilities;
import com.pgmacdesign.pgmacutilities.utilities.L;
import com.pgmacdesign.pgmacutilities.utilities.SystemUtilities;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;

/**
 * Created by pmacdowell on 9/19/2016.
 */
public class TESTING2 extends AppCompatActivity implements View.OnClickListener, TextureView.SurfaceTextureListener {

    //Library Objects
    private DatabaseUtilities dbUtilities;

    //UI
    private ImageView testing_layout2_shutter_button;
    private TextView testing_layout2_textview;
    private RelativeLayout testing_layout2_relative_layout;
    private TextureView testing_layout2_textureview;

    //Misc Objects
    private CameraDevice mCamera;
    private CameraCaptureSession mSession;
    private Surface mRawCaptureSurface, mJpegCaptureSurface, mPreviewSurface;
    private CaptureResult mPendingResult;
    private Size mPreviewSize;
    private File mPhotoDir;
    private CameraCharacteristics mCharacteristics;

    //Raw objects
    private int mCaptureImageFormat;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preInit();
        setContentView(R.layout.take_photo_activity_api21);
        initUI();
        initVariables();
    }

    //Called before the content view is set
    private void preInit(){
        //No title here. Full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        if(SystemUtilities.userHasKitKatOrHigher()){
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
                    View.SYSTEM_UI_FLAG_LOW_PROFILE |
                    View.SYSTEM_UI_FLAG_FULLSCREEN);
        } else if(SystemUtilities.userHasJellyBeanOrHigher()) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LOW_PROFILE |
                    View.SYSTEM_UI_FLAG_FULLSCREEN);
        } else {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LOW_PROFILE);
        }

    }
    private void initUI() {
        testing_layout2_textureview = (TextureView) this.findViewById(
                R.id.take_photo_activity_api21_textureview);
        testing_layout2_textureview.setTag("testing_layout2_textureview");
        testing_layout2_relative_layout = (RelativeLayout) this.findViewById(
                R.id.take_photo_activity_api21_relative_layout);
        testing_layout2_relative_layout.setTag("testing_layout2_relative_layout");
        testing_layout2_textview = (TextView) this.findViewById(
                R.id.take_photo_activity_api21_top_textview);
        testing_layout2_textview.setTag("testing_layout2_textview");
        testing_layout2_shutter_button = (ImageView) this.findViewById(
                R.id.take_photo_activity_api21_shutter_button);
        testing_layout2_shutter_button.setTag("testing_layout2_shutter_button");

        testing_layout2_shutter_button.setOnClickListener(this);

        testing_layout2_textureview.setSurfaceTextureListener(this);

    }

    private void initVariables() {
        File picturesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        mPhotoDir = new File(picturesDir, getString(R.string.app_name));
        if (!mPhotoDir.exists()) {
            mPhotoDir.mkdir();

        }

    }

    @Override
    public void onClick(View view) {
        String str = null;
        try {
            str = (String) view.getTag();
        } catch (Exception e){}
        if(str != null){
            if(str.equalsIgnoreCase("testing_layout2_shutter_button")){
                //Take picture
            }
        }
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i1) {
        try {
            initCamera(surfaceTexture);
        } catch (CameraAccessException e) {
            L.m("Error!");
        } catch (NullPointerException e1){
            e1.printStackTrace();
        }
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
        if (mCamera != null) {
            mCamera.close();
            mCamera = null;
        }
        mSession = null;
        return true;
    }

    private void initCamera(SurfaceTexture surface) throws CameraAccessException {
        CameraManager cm = (CameraManager) getSystemService(CAMERA_SERVICE);

        // get ID of rear-facing camera
        String[] cameraIds = cm.getCameraIdList();
        String cameraId = null;
        CameraCharacteristics cc = null;
        for (String id : cameraIds) {
            cc = cm.getCameraCharacteristics(id);
            if (cc.get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_BACK) {
                cameraId = id;
                break;
            }
        }
        if (cameraId == null) {
            throw new CameraAccessException(CameraAccessException.CAMERA_ERROR, "Couldn't find suitable camera");
        }

        mCharacteristics = cc;
        StreamConfigurationMap streamConfigs = cc.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);

        // determine supported output formats..
        boolean supportsRaw = false, supportsJpeg = false;
        for (int format : streamConfigs.getOutputFormats()) {
            if (format == ImageFormat.RAW_SENSOR) {
                supportsRaw = true;
            } else if (format == ImageFormat.JPEG) {
                supportsJpeg = true;
            }
        }
        if (supportsRaw) {
            mCaptureImageFormat = ImageFormat.RAW_SENSOR;
        } else if (supportsJpeg) {
            mCaptureImageFormat = ImageFormat.JPEG;
        } else {
            throw new CameraAccessException(CameraAccessException.CAMERA_ERROR, "Couldn't find supported image format");
        }

        // alternatively, make a way for the user to select a capture size..
        Size rawSize = streamConfigs.getOutputSizes(ImageFormat.RAW_SENSOR)[0];
        Size jpegSize = streamConfigs.getOutputSizes(ImageFormat.JPEG)[0];

        // find the preview size that best matches the aspect ratio of the camera sensor..
        Size[] previewSizes = streamConfigs.getOutputSizes(SurfaceTexture.class);
        mPreviewSize = findOptimalPreviewSize(previewSizes, rawSize);
        if (mPreviewSize == null) {
            return;
        }

        // set up capture surfaces and image readers..
        mPreviewSurface = new Surface(surface);
        ImageReader rawReader = ImageReader.newInstance(rawSize.getWidth(), rawSize.getHeight(),
                ImageFormat.RAW_SENSOR, 1);
        rawReader.setOnImageAvailableListener(new ImageReader.OnImageAvailableListener() {
            @Override
            public void onImageAvailable(ImageReader reader) {
                new SaveRawTask(TESTING2.this, mPhotoDir, reader.acquireLatestImage(),
                        mCharacteristics, mPendingResult).execute();
            }
        }, null);
        mRawCaptureSurface = rawReader.getSurface();
        ImageReader jpegReader = ImageReader.newInstance(jpegSize.getWidth(), jpegSize.getHeight(),
                ImageFormat.JPEG, 1);
        jpegReader.setOnImageAvailableListener(new ImageReader.OnImageAvailableListener() {
            @Override
            public void onImageAvailable(ImageReader reader) {
                new SaveJpegTask(TESTING2.this, mPhotoDir, reader.acquireLatestImage()).execute();
            }
        }, null);
        mJpegCaptureSurface = jpegReader.getSurface();

        cm.openCamera(cameraId, new CameraDevice.StateCallback() {
            @Override
            public void onOpened(@NonNull CameraDevice camera) {
                mCamera = camera;
                initPreview();
            }

            @Override
            public void onDisconnected(@NonNull CameraDevice camera) {

            }

            @Override
            public void onError(@NonNull CameraDevice camera, int error) {

            }
        }, null);
    }

    private void initPreview() {
        // scale preview size to fill screen width
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        float previewRatio = mPreviewSize.getWidth() / ((float) mPreviewSize.getHeight());
        int previewHeight = Math.round(screenWidth * previewRatio);
        ViewGroup.LayoutParams params = testing_layout2_textureview.getLayoutParams();
        params.width = screenWidth;
        params.height = previewHeight;

        List<Surface> surfaces = Arrays.asList(mPreviewSurface, mRawCaptureSurface, mJpegCaptureSurface);
        try {
            mCamera.createCaptureSession(surfaces, new StateCallback() {
                @Override
                public void onConfigured(CameraCaptureSession session) {
                    mSession = session;
                    updatePreview();
                }

                @Override
                public void onConfigureFailed(CameraCaptureSession session) {

                }
            }, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * Call this whenever some camera control changes (e.g., focus distance, white balance, etc) that should affect the preview
     */
    private void updatePreview() {
        try {
            if (mCamera == null || mSession == null) {
                return;
            }
            CaptureRequest.Builder builder = mCamera.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            builder.addTarget(mPreviewSurface);

            builder.set(CaptureRequest.CONTROL_AF_MODE, CameraMetadata.CONTROL_AF_MODE_OFF);

//            builder.set(CaptureRequest.LENS_FOCUS_DISTANCE, ...)
//            builder.set(CaptureRequest.SENSOR_SENSITIVITY, ...)
//            builder.set(CaptureRequest.CONTROL_AWB_MODE, ...)
//            builder.set(CaptureRequest.CONTROL_EFFECT_MODE, ...)
//            builder.set(CaptureRequest.CONTROL_AE_EXPOSURE_COMPENSATION, ...)
//            etc...

            mSession.setRepeatingRequest(builder.build(), new CaptureCallback() {
                @Override
                public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result) {
                    // if desired, we can get updated auto focus & auto exposure values here from 'result'
                }
            }, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * This should be triggered by a capture button press or something similar
     */
    public void capture() {
        try {
            CaptureRequest.Builder builder = mCamera.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            builder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);

            // we probably don't want to be auto focusing while an image is being captured.
            builder.set(CaptureRequest.CONTROL_AF_MODE, CameraMetadata.CONTROL_AF_MODE_OFF);
            builder.set(CaptureRequest.CONTROL_AF_TRIGGER, CameraMetadata.CONTROL_AF_TRIGGER_IDLE);

            // set options here that the user has changed
//            builder.set(CaptureRequest.LENS_FOCUS_DISTANCE, ...)
//            builder.set(CaptureRequest.CONTROL_AWB_MODE, ...)
//            builder.set(CaptureRequest.CONTROL_EFFECT_MODE, ...)
//            builder.set(CaptureRequest.CONTROL_AE_EXPOSURE_COMPENSATION, ...)
//            etc...

            builder.set(CaptureRequest.LENS_OPTICAL_STABILIZATION_MODE, CameraMetadata.LENS_OPTICAL_STABILIZATION_MODE_ON);

            if (mCaptureImageFormat == ImageFormat.JPEG) {
                builder.addTarget(mJpegCaptureSurface);
                builder.set(CaptureRequest.JPEG_QUALITY, (byte) 100);
            } else {
                builder.addTarget(mRawCaptureSurface);
                builder.set(CaptureRequest.STATISTICS_LENS_SHADING_MAP_MODE, CaptureRequest.STATISTICS_LENS_SHADING_MAP_MODE_ON);
            }

            mSession.capture(builder.build(), new CaptureCallback() {
                @Override
                public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result) {
                    // save this, as it's needed to create raw files
                    mPendingResult = result;
                }

                @Override
                public void onCaptureFailed(CameraCaptureSession session, CaptureRequest request, CaptureFailure failure) {
                    super.onCaptureFailed(session, request, failure);
                    L.m("Image capture failed");
                }
            }, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private static class SaveRawTask extends AsyncTask<Void, Void, Boolean> {

        private WeakReference<Context> mContextRef;
        private File mFile;
        private Image mImage;
        private DngCreator mDngCreator;

        public SaveRawTask(Context context, File dir, Image image, CameraCharacteristics characteristics, CaptureResult metadata) {
            mContextRef = new WeakReference<>(context);
            mFile = new File(dir, System.currentTimeMillis() + ".dng");
            mImage = image;
            mDngCreator = new DngCreator(characteristics, metadata);
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                mDngCreator.writeImage(new FileOutputStream(mFile), mImage);
                mDngCreator.close();
                mImage.close();
                return true;
            } catch (IOException e) {
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            Context context = mContextRef.get();
            if (context != null) {
                if (result) {
                    MediaScannerConnection.scanFile(context, new String[]{mFile.getAbsolutePath()}, null, null);
                    Toast.makeText(context, "Image captured!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Error saving image", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private static class SaveJpegTask extends AsyncTask<Void, Void, Boolean> {

        private File mFile;
        private Image mImage;
        private WeakReference<Context> mContextRef;

        public SaveJpegTask(Context context, File dir, Image image) {
            mContextRef = new WeakReference<>(context);
            mFile = new File(dir, System.currentTimeMillis() + ".jpg");
            mImage = image;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            ByteBuffer buffer = mImage.getPlanes()[0].getBuffer();
            byte[] bytes = new byte[buffer.capacity()];
            buffer.get(bytes);
            mImage.close();
            try {
                new FileOutputStream(mFile).write(bytes);
                return true;
            } catch (IOException e) {
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            Context context = mContextRef.get();
            if (context != null) {
                if (result) {
                    MediaScannerConnection.scanFile(context, new String[]{mFile.getAbsolutePath()}, null, null);
                    Toast.makeText(context, "Image captured!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Error saving image", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    /**
     * Given a target size for raw output, search available preview sizes for one with a similar
     * aspect ratio that does not exceed screen size.
     */
    private Size findOptimalPreviewSize(Size[] sizes, Size targetSize) {
        float targetRatio = targetSize.getWidth() * 1.0f / targetSize.getHeight();
        float tolerance = 0.1f;
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int maxPixels = screenWidth * Math.round(screenWidth * targetRatio);
        int width, height;
        float ratio;
        for (Size size : sizes) {
            width = size.getWidth();
            height = size.getHeight();
            if (width * height <= maxPixels) {
                ratio = ((float) width) / height;
                if (Math.abs(ratio - targetRatio) < tolerance) {
                    return size;
                }
            }
        }
        return null;
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1) {
        //Not used
    }
    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
        //Not used
    }
}
