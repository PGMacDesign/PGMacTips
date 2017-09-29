package com.pgmacdesign.pgmacutilities.firebaseutilities;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.gson.reflect.TypeToken;
import com.pgmacdesign.pgmacutilities.adaptersandlisteners.OnTaskCompleteListener;
import com.pgmacdesign.pgmacutilities.misc.PGMacUtilitiesConstants;
import com.pgmacdesign.pgmacutilities.networkclasses.retrofitutilities.CustomConverterFactory;
import com.pgmacdesign.pgmacutilities.networkclasses.retrofitutilities.RetrofitClient;
import com.pgmacdesign.pgmacutilities.networkclasses.retrofitutilities.RetrofitParser;
import com.pgmacdesign.pgmacutilities.utilities.NetworkUtilities;
import com.pgmacdesign.pgmacutilities.utilities.StringUtilities;

import java.lang.reflect.Type;
import java.util.Map;

import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;

/**
 * Created by pmacdowell on 2017-09-29.
 */

public class FirebaseMessagingUtilities {

    private static final String FIREBASE_BASE_URL = "https://fcm.googleapis.com";
    private static final Type TYPE_MAP_OBJECT = new TypeToken<Map<String, Object>>() {
    }.getType();

    private static FirebaseEndpoints service;

    private static void init(@NonNull String firebaseApiKey) {
        if (service == null) {
            // TODO: 2017-09-29 write in link here for universal setter / base class for logging
            service = new RetrofitClient.Builder(FirebaseEndpoints.class, FIREBASE_BASE_URL)
                    .setCustomConverterFactory(new CustomConverterFactory())
                    .setLogLevel(HttpLoggingInterceptor.Level.BODY)
                    .setTimeouts(PGMacUtilitiesConstants.ONE_MINUTE,
                            PGMacUtilitiesConstants.ONE_MINUTE)
                    .build().buildServiceClient();
        }
    }

    private static boolean doIProceed(Context context) {
        if (!NetworkUtilities.haveNetworkConnection(context)) {
            return false;
        }

        //Insert more checks here
        return true;
    }

    public static void sendAndroidPushNotification(@NonNull Context context,
                                                   @NonNull final OnTaskCompleteListener listener,
                                                   @NonNull String firebaseApiKey,
                                                   @NonNull String fcmUserId,
                                                   @NonNull Map<String, Object> keyValuePairs) {
        PushNotificationsPojo pojo = new PushNotificationsPojo();
        pojo.setMapData(keyValuePairs);
        pojo.setTo(fcmUserId);
        FirebaseMessagingUtilities.sendPushNotification(context, listener, firebaseApiKey, pojo);
    }

    public static void sendIOSPushNotification(@NonNull Context context,
                                               @NonNull final OnTaskCompleteListener listener,
                                               @NonNull String firebaseApiKey,
                                               @NonNull String fcmUserId,
                                               @NonNull PushNotificationsPojo.CustomNotificationObject notificationObject) {
        PushNotificationsPojo pojo = new PushNotificationsPojo();
        pojo.setNotification(notificationObject);
        pojo.setTo(fcmUserId);
        FirebaseMessagingUtilities.sendPushNotification(context, listener, firebaseApiKey, pojo);
    }

    /**
     * Send a push notification through firebase.
     *
     * @param context        Context
     * @param listener       {@link OnTaskCompleteListener}
     * @param firebaseApiKey Firebase Messaging API Key. NOTE! If receiving 401 unauthorized errors,
     *                       make sure to get the correct, longer server key. See this answer for details:
     *                       https://stackoverflow.com/a/40352386/2480714
     * @param pojo           {@link PushNotificationsPojo} Important Note! As of October, 2017, there are three
     *                       different 'types' of messages that can be sent.
     *                         1) JSON with "data" included and "notification" not included:
     *                       For Android: This will be read the foreground && background.
     *                       Also, it will trigger the onMessageReceived() function even if the
     *                       app is closed.
     *                       For IOS: This will not be read by default and user will not receive notification.
     *                       (Can be changed by altering the {DDDDDD} function.
     *                         2) JSON with "data" not included and "notification" included.
     *                       For Android: This will be read in both foreground and background,
     *                       but will not trigger the onMessageReceived() function so nothing can be
     *                       parsed, nor any actions performed in the background.
     *                       For IOS: This will be read by default and shown to the user in the system tray up top.
     *                         3) JSON with both "data" included and "notification" included.
     *                       For Android: This will be read in both the foreground and background,
     *                       but will not trigger the onMessageReceived() function so nothing can be
     *                       parsed, nor any actions performed in the background.
     *                       For IOS: This will be read by default and shown to the user in the system tray up top.
     *                         For more information, please see these links:
     *                         1) https://stackoverflow.com/questions/40311279/firebase-onmessagereceived-not-called-when-app-is-in-the-background
     *                         2) https://stackoverflow.com/questions/40026528/how-do-i-send-fcm-messages-from-an-android-device-to-the-server
     *                         3) https://stackoverflow.com/questions/45286202/how-does-one-distinguish-between-android-and-ios-firebase-ids-for-push-notificat
     */
    public static void sendPushNotification(@NonNull Context context,
                                            @NonNull final OnTaskCompleteListener listener,
                                            @NonNull String firebaseApiKey,
                                            @NonNull PushNotificationsPojo pojo) {
        if (StringUtilities.isNullOrEmpty(pojo.getTo())) {
            listener.onTaskComplete("Not a valid recipient. Please check the 'To' field in your object",
                    PGMacUtilitiesConstants.TAG_FCM_FAIL_RESPONSE);
            return;
        }
        if (StringUtilities.isNullOrEmpty(firebaseApiKey)) {
            listener.onTaskComplete("Invalid Firebase API Key. You can obtain this from the console or from the google-services.json file",
                    PGMacUtilitiesConstants.TAG_FCM_FAIL_RESPONSE);
            return;
        }
        if (!doIProceed(context)) {
            listener.onTaskComplete(PGMacUtilitiesConstants.NO_INTERNET_STRING,
                    PGMacUtilitiesConstants.TAG_NO_INTERNET);
            return;
        }
        init(firebaseApiKey);
        Call<Map<String, Object>> call = service.sendPushNotification("key=" + firebaseApiKey, pojo);
        RetrofitParser.parse(new OnTaskCompleteListener() {
            @Override
            public void onTaskComplete(Object result, int customTag) {
                if (customTag == 1) {
                    Map<String, Object> response = (Map<String, Object>) result;
                    listener.onTaskComplete(response, PGMacUtilitiesConstants.TAG_FCM_SUCCESS_RESPONSE);
                } else {
                    listener.onTaskComplete(null, PGMacUtilitiesConstants.TAG_FCM_FAIL_RESPONSE);
                }
            }
        }, call, TYPE_MAP_OBJECT, TYPE_MAP_OBJECT, 1, 0, false);
    }
}
