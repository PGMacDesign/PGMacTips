package com.pgmacdesign.pgmacutilities;

import android.content.Context;
import android.support.annotation.NonNull;

import com.cloudrail.si.interfaces.Profile;
import com.cloudrail.si.services.Facebook;
import com.cloudrail.si.services.GooglePlus;
import com.cloudrail.si.services.Instagram;
import com.cloudrail.si.services.Twitter;
import com.pgmacdesign.pgmacutilities.nonutilities.PGMacUtilitiesConstants;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Class for dealing with OAuth logins. It uses the Cloudrail library. Link to cloudrail below:
 * https://github.com/CloudRail/cloudrail-si-android-sdk
 * compile 'com.cloudrail:cloudrail-si-android:2.6.7' (<-- As of 2016-08-12, on version 2.6.7)
 * Created by pmacdowell on 8/12/2016.
 */
public class OAuthUtilities {

    private static final String ERROR_STRING =
            "An error occurred while trying to process your login request";

    private OAuthObject authObject;
    private Context context;
    private OnTaskCompleteListener listener;

    public OAuthUtilities(Context context, @NonNull OnTaskCompleteListener listener){
        this.listener = listener;
        this.context = context;
    }

    private void getFacebookOAuthCloudRail(String clientId, String clientSecret){

        final Profile profile = new Facebook(context, clientId, clientSecret);
        new Thread() {
            @Override
            public void run() {
                try {
                    String fullName = profile.getFullName();
                    String email = profile.getEmail();
                    String id = profile.getIdentifier();
                    authObject = new OAuthObject();
                    authObject.setNetworkType(socialNetworks.FACEBOOK);
                    listener.onTaskComplete(authObject,
                            PGMacUtilitiesConstants.TAG_OAUTH_DATA_OBJECT);
                } catch (Exception e2){
                    listener.onTaskComplete(ERROR_STRING,
                            PGMacUtilitiesConstants.TAG_OAUTH_ERROR);
                    e2.printStackTrace();
                }
            }
        }.start();
    }

    /**
     * Run a web call to get the twitter OAuth Logins. Make sure to follow the steps listed
     * on the Cloudrail wiki if you encounter any problems
     * @param clientId The client ID
     * @param clientSecret The client Secret
     */
    public void getTwitterOAuthCloudRail(String clientId, String clientSecret){
        final Profile profile = new Twitter(context, clientId, clientSecret);
        new Thread() {
            @Override
            public void run() {
                try {
                    String id = profile.getIdentifier();
                    String info = profile.saveAsString();
                    String name = profile.getFullName();
                    String imageUrl = profile.getPictureURL();

                    JSONArray jsonArray = new JSONArray(info);
                    JSONObject obj = jsonArray.getJSONObject(0);

                    String oauthToken = null, oauthTokenSecret = null, refreshToken = null;
                    try {
                        oauthToken = obj.getString("oauthToken");
                        refreshToken = obj.getString("refreshToken");
                        oauthTokenSecret = obj.getString("oauthTokenSecret");
                    } catch (Exception e){}

                    authObject = new OAuthObject();
                    authObject.setUserId(id);
                    authObject.setAccessToken(oauthToken);
                    authObject.setName(name);
                    authObject.setRefreshToken(refreshToken);
                    authObject.setImageUrl(imageUrl);
                    authObject.setNetworkType(socialNetworks.TWITTER);

                    listener.onTaskComplete(authObject,
                            PGMacUtilitiesConstants.TAG_OAUTH_DATA_OBJECT);
                } catch (Exception e2){
                    listener.onTaskComplete(ERROR_STRING,
                            PGMacUtilitiesConstants.TAG_OAUTH_ERROR);
                    e2.printStackTrace();
                }

            }
        }.start();
    }

    /**
     * Run a web call to get the Google OAuth Logins. Make sure to follow the steps listed
     * on the Cloudrail wiki if you encounter any problems
     * @param clientId The client ID
     * @param clientSecret The client Secret
     */
    public void getGoogleOAuthCloudRail(String clientId, String clientSecret){
        final Profile profile = new GooglePlus(context, clientId, clientSecret);
        new Thread() {
            @Override
            public void run() {
                try {
                    String id = profile.getIdentifier();
                    String info = profile.saveAsString();
                    String email = profile.getEmail();
                    String name = profile.getFullName();
                    String imageUrl = profile.getPictureURL();
                    try {
                        id = id.replace("googleplus-", "");
                    } catch (NullPointerException e){} //May be null

                    JSONArray jsonArray = new JSONArray(info);
                    JSONObject obj = jsonArray.getJSONObject(0);

                    String accessToken = null, refreshToken = null;
                    try {
                        accessToken = obj.getString("accessToken");
                        refreshToken = obj.getString("refreshToken");
                    } catch (Exception e){}

                    authObject = new OAuthObject();
                    authObject.setUserId(id);
                    authObject.setAccessToken(accessToken);
                    authObject.setAccessToken(refreshToken);
                    authObject.setName(name);
                    authObject.setEmail(email);
                    authObject.setImageUrl(imageUrl);

                    listener.onTaskComplete(authObject,
                            PGMacUtilitiesConstants.TAG_OAUTH_DATA_OBJECT);
                } catch (Exception e2){
                    listener.onTaskComplete(ERROR_STRING,
                            PGMacUtilitiesConstants.TAG_OAUTH_ERROR);
                    e2.printStackTrace();
                }

            }
        }.start();
    }

    /**
     * Run a web call to get the Instagram OAuth Logins. Make sure to follow the steps listed
     * on the Cloudrail wiki if you encounter any problems
     * @param clientId The client ID
     * @param clientSecret The client Secret
     */
    public void getInstagramOAuthCloudRail(String clientId, String clientSecret){
        final Profile profile = new Instagram(context, clientId, clientSecret);
        new Thread() {
            @Override
            public void run() {
                try {
                    String id = profile.getIdentifier();
                    String info = profile.saveAsString();
                    try {
                        id = id.replace("INSTAGRAM-", "");
                    } catch (NullPointerException e){} //May be null

                    JSONArray jsonArray = new JSONArray(info);
                    JSONObject obj = jsonArray.getJSONObject(0);

                    String accessToken = null, refreshToken = null;
                    try {
                        accessToken = obj.getString("accessToken");
                        refreshToken = obj.getString("refreshToken");
                    } catch (Exception e){}

                    authObject = new OAuthObject();
                    authObject.setUserId(id);
                    authObject.setAccessToken(accessToken);
                    authObject.setAccessToken(refreshToken);
                    authObject.setNetworkType(socialNetworks.INSTAGRAM);

                    listener.onTaskComplete(authObject,
                            PGMacUtilitiesConstants.TAG_OAUTH_DATA_OBJECT);
                } catch (Exception e2){
                    listener.onTaskComplete(ERROR_STRING,
                            PGMacUtilitiesConstants.TAG_OAUTH_ERROR);
                    e2.printStackTrace();
                }
            }
        }.start();
    }

    /**
     * Enum for social network types
     */
    public enum socialNetworks {
        FACEBOOK, TWITTER, GOOGLEPLUS, INSTAGRAM, OTHER
    }

    /**
     * OAuth object that contains data received from the OAuth client logins
     */
    public class OAuthObject {
        private String userId;
        private String accessToken;
        private String refreshToken;
        private String imageUrl;
        private String name;
        private String email;
        private String networkType;

        public String getNetworkType(){
            return this.networkType;
        }

        public void setNetworkType(socialNetworks whichNetwork){
            switch (whichNetwork){
                case FACEBOOK:
                    this.networkType = "Facebook";
                    break;
                case TWITTER:
                    this.networkType = "Twitter";
                    break;
                case GOOGLEPLUS:
                    this.networkType = "GooglePlus";
                    break;
                case INSTAGRAM:
                    this.networkType = "Instagram";
                    break;
                default:
                    this.networkType = "Other";
                    break;
            }
        }
        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getRefreshToken() {
            return refreshToken;
        }

        public void setRefreshToken(String refreshToken) {
            this.refreshToken = refreshToken;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getAccessToken() {
            return accessToken;
        }

        public void setAccessToken(String accessToken) {
            this.accessToken = accessToken;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
