package com.pgmacdesign.pgmactips.utilities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import android.os.Build;
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
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.security.spec.KeySpec;

import javax.crypto.AEADBadTagException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Utilizing logic from these links:
 * https://stackoverflow.com/questions/40123319/easy-way-to-encrypt-decrypt-string-in-android
 * https://nelenkov.blogspot.com/2012/04/using-password-based-encryption-on.html
 * Additional credit / Thanks to these individuals as their code was the base for the revamp of this class:
 * 1) https://stackoverflow.com/a/49034994/2480714
 * 2) https://stackoverflow.com/a/7303080/2480714
 * Created by pmacdowell on 2018-01-08.
 */
public class EncryptionUtilities {

    //region Constructor (Private)
    private EncryptionUtilities(){}
    
    //endregion
    
    //region Static Final Vars

    private static final String TAG = "EncryptionUtilities";
    private static final String INVALID_CHARSET_ERROR =
            "Invalid Charset. Acceptable charsets include: UTF-8, US_ASCII, and ISO_8859_1";

    private static final int iterationCount = 1000;
    private static final int keyLength = 256;
    private static final int IV_LENGTH_BYTES = 16;
    private static final int KEY_SPEC_ITERATIONS_COUNT = 1024;
    private static final int KEY_SPEC_LENGTH = 256;
    
    //private static final int saltLength = (keyLength / 8); // same size as key output
    private static final String PUBLIC_KEY_CRYPT_STANDARD = "PBKDF2WithHmacSHA1";
    private static final String PUBLIC_KEY_CRYPT_STANDARD2 = "PBKDF2WithHmacSHA256";
    private static final String SECURE_RANDOM_INSTANCE_STRING = "SHA1PRNG";
    private static final String CIPHER_INSTANCE_TYPE = "AES/CBC/PKCS5Padding";
    private static final String AES = "AES";
    private static final String MD5 = "MD5";
    
    // https://crypto.stackexchange.com/questions/26783/ciphertext-and-tag-size-and-iv-transmission-with-aes-in-gcm-mode
    private static final byte VERSION_BYTE = 0x01;
    private static final int VERSION_BYTE_LENGTH = 1;
    private static final int AES_KEY_BITS_LENGTH = 128;
    
    
    // fixed AES-GCM constants
    private static final String GCM_CRYPTO_NAME = "AES/GCM/NoPadding";
    private static final int GCM_IV_BYTES_LENGTH = 12;
    private static final int GCM_TAG_BYTES_LENGTH = 16;
    
    // can be tweaked, more iterations = more compute intensive to brute-force password
    private static final int PBKDF2_ITERATIONS = 1024;
    
    //endregion
    
    //region Mutatable Variables
    
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
    
    //endregion
    
    //region Public Settings Methods
    
    
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
    
    //endregion
    
    //region Private Utility Methods
    
    private static String getCharSetPreference() {
        return EncryptionUtilities.charSetPreference;
    }

    private static boolean isBypassLength16SaltHashing() {
        return bypassLength16SaltHashing;
    }
    
    
    /**
     * We derive a fixed length AES key with uniform entropy from a provided
     * passphrase. This is done with PBKDF2/HMAC256 with a fixed count
     * of iterations and a provided salt.
     *
     * @param password passphrase to derive key from
     * @param salt     salt for PBKDF2 if possible use a per-key salt, alternatively
     *                 a random constant salt is better than no salt.
     * @param keyLen   number of key bits to output
     * @return a SecretKey for AES derived from a passphrase
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    private static SecretKey deriveAesKey(char[] password, byte[] salt, int keyLen)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        
        if (password == null || salt == null || keyLen <= 0) {
            throw new IllegalArgumentException();
        }
        SecretKeyFactory factory;
        try {
            factory = SecretKeyFactory.getInstance(PUBLIC_KEY_CRYPT_STANDARD2);
        } catch (NoSuchAlgorithmException e){
            factory = SecretKeyFactory.getInstance(PUBLIC_KEY_CRYPT_STANDARD);
        }
        KeySpec spec = new PBEKeySpec(password, salt, PBKDF2_ITERATIONS, keyLen);
        SecretKey pbeKey = factory.generateSecret(spec);
        
        return new SecretKeySpec(pbeKey.getEncoded(), AES);
    }
    
    //endregion

    //region Public Utility Methods
    
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
            messageDigest.update(convertStringToBytes(plainText));
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
     *
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
            messageDigest.update(convertStringToBytes(plainText));
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
        byte[] convertedString = convertStringToBytes(md5Hash);
        return convertedString;
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
    
    
    /**
     * Simple utility to convert a byte array to a using the preferred charSetPreference.
     * {@link EncryptionUtilities#charSetPreference}
     *
     * @param bytes String to convert
     * @return String
     */
    public static String convertBytesToString(byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        if(bytes.length <= 0){
            return null;
        }
        String s;
        try {
            s = new String(bytes, getCharSetPreference());
        } catch (UnsupportedEncodingException e) {
            s = new String(bytes);
        }
        return s;
    }
    
    /**
     * Helper to convert hex strings to bytes.
     * <p>
     * May be used to read bytes from constants.
     */
    private static byte[] hexStringToByteArray(String s) {
        
        if (StringUtilities.isNullOrEmpty(s)) {
            L.m("Provided 'null' string.");
            return null;
        }
        
        int len = s.length();
        if (len % 2 != 0) {
            L.m("Invalid length: " + len);
            return null;
        }
        
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len - 1; i += 2) {
            byte b = (byte) toHexDigit(s, i);
            b <<= 4;
            b |= toHexDigit(s, i + 1);
            data[i / 2] = b;
        }
        return data;
    }
    
    private static int toHexDigit(String s, int pos) {
        if(StringUtilities.isNullOrEmpty(s)){
            return -1;
        }
        int d = Character.digit(s.charAt(pos), 16);
        if (d < 0) {
            L.m("Cannot parse hex digit: " + s + " at " + pos);
            return -1;
        }
        return d;
    }
    
    private static String byteArrayToHexString(byte[] bytes) {
        if(bytes == null){
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }
    
    //endregion
    
    //region Encryption
    
    /**
     *
     * Encrypt a String (Overloaded to allow fro String salt to be passed in)
     * @param plaintext  Plain text to encrypt
     * @param password {@link TempString} Password to use
     * @param salt Salt to use
     * @return Encrypted String
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static String encryptString(String plaintext, @NonNull TempString password, String salt) {
        if(StringUtilities.isNullOrEmpty(salt)){
            return null;
        }
        return encryptString(plaintext, password, convertStringToBytes(salt));
    }
    
    /**
     * Encrypt a String
     * @param plaintext  Plain text to encrypt
     * @param password {@link TempString} Password to use
     * @param salt Salt to use
     * @return Encrypted String
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static String encryptString(String plaintext, @NonNull TempString password, byte[] salt) {
        if(StringUtilities.isNullOrEmpty(plaintext)){
            return null;
        }
        if(password == null || salt == null){
            return null;
        }
        if(StringUtilities.isNullOrEmpty(password.getTempStringData())){
            return null;
        }
        byte[] encrypted = null;
        try {
            encrypted = encrypt(convertStringToBytes(plaintext), password.getTempStringData().toCharArray(), salt);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException //
                | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException //
                | InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return byteArrayToHexString(encrypted);
    }
    
    /**
     * Encrypts a plaintext with a password.
     * <p>
     * The encryption provides the following security properties:
     * Confidentiality + Integrity
     * <p>
     * This is achieved my using the AES-GCM AEAD blockmode with a randomized IV.
     * <p>
     * The tag is calculated over the version byte, the IV as well as the ciphertext.
     * <p>
     * Finally the encrypted bytes have the following structure:
     * <pre>
     *          +-------------------------------------------------------------------+
     *          |         |               |                             |           |
     *          | version | IV bytes      | ciphertext bytes            |    tag    |
     *          |         |               |                             |           |
     *          +-------------------------------------------------------------------+
     * Length:     1B        12B            len(plaintext) bytes            16B
     * </pre>
     * Note: There is no padding required for AES-GCM, but this also implies that
     * the exact plaintext length is revealed.
     *
     * @param password  password to use for encryption
     * @param plaintext plaintext to encrypt
     * @throws NoSuchAlgorithmException
    //     * @throws NoSuchProviderException
     * @throws NoSuchPaddingException
     * @throws InvalidAlgorithmParameterException
     * @throws InvalidKeyException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     * @throws InvalidKeySpecException
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static byte[] encrypt(byte[] plaintext, char[] password, byte[] salt)
            throws NoSuchAlgorithmException, NoSuchPaddingException,
            InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException,
            IllegalBlockSizeException, InvalidKeySpecException {
        
        // initialise random and generate IV (initialisation vector)
        SecretKey key = deriveAesKey(password, salt, AES_KEY_BITS_LENGTH);
        final byte[] iv = new byte[GCM_IV_BYTES_LENGTH];
        SecureRandom secureRandom;
        if(Build.VERSION.SDK_INT >= 26) {
            secureRandom = SecureRandom.getInstanceStrong();
        } else {
            secureRandom = SecureRandom.getInstance(SECURE_RANDOM_INSTANCE_STRING);
        }
        secureRandom.nextBytes(iv);
        
        // encrypt
        Cipher cipher = Cipher.getInstance(GCM_CRYPTO_NAME);
        GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_BYTES_LENGTH * 8, iv);
        cipher.init(Cipher.ENCRYPT_MODE, key, spec);
        
        // add IV to MAC
        final byte[] versionBytes = new byte[]{VERSION_BYTE};
        cipher.updateAAD(versionBytes);
        cipher.updateAAD(iv);
        
        // encrypt and MAC plaintext
        byte[] ciphertext = cipher.doFinal(plaintext);
        
        // prepend VERSION and IV to ciphertext
        byte[] encrypted = new byte[1 + GCM_IV_BYTES_LENGTH + ciphertext.length];
        int pos = 0;
        System.arraycopy(versionBytes, 0, encrypted, 0, VERSION_BYTE_LENGTH);
        pos += VERSION_BYTE_LENGTH;
        System.arraycopy(iv, 0, encrypted, pos, iv.length);
        pos += iv.length;
        System.arraycopy(ciphertext, 0, encrypted, pos, ciphertext.length);
        return encrypted;
    }
    
    //endregion
    
    //region Decryption
    
    /**
     * Decrypt a String (Overloaded to allow for String salt to be used)
     * @param ciphertext Cipher text to decrypt
     * @param password {@link TempString} Password to use for decryption
     * @param salt Salt to use for decryption
     * @return Decrypted String
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static String decryptString(String ciphertext, @NonNull TempString password, String salt) {
        if(StringUtilities.isNullOrEmpty(salt)){
            return null;
        }
        return decryptString(ciphertext, password, convertStringToBytes(salt));
    }
    
    /**
     * Decrypt a String
     * @param ciphertext Cipher text to decrypt
     * @param password {@link TempString} Password to use for decryption
     * @param salt Salt to use for decryption
     * @return Decrypted String
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static String decryptString(String ciphertext, @NonNull TempString password, byte[] salt) {
        if(StringUtilities.isNullOrEmpty(ciphertext)){
            return null;
        }
        if(password == null || salt == null){
            return null;
        }
        if(StringUtilities.isNullOrEmpty(password.getTempStringData())){
            return null;
        }
        byte[] ct = hexStringToByteArray(ciphertext);
        byte[] plaintext = null;
        try {
            plaintext = decrypt(ct, password.getTempStringData().toCharArray(), salt);
        } catch (AEADBadTagException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeySpecException //
                | InvalidKeyException | InvalidAlgorithmParameterException | IllegalBlockSizeException //
                | BadPaddingException e) {
            e.printStackTrace();
        }
        return EncryptionUtilities.convertBytesToString(plaintext);
    }
    
    
    /**
     * Decrypts an AES-GCM encrypted ciphertext and is
     * the reverse operation of { AesGcmCryptor#encrypt(char[], byte[])}
     *
     * @param password   passphrase for decryption
     * @param ciphertext encrypted bytes
     * @return plaintext bytes
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
    //     * @throws NoSuchProviderException
     * @throws InvalidKeySpecException
     * @throws InvalidAlgorithmParameterException
     * @throws InvalidKeyException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     * @throws IllegalArgumentException           if the length or format of the ciphertext is bad
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static byte[] decrypt(byte[] ciphertext, char[] password, byte[] salt)
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeySpecException,
            InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        
        // input validation
        if (ciphertext == null) {
            throw new IllegalArgumentException("ciphertext cannot be null");
        }
        
        if (ciphertext.length <= VERSION_BYTE_LENGTH + GCM_IV_BYTES_LENGTH + GCM_TAG_BYTES_LENGTH) {
            throw new IllegalArgumentException("ciphertext too short");
        }
        
        // the version must match, we don't decrypt other versions
        // TODO: 3/8/19 determine better approach for this eventually
        if (ciphertext[0] != VERSION_BYTE) {
            throw new IllegalArgumentException("wrong version: " + ciphertext[0]);
        }
        
        // input seems legit, lets decrypt and check integrity
        
        // derive key from password
        SecretKey key = deriveAesKey(password, salt, AES_KEY_BITS_LENGTH);
        
        // init cipher
        Cipher cipher = Cipher.getInstance(GCM_CRYPTO_NAME);
        GCMParameterSpec params = new GCMParameterSpec(GCM_TAG_BYTES_LENGTH * 8,
                ciphertext, VERSION_BYTE_LENGTH, GCM_IV_BYTES_LENGTH
        );
        cipher.init(Cipher.DECRYPT_MODE, key, params);
        
        final int ciphertextOffset = VERSION_BYTE_LENGTH + GCM_IV_BYTES_LENGTH;
        
        // add version and IV to MAC
        cipher.updateAAD(ciphertext, 0, ciphertextOffset);
        
        // decipher and check MAC
        byte[] bb = cipher.doFinal(ciphertext, ciphertextOffset, ciphertext.length - ciphertextOffset);
        return bb;
    }
    
    //endregion
    
    //region Deprecated Methods
    
    
    /**
     * Encrypt the String. Pulled from: https://stackoverflow.com/a/31245375/2480714
     *
     * @param messageToEncrypt String message to encrypt
     * @param password         {@link TempString} Password String
     * @param salt             salt byte[]
     * @return Encrypted byte array
     * @throws Exception throws various exceptions
     */
    @Deprecated
    private static byte[] encryptStringOLD2(@NonNull String messageToEncrypt,
                                        @NonNull TempString password, @NonNull byte[] salt)
            throws NoSuchAlgorithmException, NoSuchPaddingException,
            InvalidKeyException, InvalidParameterSpecException,
            IllegalBlockSizeException, BadPaddingException,
            UnsupportedEncodingException, InvalidAlgorithmParameterException,
            InvalidKeySpecException {
        if(messageToEncrypt == null || salt == null || password == null){
            return null;
        }
        salt = convertSaltTo16Digits(salt);
        SecretKeyFactory factory = SecretKeyFactory.getInstance(PUBLIC_KEY_CRYPT_STANDARD);
        PBEKeySpec spec = new PBEKeySpec(password.getTempStringData().toCharArray(), salt,
                KEY_SPEC_ITERATIONS_COUNT,KEY_SPEC_LENGTH);
        SecretKey secretKey = factory.generateSecret(spec);
        SecretKeySpec secret = new SecretKeySpec(secretKey.getEncoded(), AES);
        Cipher cipher = Cipher.getInstance(CIPHER_INSTANCE_TYPE);
        cipher.init(Cipher.ENCRYPT_MODE, secret);
        byte[] ivBytes = cipher.getParameters().getParameterSpec(IvParameterSpec.class).getIV();
        byte[] encryptedTextBytes = cipher.doFinal(convertStringToBytes(messageToEncrypt));
        byte[] finalByteArray = new byte[ivBytes.length + encryptedTextBytes.length];
        System.arraycopy(ivBytes, 0, finalByteArray, 0, ivBytes.length);
        System.arraycopy(encryptedTextBytes, 0, finalByteArray, ivBytes.length, encryptedTextBytes.length);
        return finalByteArray;
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
    @Deprecated
    private static byte[] encryptStringOLD2(@NonNull String messageToEncrypt,
                                        @NonNull TempString password, @NonNull String salt)
            throws NoSuchAlgorithmException, NoSuchPaddingException,
            InvalidKeyException, InvalidParameterSpecException,
            IllegalBlockSizeException, BadPaddingException,
            UnsupportedEncodingException, InvalidAlgorithmParameterException,
            InvalidKeySpecException {
        if(messageToEncrypt == null || salt == null || password == null){
            return null;
        }
        return encryptStringOLD2(messageToEncrypt, password, EncryptionUtilities.convertStringToBytes(salt));
    }
    
    /**
     * Decrypt the message
     *
     * @param input cipher input text to be decrypted
     * @param password   Password to use to decrypt
     * @param salt       salt to use to decrypt
     * @return Decrypted String
     * @throws Exception throws various exceptions
     */
    @Deprecated
    private static String decryptStringOLD2(@NonNull byte[] input,
                                        @NonNull TempString password, @NonNull byte[] salt)
            throws NoSuchPaddingException, NoSuchAlgorithmException,
            InvalidParameterSpecException, InvalidAlgorithmParameterException,
            InvalidKeyException, BadPaddingException, IllegalBlockSizeException,
            UnsupportedEncodingException, InvalidKeySpecException {
        if(input == null || salt == null || password == null){
            return null;
        }
        byte[] ivBytes = new byte[IV_LENGTH_BYTES];
        System.arraycopy(input, 0, ivBytes, 0, IV_LENGTH_BYTES);
        byte[] encryptedTextBytes = new byte[input.length - ivBytes.length];
        System.arraycopy(input, IV_LENGTH_BYTES, encryptedTextBytes, 0, encryptedTextBytes.length);
        SecretKeyFactory factory = SecretKeyFactory.getInstance(PUBLIC_KEY_CRYPT_STANDARD);
        PBEKeySpec spec = new PBEKeySpec(password.getTempStringData().toCharArray(), salt, KEY_SPEC_ITERATIONS_COUNT, KEY_SPEC_LENGTH);
        SecretKey secretKey = factory.generateSecret(spec);
        SecretKeySpec secret = new SecretKeySpec(secretKey.getEncoded(), AES);
        Cipher cipher = Cipher.getInstance(CIPHER_INSTANCE_TYPE);
        cipher.init(Cipher.DECRYPT_MODE, secret, new IvParameterSpec(ivBytes));
        byte[] decryptedTextBytes = cipher.doFinal(encryptedTextBytes);
        return new String(decryptedTextBytes);
    }
    
    
    /**
     * Overloaded to allow for string salt
     */
    @Deprecated
    private static String decryptStringOLD2(@NonNull byte[] cipherText,
                                        @NonNull TempString password, @NonNull String salt)
            throws NoSuchPaddingException, NoSuchAlgorithmException,
            InvalidParameterSpecException, InvalidAlgorithmParameterException,
            InvalidKeyException, BadPaddingException, IllegalBlockSizeException,
            UnsupportedEncodingException, InvalidKeySpecException {
        if(cipherText == null || salt == null || password == null){
            return null;
        }
        return decryptStringOLD2(cipherText, password, EncryptionUtilities.convertStringToBytes(salt));
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
    @Deprecated
    private static String decryptStringOLD(@NonNull byte[] cipherText,
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
     * Encrypt the String
     *
     * @param messageToEncrypt String message to encrypt
     * @param password         {@link TempString} Password String
     * @param salt             salt byte[]
     * @return Encrypted byte array
     * @throws Exception throws various exceptions
     */
    @Deprecated
    private static byte[] encryptStringOLD(@NonNull String messageToEncrypt,
                                           @NonNull TempString password, @NonNull byte[] salt)
            throws NoSuchAlgorithmException, NoSuchPaddingException,
            InvalidKeyException, InvalidParameterSpecException,
            IllegalBlockSizeException, BadPaddingException,
            UnsupportedEncodingException, InvalidAlgorithmParameterException,
            InvalidKeySpecException {
        
        salt = convertSaltTo16Digits(salt);
        Cipher cipher = Cipher.getInstance(CIPHER_INSTANCE_TYPE);
        IvParameterSpec ivParams;
        ivParams = new IvParameterSpec(salt);
        cipher.init(Cipher.ENCRYPT_MODE, generateKey(password, salt), ivParams);
        byte[] toReturn = cipher.doFinal(convertStringToBytes(messageToEncrypt));
        return toReturn;
    }
    
    /**
     * Overloaded to allow for String salt to be passed
     */
    @Deprecated
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
    @Deprecated
    private static SecretKey generateKey(@NonNull TempString password, @NonNull byte[] salt)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
//        SecureRandom random = new SecureRandom();
//        byte[] salt = new byte[saltLength];
//        random.nextBytes(salt);
        salt = convertSaltTo16Digits(salt);
        PBEKeySpec keySpec = new PBEKeySpec(password.getTempStringData().toCharArray(), salt, //KeySpec
                KEY_SPEC_ITERATIONS_COUNT, KEY_SPEC_LENGTH);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(PUBLIC_KEY_CRYPT_STANDARD);
        byte[] keyBytes = keyFactory.generateSecret(keySpec).getEncoded();
        return new SecretKeySpec(keyBytes, AES);
    }
    
    //endregion
 
    
}
