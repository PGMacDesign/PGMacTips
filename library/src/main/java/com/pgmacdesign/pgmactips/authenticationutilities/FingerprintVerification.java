package com.pgmacdesign.pgmactips.authenticationutilities;

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
import com.pgmacdesign.pgmactips.utilities.L;
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
 */
@RequiresApi(23)
public class FingerprintVerification {
    /*
    1) This class assumes the following pre-requisites have been checked against:
    2) The device is running Android 6.0 or higher. If your project’s minSdkversion is 23 or higher, then you won’t need to perform this check.
    3) The device features a fingerprint sensor. If you marked android.hardware.fingerprint as something that your app requires (android:required=”true”) then you don’t need to perform this check.
    4) The user has granted your app permission to access the fingerprint sensor.
    5) The user has protected their lockscreen. Fingerprints can only be registered once the user has secured their lockscreen with either a PIN, pattern or password, so you’ll need to ensure the lockscreen is secure before proceeding.
    6) The user has registered at least one fingerprint on their device.
     */

    private static final String ANDROID_KEYSTORE = "AndroidKeyStore";
    private static final String FINGERPRINT_SUFFIX_STRING = ".fingerprint";
    /**
     * Will trigger upon authentication success, IE, fingerprint matches those stored in phone.
     * When using {@link OnTaskCompleteListener#onTaskComplete(Object, int)}, the object will always be passed as a boolean
     */
    public static final int TAG_AUTHENTICATION_SUCCESS = 9322;
    /**
     * Will trigger upon authentication fail, IE, fingerprint does not match any stored in phone.
     * When using {@link OnTaskCompleteListener#onTaskComplete(Object, int)}, the object will always be passed as a boolean
     */
    public static final int TAG_AUTHENTICATION_FAIL = 9323;
    /**
     * Will trigger upon an error, IE, manual call to {@link FingerprintHandler#stopAuth()} or
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

    /**
     * Fingerprint Verification Constructor
     * @param context Context to be used in the class
     * @param listener {@link OnTaskCompleteListener} link to send back results
     * @param keyName String keyName desired to use. If null, will attempt to pull package name and
     *                use that as the name. If that fails, it will use random numbers plus the
     *                '.fingerprint' suffix String.
     * @throws FingerprintException {@link FingerprintException}
     */
    @RequiresPermission(Manifest.permission.USE_FINGERPRINT)
    @RequiresApi(23)
    public FingerprintVerification(@NonNull final Context context,
                                   @NonNull final OnTaskCompleteListener listener,
                                   @Nullable String keyName) throws FingerprintException{
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
        this.init();
    }

    @RequiresPermission(Manifest.permission.USE_FINGERPRINT)
    private void init() throws FingerprintException{
        try {
            this.keyguardManager = (KeyguardManager) context
                    .getSystemService(Context.KEYGUARD_SERVICE);
            this.fingerprintManager = (FingerprintManager) context
                    .getSystemService(Context.FINGERPRINT_SERVICE);

            //Check initial things here todo maybe refactor into onTaskComplete instead?
            if(!this.doesHaveFingerprintPermission()){
                throw new FingerprintException("Must request permission before use.\nandroid.permission.USE_FINGERPRINT");
            }
            if(!this.isFingerprintSensorAvailable()){
                throw new FingerprintException("Fingerprint sensor not available on this device");
            }
            if(!this.doesUserHaveEnrolledFingerprints()){
                throw new FingerprintException("User does not have any enrolled fingerprints");
            }
            if(!this.doesUserHaveLockEnabled()){
                throw new FingerprintException("User does not have a lock screen enabled. A lock screen is required before this feature can be used.");
            }

            //Finish the builders
            boolean didInitSuccessfully = this.initCipher();
            if (!didInitSuccessfully) {
                throw new FingerprintException("Did not successfully initialize all init methods");
            }
            this.cryptoObject = new FingerprintManager.CryptoObject(this.cipher);
            this.fingerprintHandler = new FingerprintHandler();
        } catch (Exception e){
            e.printStackTrace();
            throw new FingerprintException(e);
        }

    }

    /**
     * Checks if the the fingerprint sensor hardware is available on the device
     * @return boolean if true, sensor is available on the device, false if not
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
     * Generate the Key
     * @return {@link SecretKey}
     */
    private SecretKey generateKey() {
        try {
            if(this.secretKey != null){
                return this.secretKey;
            }

            // Obtain a reference to the Keystore using the standard Android keystore container identifier (“AndroidKeystore”)//
            this.keyStore = KeyStore.getInstance(ANDROID_KEYSTORE);

            //Generate the key//
            this.keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEYSTORE);

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
            this.cipher = Cipher.getInstance(
                    KeyProperties.KEY_ALGORITHM_AES + "/"
                            + KeyProperties.BLOCK_MODE_CBC + "/"
                            + KeyProperties.ENCRYPTION_PADDING_PKCS7);
        } catch (NoSuchAlgorithmException |
                NoSuchPaddingException e) {
            e.printStackTrace();
            return false;
        }

        try {
            this.secretKey = generateKey();
            this.keyStore.load(null);
//            key = (SecretKey) this.keyStore.getKey(keyName, null); todo needed?
            this.cipher.init(Cipher.ENCRYPT_MODE, this.secretKey);
            //Return true if the cipher has been initialized successfully//
            return true;
        } catch (KeyPermanentlyInvalidatedException e) {

            //Return false if cipher initialization failed//
            return false;
        } catch (CertificateException //KeyStoreException
                | IOException //UnrecoverableKeyException
                | NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Begins the Authentication session and starts listening for the finger to hit the sensor.
     */
    @RequiresPermission(Manifest.permission.USE_FINGERPRINT)
    public void startAuth() {
        if(this.cancellationSignal == null) {
            this.cancellationSignal = new CancellationSignal();
        }
        if(!this.doesHaveFingerprintPermission()){
            // TODO: 7/6/2018 trigger onTaskComplete()?
            return;
        }
        try {
            this.fingerprintManager.authenticate(this.cryptoObject, this.cancellationSignal,
                    0, this.fingerprintHandler, null);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Stops all active Auth. Call this if onStop is suddenly called in your app
     */
    public void stopAuth(){
        try {
            if(this.cancellationSignal != null) {
                this.cancellationSignal.cancel();
            } else {
                L.m("You must call startAuth() before you can call stopAuth()");
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
            listener.onTaskComplete(errString, TAG_AUTHENTICATION_ERROR);
        }

        @Override
        public void onAuthenticationFailed() {
            //Authentication failed (Fingerprints don't match ones on device)
            listener.onTaskComplete(false, TAG_AUTHENTICATION_FAIL);
        }

        @Override
        public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
            //Non-Fatal error (IE moved finger too quickly)
            listener.onTaskComplete(helpString.toString(), TAG_AUTHENTICATION_HELP);
        }

        @Override
        public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
            //Authentication Succeeded
            listener.onTaskComplete(true, TAG_AUTHENTICATION_SUCCESS);
        }
    }
}

