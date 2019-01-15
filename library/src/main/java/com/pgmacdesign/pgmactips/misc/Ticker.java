package com.pgmacdesign.pgmactips.misc;

import androidx.annotation.Nullable;

import com.pgmacdesign.pgmactips.utilities.L;
import com.pgmacdesign.pgmactips.utilities.NumberUtilities;
import com.pgmacdesign.pgmactips.utilities.StringUtilities;

import java.util.Date;

/**
 * Class for measuring system time while running an app. Useful for debugging
 * Created by pmacdowell on 2017-08-21.
 */

public class Ticker {

    private static final String TICK_TIME_PART_1 = "Tick Time: \nActivity == ";
    private static final String TICK_TIME_PART_2 = "\nHitting Line Number == ";
    private static final String TICK_TIME_PART_3 = "\nTime taken up until this point == ";

    private String screenName;
    private long timeAtInit;
    /**
     * Constructor is used for time measurement method
     * @param screenName Screen name to use in print statements
     * @param timeAtInit time (in milliseconds) of init start. use Date().getTime() for long.
     *                   If null is passed, it will auto get time from right now
     */
    public Ticker (@Nullable String screenName, @Nullable Long timeAtInit){
        if(StringUtilities.isNullOrEmpty(screenName)){
            this.screenName = "N/A";
        } else {
            this.screenName = screenName;
        }
        if(timeAtInit == null){
            this.timeAtInit = new Date().getTime();
        } else {
            this.timeAtInit = timeAtInit;
        }
    }

    /**
     * Reset the ticker to a new time
     * @param timeAtNow Time (in milliseconds) of start point. If null is passed, it will
     *                  auto get time from right now
     */
    public void resetTicker(@Nullable Long timeAtNow){
        if(timeAtNow == null){
            timeAtNow = new Date().getTime();
        }
        this.timeAtInit = timeAtNow;
    }

    /**
     * Print a tick in the logcat to indicate how much time has passed
     * @param lineNumber
     */
    public void tick(Integer lineNumber){
        String lineStr;
        if(lineNumber == null){
            lineStr = "";
        } else {
            lineStr = "" + lineNumber;
        }
        long now = new Date().getTime();
        long gap = now - this.timeAtInit;
        String appendEnd = "";
        if(gap < 1000){
            appendEnd = "milliseconds";
        } else if(gap >= 1000 && gap < PGMacTipsConstants.ONE_MINUTE){
            gap /= 1000;
            appendEnd = "seconds";
        } else if(gap >= PGMacTipsConstants.ONE_MINUTE && gap < PGMacTipsConstants.ONE_HOUR){
            gap /= 10000;
            appendEnd = "minutes";
        } else {
            gap /= 1000000;
            appendEnd = "minutes";
        }
        String str = NumberUtilities.formatNumberAddCommas(gap);
        L.m(    TICK_TIME_PART_1 + screenName
                + TICK_TIME_PART_2 + lineStr
                + TICK_TIME_PART_3 + (str + " " + appendEnd)
        );

    }
}
