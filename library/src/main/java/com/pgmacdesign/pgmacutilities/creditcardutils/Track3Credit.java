package com.pgmacdesign.pgmacutilities.creditcardutils;

import com.pgmacdesign.pgmacutilities.utilities.StringUtilities;

import java.util.regex.Matcher;

public class Track3Credit extends CardTrackBase {

    public static Track3Credit parse(final String rawTrackData) {
        String newTrackData = rawTrackData.replace(" ", "");
        final Matcher matcher = CardConstants.TRACK_3_PATTERN.matcher(StringUtilities.removeSpaces(newTrackData));

        final String rawTrack3Data;
        final String discretionaryData;
        if (matcher.matches()) {
            rawTrack3Data = getGroup(matcher, 1);
            discretionaryData = getGroup(matcher, 2);
        } else {
            rawTrack3Data = null;
            discretionaryData = "";
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
