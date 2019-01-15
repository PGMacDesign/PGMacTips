package com.pgmacdesign.pgmactips.utilities;

import android.app.admin.DevicePolicyManager;
import android.app.admin.SystemUpdatePolicy;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.UserManager;
import android.provider.Settings;
import androidx.annotation.RequiresApi;
import android.widget.Toast;

/**
 * This class is used for locking down an application for COSU (Company-owned Single Use):
 * https://developer.android.com/work/cosu.html
 * This class requires a device admin receiver to function properly as well as some changes
 * to the manifest, xml, and commands via the adb.
 * <b>WARNING! If the lock {@link AppLockdownUtils#lockApp(Context, Class, ComponentName)} is triggered
 * without any way to access the unlock {@link AppLockdownUtils#unlockApp(Context, Class, ComponentName)},
 * you will be unable to 'free' or change the lock state until you hard reset (wipe) the device! Please
 * use this only at your own risk and after you understand the consequences of your actions. </b>
 * Note, this requires a device with API 23 {@link Build.VERSION_CODES#M} or above
 *
 * First, here is a sample device admin receiver:
 *
 public class DeviceAdminReceiver extends android.app.admin.DeviceAdminReceiver {
    public static ComponentName getComponentName(Context context) {
        return new ComponentName(context.getApplicationContext(),  DeviceAdminReceiver.class);
    }
 }
 * Second, The manifest needs these intents to be added to the activity you are intending to lock down:
    ```
    <category android:name="android.intent.category.LAUNCHER" />
    <category android:name="android.intent.category.HOME"/>
    <category android:name="android.intent.category.DEFAULT"/>
    ```
 *
 * Third, The manifest needs the receiver to be added that corresponds do your device admin receiver
 ```
 <receiver
     android:name=".YOUR_DEVICE_ADMIN_RECEIVER_NAME_HERE"
     android:description="@string/app_name"
     android:label="@string/app_name"
     android:permission="android.permission.BIND_DEVICE_ADMIN">
     <meta-data
         android:name="android.app.device_admin"
         android:resource="@xml/device_admin_receiver" />
     <intent-filter>
         <action android:name="android.intent.action.DEVICE_ADMIN_ENABLED"/>
         <action android:name="android.intent.action.PROFILE_PROVISIONING_COMPLETE"/>
         <action android:name="android.intent.action.BOOT_COMPLETED"/>
     </intent-filter>
 </receiver>
 ```
 *
 * Fourth, this needs to be added to the res directory under the xml (named device_admin_receiver above)
 ```
 <?xml version="1.0" encoding="utf-8"?>
 <device-admin>
     <uses-policies>
        <disable-keyguard-features/>
     </uses-policies>
 </device-admin>
 ```
 *
 * Fifth and last, the following command needs to be run from the ADB in order to designate the app as a device owner
    adb shell dpm set-device-owner YOUR.PACKAGE.GOES.HERE/.YOUR_DEVICE_ADMIN_RECEIVER_NAME_HERE
 *
 * Created by pmacdowell on 2018-02-27.
 */
public class AppLockdownUtils {

    private static boolean shouldToastResults = true;

    private static final String LOCK_FAIL = "Could not lock app, not device owner";
    private static final String LOCK_SUCCESS = "Successfully locked app";
    private static final String UNLOCK_FAIL = "Could not unlock app, not device owner";
    private static final String UNLOCK_SUCCESS = "Successfully unlocked app";
    private static final String UNKNOWN_ERROR = "Could not perform lock / unlock operation; unknown error";

    /**
     * Call this to disable toasts (with a false) should you choose to disable / enable them
     * @param shouldToast Defaults to true, will show toasts unless specified
     */
    public static void shouldToastResults(final boolean shouldToast){
        AppLockdownUtils.shouldToastResults = shouldToast;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static void lockApp(final Context context, final Class myClass,
                               final ComponentName mAdminComponentName){
        try {
            DevicePolicyManager mDevicePolicyManager = (DevicePolicyManager) context.getSystemService(
                    Context.DEVICE_POLICY_SERVICE);
            if (mDevicePolicyManager.isDeviceOwnerApp(context.getPackageName())) {
                setDefaultCosuPolicies(context, myClass, true, mDevicePolicyManager, mAdminComponentName);
                toastStuff(context, LOCK_SUCCESS);
            } else {
                toastStuff(context, LOCK_FAIL);
            }
        } catch (Exception e){
            if(AppLockdownUtils.shouldToastResults){
                e.printStackTrace();
            }
            toastStuff(context, UNKNOWN_ERROR);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static void unlockApp(final Context context, final Class myClass,
                                 final ComponentName mAdminComponentName){
        try {
            DevicePolicyManager mDevicePolicyManager = (DevicePolicyManager) context.getSystemService(
                    Context.DEVICE_POLICY_SERVICE);
            if (mDevicePolicyManager.isDeviceOwnerApp(context.getPackageName())) {
                setDefaultCosuPolicies(context, myClass, false, mDevicePolicyManager, mAdminComponentName);
                toastStuff(context, UNLOCK_SUCCESS);
            } else {
                toastStuff(context, UNLOCK_FAIL);
            }
        } catch (Exception e){
            if(AppLockdownUtils.shouldToastResults){
                e.printStackTrace();
            }
            toastStuff(context, UNKNOWN_ERROR);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private static void setDefaultCosuPolicies(final Context context, final Class myClass,
                                               boolean active, final DevicePolicyManager mDevicePolicyManager,
                                               final ComponentName mAdminComponentName){

        if(mDevicePolicyManager == null || mAdminComponentName == null){
            toastStuff(context, UNKNOWN_ERROR);
            return;
        }
        // Set user restrictions
        setUserRestriction(UserManager.DISALLOW_SAFE_BOOT, active, mDevicePolicyManager, mAdminComponentName);
        setUserRestriction(UserManager.DISALLOW_FACTORY_RESET, active, mDevicePolicyManager, mAdminComponentName);
        setUserRestriction(UserManager.DISALLOW_ADD_USER, active, mDevicePolicyManager, mAdminComponentName);
        setUserRestriction(UserManager.DISALLOW_MOUNT_PHYSICAL_MEDIA, active, mDevicePolicyManager, mAdminComponentName);
        setUserRestriction(UserManager.DISALLOW_ADJUST_VOLUME, active, mDevicePolicyManager, mAdminComponentName);


        // Disable keyguard and status bar
        mDevicePolicyManager.setKeyguardDisabled(mAdminComponentName, active);
        mDevicePolicyManager.setStatusBarDisabled(mAdminComponentName, active);

        // Enable STAY_ON_WHILE_PLUGGED_IN
        enableStayOnWhilePluggedIn(active, mDevicePolicyManager, mAdminComponentName);

        // Set system update policy
        if (active){
            mDevicePolicyManager.setSystemUpdatePolicy(mAdminComponentName,
                    SystemUpdatePolicy.createWindowedInstallPolicy(60, 120));
        } else {
            mDevicePolicyManager.setSystemUpdatePolicy(mAdminComponentName,
                    null);
        }

        // set this Activity as a lock task package
        mDevicePolicyManager.setLockTaskPackages(mAdminComponentName,
                active ? new String[]{context.getPackageName()} : new String[]{});

        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_MAIN);
        intentFilter.addCategory(Intent.CATEGORY_HOME);
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);

        if (active) {
            // set Cosu activity as home intent receiver so that it is started
            // on reboot
            mDevicePolicyManager.addPersistentPreferredActivity(
                    mAdminComponentName, intentFilter, new ComponentName(
                            context.getPackageName(), myClass.getName()));
        } else {
            mDevicePolicyManager.clearPackagePersistentPreferredActivities(
                    mAdminComponentName, context.getPackageName());
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private static void setUserRestriction(String restriction, boolean disallow,
                                    final DevicePolicyManager mDevicePolicyManager,
                                    final ComponentName mAdminComponentName){
        if(mDevicePolicyManager == null || mAdminComponentName == null){
            return;
        }
        if (disallow) {
            mDevicePolicyManager.addUserRestriction(mAdminComponentName,
                    restriction);
        } else {
            mDevicePolicyManager.clearUserRestriction(mAdminComponentName,
                    restriction);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private static void enableStayOnWhilePluggedIn(boolean enabled,
                                            final DevicePolicyManager mDevicePolicyManager,
                                            final ComponentName mAdminComponentName){
        if(mDevicePolicyManager == null || mAdminComponentName == null){
            return;
        }
        if (enabled) {
            mDevicePolicyManager.setGlobalSetting(
                    mAdminComponentName,
                    Settings.Global.STAY_ON_WHILE_PLUGGED_IN,
                    Integer.toString(BatteryManager.BATTERY_PLUGGED_AC
                            | BatteryManager.BATTERY_PLUGGED_USB
                            | BatteryManager.BATTERY_PLUGGED_WIRELESS));
        } else {
            mDevicePolicyManager.setGlobalSetting(
                    mAdminComponentName,
                    Settings.Global.STAY_ON_WHILE_PLUGGED_IN,
                    "0"
            );
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.M) //Unused atm
    protected static Intent buildLockAppIntent(final Context context, final Class myClass,
                                               final PackageManager packageManager){
        try {
            DevicePolicyManager mDevicePolicyManager = (DevicePolicyManager) context.getSystemService(
                    Context.DEVICE_POLICY_SERVICE);
            if ( mDevicePolicyManager.isDeviceOwnerApp(
                    context.getPackageName())) {
                Intent lockIntent = new Intent(context,
                        myClass);
                packageManager.setComponentEnabledSetting(
                        new ComponentName(context, myClass),
                        PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                        PackageManager.DONT_KILL_APP);
                return lockIntent;
            }
        } catch (Exception e){
            if(AppLockdownUtils.shouldToastResults){
                e.printStackTrace();
            }
            toastStuff(context, UNKNOWN_ERROR);
        }
        return null;
    }

    private static void toastStuff(Context context, String str){
        if(context != null && str != null && AppLockdownUtils.shouldToastResults) {
            Toast.makeText(context, str, Toast.LENGTH_LONG).show();
        }
    }

}
