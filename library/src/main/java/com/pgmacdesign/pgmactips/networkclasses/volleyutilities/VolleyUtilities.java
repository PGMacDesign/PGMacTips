package com.pgmacdesign.pgmactips.networkclasses.volleyutilities;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.pgmacdesign.pgmactips.adaptersandlisteners.OnTaskCompleteListener;
import com.pgmacdesign.pgmactips.misc.CustomAnnotationsBase;
import com.pgmacdesign.pgmactips.networkclasses.sslsocketsandprotocols.ClientSSLSocketFactory;
import com.pgmacdesign.pgmactips.networkclasses.sslsocketsandprotocols.SSLProtocolOptions;
import com.pgmacdesign.pgmactips.utilities.MiscUtilities;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.SSLSocketFactory;

/**
 * Volley Utilities to shorthand calls. Code pulled from:
 * https://developer.android.com/training/volley/simple.html
 * Created by PatrickSSD2 on 8/29/2016.
 */
@CustomAnnotationsBase.RequiresDependency(requiresDependencies = {CustomAnnotationsBase.Dependencies.Volley,
        CustomAnnotationsBase.Dependencies.GSON})
public class VolleyUtilities {

    //Tags to be returned on a successful or failed Volley call
    public static final int VOLLEY_REQUEST_SUCCESS_STRING = 444;
    public static final int VOLLEY_REQUEST_VOLLEY_ERROR = 445;
    public static final int VOLLEY_REQUEST_NULL_RETURN = 446;


    // TODO: 10/2/2018 refactor in dynamic handshake timeout window
    private static SSLSocketFactory getSocketFactory(@NonNull Context context,
                                                     SSLProtocolOptions sslProtocolOption) {
        try {
            return ClientSSLSocketFactory.getSocketFactory(context, 10000,
                    (sslProtocolOption != null) ? sslProtocolOption : SSLProtocolOptions.TLS);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Overloaded to allow for omission of {@link SSLProtocolOptions}
     */
    @CustomAnnotationsBase.RequiresDependency(requiresDependencies = {CustomAnnotationsBase.Dependencies.Volley,
            CustomAnnotationsBase.Dependencies.GSON})
    public static void makeGetRequest(@NonNull final OnTaskCompleteListener listener,
                                      @NonNull Context context, @NonNull String url,
                                      @Nullable final Map<String, String> headers) {
        VolleyUtilities.makeGetRequest(listener, context, url, headers, SSLProtocolOptions.TLS);
    }

    /**
     * Simple get example
     *
     * @param listener          listener to pass data back on
     * @param context           context
     * @param url               Url to send to
     * @param headers           Map of headers to send
     * @param sslProtocolOption {@link SSLProtocolOptions} to use. Defaults to {@link SSLProtocolOptions#TLS}
     */
    @CustomAnnotationsBase.RequiresDependency(requiresDependencies = {CustomAnnotationsBase.Dependencies.Volley,
            CustomAnnotationsBase.Dependencies.GSON})
    public static void makeGetRequest(@NonNull final OnTaskCompleteListener listener,
                                      @NonNull Context context, @NonNull String url,
                                      @Nullable final Map<String, String> headers,
                                      @NonNull SSLProtocolOptions sslProtocolOption) {
        SSLSocketFactory socketFactory = VolleyUtilities.getSocketFactory(context, sslProtocolOption);
        RequestQueue requestQueue = (socketFactory == null) ? Volley.newRequestQueue(context)
                : Volley.newRequestQueue(context, new HurlStack(null, socketFactory));

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
     * Overloaded to allow for omission of {@link SSLProtocolOptions}
     */
    @CustomAnnotationsBase.RequiresDependency(requiresDependencies = {CustomAnnotationsBase.Dependencies.Volley,
            CustomAnnotationsBase.Dependencies.GSON})
    public static void makePostRequest(@NonNull final OnTaskCompleteListener listener,
                                       @NonNull Context context, @NonNull String url,
                                       Object body, Class objectClass,
                                       @Nullable final Map<String, String> headers) {
        JSONObject object = null;
        try {
            String jsonString = new Gson().toJson(body, objectClass);
            object = new JSONObject(jsonString);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        makePostRequestLocal(listener, context, url, object, headers, SSLProtocolOptions.TLS);
    }

    /**
     * Simple post example with an object
     *
     * @param listener          listener to pass data back on
     * @param context           context
     * @param url               String URL
     * @param body              body object to be converted into json
     * @param objectClass       class type of the body object passed
     * @param headers           Map of headers to send
     * @param sslProtocolOption {@link SSLProtocolOptions} SSL Protocol option.
     *                          If unsure, call overloaded method
     */
    @CustomAnnotationsBase.RequiresDependency(requiresDependencies = {CustomAnnotationsBase.Dependencies.Volley,
            CustomAnnotationsBase.Dependencies.GSON})
    public static void makePostRequest(@NonNull final OnTaskCompleteListener listener,
                                       @NonNull Context context, @NonNull String url,
                                       Object body, Class objectClass,
                                       @Nullable final Map<String, String> headers,
                                       @NonNull SSLProtocolOptions sslProtocolOption) {

        JSONObject object = null;
        try {
            String jsonString = new Gson().toJson(body, objectClass);
            object = new JSONObject(jsonString);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        makePostRequestLocal(listener, context, url, object, headers, sslProtocolOption);
    }

    /**
     * Overloaded to allow for omission of {@link SSLProtocolOptions}
     */
    @CustomAnnotationsBase.RequiresDependency(requiresDependencies = {CustomAnnotationsBase.Dependencies.Volley,
            CustomAnnotationsBase.Dependencies.GSON})
    public static void makePostRequest(final OnTaskCompleteListener listener,
                                       Context context, String url,
                                       Map<String, ?> params,
                                       @Nullable final Map<String, String> headers) {
        JSONObject object = new JSONObject(params);
        makePostRequestLocal(listener, context, url, object, headers, SSLProtocolOptions.TLS);
    }

    /**
     * Overloaded to include map<String, Object> input
     *
     * @param listener
     * @param context
     * @param url
     * @param params            Map<String, Object> to convert to jsonObject
     * @param headers           Map of headers to send
     * @param sslProtocolOption {@link SSLProtocolOptions} SSL Protocol option.
     *                          If unsure, call overloaded method
     */
    @CustomAnnotationsBase.RequiresDependency(requiresDependencies = {CustomAnnotationsBase.Dependencies.Volley,
            CustomAnnotationsBase.Dependencies.GSON})
    public static void makePostRequest(final OnTaskCompleteListener listener,
                                       Context context, String url,
                                       Map<String, ?> params,
                                       @Nullable final Map<String, String> headers,
                                       @NonNull SSLProtocolOptions sslProtocolOption) {
        JSONObject object = new JSONObject(params);
        makePostRequestLocal(listener, context, url, object, headers, sslProtocolOption);
    }

    /**
     * Overloaded to allow for empty {@link SSLProtocolOptions}
     */
    @CustomAnnotationsBase.RequiresDependency(requiresDependencies = {CustomAnnotationsBase.Dependencies.Volley,
            CustomAnnotationsBase.Dependencies.GSON})
    public static void makePostRequest(final OnTaskCompleteListener listener,
                                       Context context, String url,
                                       JSONObject jsonObject,
                                       @Nullable final Map<String, String> headers) {
        makePostRequestLocal(listener, context, url, jsonObject, headers, SSLProtocolOptions.TLS);
    }

    /**
     * Overloaded to include JSONObject input
     *
     * @param listener
     * @param context
     * @param url
     * @param jsonObject
     * @param headers           Map of headers to send
     * @param sslProtocolOption {@link SSLProtocolOptions} SSL Protocol option.
     *                          If unsure, call overloaded method
     */
    @CustomAnnotationsBase.RequiresDependency(requiresDependencies = {CustomAnnotationsBase.Dependencies.Volley,
            CustomAnnotationsBase.Dependencies.GSON})
    public static void makePostRequest(final OnTaskCompleteListener listener,
                                       Context context, String url,
                                       JSONObject jsonObject,
                                       @Nullable final Map<String, String> headers,
                                       @NonNull SSLProtocolOptions sslProtocolOption) {
        makePostRequestLocal(listener, context, url, jsonObject, headers, sslProtocolOption);
    }

    private static void makePostRequestLocal(final OnTaskCompleteListener listener,
                                             Context context, String url,
                                             JSONObject jsonObject,
                                             @Nullable final Map<String, String> headers,
                                             @NonNull SSLProtocolOptions sslProtocolOption) {
        SSLSocketFactory socketFactory = VolleyUtilities.getSocketFactory(context, sslProtocolOption);
        RequestQueue requestQueue = (socketFactory == null) ? Volley.newRequestQueue(context)
                : Volley.newRequestQueue(context, new HurlStack(null, socketFactory));
        JsonObjectRequest objectRequest = new JsonObjectRequest(
                Request.Method.POST, url, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (response != null) {
                            try {
                                String jsonString = response.toString();
                                //Do whatever here with response
                                listener.onTaskComplete(jsonString, VOLLEY_REQUEST_SUCCESS_STRING);
                                return;
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
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
        requestQueue.add(objectRequest);
    }

}
