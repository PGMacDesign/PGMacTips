package com.pgmacdesign.pgmactips.utilities;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.pgmacdesign.pgmactips.misc.PGMacTipsConstants;

import java.util.ArrayList;
import java.util.List;

import static com.pgmacdesign.pgmactips.misc.PGMacTipsConstants.TAG_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE;

/**
 * Created by pmacdowell on 8/12/2016.
 */
public class PermissionUtilities {

    public static interface permissionsFinishedListener {
        public void onPermissionsDenied(permissionsEnum[] perms);
        public void onPermissionsGranted(permissionsEnum[] perms);
        public void onPermissionsRationale(permissionsEnum[] perms);
    }

    //Write external Storage
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE =
            PGMacTipsConstants.TAG_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE;
    //Read External Storage
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE =
            PGMacTipsConstants.TAG_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE;
    //Read Phone State
    private static final int MY_PERMISSIONS_REQUEST_READ_PHONE_STATE =
            PGMacTipsConstants.TAG_PERMISSIONS_REQUEST_READ_PHONE_STATE;
    //Camera
    private static final int MY_PERMISSIONS_REQUEST_CAMERA =
            PGMacTipsConstants.TAG_PERMISSIONS_REQUEST_CAMERA;
    //Gallery
//    private static final int MY_PERMISSIONS_REQUEST_GALLERY =
//            PGMacTipsConstants.TAG_PERMISSIONS_REQUEST_GALLERY;
    //Contacts
    private static final int MY_PERMISSIONS_REQUEST_CONTACTS =
            PGMacTipsConstants.TAG_PERMISSIONS_REQUEST_CONTACTS;
    //Network State
    private static final int MY_PERMISSIONS_ACCESS_NETWORK_STATE =
            PGMacTipsConstants.TAG_PERMISSIONS_ACCESS_NETWORK_STATE;
    //Wifi State
    private static final int MY_PERMISSIONS_ACCESS_WIFI_STATE =
            PGMacTipsConstants.TAG_PERMISSIONS_ACCESS_WIFI_STATE;
    //Location (GPS)
    private static final int MY_PERMISSIONS_ACCESS_FINE_LOCATION =
            PGMacTipsConstants.TAG_PERMISSIONS_ACCESS_FINE_LOCATION;
    //Location (GPS)
    private static final int MY_PERMISSIONS_ACCESS_COARSE_LOCATION =
            PGMacTipsConstants.TAG_PERMISSIONS_ACCESS_COARSE_LOCATION;
    //Boot Completed
    private static final int MY_PERMISSIONS_RECEIVE_BOOT_COMPLETED =
            PGMacTipsConstants.TAG_PERMISSIONS_RECEIVE_BOOT_COMPLETED;
    //All Permissions Request
    private static final int MY_PERMISSIONS_REQUEST_ALL =
            PGMacTipsConstants.TAG_PERMISSIONS_REQUEST_ALL;

    private static final int PERMISSIONS_REQUEST_BASE_CALL =
            PGMacTipsConstants.TAG_PERMISSIONS_REQUEST_BASE_CALL;


    private boolean bypassShowRationale, userCheckedNeverRemindBox;

    private Activity activity;
    private int numRequests, currentAttemptNumber;

    private static PermissionUtilities staticPermissionUtility;
    /**
     * Static shortcut to allow for quick calls if a recurring call is not needed
     * NOTE! Does make the actual permissions request
     * @param activity Activity in context
     * @param perms Permissions array being requested
     */
    public static void permissionsRequestShortcut(@NonNull Activity activity,
                                                  @NonNull PermissionUtilities.permissionsEnum[] perms){
        getInstance(activity).startPermissionsRequest(perms);
    }

    /**
     * Static shortcut to allow for quick calls if a recurring call is not needed. It also
     * returns a boolean if the perms have already been granted. This is a hybrid call of the
     * Shortcut request above and the check granted permissions call below.
     * NOTE! Does make the actual permissions request
     * @param activity Activity in context
     * @param perms Permissions array being requested
     */
    public static boolean permissionsRequestShortcutReturn(
            @NonNull Activity activity,
            @NonNull PermissionUtilities.permissionsEnum[] perms){
        return (getInstance(activity).startPermissionsRequest(perms));
    }

    /**
     * Static shortcut to allow for quick calls to check if perms were granted.
     * NOTE! Does NOT make the actual permissions request
     * @param activity Activity in context
     * @param perms Permissions array being requested
     */
    public static boolean checkGrantedPermissions(@NonNull Activity activity,
                                                  @NonNull PermissionUtilities.permissionsEnum[] perms){
        return (getInstance(activity).checkGrantedPermissions(perms));
    }

    public static PermissionUtilities getInstance(@NonNull Activity activity){
        if(staticPermissionUtility == null) {
            staticPermissionUtility = new PermissionUtilities(activity);
        }
        return staticPermissionUtility;
    }
    /**
     * Constructor
     * @param activity
     */
    public PermissionUtilities(@NonNull Activity activity){
        this.activity = activity;
        this.numRequests = 0;
        this.currentAttemptNumber = 0;
        this.bypassShowRationale = false;
        this.userCheckedNeverRemindBox = false;
    }


    public enum permissionsEnum {
//        ACCESS_COARSE_LOCATION("Access Coarse Location", "Used in determining your location.",
//                Manifest.permission.R,
//                MY_PERMISSIONS_ACCESS_COARSE_LOCATION, false),
        ACCESS_COARSE_LOCATION("Access Coarse Location", "Used in determining your location.",
                Manifest.permission.ACCESS_COARSE_LOCATION,
                MY_PERMISSIONS_ACCESS_COARSE_LOCATION, false),
        ACCESS_FINE_LOCATION("Access Fine Location", "Used in determining your location.",
                Manifest.permission.ACCESS_FINE_LOCATION,
                MY_PERMISSIONS_ACCESS_FINE_LOCATION, false),
        WRITE_EXTERNAL_STORAGE("Write External Storage", "Allows the ability to write to external " +
                "storage. An example of this would be to save data so that it won't be erased when " +
                "leaving the application and coming back in.",
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                TAG_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE, false),
        CAMERA("Camera", "Allows pictures to be taken with the camera.",
                Manifest.permission.CAMERA,
                MY_PERMISSIONS_REQUEST_CAMERA, false),
        READ_EXTERNAL_STORAGE("Read External Storage", "Allows the ability to read external " +
                "storage. An example of this would be allowing gallery pictures to be accessed.",
                Manifest.permission.READ_EXTERNAL_STORAGE,
                MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE, false),
        READ_PHONE_STATE("Read Phone State", "Used for determining phone state (IE Wifi) so " +
                "internet calls will not fail",
                Manifest.permission.READ_PHONE_STATE,
                MY_PERMISSIONS_REQUEST_READ_PHONE_STATE, false),
        READ_CONTACTS("Read Contacts", "Used to provide you direct access to your contacts " +
                "from within the app.",
                Manifest.permission.READ_CONTACTS,
                MY_PERMISSIONS_REQUEST_CONTACTS, false),//,
        ACCESS_WIFI_STATE("Determine Wifi State", "Used in determining internet connectivity " +
                "before making web calls.",
                Manifest.permission.ACCESS_WIFI_STATE,
                MY_PERMISSIONS_ACCESS_WIFI_STATE, false),
        ACCESS_NETWORK_STATE("Access Network State", "Used in determining internet connectivity " +
                "before making web calls.",
                Manifest.permission.ACCESS_NETWORK_STATE,
                MY_PERMISSIONS_ACCESS_NETWORK_STATE, false),
        BLUETOOTH("BlueTooth", "Used for connection with bluetooth devices.",
                Manifest.permission.BLUETOOTH, 29, false),
        BLUETOOTH_ADMIN("BlueTooth Admin", "Used for making changes to the Bluetooth settings. " +
                "Some location-based services require this.",
                Manifest.permission.BLUETOOTH_ADMIN, 30, false),
        RECEIVE_BOOT_COMPLETED("Receive Boot Completed", "Used for determining when the device " +
                "has finished rebooting. This is used to restart services that are shut off when a " +
                "phone is rebooted.",
                Manifest.permission.READ_PHONE_STATE,
                MY_PERMISSIONS_RECEIVE_BOOT_COMPLETED, false),
        ;

        String permissionName;
        String permissionDescription;
        String permissionManifestName;
        int permissionCode;
        boolean permissionGiven;

        permissionsEnum(String permissionName, String permissionDescription,
                        String permissionManifestName, int permissionCode,
                        boolean permissionGiven){
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
     * Overloaded method to allow for one permissions
     * @param whichPerm single permission to request
     * @return Returns a boolean, true if all permissions are set to true, false if not
     */
    public boolean startPermissionsRequest(@NonNull permissionsEnum whichPerm){
        if(Build.VERSION.SDK_INT < 23){
            return true;
        }
        return this.startPermissionsRequest(new permissionsEnum[]{whichPerm});
    }

    /**
     * Request permissions
     * @param whichPerms array of permissions to request. NOTE! If you send permissions
     *                   which are not requested in the manifest, you will get a ANR
     *                   error and the screen will freeze entirely, IE:
     *                   ( Wrote stack traces to '/data/anr/traces.txt' ). This same error
     *                   will be thrown if this is called in an activity before the
     *                   view is set (setContentView()).
     * @return Returns a boolean, true if all permissions are set to true, false if not
     */
    public boolean startPermissionsRequest(@NonNull permissionsEnum[] whichPerms){
        //If build version is less than marshmallow, can return true and assume yes
        if(Build.VERSION.SDK_INT < 23){
            return true;
        }


        //Vars to use
        int numPermsDenied, numPermsGranted;
        permissionsEnum[] deniedPerms = null;
        List<permissionsEnum> deniedPermsList = new ArrayList<>();

        //Iterate to check which perms have been allowed already and add them to a list
        for(permissionsEnum currentPerm : whichPerms){
            if(ContextCompat.checkSelfPermission(activity,
                    currentPerm.getPermissionManifestName())
                    != PackageManager.PERMISSION_GRANTED){
                deniedPermsList.add(currentPerm);
            }
        }

        //If no perms have been denied, return true, else, add them to the list / array
        if(deniedPermsList.size() <= 0){
            return true;
        } else {
            deniedPerms = new permissionsEnum[deniedPermsList.size()];
            for(int x = 0; x < deniedPermsList.size(); x++){
                deniedPerms[x] = deniedPermsList.get(x);
            }
        }

        //Actual requests
        this.requestPermissions(deniedPerms);

        //If this is reached, there are at least 1 denied perm
        return false;
    }

    /**
     * Class that checks if all the permissions passed in have been grated.
     * @param whichPerms  Which perms to check
     * @return True if all have been granted, false if not
     */
    public boolean checkGrantedPermissions(@NonNull permissionsEnum[] whichPerms){
        //If build version is less than marshmallow, can return true and assume yes
        if(Build.VERSION.SDK_INT < 23){
            return true;
        }

        //Vars to use
        int numPermsDenied, numPermsGranted;
        permissionsEnum[] deniedPerms = null;
        List<permissionsEnum> deniedPermsList = new ArrayList<>();

        //Iterate to check which perms have been allowed already and add them to a list
        for(permissionsEnum currentPerm : whichPerms){
            if(ContextCompat.checkSelfPermission(activity,
                    currentPerm.getPermissionManifestName())
                    != PackageManager.PERMISSION_GRANTED){
                deniedPermsList.add(currentPerm);
            }
        }

        //If no perms have been denied, return true, else, add them to the list / array
        if(deniedPermsList.size() <= 0){
            return true;
        } else {
            return false;
        }
    }

    /**
     * Requests an array of permissions. This assumes that the check for them being
     * denied was already performed
     * @param whichPerms The perms being requested (THAT HAVE ALREADY BEEN DENIED)
     */
    private void requestPermissions(@NonNull permissionsEnum[] whichPerms){
        //Handler Thread and handlers initialize:
        HandlerThread handlerThread = new HandlerThread("PermissionUtilitiesThread");
        handlerThread.start();
        Looper looper = handlerThread.getLooper();
        Handler handler = new Handler(looper);

        //List to add to if they want an explanation
        List<permissionsEnum> permsToShowExplanation = new ArrayList<>();
        List<String> permStrings = new ArrayList<>();

        for(permissionsEnum currentPerm : whichPerms){
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    currentPerm.getPermissionManifestName())) {
                permsToShowExplanation.add(currentPerm);
            }
            permStrings.add(currentPerm.getPermissionManifestName());
        }

        final String[] permStringArray = permStrings.toArray(new String[permStrings.size()]);

        if(permsToShowExplanation.size() <= 0 || bypassShowRationale){
            bypassShowRationale = false;
            //No explanation needed, make the request
            try {
                handler.post(
                        new Runnable() {
                            @Override
                            public void run() {
                                ActivityCompat.requestPermissions(activity, permStringArray,
                                        PERMISSIONS_REQUEST_BASE_CALL);
                            }
                        }
                );
            } catch (Exception e){
                e.printStackTrace();
            }

        } else {
            bypassShowRationale = true;
            //Request explanations needed, show
            //Insert here if used (marker **)
            if(activity instanceof PermissionUtilities.permissionsFinishedListener){
                ((PermissionUtilities.permissionsFinishedListener)activity)
                        .onPermissionsRationale(whichPerms);
            } else {
                PermissionUtilities.makeDialogForSettings(activity, whichPerms);
            }

        }
    }

    /**
     * Makes a dialog popup where if the user clicks the affirmative it will take them to
     * the settings of the phone so they can adjust the settings
     * @param activity
     * @param perms
     */
    public static void makeDialogForSettings(@NonNull final Activity activity, @NonNull permissionsEnum[] perms){
        String message = null;
        if(perms != null){
            if(perms.length > 0){
                StringBuilder sb = new StringBuilder();
                sb.append("This app requires the following permissions in order to function properly:");
                sb.append("\n\n");
                int pos = 0;
                for(permissionsEnum aPerm: perms){

                    String name = aPerm.getPermissionName();
                    sb.append(name);

                    String desc = aPerm.getPermissionDescription();
                    sb.append(", " + desc);

                    if(pos < (perms.length - 2)) {
                        sb.append("\n\n");
                    }
                    pos++;
                }
                sb.append("\nTo enable these permissions, click on the 'Grant Permissions'" +
                        " button below and turn them on in settings ");
                message = sb.toString();
            }
        }
        final Dialog dialog = DialogUtilities.buildOptionDialog(
                activity, new DialogUtilities.DialogFinishedListener() {
                    @Override
                    public void dialogFinished(Object object, int tag) {
                        if(tag == DialogUtilities.SUCCESS_RESPONSE) {
                            try {
                                Uri uri = Uri.parse("package:" + activity.getPackageName());
                                Intent intent = new Intent();
                                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                intent.addCategory(Intent.CATEGORY_DEFAULT);
                                intent.setData(uri);
                                intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                                activity.startActivityForResult(intent,
                                        PERMISSIONS_REQUEST_BASE_CALL);
                            } catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    }
                }, "Grant Permissions", null, "Later", "Permissions Authorization Needed", message
        );
        if(dialog != null) {
            dialog.show();
        }
    }

    //Quick Utils

    public static boolean getContactPermissions(@NonNull Activity activity){
        if(Build.VERSION.SDK_INT < 23){
            return true;
        } else {
            if(ContextCompat.checkSelfPermission(activity,
                    permissionsEnum.READ_CONTACTS.getPermissionManifestName())
                    != PackageManager.PERMISSION_GRANTED){

                ActivityCompat.requestPermissions(activity,
                        new String[]{permissionsEnum.READ_CONTACTS.getPermissionManifestName()},
                        PERMISSIONS_REQUEST_BASE_CALL);
                return false;
            } else {
                return true;
            }

        }

    }

}
