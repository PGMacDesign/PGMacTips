package com.pgmacdesign.pgmactips.networkclasses.volleyutilities;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.pgmacdesign.pgmactips.adaptersandlisteners.OnTaskCompleteListener;
import com.pgmacdesign.pgmactips.utilities.L;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * Volley Simple request. Code pulled from:
 * https://developer.android.com/training/volley/simple.html
 * Created by PatrickSSD2 on 8/29/2016.
 *
 */
public class VolleyUtilities {

    private VolleyUtilities(){}

    /**
     * Simple get example
     * @param listener listener to pass data back on
     * @param context context
     * @param url Url to send to
     */
    public static void makeGetRequest(final OnTaskCompleteListener listener,
                                      Context context, String url){
        RequestQueue requestQueue = Volley.newRequestQueue(context);

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Parse String response here
                        listener.onTaskComplete(response, 0);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                listener.onTaskComplete("error", 1);
            }
        });
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
    public static void makePostRequest(final OnTaskCompleteListener listener,
                                       Context context, String url,
                                       Object body, Class objectClass){
        String jsonString = new Gson().toJson(body, objectClass);
        JSONObject object = null;
        try {
            object = new JSONObject(jsonString);
        } catch (JSONException e){
            e.printStackTrace();
            return;
        }
        makePostRequest(listener, context, url, object);
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
                                       Map<String, ?> params){
        JSONObject object = new JSONObject(params);
        makePostRequest(listener, context, url, object);
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
                                       JSONObject jsonObject){
        RequestQueue requestQueue = Volley.newRequestQueue(context);

        JsonObjectRequest objectRequest = new JsonObjectRequest(
                Request.Method.POST, url, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if(response != null){
                            String jsonString = response.toString();
                            L.m("response = " + jsonString);
                            //Do whatever here with response
                            listener.onTaskComplete(jsonString, 0);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        listener.onTaskComplete("error", 1);
                        error.printStackTrace();
                    }
                }
        );

        // Add the request to the RequestQueue.
        requestQueue.add(objectRequest);
    }

}
