/**
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package com.pgmacdesign.pgmactips.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.Base64;

import com.pgmacdesign.pgmactips.misc.TempString;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


/**
 * This class is for SharedPreferences handling.
 * Encrypted shared prefs schema pulled from:
 * https://github.com/scottyab/secure-preferences/blob/master/library/src/main/java/com/securepreferences/SecurePreferences.java
 * Created by pmacdowell on 2016/08/12.
 */
public class SharedPrefs {

    //region Static final Vars
    private static final String OLD_AND_NEW_PW_DONT_MATCH = "Old and new passwords are not the same. If you want to change your password, please call the changePassword() method.";
    private static final String ENCRYPTED_PREFIX = "enc_";
    private static final String INVALID_RE_ENABLE_ENCRYPTION_CALL = "You must call setEncrypted() or initialize encryption through the constructor. This method is to re-enable after disabling";
    private static final String ENABLE_ENCRYPTION_BEFORE_CHANGING_PASSWORD = "You must have encryption enabled before a password can be changed. Please do so either in the constructor or by calling setEncrypted()";

    //endregion

    //region Instance Variables

    //Shared Prefs vars
    private SharedPreferences.Editor edit1;
    private SharedPreferences prefs1;

    //Misc vars
    private String sharedPrefsName;
    private Context context;

    //Encryption vars
    private boolean isEncrypted;
    private TempString password;
    private String salt;
//    private EncryptionUtilitiesV2.SecretKeys keys, keyKeys;

    //endregion

    ////////////////////////////
    //region Static Instance Builders//
    ////////////////////////////

    /**
     * Create a shared prefs instance
     * @param context context, should be ApplicationContext not Activity
     * @param sharedPrefsName Name of shared prefs. If null or empty, will attempt to use package name
     * @return Instance of SharedPrefs with encryption turned off
     */
    public static SharedPrefs getSharedPrefsInstance(@NonNull Context context,
                                                     @Nullable String sharedPrefsName){
        return new SharedPrefs(context, getSPName(sharedPrefsName, context));
    }

    /**
     * Create an encrypted shared prefs instance
     * @param context context, should be ApplicationContext not Activity
     * @param sharedPrefsName Name of shared prefs. If null or empty, will attempt to use package name
     * @param password Password to be used for encryption
     * @return Instance of SharedPrefs with encryption turned on
     */
    public static SharedPrefs getSharedPrefsInstance(@NonNull Context context,
                                                     @Nullable String sharedPrefsName,
                                                     @NonNull TempString password) throws GeneralSecurityException{
        return new SharedPrefs(context, getSPName(sharedPrefsName, context), password);
    }

    /**
     * Create an encrypted shared prefs instance
     * @param context context, should be ApplicationContext not Activity
     * @param sharedPrefsName Name of shared prefs. If null or empty, will attempt to use package name
     * @param password Password to be used for encryption
     * @param salt Salt to be used for encryption
     * @return Instance of SharedPrefs with encryption turned on
     */
    public static SharedPrefs getSharedPrefsInstance(@NonNull Context context,
                                                     @Nullable String sharedPrefsName,
                                                     @NonNull TempString password,
                                                     @NonNull String salt) throws GeneralSecurityException{
        return new SharedPrefs(context, getSPName(sharedPrefsName, context), password, salt);
    }

    //endregion

    //region Private Constructors
    /**
     * Constructor
     * @param context Context
     * @param sharedPrefsName prefs name
     */
    private SharedPrefs(@NonNull Context context, @NonNull String sharedPrefsName){
        this.context = context;
        this.sharedPrefsName = sharedPrefsName;
        this.isEncrypted = false;
    }

    /**
     * Constructor for encrypted prefs
     * @param context Context
     * @param sharedPrefsName prefs name
     * @param password password to use
     */
    private SharedPrefs(@NonNull Context context,
                        @NonNull String sharedPrefsName,
                        @NonNull TempString password) throws GeneralSecurityException{
        this.context = context;
        this.sharedPrefsName = sharedPrefsName;
        this.isEncrypted = true;
        this.password = password;
        this.salt = getIdentifier(context);
        this.encryptionInit();
    }

    /**
     * Constructor for encrypted prefs
     * @param context Context
     * @param sharedPrefsName prefs name
     * @param password password to use
     * @param salt salt to use
     */
    private SharedPrefs(@NonNull Context context,
                        @NonNull String sharedPrefsName,
                        @NonNull TempString password,
                        @NonNull String salt) throws GeneralSecurityException{
        this.context = context;
        this.sharedPrefsName = sharedPrefsName;
        this.isEncrypted = true;
        this.password = password;
        this.salt = (StringUtilities.isNullOrEmpty(salt) ? getIdentifier(context) : salt);
        this.encryptionInit();
    }

    //endregion

    //////////////////
    //region Public Setters//
    //////////////////

    /**
     * Change Shared prefs to be encrypted. Requires the password.
     * Overloaded to allow for empty salt to be passed
     * @param password Password of your choosing
     */
    public void setEncrypted(@NonNull TempString password) throws GeneralSecurityException{
        this.setEncrypted(password, null);
    }

    /**
     * Change Shared prefs to be encrypted. Requires the password and (nullable) salt
     * @param password Password of your choosing
     * @param salt Salt of your choosing. If null, will default to the serial number
     */
    public void setEncrypted(@NonNull TempString password, @Nullable String salt) throws GeneralSecurityException{
        if(this.password != null){
            if(!this.password.equals(password)){
                throw new GeneralSecurityException(OLD_AND_NEW_PW_DONT_MATCH);
            }
        }
        this.isEncrypted = true;
        this.password = password;
        this.salt = (StringUtilities.isNullOrEmpty(salt) ? getIdentifier(context) : salt);
        this.encryptionInit();
    }

    /**
     * Change SharedPrefs to be not encrypted. Will stop using encryption variables when this is called
     */
    public void disableEncryption() throws GeneralSecurityException{
        this.isEncrypted = false;
    }

    /**
     * Change SharedPrefs to be encrypted again. NOTE! This is only used after
     * {@link SharedPrefs#disableEncryption()} is called in order to re-enable it. Examples of
     * usage would be if you wanted to add in both encrypted and non-encrypted data to the Shared Prefs.
     */
    public void reEnableEncryption() throws GeneralSecurityException{
        if(this.password == null){
            throw new GeneralSecurityException(INVALID_RE_ENABLE_ENCRYPTION_CALL);
        }
        if(StringUtilities.isNullOrEmpty(this.password.getTempStringData())){
            throw new GeneralSecurityException(INVALID_RE_ENABLE_ENCRYPTION_CALL);
        }
        if(StringUtilities.isNullOrEmpty(this.getSalt())){
            throw new GeneralSecurityException(INVALID_RE_ENABLE_ENCRYPTION_CALL);
        }
        this.isEncrypted = true;
        this.encryptionInit();
    }

    //endregion

    //////////////////
    //region Init Functions//
    //////////////////

    /**
     * This is used in the constructor or after adding / removing encryption
     */
    private void encryptionInit() throws GeneralSecurityException{
        try {
//            final byte[] saltBytes = this.getSalt().getBytes();
//            final byte[] keySaltBytes = this.getKeySalt().getBytes();
//            this.keys = EncryptionUtilitiesV2.generateKeyFromPassword(this.password.getTempStringData(), saltBytes);
//            this.keyKeys = EncryptionUtilitiesV2.generateKeyFromPassword(this.password.getTempStringData(), keySaltBytes);
        } catch (Exception e){
            e.printStackTrace();
            throw new GeneralSecurityException("Could not generate key from password: " + e.getMessage());
        }
    }

    /**
     * Init func to be used before CRUD operations
     */
    private void init(){
        this.prefs1 = getPrefs();
        this.edit1 = this.prefs1.edit();
    }

    //endregion

    //////////////////
    //region Save Methods//
    //////////////////
    /*
    All the methods below are save methods. First param is the key and the second is the
    value. There are multiple overloaded types depending on type passed in.
    */

    public void save(String valueKey, String value) {
        init();
        L.m("SAVE STRING. " + "isEncrypted? " + isEncrypted);
        L.m("SAVE STRING. " + "value Key before encryption == " + valueKey);
        L.m("SAVE STRING. " + "value before encryption == " + value);
        if(this.isEncrypted){
//            valueKey = SharedPrefs.hashPrefKey(valueKey);
            valueKey = encryptKey(valueKey);
            String newValue = encrypt(value);
            L.m("SAVE STRING. " + "value Key after encryption == " + valueKey);
            L.m("SAVE STRING. " + "value after encryption == " + newValue);
            this.edit1.putString(valueKey, newValue);
        } else {
            this.edit1.putString(valueKey, value);
        }
        this.edit1.commit();
    }

    public void save(String valueKey, int value) {
        init();
        if(this.isEncrypted){
//            valueKey = SharedPrefs.hashPrefKey(valueKey);
            valueKey = encryptKey(valueKey);
            String newValue = encrypt(Integer.toString(value));
            this.edit1.putString(valueKey, newValue);
        } else {
            this.edit1.putInt(valueKey, value);
        }
        this.edit1.commit();
    }

    public void save(String valueKey, boolean value) {
        init();
        if(this.isEncrypted){
//            valueKey = SharedPrefs.hashPrefKey(valueKey);
            valueKey = encryptKey(valueKey);
            String newValue = encrypt(Boolean.toString(value));
            this.edit1.putString(valueKey, newValue);
        } else {
            this.edit1.putBoolean(valueKey, value);
        }
        this.edit1.commit();
    }

    public void save(String valueKey, long value) {
        init();
        if(this.isEncrypted){
//            valueKey = SharedPrefs.hashPrefKey(valueKey);
            valueKey = encryptKey(valueKey);
            String newValue = encrypt(Long.toString(value));
            this.edit1.putString(valueKey, newValue);
        } else {
            this.edit1.putLong(valueKey, value);
        }
        this.edit1.commit();
    }

    public void save(String valueKey, double value) {
        init();
        if(this.isEncrypted){
//            valueKey = SharedPrefs.hashPrefKey(valueKey);
            valueKey = encryptKey(valueKey);
            //This would be if we had known types
//            long rawLongBits = Double.doubleToRawLongBits(value);
//            String newValue = encrypt(Long.toString(rawLongBits));
            //Use these instead due to having no knowledge of type on the pull
            String newValue = encrypt(Double.toString(value));
            this.edit1.putString(valueKey, newValue);
        } else {
            this.edit1.putLong(valueKey, Double.doubleToRawLongBits(value));
        }
        this.edit1.commit();
    }

    public void save(String valueKey, Set<String> values) {
        init();
        if(this.isEncrypted){
//            valueKey = SharedPrefs.hashPrefKey(valueKey);
            valueKey = encryptKey(valueKey);
            final Set<String> encryptedValues = new HashSet<String>(
                    values.size());
            for (String value : values) {
                encryptedValues.add(encrypt(value));
            }
            this.edit1.putStringSet(valueKey, encryptedValues);
        } else {
            this.edit1.putStringSet(valueKey, values);
        }
        this.edit1.commit();
    }

    /**
     * Will convert the secondary value of type ? to a String and set it into the Shared Prefs
     * @param map Map of type <String, ?>
     */
    public void save(Map<String, ?> map){
        init();
        if(this.isEncrypted){
            for (Map.Entry<String, ?> entry : map.entrySet()) {
//                String key = SharedPrefs.hashPrefKey(entry.getKey());
                String key = encryptKey(entry.getKey());
                Object object1 = entry.getValue();
                if (!StringUtilities.isNullOrEmpty(key) && object1 != null) {
                    this.edit1.putString(key, encrypt(object1.toString()));
                }
            }
        } else {
            for (Map.Entry<String, ?> entry : map.entrySet()) {
                String key = entry.getKey();
                Object object = entry.getValue();
                if (!StringUtilities.isNullOrEmpty(key) && object != null) {
                    this.edit1.putString(key, object.toString());
                }
            }
        }
        this.edit1.commit();
    }

    //endregion

    ///////////////
    //region Get Methods//
    ///////////////

    public String getString(String valueKey, String defaultValue) {
        init();
        L.m("GET STRING. " + "isEncrypted? " + this.isEncrypted);
        if(this.isEncrypted){
//            valueKey = SharedPrefs.hashPrefKey(valueKey);
            L.m("GET STRING. " + "key before encryption == " + valueKey);
            valueKey = encryptKey(valueKey);
            L.m("GET STRING. " + "key after encryption == " + valueKey);
            String value = this.prefs1.getString(valueKey, null);
            L.m("GET STRING. " + "value after extraction, but before encryption == " + value);
            if(value == null){
                return defaultValue;
            } else {
                String decryptedString = decrypt(value);
                L.m("GET STRING. " + "value after encryption == " + decryptedString);
                return decryptedString;
            }
        } else {
            return this.prefs1.getString(valueKey, defaultValue);
        }
    }

    public int getInt(String valueKey, int defaultValue) {
        init();
        if(this.isEncrypted){
//            valueKey = SharedPrefs.hashPrefKey(valueKey);
            valueKey = encryptKey(valueKey);
            String value = this.prefs1.getString(valueKey, null);
            if(value == null){
                return defaultValue;
            } else {
                String decryptedString = decrypt(value);
                try {
                    return Integer.parseInt(decryptedString);
                } catch (Exception e){
                    e.printStackTrace(); //To prevent parsing or casting issues
                    return defaultValue;
                }
            }
        } else {
            return this.prefs1.getInt(valueKey, defaultValue);
        }
    }

    public boolean getBoolean(String valueKey, boolean defaultValue) {
        init();
        if(this.isEncrypted){
//            valueKey = SharedPrefs.hashPrefKey(valueKey);
            valueKey = encryptKey(valueKey);
            String value = this.prefs1.getString(valueKey, null);
            if(value == null){
                return defaultValue;
            } else {
                String decryptedString = decrypt(value);
                try {
                    return Boolean.parseBoolean(decryptedString);
                } catch (Exception e){
                    e.printStackTrace(); //To prevent parsing or casting issues
                    return defaultValue;
                }
            }
        } else {
            return this.prefs1.getBoolean(valueKey, defaultValue);
        }
    }

    public long getLong(String valueKey, long defaultValue) {
        init();
        if(this.isEncrypted){
//            valueKey = SharedPrefs.hashPrefKey(valueKey);
            valueKey = encryptKey(valueKey);
            String value = this.prefs1.getString(valueKey, null);
            if(value == null){
                return defaultValue;
            } else {
                String decryptedString = decrypt(value);
                try {
                    return Long.parseLong(decryptedString);
                } catch (Exception e){
                    e.printStackTrace(); //To prevent parsing or casting issues
                    return defaultValue;
                }
            }
        } else {
            return this.prefs1.getLong(valueKey, defaultValue);
        }
    }

    public double getDouble(String valueKey, double defaultValue) {
        init();
        if(this.isEncrypted){
//            valueKey = SharedPrefs.hashPrefKey(valueKey);
            valueKey = encryptKey(valueKey);
            String value = this.prefs1.getString(valueKey, null);
            if(value == null){
                return defaultValue;
            } else {
                String decryptedString = decrypt(value);
                try {
                    //Would use this if we knew the return type in all situations, but not in getAllPrefs
//                    long rawBitsLong = Long.parseLong(decryptedString);
//                    double dbl = Double.longBitsToDouble(rawBitsLong);
//                    return Double.longBitsToDouble(rawBitsLong);
                    //Instead use this since it is just a String
                    Double dbl = Double.parseDouble(decryptedString);
                    return dbl;
                } catch (Exception e){
                    e.printStackTrace(); //To prevent parsing or casting issues
                    return defaultValue;
                }
            }
        } else {
            return Double.longBitsToDouble(this.prefs1.getLong(valueKey, Double.doubleToLongBits(defaultValue)));
        }
    }

    public Set<String> getSet(String valueKey, Set<String> defaultValues) {
        init();
        if(this.isEncrypted){
//            valueKey = SharedPrefs.hashPrefKey(valueKey);
            valueKey = encryptKey(valueKey);
            Set<String> encryptedSet = this.prefs1.getStringSet(valueKey, null);
            if(encryptedSet == null) {
                return defaultValues;
            } else {
                if(encryptedSet.size() <= 0){
                    return defaultValues;
                }
                final Set<String> decryptedSet = new HashSet<String>(
                        encryptedSet.size());
                for (String encryptedValue : encryptedSet) {
                    decryptedSet.add(decrypt(encryptedValue));
                }
                return decryptedSet;
            }
        } else {
            return this.prefs1.getStringSet(valueKey, defaultValues);
        }
    }

    /**
     * Gets everything in the shared preferences
     * @return A map of String to objects
     */
    public Map<String, ?> getAllPrefs(){
        init();
        if(this.isEncrypted){
            Map<String, ?> myMap = this.prefs1.getAll();
            if(MiscUtilities.isMapNullOrEmpty(myMap)){
                return myMap;
            }
            Map<String, String> map = new HashMap<>();
            for(Map.Entry<String, ?> m : myMap.entrySet()){
                try {
                    String key = m.getKey();
                    key = decryptKey(key);
                    String value = m.getValue().toString();
                    value = decrypt(value);
                    map.put(key, value);
                } catch (NullPointerException e){} //To catch null errors
            }
            return map;
        } else {
            Map<String, ?> myMap = this.prefs1.getAll();
            return myMap;
        }
    }

    //endregion

    //region Clear Pref Values

    public void clearPref(String[] keys){
        if(MiscUtilities.isArrayNullOrEmpty(keys)){
            return;
        }
        init();
        for(String key : keys) {
            if(this.isEncrypted){
//                this.edit1.remove(hashPrefKey(key));
                this.edit1.remove(encryptKey(key));
            } else {
                this.edit1.remove(key);
            }
            this.edit1.commit();
        }
    }

    public void clearPref(String key){
        if(StringUtilities.isNullOrEmpty(key)){
            return;
        }
        init();
        if(this.isEncrypted){
//            this.edit1.remove(hashPrefKey(key));
            this.edit1.remove(encryptKey(key));
        } else {
            this.edit1.remove(key);
        }
        this.edit1.commit();
    }

    /**
     * Clears ALL preferences stored.
     * @param areYouSure Confirmation boolean
     *                   (To prevent IDE Autocomplete from using this func by accident)
     */
    public void clearAllPrefs(boolean areYouSure){
        if(!areYouSure){
            return;
        }
        init();
        this.edit1.clear();
        this.edit1.commit();
        this.prefs1 = null;
        try {
            //Check the 'enc_' variation
            if(!StringUtilities.isNullOrEmpty(this.password)) {
                if (this.isEncrypted) {
                    this.disableEncryption();
                } else {
                    this.reEnableEncryption();
                }
                init();
                this.edit1.clear();
                this.edit1.commit();
                this.prefs1 = null;

                if(this.isEncrypted){
                    this.disableEncryption();
                } else {
                    this.reEnableEncryption();
                }
            }
        } catch (Exception e){}
    }

    //endregion

    //region UPublic tilities

    /**
     * Change the stored password.
     * Copies the SP file with a new pointer in reference to the newly created objects
     * @param newPassword
     * @throws GeneralSecurityException
     */
    public void changePassword(@NonNull TempString newPassword) throws GeneralSecurityException{
        if(!isEncrypted){
            throw new GeneralSecurityException(ENABLE_ENCRYPTION_BEFORE_CHANGING_PASSWORD);
        }
        if(StringUtilities.isNullOrEmpty(newPassword.getTempStringData())){
            throw new GeneralSecurityException("Please pass a valid password");
        }

        //Get all SP data
        Map<String, ?> encryptedMap = this.getAllPrefs();

        //Clear data and create new ones
        clearAllPrefs(true);
        destroySensitiveData();
        this.password = newPassword;
        //Leave salt alone for now
        this.isEncrypted = true;
        this.encryptionInit();

        //Re-put data into the new SP file
        if(!MiscUtilities.isMapNullOrEmpty(encryptedMap)) {
            for (Map.Entry<String, ?> map : encryptedMap.entrySet()) {
                String key = map.getKey();
                Object o = map.getValue();
                if(o != null) {
                    String value = o.toString();
                    if (!StringUtilities.isNullOrEmpty(key) && !StringUtilities.isNullOrEmpty(value)) {
                        this.save(key, value);
                    }
                }
            }
        }
    }

    /**
     * Destroys all keys and passwords in memory.
     * Will also revert back to non-encrypted so the class can still be used
     */
    public void destroySensitiveData(){
        try {
            this.destroyKeys();
            this.destroyPassword();
            this.isEncrypted = false;
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    //endregion

    ///////////////////////////
    //region Private Utility Methods//
    ///////////////////////////

    /**
     * nulls in memory keys
     */
    private void destroyKeys() {
//        this.keys = null;
//        this.keyKeys = null;
    }

    /**
     * Destroys password in memory
     */
    private void destroyPassword(){
        if(this.password != null){
            this.password.disposeData();
        }
    }

    /**
     * Get the Prefs for editing
     * @return
     */
    private SharedPreferences getPrefs(){
        return (this.isEncrypted)
                ? this.context.getSharedPreferences((ENCRYPTED_PREFIX + this.sharedPrefsName), Context.MODE_PRIVATE)
                : this.context.getSharedPreferences(this.sharedPrefsName, Context.MODE_PRIVATE);
    }

    /**
     * Simple getter for the salt in case it is null
     * @return
     */
    private String getSalt(){
        if(StringUtilities.isNullOrEmpty(this.salt)){
            return SharedPrefs.getIdentifier(this.context);
        } else {
            return this.salt;
        }
    }

    /**
     * Simple getter for the salt in case it is null
     * @return
     */
    private String getKeySalt(){
        return StringUtilities.reverseString(getSalt());
    }

//    private String getDeviceId() {
//        try {
//            String serial;
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                //Can throw permissions exception if READ_PHONE_STATE not included
//                //Needs permission READ_PHONE_STATE
//                serial = android.os.Build.getSerial();
//            } else {
//                serial = android.os.Build.SERIAL;
//            }
//            return serial;
//        } catch (SecurityException se) {
//            se.printStackTrace();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
//    }

    /**
     * Get the SP name using the context in case it is null or empty
     * @param sharedPrefsName
     * @return
     */
    private static String getSPName(@Nullable String sharedPrefsName, @NonNull Context context){
        String str;
        if(StringUtilities.isNullOrEmpty(sharedPrefsName)){
            String packageName = SystemUtilities.getPackageName(context);
            if(!StringUtilities.isNullOrEmpty(packageName)){
                str = packageName + ".sp";
            } else {
                str = context.getPackageName() + ".sp";
            }
        } else {
            str = sharedPrefsName;
        }
        return str;
    }

    /**
     * Gets the salt string to be used from either the device serial number or the package
     */
    private static String getIdentifier(Context context) {
        String toReturn = null;
        try {
            toReturn = SystemUtilities.getPackageName(context);
        } catch (Exception e){}
        if(StringUtilities.isNullOrEmpty(toReturn)){
            try {
                toReturn = SystemUtilities.getDeviceSerialNumber(context);
            } catch (Exception e){
                toReturn = "pgmacdesign.provided.salt";
            }
        }
        return toReturn;
    }

    /**
     * Uses device and application values to generate the pref key for the encryption key
     *
     * @return String to be used as the AESkey Pref key
     * @throws GeneralSecurityException if something goes wrong in generation
     */
//    private String generateAesKeyName() throws GeneralSecurityException {
//        final String password = this.context.getPackageName();
//        final byte[] salt = getSalt().getBytes();
//        EncryptionUtilitiesV2.SecretKeys generatedKeyName = EncryptionUtilitiesV2
//                .generateKeyFromPassword(password, salt);
//        return hashPrefKey(generatedKeyName.toString());
//    }

    /**
     * Hash the key in the key-value pair of the shared prefs.
     *
     * @param prefKey key pref to be hashed
     * @return SHA-256 Hash of the preference key
     */
//    static String hashPrefKey(String prefKey) {
//        final MessageDigest digest;
//        try {
//            digest = MessageDigest.getInstance("SHA-256");
//            byte[] bytes = prefKey.getBytes("UTF-8");
//            digest.update(bytes, 0, bytes.length);
//
//            return Base64.encodeToString(digest.digest(), EncryptionUtilitiesV2.BASE64_FLAGS);
//
//        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }

    /**
     * Encrypt the key in the key value pair portion of the Shared Prefs
     * @param key String (key) to encrypt
     * @return Encrypted String. If it fails, will send back plain text string
     */
    private String encryptKey(String key){
        if (StringUtilities.isNullOrEmpty(key)) {
            return key;
        }
        try {
            return new String(EncryptionUtilities.encryptString(key, this.password, this.salt));
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
            return key;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return key;
    }

    /**
     * Encrypt the clear text
     * @param cleartext Text to encrypt
     * @return obfuscated and encrypted text. Will return passed cleartext if a problem occurs.
     */
    private String encrypt(String cleartext) {
        if (StringUtilities.isNullOrEmpty(cleartext)) {
            return cleartext;
        }
        try {
            return new String(EncryptionUtilities.encryptString(cleartext, this.password, this.salt));
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
            return cleartext;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return cleartext;
    }

    /**
     * Decrypt a String
     * @param cipherText Text to decrypt
     * @return decrypted plain text, unless decryption fails, in which case null
     */
    private String decrypt(final String cipherText) {
        if (StringUtilities.isNullOrEmpty(cipherText)) {
            return cipherText;
        }
        try {
            return EncryptionUtilities.decryptString(cipherText.getBytes(), this.password, this.salt);
//            EncryptionUtilitiesV2.CipherTextIvMac cipherTextIvMac = new EncryptionUtilitiesV2.CipherTextIvMac(cipherText);
//            return EncryptionUtilitiesV2.decryptString(cipherTextIvMac, this.keys);
        } catch (IllegalArgumentException iae) { //Will trigger if trying to decrypt non-mac)
            //No logging for now
        } catch (GeneralSecurityException |
                UnsupportedEncodingException e) {
//            e.printStackTrace(); //Removing for now
        }
        return null;
    }

    /**
     * Decrypt a String (the key)
     * @param cipherText Text to decrypt
     * @return decrypted plain text, unless decryption fails, in which case null
     */
    private String decryptKey(final String cipherText) {
        if (StringUtilities.isNullOrEmpty(cipherText)) {
            return cipherText;
        }
        try {
            return EncryptionUtilities.decryptString(cipherText.getBytes(), this.password, this.salt);
//            EncryptionUtilitiesV2.CipherTextIvMac cipherTextIvMac = new EncryptionUtilitiesV2.CipherTextIvMac(cipherText);
//            return EncryptionUtilitiesV2.decryptString(cipherTextIvMac, this.keyKeys);
        } catch (IllegalArgumentException iae) { //Will trigger if trying to decrypt non-mac)
            //No logging for now
        } catch (GeneralSecurityException |
                UnsupportedEncodingException e) {
            //e.printStackTrace(); //Removing for now
        }
        return cipherText;
    }

    //endregion
}
