package com.pgmacdesign.pgmactips.utilities;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RemoteViews;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.lang.reflect.Method;

/**
 * Utilizing this tutorial - https://medium.com/exploring-android/exploring-android-o-notification-channels-94cd274f604c
 * Created by pmacdowell on 2018-05-09.
 */

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
	
    //region Adjusting the Notification UI Elements
	
	// Method reference to Notification.Builder#makeContentView
	private static Method MAKE_CONTENT_VIEW_METHOD;
	private static void initMethodGenerator() {
		Method m = null;
		try {
			m = Notification.Builder.class.getDeclaredMethod("makeContentView");
			m.setAccessible(true);
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
		MAKE_CONTENT_VIEW_METHOD = m;
	}
	
	/**
	 * Build a Notification with a background resource drawable
	 * @param context
	 * @param builder
	 * @param res
	 * @return
	 */
	public static Notification buildWithBackgroundResource(Context context,
	                                                       Notification.Builder builder,
	                                                       @DrawableRes int res) {
		if (MAKE_CONTENT_VIEW_METHOD == null){
			initMethodGenerator();
			if(MAKE_CONTENT_VIEW_METHOD == null) {
				return buildNotification(builder);
			}
		}
		RemoteViews remoteViews = obtainRemoteViews(builder);
		Notification notification = buildNotification(builder);
		
		// Find the root of the content view and apply the background to it
		if (remoteViews != null) {
			View v = LayoutInflater.from(context).inflate(remoteViews.getLayoutId(), null);
			remoteViews.setInt(v.getId(), "setBackgroundResource", res);
		}
		
		return notification;
	}
	
	/**
	 * Build a notification with the background color
	 * @param context
	 * @param builder
	 * @param color
	 * @return
	 */
	public static Notification buildWithBackgroundColor(Context context,
	                                                    Notification.Builder builder,
	                                                    @ColorInt int color) {
		if (MAKE_CONTENT_VIEW_METHOD == null){
			initMethodGenerator();
			if(MAKE_CONTENT_VIEW_METHOD == null) {
				return buildNotification(builder);
			}
		}
		RemoteViews remoteViews = obtainRemoteViews(builder);
		Notification notification = buildNotification(builder);
		
		// Find the root of the content view and apply the color to it
		if (remoteViews != null) {
			View v = LayoutInflater.from(context).inflate(remoteViews.getLayoutId(), null);
			remoteViews.setInt(v.getId(), "setBackgroundColor", color);
			
			// Calculate a contrasting text color to ensure readability, and apply it to all TextViews within the notification layout
			boolean useLightText = ColorUtilities.isColorDark(color);
			int textColor = useLightText ? 0xffffffff : 0xff000000;
			applyTextColorToRemoteViews(remoteViews, v, textColor);
		}
		
		return notification;
	}
	
	/**
	 * Obtain the remote views from the builder being set
	 * @param builder
	 * @return
	 */
	private static RemoteViews obtainRemoteViews(Notification.Builder builder) {
		try {
			// Explicitly force creation of the content view and re-assign it to the notification
			RemoteViews remoteViews = (RemoteViews) MAKE_CONTENT_VIEW_METHOD.invoke(builder);
			builder.setContent(remoteViews);
			return remoteViews;
			
		} catch (Throwable ignored) {
			return null;
		}
	}
	
	/**
	 * Build the notification
	 * @param builder
	 * @return
	 */
	private static Notification buildNotification(Notification.Builder builder) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			return builder.build();
		} else {
			return builder.getNotification();
		}
	}
	
	/**
	 * Apply the text color to the view and child views
	 * @param remoteViews
	 * @param view
	 * @param color
	 */
	private static void applyTextColorToRemoteViews(RemoteViews remoteViews, View view, int color) {
		if (view instanceof ViewGroup) {
			ViewGroup vg = (ViewGroup) view;
			for (int i = 0, count = vg.getChildCount(); i < count; i++) {
				applyTextColorToRemoteViews(remoteViews, vg.getChildAt(i), color);
			}
		} else if (view instanceof TextView) {
			remoteViews.setTextColor(view.getId(), color);
		}
	}
	
	//endregion
	
}
