package com.pgmacdesign.pgmactips.datamodels;

import com.google.gson.annotations.Expose;

/**
 * POJO For use with the Stripe SDK
 * Created by pmacdowell on 2018-02-23.
 */

public class StripeCustomerSourceObject3 {
    @Expose
    private SourceTypeData mSourceTypeData;

    public SourceTypeData getmSourceTypeData() {
        return mSourceTypeData;
    }

    public void setmSourceTypeData(SourceTypeData mSourceTypeData) {
        this.mSourceTypeData = mSourceTypeData;
    }

    public static class SourceTypeData {
        @Expose
        private String mId;
        @Expose
        private Float mAmount;
        @Expose
        private Long mCreated;
        @Expose
        private String mClientSecret;
        @Expose
        private String mType;
        @Expose
        private String exp_month;
        @Expose
        private String exp_year;
        @Expose
        private String mTypeRaw;
        @Expose
        private String mStatus;
        @Expose
        private String brand;
        @Expose
        private String last4;

        public String getExp_month() {
            return exp_month;
        }

        public void setExp_month(String exp_month) {
            this.exp_month = exp_month;
        }

        public String getExp_year() {
            return exp_year;
        }

        public void setExp_year(String exp_year) {
            this.exp_year = exp_year;
        }

        public String getBrand() {
            return brand;
        }

        public void setBrand(String brand) {
            this.brand = brand;
        }

        public String getLast4() {
            return last4;
        }

        public void setLast4(String last4) {
            this.last4 = last4;
        }

        public String getmId() {
            return mId;
        }

        public void setmId(String mId) {
            this.mId = mId;
        }

        public Float getmAmount() {
            return mAmount;
        }

        public void setmAmount(Float mAmount) {
            this.mAmount = mAmount;
        }

        public Long getmCreated() {
            return mCreated;
        }

        public void setmCreated(Long mCreated) {
            this.mCreated = mCreated;
        }

        public String getmClientSecret() {
            return mClientSecret;
        }

        public void setmClientSecret(String mClientSecret) {
            this.mClientSecret = mClientSecret;
        }

        public String getmType() {
            return mType;
        }

        public void setmType(String mType) {
            this.mType = mType;
        }

        public String getmTypeRaw() {
            return mTypeRaw;
        }

        public void setmTypeRaw(String mTypeRaw) {
            this.mTypeRaw = mTypeRaw;
        }

        public String getmStatus() {
            return mStatus;
        }

        public void setmStatus(String mStatus) {
            this.mStatus = mStatus;
        }

    }
}
