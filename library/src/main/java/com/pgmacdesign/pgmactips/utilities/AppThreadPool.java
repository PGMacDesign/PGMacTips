package com.pgmacdesign.pgmactips.utilities;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

import androidx.annotation.NonNull;

import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Class for managing Thread Pools as well as performing tasks on either background or foreground
 * threads depending on the end goal / need.
 * To use it in a simple way, just call {@link #getInstance()#runInBackground(Runnable)} and the
 * item will run in the background
 * Previously this class held the methods found in `ThreadUtilities` so if you find this
 * sentence by looking for that string, this is the class you are looking for.
 */
public class AppThreadPool {
	
	/*
	    // http://blogs.innovationm.com/multiple-asynctask-in-android/
    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    private static final int CORE_POOL_SIZE = CPU_COUNT + 1;
    private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;
	 */
	
	private final ScheduledThreadPoolExecutor threadPool;
	private final Handler mainThreadHandler;
	
	private static class LazyHolder {
		private static final AppThreadPool INSTANCE = new AppThreadPool();
	}
	
	public static final AppThreadPool getInstance() {
		return LazyHolder.INSTANCE;
	}
	
	private AppThreadPool() {
		this.threadPool = new ScheduledThreadPoolExecutor(this.getOptimalThreadPoolCount());
		this.mainThreadHandler = new Handler();
	}
	
	public <T> void postTask(final Callable<T> callable, final FinishInMainThreadCallback<T> callback) {
		this.threadPool.execute(() -> {
			try {
				final Object result = callable.call();
				this.mainThreadHandler.post(() -> {
					if (callback != null) {
						callback.onFinish((T) result);
					}
				});
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		});
	}
	
	/**
	 * Post a task to be completed via a runnable and have the callback trigger when it has finished
	 * @param callable
	 * @param timeInMilliSec
	 * @param callback
	 * @param <T>
	 */
	public <T> void postTask(final Callable<T> callable, long timeInMilliSec, final FinishInMainThreadCallback<T> callback) {
		this.threadPool.schedule(() -> {
			try {
				final Object result = callable.call();
				this.mainThreadHandler.post(() -> {
					if (callback != null) {
						callback.onFinish((T) result);
					}
				});
			} catch (Exception e) {
				e.printStackTrace();
			}
		}, timeInMilliSec, TimeUnit.MILLISECONDS);
	}
	
	/**
	 * Run a Runnable on the Main Thread
	 * @param runnable
	 */
	public void runOnUiThread(final Runnable runnable) {
		if (this.mainThreadHandler == null) {
			throw new IllegalThreadStateException("Original Instantiation did not occur on the main thread.");
		}
		this.mainThreadHandler.post(runnable);
	}
	
	/**
	 * Run a Runnable on a background thread
	 * @param runnable
	 */
	public void runInBackground(final Runnable runnable) {
		this.threadPool.submit(runnable);
	}
	
	/**
	 * Interface for linking back on the Main Thread
	 * @param <T>
	 */
	public interface FinishInMainThreadCallback<T> {
		void onFinish(T result);
	}
	
	//region Misc Local Utils
	
	/**
	 * Get the optimal thread pool count
	 * @return Returns the optimal pool size; will return 3 if it cannot generate a valid #
	 */
	private int getOptimalThreadPoolCount(){
		try {
			int cpuCount = Runtime.getRuntime().availableProcessors();
			int corePoolSize = cpuCount + 1;
//			int maxPoolSize = (cpuCount * 2) + 1;
			return corePoolSize;
		} catch (Exception e){
			L.e(e);
			return 3;
		}
	}
	
	//endregion
	
	//region Refactored From ThreadUtilities
	
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
	
	//endregion
}
