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
import android.support.annotation.NonNull;

import java.util.Map;
import java.util.Set;


/**
 * This class is for SharedPreferences handling.
 * Created by pmacdowell on 2016/08/12.
 */
public class SharedPrefs {

    //Shared Prefs objects
    private SharedPreferences.Editor edit1;
    private SharedPreferences prefs1;

    private String sharedPrefsName;
    private Context context;

    /**
     * Build a shared prefs instance and return it
     * @param context Context
     * @param sharedPrefsName shared prefs name
     * @return SharedPrefs Object
     */
    public static SharedPrefs getSharedPrefsInstance(@NonNull Context context,
                                                     String sharedPrefsName){
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
        SharedPrefs sp = new SharedPrefs(context, str);
        return sp;
    }


    /**
     * Constructor
     * @param context Context
     * @param sharedPrefsName prefs name
     */
    private SharedPrefs(@NonNull Context context, @NonNull String sharedPrefsName){
        this.context = context;
        this.sharedPrefsName = sharedPrefsName;
    }

    /*
    All the methods below are save methods. First param is the key and the second is the
    value. There are multiple overloaded types depending on type passed in.
    */

    public void save(String valueKey, String value) {
        init();
        edit1.putString(valueKey, value);
        edit1.commit();
    }

    public void save(String valueKey, int value) {
        init();
        edit1.putInt(valueKey, value);
        edit1.commit();
    }

    public void save(String valueKey, boolean value) {
        init();
        edit1.putBoolean(valueKey, value);
        edit1.commit();
    }

    public void save(String valueKey, long value) {
        init();
        edit1.putLong(valueKey, value);
        edit1.commit();
    }

    public void save(String valueKey, double value) {
        init();
        edit1.putLong(valueKey, Double.doubleToRawLongBits(value));
        edit1.commit();
    }

    public void save(String valueKey, Set<String> values) {
        init();
        edit1.putStringSet(valueKey, values);
        edit1.commit();
    }

    public void save(String valueKey, String[] values) {
        init();
        for(int i = 0 ; i < values.length; i++){
            edit1.putString(valueKey + "-" + i, values[i]);
        }
        edit1.commit();
    }

    public void save(String valueKey, int[] values) {
        init();
        for(int i = 0 ; i < values.length; i++){
            edit1.putInt(valueKey + "-" + i, values[i]);
        }
        edit1.commit();
    }

    public void save(String valueKey, long[] values) {
        init();
        for(int i = 0 ; i < values.length; i++){
            edit1.putLong(valueKey + "-" + i, values[i]);
        }
        edit1.commit();
    }

    public void save(String valueKey, double[] values) {
        init();
        for(int i = 0 ; i < values.length; i++){
            edit1.putLong(valueKey + "-" + i, Double.doubleToRawLongBits(values[i]));
        }
        edit1.commit();
    }

    public void save(String valueKey, boolean[] values) {
        init();
        for(int i = 0 ; i < values.length; i++){
            edit1.putBoolean(valueKey + "-" + i, values[i]);
        }
        edit1.commit();
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
                edit1.putString(key, object.toString());
            }
        }
        edit1.commit();
    }

    //
    //Get methods
    //

    public String getString(String valueKey, String defaultValue) {
        init();
        return prefs1.getString(valueKey, defaultValue);
    }

    public int getInt(String valueKey, int defaultValue) {
        init();
        return prefs1.getInt(valueKey, defaultValue);
    }

    public boolean getBoolean(String valueKey, boolean defaultValue) {
        init();
        return prefs1.getBoolean(valueKey, defaultValue);
    }

    public long getLong(String valueKey, long defaultValue) {
        init();
        return prefs1.getLong(valueKey, defaultValue);
    }

    public double getDouble(String valueKey, double defaultValue) {
        init();
        return Double.longBitsToDouble(prefs1.getLong(valueKey, Double.doubleToLongBits(defaultValue)));
    }

    public Set<String> getSet(String valueKey, Set<String> defaultValues) {
        init();
        return prefs1.getStringSet(valueKey, defaultValues);
    }

    public void clearPref(String[] keys){
        if(MiscUtilities.isArrayNullOrEmpty(keys)){
            return;
        }
        init();
        for(String key : keys) {
            edit1.remove(key);
            edit1.commit();
        }
    }

    public void clearPref(String key){
        if(StringUtilities.isNullOrEmpty(key)){
            return;
        }
        init();
        edit1.remove(key);
        edit1.commit();
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
        edit1.clear();
        edit1.commit();

    }

    /**
     * Gets everything in the shared preferences
     * @return A map of String to objects
     */
    public Map<String, ?> getAllPrefs(){
        init();
        Map<String, ?> myMap = prefs1.getAll();
        return myMap;
    }

    private SharedPreferences getPrefs(){
        prefs1 = context.getSharedPreferences(sharedPrefsName, Context.MODE_PRIVATE);
        return prefs1;
    }

    private void init(){
        if (prefs1 == null) {
            getPrefs();
        }
        if (edit1 == null) {
            edit1 = prefs1.edit();
        }
    }
}
