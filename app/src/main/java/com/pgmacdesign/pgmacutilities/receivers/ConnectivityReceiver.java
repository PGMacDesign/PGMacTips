package com.pgmacdesign.pgmacutilities.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Connectivity Receiver. Useful for detecting no internet connection (as a listener)
 * Created by pmacdowell on 8/16/2016.
 */
public class ConnectivityReceiver extends BroadcastReceiver {

    /*
    HOW TO USE:
    //1) In your MyApplication class, put this code:
    public void setConnectivityListener(ConnectivityReceiver.ConnectivityReceiverListener listener) {
        ConnectivityReceiver.connectivityReceiverListener = listener;

    //2) Next, in your activity/ fragment, use this code in the onCreate or onResume:
    MyApplication.getInstance().setConnectivityListener(this);

    //3) Lastly, make that same activity/ fragment implement:
    ConnectivityReceiver.ConnectivityReceiverListener

    //4) Which will make you implement this:
    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (!isConnected) {
            //No internet, do something?
        } else {
            //Has internet, do something?
        }

    }

     */
    public static ConnectivityReceiverListener connectivityReceiverListener;

    public ConnectivityReceiver() {
        super();
    }

    @Override
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
