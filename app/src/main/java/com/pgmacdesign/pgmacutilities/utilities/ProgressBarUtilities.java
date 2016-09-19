package com.pgmacdesign.pgmacutilities.utilities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;

import com.pgmacdesign.pgmacutilities.nonutilities.PGMacCustomProgressBar;
import com.pgmacdesign.pgmacutilities.nonutilities.PGMacUtilitiesConstants;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by pmacdowell on 8/12/2016.
 */
public class ProgressBarUtilities {

    private static ProgressDialog progressDialog;
    private static Timer timeoutTimer;
    private static Context mContext;

    ////////////////////////////////////////////////////////////////////////////////////////////////
    /////SVG Progress Dialog////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

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
            if (progressDialog == null) {
                progressDialog = PGMacCustomProgressBar.buildSVGDialog(context, dismissible,
                        imageSizeX, imageSizeY, svgArray, svgColors, svgTraceColors);
                progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        L.toast(context, "Canceled");
                    }
                });
                progressDialog.show();
            }else {
                progressDialog.show();
            }
        } catch (Exception ex) {
            setupTimeoutTimer();
            try {
                progressDialog = PGMacCustomProgressBar.buildSVGDialog(mContext);
                progressDialog.show();
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

    ////////////////////////////////////////////////////////////////////////////////////////////////
    /////Elastic Progress Dialog////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static void showElasticDialog(final Context context, boolean dismissable,
                                         Integer timeoutInMilliseconds){
        mContext = context;
        try {
            if (progressDialog == null) {
                progressDialog = PGMacCustomProgressBar.buildElasticDialog(context, dismissable);
                progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        L.toast(context, "Canceled");
                    }
                });
                progressDialog.show();
            }else {
                progressDialog.show();
            }
        } catch (Exception ex) {
            setupTimeoutTimer();
            try {
                progressDialog = PGMacCustomProgressBar.buildSVGDialog(mContext);
                progressDialog.show();
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

    ////////////////////////////////////////////////////////////////////////////////////////////////
    /////Dismiss and Timers/////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Dismiss the progress Dialog
     */
    public static void dismissProgressDialog() {
        try {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
                progressDialog = null;
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
        setupTimeoutTimer((PGMacUtilitiesConstants.ONE_SECOND * 5));
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
}