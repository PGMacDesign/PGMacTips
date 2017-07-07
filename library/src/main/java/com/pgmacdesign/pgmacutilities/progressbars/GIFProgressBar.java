package com.pgmacdesign.pgmacutilities.progressbars;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.pgmacdesign.pgmacutilities.R;
import com.pgmacdesign.pgmacutilities.customui.GIFLoadingView;
import com.pgmacdesign.pgmacutilities.utilities.L;
import com.pgmacdesign.pgmacutilities.utilities.StringUtilities;

/**
 * Created by pmacdowell on 2017-07-07.
 */

public class GIFProgressBar extends ProgressDialog {

    private GIFLoadingView gif_loading_view;

    private Context context;
    private String resourceURIPath;
    private int resourceId;
    private boolean useResourceId, useResouceURIPath;

    public GIFProgressBar(Context context) {
        super(context);
        this.context = context;
    }

    public GIFProgressBar(Context context, int theme) {
        super(context, theme);
        this.context = context;
    }

    private void setGifDetails(int resourceId, String resourceURIPath){
        this.resourceId = resourceId;
        this.resourceURIPath = resourceURIPath;
        init();
    }

    public static Dialog buildGIFDialog(Context context, @NonNull String resourceURIPath){
        L.m("static build gif dialog -- " + 45);
        GIFProgressBar customDialog = new GIFProgressBar(context);
        L.m("static build gif dialog -- " + 47);
        customDialog.setGifDetails(0, resourceURIPath);
        L.m("static build gif dialog -- " + 49);
        Dialog toReturn = (Dialog) customDialog;
        L.m("static build gif dialog -- " + 51);
        return toReturn;
    }

    public static Dialog buildGIFDialog(Context context, int resourceId){
        L.m("static build gif dialog -- " + 56);
        GIFProgressBar customDialog = new GIFProgressBar(context);
        L.m("static build gif dialog -- " + 58);
        customDialog.setGifDetails(resourceId, null);
        L.m("static build gif dialog -- " + 60);
        Dialog toReturn = (Dialog) customDialog;
        L.m("static build gif dialog -- " + 62);
        return toReturn;
    }


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
            L.m("GIF resourceId and GIF resourceURIPath were null, please make sure to pass a valid GIF");
            return;
        }


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gif_progress_bar);
        gif_loading_view = (GIFLoadingView) this.findViewById(R.id.gif_loading_view);

        if(useResourceId) {
            gif_loading_view.setGifImageResource(resourceId);
        } else if (useResouceURIPath){
            gif_loading_view.setGifImageUri(Uri.parse(resourceURIPath));
        }
    }

}
