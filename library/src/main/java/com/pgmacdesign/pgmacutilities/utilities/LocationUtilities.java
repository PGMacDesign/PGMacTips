package com.pgmacdesign.pgmacutilities.utilities;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;

/**
 * This class works with the location API to
 * Created by pmacdowell on 2017-01-18.
 */
public class LocationUtilities {

    /**
     * Calculate distance between 2 points
     * @param latitudeStart latitude start (in double)
     * @param longitudeStart longitude start (in double)
     * @param latitudeEnd latitude end (in double)
     * @param longitudeEnd longitude end (in double)
     * @return float (in meters) of distance between points.
     *         {@link Location#distanceTo(Location)}
     */
    public static float calculateDistance(double latitudeStart, double longitudeStart,
                                          double latitudeEnd, double longitudeEnd){
        Location loc1 = new Location("A");
        Location loc2 = new Location("B");
        loc1.setLatitude(latitudeStart);
        loc1.setLongitude(longitudeStart);
        loc2.setLatitude(latitudeEnd);
        loc2.setLongitude(longitudeEnd);
        return calculateDistance(loc1, loc2);
    }

    /**
     * Calculate distance between 2 points
     * @param start Start {@link Location}
     * @param end End {@link Location}
     * @return float (in meters) of distance between points.
     *         {@link Location#distanceTo(Location)}
     */
    public static float calculateDistance(Location start, Location end){
        if(start == null || end == null){
            return 0;
        }
        float distance = start.distanceTo(end);
        return distance;
    }

    /**
     * Listener to start listening for location updates. This assumes that permission has already
     * been granted. Make sure to request it before making this static call
     * @param context Context
     * @param listener Listener to send data back on {@link android.location.LocationListener}
     */
    public static void startListeningForLocation(Context context, long timeGapBetweenUpdates,
                                                 float minimumDistanceBetweenUpdates,
                                                 android.location.LocationListener listener){

        LocationManager locationManager = (LocationManager) context
                .getSystemService(Context.LOCATION_SERVICE);
        try {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                    timeGapBetweenUpdates, minimumDistanceBetweenUpdates, listener);
        } catch (SecurityException se){
            se.printStackTrace();
            // TODO: 2017-07-19 update here
        }
    }
}
