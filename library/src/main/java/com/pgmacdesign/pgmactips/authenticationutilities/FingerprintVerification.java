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
import java.security.UnrecoverableKeyException;
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
 * Created by pmacdowell on 7/6/2018.
 */
@RequiresApi(23)
public class FingerprintVerification extends FingerprintManager.AuthenticationCallback {
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

    //Standard Vars
    private Context context;
    private String keyName;

    @RequiresPermission(Manifest.permission.USE_FINGERPRINT)
    @RequiresApi(23)
    public FingerprintVerification(@NonNull Context context, @Nullable String keyName){
        this.context = context;
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

    private void init(){
        this.keyguardManager = (KeyguardManager) context
                .getSystemService(Context.KEYGUARD_SERVICE);
        this.fingerprintManager = (FingerprintManager) context
                .getSystemService(Context.FINGERPRINT_SERVICE);
        this.secretKey = this.generateKey();
        boolean didInitSuccessfully = this.initCipher();
        if(!didInitSuccessfully){
            L.m("Did not successfully initialize all init methods");
            return;
        }
    }

    /**
     * Checks if the the fingerprint sensor hardware is available on the device
     * @return
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
            // Obtain a reference to the Keystore using the standard Android keystore container identifier (“AndroidKeystore”)//
            keyStore = KeyStore.getInstance(ANDROID_KEYSTORE);

            //Generate the key//
            keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEYSTORE);

            //Initialize an empty KeyStore//
            keyStore.load(null);

            //Initialize the KeyGenerator//
            keyGenerator.init(new
                    //Specify the operation(s) this key can be used for//
                    KeyGenParameterSpec.Builder(keyName,
                    KeyProperties.PURPOSE_ENCRYPT |
                            KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    //Configure this key so that the user has to confirm their identity with a fingerprint each time they want to use it//
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .build());

            //Generate the key//
            return keyGenerator.generateKey();

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

    //Create a new method that we’ll use to initialize our cipher//
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
            this.keyStore.load(null);
            SecretKey key = (SecretKey) this.keyStore.getKey(keyName,
                    null);
            this.cipher.init(Cipher.ENCRYPT_MODE, key);
            //Return true if the cipher has been initialized successfully//
            return true;
        } catch (KeyPermanentlyInvalidatedException e) {

            //Return false if cipher initialization failed//
            return false;
        } catch (KeyStoreException | CertificateException
                | UnrecoverableKeyException | IOException
                | NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace();
            return false;
        }
    }

}

