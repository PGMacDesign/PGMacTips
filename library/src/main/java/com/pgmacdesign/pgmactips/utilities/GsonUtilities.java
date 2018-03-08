package com.pgmacdesign.pgmactips.utilities;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

import java.lang.reflect.Type;

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

    public static String convertObjectToJson(Object object, Type objectSourceClass){
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
        try {
            Object obj = gson.fromJson(jsonString, objectSourceClass);
            return obj;
        } catch (Exception e){
            //For testing
            e.printStackTrace();
            return null;
        }
    }

    public static Object convertJsonToObject(String jsonString, Type objectSourceClass){
        GsonUtilities.init();
        try {
            Object obj = gson.fromJson(jsonString, objectSourceClass);
            return obj;
        } catch (Exception e){
            //For testing
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Convert an object to a JSON Element {@link JsonElement} If cannot convert, will return null
     * @param o Object to be converted
     * @return JsonElement to be returned
     */
    public static JsonElement convertToJsonElement(Object o){
        GsonUtilities.init();
        JsonElement jsonElement = null;
        try {
            jsonElement = gson.toJsonTree(o);
        } catch (Exception e){
            e.printStackTrace();
        }
        return jsonElement;
    }
}
