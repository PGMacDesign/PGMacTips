# PGMacUtilities
This will be a collection of utility methods that I use in nearly all of my projects


If you are having trouble compiling and receiving this error:
	Execution failed for task ':app:transformClassesWithDexForDebug'.
	com.android.build.api.transform.TransformException: 
	java.util.concurrent.ExecutionException: 
	com.android.dex.DexException: 
	Multiple dex files define Lokhttp3/Address;

Use the following code in your build.gradle file to solve the issue
android {
	configurations {
		all*.exclude group: 'com.squareup.okhttp3', module: 'okhttp'
	}
}