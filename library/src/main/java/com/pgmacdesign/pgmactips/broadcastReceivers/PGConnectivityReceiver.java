package com.pgmacdesign.pgmactips.broadcastReceivers;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Connectivity Receiver. Useful for detecting no internet connection (as a listener)
 * Requires Permission: <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
 * Created by pmacdowell on 8/16/2016.
 * NOTE! This has been deprecated as of Android N and above as of 2018-03-20
 */
public class PGConnectivityReceiver extends BroadcastReceiver {

    // TODO: 2018-03-20 refactor to usable
    /*
    HOW TO USE:
    //1) In your MyApplication class, put this code:
    public void setConnectivityListener(PGConnectivityReceiver.ConnectivityReceiverListener listener) {
        PGConnectivityReceiver.connectivityReceiverListener = listener;
    }

    //2) Next, in your activity/ fragment, use this code in the onCreate or onResume:
    MyApplication.getInstance().setConnectivityListener(this);

    //3) Make that same activity/ fragment implement:
    PGConnectivityReceiver.ConnectivityReceiverListener

    //4) Which will make you implement this:
    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (!isConnected) {
            //No internet, do something?
        } else {
            //Has internet, do something?
        }

    }

    //5) Lastly, add this line to your manifest
        <receiver android:name="com.pgmacdesign.pgmactips.broadcastReceivers.PGConnectivityReceiver">
            <intent-filter>
                <action android:name="android.net.conn.CONNECTIVITY_CHANGE" />
            </intent-filter>
        </receiver>

     */
    public static ConnectivityReceiverListener connectivityReceiverListener;

    public PGConnectivityReceiver() {
        super();
    }

    /**
     * Note! Requires permission to run, will throw exception otherwise
     */
    @Override
    public void onReceive(Context context, Intent arg1) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        @SuppressLint("MissingPermission") NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null
                && activeNetwork.isConnectedOrConnecting();

        if (connectivityReceiverListener != null) {
            connectivityReceiverListener.onNetworkConnectionChanged(isConnected);
        }
    }

    public interface ConnectivityReceiverListener {
        void onNetworkConnectionChanged(boolean isConnected);
    }
}
