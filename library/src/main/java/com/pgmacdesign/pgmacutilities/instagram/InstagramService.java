package com.pgmacdesign.pgmacutilities.instagram;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by pmacdowell on 2017-04-19.
 */

public interface InstagramService {

    public static final String VERSION = "/v1";

    //Misc: https://www.instagram.com/developer/endpoints/
    public static final String USERS = "/users";
    public static final String SELF = "/self";
    public static final String MEDIA = "/media";
    public static final String RECENT = "/recent";

    //User Endpoints: https://www.instagram.com/developer/endpoints/users/
    public static final String LIKED = "/liked";
    public static final String SEARCH = "/search";

    //Relationship Endpoints: https://www.instagram.com/developer/endpoints/relationships/
    public static final String FOLLOWS = "/follows";
    public static final String FOLLOWED_BY = "/followed-by";
    public static final String REQUESTED_BY = "/requested-by";
    public static final String RELATIONSHIP = "/relationship";

    //Media Endpoints: https://www.instagram.com/developer/endpoints/media/
    public static final String SHORTCODE = "/shortcode";

    //Comments Endpoints: https://www.instagram.com/developer/endpoints/comments/
    public static final String COMMENTS = "/comments";

    //Comments Endpoints: https://www.instagram.com/developer/endpoints/likes/
    public static final String LIKES = "/likes";

    //Tag Endpoints: https://www.instagram.com/developer/endpoints/tags/
    public static final String TAGS = "/tags";

    //Locations Endpoints: https://www.instagram.com/developer/endpoints/locations/
    public static final String LOCATIONS = "/locations";

    //For a full listing of the endpoints, check out documentation here
    // https://www.instagram.com/developer/endpoints/media/


    ////////////////////
    // User Endpoints //
    ////////////////////

    @GET(VERSION + USERS + SELF)
    Call<InstagramDataObject.UserDataObject> getSelfData(
            @Query("access_token") String access_token
    );

    @GET(VERSION + USERS + "/{user-id}/")
    Call<InstagramDataObject.UserDataObject> getUserData(
            @Path("user-id") String userId,
            @Query("access_token") String access_token
    );

    @GET(VERSION + USERS + SELF + MEDIA + RECENT)
    Call<InstagramDataObject.MediaDataObject> getSelfRecentMedia(
            @Query("access_token") String access_token,
            @Query("max_id")String max_id,
            @Query("min_id")String min_id,
            @Query("count")String count
    );

    @GET(VERSION + USERS + "/{user-id}" + MEDIA + RECENT)
    Call<InstagramDataObject.MediaDataObject> getRecentMedia(
            @Path("user-id") String userId,
            @Query("access_token") String access_token,
            @Query("max_id")String max_id,
            @Query("min_id")String min_id,
            @Query("count")String count
    );

    @GET(VERSION + USERS + SELF + MEDIA + LIKED)
    Call<InstagramDataObject.MediaDataObject> getSelfRecentLiked(
            @Query("access_token") String access_token,
            @Query("max_id")String max_id,
            @Query("count")String count
    );

    @GET(VERSION + USERS + SEARCH)
    Call<InstagramDataObject.MultipleUserDataObject> searchUsers(
            @Query("access_token") String access_token,
            @Query("q")String query,
            @Query("count")String count
    );


    ////////////////////////////
    // Relationship Endpoints //
    ////////////////////////////

    @GET(VERSION + USERS + SELF + FOLLOWS)
    Call<InstagramDataObject.MultipleUserDataObject> getSelfFollows(
            @Query("access_token") String access_token
    );

    @GET(VERSION + USERS + SELF + FOLLOWED_BY)
    Call<InstagramDataObject.MultipleUserDataObject> getSelfFollowedBy(
            @Query("access_token") String access_token
    );

    @GET(VERSION + USERS + SELF + REQUESTED_BY)
    Call<InstagramDataObject.MultipleUserDataObject> getSelfRequestedBy(
            @Query("access_token") String access_token
    );

    @GET(VERSION + USERS + "{user-id}/" + RELATIONSHIP)
    Call<InstagramDataObject.UserRelationshipObj> getRelationship(
            @Path("user-id") String otherUserId,
            @Query("access_token") String access_token
    );

    /**
    For updating the request. Action can be:
    	follow | unfollow | approve | ignore
     */
    @POST(VERSION + USERS + SELF + FOLLOWS)
    Call<InstagramDataObject.UserRelationshipObj> setRelationship(
            @Query("access_token") String access_token,
            @Query("action") String action
    );

    ////////////////////////////
    // Media Endpoints //
    ////////////////////////////

    /*
    @GET(VERSION + USERS + SELF + FOLLOWS)
    Call<Object> doStuff(
            @Query("access_token") String access_token
    );
    */


    ////////////////////////////
    // Comments Endpoints //
    ////////////////////////////

    /*
    @GET(VERSION + USERS + SELF + FOLLOWS)
    Call<Object> doStuff(
            @Query("access_token") String access_token
    );
    */


    ////////////////////////////
    // Likes Endpoints //
    ////////////////////////////

    /*
    @GET(VERSION + USERS + SELF + FOLLOWS)
    Call<Object> doStuff(
            @Query("access_token") String access_token
    );
    */


    ////////////////////////////
    // Tags Endpoints //
    ////////////////////////////

    /*
    @GET(VERSION + USERS + SELF + FOLLOWS)
    Call<Object> doStuff(
            @Query("access_token") String access_token
    );
    */


    ////////////////////////////
    // Locations Endpoints //
    ////////////////////////////

    /*
    @GET(VERSION + USERS + SELF + FOLLOWS)
    Call<Object> doStuff(
            @Query("access_token") String access_token
    );
    */
}

