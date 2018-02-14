package com.pgmacdesign.pgmactips.misc;

import android.content.Context;
import android.support.annotation.NonNull;

/**
 * Testing centralized context class. Will refactor when time permits.
 * Created by pmacdowell on 2017-07-03.
 */
public class PGMacTipsConfig {

    private static PGMacTipsConfig instance;

    /**
     * Default Configs
     * @param config
     */
    public static void initDefault(PGMacTipsConfig config){
        instance = config;
    }

    public static PGMacTipsConfig getInstance(){
        if(instance == null){
            instance = new PGMacTipsConfig(new Builder());
        }
        return instance;
    }

    //Default Variables
    private final boolean retrofitCallsAreApplicationJson;
    private final boolean defaultStringForDatabaseName;
	private Context context;
    

    /**
     * Overloaded constructor for builder
     * @param builder
     */
    protected PGMacTipsConfig(Builder builder){
        retrofitCallsAreApplicationJson = builder.retrofitCallsAreApplicationJson;
        defaultStringForDatabaseName = builder.defaultStringForDatabaseName;
        context = builder.context;
    }

    /**
     * Builder class
     */
    public static class Builder {
        private boolean retrofitCallsAreApplicationJson;
        private boolean defaultStringForDatabaseName;
        private Context context;

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

        public PGMacTipsConfig build(@NonNull Context context){
            this.context = context;
            return new PGMacTipsConfig(this);
        }
	
	    public PGMacTipsConfig build(){
		    this.context = PGMacTipsConfig.getContext();
		    return new PGMacTipsConfig(this);
	    }
    }
	
    public static Context getContext(){
    	try {
		    PGMacContextProvider p = new PGMacContextProvider();
		    return p.getContext();
	    } catch (Exception e){
    		e.printStackTrace();
    		return null;
    	}
    }
    
	public boolean getRetrofitCallsAreApplicationJson() {
		return retrofitCallsAreApplicationJson;
	}
	
	public boolean getDefaultStringForDatabaseName() {
		return defaultStringForDatabaseName;
	}
	
}
