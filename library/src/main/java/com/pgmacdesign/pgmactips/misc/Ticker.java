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

    //region Static final Strings
    private static final String TICK_TIME_PART_1 = "Tick Time: \nActivity == ";
    private static final String TICK_TIME_PART_2 = "\nHitting Line Number == ";
    private static final String TICK_TIME_PART_3 = "\nTime taken up until this point == ";
    
    private static final String TICK_TIME_PART_1_HIDE_NL = "Tick Time: Activity == ";
    private static final String TICK_TIME_PART_2_HIDE_NL = ", Hitting Line Number == ";
    private static final String TICK_TIME_PART_3_HIDE_NL = ", Time taken up until this point == ";
    //endregion
    
    //region Static Vars
    private static boolean shouldHideNewLineCharacters;
    //endregion
    
    //region Instance Vars
    private String screenName;
    private long timeAtInit;
    //endregion
    
    //region Constructor
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
        Ticker.shouldHideNewLineCharacters = false;
    }
    //endregion
    
    //region Public Static Methods
    
    /**
     * Simple setter for whether or not the class should hide printing the new line characters.
     * Defaults to false
     * @param shouldHide If false, it will print like this:
     *                      Tick Time: Activity == {Some Activity}
     *                      Hitting Line number == 123
     *                      Time Taken up until this points == 123
     *                   If true, it will print like this:
     *                      Tick Time: Activity == {Some Activity}, Hitting Line number == 123, Time Taken up until this points == 123
     */
    public static void setShouldHideNewLineCharacters(boolean shouldHide){
        Ticker.shouldHideNewLineCharacters = shouldHide;
    }
    
    //endregion
    
    //region Public Methods
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
        if(Ticker.shouldHideNewLineCharacters){
            L.m(TICK_TIME_PART_1_HIDE_NL + screenName
                    + TICK_TIME_PART_2_HIDE_NL + lineStr
                    + TICK_TIME_PART_3_HIDE_NL + (str + " " + appendEnd)
            );
        } else {
            L.m(TICK_TIME_PART_1 + screenName
                    + TICK_TIME_PART_2 + lineStr
                    + TICK_TIME_PART_3 + (str + " " + appendEnd)
            );
        }

    }
    //endregion
}
