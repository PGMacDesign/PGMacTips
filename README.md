[![Platform](https://img.shields.io/badge/platform-android-green.svg)](http://developer.android.com/index.html)
<img src="https://img.shields.io/badge/license-Apache 2.0-green.svg?style=flat">
[![API](https://img.shields.io/badge/API-15%2B-green.svg?style=flat)](https://android-arsenal.com/api?level=15)
[![JitPack](https://jitpack.io/v/pgmacdesign/PGMacTips.svg)](https://jitpack.io/#pgmacdesign/PGMacTips)
[![Build Status](https://travis-ci.org/PGMacDesign/PGMacTips.svg?branch=master)](https://travis-ci.org/PGMacDesign/PGMacTips)
![GitHub last commit](https://img.shields.io/github/last-commit/google/skia.svg)

# PGMacTips

This is a collection of utility methods, various wrappers, and examples that are all used to reduce boilerplate code in projects. 

For a list of changes and the differences in versions, please see the [Changelog](https://github.com/PGMacDesign/PGMacTips/blob/master/CHANGELOG.MD). 

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
implementation ('com.github.PGMacDesign:PGMacTips:0.0.67')
```

Having trouble with Jitpack? [This link](https://jitpack.io/#pgmacdesign/PGMacTips) here will show what is going on with the current build as well as give you instructions on integrating Jitpack into your project. 

<!-- 
//Removed for now due to Jitpack issues 

## Javadoc

Javadoc info can be found [here](https://jitpack.io/com/github/pgmacdesign/PGMacTips/0.0.67/javadoc/): 

If you would like to view docs for older version (supported versions are those >= 0.0.602), just replace the version code in this url:

https://jitpack.io/com/github/pgmacdesign/PGMacTips/[VERSION-GOES-HERE]/javadoc/

-->


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

Note that one of the dependencies [Realm](https://github.com/realm/realm-java) has a plethora of releases and using different versions can cause crashes and problems, IE [Issue 3](https://github.com/PGMacDesign/PGMacTips/issues/3).  The solution for that particular issue was related to a custom Module, regardless, see the versioning guide below to prevent code breaking builds if you yourself are using Realm as a dependency:

  * If using version >= 3.0.0 but < than 4.2.0, use a release equal to or less than [0.0.64](https://github.com/PGMacDesign/PGMacTips/releases/tag/0.0.64)
  * If using version 4.2.0, use a release at or above [0.0.66](https://github.com/PGMacDesign/PGMacTips/releases/tag/0.0.66). 

Also note that the old method for declaring Realm in the gradle file has been updated as per [this](https://realm.io/blog/android-installation-change/) announcement.
The new way I am declaring the library is as follows:

in the build.gradle file, but here is the code copied over (changing api to implementation):

At the project level: 
```java
buildscript {
    repositories {
        jcenter()
    }
    allprojects {
        repositories {
            jcenter()
        }
    }
    dependencies {
        classpath 'com.android.tools.build:gradle:3.0.1' //Your gradle version
		
        //Realm
        classpath "io.realm:realm-gradle-plugin:4.2.0" //<--------

        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}
```

At the module level:
```java
apply plugin: 'realm-android' //<--------
allprojects {
    repositories {
        jcenter()
        maven { url "https://jitpack.io" }
    }
}

```

I started deviating from Realm's updates once they started requiring accounts / credentials to use. 
I originally used version 3.0.0, but have since upgraded to 4.2.0. See the list of releases [here](https://github.com/realm/realm-java/releases)
I am unsure of forwards compatibility, but if newer versions will work or break the current project, please let me know and I will update to the highest possible one without making changes to the source code. 



## Known Issues

### Manifest merger errors 

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
                    details.useVersion '27.1.1' //Replace version here with whatever you are using; this will override the other one
                }
            }
        }
    }
```

### TransformExceptions

Your build may work just fine when debugging, but break when you try to make an APK file. If it does not work when you do this and this error appears:

```
Note: Recompile with -Xlint:unchecked for details.
FAILURE: Build failed with an exception.

* What went wrong:
Execution failed for task ':app:transformClassesWithMultidexlistForDebug'.
> com.android.build.api.transform.TransformException: Error while generating the main dex list.

* Try:
Run with --stacktrace option to get the stack trace. Run with --info or --debug option to get more log output. Run with --scan to get full insights.

* Get more help at https://help.gradle.org

BUILD FAILED in 49s
```

This issue was discovered with [Issue 3](https://github.com/PGMacDesign/PGMacTips/issues/3) and is related to version compatability issues relating to a dependency or yours clashing with one of mine. Normally this is fixed by following the logic in one of the following stackoverflow links: [Solution 1](https://stackoverflow.com/questions/33717886/errorexecution-failed-for-task-apptransformclasseswithdexfordebug), [solution 2](https://stackoverflow.com/questions/37497882/errorexecution-failed-for-task-apptransformclasseswithdexfordebug-in-androi/37498940), or [Solution 3](https://stackoverflow.com/questions/35890257/android-errorexecution-failed-for-task-apptransformclasseswithdexforrelease). 

Sometimes, however, none of these will resolve your issue. Don't despair though! This can happen when a dependency has made changes that are breaking a core section of my code. An example would be Realm when it upgraded from versions 3.0.0 to 4.2.0 we saw an enormous number of breaking changes and it caused this issue to come about. 

If you happen to be in this situation, either [make a new issue](https://github.com/PGMacDesign/PGMacTips/issues/new) or message me with the info and I will work to get an update out as soon as I am able. 

Update: the [issue](https://github.com/PGMacDesign/PGMacTips/issues/3) has been resolved since 2018-08-16 in version [0.0.66](https://github.com/PGMacDesign/PGMacTips/releases/tag/0.0.66). and the solution to fix it was declared within the issue description and comments.

## New Issues

If you run into any compatability issues or bugs, please [make a new issue](https://github.com/PGMacDesign/PGMacTips/issues/new) ASAP so I can take a look at it. 

## Important Notes

Please keep in mind that as this is still in the beta phase, it will change dramatically before launch. 