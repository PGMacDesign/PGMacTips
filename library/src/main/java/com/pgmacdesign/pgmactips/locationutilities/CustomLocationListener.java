package com.pgmacdesign.pgmactips.locationutilities;

import android.Manifest;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresPermission;

import com.pgmacdesign.pgmactips.utilities.L;

/**
 * Location Listener for use in obtaining {@link Location}
 * Created by pmacdowell on 2017-10-16.
 */
public class CustomLocationListener implements LocationListener {

    private Context context;

    // flag for GPS status
    private boolean isGPSEnabled = false;
    // flag for network status
    private boolean isNetworkEnabled = false;

    private LocationManager locationManager;

    public static interface LocationLoadedListener {
        public void locationTurnedOn(boolean bool);
        public void locationLoaded(Location location);
        public void locationError(String error);
    }

    private LocationLoadedListener listener;

    private static CustomLocationListener instance;
    public static CustomLocationListener getInstance(@NonNull LocationLoadedListener listener,
                                                     @NonNull Context context){
        if(instance == null){
            instance = new CustomLocationListener(listener, context);
        }
        return instance;
    }

    public static void clearInstance(){
        instance = null;
    }

    //Constructor
    private CustomLocationListener(@NonNull LocationLoadedListener listener,
                                   @NonNull Context context){
        this.listener = listener;
        this.context = context;
        this.locationManager = null;
    }

    public LocationLoadedListener getListener(){
        return this.listener;
    }

    /**
     * Start listening for location updates
     * @param minimumTimeBetweenUpdates The minimum time (in milliseconds) between updates
     *                                  to a user's location (Sample, 60000 == 1 minute)
     * @param minimumDistanceBetweenUpdates The minimum distance (in meters) to update a user's
     *                                      location. (Sample, 10 == 10 meters)
     */
    @RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    public void startListeningForLocation(long minimumTimeBetweenUpdates,
                                          float minimumDistanceBetweenUpdates){
        try {
            locationManager = (LocationManager) context
                    .getSystemService(Context.LOCATION_SERVICE);

            // getting GPS status
            isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if(!isGPSEnabled && !isNetworkEnabled){
                listener.locationError("Unable to load GPS, please make sure location is turned on");
                return;
            }

            if(isGPSEnabled){
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                        minimumTimeBetweenUpdates, minimumDistanceBetweenUpdates, this);
            } else if (isNetworkEnabled){
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                        minimumTimeBetweenUpdates, minimumDistanceBetweenUpdates, this);
            }
            Criteria crit = new Criteria();
            crit.setAccuracy(Criteria.ACCURACY_FINE);
            String provider = locationManager.getBestProvider(crit, true);
            Location loc = locationManager.getLastKnownLocation(provider);
            onLocationChanged(loc);
        } catch (SecurityException se){
            se.printStackTrace();
            listener.locationError(
                    "Location permission has been denied; it must be enabled before location can be used.");
        }
    }

    /**
     * Overloaded, allows for auto clearing of the instance
     */
    @RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    public void stopListeningForLocation(){
        stopListeningForLocation(false);
    }

    /**
     * Stop listening for location updates
     * @param keepInstance boolean, if true, it will keep the instance of the listener active. NOTE
     *                     that this can lead to a memory leak if not cleared. Simplest way is to
     *                     pass true, let it clear the instance, and use the static builder to
     *                     rebuild it when using it again.
     */
    @RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    public void stopListeningForLocation(boolean keepInstance){
        try {
            if(locationManager != null){
                locationManager.removeUpdates(this);
            }
        } catch (Exception e){}
        if(!keepInstance){
            clearInstance();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        if(location == null){
            return;
        }
        listener.locationLoaded(location);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO: 2017-10-16 Not used atm
    }

    @Override
    public void onProviderEnabled(String provider) {
        L.m("provider enabled + " + provider);
        listener.locationTurnedOn(true);
    }

    @Override
    public void onProviderDisabled(String provider) {
        L.m("provider disabled + " + provider);
        listener.locationTurnedOn(false);
    }
}
