package com.pgmacdesign.pgmactips.networkclasses.volleyutilities;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.pgmacdesign.pgmactips.adaptersandlisteners.OnTaskCompleteListener;
import com.pgmacdesign.pgmactips.utilities.MiscUtilities;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Volley Simple request. Code pulled from:
 * https://developer.android.com/training/volley/simple.html
 * Created by PatrickSSD2 on 8/29/2016.
 *
 */
public class VolleyUtilities {

    //Tags to be returned on a successful or failed Volley call
    public static final int VOLLEY_REQUEST_SUCCESS_STRING = 444;
    public static final int VOLLEY_REQUEST_VOLLEY_ERROR = 445;
    public static final int VOLLEY_REQUEST_NULL_RETURN = 446;

    private VolleyUtilities(){}

    /**
     * Simple get example
     * @param listener listener to pass data back on
     * @param context context
     * @param url Url to send to
     */
    public static void makeGetRequest(@NonNull final OnTaskCompleteListener listener,
                                      @NonNull Context context, @NonNull String url,
                                      @Nullable final Map<String, String> headers){
        RequestQueue requestQueue = Volley.newRequestQueue(context);

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Parse String response here
                        listener.onTaskComplete(response, VOLLEY_REQUEST_SUCCESS_STRING);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        listener.onTaskComplete(error, VOLLEY_REQUEST_VOLLEY_ERROR);
                    }
        }) {
            /**
             * Passing some request headers
             */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                if (MiscUtilities.isMapNullOrEmpty(headers)) {
                    Map<String, String> headers1 = new HashMap<String, String>();
                    headers1.put("Content-Type", "application/json");
                    return headers1;
                } else {
                    return headers;
                }
            }
        };

        // Add the request to the RequestQueue.
        requestQueue.add(stringRequest);
    }

    /**
     * Simple post example with an object
     * @param listener listener to pass data back on
     * @param context context
     * @param url String URL
     * @param body body object to be converted into json
     * @param objectClass class type of the body object passed
     */
    public static void makePostRequest(@NonNull final OnTaskCompleteListener listener,
                                       @NonNull Context context, @NonNull String url,
                                       Object body, Class objectClass,
                                       @Nullable final Map<String, String> headers){

        JSONObject object = null;
        try {
            String jsonString = new Gson().toJson(body, objectClass);
            object = new JSONObject(jsonString);
        } catch (JSONException e){
            e.printStackTrace();
        }
        makePostRequestLocal(true, listener, context, url, object, headers);
    }

    /**
     * Overloaded to include map<String, Object> input
     * @param listener
     * @param context
     * @param url
     * @param params Map<String, Object> to convert to jsonObject
     */
    public static void makePostRequest(final OnTaskCompleteListener listener,
                                       Context context, String url,
                                       Map<String, ?> params,
                                       @Nullable final Map<String, String> headers){
        JSONObject object = new JSONObject(params);
        makePostRequestLocal(true, listener, context, url, object, headers);
    }

    /**
     * Overloaded to include JSONObject input
     * @param listener
     * @param context
     * @param url
     * @param jsonObject
     */
    public static void makePostRequest(final OnTaskCompleteListener listener,
                                       Context context, String url,
                                       JSONObject jsonObject,
                                       @Nullable final Map<String, String> headers){
        makePostRequestLocal(true, listener, context, url, jsonObject, headers);
    }

    private static void makePostRequestLocal(boolean makePost,
                                             final OnTaskCompleteListener listener,
                                       Context context, String url,
                                       JSONObject jsonObject,
                                       @Nullable final Map<String, String> headers){
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        JsonObjectRequest objectRequest = new JsonObjectRequest(
                Request.Method.POST, url, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if(response != null){
                            try {
                                String jsonString = response.toString();
                                //Do whatever here with response
                                listener.onTaskComplete(jsonString, VOLLEY_REQUEST_SUCCESS_STRING);
                                return;
                            } catch (Exception e){e.printStackTrace();}
                        }
                        listener.onTaskComplete(null, VOLLEY_REQUEST_NULL_RETURN);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        listener.onTaskComplete(error, VOLLEY_REQUEST_VOLLEY_ERROR);
                    }
                }){
            /**
             * Passing some request headers
             */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                if(MiscUtilities.isMapNullOrEmpty(headers)){
                    Map<String, String> headers1 = new HashMap<String, String>();
                    headers1.put("Content-Type", "application/json");
                    return headers1;
                } else {
                    return headers;
                }
            }
        };

        // Add the request to the RequestQueue.
        requestQueue.add(objectRequest);
    }

}