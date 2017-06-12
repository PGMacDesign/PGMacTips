package com.pgmacdesign.pgmacutilities.utilities;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;

import com.pgmacdesign.pgmacutilities.adaptersandlisteners.OnTaskCompleteListener;
import com.pgmacdesign.pgmacutilities.nonutilities.PGMacUtilitiesConstants;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Utility for handling Timers and allowing access to main UI Thread through a handler
 * Created by pmacdowell on 2017-06-12.
 */
public class TimerUtilities {

    public static final int TAG_TIMER_UTILITIES_FINISHED =
            PGMacUtilitiesConstants.TAG_TIMER_UTILITIES_FINISHED;
    public static final int TAG_TIMER_UTILITIES_FINISHED_WITH_DATA =
            PGMacUtilitiesConstants.TAG_TIMER_UTILITIES_FINISHED_WITH_DATA;

    private static Timer localTimer;

    /**
     * Overloaded method to allow for null object and no time (Defaults to 1 second /
     * 1000 milliseconds) to be passed in shorthand.
     * For more info, see {@link TimerUtilities#startTimer(OnTaskCompleteListener, Long, Object)}
     * @param listener
     */
    public static void startTimer(@NonNull final OnTaskCompleteListener listener){
        startTimer(listener, null, null);
    }

    /**
     * Overloaded Function to allow for null object to be passed in shorthand.
     * For more info, see {@link TimerUtilities#startTimer(OnTaskCompleteListener, Long, Object)}
     * @param listener
     * @param timeInMilliseconds
     */
    public static void startTimer(@NonNull final OnTaskCompleteListener listener,
                                  Long timeInMilliseconds){
        startTimer(listener, timeInMilliseconds, null);
    }

    /**
     * Start a timer that will pass back a response on the {@link OnTaskCompleteListener} and
     * if the dataToReturn is not null, it will include that too.
     * @param listener Listener to pass data back on. {@link OnTaskCompleteListener}
     * @param timeInMilliseconds Time (in milliseconds) for timer to run. Must be >0 milliseconds.
     *                           If null, will default to 1 second (1000 milliseconds)
     * @param dataToReturn Optional Data object to return. If null, will pass back result along
     *                     the listener with the tag
     *                     {@link TimerUtilities#TAG_TIMER_UTILITIES_FINISHED}
     *                     If not null, it will pass back the result along the listener with the tag:
     *                     {@link TimerUtilities#TAG_TIMER_UTILITIES_FINISHED_WITH_DATA}
     */
    public static void startTimer(@NonNull final OnTaskCompleteListener listener,
                                  Long timeInMilliseconds, final Object dataToReturn){
        //Default to one second in case null is passed
        if(timeInMilliseconds == null){
            timeInMilliseconds = 1000L;
        }
        //Bail out if timer is set to zero or less
        if(timeInMilliseconds <= 0){
            return;
        }
        final Handler handler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg) {
                if(dataToReturn == null){
                    listener.onTaskComplete(dataToReturn,
                            TAG_TIMER_UTILITIES_FINISHED_WITH_DATA);
                } else {
                    listener.onTaskComplete(null,
                            TAG_TIMER_UTILITIES_FINISHED);
                }
            }
        };
        if(localTimer == null){
            localTimer = new Timer();
        }
        localTimer.cancel();
        localTimer = new Timer();
        localTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.sendMessage(new Message());
            }
        }, (timeInMilliseconds));
    }

}
