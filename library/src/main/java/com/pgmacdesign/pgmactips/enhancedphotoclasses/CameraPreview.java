package com.pgmacdesign.pgmactips.enhancedphotoclasses;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.hardware.Camera;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import com.pgmacdesign.pgmactips.adaptersandlisteners.OnTaskCompleteListener;
import com.pgmacdesign.pgmactips.utilities.L;

import java.io.IOException;
import java.util.List;

/**
 * Created by pmacdowell on 9/1/2016.
 */
@Deprecated
class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {

    private Camera camera;
    private SurfaceHolder surfaceHolder;
    private Context context;
    int width,w,h;
    Camera.Parameters parameters;
    private SurfaceView preview=null;
    private OnTaskCompleteListener camList;
    private boolean inPreview=false;
    private boolean cameraConfigured=false;
    private Activity activity;


    public CameraPreview(Context context, Activity activity,
                         Camera camera, OnTaskCompleteListener cam_listener) {
        super(context);
        this.activity = activity;
        this.camera = camera;
        this.context = context;
        this.camList = cam_listener;
        surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder,
                               int format, int width,
                               int height) {
        L.m("56 in cameraPreview");
        try {
            this.surfaceHolder = holder;
            if(camera==null){
                try {
                    camera = camera.open(1);
                }catch(Exception ex){
                    ex.printStackTrace();
                }
            }
            determineDisplayOrientation();
            L.l(68);
//            camera = Camera.open(1);
//            parameters = camera.getParameters();
        } catch (Exception exception) {
            L.l(71);
            exception.printStackTrace();
        }
        initPreview(width, height);
        startPreview();

    }

    private void initPreview(int width, int height) {
        if (camera!=null && surfaceHolder.getSurface()!=null) {
            try {
                camera.setPreviewDisplay(surfaceHolder);
            }
            catch (Throwable t) {
                t.printStackTrace();
                Toast.makeText(context, "Error occured please try again later", Toast.LENGTH_SHORT).show();
            }

            if (!cameraConfigured) {
                Camera.Parameters parameters=camera.getParameters();
                Camera.Size bestPreviewSize = determineBestPreviewSize(parameters);
                Camera.Size bestPictureSize = determineBestPictureSize(parameters);

                if (bestPreviewSize!=null) {
                    parameters.setPreviewSize(bestPreviewSize.width, bestPreviewSize.height);
                    parameters.setPictureSize(bestPictureSize.width, bestPictureSize.height);
                    camera.setParameters(parameters);
                    cameraConfigured=true;
                }
            }
        }
    }

    private void startPreview() {
        if (cameraConfigured && camera!=null) {
            camera.startPreview();
            inPreview=true;
        }else{
            Toast.makeText(context, "Error occured please try again later", Toast.LENGTH_SHORT).show();
        }
    }

    private Camera.Size getBestPreviewSize(int width, int height,
                                           Camera.Parameters parameters) {
        Camera.Size result=null;

        for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
            if (size.width<=width && size.height<=height) {
                if (result==null) {
                    result=size;
                }
                else {
                    int resultArea=result.width*result.height;
                    int newArea=size.width*size.height;

                    if (newArea>resultArea) {
                        result=size;
                    }
                }
            }
        }
        Log.e("Check Camera", "camera size " + result.width + " " + result.height);
        return(result);
    }
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        setWillNotDraw(false);

    }
//        this.surfaceHolder = holder;
//        determineDisplayOrientation();
//        try{
//            setupCamera();
//        }catch(Exception ex){
//            Toast.makeText(context, "Error occured please try again later", Toast.LENGTH_SHORT).show();
//        }
//
//
//        // LogUtils.e(" preview size " + parameters.getPictureSize().width+ parameters.getPictureSize().height);
//        startCameraPreview();


    @SuppressWarnings("deprecation")
    private void startCameraPreview() {

        try {
            camera.setPreviewDisplay(surfaceHolder);
            camera.startPreview();
        } catch (IOException exception) {
            exception.printStackTrace();
        }catch (Exception e) {
            Toast.makeText(context, "An error occurred, please try again",
                    Toast.LENGTH_SHORT).show();
        }

    }

    private synchronized void stopCameraPreview() {
        try {
            if (camera != null) {
                camera.stopPreview();
                camera.setPreviewCallback(null);
                camera.release();
                camera = null;
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder arg0) {
        stopCameraPreview();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        h = MeasureSpec.getSize(heightMeasureSpec);
        w = MeasureSpec.getSize(widthMeasureSpec);
        setMeasuredDimension(w,h);
    }



    private void initializeCamera(int widthMeasureSpec, int heightMeasureSpec) {
        int width =resolveSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        int height =resolveSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        setMeasuredDimension(width, height);
        List<Camera.Size> mSupportedPreviewSizes = camera.getParameters().getSupportedPreviewSizes();
        Camera.Size optimalPreviewSize = getOptimalPreviewSize(mSupportedPreviewSizes, width, height);
    }

    public void determineDisplayOrientation() {
        int cameraId = 0;
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        int cameraCount = Camera.getNumberOfCameras();
        for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
            Camera.getCameraInfo(camIdx, cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {

                cameraId = camIdx;

            }else if(cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT){
                cameraId = camIdx;
            }
        }

        Camera.getCameraInfo(0, cameraInfo);

        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        int degrees  = 0;
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

        int displayOrientation = 0;

        if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            displayOrientation = (cameraInfo.orientation + degrees) % 360;
            displayOrientation = (360 - displayOrientation) % 360;
        } else if(cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK){
            displayOrientation = (cameraInfo.orientation - degrees + 360) % 360;
        }
        try{
            camera.setDisplayOrientation(displayOrientation);
        }catch(Exception ex){
            ex.printStackTrace();
        }

    }


    private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio=(double)h / w;

        if (sizes == null) return null;

        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;

        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }

    /**
     * Setup the camera parameters.
     */
    public void setupCamera() {
        if (camera!=null && surfaceHolder.getSurface()!=null) {
            try {
                camera.setPreviewDisplay(surfaceHolder);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        parameters = camera.getParameters();

        Camera.Size bestPreviewSize = determineBestPreviewSize(parameters);
        Camera.Size bestPictureSize = determineBestPictureSize(parameters);


        //parameters.setPreviewSize(bestPreviewSize.width, bestPreviewSize.height);
        parameters.setPictureSize(bestPictureSize.width,bestPictureSize.height);
        Log.e("best "," " +bestPreviewSize.width + bestPreviewSize.height);
        camera.setParameters(parameters);
    }

    private Camera.Size determineBestPreviewSize(Camera.Parameters parameters) {
        List<Camera.Size> sizes = parameters.getSupportedPreviewSizes();
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        width= metrics.widthPixels;
        return determineBestSize(sizes, width);
    }

    private Camera.Size determineBestPictureSize(Camera.Parameters parameters) {
        List<Camera.Size> sizes = parameters.getSupportedPictureSizes();

        return determineBestSize(sizes, width);
    }

    protected Camera.Size determineBestSize(List<Camera.Size> sizes, int widthThreshold) {
        Camera.Size bestSize = null;

        for (Camera.Size currentSize : sizes) {
            Log.e("best "," " +currentSize.width + currentSize.height);
            boolean isDesiredRatio = (currentSize.width / 4) == (currentSize.height / 3);
            boolean isBetterSize = (bestSize == null || currentSize.width > bestSize.width);
            boolean isInBounds = currentSize.width <= width;

            if (isDesiredRatio && isInBounds && isBetterSize) {
                bestSize = currentSize;
            }
        }

        if (bestSize == null) {


            return sizes.get(0);
        }

        return bestSize;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);

        Log.e(" ","onDraw called");
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(10);
        TypedValue tv = new TypedValue();
        int actionBarHeight = 0;
        if (context.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true))
        {
            actionBarHeight= TypedValue.complexToDimensionPixelSize(
                    tv.data,getResources().getDisplayMetrics());
        }
        int top = new Double(getHeight()*.042).intValue();

        int left= new Double(getWidth()*.1775).intValue();

        RectF oval1 = null;
        oval1 = new RectF(left, actionBarHeight+(top), getWidth()-(left),getWidth()+(top));
        canvas.drawOval(oval1, paint);
    }




}
