package pgmacdesign.pgmactips.samples.misc;

import android.app.Application;
import android.content.Context;
import android.location.Location;

import com.pgmacdesign.pgmactips.misc.PGMacTipsConfig;
import com.pgmacdesign.pgmactips.misc.PGMacTipsConstants;
import com.pgmacdesign.pgmactips.utilities.DatabaseUtilities;
import com.pgmacdesign.pgmactips.utilities.DisplayManagerUtilities;
import com.pgmacdesign.pgmactips.utilities.SharedPrefs;

import io.realm.RealmConfiguration;

public class SampleMyApplication  extends Application {       //extends MultiDexApplication {
	
	//Boolean for live build or not
	private static final boolean IS_LIVE_BUILD = false;
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
	//Config
	private PGMacTipsConfig config;
	
	/**
	 * Constructor
	 */
	public SampleMyApplication(){
		super();
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		sInstance = this;
		context = getContext();
		dbUtilities = getDatabaseInstance();
		sp = getSharedPrefsInstance();
		this.initPGMacTipsConfig();
	}
	
	/**
	 * Get context, if it is null, get an instance first and then return it
	 * @return Context
	 */
	public static synchronized Context getContext(){
		if(context == null){
			context = getInstance().getApplicationContext();
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
					//Replace me with your DB Name
					PGMacTipsConstants.DB_NAME,
					//Replace me with your DB Version
					PGMacTipsConstants.DB_VERSION,
					//Replace me with your DB boolean flag
					PGMacTipsConstants.DELETE_DB_IF_NEEDED
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
			//Replace me with your Shared Prefs Name
			sp = SharedPrefs.getSharedPrefsInstance(getContext(),
					PGMacTipsConstants.SHARED_PREFS_NAME);
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
	
	/**
	 * Initialize the {@link PGMacTipsConfig} class for use. Entirely optional, but useful
	 * if you want to simply flip a boolean flag and stop logging for the entirety of the project,
	 * change the logging tag, or adjust the default database name
	 */
	private void initPGMacTipsConfig(){
		config = new PGMacTipsConfig.Builder()
				//Replace with the Logging tag of your choosing
				.setTagForLogging("PGMacTips-Samples")
				//Boolean flag for is it is a live build. Defaults to false. Used in logging (stops on live builds)
				.setLiveBuild(IS_LIVE_BUILD)
				//Default DB name if you don't set one yourself. It's suggested that you do though
				.setDefaultDatabaseName("PGMacTips-Samples.db")
				.build(getContext());
	}
}
