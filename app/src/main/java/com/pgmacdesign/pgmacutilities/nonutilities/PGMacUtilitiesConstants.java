package com.pgmacdesign.pgmacutilities.nonutilities;

/**
 * Created by pmacdowell on 8/12/2016.
 */
public class PGMacUtilitiesConstants {

    ////////////////////////////////////////////////////////////////////////////////////////////////
    /////Misc Strings///////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static final String WEB_URL_ENCODING = "\\b(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
    public static final String PASSWORD_PATTERN = "^\\S*(?=\\S*[a-zA-Z])(?=\\S*[0-9])\\S*$";
    public static final String PHONE_URI_TO_WRITE_TO = "/storage/emulated/0/Download/";
    public static final String FILE_NAME = "debugLoggingData.txt";
    public static final String URL_GOOGLE = "https://www.google.com";


    ////////////////////////////////////////////////////////////////////////////////////////////////
    /////Custom Tags (There is no specific order to these numbers)//////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    //Request codes used for permission requests
    public static final int TAG_PERMISSIONS_REQUEST_CODE_INT = 4400;
    public static final int TAG_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 4401;
    public static final int TAG_PERMISSIONS_REQUEST_CAMERA = 4402;
    public static final int TAG_PERMISSIONS_REQUEST_ALL = 4403;
    //File Creation Tags
    public static final int TAG_TXT_FILE_CREATION = 4404;
    //Date Formatting Tags, used for comparison and formatting
    public static final int DATE_MM_DD_YYYY = 4405;
    public static final int DATE_MM_DD_YY = 4406;
    public static final int DATE_MM_DD_YYYY_AMERICANS_NEED_TO_LEARN_TO_FORMAT_CORRECTLY
            = 4405; //AKA DATE_STUPID_AMERICAN_WAY
    public static final int DATE_MM_DD_YY_AMERICANS_NEED_TO_LEARN_TO_FORMAT_CORRECTLY
            = 4406; //AKA DATE_STUPID_AMERICAN_WAY
    public static final int DATE_YYYY_MM_DD = 4407;
    public static final int DATE_MM_DD = 4408;
    public static final int DATE_MM_YY = 4409;
    public static final int DATE_MM_YYYY = 4410;
    public static final int DATE_MILLISECONDS = 4411;
    public static final int DATE_EPOCH = 4412;
    public static final int DATE_MM_DD_YYYY_HH_MM = 4413;
    //More Misc Tags
    public static final int TAG_OAUTH_DATA_OBJECT = 4414;
    public static final int TAG_OAUTH_ERROR = 4415;
    public static final int TAG_PHOTO_BAD_URL = 4416;
    public static final int TAG_FILE_DOWNLOADED = 4417;
    public static final int TAG_OAUTH_ERROR11 = 4418;
    public static final int TAG_OAUTH_ERROR111 = 4419;
    public static final int TAG_OAUTH_ERROR1111 = 4420;



    ////////////////////////////////////////////////////////////////////////////////////////////////
    /////Time Values ///////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    //Time values in milliseconds
    public static final long ONE_SECOND = (1000);
    public static final long ONE_MINUTE = (1000*60);
    public static final long ONE_HOUR = (1000*60*60);
    public static final long ONE_DAY = (1000*60*60*24);
    public static final long ONE_WEEK = (1000*60*60*24*7);
    public static final long ONE_MONTH = (1000*60*60*24*30);
    public static final long ONE_YEAR = (1000*60*60*24*365);

    ////////////////////////////////////////////////////////////////////////////////////////////////
    /////Colors/////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    //Color Strings
    public static final String COLOR_TRANSPARENT = "#00000000";
    public static final String COLOR_WHITE = "#FFFFFF";
    public static final String COLOR_BLACK = "#000000";
    public static final String COLOR_YELLOW = "#FFFF00";
    public static final String COLOR_FUCHSIA = "#FF00FF";
    public static final String COLOR_RED = "#FF0000";
    public static final String COLOR_SILVER = "#C0C0C0";
    public static final String COLOR_GRAY = "#808080";
    public static final String COLOR_LIGHT_GRAY = "#D3D3D3";
    public static final String COLOR_DARK_GRAY = "#666666";
    public static final String COLOR_OLIVE = "#808000";
    public static final String COLOR_PURPLE = "#800080";
    public static final String COLOR_MAROON = "#800000";
    public static final String COLOR_AQUA = "#00FFFF";
    public static final String COLOR_LIME = "#00FF00";
    public static final String COLOR_TEAL = "#008080";
    public static final String COLOR_GREEN = "#008000";
    public static final String COLOR_PINK = "#FFC0CB";
    public static final String COLOR_BLUE = "#0000FF";
    public static final String COLOR_NAVY_BLUE = "#000080";
    //For the Semi transparent colors, the higher the number, the darker (less opaque) the color
    public static final String COLOR_SEMI_TRANSPARENT_1 = "#20111111";
    public static final String COLOR_SEMI_TRANSPARENT_2 = "#30111111";
    public static final String COLOR_SEMI_TRANSPARENT_3 = "#40111111";
    public static final String COLOR_SEMI_TRANSPARENT_4 = "#50111111";
    public static final String COLOR_SEMI_TRANSPARENT_5 = "#69111111";
    public static final String COLOR_SEMI_TRANSPARENT_6 = "#79111111";
    public static final String COLOR_SEMI_TRANSPARENT_7 = "#89111111";
    public static final String COLOR_SEMI_TRANSPARENT_8 = "#99111111";
    public static final String COLOR_SEMI_TRANSPARENT_9 = "#A9111111";
}
