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
     * Get the instance
     * @param context Context
     * @return this
     */
    public static PGMacTipsConfig getInstance(@NonNull Context context){
        if(instance == null){
            instance = new PGMacTipsConfig.Builder().build(context);
        }
        return instance;
    }

    /**
     * Use to forcefully reset instance with context
     * @param context Context
     * @return this
     */
    public static PGMacTipsConfig resetInstance(@NonNull Context context){
        instance = null;
        instance = new PGMacTipsConfig.Builder().build(context);
        return instance;
    }

    /**
     * Can return null if set to null
     * @return this
     */
    public static PGMacTipsConfig getInstance(){
        return instance;
    }

    //Default Variables
    private final boolean retrofitCallsAreApplicationJson;
    private final boolean defaultStringForDatabaseName;
    private final boolean isLiveBuild;
    private final Context context;
    

    /**
     * Overloaded constructor for builder
     * @param builder
     */
    private PGMacTipsConfig(Builder builder){
        this.context = builder.context;
        this.retrofitCallsAreApplicationJson = builder.retrofitCallsAreApplicationJson;
        this.defaultStringForDatabaseName = builder.defaultStringForDatabaseName;
        this.isLiveBuild = builder.isLiveBuild;
    }

    /**
     * Builder class
     */
    public static class Builder {
        private Context context;
        private boolean retrofitCallsAreApplicationJson;
        private boolean defaultStringForDatabaseName;
        private boolean isLiveBuild;


        public Builder setRetrofitCallsAreApplicationJson(
                boolean retrofitCallsAreApplicationJson) {
            this.retrofitCallsAreApplicationJson = retrofitCallsAreApplicationJson;
            return this;
        }

        public Builder setDefaultStringForDatabaseName(
                boolean defaultStringForDatabaseName) {
            this.defaultStringForDatabaseName = defaultStringForDatabaseName;
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
		    return new PGMacTipsConfig(this);
	    }
    }
    
	public boolean getRetrofitCallsAreApplicationJson() {
		return retrofitCallsAreApplicationJson;
	}
	
	public boolean getDefaultStringForDatabaseName() {
		return defaultStringForDatabaseName;
	}

    public boolean getIsLiveBuild() {
        return isLiveBuild;
    }

    public Context getContext(){
        return this.context;
    }
}
