package com.pgmacdesign.pgmactips.customui;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Movie;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Build;
import android.os.SystemClock;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.pgmacdesign.pgmactips.adaptersandlisteners.OnTaskCompleteListener;
import com.pgmacdesign.pgmactips.utilities.ViewUtilities;

import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Thanks for the tutorial Jijith-- http://www.mavengang.com/2016/05/02/gif-animation-android/
 * Created by pmacdowell on 2017-07-07.
 * NOTE! In order for this class to be used, you must include this tag in your manifest
 * under the application tag:
 *         <application
 *             .
 *             .
 *             android:hardwareAccelerated="false" >
 */
public class GIFLoadingView extends View {

    private static final String NOT_A_GIF = "The resource you passed was not found or not useable. Please check your resource to make sure it is a GIF.";
    private static final String NOT_A_URI = "The uri / path you passed was not found or not useable. Please check your string path to make sure it is a GIF.";
    private static final String TAG = "GIFLoadingView";
    private InputStream mInputStream;
    private Movie mMovie;
    private int mWidth, mHeight, viewWidth, viewHeight;
    float widthScale, heightScale, translateWidth, translateHeight;
    private long mStart;
    private Context mContext;
    private boolean viewHasBeenLoaded, viewStartedLoading;
    private Paint paint;

    public GIFLoadingView(Context context) {
        super(context);
        this.mContext = context;
        this.paint = null;
        this.viewHasBeenLoaded = this.viewStartedLoading = false;
        init();
    }

    public GIFLoadingView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        this.mContext = context;
        this.paint = null;
        this.viewHasBeenLoaded = this.viewStartedLoading = false;
        init();
    }

    public GIFLoadingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        this.paint = null;
        this.viewHasBeenLoaded = this.viewStartedLoading = false;
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public GIFLoadingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.mContext = context;
        this.paint = null;
        this.viewHasBeenLoaded = this.viewStartedLoading = false;
        init();
    }

    /**
     * Init method, this should be called after constructors and also after view updates to
     * allow the GIF to be resized properly
     */
    private void init() {
        if(paint == null){
            Paint p = new Paint();
            p.setAntiAlias(true);
            p.setColor(Color.TRANSPARENT);
        }
        setFocusable(true);
        if(mInputStream == null){
            return;
        }
        if(!this.viewHasBeenLoaded){
            if(!this.viewStartedLoading){
                this.viewStartedLoading = true;
                ViewUtilities.getDrawnView(
                        new OnTaskCompleteListener() {
                            @Override
                            public void onTaskComplete(Object result, int customTag) {
                                try {
                                    View v = (View) result;
                                    viewHeight = v.getHeight();
                                    viewWidth = v.getWidth();
                                } catch (Exception e){
                                    e.printStackTrace();
                                }
                                viewHasBeenLoaded = true;
                                init();
                            }
                        }, this);
            }
            return;
        }
        mMovie = Movie.decodeStream(mInputStream);
        if(mMovie == null){
            return;
        }

        if(!(viewHeight == 0 || viewWidth == 0)) {
            ViewUtilities.ResizingViewObject obj = ViewUtilities.scaleGIFTo(
                    mMovie.width(), mMovie.height(), viewWidth,
                    viewHeight, true);
            if (obj != null) {
                mWidth = (int) obj.getNewWidth();
                mHeight = (int) obj.getNewHeight();
                widthScale = obj.getWidthScaleMultiplier();
                heightScale = obj.getHeightScaleMultiplier();
                translateWidth = obj.getWidthTranslatePixels();
                translateHeight = obj.getHeightTranslatePixels();
            } else {
                mWidth = mMovie.width();
                mHeight = mMovie.height();
            }
        } else {
            mWidth = mMovie.width();
            mHeight = mMovie.height();
        }

        setMeasuredDimension(mWidth, mHeight);
        requestLayout();
    }

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
                            (translateWidth),
                            (translateHeight));
                }
            }
            mMovie.draw(canvas, 0, 0, paint);
            invalidate();
        }
    }

    /**
     * Set the GIF Image Resource.
     * @param id int ID of the resource. Sample: 'R.drawable.my_gif'
     */
    public void setGifImageResource(int id) {
        try {
            mInputStream = mContext.getResources().openRawResource(id);
            init();
        } catch (Resources.NotFoundException rnf){
            Log.d(TAG, NOT_A_GIF);
        }
    }

    /**
     * Set the GIF Image path / URI.
     * @param uri Uri / String path to the GIF that will be used upon inflation.
     */
    public void setGifImageUri(Uri uri) {
        try {
            mInputStream = mContext.getContentResolver().openInputStream(uri);
            init();
        } catch (FileNotFoundException e) {
            Log.d(TAG, NOT_A_URI);
        } catch (NullPointerException npe){
            npe.printStackTrace();
        }
    }
}
