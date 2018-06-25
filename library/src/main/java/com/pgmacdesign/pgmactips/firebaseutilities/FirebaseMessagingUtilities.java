package com.pgmacdesign.pgmactips.firebaseutilities;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.gson.reflect.TypeToken;
import com.pgmacdesign.pgmactips.adaptersandlisteners.OnTaskCompleteListener;
import com.pgmacdesign.pgmactips.misc.PGMacTipsConstants;
import com.pgmacdesign.pgmactips.networkclasses.retrofitutilities.CustomConverterFactory;
import com.pgmacdesign.pgmactips.networkclasses.retrofitutilities.RetrofitClient;
import com.pgmacdesign.pgmactips.networkclasses.retrofitutilities.RetrofitParser;
import com.pgmacdesign.pgmactips.utilities.NetworkUtilities;
import com.pgmacdesign.pgmactips.utilities.StringUtilities;

import java.lang.reflect.Type;
import java.util.Map;

import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;

/**
 * This class is designed to be used with Firebase messaging.
 * Created by pmacdowell on 2017-09-29.
 */
public class FirebaseMessagingUtilities {

    //Base URL
    private static final String FIREBASE_BASE_URL = "https://fcm.googleapis.com";
    //Map <String, Object> type token
    private static final Type TYPE_MAP_OBJECT = new TypeToken<Map<String, Object>>() {
    }.getType();
    //Service interface
    private static FirebaseEndpoints service;

    /**
     * Init function
     */
    private static void init() {
        if (service == null) {
            // TODO: 2017-09-29 write in link here for universal setter / base class for logging
            service = new RetrofitClient.Builder(FirebaseEndpoints.class, FIREBASE_BASE_URL)
                    .setCustomConverterFactory(new CustomConverterFactory())
                    .setLogLevel(HttpLoggingInterceptor.Level.BODY)
                    .setTimeouts(PGMacTipsConstants.ONE_MINUTE,
                            PGMacTipsConstants.ONE_MINUTE)
                    .build().buildServiceClient();
        }
    }

    /**
     * Check on internet access before call
     * @param context
     * @return
     */
    private static boolean doIProceed(Context context) {
        if (!NetworkUtilities.haveNetworkConnection(context)) {
            return false;
        }

        //Insert more checks here
        return true;
    }

    /**
     * Send a push notification for Android to read and parse in the background.
     * For more info, see
     * {@link FirebaseMessagingUtilities#sendPushNotification(Context, OnTaskCompleteListener, String, PushNotificationsPojo)}
     */
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

    /**
     * Send a push notification for IOS to read.
     * For more info, see
     * {@link FirebaseMessagingUtilities#sendPushNotification(Context, OnTaskCompleteListener, String, PushNotificationsPojo)}
     */
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
     * @param context        {@link Context}
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
     *                       (Can be changed by altering the {didReceiveRemoteNotification}
     *                       function. See the code at the bottom of the screen for sample.
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
                    PGMacTipsConstants.TAG_FCM_FAIL_RESPONSE);
            return;
        }
        if (StringUtilities.isNullOrEmpty(firebaseApiKey)) {
            listener.onTaskComplete("Invalid Firebase API Key. You can obtain this from the console or from the google-services.json file",
                    PGMacTipsConstants.TAG_FCM_FAIL_RESPONSE);
            return;
        }
        if (!doIProceed(context)) {
            listener.onTaskComplete(PGMacTipsConstants.NO_INTERNET_STRING,
                    PGMacTipsConstants.TAG_NO_INTERNET);
            return;
        }
        init();
        Call<ResponseBody> call = service.sendPushNotification("key=" + firebaseApiKey, pojo);
        RetrofitParser.parse(new OnTaskCompleteListener() {
            @Override
            public void onTaskComplete(Object result, int customTag) {
                if (customTag == 1) {
	                Map<String, Object> res = (Map<String, Object>) result;
                    listener.onTaskComplete(res, PGMacTipsConstants.TAG_FCM_SUCCESS_RESPONSE);
                } else {
                    listener.onTaskComplete(null, PGMacTipsConstants.TAG_FCM_FAIL_RESPONSE);
                }
            }
        }, call, TYPE_MAP_OBJECT, TYPE_MAP_OBJECT, 1, 0, false);

    }


    /*
    IOS Sample code for obtaining from "data" field:
        func application(_ application: UIApplication, didReceiveRemoteNotification userInfo: [AnyHashable : Any], fetchCompletionHandler completionHandler: @escaping (UIBackgroundFetchResult) -> Void) {
            if let messageID = userInfo["gcm.message_id"] {
                print("message id is \(messageID)")
            }
            Messaging.messaging().appDidReceiveMessage(userInfo)
            completionHandler(.newData)
        }
     */
}
