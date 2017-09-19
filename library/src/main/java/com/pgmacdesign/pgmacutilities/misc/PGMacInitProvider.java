package com.pgmacdesign.pgmacutilities.misc;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.pgmacdesign.pgmacutilities.utilities.L;

/**
 * Following Guide from -- https://medium.com/@andretietz/auto-initialize-your-android-library-2349daf06920
 *
 * Created by pmacdowell on 2017-09-19.
 */
public final class PGMacInitProvider  extends ContentProvider {

    private static final String ERROR_STR_1 = "Cannot initialize PGMacUtilities Context. Please make sure ProviderInfo is not null";
    private static final String ERROR_STR_2 = "Incorrect provider authority in manifest. Most likely due to a "
            + "missing applicationId variable in application\'s build.gradle.";

    public PGMacInitProvider() {}

    @Override
    public boolean onCreate() {
        // get the context (Application context)
        Context context = getContext();

        // Call init here on various things

        //Then return it
        return (context != null);
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return null;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }


    @Override
    public void attachInfo(Context context, ProviderInfo providerInfo) {
        if (providerInfo == null) {
            L.m(ERROR_STR_1);
            //throw new NullPointerException("YourLibraryInitProvider ProviderInfo cannot be null.");
            return;
        }
        // So if the authorities equal the library internal ones, the developer forgot to set his applicationId
        if ("<your-library-applicationid>.yourlibraryinitprovider".equals(providerInfo.authority)) {
            L.m(ERROR_STR_2);
            //throw new IllegalStateException();
            return;
        }
        super.attachInfo(context, providerInfo);
    }
}