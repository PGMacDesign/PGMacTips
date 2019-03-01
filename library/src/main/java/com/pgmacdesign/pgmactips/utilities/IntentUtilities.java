package com.pgmacdesign.pgmactips.utilities;

import android.content.Intent;
import android.net.Uri;

import androidx.annotation.NonNull;

public class IntentUtilities {
	
	/**
	 * Set flags on a valid / existing URL to set the current activity to transition out and
	 * clear all other activities on top as well as label the new one as a new task (meaning the
	 * current one being left will be removed as well)
	 * @param intent
	 */
	public static void setFlagsToClearAllOthersAndOpenNew(@NonNull Intent intent){
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
	}
	
	/**
	 * Build a URL to visit a specific website
	 * @param urlToVisit the valid String url to visit
	 * @return {@link Intent}
	 */
	public static Intent buildVisitURLIntent(@NonNull String urlToVisit){
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setData(Uri.parse(urlToVisit));
		return i;
	}
	
	
	/**
	 * Build an Intent to open up the play store and visit an app.
	 * Note that this can fail and throw the
	 * {@link android.content.ActivityNotFoundException}
	 * if the user has disabled or uninstalled the Play store
	 * application. In the event of that exception, call the sister method below:
	 * {@link IntentUtilities#buildGooglePlayStoreIntentBackup(String)}
	 * @param packageString The Package String, IE com.your.app
	 * @return {@link Intent}
	 */
	public static Intent buildGooglePlayStoreIntent(@NonNull String packageString){
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setData(Uri.parse("market://details?id=" + packageString));
		return i;
	}
	
	/**
	 * Build an Intent to open up the play store and visit an app.  Note that this
	 * one gives the user the option to choose ther web browser instead of defaulting
	 * to the play store directly; this should mainly be used as a backup if the
	 * above methods fails.
	 * @param packageString The Package String, IE com.your.app
	 * @return {@link Intent}
	 */
	public static Intent buildGooglePlayStoreIntentBackup(@NonNull String packageString){
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setData(Uri.parse("https://play.google.com/store/apps/details?id=" + packageString));
		return i;
	}
}
