package com.pgmacdesign.pgmacutilities.mapzen;

import android.content.Context;
import android.support.annotation.NonNull;

import com.pgmacdesign.pgmacutilities.R;
import com.pgmacdesign.pgmacutilities.adaptersandlisteners.OnTaskCompleteListener;
import com.pgmacdesign.pgmacutilities.networkclasses.retrofitutilities.RetrofitClient;
import com.pgmacdesign.pgmacutilities.utilities.NetworkUtilities;
import com.pgmacdesign.pgmacutilities.utilities.StringUtilities;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * This is a Rest API to interface with the Mapzen back-end. Calls can be made here to return
 * lists MapzenPOJO objects {@link MapzenPOJO}, which contain location data.
 * This utilizes {@link MapzenInterface} and {@link RetrofitClient} for calls
 * Mapzen Documentation - https://mapzen.com/documentation/search/reverse/
 * Created by pmacdowell on 2017-02-17.
 */
public class MapzenAPICalls {

    private Context context;
    private RetrofitClient retrofitClient;
    private MapzenInterface mapzenInterface;
    private String ApiKey;
    private OnTaskCompleteListener listener;

    //Final Var Strings and Int Tags
    private static final String URL_BASE = "https://search.mapzen.com";
    public static final int TAG_MAPZEN_SUCCESS = 790;
    public static final int TAG_MAPZEN_FAILURE = 791;
    public static final int TAG_MAPZEN_INVALID_QUERY = 792;
    public static final int TAG_MAPZEN_CONNECTIVITY_ISSUE = 793;
    public static final int TAG_MAPZEN_TBD_1 = 794;
    public static final int TAG_MAPZEN_TBD_2 = 795;


    public MapzenAPICalls(@NonNull Context context, @NonNull OnTaskCompleteListener listener) {
        this.context = context;
        this.initClient();
        this.ApiKey = context.getResources().getString(R.string.mapzen_string_api_key);
        this.listener = listener;
    }

    /**
     * Client and interface initializer
     */
    private void initClient() {
        if (retrofitClient == null) {
            RetrofitClient.Builder builder = new RetrofitClient.Builder(
                    MapzenInterface.class, URL_BASE);
            builder.callIsJSONFormat();
            retrofitClient = builder.build();
        }
        if (mapzenInterface == null) {
            mapzenInterface = retrofitClient.buildServiceClient();
        }
    }

    /**
     * Query the map
     *
     * @param query              Query to search
     * @param localizedLatitude  Can be null. If not null, will localize to said lat
     * @param localizedLongitude Can be null. If not null, will localize to said lng
     */
    public void searchMap(String query, Double localizedLatitude, Double localizedLongitude) {
        if (!checkForBadQuery(query) || !checkForInternetConnectivity()) {
            return;
        }

        //Initalize the call
        Call<MapzenPOJO> call = mapzenInterface.searchMap(
                ApiKey, localizedLatitude, localizedLongitude, query
        );

        //Enqueue the call asynchronously
        call.enqueue(new Callback<MapzenPOJO>() {
                         @Override
                         public void onResponse(Call<MapzenPOJO> call, Response<MapzenPOJO> response) {
                             //Check for response or not
                             if (!response.isSuccessful()) {
                                 listener.onTaskComplete(null, TAG_MAPZEN_FAILURE);
                             } else {
                                 //Response was successful. Send back via listener
                                 try {
                                     MapzenPOJO body = (MapzenPOJO) response.body();
                                     listener.onTaskComplete(body, TAG_MAPZEN_SUCCESS);
                                 } catch (Exception e) {
                                     listener.onTaskComplete(e.getMessage(), TAG_MAPZEN_FAILURE);
                                 }
                             }
                         }

                         @Override
                         public void onFailure(Call<MapzenPOJO> call, Throwable t) {
                             t.printStackTrace();
                             listener.onTaskComplete(t.getMessage(), TAG_MAPZEN_FAILURE);
                         }
                     }
        );

    }

    /**
     * Query the map with filters
     *
     * @param query              Query to search
     * @param localizedLatitude  Can be null. If not null, will localize to said lat
     * @param localizedLongitude Can be null. If not null, will localize to said lng
     * @param searchFilter       Search Filter. See https://mapzen.com/documentation/search/autocomplete/
     *                           for more details on filters
     */
    public void searchMapWithFilter(String query, Double localizedLatitude,
                                    Double localizedLongitude, String searchFilter) {
        if (!checkForBadQuery(query) || !checkForInternetConnectivity()) {
            return;
        }

        //Initalize the call
        Call<MapzenPOJO> call = mapzenInterface.searchMapWithFilter(
                ApiKey, localizedLatitude, localizedLongitude, query, searchFilter
        );

        //Enqueue the call asynchronously
        call.enqueue(new Callback<MapzenPOJO>() {
                         @Override
                         public void onResponse(Call<MapzenPOJO> call, Response<MapzenPOJO> response) {
                             //Check for response or not
                             if (!response.isSuccessful()) {
                                 listener.onTaskComplete(null, TAG_MAPZEN_FAILURE);
                             } else {
                                 //Response was successful. Send back via listener
                                 try {
                                     MapzenPOJO body = (MapzenPOJO) response.body();
                                     listener.onTaskComplete(body, TAG_MAPZEN_SUCCESS);
                                 } catch (Exception e) {
                                     listener.onTaskComplete(e.getMessage(), TAG_MAPZEN_FAILURE);
                                 }
                             }
                         }

                         @Override
                         public void onFailure(Call<MapzenPOJO> call, Throwable t) {
                             t.printStackTrace();
                             listener.onTaskComplete(t.getMessage(), TAG_MAPZEN_FAILURE);
                         }
                     }
        );

    }


    /**
     * Query the map with a layer
     *
     * @param query              Query to search
     * @param localizedLatitude  Can be null. If not null, will localize to said lat
     * @param localizedLongitude Can be null. If not null, will localize to said lng
     * @param searchLayer        Search Filter. See https://mapzen.com/documentation/search/autocomplete/
     *                           for more details on filters
     */
    public void searchMapWithLayer(String query, Double localizedLatitude,
                                   Double localizedLongitude, String searchLayer) {
        if (!checkForBadQuery(query) || !checkForInternetConnectivity()) {
            return;
        }

        //Initalize the call
        Call<MapzenPOJO> call = mapzenInterface.searchMapWithLayer(
                ApiKey, localizedLatitude, localizedLongitude, query, searchLayer
        );

        //Enqueue the call asynchronously
        call.enqueue(new Callback<MapzenPOJO>() {
                         @Override
                         public void onResponse(Call<MapzenPOJO> call, Response<MapzenPOJO> response) {
                             //Check for response or not
                             if (!response.isSuccessful()) {
                                 listener.onTaskComplete(null, TAG_MAPZEN_FAILURE);
                             } else {
                                 //Response was successful. Send back via listener
                                 try {
                                     MapzenPOJO body = (MapzenPOJO) response.body();
                                     listener.onTaskComplete(body, TAG_MAPZEN_SUCCESS);
                                 } catch (Exception e) {
                                     listener.onTaskComplete(e.getMessage(), TAG_MAPZEN_FAILURE);
                                 }
                             }
                         }

                         @Override
                         public void onFailure(Call<MapzenPOJO> call, Throwable t) {
                             t.printStackTrace();
                             listener.onTaskComplete(t.getMessage(), TAG_MAPZEN_FAILURE);
                         }
                     }
        );

    }

    /**
     * Query the map with filters
     *
     * @param query              Query to search
     * @param localizedLatitude  Can be null. If not null, will localize to said lat
     * @param localizedLongitude Can be null. If not null, will localize to said lng
     * @param countryISOCode     Country ISO Code. See
     *                           {@link MapzenInterface#searchMapViaCountry(String, double, double, String, String)} for details
     */
    public void searchMapViaCountry(String query, Double localizedLatitude,
                                    Double localizedLongitude, String countryISOCode) {
        if (!checkForBadQuery(query) || !checkForInternetConnectivity()) {
            return;
        }

        //Initalize the call
        Call<MapzenPOJO> call = mapzenInterface.searchMapViaCountry(
                ApiKey, localizedLatitude, localizedLongitude, query, countryISOCode
        );

        //Enqueue the call asynchronously
        call.enqueue(new Callback<MapzenPOJO>() {
                         @Override
                         public void onResponse(Call<MapzenPOJO> call, Response<MapzenPOJO> response) {
                             //Check for response or not
                             if (!response.isSuccessful()) {
                                 listener.onTaskComplete(null, TAG_MAPZEN_FAILURE);
                             } else {
                                 //Response was successful. Send back via listener
                                 try {
                                     MapzenPOJO body = (MapzenPOJO) response.body();
                                     listener.onTaskComplete(body, TAG_MAPZEN_SUCCESS);
                                 } catch (Exception e) {
                                     listener.onTaskComplete(e.getMessage(), TAG_MAPZEN_FAILURE);
                                 }
                             }
                         }

                         @Override
                         public void onFailure(Call<MapzenPOJO> call, Throwable t) {
                             t.printStackTrace();
                             listener.onTaskComplete(t.getMessage(), TAG_MAPZEN_FAILURE);
                         }
                     }
        );

    }

    /**
     * Query the map within a certain boundary
     *
     * @param query              Query to search
     * @param localizedLatitude  Can be null. If not null, will localize to said lat
     * @param localizedLongitude Can be null. If not null, will localize to said lng
     * @param minLat             Minimum latitude. If null, it will omit the other boundaries
     * @param maxLat             Maximum latitude. If null, it will omit the other boundaries
     * @param minLng             Minimum longitude. If null, it will omit the other boundaries
     * @param maxLng             Maximum longitude. If null, it will omit the other boundaries
     */
    public void searchMapWithinBounds(String query, Double localizedLatitude,
                                      Double localizedLongitude, Double minLat, Double maxLat,
                                      Double minLng, Double maxLng) {
        if (!checkForBadQuery(query) || !checkForInternetConnectivity()) {
            return;
        }

        //If any are null, do a normal search query
        if (minLat == null || minLng == null || maxLat == null || maxLng == null) {
            this.searchMap(query, localizedLatitude, localizedLongitude);
            return;
        }

        //Initalize the call
        Call<MapzenPOJO> call = mapzenInterface.searchMapWithinBounds(
                ApiKey, localizedLatitude, localizedLongitude, query, minLat, maxLat, minLng, maxLng
        );

        //Enqueue the call asynchronously
        call.enqueue(new Callback<MapzenPOJO>() {
                         @Override
                         public void onResponse(Call<MapzenPOJO> call, Response<MapzenPOJO> response) {
                             //Check for response or not
                             if (!response.isSuccessful()) {
                                 listener.onTaskComplete(null, TAG_MAPZEN_FAILURE);
                             } else {
                                 //Response was successful. Send back via listener
                                 try {
                                     MapzenPOJO body = (MapzenPOJO) response.body();
                                     listener.onTaskComplete(body, TAG_MAPZEN_SUCCESS);
                                 } catch (Exception e) {
                                     listener.onTaskComplete(e.getMessage(), TAG_MAPZEN_FAILURE);
                                 }
                             }
                         }

                         @Override
                         public void onFailure(Call<MapzenPOJO> call, Throwable t) {
                             t.printStackTrace();
                             listener.onTaskComplete(t.getMessage(), TAG_MAPZEN_FAILURE);
                         }
                     }
        );

    }

    /**
     * Simple checker to make sure query string is not empty or null
     *
     * @param query Query to search
     * @return boolean, false if it is bad, true if it is good. If the bad
     * bool is triggered, it will send back the response along the
     * listener so there is no need to implement in each call.
     */
    private boolean checkForBadQuery(String query) {
        if (StringUtilities.isNullOrEmpty(query)) {
            listener.onTaskComplete(null, TAG_MAPZEN_INVALID_QUERY);
            return false;
        } else {
            return true;
        }
    }

    /**
     * Check for internet connection before making call
     * If no network connectivity, sends back response on the listener from here
     *
     * @return boolean. False if no network, true if has network
     */
    private boolean checkForInternetConnectivity() {
        boolean bool = NetworkUtilities.haveNetworkConnection(this.context);
        if (!bool) {
            listener.onTaskComplete(null, TAG_MAPZEN_CONNECTIVITY_ISSUE);
        }
        return bool;
    }
}
