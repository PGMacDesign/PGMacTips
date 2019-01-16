package com.pgmacdesign.pgmactips.broadcastreceivers;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import androidx.annotation.RequiresPermission;

/**
 * Connectivity Receiver. Useful for detecting no internet connection (as a listener)
 * Requires Permission: <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
 * Created by pmacdowell on 8/16/2016.
 * NOTE! This has been deprecated as of Android N and above as of 2018-03-20
 */
public class PGConnectivityReceiver extends BroadcastReceiver {

    //
    /*
    HOW TO USE:
    //1) In your MyApplication class, put this code:

    //variable outside of the classes:
    private static BroadcastReceiver mNetworkReceiver;

    //To register the receiver
    public static synchronized void setConnectivityListener(PGConnectivityReceiver.ConnectivityReceiverListener listener) {
        if(MyApplication.mNetworkReceiver == null) {
            MyApplication.mNetworkReceiver = new PGConnectivityReceiver();
            //This next line is required for versions Nougat (24) or higher
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                getContext().registerReceiver(mNetworkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
            }
        }
        PGConnectivityReceiver.connectivityReceiverListener = listener;
    }

    //To Unregister the receiver
    public static synchronized void removeConnectivityListener() {
        if(MyApplication.mNetworkReceiver != null ) {
            getContext().unregisterReceiver(mNetworkReceiver);
        }
        PGConnectivityReceiver.connectivityReceiverListener = null;
        MyApplication.mNetworkReceiver = null;
    }

    //2)
        a) Next, in your activity/ fragment, use this code in the onCreate or onStart:
    MyApplication.getInstance().setConnectivityListener(this);

        b) Make sure to include this in your onStop() so as to prevent memory leaks


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
        <receiver android:name="com.pgmacdesign.pgmactips.broadcastreceivers.PGConnectivityReceiver"
            android:enabled="true"
            >
            <intent-filter>
                <action android:name="android.net.conn.CONNECTIVITY_CHANGE" />
            </intent-filter>
        </receiver>

     */
    public static ConnectivityReceiverListener connectivityReceiverListener;

    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    public PGConnectivityReceiver() {
        super();
    }

    /**
     * Note! Requires permission to run, will throw exception otherwise
     */
    @Override
    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    public void onReceive(Context context, Intent arg1) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
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
