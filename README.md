# PGMacUtilities
This will be a collection of utility methods that I use in nearly all of my projects

## Installation

To install, insert this into your build.gradle file 

```java

allprojects {
 repositories {
    jcenter()
    maven { url "https://jitpack.io" }
 }
}

```

And include this underneath your dependencies section:

```java

compile ('com.github.PGMacDesign:PGMacUtilities:0.0.26')

```

## Nested Libraries

This library utilizes many others within it. Below is a listing of all of the nested libraries this project utilizes under the hood. If you wish to use a class, method, or function that uses one of these other libraries, make sure to include it in your project.

```java

        //Secure Shared Prefs. Link: https://github.com/scottyab/secure-preferences
        compile 'com.scottyab:secure-preferences-lib:0.1.4'

        //Cloudrail. Link: https://github.com/CloudRail/cloudrail-si-android-sdk
        compile 'com.cloudrail:cloudrail-si-android:2.16.0'

        //Progress bar animator. This one is better for longer processes, IE uploading photos.
        //Link: https://github.com/Tibolte/ElasticDownload
        compile 'com.github.tibolte:elasticdownload:1.0.4'

        //Image Cropper. https://github.com/Yalantis/uCrop
        compile 'com.yalantis:ucrop:2.2.0'

        //Animated SVG View (Custom, takes an SVG To work).
        //Link: https://github.com/jaredrummler/AnimatedSvgView
        compile 'com.jaredrummler:animated-svg-view:1.0.5'

        //Volley. Link: https://github.com/google/volley
        compile 'com.android.volley:volley:1.0.0'

        //GSON. Link https://github.com/google/gson
        compile 'com.google.code.gson:gson:2.8.1'

        //Picasso. Link: https://github.com/square/picasso
        compile 'com.squareup.picasso:picasso:2.5.2'

        //Animations Base. Link: https://github.com/JakeWharton/NineOldAndroids
        compile 'com.nineoldandroids:library:2.4.0'

        //Retrofit, Retrofit Converters, and OKHTTP.
        //Retrofit - https://github.com/square/retrofit
        //OKHttp - https://github.com/square/okhttp
        compile 'com.squareup.retrofit2:retrofit:2.3.0'
        compile 'com.squareup.okhttp3:okhttp:3.9.0'
        compile 'com.squareup.okhttp3:logging-interceptor:3.9.0'
        //Type-safe HTTP client for Android and Java: https://github.com/square/retrofit
        compile 'com.squareup.retrofit2:converter-gson:2.3.0'
        //OKIO. Link: https://github.com/square/okio
        compile 'com.squareup.okio:okio:1.13.0'

        //Part of The Android Animations collection below
        compile 'com.daimajia.easing:library:2.0@aar'
        //Android Animations. Link: https://github.com/daimajia/AndroidViewAnimations
        compile 'com.daimajia.androidanimations:library:2.2@aar'

        //Text View + Animations. Link: https://github.com/hanks-zyh/HTextView
        compile 'hanks.xyz:htextview-library:0.1.5'


        //////////////////////////////////////
        //Misc Dependencies (IE Google Libs)//
        //////////////////////////////////////

        //Multi-dex. For more info: https://developer.android.com/studio/build/multidex.html
        compile 'com.android.support:multidex:1.0.1'

```


## Issues

If you run into any compatability issues or bugs, please open a ticket ASAP so I can take a look at it. 

## Important Notes

Please keep in mind that as this is still in the beta phase, it will change dramatically before launch. 