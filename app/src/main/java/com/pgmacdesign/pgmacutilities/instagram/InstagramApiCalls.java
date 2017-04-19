package com.pgmacdesign.pgmacutilities.instagram;

import android.content.Context;

import com.pgmacdesign.pgmacutilities.adaptersandlisteners.OnTaskCompleteListener;
import com.pgmacdesign.pgmacutilities.utilities.NetworkUtilities;
import com.pgmacdesign.pgmacutilities.utilities.StringUtilities;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
/**
 * Created by pmacdowell on 2017-04-19.
 */

public class InstagramApiCalls {

    private InstagramService myService;
    private Context context;
    private String instagramAccessToken, instagramUserId;

    public InstagramApiCalls(Context context, InstagramService myService,
                             String instagramUserId, String instagramAccessToken){
        this.myService = myService;
        this.context = context;
        this.instagramAccessToken = instagramAccessToken;
        this.instagramUserId = instagramUserId;
    }

    private boolean doIProceed() {
        if(this.myService == null || this.context == null){
            return false;
        }
        if(StringUtilities.isNullOrEmpty(this.instagramAccessToken)){
            return false;
        }
        if(StringUtilities.isNullOrEmpty(this.instagramUserId)){
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
    public void getRecentMedia(final OnTaskCompleteListener listener, String paginationMaxId){
        if (!doIProceed()) {
            listener.onTaskComplete(InstagramConstants.GENERIC_INSTAGRAM_RESPONSE_STRING,
                    InstagramConstants.TAG_INSTAGRAM_UNKNOWN_ERROR);
            return;
        }

        Call<InstagramDataObject.MediaDataObject> call = myService.
                getRecentMedia(instagramAccessToken, paginationMaxId);
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
                getUserData(instagramUserId, instagramAccessToken);
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



    
}
