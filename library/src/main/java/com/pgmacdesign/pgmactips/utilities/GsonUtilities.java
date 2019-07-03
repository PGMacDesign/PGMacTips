package com.pgmacdesign.pgmactips.utilities;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.pgmacdesign.pgmactips.misc.CustomAnnotationsBase;

import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by pmacdowell on 8/18/2016.
 */
@CustomAnnotationsBase.RequiresDependency(requiresDependency = CustomAnnotationsBase.Dependencies.GSON)
public class GsonUtilities {

    private static Gson gson;

    static {
        if(gson == null){
            gson = new Gson();
        }
    }

    /**
     * Convert an object to a json string using the {@link Class}
     * @param object
     * @param objectSourceClass
     * @return
     */
    @CustomAnnotationsBase.RequiresDependency(requiresDependency = CustomAnnotationsBase.Dependencies.GSON)
    public static String convertObjectToJson(Object object, Class objectSourceClass){
        String str = null;
        try {
            str = gson.toJson(object, objectSourceClass);
        } catch (IllegalArgumentException ile){
            //Triggers if you declare 2 fields with the same @Serialized string name
            ile.printStackTrace();
        } catch (Exception e){}
        return(str);
    }

    /**
     * Convert an object to a json string using the {@link Type}
     * @param object
     * @param objectSourceClass
     * @return
     */
    @CustomAnnotationsBase.RequiresDependency(requiresDependency = CustomAnnotationsBase.Dependencies.GSON)
    public static String convertObjectToJson(Object object, Type objectSourceClass){
        String str = null;
        try {
            str = gson.toJson(object, objectSourceClass);
        } catch (IllegalArgumentException ile){
            //Triggers if you declare 2 fields with the same @Serialized string name
            ile.printStackTrace();
        } catch (Exception e){}
        return(str);
    }

    /**
     * Convert a json string to an object using the {@link Class}
     * @param jsonString
     * @param objectSourceClass
     * @return
     */
    @CustomAnnotationsBase.RequiresDependency(requiresDependency = CustomAnnotationsBase.Dependencies.GSON)
    public static Object convertJsonToObject(String jsonString, Class objectSourceClass){
        try {
            Object obj = gson.fromJson(jsonString, objectSourceClass);
            return obj;
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Convert a json string to an object using the {@link Type}
     * @param jsonString
     * @param objectSourceClass
     * @return
     */
    @CustomAnnotationsBase.RequiresDependency(requiresDependency = CustomAnnotationsBase.Dependencies.GSON)
    public static Object convertJsonToObject(String jsonString, Type objectSourceClass){
        try {
            Object obj = gson.fromJson(jsonString, objectSourceClass);
            return obj;
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Convert an object to a JSON Element {@link JsonElement} If cannot convert, will return null
     * @param o Object to be converted
     * @return JsonElement to be returned
     */
    @CustomAnnotationsBase.RequiresDependency(requiresDependency = CustomAnnotationsBase.Dependencies.GSON)
    public static JsonElement convertToJsonElement(Object o){
        JsonElement jsonElement = null;
        try {
            jsonElement = gson.toJsonTree(o);
        } catch (Exception e){
            e.printStackTrace();
        }
        return jsonElement;
    }

    /**
     * Convert an object to a String (using gson)
     * @param o
     * @param type {@link Type}. To see type examples, look at sample:
     * {@link com.pgmacdesign.pgmactips.networkclasses.retrofitutilities.CustomConverterFactory#TYPE_TESTINGPOJO}
     * @return
     */
    @CustomAnnotationsBase.RequiresDependency(requiresDependency = CustomAnnotationsBase.Dependencies.GSON)
    public static String convertObjectToString(@NonNull final Object o, @NonNull final Type type){
//        JsonElement jsonElement = new Gson().toJsonTree(o);
        JsonElement jsonElement = new Gson().toJsonTree(o, type);
        if(jsonElement == null){
            return null;
        }
        StringBuilder sb = new StringBuilder();
        if(jsonElement.isJsonArray()){
            JsonArray jsonArray = jsonElement.getAsJsonArray();
            Iterator<JsonElement> j = jsonArray.iterator();
            while (j.hasNext()){
                JsonElement element = j.next();
                String s = convertJsonElementToString(element);
                if(!StringUtilities.isNullOrEmpty(s)){
                    sb.append(s + "\n");
                }
            }
        } else if (jsonElement.isJsonObject()){
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            sb.append(convertJsonObjectToString(jsonObject));
        } else if (jsonElement.isJsonNull()){
            return null;
        } else if (jsonElement.isJsonPrimitive()){
            sb.append("JsonPrimitive: " + jsonElement.getAsJsonPrimitive().toString());
        }
        return sb.toString();
    }

    /**
     * Convert a {@link JsonElement} to a String using gson
     * @param jsonElement
     * @return
     */
    @CustomAnnotationsBase.RequiresDependency(requiresDependency = CustomAnnotationsBase.Dependencies.GSON)
    public static String convertJsonElementToString(JsonElement jsonElement){
        if(jsonElement == null){
            return null;
        }
        return jsonElement.getAsString();
    }

    /**
     * Convert a {@link JsonObject} to a String using gson
     * @param jsonObject
     * @return
     */
    @CustomAnnotationsBase.RequiresDependency(requiresDependency = CustomAnnotationsBase.Dependencies.GSON)
    public static String convertJsonObjectToString(JsonObject jsonObject){
        if(jsonObject == null){
            return null;
        }
        Set<Map.Entry<String, JsonElement>> entries = jsonObject.entrySet();
        StringBuilder sb = new StringBuilder();
        for(Map.Entry<String, JsonElement> map : entries){
            if(map == null){
                continue;
            }
            String key = map.getKey();
            JsonElement value = map.getValue();
            if(!StringUtilities.isNullOrEmpty(key) && value != null){
                try {
                	/*
                	todo fix issue below:
                	Bug is happening here:
W: java.lang.IllegalStateException
W:     at com.google.gson.JsonArray.getAsString(JsonArray.java:226)
W:     at com.pgmacdesign.pgmactips.utilities.GsonUtilities.convertJsonObjectToString(GsonUtilities.java:185)
W:     at com.pgmacdesign.pgmactips.utilities.GsonUtilities.convertObjectToString(GsonUtilities.java:143)
W:     at my.app.networking.APICalls$3.onResponse(APICalls.java:231)
W:     at retrofit2.ExecutorCallAdapterFactory$ExecutorCallbackCall$1$1.run(ExecutorCallAdapterFactory.java:70)
W:     at android.os.Handler.handleCallback(Handler.java:873)
W:     at android.os.Handler.dispatchMessage(Handler.java:99)
W:     at android.os.Looper.loop(Looper.java:193)
W:     at android.app.ActivityThread.main(ActivityThread.java:6718)
W:     at java.lang.reflect.Method.invoke(Native Method)
W:     at com.android.internal.os.RuntimeInit$MethodAndArgsCaller.run(RuntimeInit.java:493)
W:     at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:858)
                	 */
                    sb.append(key + " : " + value.getAsString() + "\n");
                } catch (UnsupportedOperationException o){
                    sb.append(key + " : " + value.toString() + "\n");
                }
            }
        }
        return sb.toString();
    }

    /**
     * Makes a deep copy of an object. From:
     * https://stackoverflow.com/questions/64036/how-do-you-make-a-deep-copy-of-an-object-in-java
     * @param objectToCopy Object to copy
     * @param typeOfObject Type of the object
     * @return Will returned copied object or null if it did not process correctly
     */
    public static Object copyObject(Object objectToCopy, Type typeOfObject){
        try {
            String json = gson.toJson(objectToCopy, typeOfObject);
            return gson.fromJson(json, typeOfObject);
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Makes a deep copy of an object. From:
     * https://stackoverflow.com/questions/64036/how-do-you-make-a-deep-copy-of-an-object-in-java
     * @param objectToCopy Object to copy
     * @param typeOfObject Class of the object
     * @return Will returned copied object or null if it did not process correctly
     */
    public static Object copyObject(Object objectToCopy, Class typeOfObject){
        try {
            String json = gson.toJson(objectToCopy, typeOfObject);
            return gson.fromJson(json, typeOfObject);
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
}
