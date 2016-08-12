package com.pgmacdesign.pgmacutilities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by pmacdowell on 8/12/2016.
 */
public class NetworkUtilities {

    /**
     * Checks for network connectivity either via wifi or cellular.
     * @param context The context of the activity doing the checking
     * @return A Boolean. True if they have connection, false if they do not
     */
    public static boolean haveNetworkConnection(Context context) {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;
        if(context == null){
            return false;
        }
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(
                Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

    /**
     * This checks the system-side for network connectivity, then pings the google
     * server to make sure they have internet connection. It is slightly slower and is only
     * included here in case the previous haveNetworkConnection code gets completely deprecated.
     * @param context Context to be passed
     * @return Returns a boolean, true if they have internet, false if they do not.
     */
    public static boolean haveNetworkConnection2(Context context) {
        boolean bool = false;
        if(haveNetworkConnection(context)){
            try {
                HttpURLConnection urlc = (HttpURLConnection)
                        (new URL(PGMacUtilitiesConstants.URL_GOOGLE).openConnection());
                urlc.setRequestProperty("User-Agent", "Test");
                urlc.setRequestProperty("Connection", "close");
                urlc.setConnectTimeout(1500);
                urlc.connect();
                bool = (urlc.getResponseCode() == 200);
                return bool;
            } catch (IOException e) {
                return false;
            }
        } else {
            return false;
        }
    }
}
