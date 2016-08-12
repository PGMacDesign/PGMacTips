package com.pgmacdesign.pgmacutilities;

import android.os.Build;

/**
 * Created by pmacdowell on 8/12/2016.
 */
public class SystemUtilities {

    //These are all checks for determining the build version.

    /**
     * API Level 23 or higher
     * @return
     */
    public static boolean userHasMarshmallowOrHigher(){
        if(Build.VERSION.SDK_INT >= 23){
            return true;
        } else {
            return false;
        }
    }

    /**
     * API Level 21 or higher
     * @return
     */
    public static boolean userHasLollipopOrHigher(){
        if(Build.VERSION.SDK_INT >= 21){
            return true;
        } else {
            return false;
        }
    }

    /**
     * API Level 19 or higher
     * @return
     */
    public static boolean userHasKitKatOrHigher(){
        if(Build.VERSION.SDK_INT >= 19){
            return true;
        } else {
            return false;
        }
    }

    /**
     * API Level 16 or higher
     * @return
     */
    public static boolean userHasJellyBeanOrHigher(){
        if(Build.VERSION.SDK_INT >= 16){
            return true;
        } else {
            return false;
        }
    }

    /**
     * API Level 14 or higher
     * @return
     */
    public static boolean userHasICSOrHigher(){
        if(Build.VERSION.SDK_INT >= 14){
            return true;
        } else {
            return false;
        }
    }

}
