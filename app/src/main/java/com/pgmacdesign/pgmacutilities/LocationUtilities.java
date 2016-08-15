package com.pgmacdesign.pgmacutilities;

/**
 * Get location via the GPS. To use the built in GPS methods, request permissions and then:
 *
 * //GLOBAL VARS
 * private LocationManager lm;
 * lm = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
 *
 * //TO ADD / START
 * lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 2000, 200, context);
 *
 * //TO REMOVE / STOP
 * lm.removeUpdates(context)
 *
 * To use this class, first request permissions, then:
 *
   final LocationUtilities.LocationResult myResult = new LocationUtilities.LocationResult() {
            @Override
            public void gotLocation(Location location) {
                location.getLatitude();
                location.getLongitude();
                //Do stuff here with returned results
            }
        };

    LocationUtilities loc1 = new LocationUtilities() {
            @Override
            public boolean getLocation(Activity activity, Context context, LocationResult result) {
                return super.getLocation(activity, context, result);
            }
        };

    loc1.getLocation(this, this, myResult);

 * Created by pmacdowell on 8/15/2016.
 *
 * NOTE! Currently reworking this code. Leaving the class in and commented out so that
 * it can be referenced for anyone needing it, but for now, not active code
 */
public abstract class LocationUtilities {

    /*
    Timer timer1;
    LocationManager lm;
    LocationResult locationResult;
    boolean gps_enabled=false;
    boolean network_enabled=false;

    public boolean getLocation(Activity activity, Context context, LocationResult result)
    {
        //Permissions
        try {
            PermissionUtilities.getLocationPermissions(activity);
        } catch (Exception e){
            //If permissions are not avail, bail out
            return false;
        }
        //Once permissions are granted, move on
        try {
            //I use LocationResult callback class to pass location value from MyLocation to user code.
            locationResult = result;
            if (lm == null)
                lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

            //exceptions will be thrown if provider is not permitted.
            try {
                gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
            } catch (Exception ex) {
            }
            try {
                network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            } catch (Exception ex) {
            }

            //don't start listeners if no provider is enabled
            if (!gps_enabled && !network_enabled)
                return false;

            if (gps_enabled)
                lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListenerGps);
            if (network_enabled)
                lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListenerNetwork);
            timer1 = new Timer();
            timer1.schedule(new GetLastLocation(), 20000);
            return true;
        } catch (SecurityException e2){
            L.m("Location not enabled...");
        } catch (Exception e){
            return false;
        }
        return false;
    }

    LocationListener locationListenerGps = new LocationListener() {
        public void onLocationChanged(Location location) {
            timer1.cancel();
            locationResult.gotLocation(location);
            try {
                lm.removeUpdates(this);
                lm.removeUpdates(locationListenerNetwork);
            } catch (SecurityException e){
                L.m("Does not have location enabled...");
            }
        }
        public void onProviderDisabled(String provider) {}
        public void onProviderEnabled(String provider) {}
        public void onStatusChanged(String provider, int status, Bundle extras) {}
    };

    LocationListener locationListenerNetwork = new LocationListener() {
        public void onLocationChanged(Location location) {
            timer1.cancel();
            locationResult.gotLocation(location);
            try {
                lm.removeUpdates(this);
                lm.removeUpdates(locationListenerGps);
            } catch (SecurityException e){
                L.m("Does not have location enabled...");
            }
        }
        public void onProviderDisabled(String provider) {}
        public void onProviderEnabled(String provider) {}
        public void onStatusChanged(String provider, int status, Bundle extras) {}
    };

    class GetLastLocation extends TimerTask {
        @Override
        public void run() {
            try {
                lm.removeUpdates(locationListenerGps);
                lm.removeUpdates(locationListenerNetwork);

                Location net_loc = null, gps_loc = null;
                if (gps_enabled)
                    gps_loc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (network_enabled)
                    net_loc = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                //if there are both values use the latest one
                if (gps_loc != null && net_loc != null) {
                    if (gps_loc.getTime() > net_loc.getTime())
                        locationResult.gotLocation(gps_loc);
                    else
                        locationResult.gotLocation(net_loc);
                    return;
                }

                if (gps_loc != null) {
                    locationResult.gotLocation(gps_loc);
                    return;
                }
                if (net_loc != null) {
                    locationResult.gotLocation(net_loc);
                    return;
                }
                locationResult.gotLocation(null);
            } catch (SecurityException e){
                L.m("Does not have location enabled...");
            }
        }
    }

    public static abstract class LocationResult{
        public abstract void gotLocation(Location location);
    }
     */
}
