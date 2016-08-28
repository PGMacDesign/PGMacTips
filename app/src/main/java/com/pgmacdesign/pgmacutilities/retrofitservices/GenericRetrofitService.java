package com.pgmacdesign.pgmacutilities.retrofitservices;

import com.pgmacdesign.pgmacutilities.TESTINGPOJO;
import com.pgmacdesign.pgmacutilities.TESTINGPOJO2;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * SAMPLE SERVICE EXAMPLE
 * Created by pmacdowell on 8/25/2016.
 */
public interface GenericRetrofitService {

    public static final String VERSION_STRING = "v1";
    public static final String API_SUFFIX_STRING = "/api";

    /**
     * This would be a sample of a post request to a login endpoint that takes in an object
     * of type TESTINGPOJO for the credentials and then returns an object of type TESTINGPOJO2
     * @param credentials object of type TESTINGPOJO
     * @return An object of type TESTINGPOJO2
     */
    @POST(API_SUFFIX_STRING + VERSION_STRING + "/login")
    Call<TESTINGPOJO2> login(@Body TESTINGPOJO credentials);

    /**
     * This would be a sample of a post request to a login endpoint that takes in an object
     * of type TESTINGPOJO for the credentials and then returns an object of type TESTINGPOJO2
     * @param authToken Sample auth token
     * @param userId Sample user id
     * @return Return an object of type TESTINGPOJO2
     */
    @GET(API_SUFFIX_STRING + VERSION_STRING + "/users/{userId}")
    Call<TESTINGPOJO2> getUser(@Header("AuthToken") String authToken,
                               @Path("userId") String userId);
}
