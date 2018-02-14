package com.pgmacdesign.pgmactips.networkclasses.retrofitutilities.serviceapiinterfaces;

import com.pgmacdesign.pgmactips.datamodels.SamplePojo;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * SAMPLE SERVICE EXAMPLE
 * Created by pmacdowell on 8/25/2016.
 */
public interface SampleRetrofitService {

    public static final String VERSION_STRING = "/v1";
    public static final String API_SUFFIX_STRING = "/api";

    /**
     * This would be a sample of a post request to a login endpoint that takes in an object
     * of type SamplePojo for the credentials and then returns an object of type SamplePojo
     * @param credentials object of type SamplePojo
     * @return An object of type SamplePojo
     * This url request would look like this if printed out:
     * www.baseurl.com/api/v1/login  + the headers  +  the JSON Body of type SamplePojo
     */
    @POST(API_SUFFIX_STRING + VERSION_STRING + "/login")
    Call<SamplePojo> login(@Body SamplePojo credentials);

    /**
     * This would be a sample of a post request to a sample get user endpoint that takes in a path
     * string attached to the user id and returns an object of type SamplePojo
     * @param authToken Sample auth token
     * @param userId Sample user id
     * @return Return an object of type SamplePojo
     * This url request would look like this if printed out:
     * www.baseurl.com/api/v1/user/USER_ID_HERE  + the headers
     */
    @GET(API_SUFFIX_STRING + VERSION_STRING + "/users/{userId}")
    Call<SamplePojo> getUser(@Header("AuthToken") String authToken,
                               @Path("userId") String userId);

    /**
     * This would be a sample of a post request to a sample get user endpoint that takes in a path
     * string attached to the user id as well as some query variables for fields
     * and returns an object of type SamplePojo.
     * @param authToken Sample auth token
     * @param userId Sample user id
     * @return Return an a map of Strings linked to objects of type SamplePojo. This would be
     * fairly typical if returning an ID to their account user object
     * This url request would look like this if printed out:
     * www.baseurl.com/api/v1/user/USER_ID_HERE/specifics?fields=FIELDS_STRING_HERE&sortByName=SORT_BY_NAME_STRING_HERE
     * + the headers
     */
    @GET(API_SUFFIX_STRING + VERSION_STRING + "/users/{userId}/specifics")
    Call<Map<String, SamplePojo>> getUsers(@Header("AuthToken") String authToken,
                               @Path("userId") String userId,
                               @Query("fields") String fields,
                               @Query("sortByName") String sortByName);

    /**
     * This would be a sample of a PUT request to a sample get user endpoint that takes in a path
     * string attached to the user id as well as a user object (SamplePojo object) and returns
     * a map of type String, ?. An example return here would be something like:
     * {"deleted" : "successful", "info" {"deletedOn" : "2016-09-01", "deletedVia" : "mobile"}}
     * @param authToken Sample auth token
     * @param userId Sample user id
     * @param userObject User object of type SamplePojo to update the user details. Maybe changing
     *                   their name or some other portion of their object
     * @return Return an object of type SamplePojo (See above for sample printed return)
     * This url request would look like this if printed out:
     * www.baseurl.com/api/v1/user/USER_ID_HERE  + the headers  + JSON Body
     */
    @PUT(API_SUFFIX_STRING + VERSION_STRING + "/user/{userId}")
    Call<Map<String, ?>> updateUser(@Header("AuthToken") String authToken,
                                         @Path("userId") String userId,
                                         @Body SamplePojo userObject);

    /**
     * This would be a sample of a DELETE request to remove a user from the DB. The data sent
     * would be the auth token and their user ID to delete.
     * @param authToken Sample auth token
     * @param userId Sample user id
     * @return Returns nothing, so void. In this case, just check for an error response
     * This url request would look like this if printed out:
     * www.baseurl.com/api/v1/user/USER_ID_HERE  + the headers
     */
    @DELETE(API_SUFFIX_STRING + VERSION_STRING + "/user/{userId}")
    Call<Void> deleteUser(@Header("AuthToken") String authToken,
                               @Path("userId") String userId);
}
