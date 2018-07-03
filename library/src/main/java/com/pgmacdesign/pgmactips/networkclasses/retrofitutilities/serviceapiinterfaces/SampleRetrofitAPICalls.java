package com.pgmacdesign.pgmactips.networkclasses.retrofitutilities.serviceapiinterfaces;

import android.support.annotation.NonNull;

import com.google.gson.reflect.TypeToken;
import com.pgmacdesign.pgmactips.adaptersandlisteners.OnTaskCompleteListener;
import com.pgmacdesign.pgmactips.datamodels.SamplePojo;
import com.pgmacdesign.pgmactips.misc.CustomAnnotationsBase;
import com.pgmacdesign.pgmactips.networkclasses.retrofitutilities.RetrofitClient;
import com.pgmacdesign.pgmactips.networkclasses.retrofitutilities.RetrofitParser;
import com.pgmacdesign.pgmactips.utilities.L;

import java.util.Map;

import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;

/**
 * This class is a sample of sample API calls and how to use them.
 * Created by pmacdowell on 8/29/2016.
 */
@CustomAnnotationsBase.RequiresDependency(requiresDependencies = {CustomAnnotationsBase.Dependencies.Retrofit2,
        CustomAnnotationsBase.Dependencies.Retrofit2GSONConverter, CustomAnnotationsBase.Dependencies.GSON,
        CustomAnnotationsBase.Dependencies.OkHttp3LoggingInterceptor, CustomAnnotationsBase.Dependencies.Okio})
public class SampleRetrofitAPICalls {

    private static SampleRetrofitService retrofitService;

    private static final String SAMPLE_BASE_URL = "http://www.google.com/api/";
    private static final boolean SAMPLE_IS_LIVE_BUILD = false;
    private static final long ONE_MINUTE = 1000 * 60;
    private static final int TAG_FOR_SUCCESS = 1;
    private static final int TAG_FOR_FAILURE = 0;

    private static void init(){
        if(retrofitService == null){
            RetrofitClient.Builder builder = new RetrofitClient.Builder(
                    SampleRetrofitService.class, SAMPLE_BASE_URL);
            HttpLoggingInterceptor.Level level = (SAMPLE_IS_LIVE_BUILD)
                    ? HttpLoggingInterceptor.Level.NONE : HttpLoggingInterceptor.Level.BODY;
            builder.setLogLevel(level);
            builder.setTimeouts(ONE_MINUTE, ONE_MINUTE);
            retrofitService = builder.build().buildServiceClient();
        }
    }

    /**
     * Sample call using the {@link SampleRetrofitService} and the {@link RetrofitParser} class.
     * @param listener {@link OnTaskCompleteListener}
     * @param pojo Object to be sent in as body for POST call
     */
    @CustomAnnotationsBase.RequiresDependency(requiresDependencies = {CustomAnnotationsBase.Dependencies.Retrofit2,
            CustomAnnotationsBase.Dependencies.Retrofit2GSONConverter, CustomAnnotationsBase.Dependencies.GSON,
            CustomAnnotationsBase.Dependencies.OkHttp3LoggingInterceptor, CustomAnnotationsBase.Dependencies.Okio})
    public static void sampleCall(@NonNull final OnTaskCompleteListener listener, SamplePojo pojo){
        init();
        Call<ResponseBody> call = retrofitService.login(pojo);
        RetrofitParser.parse(new OnTaskCompleteListener() {
            @Override
            public void onTaskComplete(Object result, int customTag) {
                //Parse result and send back upon listener
                switch (customTag){
                    case TAG_FOR_SUCCESS:
                        //Success, parse to success model
                        //This will match your success model data type
                        break;
                    case TAG_FOR_FAILURE:
                        //Failure, parse to fail model
                        //This will match your failure model data type
                        break;
                    case RetrofitParser.TAG_RETROFIT_PARSE_ERROR:
                        //Parse error, could not parse with either success or fail model
                        //Result will always be null here, no need to cast or check
                        break;
                    case RetrofitParser.TAG_RETROFIT_CALL_ERROR:
                        //Call failed entirely, IE, timeout error
                        String str = (String) result;
                        L.m("Error was: " + str);
                        break;
                    default:
                        //Shouldn't trigger
                }
            }
        }, call, //Call to send out. Call<ResponseBody> is required
                SamplePojo.class, //Expected response data model type
                new TypeToken<Map<String, String>>(){}.getType(), //Expected error response data model type
                TAG_FOR_SUCCESS, //Tag to be sent back upon if successfully parsed on success data model
                TAG_FOR_FAILURE); //Tag to be sent back upon if successfully parsed on failure data model
    }
}
