package com.pgmacdesign.pgmacutilities.progressbars;

import android.app.ProgressDialog;
import android.content.Context;

/**
 * Created by pmacdowell on 8/12/2016.
 */
public class PGMacCustomProgressBar extends ProgressDialog {


    /*
    Getting errors with uploads. Removing entirely for now
     */



    public PGMacCustomProgressBar(Context context) {
        super(context);
    }


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
    ----/
    public static Dialog buildSVGDialog(Context context, boolean cancelable,
                                                Integer imageSizeX, Integer imageSizeY,
                                                String[] svgArray,
                                                int[] svgColors, int[] svgTraceColors){
        // TODO: 8/12/2016 checks on nulls
        Dialog customAlertDialog = new PGMacCustomProgressBar(context, SVG_DIALOG);

        //customAlertDialog.setIndeterminate(true);
        customAlertDialog.setCancelable(cancelable);
        //customAlertDialog.setInverseBackgroundForced(true);
        Window window = customAlertDialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(lp);
        window.setBackgroundDrawable(new ColorDrawable(
                ContextCompat.getColor(context, R.color.Transparent)
        ));


        return customAlertDialog;
    }


    private boolean stopSVG = false;
    private Timer timer;
    private com.jrummyapps.android.widget.AnimatedSvgView animated_svg_view;
    private is.arontibo.library.ElasticDownloadView elastic_download_view;

    private DisplayManagerUtilities dmu;
    public static final int SVG_DIALOG = 0;
    public static final int ELASTIC_DIALOG = 1;
    public static final int CALIFORNIA_SVG_DIALOG = 2;
    private int whichSelected;
    private Context localContext;

    /**
     * Overloaded method, excludes boolean in case it is not added
     * @param context
     * @return
     ----/
    public static Dialog buildSVGDialog(Context context){
        return (PGMacCustomProgressBar.buildSVGDialog(context, false, 40, 40, null, null, null));
    }

    public static Dialog buildCaliforniaSVGDialog(Context context, boolean cancelable){
        Dialog customAlertDialog = new PGMacCustomProgressBar(context, CALIFORNIA_SVG_DIALOG);

        //customAlertDialog.setIndeterminate(true);
        customAlertDialog.setCancelable(cancelable);
        //customAlertDialog.setInverseBackgroundForced(true);
        Window window = customAlertDialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(lp);
        window.setBackgroundDrawable(new ColorDrawable(
                ContextCompat.getColor(context, R.color.Transparent)
        ));


        return customAlertDialog;
    }

    /**
     * Builder for this class
     * @param context Context
     * @param cancelable Cancelable or not; defaults to false
     * @return
     ----/
    public static ProgressDialog buildElasticDialog(Context context, boolean cancelable){
        PGMacCustomProgressBar customAlertDialog = new PGMacCustomProgressBar(
                context, ELASTIC_DIALOG);

        customAlertDialog.setCancelable(cancelable);
        Window window = customAlertDialog.getWindow();
        int transparent = ContextCompat.getColor(context, R.color.Transparent);
        ColorDrawable colorDrawable = new ColorDrawable(transparent);
        window.setBackgroundDrawable(colorDrawable);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(lp);

        return customAlertDialog;
    }


    /**
     * Overloaded method, excludes boolean in case it is not added
     * @param context
     * @return
    ----/
    public static ProgressDialog buildElasticDialog(Context context){
        return (buildElasticDialog(context, false));
    }

    /**
     * Alert dialog constructor
     * @param context
    ----/
    private PGMacCustomProgressBar(Context context) {
        super(context);
        //NOTE! If not transparent, use this: super(context, R.style.CustomTransparentDialog);
        this.localContext = context;
        whichSelected = 0;
        this.dmu = new DisplayManagerUtilities(context);
    }

    /**
     * Overloaded for secondary alert popup
     * @param context
     * @param whichSelected
    ----/
    private PGMacCustomProgressBar(Context context, int whichSelected) {
        super(context);
        //NOTE! If not transparent, use this: super(context, R.style.CustomTransparentDialog);
        this.localContext = context;
        this.whichSelected = whichSelected;
        this.dmu = new DisplayManagerUtilities(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        switch (whichSelected){

            case SVG_DIALOG:
                setContentView(R.layout.custom_alert_dialog_svg);
                animated_svg_view = (AnimatedSvgView) this.findViewById(
                        R.id.animated_svg_view);
                /*
                ViewGroup.LayoutParams params = animated_svg_view.getLayoutParams();
                int screenWidth = dmu.getPixelsWidth();
                int screenHeight = dmu.getPixelsHeight();
                int min = screenWidth;
                if(screenHeight < screenWidth){
                    min = screenHeight;
                }
                min = (int)(min * 0.25);
                animated_svg_view.setViewportSize(animated_svg_view.getWidth(),
                        animated_svg_view.getHeight());
    ----/
                break;

            case ELASTIC_DIALOG:
                setContentView(R.layout.custom_alert_dialog_elastic);
                elastic_download_view = (ElasticDownloadView) this.findViewById(
                        R.id.elastic_download_view);

                break;

            case CALIFORNIA_SVG_DIALOG:
                setContentView(R.layout.california_svg_view);
                animated_svg_view = (AnimatedSvgView) this.findViewById(
                        R.id.animated_svg_view);

        }
    }


    public void setSVGDetails(int imageSizeX, int imageSizeY, String[] svgArray,
                              int[] svgColors, int[] svgTraceColors){


        animated_svg_view.setViewportSize(imageSizeX, imageSizeY);
        animated_svg_view.setGlyphStrings(svgArray);
        animated_svg_view.setFillColors(svgColors);
        animated_svg_view.setTraceColors(svgTraceColors);

    }


    @Override
    public void show() {
        super.show();

        switch (whichSelected){
            case CALIFORNIA_SVG_DIALOG:
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
    ----/
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

    */

}
