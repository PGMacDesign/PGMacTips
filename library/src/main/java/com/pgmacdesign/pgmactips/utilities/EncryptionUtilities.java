package com.pgmacdesign.pgmactips.utilities;

import android.support.annotation.NonNull;

import com.pgmacdesign.pgmactips.magreaderutils.TempString;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.security.spec.KeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Note! This class currently only works if the salt is of length 16. Will need to refactor
 * Created by pmacdowell on 2018-01-08.
 */

public class EncryptionUtilities {


    //https://stackoverflow.com/questions/40123319/easy-way-to-encrypt-decrypt-string-in-android
    //https://nelenkov.blogspot.com/2012/04/using-password-based-encryption-on.html

    private SecretKey secret;
    private TempString password;
    private byte[] salt;

    private static final int iterationCount = 1000;
    private static final int keyLength = 256;
    private static final int saltLength = (keyLength / 8); // same size as key output
    private static final String PUBLIC_KEY_CRYPT_STANDARD = "PBKDF2WithHmacSHA1";
    private static final String CIPHER_INSTANCE_TYPE = "AES/CBC/PKCS5Padding";
    private static final String AES = "AES";
    public final static String UTF8 = "UTF-8";

    /**
     * Clear all locally stored data
     */
    public void clear(){
        this.secret = null;
        this.password.disposeData();
        this.salt = null;
    }

    public EncryptionUtilities(@NonNull final String password,
                               @NonNull final String salt){
        this.password = new TempString(password);
        try {
            this.salt = salt.getBytes(UTF8);
        } catch (UnsupportedEncodingException e){
            this.salt = salt.getBytes();
        }
        try {
            this.secret = generateKey();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public EncryptionUtilities(@NonNull final String password,
                               @NonNull final byte[] salt){
        this.password = new TempString(password);
        this.salt = salt;
        try {
            this.secret = generateKey();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private SecretKey generateKey() throws NoSuchAlgorithmException, InvalidKeySpecException {
//        SecureRandom random = new SecureRandom();
//        byte[] salt = new byte[saltLength];
//        random.nextBytes(salt);
        KeySpec keySpec = new PBEKeySpec(password.getTempStringData().toCharArray(), salt,
                iterationCount, keyLength);
        SecretKeyFactory keyFactory = SecretKeyFactory
                .getInstance(PUBLIC_KEY_CRYPT_STANDARD);
        byte[] keyBytes = keyFactory.generateSecret(keySpec).getEncoded();
        return new SecretKeySpec(keyBytes, AES);
    }

    /**
     * Encrypt the String
     * @param message
     * @return encrypted byte array
     */
    public byte[] encryptString(String message)
            throws NoSuchAlgorithmException, NoSuchPaddingException,
            InvalidKeyException, InvalidParameterSpecException,
            IllegalBlockSizeException, BadPaddingException,
            UnsupportedEncodingException, InvalidAlgorithmParameterException {

        Cipher cipher = Cipher.getInstance(CIPHER_INSTANCE_TYPE);
        IvParameterSpec ivParams;
        ivParams = new IvParameterSpec(salt);
        cipher.init(Cipher.ENCRYPT_MODE, secret, ivParams);
        return cipher.doFinal(message.getBytes(UTF8));
    }

    /**
     * Decrypt the message
     * @param cipherText
     * @return decrypted String
     */
    public String decryptString(byte[] cipherText)
            throws NoSuchPaddingException, NoSuchAlgorithmException,
            InvalidParameterSpecException, InvalidAlgorithmParameterException,
            InvalidKeyException, BadPaddingException, IllegalBlockSizeException,
            UnsupportedEncodingException {
        Cipher cipher = Cipher.getInstance(CIPHER_INSTANCE_TYPE);
        IvParameterSpec ivParams;
        ivParams = new IvParameterSpec(salt);
        cipher.init(Cipher.DECRYPT_MODE, secret, ivParams);
        byte[] plaintext = cipher.doFinal(cipherText);
        return new String(plaintext , UTF8);
    }





}
