package com.pgmacdesign.pgmacutilities.magreaderutils;

import com.pgmacdesign.pgmacutilities.utilities.L;

import java.util.regex.Matcher;

import static com.pgmacdesign.pgmacutilities.utilities.StringUtilities.isNullOrEmpty;

public class Track3Credit extends MagReaderTrackBase {

    public static Track3Credit parse(final String rawTrackData) {
        if (isNullOrEmpty(rawTrackData)) {
            return null;
        }
        final Matcher matcher = CardConstants.TRACK_3_PATTERN.matcher(rawTrackData.trim());

        String rawTrack3Data = null;
        String discretionaryData = "";
        if (matcher.matches()) {
            int groupSize = matcher.groupCount();
            for(int i = 1; i <= groupSize; i++) {
                switch (i) {
                    case 1:
                        rawTrack3Data = getGroup(matcher, 1);
                        L.m("rawTrack3Data == " + rawTrack3Data);
                        break;

                    case 2:
                        discretionaryData = getGroup(matcher, 2);
                        L.m("discretionaryData == " + discretionaryData);
                        break;

                }
            }


        }
        return new Track3Credit(rawTrack3Data, discretionaryData);
    }


    private Track3Credit(final String rawTrack2Data, final String discretionaryData) {
        super(rawTrack2Data, discretionaryData);
    }

    public boolean exceedsMaximumLength() {
        return tempStringTooLong();
    }

    @Override
    public boolean tempStringTooLong() {
        return false;
    }
}
