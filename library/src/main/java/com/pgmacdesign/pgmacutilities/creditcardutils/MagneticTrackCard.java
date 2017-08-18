
package com.pgmacdesign.pgmacutilities.creditcardutils;

public class MagneticTrackCard extends CardTrackBase {

    private final Track1Credit track1Credit;
    private final Track2Credit track2Credit;
    private final Track3Credit track3Credit;

    private MagneticTrackCard(String rawTrackData, Track1Credit track1Credit,
                              Track2Credit track2Credit, Track3Credit track3Credit) {
        super(rawTrackData, "");
        this.track1Credit = track1Credit;
        this.track2Credit = track2Credit;
        this.track3Credit = track3Credit;
    }

    public static MagneticTrackCard parse(final String rawTrackData) {
        final Track1Credit track1Credit = Track1Credit.parse(rawTrackData);
        final Track2Credit track2Credit = Track2Credit.parse(rawTrackData);
        final Track3Credit track3Credit = Track3Credit.parse(rawTrackData);
        return new MagneticTrackCard(rawTrackData, track1Credit, track2Credit, track3Credit);
    }

    public Track1Credit getTrack1Credit() {
        return this.track1Credit;
    }

    public Track2Credit getTrack2Credit() {
        return this.track2Credit;
    }

    public Track3Credit getTrack3Credit() {
        return this.track3Credit;
    }

    public boolean isConsistent() {
        return this.track1Credit.sharesSimilaritiesTo(this.track2Credit);
    }

    public CreditCardObject convertToCreditCard() {

        AccountNumber accountNumber;
        if (this.track1Credit.hasAccountNumber()) {
            accountNumber = this.track1Credit.getAccountNumber();
        } else {
            accountNumber = this.track2Credit.getAccountNumber();
        }

        Name name;
        if (this.track1Credit.hasName()) {
            name = this.track1Credit.getName();
        } else {
            name = new Name();
        }

        ExpirationDateObject expirationDateObject;
        if (this.track1Credit.hasExpirationDate()) {
            expirationDateObject = this.track1Credit.getExpirationDateObject();
        } else {
            expirationDateObject = this.track2Credit.getExpirationDateObject();
        }

        ServiceCode serviceCode;
        if (this.track1Credit.hasServiceCode()) {
            serviceCode = this.track1Credit.getServiceCode();
        } else {
            serviceCode = this.track2Credit.getServiceCode();
        }

        CreditCardObject cardInfo = new CreditCardObject(accountNumber,
                expirationDateObject, name, serviceCode);
        return cardInfo;
    }

    @Override
    public boolean tempStringTooLong() {
        return (this.track1Credit.exceedsMaximumLength()
                || this.track2Credit.exceedsMaximumLength()
                || this.track3Credit.exceedsMaximumLength()
        );
    }
}
