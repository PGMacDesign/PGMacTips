
package com.pgmacdesign.pgmactips.magreaderutils;

/**
 * Base abstract class used by the various Track classes.
 */
abstract class MagReaderMagReaderTrackBase extends MagReaderTrackBase {


    private final AccountNumber accountNumber;
    private final ExpirationDateObject expirationDateObject;
    private final ServiceCode serviceCode;
    private final BirthDateObject birthDateObject;

    protected MagReaderMagReaderTrackBase(String rawTrackData, AccountNumber accountNumber,
                                          ExpirationDateObject expirationDateObject,
                                          ServiceCode serviceCode, String discretionaryData) {
        super(rawTrackData, discretionaryData);
        this.accountNumber = accountNumber;
        this.expirationDateObject = expirationDateObject;
        this.serviceCode = serviceCode;
        this.birthDateObject = null;
    }

    protected MagReaderMagReaderTrackBase(String rawTrackData, AccountNumber accountNumber,
                                          ExpirationDateObject expirationDateObject,
                                          ServiceCode serviceCode, String discretionaryData,
                                          String birthYear, String birthMonth, String birthDay) {
        super(rawTrackData, discretionaryData);
        this.accountNumber = accountNumber;
        this.expirationDateObject = expirationDateObject;
        this.serviceCode = serviceCode;
        this.birthDateObject = new BirthDateObject(birthYear, birthMonth, birthDay);
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

    public boolean sharesSimilaritiesTo(final MagReaderMagReaderTrackBase other) {
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

    public BirthDateObject getBirthDateObject() {
        return birthDateObject;
    }
}
