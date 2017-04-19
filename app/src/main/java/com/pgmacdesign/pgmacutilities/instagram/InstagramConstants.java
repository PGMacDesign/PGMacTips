package com.pgmacdesign.pgmacutilities.instagram;

/**
 * From Docs:
 * https://www.instagram.com/developer/authorization/
 * Created by pmacdowell on 2017-04-19.
 */
public class InstagramConstants {

    public static final String GENERIC_INSTAGRAM_RESPONSE_STRING =
            "Unknown error, please try again later";
    public static final String INSTAGRAM_BASE_URL =
            "https://api.instagram.com/";
    public static final String INSTAGRAM_AUTH_URL =
            "https://api.instagram.com/oauth/authorize/?client_id="
                    + "CLIENT-ID " + "&redirect_uri=" + "REDIRECT-URI"
                    + "&response_type=token";

    public static enum PermissionScopes {
        basic, public_content, follower_list,
        comments, relationships, likes
    }

    public static final int TAG_INSTAGRAM_UNKNOWN_ERROR = 4122;
    public static final int TAG_INSTAGRAM_USERDATAOBJECT = 4123;
    public static final int TAG_INSTAGRAM_MEDIADATAOBJECT = 4124;
    public static final int TAG_INSTAGRAM_ERROR_STRING = 4125;
}
