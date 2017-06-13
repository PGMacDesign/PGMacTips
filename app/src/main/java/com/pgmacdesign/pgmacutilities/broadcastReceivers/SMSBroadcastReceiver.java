package com.pgmacdesign.pgmacutilities.broadcastReceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.support.annotation.NonNull;
import android.telephony.SmsMessage;
import android.util.Log;

import com.google.gson.Gson;
import com.pgmacdesign.pgmacutilities.adaptersandlisteners.OnTaskCompleteListener;
import com.pgmacdesign.pgmacutilities.nonutilities.PGMacUtilitiesConstants;
import com.pgmacdesign.pgmacutilities.utilities.L;

/**
 * SMS Broadcast Receiver. See bottom for required Manifest Params
 * Created by pmacdowell on 2017-06-13.
 */
public class SMSBroadcastReceiver  extends BroadcastReceiver {

    private static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
    private static final String TAG = "SMSBroadcastReceiver";
    public static final String INTENT_FILTER_STRING = "SMSBroadcastReceiver-custom-event-name";
    private static final int TAG_SMS_RECEIVED_BROADCAST_RECEIVER =
            PGMacUtilitiesConstants.TAG_SMS_RECEIVED_BROADCAST_RECEIVER;
    private static final int TAG_SMS_RECEIVED_BROADCAST_RECEIVER_EMPTY =
            PGMacUtilitiesConstants.TAG_SMS_RECEIVED_BROADCAST_RECEIVER_EMPTY;

    //Listener to send data back on
    private OnTaskCompleteListener listener;

    public SMSBroadcastReceiver(){
        this.listener = null;
    }

    public SMSBroadcastReceiver(@NonNull OnTaskCompleteListener listener){
        this.listener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "Intent received: " + intent.getAction());

        try {
            if (intent.getAction() != null) {
                if (intent.getAction().equals(SMS_RECEIVED)) {
                    Bundle bundle = intent.getExtras();
                    if (bundle != null) {
                        Object[] pdus = (Object[]) bundle.get("pdus");
                        final SmsMessage[] messages = new SmsMessage[pdus.length];
                        for (int i = 0; i < pdus.length; i++) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i],
                                        Telephony.Sms.Intents.SMS_RECEIVED_ACTION);
                            } else {
                                messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                            }
                        }
                        if (messages.length > -1) {
                            for (SmsMessage message : messages) {
                                // TODO: Do stuff here with message received
                                L.m("MESSAGE RECEIVED == " + new Gson().toJson(message, SmsMessage.class));
                            }
                            if (this.listener != null) {
                                listener.onTaskComplete(messages, TAG_SMS_RECEIVED_BROADCAST_RECEIVER);
                                return;
                            }
                        }
                    }
                    if (this.listener != null) {
                        listener.onTaskComplete(null, TAG_SMS_RECEIVED_BROADCAST_RECEIVER_EMPTY);
                        return;
                    }

                }
            }
        } catch (Exception e){
            e.printStackTrace();
            listener.onTaskComplete(null, TAG_SMS_RECEIVED_BROADCAST_RECEIVER_EMPTY);
        }
    }

    /*
    IMPORTANT: This must be put into the Manifest for this to work:
    <manifest
        ...>
        <uses-permission android:name="android.permission.RECEIVE_SMS" />
        <application
            ...
            <!-- SMS Broadcast Receiver -->
            <receiver android:name=".SMSBroadcastReceiver">
                <intent-filter>
                    <action android:name="android.provider.Telephony.SMS_RECEIVED"></action>
                </intent-filter>
            </receiver>
            ...
        </application>
    </manifest>
     */
}