package com.pgmacdesign.pgmacutilities.utilities;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.support.annotation.NonNull;

/**
 * Class for managing and handling threads {@link Thread}
 * Created by pmacdowell on 2017-02-21.
 */
public class ThreadUtilities {

    /*
    // http://blogs.innovationm.com/multiple-asynctask-in-android/
    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    private static final int CORE_POOL_SIZE = CPU_COUNT + 1;
    private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;
    private static final BlockingQueue<Runnable> sPoolWorkQueue = new LinkedBlockingQueue<Runnable>(128);
    */
    /**
     * Build and returns a HandlerThread on a background thread using the passed
     * String threadname to define it.
     * @param threadName String thread name to use. If null is passed, uses the
     *                   current time in milliseconds as a String for the name
     * @return {@link HandlerThread}
     */
    public static HandlerThread getBackgroundHandlerThread(String threadName){
        if(StringUtilities.isNullOrEmpty(threadName)){
            //Default to current time in milliseconds if left null
            threadName = DateUtilities.getCurrentDateLong() + " ";
        }
        HandlerThread handlerThread = new HandlerThread(threadName);
        return handlerThread;
    }

    /**
     * Get a Handler that is running on a background thread.
     * NOTE!!! The HandlerThread that is passed as a param MUST be stopped via
     * handlerThread.quitSafely() or handlerThread.quit();
     * @param handlerThread {@link HandlerThread}Cannot be null since this will start
     *                                           the thread when it is passed.
     * @return {@link Handler}
     */
    public static Handler getBackgroundHandler(@NonNull HandlerThread handlerThread){
        handlerThread.start();
        Handler handler = new Handler(handlerThread.getLooper());
        return handler;
    }

    /**
     * Get a handler object tied to the callback implemented in the activity / fragment
     * @return {@link Handler}
     */
    public static Handler getHandlerWithCallback(Handler.Callback callback){
        Handler handler = new Handler(callback);
        return handler;
    }

    /**
     * Get the Main Thread looper
     * @return {@link Handler}
     */
    public static Handler getMainHandler(){
        Handler handler = new Handler(Looper.getMainLooper());
        return handler;
    }

}
