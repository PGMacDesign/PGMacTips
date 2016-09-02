package com.pgmacdesign.pgmacutilities.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by pmacdowell on 9/2/2016.
 */
public class ServiceFlash extends Service {
    private boolean isFlashOn = false;
    private Camera camera;
    Context context;
    PackageManager pm;


    @Override
    public void onCreate() {
        context = getApplicationContext();
        super.onCreate();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        pm = context.getPackageManager();

        if (!pm.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            Log.e("err", "Device has no camera!");

            return 0;
        }

        try {
            camera = Camera.open();
            final Camera.Parameters p = camera.getParameters();

            // turn flash on
            if (isFlashOn) {
                Log.i("info", "torch is turned off!");
                p.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                camera.setParameters(p);
                isFlashOn = false;
            } else {
                Log.i("info", "torch is turned on!");
                p.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                camera.setParameters(p);
                isFlashOn = true;
            }
        } catch (Exception e){}
        return super.onStartCommand(intent, flags, startId);

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }
}