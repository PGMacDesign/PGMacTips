package com.pgmacdesign.pgmactips.utilities;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.pgmacdesign.pgmactips.misc.CustomAnnotationsBase;
import com.securepreferences.SecurePreferences;

import java.util.Map;
import java.util.Set;

/**
 * This class is for SharedPreferences handling. It utilizes Scottyab's secure-preferences
 * for encryption purposes. This class uses his library, which is listed below:
 * Secure Shared Prefs. Link: https://github.com/scottyab/secure-preferences
 * compile 'com.scottyab:secure-preferences-lib:0.1.4'
 * Created by pmacdowell on 2017-07-13.
 */
@CustomAnnotationsBase.RequiresDependency(requiresDependency = CustomAnnotationsBase.Dependencies.SecurePreferences)
public class SharedPrefsEncrypted {

    //Secured Shared Prefs Objects
    private SecurePreferences.Editor secureEdit;
    private SecurePreferences securePrefs;

    private String sharedPrefsName, encryptedSharedPrefsName, encryptionPassword;
    private Context context;

    /**
     * Method for building an encrypted set of shared prefs
     * @param context Context
     * @param sharedPrefsName prefs name. If null, will use default as package name + '.sp'
     * @param encryptionPassword the password. If null, it will not have a password
     * @return Shared Prefs (encrypted) object
     */
    @CustomAnnotationsBase.RequiresDependency(requiresDependency = CustomAnnotationsBase.Dependencies.SecurePreferences)
    public static SharedPrefsEncrypted getEncryptedSharedPrefsInstance(@NonNull Context context,
                                                                       @Nullable String sharedPrefsName,
                                                                       @Nullable String encryptionPassword){
        String str;
        if(StringUtilities.isNullOrEmpty(sharedPrefsName)){
            String packageName = MiscUtilities.getPackageName(context);
            if(!StringUtilities.isNullOrEmpty(packageName)){
                str = packageName + ".sp";
            } else {
                str = context.getPackageName() + ".sp";
            }
        } else {
            str = sharedPrefsName;
        }
        SharedPrefsEncrypted sp = new SharedPrefsEncrypted(
                context, str, encryptionPassword);
        return sp;
    }

    /**
     * Constructor
     * @param context Context
     * @param sharedPrefsName prefs name
     */
    private SharedPrefsEncrypted(@NonNull Context context, @NonNull String sharedPrefsName){
        this.context = context;
        this.encryptionPassword = null;
        this.encryptedSharedPrefsName = null;
        this.sharedPrefsName = sharedPrefsName;
    }

    /**
     * Constructor (overloaded) for secure prefs (encrypted)
     * @param context Context
     * @param sharedPrefsName prefs name
     * @param password the password. If null, it will not have a password
     */
    private SharedPrefsEncrypted(@NonNull Context context, @NonNull String sharedPrefsName, @Nullable String password){
        if(StringUtilities.isNullOrEmpty(password)){
            //Eliminates any empty Strings as passwords
            password = null;
        }
        this.context = context;
        this.encryptionPassword = password;
        this.encryptedSharedPrefsName = sharedPrefsName;
        this.sharedPrefsName = null;
    }

    /*
    All the methods below are save methods. First param is the key and the second is the
    value. There are multiple overloaded types depending on type passed in.
    */

    public void save(String valueKey, String value) {
        init();
        secureEdit.putString(valueKey, value);
        secureEdit.commit();
    }

    public void save(String valueKey, int value) {
        init();
        secureEdit.putInt(valueKey, value);
        secureEdit.commit();
    }

    public void save(String valueKey, boolean value) {
        init();
        secureEdit.putBoolean(valueKey, value);
        secureEdit.commit();
    }

    public void save(String valueKey, long value) {
        init();
        secureEdit.putLong(valueKey, value);
        secureEdit.commit();
    }

    public void save(String valueKey, double value) {
        init();
        secureEdit.putLong(valueKey, Double.doubleToRawLongBits(value));
        secureEdit.commit();
    }

    public void save(String valueKey, Set<String> values) {
        init();
        secureEdit.putStringSet(valueKey, values);
        secureEdit.commit();
    }

    public void save(String valueKey, String[] values) {
        init();
        for(int i = 0 ; i < values.length; i++){
            secureEdit.putString(valueKey + "-" + i, values[i]);
        }
        secureEdit.commit();
    }

    public void save(String valueKey, int[] values) {
        init();
        for(int i = 0 ; i < values.length; i++){
            secureEdit.putInt(valueKey + "-" + i, values[i]);
        }
        secureEdit.commit();
    }

    public void save(String valueKey, long[] values) {
        init();
        for(int i = 0 ; i < values.length; i++){
            secureEdit.putLong(valueKey + "-" + i, values[i]);
        }
        secureEdit.commit();
    }

    public void save(String valueKey, double[] values) {
        init();
        for(int i = 0 ; i < values.length; i++){
            secureEdit.putLong(valueKey + "-" + i, Double.doubleToRawLongBits(values[i]));
        }
        secureEdit.commit();
    }

    public void save(String valueKey, boolean[] values) {
        init();
        for(int i = 0 ; i < values.length; i++){
            secureEdit.putBoolean(valueKey + "-" + i, values[i]);
        }
        secureEdit.commit();
    }

    /**
     * Will convert the secondary value of type ? to a String and set it into the Shared Prefs
     * @param map Map of type <String, ?>
     */
    public void save(Map<String, ?> map){
        init();
        for (Map.Entry<String, ?> entry : map.entrySet()) {
            String key = entry.getKey();
            Object object = entry.getValue();
            if (!StringUtilities.isNullOrEmpty(key) && object != null) {
                secureEdit.putString(key, object.toString());
            }
        }
        secureEdit.commit();
    }

    //
    //Get methods
    //

    public String getString(String valueKey, String defaultValue) {
        init();
        return securePrefs.getString(valueKey, defaultValue);
    }

    public int getInt(String valueKey, int defaultValue) {
        init();
        return securePrefs.getInt(valueKey, defaultValue);
    }

    public boolean getBoolean(String valueKey, boolean defaultValue) {
        init();
        return securePrefs.getBoolean(valueKey, defaultValue);
    }

    public long getLong(String valueKey, long defaultValue) {
        init();
        return securePrefs.getLong(valueKey, defaultValue);
    }

    public double getDouble(String valueKey, double defaultValue) {
        init();
        return Double.longBitsToDouble(securePrefs.getLong(valueKey, Double.doubleToLongBits(defaultValue)));
    }

    public Set<String> getSet(String valueKey, Set<String> defaultValues) {
        init();
        return securePrefs.getStringSet(valueKey, defaultValues);
    }

    public void clearPref(String[] keys){
        if(MiscUtilities.isArrayNullOrEmpty(keys)){
            return;
        }
        init();
        for(String key : keys) {
            secureEdit.remove(key);
            secureEdit.commit();
        }
    }

    public void clearPref(String key){
        if(StringUtilities.isNullOrEmpty(key)){
            return;
        }
        init();
        secureEdit.remove(key);
        secureEdit.commit();
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
        secureEdit.clear();
        secureEdit.commit();

    }

    /**
     * Gets everything in the shared preferences
     * @return A map of String to objects
     */
    public Map<String, ?> getAllPrefs(){
        init();
        Map<String, ?> myMap = securePrefs.getAll();
        return myMap;
    }

    private SecurePreferences getPrefsEncrypted(){
        securePrefs = new SecurePreferences(context, encryptionPassword, encryptedSharedPrefsName);
        return securePrefs;
    }

    private void init(){
        if (securePrefs == null) {
            getPrefsEncrypted();
        }
        if (secureEdit == null) {
            secureEdit = securePrefs.edit();
        }
    }
}
