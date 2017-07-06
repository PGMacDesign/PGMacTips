package com.pgmacdesign.pgmacutilities.utilities;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.pgmacdesign.pgmacutilities.misc.PGMacUtilitiesConstants;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

/**
 * Simplistic class. Used primarily for logs, toasts, and OTHER tedious things
 * Created by pmacdowell on 8/12/2016.
 */
public class L {

    private static final String TICK_TIME_PART_1 = "Tick Time: \nActivity == ";
    private static final String TICK_TIME_PART_2 = "\nHitting Line Number == ";
    private static final String TICK_TIME_PART_3 = "\nTime taken up until this point == ";
    private static final String TAG = "PGMacUtilities";

    private String screenName;
    private long timeAtInit;
    /**
     * Constructor is used for time measurement method
     * @param screenName Screen name to use in print statements
     * @param timeAtInit time (in milliseconds) of init start. use Date().getTime() for long.
     */
    public L (String screenName, Long timeAtInit){
        if(StringUtilities.isNullOrEmpty(screenName)){
            this.screenName = "N/A";
        } else {
            this.screenName = screenName;
        }
        if(timeAtInit == null){
            this.timeAtInit = new Date().getTime();
        } else {
            this.timeAtInit = timeAtInit;
        }
    }

    public void tick(Integer lineNumber){
        String lineStr;
        if(lineNumber == null){
            lineStr = "";
        } else {
            lineStr = "" + lineNumber;
        }
        long now = new Date().getTime();
        L.m(    TICK_TIME_PART_1 + screenName
                + TICK_TIME_PART_2 + lineStr
                + TICK_TIME_PART_3 + (now - this.timeAtInit)
        );

    }

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
        if (str.length() > 4000) {
            Log.v(TAG, "sb.length = " + str.length());
            int chunkCount = str.length() / 4000;     // integer division
            for (int i = 0; i <= chunkCount; i++) {
                int max = 4000 * (i + 1);
                if (max >= str.length()) {
                    Log.d(TAG, "chunk " + i + " of " + chunkCount + ":" + str.substring(4000 * i));
                } else {
                    Log.d(TAG, "chunk " + i + " of " + chunkCount + ":" + str.substring(4000 * i, max));
                }
            }
        } else {
            Log.d(TAG, str);
        }
    }
    /**
     * Quick println in the logcat and write it to the file under downloads
     * @param myObject The string to print (or double, int, whatever)
     * @param <E> Extends object
     */
    public static <E> void logAndWrite (E myObject){
        L.m(myObject);
        writeToOutput(myObject);
    }
    /**
     * Quick println for the line number
     * @param x int, line number
     */
    public static void l(int x){
        Log.d(TAG, "Line Number " + x + " hit");
    }

    /**
     * Quick println for the line number
     * @param x int, line number
     */
    public static void l(Context context, int x){
        String activityName = null;
        try {
            activityName = context.getClass().getSimpleName();
        } catch (Exception e){}
        if(activityName == null){
            activityName = "Unknown";
        }
        Log.d(TAG, "Activity: " + activityName + ", " + "Line Number " + x + " hit");
    }

    /**
     * Write data to a text file with data. The name of the file is debugLoggingData and its
     * location is under the downloads section of the phone's memory (/storage/emulated/0/Download/).
     * Maxes out at 5mb (which is enormous for a text file)
     * @param myObject String to print
     * @param <E> Extends Object
     */
    public static <E> void writeToOutput(E myObject){
        try {
            FileWriter fw;
            //FileOutputStream outputStream;
            String ss = myObject + "";
            //File file = new File(MyApplication.getAppContext().getExternalFilesDir(Environment.
            //DIRECTORY_DOWNLOADS), "loggingData.txt");
            File file = new File(PGMacUtilitiesConstants.PHONE_URI_TO_WRITE_TO, PGMacUtilitiesConstants.FILE_NAME);
            if(file == null){
                file = new File(StringUtilities.getDataDirectoryLocation(), PGMacUtilitiesConstants.FILE_NAME);
            }
            if(file == null){
                return;
            }
            long fileSize = file.length();
            double megabytes = FileUtilities.convertSize(fileSize, FileUtilities.ByteSizeNames.Bytes, FileUtilities.ByteSizeNames.Megabytes);
            if(megabytes > 5){
                //File is too long, erase and start over
                fw = new FileWriter(file, false);
            } else {
                //File is not too long, append
                fw = new FileWriter(file, true);
            }

            fw.write(ss + "\n");
            fw.flush();
            fw.close();

        } catch (IOException e){
            L.m("error writing to file in PGMacUtilities");
        } catch (Exception e){}

    }
    /**
     * Short toast
     * @param context context
     * @param myObject String to print (If OTHER things are passed in, it converts it to a String first)
     */
    public static <E> void toast(Context context, E myObject){
        String str = myObject + ""; //Cast it to a String
        Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
    }

    /**
     * Long toast
     * @param context context
     * @param myObject String to print (If OTHER things are passed in, it converts it to a String first)
     */
    public static <E> void Toast(Context context, E myObject){
        String str = myObject + ""; //Cast it to a String
        Toast.makeText(context, str, Toast.LENGTH_LONG).show();
    }

    /**
     * Long toast. Overloaded to include option to alter length
     * @param context context
     * @param myObject String to print (If OTHER things are passed in, it converts it to a String first)
     */
    public static <E> void Toast(Context context, E myObject, int length){
        String str = myObject + ""; //Cast it to a String
        Toast.makeText(context, str, length).show();
    }

}
