package com.pgmacdesign.pgmactips.broadcastreceivers;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;

import com.pgmacdesign.pgmactips.utilities.L;
import com.pgmacdesign.pgmactips.utilities.MiscUtilities;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
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
    public static synchronized void setConnectivityListener(String activityName, PGConnectivityReceiver.ConnectivityReceiverListener listener) {
        if(MyApplication.mNetworkReceiver == null) {
            MyApplication.mNetworkReceiver = new PGConnectivityReceiver();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                getContext().registerReceiver(mNetworkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
            }
        }
        PGConnectivityReceiver.addConnectivityReceiverListener(activityName, listener);
    }

    //To Unregister the receiver
    public static synchronized void removeConnectivityListener(String activityName) {
        PGConnectivityReceiver.removeConnectivityReceiverListener(activityName);
        if(PGConnectivityReceiver.getNumberOfConnectivityReceivers() == 0){
            if(MyApplication.mNetworkReceiver != null ) {
                getContext().unregisterReceiver(mNetworkReceiver);
            }
            MyApplication.mNetworkReceiver = null;
        }
    }

    //2)
        a) Next, in your activity/ fragment, use this code in the onCreate or onStart:
    MyApplication.setConnectivityListener("SomeUniqueKeyName", this);

        b) Make sure to include this in your onStop() so as to prevent memory leaks
    MyApplication.removeConnectivityListener("SomeUniqueKeyName");

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
    
    
    //region Vars
    private static Map<String, ConnectivityReceiverListener> connectivityReceiverListeners;
    //endregion
    
    //region Setters, Appenders, and Clearers
    
    /**
     * Clear all connectivity receivers
     */
    public static void clearAllConnectivityReceiverListeners(){
        connectivityReceiverListeners = new HashMap<>();
    }
    
    /**
     * Remove a single connectivity receiver
     * @param activityName The Activity Name being referenced for the Map of listeners
     */
    public static void removeConnectivityReceiverListener(@NonNull String activityName){
        if(connectivityReceiverListeners == null){
            connectivityReceiverListeners = new HashMap<>();
        }
        if(connectivityReceiverListeners.containsKey(activityName)) {
            connectivityReceiverListeners.remove(activityName);
        }
        
    }
    
    /**
     * Add a single connectivity receiver
     * @param activityName The Activity Name being referenced for the Map of listeners
     * @param listener {@link ConnectivityReceiverListener}
     */
    public static void addConnectivityReceiverListener(@NonNull String activityName,
                                                       @NonNull PGConnectivityReceiver.ConnectivityReceiverListener listener){
        if(connectivityReceiverListeners == null){
            connectivityReceiverListeners = new HashMap<>();
        }
        connectivityReceiverListeners.put(activityName, listener);
    }
    
    /**
     * Clear all previous and add one connectivity receiver
     * @param activityName The Activity Name being referenced for the Map of listeners
     * @param listener {@link ConnectivityReceiverListener}
     */
    public static void setConnectivityReceiverListener(@NonNull String activityName,
                                                       @NonNull PGConnectivityReceiver.ConnectivityReceiverListener listener){
        clearAllConnectivityReceiverListeners();
        addConnectivityReceiverListener(activityName, listener);
    }
    
    /**
     * Get the number of connectivity receivers
     * @return The number of connectivity receivers
     */
    public static int getNumberOfConnectivityReceivers(){
        return (MiscUtilities.isMapNullOrEmpty(connectivityReceiverListeners) ? 0 : connectivityReceiverListeners.size());
    }
    //endregion
    
    //region Constructor and @Override
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
        
        if(connectivityReceiverListeners != null){
            if(connectivityReceiverListeners.size() > 0){
                for(Map.Entry<String, ConnectivityReceiverListener> map : connectivityReceiverListeners.entrySet()){
                    if(map == null){
                        continue;
                    }
                    ConnectivityReceiverListener connectivityReceiverListener = map.getValue();
                    if (connectivityReceiverListener != null) {
                        connectivityReceiverListener.onNetworkConnectionChanged(isConnected);
                    }
                }
            }
        }
        
    }
    
    //endregion
    
    //region Interface Listener
    public interface ConnectivityReceiverListener {
        void onNetworkConnectionChanged(boolean isConnected);
    }
    //endregion
}
