package com.pgmacdesign.pgmactips.transformations;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import com.squareup.picasso.Transformation;

/**
 * Created by pmacdowell on 2017-11-13.
 */

public class CircleTransformV2 implements Transformation {

    private Integer circleFrameColor, circleFrameWidth, areaMargin;

    /**
     * Simple circlular transform
     */
    public CircleTransformV2(){
        this.circleFrameColor = null;
        this.circleFrameWidth = null;
        this.areaMargin = null;
    }

    /**
     * Overloaded constructor to add a color frame around the image
     * @param circleFrameColor Color to use (IE, ContextCompat.getColor(context, R.color.Red);
     *                         If null, no circular frame boarder will be drawn.
     * @param circleFrameWidth Width of the frame to use. If null, will default to 2 (pixels)
     */
    public CircleTransformV2(Integer circleFrameColor, Integer circleFrameWidth){
        this.circleFrameColor = circleFrameColor;
        this.circleFrameWidth = circleFrameWidth;
        this.areaMargin = null;
    }

    /**
     * Overloaded constructor to add a color frame around the image
     * @param circleFrameColor Color to use (IE, ContextCompat.getColor(context, R.color.Red);
     *                         If null, no circular frame boarder will be drawn.
     * @param circleFrameWidth Width of the frame to use. If null, will default to 2 (pixels)
     */
    public CircleTransformV2(Integer circleFrameColor, Integer circleFrameWidth, Integer margin){
        this.circleFrameColor = circleFrameColor;
        this.circleFrameWidth = circleFrameWidth;
        this.areaMargin = margin;
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
            int margin = 0;
            if(areaMargin != null){
                margin = areaMargin;
            }
            bitmap = Bitmap.createBitmap(size, size, config);
            Canvas canvas = new Canvas(bitmap);
            Paint paint = new Paint();
            BitmapShader shader = new BitmapShader(squaredBitmap,
                    BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
            paint.setShader(shader);
            paint.setAntiAlias(true);
            float r = size/2f;
            //canvas.drawCircle(r, r, r, paint);
            canvas.drawRoundRect(new RectF(margin, margin,
                    source.getWidth() - margin,
                    source.getHeight() - margin),
                    r, r, paint);

            if (source != bitmap) {
                source.recycle();
            }
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
                canvas.drawCircle((source.getWidth() - margin)/2,
                        (source.getHeight() - margin)/2,
                        r-2, paint2);

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
