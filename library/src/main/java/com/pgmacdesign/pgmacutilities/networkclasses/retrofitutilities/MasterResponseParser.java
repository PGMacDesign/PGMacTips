package com.pgmacdesign.pgmacutilities.networkclasses.retrofitutilities;

import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.pgmacdesign.pgmacutilities.adaptersandlisteners.OnTaskCompleteListener;
import com.pgmacdesign.pgmacutilities.misc.PGMacUtilitiesConstants;
import com.pgmacdesign.pgmacutilities.utilities.L;
import com.pgmacdesign.pgmacutilities.utilities.StringUtilities;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by pmacdowell on 2017-09-11.
 */

public class MasterResponseParser {

    /*
    Sample Retrofit client:

    TempInterface temp = new RetrofitClient.Builder<>(TempInterface.class, "www.google.com")
        .setLogLevel(HttpLoggingInterceptor.Level.BODY)
        .setHeaders(RetrofitClient.Builder.getApplicationJSONMap())
        .setDateFormat("YYYY/mm/DD HH:MM:SS.sss")
        .setCustomConverterFactory(new CustomConverterFactory())
        .setTimeouts(60000, 60000)
        .build()
        .buildServiceClient();
     */

    public static final int TAG_PARSE_ERROR = PGMacUtilitiesConstants.TAG_PARSE_ERROR;


    public static void parse(@NonNull final OnTaskCompleteListener listener,
                             @NonNull final retrofit2.Call call,
                             @NonNull final Class successClassDataModel,
                             @NonNull final Class errorClassDataModel,
                             final Integer successCallbackTag, final Integer failCallbackTag,
                             final boolean serverCanReturn200Error){
        try {
            call.enqueue(new Callback() {
                @Override
                public void onResponse(Call call, Response response) {
                    if(response == null){
                        L.m("response was null");
                        listener.onTaskComplete(null, failCallbackTag);
                        return;
                    }

                    if(serverCanReturn200Error){
                        Object oo = MasterResponseParser.checkForError(
                                response, errorClassDataModel);
                        if(oo != null){
                            listener.onTaskComplete(oo, failCallbackTag);
                            return;
                        } else {
                            listener.onTaskComplete(MasterResponseParser.convert(
                                    response, successClassDataModel), successCallbackTag);
                            return;
                        }
                    } else {
                        if(!response.isSuccessful()){
                            ResponseBody responseError = response.errorBody();
                            listener.onTaskComplete(MasterResponseParser.checkForError(
                                    responseError, errorClassDataModel), failCallbackTag);
                            return;
                        } else {
                            listener.onTaskComplete(MasterResponseParser.convert(
                                    response, successClassDataModel), successCallbackTag);
                            return;
                        }
                    }

                    //Add more logging here if need be
                }

                @Override
                public void onFailure(Call call, Throwable throwable) {
                    listener.onTaskComplete(throwable.getMessage(), failCallbackTag);
                }
            });
        } catch (Exception e){
            e.printStackTrace();
            listener.onTaskComplete(e.getMessage(), TAG_PARSE_ERROR);
        }
    }

    public static void parse(@NonNull final OnTaskCompleteListener listener,
                             @NonNull final retrofit2.Call call,
                             @NonNull final Class successClassDataModel,
                             @NonNull final Class errorClassDataModel,
                             final Integer successCallbackTag, final Integer failCallbackTag){
        MasterResponseParser.parse(listener, call, successClassDataModel,
                errorClassDataModel, successCallbackTag, failCallbackTag, false);
    }

    private static <T> Object convert(Object responseObject,
                                      @NonNull final Class successClassDataModel){
        try {
            return successClassDataModel.cast(responseObject);
        } catch (Exception e){
            e.printStackTrace();
        }
        try {
            /*
            final T myClass = (T) successClassDataModel;
            Type myType = new TypeToken<successClassDataModel>().getType();
            String str = new Gson().toJson(responseObject,
                    new TypeToken<myClass>(){}.getType());
            */
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    private static Object checkForError(retrofit2.Response response,
                                        @NonNull final Class errorClassDataModel){
        if(response == null){
            return null;
        }
        try {
            String str = response.errorBody().string();
            if(!StringUtilities.isNullOrEmpty(str)){
                return new Gson().fromJson(str, errorClassDataModel);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        try {
            return errorClassDataModel.cast(response.body());
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    private static Object checkForError(ResponseBody responseError,
                                        @NonNull final Class errorClassDataModel){
        if(responseError == null){
            return null;
        }
        try {
            String str = responseError.string();
            if(!StringUtilities.isNullOrEmpty(str)){
                return new Gson().fromJson(str, errorClassDataModel);
            }
            //Error
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

}
