package com.pgmacdesign.pgmactips.customui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * This dotted circle is drawn dynamically as per the params passed
 * Created by pmacdowell on 2017-05-31.
 */

public class DottedCircle extends View {

    private static final String DOTTED_CIRCLE_ERROR =
            "dottedIntervals array of floats must be even in number. See documentation for details";
    private static final String TAG =
            "PGMacUtil-DottedCircle";

    private Context context;
    private Integer colorOfCircle;
    private Float transparencyOfCircle, dotPhase;
    private Paint paint;
    private float[] dotIntervals;
    private DashPathEffect dashPath;
    private int onePercentPixels, widthOfCircleStroke;

    public DottedCircle(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public DottedCircle(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    public DottedCircle(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public DottedCircle(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.context = context;
        init();
    }

    public void setColorOfCircle(int colorOfCircle) {
        this.colorOfCircle = colorOfCircle;
        if(this.paint != null) {
            this.paint.setColor(colorOfCircle);
        }
    }

    /**
     * Width of circle stroke in pixels
     * @param widthOfCircleStroke number >= 1
     */
    public void setWidthOfCircleStroke(int widthOfCircleStroke) {
        if(widthOfCircleStroke < 1){
            return;
        }
        this.widthOfCircleStroke = widthOfCircleStroke;
        this.paint.setStrokeWidth(this.widthOfCircleStroke);
    }

    /**
     * Transparency of circle in percent (ie 0.50 would be 50% transparent)
     * @param transparencyOfCircle Must be between 0 and 1.
     */
    public void setTransparencyOfCircle(float transparencyOfCircle) {
        if(transparencyOfCircle < 0 || transparencyOfCircle > 1){
            return;
        }
        this.transparencyOfCircle = transparencyOfCircle;
        if(this.paint != null) {
            this.paint.setAlpha((int) (this.transparencyOfCircle * 255));
        }
    }

    public void setPaint(Paint paint) {
        this.paint = paint;
    }

    /**
     * Dot interval pattern. Can be custom
     * @param dotIntervals Must be even in length. See documentation here for details:
     *                     {@link DashPathEffect#DashPathEffect}
     */
    public void setDotIntervals(float[] dotIntervals) {
        if(dotIntervals == null){
            return;
        }
        int mLength = dotIntervals.length;
        if((mLength % 2) != 0){
            Log.d(TAG, DOTTED_CIRCLE_ERROR);
            return;
        }
        this.dotIntervals = dotIntervals;
        setupDotPath();
    }

    public void setDotPhase(float dotPhase) {
        this.dotPhase = dotPhase;
        setupDotPath();
    }

    private void init(){
        this.widthOfCircleStroke = 1;
        this.paint = new Paint();
        this.paint.setAntiAlias(true);
        if(this.colorOfCircle == null){
            this.colorOfCircle = Color.parseColor("#00000000");
        }
        if(this.transparencyOfCircle == null){
            this.transparencyOfCircle = 1f;
        }
        if(this.transparencyOfCircle > 1 || this.transparencyOfCircle < 0){
            this.transparencyOfCircle = 1f;
        }
        setupDotPath();
        this.paint.setStrokeWidth(widthOfCircleStroke);
        this.paint.setAlpha((int)(this.transparencyOfCircle * 255));
        this.paint.setColor(colorOfCircle);
        this.paint.setStyle(Paint.Style.STROKE);
    }

    /**
     * Setup the dot path. Modularized out to reduce clutter
     */
    private void setupDotPath(){
        if(this.dotIntervals == null){
            this.dotIntervals = new float[]{10,10};
        } else {
            if(this.dotIntervals.length <= 0){
                this.dotIntervals = new float[]{10,10};
            }
        }
        if(this.dotPhase == null){
            this.dotPhase = 1.0f;
        }
        this.dashPath = new DashPathEffect(dotIntervals, dotPhase);
        if(this.paint != null){
            this.paint.setPathEffect(dashPath);
        }
    }

    /**
     * Draw the circle. Every time this is called, the circle will be redrawn
     */
    public void drawCircle(){
        try {
            invalidate();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = this.getWidth();
        int height = this.getHeight();

        int smallestWidth = width;
        if(height < width){
            //In case phone is rotated
            smallestWidth = height;
        }

        onePercentPixels = (int)(smallestWidth * 0.01);

        float centerOfCircle = smallestWidth / 2;
        float rradius = centerOfCircle - onePercentPixels - widthOfCircleStroke;

        drawImage(canvas, centerOfCircle, centerOfCircle, rradius);
    }

    private void drawImage(Canvas canvas, float centerX, float centerY, float rRadius){
        canvas.drawCircle(centerX, centerY, rRadius, paint);
    }
}
