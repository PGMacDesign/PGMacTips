package com.pgmacdesign.pgmactips.customui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;

/**
 * This Multi-color circle is drawn dynamically as per the params passed
 * Created by pmacdowell on 2017-05-22.
 */
public class MultiColorCircle extends View  {

    /**
     * Frames per second options on animation. Defaults to 60
     */
    public static enum FPS {
        FPS_1, FPS_5, FPS_10, FPS_15, FPS_30, FPS_60, FPS_90, FPS_120, FPS_240
    }

    private static final int MAX_DEFAULT_ANIMATION_TIME_IN_MILLISEC = (int)(1.5 * 1000);

    private double numberOfAnimationRuns;
    private FPS fps;
    private RectF rect, outerRect, innerRect;
    private Paint perimeterPaint;
    private List<CustomStrokeObject> strokeObjects, singleStrokeObjects, animatedStrokeObjects;
    private int widthOfCircleStroke, widthOfBoarderStroke,
            colorOfBoarderStroke, onePercentPixels;
    private boolean animateStrokes, drawBoarderWithCircle, drawAsSingleLine;
    private long totalAnimationTime;

    public MultiColorCircle(Context context) {
        super(context);
        init();
    }

    public MultiColorCircle(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MultiColorCircle(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public MultiColorCircle(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    /**
     * Setter for the width of the circle stroke. Affects all arcs drawn. This is the width of
     * the various arcs that make up the actual circle, this is NOT the boarder, that is different
     * @param widthOfCircleStroke
     */
    public void setWidthOfCircleStroke(int widthOfCircleStroke){
        this.widthOfCircleStroke = widthOfCircleStroke;
    }

    /**
     * Setter for the width of the boarder stroke. This is the width of the boarder strokes used
     * to make the inner and outer boarder of the rings that surround the main body circle.
     * They will default to black and 1 pixel in width. To hide them, pass null as the color
     * @param widthOfBoarderStroke
     */
    public void setWidthOfBoarderStroke(int widthOfBoarderStroke){
        this.widthOfBoarderStroke = widthOfBoarderStroke;
        this.perimeterPaint.setStrokeWidth(this.widthOfBoarderStroke);
    }

    /**
     * Set the color of the boarder stroke. Send in null if you want it to be hidden
     * @param colorOfBoarderStroke
     */
    public void setColorOfBoarderStroke(Integer colorOfBoarderStroke){
        if(colorOfBoarderStroke == null){
            //Set to transparent
            this.colorOfBoarderStroke = Color.parseColor("#00000000");
        } else {
            this.colorOfBoarderStroke = colorOfBoarderStroke;
        }
        this.perimeterPaint.setColor(this.colorOfBoarderStroke);
    }

    /**
     * Sets whether or not the inner and outer boarders should be drawn at the same time as
     * the other arcs or drawn before hand. Note, this only applied if there is animation, if
     * no animation is being used, this will be ignored.
     * @param bool If set to true, boarder circles will draw at the same time and will not draw
     *             beforehand (no 'empty' ring). If set to false, this will have the ring pre-
     *             drawn when the first invalidate gets called and it will appear as though the
     *             ring is being 'filled-in' instead.
     *             Defaults to false.
     */
    public void setDrawBoarderWithCircle(boolean bool){
        this.drawBoarderWithCircle = bool;
    }

    /**
     * Whether or not to animate the strokes. If true, will animate, if not, will not
     * @param bool
     */
    public void setAnimateStrokes(boolean bool, long timeInMilliseconds){
        this.animateStrokes = bool;
        if(timeInMilliseconds < 0){
            timeInMilliseconds = 0;
        }
        this.totalAnimationTime = timeInMilliseconds;
    }

    /**
     * Set the FPS for the animation. Defaults to 30 if not set
     * @param fps Frames per second {@link FPS}
     */
    public void setFps(FPS fps){
        if(fps != null){
            this.fps = fps;
        } else {
            this.fps = FPS.FPS_60;
        }
    }

    /**
     * Set whether or not the program should draw as a single line. If this is set to true,
     * the line will draw in one long arc as opposed to all the different arcs starting at
     * the same time. This defaults to false so that all arcs draw simultaneously.
     * @param bool true to draw lines synchronously, false to draw lines asynchronously
     */
    public void setDrawAsSingleLine(boolean bool){
        this.drawAsSingleLine = bool;
    }

    private void init(){
        this.fps = FPS.FPS_60;
        this.drawAsSingleLine = false;
        this.drawBoarderWithCircle = false;
        this.strokeObjects = new ArrayList<>();
        this.singleStrokeObjects = new ArrayList<>();
        this.animatedStrokeObjects = new ArrayList<>();
        this.animateStrokes = false; //Default
        this.totalAnimationTime = 0; //Default
        this.onePercentPixels = 0; //Default
        this.widthOfCircleStroke = 1; //Default
        this.widthOfBoarderStroke = 1; //Default
        this.colorOfBoarderStroke = Color.parseColor("#000000"); //Default, black
        this.rect = new RectF();
        this.outerRect = new RectF();
        this.innerRect = new RectF();
        this.perimeterPaint = new Paint();
        this.perimeterPaint.setStrokeWidth(widthOfBoarderStroke);
        this.perimeterPaint.setColor(colorOfBoarderStroke);
        this.perimeterPaint.setAntiAlias(true);
        this.perimeterPaint.setStyle(Paint.Style.STROKE);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = this.getWidth();
        int left = 0;
        int top = 0;
        int right = (left + width);
        int bottom = (top + width);

        onePercentPixels = (int)(this.getWidth() * 0.01);
        left = left + onePercentPixels + widthOfCircleStroke;
        top = top + onePercentPixels + widthOfCircleStroke;
        right = right - onePercentPixels - widthOfCircleStroke;
        bottom = bottom - onePercentPixels - widthOfCircleStroke;

        drawCircle(canvas, left, top, right, bottom);
    }

    private void drawCircle(Canvas canvas, int left, int top, int right, int bottom){
        //Base rect for sides of circle parameters
        rect.set(left, top, right, bottom);

        if(drawAsSingleLine && animateStrokes){
            if(this.singleStrokeObjects.size() <= 0){
                return;
            }
            for (CustomStrokeObject strokeObject : this.singleStrokeObjects) {
                if (strokeObject == null) {
                    continue;
                }

                Paint paint = strokeObject.paint;
                paint.setStrokeWidth(this.widthOfCircleStroke);
                try {
                    canvas.drawArc(rect, strokeObject.percentToStartAt,
                            strokeObject.percentOfCircle, false, paint);
                } catch (ConcurrentModificationException cme) {
                    cme.printStackTrace();
                    continue;
                }
            }

        } else {
            if(this.strokeObjects.size() <= 0){
                return;
            }
            for (CustomStrokeObject strokeObject : this.strokeObjects) {
                if (strokeObject == null) {
                    continue;
                }
                Paint paint = strokeObject.paint;
                paint.setStrokeWidth(this.widthOfCircleStroke);
                try {
                    canvas.drawArc(rect, strokeObject.percentToStartAt,
                            strokeObject.percentOfCircle, false, paint);
                } catch (ConcurrentModificationException cme) {
                    continue;
                }
            }
        }
        drawPerimeterCircle(canvas, left, top, right, bottom);
    }

    /**
     * Draws the outer and inner boarder arcs of black to create a boarder
     */
    private void drawPerimeterCircle(Canvas canvas, int left, int top, int right, int bottom){
        //Base inner and outer rectanges for circles to be drawn
        outerRect.set(
                (left - (widthOfCircleStroke / 2)),
                (top - (widthOfCircleStroke / 2)),
                (right + (widthOfCircleStroke / 2)),
                (bottom + (widthOfCircleStroke / 2))
        );
        innerRect.set(
                (left + (widthOfCircleStroke / 2)),
                (top + (widthOfCircleStroke / 2)),
                (right - (widthOfCircleStroke / 2)),
                (bottom - (widthOfCircleStroke / 2))
        );

        //To allow the user to either allow or deny the ring to look pre 'filled-in'
        if(drawBoarderWithCircle){

            if(drawAsSingleLine && animateStrokes){
                for (CustomStrokeObject strokeObject : this.singleStrokeObjects) {
                    if (strokeObject == null) {
                        continue;
                    }
                    try {
                        canvas.drawArc(outerRect, strokeObject.percentToStartAt,
                                strokeObject.percentOfCircle, false, perimeterPaint);
                        canvas.drawArc(innerRect, strokeObject.percentToStartAt,
                                strokeObject.percentOfCircle, false, perimeterPaint);
                    } catch (ConcurrentModificationException cme) {
                        continue;
                    }
                }
            } else {
                for (CustomStrokeObject strokeObject : this.strokeObjects) {
                    if (strokeObject == null) {
                        continue;
                    }
                    try {
                        canvas.drawArc(outerRect, strokeObject.percentToStartAt,
                                strokeObject.percentOfCircle, false, perimeterPaint);
                        canvas.drawArc(innerRect, strokeObject.percentToStartAt,
                                strokeObject.percentOfCircle, false, perimeterPaint);
                    } catch (ConcurrentModificationException cme) {
                        continue;
                    }
                }
            }
        } else {
            canvas.drawArc(outerRect, 0, 360, false, perimeterPaint);
            canvas.drawArc(innerRect, 0, 360, false, perimeterPaint);
        }

        this.bringToFront();
    }

    /**
     * Setter method for setting the various strokes on the circle
     * @param strokeObjects {@link CustomStrokeObject}
     */
    public void setCircleStrokes(List<CustomStrokeObject> strokeObjects){
        if(strokeObjects == null){
            return;
        }
        if(strokeObjects.size() == 0){
            return;
        }
        this.strokeObjects = new ArrayList<>();
        this.singleStrokeObjects = new ArrayList<>();
        this.strokeObjects = strokeObjects;
        if(animateStrokes){
            animateLines();
        } else {
            invalidate();
        }
    }

    /**
     * Animate the drawing of the lines/ arcs on the screen
     */
    private void animateLines(){

        this.animatedStrokeObjects = this.strokeObjects;

        calculateNumAnimationRuns();
        if(drawAsSingleLine){
            numberOfAnimationRuns = numberOfAnimationRuns * (this.animatedStrokeObjects.size());
        }
        long millisecondsPerRun;
        //Too quick at 10 milliseconds anyway, this is just to account for user error on param passing
        if(totalAnimationTime <= 10){
            totalAnimationTime = MAX_DEFAULT_ANIMATION_TIME_IN_MILLISEC;
            millisecondsPerRun = (long)(MAX_DEFAULT_ANIMATION_TIME_IN_MILLISEC / numberOfAnimationRuns);
        } else {
            millisecondsPerRun = (long)(totalAnimationTime / numberOfAnimationRuns);
        }
        CountDownTimer countDownTimer = new CountDownTimer(totalAnimationTime, millisecondsPerRun) {
            @Override
            public void onTick(long millisUntilFinished) {
                animateStrokePerMillisecond(millisUntilFinished);
            }

            @Override
            public void onFinish() {
                animateStrokePerMillisecond(0);
            }
        };
        countDownTimer.start();
    }


    /**
     * Send the animation 'request' by passing over the number of milliseconds until the timer
     * is done. This is counting backwards from the total time to complete. So if zero is passed,
     * it will be done and compelte the drawing.
     * @param millisUntilFinished
     */
    private void animateStrokePerMillisecond(long millisUntilFinished){
        //Update per tick. This will update as per # of runs
        int listSize = animatedStrokeObjects.size();
        strokeObjects = new ArrayList<>();

        double percentComplete =
                (((double)totalAnimationTime - (double)millisUntilFinished) / (double)totalAnimationTime);

        if(drawAsSingleLine){
            percentComplete = percentComplete * (listSize);
            /*
                X represents the remainder of which "percent" we are on.
                <1 && >=0 means first.
                <2 && >=1 means second, etc.
                Once last one is reached, it will throw an out of bounds error on array list
                as it is one too large. Just decrement and retrieve from object list again.
             */
            int x = (int) (percentComplete % 100);
            CustomStrokeObject s = null;
            while (s == null){
                if(x < 0){
                    return;
                }
                try {
                    s = animatedStrokeObjects.get(x);
                } catch (IndexOutOfBoundsException iobe){
                    x--;
                }
            }

            double z = (percentComplete % 100);
            while(z > 1){
                z-=1;
            }
            //As of now, Z represents the percent complete times the list size

            double f1 = (s.percentOfCircle * (z));
            CustomStrokeObject sepObj = new CustomStrokeObject(s.paint, (float) f1,
                    s.percentToStartAt, s.colorOfLine);

            try {
                this.singleStrokeObjects.set(x, sepObj);
            } catch (IndexOutOfBoundsException iobe){
                if(x == 0){
                    this.singleStrokeObjects.add(sepObj);
                } else {
                    //Go back one, set to 100%, then add this one back in
                    try {
                        int y = (x - 1); //Do not want to alter x, using it after this
                        CustomStrokeObject s2 = animatedStrokeObjects.get(y);
                        //double f2 = (s2.percentOfCircle * (actualPercentComplete));
                        CustomStrokeObject sepObj2 = new CustomStrokeObject(s2.paint,
                                s2.percentOfCircle, s2.percentToStartAt, s2.colorOfLine);
                        this.singleStrokeObjects.set(y, sepObj2);
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                    this.singleStrokeObjects.add(sepObj);
                }
            }

        } else {
            for (CustomStrokeObject s : animatedStrokeObjects) {

                double f1 = (s.percentOfCircle * percentComplete);

                CustomStrokeObject sepObj = new CustomStrokeObject(s.paint, (float) f1,
                        s.percentToStartAt, s.colorOfLine);
                strokeObjects.add(sepObj);
            }
        }

        invalidate();

    }

    /**
     * Calculate the number of animation runs by calculating the Frames per second
     * (FPS chosen * total animation time in seconds)
     */
    private void calculateNumAnimationRuns(){
        switch (fps){
            case FPS_1:
                numberOfAnimationRuns = 1*((double)totalAnimationTime / 1000);
                break;

            case FPS_5:
                numberOfAnimationRuns = 5*((double)totalAnimationTime / 1000);
                break;

            case FPS_10:
                numberOfAnimationRuns = 10*((double)totalAnimationTime / 1000);
                break;

            case FPS_15:
                numberOfAnimationRuns = 15*((double)totalAnimationTime / 1000);
                break;

            case FPS_30:
                numberOfAnimationRuns = 30*((double)totalAnimationTime / 1000);
                break;

            case FPS_60:
                numberOfAnimationRuns = 60*((double)totalAnimationTime / 1000);
                break;

            case FPS_90:
                numberOfAnimationRuns = 90*((double)totalAnimationTime / 1000);
                break;

            case FPS_120:
                numberOfAnimationRuns = 120*((double)totalAnimationTime / 1000);
                break;

            case FPS_240:
                numberOfAnimationRuns = 240*((double)totalAnimationTime / 1000);
                break;

        }
    }

    /**
     * Class used in drawing arcs of circle
     */
    public static class CustomStrokeObject {

        float percentOfCircle;
        float percentToStartAt;
        Integer colorOfLine;
        Paint paint;



        /**
         * Constructor. This will also do the calculations to convert the percentages into the
         * circle numbers so that passing in 50 will be converted into 180 for mapping on to a
         * circle. Also, I am adding in a very tiny amount of overlap (a couple pixels) so that
         * there will not be a gap between the arcs because the whitespace gap of a couple pixels
         * does not look very good. To remove this, just remove the -.1 and .1 to startAt and circle
         * @param percentOfCircle Percent of the circle to fill.
         *                        NOTE! THIS IS BASED OFF OF 100%!
         *                        This is not based off of a full 360 circle so if you want something
         *                        to fill half the circle, pass 50, not 180.
         * @param percentToStartAt Percent to start at (for filling multiple colors).
         *                         NOTE! THIS IS BASED OFF OF 100%!
         *                         This is not based off of a full 360 circle so if you want something
         *                         to fill half the circle, pass 50, not 180.
         * @param colorOfLine Int color of the line to use
         */
        public CustomStrokeObject(float percentOfCircle, float percentToStartAt, Integer colorOfLine){
            this.percentOfCircle = percentOfCircle;
            this.percentToStartAt = percentToStartAt;
            this.colorOfLine = colorOfLine;
            if(this.percentOfCircle < 0 || this.percentOfCircle > 100){
                this.percentOfCircle = 100; //Default to 100%
            }

            this.percentOfCircle = (float)((360 * (percentOfCircle)) / 100);
            if(this.percentToStartAt < 0 || this.percentToStartAt > 100){
                this.percentToStartAt = 0;
            }
            //-90 so it will start at top, Ex: http://www.cumulations.com/images/blog/screen1.png
            this.percentToStartAt = (float)((360 * (percentToStartAt)) / 100) - 90;
            if(this.colorOfLine == null){
                this.colorOfLine = Color.parseColor("#000000"); //Default to black
            }

            this.paint = new Paint();
            this.paint.setColor(colorOfLine);
            this.paint.setAntiAlias(true);
            this.paint.setStyle(Paint.Style.STROKE);
            //If you want to change this, use one of the overloaded constructors or setPaint()
            this.paint.setStrokeCap(Paint.Cap.ROUND);
        }

        /**
         * Overloaded method. Allows for items to be passed in that already have calculations
         * done for conversion.
         */
        public CustomStrokeObject(Paint paint, float percentOfCircle,
                                  float percentToStartAt, Integer colorOfLine){
            this.percentOfCircle = percentOfCircle;
            this.percentToStartAt = percentToStartAt;
            this.colorOfLine = colorOfLine;
            if(this.colorOfLine == null){
                this.colorOfLine = Color.parseColor("#000000"); //Default to black
            }
            this.paint = paint;
        }

        /**
         * Custom overloaded constructor. Allows for duplicating objects within foreach loops
         * @param obj
         */
        public CustomStrokeObject(CustomStrokeObject obj){
            this.percentOfCircle = obj.percentOfCircle;
            this.percentToStartAt = obj.percentToStartAt;
            this.colorOfLine = obj.colorOfLine;
            this.paint = obj.paint;
        }

        /**
         * Overloaded setter, in case you want to set a custom paint object here
         * @param paint Paint object to overwrite one set by constructor
         */
        public void setPaint(Paint paint){
            this.paint = paint;
        }

        /**
         * Getter for Paint. Allows editing of the paint object for setting custom prefs
         * @return Paint
         */
        public Paint getPaint(){
            return this.paint;
        }
    }
}


