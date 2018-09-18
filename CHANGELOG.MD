Change Log
==========


Version 0.0.69 *(TBD)*
----------------------------

 * New: Added in new Dialog static methods and variables to the [Dialog Utilities](https://github.com/PGMacDesign/PGMacTips/blob/master/library/src/main/java/com/pgmacdesign/pgmactips/utilities/DialogUtilities.java) class. The new static int references of <i>NO_RESPONSE</i>, <i>YES_RESPONSE</i>, and <i>SIMPLE_CLOSE_RESPONSE</i> mirror their respective counterparts of <i>FAIL_RESPONSE</i>, <i>SUCCESS_RESPONSE</i>, and <i>OTHER_RESPONSE</i>; they were added for clarification. 
 * New: Added in 2 new images: [close_x_black](https://github.com/PGMacDesign/PGMacTips/blob/master/library/src/main/res/mipmap-xxxhdpi/close_x_black.png), and [close_x_white](https://github.com/PGMacDesign/PGMacTips/blob/master/library/src/main/res/mipmap-xxxhdpi/close_x_white.png) which are simple close icons. They are used in Dialog Utilities and can be accessed from outside the library as well. 

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
 * New: Added in some new StringUtilities to calculate the [LevenshteinDistance](https://en.wikipedia.org/wiki/Levenshtein_distance)
 
Version 0.0.60 *(2018-07-10)*
----------------------------

 * New: Added in BiometricUtilities and BiometricVerification, which allows access to fingerprint checking. 
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