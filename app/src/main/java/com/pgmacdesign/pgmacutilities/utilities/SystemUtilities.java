package com.pgmacdesign.pgmacutilities.utilities;

import android.os.Build;

/**
 * Created by pmacdowell on 8/12/2016.
 */
public class SystemUtilities {

    /**
     * Build versions. For use to get more info that what is just in the source code
     * {@link android.os.Build.VERSION_CODES}
     */
    public static enum BuildVersions {
        BASE(1, "BASE", ""),
        BASE_1_1(2, "BASE_1_1", ""),
        CUPCAKE(3, "CUPCAKE", ""),
        DONUT(4, "DONUT", ""),
        ECLAIR(5, "ECLAIR", ""),
        ECLAIR_0_1(6, "ECLAIR_0_1", ""),
        ECLAIR_MR1(7, "ECLAIR_MR1", ""),
        FROYO(8, "FROYO", ""),
        GINGERBREAD(9, "GINGERBREAD", ""),
        GINGERBREAD_MR1(10, "GINGERBREAD_MR1", ""),
        HONEYCOMB(11, "HONEYCOMB", ""),
        HONEYCOMB_MR1(12, "HONEYCOMB_MR1", ""),
        HONEYCOMB_MR2(13, "HONEYCOMB_MR2", ""),
        ICE_CREAM_SANDWICH(14, "ICE_CREAM_SANDWICH", ""),
        ICE_CREAM_SANDWICH_MR1(15, "ICE_CREAM_SANDWICH_MR1", ""),
        JELLY_BEAN(16, "JELLY_BEAN", ""),
        JELLY_BEAN_MR1(17, "JELLY_BEAN_MR1", ""),
        JELLY_BEAN_MR2(18, "JELLY_BEAN_MR2", ""),
        KITKAT(19, "KITKAT", ""),
        KITKAT_WATCH(20, "KITKAT_WATCH", ""),
        LOLLIPOP(21, "LOLLIPOP", ""),
        LOLLIPOP_MR1(22, "LOLLIPOP_MR1", ""),
        M(23, "M", "MarshMallow"),
        N(24, "N", "Nougat"),
        CUR_DEVELOPMENT(10000, "NA", "NA");

        //The build name within the source code itself
        private String buildName;
        //This is for when the source code is not up to date on names. IE, M is Marshmallow
        private String buildNameAKA;
        //The API Version
        private int buildVersion;

        BuildVersions(int version, String buildName, String buildNameAKA){
            this.buildNameAKA = buildNameAKA;
            this.buildName = buildName;
            this.buildVersion = version;
        }

        public String getBuildName() {
            return buildName;
        }

        public String getBuildNameAKA() {
            return buildNameAKA;
        }

        public int getBuildVersion() {
            return buildVersion;
        }

        /**
         * For a quick check as to which version it is. Pass in the API level and it will spit
         * back the String name of it.
         * @param version int version. Should match {@link android.os.Build.VERSION_CODES}
         * @return
         */
        public String whichBuildIsThis(int version){
            switch (version){
                case Build.VERSION_CODES.BASE:
                    return BASE.getBuildName();
                case Build.VERSION_CODES.BASE_1_1:
                    return BASE_1_1.getBuildName();
                case Build.VERSION_CODES.CUPCAKE:
                    return CUPCAKE.getBuildName();
                case Build.VERSION_CODES.DONUT:
                    return DONUT.getBuildName();
                case Build.VERSION_CODES.ECLAIR:
                    return ECLAIR.getBuildName();
                case Build.VERSION_CODES.ECLAIR_0_1:
                    return ECLAIR_0_1.getBuildName();
                case Build.VERSION_CODES.ECLAIR_MR1:
                    return ECLAIR_MR1.getBuildName();
                case Build.VERSION_CODES.FROYO:
                    return FROYO.getBuildName();
                case Build.VERSION_CODES.GINGERBREAD:
                    return GINGERBREAD.getBuildName();
                case Build.VERSION_CODES.GINGERBREAD_MR1:
                    return GINGERBREAD_MR1.getBuildName();
                case Build.VERSION_CODES.HONEYCOMB:
                    return HONEYCOMB.getBuildName();
                case Build.VERSION_CODES.HONEYCOMB_MR1:
                    return HONEYCOMB_MR1.getBuildName();
                case Build.VERSION_CODES.HONEYCOMB_MR2:
                    return HONEYCOMB_MR2.getBuildName();
                case Build.VERSION_CODES.ICE_CREAM_SANDWICH:
                    return ICE_CREAM_SANDWICH.getBuildName();
                case Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1:
                    return ICE_CREAM_SANDWICH_MR1.getBuildName();
                case Build.VERSION_CODES.JELLY_BEAN:
                    return JELLY_BEAN.getBuildName();
                case Build.VERSION_CODES.JELLY_BEAN_MR1:
                    return JELLY_BEAN_MR1.getBuildName();
                case Build.VERSION_CODES.JELLY_BEAN_MR2:
                    return JELLY_BEAN_MR2.getBuildName();
                case Build.VERSION_CODES.KITKAT:
                    return KITKAT.getBuildName();
                case Build.VERSION_CODES.KITKAT_WATCH:
                    return KITKAT_WATCH.getBuildName();
                case Build.VERSION_CODES.LOLLIPOP:
                    return LOLLIPOP.getBuildName();
                case Build.VERSION_CODES.LOLLIPOP_MR1:
                    return LOLLIPOP_MR1.getBuildName();
                case Build.VERSION_CODES.M:
                    return M.getBuildName();
                case Build.VERSION_CODES.N:
                    return N.getBuildName();
                case Build.VERSION_CODES.CUR_DEVELOPMENT:
                    return CUR_DEVELOPMENT.getBuildName();
                default:
                    return null;
            }
        }
    }
    //These are all checks for determining the build version.

    public static int phoneBuildVersion(){
        return Build.VERSION.SDK_INT;
    }

    /**
     * Dynamic method to check against a specific API level. Pass in the api level to check
     * @param x The int API level checking against
     * @return True if the user has that API level or higher, false if it is lower than that
     */
    public static boolean userHasOrIsHigherThan(int x){
        if(x < 0){
            return false;
        }
        if(SystemUtilities.phoneBuildVersion() >= x){
            return true;
        } else {
            return false;
        }
    }
    /**
     * API Level 24 or higher
     * @return
     */
    public static boolean userHasNougatOrHigher(){
        if(Build.VERSION.SDK_INT >= BuildVersions.N.getBuildVersion()){
            return true;
        } else {
            return false;
        }
    }

    /**
     * API Level 23 or higher
     * @return
     */
    public static boolean userHasMarshmallowOrHigher(){
        if(Build.VERSION.SDK_INT >= BuildVersions.M.getBuildVersion()){
            return true;
        } else {
            return false;
        }
    }

    /**
     * API Level 22 or higher
     * @return
     */
    public static boolean userHasLollipopMR1OrHigher(){
        //Build.VERSION_CODES.LOLLIPOP_MR1;
        if(Build.VERSION.SDK_INT >= BuildVersions.LOLLIPOP_MR1.getBuildVersion()){
            return true;
        } else {
            return false;
        }
    }

    /**
     * API Level 21 or higher
     * @return
     */
    public static boolean userHasLollipopOrHigher(){
        if(Build.VERSION.SDK_INT >= BuildVersions.LOLLIPOP.getBuildVersion()){
            return true;
        } else {
            return false;
        }
    }

    /**
     * API Level 19 or higher
     * @return
     */
    public static boolean userHasKitKatOrHigher(){
        if(Build.VERSION.SDK_INT >= BuildVersions.KITKAT.getBuildVersion()){
            return true;
        } else {
            return false;
        }
    }

    /**
     * API Level 16 or higher
     * @return
     */
    public static boolean userHasJellyBeanOrHigher(){
        if(Build.VERSION.SDK_INT >= BuildVersions.JELLY_BEAN.getBuildVersion()){
            return true;
        } else {
            return false;
        }
    }

    /**
     * API Level 14 or higher
     * @return
     */
    public static boolean userHasICSOrHigher(){
        if(Build.VERSION.SDK_INT >= BuildVersions.ICE_CREAM_SANDWICH.getBuildVersion()){
            return true;
        } else {
            return false;
        }
    }

}
