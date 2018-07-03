package com.pgmacdesign.pgmactips.networkclasses.retrofitutilities;

import com.pgmacdesign.pgmactips.misc.CustomAnnotationsBase;
import com.pgmacdesign.pgmactips.utilities.GsonUtilities;
import com.pgmacdesign.pgmactips.utilities.StringUtilities;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Response;

/**
 * This class will handle Retrofit errors (ErrorBodies)
 * Created by pmacdowell on 8/29/2016.
 */
@CustomAnnotationsBase.RequiresDependency(requiresDependencies = {CustomAnnotationsBase.Dependencies.Retrofit2,
        CustomAnnotationsBase.Dependencies.Retrofit2GSONConverter, CustomAnnotationsBase.Dependencies.GSON,
        CustomAnnotationsBase.Dependencies.OkHttp3LoggingInterceptor, CustomAnnotationsBase.Dependencies.Okio})
public class RetrofitErrorHandling {


    /**
     * Checks the Response for an error body
     * @param response Response from Retrofit. {@link Response}
     * @return ResponseBody (Error body) from OKHttp {@link ResponseBody}
     */
    @CustomAnnotationsBase.RequiresDependency(requiresDependencies = {CustomAnnotationsBase.Dependencies.Retrofit2,
            CustomAnnotationsBase.Dependencies.Retrofit2GSONConverter, CustomAnnotationsBase.Dependencies.GSON,
            CustomAnnotationsBase.Dependencies.OkHttp3LoggingInterceptor, CustomAnnotationsBase.Dependencies.Okio})
    public static ResponseBody getErrorBody(Response response){
        try {
            ResponseBody rb = response.errorBody();
            return rb;
        } catch (Exception e){
            //In the event that the response is null or contains no error in it
            return null;
        }
    }

    /**
     * This will attempt to parse the errorReponse into a single Boolean. This is a less preferable
     * method to use in that it simply checks for 'equalsIgnoreCase' as opposed to parsing individual
     * Strings. I recommend you use parseErrorResponse() methods instead and use this only if
     * you are certain you will ONLY be receiving back a simple boolean response from the server
     * @param response The Retrofit Reponse to parse {@link Response}
     * @return String with the error body String. If a parsing error occurrs, it will return null
     */
    @CustomAnnotationsBase.RequiresDependency(requiresDependencies = {CustomAnnotationsBase.Dependencies.Retrofit2,
            CustomAnnotationsBase.Dependencies.Retrofit2GSONConverter, CustomAnnotationsBase.Dependencies.GSON,
            CustomAnnotationsBase.Dependencies.OkHttp3LoggingInterceptor, CustomAnnotationsBase.Dependencies.Okio})
    public static Boolean parseBooleanResponse(Response response){
        String str = parseErrorResponse(response);
        return StringUtilities.convertStringToBoolean(str);
    }

    /**
     * This will attempt to parse the errorReponse with no class to compare against. It will
     * pull back a String to return error body. See {@link ResponseBody} for more info
     * @param response The Retrofit Reponse to parse {@link Response}
     * @return String with the error body String. If a parsing error occurrs, it will return null
     */
    @CustomAnnotationsBase.RequiresDependency(requiresDependencies = {CustomAnnotationsBase.Dependencies.Retrofit2,
            CustomAnnotationsBase.Dependencies.Retrofit2GSONConverter, CustomAnnotationsBase.Dependencies.GSON,
            CustomAnnotationsBase.Dependencies.OkHttp3LoggingInterceptor, CustomAnnotationsBase.Dependencies.Okio})
    public static String parseErrorResponse(Response response){
        ResponseBody body = getErrorBody(response);
        if(body == null){
            return null;
        }
        try {
            return(body.string());
        } catch (IOException ioe){
            ioe.printStackTrace();
            return null;
        }
    }

    /**
     * This will custom parse an error response with your own class passed in as the parsing class.
     * An example would be a class that matches the error response from a server.
     * todo add sample parsing error class here for visual
     * @param response Response to parse {@link Response}
     * @param parsingClass Class that matches the server's return json data model for parsing
     * @return Returns an Object matching the parsingClass structure if it parses correctly. If not,
     *         it will send back null.
     */
    @CustomAnnotationsBase.RequiresDependency(requiresDependencies = {CustomAnnotationsBase.Dependencies.Retrofit2,
            CustomAnnotationsBase.Dependencies.Retrofit2GSONConverter, CustomAnnotationsBase.Dependencies.GSON,
            CustomAnnotationsBase.Dependencies.OkHttp3LoggingInterceptor, CustomAnnotationsBase.Dependencies.Okio})
    public static Object parseErrorResponse(Response response, Class parsingClass){
        ResponseBody body = getErrorBody(response);
        if(body == null || parsingClass == null){
            return null;
        }
        try {
            String str = body.string();
            Object obj = GsonUtilities.convertJsonToObject(str, parsingClass);
            return obj;
        } catch (IOException ioe){
            ioe.printStackTrace();
            return null;
        }
    }

    /**
     * Simpler error code handler. Use this in conjunction with other things
     * @param response {@link Response}
     * @return integer
     */
    @CustomAnnotationsBase.RequiresDependency(requiresDependencies = {CustomAnnotationsBase.Dependencies.Retrofit2,
            CustomAnnotationsBase.Dependencies.Retrofit2GSONConverter, CustomAnnotationsBase.Dependencies.GSON,
            CustomAnnotationsBase.Dependencies.OkHttp3LoggingInterceptor, CustomAnnotationsBase.Dependencies.Okio})
    public static int getReturnCode(Response response){
        return response.code();
    }
}
