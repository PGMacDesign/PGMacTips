package com.pgmacdesign.pgmactips.utilities;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;

import com.pgmacdesign.pgmactips.adaptersandlisteners.OnTaskCompleteListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class for managing the state of the application and determining of the application or a specific
 * activity is currently in the foreground, background, or neither.
 * To use, call the {@link ApplicationStateManagementUtilities#getInstance(Application)} or use
 * the overloaded method to pass in a listener to listen for all callbacks.
 * {@link ApplicationStateManagementUtilities#getInstance(Application, OnTaskCompleteListener)}
 */
public class ApplicationStateManagementUtilities {
	
	//region Public static final vars
	public static final int LIFECYCLE_CALLBACK_ACTIVITY_CREATED = 0;
	public static final int LIFECYCLE_CALLBACK_ACTIVITY_STARTED = 1;
	public static final int LIFECYCLE_CALLBACK_ACTIVITY_RESUMED = 2;
	public static final int LIFECYCLE_CALLBACK_ACTIVITY_PAUSED = 3;
	public static final int LIFECYCLE_CALLBACK_ACTIVITY_SAVE_INSTANCE_STATE = 4;
	public static final int LIFECYCLE_CALLBACK_ACTIVITY_STOPPED = 5;
	public static final int LIFECYCLE_CALLBACK_ACTIVITY_DESTROYED = 6;
	public static final int ACTIVITY_IS_IN_FOREGROUND = 7;
	public static final int ACTIVITY_IS_IN_BACKGROUND = 8;
	public static final int ACTIVITY_HAS_BEEN_DESTROYED = 9;
	public static final int APPLICATION_IS_IN_FOREGROUND = 10;
	public static final int APPLICATION_IS_IN_BACKGROUND = 11;
	//endregion
	
	//region Vars
	private Map<String, ActivityState> statesOfActivities;
	private OnTaskCompleteListener listener, userPassedCallback;
	private MyActivityLifecycleCallbacks mCallbacks;
	private ActivityState stateOfApplication;
	private static ApplicationStateManagementUtilities instance;
	//endregion
	
	//region Public Static Initializers
	
	/**
	 * Get an Instance to use
	 * @param application Your application (Class that extends {@link Application})
	 * @return Instance of this class
	 */
	public static ApplicationStateManagementUtilities getInstance(Application application){
		if(ApplicationStateManagementUtilities.instance == null){
			ApplicationStateManagementUtilities.instance = new ApplicationStateManagementUtilities(application, null);
		}
		return ApplicationStateManagementUtilities.instance;
	}
	
	/**
	 * Get an Instance to use
	 * @param application Your application (Class that extends {@link Application})
	 * @param listener Optional listener. If you send it, you will receive callbacks on all event
	 *                 changes that are utilized in this class as well as some additional ones that
	 *                 are unused, but may be useful to you as the activity owner.
	 * @return Instance of this class
	 */
	public static ApplicationStateManagementUtilities getInstance(Application application, OnTaskCompleteListener listener){
		if(ApplicationStateManagementUtilities.instance == null){
			ApplicationStateManagementUtilities.instance = new ApplicationStateManagementUtilities(application, listener);
		}
		return ApplicationStateManagementUtilities.instance;
	}
	
	//endregion
	
	//region Private Constructors and init
	
	/**
	 * Constructor
	 * @param application
	 * @param userPassedCallback1
	 */
	private ApplicationStateManagementUtilities(@NonNull Application application, OnTaskCompleteListener userPassedCallback1){
		this.userPassedCallback = userPassedCallback1;
		this.stateOfApplication = ActivityState.Unknown;
		this.statesOfActivities = new HashMap<>();
		this.listener = new OnTaskCompleteListener() {
			@Override
			public void onTaskComplete(Object result, int customTag) {
				if(userPassedCallback != null){
					userPassedCallback.onTaskComplete(result, customTag);
				}
				String activityStringName;
				try {
					activityStringName = (String) result;
				} catch (ClassCastException e){
					activityStringName = "Unknown";
				}
				switch (customTag){
					case LIFECYCLE_CALLBACK_ACTIVITY_CREATED:
						//Unused as of now, but keeping here for future use
						break;
					
					case LIFECYCLE_CALLBACK_ACTIVITY_STARTED:
						//Unused as of now, but keeping here for future use
						break;
					
					case LIFECYCLE_CALLBACK_ACTIVITY_RESUMED:
						addItemToHashMap(activityStringName, ActivityState.InForeground);
						break;
					
					case LIFECYCLE_CALLBACK_ACTIVITY_SAVE_INSTANCE_STATE:
						//Unused as of now, but keeping here for future use
						break;
					
					case LIFECYCLE_CALLBACK_ACTIVITY_STOPPED:
						addItemToHashMap(activityStringName, ActivityState.InBackground);
						break;
					
					case LIFECYCLE_CALLBACK_ACTIVITY_DESTROYED:
						removeItemFromHashMap(activityStringName);
						break;
					
					case ACTIVITY_IS_IN_FOREGROUND:
						stateOfApplication = ActivityState.InForeground;
						break;
						
					case ACTIVITY_IS_IN_BACKGROUND:
						//Unused as of now, but keeping here for future use
						break;
					
					case ACTIVITY_HAS_BEEN_DESTROYED:
						//Unused as of now, but keeping here for future use
						break;
					
					case APPLICATION_IS_IN_FOREGROUND:
						stateOfApplication = ActivityState.InForeground;
						break;
					
					case APPLICATION_IS_IN_BACKGROUND:
						stateOfApplication = ActivityState.InBackground;
						break;
				}
			}
		};
		this.mCallbacks = new MyActivityLifecycleCallbacks(this.listener);
		application.registerActivityLifecycleCallbacks(this.mCallbacks);
	}
	
	
	//endregion
	
	//region Private Methods and Classes
	
	/**
	 * Add an item to the hashmap
	 * @param activityStringName
	 * @param state
	 */
	private void addItemToHashMap(@NonNull String activityStringName, ActivityState state){
		this.statesOfActivities.put(activityStringName, state);
		switch (state){
			case InBackground:
				this.listener.onTaskComplete(activityStringName, ACTIVITY_IS_IN_BACKGROUND);
				if(this.isAtLeastOneActivityInForeground()){
					this.listener.onTaskComplete(null, APPLICATION_IS_IN_FOREGROUND);
				} else {
					this.listener.onTaskComplete(null, APPLICATION_IS_IN_BACKGROUND);
				}
				break;
			case InForeground:
				this.listener.onTaskComplete(activityStringName, ACTIVITY_IS_IN_FOREGROUND);
				this.listener.onTaskComplete(null, APPLICATION_IS_IN_FOREGROUND);
				break;
		}
	}
	
	/**
	 * Remove an item from the hashmap
	 * @param activityStringName
	 */
	private void removeItemFromHashMap(@NonNull String activityStringName){
		this.statesOfActivities.remove(activityStringName);
		this.listener.onTaskComplete(activityStringName, ACTIVITY_HAS_BEEN_DESTROYED);
		if (MiscUtilities.isMapNullOrEmpty(this.statesOfActivities)) {
			this.listener.onTaskComplete(null, APPLICATION_IS_IN_BACKGROUND);
			return;
		}
		if(!this.isAtLeastOneActivityInForeground()){
			this.listener.onTaskComplete(null, APPLICATION_IS_IN_FOREGROUND);
		}
	}
	
	/**
	 * Lifecycle callbacks
	 */
	private static class MyActivityLifecycleCallbacks implements Application.ActivityLifecycleCallbacks {
		
		private OnTaskCompleteListener listener;
		
		MyActivityLifecycleCallbacks(OnTaskCompleteListener callback){
			this.listener = callback;
		}
		
		@Override
		public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
			if(activity != null) {
				this.listener.onTaskComplete(activity.getClass().getSimpleName(), LIFECYCLE_CALLBACK_ACTIVITY_CREATED);
			}
		}
		
		@Override
		public void onActivityStarted(Activity activity) {
			if(activity != null) {
				this.listener.onTaskComplete(activity.getClass().getSimpleName(), LIFECYCLE_CALLBACK_ACTIVITY_STARTED);
			}
		}
		
		@Override
		public void onActivityResumed(Activity activity) {
			if(activity != null) {
				this.listener.onTaskComplete(activity.getClass().getSimpleName(), LIFECYCLE_CALLBACK_ACTIVITY_RESUMED);
			}
		}
		
		@Override
		public void onActivityPaused(Activity activity) {
			if(activity != null) {
				this.listener.onTaskComplete(activity.getClass().getSimpleName(), LIFECYCLE_CALLBACK_ACTIVITY_PAUSED);
			}
		}
		
		@Override
		public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
			if(activity != null) {
				this.listener.onTaskComplete(activity.getClass().getSimpleName(), LIFECYCLE_CALLBACK_ACTIVITY_SAVE_INSTANCE_STATE);
			}
		}
		
		@Override
		public void onActivityStopped(Activity activity) {
			if(activity != null) {
				this.listener.onTaskComplete(activity.getClass().getSimpleName(), LIFECYCLE_CALLBACK_ACTIVITY_STOPPED);
			}
		}
		
		@Override
		public void onActivityDestroyed(Activity activity) {
			if(activity != null) {
				this.listener.onTaskComplete(activity.getClass().getSimpleName(), LIFECYCLE_CALLBACK_ACTIVITY_DESTROYED);
			}
		}
	}
	
	//endregion
	
	//region State Enums
	
	public static enum ActivityState {
		InForeground, InBackground, Unknown
	}
	
	//endregion
	
	//region Public Methods
	
	/**
	 * Check if at least one activity is in the foreground
	 * @return if true, it is, else, is not
	 */
	public boolean isAtLeastOneActivityInForeground(){
		for(Map.Entry<String, ActivityState> map : statesOfActivities.entrySet()){
			if(map != null){
				switch (map.getValue()){
					case InForeground:
						return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * Check if the application is in the foreground
	 * @return if true, it is, else, is not
	 */
	public boolean isApplicationInForeground(){
		return this.isAtLeastOneActivityInForeground();
	}
	
	/**
	 * Get the state of a specific activity
	 * @param activityString
	 * @return Will always return a value from the enum, never null
	 */
	public ApplicationStateManagementUtilities.ActivityState getStateOfActivity(@NonNull String activityString){
		if(MiscUtilities.isMapNullOrEmpty(this.statesOfActivities)){
			return ActivityState.Unknown;
		}
		ActivityState s = this.statesOfActivities.get(activityString);
		return (s == null) ? ActivityState.Unknown : s;
	}
	
	/**
	 * Overloaded to allow for less code
	 * @param activity
	 * @return Will always return a value from the enum, never null
	 */
	public ApplicationStateManagementUtilities.ActivityState getStateOfActivity(@NonNull Activity activity){
		return getStateOfActivity(activity.getClass().getSimpleName());
	}
	
	/**
	 * Get a list of the activities that are in the foreground
	 * @return
	 */
	public List<String> getActivitiesInForeground(){
		List<String> toReturn = new ArrayList<>();
		for(Map.Entry<String, ActivityState> map : statesOfActivities.entrySet()){
			if(map != null){
				switch (map.getValue()){
					case InForeground:
						toReturn.add(map.getKey());
				}
			}
		}
		return toReturn;
	}
	
	/**
	 * Get a list of the activities that are in the background
	 * @return
	 */
	public List<String> getActivitiesInBackground(){
		List<String> toReturn = new ArrayList<>();
		for(Map.Entry<String, ActivityState> map : statesOfActivities.entrySet()){
			if(map != null){
				switch (map.getValue()){
					case InBackground:
						toReturn.add(map.getKey());
				}
			}
		}
		return toReturn;
	}
	
	/**
	 * Get the states of activities via a hashmap
	 * @return
	 */
	public Map<String, ActivityState> getStatesOfActivities(){
		return this.statesOfActivities;
	}
	
	/**
	 * Print the activities in the foreground into the console
	 */
	public void printOutActivitiesInForeground(){
		MiscUtilities.printOutList(getActivitiesInForeground());
	}
	
	/**
	 * Print the activities in the background into the console
	 */
	public void printOutActivitiesInBackground(){
		MiscUtilities.printOutList(getActivitiesInBackground());
	}
	
	//endregion
}
