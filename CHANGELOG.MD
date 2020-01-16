Change Log
==========

Version 1.2.6 *(TBD)*
----------------------------

* Updated the Build.gradle to support Obfuscation via parent declarations as per [this](https://stackoverflow.com/a/31031491/2480714) recommendation.

Version 1.2.5 *(2019-12-13)*
----------------------------

* Bugfix: Fixed a bug in the [FileUtilities](https://github.com/PGMacDesign/PGMacTips/blob/master/library/src/main/java/com/pgmacdesign/pgmactips/utilities/FileUtilities.java) class that was returning file extensions with 2 periods in front.
* Update: Updates in the [FileUtilities](https://github.com/PGMacDesign/PGMacTips/blob/master/library/src/main/java/com/pgmacdesign/pgmactips/utilities/FileUtilities.java) class to have better handling of Google Drive file management as well as access to check virtual file status.

Version 1.2.4 *(2019-11-22)*
----------------------------

* Update: Adding in a few new methods to the [FileUtilities](https://github.com/PGMacDesign/PGMacTips/blob/master/library/src/main/java/com/pgmacdesign/pgmactips/utilities/FileUtilities.java) class.
* Bugfix: Fixed an issue in the [DatabaseUtilities](https://github.com/PGMacDesign/PGMacTips/blob/master/library/src/main/java/com/pgmacdesign/pgmactips/utilities/DatabaseUtilities.java) that was causing Reads from a background service to not function properly.

Version 1.2.3 *(2019-11-21)*
----------------------------

* Update: Updates to the [FileUtilities](https://github.com/PGMacDesign/PGMacTips/blob/master/library/src/main/java/com/pgmacdesign/pgmactips/utilities/FileUtilities.java) class to include updated find the real Absolute URI when using Oreo or higher devices. 
* Update: Moved the absolute Uri methods from the  [ImageUtilities](https://github.com/PGMacDesign/PGMacTips/blob/master/library/src/main/java/com/pgmacdesign/pgmactips/utilities/ImageUtilities.java) class to the [FileUtilities](https://github.com/PGMacDesign/PGMacTips/blob/master/library/src/main/java/com/pgmacdesign/pgmactips/utilities/FileUtilities.java) class and updated many of them.

Version 1.2.2 *(2019-11-14)*
----------------------------

* Added in bugfix / catch logic for the [SharedPrefs](https://github.com/PGMacDesign/PGMacTips/blob/master/library/src/main/java/com/pgmacdesign/pgmactips/utilities/SharedPrefs.java) class to save and retrieve values if the Android device being used does not support the encryption logic due to API errors. 

Version 1.2.1 *(2019-11-12)*
----------------------------

* Update: Minor update to the [TempString](https://github.com/PGMacDesign/PGMacTips/blob/master/library/src/main/java/com/pgmacdesign/pgmactips/misc/TempString.java) class to allow for static instance getter for the purpose of passing in a byte array. 
* Update: Overhaul of the [ColorUtilities](https://github.com/PGMacDesign/PGMacTips/blob/master/library/src/main/java/com/pgmacdesign/pgmactips/utilities/ColorUtilities.java) class to add in multiple new features as well as material design color palette support. 

Version 1.2.0 *(2019-11-06)*
----------------------------

* New: Brand New functionality added to the [DatabaseUtilities](https://github.com/PGMacDesign/PGMacTips/blob/master/library/src/main/java/com/pgmacdesign/pgmactips/utilities/DatabaseUtilities.java) class; added in support for encrypted values.
* Update: More updates in the [DatabaseUtilities](https://github.com/PGMacDesign/PGMacTips/blob/master/library/src/main/java/com/pgmacdesign/pgmactips/utilities/DatabaseUtilities.java) to allow a transfer of the data to encrypted as well as printing both encrypted and decrypted values.
* Update: Updated the Realm dependency from `4.2.0` to the most current (as of now) version, `6.0.0`. 
* Update: Update the sample classes ([Java](https://github.com/PGMacDesign/PGMacTips/blob/master/samples/src/main/java/pgmacdesign/pgmactips/samples/activitysamples/SampleDBClass.java) / [Kotlin](https://github.com/PGMacDesign/PGMacTips/blob/master/samples/src/main/java/pgmacdesign/pgmactips/samples/activitysamples/SampleDBClassKotlin.kt)) to demonstrate how to use the new encryption logic for the DB as well as migrate unencrypted to encrypted. (Remember that different Salt + Password combos will change encryption / decryption logic)

Version 1.1.2 *(2019-11-05)*
----------------------------

* Delete: Removed MalwareUtilities in the event that the stored array of String values was triggering any alarm or alert on the app submission process by Google.
* New: Added in a valid hex color checker in the [ColorUtilities](https://github.com/PGMacDesign/PGMacTips/blob/master/library/src/main/java/com/pgmacdesign/pgmactips/utilities/ColorUtilities.java).
* Update: Updated to the most current versions for Android Support, Maps, Recyclerview, and a few other Misc AndroidX & Google API dependencies. 
* Bugfix: Updated the logic in the  [DatabaseUtilities](https://github.com/PGMacDesign/PGMacTips/blob/master/library/src/main/java/com/pgmacdesign/pgmactips/utilities/DatabaseUtilities.java) class that was causing an IllegalState exception when a race condition was triggered due to certain circumstances. Basically the Realm object was being instantiated and simultaneously closing when a full Master DB wipe happened within nanoseconds. As a result, I switched the Realm object to a singleton for this method only and it is cleared whenever the deleteDB is called; values were of a difference of 0.153646 milliseconds
* `IMPORTANT NOTE!` The Realm dependency will be updated in the next release so anyone who needs to support an older version of Realm `4.2.0` should *NOT* move to a version after this. 

Version 1.1.1 *(2019-10-25)*
----------------------------

* Skipped 1.1.0 Update as it had an issue with slow computational operations with mandated increased entropy generation in [Encryption Utilities]([EncryptionUtilities](https://github.com/PGMacDesign/PGMacTips/blob/master/library/src/main/java/com/pgmacdesign/pgmactips/utilities/EncryptionUtilities.java)). 
* Update: Updated code in the [ImageUtilities](https://github.com/PGMacDesign/PGMacTips/blob/master/library/src/main/java/com/pgmacdesign/pgmactips/utilities/ImageUtilities.java) class for obtaining the absolute Uri path correctly. Now includes support for Google Drive files 
* Update: Updated the [PGConnectivityReceiver](https://github.com/PGMacDesign/PGMacTips/blob/master/library/src/main/java/com/pgmacdesign/pgmactips/broadcastreceivers/PGConnectivityReceiver.java) to include a non-deprecated option for wifi checking as well as updating permissions requirements.
* New: Added a new class, [ApplicationStateManagementUtilities](https://github.com/PGMacDesign/PGMacTips/blob/master/library/src/main/java/com/pgmacdesign/pgmactips/utilities/ApplicationStateManagementUtilities.java) to serve the primary Application class in an app and allow the dev to track application and activity Foreground + Background changes more effectively and with less work.  
* Update: New code in the [FileUtilities](https://github.com/PGMacDesign/PGMacTips/blob/master/library/src/main/java/com/pgmacdesign/pgmactips/utilities/FileUtilities.java) that allows easier writing to and reading from files. Also added in read from and write to encrypted file methods.
* Update: Added in options to dynamically alter the `PBKDF2_ITERATIONS` from `256` to `15000` in the [EncryptionUtilities](https://github.com/PGMacDesign/PGMacTips/blob/master/library/src/main/java/com/pgmacdesign/pgmactips/utilities/EncryptionUtilities.java) as per [this article](https://cryptosense.com/blog/parameter-choice-for-pbkdf2/) as per user provided params. Also added in some minor updates to logic to prevent erroneous errors.   

Version 1.0.9 *(2019-10-04)*
----------------------------

* New: Added in the code for taking screen shots in the [MiscUtilities](https://github.com/PGMacDesign/PGMacTips/blob/master/library/src/main/java/com/pgmacdesign/pgmactips/utilities/MiscUtilities.java) class.
* Bugfix: Fixed a bug in the [DatabaseUtilities](https://github.com/PGMacDesign/PGMacTips/blob/master/library/src/main/java/com/pgmacdesign/pgmactips/utilities/DatabaseUtilities.java) class that as preventing custom persisted objects from being deleted properly. 


Version 1.0.8 *(2019-10-01)*
----------------------------

* Update: Added an overloaded method in the [L](https://github.com/PGMacDesign/PGMacTips/blob/master/library/src/main/java/com/pgmacdesign/pgmactips/utilities/L.java) class to allow for an overloaded call to omit the "chunk 2 of 20:" string for easier logging copying / pasting / usage.
* Update: Updated the [PGAnimatedSvgView](https://github.com/PGMacDesign/PGMacTips/blob/master/library/src/main/java/com/pgmacdesign/pgmactips/customui/animatedsvg/PGAnimatedSvgView.java) class to add in the option to use a gradient.  
* Update: New Utilities in the [DateUtilities](https://github.com/PGMacDesign/PGMacTips/blob/master/library/src/main/java/com/pgmacdesign/pgmactips/utilities/DateUtilities.java) class for getting simple date via an overloaded method.
* Update: Clarifying an error that can happen by updating the README.
* Update: Updated the [SystemUtilities](https://github.com/PGMacDesign/PGMacTips/blob/master/library/src/main/java/com/pgmacdesign/pgmactips/utilities/SystemUtilities.java) to include references to [Pie](https://developer.android.com/about/versions/pie/android-9.0) and [Android 10](https://developer.android.com/about/versions/10/features). 
* New: Adding new methods into both the [SystemUtilities](https://github.com/PGMacDesign/PGMacTips/blob/master/library/src/main/java/com/pgmacdesign/pgmactips/utilities/SystemUtilities.java) and the [MiscUtilities](https://github.com/PGMacDesign/PGMacTips/blob/master/library/src/main/java/com/pgmacdesign/pgmactips/utilities/MiscUtilities.java) to include code for checking if a device is rooted.
* Update: Updated the [DisplayManagerUtilities](https://github.com/PGMacDesign/PGMacTips/blob/master/library/src/main/java/com/pgmacdesign/pgmactips/utilities/DisplayManagerUtilities.java) to include more accurate updates and info. Updated references came from [here](https://stackoverflow.com/a/16629223/2480714) and [here](https://stackoverflow.com/a/8633003/2480714) 

Version 1.0.7 *(2019-08-15)*
----------------------------

* Update: Updated the [TextField Utilities](https://github.com/PGMacDesign/PGMacTips/blob/master/library/src/main/java/com/pgmacdesign/pgmactips/utilities/TextFieldUtilities.java) to include a fix for when text is getting cut off in a TextView nested underneath a Linear Layout.
* New: Added in some commented reference code in the [ColorUtilities](https://github.com/PGMacDesign/PGMacTips/blob/master/library/src/main/java/com/pgmacdesign/pgmactips/utilities/ColorUtilities.java) that can be used to reference alpha --> Hex code value conversions.  
* New: Added in a new custom UI Element, the [PGAnimatedSvgView](https://github.com/PGMacDesign/PGMacTips/blob/master/library/src/main/java/com/pgmacdesign/pgmactips/customui/animatedsvg/PGAnimatedSvgView.java) class that is used for drawing animated SVG views / icons.   

Version 1.0.5 *(2019-08-08)*
----------------------------

* New: Added in new Layouts in the cutomui package. [CustomRelativeLayout](https://github.com/PGMacDesign/PGMacTips/blob/master/library/src/main/java/com/pgmacdesign/pgmactips/customui/CustomRelativeLayout.java), [CustomFrameLayout](https://github.com/PGMacDesign/PGMacTips/blob/master/library/src/main/java/com/pgmacdesign/pgmactips/customui/CustomFrameLayout.java), & [CustomLinearLayout](https://github.com/PGMacDesign/PGMacTips/blob/master/library/src/main/java/com/pgmacdesign/pgmactips/customui/CustomLinearLayout.java). Each of them is used for specifying customized width and height values (IE, max size) which can be tedious if done normally through custom defined UI 
* Update: Updated the [NumberUtilities](https://github.com/PGMacDesign/PGMacTips/blob/master/library/src/main/java/com/pgmacdesign/pgmactips/utilities/NumberUtilities.java) to include a safe Integer parser. 
* Update: Updated the [DatabaseUtilities](https://github.com/PGMacDesign/PGMacTips/blob/master/library/src/main/java/com/pgmacdesign/pgmactips/utilities/DatabaseUtilities.java) to include a logging option for CRUD operations. 
* Bugfix: Fixed the Database Custom CRUD Operations in the DatabaseUtilities that were using the wrong TypeToken Strings 

Version 1.0.3 *(2019-08-05)*
----------------------------

* New: Added in code in the [MiscUtilities](https://github.com/PGMacDesign/PGMacTips/blob/master/library/src/main/java/com/pgmacdesign/pgmactips/utilities/MiscUtilities.java) class to add more getter methods for use with Maps and also added in regions for easier viewing in an IDE. Also added in some sorting methods and a Generic Sorting class.
* Update: Updated the [RetrofitClient](https://github.com/PGMacDesign/PGMacTips/blob/master/library/src/main/java/com/pgmacdesign/pgmactips/networkclasses/retrofitutilities/RetrofitClient.java) class to include some additional options including customizing the Okhttp ConnectionSpec var. 
* New: Adding in a new class, [GenericComparator](https://github.com/PGMacDesign/PGMacTips/blob/master/library/src/main/java/com/pgmacdesign/pgmactips/misc/GenericComparator.java) which is used in the MiscUtilities update mentioned above for comparing objects dynamically as per their fields. Adds in a huge amount of flexibility with regards to Comparator sorting and should be able to support most data types.  
* New: Added in some sample usages of the various new comparator functions listed above in the [MyTestActivity](https://github.com/PGMacDesign/PGMacTips/blob/master/samples/src/main/java/pgmacdesign/pgmactips/samples/MyTestActivity.java) class; the name of the function is `sortAndPrintLists()` 

Version 1.0.2 *(2019-07-12)*
----------------------------

* Update: Updates to the [ShortcutManagerUtilities](https://github.com/PGMacDesign/PGMacTips/blob/master/library/src/main/java/com/pgmacdesign/pgmactips/utilities/ShortcutManagerUtilities.java) class to include some new methods as well as catch the 'max number of shortcuts' exception thrown when passing more than 5. (Current max within the OS is 5 at the time of writing this)
* Bugfix: Fixed a bug in DateUtilities where the wrong date formatter was being used in accordance with a mismatched tag
* Update: Added some new methods in the [NumberUtilities](https://github.com/PGMacDesign/PGMacTips/blob/master/library/src/main/java/com/pgmacdesign/pgmactips/utilities/NumberUtilities.java) class to help with getting number values without worrying about null checks.
* Bugfix: Updated the [BiometricVerification](https://github.com/PGMacDesign/PGMacTips/blob/master/library/src/main/java/com/pgmacdesign/pgmactips/biometricutilities/BiometricVerification.java) class to update the required permissions for Android API level 28 (P) and fixed a bug relating to the cancellation listener. 
* Update: Updated [Permission Utilities](https://github.com/PGMacDesign/PGMacTips/blob/master/library/src/main/java/com/pgmacdesign/pgmactips/utilities/PermissionUtilities.java) to include the Biometric permission for for Android API level 28 (P).
* Update: Updated the [DateUtilities](https://github.com/PGMacDesign/PGMacTips/blob/master/library/src/main/java/com/pgmacdesign/pgmactips/utilities/DateUtilities.java) to include some new calculators. 
* Update: Updated the [DatabaseUtilities](https://github.com/PGMacDesign/PGMacTips/blob/master/library/src/main/java/com/pgmacdesign/pgmactips/utilities/DatabaseUtilities.java) to include some overloaded methods for maintaining the naming pattern. 
* Update: Updated the [GUIUtilities](https://github.com/PGMacDesign/PGMacTips/blob/master/library/src/main/java/com/pgmacdesign/pgmactips/utilities/GUIUtilities.java) to include code for adjusting the status bar color.
* Update: Included new code in the [ImageUtilities](https://github.com/PGMacDesign/PGMacTips/blob/master/library/src/main/java/com/pgmacdesign/pgmactips/utilities/ImageUtilities.java) class for obtaining the absolute Uri path correctly. 
* Update: Updated the [ViewUtilities](https://github.com/PGMacDesign/PGMacTips/blob/master/library/src/main/java/com/pgmacdesign/pgmactips/utilities/ViewUtilities.java) class to include code to convert bitmaps, views, and Drawables among different types. 

Version 1.0.1 *(2019-05-14)*
----------------------------

* Bugfix: Fixed multiple calls bug in the [RetrofitClient](https://github.com/PGMacDesign/PGMacTips/blob/master/library/src/main/java/com/pgmacdesign/pgmactips/networkclasses/retrofitutilities/RetrofitClient.java) class
* Update: Added in new customization options in the [RetrofitClient](https://github.com/PGMacDesign/PGMacTips/blob/master/library/src/main/java/com/pgmacdesign/pgmactips/networkclasses/retrofitutilities/RetrofitClient.java)  with respect to cookie response listeners, added in support for the retry count, and added in multiple new builder options.
* Update: Updated the [NumberUtilities](https://github.com/PGMacDesign/PGMacTips/blob/master/library/src/main/java/com/pgmacdesign/pgmactips/utilities/NumberUtilities.java) class to add in some simple functions for int array checks.

Version 1.0.0 *(2019-05-10)*
----------------------------

* Update: Refactored the MySampleApplication and MyTestActivity into a new samples module.
* New: Added a Java and Kotlin sample class for interacting with the [DatabaseUtilities](https://github.com/PGMacDesign/PGMacTips/blob/master/library/src/main/java/com/pgmacdesign/pgmactips/utilities/DatabaseUtilities.java) class and how to use it.

Version 0.0.81 *(2019-05-09)*
----------------------------

* New: Added in some new features to the [ImageUtilities](https://github.com/PGMacDesign/PGMacTips/blob/master/library/src/main/java/com/pgmacdesign/pgmactips/utilities/ImageUtilities.java) to allow for drawable coloring, imageview alterations, and a few other minor operations.
* New: Added in an overloaded method for `"isNullOrEmpty(String... strs)"` in the [StringUtilities](https://github.com/PGMacDesign/PGMacTips/blob/master/library/src/main/java/com/pgmacdesign/pgmactips/utilities/StringUtilities.java) class for easier checking of multiple values.
* New: Added in some new methods for checking that the passed int position in an array or list will not throw an ArrayIndexOutOfBoundsException in the [StringUtilities](https://github.com/PGMacDesign/PGMacTips/blob/master/library/src/main/java/com/pgmacdesign/pgmactips/utilities/StringUtilities.java) class.
* New: Added in code to adjust screen brightness (without Permissions) in the [MiscUtilities](https://github.com/PGMacDesign/PGMacTips/blob/master/library/src/main/java/com/pgmacdesign/pgmactips/utilities/MiscUtilities.java) class.
* Update: Updated the [Display Manager Utilities](https://github.com/PGMacDesign/PGMacTips/blob/master/library/src/main/java/com/pgmacdesign/pgmactips/utilities/DisplayManagerUtilities.java) to have more measurement options for the screen. Also updated the code folding / regions for easier reading

Version 0.0.80 *(2019-03-28)*
----------------------------

 * Update: Updates to the [ShortcutManagerUtilities](https://github.com/PGMacDesign/PGMacTips/blob/master/library/src/main/java/com/pgmacdesign/pgmactips/utilities/ShortcutManagerUtilities.java) class to include additional data save options and a quick bug fix.
 * Note: Version 0.0.79 removed because of small bug in release.

Version 0.0.78 *(2019-03-27)*
----------------------------

 * Update: Fixed a bug in [EncryptionUtilities](https://github.com/PGMacDesign/PGMacTips/blob/master/library/src/main/java/com/pgmacdesign/pgmactips/utilities/EncryptionUtilities.java) relating to devices that don't support the 256 encryption algorithm
 * New: Added in a new class, [ShortcutManagerUtilities](https://github.com/PGMacDesign/PGMacTips/blob/master/library/src/main/java/com/pgmacdesign/pgmactips/utilities/ShortcutManagerUtilities.java) as a boilerplate saver / code wrapper for the [Android Shortcuts](https://developer.android.com/guide/topics/ui/shortcuts) api.

Version 0.0.77 *(2019-03-21)*
----------------------------

 * Update: Changed the logic for [PGConnectivityReceiver](https://github.com/PGMacDesign/PGMacTips/tree/master/library/src/main/java/com/pgmacdesign/pgmactips/broadcastreceivers) to remove the camel-casing (broadcastReceivers --> broadcastreceivers). Note that this WILL break any manifest files that declare the [Connectivity Receiver](https://github.com/PGMacDesign/PGMacTips/blob/master/library/src/main/java/com/pgmacdesign/pgmactips/broadcastreceivers/PGConnectivityReceiver.java) to allow more than one listener to be present in the class.
 * Delete: Removed the FunctionalRecyclerview class + Package as it did not serve a purpose anymore.
 * Update: Updated the [Ticker](https://github.com/PGMacDesign/PGMacTips/blob/master/library/src/main/java/com/pgmacdesign/pgmactips/misc/Ticker.java) class to include a public static method that allows changing of the output logging to a single line for less logcat clutter.
 * New: Added in new Dimentions (@dimen/) vars for the different Text sizes for reference.
 * Update: Changed the [Permission Utilities](https://github.com/PGMacDesign/PGMacTips/blob/master/library/src/main/java/com/pgmacdesign/pgmactips/utilities/PermissionUtilities.java) logic to remove the show rationale dialog as it was causing problems for people who had other dialogs in place to manage it.

Version 0.0.76 *(2019-03-08)*
----------------------------

 * Update: Rework of [SharedPrefs](https://github.com/PGMacDesign/PGMacTips/blob/master/library/src/main/java/com/pgmacdesign/pgmactips/utilities/SharedPrefs.java) and [EncryptionUtilities](https://github.com/PGMacDesign/PGMacTips/blob/master/library/src/main/java/com/pgmacdesign/pgmactips/utilities/EncryptionUtilities.java) to finally fix the writing encrypted values to SP bug.
 * Update: Updated the [MiscUtilities](https://github.com/PGMacDesign/PGMacTips/blob/master/library/src/main/java/com/pgmacdesign/pgmactips/utilities/MiscUtilities.java) class to add in the ability to print byte arrays.
 * New: Added a new class, [StackStringsManager](https://github.com/PGMacDesign/PGMacTips/blob/master/library/src/main/java/com/pgmacdesign/pgmactips/stackmanagement/StackStringsManager.java) which is functionally very similar to the [StackManager](https://github.com/PGMacDesign/PGMacTips/blob/master/library/src/main/java/com/pgmacdesign/pgmactips/stackmanagement/StackManager.java) class except it manages stacks of Strings as opposed to a predefined set of enum values. Also removed the old StackManagerObjects class as it was superseded by the StringsStackManager.
 * New: Updated the [README](https://github.com/PGMacDesign/PGMacTips/blob/master/README.md) to include instructions on Obfuscation and utilizing Proguard with PGMacTips.

Version 0.0.75 *(2019-02-28)*
----------------------------

 * New: Added in new [PGMaterialRippleLayout](https://github.com/PGMacDesign/PGMacTips/blob/master/library/src/main/java/com/pgmacdesign/pgmactips/customui/PGMaterialRippleLayout.java) class for Ripple effect backgrounds.
 * New / Update: Updated old animations under the R.anim folder as well as added new ones.
 * New: Added in some new icons under the mipmap folders
 * Update: Updated the [PGConnectivityReceiver](https://github.com/PGMacDesign/PGMacTips/tree/master/library/src/main/java/com/pgmacdesign/pgmactips/broadcastreceivers) to remove the camel-casing (broadcastReceivers --> broadcastreceivers). Note that this WILL break any manifest files that declare the [Connectivity Receiver](https://github.com/PGMacDesign/PGMacTips/blob/master/library/src/main/java/com/pgmacdesign/pgmactips/broadcastreceivers/PGConnectivityReceiver.java) with newer instructions on how to operate with respect to newer APIs not registering the receiver.

Version 0.0.74 *(2019-01-15)*
----------------------------

 * New: Added in new gradle.properties entries that allow for AndroidX Support. Put these in your gradle.properties file to mirror this:


		# Jetpack Library info can be found here: https://developer.android.com/jetpack/androidx/
		# More info at this link: https://developer.android.com/jetpack/androidx/migrate
		android.useAndroidX=true
		android.enableJetifier=true

 * New: Removed the /ignore on the build.gradle file so it will be included in the next build; that wasy it can be used as a sample.

 * Update: Updating the [CustomAnnotationsBase](https://github.com/PGMacDesign/PGMacTips/blob/master/library/src/main/java/com/pgmacdesign/pgmactips/misc/CustomAnnotationsBase.java) to reference AndroidX support as well as updating Support lib: design - 28.0.0 and reverting back some of the changes from AndroidX migration so as to not cause conflict issues.
 * Bugfix: Users were indicating an issue with regards to updating gradle to 3.2.0 [issue is here](https://github.com/PGMacDesign/PGMacTips/issues/5) and this release should fix said issue, if not, will update and increment version afterwards.
 * Update: Adding in new functions to the [DateUtilities](https://github.com/PGMacDesign/PGMacTips/blob/master/library/src/main/java/com/pgmacdesign/pgmactips/utilities/DateUtilities.java) class for use in simple getters for a date String in the format of YYYY-MM-DD.


Version 0.0.73 *(2018-12-06)*
----------------------------

 * Fix: Fixed a bug in [VolleyUtilities](https://github.com/PGMacDesign/PGMacTips/blob/master/library/src/main/java/com/pgmacdesign/pgmactips/networkclasses/volleyutilities/VolleyUtilities.java) class that was preventing custom Content-Type headers from being observed.  
 * New: Adding in new functions to the [DateUtilities](https://github.com/PGMacDesign/PGMacTips/blob/master/library/src/main/java/com/pgmacdesign/pgmactips/utilities/DateUtilities.java) class for use in simple getters.
 * Update: Updating the [CustomAnnotationsBase](https://github.com/PGMacDesign/PGMacTips/blob/master/library/src/main/java/com/pgmacdesign/pgmactips/misc/CustomAnnotationsBase.java) to reference newest versions.

Version 0.0.72 *(2018-10-24)*
----------------------------

 * Update: Updated the Picasso library to the most current version and rewrote [ImageUtilities](https://github.com/PGMacDesign/PGMacTips/blob/master/library/src/main/java/com/pgmacdesign/pgmactips/utilities/ImageUtilities.java) to match the new structure. Note that image caching is handled automatically now and no longer has an overloaded method to pass in a percent max cache integer. Also, <i>Context</i> was removed as a required param for calls as it is not required anymore. Lastly, added region mapping in ImageUtilities for easier source code reading.
 * Update: Changed the code in the [PGMacTipsConfig](https://github.com/PGMacDesign/PGMacTips/blob/master/library/src/main/java/com/pgmacdesign/pgmactips/misc/PGMacTipsConfig.java) class to auto-initialize more fields.
 * Update: Updated the Gradle Build to Version 3.1.3 as per the recent Android Studio update. Note, did not use version 3.2.1 as per [this](https://stackoverflow.com/a/52608833/2480714) issue.
 * Update Updated the maps play services to version 16.0.0. Please note that I also had to reorganize where the `google()` and `maven { url "https://maven.google.com" }` items were placed within the build.gradle for <i>whatever reason</i>.
 * New: Added in a reference to the <i>Play-Services-Basement Issue</i> into the [ReadMe](https://github.com/PGMacDesign/PGMacTips/blob/master/README.md). This definitely took some time to resolve so I am placing it there for anyone else who has the issue.
 * Update: Updating the <i>android.enableAapt2=true</i> field in the gradle.properties class as per Google deprecation warnings.
 * Update: Updating the [RetrofitClient](https://github.com/PGMacDesign/PGMacTips/blob/master/library/src/main/java/com/pgmacdesign/pgmactips/networkclasses/retrofitutilities/RetrofitClient.java) with retry functionality.

Version 0.0.70 *(2018-10-02)*
----------------------------

 * New: Added in new Dialog static methods and variables to the [Dialog Utilities](https://github.com/PGMacDesign/PGMacTips/blob/master/library/src/main/java/com/pgmacdesign/pgmactips/utilities/DialogUtilities.java) class. The new static int references of <i>NO_RESPONSE</i>, <i>YES_RESPONSE</i>, and <i>SIMPLE_CLOSE_RESPONSE</i> mirror their respective counterparts of <i>FAIL_RESPONSE</i>, <i>SUCCESS_RESPONSE</i>, and <i>OTHER_RESPONSE</i>; they were added for clarification.
 * New: Added in 2 new images: [close_x_black](https://github.com/PGMacDesign/PGMacTips/blob/master/library/src/main/res/mipmap-xxxhdpi/close_x_black.png), and [close_x_white](https://github.com/PGMacDesign/PGMacTips/blob/master/library/src/main/res/mipmap-xxxhdpi/close_x_white.png) which are simple close icons. They are used in Dialog Utilities and can be accessed from outside the library as well.
 * New: Added in new class, [ClientSSLSocketFactory](https://github.com/PGMacDesign/PGMacTips/blob/master/library/src/main/java/com/pgmacdesign/pgmactips/networkclasses/volleyutilities/ClientSSLSocketFactory.java), for creating SSLSockets to use in the [VolleyUtilities](https://github.com/PGMacDesign/PGMacTips/blob/master/library/src/main/java/com/pgmacdesign/pgmactips/networkclasses/volleyutilities/VolleyUtilities.java) class. This is mirroring the response given in [this](https://stackoverflow.com/a/30943348/2480714) stackoverflow answer after stumbling upon [this error](https://stackoverflow.com/questions/30538640/javax-net-ssl-sslexception-read-error-ssl-0x9524b800-i-o-error-during-system) yesterday. For more information on this issue, see the [TLS](https://en.wikipedia.org/wiki/Transport_Layer_Security) topic and how Google's Volley library relates in my links above.

Version 0.0.68 *(2018-09-06)*
----------------------------

 * Rename: Renamed the broadcastreceivers package name [here](https://github.com/PGMacDesign/PGMacTips/tree/master/library/src/main/java/com/pgmacdesign/pgmactips/broadcastreceivers) to remove the camel-casing (broadcastReceivers --> broadcastreceivers). Note that this WILL break any manifest files that declare the [Connectivity Receiver](https://github.com/PGMacDesign/PGMacTips/blob/master/library/src/main/java/com/pgmacdesign/pgmactips/broadcastreceivers/PGConnectivityReceiver.java) as it changes the pathing. Just rename it your manifest and it should work the same.
 * Rename: Renamed SamplePojo @SerializedName field that was a duplicate to manually trigger IllegalStateException back to different one.
 * Fix: Fixed a bug in DatabaseUtilities that was causing a NullPointerException to be thrown if no items could be found to be deleted / de-persisted. (Should also fix objects not being removed properly from the DB)


Version 0.0.67 *(2018-09-04)*
----------------------------

 * Update: Updated the [StackManager](https://github.com/PGMacDesign/PGMacTips/blob/master/library/src/main/java/com/pgmacdesign/pgmactips/stackmanagement/StackManager.java) class to include overloaded methods for single stack management of one type of enum, added in getters for the size, and changed the behavior so it will display errors in the log rather than throwing StackManagerExceptions.
 * Update: Updated the [MiscUtilities](https://github.com/PGMacDesign/PGMacTips/blob/master/library/src/main/java/com/pgmacdesign/pgmactips/utilities/MiscUtilities.java) to include a simple function to convert the passed enum into a list of said enum values.

Version 0.0.66 *(2018-08-16)*
----------------------------

 * Revert: Reverted changes made in the [MapUtilities](https://github.com/PGMacDesign/PGMacTips/blob/master/library/src/main/java/com/pgmacdesign/pgmactips/maputilities/MapUtilities.java), [SphericalUtils](https://github.com/PGMacDesign/PGMacTips/blob/master/library/src/main/java/com/pgmacdesign/pgmactips/maputilities/SphericalUtils.java) , and [ScaleBar](https://github.com/PGMacDesign/PGMacTips/blob/master/library/src/main/java/com/pgmacdesign/pgmactips/customui/ScaleBar.java) classes as they were not the direct cause of [this issue](https://github.com/PGMacDesign/PGMacTips/issues/3).
 * Revert: Reverted the changes in the [CustomAnnotationsBase](https://github.com/PGMacDesign/PGMacTips/blob/master/library/src/main/java/com/pgmacdesign/pgmactips/misc/CustomAnnotationsBase.java) class for the same reasons mentioned above.
 * Update: Updated the Realm Version from [3.0.0](https://realm.io/docs/java/3.0.0/api/) to [4.2.0](https://realm.io/docs/java/4.2.0/api/). Those wishing to use previous versions of Realm (or those that have a version lower than 4.2.0 declared in their own gradle file) will need to use PGMacTips version 0.0.64 or below.
 * Update: Made multiple internal edits to the [DatabaseUtilities](https://github.com/PGMacDesign/PGMacTips/blob/master/library/src/main/java/com/pgmacdesign/pgmactips/utilities/DatabaseUtilities.java) class that should not affect existing code using it. Also added in logging to indicate when objects won't save due to multiple @SerializedName Strings; as seen [here](https://stackoverflow.com/a/42517143/2480714). For the DefaultRealmModule [issue](https://github.com/PGMacDesign/PGMacTips/issues/3), added in new module class to prevent more future crashing.
 * Note: Version 65 was skipped as the initial release missed a few bugs.

Version 0.0.64 *(2018-08-15)*
----------------------------

 * Fix: Fixed a bug in the [MapUtilities](https://github.com/PGMacDesign/PGMacTips/blob/master/library/src/main/java/com/pgmacdesign/pgmactips/maputilities/MapUtilities.java), [SphericalUtils](https://github.com/PGMacDesign/PGMacTips/blob/master/library/src/main/java/com/pgmacdesign/pgmactips/maputilities/SphericalUtils.java) , and [ScaleBar](https://github.com/PGMacDesign/PGMacTips/blob/master/library/src/main/java/com/pgmacdesign/pgmactips/customui/ScaleBar.java) classes; specifically how it conflicted with other build variants in projects using a different version of Google Maps. See the link [here](https://github.com/PGMacDesign/PGMacTips/issues/3) for more details.
 * Update: Removed the OAuthUtilities class as it was mostly deprecated and unused anyway.
 * Update: Removed the <i>CustomAnnotationsBase.Dependencies.GooglePlayServices_Maps</i> annotation from the [CustomAnnotationsBase](https://github.com/PGMacDesign/PGMacTips/blob/master/library/src/main/java/com/pgmacdesign/pgmactips/misc/CustomAnnotationsBase.java) class as it will no longer be needed as per fixes mentioned in this update.
 * Update: Changed the way the Realm dependency is declared in the gradle files as per [this](https://stackoverflow.com/a/51447494/2480714) stackoverflow answer.
 * Note: Version 63 was skipped as the initial release missed a few bugs.


Version 0.0.62 *(2018-08-01)*
----------------------------

 * Update: Refactored the LyftUtilities and UberUtilities into a new package named [MiscThirdPartyUtilities](https://github.com/PGMacDesign/PGMacTips/tree/master/library/src/main/java/com/pgmacdesign/pgmactips/miscthirdpartyutilities)
 * New: Added some new functions into the [NumberUtilities](https://github.com/PGMacDesign/PGMacTips/blob/master/library/src/main/java/com/pgmacdesign/pgmactips/utilities/NumberUtilities.java) class for simple calculations.
 * Fix: Fixed a bug relating to the [RetrofitParser](https://github.com/PGMacDesign/PGMacTips/blob/master/library/src/main/java/com/pgmacdesign/pgmactips/networkclasses/retrofitutilities/RetrofitParser.java) and how it parsed objects that were not null, but contained no elements.

Version 0.0.61 *(2018-07-24)*
----------------------------

 * Fix: Fixed bugs relating to [RetrofitParser](https://github.com/PGMacDesign/PGMacTips/blob/master/library/src/main/java/com/pgmacdesign/pgmactips/networkclasses/retrofitutilities/RetrofitParser.java) and updated the responses.
 * New: Added in some new [StringUtilities](https://github.com/PGMacDesign/PGMacTips/blob/master/library/src/main/java/com/pgmacdesign/pgmactips/utilities/StringUtilities.java) to calculate the [LevenshteinDistance](https://en.wikipedia.org/wiki/Levenshtein_distance)

Version 0.0.60 *(2018-07-10)*
----------------------------

 * New: Added in the BiometricUtilities package and [BiometricVerification](https://github.com/PGMacDesign/PGMacTips/blob/master/library/src/main/java/com/pgmacdesign/pgmactips/biometricutilities/BiometricVerification.java) class, which allows access to fingerprint checking.
 * Improvement: Refactored [SharedPrefs](https://github.com/PGMacDesign/PGMacTips/blob/master/library/src/main/java/com/pgmacdesign/pgmactips/utilities/SharedPrefs.java) to have all Encrypted aspects merged into the one file. Deprecated the older SharedPrefsEncrypted class and removed the dependency. The class should work identical to before but now has the option to use either encryption or not on the fly without having to issue a new build.
 * Update: Altered the ThreeButtonDialog in [DialogUtilities](https://github.com/PGMacDesign/PGMacTips/blob/master/library/src/main/java/com/pgmacdesign/pgmactips/utilities/DialogUtilities.java) class to resize proportionally for less text and allowed auto-resizing whenever one button text is sent in as null; this will allow for <3 buttons should someone choose to omit one purposefully.
 * Update: Refactored the [EncryptionUtilities](https://github.com/PGMacDesign/PGMacTips/blob/master/library/src/main/java/com/pgmacdesign/pgmactips/utilities/EncryptionUtilities.java) class to utilize better practices in saving and persisting data. Pulled most of the code from [this](https://github.com/tozny/java-aes-crypto/blob/master/aes-crypto/src/main/java/com/tozny/crypto/android/AesCbcWithIntegrity.java) repository.
 * Update: Refactored getPackageName() out of [MiscUtilities](https://github.com/PGMacDesign/PGMacTips/blob/master/library/src/main/java/com/pgmacdesign/pgmactips/utilities/MiscUtilities.java) and into [SystemUtilities](https://github.com/PGMacDesign/PGMacTips/blob/master/library/src/main/java/com/pgmacdesign/pgmactips/utilities/SystemUtilities.java)
 * Update: Refactored the various getUTF8(), getISO8859(), etc calls out of [EncryptionUtilities](https://github.com/PGMacDesign/PGMacTips/blob/master/library/src/main/java/com/pgmacdesign/pgmactips/utilities/EncryptionUtilities.java) and into [MiscUtilities](https://github.com/PGMacDesign/PGMacTips/blob/master/library/src/main/java/com/pgmacdesign/pgmactips/utilities/MiscUtilities.java).



Version 0.0.57-59 *(2018-07-03)*
----------------------------

 * Improvement: Updated PermissionUtilities to add overloaded methods to take in String arrays for permissions instead of the dedicated enum.
 * Fix: Updated the SharedPrefsEncrypted class to designate better
 * Improvement: Moved the larger .gif files to a separate folder so that they won't take up space in the build files. They can now be found in the **sample_gifs** directory in the root.
 * Update: Changed the logo icons as well as deleting the old ic images.
 * Update: Changed the documentation for the SMSBroadcastReceiver to clarify some things and make sure the manifest sample info gets included with the javadoc files.
 * Fix: Updated multiple portions of the app with if checks on the version so as to prevent exceptions from arising when hitting them with API levels <16.
 * Update: Refactored the package naming for multiple directories (Google Vision, customUI, etc) and moved files around to make for a more logical hierarchy.
 * Update: Removed dup class GPSTracker
 * New: Custom Annotations. Added in new custom 'requiresDependency' and 'requiresDependencies' annotations that indicate which classes and methods use certain dependencies so as to prevent crashes when they are excluded.
 * Update: Added in new documentation to many classes to help clear up ambiguity

Version 0.0.56 *(2018-06-25)*
----------------------------

 * Fix: Multiple bug fixes across the library to catch unique bugs that may be cause by omitting certain libraries but maintaining this one.
 * New: Added in Javadoc zip file for documentation purposes.

Version 0.0.55 *(2018-04-23)*
----------------------------

 * New: Added in the getContactData() method to ContactUtilities which allows a single call to pull all data from the contact in one call as opposed to the only other way being the nested cursors call, which can be very slow.


Version 0.0.54 *(2018-04-20)*
----------------------------

 * New: Upgraded ContactUtilities to include 3 new overloaded methods (getAllContacts()) which will retrieve ALL data from the contact DB. Note that this is slower as it is doing nested calls, but it is definitely pulling all info.
 * Fix: Fixed ContactUtilities issues where crashes were happening due to not properly declaring the permissions needed. Should not be annotated over the respective methods.


Version 0.0.53 *(2018-04-19)*
----------------------------

 * Fix: Reduced minimum required SDK to 15 (from 17) as this was causing problems for those setting lower minimum SDKs.


Version < 0.0.52 *(2018-04-18)*
----------------------------

 * New: Changelog will be kept up to date from this point forward.
