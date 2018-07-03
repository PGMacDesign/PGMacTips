package com.pgmacdesign.pgmactips.instagram;

import android.content.Context;

import com.pgmacdesign.pgmactips.adaptersandlisteners.OnTaskCompleteListener;
import com.pgmacdesign.pgmactips.misc.CustomAnnotationsBase;
import com.pgmacdesign.pgmactips.utilities.NetworkUtilities;
import com.pgmacdesign.pgmactips.utilities.StringUtilities;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Instagram API SDK since as of this date there is no Instagram SDK
 * Created by pmacdowell on 2017-04-19.
 */
@CustomAnnotationsBase.RequiresDependency(requiresDependencies = {CustomAnnotationsBase.Dependencies.Retrofit2,
        CustomAnnotationsBase.Dependencies.Retrofit2GSONConverter, CustomAnnotationsBase.Dependencies.GSON,
        CustomAnnotationsBase.Dependencies.OkHttp3LoggingInterceptor, CustomAnnotationsBase.Dependencies.Okio})
public class InstagramApiCalls {

    private InstagramService myService;
    private Context context;
    private String instagramAccessToken, instagramUserId;

    /**
     * Constructor
     * @param context
     * @param myService
     * @param instagramAccessToken
     */
    @CustomAnnotationsBase.RequiresDependency(requiresDependencies = {CustomAnnotationsBase.Dependencies.Retrofit2,
            CustomAnnotationsBase.Dependencies.Retrofit2GSONConverter, CustomAnnotationsBase.Dependencies.GSON,
            CustomAnnotationsBase.Dependencies.OkHttp3LoggingInterceptor, CustomAnnotationsBase.Dependencies.Okio})
    public InstagramApiCalls(Context context, InstagramService myService,
                             String instagramAccessToken){
        this.myService = myService;
        this.context = context;
        this.instagramAccessToken = instagramAccessToken;
    }

    /**
     * Set the userId (As this is retrieved after the fact
     * @param instagramUserId
     */
    public void setUserId(String instagramUserId){
        this.instagramUserId = instagramUserId;
    }

    /**
     * Checker to make sure data is set and internet is working
     * @return
     */
    private boolean doIProceed() {
        if(this.myService == null || this.context == null){
            return false;
        }
        if(StringUtilities.isNullOrEmpty(this.instagramAccessToken)){
            return false;
        }
        return NetworkUtilities.haveNetworkConnection(context);
    }

    /**
     * Get recent media uploaded by the user
     * (Too long to list sample, check link here -
     * https://www.instagram.com/developer/endpoints/users/  )
     * @param listener
     */
    public void getRecentMedia(final OnTaskCompleteListener listener, String paginationMaxId,
                               String paginationMinId, String countRequested){
        if (!doIProceed()) {
            listener.onTaskComplete(InstagramConstants.GENERIC_INSTAGRAM_RESPONSE_STRING,
                    InstagramConstants.TAG_INSTAGRAM_UNKNOWN_ERROR);
            return;
        }

        Call<InstagramDataObject.MediaDataObject> call = myService.
                getSelfRecentMedia(instagramAccessToken, paginationMaxId,
                        paginationMinId, countRequested);
        call.enqueue(new Callback<InstagramDataObject.MediaDataObject>() {
            @Override
            public void onResponse(Call<InstagramDataObject.MediaDataObject> call, Response<InstagramDataObject.MediaDataObject>
                    response) {
                try {
                    InstagramDataObject.MediaDataObject returnedObject = response.body();
                    if (returnedObject != null) {
                        listener.onTaskComplete(returnedObject,
                                InstagramConstants.TAG_INSTAGRAM_MEDIADATAOBJECT);
                    } else {
                        if(response.errorBody() != null) {
                            listener.onTaskComplete(response.errorBody().string(),
                                    InstagramConstants.TAG_INSTAGRAM_ERROR_STRING);
                        } else {
                            listener.onTaskComplete(InstagramConstants.GENERIC_INSTAGRAM_RESPONSE_STRING,
                                    InstagramConstants.TAG_INSTAGRAM_UNKNOWN_ERROR);
                        }
                    }
                } catch (Exception e ){
                    e.printStackTrace();
                    listener.onTaskComplete(InstagramConstants.GENERIC_INSTAGRAM_RESPONSE_STRING,
                            InstagramConstants.TAG_INSTAGRAM_UNKNOWN_ERROR);
                }
            }

            @Override
            public void onFailure(Call<InstagramDataObject.MediaDataObject> call, Throwable t) {
                t.printStackTrace();
                listener.onTaskComplete(t.getMessage(),
                        InstagramConstants.TAG_INSTAGRAM_ERROR_STRING);
            }
        });
    }

    /**
     * Overloaded method, gets recent media of other userId passed in
     * @param listener
     * @param paginationMaxId
     * @param paginationMinId
     * @param countRequested
     * @param otherUserId
     */
    public void getRecentMedia(final OnTaskCompleteListener listener, String paginationMaxId,
                               String paginationMinId, String countRequested, String otherUserId){
        if (!doIProceed()) {
            listener.onTaskComplete(InstagramConstants.GENERIC_INSTAGRAM_RESPONSE_STRING,
                    InstagramConstants.TAG_INSTAGRAM_UNKNOWN_ERROR);
            return;
        }

        Call<InstagramDataObject.MediaDataObject> call = myService.
                getRecentMedia(otherUserId, instagramAccessToken, paginationMaxId,
                        paginationMinId, countRequested);
        call.enqueue(new Callback<InstagramDataObject.MediaDataObject>() {
            @Override
            public void onResponse(Call<InstagramDataObject.MediaDataObject> call, Response<InstagramDataObject.MediaDataObject>
                    response) {
                try {
                    InstagramDataObject.MediaDataObject returnedObject = response.body();
                    if (returnedObject != null) {
                        listener.onTaskComplete(returnedObject,
                                InstagramConstants.TAG_INSTAGRAM_MEDIADATAOBJECT);
                    } else {
                        if(response.errorBody() != null) {
                            listener.onTaskComplete(response.errorBody().string(),
                                    InstagramConstants.TAG_INSTAGRAM_ERROR_STRING);
                        } else {
                            listener.onTaskComplete(InstagramConstants.GENERIC_INSTAGRAM_RESPONSE_STRING,
                                    InstagramConstants.TAG_INSTAGRAM_UNKNOWN_ERROR);
                        }
                    }
                } catch (Exception e ){
                    e.printStackTrace();
                    listener.onTaskComplete(InstagramConstants.GENERIC_INSTAGRAM_RESPONSE_STRING,
                            InstagramConstants.TAG_INSTAGRAM_UNKNOWN_ERROR);
                }
            }

            @Override
            public void onFailure(Call<InstagramDataObject.MediaDataObject> call, Throwable t) {
                t.printStackTrace();
                listener.onTaskComplete(t.getMessage(),
                        InstagramConstants.TAG_INSTAGRAM_ERROR_STRING);
            }
        });
    }

    /**
     * Get the user data. Sample return :
     *     "data": {
     "id": "1574083",
     "username": "snoopdogg",
     "full_name": "Snoop Dogg",
     "profile_picture": "http://distillery.s3.amazonaws.com/profiles/profile_1574083_75sq_1295469061.jpg",
     "bio": "This is my bio",
     "website": "http://snoopdogg.com",
     "counts": {
     "media": 1320,
     "follows": 420,
     "followed_by": 3410
     }
     * @param listener
     */
    public void getUserData(final OnTaskCompleteListener listener){
        if (!doIProceed()) {
            listener.onTaskComplete(InstagramConstants.GENERIC_INSTAGRAM_RESPONSE_STRING,
                    InstagramConstants.TAG_INSTAGRAM_UNKNOWN_ERROR);
            return;
        }

        Call<InstagramDataObject.UserDataObject> call = myService.
                getSelfData(instagramAccessToken);
        call.enqueue(new Callback<InstagramDataObject.UserDataObject>() {
            @Override
            public void onResponse(Call<InstagramDataObject.UserDataObject> call, Response<InstagramDataObject.UserDataObject>
                    response) {
                try {
                    InstagramDataObject.UserDataObject returnedObject = response.body();
                    if (returnedObject != null) {
                        listener.onTaskComplete(returnedObject,
                                InstagramConstants.TAG_INSTAGRAM_USERDATAOBJECT);
                    } else {
                        if(response.errorBody() != null) {
                            listener.onTaskComplete(response.errorBody().string(),
                                    InstagramConstants.TAG_INSTAGRAM_ERROR_STRING);
                        } else {
                            listener.onTaskComplete(InstagramConstants.GENERIC_INSTAGRAM_RESPONSE_STRING,
                                    InstagramConstants.TAG_INSTAGRAM_UNKNOWN_ERROR);
                        }
                    }
                } catch (Exception e ){
                    e.printStackTrace();
                    listener.onTaskComplete(InstagramConstants.GENERIC_INSTAGRAM_RESPONSE_STRING,
                            InstagramConstants.TAG_INSTAGRAM_UNKNOWN_ERROR);
                }
            }

            @Override
            public void onFailure(Call<InstagramDataObject.UserDataObject> call, Throwable t) {
                t.printStackTrace();
                listener.onTaskComplete(t.getMessage(),
                        InstagramConstants.TAG_INSTAGRAM_ERROR_STRING);
            }
        });
    }

    /**
     * Overloaded method, this is used for searching someone else's account
     * @param listener
     * @param otherUserId
     */
    public void getUserData(final OnTaskCompleteListener listener, String otherUserId){
        if (!doIProceed()) {
            listener.onTaskComplete(InstagramConstants.GENERIC_INSTAGRAM_RESPONSE_STRING,
                    InstagramConstants.TAG_INSTAGRAM_UNKNOWN_ERROR);
            return;
        }

        Call<InstagramDataObject.UserDataObject> call = myService.
                getUserData(otherUserId, instagramAccessToken);
        call.enqueue(new Callback<InstagramDataObject.UserDataObject>() {
            @Override
            public void onResponse(Call<InstagramDataObject.UserDataObject> call, Response<InstagramDataObject.UserDataObject>
                    response) {
                try {
                    InstagramDataObject.UserDataObject returnedObject = response.body();
                    if (returnedObject != null) {
                        listener.onTaskComplete(returnedObject,
                                InstagramConstants.TAG_INSTAGRAM_USERDATAOBJECT);
                    } else {
                        if(response.errorBody() != null) {
                            listener.onTaskComplete(response.errorBody().string(),
                                    InstagramConstants.TAG_INSTAGRAM_ERROR_STRING);
                        } else {
                            listener.onTaskComplete(InstagramConstants.GENERIC_INSTAGRAM_RESPONSE_STRING,
                                    InstagramConstants.TAG_INSTAGRAM_UNKNOWN_ERROR);
                        }
                    }
                } catch (Exception e ){
                    e.printStackTrace();
                    listener.onTaskComplete(InstagramConstants.GENERIC_INSTAGRAM_RESPONSE_STRING,
                            InstagramConstants.TAG_INSTAGRAM_UNKNOWN_ERROR);
                }
            }

            @Override
            public void onFailure(Call<InstagramDataObject.UserDataObject> call, Throwable t) {
                t.printStackTrace();
                listener.onTaskComplete(t.getMessage(),
                        InstagramConstants.TAG_INSTAGRAM_ERROR_STRING);
            }
        });
    }

    /**
     * Get recently liked data
     * @param listener
     * @param paginationMaxId
     * @param countRequested
     */
    public void getLiked(final OnTaskCompleteListener listener, String paginationMaxId,
                               String countRequested){
        if (!doIProceed()) {
            listener.onTaskComplete(InstagramConstants.GENERIC_INSTAGRAM_RESPONSE_STRING,
                    InstagramConstants.TAG_INSTAGRAM_UNKNOWN_ERROR);
            return;
        }

        Call<InstagramDataObject.MediaDataObject> call = myService.
                getSelfRecentLiked(instagramAccessToken, paginationMaxId, countRequested);
        call.enqueue(new Callback<InstagramDataObject.MediaDataObject>() {
            @Override
            public void onResponse(Call<InstagramDataObject.MediaDataObject> call, Response<InstagramDataObject.MediaDataObject>
                    response) {
                try {
                    InstagramDataObject.MediaDataObject returnedObject = response.body();
                    if (returnedObject != null) {
                        listener.onTaskComplete(returnedObject,
                                InstagramConstants.TAG_INSTAGRAM_MEDIADATAOBJECT);
                    } else {
                        if(response.errorBody() != null) {
                            listener.onTaskComplete(response.errorBody().string(),
                                    InstagramConstants.TAG_INSTAGRAM_ERROR_STRING);
                        } else {
                            listener.onTaskComplete(InstagramConstants.GENERIC_INSTAGRAM_RESPONSE_STRING,
                                    InstagramConstants.TAG_INSTAGRAM_UNKNOWN_ERROR);
                        }
                    }
                } catch (Exception e ){
                    e.printStackTrace();
                    listener.onTaskComplete(InstagramConstants.GENERIC_INSTAGRAM_RESPONSE_STRING,
                            InstagramConstants.TAG_INSTAGRAM_UNKNOWN_ERROR);
                }
            }

            @Override
            public void onFailure(Call<InstagramDataObject.MediaDataObject> call, Throwable t) {
                t.printStackTrace();
                listener.onTaskComplete(t.getMessage(),
                        InstagramConstants.TAG_INSTAGRAM_ERROR_STRING);
            }
        });
    }

    /**
     * Search for a user with a query
     * @param listener
     * @param query
     * @param countRequested
     */
    public void searchUsers(final OnTaskCompleteListener listener, String query,
                         String countRequested){
        if (!doIProceed()) {
            listener.onTaskComplete(InstagramConstants.GENERIC_INSTAGRAM_RESPONSE_STRING,
                    InstagramConstants.TAG_INSTAGRAM_UNKNOWN_ERROR);
            return;
        }

        Call<InstagramDataObject.MultipleUserDataObject> call = myService.
                searchUsers(instagramAccessToken, query, countRequested);
        call.enqueue(new Callback<InstagramDataObject.MultipleUserDataObject>() {
            @Override
            public void onResponse(Call<InstagramDataObject.MultipleUserDataObject> call,
                                   Response<InstagramDataObject.MultipleUserDataObject>
                    response) {
                try {
                    InstagramDataObject.MultipleUserDataObject returnedObject = response.body();
                    if (returnedObject != null) {
                        listener.onTaskComplete(returnedObject,
                                InstagramConstants.TAG_INSTAGRAM_MULTIPLE_USER_DATA);
                    } else {
                        if(response.errorBody() != null) {
                            listener.onTaskComplete(response.errorBody().string(),
                                    InstagramConstants.TAG_INSTAGRAM_ERROR_STRING);
                        } else {
                            listener.onTaskComplete(InstagramConstants.GENERIC_INSTAGRAM_RESPONSE_STRING,
                                    InstagramConstants.TAG_INSTAGRAM_UNKNOWN_ERROR);
                        }
                    }
                } catch (Exception e ){
                    e.printStackTrace();
                    listener.onTaskComplete(InstagramConstants.GENERIC_INSTAGRAM_RESPONSE_STRING,
                            InstagramConstants.TAG_INSTAGRAM_UNKNOWN_ERROR);
                }
            }

            @Override
            public void onFailure(Call<InstagramDataObject.MultipleUserDataObject> call,
                                  Throwable t) {
                t.printStackTrace();
                listener.onTaskComplete(t.getMessage(),
                        InstagramConstants.TAG_INSTAGRAM_ERROR_STRING);
            }
        });
    }


    /**
     * Get a list of who the user follows
     * @param listener
     */
    public void getFollows(final OnTaskCompleteListener listener){
        if (!doIProceed()) {
            listener.onTaskComplete(InstagramConstants.GENERIC_INSTAGRAM_RESPONSE_STRING,
                    InstagramConstants.TAG_INSTAGRAM_UNKNOWN_ERROR);
            return;
        }

        Call<InstagramDataObject.MultipleUserDataObject> call = myService.
                getSelfFollows(instagramAccessToken);
        call.enqueue(new Callback<InstagramDataObject.MultipleUserDataObject>() {
            @Override
            public void onResponse(Call<InstagramDataObject.MultipleUserDataObject> call,
                                   Response<InstagramDataObject.MultipleUserDataObject>
                                           response) {
                try {
                    InstagramDataObject.MultipleUserDataObject returnedObject = response.body();
                    if (returnedObject != null) {
                        listener.onTaskComplete(returnedObject,
                                InstagramConstants.TAG_INSTAGRAM_MULTIPLE_USER_DATA);
                    } else {
                        if(response.errorBody() != null) {
                            listener.onTaskComplete(response.errorBody().string(),
                                    InstagramConstants.TAG_INSTAGRAM_ERROR_STRING);
                        } else {
                            listener.onTaskComplete(InstagramConstants.GENERIC_INSTAGRAM_RESPONSE_STRING,
                                    InstagramConstants.TAG_INSTAGRAM_UNKNOWN_ERROR);
                        }
                    }
                } catch (Exception e ){
                    e.printStackTrace();
                    listener.onTaskComplete(InstagramConstants.GENERIC_INSTAGRAM_RESPONSE_STRING,
                            InstagramConstants.TAG_INSTAGRAM_UNKNOWN_ERROR);
                }
            }

            @Override
            public void onFailure(Call<InstagramDataObject.MultipleUserDataObject> call,
                                  Throwable t) {
                t.printStackTrace();
                listener.onTaskComplete(t.getMessage(),
                        InstagramConstants.TAG_INSTAGRAM_ERROR_STRING);
            }
        });
    }

    /**
     * Get a list of who follows the user
     * @param listener
     */
    public void getFollowedBy(final OnTaskCompleteListener listener){
        if (!doIProceed()) {
            listener.onTaskComplete(InstagramConstants.GENERIC_INSTAGRAM_RESPONSE_STRING,
                    InstagramConstants.TAG_INSTAGRAM_UNKNOWN_ERROR);
            return;
        }

        Call<InstagramDataObject.MultipleUserDataObject> call = myService.
                getSelfFollowedBy(instagramAccessToken);
        call.enqueue(new Callback<InstagramDataObject.MultipleUserDataObject>() {
            @Override
            public void onResponse(Call<InstagramDataObject.MultipleUserDataObject> call,
                                   Response<InstagramDataObject.MultipleUserDataObject>
                                           response) {
                try {
                    InstagramDataObject.MultipleUserDataObject returnedObject = response.body();
                    if (returnedObject != null) {
                        listener.onTaskComplete(returnedObject,
                                InstagramConstants.TAG_INSTAGRAM_MULTIPLE_USER_DATA);
                    } else {
                        if(response.errorBody() != null) {
                            listener.onTaskComplete(response.errorBody().string(),
                                    InstagramConstants.TAG_INSTAGRAM_ERROR_STRING);
                        } else {
                            listener.onTaskComplete(InstagramConstants.GENERIC_INSTAGRAM_RESPONSE_STRING,
                                    InstagramConstants.TAG_INSTAGRAM_UNKNOWN_ERROR);
                        }
                    }
                } catch (Exception e ){
                    e.printStackTrace();
                    listener.onTaskComplete(InstagramConstants.GENERIC_INSTAGRAM_RESPONSE_STRING,
                            InstagramConstants.TAG_INSTAGRAM_UNKNOWN_ERROR);
                }
            }

            @Override
            public void onFailure(Call<InstagramDataObject.MultipleUserDataObject> call,
                                  Throwable t) {
                t.printStackTrace();
                listener.onTaskComplete(t.getMessage(),
                        InstagramConstants.TAG_INSTAGRAM_ERROR_STRING);
            }
        });
    }


    /**
     * Get a list of the users who have requested this user's permission to follow.
     * @param listener
     */
    public void getRequestedBy(final OnTaskCompleteListener listener){
        if (!doIProceed()) {
            listener.onTaskComplete(InstagramConstants.GENERIC_INSTAGRAM_RESPONSE_STRING,
                    InstagramConstants.TAG_INSTAGRAM_UNKNOWN_ERROR);
            return;
        }

        Call<InstagramDataObject.MultipleUserDataObject> call = myService.
                getSelfRequestedBy(instagramAccessToken);
        call.enqueue(new Callback<InstagramDataObject.MultipleUserDataObject>() {
            @Override
            public void onResponse(Call<InstagramDataObject.MultipleUserDataObject> call,
                                   Response<InstagramDataObject.MultipleUserDataObject>
                                           response) {
                try {
                    InstagramDataObject.MultipleUserDataObject returnedObject = response.body();
                    if (returnedObject != null) {
                        listener.onTaskComplete(returnedObject,
                                InstagramConstants.TAG_INSTAGRAM_MULTIPLE_USER_DATA);
                    } else {
                        if(response.errorBody() != null) {
                            listener.onTaskComplete(response.errorBody().string(),
                                    InstagramConstants.TAG_INSTAGRAM_ERROR_STRING);
                        } else {
                            listener.onTaskComplete(InstagramConstants.GENERIC_INSTAGRAM_RESPONSE_STRING,
                                    InstagramConstants.TAG_INSTAGRAM_UNKNOWN_ERROR);
                        }
                    }
                } catch (Exception e ){
                    e.printStackTrace();
                    listener.onTaskComplete(InstagramConstants.GENERIC_INSTAGRAM_RESPONSE_STRING,
                            InstagramConstants.TAG_INSTAGRAM_UNKNOWN_ERROR);
                }
            }

            @Override
            public void onFailure(Call<InstagramDataObject.MultipleUserDataObject> call,
                                  Throwable t) {
                t.printStackTrace();
                listener.onTaskComplete(t.getMessage(),
                        InstagramConstants.TAG_INSTAGRAM_ERROR_STRING);
            }
        });
    }


    /**
     * Get information about a relationship to another user.
     * Relationships are expressed using the following terms in the response:
            1) outgoing_status: Your relationship to the user. Can be 'follows',
               'requested', 'none'.
            2) incoming_status: A user's relationship to you. Can be 'followed_by',
               'requested_by', 'blocked_by_you', 'none'.
     * @param listener
     * @param otherUserId The other userId to compare against
     */
    public void getRelationships(final OnTaskCompleteListener listener, String otherUserId){
        if (!doIProceed()) {
            listener.onTaskComplete(InstagramConstants.GENERIC_INSTAGRAM_RESPONSE_STRING,
                    InstagramConstants.TAG_INSTAGRAM_UNKNOWN_ERROR);
            return;
        }

        Call<InstagramDataObject.UserRelationshipObj> call = myService.
                getRelationship(otherUserId, instagramAccessToken);
        call.enqueue(new Callback<InstagramDataObject.UserRelationshipObj>() {
            @Override
            public void onResponse(Call<InstagramDataObject.UserRelationshipObj> call,
                                   Response<InstagramDataObject.UserRelationshipObj>
                                           response) {
                try {
                    InstagramDataObject.UserRelationshipObj returnedObject = response.body();
                    if (returnedObject != null) {
                        listener.onTaskComplete(returnedObject,
                                InstagramConstants.TAG_INSTAGRAM_USER_RELATIONSHIP);
                    } else {
                        if(response.errorBody() != null) {
                            listener.onTaskComplete(response.errorBody().string(),
                                    InstagramConstants.TAG_INSTAGRAM_ERROR_STRING);
                        } else {
                            listener.onTaskComplete(InstagramConstants.GENERIC_INSTAGRAM_RESPONSE_STRING,
                                    InstagramConstants.TAG_INSTAGRAM_UNKNOWN_ERROR);
                        }
                    }
                } catch (Exception e ){
                    e.printStackTrace();
                    listener.onTaskComplete(InstagramConstants.GENERIC_INSTAGRAM_RESPONSE_STRING,
                            InstagramConstants.TAG_INSTAGRAM_UNKNOWN_ERROR);
                }
            }

            @Override
            public void onFailure(Call<InstagramDataObject.UserRelationshipObj> call, Throwable t) {
                t.printStackTrace();
                listener.onTaskComplete(t.getMessage(),
                        InstagramConstants.TAG_INSTAGRAM_ERROR_STRING);
            }
        });
    }

    /**
     * Update a relationship by taking an action (See actions for more info)
     * @param listener
     * @param action {@link com.pgmacdesign.pgmactips.instagram.InstagramConstants.InstagramActions}
     */
    public void updateRelationships(final OnTaskCompleteListener listener,
                                    InstagramConstants.InstagramActions action){
        if (!doIProceed()) {
            listener.onTaskComplete(InstagramConstants.GENERIC_INSTAGRAM_RESPONSE_STRING,
                    InstagramConstants.TAG_INSTAGRAM_UNKNOWN_ERROR);
            return;
        }

        Call<InstagramDataObject.UserRelationshipObj> call = myService.
                setRelationship(instagramAccessToken, action.toString());
        call.enqueue(new Callback<InstagramDataObject.UserRelationshipObj>() {
            @Override
            public void onResponse(Call<InstagramDataObject.UserRelationshipObj> call,
                                   Response<InstagramDataObject.UserRelationshipObj>
                                           response) {
                try {
                    InstagramDataObject.UserRelationshipObj returnedObject = response.body();
                    if (returnedObject != null) {
                        listener.onTaskComplete(returnedObject,
                                InstagramConstants.TAG_INSTAGRAM_USER_RELATIONSHIP);
                    } else {
                        if(response.errorBody() != null) {
                            listener.onTaskComplete(response.errorBody().string(),
                                    InstagramConstants.TAG_INSTAGRAM_ERROR_STRING);
                        } else {
                            listener.onTaskComplete(InstagramConstants.GENERIC_INSTAGRAM_RESPONSE_STRING,
                                    InstagramConstants.TAG_INSTAGRAM_UNKNOWN_ERROR);
                        }
                    }
                } catch (Exception e ){
                    e.printStackTrace();
                    listener.onTaskComplete(InstagramConstants.GENERIC_INSTAGRAM_RESPONSE_STRING,
                            InstagramConstants.TAG_INSTAGRAM_UNKNOWN_ERROR);
                }
            }

            @Override
            public void onFailure(Call<InstagramDataObject.UserRelationshipObj> call, Throwable t) {
                t.printStackTrace();
                listener.onTaskComplete(t.getMessage(),
                        InstagramConstants.TAG_INSTAGRAM_ERROR_STRING);
            }
        });
    }

}
