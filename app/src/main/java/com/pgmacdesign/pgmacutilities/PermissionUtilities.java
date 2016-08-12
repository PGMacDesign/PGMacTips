package com.pgmacdesign.pgmacutilities;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

/**
 * Created by pmacdowell on 8/12/2016.
 */
public class PermissionUtilities {

    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE =
            PGMacUtilitiesConstants.TAG_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE;
    private static final int MY_PERMISSIONS_REQUEST_CAMERA =
            PGMacUtilitiesConstants.TAG_PERMISSIONS_REQUEST_CAMERA;
    private static final int MY_PERMISSIONS_REQUEST_ALL =
            PGMacUtilitiesConstants.TAG_PERMISSIONS_REQUEST_ALL;

    private static final int PERMISSIONS_REQUEST_CODE_INT =
            PGMacUtilitiesConstants.TAG_PERMISSIONS_REQUEST_CODE_INT;

    private permissionsEnum whichPerm;
    private permissionsEnum[] whichPerms;

    private Activity activity;
    private OnTaskCompleteListener listener;
    private int numRequests, currentAttemptNumber;


    public PermissionUtilities(Activity activity, OnTaskCompleteListener listener){
        this.activity = activity;
        this.listener = listener;
        this.numRequests = 0;
        this.currentAttemptNumber = 0;
    }
    public PermissionUtilities(Activity activity, OnTaskCompleteListener listener, int numRequests){
        this.activity = activity;
        this.listener = listener;
        this.numRequests = numRequests;
        this.currentAttemptNumber = 0;
    }

    public enum permissionsEnum {
        ACCESS_COARSE_LOCATION("Access Coarse Location", "Used in determining your location",
                Manifest.permission.ACCESS_COARSE_LOCATION, 20, false),
        ACCESS_FINE_LOCATION("Access Fine Location", "Used in determining your location",
                Manifest.permission.ACCESS_FINE_LOCATION, 21, false),
        WRITE_EXTERNAL_STORAGE("Write External Storage", "Allows pictures to be taken, edited, and cropped",
                Manifest.permission.WRITE_EXTERNAL_STORAGE, 22, false),
        CAMERA("Camera", "Allows pictures to be taken with the camera",
                Manifest.permission.CAMERA, 23, false),
        READ_EXTERNAL_STORAGE("Read External Storage", "Allows gallery pictures to be accessed",
                Manifest.permission.READ_EXTERNAL_STORAGE, 24, false),
        READ_PHONE_STATE("Read Phone State", "Used for determining phone state (IE Wifi) so internet calls will not fail",
                Manifest.permission.READ_PHONE_STATE, 25, false),
        READ_CONTACTS("Read Contacts", "Used to provide you direct access to your contacts from within the app",
                Manifest.permission.READ_CONTACTS, 26, false),//,
        ACCESS_WIFI_STATE("Determine Wifi State", "Used in determining internet connectivity",
                Manifest.permission.ACCESS_WIFI_STATE, 27, false),
        ACCESS_NETWORK_STATE("Read Contacts", "Used in determining internet connectivity",
                Manifest.permission.ACCESS_NETWORK_STATE, 28, false),
        BLUETOOTH("Read Contacts", "Used for connection with bluetooth devices",
                Manifest.permission.BLUETOOTH, 29, false),
        BLUETOOTH_ADMIN("Read Contacts", "Used for connection with various location-based services and misc bluetooth settings",
                Manifest.permission.BLUETOOTH_ADMIN, 30, false)
        ;

        String permissionName;
        String permissionDescription;
        String permissionManifestName;
        int permissionCode;
        boolean permissionGiven;

        permissionsEnum(String permissionName, String permissionDescription,
                        String permissionManifestName, int permissionCode, boolean permissionGiven){
            this.permissionName = permissionName;
            this.permissionDescription = permissionDescription;
            this.permissionManifestName = permissionManifestName;
            this.permissionCode = permissionCode;
            this.permissionGiven = permissionGiven;
        }

        public void setPermissionGiven(boolean permissionGiven){
            this.permissionGiven = permissionGiven;
        }
        /**
         * Get the permissions Description
         * @return
         */
        public String getPermissionDescription(){
            return this.permissionDescription;
        }

        /**
         * Get the permissions name
         * @return
         */
        public String getPermissionName(){
            return this.permissionName;
        }

        /**
         * Get the permissions manifest dedicated name
         * @return
         */
        public String getPermissionManifestName(){
            return this.permissionManifestName;
        }

        /**
         * Get the permissions code
         * @return
         */
        public int getPermissionCode(){
            return this.permissionCode;
        }
    };

    /**
     * Request permissions
     * @param whichPerms array of permissions to request
     */
    public void startPermissionsRequest(permissionsEnum[] whichPerms){
        this.numRequests = whichPerms.length;
        for(int i = 0; i < this.numRequests; i++){
            boolean bool = requestPermission(whichPerms[i]);
            whichPerms[i].setPermissionGiven(bool);
        }
    }

    /**
     * Overloaded method to allow for one permissions
     * @param whichPerm single permission to request
     */
    public void startPermissionsRequest(permissionsEnum whichPerm){
        if(whichPerm != null){
            permissionsEnum[] somePerms = {whichPerm};
            this.startPermissionsRequest(somePerms);
        } else {
            //Error
        }

    }
    public boolean requestPermission(permissionsEnum whichPerm){
        try {
            if(ContextCompat.checkSelfPermission(activity, whichPerm.getPermissionManifestName())
                    != PackageManager.PERMISSION_GRANTED){

                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                        whichPerm.getPermissionManifestName())) {

                    L.Toast(activity, whichPerm.getPermissionDescription());
                    ActivityCompat.requestPermissions(activity,
                            new String[]{whichPerm.getPermissionManifestName()},
                            PERMISSIONS_REQUEST_CODE_INT);

                } else {

                    // No explanation needed, we can request the permission.
                    ActivityCompat.requestPermissions(activity,
                            new String[]{whichPerm.getPermissionManifestName()},
                            PERMISSIONS_REQUEST_CODE_INT);

                }
                return false;
            } else {
                return true;
            }
        } catch (NullPointerException e1){
            e1.printStackTrace();
        } catch (IllegalArgumentException e2){
            e2.printStackTrace();
        } catch (Exception e0){
            e0.printStackTrace();
        }
        return false;
    }

    /**
     * Combining all permissions together into one
     * @param activity
     * @return
     */
    public static boolean getAllRequiredPermissions(Activity activity){
        try {
            if (Build.VERSION.SDK_INT >= 23) {
                if (ContextCompat.checkSelfPermission(activity,
                        Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                        ||
                        ContextCompat.checkSelfPermission(activity,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                        ||
                        ContextCompat.checkSelfPermission(activity,
                                Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED){

                    ActivityCompat.requestPermissions(activity,
                            new String[]{Manifest.permission.CAMERA,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                    Manifest.permission.READ_CONTACTS},
                            MY_PERMISSIONS_REQUEST_ALL);
                } else {
                    return true;
                }
            } else {
                return true;
            }
        } catch (Exception e){}
        return false;
    }
    /**
     * Combining Write external storage and camera permissions together into one
     * @param activity
     * @return
     */
    public static boolean getImagePermissions(Activity activity){
        try {
            if (Build.VERSION.SDK_INT >= 23) {
                if (ContextCompat.checkSelfPermission(activity,
                        Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                        ||
                        ContextCompat.checkSelfPermission(activity,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(activity,
                            new String[]{Manifest.permission.CAMERA,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            MY_PERMISSIONS_REQUEST_CAMERA);
                } else {
                    return true;
                }
            } else {
                return true;
            }
        } catch (Exception e){}
        return false;
    }

    /**
     * Checks for camera permissions
     * @param activity
     * @return
     */
    public static boolean getCameraPermissions(Activity activity){
        try {
            if (Build.VERSION.SDK_INT >= 23) {
                if (ContextCompat.checkSelfPermission(activity,
                        Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(activity,
                            new String[]{Manifest.permission.CAMERA},
                            MY_PERMISSIONS_REQUEST_CAMERA);
                } else {
                    return true;
                }
            } else {
                return true;
            }
        } catch (Exception e){}
        return false;
    }

    /**
     * Checks for write and read external storage permissions
     * @param activity
     * @return
     */
    public static boolean getStoragePermissions(Activity activity){
        try {
            if (Build.VERSION.SDK_INT >= 23){
                //Storage Permissions Next
                if (ContextCompat.checkSelfPermission(activity,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(activity,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
                } else {
                    return true;
                }
            } else {
                return true;
            }
        } catch (Exception e){}
        return false;
    }

}
