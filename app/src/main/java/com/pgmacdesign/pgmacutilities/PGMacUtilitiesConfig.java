package com.pgmacdesign.pgmacutilities;

import android.content.Context;
import android.support.annotation.NonNull;

/**
 * Testing centralized context class. Will refactor when time permits.
 * Created by pmacdowell on 2017-07-03.
 */
public class PGMacUtilitiesConfig {

    private static PGMacUtilitiesConfig instance;

    /**
     * Default Configs
     * @param config
     */
    public static void initDefault(PGMacUtilitiesConfig config){
        instance = config;
    }

    public static PGMacUtilitiesConfig getInstance(){
        if(instance == null){
            instance = new PGMacUtilitiesConfig(new Builder());
        }
        return instance;
    }

    //Default Variables
    private final boolean retrofitCallsAreApplicationJson;
    private final boolean defaultStringForDatabaseName;
    private final Context PGMacContext;

    public boolean isRetrofitCallsAreApplicationJson() {
        return retrofitCallsAreApplicationJson;
    }

    public boolean isDefaultStringForDatabaseName() {
        return defaultStringForDatabaseName;
    }

    public Context getContext() {
        return PGMacContext;
    }

    /**
     * Overloaded constructor for builder
     * @param builder
     */
    protected PGMacUtilitiesConfig(Builder builder){
        retrofitCallsAreApplicationJson = builder.retrofitCallsAreApplicationJson;
        defaultStringForDatabaseName = builder.defaultStringForDatabaseName;
        PGMacContext = builder.context;
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

        public PGMacUtilitiesConfig build(@NonNull Context context){
            this.context = context;
            return new PGMacUtilitiesConfig(this);
        }
    }

}
