package com.pgmacdesign.pgmacutilities.utilities;

import android.graphics.Point;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

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
    public static final int VIEW_FINISHED_DRAWING =
            PGMacUtilitiesConstants.VIEW_FINISHED_DRAWING;

    public static ResizingViewObject scaleGIFTo(float gifWidth, float gifHeight,
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

        ResizingViewObject obj = new ResizingViewObject();
        obj.maintainedRatio = maintainRatio;
        if(maintainRatio){
            float multiplier, newWidth, newHeight, widthTranslatePixels, heightTranslatePixels;
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
     * Object used for resizing views returns
     */
    public static class ResizingViewObject {

        //All used in translating objects
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

    /**
     * Set the view margins and attempt to redraw
     * @param view View to alter
     * @param left   margins - left side
     * @param top    margins - top side
     * @param right  margins - right side
     * @param bottom margins - bottom side
     */
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

    /**
     * Returns view margins in same order as setting (left, top, right, bottom)
     * @param view View to check
     * @return int array (always size 3) where all 4 elements match same order
     *         as setting (left, top, right, bottom). If margins are unable to
     *         be retrieved, will return array full of zeros
     */
    public static int[] getViewMargins(View view){
        int[] toReturn = {0, 0, 0, 0};
        if(view == null){
            return toReturn;
        }
        try {
            if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
                ViewGroup.MarginLayoutParams p =
                        (ViewGroup.MarginLayoutParams) view.getLayoutParams();
                toReturn[0] = p.leftMargin;
                toReturn[1] = p.topMargin;
                toReturn[2] = p.rightMargin;
                toReturn[3] = p.bottomMargin;
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return toReturn;
    }

    /**
     * Resize a view to a square by taking lowest width or height and resizing to that
     * @param view View to alter && return
     * @return If successful, altered view. If not successful, passed view
     */
    public static <T extends View> T resizeViewToSquare(@NonNull T view){
        return resizeViewToSquare(view, false, false);
    }

    /**
     * Resize a view to a square by taking lowest width or height and resizing to that.
     * Overloaded, takes in 2 new params to allow for centering and margin removal
     * @param view View to alter && return
     * @param centerView Should the view be centered? If so, will attempt to center view
     *                   within layout params capabilities
     * @param removeMargins Should remove the margins? This is useful if trying to resize
     *                      something and want it to go to the edges. Note, will not remove
     *                      the padding if that exists. If true, this will set all the set
     *                      margins to zero.
     * @return If successful, altered view. If not successful, passed view
     */
    public static <T extends View> T resizeViewToSquare(@NonNull T view,
                                                         boolean centerView, boolean removeMargins){
        if(view == null){
            return view;
        }

        int width = view.getWidth();
        int height = view.getHeight();
        int lowestOfTwo;
        //Portraint vs landscape
        if(width < height){
            lowestOfTwo = width;
        } else {
            lowestOfTwo = height;
        }

        try {
            if (view.getLayoutParams() instanceof LinearLayout.LayoutParams) {
                LinearLayout.LayoutParams p = (LinearLayout.LayoutParams)
                        view.getLayoutParams();
                p.width = lowestOfTwo;
                p.height = lowestOfTwo;
                if (removeMargins) {
                    p.setMargins(0, 0, 0, 0);
                }
                if (centerView) {
                    p.gravity = Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL;
                }

            } else if (view.getLayoutParams() instanceof RelativeLayout.LayoutParams) {
                RelativeLayout.LayoutParams p = (RelativeLayout.LayoutParams)
                        view.getLayoutParams();
                p.width = lowestOfTwo;
                p.height = lowestOfTwo;
                if (removeMargins) {
                    p.setMargins(0, 0, 0, 0);
                }
                if (centerView) {
                    p.addRule(RelativeLayout.CENTER_IN_PARENT);
                    p.addRule(RelativeLayout.CENTER_HORIZONTAL);
                    p.addRule(RelativeLayout.CENTER_VERTICAL);
                }

            } else if (view.getLayoutParams() instanceof FrameLayout.LayoutParams) {
                FrameLayout.LayoutParams p = (FrameLayout.LayoutParams)
                        view.getLayoutParams();
                p.width = lowestOfTwo;
                p.height = lowestOfTwo;
                if (removeMargins) {
                    p.setMargins(0, 0, 0, 0);
                }
                if (centerView) {
                    p.gravity = Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL;
                }
            } else if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
                ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams)
                        view.getLayoutParams();

                p.width = lowestOfTwo;
                p.height = lowestOfTwo;
                if (removeMargins) {
                    p.setMargins(0, 0, 0, 0);
                }

            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return view;
    }

    /**
     * Used to get a view after it has been drawn. If you are attempting to get view bounds
     * or sizing and are noticing you are always getting 0 for everything, it is because you
     * need to wait for it to be drawn before you can obtain these results. Use this method
     * to get the fully-drawn view and use it however.
     * @param listener Listener to pass back view on
     * @param view View that will be returned when loaded
     */
    public static void getDrawnView(@NonNull final OnTaskCompleteListener listener,
                                     @NonNull final View view){
        view.post(new Runnable() {
            @Override
            public void run() {
                listener.onTaskComplete(view, VIEW_FINISHED_DRAWING);
            }
        });
    }

    /**
     * Used when returning a view bounds (top left, top right, bottom left, bottom right)
     */
    public static class ViewBoundsObject {
        private Point topLeft;
        private Point topRight;
        private Point bottomLeft;
        private Point bottomRight;

        public Point getTopLeft() {
            return topLeft;
        }

        public Point getTopRight() {
            return topRight;
        }

        public Point getBottomLeft() {
            return bottomLeft;
        }

        public Point getBottomRight() {
            return bottomRight;
        }
    }

    /**
     * Get view bounds. Note! This will account for margins! If you do not want to account
     * for Margins, please use the overloaded method:
     * todo refactor in padding?
     * @param view View to be checked
     * @return {@link ViewBoundsObject}
     */
    public static ViewBoundsObject getViewCoordinates(@NonNull View view){
        int[] topLeftCoord = new int[2];
        view.getLocationOnScreen(topLeftCoord);
        return getViewBounds(view, topLeftCoord);
    }

    /**
     * get view bounds
     * @param view view View to be checked
     * @param isADialog For passing in additional option. Dialogs are measured slightly
     *                  differently and should be measured differently than normal views.
     * @param useRelativeToParent If this is set to true, it will override isADialog (if that is
     *                            set to true). This gets params in respect to parent view.
     * @return {@link ViewBoundsObject}
     */
    public static ViewBoundsObject getViewCoordinates(@NonNull View view,
                                                      boolean isADialog,
                                                      boolean useRelativeToParent){
        int[] topLeftCoord = new int[2];
        if(isADialog){
            view.getLocationInWindow(topLeftCoord);
        } else {
            view.getLocationOnScreen(topLeftCoord);
        }
        if(useRelativeToParent){
            topLeftCoord[0] = view.getLeft();
            topLeftCoord[1] = view.getTop();
        }
        return getViewBounds(view, topLeftCoord);
    }

    private static ViewBoundsObject getViewBounds(@NonNull View view, int[] topLeftCoord){

        ViewBoundsObject o = new ViewBoundsObject();
        int viewWidth = view.getWidth();
        int viewHeight = view.getHeight();

        //Top Left
        int x0 = topLeftCoord[0];
        int y0 = topLeftCoord[1];
        //Top Right
        int x1 = x0 + viewWidth;
        int y1 = y0;
        //Bottom Left
        int x2 = x0;
        int y2 = y0 + viewHeight;
        //Bottom Right
        int x3 = x0 + viewWidth;
        int y3 = y0 + viewHeight;

        /*
        To my understanding, this is already done with the calls
        if(accountForMargins) {
            if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
                ViewGroup.MarginLayoutParams params =
                        (ViewGroup.MarginLayoutParams) view.getLayoutParams();

                int bottomMargin = params.bottomMargin;
                int rightMargin = params.rightMargin;
                int leftMargin = params.leftMargin;
                int topMargin = params.topMargin;

                //Top Left
                x0 += leftMargin;
                y0 += topMargin;
                //Top Right
                x1 -= rightMargin;
                y1 += topMargin;
                //Bottom Left
                x2 += leftMargin;
                y2 -= bottomMargin;
                //Bottom Right
                x3 -= rightMargin;
                y3 -= bottomMargin;
            }
        }
        */

        Point topLeft = new Point(x0, y0);
        Point topRight = new Point(x1, y1);
        Point bottomLeft = new Point(x2, y2);
        Point bottomRight = new Point(x3, y3);

        o.topLeft = topLeft;
        o.topRight = topRight;
        o.bottomLeft = bottomLeft;
        o.bottomRight = bottomRight;

        return o;
    }
}
