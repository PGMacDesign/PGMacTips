package com.pgmacdesign.pgmacutilities.misc;

import android.content.Context;
import android.location.Location;
import android.support.multidex.MultiDexApplication;

import com.pgmacdesign.pgmacutilities.utilities.DatabaseUtilities;
import com.pgmacdesign.pgmacutilities.utilities.DisplayManagerUtilities;
import com.pgmacdesign.pgmacutilities.utilities.SharedPrefs;

import io.realm.RealmConfiguration;


/**
 * This is a sample MyApplication class. This is how it would be implemented in the app.
 * NOTE! DO NOT FORGET THAT THIS NEEDS TO BE INCLUDED IN THE MANIFEST UNDER THE APPLICATION TAG!
 * ----> android:name=".misc.MyApplication" <----
 * Created by pmacdowell on 2017-02-13.
 */
public class SampleMyApplication extends MultiDexApplication {

    //Instance of the application
    private static SampleMyApplication sInstance;
    //Context
    private static Context context;
    //Shared preferences wrapper class
    private static SharedPrefs sp;
    //Localized Database management class.
    private static DatabaseUtilities dbUtilities;
    //Used for managing screen width, height, and other visual metrics
    private static DisplayManagerUtilities dmu;
    //Latitude and Longitude for location purposes
    private static double lastKnownLat, lastKnownLng;
    //Google Location object
    private static Location location;

    /**
     * Constructor
     */
    public SampleMyApplication(){
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        SampleMyApplication.sInstance = this;
        context = getContext();
        dbUtilities = getDatabaseInstance();
        sp = getSharedPrefsInstance();
        //setupLocationServices();
    }

    /*
    private void setupLocationServices(){
        lastKnownLat = lastKnownLng = 0;
        location = new Location(ddd);
    }
    */

    /**
     * Get context, if it is null, get an instance first and then return it
     * @return Context
     */
    public static synchronized Context getContext(){
        if(context == null){
            SampleMyApplication.context = getInstance().getApplicationContext();
        }
        return context;
    }

    /**
     * Build and return a DatabaseUtilities instance
     * @return {@link DatabaseUtilities}
     */
    public static synchronized DatabaseUtilities getDatabaseInstance(){
        if(dbUtilities == null){
            RealmConfiguration config = DatabaseUtilities.buildRealmConfig(
                    getContext(),
                    PGMacUtilitiesConstants.DB_NAME,
                    PGMacUtilitiesConstants.DB_VERSION,
                    PGMacUtilitiesConstants.DELETE_DB_IF_NEEDED
            );
            dbUtilities = new DatabaseUtilities(getContext(), config);
        }
        return dbUtilities;
    }

    /**
     * Build and return a shared prefs instance state.
     * @return {@link SharedPrefs}
     */
    public static synchronized SharedPrefs getSharedPrefsInstance(){
        if(sp == null){
            sp = SharedPrefs.getSharedPrefsInstance(getContext(),
                    PGMacUtilitiesConstants.SHARED_PREFS_NAME);
        }
        return sp;
    }

    /**
     * DMU is utilized in screen measurements in pixels and DP. Useful for setting view
     * dimensions on the fly.
     * @return {@link DisplayManagerUtilities}
     */
    public static synchronized DisplayManagerUtilities getDMU(){
        if(dmu == null) {
            dmu = new DisplayManagerUtilities(getContext());
        }
        return dmu;
    }

    /**
     * Get an instance of the application. This will cascade down and define/ initialize
     * other variables like context as well.
     * @return {@link SampleMyApplication}
     */
    public static synchronized SampleMyApplication getInstance(){
        if(sInstance == null) {
            sInstance = new SampleMyApplication();
        }
        return sInstance;
    }
}
