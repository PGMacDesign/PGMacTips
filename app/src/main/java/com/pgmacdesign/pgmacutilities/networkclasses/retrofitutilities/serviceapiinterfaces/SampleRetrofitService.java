package com.pgmacdesign.pgmacutilities.networkclasses.retrofitutilities.serviceapiinterfaces;

import com.pgmacdesign.pgmacutilities.TESTINGPOJO;
import com.pgmacdesign.pgmacutilities.TESTINGPOJO2;

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

    public static final String VERSION_STRING = "v1";
    public static final String API_SUFFIX_STRING = "/api";

    /**
     * This would be a sample of a post request to a login endpoint that takes in an object
     * of type TESTINGPOJO for the credentials and then returns an object of type TESTINGPOJO2
     * @param credentials object of type TESTINGPOJO
     * @return An object of type TESTINGPOJO2
     * This url request would look like this if printed out:
     * www.baseurl.com/api/v1/login  + the headers  +  the JSON Body of type TESTINGPOJO
     */
    @POST(API_SUFFIX_STRING + VERSION_STRING + "/login")
    Call<TESTINGPOJO2> login(@Body TESTINGPOJO credentials);

    /**
     * This would be a sample of a post request to a sample get user endpoint that takes in a path
     * string attached to the user id and returns an object of type TESTINGPOJO2
     * @param authToken Sample auth token
     * @param userId Sample user id
     * @return Return an object of type TESTINGPOJO2
     * This url request would look like this if printed out:
     * www.baseurl.com/api/v1/user/USER_ID_HERE  + the headers
     */
    @GET(API_SUFFIX_STRING + VERSION_STRING + "/users/{userId}")
    Call<TESTINGPOJO2> getUser(@Header("AuthToken") String authToken,
                               @Path("userId") String userId);

    /**
     * This would be a sample of a post request to a sample get user endpoint that takes in a path
     * string attached to the user id as well as some query variables for fields
     * and returns an object of type TESTINGPOJO2.
     * @param authToken Sample auth token
     * @param userId Sample user id
     * @return Return an a map of Strings linked to objects of type TESTINGPOJO2. This would be
     * fairly typical if returning an ID to their account user object
     * This url request would look like this if printed out:
     * www.baseurl.com/api/v1/user/USER_ID_HERE/specifics?fields=FIELDS_STRING_HERE&sortByName=SORT_BY_NAME_STRING_HERE
     * + the headers
     */
    @GET(API_SUFFIX_STRING + VERSION_STRING + "/users/{userId}/specifics")
    Call<Map<String, TESTINGPOJO2>> getUsers(@Header("AuthToken") String authToken,
                               @Path("userId") String userId,
                               @Query("fields") String fields,
                               @Query("sortByName") String sortByName);

    /**
     * This would be a sample of a PUT request to a sample get user endpoint that takes in a path
     * string attached to the user id as well as a user object (TESTINGPOJO2 object) and returns
     * a map of type String, ?. An example return here would be something like:
     * {"deleted" : "successful", "info" {"deletedOn" : "2016-09-01", "deletedVia" : "mobile"}}
     * @param authToken Sample auth token
     * @param userId Sample user id
     * @param userObject User object of type TESTINGPOJO2 to update the user details. Maybe changing
     *                   their name or some other portion of their object
     * @return Return an object of type TESTINGPOJO2 (See above for sample printed return)
     * This url request would look like this if printed out:
     * www.baseurl.com/api/v1/user/USER_ID_HERE  + the headers  + JSON Body
     */
    @PUT(API_SUFFIX_STRING + VERSION_STRING + "/user/{userId}")
    Call<Map<String, ?>> updateUser(@Header("AuthToken") String authToken,
                                         @Path("userId") String userId,
                                         @Body TESTINGPOJO2 userObject);

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
