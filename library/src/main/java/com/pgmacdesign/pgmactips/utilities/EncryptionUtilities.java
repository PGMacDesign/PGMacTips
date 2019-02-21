package com.pgmacdesign.pgmactips.utilities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.Log;

import com.pgmacdesign.pgmactips.misc.TempString;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
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
 * Utilizing logic from these links:
 * https://stackoverflow.com/questions/40123319/easy-way-to-encrypt-decrypt-string-in-android
 * https://nelenkov.blogspot.com/2012/04/using-password-based-encryption-on.html
 * Created by pmacdowell on 2018-01-08.
 */
class EncryptionUtilities {

    @Deprecated
    private EncryptionUtilities(){}

    private static final String TAG = "EncryptionUtilities";
    private static final String INVALID_CHARSET_ERROR =
            "Invalid Charset. Acceptable charsets include: UTF-8, US_ASCII, and ISO_8859_1";

    private static final int iterationCount = 1000;
    private static final int keyLength = 256;
    private static final int IV_LENGTH_BYTES = 16;
    //private static final int saltLength = (keyLength / 8); // same size as key output
    private static final String PUBLIC_KEY_CRYPT_STANDARD = "PBKDF2WithHmacSHA1";
    private static final String CIPHER_INSTANCE_TYPE = "AES/CBC/PKCS5Padding";
    private static final String AES = "AES";
    private static final String MD5 = "MD5";

    /**
     * The charset preference to be used throughout the class. {@link StandardCharsets}
     * Charsets that WILL work:
     * {@link StandardCharsets#UTF_8}
     * {@link StandardCharsets#ISO_8859_1}
     * {@link StandardCharsets#US_ASCII}
     * Charsets that will NOT work:
     * {@link StandardCharsets#UTF_16}
     * {@link StandardCharsets#UTF_16BE}
     * {@link StandardCharsets#UTF_16LE}
     */
    private static String charSetPreference = MiscUtilities.getUTF8();

    /**
     * If this is true and a salt of length 16 is passed in, the string / byte[] will not be
     * hashed using MD5. If false, any String / byte[] will be hashed using MD5
     * {@link EncryptionUtilities#convertToMD5Hash(String)}
     */
    private static boolean bypassLength16SaltHashing = false;

    /**
     * Set the charset (IE, 'UTF-8'). If this is never set, all calls will default to UTF-8
     *
     * @param charSetPreference {@link StandardCharsets}
     *                          Charsets that WILL work:
     *                          {@link StandardCharsets#UTF_8}
     *                          {@link StandardCharsets#ISO_8859_1}
     *                          {@link StandardCharsets#US_ASCII}
     *                          Charsets that will NOT work:
     *                          {@link StandardCharsets#UTF_16}
     *                          {@link StandardCharsets#UTF_16BE}
     *                          {@link StandardCharsets#UTF_16LE}
     */
    public static void setCharSetPreference(@NonNull Charset charSetPreference) {
        boolean okToUse;
        if (StringUtilities.doesEqual(charSetPreference.toString(), MiscUtilities.getUTF8())) {
            okToUse = true;
        } else if (StringUtilities.doesEqual(charSetPreference.toString(), MiscUtilities.getISO8859())) {
            okToUse = true;
        } else if (StringUtilities.doesEqual(charSetPreference.toString(), MiscUtilities.getASCII())) {
            okToUse = true;
        } else if (StringUtilities.doesEqual(charSetPreference.toString(), MiscUtilities.getUTF16())) {
            okToUse = false;
        } else if (StringUtilities.doesEqual(charSetPreference.toString(), MiscUtilities.getUTF16BE())) {
            okToUse = false;
        } else if (StringUtilities.doesEqual(charSetPreference.toString(), MiscUtilities.getUTF16LE())) {
            okToUse = false;
        } else {
            okToUse = false;
        }
        if (okToUse) {
            EncryptionUtilities.charSetPreference = charSetPreference.toString();
        } else {
            Log.d(TAG, EncryptionUtilities.INVALID_CHARSET_ERROR);
            EncryptionUtilities.charSetPreference = MiscUtilities.getUTF8();
        }
    }

    private static String getCharSetPreference() {
        return EncryptionUtilities.charSetPreference;
    }

    /**
     * Set the bypass length 16 salt hash bool. If this is never set, will default to false
     *
     * @param bypassLength16SaltHashing If this is true and a salt of length 16 is passed in,
     *                                  the string / byte[] will not be hashed using MD5.
     *                                  If false, any String / byte[] will be hashed using MD5
     */
    public static void setBypassLength16SaltHashing(boolean bypassLength16SaltHashing) {
        EncryptionUtilities.bypassLength16SaltHashing = bypassLength16SaltHashing;
    }

    private static boolean isBypassLength16SaltHashing() {
        return bypassLength16SaltHashing;
    }

    /**
     * Overloaded to allow for String salt to be passed
     */
    private static SecretKey generateKey(@NonNull TempString password, @NonNull String salt)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        return generateKey(password, EncryptionUtilities.convertStringToBytes(salt));
    }

    /**
     * Generate the secret key to use for encryption or decryption
     *
     * @param password Password
     * @param salt     Salt
     * @return {@link SecretKey}
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    private static SecretKey generateKey(@NonNull TempString password, @NonNull byte[] salt)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
//        SecureRandom random = new SecureRandom();
//        byte[] salt = new byte[saltLength];
//        random.nextBytes(salt);
        salt = convertSaltTo16Digits(salt);
        L.m("EncryptionUtilities, generateKey: salt == " + salt);
        KeySpec keySpec = new PBEKeySpec(password.getTempStringData().toCharArray(), salt,
                iterationCount, keyLength);
        SecretKeyFactory keyFactory = SecretKeyFactory
                .getInstance(PUBLIC_KEY_CRYPT_STANDARD);
        byte[] keyBytes = keyFactory.generateSecret(keySpec).getEncoded();
        L.m("EncryptionUtilities, generateKey: keyBytes == " + new String(keyBytes));
        return new SecretKeySpec(keyBytes, AES);
    }

    /**
     * Encrypt the String
     *
     * @param messageToEncrypt String message to encrypt
     * @param password         {@link TempString} Password String
     * @param salt             salt byte[]
     * @return Encrypted byte array
     * @throws Exception throws various exceptions
     */
    public static byte[] encryptString(@NonNull String messageToEncrypt,
                                       @NonNull TempString password, @NonNull byte[] salt)
            throws NoSuchAlgorithmException, NoSuchPaddingException,
            InvalidKeyException, InvalidParameterSpecException,
            IllegalBlockSizeException, BadPaddingException,
            UnsupportedEncodingException, InvalidAlgorithmParameterException,
            InvalidKeySpecException {

        salt = convertSaltTo16Digits(salt);
        L.m("In Encryption Utilities, Salt == " + salt);
        Cipher cipher = Cipher.getInstance(CIPHER_INSTANCE_TYPE);
        IvParameterSpec ivParams;
        ivParams = new IvParameterSpec(salt);
        cipher.init(Cipher.ENCRYPT_MODE, generateKey(password, salt), ivParams);
        byte[] toReturn = cipher.doFinal(messageToEncrypt.getBytes(getCharSetPreference()));
        L.m("In Encryption Utilities, Encrypted String == " + new String(toReturn));
        return toReturn;
    }

    /**
     * Overloaded to allow for String salts to be passed
     *
     * @param messageToEncrypt String message to encrypt
     * @param password         {@link TempString} Password String
     * @param salt             salt byte[]
     * @return Encrypted byte array
     * @throws Exception throws various exceptions
     */
    public static byte[] encryptString(@NonNull String messageToEncrypt,
                                       @NonNull TempString password, @NonNull String salt)
            throws NoSuchAlgorithmException, NoSuchPaddingException,
            InvalidKeyException, InvalidParameterSpecException,
            IllegalBlockSizeException, BadPaddingException,
            UnsupportedEncodingException, InvalidAlgorithmParameterException,
            InvalidKeySpecException {
        return encryptString(messageToEncrypt, password, EncryptionUtilities.convertStringToBytes(salt));
    }


    /**
     * Decrypt the message
     *
     * @param cipherText cipher text to be decrypted
     * @param password   Password to use to decrypt
     * @param salt       salt to use to decrypt
     * @return Decrypted String
     * @throws Exception throws various exceptions
     */
    public static String decryptString(@NonNull byte[] cipherText,
                                       @NonNull TempString password, @NonNull byte[] salt)
            throws NoSuchPaddingException, NoSuchAlgorithmException,
            InvalidParameterSpecException, InvalidAlgorithmParameterException,
            InvalidKeyException, BadPaddingException, IllegalBlockSizeException,
            UnsupportedEncodingException, InvalidKeySpecException {
        salt = convertSaltTo16Digits(salt);
        Cipher cipher = Cipher.getInstance(CIPHER_INSTANCE_TYPE);
        IvParameterSpec ivParams;
        ivParams = new IvParameterSpec(salt);
        cipher.init(Cipher.DECRYPT_MODE, generateKey(password, salt), ivParams);
        byte[] plaintext = cipher.doFinal(cipherText);
        return new String(plaintext, getCharSetPreference());
    }

    /**
     * Overloaded to allow for string salt
     */
    public static String decryptString(@NonNull byte[] cipherText,
                                       @NonNull TempString password, @NonNull String salt)
            throws NoSuchPaddingException, NoSuchAlgorithmException,
            InvalidParameterSpecException, InvalidAlgorithmParameterException,
            InvalidKeyException, BadPaddingException, IllegalBlockSizeException,
            UnsupportedEncodingException, InvalidKeySpecException {
        return decryptString(cipherText, password, EncryptionUtilities.convertStringToBytes(salt));
    }

    /**
     * Convert a String to an MD5 Hash. If the conversion fails, returns null
     *
     * @param plainText String plain text to convert to MD5
     * @return MD5 String
     */
    public static String convertToMD5Hash(@NonNull String plainText) {
        if (StringUtilities.isNullOrEmpty(plainText)) {
            return null;
        }
        MessageDigest messageDigest;
        try {
            messageDigest = MessageDigest.getInstance(MD5);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
        messageDigest.reset();
        try {
            messageDigest.update(plainText.getBytes(getCharSetPreference()));
        } catch (UnsupportedEncodingException uee) {
            uee.printStackTrace();
            messageDigest.update(plainText.getBytes());
        }
        final byte[] digest = messageDigest.digest();
        plainText = null;
        return StringUtilities.toHex(digest, false);
    }

    /**
     * Convert a byte array to an MD5 Hash. If the conversion fails, returns null
     *
     * @param plainText String plain text to convert to MD5
     * @return MD5 String
     */
    public static String convertToMD5Hash(@NonNull byte[] plainText) {
        MessageDigest messageDigest;
        try {
            messageDigest = MessageDigest.getInstance(MD5);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
        messageDigest.reset();
        messageDigest.update(plainText);
        final byte[] digest = messageDigest.digest();
        plainText = null;
        String hex = StringUtilities.toHex(digest, false);
        return hex;
    }

    /**
     * /**
     * Convert a String to MD5 Hash. If the conversion fails, returns null
     *
     * @param plainText  String plain text to convert to MD5
     * @param radixToUse {@link BigInteger#toString(int)} Pass null if not using or unsure of purpose
     * @return MD5 String (of ints)
     */
    public static String convertToMD5HashInt(@NonNull String plainText, @Nullable Integer radixToUse) {
        if (StringUtilities.isNullOrEmpty(plainText)) {
            return null;
        }
        MessageDigest messageDigest;
        try {
            messageDigest = MessageDigest.getInstance(MD5);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
        messageDigest.reset();
        try {
            messageDigest.update(plainText.getBytes(getCharSetPreference()));
        } catch (UnsupportedEncodingException uee) {
            uee.printStackTrace();
            messageDigest.update(plainText.getBytes());
        }
        plainText = null;
        final byte[] digest = messageDigest.digest();
        final BigInteger bigInt = new BigInteger(1, digest);
        if (radixToUse == null) {
            return bigInt.toString(); //Removed '8' as radix arg. Add back in if needed
        } else {
            return bigInt.toString(radixToUse);
        }
    }

    /**
     * Overloaded to allow String
     */
    public static byte[] convertSaltTo16Digits(@NonNull String salt) {
        return convertSaltTo16Digits(convertStringToBytes(salt));
    }

    /**
     * Convert a Salt so that it becomes 16 digits in length through hashing.
     * This is useful if you have a salt that is not 16 digits and need it to
     *
     * @param salt Salt to convert
     * @return
     */
    public static byte[] convertSaltTo16Digits(@NonNull byte[] salt) {
        if(salt == null){
            return salt;
        }
        if(salt.length <= 0){
            return salt;
        }
        if (salt.length == 16) {
            if (bypassLength16SaltHashing) {
                return salt;
            }
        }
        String md5Hash = convertToMD5Hash(salt);
        if (StringUtilities.isNullOrEmpty(md5Hash)) {
            return null;
        }
        md5Hash = md5Hash.substring(0, 16);
        return convertStringToBytes(md5Hash);
    }

    /**
     * Simple utility to convert a String to bytes using the preferred charSetPreference.
     * {@link EncryptionUtilities#charSetPreference}
     *
     * @param str String to convert
     * @return byte[]
     */
    public static byte[] convertStringToBytes(String str) {
        if (StringUtilities.isNullOrEmpty(str)) {
            return null;
        }
        byte[] s;
        try {
            s = str.getBytes(getCharSetPreference());
        } catch (UnsupportedEncodingException e) {
            s = str.getBytes();
        }
        return s;
    }
}
