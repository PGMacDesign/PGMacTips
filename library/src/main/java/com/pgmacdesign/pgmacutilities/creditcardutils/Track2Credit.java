package com.pgmacdesign.pgmacutilities.creditcardutils;


import com.pgmacdesign.pgmacutilities.utilities.StringUtilities;

import java.util.regex.Matcher;

public class Track2Credit extends CreditCardCardTrackBase {

    public static Track2Credit parse(final String rawTrackData) {
        Matcher matcher = CardConstants.TRACK_2_PATTERN.matcher(
                StringUtilities.removeSpaces(rawTrackData));
        ExpirationDateObject expirationDateObject;
        AccountNumber accountNumber;
        String discretionaryData;
        ServiceCode serviceCode;
        String rawTrack2Data;

        if (matcher.matches()) {
            rawTrack2Data = getGroup(matcher, 1);
            accountNumber = new AccountNumber(getGroup(matcher, 2));
            expirationDateObject = new ExpirationDateObject(getGroup(matcher, 3));
            serviceCode = new ServiceCode(getGroup(matcher, 4));
            discretionaryData = getGroup(matcher, 5);
        } else {
            rawTrack2Data = null;
            accountNumber = new AccountNumber(null);
            expirationDateObject = new ExpirationDateObject();
            serviceCode = new ServiceCode();
            discretionaryData = "";
        }

        return new Track2Credit(rawTrack2Data,
                accountNumber,
                expirationDateObject,
                serviceCode,
                discretionaryData);
    }

    private Track2Credit(String rawTrackData, AccountNumber accountNumber,
                         ExpirationDateObject expirationDateObject,
                         ServiceCode serviceCode, String discretionaryData) {
        super(rawTrackData, accountNumber, expirationDateObject, serviceCode, discretionaryData);
    }

    public boolean exceedsMaximumLength() {
        return tempStringTooLong();
    }

    @Override
    public boolean tempStringTooLong() {
        boolean tooLong = false;
        if(super.getTempString() != null){
            if(super.getTempString().length() > 40){
                tooLong = true;
            }
        }
        return tooLong;
    }
}
