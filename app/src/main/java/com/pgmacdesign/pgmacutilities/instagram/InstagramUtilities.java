package com.pgmacdesign.pgmacutilities.instagram;

import com.pgmacdesign.pgmacutilities.utilities.MiscUtilities;
import com.pgmacdesign.pgmacutilities.utilities.StringUtilities;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by pmacdowell on 2017-04-19.
 */

public class InstagramUtilities {


    /**
     * Build the instagram Auth URL
     * @param clientId Client ID
     * @param redirectUri Redirect URI
     * @param responseType return type, pass null to automatically use 'token'
     * @return Full html string to use in a web page
     */
    public static String buildInstagramAuthUrl(String clientId, String redirectUri, String responseType,
                                               InstagramConstants.PermissionScopes[] permsRequested){
        if(StringUtilities.isNullOrEmpty(clientId) || StringUtilities.isNullOrEmpty(redirectUri)){
            return null;
        }
        if(StringUtilities.isNullOrEmpty(responseType)){
            responseType = "token";
        }
        String str = "https://api.instagram.com/oauth/authorize/?client_id="
                + clientId + "&redirect_uri=" + redirectUri
                + "&response_type=" + responseType;
        if(MiscUtilities.isArrayNullOrEmpty(permsRequested)){
            return str;
        } else {
            StringBuilder sb = new StringBuilder();
            for (InstagramConstants.PermissionScopes perm : permsRequested){
                sb.append(perm.toString());
                sb.append("+");
            }
            String str2 = sb.toString();
            if(!StringUtilities.isNullOrEmpty(str2)){
                //Remove last plus sign
                str2 = str2.substring(0, (str2.length() - 1));
            }
            str = str + "&scope=" +str2;
            return str;
        }
    }
    //To obtain token, parse return data from this:
    //http://your-redirect-uri#access_token=ACCESS-TOKEN


    /**
     * Build the instagramData object (sort through for map of String / string and id
     * @param mediaDataObject
     * @return
     */
    public static InstagramDataObject buildInstagramDataObject (
            InstagramDataObject.MediaDataObject mediaDataObject){
        if(mediaDataObject == null){
            return null;
        }

        InstagramDataObject obj = new InstagramDataObject();

        InstagramDataObject.MediaData[] datas = mediaDataObject.getData();
        Map<String, String> instagramMap = new HashMap<>();
        for(InstagramDataObject.MediaData data : datas){
            String id = data.getId();
            String url = null;
            try {
                url = data.getImages().getStandard_resolution().getUrl();
            } catch (NullPointerException e){}
            if(url == null){
                url = data.getLink();
            }
            if(id != null && url != null) {
                instagramMap.put(id, url);
            }
        }

        //Full map of ids and URLs written
        obj.setInstagramPhotoIDUrls(instagramMap);

        //Get last Id
        String lastId = null;
        try {
            lastId = mediaDataObject.getPagination().getNext_max_id();
        } catch (Exception e){}
        obj.setNextMaxId(lastId);

        return obj;

    }

    /**
     * Checks if login was successful by checking if they received back an access token
     * @param url Response from server (on page finished() call)
     * @param redirectUri Redirect uri used
     * @return boolean, true if it was successful (token to parse out), false if not.
     */
    public static boolean wasLoginSuccessful(String url, String redirectUri){
        if(StringUtilities.isNullOrEmpty(url) || StringUtilities.isNullOrEmpty(redirectUri)){
            return false;
        }

        if(!url.startsWith(redirectUri)){
            return false;
        } else {
            if(redirectUri.contains("#access_token=")){
                return true;
            } else {
                return false;
            }
        }
    }

    /**
     * Get the access token for instagram from the passed in url (response) and redirectUri
     * @param url Response from server (on page finished() call)
     * @param redirectUri Redirect uri used
     * @return String of access token. Will be null if it could not parse out
     */
    public static String getLoginAccessToken(String url, String redirectUri){
        if(StringUtilities.isNullOrEmpty(url) || StringUtilities.isNullOrEmpty(redirectUri)){
            return null;
        }
        if(!url.startsWith(redirectUri)){
            return null;
        } else {
            String str = url.replace(redirectUri, "");
            str = str.replace("#access_token=", "");
            str = str.trim();
            if(str.startsWith("/")){
                str.replace("/", "");
                str = str.trim();
            }
            return str;
        }
    }
}
