package com.pgmacdesign.pgmactips.broadcastreceivers;

import android.content.Context;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;

import com.pgmacdesign.pgmactips.adaptersandlisteners.OnTaskCompleteListener;

/**
 * Created by pmacdowell on 2017-06-13.
 */

public class LocalSMSBroadcastReceiver {

    private Context context;
    private OnTaskCompleteListener listener;
    private SMSBroadcastReceiver broadcastReceiver;

    public LocalSMSBroadcastReceiver(Context context, @NonNull OnTaskCompleteListener listener){
        this.listener = listener;
        this.context = context;
        this.broadcastReceiver = new SMSBroadcastReceiver(listener);
    }

    public void startReceiver(String fromContents){
        IntentFilter iff= new IntentFilter(SMSBroadcastReceiver.INTENT_FILTER_STRING);
        try {
            LocalBroadcastManager.getInstance(context).registerReceiver(broadcastReceiver, iff);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void stopReceiver(){
        try {
            LocalBroadcastManager.getInstance(context).unregisterReceiver(broadcastReceiver);
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
