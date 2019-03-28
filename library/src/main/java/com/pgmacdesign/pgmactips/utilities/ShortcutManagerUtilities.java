package com.pgmacdesign.pgmactips.utilities;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.google.gson.annotations.SerializedName;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

/**
 * Boilerplate code wrapper for utilizing the {@link ShortcutManager} Framework
 * Useful Links:
 * 1) Android docs - https://developer.android.com/guide/topics/ui/shortcuts
 * 2) Android docs - https://developer.android.com/reference/android/content/pm/ShortcutInfo
 * 3) Great library for declarative shortcuts - https://github.com/MatthiasRobbers/shortbread
 * Note that I made this instead of using Shortbread because I needed to be able to declare the
 * shortcuts dynamically on the fly within the app and the library did not support that.
 */
public class ShortcutManagerUtilities {
	//region Static final Strings
	public static final String SHORTCUT_BUNDLE_KEY = "pb_shortcut_key";
	public static final String SHORTCUT_BUNDLE_ADDITIONAL_DATA = "pb_xtra_data_key";
	//endregion
	
	//region Public Accessors to Set && Add Shortcuts
	
	/**
	 * Add shortcut. Adds a shortcut to the existing ones
	 * @param context1
	 * @param shortcutPojo
	 * @return
	 * @throws PGShortcutException
	 */
	@RequiresApi(api = Build.VERSION_CODES.O)
	public static boolean addShortcut(@NonNull Context context1, @NonNull ShortcutPojo shortcutPojo) throws PGShortcutException {
		if (context1 == null || shortcutPojo == null) {
			throw buildInvalidParamsException();
		}
		return addShortcut(context1, Collections.singletonList(shortcutPojo));
	}
	
	/**
	 * Add shortcuts. Adds shortcuts to the existing ones
	 * @param context1
	 * @param shortcutPojos
	 * @return
	 * @throws PGShortcutException
	 */
	@RequiresApi(api = Build.VERSION_CODES.O)
	public static boolean addShortcut(@NonNull Context context1, @NonNull List<ShortcutPojo> shortcutPojos) throws PGShortcutException {
		if (context1 == null || MiscUtilities.isListNullOrEmpty(shortcutPojos)) {
			throw buildInvalidParamsException();
		}
		if (Build.VERSION.SDK_INT < 26) {
			throw buildInvalidAPILevelException();
		}
		
		Context context = context1.getApplicationContext();
		ShortcutManager shortcutManager = getShortcutManager(context);
		if(shortcutManager == null){
			throw buildShortcutInstantiationException();
		}
		boolean isSupported = shortcutManager.isRequestPinShortcutSupported();
		if (!isSupported) {
			throw buildShortcutsNotSupportedException();
		}
		try {
			List<ShortcutInfo> s = new ArrayList<>();
			for(ShortcutPojo pojo : shortcutPojos){
				if(pojo == null){
					continue;
				}
				ShortcutInfo i = convertToShortcutInfo(context1, pojo);
				if(i != null) {
					s.add(i);
				} else {
					L.m("Could not add pojo with id: " + pojo.getShortcutId());
				}
			}
			if(MiscUtilities.isListNullOrEmpty(s)){
				throw buildGenericException();
			}
			return addShortcuts(context, s);
		} catch (Exception e) {
			throw buildGenericException(e.getMessage());
		}
	}
	
	/**
	 * Set shortcut. This will replace any current ones with the shortcut passed
	 * @param context1
	 * @param shortcutPojo
	 * @return
	 * @throws PGShortcutException
	 */
	@RequiresApi(api = Build.VERSION_CODES.O)
	public static boolean setShortcut(@NonNull Context context1, @NonNull ShortcutPojo shortcutPojo) throws PGShortcutException {
		if (context1 == null || shortcutPojo == null) {
			throw buildInvalidParamsException();
		}
		return setShortcut(context1, Collections.singletonList(shortcutPojo));
	}
	
	/**
	 * Set shortcuts. This will replace any current ones with the shortcuts passed
	 * @param context1
	 * @param shortcutPojos
	 * @return
	 * @throws PGShortcutException
	 */
	@RequiresApi(api = Build.VERSION_CODES.O)
	public static boolean setShortcut(@NonNull Context context1, @NonNull List<ShortcutPojo> shortcutPojos) throws PGShortcutException {
		if (context1 == null || MiscUtilities.isListNullOrEmpty(shortcutPojos)) {
			throw buildInvalidParamsException();
		}
		if (Build.VERSION.SDK_INT < 26) {
			throw buildInvalidAPILevelException();
		}
		
		Context context = context1.getApplicationContext();
		ShortcutManager shortcutManager = getShortcutManager(context);
		if(shortcutManager == null){
			throw buildShortcutInstantiationException();
		}
		boolean isSupported = shortcutManager.isRequestPinShortcutSupported();
		if (!isSupported) {
			throw buildShortcutsNotSupportedException();
		}
		try {
			List<ShortcutInfo> s = new ArrayList<>();
			for(ShortcutPojo pojo : shortcutPojos){
				if(pojo == null){
					continue;
				}
				ShortcutInfo i = convertToShortcutInfo(context1, pojo);
				if(i != null) {
					s.add(i);
				} else {
					L.m("Could not add pojo with id: " + pojo.getShortcutId());
				}
			}
			if(MiscUtilities.isListNullOrEmpty(s)){
				throw buildGenericException();
			}
			return setShortcuts(context, s);
		} catch (Exception e) {
			throw buildGenericException(e.getMessage());
		}
	}
	
	//endregion
	
	//region Add Shortcuts (Not remove others)
	
	/**
	 * Add shortcut. Adds a shortcut to the existing ones
	 * @param context
	 * @param shortcutInfos
	 * @return
	 */
	@RequiresApi(api = Build.VERSION_CODES.O)
	private static boolean addShortcut(@NonNull Context context, @NonNull ShortcutInfo shortcutInfos){
		return addShortcuts(context, Collections.singletonList(shortcutInfos));
	}
	
	/**
	 * Add shortcuts. Adds shortcuts to the existing ones
	 * @param context
	 * @param shortcutInfos
	 * @return
	 */
	@RequiresApi(api = Build.VERSION_CODES.O)
	private static boolean addShortcuts(@NonNull Context context, @NonNull List<ShortcutInfo> shortcutInfos){
		if(MiscUtilities.isListNullOrEmpty(shortcutInfos)){
			L.m("List of ShortcutInfo objects null");
			return false;
		}
		ShortcutManager shortcutManager = getShortcutManager(context);
		if(shortcutManager == null){
			L.m("Could not instantiate ShortcutManager");
			return false;
		}
		shortcutManager.addDynamicShortcuts(shortcutInfos);
		return true;
	}
	
	//endregion
	
	//region Set Shortcuts (Remove previous and set new)
	
	/**
	 * Set shortcut. This will replace any current ones with the shortcut passed
	 * @param context
	 * @param shortcutInfos
	 * @return
	 */
	@RequiresApi(api = Build.VERSION_CODES.O)
	private static boolean setShortcut(@NonNull Context context, @NonNull ShortcutInfo shortcutInfos){
		return setShortcuts(context, Collections.singletonList(shortcutInfos));
	}
	
	/**
	 * Set shortcuts. This will replace any current ones with the shortcuts passed
	 * @param context
	 * @param shortcutInfos
	 * @return
	 */
	@RequiresApi(api = Build.VERSION_CODES.O)
	private static boolean setShortcuts(@NonNull Context context, @NonNull List<ShortcutInfo> shortcutInfos){
		if(MiscUtilities.isListNullOrEmpty(shortcutInfos)){
			L.m("List of ShortcutInfo objects null");
			return false;
		}
		ShortcutManager shortcutManager = getShortcutManager(context);
		if(shortcutManager == null){
			L.m("Could not instantiate ShortcutManager");
			return false;
		}
		shortcutManager.removeAllDynamicShortcuts();
		shortcutManager.setDynamicShortcuts(shortcutInfos);
		return true;
	}
	
	//endregion
	
	//region Remove Shortcut
	
	/**
	 * Remove a single shortcut
	 * @param context
	 * @param id ID of the shortcut
	 * @return true if it was successfully removed, false if not.
	 */
	@RequiresApi(api = Build.VERSION_CODES.O)
	public static boolean removeShortcut(@NonNull Context context, String id) {
		try {
			getShortcutManager(context).removeDynamicShortcuts(Collections.singletonList(id));
			return true;
		} catch (Exception e){
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Remove multiple shortcuts
	 * @param context
	 * @param ids IDs of the shortcuts
	 * @return true if they were successfully removed, false if not.
	 */
	@RequiresApi(api = Build.VERSION_CODES.O)
	public static boolean removeShortcuts(@NonNull Context context, List<String> ids) {
		try {
			getShortcutManager(context).removeDynamicShortcuts(ids);
			return true;
		} catch (Exception e){
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Remove all shortcuts
	 * @param context
	 * @return true if they were successfully removed, false if not.
	 */
	@RequiresApi(api = Build.VERSION_CODES.O)
	public static boolean removeAllShortcuts(@NonNull Context context) {
		try {
			getShortcutManager(context).removeAllDynamicShortcuts();
			return true;
		} catch (Exception e){
			e.printStackTrace();
			return false;
		}
	}
	
	//endregion
	
	//region Private Utilities
	
	/**
	 * Convert a {@link ShortcutPojo} to a {@link ShortcutInfo} object
	 * @param context
	 * @param shortcutPojo {@link ShortcutPojo}
	 * @return  {@link ShortcutInfo}
	 */
	@RequiresApi(api = Build.VERSION_CODES.O)
	private static ShortcutInfo convertToShortcutInfo(@NonNull Context context, @NonNull ShortcutPojo shortcutPojo) throws PGShortcutException {
		if(shortcutPojo == null || context == null){
			return null;
		}
		if(StringUtilities.isNullOrEmpty(shortcutPojo.getShortcutId())){
			throw buildNullIdException();
		}
		ShortcutInfo.Builder builder = new ShortcutInfo.Builder(context, shortcutPojo.getShortcutId());
		builder.setIcon(Icon.createWithResource(context, shortcutPojo.getResourceDrawableId()));
		Intent intent;
		boolean shouldAddClearTask = false;
		if(shortcutPojo.getClassToOpen() == null && shortcutPojo.getCustomUriToOpen() != null){
			intent = new Intent(Intent.ACTION_VIEW, shortcutPojo.getCustomUriToOpen());
		} else if (shortcutPojo.getClassToOpen() != null && shortcutPojo.getCustomUriToOpen() == null){
			shouldAddClearTask = true;
			intent = new Intent(Intent.ACTION_MAIN, Uri.EMPTY, context, shortcutPojo.getClassToOpen());
			intent.putExtra(SHORTCUT_BUNDLE_KEY, shortcutPojo.getShortcutId());
			if(!StringUtilities.isNullOrEmpty(shortcutPojo.getAdditionalDataForIntent())) {
				intent.putExtra(SHORTCUT_BUNDLE_ADDITIONAL_DATA, shortcutPojo.getAdditionalDataForIntent());
			}
		} else {
			throw buildMissingRequiredFieldNotIdException();
		}
		int[] intentFlags = shortcutPojo.getIntentFlagsToAdd();
		boolean setAtLeastOneFlag = false;
		if(intentFlags != null){
			if(intentFlags.length > 0){
				setAtLeastOneFlag = true;
				for(int x : intentFlags){
					intent.addFlags(x);
				}
			}
		}
		if(!setAtLeastOneFlag && shouldAddClearTask){
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
		}
		builder.setIntents(new Intent[]{intent});
		builder.setRank(shortcutPojo.getRankInList());
		builder.setShortLabel(shortcutPojo.getShortcutString());
		builder.setLongLabel(shortcutPojo.getShortcutStringLong());
		return builder.build();
	}
	
	//endregion
	
	//region Public Utilities

	
	/**
	 * Gets the shortcut manager {@link ShortcutManager}
	 * Note that ideally you should pass the {@link Context#getApplicationContext()} instead of
	 * the Activity context.
	 * @param context Context {@link Context#getApplicationContext()}
	 * @return Shortcut manager, but null if it cannot get it.
	 */
	@RequiresApi(api = Build.VERSION_CODES.O)
	public static ShortcutManager getShortcutManager(@NonNull Context context){
		try {
			ShortcutManager shortcutManager = context.getSystemService(ShortcutManager.class);
			if(shortcutManager != null){
				return shortcutManager;
			}
		} catch (Exception e){}
		try {
			ShortcutManager shortcutManager = context.getApplicationContext().getSystemService(ShortcutManager.class);
			if(shortcutManager != null){
				return shortcutManager;
			}
		} catch (Exception e){}
		return null;
	}
	
	/**
	 * Gets the last clicked shortcut
	 * @param activity
	 * @return
	 */
	@RequiresApi(api = Build.VERSION_CODES.O)
	public static String getLastClickedShortcut(@NonNull Activity activity){
		if (activity != null) {
			return getLastClickedShortcut(activity.getIntent());
		}
		return null;
	}
	
	/**
	 * Gets the last clicked shortcut
	 * @param intent the intent received from {@link Activity#getIntent()}
	 * @return
	 */
	@RequiresApi(api = Build.VERSION_CODES.O)
	public static String getLastClickedShortcut(@NonNull Intent intent){
		try {
			String str = intent.getStringExtra(SHORTCUT_BUNDLE_KEY);
			if(!StringUtilities.isNullOrEmpty(str)){
				return str;
			}
		} catch (Exception e){}
		try {
			Bundle bundle = intent.getExtras();
			String str = bundle.getString(SHORTCUT_BUNDLE_KEY);
			if(!StringUtilities.isNullOrEmpty(str)) {
				return str;
			}
		} catch (Exception e){}
		return null;
	}
	
	/**
	 * Gets the additional data {@link ShortcutPojo#additionalDataForIntent} String
	 * @param activity
	 * @return
	 */
	@RequiresApi(api = Build.VERSION_CODES.O)
	public static String getAdditionalDataString(@NonNull Activity activity){
		if (activity != null) {
			return getAdditionalDataString(activity.getIntent());
		}
		return null;
	}
	
	/**
	 * Gets the additional data {@link ShortcutPojo#additionalDataForIntent} String
	 * @param intent the intent received from {@link Activity#getIntent()}
	 * @return
	 */
	@RequiresApi(api = Build.VERSION_CODES.O)
	public static String getAdditionalDataString(@NonNull Intent intent){
		try {
			String str = intent.getStringExtra(SHORTCUT_BUNDLE_ADDITIONAL_DATA);
			if(!StringUtilities.isNullOrEmpty(str)){
				return str;
			}
		} catch (Exception e){}
		try {
			Bundle bundle = intent.getExtras();
			String str = bundle.getString(SHORTCUT_BUNDLE_ADDITIONAL_DATA);
			if(!StringUtilities.isNullOrEmpty(str)) {
				return str;
			}
		} catch (Exception e){}
		return null;
	}
	
	/**
	 * Gets the shortcut from the String id passed
	 * @param context
	 * @param shortcutId
	 * @return
	 */
	@RequiresApi(api = Build.VERSION_CODES.O)
	public static ShortcutInfo getShortcut(@NonNull Context context, @NonNull String shortcutId){
		if(context == null || StringUtilities.isNullOrEmpty(shortcutId)){
			return null;
		}
		try {
			List<ShortcutInfo> infos = getShortcutManager(context).getDynamicShortcuts();
			for(ShortcutInfo i : infos){
				if(i != null){
					if(i.getId().equals(shortcutId)){
						return i;
					}
				}
			}
		} catch (Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Get all shortcuts currently in place
	 * @param context
	 * @return
	 */
	@RequiresApi(api = Build.VERSION_CODES.O)
	public static List<ShortcutInfo> getAllShortcuts(@NonNull Context context){
		if(context == null){
			return null;
		}
		try {
			return getShortcutManager(context).getDynamicShortcuts();
		} catch (Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Get the number of shortcuts currently in place
	 * @param context
	 * @return
	 */
	@RequiresApi(api = Build.VERSION_CODES.O)
	public static int getNumberOfShortcuts(@NonNull Context context){
		if(context == null){
			return 0;
		}
		try {
			return getShortcutManager(context).getDynamicShortcuts().size();
		} catch (Exception e){
			e.printStackTrace();
		}
		return 0;
	}
	
	//endregion
	
	//region Pojo
	
	/**
	 * Shortcut object to be used to reference the {@link android.content.pm.ShortcutInfo} class
	 * https://medium.com/@gabornovak/android-7-1-app-shortcuts-2cc251f562c3
	 */
	public static class ShortcutPojo {
		/**
		 * Max Length 10 characters
		 */
		@SerializedName("shortcutString")
		private String shortcutString;
		/**
		 * Max Length 25 characters
		 */
		@SerializedName("shortcutStringLong")
		private String shortcutStringLong;
		@SerializedName("shortcutId")
		private String shortcutId;
		@SerializedName("resourceDrawableId")
		private int resourceDrawableId;
		@SerializedName("rankInList")
		private int rankInList;
		/**
		 * Additional data to pass in for your use. Tip, if you want to pass a larger amount of
		 * data, just convert an object to a JSON String using
		 * {@link GsonUtilities#convertObjectToJson(Object, Type)} or
		 * {@link GsonUtilities#convertObjectToJson(Object, Class)}
		 * Note that it has a cap of 500kb in size as per this link:
		 * https://stackoverflow.com/a/18030595/2480714
		 * To check length of passed String, use {@link StringUtilities#getStringSizeInBytes(String str)}
		 */
		@SerializedName("additionalDataForIntent")
		private String additionalDataForIntent;
		@SerializedName("customUriToOpen")
		private Uri customUriToOpen;
		@SerializedName("classToOpen")
		private Class classToOpen;
		/**
		 * Intent flags to be added to the launch intent.
		 * If you want to clear all current tasks entirely, recommended to use:
		 * 1) {@link Intent#FLAG_ACTIVITY_CLEAR_TASK}
		 * 2) {@link Intent#FLAG_ACTIVITY_NEW_TASK}
		 *
		 * If you just want to launch an activity, recommended to use:
		 * 1) {@link Intent#FLAG_ACTIVITY_CLEAR_TASK}
		 *
		 */
		@SerializedName("intentFlagsToAdd")
		private int[] intentFlagsToAdd;
		
		private ShortcutPojo(){}
		
		public ShortcutPojo(@NonNull String shortcutId,
		                    @NonNull Class classToOpen){
			this.shortcutId = shortcutId;
			this.classToOpen = classToOpen;
			this.customUriToOpen = null;
		}
		
		public ShortcutPojo(@NonNull String shortcutId,
		                    @NonNull Uri customUriToOpen){
			this.shortcutId = shortcutId;
			this.customUriToOpen = customUriToOpen;
			this.classToOpen = null;
		}
		
		public String getAdditionalDataForIntent() {
			return additionalDataForIntent;
		}
		
		public void setAdditionalDataForIntent(String additionalDataForIntent) {
			if(!StringUtilities.isNullOrEmpty(additionalDataForIntent)){
				long dataLength = StringUtilities.getSizeInBytes(additionalDataForIntent);
				if(dataLength > (FileUtilities.convertToBytes(500, FileUtilities.ByteSizeNames.Kilobytes))){
					L.m("String data passed > 500KB, (Actual size == " + dataLength + ") unable to save");
					return;
				}
			}
			this.additionalDataForIntent = additionalDataForIntent;
		}
		
		public int[] getIntentFlagsToAdd() {
			return intentFlagsToAdd;
		}
		
		public void setIntentFlagsToAdd(int[] intentFlagsToAdd) {
			this.intentFlagsToAdd = intentFlagsToAdd;
		}
		
		public int getRankInList() {
			return rankInList;
		}
		
		public void setRankInList(int rankInList) {
			this.rankInList = rankInList;
		}
		
		public Class getClassToOpen() {
			return classToOpen;
		}
		
		public String getShortcutString() {
			return shortcutString;
		}
		
		public void setShortcutString(String shortcutString) {
			this.shortcutString = shortcutString;
		}
		
		public String getShortcutStringLong() {
			return shortcutStringLong;
		}
		
		public void setShortcutStringLong(String shortcutStringLong) {
			this.shortcutStringLong = shortcutStringLong;
		}
		
		public String getShortcutId() {
			return shortcutId;
		}
		
		public int getResourceDrawableId() {
			return resourceDrawableId;
		}
		
		public void setResourceDrawableId(int resourceDrawableId) {
			this.resourceDrawableId = resourceDrawableId;
		}
		
		public Uri getCustomUriToOpen() {
			return customUriToOpen;
		}
		
	}
	
	//endregion
	
	//region Simple Exception For Runtime
	
	/**
	 * Extends {@link RuntimeException} so as to prevent lint errors
	 */
	private static class PGShortcutException extends RuntimeException {
		public PGShortcutException(String str){
			super(str);
		}
	}
	
	private static PGShortcutException buildInvalidParamsException(){
		return new PGShortcutException("Context, POJO, or passed param was null / invalid, unable to add");
	}
	
	private static PGShortcutException buildInvalidAPILevelException(){
		return new PGShortcutException("Build Version is < API 26, unable to utilize Shortcuts");
	}
	
	private static PGShortcutException buildShortcutsNotSupportedException(){
		return new PGShortcutException("Shortcuts are not supported on this device");
	}
	
	private static PGShortcutException buildShortcutInstantiationException(){
		return new PGShortcutException("Could not instantiate ShortcutManager, unable to add shortcut; was your context valid?");
	}
	
	private static PGShortcutException buildMissingRequiredFieldNotIdException(){
		return new PGShortcutException("Missing required param. Must include either 'classToOpen' or 'customUriToOpen'");
	}
	
	private static PGShortcutException buildNullIdException(){
		return new PGShortcutException("Shortcut ID was null or empty, it is a required field");
	}
	
	private static PGShortcutException buildGenericException(){
		return new PGShortcutException("Could not create shortcut, unknown error");
	}
	
	private static PGShortcutException buildGenericException(String str){
		return new PGShortcutException(str);
	}
	
	//endregion
}
