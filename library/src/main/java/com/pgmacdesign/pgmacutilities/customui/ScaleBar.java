package com.pgmacdesign.pgmacutilities.customui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.location.Location;
import android.widget.ImageView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.LatLng;
import com.pgmacdesign.pgmacutilities.utilities.DisplayManagerUtilities;
import com.pgmacdesign.pgmacutilities.utilities.NumberUtilities;
import com.pgmacdesign.pgmacutilities.utilities.StringUtilities;

/**
 * Created by pmacdowell on 2017-02-21.
 * From: http://stackoverflow.com/a/29076258/2480714
 */
@SuppressLint("AppCompatCustomView")
public class ScaleBar extends ImageView { //public class ScaleBar extends ImageView { android.support.v7.widget.AppCompatImageView {
	private float mXOffset = 10;
    private float mYOffset = 10;
    private float mLineWidth = 3;

    private boolean mIsImperial = true;
    private boolean mIsNautical = false;
    private boolean mIsLatitudeBar = true;

    private boolean mIsLongitudeBar = true;

    private static final int TEXT_SIZE_BASE = 16;

    private GoogleMap mMap;

    private float mXdpi;
    private float mYdpi;

    private boolean drawLine, drawNumbers;

    private Canvas currentCanvas;

    private static final int NUM_DIGITS_TO_ROUND = 0;
    public static final String SCALEBAR_LOCATION_PART_1 = "ScaleBar location p1";
    public static final String SCALEBAR_LOCATION_PART_2 = "ScaleBar location p2";

    private DisplayManagerUtilities dmu;

    public ScaleBar(Context context, GoogleMap map) {
        super(context);
        this.dmu = new DisplayManagerUtilities(context);
        this.mMap = map;

        this.mXdpi = dmu.getXdpi();
        this.mYdpi = dmu.getYdpi();
    }

    @Override
    public void onDraw(Canvas canvas) {
        this.drawLine = this.drawNumbers = true;
        startDrawProcess(canvas);
    }

    private void startDrawProcess(Canvas canvas){
        canvas.save();
        drawScaleBarPicture(canvas);
        canvas.restore();
    }
    private void drawScaleBarPicture(Canvas canvas) {
        // We want the scale bar to be as long as the closest round-number miles/kilometers
        // to 1-inch at the latitude at the current center of the screen.

        Projection projection = mMap.getProjection();

        if (projection == null) {
            return;
        }

        final Paint barPaint = new Paint();
        barPaint.setColor(Color.BLACK);
        barPaint.setAntiAlias(true);
        barPaint.setStrokeWidth(mLineWidth);

        final Paint textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(dmu.getScalablePixelTextSize(TEXT_SIZE_BASE));

        drawXMetric(canvas, textPaint, barPaint);

        drawYMetric(canvas, textPaint, barPaint);
    }

   private void drawXMetric(Canvas canvas, Paint textPaint, Paint barPaint) {
        Projection projection = mMap.getProjection();


        if (projection != null) {
            int point1, point2;
            point1 = (int) (((getWidth() / 2) - (mXdpi / 2)));
            point2 =  (int) (getHeight() / 2);
            LatLng p1 = projection.fromScreenLocation(
                    new Point(point1, point2));

            int point3, point4;
            point3 = (int) (((getWidth() / 2) + (mXdpi / 2)));
            point4 =  (int) (getHeight() / 2);
            LatLng p2 = projection.fromScreenLocation(
                    new Point(point3, point4));

            Location locationP1 = new Location(SCALEBAR_LOCATION_PART_1);
            Location locationP2 = new Location(SCALEBAR_LOCATION_PART_2);

            locationP1.setLatitude(p1.latitude);
            locationP2.setLatitude(p2.latitude);
            locationP1.setLongitude(p1.longitude);
            locationP2.setLongitude(p2.longitude);

            float xMetersPerInch = locationP1.distanceTo(locationP2);

            if (mIsLatitudeBar) {
                String xMsg = scaleBarLengthText(xMetersPerInch, mIsImperial, mIsNautical);
                Rect xTextRect = new Rect();
                textPaint.getTextBounds(xMsg, 0, xMsg.length(), xTextRect);

                int textSpacing = (int) (xTextRect.height() / 5.0);

                if(drawLine) {
                    canvas.drawRect(mXOffset, mYOffset, mXOffset + mXdpi, mYOffset + mLineWidth, barPaint);
                    canvas.drawRect(mXOffset + mXdpi, mYOffset, mXOffset + mXdpi + mLineWidth, mYOffset +
                            xTextRect.height() + mLineWidth + textSpacing, barPaint);
                }

                if (!mIsLongitudeBar) {
                    if(drawLine) {
                        canvas.drawRect(mXOffset, mYOffset, mXOffset + mLineWidth, mYOffset +
                                xTextRect.height() + mLineWidth + textSpacing, barPaint);
                    }
                }
                if(drawNumbers) {
                    canvas.drawText(xMsg, (mXOffset + mXdpi / 2 - xTextRect.width() / 2),
                            (mYOffset + xTextRect.height() + mLineWidth + textSpacing), textPaint);
                }
            }
        }
    }

    private void drawYMetric(Canvas canvas, Paint textPaint, Paint barPaint) {
        Projection projection = mMap.getProjection();

        if (projection != null) {
            Location locationP1 = new Location(SCALEBAR_LOCATION_PART_1);
            Location locationP2 = new Location(SCALEBAR_LOCATION_PART_2);

            LatLng p1 = projection.fromScreenLocation(new Point(getWidth() / 2,
                    (int) ((getHeight() / 2) - (mYdpi / 2))));
            LatLng p2 = projection.fromScreenLocation(new Point(getWidth() / 2,
                    (int) ((getHeight() / 2) + (mYdpi / 2))));

            locationP1.setLatitude(p1.latitude);
            locationP2.setLatitude(p2.latitude);
            locationP1.setLongitude(p1.longitude);
            locationP2.setLongitude(p2.longitude);

            float yMetersPerInch = locationP1.distanceTo(locationP2);

            if (mIsLongitudeBar) {
                String yMsg = scaleBarLengthText(yMetersPerInch, mIsImperial, mIsNautical);
                Rect yTextRect = new Rect();
                textPaint.getTextBounds(yMsg, 0, yMsg.length(), yTextRect);

                int textSpacing = (int) (yTextRect.height() / 5.0);

                if(drawLine) {
                    canvas.drawRect(mXOffset, mYOffset, mXOffset + mLineWidth, mYOffset + mYdpi, barPaint);
                    canvas.drawRect(mXOffset, mYOffset + mYdpi, mXOffset + yTextRect.height() +
                            mLineWidth + textSpacing, mYOffset + mYdpi + mLineWidth, barPaint);
                }
                if (!mIsLatitudeBar) {
                    if(drawLine) {
                        canvas.drawRect(mXOffset, mYOffset, mXOffset + yTextRect.height() +
                                mLineWidth + textSpacing, mYOffset + mLineWidth, barPaint);
                    }
                }

                float x = mXOffset + yTextRect.height() + mLineWidth + textSpacing;
                float y = mYOffset + mYdpi / 2 + yTextRect.width() / 2;

                if(drawNumbers) {
                    canvas.rotate(-90, x, y);
                    canvas.drawText(yMsg, x, y + textSpacing, textPaint);
                }
            }
        }
    }

    public void invalidateNumbersOnly(){
        this.drawLine = false;
        this.drawNumbers = true;
        startDrawProcess(this.currentCanvas);
    }

    private float adjustNumbersForReadability(float distance){
        if(distance > 1) {
            distance = (float) NumberUtilities.round(distance, 2);
            return distance;
        } else {
            String str = null;
            str = distance + "";
            if (!StringUtilities.isNullOrEmpty(str)) {
                str = str.trim();
                if (str.length() > 4) {
                    str = str.substring(0, 4);
                }
                if (str.startsWith("0.")) {
                    str = str.replace("0.", ".");
                }
            }
            try {
                return Float.parseFloat(str);
            } catch (Exception e){
                e.printStackTrace();
                return distance;
            }
        }
        //return distance;
    }

    private String scaleBarLengthText(float meters, boolean imperial, boolean nautical) {
        if (this.mIsImperial) {
            if (meters >= 1609.344) {
                float x = (float) (meters / 1609.344);
                x = adjustNumbersForReadability(x);
                return x + "mi";
            } else if (meters >= 1609.344/10) {
                float x = (float) ((meters / 1609.344)/10);
                x = adjustNumbersForReadability(x);
                return x + "mi";
            } else {
                float x = (float) (meters * 3.2808399);
                x = adjustNumbersForReadability(x);
                return x + "ft";
            }
        } else if (this.mIsNautical) {
            if (meters >= 1852) {
                float x = (float) (meters / 1852);
                x = adjustNumbersForReadability(x);
                return x + "nm";
            } else if (meters >= 1852/10) {
                float x = (float) (((meters / 185.2)) / 10.0);
                x = adjustNumbersForReadability(x);
                return x + "nm";
            } else {
                float x = (float) (meters * 3.2808399);
                x = adjustNumbersForReadability(x);
                return x + "ft";
            }
        } else {
            if (meters >= 1000) {
                float x = (float) (meters / 1000);
                x = adjustNumbersForReadability(x);
                return x + "km";
            } else if (meters > 100) {
                float x = (float) ((meters / 100.0) / 10.0);
                x = adjustNumbersForReadability(x);
                return x + "km";
            } else {
                float x = (float) (meters);
                x = adjustNumbersForReadability(x);
                return x + "m";
            }
        }
    }


}