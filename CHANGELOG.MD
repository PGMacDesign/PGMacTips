Change Log
==========

Version 0.0.60 *(2018-07-06)*
----------------------------

 * New: Added in FingerprintUtilities, which allows access to fingerprint checking. 
 
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