package com.pgmacdesign.pgmactips.datamodels;

import com.google.gson.annotations.Expose;

/**
 * POJO For use with the Stripe SDK
 * Created by pmacdowell on 2018-02-23.
 */

public class StripeCustomerSourceObject2 {
    @Expose
    private StripePaymentSource mStripePaymentSource;

    public StripePaymentSource getmStripePaymentSource() {
        return mStripePaymentSource;
    }

    public void setmStripePaymentSource(StripePaymentSource mStripePaymentSource) {
        this.mStripePaymentSource = mStripePaymentSource;
    }

    public static class StripePaymentSource {
        @Expose
        private Integer expYear;
        @Expose
        private Integer expMonth;
        @Expose
        private String last4;
        @Expose
        private String id;
        @Expose
        private String brand;
        @Expose
        private String country;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public Integer getExpYear() {
            return expYear;
        }

        public void setExpYear(Integer expYear) {
            this.expYear = expYear;
        }

        public Integer getExpMonth() {
            return expMonth;
        }

        public void setExpMonth(Integer expMonth) {
            this.expMonth = expMonth;
        }

        public String getLast4() {
            return last4;
        }

        public void setLast4(String last4) {
            this.last4 = last4;
        }

        public String getBrand() {
            return brand;
        }

        public void setBrand(String brand) {
            this.brand = brand;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }
    }
}
