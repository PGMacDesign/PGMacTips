package com.pgmacdesign.pgmacutilities.utilities;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Point;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.WindowManager;

import com.pgmacdesign.pgmacutilities.R;

/**
 * This class helps to determine the screen height and width in Density Pixels (DP)
 * Created by pmacdowell on 8/15/2016.
 */
public class DisplayManagerUtilities {

    //Display display;
    private DisplayMetrics outMetrics, api17OutMetrics; //The latter is for APIs 17 or higher
    private WindowManager windowManager;
    private Display mDisplay;
    private Configuration mConfig;
    private float densityRatio;
    private float dpWidth, dpHeight;
    private int pixelsWidth, pixelsHeight;
    private String totalScreenDimensionsPixels, totalScreenDimensionsDP;

    //Misc values
    private Context context;


    /**
     * This will get the width and height of the screen in DP. This constructor is used to define
     * the instance variables and set them while the other methods are used as accessor/ getters.
     * @param context Context from activity would be 'this' and from fragment would be 'getActivity()'
     */
    public DisplayManagerUtilities(Context context){
        this.context = context;
        init();
    }

    private void init(){
        outMetrics = new DisplayMetrics();
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(outMetrics);
        mDisplay = windowManager.getDefaultDisplay();
        mConfig = context.getResources().getConfiguration();

        if(Build.VERSION.SDK_INT >= 17) {
            api17OutMetrics = new DisplayMetrics();
            mDisplay.getRealMetrics(api17OutMetrics);
        }

        //Height and width in pixels
        if(Build.VERSION.SDK_INT >= 13) {
            Point point = new Point();
            mDisplay.getSize(point);
            pixelsWidth = point.x;
            pixelsHeight = point.y;
        } else {
            pixelsWidth = mDisplay.getWidth();
            pixelsHeight = mDisplay.getHeight();
        }
        totalScreenDimensionsPixels = pixelsWidth + "x" + pixelsHeight;

        //Height and weight in DP
        int tempx = -1, tempy = -1;
        if(Build.VERSION.SDK_INT >= 17) {
            tempx = api17OutMetrics.widthPixels;
            tempy = api17OutMetrics.heightPixels;
        }
        if(tempx > 0 && tempy >  0){
            //API 17+
            densityRatio = api17OutMetrics.density;
            dpHeight = (float)((tempy / densityRatio) + 0.5);
            dpWidth = (float)((tempx / densityRatio) + 0.5);
        } else {
            //API 16-
            densityRatio = outMetrics.density;
            dpHeight = (float)((pixelsHeight / densityRatio) + 0.5);
            dpWidth = (float)((pixelsWidth / densityRatio) + 0.5);
        }
        totalScreenDimensionsDP = dpWidth + "x" + dpHeight;
    }

    /**
     * Gets the width of the screen in DP
     * @return returns a float
     */
    public float getScreenWidthDP(){
        return dpWidth;
    }

    /**
     * Gets the height of the screen in DP
     * @return returns a float
     */
    public float getScreenHeightDP(){
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

    public float getDensityRatio(){
        return densityRatio;
    }

    /**
     * This returns the size of the navigation bar height
     * @return an int (in pixels);
     */
    public int getNavigationBarSize(){
        float x = context.getResources().getDimension(
                R.dimen.abc_action_bar_default_height_material);
        return (int) x;
    }

    /**
     * This method converts device specific pixels to densityRatio independent pixels.
     *
     * @param px A value in px (pixels) unit. Which we need to convert into db
     * @return A float value to represent dp equivalent to px value
     */
    public float convertPixelsToDp(float px){
        float dp;
        if(Build.VERSION.SDK_INT >= 17) {
            dp = (float) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, px, api17OutMetrics);
        } else {
            dp = (float) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, px, outMetrics);
        }
        return dp;
    }

    /**
     * This method converts dp unit to equivalent pixels, depending on device densityRatio.
     *
     * @param dp A value in dp (densityRatio independent pixels) unit. Which we need to convert into pixels
     * @return A float value to represent px equivalent to dp depending on device densityRatio
     */
    public float convertDpToPixel(float dp){
        float px = (float)(dp * densityRatio);
        return px;
    }

    /**
     * Builds and returns the radius of the screen for a circle to be drawn in the center.
     * If you want to make this smaller, simply divide it by X%
     * @return
     */
    public float getWidthRadius(){
        return (pixelsWidth / 2);
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
