package com.pgmacdesign.pgmacutilities.locationutilities;

import android.location.Location;

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

}
