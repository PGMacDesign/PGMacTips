package com.pgmacdesign.pgmactips.misc;

import android.content.Context;
import android.support.annotation.NonNull;

import com.pgmacdesign.pgmactips.utilities.StringUtilities;
import com.pgmacdesign.pgmactips.utilities.SystemUtilities;

/**
 * Class for defaulting global config variables
 * Currently used in the following classes:
 *   1){@link com.pgmacdesign.pgmactips.utilities.L}
 *
 * Please note that this will be a default for config changes in the future.
 * Created by pmacdowell on 2017-07-03.
 */
public class PGMacTipsConfig {

    // (1/7). Obtained from: https://stackoverflow.com/questions/20090265/android-picasso-configure-lrucache-size
    private static final double DEFAULT_MAX_LRU_CACHE_PICASSO = 0.14286;

    private static PGMacTipsConfig instance;

    /**
     * Default Configs. Unused atm
     * @param config
     */
    private static void initDefault(PGMacTipsConfig config){
        instance = config;
    }

    /**
     * Reset the instance. To create an instance with the variables set, use the
     * {@link PGMacTipsConfig.Builder} class
     * @param context Context
     * @return this
     */
    public static PGMacTipsConfig resetInstance(@NonNull Context context){
        instance = new PGMacTipsConfig.Builder().build(context);
        return instance;
    }

    /**
     * Return Can return null if set to null
     * @return this
     */
    public static PGMacTipsConfig getInstance(){
        return instance;
    }

    //Default Variables
    private final boolean isLiveBuild;
    private final Context context;
    private final String tagForLogging;
    private final String defaultDatabaseName;


    /**
     * Overloaded constructor for builder
     * @param builder
     */
    private PGMacTipsConfig(Builder builder){
        this.context = builder.context;
        this.isLiveBuild = builder.isLiveBuild;
        this.tagForLogging = builder.tagForLogging;
        this.defaultDatabaseName = builder.defaultDatabaseName;
//        this.initPicassoSingleton(); //Removed due to 'java.lang.NoClassDefFoundError: Failed resolution of: Lcom/squareup/picasso/Picasso$Builder;'
        PGMacTipsConfig.instance = this;
    }

    /**
     * Builder class
     */
    public static class Builder {
        private Context context;
        private String tagForLogging;
        private String defaultDatabaseName;
        private boolean isLiveBuild;

        /**
         * This is used to set the default Database name within the
         * {@link com.pgmacdesign.pgmactips.utilities.DatabaseUtilities} class.
         * This will only be used if null is passed into the constructor of the DatabaseUtilities, IE
         * {@link com.pgmacdesign.pgmactips.utilities.DatabaseUtilities#DatabaseUtilities(Context, String, Integer, Boolean)}
         * @param defaultDatabaseName
         * @return
         */
        public Builder setDefaultDatabaseName(String defaultDatabaseName) {
            this.defaultDatabaseName = defaultDatabaseName;
            return this;
        }

        //Removed on 2018-10-24 as leaving at default 1/7 for now
//        /**
//         * This is used to set the max used percent of the device storage capacity for Picasso to use
//         * as an LRU cache storage.
//         * @param picassoCacheSizeMaxPercent Passing in an int value as the percent to use. IE, passing
//         *                                   int 10 will use 10% of the device's memory while passing
//         *                                   int 50 will use 50% of the device's memory.
//         *                                   Defaults to 1/7 as per recommended defaults.
//         *                                   See {@link PGMacTipsConfig#DEFAULT_MAX_LRU_CACHE_PICASSO} for more info.
//         * @return this
//         */
//        public Builder setMaxPicassoCachePercent(@IntRange(from = 1, to = 99) @Nullable Integer picassoCacheSizeMaxPercent) {
//            if(picassoCacheSizeMaxPercent == null){
//                picassoCacheSizeMaxPercent = 14;
//            }
//            if(picassoCacheSizeMaxPercent < 0 || picassoCacheSizeMaxPercent >= 100){
//                picassoCacheSizeMaxPercent = 14;
//            }
//            this.picassoCacheSizeMaxPercent = picassoCacheSizeMaxPercent;
//            return this;
//        }

        /**
         * This is used for setting the tag String to be printed out in the logging class
         * {@link com.pgmacdesign.pgmactips.utilities.L
         * @param tagForLogging
         */
        public Builder setTagForLogging(@NonNull String tagForLogging) {
            this.tagForLogging = tagForLogging;
            return this;
        }

        /**
         * Used for disabling all print statements in the
         * {@link com.pgmacdesign.pgmactips.utilities.L} class. IE:
         * {@link com.pgmacdesign.pgmactips.utilities.L#m(Object)});}
         * @param isLiveBuild If true, will treat as live build and not print, else, will print
         * @return this
         */
        public Builder setLiveBuild(boolean isLiveBuild) {
            this.isLiveBuild = isLiveBuild;
            return this;
        }

	    public PGMacTipsConfig build(@NonNull Context context){
            //Context
            this.context = context;
            String packageName = null;
            //Package String
            try {
                packageName = SystemUtilities.getPackageName(this.context);
                if(!StringUtilities.isNullOrEmpty(packageName)){
                    if(packageName.length() > 23){
                        //Max length 23: https://stackoverflow.com/questions/28168622/the-logging-tag-can-be-at-most-23-characters
                        packageName = packageName.substring((packageName.length() - (1 + 23)), (packageName.length() - 1));
                    }
                }
            } catch (Exception e) {}
            if(StringUtilities.isNullOrEmpty(packageName)){
                packageName = "PGMacTips";
            }
            //Tag
            if(StringUtilities.isNullOrEmpty(this.tagForLogging)){
                this.tagForLogging = packageName;
            } else {
                if(this.tagForLogging.length() > 23){
                    this.tagForLogging = packageName;
                }
            }
            //DB Name
            if (StringUtilities.isNullOrEmpty(this.defaultDatabaseName)) {
                this.defaultDatabaseName = packageName + ".realm.db";
            }

            PGMacTipsConfig.instance = new PGMacTipsConfig(this);
		    return PGMacTipsConfig.instance;
	    }

    }

    public String getDefaultDatabaseName() {
        return defaultDatabaseName;
    }

    public boolean getIsLiveBuild() {
        return isLiveBuild;
    }

    public String getTagForLogging() {
        return tagForLogging;
    }

    public Context getContext(){
        return this.context;
    }


}
