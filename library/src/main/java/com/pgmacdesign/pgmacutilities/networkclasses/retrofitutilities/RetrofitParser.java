package com.pgmacdesign.pgmacutilities.networkclasses.retrofitutilities;

import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.pgmacdesign.pgmacutilities.adaptersandlisteners.OnTaskCompleteListener;
import com.pgmacdesign.pgmacutilities.misc.PGMacUtilitiesConstants;
import com.pgmacdesign.pgmacutilities.utilities.StringUtilities;

import java.lang.reflect.Type;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Master Response Parser that is used for parsing calls via enqueue.
 * Created by pmacdowell on 2017-09-11.
 *
 * Note: If you want to send in a Type {@link Type} for these overloaded methods, use one of
 * the examples shown in {@link CustomConverterFactory} at the top of the class
 */
public class RetrofitParser {

    public static final int TAG_PARSE_ERROR = PGMacUtilitiesConstants.TAG_PARSE_ERROR;


    ///////////////////////
    //Synchronous Parsers//
    ///////////////////////

    /**
     * Parse a {@link Call}. This will run the call and attempt to parse the response
     * NOTE! This call is Synchronous and will run on the Main thread, unless otherwise designed.
     * Please make sure to run this on a thread, within Asynctask, or any other asynchronous way
     * as this will throw a runOnMainUiException without any thread to handle it; I recommend
     * you use the accompanying method that utilizes the {@link OnTaskCompleteListener}.
     * If you wish to run this asynchronously, see
     * {@link RetrofitParser#parse(OnTaskCompleteListener, Call, Class, Class, Integer, Integer, boolean)}
     *
     * @param call                    {@link Call} The call to send
     * @param successClassDataModel   {@link Class} The class data model to attempt to cast a
     *                                successful response into.
     * @param errorClassDataModel     {@link Class} The class data model to attempt to cast an
     *                                unsuccessful response into.
     * @param serverCanReturn200Error This represents whether the server you are hitting can send
     *                                back a 200 response code with an error object. As most REST
     *                                APIs will return a 200 tag for a success and a 400 for an
     *                                error, this is not usually the case, but some servers will
     *                                respond with a 200 response code and have the 'error object'
     *                                be considered a 'fail' response. Sending this in as true
     *                                will attempt to parse the 'fail' response from both the
     *                                response body as well as the response error body.
     */
    public static <T> Object parse(@NonNull final retrofit2.Call<T> call,
                                   @NonNull final Class successClassDataModel,
                                   @NonNull final Class errorClassDataModel,
                                   final boolean serverCanReturn200Error) {
        try {
            Response response = call.execute();
            if (serverCanReturn200Error) {
                Object oo = RetrofitParser.checkForError(
                        response, errorClassDataModel);
                if (oo != null) {
                    return oo;
                } else {
                    return RetrofitParser.convert(
                            response.body(), successClassDataModel);
                }
            } else {
                if (!response.isSuccessful()) {
                    ResponseBody responseError = response.errorBody();
                    return RetrofitParser.checkForError(
                            responseError, errorClassDataModel);
                } else {
                    return RetrofitParser.convert(
                            response.body(), successClassDataModel);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return e;
        }
    }

    /**
     * Overloaded to allow for Type {@link Type} to be sent in as data models
     */
    public static <T> Object parse(@NonNull final retrofit2.Call<T> call,
                                   @NonNull final Type successClassDataModel,
                                   @NonNull final Type errorClassDataModel,
                                   final boolean serverCanReturn200Error) {
        try {
            Response response = call.execute();
            if (serverCanReturn200Error) {
                Object oo = RetrofitParser.checkForError(
                        response, errorClassDataModel);
                if (oo != null) {
                    return oo;
                } else {
                    return RetrofitParser.convert(
                            response.body(), successClassDataModel);
                }
            } else {
                if (!response.isSuccessful()) {
                    ResponseBody responseError = response.errorBody();
                    return RetrofitParser.checkForError(
                            responseError, errorClassDataModel);
                } else {
                    return RetrofitParser.convert(
                            response.body(), successClassDataModel);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return e;
        }
    }

    /**
     * Overloaded method to allow for excluding last boolean. See
     * {@link RetrofitParser#parse(Call, Class, Class, boolean)}
     * For full documentation
     */
    public static <T> void parse(@NonNull final retrofit2.Call<T> call,
                                 @NonNull final Class successClassDataModel,
                                 @NonNull final Class errorClassDataModel) {
        RetrofitParser.parse(call, successClassDataModel, errorClassDataModel, false);
    }

    /**
     * Overloaded to allow for type {@link Type} entry
     */
    public static <T> void parse(@NonNull final retrofit2.Call<T> call,
                                 @NonNull final Type successClassDataModel,
                                 @NonNull final Type errorClassDataModel) {
        RetrofitParser.parse(call, successClassDataModel, errorClassDataModel, false);
    }

    ////////////////////////
    //Asynchronous Parsers//
    ////////////////////////

    /**
     * Parse a {@link Call}. This will run the call and attempt to parse the response
     * NOTE! This call is Asynchronous and will run on a background thread. Data will be
     * returned along the {@link OnTaskCompleteListener}. If you wish to run this synchronously,
     * see {@link RetrofitParser#parse(Call, Class, Class, boolean)}
     *
     * @param listener                {@link OnTaskCompleteListener} To send data back on
     * @param call                    {@link Call} The call to send
     * @param successClassDataModel   {@link Class} The class data model to attempt to cast a
     *                                successful response into.
     * @param errorClassDataModel     {@link Class} The class data model to attempt to cast an
     *                                unsuccessful response into.
     * @param successCallbackTag      {@link Integer} Callback tag (enum / int) to be used
     *                                if the call is Successful and parsed correctly.
     * @param failCallbackTag         {@link Integer} Callback tag (enum / int) to be used
     *                                if the call is Unsuccessful or parsed incorrectly.
     * @param serverCanReturn200Error This represents whether the server you are hitting can send
     *                                back a 200 response code with an error object. As most REST
     *                                APIs will return a 200 tag for a success and a 400 for an
     *                                error, this is not usually the case, but some servers will
     *                                respond with a 200 response code and have the 'error object'
     *                                be considered a 'fail' response. Sending this in as true
     *                                will attempt to parse the 'fail' response from both the
     *                                response body as well as the response error body. If you
     *                                are unsure as to wether this applies to you, start by
     *                                sending false and check the responses.
     */
    public static <T> void parse(@NonNull final OnTaskCompleteListener listener,
                                 @NonNull final retrofit2.Call<T> call,
                                 @NonNull final Class successClassDataModel,
                                 @NonNull final Class errorClassDataModel,
                                 final Integer successCallbackTag,
                                 final Integer failCallbackTag,
                                 final boolean serverCanReturn200Error) {
        try {
            call.enqueue(new Callback<T>() {
                @Override
                public void onResponse(@NonNull Call<T> call, @NonNull Response<T> response) {
                    if (response == null) {
                        //Response was null, bail out
                        listener.onTaskComplete(null, failCallbackTag);
                        return;
                    }

                    if (serverCanReturn200Error) {

                        if(response.body() != null){
                            Object o = RetrofitParser.convert(
                                    response.body(), successClassDataModel);
                            if(o != null){
                                listener.onTaskComplete(o, successCallbackTag);
                                return;
                            }
                            Object oo = RetrofitParser.checkForError(
                                    response, errorClassDataModel);
                            if (oo != null) {
                                listener.onTaskComplete(oo, failCallbackTag);
                                return;
                            }
                        } else {
                            Object oo = RetrofitParser.checkForError(
                                    response, errorClassDataModel);
                            if (oo != null) {
                                listener.onTaskComplete(oo, failCallbackTag);
                                return;
                            }
                        }
                        listener.onTaskComplete(null, failCallbackTag);
                        return;
                    } else {
                        if (!response.isSuccessful()) {
                            ResponseBody responseError = response.errorBody();
                            listener.onTaskComplete(RetrofitParser.checkForError(
                                    responseError, errorClassDataModel), failCallbackTag);
                            return;
                        } else {
                            listener.onTaskComplete(RetrofitParser.convert(
                                    response.body(), successClassDataModel), successCallbackTag);
                            return;
                        }
                    }

                    //Add more logging here if need be
                }

                @Override
                public void onFailure(@NonNull Call<T> call, @NonNull Throwable throwable) {
                    listener.onTaskComplete(throwable.getMessage(), failCallbackTag);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            listener.onTaskComplete(e.getMessage(), TAG_PARSE_ERROR);
        }
    }

    /**
     * Overloaded to allow for Type {@link Type} entry
     */
    public static <T> void parse(@NonNull final OnTaskCompleteListener listener,
                                 @NonNull final retrofit2.Call<T> call,
                                 @NonNull final Type successClassDataModel,
                                 @NonNull final Type errorClassDataModel,
                                 final Integer successCallbackTag,
                                 final Integer failCallbackTag,
                                 final boolean serverCanReturn200Error) {
        try {
            call.enqueue(new Callback<T>() {
                @Override
                public void onResponse(@NonNull Call<T> call, @NonNull Response<T> response) {
                    if (response == null) {
                        //Response was null, bail out
                        listener.onTaskComplete(null, failCallbackTag);
                        return;
                    }

                    if (serverCanReturn200Error) {

                        if(response.body() != null){
                            Object o = RetrofitParser.convert(
                                    response.body(), successClassDataModel);
                            if(o != null){
                                listener.onTaskComplete(o, successCallbackTag);
                                return;
                            }
                            Object oo = RetrofitParser.checkForError(
                                    response, errorClassDataModel);
                            if (oo != null) {
                                listener.onTaskComplete(oo, failCallbackTag);
                                return;
                            }
                        } else {
                            Object oo = RetrofitParser.checkForError(
                                    response, errorClassDataModel);
                            if (oo != null) {
                                listener.onTaskComplete(oo, failCallbackTag);
                                return;
                            }
                        }
                        listener.onTaskComplete(null, failCallbackTag);
                        return;
                    } else {
                        if (!response.isSuccessful()) {
                            ResponseBody responseError = response.errorBody();
                            listener.onTaskComplete(RetrofitParser.checkForError(
                                    responseError, errorClassDataModel), failCallbackTag);
                            return;
                        } else {
                            listener.onTaskComplete(RetrofitParser.convert(
                                    response.body(), successClassDataModel), successCallbackTag);
                            return;
                        }
                    }

                    //Add more logging here if need be
                }

                @Override
                public void onFailure(@NonNull Call<T> call, @NonNull Throwable throwable) {
                    listener.onTaskComplete(throwable.getMessage(), failCallbackTag);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            listener.onTaskComplete(e.getMessage(), TAG_PARSE_ERROR);
        }
    }

    /**
     * Overloaded method to allow for excluding last boolean. See
     * {@link RetrofitParser#parse(OnTaskCompleteListener, Call, Class, Class, Integer, Integer, boolean)}
     * For full documentation
     */
    public static <T> void parse(@NonNull final OnTaskCompleteListener listener,
                                 @NonNull final retrofit2.Call<T> call,
                                 @NonNull final Class successClassDataModel,
                                 @NonNull final Class errorClassDataModel,
                                 final Integer successCallbackTag,
                                 final Integer failCallbackTag) {
        RetrofitParser.parse(listener, call, successClassDataModel,
                errorClassDataModel, successCallbackTag, failCallbackTag, false);
    }

    /**
     * Overloaded to allow for Type {@link Type} entry
     */
    public static <T> void parse(@NonNull final OnTaskCompleteListener listener,
                                 @NonNull final retrofit2.Call<T> call,
                                 @NonNull final Type successClassDataModel,
                                 @NonNull final Type errorClassDataModel,
                                 final Integer successCallbackTag,
                                 final Integer failCallbackTag) {
        RetrofitParser.parse(listener, call, successClassDataModel,
                errorClassDataModel, successCallbackTag, failCallbackTag, false);
    }


    /////////////////////////////
    //Private Utility Functions//
    /////////////////////////////

    /**
     * Convert a response object into the success class data model
     *
     * @param responseObject        Response body from the {@link Call} response
     * @param successClassDataModel The data model to attempt to convert into
     * @param <T>
     * @return Object. It will need to be cast into the success data model once completed
     */
    private static <T> Object convert(T responseObject,
                                      @NonNull final Class successClassDataModel) {
        try {
            return successClassDataModel.cast(responseObject);
        } catch (Exception e) {}
        if(responseObject != null){
            try {
                JsonElement jsonElement = new Gson().toJsonTree(responseObject);
                return new Gson().fromJson(jsonElement, successClassDataModel);
            } catch (Exception e){}
        }
        return null;
    }

    /**
     * Convert a response object into the success type data model
     *
     * @param responseObject        Response body from the {@link Call} response
     * @param type The {@link Type} Java model. This is primarily used for casting things
     *             into odd things like lists or arrays, which are more problematic
     *             to convert using a class
     * @param <T>
     * @return Object. It will need to be cast into the success data model once completed
     */
    private static <T> Object convert(T responseObject,
                                      @NonNull final Type type) {

        try {
            return type.getClass().cast(responseObject);
        } catch (Exception e) {}
        if(responseObject != null){
            try {
                JsonElement jsonElement = new Gson().toJsonTree(responseObject);
                return new Gson().fromJson(jsonElement, type);
            } catch (Exception e){}
        }
        return null;
    }

    /**
     * Checks for errors
     *
     * @param response            {@link Response}
     * @param errorClassDataModel Class error data model to try to convert into
     * @param <T>
     * @return Object, will need to be case back once compelted
     */
    private static <T> Object checkForError(retrofit2.Response<T> response,
                                            @NonNull final Class errorClassDataModel) {
        if (response == null) {
            return null;
        }
        try {
            String str = response.errorBody().string();
            if (!StringUtilities.isNullOrEmpty(str)) {
                return new Gson().fromJson(str, errorClassDataModel);
            }
        } catch (Exception e) {}
        try {
            return errorClassDataModel.cast(((T) response.body()));
        } catch (Exception e) {}
        try {
            JsonElement jsonElement = new Gson().toJsonTree(response.body());
            return new Gson().fromJson(jsonElement, errorClassDataModel);
        } catch (Exception e){}
        return null;
    }

    /**
     * Checks for errors
     *
     * @param responseError       {@link ResponseBody}
     * @param errorClassDataModel Class error data model to try to convert into
     * @param <T>
     * @return Object, will need to be case back once compelted
     */
    private static <T> Object checkForError(ResponseBody responseError,
                                            @NonNull final Class errorClassDataModel) {
        if (responseError == null) {
            return null;
        }
        try {
            String str = responseError.string();
            if (!StringUtilities.isNullOrEmpty(str)) {
                return new Gson().fromJson(str, errorClassDataModel);
            }
            //Error
        } catch (Exception e) {}
        try {
            JsonElement jsonElement = new Gson().toJsonTree(responseError);
            return new Gson().fromJson(jsonElement, errorClassDataModel);
        } catch (Exception e){}
        return null;
    }

    /**
     * Checks for errors
     *
     * @param response            {@link Response}
     * @param errorClassDataModel Type error data model to try to convert into. {@link Type}
     * @param <T>
     * @return Object, will need to be case back once compelted
     */
    private static <T> Object checkForError(retrofit2.Response<T> response,
                                            @NonNull final Type errorClassDataModel) {
        if (response == null) {
            return null;
        }
        try {
            String str = response.errorBody().string();
            if (!StringUtilities.isNullOrEmpty(str)) {
                return new Gson().fromJson(str, errorClassDataModel);
            }
        } catch (Exception e) {}
        try {
            return errorClassDataModel.getClass().cast(((T) response.body()));
        } catch (Exception e) {}
        try {
            JsonElement jsonElement = new Gson().toJsonTree(response.body());
            return new Gson().fromJson(jsonElement, errorClassDataModel);
        } catch (Exception e){}
        return null;
    }

    /**
     * Checks for errors
     *
     * @param responseError       {@link ResponseBody}
     * @param errorClassDataModel Type error data model to try to convert into. {@link Type}
     * @param <T>
     * @return Object, will need to be case back once compelted
     */
    private static <T> Object checkForError(ResponseBody responseError,
                                            @NonNull final Type errorClassDataModel) {
        if (responseError == null) {
            return null;
        }
        try {
            String str = responseError.string();
            if (!StringUtilities.isNullOrEmpty(str)) {
                return new Gson().fromJson(str, errorClassDataModel);
            }
            //Error
        } catch (Exception e) {}
        try {
            JsonElement jsonElement = new Gson().toJsonTree(responseError);
            return new Gson().fromJson(jsonElement, errorClassDataModel);
        } catch (Exception e){}
        return null;
    }

}
