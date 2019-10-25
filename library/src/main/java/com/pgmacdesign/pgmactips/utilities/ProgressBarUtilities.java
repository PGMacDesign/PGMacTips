package com.pgmacdesign.pgmactips.utilities;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import androidx.annotation.NonNull;

import com.pgmacdesign.pgmactips.misc.PGMacTipsConstants;
import com.pgmacdesign.pgmactips.progressbars.GIFProgressBar;
import com.pgmacdesign.pgmactips.progressbars.PGMacCustomProgressBar;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by pmacdowell on 8/12/2016.
 */
public class ProgressBarUtilities {

    private static Dialog myDialog;
    private static GIFProgressBar myDialog2;
    private static Timer timeoutTimer;
    private static Context mContext;


    ////////////////////////
    //SVG Progress Dialog//
    ///////////////////////

    public static void showGIFProgressDialog(Context context, String imageResourceURIPath){
        if(myDialog == null){
            myDialog = GIFProgressBar.buildGIFDialog(context, imageResourceURIPath, 0);
        }
        try {
            myDialog.show();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    /*
    public static void showGIFProgressDialog(Context context, String imageResourceURIPath,
                                             int customWidthTranslate, int customHeightTranslate){
        if(myDialog == null){
            myDialog = GIFProgressBar.buildGIFDialog(context, imageResourceURIPath, 0,
                    customWidthTranslate, customHeightTranslate);
        }
        try {
            myDialog.show();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    */

    public static void showGIFProgressDialog(Context context, int imageResourceId){
        if(myDialog == null){
            myDialog = GIFProgressBar.buildGIFDialog(context, null, imageResourceId);
        }
        try {
            myDialog.show();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    /*
    public static void showGIFProgressDialog(Context context, int imageResourceId,
                                             int customWidthTranslate, int customHeightTranslate){
        if(myDialog == null){
            myDialog = GIFProgressBar.buildGIFDialog(context, null, imageResourceId,
                    customWidthTranslate, customHeightTranslate);
        }
        try {
            myDialog.show();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    */

    private static void show(){

    }
    ////////////////////////
    //SVG Progress Dialog//
    ///////////////////////

    /**
     * Shows an SVG Dialog. Overloaded for only context needed
     * @param context context
     */
    public static void showSVGProgressDialog(@NonNull Context context){
        showSVGProgressDialog(context, false, null, null, null, null, null, null);
    }

    /**
     * Shows an SVG Dialog. Overloaded for only context needed
     * @param context context
     * @param timeoutInMilliseconds timeout in milliseconds. If null, will default to 5 seconds.
     *                              If <0, indefinite
     */
    public static void showSVGProgressDialog(@NonNull Context context, Integer timeoutInMilliseconds){
        showSVGProgressDialog(context, false, timeoutInMilliseconds, null, null, null, null, null);
    }

    /**
     * Builds an SVG Progress Dialog and shows it. For more detailed param info,
     * see {@link PGMacCustomProgressBar}
     * @param context Context
     * @param dismissible Is cancellable progress bar or not
     * @param timeoutInMilliseconds timeout in milliseconds. If null, will default to 5 seconds.
     *                              If <0, indefinite
     * @param imageSizeX Size of the SVG Image (x - width)
     * @param imageSizeY Size of the SVG Image (y - height)
     * @param svgArray The array of SVG Strings. If null, it will default to the spinning circle
     * @param svgColors The array of svg colors. SIZE MUST MATCH THE SIZE OF THE ARRAY OF SVG
     *                  STRINGS!
     * @param svgTraceColors The array of svg tracing colors. SIZE MUST MATCH THE SIZE OF
     *                       THE ARRAY OF SVG STRINGS!
     */
    public static void showSVGProgressDialog(@NonNull final Context context, boolean dismissible,
                                          Integer timeoutInMilliseconds, Integer imageSizeX,
                                          Integer imageSizeY, String[] svgArray,
                                          int[] svgColors, int[] svgTraceColors) {
        mContext = context;
        try {
            if (myDialog == null) {
                //Removed on 2017-07-05 Due to problems with compiling
                //myDialog = PGMacCustomProgressBar.buildSVGDialog(context, dismissible,
                        //imageSizeX, imageSizeY, svgArray, svgColors, svgTraceColors);
                myDialog = new Dialog(context);
                myDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        L.toast(context, "Canceled");
                    }
                });
                myDialog.show();
            }else {
                myDialog.show();
            }
        } catch (Exception ex) {
            setupTimeoutTimer();
            try {
                //Removed on 2017-07-05 Due to problems with compiling
                //myDialog = PGMacCustomProgressBar.buildSVGDialog(mContext);
                myDialog = new Dialog(context);
                myDialog.show();
            } catch (Exception e2){
                e2.printStackTrace();
            }
        }
        if(timeoutInMilliseconds != null) {
            setupTimeoutTimer(timeoutInMilliseconds);
        } else {
            setupTimeoutTimer();
        }
    }

    ///////////////////////////
    //Elastic Progress Dialog//
    ///////////////////////////

    public static void showElasticDialog(final Context context, boolean dismissable,
                                         Integer timeoutInMilliseconds){
        mContext = context;
        try {
            if (myDialog == null) {
                //Removed on 2017-07-05 Due to problems with compiling
                //myDialog = PGMacCustomProgressBar.buildElasticDialog(context, dismissable);
                myDialog = new Dialog(context);
                myDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        L.toast(context, "Canceled");
                    }
                });
                myDialog.show();
            }else {
                myDialog.show();
            }
        } catch (Exception ex) {
            setupTimeoutTimer();
            try {
                myDialog = new Dialog(context);
                //Removed on 2017-07-05 Due to problems with compiling
                //myDialog = PGMacCustomProgressBar.buildSVGDialog(mContext);
                myDialog.show();
            } catch (Exception e2){
                e2.printStackTrace();
            }
        }
        if(timeoutInMilliseconds != null) {
            setupTimeoutTimer(timeoutInMilliseconds);
        } else {
            setupTimeoutTimer();
        }
    }

    public static void showElasticDialog(Context context){
        ProgressBarUtilities.showElasticDialog(context, false, null);
    }

    //////////////////////
    //Dismiss and Timers//
    //////////////////////

    /**
     * Dismiss the progress Dialog
     */
    public static void dismissProgressDialog() {
        try {
            if (myDialog != null && myDialog.isShowing()) {
                myDialog.dismiss();
                myDialog = null;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Dismiss the progress dialog with a toast
     * @param message The message String to toast
     */
    public static void dismissProgressDialog(String message) {
        try {
            dismissProgressDialog();
            L.toast(mContext, message);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Setup a timeout timer to auto-dismiss the progress dialog after X seconds
     */
    private static void setupTimeoutTimer(){
        setupTimeoutTimer((PGMacTipsConstants.ONE_SECOND * 5));
    }

    /**
     * Overloaded method for setting up timeout to include custom long param
     * @param millisecondsToEnd Long milliseconds timeout (5,000 would be 5 seconds)
     */
    private static void setupTimeoutTimer(long millisecondsToEnd){
        if(timeoutTimer == null){
            timeoutTimer = new Timer();
        }

        timeoutTimer.cancel();
        timeoutTimer = new Timer();
        if(millisecondsToEnd < 0){
            return;
        }
        timeoutTimer.schedule(new TimerTask() {
            @Override
            public void run() {

                try {
                    dismissProgressDialog();
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }, ((int)(millisecondsToEnd)));
    }

    /**
     * Quick method for determining whether or not the dialog is showing. Useful for onStop calls
     * @return boolean, true if it is showing, false if it is not or is null.
     */
    public static boolean isDialogShowing(){
        if(myDialog == null){
            return false;
        }
        return myDialog.isShowing();
    }

}