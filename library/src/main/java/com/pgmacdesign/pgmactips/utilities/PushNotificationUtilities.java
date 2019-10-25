package com.pgmacdesign.pgmactips.utilities;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

/**
 * Utilizing this tutorial - https://medium.com/exploring-android/exploring-android-o-notification-channels-94cd274f604c
 * Created by pmacdowell on 2018-05-09.
 */

@RequiresApi(api = Build.VERSION_CODES.O)
public class PushNotificationUtilities {

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static NotificationManager buildNotificationManager(@NonNull Context context,
                                                               @NonNull NotificationChannel notificationChannel) throws NullPointerException{
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(notificationChannel);
        return notificationManager;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static NotificationChannel buildNotificationChannel(@NonNull String channelId,
                                                               @NonNull String channelName){
        return buildNotificationChannel(channelId, channelName,
                null, null, null, null, null);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static NotificationChannel buildNotificationChannel(@NonNull String channelId,
                                                               @NonNull String channelName,
                                                               @Nullable Integer importance,
                                                               @Nullable Boolean enableLights,
                                                               @Nullable Integer notificationColor,
                                                               @Nullable Boolean enableVibration,
                                                               @Nullable long[] vibrationPattern){
        int importanceI;
        boolean vibPatternOk;
        if(vibrationPattern == null){
            vibPatternOk = false;
        } else {
            if(vibrationPattern.length <= 0){
                vibPatternOk = false;
            } else {
                vibPatternOk = true;
            }
        }
        if(importance == null){
            importanceI = NotificationManager.IMPORTANCE_DEFAULT;
        } else {
            if(importance >= NotificationManager.IMPORTANCE_NONE &&
		            importance <= NotificationManager.IMPORTANCE_HIGH){
                importanceI = importance;
            } else {
                importanceI = NotificationManager.IMPORTANCE_DEFAULT;
            }
        }
        NotificationChannel notificationChannel = new NotificationChannel(channelId, channelName, importanceI);
        notificationChannel.enableLights((enableLights == null) ? true : enableLights);
        notificationChannel.setLightColor((notificationColor == null) ? Color.RED : notificationColor);
        notificationChannel.enableVibration((enableVibration == null) ? true : enableVibration);
        notificationChannel.setVibrationPattern((vibPatternOk) ? vibrationPattern :
//                new long[]{100, 200, 300, 400, 500, 400, 300, 200, 100});
                new long[]{200, 200});
        return notificationChannel;
    }
}
