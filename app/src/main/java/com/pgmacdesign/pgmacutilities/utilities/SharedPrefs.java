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
package com.pgmacdesign.pgmacutilities.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import com.securepreferences.SecurePreferences;

import java.util.Map;
import java.util.Set;



/**
 * This class is for SharedPreferences handling. It utilizes Scottyab's secure-preferences
 * for encryption purposes if utilized. This class uses his library, which is listed below:
 * Secure Shared Prefs. Link: https://github.com/scottyab/secure-preferences
 * compile 'com.scottyab:secure-preferences-lib:0.1.4'
 * Created by pmacdowell on 2016/08/12.
 */
public class SharedPrefs {

    //Shared Prefs objects
    private SharedPreferences.Editor edit1;
    private SharedPreferences prefs1;
    //Secured Shared Prefs Objects
    private SecurePreferences.Editor secureEdit;
    private SecurePreferences securePrefs;

    private String sharedPrefsName, encryptedSharedPrefsName, encryptionPassword;
    private Context context;
    private boolean isEncrypted;

    /**
     * Build a shared prefs instance and return it
     * @param context Context
     * @param sharedPrefsName shared prefs name
     * @return SharedPrefs Object
     */
    public static SharedPrefs getSharedPrefsInstance(@NonNull Context context, @NonNull String sharedPrefsName){
        SharedPrefs sp = new SharedPrefs(context, sharedPrefsName);
        return sp;
    }

    /**
     * Method for building an encrypted set of shared prefs
     * @param context Context
     * @param sharedPrefsName prefs name
     * @param encryptionPassword the password. If null, it will not have a password
     * @return Shared Prefs (encrypted) object
     */
    public static SharedPrefs getEncryptedSharedPrefsInstance(@NonNull Context context,
                                                              @NonNull String sharedPrefsName,
                                                              String encryptionPassword){
        SharedPrefs sp = new SharedPrefs(context, sharedPrefsName, encryptionPassword);
        return sp;
    }

    /**
     * Constructor
     * @param context Context
     * @param sharedPrefsName prefs name
     */
    private SharedPrefs(@NonNull Context context, @NonNull String sharedPrefsName){
        this.context = context;
        this.encryptionPassword = null;
        this.encryptedSharedPrefsName = null;
        this.sharedPrefsName = sharedPrefsName;
        this.isEncrypted = false;
    }

    /**
     * Constructor (overloaded) for secure prefs (encrypted)
     * @param context Context
     * @param sharedPrefsName prefs name
     * @param password the password. If null, it will not have a password
     */
    private SharedPrefs(@NonNull Context context, @NonNull String sharedPrefsName, String password){
        if(StringUtilities.isNullOrEmpty(password)){
            //Eliminates any empty Strings as passwords
            password = null;
        }
        this.context = context;
        this.encryptionPassword = password;
        this.encryptedSharedPrefsName = sharedPrefsName;
        this.sharedPrefsName = null;
        this.isEncrypted = true;
    }

    /*
    All the methods below are save methods. First param is the key and the second is the
    value. There are multiple overloaded types depending on type passed in.
    */

    public void save(String valueKey, String value) {
        init();
        if(isEncrypted){
            secureEdit.putString(valueKey, value);
            secureEdit.commit();
        } else {
            edit1.putString(valueKey, value);
            edit1.commit();
        }
    }

    public void save(String valueKey, int value) {
        init();
        if(isEncrypted){
            secureEdit.putInt(valueKey, value);
            secureEdit.commit();
        } else {
            edit1.putInt(valueKey, value);
            edit1.commit();
        }
    }

    public void save(String valueKey, boolean value) {
        init();
        if(isEncrypted){
            secureEdit.putBoolean(valueKey, value);
            secureEdit.commit();
        } else {
            edit1.putBoolean(valueKey, value);
            edit1.commit();
        }
    }

    public void save(String valueKey, long value) {
        init();
        if(isEncrypted){
            secureEdit.putLong(valueKey, value);
            secureEdit.commit();
        } else {
            edit1.putLong(valueKey, value);
            edit1.commit();
        }
    }

    public void save(String valueKey, double value) {
        init();
        if(isEncrypted){
            secureEdit.putLong(valueKey, Double.doubleToRawLongBits(value));
            secureEdit.commit();
        } else {
            edit1.putLong(valueKey, Double.doubleToRawLongBits(value));
            edit1.commit();
        }
    }

    public void save(String valueKey, Set<String> values) {
        init();
        if(isEncrypted){
            secureEdit.putStringSet(valueKey, values);
            secureEdit.commit();
        } else {
            edit1.putStringSet(valueKey, values);
            edit1.commit();
        }
    }

    public void save(String valueKey, String[] values) {
        init();
        if(isEncrypted){
            for(int i = 0 ; i < values.length; i++){
                secureEdit.putString(valueKey + "-" + i, values[i]);
            }
            secureEdit.commit();
        } else {
            for(int i = 0 ; i < values.length; i++){
                edit1.putString(valueKey + "-" + i, values[i]);
            }
            edit1.commit();
        }
    }

    public void save(String valueKey, int[] values) {
        init();
        if(isEncrypted){
            for(int i = 0 ; i < values.length; i++){
                secureEdit.putInt(valueKey + "-" + i, values[i]);
            }
            secureEdit.commit();
        } else {
            for(int i = 0 ; i < values.length; i++){
                edit1.putInt(valueKey + "-" + i, values[i]);
            }
            edit1.commit();
        }
    }

    public void save(String valueKey, long[] values) {
        init();
        if(isEncrypted){
            for(int i = 0 ; i < values.length; i++){
                secureEdit.putLong(valueKey + "-" + i, values[i]);
            }
            secureEdit.commit();
        } else {
            for(int i = 0 ; i < values.length; i++){
                edit1.putLong(valueKey + "-" + i, values[i]);
            }
            edit1.commit();
        }
    }

    public void save(String valueKey, double[] values) {
        init();
        if(isEncrypted){
            for(int i = 0 ; i < values.length; i++){
                secureEdit.putLong(valueKey + "-" + i, Double.doubleToRawLongBits(values[i]));
            }
            secureEdit.commit();
        } else {
            for(int i = 0 ; i < values.length; i++){
                edit1.putLong(valueKey + "-" + i, Double.doubleToRawLongBits(values[i]));
            }
            edit1.commit();
        }
    }

    public void save(String valueKey, boolean[] values) {
        init();
        if(isEncrypted){
            for(int i = 0 ; i < values.length; i++){
                secureEdit.putBoolean(valueKey + "-" + i, values[i]);
            }
            secureEdit.commit();
        } else {
            for(int i = 0 ; i < values.length; i++){
                edit1.putBoolean(valueKey + "-" + i, values[i]);
            }
            edit1.commit();
        }
    }

    /**
     * Will convert the secondary value of type ? to a String and set it into the Shared Prefs
     * @param map Map of type <String, ?>
     */
    public void save(Map<String, ?> map){
        init();

        if(isEncrypted) {
            for (Map.Entry<String, ?> entry : map.entrySet()) {
                String key = entry.getKey();
                Object object = entry.getValue();
                if (!StringUtilities.isNullOrEmpty(key) && object != null) {
                    secureEdit.putString(key, object.toString());
                }
            }
            secureEdit.commit();
        } else {
            for (Map.Entry<String, ?> entry : map.entrySet()) {
                String key = entry.getKey();
                Object object = entry.getValue();
                if (!StringUtilities.isNullOrEmpty(key) && object != null) {
                    edit1.putString(key, object.toString());
                }
            }
            edit1.commit();
        }
    }

    //
    //Get methods
    //

    public String getString(String valueKey, String defaultValue) {
        init();
        if(isEncrypted){
            return securePrefs.getString(valueKey, defaultValue);
        } else {
            return prefs1.getString(valueKey, defaultValue);
        }
    }

    public int getInt(String valueKey, int defaultValue) {
        init();
        if(isEncrypted){
            return securePrefs.getInt(valueKey, defaultValue);
        } else {
            return prefs1.getInt(valueKey, defaultValue);
        }
    }

    public boolean getBoolean(String valueKey, boolean defaultValue) {
        init();
        if(isEncrypted){
            return securePrefs.getBoolean(valueKey, defaultValue);
        } else {
            return prefs1.getBoolean(valueKey, defaultValue);
        }
    }

    public long getLong(String valueKey, long defaultValue) {
        init();
        if(isEncrypted){
            return securePrefs.getLong(valueKey, defaultValue);
        } else {
            return prefs1.getLong(valueKey, defaultValue);
        }
    }

    public double getDouble(String valueKey, double defaultValue) {
        init();
        if(isEncrypted){
            return Double.longBitsToDouble(securePrefs.getLong(valueKey, Double.doubleToLongBits(defaultValue)));
        } else {
            return Double.longBitsToDouble(prefs1.getLong(valueKey, Double.doubleToLongBits(defaultValue)));
        }
    }

    public Set<String> getSet(String valueKey, Set<String> defaultValues) {
        init();
        if(isEncrypted){
            return securePrefs.getStringSet(valueKey, defaultValues);
        } else {
            return prefs1.getStringSet(valueKey, defaultValues);
        }
    }


    public void clearPref(String key){
        init();
        if(isEncrypted){
            secureEdit.remove(key);
            secureEdit.commit();
        } else {
            edit1.remove(key);
            edit1.commit();
        }
    }

    /**
     * Clears ALL preferences stored.
     */
    public void clearAllPrefs(){
        init();
        if(isEncrypted){
            secureEdit.clear();
            secureEdit.commit();
        } else {
            edit1.clear();
            edit1.commit();
        }

    }

    /**
     * Gets everything in the shared preferences
     * @return A map of String to objects
     */
    public Map<String, ?> getAllPrefs(){
        init();
        if(isEncrypted){
            Map<String, ?> myMap = securePrefs.getAll();
            return myMap;
        } else {
            Map<String, ?> myMap = prefs1.getAll();
            return myMap;
        }
    }

    public SharedPreferences getPrefs(){
        prefs1 = context.getSharedPreferences(sharedPrefsName, Context.MODE_PRIVATE);
        return prefs1;
    }

    public SecurePreferences getPrefsEncrypted(){
        securePrefs = new SecurePreferences(context, encryptionPassword, encryptedSharedPrefsName);
        return securePrefs;
    }

    private void init(){
        if(isEncrypted){
            if (securePrefs == null) {
                getPrefsEncrypted();
            }
            if (secureEdit == null) {
                secureEdit = securePrefs.edit();
            }
        } else {
            if (prefs1 == null) {
                getPrefs();
            }
            if (edit1 == null) {
                edit1 = prefs1.edit();
            }
        }
    }
}
