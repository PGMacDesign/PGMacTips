package com.pgmacdesign.pgmacutilities.utilities;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.pgmacdesign.pgmacutilities.R;

/**
 * This class helps to determine the screen height and width in Density Pixels (DP)
 * Created by pmacdowell on 8/15/2016.
 */
public class DisplayManagerUtilities {

    //Display display;
    private DisplayMetrics outMetrics;
    private WindowManager windowManager;
    private float density;
    private float dpWidth;
    private float dpHeight;
    private int pixelsWidth;
    private int pixelsHeight;

    /**
     * This will get the width and height of the screen in DP. This constructor is used to define
     * the instance variables and set them while the other methods are used as accessor/ getters.
     * @param context Context from activity would be 'this' and from fragment would be 'getActivity()'
     */
    public DisplayManagerUtilities(Context context){
        outMetrics = new DisplayMetrics();
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(outMetrics);
        density  = (int)(outMetrics.density * 160f);
        pixelsHeight = outMetrics.heightPixels;
        pixelsWidth = outMetrics.widthPixels;
        dpWidth  = pixelsWidth / density;
        dpHeight = pixelsHeight / density;
    }

    /**
     * Gets the width of the screen in DP
     * @return returns a float
     */
    public float getScreenWidth(){
        return dpWidth;
    }

    /**
     * Gets the height of the screen in DP
     * @return returns a float
     */
    public float getScreenHeight(){
        return dpHeight;
    }

    /**
     * Returns the width of the screen in pixels
     * @return int
     */
    public int getPixelsWidth(){
        return pixelsWidth;
    }

    /**
     * Returns the height of the screen in pixels
     * @return int
     */
    public int getPixelsHeight(){
        return pixelsHeight;
    }

    public float getDensity(){
        return density;
    }

    /**
     * This returns the size of the navigation bar height
     * @param context Context needed
     * @return an int (in pixels);
     */
    public static int getNavigationBarSize(Context context){
        float x = context.getResources().getDimension(R.dimen.abc_action_bar_default_height_material);
        return (int) x;
    }

    /**
     * This method converts device specific pixels to density independent pixels.
     *
     * @param px A value in px (pixels) unit. Which we need to convert into db
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent dp equivalent to px value
     */
    public static float convertPixelsToDp(float px, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / (metrics.densityDpi / 160f);
        return dp;
    }

    /**
     * This method converts dp unit to equivalent pixels, depending on device density.
     *
     * @param dp A value in dp (density independent pixels) unit. Which we need to convert into pixels
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent px equivalent to dp depending on device density
     */
    public static float convertDpToPixel(float dp, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return px;
    }

    /**
     * Calculates the % opacity I want to use. higher number means less see-through
     * @param percent % to convert, 0 means transparent, 100 means completely blocking background
     * @return int, used like this, view.getBackground().setAlpha(opacityPercent(55));
     */
    public static int opacityPercent(float percent){
        float x = 255;
        if(percent < 0 || percent > 100){
            return 255;
        } else {
            int y = (int) (x * (percent/100));
            return y;
        }
    }

    /**
     * Gets the height of the status bar (Above toolbar)
     * @param context
     * @return
     */
    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
}
