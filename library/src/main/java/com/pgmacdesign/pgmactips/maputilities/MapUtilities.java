package com.pgmacdesign.pgmactips.maputilities;

import android.graphics.Point;
import android.support.annotation.NonNull;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.pgmacdesign.pgmactips.utilities.DisplayManagerUtilities;

/**
 * Created by pmacdowell on 2017-11-21.
 */

public class MapUtilities {

    final static int GLOBE_WIDTH = 256; // a constant in Google's map projection
    final static int ZOOM_MAX = 21;

    public static LatLngBounds calculateBounds(@NonNull LatLng center, double radius) {
        return new LatLngBounds.Builder().
                include(SphericalUtils.computeOffset(center, radius, 0)).
                include(SphericalUtils.computeOffset(center, radius, 90)).
                include(SphericalUtils.computeOffset(center, radius, 180)).
                include(SphericalUtils.computeOffset(center, radius, 270)).build();
    }

    public static CameraUpdate calculateZoomLevel(@NonNull LatLng latLng, float radiusInMeters,
                                                   @NonNull DisplayManagerUtilities dmu){
        if(radiusInMeters < 0){
            radiusInMeters = 10;
        }
        int width, height;
        Point point = dmu.getAppUsableScreenSize();
        if(point != null) {
            width = point.x;
            height = point.y;
        } else {
            width = dmu.getPixelsWidth();
            height = dmu.getPixelsHeight();
        }

        LatLngBounds latLngBounds = calculateBounds(latLng, radiusInMeters);
        return CameraUpdateFactory.newLatLngBounds(latLngBounds, width, height, 0);
    }

//    public static float radiusToZoom(float radius){
//        float x = (Math.round(14-(Math.log(radius)/Math.L)))
//    }

    public static float getMetersPerPixel(LatLng latLng, float zoom){
        float metersPerPx = (float)(156543.03392 * Math.cos(
                latLng.latitude * Math.PI / 180) / Math.pow(2, zoom));
        return metersPerPx;
    }

    public static int calculateZoomLevel(int screenWidthPixels) {
        double equatorLength = 40075004; // in meters
        double widthInPixels = screenWidthPixels;
        double metersPerPixel = equatorLength / 256;
        int zoomLevel = 1;
        while ((metersPerPixel * widthInPixels) > 2000) {
            metersPerPixel /= 2;
            ++zoomLevel;
        }
        return zoomLevel;
    }


    public static int getBoundsZoomLevel(LatLng northeast,LatLng southwest,
                                         int width, int height) {
        double latFraction = (latRad(northeast.latitude) - latRad(southwest.latitude)) / Math.PI;
        double lngDiff = northeast.longitude - southwest.longitude;
        double lngFraction = ((lngDiff < 0) ? (lngDiff + 360) : lngDiff) / 360;
        double latZoom = zoom(height, GLOBE_WIDTH, latFraction);
        double lngZoom = zoom(width, GLOBE_WIDTH, lngFraction);
        double zoom = Math.min(Math.min(latZoom, lngZoom),ZOOM_MAX);
        return (int)(zoom);
    }

    private static double latRad(double lat) {
        double sin = Math.sin(lat * Math.PI / 180);
        double radX2 = Math.log((1 + sin) / (1 - sin)) / 2;
        return Math.max(Math.min(radX2, Math.PI), -Math.PI) / 2;
    }

    private static double zoom(double widthOrHeightInPixels,
                               double globeWidth,
                               double latOrLngFraction) {
        final double LN2 = .693147180559945309417;
        return (Math.log(widthOrHeightInPixels / globeWidth / latOrLngFraction) / LN2);
    }

    public static LatLng getScreenLocation(@NonNull GoogleMap map, @NonNull Point point){
        return map.getProjection().fromScreenLocation(point);
    }

}
