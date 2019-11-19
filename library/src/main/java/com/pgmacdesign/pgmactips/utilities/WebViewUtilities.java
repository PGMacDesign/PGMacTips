package com.pgmacdesign.pgmactips.utilities;

import android.os.Build;
import android.webkit.ValueCallback;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.pgmacdesign.pgmactips.adaptersandlisteners.OnTaskCompleteListener;

import javax.annotation.Nonnegative;

/**
 * Collection of utilities to be used in conjunction with Android {@link android.webkit.WebView}s
 *
 */
public class WebViewUtilities {
	
	public static final String GET_HTML_JS_STRING =
			"(function() { return ('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>'); })();";
	
	//region User agent and HTML utilities
	
	/**
	 * Gets the user agent String. Data can be used from user-agent to extrapolate things like
	 * the chrome version, Webkit version, etc.
	 * @param webView
	 * @return User agent String, will return null if not present or fails
	 */
	public static String getUserAgent(@NonNull WebView webView){
		try {
			return webView.getSettings().getUserAgentString();
		} catch (Exception eee){
			eee.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Get the HTML of the page currently loaded into the webview
	 * @param webView
	 * @param callbackListener
	 */
	@RequiresApi(value = Build.VERSION_CODES.KITKAT)
	public static void getPageHTML(@NonNull WebView webView,
	                               @NonNull final OnTaskCompleteListener callbackListener){
		try {
			webView.evaluateJavascript(GET_HTML_JS_STRING, new ValueCallback<String>() {
				@Override
				public void onReceiveValue(String value) {
					callbackListener.onTaskComplete(value, 1);
				}
			});
		} catch (Exception e){
			callbackListener.onTaskComplete(null, 0);
		}
	}
	
	//endregion
	
}
