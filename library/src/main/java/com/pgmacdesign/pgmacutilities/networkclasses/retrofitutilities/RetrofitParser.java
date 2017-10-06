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
 * <p>
 * Note: If you want to send in a Type {@link Type} for these overloaded methods, use one of
 * the examples shown in {@link CustomConverterFactory} at the top of the class
 */
public class RetrofitParser {

    public static final int TAG_PARSE_ERROR = PGMacUtilitiesConstants.TAG_PARSE_ERROR;
    public static final String EMPTY_JSON_RESPONSE = "{}";

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
    public static <T> Object parse(@NonNull final Call<T> call,
                                   final Class successClassDataModel,
                                   final Class errorClassDataModel,
                                   final boolean serverCanReturn200Error) {
        try {
            Response response = call.execute();
            if (response == null) {
                return null;
            }

            if (serverCanReturn200Error) {
                Object o1 = RetrofitParser.checkForError(response, errorClassDataModel);
                if (o1 != null) {
                    return o1;
                }

                if (response.body() != null) {
                    Object o2 = RetrofitParser.convert(
                            response.body(), successClassDataModel);
                    if (o2 != null) {
                        return o2;
                    }
                } else {
                    Object o3 = RetrofitParser.checkForError(
                            response, errorClassDataModel);
                    if (o3 != null) {
                        return o3;
                    }
                }
                return null;
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
    public static <T> Object parse(@NonNull final Call<T> call,
                                   @NonNull final Type successClassDataModel,
                                   @NonNull final Type errorClassDataModel,
                                   final boolean serverCanReturn200Error) {
        try {
            Response response = call.execute();
            if (response == null) {
                return null;
            }

            if (serverCanReturn200Error) {
                Object o1 = RetrofitParser.checkForError(response, errorClassDataModel);
                if (o1 != null) {
                    return o1;
                }

                if (response.body() != null) {
                    Object o2 = RetrofitParser.convert(
                            response.body(), successClassDataModel);
                    if (o2 != null) {
                        return o2;
                    }
                } else {
                    Object o3 = RetrofitParser.checkForError(
                            response, errorClassDataModel);
                    if (o3 != null) {
                        return o3;
                    }
                }
                return null;
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
    public static <T> void parse(@NonNull final Call<T> call,
                                 final Class successClassDataModel,
                                 final Class errorClassDataModel) {
        RetrofitParser.parse(call, successClassDataModel, errorClassDataModel, false);
    }

    /**
     * Overloaded to allow for type {@link Type} entry
     */
    public static <T> void parse(@NonNull final Call<T> call,
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
                                 @NonNull final Call<T> call,
                                 final Class successClassDataModel,
                                 final Class errorClassDataModel,
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
                        Object o1 = RetrofitParser.checkForError(response, errorClassDataModel);
                        if (o1 != null) {
                            listener.onTaskComplete(o1, failCallbackTag);
                            return;
                        }

                        if (response.body() != null) {
                            Object o2 = RetrofitParser.convert(
                                    response.body(), successClassDataModel);
                            if (o2 != null) {
                                listener.onTaskComplete(o2, successCallbackTag);
                                return;
                            }
                        } else {
                            Object o3 = RetrofitParser.checkForError(
                                    response, errorClassDataModel);
                            if (o3 != null) {
                                listener.onTaskComplete(o3, failCallbackTag);
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
                                 @NonNull final Call<T> call,
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
                        Object o1 = RetrofitParser.checkForError(response, errorClassDataModel);
                        if (o1 != null) {
                            listener.onTaskComplete(o1, failCallbackTag);
                            return;
                        }

                        if (response.body() != null) {
                            Object o2 = RetrofitParser.convert(
                                    response.body(), successClassDataModel);
                            if (o2 != null) {
                                listener.onTaskComplete(o2, successCallbackTag);
                                return;
                            }
                        } else {
                            Object o3 = RetrofitParser.checkForError(
                                    response, errorClassDataModel);
                            if (o3 != null) {
                                listener.onTaskComplete(o3, failCallbackTag);
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
                                 @NonNull final Call<T> call,
                                 final Class successClassDataModel,
                                 final Class errorClassDataModel,
                                 final Integer successCallbackTag,
                                 final Integer failCallbackTag) {
        RetrofitParser.parse(listener, call, successClassDataModel,
                errorClassDataModel, successCallbackTag, failCallbackTag, false);
    }

    /**
     * Overloaded to allow for Type {@link Type} entry
     */
    public static <T> void parse(@NonNull final OnTaskCompleteListener listener,
                                 @NonNull final Call<T> call,
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
     * @param successClassDataModel The data model to attempt to convert into. If you are expecting
     *                              an empty response ({}), send null here and if the response is an
     *                              empty object, this function will return an empty object, else,
     *                              it will return an object that has been cast successfully into
     *                              the one passed.
     * @param <T>
     * @return Object. It will need to be cast into the success data model
     * once completed. If null is returned, it means the object
     * did not parse correctly into the success data model
     */
    private static <T> Object convert(T responseObject,
                                      final Class successClassDataModel) {
        try {
            JsonElement jsonElement = new Gson().toJsonTree(responseObject);
            if (jsonElement != null) {
                if (!jsonElement.isJsonNull()) {
                    if (jsonElement.toString().equalsIgnoreCase(EMPTY_JSON_RESPONSE)) {
                        //Empty / Null object
                        if (successClassDataModel == null) {
                            return new Object();
                        } else {
                            return null;
                        }
                    } else {
                        return new Gson().fromJson(jsonElement, successClassDataModel);
                    }
                } else {
                    return successClassDataModel.cast(responseObject);
                }
            } else {
                return successClassDataModel.cast(responseObject);
            }
        } catch (Exception e) {
        }
        return null;
    }

    /**
     * Convert a response object into the success type data model
     *
     * @param responseObject Response body from the {@link Call} response
     * @param type           The {@link Type} Java model. The data model to attempt to convert into. If you are expecting
     *                       an empty response ({}), send null here and if the response is an
     *                       empty object, this function will return an empty object, else,
     *                       it will return an object that has been cast successfully into
     *                       the one passed.
     * @param <T>
     * @return Object. It will need to be cast into the success data model once completed
     */
    private static <T> Object convert(T responseObject,
                                      final Type type) {
        try {
            JsonElement jsonElement = new Gson().toJsonTree(responseObject);
            if (jsonElement != null) {
                if (!jsonElement.isJsonNull()) {
                    if (jsonElement.toString().equalsIgnoreCase(EMPTY_JSON_RESPONSE)) {
                        //Empty / Null object
                        if (type == null) {
                            return new Object();
                        } else {
                            return null;
                        }
                    } else {
                        return new Gson().fromJson(jsonElement, type);
                    }
                } else {
                    return type.getClass().cast(responseObject);
                }
            } else {
                return type.getClass().cast(responseObject);
            }
        } catch (Exception e) {
        }
        return null;
    }

    /**
     * Checks for errors
     *
     * @param response            {@link Response}
     * @param errorClassDataModel The data model to attempt to convert into. If you are expecting
     *                            an empty response ({}), send null here and if the response is an
     *                            empty object, this function will return an empty object, else,
     *                            it will return an object that has been cast successfully into
     *                            the one passed.
     * @param <T>
     * @return Object, will need to be case back once completed
     */
    private static <T> Object checkForError(Response<T> response,
                                            final Class errorClassDataModel) {
        if (response == null) {
            return null;
        }
        try {
            Object toReturn = null;
            ResponseBody responseErrorBody = response.errorBody();
            T responseBody = response.body();
            if (responseErrorBody != null) {
                JsonElement jsonElement = new Gson().toJsonTree(responseErrorBody);
                if (jsonElement != null) {
                    if (!jsonElement.isJsonNull()) {
                        if (jsonElement.toString().equalsIgnoreCase(EMPTY_JSON_RESPONSE)) {
                            if (errorClassDataModel == null) {
                                return new Object(); //Empty / Null object
                            } else {
                                return null;
                            }
                        } else {
                            toReturn = new Gson().fromJson(jsonElement, errorClassDataModel);
                            if (toReturn != null) {
                                return toReturn;
                            }
                        }
                    }
                }
            }
            if (responseBody != null) {
                JsonElement jsonElement2 = new Gson().toJsonTree(responseBody);
                if (jsonElement2 != null) {
                    if (!jsonElement2.isJsonNull()) {
                        if (jsonElement2.toString().equalsIgnoreCase(EMPTY_JSON_RESPONSE)) {
                            if (errorClassDataModel == null) {
                                return new Object(); //Empty / Null object
                            } else {
                                return null;
                            }
                        } else {
                            toReturn = new Gson().fromJson(jsonElement2, errorClassDataModel);
                            if (toReturn != null) {
                                return toReturn;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
        }
        try {
            Object toReturn = null;
            ResponseBody responseErrorBody = response.errorBody();
            String error1 = responseErrorBody.string();
            if (!StringUtilities.isNullOrEmpty(error1)) {
                toReturn = new Gson().fromJson(error1, errorClassDataModel);
                if (toReturn != null) {
                    return toReturn;
                }
            }
        } catch (Exception e) {
        }
        try {
            Object toReturn = null;
            String str = response.body().toString();
            if (!StringUtilities.isNullOrEmpty(str)) {
                toReturn = new Gson().fromJson(str, errorClassDataModel);
                if (toReturn != null) {
                    return toReturn;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            return errorClassDataModel.cast(response.errorBody());
        } catch (Exception e) {
        }
        try {
            return errorClassDataModel.cast(response.body());
        } catch (Exception e) {
        }
        return null;
    }

    /**
     * Checks for errors
     *
     * @param responseError       {@link ResponseBody}
     * @param errorClassDataModel The data model to attempt to convert into. If you are expecting
     *                            an empty response ({}), send null here and if the response is an
     *                            empty object, this function will return an empty object, else,
     *                            it will return an object that has been cast successfully into
     *                            the one passed.
     * @param <T>
     * @return Object, will need to be case back once compelted
     */
    private static <T> Object checkForError(ResponseBody responseError,
                                            final Class errorClassDataModel) {
        if (responseError == null) {
            return null;
        }
        try {
            Object toReturn = null;
            if (responseError != null) {
                JsonElement jsonElement = new Gson().toJsonTree(responseError);
                if (jsonElement != null) {
                    if (!jsonElement.isJsonNull()) {
                        if (jsonElement.toString().equalsIgnoreCase(EMPTY_JSON_RESPONSE)) {
                            if (errorClassDataModel == null) {
                                return new Object(); //Empty / Null object
                            } else {
                                return null;
                            }
                        } else {
                            toReturn = new Gson().fromJson(jsonElement, errorClassDataModel);
                            if (toReturn != null) {
                                return toReturn;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
        }
        try {
            Object toReturn = null;
            String error1 = responseError.string();
            if (!StringUtilities.isNullOrEmpty(error1)) {
                toReturn = new Gson().fromJson(error1, errorClassDataModel);
                if (toReturn != null) {
                    return toReturn;
                }
            }
        } catch (Exception e) {
        }
        try {
            return errorClassDataModel.cast(responseError);
        } catch (Exception e) {
        }
        return null;
    }

    /**
     * Checks for errors
     *
     * @param response            {@link Response}
     * @param errorClassDataModel {@link Type} The data model to attempt to convert into. If you are expecting
     *                            an empty response ({}), send null here and if the response is an
     *                            empty object, this function will return an empty object, else,
     *                            it will return an object that has been cast successfully into
     *                            the one passed.
     * @param <T>
     * @return Object, will need to be case back once compelted
     */
    private static <T> Object checkForError(Response<T> response,
                                            final Type errorClassDataModel) {
        if (response == null) {
            return null;
        }
        try {
            Object toReturn = null;
            ResponseBody responseErrorBody = response.errorBody();
            T responseBody = response.body();
            if (responseErrorBody != null) {
                JsonElement jsonElement = new Gson().toJsonTree(responseErrorBody);
                if (jsonElement != null) {
                    if (!jsonElement.isJsonNull()) {
                        if (jsonElement.toString().equalsIgnoreCase(EMPTY_JSON_RESPONSE)) {
                            if (errorClassDataModel == null) {
                                return new Object(); //Empty / Null object
                            } else {
                                return null;
                            }
                        } else {
                            toReturn = new Gson().fromJson(jsonElement, errorClassDataModel);
                            if (toReturn != null) {
                                return toReturn;
                            }
                        }
                    }
                }
            }
            if (responseBody != null) {
                JsonElement jsonElement2 = new Gson().toJsonTree(responseBody);
                if (jsonElement2 != null) {
                    if (!jsonElement2.isJsonNull()) {
                        if (jsonElement2.toString().equalsIgnoreCase(EMPTY_JSON_RESPONSE)) {
                            if (errorClassDataModel == null) {
                                return new Object(); //Empty / Null object
                            } else {
                                return null;
                            }
                        } else {
                            toReturn = new Gson().fromJson(jsonElement2, errorClassDataModel);
                            if (toReturn != null) {
                                return toReturn;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
        }
        try {
            Object toReturn = null;
            ResponseBody responseErrorBody = response.errorBody();
            String error1 = responseErrorBody.string();
            if (!StringUtilities.isNullOrEmpty(error1)) {
                toReturn = new Gson().fromJson(error1, errorClassDataModel);
                if (toReturn != null) {
                    return toReturn;
                }
            }
        } catch (Exception e) {
        }
        try {
            Object toReturn = null;
            String str = response.body().toString();
            if (!StringUtilities.isNullOrEmpty(str)) {
                toReturn = new Gson().fromJson(str, errorClassDataModel);
                if (toReturn != null) {
                    return toReturn;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            return errorClassDataModel.getClass().cast(response.errorBody());
        } catch (Exception e) {
        }
        try {
            return errorClassDataModel.getClass().cast(response.body());
        } catch (Exception e) {
        }
        return null;
    }

    /**
     * Checks for errors
     *
     * @param responseError       {@link ResponseBody}
     * @param errorClassDataModel {@link Type} The data model to attempt to convert into. If you are expecting
     *                            an empty response ({}), send null here and if the response is an
     *                            empty object, this function will return an empty object, else,
     *                            it will return an object that has been cast successfully into
     *                            the one passed.
     * @param <T>
     * @return Object, will need to be case back once compelted
     */
    private static <T> Object checkForError(ResponseBody responseError,
                                            final Type errorClassDataModel) {
        if (responseError == null) {
            return null;
        }
        try {
            Object toReturn = null;
            JsonElement jsonElement = new Gson().toJsonTree(responseError);
            if (jsonElement != null) {
                if (!jsonElement.isJsonNull()) {
                    if (jsonElement.toString().equalsIgnoreCase(EMPTY_JSON_RESPONSE)) {
                        if (errorClassDataModel == null) {
                            return new Object(); //Empty / Null object
                        } else {
                            return null;
                        }
                    } else {
                        toReturn = new Gson().fromJson(jsonElement, errorClassDataModel);
                        if (toReturn != null) {
                            return toReturn;
                        }
                    }
                }
            }
        } catch (Exception e) {
        }
        try {
            Object toReturn = null;
            String error1 = responseError.string();
            if (!StringUtilities.isNullOrEmpty(error1)) {
                toReturn = new Gson().fromJson(error1, errorClassDataModel);
                if (toReturn != null) {
                    return toReturn;
                }
            }
        } catch (Exception e) {
        }
        try {
            return errorClassDataModel.getClass().cast(responseError);
        } catch (Exception e) {
        }
        return null;
    }

}
