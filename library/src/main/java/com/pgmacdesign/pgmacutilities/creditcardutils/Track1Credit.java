
package com.pgmacdesign.pgmacutilities.creditcardutils;


import com.pgmacdesign.pgmacutilities.utilities.StringUtilities;

import java.util.regex.Matcher;

public class Track1Credit extends CreditCardCardTrackBase {

    public static Track1Credit parse(final String rawTrackData) {

        Matcher matcher = CardConstants.TRACK_1_PATTERN.matcher(StringUtilities.removeSpaces(rawTrackData));
        String rawTrack1Data;
        AccountNumber pan;
        ExpirationDateObject expirationDateObject;
        Name name;
        ServiceCode serviceCode;
        String formatCode;
        String discretionaryData;

        if (matcher.matches()) {
            rawTrack1Data = getGroup(matcher, 1);
            formatCode = getGroup(matcher, 2);
            pan = new AccountNumber(getGroup(matcher, 3));
            name = new Name(getGroup(matcher, 4));
            expirationDateObject = new ExpirationDateObject(getGroup(matcher, 5));
            serviceCode = new ServiceCode(getGroup(matcher, 6));
            discretionaryData = getGroup(matcher, 7);
        } else {
            rawTrack1Data = null;
            formatCode = "";
            pan = new AccountNumber(null);
            name = new Name();
            expirationDateObject = new ExpirationDateObject();
            serviceCode = new ServiceCode();
            discretionaryData = "";
        }

        return new Track1Credit(rawTrack1Data, pan, expirationDateObject, name,
                serviceCode, formatCode, discretionaryData);
    }

    private final Name name;
    private final String formatCode;

    private Track1Credit(String rawTrackData, AccountNumber accountNumber,
                         ExpirationDateObject expirationDateObject,
                         Name name, ServiceCode serviceCode,
                         String formatCode, String discretionaryData) {
        super(rawTrackData, accountNumber, expirationDateObject,
                serviceCode, discretionaryData);
        this.formatCode = formatCode;
        this.name = name;
    }

    public boolean exceedsMaximumLength() {
        return tempStringTooLong();
    }

    public String getFormatCode() {
        return formatCode;
    }

    public Name getName() {
        return name;
    }

    public boolean hasFormatCode() {
        if(formatCode == null){
            return false;
        }
        if(formatCode.isEmpty()){
            return false;
        }
        return true;
    }

    public boolean hasName() {
        return (name != null && name.objectHasName());
    }

    @Override
    public boolean tempStringTooLong() {
        boolean tooLong = false;
        if(getTempString() != null){
            if(getTempString().length() > 79){
                tooLong = true;
            }
        }
        return tooLong;
    }

    @Override
    public boolean tempStringHasData() {
        return super.tempStringHasData();
    }

    @Override
    public String getTempString() {
        return super.getTempString();
    }

    @Override
    public void clearTempString() {
        super.clearTempString();
    }

}
