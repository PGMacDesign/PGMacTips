package com.pgmacdesign.pgmactips.datamodels;

import com.google.gson.annotations.Expose;

/**
 * Created by pmacdowell on 2018-02-23.
 */

public class StripeCustomerSourceObject {
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
        private String mId;

        @Expose
        private SourceTypeModel mSourceTypeModel;

        public SourceTypeModel getmSourceTypeModel() {
            return mSourceTypeModel;
        }

        public void setmSourceTypeModel(SourceTypeModel mSourceTypeModel) {
            this.mSourceTypeModel = mSourceTypeModel;
        }

        public String getmId() {
            return mId;
        }

        public void setmId(String mId) {
            this.mId = mId;
        }
    }

    public static class SourceTypeModel {
        @Expose
        private String mBrand;
        @Expose
        private String mCountry;
        @Expose
        private String mExpiryMonth;
        @Expose
        private String mExpiryYear;
        @Expose
        private String mFunding;
        @Expose
        private String mLast4;

        public String getmBrand() {
            return mBrand;
        }

        public void setmBrand(String mBrand) {
            this.mBrand = mBrand;
        }

        public String getmCountry() {
            return mCountry;
        }

        public void setmCountry(String mCountry) {
            this.mCountry = mCountry;
        }

        public String getmExpiryMonth() {
            return mExpiryMonth;
        }

        public void setmExpiryMonth(String mExpiryMonth) {
            this.mExpiryMonth = mExpiryMonth;
        }

        public String getmExpiryYear() {
            return mExpiryYear;
        }

        public void setmExpiryYear(String mExpiryYear) {
            this.mExpiryYear = mExpiryYear;
        }

        public String getmFunding() {
            return mFunding;
        }

        public void setmFunding(String mFunding) {
            this.mFunding = mFunding;
        }

        public String getmLast4() {
            return mLast4;
        }

        public void setmLast4(String mLast4) {
            this.mLast4 = mLast4;
        }
    }
}
