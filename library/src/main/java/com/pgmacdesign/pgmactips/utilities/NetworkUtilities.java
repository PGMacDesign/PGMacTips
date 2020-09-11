package com.pgmacdesign.pgmactips.utilities;

import android.Manifest;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import androidx.annotation.RequiresPermission;

import com.pgmacdesign.pgmactips.misc.CustomAnnotationsBase;
import com.pgmacdesign.pgmactips.misc.PGMacTipsConfig;
import com.pgmacdesign.pgmactips.misc.PGMacTipsConstants;
import com.pgmacdesign.pgmactips.networkclasses.sslsocketsandprotocols.SSLProtocolOptions;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Permission;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * Class Uses Permission android.permission.ACCESS_NETWORK_STATE.
 * {@link com.pgmacdesign.pgmactips.utilities.PermissionUtilities.permissionsEnum}
 * Created by pmacdowell on 8/12/2016.
 */
public class NetworkUtilities {

    public static final String URL_GOOGLE = "https://www.google.com";


    /**
     * Checks for network connectivity either via wifi or cellular.
     * @param context The context of the activity doing the checking
     * @return A Boolean. True if they have connection, false if they do not
     */
    @RequiresPermission(value = Manifest.permission.ACCESS_NETWORK_STATE)
    public static boolean haveNetworkConnection(Context context) {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;
        if(context == null){
            return false;
        }
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(
                Context.CONNECTIVITY_SERVICE);
        if(cm != null) {
	        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
	        for (NetworkInfo ni : netInfo) {
		        if (ni.getTypeName().equalsIgnoreCase("WIFI"))
			        if (ni.isConnected())
				        haveConnectedWifi = true;
		        if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
			        if (ni.isConnected())
				        haveConnectedMobile = true;
	        }
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

    /**
     * This checks the system-side for network connectivity, then pings the google
     * server to make sure they have internet connection. It is slightly slower and is only
     * included here in case the previous haveNetworkConnection code gets completely deprecated.
     * @param context Context to be passed
     * @return Returns a boolean, true if they have internet, false if they do not.
     */
    public static boolean haveNetworkConnection2(Context context) {
        boolean bool = false;
        if(haveNetworkConnection(context)){
            try {
                HttpURLConnection urlc = (HttpURLConnection)
                        (new URL(NetworkUtilities.URL_GOOGLE).openConnection());
                urlc.setRequestProperty("User-Agent", "Test");
                urlc.setRequestProperty("Connection", "close");
                urlc.setConnectTimeout(1500);
                urlc.connect();
                bool = (urlc.getResponseCode() == 200);
                return bool;
            } catch (IOException e) {
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * This checks the system-side for network connectivity, then pings the google
     * server to make sure they have internet connection. One other method avail here for use
     * in checking via ConnectivityManager.
     * @param context Context to be passed
     * @return Returns a boolean, true if they have internet, false if they do not.
     */
    public static boolean haveNetworkConnection3(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null
                && activeNetwork.isConnectedOrConnecting();
    }

    @SuppressWarnings("deprecation")
    public static void clearCookies(Context context)
    {

        if (Build.VERSION.SDK_INT >= 21) {
            try {
                CookieManager.getInstance().removeAllCookies(null);
                CookieManager.getInstance().flush();
            } catch (Exception e){}
        } else {
            try {
                CookieSyncManager cookieSyncMngr = CookieSyncManager.createInstance(context);
                cookieSyncMngr.startSync();
                CookieManager cookieManager = CookieManager.getInstance();
                cookieManager.removeAllCookie();
                cookieManager.removeSessionCookie();
                cookieSyncMngr.stopSync();
                cookieSyncMngr.sync();
            } catch (Exception e){}
        }
    }
	
	/**
	 * Build an {@link OkHttpClient} client
	 * @return {@link OkHttpClient}
	 */
	@CustomAnnotationsBase.RequiresDependency(requiresDependency = CustomAnnotationsBase.Dependencies.OkHttp3)
	public static OkHttpClient buildOkHttpClient(){
		boolean isLiveBuild = true;
		if(PGMacTipsConfig.getInstance() != null){
			isLiveBuild = PGMacTipsConfig.getInstance().getIsLiveBuild();
		}
		return NetworkUtilities.buildOkHttpClient(
				0, 0, isLiveBuild);
	}
	
	
	/**
	 * Build an {@link OkHttpClient} client
	 * @param readTimeoutInMilliseconds The read Timeout in milliseconds
	 *                                  If <= 0, defaults to 2 seconds (2,000 milliseconds)
	 * @param writeTimeoutInMilliseconds The Write Timeout in milliseconds.
	 *                                   If <= 0, defaults to 2 seconds (2,000 milliseconds)
	 * @param isLiveBuild boolean that determines the {@link HttpLoggingInterceptor} value. If
	 *                    this is true, it will have no logging. If it is false, logging will be
	 *                    set to {@link HttpLoggingInterceptor.Level#BODY}
	 * @return {@link OkHttpClient}
	 */
	@CustomAnnotationsBase.RequiresDependency(requiresDependency = CustomAnnotationsBase.Dependencies.OkHttp3)
	public static OkHttpClient buildOkHttpClient(long readTimeoutInMilliseconds,
	                                             long writeTimeoutInMilliseconds,
	                                             boolean isLiveBuild){
		//Timeouts and logging (10 seconds)
		OkHttpClient.Builder builder = new OkHttpClient.Builder();
		builder.readTimeout((readTimeoutInMilliseconds <= 0) ? PGMacTipsConstants.ONE_SECOND * 2
				: readTimeoutInMilliseconds, TimeUnit.MILLISECONDS);
		builder.writeTimeout((writeTimeoutInMilliseconds <= 0) ? PGMacTipsConstants.ONE_SECOND * 2
				: writeTimeoutInMilliseconds, TimeUnit.MILLISECONDS);
		try {
			//Note, this is wrapped in a Try Catch in case the Logging interceptor dependency is excluded
			HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
			logging.setLevel(isLiveBuild ? HttpLoggingInterceptor.Level.NONE : HttpLoggingInterceptor.Level.BODY);
			builder.addInterceptor(logging);
		} catch (Exception e){
			L.e(e);
		}
		//Configure SSL
		try {
			TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(
					TrustManagerFactory.getDefaultAlgorithm());
			trustManagerFactory.init((KeyStore) null);
			TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
			if (trustManagers.length != 1 || !(trustManagers[0] instanceof X509TrustManager)) {
				throw new IllegalStateException("Unexpected default trust managers:"
						+ Arrays.toString(trustManagers));
			}
			X509TrustManager trustManager = (X509TrustManager) trustManagers[0];
			SSLContext sslContext;
			SSLSocketFactory sslSocketFactory;
			//Can adjust TLS or other types here
			sslContext = SSLContext.getInstance(SSLProtocolOptions.TLS.name);
			sslContext.init(null, new TrustManager[]{trustManager}, null);
			sslSocketFactory = sslContext.getSocketFactory();
			builder.sslSocketFactory(sslSocketFactory, trustManager);
		} catch (KeyManagementException kme){
			kme.printStackTrace();
		} catch (NoSuchAlgorithmException nsa){
			nsa.printStackTrace();
		} catch (KeyStoreException kse){
			kse.printStackTrace();
		} catch (IllegalStateException ise){
			ise.printStackTrace();
		}
		//Build the client
		return builder.build();
	}
	
}
