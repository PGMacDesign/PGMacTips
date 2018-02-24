package com.pgmacdesign.pgmactips.locationutilities;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;

import com.pgmacdesign.pgmactips.utilities.PermissionUtilities;

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

    public static boolean hasLocationReadyToGo(Activity activity, Context context){
        if(context == null || activity == null){
            return false;
        }
        if(ContextCompat.checkSelfPermission(activity,
                PermissionUtilities.permissionsEnum.ACCESS_FINE_LOCATION
                        .getPermissionManifestName()) != PackageManager.PERMISSION_GRANTED){
            return false;
        }
        LocationManager locationManager = (LocationManager) context
                .getSystemService(Context.LOCATION_SERVICE);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            try {
                int locationMode = Settings.Secure.getInt(context.getContentResolver(),
                        Settings.Secure.LOCATION_MODE);
                if(locationMode == Settings.Secure.LOCATION_MODE_OFF){
                    return false;
                }
            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
                return false;
            }
        } else {
            try {
                String locationProviders = Settings.Secure.getString(
                        context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
                if(!locationManager.isProviderEnabled(locationProviders)){
                    return false;
                }
            } catch (Exception e){
                e.printStackTrace();
            }
        }
        try {
            boolean gps_enabled = false;
            boolean network_enabled = false;

            try {
                gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            } catch(Exception ex) {}

            try {
                network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            } catch(Exception ex) {}
            if(!gps_enabled && !network_enabled){
                return false;
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return true;
    }
}
