
package com.pgmacdesign.pgmacutilities.magreaderutils;


import com.pgmacdesign.pgmacutilities.utilities.L;

import java.util.regex.Matcher;

import static com.pgmacdesign.pgmacutilities.utilities.StringUtilities.isNullOrEmpty;

public class Track1Credit extends MagReaderMagReaderTrackBase {

    public static Track1Credit parse(final String rawTrackData) {
        if(isNullOrEmpty(rawTrackData)){
            return null;
        }
        Matcher matcher = CardConstants.TRACK_1_PATTERN.matcher(rawTrackData.trim());
        String rawTrack1Data = null;
        AccountNumber accountNumber = new AccountNumber(null);
        ExpirationDateObject expirationDateObject = new ExpirationDateObject();
        Name name = new Name();
        ServiceCode serviceCode = new ServiceCode();
        String formatCode = "";
        String discretionaryData = "";

        if (matcher.matches()) {
            int groupSize = matcher.groupCount();
            for(int i = 1; i <= groupSize; i++){
                switch (i){
                    case 1:
                        rawTrack1Data = getGroup(matcher, 1);
                        break;
                    case 2:
                        formatCode = getGroup(matcher, 2);
                        break;
                    case 3:
                        accountNumber = new AccountNumber(getGroup(matcher, 3));
                        break;
                    case 4:
                        name = new Name(getGroup(matcher, 4));
                        break;
                    case 5:
                        expirationDateObject = new ExpirationDateObject(getGroup(matcher, 5));
                        break;
                    case 6:
                        serviceCode = new ServiceCode(getGroup(matcher, 6));
                        break;
                    case 7:
                        discretionaryData = getGroup(matcher, 7);
                        break;
                }
            }
        }

        return new Track1Credit(rawTrack1Data, accountNumber, expirationDateObject, name,
                serviceCode, formatCode, discretionaryData);
    }

    public static MagReaderMagReaderTrackBase parse(final String rawTrackData, boolean bool) {
        if(isNullOrEmpty(rawTrackData)){
            return null;
        }
        Matcher matcher = CardConstants.TRACK_1_PATTERN.matcher(rawTrackData.trim());
        String rawTrack1Data = null;
        AccountNumber accountNumber = new AccountNumber(null);
        ExpirationDateObject expirationDateObject = new ExpirationDateObject();
        Name name = new Name();
        ServiceCode serviceCode = new ServiceCode();
        String formatCode = "";
        String discretionaryData = "";

        if (matcher.matches()) {
            int groupSize = matcher.groupCount();
            for(int i = 1; i <= groupSize; i++){
                switch (i){
                    case 1:
                        rawTrack1Data = getGroup(matcher, 1);
                        L.m("rawTrack1Data == " + rawTrack1Data);
                        break;
                    case 2:
                        formatCode = getGroup(matcher, 2);
                        L.m("formatCode == " + formatCode);
                        break;
                    case 3:
                        accountNumber = new AccountNumber(getGroup(matcher, 3));
                        L.m("accountNumber == " + accountNumber);
                        break;
                    case 4:
                        name = new Name(getGroup(matcher, 4));
                        L.m("name == " + name);
                        break;
                    case 5:
                        expirationDateObject = new ExpirationDateObject(getGroup(matcher, 5));
                        L.m("expirationDateObject == " + getGroup(matcher, 5));
                        break;
                    case 6:
                        serviceCode = new ServiceCode(getGroup(matcher, 6));
                        L.m("serviceCode == " + serviceCode);
                        break;
                    case 7:
                        discretionaryData = getGroup(matcher, 7);
                        L.m("discretionaryData == " + discretionaryData);
                        break;
                }
            }
        }

        return new Track1Credit(rawTrack1Data, accountNumber, expirationDateObject, name,
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
        if(!isNullOrEmpty(getTempString())){
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
