package com.pgmacdesign.pgmacutilities.utilities;

import com.google.gson.Gson;

/**
 * Created by pmacdowell on 8/18/2016.
 */
public class GsonUtilities {

    private static Gson gson;

    private static void init(){
        if(gson == null){
            gson = new Gson();
        }
    }
    public static String convertObjectToJson(Object object, Class objectSourceClass){
        GsonUtilities.init();
        String str = null;
        try {
            str = gson.toJson(object, objectSourceClass);
        } catch (Exception e){
            //For testing
        }
        return(str);
    }
    public static Object convertJsonToObject(String jsonString, Class objectSourceClass){
        GsonUtilities.init();
        Object obj = null;
        try {
            obj = gson.fromJson(jsonString, objectSourceClass);
        } catch (Exception e){
            //For testing
        }
        return(obj);
    }
}
