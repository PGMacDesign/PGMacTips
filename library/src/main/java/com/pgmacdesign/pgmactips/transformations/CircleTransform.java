package com.pgmacdesign.pgmactips.transformations;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.squareup.picasso.Transformation;

/**
 * Image boarder color frame Not working at the moment :/
 * From: https://stackoverflow.com/a/27236821/2480714
 * Created by pmacdowell on 8/15/2016.
 */
public class CircleTransform implements Transformation {

    private Integer circleFrameColor, circleFrameWidth;

    /**
     * Simple circlular transform
     */
    public CircleTransform(){
        this.circleFrameColor = null;
        this.circleFrameWidth = null;
    }

    /**
     * Overloaded constructor to add a color frame around the image
     * @param circleFrameColor Color to use (IE, ContextCompat.getColor(context, R.color.Red);
     *                         If null, no circular frame boarder will be drawn.
     * @param circleFrameWidth Width of the frame to use. If null, will default to 2 (pixels)
     */
    public CircleTransform(Integer circleFrameColor, Integer circleFrameWidth){
        this.circleFrameColor = circleFrameColor;
        this.circleFrameWidth = circleFrameWidth;
    }

    @Override
    public Bitmap transform(Bitmap source) {
        int size = Math.min(source.getWidth(), source.getHeight());

        int x = (source.getWidth() - size) / 2;
        int y = (source.getHeight() - size) / 2;

        Bitmap squaredBitmap = Bitmap.createBitmap(source, x, y, size, size);
        if (squaredBitmap != source) {
            source.recycle();
        }

        Bitmap.Config config = (source.getConfig() != null)
                ? source.getConfig() : Bitmap.Config.ARGB_8888;
        Bitmap bitmap = null;

        try {
            bitmap = Bitmap.createBitmap(size, size, config);
            Canvas canvas = new Canvas(bitmap);
            Paint paint = new Paint();
            BitmapShader shader = new BitmapShader(squaredBitmap,
                    BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
            paint.setShader(shader);
            paint.setAntiAlias(true);
            float r = size/2f;
            canvas.drawCircle(r, r, r, paint);
            squaredBitmap.recycle();
            if(this.circleFrameColor != null){
                Paint paint2 = new Paint();
                paint2.setColor(circleFrameColor);
                paint2.setStyle(Paint.Style.STROKE);
                if(circleFrameWidth == null){
                    this.circleFrameWidth = 2;
                }
                if(this.circleFrameWidth < 1){
                    this.circleFrameWidth = 2;
                }
                paint2.setStrokeWidth(this.circleFrameWidth);
                paint2.setAntiAlias(true);
                canvas.drawCircle((source.getWidth() + circleFrameWidth)/2,
                        (source.getHeight() + circleFrameWidth)/2,
                        circleFrameWidth, paint2);
            }

            return bitmap;
        } catch (Exception e){
            e.printStackTrace();
        }
        return source;
    }

    @Override
    public String key() {
        return "circle";
    }
}