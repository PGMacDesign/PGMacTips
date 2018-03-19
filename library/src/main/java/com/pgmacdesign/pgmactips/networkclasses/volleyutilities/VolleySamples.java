package com.pgmacdesign.pgmactips.networkclasses.volleyutilities;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by PatrickSSD2 on 8/29/2016.
 */
class VolleySamples {

    private static void doStuff2(Context context, StringRequest stringRequest){
        // Get a RequestQueue
        RequestQueue queue = VolleySingleton.getInstance(context).
                getRequestQueue();

        // Add a request (in this example, called stringRequest) to your RequestQueue.
        VolleySingleton.getInstance(context).addToRequestQueue(stringRequest);
    }

    private static void doStuff1(Context context, String url){
        RequestQueue requestQueue = Volley.newRequestQueue(context);

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Parse String response here
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        // Add the request to the RequestQueue.
        requestQueue.add(stringRequest);
    }

    private static void setupCache(Context context){
        // Instantiate the cache
        Cache cache = new DiskBasedCache(context.getCacheDir(), 1024 * 1024); // 1MB cap

        // Set up the network to use HttpURLConnection as the HTTP client.
        Network network = new BasicNetwork(new HurlStack());

        // Instantiate the RequestQueue with the cache and network.
        RequestQueue mRequestQueue;
        mRequestQueue = new RequestQueue(cache, network);

        // Start the queue
        mRequestQueue.start();

        String url ="http://www.example.com";

        // Formulate the request and handle the response.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Do something with the response
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error
                    }
                });

        // Add the request to the RequestQueue.
        mRequestQueue.add(stringRequest);
    }

    private static void makeImageRequest(Context context){
        ImageView mImageView;
        String url = "http://i.imgur.com/7spzG.png";

        Response.Listener<Bitmap> listener = new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                //Do stuff here with bitmap
            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                //Do stuff with error
            }
        };
        // Retrieves an image specified by the URL, displays it in the UI.
        ImageRequest request = new ImageRequest(url, listener, 0, 0,
                ImageView.ScaleType.FIT_XY, null, errorListener);

        VolleySingleton.getInstance(context).addToRequestQueue(request);
    }

    private static void setupCache(RequestQueue requestQueue, Context context){
        ImageLoader mImageLoader = new ImageLoader(requestQueue, new VolleyBitmapCache(
                VolleyBitmapCache.getCacheSize(context)));
    }

    private void startJsonObjectRequest(Context context){
        String url = "http://my-json-feed";

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        String str = response.toString();
                        //Do stuff with string here.
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Do stuff with response here
                    }
                });

        // Access the RequestQueue through your singleton class.
        VolleySingleton.getInstance(context).addToRequestQueue(jsObjRequest);
    }

    private void startJsonArrayRequest(Context context){
        String url = "http://my-json-feed";

        JsonArrayRequest jsObjRequest = new JsonArrayRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        String str = response.toString();
                        //Do stuff with string here.
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Do stuff with response here
                    }
                });

        // Access the RequestQueue through your singleton class.
        VolleySingleton.getInstance(context).addToRequestQueue(jsObjRequest);
    }

    private void customRequest(Response.ErrorListener errorListener, RequestQueue mRequestQueue,
                          final Class classToConvertAgainst){

        String url = "http://my-json-feed";

        com.android.volley.Request myRequest = new Request(Request.Method.GET,
                url, errorListener) {
            @Override
            protected Response parseNetworkResponse(NetworkResponse response) {
                try {
                    Gson gson = new Gson();
                    String json = new String(response.data,
                            HttpHeaderParser.parseCharset(response.headers));
                    return Response.success(gson.fromJson(json, classToConvertAgainst),
                            HttpHeaderParser.parseCacheHeaders(response));
                } catch (Exception e){
                    return null;
                }
            }

            @Override
            protected void deliverResponse(Object response) {
                //
            }

            @Override
            public int compareTo(Object o) {
                return 0;
            }
        };
        mRequestQueue.add(myRequest);
    }

}
