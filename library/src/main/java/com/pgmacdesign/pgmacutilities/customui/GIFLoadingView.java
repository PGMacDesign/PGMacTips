package com.pgmacdesign.pgmacutilities.customui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Movie;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Build;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.pgmacdesign.pgmacutilities.utilities.DisplayManagerUtilities;
import com.pgmacdesign.pgmacutilities.utilities.L;
import com.pgmacdesign.pgmacutilities.utilities.ViewUtilities;

import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Thanks for the tutorial Jijith-- http://www.mavengang.com/2016/05/02/gif-animation-android/
 * Created by pmacdowell on 2017-07-07.
 */

public class GIFLoadingView extends View {

    private static final String STRING_BACKGROUND = "background";
    private InputStream mInputStream;
    private Movie mMovie;
    private int mWidth, mHeight, viewWidth, viewHeight, customTranslateWidth, customTranslateHeight;
    float widthScale, heightScale, translateWidth, translateHeight;
    private long mStart;
    private Context mContext;
    private Paint paint;
    private DisplayManagerUtilities dmu;

    public GIFLoadingView(Context context) {
        super(context);
        this.mContext = context;
        init();
    }

    public GIFLoadingView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        this.mContext = context;
        init();
    }

    public GIFLoadingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public GIFLoadingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.mContext = context;
        init();
    }

    private void init() {
        if(paint == null){
            Paint p = new Paint();
            p.setAntiAlias(true);
            p.setColor(Color.TRANSPARENT);
        }
        if(mContext != null){
            this.dmu = new DisplayManagerUtilities(mContext);
        }
        setFocusable(true);
        if(mInputStream == null){
            return;
        }
        mMovie = Movie.decodeStream(mInputStream);
        if(mMovie == null){
            return;
        }

        if(dmu != null) {
            viewHeight = dmu.getPixelsHeight();
            viewWidth = dmu.getPixelsWidth();

            ViewUtilities.PGMViewObject obj = ViewUtilities.scaleGIFTo(
                    mMovie.width(), mMovie.height(), viewWidth,
                    viewHeight, true);
            if (obj != null) {
                mWidth = (int) obj.getNewWidth();
                mHeight = (int) obj.getNewHeight();
                L.m("mWidth == " + mWidth);
                L.m("mHeight == " + mHeight);
                widthScale = obj.getWidthScaleMultiplier();
                heightScale = obj.getHeightScaleMultiplier();
                L.m("widthScale == " + widthScale);
                L.m("heightScale == " + heightScale);

                translateWidth = obj.getWidthTranslatePixels();
                L.m("translate Width == " + translateWidth);
                translateHeight = obj.getHeightTranslatePixels();
                L.m("translate Height == " + translateHeight);

                L.m("viewWidth == " + viewWidth);
                L.m("viewHeight == " + viewHeight);

                L.m("mMovie.height() == " + mMovie.height());
                L.m("mMovie.width() == " + mMovie.width());

                //TEST
                //translateWidth = 0;//-20;
                //translateWidth = -20;
                //L.m("viewWidth / widthScale == " + ((float)viewWidth / widthScale));
                //L.m("(float)mMovie.width())/2f == " + ((float)mMovie.width())/2f);
                //translateWidth = ((float)viewWidth / widthScale - (float)mMovie.width())/2f;

                L.m("translateWidth == " + translateWidth);

                //TEST
                //translateHeight = 0;//50;
                //translateHeight = (float)65.7873416;
                //translateHeight = ((float)viewHeight / heightScale - ((float)mMovie.height())/2f);
                L.m("translateHeight == " + translateHeight);

                Point p = dmu.getCenterXYCoordinates();
                L.m("CENTER COORDINATES OF SCREEN == " + p.x + "," + p.y);


            } else {
                mWidth = mMovie.width();
                mHeight = mMovie.height();
            }
        } else {
            mWidth = mMovie.width();
            mHeight = mMovie.height();
        }
        //requestLayout();
        //setMeasuredDimension(mWidth, mHeight);
    }

    /*
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasur2edDimension(mWidth, mHeight);
    }
    */

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        long now = SystemClock.uptimeMillis();

        if (mStart == 0) {
            mStart = now;
        }

        if (mMovie != null) {

            int duration = mMovie.duration();
            if (duration == 0) {
                duration = 1000;
            }

            int relTime = (int) ((now - mStart) % duration);

            mMovie.setTime(relTime);

            if(widthScale != 0 && heightScale != 0){
                canvas.scale(widthScale, heightScale);
                if(translateWidth == 0 && translateHeight == 0){
                    //No need to translate
                } else {
                    canvas.translate(
                            (translateWidth + customTranslateWidth),
                            (translateHeight + customTranslateHeight));
                }
            }
            mMovie.draw(canvas, 0, 0, paint);
            invalidate();
        }
    }



    public void setGifImageResource(int id) {
        mInputStream = mContext.getResources().openRawResource(id);
        init();
    }

    public void setGifImageUri(Uri uri) {
        try {
            mInputStream = mContext.getContentResolver().openInputStream(uri);
            init();
        } catch (FileNotFoundException e) {
            Log.e("GIFLoadingView", "File not found");
        }
    }
}
