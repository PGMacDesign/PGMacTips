
package com.pgmacdesign.pgmacutilities.creditcardutils;


abstract class CreditCardCardTrackBase extends CardTrackBase {


    private final AccountNumber accountNumber;
    private final ExpirationDateObject expirationDateObject;
    private final ServiceCode serviceCode;

    protected CreditCardCardTrackBase(String rawTrackData, AccountNumber accountNumber,
                                      ExpirationDateObject expirationDateObject,
                                      ServiceCode serviceCode, String discretionaryData) {
        super(rawTrackData, discretionaryData);
        this.accountNumber = accountNumber;
        this.expirationDateObject = expirationDateObject;
        this.serviceCode = serviceCode;
    }

    public AccountNumber getAccountNumber() {
        return this.accountNumber;
    }

    public ExpirationDateObject getExpirationDateObject() {
        return this.expirationDateObject;
    }

    public ServiceCode getServiceCode() {
        return this.serviceCode;
    }

    public boolean hasAccountNumber() {
        return (this.accountNumber != null &&
                this.accountNumber.hasAccountNumber()
        );
    }

    public boolean hasExpirationDate() {
        return (this.expirationDateObject != null &&
                this.expirationDateObject.hasExpirationDate()
        );
    }

    public boolean hasServiceCode() {
        return (this.serviceCode != null &&
                this.serviceCode.hasServiceCode()
        );
    }

    public boolean sharesSimilaritiesTo(final CreditCardCardTrackBase other) {
        if (other == null) {
            return false;
        }
        if (this == other) {
            return true;
        }

        boolean shareSimilarities = true;

        if (hasAccountNumber() && other.hasAccountNumber()) {
            if (!getAccountNumber().equals(other.getAccountNumber())) {
                shareSimilarities = false;
            }
        }

        if (hasExpirationDate() && other.hasExpirationDate()) {
            if (!getExpirationDateObject().equals(other.getExpirationDateObject())) {
                shareSimilarities = false;
            }
        }

        if (hasServiceCode() && other.hasServiceCode()) {
            if (!getServiceCode().equals(other.getServiceCode())) {
                shareSimilarities = false;
            }
        }

        return shareSimilarities;
    }

}
