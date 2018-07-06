package com.pgmacdesign.pgmactips.authenticationutilities;

import android.support.annotation.RequiresApi;

/**
 * A class for managing Fingerprint login credentials. Minimum supported SDK version for
 * this class to operate correctly is API level 23 or above (Marshmallow / 6.0 or higher)
 * {@link android.os.Build.VERSION_CODES#M}
 * This is required to add to the manifest (Though true can be false to make it optional):
 <uses-feature android:name="android.hardware.fingerprint"
     android:required="true"/>
 <uses-permission
     android:name="android.permission.USE_FINGERPRINT" />
 * Pulling from: https://www.androidauthority.com/how-to-add-fingerprint-authentication-to-your-android-app-747304/
 * Created by pmacdowell on 7/6/2018.
 */
@RequiresApi(23)
public class FingerprintVerification {
}
