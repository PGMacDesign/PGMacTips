package com.pgmacdesign.pgmacutilities.instagram;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by pmacdowell on 2017-04-19.
 */

public interface InstagramService {

    public static final String VERSION = "v1";
    public static final String OAUTH_PATH = "/oauth";
    public static final String AUTH_PATH = "/authorize";
    //public static final String OAUTH_PATH = "/oauth";
    //public static final String OAUTH_PATH = "/oauth";
    //public static final String OAUTH_PATH = "/oauth";

    //For a full listing of the endpoints, check out documentation here
    // https://www.instagram.com/developer/endpoints/media/

    /*
    SAMPLES:
    This is the web view URL to show the user: VVV
    ---> https://api.instagram.com/oauth/authorize/?client_id=CLIENT-ID&redirect_uri=REDIRECT-URI&response_type=token
    http://your-redirect-uri#access_token=ACCESS-TOKEN
     */
    /*
    @GET(OAUTH_PATH + AUTH_PATH + "/")
    Call<InstagramDataObject> getRecentMedia(
            @Query("client_id") String client_id,
            @Query("redirect_uri")String redirect_uri,
            @Query("response_type") String response_type
    );
    */

    @GET(VERSION + "/users/self/media/recent")
    Call<InstagramDataObject.MediaDataObject> getRecentMedia(
            @Query("access_token") String access_token,
            @Query("max_id")String max_id
    );

    @GET(VERSION + "/users/{userId}")
    Call<InstagramDataObject.UserDataObject> getUserData(
            @Path("userId") String userId,
            @Query("access_token") String access_token
    );


}

