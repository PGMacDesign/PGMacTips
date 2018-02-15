[![Platform](https://img.shields.io/badge/platform-android-green.svg)](http://developer.android.com/index.html)
<img src="https://img.shields.io/badge/license-Apache 2.0-green.svg?style=flat">
[![API](https://img.shields.io/badge/API-14%2B-green.svg?style=flat)](https://android-arsenal.com/api?level=14)
[![JitPack](https://jitpack.io/v/pgmacdesign/PGMacTips.svg)](https://jitpack.io/#pgmacdesign/PGMacTips)
[![Build Status](https://travis-ci.org/PGMacDesign/PGMacTips.svg?branch=master)](https://travis-ci.org/PGMacDesign/PGMacTips)


# PGMacTips
This will be a collection of utility methods that I use in nearly all of my projects

## Installation

To install, insert this into your build.gradle file 

```java

allprojects {
	repositories {
		jcenter()
		maven { url "https://jitpack.io" }
		maven { url "https://maven.google.com" } // Google's Maven repository 
	}
}

```

And include this in your dependencies section:

```java

implementation ('com.github.PGMacDesign:PGMacTips:0.0.46')

```

Having trouble with Jitpack? [This link](https://jitpack.io/#pgmacdesign/PGMacTips) here will show what is going on with the current build as well as give you instructions on integrating Jitpack into your project. 

## Nested Libraries

This library utilizes many others within it. Below is a listing of all of the nested libraries this project utilizes under the hood. If you wish to use a class, method, or function that uses one of these other libraries, make sure to include it in your project.

```java

        //Secure Shared Prefs. Link: https://github.com/scottyab/secure-preferences
        implementation 'com.scottyab:secure-preferences-lib:0.1.4'

        //Progress bar animator. This one is better for longer processes, IE uploading photos.
        //Link: https://github.com/Tibolte/ElasticDownload
        implementation 'com.github.tibolte:elasticdownload:1.0.4'

        //Image Cropper. https://github.com/Yalantis/uCrop
        implementation 'com.yalantis:ucrop:2.2.0'

        //Animated SVG View (Custom, takes an SVG To work).
        //Link: https://github.com/jaredrummler/AnimatedSvgView
        implementation 'com.jaredrummler:animated-svg-view:1.0.5'

        //Volley. Link: https://github.com/google/volley
        implementation 'com.android.volley:volley:1.0.0'

        //GSON. Link https://github.com/google/gson
        implementation 'com.google.code.gson:gson:2.8.1'

        //Picasso. Link: https://github.com/square/picasso
        implementation 'com.squareup.picasso:picasso:2.5.2'

        //Animations Base. Link: https://github.com/JakeWharton/NineOldAndroids
        implementation 'com.nineoldandroids:library:2.4.0'

        //Retrofit, Retrofit Converters, and OKHTTP.
        //Retrofit - https://github.com/square/retrofit
        //OKHttp - https://github.com/square/okhttp
        implementation 'com.squareup.retrofit2:retrofit:2.3.0'
        implementation 'com.squareup.okhttp3:okhttp:3.9.0'
        implementation 'com.squareup.okhttp3:logging-interceptor:3.9.0'
        //Type-safe HTTP client for Android and Java: https://github.com/square/retrofit
        implementation 'com.squareup.retrofit2:converter-gson:2.3.0'
        //OKIO. Link: https://github.com/square/okio
        implementation 'com.squareup.okio:okio:1.13.0'

        //Part of The Android Animations collection below
        implementation 'com.daimajia.easing:library:2.0@aar'
        //Android Animations. Link: https://github.com/daimajia/AndroidViewAnimations
        implementation 'com.daimajia.androidanimations:library:2.2@aar'

        //Text View + Animations. Link: https://github.com/hanks-zyh/HTextView
        implementation 'hanks.xyz:htextview-library:0.1.5'


        //////////////////////////////////////////////////////
        //Recommended to use if utilizing multiple libraries//
        //////////////////////////////////////////////////////

        //Multi-dex. For more info: https://developer.android.com/studio/build/multidex.html
        implementation 'com.android.support:multidex:1.0.1'

```

## Known Issues

Depending on your version of Google's Libraries, you may run into this error:

```java
Error:Execution failed for task ':app:processDebugManifest'.
> Manifest merger failed : Attribute meta-data#android.support.VERSION@value value=(26.0.1) from [com.android.support:design:26.0.1] AndroidManifest.xml:28:13-35
	is also present at [com.android.support:appcompat-v7:26.1.0] AndroidManifest.xml:28:13-35 value=(26.1.0).
	Suggestion: add 'tools:replace="android:value"' to <meta-data> element at AndroidManifest.xml:26:9-28:38 to override.
```

Or something along those lines. If you do, simply add this line of code to your build.gradle file underneath the Android Tag

```java
    configurations.all {
        resolutionStrategy.eachDependency { DependencyResolveDetails details ->
            def requested = details.requested
            if (requested.group == 'com.android.support') { //Replace String here with whichever error is thrown
                if (!requested.name.startsWith("multidex")) {
                    details.useVersion '26.0.2' //Replace version here with whatever you are using
                }
            }
        }
    }
```	

## New Issues

If you run into any compatability issues or bugs, please open a ticket ASAP so I can take a look at it. 

## Important Notes

Please keep in mind that as this is still in the beta phase, it will change dramatically before launch. 