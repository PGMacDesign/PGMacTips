package com.pgmacdesign.pgmacutilities.utilities;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Looper;
import android.util.ArrayMap;
import android.util.Base64;
import android.util.Log;

import java.lang.reflect.Field;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by pmacdowell on 8/15/2016.
 */
public class MiscUtilities {
    public static void printOutMyHashKey(Context context, String packageName){
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(
                    packageName,
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the current Activity. NOTE! As of 4.0+, this may throw errors
     * @return
     */
    public static Activity getActivity() {
        try {
            Class activityThreadClass = Class.forName("android.app.ActivityThread");
            Object activityThread = activityThreadClass.getMethod("currentActivityThread").invoke(null);
            Field activitiesField = activityThreadClass.getDeclaredField("mActivities");
            activitiesField.setAccessible(true);
            try {
                HashMap activities = (HashMap) activitiesField.get(activityThread);
                for (Object activityRecord : activities.values()) {
                    Class activityRecordClass = activityRecord.getClass();
                    Field pausedField = activityRecordClass.getDeclaredField("paused");
                    pausedField.setAccessible(true);
                    if (!pausedField.getBoolean(activityRecord)) {
                        Field activityField = activityRecordClass.getDeclaredField("activity");
                        activityField.setAccessible(true);
                        Activity activity = (Activity) activityField.get(activityRecord);
                        return activity;
                    }
                }
            } catch (Exception e1){
                try {
                    android.support.v4.util.ArrayMap activities = (android.support.v4.util.ArrayMap) activitiesField.get(activityThread);
                    for (Object activityRecord : activities.values()) {
                        Class activityRecordClass = activityRecord.getClass();
                        Field pausedField = activityRecordClass.getDeclaredField("paused");
                        pausedField.setAccessible(true);
                        if (!pausedField.getBoolean(activityRecord)) {
                            Field activityField = activityRecordClass.getDeclaredField("activity");
                            activityField.setAccessible(true);
                            Activity activity = (Activity) activityField.get(activityRecord);
                            return activity;
                        }
                    }
                } catch (Exception e2){
                    ArrayMap activities = (ArrayMap) activitiesField.get(activityThread);
                    for (Object activityRecord : activities.values()) {
                        Class activityRecordClass = activityRecord.getClass();
                        Field pausedField = activityRecordClass.getDeclaredField("paused");
                        pausedField.setAccessible(true);
                        if (!pausedField.getBoolean(activityRecord)) {
                            Field activityField = activityRecordClass.getDeclaredField("activity");
                            activityField.setAccessible(true);
                            Activity activity = (Activity) activityField.get(activityRecord);
                            return activity;
                        }
                    }
                }
            }
        } catch (Exception e){
            return null;
        }
        return null;
    }

    /**
     * This class will determine if the current loop being run is on the main thread or not
     * @return boolean, true if on main ui thread, false if not
     */
    public static boolean isRunningOnMainThread() {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Checks a list for either being empty or containing objects within it
     * @param myList List to check
     * @param <T> T extends object
     * @return Boolean, true if it is null or empty, false it if is not
     */
    public static <T extends Object> boolean isListNullOrEmpty(List<T> myList){
        if(myList == null){
            return true;
        }
        if(myList.size() <= 0){
            return true;
        }
        return false;
    }

    /**
     * Checks a map for either being empty or containing objects within it
     * @param myMap map to check
     * @param <T> T extends object
     * @return Boolean, true if it is null or empty, false it if is not
     */
    public static <T extends Object> boolean isMapNullOrEmpty(Map<T, T> myMap){
        if(myMap == null){
            return true;
        }
        if(myMap.size() <= 0){
            return true;
        }
        return false;
    }
}
