Change Log
==========

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