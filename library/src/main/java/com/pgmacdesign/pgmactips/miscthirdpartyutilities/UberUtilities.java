package com.pgmacdesign.pgmactips.miscthirdpartyutilities;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import androidx.annotation.NonNull;

import com.pgmacdesign.pgmactips.utilities.StringUtilities;

/**
 * Utilities for Uber (Car driving / taxi service)
 * See docs here for more info:
 * https://developer.uber.com/docs/riders/ride-requests/tutorials/deep-links/introduction
 // TODO: 2017-04-26 In the future, will add trip branding functionality:
 // https://developer.uber.com/docs/riders/ride-requests/tutorials/deep-links/trip-branding
 * Created by pmacdowell on 2017-04-26.
 */
public class UberUtilities {


    public static class Builder {

        static final String UBER_PACKAGE = "com.ubercab";
        static final String UBER_APP_BASE_STRING = "uber://?";
        static final String UBER_WEB_BASE_STRING = "https://m.uber.com/ul/?";

        private Context context;
        private double pickupLatitude, pickupLongitude, dropOffLatitude, dropOffLongitude;
        private String clientId, pickupAddress, dropoffAddress,
                nameOfPickupLocation, nameOfDropoffLocation;

        /**
         * Constructor. Requires all lat / lng fields to be filled out
         * @param context Context
         * @param pickupLatitude pickup latitude (Starting point)
         * @param pickupLongitude pickup longitude (Starting point)
         * @param dropOffLatitude drop-off latitude (ending point)
         * @param dropOffLongitude drop-off longitude (ending point)
         */
        public Builder (@NonNull Context context, double pickupLatitude,
                        double pickupLongitude, double dropOffLatitude, double dropOffLongitude){
            this.context = context;
            this.pickupLatitude = pickupLatitude;
            this.pickupLongitude = pickupLongitude;
            this.dropOffLatitude = dropOffLatitude;
            this.dropOffLongitude = dropOffLongitude;
        }

        /**
         * Set the client ID
         * @param clientId Client id, obtained by the Uber dev portal
         * @return this
         */
        public Builder setClientId(String clientId){
            this.clientId = clientId;
            return this;
        }

        /**
         * Set the nickname for the dropoff location. This will show in the uber app
         * @param dropoffNickname Drop-off nickname (IE "Walmart" or "Destination")
         * @return this
         */
        public Builder setDropoffNickname(String dropoffNickname){
            this.nameOfDropoffLocation = dropoffNickname;
            return this;
        }

        /**
         * Set the nickname for the pickup location. This will show in the uber app
         * @param pickupNickname Pick-up nickname (IE "Walmart" or "Home")
         * @return this
         */
        public Builder setPickupNickname(String pickupNickname){
            this.nameOfPickupLocation = pickupNickname;
            return this;
        }

        /**
         * Set the nickname for the dropoff address. This will show in the uber app
         * @param dropoffAddress formatted Drop-off address
         *                       (IE "123 Fake St, New york, New York, 12345")
         * @return this
         */
        public Builder setDropoffAddress(String dropoffAddress){
            this.dropoffAddress = dropoffAddress;
            return this;
        }

        /**
         * Set the nickname for the dropoff address. This will show in the uber app
         * @param pickupAddress formatted Pick-up address
         *                       (IE "123 Fake St, New york, New York, 12345")
         * @return this
         */
        public Builder setPickupAddress(String pickupAddress){
            this.pickupAddress = pickupAddress;
            return this;
        }

        public Intent build(){
            boolean userHasUberApp;
            StringBuilder sb = new StringBuilder();
            try {
                PackageManager pm = context.getPackageManager();
                pm.getPackageInfo(UBER_PACKAGE, PackageManager.GET_ACTIVITIES);
                userHasUberApp = true;
                sb.append(UBER_APP_BASE_STRING);
            } catch (PackageManager.NameNotFoundException e) {
                // No Uber app! Open mobile website.
                userHasUberApp = false;
                sb.append(UBER_WEB_BASE_STRING);
            }

            sb.append("action=setPickup&");
            if (!StringUtilities.isNullOrEmpty(nameOfDropoffLocation)) {
                sb.append("dropoff[nickname]=" + nameOfDropoffLocation + "&");
            }
            if (!StringUtilities.isNullOrEmpty(nameOfPickupLocation)) {
                sb.append("pickup[nickname]=" + nameOfPickupLocation + "&");
            }
            if(!StringUtilities.isNullOrEmpty(pickupAddress)){
                sb.append("pickup[formatted_address]=" + pickupAddress + "&");
            }
            if(!StringUtilities.isNullOrEmpty(dropoffAddress)){
                sb.append("dropoff[formatted_address]=" + dropoffAddress + "&");
            }
            if(!StringUtilities.isNullOrEmpty(clientId)) {
                sb.append("client_id=" + clientId + "&");
            }
            sb.append("pickup[latitude]=" + pickupLatitude + "&");
            sb.append("pickup[longitude]=" + pickupLongitude + "&");
            sb.append("dropoff[latitude]=" + dropOffLatitude + "&");
            sb.append("dropoff[longitude]=" + dropOffLongitude + "&");

            String uri = sb.toString();

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(uri));
            return intent;

            /*
            Leaving this in place in case I need to differentiate in the future
            if(userHasUberApp){
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(uri));
                return intent;

            } else {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(uri));
                return intent;
            }
            */
        }

    }
}
