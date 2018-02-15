package com.pgmacdesign.pgmactips.misc;

import android.content.Context;
import android.support.annotation.NonNull;

/**
 * Class for defaulting global config variables
 * Currently used in the following classes:
 *   1){@link com.pgmacdesign.pgmactips.utilities.L}
 *
 *
 * Created by pmacdowell on 2017-07-03.
 */
public class PGMacTipsConfig {

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


    /**
     * Overloaded constructor for builder
     * @param builder
     */
    private PGMacTipsConfig(Builder builder){
        this.context = builder.context;
        this.isLiveBuild = builder.isLiveBuild;
        this.tagForLogging = builder.tagForLogging;
        PGMacTipsConfig.instance = this;
    }

    /**
     * Builder class
     */
    public static class Builder {
        private Context context;
        private String tagForLogging;
        private boolean isLiveBuild;

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
            this.context = context;
            PGMacTipsConfig.instance = new PGMacTipsConfig(this);
		    return PGMacTipsConfig.instance;
	    }
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
