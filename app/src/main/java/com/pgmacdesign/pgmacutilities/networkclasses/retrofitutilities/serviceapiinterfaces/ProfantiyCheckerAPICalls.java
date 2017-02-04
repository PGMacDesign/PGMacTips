package com.pgmacdesign.pgmacutilities.networkclasses.retrofitutilities.serviceapiinterfaces;

import android.content.Context;
import android.os.NetworkOnMainThreadException;
import android.support.annotation.NonNull;

import com.pgmacdesign.pgmacutilities.adaptersandlisteners.OnTaskCompleteListener;
import com.pgmacdesign.pgmacutilities.networkclasses.retrofitutilities.RetrofitClient;
import com.pgmacdesign.pgmacutilities.networkclasses.retrofitutilities.RetrofitErrorHandling;
import com.pgmacdesign.pgmacutilities.nonutilities.PGMacUtilitiesConstants;
import com.pgmacdesign.pgmacutilities.utilities.NetworkUtilities;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * This serves as a wrapper class for accessing the PurgoMaluminterface. Use it to check for curse
 * words / profanity in a String.
 * Class Uses Permission android.permission.ACCESS_NETWORK_STATE.
 * {@link com.pgmacdesign.pgmacutilities.utilities.PermissionUtilities.permissionsEnum}
 * Created by pmacdowell on 8/29/2016.
 */
public class ProfantiyCheckerAPICalls {

    private static ProfantiyCheckerInterface serviceInterface;
    private static final String BASE_URL = "http://www.purgomalum.com";

    static {
        init();
    }

    /**
     * This will make a web call to check for profanity in a word passed in.
     * NOTE! This does handle asynchronous behavior and will therefore return the data along
     * the listener; this method will always return null as a result.
     * @param context Context
     * @param listener Listener to send the data back on {@link OnTaskCompleteListener}
     * @param curseWordToCheck String word to check if it contains profanity
     */
    public static void checkProfanityAsynchronous(@NonNull Context context,
                                                 @NonNull final OnTaskCompleteListener listener,
                                                 String curseWordToCheck){
        //Check on internet connectivity before making call
        if (!canIProceed(context)) {
            if(listener != null){
                listener.onTaskComplete(null, PGMacUtilitiesConstants.TAG_RETROFIT_CALL_FAILED);
            }
            return;
        }

        //Make the Call. This handles asyncrhonous(ity) and doesn't require AsyncTask, so we can call enqueue
        Call<ResponseBody> call = serviceInterface.checkProfanity(curseWordToCheck);
        call.enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                //Check for response or not
                if(!response.isSuccessful()){
                    //Response was not successful. Send to error Handler
                    String str = RetrofitErrorHandling.parseErrorResponse(response);
                    listener.onTaskComplete(str, PGMacUtilitiesConstants.TAG_RETROFIT_CALL_FAILED);

                } else {
                    //Response was successful. Send back via listener
                    try {
                        ResponseBody body = response.body();
                        Boolean bool = isConvertReturnTypeObject(body);
                        response.message();
                        String str = body.string();
                        int whichTagToUse;
                        boolean whichToSendBack;

                        if(str.equalsIgnoreCase("true")){
                            whichTagToUse = PGMacUtilitiesConstants.TAG_RETROFIT_CALL_SUCCESS_BOOLEAN;
                            whichToSendBack = true;

                        } else if(str.equalsIgnoreCase("false")){
                            whichTagToUse = PGMacUtilitiesConstants.TAG_RETROFIT_CALL_SUCCESS_BOOLEAN;
                            whichToSendBack = false;

                        } else {
                            //This will handle empty string requests as it returns "No Input" from the server
                            whichTagToUse = PGMacUtilitiesConstants.TAG_RETROFIT_CALL_SUCCESS_BOOLEAN;
                            whichToSendBack = false;
                        }

                        listener.onTaskComplete(whichToSendBack, whichTagToUse);
                    } catch (NullPointerException e){
                        e.printStackTrace();
                        //In case the server sends back weird stuff, handle the responses
                        listener.onTaskComplete(null, PGMacUtilitiesConstants.TAG_RETROFIT_CALL_FAILED);
                    } catch (IOException e){
                        e.printStackTrace();
                    }
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                listener.onTaskComplete(null, PGMacUtilitiesConstants.TAG_RETROFIT_CALL_FAILED);
            }
        });
    }


    /**
     * This will make a web call to check for profanity in a word passed in.
     * NOTE! This does NOT handle asynchronous behavior and assumes that this will be called
     * within a thread or AsyncTask. If this runs without the aid of an asynchronous thread, it
     * will throw the error
     * @param context Context
     * @param curseWordToCheck String word to check if it contains profanity
     * @return True or false if parsed correctly, null if it parses incorrectly
     * @throws NetworkOnMainThreadException Will throw if not run on an Asynchronous thread
     */
    public static Boolean checkProfanity(@NonNull Context context, String curseWordToCheck) {
        //Check on internet connectivity before making call
        if (!canIProceed(context)) {
            return null;
        }
        //Make the Call. This does not handle asyncrhonous(ity) and does require AsyncTask. We can call execute
        Call<ResponseBody> call = serviceInterface.checkProfanity(curseWordToCheck);
        Boolean boolToReturn = null;
        try {
            Response response = call.execute();
            //Response was not successful, handle error response
            if(!response.isSuccessful()){
                //Response was not successful. Send to error Handler
                //Boolean bool = isConvertReturnTypeObject(response.body());
                return RetrofitErrorHandling.parseBooleanResponse(response);

            } else {
                //Response was successful. Send back via listener
                Object obj = response.body();
                //ResponseBody repsonseBody = response.body().string();
                //ResponseBody rb = response.body().string();
                //String str = rb.string();
                //return StringUtilities.convertStringToBoolean(str);
                return null;
            }
        } catch (IOException e0){
            e0.printStackTrace();
            boolToReturn = null;
        } finally {
            return boolToReturn;
        }
    }

    /**
     * Init method every time something is called. Initializes the client
     */
    private static void init(){
        if(serviceInterface == null) {
            RetrofitClient retrofitClient = new RetrofitClient(
                    ProfantiyCheckerInterface.class, BASE_URL);
            retrofitClient.setHeaders(RetrofitClient.getApplicationJSONMap());
            serviceInterface = retrofitClient.buildServiceClient();
        }
        return;
    }

    /**
     * Put pre-web call checks here. Things like, do we have internet connectivity and / or is the
     * battery level over X%, etc.
     * @param context Android Context
     * @return boolean, true if ok to proceed with call, false it not
     * NOTE! Must check for android.permission.ACCESS_NETWORK_STATE permission before calling
     */
    private static boolean canIProceed(Context context){
        if(context == null){
            return false;
        }
        boolean bool = NetworkUtilities.haveNetworkConnection3(context);
        //Add more here if checking for anything other than just web connectivity
        // TODO: 8/29/2016 add global receiver here for snackbars
        return bool;
    }


    /**
     * Used for checking the return type from the server.
     * @param responseBody Allows for custom type converter
     * @return Boolean. True if an object, false if it is a String/ boolean, null if it is null
     */
    private static Boolean isConvertReturnTypeObject(ResponseBody responseBody){
        if(responseBody == null){
            return null;
        }
        try {
            String str = (String) responseBody.string();
            str = str.trim();
            if(str.equalsIgnoreCase("true") ||
                    str.equalsIgnoreCase("false") ||
                    str.equalsIgnoreCase("success") ||
                    str.equalsIgnoreCase("")
                    ){
                return false;
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        try {
            Boolean bool = Boolean.parseBoolean(responseBody.string());
            if(bool != null){
                return false;
            }
        } catch (Exception e){}
        return true;
    }
}
