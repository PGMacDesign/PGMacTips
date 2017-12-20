package com.pgmacdesign.pgmactips.utilities;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

/**
 * Created by pmacdowell on 2017-07-18.
 */

public class OAuthUtilities implements GoogleApiClient.OnConnectionFailedListener {

    private GoogleSignInOptions gso;
    private GoogleApiClient mGoogleApiClient;
    private Activity activity;

    public OAuthUtilities(Activity activity){
        this.activity = activity;
    }


    public void loginWithGoogle(){
        /*
        gso = new GoogleSignInOptions.Builder(
                GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        mGoogleApiClient = new GoogleApiClient.Builder(activity)
                .enableAutoManage(activity, this)
                .addApi(com.google.android.gms.auth.api.Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        try {
            Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
            startActivityForResult(signInIntent, Constants.TAG_GOOGLE_PLUS_SIGNIN);
        }catch (Exception e){
            e.printStackTrace();
        }
        */
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        /*
        if(requestCode == Constants.TAG_GOOGLE_PLUS_SIGNIN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if(result != null) {
                if (result.isSuccess()) {
                    GoogleSignInAccount acct = result.getSignInAccount();
                    // Get account information
                    if (acct != null) {
                        try {
                            //Use the data here to set and return
                        } catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        */

    }

    /**
     * Google login connection result
     * @param connectionResult
     */
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        //Triggers when google signin failed
    }

    /**
     * Enum for social network types
     */
    public enum socialNetworks {
        FACEBOOK, TWITTER, GOOGLEPLUS, INSTAGRAM, OTHER
    }
}
