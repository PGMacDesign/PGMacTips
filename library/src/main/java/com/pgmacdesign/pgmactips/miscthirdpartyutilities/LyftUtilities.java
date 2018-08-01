package com.pgmacdesign.pgmactips.miscthirdpartyutilities;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.pgmacdesign.pgmactips.utilities.StringUtilities;

/**
 * Lyft Utilities, see documentation from Lyft for more information:
 * https://developer.lyft.com/docs
 * Created by pmacdowell on 2017-05-04.
 */
public class LyftUtilities {



    public static class Builder {

        //See https://developer.lyft.com/docs/universal-links for more info
        static enum LyftRideTypes {
            lyft, // Standard Lyft
            lyft_line, //Lyft Line
            lyft_plus //Lyft Plus
        }
        private static final String LYFT_PACKAGE = "me.lyft.android";
        static final String LYFT_APP_BASE_STRING = "lyft://";
        static final String LYFT_WEB_BASE_STRING = "https://www.lyft.com/signup/SDKSIGNUP?sdkName=android_direct";

        private Context context;
        private double pickupLatitude, pickupLongitude, dropOffLatitude, dropOffLongitude;
        private String clientId, promoCode, lyftRideType;

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
         * @param clientId Client id, obtained by the Lyft dev portal
         * @return this
         */
        public Builder setClientId(String clientId){
            this.clientId = clientId;
            return this;
        }

        /**
         * Get the String lyft ride type beinf set.
         * NOTE! Defaults to standard lyft if nothing is passed
         * @return
         */
        public String getLyftRideType() {
            return lyftRideType;
        }

        /**
         * Lyft Ride types
         * @param lyftRideType {@link LyftRideTypes}
         */
        public void setLyftRideType(LyftRideTypes lyftRideType) {
            this.lyftRideType = lyftRideType.toString();
        }

        /**
         * Get the promo code
         * @return
         */
        public String getPromoCode() {
            return promoCode;
        }

        /**
         * Set a promo code
         * @param promoCode
         */
        public void setPromoCode(String promoCode) {
            this.promoCode = promoCode;
        }

        public Intent build(){
            boolean userHasLyftApp;
            StringBuilder sb = new StringBuilder();
            try {
                PackageManager pm = context.getPackageManager();
                pm.getPackageInfo(LYFT_PACKAGE, PackageManager.GET_ACTIVITIES);
                userHasLyftApp = true;
                sb.append(LYFT_APP_BASE_STRING);
                if(!StringUtilities.isNullOrEmpty(this.lyftRideType)){
                    sb.append(this.lyftRideType + "?");
                } else {
                    sb.append("lyft" + "?");
                }
            } catch (PackageManager.NameNotFoundException e) {
                // No Lyft app! Open mobile website.
                userHasLyftApp = false;
                sb.append(LYFT_WEB_BASE_STRING);
            }

            if (!StringUtilities.isNullOrEmpty(promoCode)) {
                sb.append("credits=" + promoCode + "&");
            }
            if(!StringUtilities.isNullOrEmpty(clientId)) {
                sb.append("clientId=" + clientId + "&");
            }
            sb.append("pickup[latitude]=" + pickupLatitude + "&");
            sb.append("pickup[longitude]=" + pickupLongitude + "&");
            sb.append("destination[latitude]=" + dropOffLatitude + "&");
            sb.append("destination[longitude]=" + dropOffLongitude);

            String uri = sb.toString();

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(uri));
            return intent;

        }

    }
}
