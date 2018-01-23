
package com.pgmacdesign.pgmactips.magreaderutils;


import com.pgmacdesign.pgmactips.misc.TempString;

import java.util.regex.Matcher;

abstract class MagReaderTrackBase extends BaseTempData implements TempStringInterface {

    private transient TempString tempString;

    protected MagReaderTrackBase(final String rawTrackData, final String tempString) {
        super(rawTrackData);
        this.tempString = new TempString(tempString);
    }

    static String getGroup(final Matcher matcher, final int group) {
        final int groupCount = matcher.groupCount();
        if (groupCount > (group - 1)) {
            return matcher.group(group);
        } else {
            return null;
        }
    }

    protected MagReaderTrackBase(String tempString) {
        super(tempString);
        this.tempString = new TempString(tempString);
    }

    @Override
    public boolean tempStringHasData() {
        return this.tempString.thereIsData();
    }

    @Override
    public String getTempString() {
        return this.tempString.getTempStringData();
    }

    @Override
    public void clearTempString() {
        this.tempString.disposeData();
    }


    public boolean isThereData() {
        if (tempString == null) {
            return false;
        }
        if (tempString.getTempStringData() == null) {
            return false;
        }
        return tempString.thereIsData();
    }

}
