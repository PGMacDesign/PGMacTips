package com.pgmacdesign.pgmactips.utilities;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.pgmacdesign.pgmactips.misc.PGMacTipsConfig;

/**
 * Simplistic class. Used primarily for logs && toasts
 * Created by pmacdowell on 8/12/2016.
 */
public class L {


    private static final String TAG = "PGMacTips";


    /**
     * Quick println
     * @param myObject The string to print (or double, int, whatever)
     * @param <E> Extends object
     */
    public static <E> void m (E myObject){
        String str = myObject + "";
        if(StringUtilities.isNullOrEmpty(str)){
            return;
        }
        if (isLiveBuild()) {
            return;
        }
        if (str.length() > 4000) {
            Log.v(getTag(), "sb.length = " + str.length());
            int chunkCount = str.length() / 4000;     // integer division
            for (int i = 0; i <= chunkCount; i++) {
                int max = 4000 * (i + 1);
                if (max >= str.length()) {
                    Log.d(getTag(), "chunk " + i + " of " + chunkCount + ":" + str.substring(4000 * i));
                } else {
                    Log.d(getTag(), "chunk " + i + " of " + chunkCount + ":" + str.substring(4000 * i, max));
                }
            }
        } else {
            Log.d(getTag(), str);
        }
    }

    /**
     * Quick println for the line number
     * @param x int, line number
     */
    public static void l(int x){
        if (isLiveBuild()) {
            return;
        }
        Log.d(getTag(), "Line Number " + x + " hit");
    }

    /**
     * Quick println for the line number
     * @param x int, line number
     */
    public static void l(@NonNull Context context, int x){
        if (isLiveBuild()) {
            return;
        }
        if(context == null){
            context = getContext();
        }
        String activityName = null;
        try {
            activityName = context.getClass().getSimpleName();
        } catch (Exception e){}
        if(activityName == null){
            activityName = "Unknown";
        }
        Log.d(getTag(), "Activity: " + activityName + ", " + "Line Number " + x + " hit");
    }


    /**
     * Short toast
     * @param context context
     * @param myObject String to print (If OTHER things are passed in, it converts it to a String first)
     */
    public static <E> void toast(@NonNull Context context, E myObject){
        if(context == null){
            context = getContext();
        }
        String str = myObject + ""; //Cast it to a String
        Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
    }

    /**
     * Long toast
     * @param context context
     * @param myObject String to print (If OTHER things are passed in, it converts it to a String first)
     */
    public static <E> void Toast(@NonNull Context context, E myObject){
        if(context == null){
            context = getContext();
        }
        String str = myObject + ""; //Cast it to a String
        Toast.makeText(context, str, Toast.LENGTH_LONG).show();
    }

    /**
     * Long toast. Overloaded to include option to alter length
     * @param context context
     * @param myObject String to print (If OTHER things are passed in, it converts it to a String first)
     */
    public static <E> void Toast(@NonNull Context context, E myObject, int length){
        if(context == null){
            context = getContext();
        }
        String str = myObject + ""; //Cast it to a String
        Toast.makeText(context, str, length).show();
    }

    private static boolean isLiveBuild(){
        try {
            return PGMacTipsConfig.getInstance().getIsLiveBuild();
        } catch (Exception e){
            //If this triggers, it means the context init has not run yet
            return false;
        }
    }

    private static String getTag(){
        try {
            String str = PGMacTipsConfig.getInstance().getTagForLogging();
            if(!StringUtilities.isNullOrEmpty(str)){
                if(str.length() > 23){
                    str = str.substring((str.length() - (1 + 23)), (str.length() - 1));
                }
            }
            return (!StringUtilities.isNullOrEmpty(str)) ? str : TAG;
        } catch (Exception e){
            //If this triggers, it means the context init has not run yet (OR) sizing issue
        }
        return TAG;
    }

    private static Context getContext(){
        try {
            return PGMacTipsConfig.getInstance().getContext();
        } catch (Exception e){
            //If this triggers, it means the context init has not run yet
        }
        return null;
    }
}
