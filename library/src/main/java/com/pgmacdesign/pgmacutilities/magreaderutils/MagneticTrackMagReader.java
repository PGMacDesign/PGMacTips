
package com.pgmacdesign.pgmacutilities.magreaderutils;

public class MagneticTrackMagReader extends MagReaderTrackBase {

    private final Track1Credit track1Credit;
    private final Track2Credit track2Credit;
    private final Track3Credit track3Credit;

    private MagneticTrackMagReader(String rawTrackData, Track1Credit track1Credit,
                                   Track2Credit track2Credit, Track3Credit track3Credit) {
        super(rawTrackData, "");
        this.track1Credit = track1Credit;
        this.track2Credit = track2Credit;
        this.track3Credit = track3Credit;
    }

    public static MagneticTrackMagReader parse(String track1String, String track2String, String track3String) {
        Track1Credit track1Credit = null;
        Track2Credit track2Credit = null;
        Track3Credit track3Credit = null;
        try {
            track1Credit = Track1Credit.parse(track1String);
        } catch (Exception e){}
        try {
            track2Credit = Track2Credit.parse(track2String);
        } catch (Exception e){}
        try {
            track3Credit = Track3Credit.parse(track3String);
        } catch (Exception e) {}
        return new MagneticTrackMagReader("", track1Credit, track2Credit, track3Credit);
    }

    public static MagneticTrackMagReader parse(final String rawTrackData) {
        Track1Credit track1Credit = null;
        Track2Credit track2Credit = null;
        Track3Credit track3Credit = null;
        try {
            track1Credit = (Track1Credit) Track1Credit.parse(rawTrackData, true);
        } catch (Exception e){
            try {
                track1Credit = (Track1Credit) Track2Credit.parse(rawTrackData, true);
            } catch (Exception e1){}
        }
        try {
            track2Credit = Track2Credit.parse(rawTrackData);
        } catch (Exception e){
            try {
                track2Credit = (Track2Credit) Track1Credit.parse(rawTrackData, true);
            } catch (Exception e1){}
        }
        try {
            track3Credit = Track3Credit.parse(rawTrackData);
        } catch (Exception e) {}
        return new MagneticTrackMagReader(rawTrackData, track1Credit, track2Credit, track3Credit);
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

    public MagReaderCardObject convertToCreditCard() {


        AccountNumber accountNumber = null;
        try {
            if (track1Credit != null) {
                if (track1Credit.hasAccountNumber()) {
                    accountNumber = track1Credit.getAccountNumber();
                }
            }
            if (accountNumber == null) {
                if (track2Credit != null) {
                    if (track2Credit.hasAccountNumber()) {
                        accountNumber = track2Credit.getAccountNumber();
                    }
                }
            }
        } catch (Exception e){}
        if(accountNumber == null){
            accountNumber = new AccountNumber();
        }

        Name name = null;
        try {
            if (track1Credit != null) {
                if (track1Credit.hasName()) {
                    name = track1Credit.getName();
                }
            }
        } catch (Exception e){}
        if(name == null){
            name = new Name();
        }

        ExpirationDateObject expirationDateObject = null;
        if(track1Credit != null){
            if(track1Credit.hasExpirationDate()){
                expirationDateObject = track1Credit.getExpirationDateObject();
            }
        }
        try {
            if (expirationDateObject == null) {
                if (track2Credit != null) {
                    if (track2Credit.hasExpirationDate()) {
                        expirationDateObject = track2Credit.getExpirationDateObject();
                    }
                }
            }
        } catch (Exception e){}
        if(expirationDateObject == null){
            expirationDateObject = new ExpirationDateObject();
        }

        ServiceCode serviceCode = null;
        try {
            if (track1Credit != null) {
                if (track1Credit.hasServiceCode()) {
                    serviceCode = track1Credit.getServiceCode();
                }
            }
            if (serviceCode == null) {
                if (track2Credit != null) {
                    if (track2Credit.hasServiceCode()) {
                        serviceCode = track2Credit.getServiceCode();
                    }
                }
            }
        } catch (Exception e){}
        if(serviceCode == null){
            serviceCode = new ServiceCode();
        }

        MagReaderCardObject cardInfo = new MagReaderCardObject(accountNumber,
                expirationDateObject, name, serviceCode);
        return cardInfo;
    }

    @Override
    public boolean tempStringTooLong() {
        if(track1Credit != null){
            return this.track1Credit.exceedsMaximumLength();
        }
        if(track2Credit != null){
            return this.track2Credit.exceedsMaximumLength();
        }
        if(track3Credit != null){
            return this.track3Credit.exceedsMaximumLength();
        }
        return false;
    }
}
