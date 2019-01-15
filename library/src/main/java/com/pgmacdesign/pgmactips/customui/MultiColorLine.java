package com.pgmacdesign.pgmactips.customui;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.os.CountDownTimer;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import com.pgmacdesign.pgmactips.R;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;

/**
 * This Multi-color line is drawn dynamically as per the params passed. Sample of how to use:
 *     Sample of how to use:
 *          multiColorLine = (MultiColorLine) view.findViewById(R.id.multiColorLine);
 *          multiColorLine.setDrawDiagonally(false);
 *          multiColorLine.setDrawAsSingleLine(true);
 *          multiColorLine.setReverseDrawingAnimation(true);
 *          multiColorLine.setAnimateStrokes(true, 750);
 *          multiColorLine.setColorOfBoarderStroke(ContextCompat.getColor(context, R.color.Black));
 *          multiColorLine.setDrawBoarderWithLine(false);
 *          multiColorLine.setFps(MultiColorLine.FPS.FPS_120);
 *          multiColorLine.setWidthOfBoarderStroke(1);
 *          multiColorLine.setRoundCapPaddingAddition(0.04F);
 *          float flt = this.dmu.convertToPixels(DisplayManagerUtilities.ComplexUnits
 *          .COMPLEX_UNIT_DIP, 19F);
 *          multiColorLine.setWidthOfLineStroke((int) flt);
 *          float percentComplete1 = 45F;
 *          float percentComplete2 = 55F;
 *
 *          //Line 1
 *          int colorBlue = ContextCompat.getColor(context, R.color.Blue);
 *          MultiColorLine.CustomStrokeObject obj1 = new MultiColorLine.CustomStrokeObject(
 *          percentComplete1, 0, colorBlue);
 *          Paint paint1 = new Paint();
 *          paint1.setAntiAlias(true);
 *          paint1.setColor(colorBlue); //Depending on your color, this may be necessary.
 *          paint1.setStyle(Paint.Style.STROKE);
 *          paint1.setStrokeCap(Paint.Cap.ROUND);
 *          //Set shading here if need be. IE: paint1.setShader(new LinearGradient())
 *          obj1.setPaint(paint1);
 *
 *          //Line 2
 *          int colorRed = ContextCompat.getColor(context, R.color.Red);
 *          MultiColorLine.CustomStrokeObject obj2 = new MultiColorLine.CustomStrokeObject(
 *              (100 - percentComplete), percentComplete2, colorRed)
 *          );
 *          Paint paint2 = obj2.getPaint();
 *          paint2.setAntiAlias(true);
 *          paint2.setColor(colorRed); //Depending on your color, this may be necessary.
 *          paint2.setStyle(Paint.Style.STROKE);
 *          paint2.setStrokeCap(Paint.Cap.ROUND);
 *          //Set shading here if need be. IE: paint2.setShader(new LinearGradient())
 *          obj2.setPaint(paint2);
 *
 *          List<MultiColorLine.CustomStrokeObject> objects = new ArrayList<>();
 *          objects.add(obj1);
 *          objects.add(obj2);
 *         multiColorLine.setLineStrokes(objects);
 * Created by pmacdowell on 2017-06-20.
 */
public class MultiColorLine extends View {

    
    
    /**
     * Frames per second options on animation. Defaults to 60
     */
    public static enum FPS {
        FPS_1, FPS_5, FPS_10, FPS_15, FPS_30, FPS_60, FPS_90, FPS_120, FPS_240
    }

    private static final int MAX_DEFAULT_ANIMATION_TIME_IN_MILLISEC = (int) (1.5 * 1000);

    private double numberOfAnimationRuns;
    private MultiColorLine.FPS fps;
    private RectF rect, outerRect;
    private Paint perimeterPaint, transparentPaint;
    private List<CustomStrokeObject> strokeObjects, singleStrokeObjects, animatedStrokeObjects;
    private int widthOfLineStroke, widthOfBoarderStroke,
            colorOfBoarderStroke, onePercentPixels;
    private boolean animateStrokes, drawBoarderWithLine, drawAsSingleLine,
            drawDiagonally, reverseDrawingAnimation;
    private long totalAnimationTime;
    private float roundCapPaddingAddition;

    public MultiColorLine(Context context) {
        super(context);
        init();
    }

    public MultiColorLine(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MultiColorLine(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public MultiColorLine(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    /**
     * Setter for the width of the line stroke. Affects all lines drawn. This is the width of
     * the line, this is NOT the boarder (outside edges), that is different
     *
     * @param widthOfLineStroke
     */
    public void setWidthOfLineStroke(int widthOfLineStroke) {
        this.widthOfLineStroke = widthOfLineStroke;
    }

    /**
     * Setter for the width of the boarder stroke. This is the width of the boarder strokes used
     * to make the inner and outer boarder of the line that surrounds the main body line.
     * They will default to black and 1 pixel in width. To hide them, pass null as the color
     *
     * @param widthOfBoarderStroke
     */
    public void setWidthOfBoarderStroke(int widthOfBoarderStroke) {
        this.widthOfBoarderStroke = widthOfBoarderStroke;
        this.perimeterPaint.setStrokeWidth(this.widthOfBoarderStroke);
    }

    /**
     * Set the color of the boarder stroke. Send in null if you want it to be hidden
     *
     * @param colorOfBoarderStroke
     */
    public void setColorOfBoarderStroke(Integer colorOfBoarderStroke) {
        if (colorOfBoarderStroke == null) {
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
     *
     * @param bool If set to true, boarder lines will draw at the same time and will not draw
     *             beforehand (no 'empty' ring). If set to false, this will have the ring pre-
     *             drawn when the first invalidate gets called and it will appear as though the
     *             ring is being 'filled-in' instead.
     *             Defaults to false.
     */
    public void setDrawBoarderWithLine(boolean bool) {
        this.drawBoarderWithLine = bool;
    }

    /**
     * Whether or not to animate the strokes. If true, will animate, if not, will not
     *
     * @param bool
     */
    public void setAnimateStrokes(boolean bool, long timeInMilliseconds) {
        this.animateStrokes = bool;
        if (timeInMilliseconds < 0) {
            timeInMilliseconds = 0;
        }
        this.totalAnimationTime = timeInMilliseconds;
    }

    /**
     * Set the FPS for the animation. Defaults to 30 if not set
     *
     * @param fps Frames per second {@link MultiColorLine.FPS}
     */
    public void setFps(MultiColorLine.FPS fps) {
        if (fps != null) {
            this.fps = fps;
        } else {
            this.fps = MultiColorLine.FPS.FPS_60;
        }
    }

    /**
     * Set whether or not the program should draw as a single line. If this is set to true,
     * the line will draw in one long arc as opposed to all the different arcs starting at
     * the same time. This defaults to false so that all arcs draw simultaneously.
     *
     * @param bool true to draw lines synchronously, false to draw lines asynchronously
     */
    public void setDrawAsSingleLine(boolean bool) {
        this.drawAsSingleLine = bool;
    }

    /**
     * Set whether or not the program should draw the line diagonally.
     * Defaults to false. If set to true, it will draw from right to left as normal.
     * If set to true, it will draw from top left to bottom right.
     * @param drawDiagonally
     */
    public void setDrawDiagonally(boolean drawDiagonally) {
        this.drawDiagonally = drawDiagonally;
    }

    /**
     * This is designed to add padding on the sides if the paint Cap type is round. It
     * is mainly used for adding in a little overlap so there is not a gap on the edges
     * or in the center where the 2 round spots meet.
     * @param roundCapPaddingAddition Float value (<1 && >0) to use. If nothing is set,
     *                                it will default to 3% (0.03).
     */
    public void setRoundCapPaddingAddition(float roundCapPaddingAddition) {
        this.roundCapPaddingAddition = roundCapPaddingAddition;
    }

    /**
     * Set to reverse the direction of the drawing animation.
     * NOTE! As of right now, this is not functioning as intended. It currently just reverses the
     * order in which the lines are drawn, meaning that the left lines will be "on top" of the right
     * lines. Will refactor when time permits
     * @param shouldReverseAnimation If false (default) will draw from left to right, if true,
     *                                will draw from left to right.
     */
    public void setReverseDrawingAnimation(boolean shouldReverseAnimation) {
        this.reverseDrawingAnimation = shouldReverseAnimation;
    }

    private void init() {
        this.fps = MultiColorLine.FPS.FPS_60;
        this.drawDiagonally = false;
        this.drawAsSingleLine = false;
        this.drawBoarderWithLine = false;
        this.reverseDrawingAnimation = false;
        this.strokeObjects = new ArrayList<>();
        this.singleStrokeObjects = new ArrayList<>();
        this.animatedStrokeObjects = new ArrayList<>();
        this.animateStrokes = false; //Default
        this.totalAnimationTime = 0; //Default
        this.onePercentPixels = 0; //Default
        this.widthOfLineStroke = 1; //Default
        this.widthOfBoarderStroke = 1; //Default
        this.roundCapPaddingAddition = 0.03F; //Default
        this.colorOfBoarderStroke = Color.parseColor("#00000000"); //Default, Transparent
        this.rect = new RectF();
        this.outerRect = new RectF();
        this.perimeterPaint = new Paint();
        this.perimeterPaint.setStrokeWidth(widthOfBoarderStroke);
        this.perimeterPaint.setColor(colorOfBoarderStroke);
        this.perimeterPaint.setAntiAlias(true);
        this.perimeterPaint.setStyle(Paint.Style.STROKE);
        this.perimeterPaint.setStrokeCap(Paint.Cap.ROUND);

        int transparent = 0;
        try {
            transparent = ContextCompat.getColor(getContext(), R.color.Transparent);
        } catch (Resources.NotFoundException e){
            transparent = android.R.color.transparent;
        }
        this.transparentPaint = new Paint(transparent);
        this.transparentPaint.setStrokeWidth(widthOfBoarderStroke);
        this.transparentPaint.setColor(colorOfBoarderStroke);
        this.transparentPaint.setAntiAlias(true);
        this.transparentPaint.setStyle(Paint.Style.STROKE);
        //this.transparentPaint.setStrokeCap(Paint.Cap.ROUND);

        this.setBackgroundColor(transparent);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = this.getWidth();
        int height = this.widthOfLineStroke;
        int left = 0;
        int top = 0;
        int right = (left + width);
        int bottom = (top + height);

        //This is the left, top, right, and bottom of the VIEW (not the line being drawn)
        left = left + widthOfLineStroke;
        top = top + widthOfLineStroke;
        right = right - widthOfLineStroke;
        bottom = bottom;
        //Check if top and bottom are too small to view, if so, resize
        if(bottom == top || (bottom >= (top - 2)) || (top <= (bottom + 2))){
            bottom = (int)(bottom + (widthOfLineStroke * 0.3));
            top = (int)(top - (widthOfLineStroke * 0.3));
        }
        drawLine(canvas, left, top, right, bottom);
    }



    private void drawLine(Canvas canvas, int left, int top, int right, int bottom) {
        //Base rect for sides of circle parameters
        rect.set(left, top, right, bottom);
        if (drawAsSingleLine && animateStrokes) {
            if (this.singleStrokeObjects.size() <= 0) {
                return;
            }
            if(reverseDrawingAnimation){
                for(int i = this.singleStrokeObjects.size(); i > 0; i--){
                    CustomStrokeObject strokeObject = this.singleStrokeObjects.get(i-1);
                    if (strokeObject == null) {
                        continue;
                    }
                    drawSingleLineAnimate(canvas, left, top, right, bottom, strokeObject);
                }
            } else {
                for(int i = 0; i < this.singleStrokeObjects.size(); i++){
                    CustomStrokeObject strokeObject = this.singleStrokeObjects.get(i);
                    if (strokeObject == null) {
                        continue;
                    }
                    drawSingleLineAnimate(canvas, left, top, right, bottom, strokeObject);
                }
            }
        } else {
            if (this.strokeObjects.size() <= 0) {
                return;
            }
            if(reverseDrawingAnimation){
                for(int i = this.strokeObjects.size(); i > 0; i--){
                    CustomStrokeObject strokeObject = this.strokeObjects.get(i-1);
                    if (strokeObject == null) {
                        continue;
                    }
                    drawSingleLineAnimate(canvas, left, top, right, bottom, strokeObject);
                }
            } else {
                for(int i = 0; i < this.strokeObjects.size(); i++){
                    CustomStrokeObject strokeObject = this.strokeObjects.get(i);
                    if (strokeObject == null) {
                        continue;
                    }
                    drawSingleLineAnimate(canvas, left, top, right, bottom, strokeObject);
                }
            }
        }

        drawPerimeterLine(canvas, left, top, right, bottom);
    }

    /**
     * Draws the outer and inner boarder arcs of black to create a boarder
     */
    private void drawPerimeterLine(Canvas canvas, int left, int top, int right, int bottom) {
        //Base inner and outer rectanges for circles to be drawn
        left = (left - (widthOfLineStroke / 2));
        top = (top - (widthOfLineStroke / 2));
        right = (right + (widthOfLineStroke / 2));
        bottom = (bottom + (widthOfLineStroke / 2));

        //To allow the user to either allow or deny the ring to look pre 'filled-in'
        if (drawBoarderWithLine) {
            if (drawAsSingleLine && animateStrokes) {
                for (CustomStrokeObject strokeObject : this.singleStrokeObjects) {
                    if (strokeObject == null) {
                        continue;
                    }
                    try {
                        int xleft, xtop, xright, xbottom;
                        xleft = (int)(right * (strokeObject.percentToStartAt / 100));
                        if(xleft <= 0){
                            xleft = (left);
                        }
                        xright = (int)(xleft + (right * (strokeObject.percentOfLine / 100)));
                        xtop = (top);
                        //Put this line in if you want it to animate diagonally
                        //xbottom = (int)(bottom * (strokeObject.percentOfLine / 100));
                        xbottom = (bottom);
                        canvas.drawRect(xleft, xtop, xright, xbottom, perimeterPaint);

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
                        int xleft, xtop, xright, xbottom;
                        xleft = (int)(right * (strokeObject.percentToStartAt / 100));
                        if(xleft <= 0){
                            xleft = (left);
                        }
                        xright = (int)(xleft + (right * (strokeObject.percentOfLine / 100)));
                        xtop = (top);
                        //Put this line in if you want it to animate diagonally
                        //xbottom = (int)(bottom * (strokeObject.percentOfLine / 100));
                        xbottom = (bottom);
                        canvas.drawRect(xleft, xtop, xright, xbottom, perimeterPaint);

                    } catch (ConcurrentModificationException cme) {
                        continue;
                    }
                }
            }
        } else {
            canvas.drawRect(left, top, right, bottom, perimeterPaint);
        }

        this.bringToFront();
    }

    private void drawSingleLineAnimate(Canvas canvas, int left, int top, int right, int bottom,
                                       CustomStrokeObject strokeObject){
        Paint paint = strokeObject.paint;
        paint.setStrokeWidth(this.widthOfLineStroke);
        //Calculate edge padding for Cap.Round types
        boolean dontPadTheEdges = true;
        int sizeOfList = this.strokeObjects.size();
        try {
            int xleft, xtop, xright, xbottom;
            xleft = (int)(left + (right * (strokeObject.percentToStartAt / 100)));
            if(xleft <= 0){
                xleft = (left);
            }
            xright = (int)((xleft + (right * (strokeObject.percentOfLine / 100))) - left);
            xtop = (top);
            if(drawDiagonally){
                xbottom = (int)(sizeOfList * (bottom * (strokeObject.percentOfLine / 100)));
            } else {
                xbottom = (bottom);
            }
            int center = ((xtop + xbottom) / 2);

            if((paint.getStrokeCap() == Paint.Cap.ROUND)){
                //May need more logic here for determining types
                dontPadTheEdges = false;
            }
            if(roundCapPaddingAddition < 0 || roundCapPaddingAddition > 1){
                dontPadTheEdges = true;
            }
            if(!dontPadTheEdges){
                int numToAdjustBy = (int)(100 * (1-roundCapPaddingAddition));
                if(strokeObject.isLeft && strokeObject.isRight && strokeObject.isCenter){
                    //No padding needed as no crossover
                }
                if(strokeObject.isLeft && strokeObject.isCenter){
                    if(strokeObject.percentOfLine == 0){
                        xright = xleft;
                    } else if (strokeObject.percentOfLine < numToAdjustBy){
                        xright = xright + ((int)(xright * roundCapPaddingAddition));
                    }
                }
                if(strokeObject.isLeft && !strokeObject.isCenter){
                    if(strokeObject.percentOfLine == 0){
                        xright = xleft;
                    }
                }
                if(strokeObject.isRight && strokeObject.isCenter){
                    if(strokeObject.percentOfLine == 0){
                        xright = xleft;
                    } else if (strokeObject.percentOfLine < numToAdjustBy){
                        xleft = xleft - ((int)(xright * roundCapPaddingAddition));
                    }
                }
                if(strokeObject.isRight && !strokeObject.isCenter){
                    if(strokeObject.percentOfLine == 0){
                        xright = xleft;
                    }
                }
                if(strokeObject.isCenter && !strokeObject.isLeft && !strokeObject.isRight){
                    if(strokeObject.percentOfLine == 0){
                        xright = xleft;
                    } else if (strokeObject.percentOfLine < numToAdjustBy){
                        xleft = xleft - ((int)(xright * roundCapPaddingAddition));
                        xright = xright + ((int)(xright * roundCapPaddingAddition));
                    }
                }
            }

            canvas.drawLine(xleft, center, xright, center, paint);
        } catch (ConcurrentModificationException cme) {
            cme.printStackTrace();
        }
    }

    private void drawNotSingleLine(Canvas canvas, int left, int top, int right, int bottom,
                                   CustomStrokeObject strokeObject){
        int sizeOfList = this.strokeObjects.size();
        Paint paint = strokeObject.paint;
        paint.setStrokeWidth(this.widthOfLineStroke);
        //Calculate edge padding for Cap.Round types
        boolean dontPadTheEdges = true;
        try {
            int xleft, xtop, xright, xbottom;
            xleft = (int)(left + (right * (strokeObject.percentToStartAt / 100)));
            if(xleft <= 0){
                xleft = (left);
            }
            xright = (int)((xleft + (right * (strokeObject.percentOfLine / 100))) - left);
            xtop = (top);
            if(drawDiagonally){
                xbottom = (int)(sizeOfList * (bottom * (strokeObject.percentOfLine / 100)));
            } else {
                xbottom = (bottom);
            }
            int center = ((xtop + xbottom) / 2);

            if((paint.getStrokeCap() == Paint.Cap.ROUND)){
                //May need more logic here for determining types
                dontPadTheEdges = false;
            }
            if(roundCapPaddingAddition < 0 || roundCapPaddingAddition > 1){
                dontPadTheEdges = true;
            }
            if(!dontPadTheEdges){
                int numToAdjustBy = (int)(100 * (1-roundCapPaddingAddition));
                if(strokeObject.isLeft && strokeObject.isRight && strokeObject.isCenter){
                    //No padding needed as no crossover
                }
                if(strokeObject.isLeft && strokeObject.isCenter){
                    if(strokeObject.percentOfLine == 0){
                        xright = xleft;
                    } else if (strokeObject.percentOfLine < numToAdjustBy){
                        xright = xright + ((int)(xright * roundCapPaddingAddition));
                    }
                }
                if(strokeObject.isLeft && !strokeObject.isCenter){
                    if(strokeObject.percentOfLine == 0){
                        xright = xleft;
                    }
                }
                if(strokeObject.isRight && strokeObject.isCenter){
                    if(strokeObject.percentOfLine == 0){
                        xright = xleft;
                    } else if (strokeObject.percentOfLine < numToAdjustBy){
                        xleft = xleft - ((int)(xright * roundCapPaddingAddition));
                    }
                }
                if(strokeObject.isRight && !strokeObject.isCenter){
                    if(strokeObject.percentOfLine == 0){
                        xright = xleft;
                    }
                }
                if(strokeObject.isCenter && !strokeObject.isLeft && !strokeObject.isRight){
                    if(strokeObject.percentOfLine == 0){
                        xright = xleft;
                    } else if (strokeObject.percentOfLine < numToAdjustBy){
                        xleft = xleft - ((int)(xright * roundCapPaddingAddition));
                        xright = xright + ((int)(xright * roundCapPaddingAddition));
                    }
                }
            }

            canvas.drawLine(xleft, center, xright, center, paint);
        } catch (ConcurrentModificationException cme) {
            //
        }
    }

    /**
     * Setter method for setting the various strokes (line segments) on the line
     *
     * @param strokeObjects {@link CustomStrokeObject}
     */
    public void setLineStrokes(List<CustomStrokeObject> strokeObjects) {
        if (strokeObjects == null) {
            return;
        }
        if (strokeObjects.size() == 0) {
            return;
        }
        this.strokeObjects = new ArrayList<>();
        this.singleStrokeObjects = new ArrayList<>();
        this.strokeObjects = strokeObjects;
        if (animateStrokes) {
            animateLines();
        } else {
            invalidate();
        }
    }

    /**
     * Animate the drawing of the lines/ arcs on the screen
     */
    private void animateLines() {

        this.animatedStrokeObjects = this.strokeObjects;

        calculateNumAnimationRuns();
        if (drawAsSingleLine) {
            numberOfAnimationRuns = numberOfAnimationRuns * (this.animatedStrokeObjects.size());
        }
        long millisecondsPerRun;
        //Too quick at 10 milliseconds anyway, this is just to account for user error on param passing
        if (totalAnimationTime <= 10) {
            totalAnimationTime = MAX_DEFAULT_ANIMATION_TIME_IN_MILLISEC;
            millisecondsPerRun = (long) (MAX_DEFAULT_ANIMATION_TIME_IN_MILLISEC / numberOfAnimationRuns);
        } else {
            millisecondsPerRun = (long) (totalAnimationTime / numberOfAnimationRuns);
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
     *
     * @param millisUntilFinished
     */
    private void animateStrokePerMillisecond(long millisUntilFinished) {
        //Update per tick. This will update as per # of runs
        int listSize = animatedStrokeObjects.size();
        strokeObjects = new ArrayList<>();

        double percentComplete =
                (((double) totalAnimationTime - (double) millisUntilFinished) / (double) totalAnimationTime);

        if (drawAsSingleLine) {
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
            while (s == null) {
                if (x < 0) {
                    return;
                }
                try {
                    s = animatedStrokeObjects.get(x);
                } catch (IndexOutOfBoundsException iobe) {
                    x--;
                }
            }

            double z = (percentComplete % 100);
            while (z > 1) {
                z -= 1;
            }
            //As of now, Z represents the percent complete times the list size

            double f1 = (s.percentOfLine * (z));
            CustomStrokeObject sepObj = new CustomStrokeObject(s.paint, (float) f1,
                    s.percentToStartAt, s.colorOfLine);

            try {
                this.singleStrokeObjects.set(x, sepObj);
            } catch (IndexOutOfBoundsException iobe) {
                if (x == 0) {
                    this.singleStrokeObjects.add(sepObj);
                } else {
                    //Go back one, set to 100%, then add this one back in
                    try {
                        int y = (x - 1); //Do not want to alter x, using it after this
                        CustomStrokeObject s2 = animatedStrokeObjects.get(y);
                        CustomStrokeObject sepObj2 = new CustomStrokeObject(s2.paint,
                                s2.percentOfLine, s2.percentToStartAt, s2.colorOfLine);
                        this.singleStrokeObjects.set(y, sepObj2);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    this.singleStrokeObjects.add(sepObj);
                }
            }

        } else {
            for (CustomStrokeObject s : animatedStrokeObjects) {

                double f1 = (s.percentOfLine * percentComplete);

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
    private void calculateNumAnimationRuns() {
        switch (fps) {
            case FPS_1:
                numberOfAnimationRuns = 1 * ((double) totalAnimationTime / 1000);
                break;

            case FPS_5:
                numberOfAnimationRuns = 5 * ((double) totalAnimationTime / 1000);
                break;

            case FPS_10:
                numberOfAnimationRuns = 10 * ((double) totalAnimationTime / 1000);
                break;

            case FPS_15:
                numberOfAnimationRuns = 15 * ((double) totalAnimationTime / 1000);
                break;

            case FPS_30:
                numberOfAnimationRuns = 30 * ((double) totalAnimationTime / 1000);
                break;

            case FPS_60:
                numberOfAnimationRuns = 60 * ((double) totalAnimationTime / 1000);
                break;

            case FPS_90:
                numberOfAnimationRuns = 90 * ((double) totalAnimationTime / 1000);
                break;

            case FPS_120:
                numberOfAnimationRuns = 120 * ((double) totalAnimationTime / 1000);
                break;

            case FPS_240:
                numberOfAnimationRuns = 240 * ((double) totalAnimationTime / 1000);
                break;

        }
    }

    /**
     * Class used in drawing Various line segments of the main line
     */
    public static class CustomStrokeObject {

        float percentOfLine;
        float percentToStartAt;
        Integer colorOfLine;
        boolean isLeft, isRight, isCenter;
        Paint paint;


        /**
         * Constructor. I am adding in a very tiny amount of overlap (a couple pixels) so that
         * there will not be a gap between the arcs because the whitespace gap of a couple pixels
         * does not look very good. To remove this, just remove the -.1 and .1 to startAt and the line
         *
         * @param percentOfLine  Percent of the line to fill.
         *                       NOTE! THIS IS BASED OFF OF 100!
         *                       IE, passing 35.5 would mean 35.50%
         * @param percentToStartAt Percent to start at (for filling multiple colors).
         *                         NOTE! THIS IS BASED OFF OF 100%!
         *                         IE, passing 35.5 would mean 35.50%
         * @param colorOfLine      Int color of the line to use
         */
        public CustomStrokeObject(float percentOfLine, float percentToStartAt, Integer colorOfLine) {
            this.percentOfLine = percentOfLine;
            this.percentToStartAt = percentToStartAt;
            this.colorOfLine = colorOfLine;
            if (this.percentOfLine < 0 || this.percentOfLine > 100) {
                this.percentOfLine = 100; //Default to 100%
            }
            if(this.percentToStartAt < 0 || this.percentToStartAt > 100){
                this.percentToStartAt = 0; //Defaults to left side (0%);
            }
            if (this.colorOfLine == null) {
                this.colorOfLine = Color.parseColor("#00000000"); //Default to Transparent
            }
            if(this.percentOfLine == 100){
                isLeft = isCenter = isRight = true;
            } else {
                if(percentToStartAt == 0){
                    isLeft = true;
                    isRight = isCenter = false;
                }
                if(percentToStartAt > 0 && percentToStartAt < 100){
                    isCenter = true;
                    isRight = false;
                }
                if((percentToStartAt + percentOfLine) == 100){
                    isRight = true;
                }
            }
            this.paint = new Paint();
            this.paint.setColor(colorOfLine);
            this.paint.setAntiAlias(true);
            this.paint.setStyle(Paint.Style.FILL_AND_STROKE);
            //If you want to change this, use one of the overloaded constructors or setPaint()
            this.paint.setStrokeCap(Paint.Cap.ROUND);
        }

        /**
         * Overloaded method. Allows for items to be passed in that already have calculations
         * done for conversion.
         */
        public CustomStrokeObject(Paint paint, float percentOfLine,
                                  float percentToStartAt, Integer colorOfLine) {
            this.percentOfLine = percentOfLine;
            this.percentToStartAt = percentToStartAt;
            this.colorOfLine = colorOfLine;
            if (this.colorOfLine == null) {
                this.colorOfLine = Color.parseColor("#00000000"); //Default to transparent
            }
            if(this.percentOfLine == 100){
                isLeft = isCenter = isRight = true;
            } else {
                if(percentToStartAt == 0){
                    isLeft = true;
                    isRight = isCenter = false;
                }
                if(percentToStartAt > 0 && percentToStartAt < 100){
                    isCenter = true;
                    isRight = false;
                }
                if((percentToStartAt + percentOfLine) == 100){
                    isRight = true;
                }
            }
            this.paint = paint;
        }

        /**
         * Custom overloaded constructor. Allows for duplicating objects within foreach loops
         *
         * @param obj
         */
        public CustomStrokeObject(CustomStrokeObject obj) {
            this.percentOfLine = obj.percentOfLine;
            this.percentToStartAt = obj.percentToStartAt;
            this.colorOfLine = obj.colorOfLine;
            this.paint = obj.paint;
            this.isCenter = obj.isCenter;
            this.isLeft = obj.isLeft;
            this.isRight = obj.isRight;
        }

        /**
         * Overloaded setter, in case you want to set a custom paint object here
         *
         * @param paint Paint object to overwrite one set by constructor
         */
        public void setPaint(Paint paint) {
            this.paint = paint;
        }

        /**
         * Getter for Paint. Allows editing of the paint object for setting custom prefs
         *
         * @return Paint
         */
        public Paint getPaint() {
            return this.paint;
        }
    }

}