package com.pgmacdesign.pgmactips.progressbars;

import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.pgmacdesign.pgmactips.R;
import com.pgmacdesign.pgmactips.customui.GIFLoadingView;
import com.pgmacdesign.pgmactips.utilities.L;
import com.pgmacdesign.pgmactips.utilities.StringUtilities;

/**
 * Created by pmacdowell on 2017-07-07.
 */

public class GIFProgressBar extends Dialog {

    private static final String PASS_VALID_DATA =
            "GIF resourceId and GIF resourceURIPath were null, please make sure to pass a valid GIF";
    private GIFLoadingView gif_loading_view;

    private Context context;
    private String resourceURIPath;
    private int resourceId, viewWidth, viewHeight;// customWidthTranslate, customHeightTranslate;
    private boolean useResourceId, useResouceURIPath;

    public GIFProgressBar(Context context, String resourceURIPath, int resourceId) {
        super(context);
        this.context = context;
        this.resourceURIPath = resourceURIPath;
        this.resourceId = resourceId;
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        try {
            getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT);
        } catch (NullPointerException npe){
            npe.printStackTrace();
        }
//        this.customWidthTranslate = 0;
//        this.customHeightTranslate = 0;
    }

    public GIFProgressBar(Context context, String resourceURIPath, int resourceId,
                          int customWidthTranslate, int customHeightTranslate) {
        super(context);
        this.context = context;
        this.resourceURIPath = resourceURIPath;
        this.resourceId = resourceId;
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        try {
            getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT);
        } catch (NullPointerException npe){
            npe.printStackTrace();
        }
//        this.customWidthTranslate = customWidthTranslate;
//        this.customHeightTranslate = customHeightTranslate;
    }


    public static Dialog buildGIFDialog(Context context, String resourceURIPath, int resourceId){
        GIFProgressBar customDialog = new GIFProgressBar(context, resourceURIPath, resourceId);
        //Dialog toReturn = (Dialog) customDialog;
        return customDialog;
    }

    /*
    public static Dialog buildGIFDialog(Context context, String resourceURIPath, int resourceId,
                                        int customWidthTranslate, int customHeightTranslate){
        GIFProgressBar customDialog = new GIFProgressBar(context, resourceURIPath, resourceId,
                                                customWidthTranslate, customHeightTranslate);
        //Dialog toReturn = (Dialog) customDialog;
        return customDialog;
    }
    */
    private void init(){
        if(!StringUtilities.isNullOrEmpty(resourceURIPath)){
            this.useResouceURIPath = true;
        } else {
            this.useResouceURIPath = false;
        }

        if(resourceId != 0){
            this.useResourceId = true;
        } else {
            this.useResourceId = false;
        }

        if(!useResouceURIPath && !useResourceId){
            L.m(PASS_VALID_DATA);
            return;
        }


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gif_progress_bar);
        gif_loading_view = (GIFLoadingView) this.findViewById(R.id.gif_loading_view);
        init();
        if(useResourceId) {
            gif_loading_view.setGifImageResource(resourceId);
        } else if (useResouceURIPath){
            gif_loading_view.setGifImageUri(Uri.parse(resourceURIPath));
        } else {
            L.m(PASS_VALID_DATA);
        }
        //gif_loading_view.setCustomTranslationValues(customWidthTranslate, customHeightTranslate);
    }


}
