package com.pgmacdesign.pgmactips.misc;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

/**
 * Created by pmacdowell on 8/12/2016.
 */
public class PGMacTipsConstants {

	///////////////
	//Misc Values//
	///////////////
	
	// TODO: 2018-02-13 refactor this into preferences setup
	public static final String PGMACTIPS_PACKAGE_ID = "com.pgmacdesign.pgmactips";
	public static final String PGMACTIPS_CONTEXT_PROVIDER = "PGMacContextProvider";
	public static final String PGMACTIPS_STRING = "PGMacTips";

    /////////////////
    //Misc Booleans//
    /////////////////

    // TODO: 2018-02-13 refactor this into preferences setup
    public static final boolean ATTEMPT_TO_PARSE_HTML = false;

    ////////////////
    //Misc Strings//
    ////////////////

    public static final String PHONE_URI_TO_WRITE_TO = "/storage/emulated/0/Download/";
    public static final String FILE_NAME = "debugLoggingData.txt";
    public static final String URL_GOOGLE = "https://www.google.com";
    public static final String NO_INTERNET_STRING = "It looks like you do not have a stable internet connection. Please check for connectivity and try again";
    public static final String ARRAY_PAGER_ADAPTER_ERROR_1 = "Error: Null fragment in passed map.";
    public final static String HEX = "0123456789ABCDEF";
    public final static String UTF8 = "UTF-8";
    
    /////////////////////////////////////////////////////////////
    //Custom Tags (There is no specific order to these numbers)//
    /////////////////////////////////////////////////////////////

    //Request codes used for permission requests
    public static final int TAG_PERMISSIONS_ACCESS_NETWORK_STATE = 3398;
    //public static final int TAG_PERMISSIONS_REQUEST_GALLERY = 4399;
    public static final int TAG_PERMISSIONS_REQUEST_BASE_CALL = 3300;
    public static final int TAG_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 3301;
    public static final int TAG_PERMISSIONS_REQUEST_CAMERA = 3302;
    public static final int TAG_PERMISSIONS_REQUEST_ALL = 3303;
    public static final int TAG_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 3304;
    public static final int TAG_PERMISSIONS_REQUEST_READ_PHONE_STATE = 3305;
    public static final int TAG_PERMISSIONS_REQUEST_CONTACTS = 3306;
    public static final int TAG_PERMISSIONS_ACCESS_WIFI_STATE = 3307;
    public static final int TAG_PERMISSIONS_ACCESS_FINE_LOCATION = 3308;
    public static final int TAG_PERMISSIONS_ACCESS_COARSE_LOCATION = 3309;
    public static final int TAG_PERMISSIONS_RECEIVE_BOOT_COMPLETED = 3310;
    public static final int TAG_RETROFIT_PARSE_ERROR = 3311;
    public static final int TAG_RETROFIT_CALL_ERROR = 3312;
    public static final int TAG_TBD2 = 3313;
    public static final int TAG_TBD3 = 3314;
    public static final int TAG_TBD4 = 3315;
    public static final int TAG_TBD5 = 3316;
    public static final int TAG_TBD6 = 3317;
    public static final int TAG_TBD7 = 3318;
    public static final int TAG_TBD8 = 3319;
    public static final int TAG_TBD9 = 3320;

    //File Creation Tags
    public static final int TAG_TXT_FILE_CREATION = 3400;
    //Date Formatting Tags, used for comparison and formatting
    /**
     * Sample: Wed Oct 16 15:21:20 PST 2018
     */
    public static final int DATE_EEEE_MMM_dd_HH_mm_ss_z_yyyy = 4403;
    /**
     * Sample: 2018-10-16
     */
    public static final int DATE_YYYY_MM_DD_T_HH_MM_SS_SSS_Z = 4403;
    public static final int DATE_YYYY_MM_DD_T_HH_MM_SS_Z = 4404;
    public static final int DATE_MM_DD_YYYY = 4405;
    public static final int DATE_MM_DD_YY = 4406;
    public static final int DATE_YYYY_MM_DD = 4407;
    public static final int DATE_MM_DD = 4408;
    public static final int DATE_MM_YY = 4409;
    public static final int DATE_MM_YYYY = 4410;
    public static final int DATE_MILLISECONDS = 4411;
    public static final int DATE_EPOCH = 4412;
    public static final int DATE_MM_DD_YYYY_HH_MM = 4413;

    public static int[] ALL_DATE_TYPES = {DATE_YYYY_MM_DD_T_HH_MM_SS_SSS_Z,
            DATE_YYYY_MM_DD_T_HH_MM_SS_Z, DATE_MM_DD_YYYY, DATE_MM_DD_YY, DATE_YYYY_MM_DD,
            DATE_MM_DD, DATE_MM_YY, DATE_MM_YYYY, DATE_MM_DD_YYYY_HH_MM};

    //More Misc Tags
    public static final int TAG_OAUTH_DATA_OBJECT = 4414;
    public static final int TAG_OAUTH_ERROR = 4415;
    public static final int TAG_PHOTO_BAD_URL = 4416;
    public static final int TAG_FILE_DOWNLOADED = 4417;
    public static final int TAG_DIALOG_POPUP_YES = 4418;
    public static final int TAG_DIALOG_POPUP_NO = 4419;
    public static final int TAG_DIALOG_POPUP_CANCEL = 4420;
    public static final int TAG_PHONE_QUERY_REGEX_FAIL = 4421;
    public static final int TAG_PHONE_QUERY_REGEX_SUCCESS = 4422;
    public static final int TAG_CONTACT_QUERY_EMAIL = 4423;
    public static final int TAG_CONTACT_QUERY_PHONE = 4424;
    public static final int TAG_CONTACT_QUERY_ADDRESS = 4425;
    public static final int TAG_CONTACT_QUERY_NAME = 4426;
    public static final int TAG_TAKE_PICTURE_WITH_CAMERA = 4427;
    public static final int TAG_PHOTO_FROM_GALLERY = 4428;
    public static final int TAG_CROP_PHOTO = 4429;
    public static final int TAG_RETURN_IMAGE_URL = 4430;
    public static final int TAG_TAKE_VIDEO_WITH_RECORDER = 4431;
    public static final int TAG_MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 4432;
    public static final int TAG_MY_PERMISSIONS_REQUEST_CAMERA = 4433;
    public static final int TAG_PHOTO_UNKNOWN_ERROR = 4434;
    public static final int TAG_CROP_ERROR = 4435;
    public static final int TAG_CROP_SUCCESS = 4436;
    public static final int TAG_PHOTO_CANCEL = 4437;
    public static final int TAG_UPLOAD_ERROR = 4438;
    public static final int TAG_UPLOAD_SUCCESS = 4439;
    public static final int TAG_CLICK_NO_TAG_SENT = 4440;
    public static final int TAG_LONG_CLICK_NO_TAG_SENT = 4441;
    public static final int TAG_RETROFIT_CALL_FAILED = 4442;
    public static final int TAG_RETROFIT_CALL_SUCCESS_BOOLEAN = 4443;
    public static final int TAG_RETROFIT_CALL_SUCCESS_STRING = 4444;
    public static final int TAG_RETROFIT_CALL_SUCCESS_OBJECT = 4445;
    public static final int TAG_TAKE_SELF_PHOTO = 4446;
    public static final int TAG_TAKE_SELF_PHOTO_SUCCESS = 4447;
    public static final int TAG_TAKE_SELF_PHOTO_FAILURE = 4448;
    public static final int TAG_FRAGMENT_SWITCHER_ERROR = 4449;
    public static final int TAG_FRAGMENT_SWITCHER_OBJECT = 4450;
    public static final int TAG_FRAGMENT_SWITCHER_NO_OBJECT = 4451;
    public static final int TAG_TIMER_UTILITIES_FINISHED = 4452;
    public static final int TAG_TIMER_UTILITIES_FINISHED_WITH_DATA = 4453;
    public static final int TAG_SMS_RECEIVED_BROADCAST_RECEIVER = 4454;
    public static final int TAG_SMS_RECEIVED_BROADCAST_RECEIVER_EMPTY = 4455;
    public static final int TAG_VIEW_PARAMS_LOADED = 4456;
    public static final int TAG_VIEW_PARAMS_LOADING_FAILED = 4457;
    public static final int TAG_VIEW_FINISHED_DRAWING = 4458;
    public static final int TAG_FCM_SUCCESS_RESPONSE = 4459;
    public static final int TAG_FCM_FAIL_RESPONSE = 4460;
    public static final int TAG_NO_INTERNET = 4461;
    public static final int TAG_BASE64_IMAGE_ENCODE_SUCCESS = 4462;
    public static final int TAG_BASE64_IMAGE_ENCODE_FAIL = 4463;
    public static final int TAG_MULTIPURPOSE_CHOICE_CLICK_ADAPTER = 4464;
    public static final int TAG_MULTIPURPOSE_CHOICE_LONG_CLICK_ADAPTER = 4465;
	public static final int TAG_MAP_STRING_INTEGER = 4466;
	public static final int TAG_STRING = 4467;
	public static final int TAG_SIMPLE_TEXT_ICON_ADAPTER_CLICK = 4468;
	public static final int TAG_GENERIC_FILTER_RESULTS = 4469;
	public static final int TAG_CONTACT_QUERY_PROGRESS_UPDATE = 4470;
    public static final int TAG_CONTACT_QUERY_NO_RESULTS = 4471;
    public static final int TAG_CONTACT_QUERY_MISSING_CONTACT_PERMISSION = 4472;
    public static final int TAG_CONTACT_QUERY_UNKNOWN_ERROR = 4473;
    public static final int TAG_CONTACT_QUERY_ALL_MERGED_RESULTS = 4474;
    public static final int TAG_INVALID_BASE_64_IMAGE = 4475;
    public static final int TAG_GOOGLE_VISION_SUCCESS_RESULT = 4476;
    public static final int TAG_GOOGLE_VISION_FAIL_RESULT = 4477;
    public static final int TAG_GOOGLE_VISION_UNKNOWN_ERROR = 4478;
    public static final int TAG_INVALID_BITMAP_IMAGE = 4479;
    public static final int TAG_BITMAP_BASE64_CONVERSION_FAIL = 4480;
    public static final int TAG_LIFE_CYCLE_LISTENER = 4481;
    public static final int TBD1 = 4482;
    public static final int TBD2 = 4483;
    public static final int TBD3 = 4484;

    //String Tags
    public static final String TAG_SELF_PHOTO_URI = "tag_self_photo_uri";

    ///////////////////////////////////////////
    //Database / Shared Preferences Constants//
    ///////////////////////////////////////////

    public static final String DB_NAME = "PGMacTips.DB";
    public static final boolean DELETE_DB_IF_NEEDED = true;
    public static final int DB_VERSION = 1;
    public static final String SHARED_PREFS_NAME = "PGMacTips.SP";

    ////////////////
    //Time Values //
    ////////////////

    //Time values in milliseconds
    public static final long ONE_SECOND = (long)(1000L);
    public static final long ONE_MINUTE = (long)(1000L*60L);
    public static final long ONE_HOUR = (long)(1000L*60L*60L);
    public static final long ONE_DAY = (long)(1000L*60L*60L*24L);
    public static final long ONE_WEEK = (long)(1000L*60L*60L*24L*7L);
    public static final long ONE_MONTH = (long)(1000L*60L*60L*24L*30L);
    public static final long ONE_YEAR = (long)(1000L*60L*60L*24L*365L);

    //Size Values
    public static final long ONE_BYTE = 1L;
    public static final long ONE_KILOBYTE = 1024L;
    public static final long ONE_MEGABYTE = 1024L * 1024L;
    public static final long ONE_GIGABYTE = 1024L * 1024L * 1024L;
    public static final long ONE_TERABYTE = 1024L * 1024L * 1024L * 1024L;
    public static final long ONE_PETABYTE = 1024L * 1024L * 1024L * 1024L * 1024L;
    public static final long ONE_EXABYTE = 1024L * 1024L * 1024L * 1024L * 1024L * 1024L;
//    public static final long ONE_ZETTABYTE = 1024L * 1024L * 1024L * 1024L * 1024L * 1024L * 1024L;
//    public static final long ONE_YOTTABYTE = 1024L * 1024L * 1024L * 1024L * 1024L * 1024L * 1024L * 1024L;


    //Default Date Formats
    public static final String DEFAULT_ISO_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    public static final String DEFAULT_ISO_FORMAT_WITHOUT_MILLISECONDS = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    //This is the string format received when calling Date date = new Date().toString(); //<---
    public static final String DEFAULT_JAVA_DATE_FORMAT = "EEEE MMM dd HH:mm:ss z yyyy";
    public static final String DATE_STRING_PATTERN_24_HOUR_TIME = "HH:mm";
    public static final String DATE_STRING_PATTERN_12_HOUR_TIME = "hh:mm a";

    //////////
    //Colors//
    //////////

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

    /////////////
    //Raw Types//
    /////////////

    public static final Type TYPE_BOOLEAN = Boolean.TYPE;
    public static final Type TYPE_DOUBLE = Double.TYPE;
    public static final Type TYPE_INTEGER = Integer.TYPE;
    public static final Type TYPE_STRING = new TypeToken<String>(){}.getType();

    ///////////////////////
    //Regular Expressions//
    ///////////////////////

    //Credit Card Regular Expressions
    public static final String REGEX_CREDIT_CARD_VISA = "^4[0-9]{12}(?:[0-9]{3})?$";
    public static final String REGEX_CREDIT_CARD_MASTERCARD = "^5[1-5][0-9]{14}$";
    public static final String REGEX_CREDIT_CARD_AMERICAN_EXPRESS = "^3[47][0-9]{13}$";
    public static final String REGEX_CREDIT_CARD_DINERS_CLUB = "^3(?:0[0-5]|[68][0-9])[0-9]{11}$";
    public static final String REGEX_CREDIT_CARD_DISCOVER = "^6(?:011|5[0-9]{2})[0-9]{12}$";
    public static final String REGEX_CREDIT_CARD_JCB = "^(?:2131|1800|35\\d{3})\\d{11}$";
    public static final String REGEX_CREDIT_CARD_UNKNOWN = "^unknown$";

    //Misc Regexs
    public static final String REGEX_WEB_URL_ENCODING = "\\b(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
    public static final String REGEX_HTML = "<(\\\"[^\\\"]*\\\"|'[^']*'|[^'\\\">])*>";
    public static final String REGEX_HTML2 = "<html>";
    public static final String REGEX_PASSWORD_PATTERN = "^\\S*(?=\\S*[a-zA-Z])(?=\\S*[0-9])\\S*$";
    public static final String REGEX_INTEGER = "^[0-9]+$";
    public static final String REGEX_DECIMAL = "^[0-9]+(?:\\.[0-9]+)?$";
    public static final String REGEX_MONEY = "^[0-9]+(?:\\.[0-9]{0,2})?$";
    public static final String REGEX_MONEY_SIGNED = "^[-+]?[0-9]+(?:\\.[0-9]{0,2})?$";
    public static final String REGEX_PHONE = "^ *\\(?[0-9]{3,3}[-\\.\\)]? ?[0-9]{3,3}[-\\. ]?[0-9]{4,4} *$";
    public static final String REGEX_COLOR = "^#[0-9a-fA-F]{6,6}$";
    public static final String REGEX_DATE = "^[0-9]{1,2}[-/][0-9]{1,2}[-/][0-9]{2,4}$";
    public static final String REGEX_EMAIL = "^\\s*[^@\\s]+@(?:[^@\\.\\s]+\\.[^@\\.\\s]+)+\\s*$";
    public static final String REGEX_NEW_LINE = "[\\r\\n]+";
//    public static final String REGEX_IPADDRESS = "^[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}$";
    //Updated as per: https://www.mkyong.com/regular-expressions/how-to-validate-ip-address-with-regular-expression/
    public static final String REGEX_IPADDRESS = "^([01]?\\\\d\\\\d?|2[0-4]\\\\d|25[0-5])\\\\.([01]?\\\\d\\\\d?|2[0-4]\\\\d|25[0-5])\\\\.\n" +
            "([01]?\\\\d\\\\d?|2[0-4]\\\\d|25[0-5])\\\\.([01]?\\\\d\\\\d?|2[0-4]\\\\d|25[0-5])$\n";

    ////////////////////////////////
    //Animation Constants and Tags//
    ////////////////////////////////

    /**
     * Animation Techniques (From the library com.daimajia.androidanimations:library:1.1.3@aar).
     * In means it brings them into sight while out takes them out of sight
     *
     * Note, removing these for now as they are causing an exception to be thrown if the end developer
     * does not include the animations library
     *
     *

    public static final Techniques IN_ZOOM_UP = Techniques.ZoomInUp; //Fun one, opposite of ZoomOutDown, zooms it up and then in
    public static final Techniques OUT_ZOOM_DOWN = Techniques.ZoomOutDown; //Fun one, zooms away (back) and then down, useful for getting rid of things
    public static final Techniques IN_ROLL = Techniques.RollIn;//Fly in effect looks nice for new views popping in
    public static final Techniques IN_PULSE = Techniques.Pulse; //Pops the view front and back, good for a focuser
    public static final Techniques OUT_HINGE = Techniques.Hinge; //Looks like broken hinge on view and it falls off, fun looking
    public static final Techniques IN_RUBBERBAND = Techniques.RubberBand; //Good for a 'de-select' kind of effect
    public static final Techniques OUT_FLIP_Y = Techniques.FlipOutY; //Clean looking flip out on y Axis, good for deletion/ removal
    public static final Techniques IN_FLIP_X = Techniques.FlipInX;  //Clean flip look. Looks like it's rotating on the X axis in
    public static final Techniques OUT_FLIP_X = Techniques.FlipOutX; //Opposite of FlipInX, good for deletetion / removal
    public static final Techniques OUT_SLIDE = Techniques.SlideOutUp; //Using SlideOutUp in conjunction with SlideInUp would look kinda cool...
    public static final Techniques IN_RIGHT_SLIDE = Techniques.SlideInRight; //Using SlideOutUp in conjunction with SlideInUp would look kinda cool...
    public static final Techniques IN_LEFT_SLIDE = Techniques.SlideInLeft; //Using SlideOutUp in conjunction with SlideInUp would look kinda cool...
    public static final Techniques IN_SLIDE = Techniques.SlideInUp; //Using SlideOutUp in conjunction with SlideInUp would look kinda cool...
    public static final Techniques IN_FADE_DOWN = Techniques.FadeInDown; //Simple fade in and down animation
    public static final Techniques IN_FADE_UP = Techniques.FadeInUp; //Simple fade in and up animation
    public static final Techniques IN_DROP = Techniques.DropOut; //Looks cool, falls down from top and bounces
    public static final Techniques IN_TADA = Techniques.Tada; //Fun one, seems useful for focusing on a view
    public static final Techniques OUT_ROLL = Techniques.RollOut;
    public static final Techniques OUT_ZOOM = Techniques.ZoomOut;
    public static final Techniques IN_FLASH = Techniques.Flash; //Quick flash

    */

    ////////////////////////////////
    //region Known Malware Package Names //
    ////////////////////////////////

    public static final String[] MALWARE_PACKAGE_STRINGS = {
            "cn.etouch.ecalendar.life",
            "com.aimobo.weatherclear",
            "com.ali.money.shield",
            "com.anti.block.porn.safebrowser",
            "com.app.fast.boost.cleaner",
            "com.app.wifi.recovery.master",
            "com.baiwang.facesnap",
            "com.block.puzzle.game.king",
            "com.booster.ram.app.master.clean",
            "com.card.game.bl.plugintheme21",
            "com.card.game.bl.plugintheme22",
            "com.card.game.bl.plugintheme23",
            "com.cardgame.solitaire.sfour",
            "com.clean.phone.boost.android.junk.cleaner",
            "com.cleaner.booster.speed.junk.memory",
            "com.color.paper.style",
            "com.corous360.zipay",
            "com.desk.paper.watch",
            "com.exact.digital.ledcompass",
            "com.free.sudoku.puzzle",
            "com.freegames.happy.popcandy",
            "com.freegames.popstar",
            "com.freegames.popstar.exterme",
            "com.gmiles.alarmclock",
            "com.gmiles.switcher",
            "com.insta.browser",
            "com.listen.music.pedometer",
            "com.ljapps.wifix.recovery.password",
            "com.mg.callrecord",
            "com.mola.tools.mbattery",
            "com.mola.tools.openweather",
            "com.mx.cool.videoplayer",
            "com.news.boost.clean",
            "com.ojhero.nowcall",
            "com.phonecooler.battery.cleaner.wifimaster",
            "com.picture.photo.editor",
            "com.powercleaner",
            "com.red.music.audio.player",
            "com.riti.elocation.driver",
            "com.samll.game.puzzle.plus",
            "com.smartx.flashlight",
            "com.tool.powercleanlite",
            "com.tool.videomanager",
            "com.tools.freereminder",
            "com.wise.trackme.activity",
            "org.mbj.filemanager",
            "org.mbj.sticker"
    };

    //endregion

    //////////////////////
    //Misc Useful Values//
    //////////////////////

    /**
     * List of States for dropdown menus or other GUI-related uses
     */
    public static final String[] ALL_US_STATES = {"California", "Alabama", "Arkansas", "Arizona", "Alaska",
            "Colorado", "Connecticut", "Delaware", "Florida", "Georgia", "Hawaii", "Idaho", "Illinois",
            "Indiana", "Iowa", "Kansas", "Kentucky", "Louisiana", "Maine", "Maryland", "Massachusetts",
            "Michigan", "Minnesota", "Mississippi", "Missouri", "Montana", "Nebraska", "Nevada", "New Hampshire",
            "New Jersey", "New Mexico", "New York", "North Carolina", "North Dakota", "Ohio", "Oklahoma", "Oregon",
            "Pennsylvania", "Rhode Island", "South Carolina", "South Dakota", "Tennessee", "Texas", "Utah", "Vermont",
            "Virginia", "Washington", "Washington DC", "West Virginia", "Wisconsin", "Wyoming"
    };

    /**
     * USA Territories. List obtained from:
     *   https://www.50states.com/abbreviations.htm
     */
    public static enum USATerritories {
        Alabama("Alabama", "AL"),
        Alaska("Alaska", "AK"),
        Arizona("Arizona", "AZ"),
        Arkansas("Arkansas", "AR"),
        California("California", "CA"),
        Colorado("Colorado", "CO"),
        Connecticut("Connecticut", "CT"),
        Delaware("Delaware", "DE"),
        Florida("Florida", "FL"),
        Georgia("Georgia", "GA"),
        Hawaii("Hawaii", "HI"),
        Idaho("Idaho", "ID"),
        Illinois("Illinois", "IL"),
        Indiana("Indiana", "IN"),
        Iowa("Iowa", "IA"),
        Kansas("Kansas", "KS"),
        Kentucky("Kentucky", "KY"),
        Louisiana("Louisiana", "LA"),
        Maine("Maine", "ME"),
        Maryland("Maryland", "MD"),
        Massachusetts("Massachusetts", "MA"),
        Michigan("Michigan", "MI"),
        Minnesota("Minnesota", "MN"),
        Mississippi("Mississippi", "MS"),
        Missouri("Missouri", "MO"),
        Montana("Montana", "MT"),
        Nebraska("Nebraska", "NE"),
        Nevada("Nevada", "NV"),
        New_Hampshire("New Hampshire", "NH"),
        New_Jersey("New Jersey", "NJ"),
        New_Mexico("New Mexico", "NM"),
        New_York("New York", "NY"),
        North_Carolina("North Carolina", "NC"),
        North_Dakota("North Dakota", "ND"),
        Ohio("Ohio", "OH"),
        Oklahoma("Oklahoma", "OK"),
        Oregon("Oregon", "OR"),
        Pennsylvania("Pennsylvania", "PA"),
        Rhode_Island("Rhode Island", "RI"),
        South_Carolina("South Carolina", "SC"),
        South_Dakota("South Dakota", "SD"),
        Tennessee("Tennessee", "TN"),
        Texas("Texas", "TX"),
        Utah("Utah", "UT"),
        Vermont("Vermont", "VT"),
        Virginia("Virginia", "VA"),
        Washington("Washington", "WA"),
        West_Virginia("West Virginia", "WV"),
        Wisconsin("Wisconsin", "WI"),
        Wyoming("Wyoming", "WY"),
        //Commonwealth / Territories
        American_Samoa("American Samoa", "AS"),
        District_of_Columbia("District of Columbia", "DC"),
        Federated_States_of_Micronesia("Federated States of Micronesia", "FM"),
        Guam("Guam", "GU"),
        Marshall_Islands("Marshall Islands", "MH"),
        Northern_Mariana_Islands("Northern Mariana Islands", "MP"),
        Palau("Palau", "PW"),
        Puerto_Rico("Puerto Rico", "PR"),
        Virgin_Islands("Virgin Islands", "VI"),
        //Military State
        Armed_Forces_Africa("Armed Forces Africa", "AE"),
        Armed_Forces_Americas("Armed Forces Americas", "AA"),
        Armed_Forces_Canada("Armed Forces Canada", "AE"),
        Armed_Forces_Europe("Armed Forces Europe", "AE"),
        Armed_Forces_Middle_East("Armed Forces Middle East", "AE"),
        Armed_Forces_Pacific("Armed Forces Pacific", "AP"),
        //Unknown
        Unknown("Unknown", "N/A");

        String name;
        String abbreviation;

        USATerritories(String name, String abbreviation){
            this.name = name;
            this.abbreviation = abbreviation;
        }

        public String getName(){
            return this.name;
        }

        public String getAbbreviation(){
            return abbreviation;
        }

        public static String getName(USATerritories state){
            return state.name;
        }

        public static String getAbbreviation(USATerritories state){
            return state.abbreviation;
        }

        public static USATerritories parseName(String name){
            if(name == null){
                return USATerritories.Unknown;
            }
            if(name.isEmpty()){
                return USATerritories.Unknown;
            }
            name = name.trim();
            for(USATerritories e : USATerritories.values()){
                String stateName = e.getName();
                if(name.equalsIgnoreCase(stateName)){
                    return e;
                }
                stateName = stateName.replace(" ", "");
                if(name.equalsIgnoreCase(stateName)){
                    return e;
                }
                stateName = e.toString(); //For parsing underscore name like New_York
                if(name.equalsIgnoreCase(stateName)){
                    return e;
                }
            }
            return USATerritories.Unknown;
        }

        public static USATerritories parseAbbreviation(String abbreviation){
            if(abbreviation == null){
                return USATerritories.Unknown;
            }
            if(abbreviation.isEmpty()){
                return USATerritories.Unknown;
            }
            abbreviation = abbreviation.trim();
            for(USATerritories e : USATerritories.values()){
                String stateAbbreviation = e.getAbbreviation();
                if(abbreviation.equalsIgnoreCase(stateAbbreviation)){
                    return e;
                }
                stateAbbreviation = stateAbbreviation.replace(" ", "");
                if(abbreviation.equalsIgnoreCase(stateAbbreviation)){
                    return e;
                }
            }
            return USATerritories.Unknown;
        }
    }

    //region Misc
    //Commenting this out, but leaving is here for reference in case someone cannot find it after the AndroidX update
//    private static final int SNACKBAR_ID_USING_ANDROID_X_SUPPORT_LIB =
//            com.google.android.material.R.id.snackbar_text;
    //endregion
}
