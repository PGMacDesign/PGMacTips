[![Platform](https://img.shields.io/badge/platform-android-green.svg)](http://developer.android.com/index.html)
<img src="https://img.shields.io/badge/license-Apache 2.0-green.svg?style=flat">
[![API](https://img.shields.io/badge/API-15%2B-green.svg?style=flat)](https://android-arsenal.com/api?level=15)
[![JitPack](https://jitpack.io/v/pgmacdesign/PGMacTips.svg)](https://jitpack.io/#pgmacdesign/PGMacTips)
[![Build Status](https://travis-ci.org/PGMacDesign/PGMacTips.svg?branch=master)](https://travis-ci.org/PGMacDesign/PGMacTips)
![GitHub last commit](https://img.shields.io/github/last-commit/google/skia.svg)

# PGMacTips
This will be a collection of utility methods that I use in nearly all of my projects

## Installation

To install, insert this into your projects root build.gradle file 

```java
allprojects {
    repositories {
        jcenter()
        maven { url "https://jitpack.io" }
        maven { url "https://maven.google.com" } // Google's Maven repository 
    }
}
```

And include this in your dependencies section of your module .gradle file:

```java
implementation ('com.github.PGMacDesign:PGMacTips:0.0.58')
```

Having trouble with Jitpack? [This link](https://jitpack.io/#pgmacdesign/PGMacTips) here will show what is going on with the current build as well as give you instructions on integrating Jitpack into your project. 

## Javadoc

Javadoc info can be found [here](https://jitpack.io/com/github/pgmacdesign/PGMacTips/0.0.58/javadoc/): 

If you would like to view docs for older version, just replace the version code in this url:

https://jitpack.io/com/github/pgmacdesign/PGMacTips/[VERSION-GOES-HERE]/javadoc/


## Nested Libraries & Dependencies

This library utilizes many others within it; the full list of dependencies, links to their reposotiry pages, and the version used can be found under the [*CustomAnnotationsBase*](https://github.com/PGMacDesign/PGMacTips/blob/master/library/src/main/java/com/pgmacdesign/pgmactips/misc/CustomAnnotationsBase.java) class.

### RequiredDependencies

Under this same [*CustomAnnotationsBase*](https://github.com/PGMacDesign/PGMacTips/blob/master/library/src/main/java/com/pgmacdesign/pgmactips/misc/CustomAnnotationsBase.java) class, There is an interface that is used throughout the library. This is the annotation base which helps to identify which classes use or require a dependency. The use of some classes requires the dependency for any methods to be used while others only require the dependency if a specific method or function is used. 

For example, if you reference the [*ImageUtilities*](https://github.com/PGMacDesign/PGMacTips/blob/master/library/src/main/java/com/pgmacdesign/pgmactips/utilities/ImageUtilities.java) class and want to use the *resizePhoto()* method, it uses all native Android classes and requires no custom dependencies to work properly. If you instead wanted to use the *setImageWithPicasso()* method, this uses Picasso as a required dependency to operate and is so marked with said annotation above the method itself. Note that the class does not have such an annotation, but the method does. 

```java
@CustomAnnotationsBase.RequiresDependency(requiresDependency = CustomAnnotationsBase.Dependencies.Picasso)
```

On the other hand, if you want to use the [*DatabaseUtilities*](https://github.com/PGMacDesign/PGMacTips/blob/master/library/src/main/java/com/pgmacdesign/pgmactips/utilities/DatabaseUtilities.java) and any of its subsequent methods, you must include both the Gson and Realm dependencies so the class itself has the annotation indicating such. 

```java
@CustomAnnotationsBase.RequiresDependency(requiresDependencies = {CustomAnnotationsBase.Dependencies.Realm,
        CustomAnnotationsBase.Dependencies.GSON})
```

### Realm 

Note that one of the dependencies [Realm](https://github.com/realm/realm-java) is added to your project in the base build.gradle file and is done so with these two code snippets: 

```java
//This goes in your base, root .gradle file
buildscript {
    dependencies {
        classpath "io.realm:realm-gradle-plugin:3.0.0" 
        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}
```

```java
//This goes in your module level .gradle file
apply plugin: 'realm-android'
```

Please note that I started deviating from Realm's updates once they started requiring accounts / credentials to use. I utilize the code found in [this release](https://github.com/realm/realm-java/releases/tag/v3.0.0). I am unsure of forwards compatibility, but if newer versions will work, please let me know and I will update to the highest possible one without making changes to the source code. 

## Known Issues

Depending on your version of Google's Libraries, you may run into this error:

```java
Error:Execution failed for task ':app:processDebugManifest'.
> Manifest merger failed : Attribute meta-data#android.support.VERSION@value value=(26.0.1) from [com.android.support:design:26.0.1] AndroidManifest.xml:28:13-35
	is also present at [com.android.support:appcompat-v7:26.1.0] AndroidManifest.xml:28:13-35 value=(26.1.0).
	Suggestion: add 'tools:replace="android:value"' to <meta-data> element at AndroidManifest.xml:26:9-28:38 to override.
```

Or an error indicating that "Multiple dex files define...".

Or something along those lines. If you do, simply add this line of code to your build.gradle file underneath the Android Tag

```java
    configurations.all {
        resolutionStrategy.eachDependency { DependencyResolveDetails details ->
            def requested = details.requested
            if (requested.group == 'com.android.support') { //Replace String here with whichever error is thrown
                if (!requested.name.startsWith("multidex")) {
                    details.useVersion '26.0.2' //Replace version here with whatever you are using; this will override the other one
                }
            }
        }
    }
```	

## New Issues

If you run into any compatability issues or bugs, please open a ticket ASAP so I can take a look at it. 

## Important Notes

Please keep in mind that as this is still in the beta phase, it will change dramatically before launch. 