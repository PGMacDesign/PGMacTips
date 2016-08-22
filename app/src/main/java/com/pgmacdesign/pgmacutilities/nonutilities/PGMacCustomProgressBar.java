package com.pgmacdesign.pgmacutilities.nonutilities;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.Window;
import android.view.WindowManager;

import com.jrummyapps.android.widget.AnimatedSvgView;
import com.pgmacdesign.pgmacutilities.utilities.ColorUtilities;
import com.pgmacdesign.pgmacutilities.utilities.L;
import com.pgmacdesign.pgmacutilities.R;

import java.util.Timer;
import java.util.TimerTask;

import is.arontibo.library.ElasticDownloadView;

/**
 * Created by pmacdowell on 8/12/2016.
 */
public class PGMacCustomProgressBar extends ProgressDialog {

    //Defaults in case they pass in incorrect info
    private final String[] defaultLoader = {
            "M43.935,25.145c0-10.318-8.364-18.683-18.683-18.683c-10.318,0-18.683,8.365-18.683," +
                    "18.683h4.068c0-8.071,6.543-14.615,14.615-14.615c8.072,0,14.615,6.543,14." +
                    "615,14.615H43.935z"
    };
    private final int[] defaultColors = {
            ColorUtilities.parseMyColor(PGMacUtilitiesConstants.COLOR_BLUE)
    };
    private final int[] defaultTraceColors = {
            ColorUtilities.parseMyColor(PGMacUtilitiesConstants.COLOR_NAVY_BLUE)
    };

    private boolean stopSVG = false;
    private Timer timer;
    private com.jrummyapps.android.widget.AnimatedSvgView animated_svg_view;
    private is.arontibo.library.ElasticDownloadView elastic_download_view;

    private static final int SVG_DIALOG = 0;
    private static final int ELASTIC_DIALOG = 1;
    private int whichSelected;

    /**
     * Builder for the Custom Progress bar
     * @param context Context
     * @param cancelable Is cancellable progress bar or not
     * @param imageSizeX Size of the SVG Image (x - width)
     * @param imageSizeY Size of the SVG Image (y - height)
     * @param svgArray The array of SVG Strings. If null, it will default to the spinning circle
     * @param svgColors The array of svg colors. SIZE MUST MATCH THE SIZE OF THE ARRAY OF SVG
     *                  STRINGS!
     * @param svgTraceColors The array of svg tracing colors. SIZE MUST MATCH THE SIZE OF
     *                       THE ARRAY OF SVG STRINGS!
     * @return ProgressDialog
     * todo come back and refactor this in
     */
    public static ProgressDialog buildSVGDialog(Context context, boolean cancelable,
                                                Integer imageSizeX, Integer imageSizeY,
                                                String[] svgArray,
                                                int[] svgColors, int[] svgTraceColors){
        // TODO: 8/12/2016 checks on nulls
        PGMacCustomProgressBar customAlertDialog = new PGMacCustomProgressBar(context, SVG_DIALOG);
        if(customAlertDialog == null){
            L.m("custom alert dialog is null");
        }
        //customAlertDialog.setSVGDetails(imageSizeX, imageSizeY, svgArray, svgColors, svgTraceColors);
        customAlertDialog.setIndeterminate(true);
        customAlertDialog.setCancelable(cancelable);
        Window window = customAlertDialog.getWindow();
        window.setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(context,
                R.color.Transparent)));
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(lp);

        return customAlertDialog;
    }

    /**
     * Overloaded method, excludes boolean in case it is not added
     * @param context
     * @return
     */
    public static ProgressDialog buildSVGDialog(Context context){
        return (PGMacCustomProgressBar.buildSVGDialog(context, false, 40, 40, null, null, null));
    }

    /**
     * Builder for this class
     * @param context Context
     * @param cancelable Cancelable or not; defaults to false
     * @return
     */
    public static ProgressDialog buildElasticDialog(Context context, boolean cancelable){
        PGMacCustomProgressBar customAlertDialog = new PGMacCustomProgressBar(context, ELASTIC_DIALOG);

        customAlertDialog.setCancelable(cancelable);
        Window window = customAlertDialog.getWindow();
        window.setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(context,
                R.color.Transparent)));
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(lp);

        return customAlertDialog;
    }
    /**
     * Overloaded method, excludes boolean in case it is not added
     * @param context
     * @return
     */
    public static ProgressDialog buildElasticDialog(Context context){
        return (buildElasticDialog(context, false));
    }

    /**
     * Alert dialog constructor
     * @param context
     */
    public PGMacCustomProgressBar(Context context) {
        super(context);
        whichSelected = 0;
    }

    /**
     * Overloaded for secondary alert popup
     * @param context
     * @param whichSelected
     */
    public PGMacCustomProgressBar(Context context, int whichSelected) {
        super(context);
        this.whichSelected = whichSelected;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        switch (whichSelected){

            case SVG_DIALOG:
                setContentView(R.layout.custom_alert_dialog_svg);
                animated_svg_view = (AnimatedSvgView) this.findViewById(R.id.animated_svg_view);
                break;

            case ELASTIC_DIALOG:
                setContentView(R.layout.custom_alert_dialog_elastic);
                elastic_download_view = (ElasticDownloadView) this.findViewById(R.id.elastic_download_view);

                break;
        }
    }

    /*
    public void setSVGDetails(int imageSizeX, int imageSizeY, String[] svgArray,
                              int[] svgColors, int[] svgTraceColors){
        animated_svg_view.setViewportSize(imageSizeX, imageSizeY);
        animated_svg_view.setGlyphStrings(svgArray);
        animated_svg_view.setFillColors(svgColors);
        animated_svg_view.setTraceColors(svgTraceColors);
    }
    */

    @Override
    public void show() {
        super.show();

        switch (whichSelected){
            case SVG_DIALOG:
                stopSVG = false;
                startSVGAnimation();
                break;

            case ELASTIC_DIALOG:
                startElasticAnimation();
                break;
        }

    }

    @Override
    public void dismiss() {
        super.dismiss();
        try {
            stopSVG = true;
            timer.cancel();
            animated_svg_view.reset();
        } catch (Exception e){}
    }

    private void startSVGAnimation(){
        animated_svg_view.start();
        if(timer == null){
            timer = new Timer();
        }
        if(!stopSVG) {
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    animated_svg_view.reset();
                    startSVGAnimation();
                }
            }, ((long) (PGMacUtilitiesConstants.ONE_SECOND * 2.1)));
        }
    }

    private void startElasticAnimation(){
        elastic_download_view.startIntro();
    }

    @Override
    public void setProgress(int value) {
        if(value == -1){
            elastic_download_view.fail();
            return;
        }
        elastic_download_view.setProgress(value);
        //super.setProgress(value);
        if(value == 100){
            elastic_download_view.success();
        }
    }

    /**
     * Set the progress. Send either float or int
     * @param progress Progress of download
     */
    public void setElasticProgress(float progress){

    }
    public void setElasticProgress(int progress){
        elastic_download_view.setProgress(progress);
    }

    @Override
    protected void onStop() {
        try {
            stopSVG = true;
            timer.cancel();
            animated_svg_view.reset();
        } catch (Exception e){}
        super.onStop();
    }

}
