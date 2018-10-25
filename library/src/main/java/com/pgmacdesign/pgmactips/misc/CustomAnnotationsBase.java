package com.pgmacdesign.pgmactips.misc;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by pmacdowell on 7/3/2018.
 */
public class CustomAnnotationsBase {

    public static enum Dependencies {
        //Removed on 2018-07-10; refactored into SharedPrefs
//        SecurePreferences("https://github.com/scottyab/secure-preferences",
//                "com.scottyab:secure-preferences-lib:0.1.4", "0.1.4",
//                "Used for Shared Preferences encryption to protect data"),
        ElasticDownload("https://github.com/Tibolte/ElasticDownload",
                "com.github.tibolte:elasticdownload:1.0.4", "1.0.4",
                "Used as a UI animation for progress bars"),
        uCrop("https://github.com/Yalantis/uCrop",
                "com.yalantis:ucrop:2.2.0", "2.2.0",
                "Photo cropping tool with clean GUI. Used in various camera / media tools"),
        AnimatedSVGView("https://github.com/jaredrummler/AnimatedSvgView",
                "com.jaredrummler:animated-svg-view:1.0.5", "1.0.5",
                "Progress bar loading animation used as an indeterminate loader with svg Strings"),
        Volley("https://github.com/google/volley",
                "com.android.volley:volley:1.0.0", "1.0.0",
                "Volley is Google's core networking dependency. Used in the networking utilities package"),
        GSON("https://github.com/google/gson",
                "com.google.code.gson:gson:2.8.2", "2.8.2",
                "GSON is a JSON serializer and deserializer by Google. It is used in multiple places."),
        Picasso("https://github.com/square/picasso",
                "com.squareup.picasso:picasso:2.71828", "2.71828",
                "Picasso is an image setting lib that is used in the ImageUtilities to set images from local resources and web urls"),
        //Removed on 2018-07-01; not needed anymore with AndroidAnimations
//        NineOldAndroids("https://github.com/JakeWharton/NineOldAndroids",
//                "com.nineoldandroids:library:2.4.0", "2.4.0",
//                "Old Android library is used as the base for a large number of animations libraries"),
        Retrofit2("com.squareup.retrofit2:retrofit:2.4.0",
                "https://github.com/square/retrofit", "2.4.0",
                "Used for web calls, building local apis, and interacting with complicated web interfaces"),
        Retrofit2GSONConverter("https://github.com/square/retrofit",
                "com.squareup.retrofit2:converter-gson:2.3.0", "2.3.0",
                "Type-safe converter for use with Retrofit"),
        OkHttp3("https://github.com/square/okhttp",
                "com.squareup.okhttp3:okhttp:3.11.0", "3.11.0",
                "Used in conjunction with Retrofit2 for many web calls"),
        OkHttp3LoggingInterceptor("https://github.com/square/okhttp",
                "com.squareup.okhttp3:logging-interceptor:3.11.0", "3.11.0",
                "Used for logging web calls (request and response) in conjunction with Retrofit2 and OkHttp3"),
        Okio("https://github.com/square/okio",
                "com.squareup.okio:okio:1.13.0", "1.13.0",
                "Used in conjunction with OkHttp3 and Retrofit2 for easier write, read, and processing of data"),
        AndroidAnimations("https://github.com/daimajia/AndroidViewAnimations",
                "com.daimajia.androidanimations:library:2.2@aar", "2.2",
                "Android Animations used in multiple classes to simplify animations"),
        AndroidAnimationsEasing("https://github.com/daimajia/AnimationEasingFunctions",
                "com.daimajia.easing:library:2.0@aar", "2.0",
                "Part of the Android Animations dependency, this is used for animations in various classes"),
        GooglePlayServices_Maps("https://developers.google.com/android/guides/setup",
                "com.google.android.gms:play-services-maps:15.0.1", "15.0.1",
                "Google Maps API. Note! This requires the google-services.json file to be included"),
        AndroidSupport_Design("https://developer.android.com/topic/libraries/support-library/packages",
                "com.android.support:design:27.1.1", "27.1.1",
                "Android support library is utilized in any class that makes use of recyclerviews and other design-related elements"),
        AndroidSupport_Annotations("https://developer.android.com/topic/libraries/support-library/packages",
                "com.android.support:support-annotations:27.1.1", "27.1.1",
                "Android support annotations is used for custom annotations like the ones used here with the 'RequiresDependency' interface"),
        Realm("https://github.com/realm/realm-java",
                "classpath 'io.realm:realm-gradle-plugin:4.2.0'", "4.2.0",
                "Realm is a Database wrapper used to interact with the SQLite DB in the device. It is used in the DatabaseUtilities class"),
        Unknown("https://github.com/PGMacDesign/PGMacTips",
                "com.github.PGMacDesign:PGMacTips:0.0.57", "0.0.57",
                "This is the default if no known required lib has been chosen; please see com.pgmacdesign.pgmactips.misc.CustomAnnotationsBase for more details ")
        ;

        String mDependencyPackage;
        String mDependencyUrl;
        String mVersionUsed;
        String mBriefDescription;
        Dependencies(String mDependencyUrl, String mDependencyPackage,
                     String mVersionUsed, String mBriefDescription){
            this.mDependencyPackage = mDependencyPackage;
            this.mDependencyUrl = mDependencyUrl;
            this.mVersionUsed = mVersionUsed;
            this.mBriefDescription = mBriefDescription;
        }
    }

    /**
     * Custom Annotation Interface for required libraries. This will be used to help differentiate
     * which methods and classes use certain external dependencies
     */
    @Retention(RetentionPolicy.RUNTIME) //@Target(ElementType.METHOD)
    public @interface RequiresDependency {
        CustomAnnotationsBase.Dependencies requiresDependency() default Dependencies.Unknown;
        CustomAnnotationsBase.Dependencies[] requiresDependencies() default {Dependencies.Unknown};
        //Unused code below, but leaving it here for reference
//        String dependencyUrl() default "https://github.com/PGMacDesign/PGMacTips";
//        String dependencyPackage() default "Unknown";
//        String dependencyVersion() default "?.?.?";
//        String dependencyDescription() default "Missing description, please see com.pgmacdesign.pgmactips.misc.CustomAnnotationsBase for more details";
    }
}
