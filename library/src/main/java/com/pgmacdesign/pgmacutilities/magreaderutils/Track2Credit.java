package com.pgmacdesign.pgmacutilities.magreaderutils;


import java.util.regex.Matcher;

import static com.pgmacdesign.pgmacutilities.utilities.StringUtilities.isNullOrEmpty;

public class Track2Credit extends MagReaderMagReaderTrackBase {

    public static Track2Credit parse(final String rawTrackData) {
        if(isNullOrEmpty(rawTrackData)){
            return null;
        }
        Matcher matcher = CardConstants.TRACK_2_PATTERN.matcher(rawTrackData.trim());
        ExpirationDateObject expirationDateObject = new ExpirationDateObject();
        AccountNumber accountNumber = new AccountNumber(null);
        String discretionaryData = "";
        ServiceCode serviceCode = new ServiceCode();
        String rawTrack2Data = null;

        if (matcher.matches()) {
            int groupSize = matcher.groupCount();
            for(int i = 1; i <= groupSize; i++) {
                switch (i) {
                    case 1:
                        rawTrack2Data = getGroup(matcher, 1);
                        break;

                    case 2:
                        accountNumber = new AccountNumber(getGroup(matcher, 2));
                        break;

                    case 3:
                        expirationDateObject = new ExpirationDateObject(getGroup(matcher, 3));
                        break;

                    case 4:
                        serviceCode = new ServiceCode(getGroup(matcher, 4));
                        break;

                    case 5:
                        discretionaryData = getGroup(matcher, 5);
                        break;

                }
            }
        }

        return new Track2Credit(rawTrack2Data,
                accountNumber,
                expirationDateObject,
                serviceCode,
                discretionaryData);
    }

    public static MagReaderMagReaderTrackBase parse(final String rawTrackData, boolean isDriversLicense) {
        if(isNullOrEmpty(rawTrackData)){
            return null;
        }
        Matcher matcher = CardConstants.TRACK_2_PATTERN_DL.matcher(rawTrackData.trim());
        ExpirationDateObject expirationDateObject = new ExpirationDateObject();
        AccountNumber accountNumber = new AccountNumber(null);
        String discretionaryData = "";
        ServiceCode serviceCode = new ServiceCode();
        String rawTrack2Data = null;
        String birthYear = null, birthMonth = null, birthDay = null;

        if (matcher.matches()) {
            int groupSize = matcher.groupCount();
            for(int i = 1; i <= groupSize; i++) {
                switch (i) {
                    case 1:
                        rawTrack2Data = getGroup(matcher, 1);
                        break;

                    case 2:
                        accountNumber = new AccountNumber(getGroup(matcher, 2));
                        break;

                    case 3:
                        expirationDateObject = new ExpirationDateObject(getGroup(matcher, 3));
                        break;

                    case 4:
                        birthYear = getGroup(matcher, 4);
                        break;

                    case 5:
                        birthMonth = getGroup(matcher, 5);
                        break;

                    case 6:
                        birthDay = getGroup(matcher, 6);
                        break;
                }
            }
        }

        return new Track2Credit(rawTrack2Data, accountNumber, expirationDateObject,
                serviceCode, discretionaryData, birthYear, birthMonth, birthDay);
    }

    private Track2Credit(String rawTrackData, AccountNumber accountNumber,
                         ExpirationDateObject expirationDateObject,
                         ServiceCode serviceCode, String discretionaryData) {
        super(rawTrackData, accountNumber, expirationDateObject, serviceCode, discretionaryData);
    }

    private Track2Credit(String rawTrackData, AccountNumber accountNumber,
                         ExpirationDateObject expirationDateObject,
                         ServiceCode serviceCode, String discretionaryData,
                         String birthYear, String birthMonth, String birthDay) {
        super(rawTrackData, accountNumber, expirationDateObject, serviceCode,
                discretionaryData, birthYear, birthMonth, birthDay);
    }

    public boolean exceedsMaximumLength() {
        return tempStringTooLong();
    }

    @Override
    public boolean tempStringTooLong() {
        boolean tooLong = false;
        if(!isNullOrEmpty(super.getTempString())){
            if(super.getTempString().length() > 40){
                tooLong = true;
            }
        }
        return tooLong;
    }
}
