package com.pgmacdesign.pgmactips.networkclasses.retrofitutilities;

import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pgmacdesign.pgmactips.adaptersandlisteners.OnTaskCompleteListener;
import com.pgmacdesign.pgmactips.misc.CustomAnnotationsBase;
import com.pgmacdesign.pgmactips.misc.PGMacTipsConstants;
import com.pgmacdesign.pgmactips.utilities.L;
import com.pgmacdesign.pgmactips.utilities.StringUtilities;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.lang.reflect.Type;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by pmacdowell on 2017-09-11.
 * Master Response Parser that is used for parsing calls using {@link retrofit2.Call#enqueue(Callback)}.
 * How to use:
 *      //Parse the response within your onTaskCompleteListener.
 *      Call call = yourServiceInterface.apiCall(params);
 *      RetrofitParser.parse(yourOnTaskCompleteListener, call,
 *          successDataModel.class, errorDataModel.class,
 *          successCallbackIntTag, failCallbackIntTag);
 * The responses on the onTaskComplete listener will be sent back on one of the following int tags:
 *      1) Your successCallbackIntTag
 *      2) Your failCallbackIntTag
 *      3) TAG_RETROFIT_PARSE_ERROR (4411)
 *      4) TAG_RETROFIT_CALL_ERROR (4412)
 * <p>
 * Note: If you want to send in a Type {@link Type} for these overloaded methods, use one of
 * the examples shown in {@link CustomConverterFactory} at the top of the class
 */
@CustomAnnotationsBase.RequiresDependency(requiresDependencies = {CustomAnnotationsBase.Dependencies.Retrofit2,
        CustomAnnotationsBase.Dependencies.Retrofit2GSONConverter, CustomAnnotationsBase.Dependencies.OkHttp3,
        CustomAnnotationsBase.Dependencies.GSON, CustomAnnotationsBase.Dependencies.OkHttp3LoggingInterceptor,
        CustomAnnotationsBase.Dependencies.Okio})
public class RetrofitParser {

    //region Static Variables

    private static final String PARSE_FAILED_STR_1 =
            "Web response could not be converted using the passed type. ";
    private static final String PARSE_FAILED_STR_2 =
            "Response was instead resolved as type: ";
    private static final String HTML_PAGE_COMMENT =
            "HTML Page. Please enable logging to see full response";
    private static final String HTML_TEXT_MARKER = "<!DOCTYPE html>";
    private static final String PARSE_FAILED_PRINTOUT = ". Data = ";
    private static final String PARSE_FAILED_PRINTOUT_ALT = "Response = \n";
    public static final Type TYPE_BOOLEAN = Boolean.TYPE;
    public static final Type TYPE_DOUBLE = Double.TYPE;
    public static final Type TYPE_INTEGER = Integer.TYPE;
    public static final Type TYPE_STRING = new TypeToken<String>() {
    }.getType();

    /**
     * This parse error tag triggers if the call could not be parsed by either the success
     * or failure tag. If this gets sent back, the object in the
     * {@link OnTaskCompleteListener#onTaskComplete(Object, int)} will be null.
     */
    public static final int TAG_RETROFIT_PARSE_ERROR = PGMacTipsConstants.TAG_RETROFIT_PARSE_ERROR;
    /**
     * This parse error tag triggers if the call fails or something gets caught in
     * the {@link retrofit2.Callback#onFailure} method. The response will always
     * be a String of the throwable message.
     */
    public static final int TAG_RETROFIT_CALL_ERROR = PGMacTipsConstants.TAG_RETROFIT_CALL_ERROR;
    public static final String EMPTY_JSON_RESPONSE = "{}";

    //endregion

    ////////////////////////
    //region Asynchronous Parsers//
    ////////////////////////

    /**
     * Parse a {@link Call}. This will run the call and attempt to parse the response
     * NOTE! This call is Asynchronous and will run on a background thread. Data will be
     * returned along the {@link OnTaskCompleteListener}.
     * NOTE! Failing to add the Internet permission
     * (<uses-permission android:name="android.permission.INTERNET"/>)
     * in your manifest will throw the java.lang.SecurityException exception
     *
     * @param listener                {@link OnTaskCompleteListener} To send data back on
     * @param call                    {@link Call} The call to send. Note, the type being
     *                                returned must be of type {@link ResponseBody}
     * @param successClassDataModel   {@link Class} The class data model to attempt to cast a
     *                                successful response into.
     * @param errorClassDataModel     {@link Class} The class data model to attempt to cast an
     *                                unsuccessful response into.
     * @param successCallbackTag      {@link Integer} Callback tag (enum / int) to be used
     *                                if the call is Successful and parsed correctly.
     * @param failCallbackTag         {@link Integer} Callback tag (enum / int) to be used
     *                                if the call is Unsuccessful or parsed incorrectly.
     * @param serverCanReturn200Error This represents whether the server you are hitting can send
     *                                back a 2xx response code with an error object. As most REST
     *                                APIs will return a 2xx tag for a success and a 4xx for an
     *                                error, this is not usually the case, but some servers will
     *                                respond with a 2xx response code and have the 'error object'
     *                                be considered a 'fail' response. Sending this in as true
     *                                will attempt to parse the 'fail' response from both the
     *                                response body as well as the response error body. If you
     *                                are unsure as to wether this applies to you, start by
     *                                sending false and check the responses.
     */
    @CustomAnnotationsBase.RequiresDependency(requiresDependencies = {CustomAnnotationsBase.Dependencies.Retrofit2,
            CustomAnnotationsBase.Dependencies.Retrofit2GSONConverter, CustomAnnotationsBase.Dependencies.OkHttp3,
            CustomAnnotationsBase.Dependencies.GSON, CustomAnnotationsBase.Dependencies.OkHttp3LoggingInterceptor,
            CustomAnnotationsBase.Dependencies.Okio})
    public static void parse(@NonNull final OnTaskCompleteListener listener,
                             @NonNull final Call<ResponseBody> call,
                             final Class successClassDataModel,
                             final Class errorClassDataModel,
                             final Integer successCallbackTag,
                             final Integer failCallbackTag,
                             final boolean serverCanReturn200Error) {
        try {
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                    if (response == null) {
                        //Response was null, bail out
                        listener.onTaskComplete(null, failCallbackTag);
                        return;
                    }

                    ResponseBody responseBody = response.body();
                    ResponseBody errorBody = response.errorBody();
                    String responseJson = null, errorResponseJson = null;
                    if (responseBody != null) {
                        try {
                            if (successClassDataModel != null) {
                                if (successClassDataModel.isAssignableFrom(InputStream.class)) {
                                    InputStream responseInputStream = responseBody.byteStream();
                                    if(responseInputStream != null){
                                        listener.onTaskComplete(responseInputStream, successCallbackTag);
                                        return;
                                    }
                                }
                            }
                        } catch (Exception e){}
                        try {
                            responseJson = responseBody.string();
                        } catch (Exception e) {}
                    }
                    if (errorBody != null) {
                        try {
                            errorResponseJson = errorBody.string();
                        } catch (Exception e) {
                        }
                    }

                    if (serverCanReturn200Error) {
                        Object o = checkForError(responseJson, errorResponseJson,
                                errorClassDataModel);
                        if (o != null) {
                            //This means error response parsed successfully with error model OR success model
                            listener.onTaskComplete(o, failCallbackTag);
                            return;
                        } else {
                            o = convert(responseJson, successClassDataModel);
                            if (o != null) {
                                listener.onTaskComplete(o, successCallbackTag);
                                return;
                            } else {
                                //Check if either success or fail model is a raw type
                                if(RetrofitParser.isRawType(successClassDataModel)){ L.m("retrofitparser -- " + 150);
                                    Object o1 = RetrofitParser.convertRawType(
                                            responseJson, successClassDataModel);
                                    if(o1 != null){
                                        L.m("retrofitparser -- " + 156);
                                        listener.onTaskComplete(o1, successCallbackTag);
                                        return;
                                    }
                                }
                                if(RetrofitParser.isRawType(errorClassDataModel)){ L.m("retrofitparser -- " + 166);
                                    Object o1 = RetrofitParser.convertRawType(
                                            errorResponseJson, errorClassDataModel);
                                    Object o2 = RetrofitParser.convertRawType(
                                            responseJson, errorClassDataModel);
                                    if(o1 != null){
                                        listener.onTaskComplete(o1, failCallbackTag);
                                        return;
                                    }
                                    if(o2 != null){
                                        listener.onTaskComplete(o2, failCallbackTag);
                                        return;
                                    }
                                }

                                // TODO: 2018-02-13 add in html checks here
                                //At this point, no passed types match parsing, likely either unknown JSON response or an error

                                RetrofitParser.failedParsingDetermineType(responseJson, successClassDataModel);
                                RetrofitParser.failedParsingDetermineType(errorResponseJson, errorClassDataModel);
                                listener.onTaskComplete(null, TAG_RETROFIT_PARSE_ERROR);
                                return;
                            }
                        }
                    } else {
                        Object o = checkForError(errorResponseJson, errorClassDataModel);
                        if (o != null) {
                            //This means error response parsed successfully with error model
                            listener.onTaskComplete(o, failCallbackTag);
                            return;
                        } else {
                            o = convert(responseJson, successClassDataModel);
                            if (o != null) {
                                listener.onTaskComplete(o, successCallbackTag);
                                return;
                            } else {
                                //Check if either success or fail model is a raw type
                                if(RetrofitParser.isRawType(successClassDataModel)){
                                    Object o1 = RetrofitParser.convertRawType(
                                            responseJson, successClassDataModel);
                                    if(o1 != null){
                                        listener.onTaskComplete(o1, successCallbackTag);
                                        return;
                                    }
                                }
                                if(RetrofitParser.isRawType(errorClassDataModel)){
                                    Object o1 = RetrofitParser.convertRawType(
                                            errorResponseJson, errorClassDataModel);
                                    if(o1 != null){
                                        listener.onTaskComplete(o1, failCallbackTag);
                                        return;
                                    }
                                }

                                // TODO: 2018-02-13 add in html checks here
                                //At this point, no passed types match parsing, likely either unknown JSON response or an error
                                RetrofitParser.failedParsingDetermineType(responseJson, successClassDataModel);
                                RetrofitParser.failedParsingDetermineType(errorResponseJson, errorClassDataModel);
                                listener.onTaskComplete(null, TAG_RETROFIT_PARSE_ERROR);
                                return;
                            }
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable throwable) {
                    throwable.printStackTrace();
                    listener.onTaskComplete(throwable.getMessage(), TAG_RETROFIT_CALL_ERROR);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            listener.onTaskComplete(e.getMessage(), TAG_RETROFIT_CALL_ERROR);
        }
    }

    /**
     * Overloaded to allow for Type {@link Type} entry
     * NOTE! Failing to add the Internet permission
     * (<uses-permission android:name="android.permission.INTERNET"/>)
     * in your manifest will throw the java.lang.SecurityException exception
     */
    @CustomAnnotationsBase.RequiresDependency(requiresDependencies = {CustomAnnotationsBase.Dependencies.Retrofit2,
            CustomAnnotationsBase.Dependencies.Retrofit2GSONConverter, CustomAnnotationsBase.Dependencies.OkHttp3,
            CustomAnnotationsBase.Dependencies.GSON, CustomAnnotationsBase.Dependencies.OkHttp3LoggingInterceptor,
            CustomAnnotationsBase.Dependencies.Okio})
    public static void parse(@NonNull final OnTaskCompleteListener listener,
                             @NonNull final Call<ResponseBody> call,
                             @NonNull final Type successClassDataModel,
                             @NonNull final Type errorClassDataModel,
                             final Integer successCallbackTag,
                             final Integer failCallbackTag,
                             final boolean serverCanReturn200Error) {

        try {
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                    if (response == null) {
                        //Response was null, bail out
                        listener.onTaskComplete(null, failCallbackTag);
                        return;
                    }

                    ResponseBody responseBody = response.body();
                    ResponseBody errorBody = response.errorBody();
                    String responseJson = null, errorResponseJson = null;
                    if (responseBody != null) {
                        try {
                            if (successClassDataModel != null) {
                                if (successClassDataModel.getClass().isAssignableFrom(InputStream.class)) {
                                    InputStream responseInputStream = responseBody.byteStream();
                                    if(responseInputStream != null){
                                        listener.onTaskComplete(responseInputStream, successCallbackTag);
                                        return;
                                    }
                                }
                            }
                        } catch (Exception e){}
                        try {
                            responseJson = responseBody.string();
                        } catch (Exception e) {
                        }
                    }
                    if (errorBody != null) {
                        try {
                            errorResponseJson = errorBody.string();
                        } catch (Exception e) {
                        }
                    }
                    if (serverCanReturn200Error) {
                        Object o = checkForError(responseJson, errorResponseJson,
                                errorClassDataModel);
                        if (o != null) {
                            //This means error response parsed successfully with error model OR success model
                            listener.onTaskComplete(o, failCallbackTag);
                            return;
                        } else {
                            o = convert(responseJson, successClassDataModel);
                            if (o != null) {
                                listener.onTaskComplete(o, successCallbackTag);
                                return;
                            } else {
                                //Check if either success or fail model is a raw type
                                if(RetrofitParser.isRawType(successClassDataModel)){
                                    Object o1 = RetrofitParser.convertRawType(
                                            responseJson, successClassDataModel);
                                    if(o1 != null){
                                        listener.onTaskComplete(o1, successCallbackTag);
                                        return;
                                    }
                                }
                                if(RetrofitParser.isRawType(errorClassDataModel)){
                                    Object o1 = RetrofitParser.convertRawType(
                                            errorResponseJson, errorClassDataModel);
                                    Object o2 = RetrofitParser.convertRawType(
                                            responseJson, errorClassDataModel);
                                    if(o1 != null){
                                        listener.onTaskComplete(o1, failCallbackTag);
                                        return;
                                    }
                                    if(o2 != null){
                                        listener.onTaskComplete(o2, failCallbackTag);
                                        return;
                                    }
                                }

                                // TODO: 2018-02-13 add in html checks here
                                //At this point, no passed types match parsing, likely either unknown JSON response or an error
                                RetrofitParser.failedParsingDetermineType(responseJson, successClassDataModel);
                                RetrofitParser.failedParsingDetermineType(errorResponseJson, errorClassDataModel);
                                listener.onTaskComplete(null, TAG_RETROFIT_PARSE_ERROR);
                                return;
                            }
                        }
                    } else {
                        Object o = checkForError(errorResponseJson, errorClassDataModel);
                        if (o != null) {
                            //This means error response parsed successfully with error model
                            listener.onTaskComplete(o, failCallbackTag);
                            return;
                        } else {
                            o = convert(responseJson, successClassDataModel);
                            if (o != null) {
                                listener.onTaskComplete(o, successCallbackTag);
                                return;
                            } else {
                                //Check if either success or fail model is a raw type
                                if(RetrofitParser.isRawType(successClassDataModel)){
                                    Object o1 = RetrofitParser.convertRawType(
                                            responseJson, successClassDataModel);
                                    if(o1 != null){
                                        listener.onTaskComplete(o1, successCallbackTag);
                                        return;
                                    }
                                }
                                if(RetrofitParser.isRawType(errorClassDataModel)){
                                    Object o1 = RetrofitParser.convertRawType(
                                            errorResponseJson, errorClassDataModel);
                                    if(o1 != null){
                                        listener.onTaskComplete(o1, failCallbackTag);
                                        return;
                                    }
                                }

                                // TODO: 2018-02-13 add in html checks here
                                //At this point, no passed types match parsing, likely either unknown JSON response or an error
                                RetrofitParser.failedParsingDetermineType(responseJson, successClassDataModel);
                                RetrofitParser.failedParsingDetermineType(errorResponseJson, errorClassDataModel);
                                listener.onTaskComplete(null, TAG_RETROFIT_PARSE_ERROR);
                                return;
                            }
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable throwable) {
                    throwable.printStackTrace();
                    listener.onTaskComplete(throwable.getMessage(), TAG_RETROFIT_CALL_ERROR);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            listener.onTaskComplete(e.getMessage(), TAG_RETROFIT_CALL_ERROR);
        }
    }

    /**
     * Overloaded method to allow for excluding last boolean. See
     * {@link RetrofitParser#parse(OnTaskCompleteListener, Call, Class, Class, Integer, Integer, boolean)}
     * NOTE! Failing to add the Internet permission
     * (<uses-permission android:name="android.permission.INTERNET"/>)
     * in your manifest will throw the java.lang.SecurityException exception
     * For full documentation
     */
    @CustomAnnotationsBase.RequiresDependency(requiresDependencies = {CustomAnnotationsBase.Dependencies.Retrofit2,
            CustomAnnotationsBase.Dependencies.Retrofit2GSONConverter, CustomAnnotationsBase.Dependencies.OkHttp3,
            CustomAnnotationsBase.Dependencies.GSON, CustomAnnotationsBase.Dependencies.OkHttp3LoggingInterceptor,
            CustomAnnotationsBase.Dependencies.Okio})
    public static void parse(@NonNull final OnTaskCompleteListener listener,
                             @NonNull final Call<ResponseBody> call,
                             final Class successClassDataModel,
                             final Class errorClassDataModel,
                             final Integer successCallbackTag,
                             final Integer failCallbackTag) {
        RetrofitParser.parse(listener, call, successClassDataModel,
                errorClassDataModel, successCallbackTag, failCallbackTag, false);
    }

    /**
     * Overloaded to allow for Type {@link Type} entry
     * NOTE! Failing to add the Internet permission
     * (<uses-permission android:name="android.permission.INTERNET"/>)
     * in your manifest will throw the java.lang.SecurityException exception
     */
    @CustomAnnotationsBase.RequiresDependency(requiresDependencies = {CustomAnnotationsBase.Dependencies.Retrofit2,
            CustomAnnotationsBase.Dependencies.Retrofit2GSONConverter, CustomAnnotationsBase.Dependencies.OkHttp3,
            CustomAnnotationsBase.Dependencies.GSON, CustomAnnotationsBase.Dependencies.OkHttp3LoggingInterceptor,
            CustomAnnotationsBase.Dependencies.Okio})
    public static void parse(@NonNull final OnTaskCompleteListener listener,
                             @NonNull final Call<ResponseBody> call,
                             @NonNull final Type successClassDataModel,
                             @NonNull final Type errorClassDataModel,
                             final Integer successCallbackTag,
                             final Integer failCallbackTag) {
        RetrofitParser.parse(listener, call, successClassDataModel,
                errorClassDataModel, successCallbackTag, failCallbackTag, false);
    }

    //endregion

    /////////////////////////////
    //region Private Utility Functions//
    /////////////////////////////

    //region Converters
    /**
     * Convert a response object into the success class data model
     *
     * @param responseBodyString    Response body string obtained from the
     *                              {@link Call} response.string()
     * @param successClassDataModel The data model to attempt to convert into. If you are expecting
     *                              an empty response ({}), send null here and if the response is an
     *                              empty object, this function will return an empty object, else,
     *                              it will return an object that has been cast successfully into
     *                              the one passed.
     * @return Object. It will need to be cast into the success data model
     * once completed. If null is returned, it means the object
     * did not parse correctly into the success data model
     */
    private static Object convert(final String responseBodyString,
                                  final Class successClassDataModel) {
        if (StringUtilities.isNullOrEmpty(responseBodyString)) {
            return null;
        }
        if (successClassDataModel == null) {
            if (!StringUtilities.isNullOrEmpty(responseBodyString)) {
                if (responseBodyString.equalsIgnoreCase(EMPTY_JSON_RESPONSE)) {
                    //Expected empty response as per error class data model
                    return new Object();
                }
            }
        } else {
            try {
                Object o = new Gson().fromJson(responseBodyString, successClassDataModel);
                if(o != null){
                    String testing = new Gson().toJson(o);
                    if(!StringUtilities.doesEqualIgnoreCase(testing, EMPTY_JSON_RESPONSE)){
                        return o;
                    }
                }
            } catch (IllegalArgumentException ile){
                RetrofitParser.illegalArgumentHit(successClassDataModel.getName());
            } catch (Exception e) {
            }
        }
        return null;
    }

    /**
     * Convert to object using raw type checks
     * @param responseBodyString String to check against
     * @param classDataModel Class data model to be compared against raw types (String, Boolean,
     *                       Integer, Double)
     * @return Object, non-null if parsed as intended, null if it did not
     */
    private static Object convertRawType (final String responseBodyString,
                                          final Class classDataModel){
        if (StringUtilities.isNullOrEmpty(responseBodyString)) {
            return null;
        }
        if (classDataModel == null) {
            if (!StringUtilities.isNullOrEmpty(responseBodyString)) {
                if (responseBodyString.equalsIgnoreCase(EMPTY_JSON_RESPONSE)) {
                    //Expected empty response as per error class data model
                    return new Object();
                }
            }
        } else {
            //Raw Checks
            if(classDataModel == TYPE_BOOLEAN){
                L.m("retrofitparser -- " + 476);
                try {
                    Boolean bool = new Gson().fromJson(responseBodyString, TYPE_BOOLEAN);
                    if (bool != null) {
                        return bool;
                    }
                } catch (Exception e) {
                }
                try {
                    if (responseBodyString.equalsIgnoreCase("true")
                            || responseBodyString.equalsIgnoreCase("false")) {
                        Boolean bool = Boolean.parseBoolean(responseBodyString);
                        if(bool != null) {
                            return bool;
                        }
                    }
                } catch (Exception e) {
                }

            } else if (classDataModel == TYPE_DOUBLE){
                L.m("retrofitparser -- " + 496);
                try {
                    Double dbl = new Gson().fromJson(responseBodyString, TYPE_DOUBLE);
                    if (dbl != null) {
                        return dbl;
                    }
                } catch (Exception e) {
                }
                try {
                    Double dbl = Double.parseDouble(responseBodyString);
                    if (dbl != null) {
                        return dbl;
                    }
                } catch (Exception e) {
                }

            } else if (classDataModel == TYPE_INTEGER){
                L.m("retrofitparser -- " + 513);
                try {
                    Integer intx = new Gson().fromJson(responseBodyString, TYPE_INTEGER);
                    if (intx != null) {
                        return intx;
                    }
                } catch (Exception e) {
                }
                try {
                    Integer intx = Integer.parseInt(responseBodyString);
                    if (intx != null) {
                        return intx;
                    }
                } catch (Exception e) {
                }

            } else if (classDataModel == TYPE_STRING) {
                try {
                    String str = new Gson().fromJson(responseBodyString, TYPE_STRING);
                    if (!StringUtilities.isNullOrEmpty(str)) {
                        return str;
                    }
                } catch (Exception e) {
                }
                boolean isJSONObject = false, isJSONArray = false;
                try {
                    JSONObject o = new JSONObject(responseBodyString);
                    isJSONObject = true;
                } catch (Exception e) {
                }
                try {
                    JSONArray o = new JSONArray(responseBodyString);
                    isJSONArray = true;
                } catch (Exception e) {
                }
                if (isJSONArray || isJSONObject) {
                    return null;
                } else {
                    return responseBodyString;
                }
            } else {
                L.m("retrofitparser -- " + 555);
                return null;
            }
        }
        return null;
    }

    /**
     * Convert a response object into the success type data model
     *
     * @param responseBodyString   Response body from the {@link Call} response
     * @param successClassDataType The {@link Type} Java model. The data model to attempt to convert into. If you are expecting
     *                             an empty response ({}), send null here and if the response is an
     *                             empty object, this function will return an empty object, else,
     *                             it will return an object that has been cast successfully into
     *                             the one passed.
     * @return Object. It will need to be cast into the success data model once completed
     */
    private static Object convert(final String responseBodyString,
                                  final Type successClassDataType) {
        if (StringUtilities.isNullOrEmpty(responseBodyString)) {
            return null;
        }
        if (successClassDataType == null) {
            if (!StringUtilities.isNullOrEmpty(responseBodyString)) {
                if (responseBodyString.equalsIgnoreCase(EMPTY_JSON_RESPONSE)) {
                    //Expected empty response as per error class data model
                    return new Object();
                }
            }
        } else {
            try {
                Object o = new Gson().fromJson(responseBodyString, successClassDataType);
                if(o != null){
                    String testing = new Gson().toJson(o);
                    if(!StringUtilities.doesEqualIgnoreCase(testing, EMPTY_JSON_RESPONSE)){
                        return o;
                    }
                }
            } catch (IllegalArgumentException ile){
                RetrofitParser.illegalArgumentHit(successClassDataType.getClass().getName());
            } catch (Exception e) {
            }
        }
        return null;
    }

    /**
     * Overloaded to allow for {@link Type}
     */
    private static Object convertRawType (final String responseBodyString,
                                          final Type classDataModel){
        if (StringUtilities.isNullOrEmpty(responseBodyString)) {
            return null;
        }
        if (classDataModel == null) {
            if (!StringUtilities.isNullOrEmpty(responseBodyString)) {
                if (responseBodyString.equalsIgnoreCase(EMPTY_JSON_RESPONSE)) {
                    //Expected empty response as per error class data model
                    return new Object();
                }
            }
        } else {
            //Raw Checks
            if(classDataModel == TYPE_BOOLEAN){
                L.m("retrofitparser -- " + 612);
                try {
                    Boolean bool = new Gson().fromJson(responseBodyString, TYPE_BOOLEAN);
                    if (bool != null) {
                        return bool;
                    }
                } catch (Exception e) {
                }
                try {
                    if (responseBodyString.equalsIgnoreCase("true")
                            || responseBodyString.equalsIgnoreCase("false")) {
                        Boolean bool = Boolean.parseBoolean(responseBodyString);
                        if(bool != null) {
                            return bool;
                        }
                    }
                } catch (Exception e) {
                }

            } else if (classDataModel == TYPE_DOUBLE){
                L.m("retrofitparser -- " + 632);
                try {
                    Double dbl = new Gson().fromJson(responseBodyString, TYPE_DOUBLE);
                    if (dbl != null) {
                        return dbl;
                    }
                } catch (Exception e) {
                }
                try {
                    Double dbl = Double.parseDouble(responseBodyString);
                    if (dbl != null) {
                        return dbl;
                    }
                } catch (Exception e) {
                }

            } else if (classDataModel == TYPE_INTEGER){
                L.m("retrofitparser -- " + 649);
                try {
                    Integer intx = new Gson().fromJson(responseBodyString, TYPE_INTEGER);
                    if (intx != null) {
                        return intx;
                    }
                } catch (Exception e) {
                }
                try {
                    Integer intx = Integer.parseInt(responseBodyString);
                    if (intx != null) {
                        return intx;
                    }
                } catch (Exception e) {
                }

            } else if (classDataModel == TYPE_STRING){
                try {
                    String str = new Gson().fromJson(responseBodyString, TYPE_STRING);
                    if (!StringUtilities.isNullOrEmpty(str)) {
                        return str;
                    }
                } catch (Exception e) {
                }
                boolean isJSONObject = false, isJSONArray = false;
                try {
                    JSONObject o = new JSONObject(responseBodyString);
                    isJSONObject = true;
                } catch (Exception e) {
                }
                try {
                    JSONArray o = new JSONArray(responseBodyString);
                    isJSONArray = true;
                } catch (Exception e) {
                }
                if (isJSONArray || isJSONObject) {
                    return null;
                } else {
                    return responseBodyString;
                }
            } else {
                L.m("retrofitparser -- " + 691);
                return null;
            }
        }
        return null;
    }

    //endregion

    //region Error Checkers

    /**
     * Check for an error response.
     *
     * @param responseBodyString  Success body string. Some servers send back a 2xx for all
     *                            responses and expect you to parse the error out of the response
     *                            body instead of the error body; this is to account for that.
     * @param errorBodyString     Error body string
     * @param errorClassDataModel Error class model to attempt to cast to. NOTE! If you want
     *                            to intentionally check for an empty JSON response, send null
     *                            here and if an empty JSON response "{}" is received, it will
     *                            return a new object.
     * @return If null, it means parsing failed, if an object that is not null, it means
     * that the response matches the error data model sent in
     */
    private static Object checkForError(final String responseBodyString,
                                        final String errorBodyString,
                                        final Class errorClassDataModel) {
        if (StringUtilities.isNullOrEmpty(responseBodyString)
                && StringUtilities.isNullOrEmpty(errorBodyString)) {
            return null;
        }
        if (errorClassDataModel == null) {
            if (!StringUtilities.isNullOrEmpty(errorBodyString)) {
                if (errorBodyString.equalsIgnoreCase(EMPTY_JSON_RESPONSE)) {
                    //Expected empty response as per error class data model
                    return new Object();
                }
            }
            if (!StringUtilities.isNullOrEmpty(responseBodyString)) {
                if (responseBodyString.equalsIgnoreCase(EMPTY_JSON_RESPONSE)) {
                    //Expected empty response as per error class data model
                    return new Object();
                }
            }
        } else {
            try {
                Object o = new Gson().fromJson(responseBodyString, errorClassDataModel);
                if(o != null){
                    String testing = new Gson().toJson(o);
                    if(!StringUtilities.doesEqualIgnoreCase(testing, EMPTY_JSON_RESPONSE)){
                        return o;
                    }
                }
            } catch (IllegalArgumentException ile){
                RetrofitParser.illegalArgumentHit(errorClassDataModel.getClass().getName());
            } catch (Exception e) {
            }
            try {
                Object o = new Gson().fromJson(responseBodyString, errorClassDataModel);
                if(o != null){
                    String testing = new Gson().toJson(o);
                    if(!StringUtilities.doesEqualIgnoreCase(testing, EMPTY_JSON_RESPONSE)){
                        return o;
                    }
                }
            } catch (IllegalArgumentException ile){
                RetrofitParser.illegalArgumentHit(errorClassDataModel.getClass().getName());
            } catch (Exception e) {
            }
        }
        return null;
    }

    /**
     * Check for an error response.
     *
     * @param errorBodyString     Error body string
     * @param errorClassDataModel Error class model to attempt to cast to. NOTE! If you want
     *                            to intentionally check for an empty JSON response, send null
     *                            here and if an empty JSON response "{}" is received, it will
     *                            return a new object.
     * @return If null, it means parsing failed, if an object that is not null, it means
     * that the response matches the error data model sent in
     */
    private static Object checkForError(final String errorBodyString,
                                        final Class errorClassDataModel) {
        if (StringUtilities.isNullOrEmpty(errorBodyString)) {
            return null;
        }
        if (errorClassDataModel == null) {
            if (!StringUtilities.isNullOrEmpty(errorBodyString)) {
                if (errorBodyString.equalsIgnoreCase(EMPTY_JSON_RESPONSE)) {
                    //Expected empty response as per error class data model
                    return new Object();
                }
            }
        } else {
            try {
                Object o = new Gson().fromJson(errorBodyString, errorClassDataModel);
                if(o != null){
                    String testing = new Gson().toJson(o);
                    if(!StringUtilities.doesEqualIgnoreCase(testing, EMPTY_JSON_RESPONSE)){
                        return o;
                    }
                }
            } catch (IllegalArgumentException ile){
                RetrofitParser.illegalArgumentHit(errorClassDataModel.getClass().getName());
            } catch (Exception e) {
            }
        }
        return null;
    }

    /**
     * Overloaded, allows {@link Type} to be passed instead
     */
    private static Object checkForError(final String responseBodyString,
                                        final String errorBodyString,
                                        final Type errorClassDataModel) {
        if (StringUtilities.isNullOrEmpty(responseBodyString)
                && StringUtilities.isNullOrEmpty(errorBodyString)) {
            return null;
        }
        if (errorClassDataModel == null) {
            if (!StringUtilities.isNullOrEmpty(errorBodyString)) {
                if (errorBodyString.equalsIgnoreCase(EMPTY_JSON_RESPONSE)) {
                    //Expected empty response as per error class data model
                    return new Object();
                }
            }
            if (!StringUtilities.isNullOrEmpty(responseBodyString)) {
                if (responseBodyString.equalsIgnoreCase(EMPTY_JSON_RESPONSE)) {
                    //Expected empty response as per error class data model
                    return new Object();
                }
            }
        } else {
            try {
                Object o = new Gson().fromJson(responseBodyString, errorClassDataModel);
                if(o != null){
                    String testing = new Gson().toJson(o);
                    if(!StringUtilities.doesEqualIgnoreCase(testing, EMPTY_JSON_RESPONSE)){
                        return o;
                    }
                }
            } catch (IllegalArgumentException ile){
                RetrofitParser.illegalArgumentHit(errorClassDataModel.getClass().getName());
            } catch (Exception e) {
            }
            try {
                Object o = new Gson().fromJson(responseBodyString, errorClassDataModel);
                if(o != null){
                    String testing = new Gson().toJson(o);
                    if(!StringUtilities.doesEqualIgnoreCase(testing, EMPTY_JSON_RESPONSE)){
                        return o;
                    }
                }
            } catch (IllegalArgumentException ile){
                RetrofitParser.illegalArgumentHit(errorClassDataModel.getClass().getName());
            } catch (Exception e) {
            }
        }
        return null;
    }

    /**
     * Overloaded, allows for {@link Type} to be passed instead
     */
    private static Object checkForError(final String errorBodyString,
                                        final Type errorClassDataModel) {
        if (StringUtilities.isNullOrEmpty(errorBodyString)) {
            return null;
        }
        if (errorClassDataModel == null) {
            if (!StringUtilities.isNullOrEmpty(errorBodyString)) {
                if (errorBodyString.equalsIgnoreCase(EMPTY_JSON_RESPONSE)) {
                    //Expected empty response as per error class data model
                    return new Object();
                }
            }
        } else {
            try {
                Object o = new Gson().fromJson(errorBodyString, errorClassDataModel);
                if(o != null){
                    String testing = new Gson().toJson(o);
                    if(!StringUtilities.doesEqualIgnoreCase(testing, EMPTY_JSON_RESPONSE)){
                        return o;
                    }
                }
            } catch (IllegalArgumentException ile){
                RetrofitParser.illegalArgumentHit(errorClassDataModel.getClass().getName());
            } catch (Exception e) {
            }
        }
        return null;
    }

    //endregion

    //region Raw Type Utilities
    /**
     * Simple checker for if the return type is a raw type
     * @param typeToCast input to check if raw
     * @return boolean, true if it is, false if it is not
     */
    private static boolean isRawType(Type typeToCast){
        if(typeToCast == null){
            return false;
        }
        if(typeToCast == TYPE_BOOLEAN){
            return true;
        } else if (typeToCast == TYPE_DOUBLE){
            return true;
        } else if (typeToCast == TYPE_INTEGER){
            return true;
        } else if (typeToCast == TYPE_STRING){
            return true;
        } else {
            return false;
        }
    }

    /**
     * Simple checker for if the return type is a raw type
     * @param typeToCast input to check if raw
     * @return boolean, true if it is, false if it is not
     */
    private static boolean isRawType(Class typeToCast){
        if(typeToCast == null){
            return false;
        }
        if(typeToCast == TYPE_BOOLEAN){
            return true;
        } else if (typeToCast == TYPE_DOUBLE){
            return true;
        } else if (typeToCast == TYPE_INTEGER){
            return true;
        } else if (typeToCast == TYPE_STRING){
            return true;
        } else {
            return false;
        }
    }

    //endregion

    /**
     * Used to determine if response is raw type (IE Boolean, Integer, Double, String) and
     * then print out the info in the log cat
     *
     * @param responseBodyString
     * @param typeToCast
     */
    private static void failedParsingDetermineType(String responseBodyString, Type typeToCast) {
        //Raw Checks
        if (StringUtilities.isNullOrEmpty(responseBodyString)) {
            return;
        }
        try {
            Boolean bool = new Gson().fromJson(responseBodyString, TYPE_BOOLEAN);
            if (bool != null) {
                L.m((typeToCast != null) ? PARSE_FAILED_STR_1
                        + "(Passed = " + typeToCast.toString() + "). "
                        + PARSE_FAILED_STR_2 + TYPE_BOOLEAN.toString()
                        + PARSE_FAILED_PRINTOUT + bool :
                        PARSE_FAILED_STR_1 + PARSE_FAILED_STR_2 + TYPE_BOOLEAN.toString()
                                + PARSE_FAILED_PRINTOUT + bool
                );
                return;
            }
        } catch (Exception e) {
        }
        try {
            if (responseBodyString.equalsIgnoreCase("true")
                    || responseBodyString.equalsIgnoreCase("false")) {
                L.m((typeToCast != null) ? PARSE_FAILED_STR_1
                        + "(Passed = " + typeToCast.toString() + "). "
                        + PARSE_FAILED_STR_2 + TYPE_BOOLEAN.toString()
                        + PARSE_FAILED_PRINTOUT + responseBodyString :
                        PARSE_FAILED_STR_1 + PARSE_FAILED_STR_2 + TYPE_BOOLEAN.toString()
                                + PARSE_FAILED_PRINTOUT + responseBodyString
                );
                return;
            }
        } catch (Exception e) {
        }
        try {
            Double dbl = new Gson().fromJson(responseBodyString, TYPE_DOUBLE);
            if (dbl != null) {
                L.m((typeToCast != null) ? PARSE_FAILED_STR_1
                        + "(Passed = " + typeToCast.toString() + "). "
                        + PARSE_FAILED_STR_2 + TYPE_DOUBLE.toString()
                        + PARSE_FAILED_PRINTOUT + dbl :
                        PARSE_FAILED_STR_1 + PARSE_FAILED_STR_2 + TYPE_DOUBLE.toString()
                                + PARSE_FAILED_PRINTOUT + dbl);
                return;
            }
        } catch (Exception e) {
        }
        try {
            Double dbl = Double.parseDouble(responseBodyString);
            if (dbl != null) {
                L.m((typeToCast != null) ? PARSE_FAILED_STR_1
                        + "(Passed = " + typeToCast.toString() + "). "
                        + PARSE_FAILED_STR_2 + TYPE_DOUBLE.toString()
                        + PARSE_FAILED_PRINTOUT + dbl :
                        PARSE_FAILED_STR_1 + PARSE_FAILED_STR_2 + TYPE_DOUBLE.toString()
                                + PARSE_FAILED_PRINTOUT + dbl);
                return;
            }
        } catch (Exception e) {
        }
        try {
            Integer intx = new Gson().fromJson(responseBodyString, TYPE_INTEGER);
            if (intx != null) {
                L.m((typeToCast != null) ? PARSE_FAILED_STR_1
                        + "(Passed = " + typeToCast.toString() + "). "
                        + PARSE_FAILED_STR_2 + TYPE_INTEGER.toString()
                        + PARSE_FAILED_PRINTOUT + intx :
                        PARSE_FAILED_STR_1 + PARSE_FAILED_STR_2 + TYPE_INTEGER.toString()
                                + PARSE_FAILED_PRINTOUT + intx);
                return;
            }
        } catch (Exception e) {
        }
        try {
            Integer intx = Integer.parseInt(responseBodyString);
            if (intx != null) {
                L.m((typeToCast != null) ? PARSE_FAILED_STR_1
                        + "(Passed = " + typeToCast.toString() + "). "
                        + PARSE_FAILED_STR_2 + TYPE_INTEGER.toString()
                        + PARSE_FAILED_PRINTOUT + intx :
                        PARSE_FAILED_STR_1 + PARSE_FAILED_STR_2 + TYPE_INTEGER.toString()
                                + PARSE_FAILED_PRINTOUT + intx);
                return;
            }
        } catch (Exception e) {
        }
        try {
            String str = new Gson().fromJson(responseBodyString, TYPE_STRING);
            if (!StringUtilities.isNullOrEmpty(str)) {
                L.m((typeToCast != null) ? PARSE_FAILED_STR_1
                        + "(Passed = " + typeToCast.toString() + "). "
                        + PARSE_FAILED_STR_2 + TYPE_STRING.toString()
                        + PARSE_FAILED_PRINTOUT + str :
                        PARSE_FAILED_STR_1 + PARSE_FAILED_STR_2 + TYPE_STRING.toString()
                                + PARSE_FAILED_PRINTOUT + str);
                return;
            }
        } catch (Exception e) {
        }
        boolean isJSONObject = false, isJSONArray = false;
        try {
            JSONObject o = new JSONObject(responseBodyString);
            isJSONObject = true;
        } catch (Exception e) {
        }
        try {
            JSONArray o = new JSONArray(responseBodyString);
            isJSONArray = true;
        } catch (Exception e) {
        }
        if (isJSONArray || isJSONObject) {
            L.m((typeToCast != null) ? PARSE_FAILED_STR_1
                    + "(Passed = " + typeToCast.toString() + "). "
                    + PARSE_FAILED_PRINTOUT_ALT + responseBodyString :
                    PARSE_FAILED_STR_1 + PARSE_FAILED_PRINTOUT_ALT + responseBodyString);
        } else {
            boolean isHtmlString = false;
            if(PGMacTipsConstants.ATTEMPT_TO_PARSE_HTML) {
                try {
                    if (responseBodyString.contains(HTML_TEXT_MARKER)) {
                        isHtmlString = true;
                        L.m("html tag check = true");
                    } else {
                        L.m("html tag check = false");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    isHtmlString = false;
                }
            }
            if(isHtmlString){
                L.m((typeToCast != null) ? PARSE_FAILED_STR_1
                        + "(Passed = " + typeToCast.toString() + "). "
                        + PARSE_FAILED_STR_2 + HTML_PAGE_COMMENT :
                        PARSE_FAILED_STR_1 + PARSE_FAILED_STR_2 + HTML_PAGE_COMMENT);
            } else {
                L.m((typeToCast != null) ? PARSE_FAILED_STR_1
                        + "(Passed = " + typeToCast.toString() + "). "
                        + PARSE_FAILED_STR_2 + TYPE_STRING.toString()
                        + PARSE_FAILED_PRINTOUT + responseBodyString :
                        PARSE_FAILED_STR_1 + PARSE_FAILED_STR_2 + TYPE_STRING.toString()
                                + PARSE_FAILED_PRINTOUT + responseBodyString);
            }
        }
    }

    /**
     * Used to determine if response is raw type (IE Boolean, Integer, Double, String) and
     * then print out the info in the log cat
     *
     * @param responseBodyString
     * @param typeToCast
     */
    private static void failedParsingDetermineType(String responseBodyString, Class typeToCast) {
        //Raw Checks
        if (StringUtilities.isNullOrEmpty(responseBodyString)) {
            return;
        }
        try {
            Boolean bool = new Gson().fromJson(responseBodyString, TYPE_BOOLEAN);
            if (bool != null) {
                L.m((typeToCast != null) ? PARSE_FAILED_STR_1
                        + "(Passed = " + typeToCast.toString() + "). "
                        + PARSE_FAILED_STR_2 + TYPE_BOOLEAN.toString()
                        + PARSE_FAILED_PRINTOUT + bool :
                        PARSE_FAILED_STR_1 + PARSE_FAILED_STR_2 + TYPE_BOOLEAN.toString()
                                + PARSE_FAILED_PRINTOUT + bool
                );
                return;
            }
        } catch (Exception e) {
        }
        try {
            if (responseBodyString.equalsIgnoreCase("true")
                    || responseBodyString.equalsIgnoreCase("false")) {
                L.m((typeToCast != null) ? PARSE_FAILED_STR_1
                        + "(Passed = " + typeToCast.toString() + "). "
                        + PARSE_FAILED_STR_2 + TYPE_BOOLEAN.toString()
                        + PARSE_FAILED_PRINTOUT + responseBodyString :
                        PARSE_FAILED_STR_1 + PARSE_FAILED_STR_2 + TYPE_BOOLEAN.toString()
                                + PARSE_FAILED_PRINTOUT + responseBodyString
                );
                return;
            }
        } catch (Exception e) {
        }
        try {
            Double dbl = new Gson().fromJson(responseBodyString, TYPE_DOUBLE);
            if (dbl != null) {
                L.m((typeToCast != null) ? PARSE_FAILED_STR_1
                        + "(Passed = " + typeToCast.toString() + "). "
                        + PARSE_FAILED_STR_2 + TYPE_DOUBLE.toString()
                        + PARSE_FAILED_PRINTOUT + dbl :
                        PARSE_FAILED_STR_1 + PARSE_FAILED_STR_2 + TYPE_DOUBLE.toString()
                                + PARSE_FAILED_PRINTOUT + dbl);
                return;
            }
        } catch (Exception e) {
        }
        try {
            Double dbl = Double.parseDouble(responseBodyString);
            if (dbl != null) {
                L.m((typeToCast != null) ? PARSE_FAILED_STR_1
                        + "(Passed = " + typeToCast.toString() + "). "
                        + PARSE_FAILED_STR_2 + TYPE_DOUBLE.toString()
                        + PARSE_FAILED_PRINTOUT + dbl :
                        PARSE_FAILED_STR_1 + PARSE_FAILED_STR_2 + TYPE_DOUBLE.toString()
                                + PARSE_FAILED_PRINTOUT + dbl);
                return;
            }
        } catch (Exception e) {
        }
        try {
            Integer intx = new Gson().fromJson(responseBodyString, TYPE_INTEGER);
            if (intx != null) {
                L.m((typeToCast != null) ? PARSE_FAILED_STR_1
                        + "(Passed = " + typeToCast.toString() + "). "
                        + PARSE_FAILED_STR_2 + TYPE_INTEGER.toString()
                        + PARSE_FAILED_PRINTOUT + intx :
                        PARSE_FAILED_STR_1 + PARSE_FAILED_STR_2 + TYPE_INTEGER.toString()
                                + PARSE_FAILED_PRINTOUT + intx);
                return;
            }
        } catch (Exception e) {
        }
        try {
            Integer intx = Integer.parseInt(responseBodyString);
            if (intx != null) {
                L.m((typeToCast != null) ? PARSE_FAILED_STR_1
                        + "(Passed = " + typeToCast.toString() + "). "
                        + PARSE_FAILED_STR_2 + TYPE_INTEGER.toString()
                        + PARSE_FAILED_PRINTOUT + intx :
                        PARSE_FAILED_STR_1 + PARSE_FAILED_STR_2 + TYPE_INTEGER.toString()
                                + PARSE_FAILED_PRINTOUT + intx);
                return;
            }
        } catch (Exception e) {
        }
        try {
            String str = new Gson().fromJson(responseBodyString, TYPE_STRING);
            if (!StringUtilities.isNullOrEmpty(str)) {
                L.m((typeToCast != null) ? PARSE_FAILED_STR_1
                        + "(Passed = " + typeToCast.toString() + "). "
                        + PARSE_FAILED_STR_2 + TYPE_STRING.toString()
                        + PARSE_FAILED_PRINTOUT + str :
                        PARSE_FAILED_STR_1 + PARSE_FAILED_STR_2 + TYPE_STRING.toString()
                                + PARSE_FAILED_PRINTOUT + str);
                return;
            }
        } catch (Exception e) {
        }
        boolean isJSONObject = false, isJSONArray = false;
        try {
            JSONObject o = new JSONObject(responseBodyString);
            isJSONObject = true;
        } catch (Exception e) {
        }
        try {
            JSONArray o = new JSONArray(responseBodyString);
            isJSONArray = true;
        } catch (Exception e) {
        }
        if (isJSONArray || isJSONObject) {
            L.m((typeToCast != null) ? PARSE_FAILED_STR_1
                    + "(Passed = " + typeToCast.toString() + "). "
                    + PARSE_FAILED_PRINTOUT_ALT + responseBodyString :
                    PARSE_FAILED_STR_1 + PARSE_FAILED_PRINTOUT_ALT + responseBodyString);
        } else {
            boolean isHtmlString = false;
            if(PGMacTipsConstants.ATTEMPT_TO_PARSE_HTML) {
                try {
                    if (responseBodyString.contains(HTML_TEXT_MARKER)) {
                        isHtmlString = true;
                        L.m("html tag check = true");
                    } else {
                        L.m("html tag check = false");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    isHtmlString = false;
                }
            }
            if(isHtmlString){
                L.m((typeToCast != null) ? PARSE_FAILED_STR_1
                        + "(Passed = " + typeToCast.toString() + "). "
                        + PARSE_FAILED_STR_2 + HTML_PAGE_COMMENT :
                        PARSE_FAILED_STR_1 + PARSE_FAILED_STR_2 + HTML_PAGE_COMMENT);
            } else {
                L.m((typeToCast != null) ? PARSE_FAILED_STR_1
                        + "(Passed = " + typeToCast.toString() + "). "
                        + PARSE_FAILED_STR_2 + TYPE_STRING.toString()
                        + PARSE_FAILED_PRINTOUT + responseBodyString :
                        PARSE_FAILED_STR_1 + PARSE_FAILED_STR_2 + TYPE_STRING.toString()
                                + PARSE_FAILED_PRINTOUT + responseBodyString);
            }
        }
    }

    private static void illegalArgumentHit(String typeOfModelPassed){
        L.m("Error while attempting to convert the web response into your passed type: "
                + typeOfModelPassed +
                ". This can be caused by having multiple variables with the same '@Serialized' String name. Check your data model for errors and try again. See this link for more information: https://stackoverflow.com/questions/32367469/unable-to-create-converter-for-my-class-in-android-retrofit-library/42517143#42517143");
    }

    //endregion
}
