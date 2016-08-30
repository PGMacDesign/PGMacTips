package com.pgmacdesign.pgmacutilities.networkclasses.retrofitutilities.serviceapiinterfaces;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * This interface is designed to work with 'http://www.purgomalum.com/'. Its purpose is to determine
 * if there are bad (curse/ swear) words in a String.
 * Created by pmacdowell on 8/29/2016.
 */
public interface ProfantiyCheckerInterface {

    //Not used here, but left for example. Could technically use this if empty String is passed to Client class
    public static final String BASE_URL = "http://www.purgomalum.com";

    //API pathways
    public static final String SERVICE_BASE_STRING = "/service";
    public static final String PROFANITY_BASE_STRING = "/containsprofanity";

    /**
     * Check for contains profanity. Base URL will look like this:
     * http://www.purgomalum.com/service/containsprofanity?text=QUERY_TEXT_GOES_HERE
     * @param text The String to check if it contains curse words
     * @return A simple boolean, true or false. If no text is sent, it will return "No Input"
     */
    @GET(SERVICE_BASE_STRING + PROFANITY_BASE_STRING)
    Call<String> checkProfanity(@Query("text") String text);
}
