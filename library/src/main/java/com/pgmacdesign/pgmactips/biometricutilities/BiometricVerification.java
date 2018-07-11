package com.pgmacdesign.pgmactips.biometricutilities;

import android.Manifest;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.CancellationSignal;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.annotation.RequiresPermission;
import android.support.v4.app.ActivityCompat;

import com.pgmacdesign.pgmactips.adaptersandlisteners.OnTaskCompleteListener;
import com.pgmacdesign.pgmactips.utilities.MiscUtilities;
import com.pgmacdesign.pgmactips.utilities.StringUtilities;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

/**
 * A class for managing Fingerprint login credentials. Minimum supported SDK version for
 * this class to operate correctly is API level 23 or above (Marshmallow / 6.0 or higher)
 * {@link android.os.Build.VERSION_CODES#M}
 * More biometric utilities data will be added in the future.
 * This is required to add to the manifest (Though true can be false to make it optional):
 <uses-feature android:name="android.hardware.fingerprint"
     android:required="true"/>
 <uses-permission
     android:name="android.permission.USE_FINGERPRINT" />

 * Pulling from: https://www.androidauthority.com/how-to-add-fingerprint-authentication-to-your-android-app-747304/
 *
 * IMPORTANT: You should use the CancellationSignal method whenever your app can no longer process user
 * input, for example when your app goes into the background. If you don’t use this method, then other
 * apps will be unable to access the touch sensor, including the lockscreen!
 * Created by pmacdowell on 7/6/2018.
 * todo https://developer.android.com/reference/android/hardware/fingerprint/FingerprintManager.CryptoObject
 */
@RequiresApi(23)
public class BiometricVerification {

    /*
    //Sample Usage after you have requested permission via the manifest
    if(Build.VERSION.SDK_INT >= 23) {
                //Initialize here
                BiometricVerification biometricVerification = new BiometricVerification(
                        new OnTaskCompleteListener() {
                            @Override
                            public void onTaskComplete(Object result, int customTag) {
                                //Switch Statement to handle one of the five possible responses
                                switch (customTag){
                                    case BiometricVerification.TAG_AUTHENTICATION_FAIL:
                                        //Authentication failed / finger does not match
                                        boolean fail = (boolean) result;
                                        break;

                                    case BiometricVerification.TAG_AUTHENTICATION_SUCCESS:
                                        //Authentication success / finger matches.
                                        //(NOTE! Stops fingerprint listener when this triggers)
                                        boolean success = (boolean) result;
                                        break;

                                    case BiometricVerification.TAG_AUTHENTICATION_ERROR:
                                        //Error (IE called stopFingerprintAuth() or onStop() triggered)
                                        String knownAuthenticationError = (String) result;
                                        break;

                                    case BiometricVerification.TAG_AUTHENTICATION_HELP:
                                        //Authentication did not work, help string passed
                                        String helpString = (String) result;
                                        break;

                                    case BiometricVerification.TAG_GENERIC_ERROR:
                                        //Some unknown error has occurred
                                        String genericErrorString = (String) result;
                                        break;
                                }
                            }
                        }, mContext, "my_key_name");

                //Start auth here
                try {
                    if(biometricVerification.isCriteriaMet()){
                        this.biometricVerification.startFingerprintAuth();
                    }
                } catch (FingerprintException e){
                    e.printStackTrace();
                }
    }
     */

    private static final String ANDROID_KEYSTORE = "AndroidKeyStore";
    private static final String FINGERPRINT_SUFFIX_STRING = ".fingerprint";
    private static final String ERROR_MISSING_PERMISSION = "Missing required permission [android.permission.USE_FINGERPRINT].";
    private static final String MUST_CALL_START_BEFORE_STOP = "You must call startFingerprintAuth() before you can call stopFingerprintAuth()";
    private static final String UNKNOWN_ERROR = "An unknown error has occurred. Please try again";
    private static final String HARDWARE_UNAVAILABLE = "Fingerprint sensor hardware not available on this device.";
    private static final String NO_STORED_FINGERPRINTS = "User does not have any enrolled fingerprints; must have at least one stored to use this method.";
    private static final String LOCK_SCREEN_NOT_ENABLED = "User does not have a lock screen enabled. A lock screen is required before this feature can be used.";

    /**
     * Will trigger upon making a call that requires fingerprint permission but does not have it.
     * This can happen when a user uses the app, gives permission, and then goes into settings and
     * removes the given permission.
     * @deprecated Refactored into different method and will throw {@link FingerprintException} instead.
     * When using {@link OnTaskCompleteListener#onTaskComplete(Object, int)}, the object will always be passed as a String
     */
    @Deprecated
    static final int TAG_MISSING_FINGERPRINT_PERMISSION = 9320;
    /**
     * Will trigger upon making an unknown error occurring.
     * When using {@link OnTaskCompleteListener#onTaskComplete(Object, int)}, the object will always be passed as a String
     */
    public static final int TAG_GENERIC_ERROR = 9321;
    /**
     * Will trigger upon authentication success, IE, fingerprint matches those stored in phone.
     * NOTE! When this is sent back, the system automatically calls {@link BiometricVerification#stopFingerprintAuth()}
     * and will stop listening for fingerprint input. If you want to begin listening for input again, you will
     * need to call {@link BiometricVerification#startFingerprintAuth()} again.
     * When using {@link OnTaskCompleteListener#onTaskComplete(Object, int)}, the object will always be passed as a boolean
     */
    public static final int TAG_AUTHENTICATION_SUCCESS = 9322;
    /**
     * Will trigger upon authentication fail, IE, fingerprint does not match any stored in phone.
     * When using {@link OnTaskCompleteListener#onTaskComplete(Object, int)}, the object will always be passed as a boolean
     */
    public static final int TAG_AUTHENTICATION_FAIL = 9323;
    /**
     * Will trigger upon an error, IE, manual call to {@link FingerprintHandler#stopFingerprintAuth()} or
     * when the app goes into the background unexpectedly.
     * When using {@link OnTaskCompleteListener#onTaskComplete(Object, int)}, the object will always be passed as a String
     */
    public static final int TAG_AUTHENTICATION_ERROR = 9324;
    /**
     * Will trigger upon helpful hints, IE, if you move the finger too quickly, you will see this text:
     * "Finger moved too fast. Please try again".
     * When using {@link OnTaskCompleteListener#onTaskComplete(Object, int)}, the object will always be passed as a String
     */
    public static final int TAG_AUTHENTICATION_HELP = 9325;


    //For FingerprintManager.AuthenticationCallback Extension:
    private CancellationSignal cancellationSignal;
    //Vars
    private FingerprintManager.CryptoObject cryptoObject;
    private FingerprintManager fingerprintManager;
    private KeyguardManager keyguardManager;
    private Cipher cipher;
    private KeyStore keyStore;
    private KeyGenerator keyGenerator;
    private SecretKey secretKey;
    private FingerprintHandler fingerprintHandler;
    //Standard Vars
    private OnTaskCompleteListener listener;
    private Context context;
    private String keyName;
    private boolean cipherInitialized, keystoreInitialized;

    /**
     * Fingerprint Verification Constructor
     * @param listener {@link OnTaskCompleteListener} link to send back results
     * @param context Context to be used in the class
     * @param keyName String keyName desired to use. If null, will attempt to pull package name and
     *                use that as the name. If that fails, it will use random numbers plus the
     *                '.fingerprint' suffix String.
     */
    public BiometricVerification(@NonNull final OnTaskCompleteListener listener,
                                 @NonNull final Context context,
                                 @Nullable String keyName) {
        this.cipherInitialized = this.keystoreInitialized = false;
        this.context = context;
        this.listener = listener;
        if(!StringUtilities.isNullOrEmpty(keyName)) {
            this.keyName = keyName;
        } else {
            String packageName = MiscUtilities.getPackageName(this.context);
            if(!StringUtilities.isNullOrEmpty(packageName)){
                this.keyName = packageName + FINGERPRINT_SUFFIX_STRING;
            } else {
                int random = (new Random().nextInt(8999) + 1000);
                this.keyName = random + FINGERPRINT_SUFFIX_STRING;
            }
        }
        if(this.keyguardManager == null){
            this.keyguardManager = (KeyguardManager) context
                    .getSystemService(Context.KEYGUARD_SERVICE);
        }
        if(this.fingerprintManager == null) {
            this.fingerprintManager = (FingerprintManager) context
                    .getSystemService(Context.FINGERPRINT_SERVICE);
        }
    }

    /**
     * Init method
     * @throws FingerprintException {@link FingerprintException}
     */
    @RequiresPermission(Manifest.permission.USE_FINGERPRINT)
    private void init() throws FingerprintException{
        try {
            if (!this.doesHaveFingerprintPermission()) {
                throw new FingerprintException(BiometricVerification.ERROR_MISSING_PERMISSION);
            }
            if (!this.isFingerprintSensorAvailable()) {
                throw new FingerprintException(BiometricVerification.HARDWARE_UNAVAILABLE);
            }
            if (!this.doesUserHaveEnrolledFingerprints()) {
                throw new FingerprintException(BiometricVerification.NO_STORED_FINGERPRINTS);
            }
            if (!this.doesUserHaveLockEnabled()) {
                throw new FingerprintException(BiometricVerification.LOCK_SCREEN_NOT_ENABLED);
            }

            //Finish the builders
            boolean didInitSuccessfully = this.initCipher();
            if (!didInitSuccessfully) {
                throw new FingerprintException(BiometricVerification.UNKNOWN_ERROR);
            }
            if (this.cryptoObject == null) {
                this.cryptoObject = new FingerprintManager.CryptoObject(this.cipher);
            }
            if (this.fingerprintHandler == null) {
                this.fingerprintHandler = new FingerprintHandler();
            }
        } catch (FingerprintException fe){
            throw fe;
        } catch (Exception e){
            e.printStackTrace();
            throw new FingerprintException(e);
        }

    }

    /**
     * Checks if the the fingerprint sensor hardware is available on the device
     * @return boolean, if true sensor is available on the device, false if not
     */
    @RequiresPermission(Manifest.permission.USE_FINGERPRINT)
    public boolean isFingerprintSensorAvailable(){
        try {
            return this.fingerprintManager.isHardwareDetected();
        } catch (Exception e){
            return false;
        }
    }

    /**
     * Checks if the user has enrolled fingerprints in the phone itself
     * @return boolean, true if they have, false if they have not
     */
    @RequiresPermission(Manifest.permission.USE_FINGERPRINT)
    public boolean doesUserHaveEnrolledFingerprints(){
        try {
            return fingerprintManager.hasEnrolledFingerprints();
        } catch (Exception e){
            return false;
        }
    }

    /**
     * Checks if the user has given permission to use Fingerprint Scanning
     * @return boolean, true if they have, false if they have not
     */
    public boolean doesHaveFingerprintPermission(){
        try {
            int x = ActivityCompat.checkSelfPermission(context, Manifest.permission.USE_FINGERPRINT);
            return (x == PackageManager.PERMISSION_GRANTED);
        } catch (Exception e){
            return false;
        }
    }

    /**
     * Checks if the user has a phone lock available and enabled
     * @return boolean, true if they have, false if they have not
     */
    public boolean doesUserHaveLockEnabled(){
        try {
            return (keyguardManager.isKeyguardSecure());
        } catch (Exception e){
            return false;
        }
    }

    /**
     * Simple method to combine all of of the checker methods into one so as to reduce code.
     * @return boolean, will return true if all criteria has been met, false if not
     */
    @RequiresPermission(Manifest.permission.USE_FINGERPRINT)
    public boolean isCriteriaMet(){
        return (this.doesHaveFingerprintPermission() && this.doesUserHaveLockEnabled() &&
                this.doesUserHaveEnrolledFingerprints() && this.isFingerprintSensorAvailable());
    }

    /**
     * Generate the Key
     * @return {@link SecretKey}
     */
    private SecretKey generateKey() {
        try {
            if(this.secretKey != null){
                return this.secretKey;
            }

            // Obtain a reference to the Keystore using the standard Android keystore container identifier ("AndroidKeystore")//
            this.keyStore = KeyStore.getInstance(BiometricVerification.ANDROID_KEYSTORE);

            //Generate the key//
            this.keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES,
                    BiometricVerification.ANDROID_KEYSTORE);

            //Initialize an empty KeyStore//
            this.keyStore.load(null);

            //Initialize the KeyGenerator//
            this.keyGenerator.init(new
                    //Specify the operation(s) this key can be used for//
                    KeyGenParameterSpec.Builder(this.keyName,
                    KeyProperties.PURPOSE_ENCRYPT |
                            KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    //Configure this key so that the user has to confirm their identity with a fingerprint each time they want to use it//
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .build());

            //Generate the key//
            return this.keyGenerator.generateKey();

        } catch (KeyStoreException
                | NoSuchAlgorithmException
                | NoSuchProviderException
                | IllegalStateException
                | InvalidAlgorithmParameterException
                | CertificateException
                | IOException exc) {
            exc.printStackTrace();
            return null;
        }
    }

    /**
     * Initialize the cipher
     * @return true if it succeeded false if it did not
     */
    private boolean initCipher() {
        try {
            //Obtain a cipher instance and configure it with the properties required for fingerprint authentication//
            if(this.cipher == null) {
                this.cipher = Cipher.getInstance(
                        KeyProperties.KEY_ALGORITHM_AES + "/"
                                + KeyProperties.BLOCK_MODE_CBC + "/"
                                + KeyProperties.ENCRYPTION_PADDING_PKCS7);
            }
        } catch (NoSuchAlgorithmException |
                NoSuchPaddingException e) {
            e.printStackTrace();
            return false;
        }

        try {
            if(this.secretKey == null) {
                this.secretKey = generateKey();
            }
            if(this.keyStore != null) {
                if(!this.keystoreInitialized) {
                    this.keyStore.load(null);
                    this.keystoreInitialized = true;
                }
            }
//            key = (SecretKey) this.keyStore.getKey(keyName, null); todo needed?
            if(this.cipher != null) {
                if (!this.cipherInitialized) {
                    this.cipher.init(Cipher.ENCRYPT_MODE, this.secretKey);
                    this.cipherInitialized = true;
                }
            }
            //Return true if the cipher has been initialized successfully//
            return true;
        } catch (KeyPermanentlyInvalidatedException e) {

            //Return false if cipher initialization failed//
            return false;
        } catch (CertificateException //KeyStoreException
                | IOException //UnrecoverableKeyException
                | NullPointerException
                | NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Begins the Authentication session and starts listening for the finger to hit the sensor.
     * This is used in direct conjunction with the Google Fingerprint Authentication API to
     * check against stored fingerprints and ping back along the OnTaskCompleteListener when finished.
     * This class assumes the following pre-requisites have been checked against:
     *     1) The device is running Android 6.0 or higher.
     *          {@link android.os.Build.VERSION_CODES#M}
     *     2) The device features a fingerprint sensor.
     *          {@link BiometricVerification#isFingerprintSensorAvailable()}
     *     3) The user has granted your app permission to access the fingerprint sensor.
     *          {@link BiometricVerification#doesHaveFingerprintPermission()} && Permission in the manifest
     *     4) The user has protected their lockscreen
     *          {@link BiometricVerification#doesUserHaveLockEnabled()}
     *     5) The user has registered at least one fingerprint on their device.
     *          {@link BiometricVerification#doesUserHaveEnrolledFingerprints()}
     * If any of these criteria are not met, a FingerprintException will be thrown.
     * @throws FingerprintException {@link FingerprintException}
     */
    @RequiresPermission(Manifest.permission.USE_FINGERPRINT)
    public void startFingerprintAuth() throws FingerprintException{
        this.init();
        if(this.cancellationSignal == null) {
            this.cancellationSignal = new CancellationSignal();
        }
        try {
            this.fingerprintManager.authenticate(this.cryptoObject, this.cancellationSignal,
                    0, this.fingerprintHandler, null);
        } catch (Exception e){
            e.printStackTrace();
            this.listener.onTaskComplete(e.getMessage(), BiometricVerification.TAG_GENERIC_ERROR);
        }
    }

    /**
     * Stops all active Auth. Call this if onStop is suddenly called in your app
     */
    public void stopFingerprintAuth(){
        try {
            if(this.cancellationSignal != null) {
                this.cancellationSignal.cancel();
            } else {
                this.listener.onTaskComplete(BiometricVerification.MUST_CALL_START_BEFORE_STOP,
                        BiometricVerification.TAG_GENERIC_ERROR);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Helper class for authentication callback
     */
    private class FingerprintHandler extends FingerprintManager.AuthenticationCallback {
        private FingerprintHandler(){}

        @Override
        public void onAuthenticationError(int errMsgId, CharSequence errString) {
            //Authentication error
            listener.onTaskComplete(errString, BiometricVerification.TAG_AUTHENTICATION_ERROR);
        }

        @Override
        public void onAuthenticationFailed() {
            //Authentication failed (Fingerprints don't match ones on device)
            listener.onTaskComplete(false, BiometricVerification.TAG_AUTHENTICATION_FAIL);
        }

        @Override
        public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
            //Non-Fatal error (IE moved finger too quickly)
            listener.onTaskComplete((helpString != null) ? helpString.toString()
                    : UNKNOWN_ERROR, BiometricVerification.TAG_AUTHENTICATION_HELP);
        }

        @Override
        public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
            //Authentication Succeeded
            listener.onTaskComplete(true, BiometricVerification.TAG_AUTHENTICATION_SUCCESS);
        }
    }
}

