package com.pgmacdesign.pgmacutilities.graphicsanddrawing;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.pgmacdesign.pgmacutilities.nonutilities.PGMacUtilitiesConstants;
import com.pgmacdesign.pgmacutilities.utilities.ColorUtilities;
import com.pgmacdesign.pgmacutilities.utilities.L;

/**
 * This class was created in response to a need for a circle / oval in the center of a photo.
 * See stackoverflow answer here for more details:
 * http://stackoverflow.com/questions/19947835/android-canvas-draw-transparent-circle-on-image
 * Created by pmacdowell on 9/21/2016.
 */
public class CircleOverlayView extends LinearLayout {
    private Bitmap bitmap;
    private CircleOverlayParams overlayParams;

    public CircleOverlayView(Context context, CircleOverlayParams overlayParams) {
        super(context);
        this.overlayParams = overlayParams;
    }

    public CircleOverlayView(Context context, AttributeSet attrs, CircleOverlayParams overlayParams) {
        super(context, attrs);
        this.overlayParams = overlayParams;
    }

    public CircleOverlayView(Context context, AttributeSet attrs, int defStyleAttr,
                             CircleOverlayParams overlayParams) {
        super(context, attrs, defStyleAttr);
        this.overlayParams = overlayParams;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CircleOverlayView(Context context, AttributeSet attrs, int defStyleAttr,
                             int defStyleRes, CircleOverlayParams overlayParams) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.overlayParams = overlayParams;
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);

        if(overlayParams == null){
            overlayParams = new CircleOverlayParams();
        }
        if (bitmap == null) {
            createWindowFrame();
        }
        canvas.drawBitmap(bitmap, 0, 0, null);
    }

    protected void createWindowFrame() {
        bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas osCanvas = new Canvas(bitmap);

        RectF outerRectangle = new RectF(0, 0, getWidth(), getHeight());

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        int color = overlayParams.getColorToSet();
        paint.setColor(color);
        int alphaToSet = overlayParams.getAlphaToUse();
        if(alphaToSet < 0 || alphaToSet > 100){
            alphaToSet = 99;
        }
        paint.setAlpha(alphaToSet);
        osCanvas.drawRect(outerRectangle, paint);

        paint.setColor(Color.TRANSPARENT);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OUT));
        float centerX = getWidth() / 2;
        float centerY = getHeight() / 2;

        //float radius = 50F;//getResources().getDimensionPixelSize(50);
        float radius = overlayParams.getShapeRadius();
        if(radius <= 0){
            radius = 300F;
        }

        float xOffset = (radius);
        float yOffset = (radius);
        float left = centerX - xOffset;
        float top = centerY - yOffset;
        float right = centerX + xOffset;
        float bottom = centerY + yOffset;
        //Add 25% to elongate the oval a bit
        float topOval = (float)(centerY - (1.25 * yOffset));
        float bottomOval = (float)(centerY + (1.25 * yOffset));

        CircleOverlayParams.ShapeTypes shapeTypes = overlayParams.getShapeType();
        L.m("shape being set = " + shapeTypes.toString());
        switch (shapeTypes){
            case CIRCLE:
                osCanvas.drawCircle(centerX, centerY, radius, paint);
                break;

            case SQUARE:
                osCanvas.drawRect(left, top, right, bottom, paint);
                break;

            case OVAL:
            default:
                RectF rect = new RectF(left,topOval,right,bottomOval);
                osCanvas.drawOval(rect, paint);
                break;
        }

        //
    }

    @Override
    public boolean isInEditMode() {
        return true;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        bitmap = null;
    }

    public static class CircleOverlayParams {
        public static enum ShapeTypes {
            CIRCLE, OVAL, SQUARE
        }
        private int colorToSet;
        private ShapeTypes shapeType;
        private float shapeRadius;
        private int alphaToUse;

        public CircleOverlayParams(){
            colorToSet = ColorUtilities.parseMyColor(PGMacUtilitiesConstants.COLOR_LIGHT_GRAY);
            alphaToUse = 99;
            shapeType = ShapeTypes.OVAL;
        }

        public float getShapeRadius() {
            return shapeRadius;
        }

        public void setShapeRadius(float shapeRadius) {
            this.shapeRadius = shapeRadius;
        }

        public int getColorToSet() {
            return colorToSet;
        }

        public void setColorToSet(int colorToSet) {
            this.colorToSet = colorToSet;
        }

        public ShapeTypes getShapeType() {
            return shapeType;
        }

        public void setShapeType(ShapeTypes shapeType) {
            this.shapeType = shapeType;
        }

        public int getAlphaToUse() {
            return alphaToUse;
        }

        public void setAlphaToUse(int alphaToUse) {
            this.alphaToUse = alphaToUse;
        }
    }
}
