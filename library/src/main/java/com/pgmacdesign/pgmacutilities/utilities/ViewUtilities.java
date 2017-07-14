package com.pgmacdesign.pgmacutilities.utilities;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.pgmacdesign.pgmacutilities.adaptersandlisteners.OnTaskCompleteListener;
import com.pgmacdesign.pgmacutilities.misc.PGMacUtilitiesConstants;

/**
 * Class for managing views in various ways
 * Created by pmacdowell on 2017-07-10.
 */
public class ViewUtilities {

    public static final int VIEW_PARAMS_LOADED =
            PGMacUtilitiesConstants.VIEW_PARAMS_LOADED;
    public static final int VIEW_PARAMS_LOADING_FAILED =
            PGMacUtilitiesConstants.VIEW_PARAMS_LOADING_FAILED;

    public static PGMViewObject scaleGIFTo(float gifWidth, float gifHeight,
                                           float maxAvailableWidth, float maxAvailableHeight,
                                           Boolean maintainRatio){
        //Check incorrect data first
        if(gifHeight == 0 && gifWidth == 0){
            return null;
        }
        if(maxAvailableWidth == 0 && maxAvailableHeight == 0){
            return null;
        }
        //Defaults
        if(maintainRatio == null){
            maintainRatio = false;
        }

        float currentGifRatio = (float)(gifWidth / gifHeight);
        float layoutParamsRatio = (float)(maxAvailableWidth / maxAvailableHeight);
        if(currentGifRatio < 0){
            //Something went wrong
            return null;
        }

        PGMViewObject obj = new PGMViewObject();
        obj.maintainedRatio = maintainRatio;
        if(maintainRatio){
            float multiplier, newWidth, newHeight, widthTranslatePixels, heightTranslatePixels;
            L.m("Current gif ratio == " + currentGifRatio);
            L.m("View ratio == " + layoutParamsRatio);
            if(layoutParamsRatio < 1){
                //View is Portrait mode; height > width ; width is lowest #
                if(currentGifRatio < 1){
                    //Image is Portrait mode; height > width ; width is lowest #
                    multiplier = maxAvailableWidth / gifWidth;
                } else {
                    //Image is Landscape mode; width > height ; height is lowest #
                    multiplier = maxAvailableWidth / gifHeight;
                }
                newWidth = gifWidth * multiplier;
                newHeight = gifHeight * multiplier;
                // TODO: 2017-07-10 something is inherently wrong with Y (width) translation. Fix it!
                widthTranslatePixels = (((((float)maxAvailableWidth) - ((float)newWidth)) / 2F) / ((float)multiplier));
                heightTranslatePixels = (((((float)maxAvailableHeight) - ((float)newHeight)) / 2F) / ((float)multiplier));
            } else {
                //View is Landscape mode; width > height; height is lowest #
                if(currentGifRatio < 1){
                    //Image is Portrait mode; height > width ; width is lowest #
                    multiplier = maxAvailableHeight / gifWidth;
                } else {
                    //Image is Landscape mode; width > height ; height is lowest #
                    multiplier = maxAvailableHeight / gifHeight;
                }
                newWidth = gifWidth * multiplier;
                newHeight = gifHeight * multiplier;
                // TODO: 2017-07-10 something is inherently wrong with Y (width) translation. Fix it!
                widthTranslatePixels = (((((float)maxAvailableWidth) - ((float)newWidth)) / 2F) / ((float)multiplier));
                heightTranslatePixels = (((((float)maxAvailableHeight) - ((float)newHeight)) / 2F) / ((float)multiplier));
            }
            //Adjust Padding
            obj.newHeight = newHeight;//(newHeight - (2 * paddingInPixels));
            obj.newWidth = newWidth;//(newWidth - (2 * paddingInPixels));
            obj.widthScaleMultiplier = multiplier;
            obj.heightScaleMultiplier = multiplier;
            obj.widthTranslatePixels = widthTranslatePixels;
            obj.heightTranslatePixels = heightTranslatePixels;
        } else {

            obj.newHeight = maxAvailableHeight;//(maxAvailableHeight - (2 * paddingInPixels));
            obj.newWidth = maxAvailableWidth;//(maxAvailableWidth - (2 * paddingInPixels));
            obj.widthScaleMultiplier = maxAvailableWidth / gifWidth;
            obj.heightScaleMultiplier = maxAvailableHeight / gifHeight;
            obj.widthTranslatePixels = 0;
            obj.heightTranslatePixels = 0;
        }
        return obj;
    }

    /**
     * Class to load the view height and width after it has loaded. Follow this thread for info:
     * https://stackoverflow.com/questions/9575706/android-get-height-of-a-view-before-it%C2%B4s-drawn
     * @param view
     * @param listener
     */
    public static void getViewParamsAfterLoading(final View view,
                                                 final OnTaskCompleteListener listener){
        if(view == null){
            return;
        }
        if(listener == null){
            return;
        }
        try {
            view.getViewTreeObserver().addOnGlobalLayoutListener(
                    new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            int width = view.getWidth();
                            int height = view.getHeight();
                            PGMViewObject o = new PGMViewObject();
                            o.newWidth = width;
                            o.newHeight = height;
                            view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                            listener.onTaskComplete(o, VIEW_PARAMS_LOADED);
                        }
                    }
            );
        } catch (Exception e){
            e.printStackTrace();
            listener.onTaskComplete(null, VIEW_PARAMS_LOADING_FAILED);
        }
    }

    public static class PGMViewObject {
        private float newWidth;
        private float newHeight;
        private boolean maintainedRatio;
        private float widthScaleMultiplier;
        private float heightScaleMultiplier;
        private float widthTranslatePixels;
        private float heightTranslatePixels;

        public float getWidthTranslatePixels() {
            return widthTranslatePixels;
        }

        public float getHeightTranslatePixels() {
            return heightTranslatePixels;
        }

        public float getWidthScaleMultiplier() {
            return widthScaleMultiplier;
        }

        public float getHeightScaleMultiplier() {
            return heightScaleMultiplier;
        }

        public float getNewWidth() {
            return newWidth;
        }

        public float getNewHeight() {
            return newHeight;
        }

        public boolean didMaintainRatio() {
            return maintainedRatio;
        }
    }

    public static void setViewMargins (View view, int left, int top, int right, int bottom) {
        if(view == null){
            return;
        }
        try {
            if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
                ViewGroup.MarginLayoutParams p =
                        (ViewGroup.MarginLayoutParams) view.getLayoutParams();
                p.setMargins(left, top, right, bottom);
                view.requestLayout();
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

}
