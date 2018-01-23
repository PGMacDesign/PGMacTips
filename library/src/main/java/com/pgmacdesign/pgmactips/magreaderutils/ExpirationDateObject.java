
package com.pgmacdesign.pgmactips.magreaderutils;


import com.pgmacdesign.pgmactips.misc.TempString;
import com.pgmacdesign.pgmactips.utilities.StringUtilities;

import java.util.Calendar;
import java.util.Date;

import static com.pgmacdesign.pgmactips.utilities.StringUtilities.isNullOrEmpty;
import static com.pgmacdesign.pgmactips.utilities.StringUtilities.keepNumbersOnly;

public final class ExpirationDateObject implements TempStringInterface{


    private transient TempString expirationYearMonth;

    public ExpirationDateObject() {
        this.expirationYearMonth = new TempString(null);
    }

    public ExpirationDateObject(final Date date) {
        this.expirationYearMonth = new TempString(null);
        if (date != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            String month = Integer.toString(cal.get(Calendar.MONTH) + 1);
            String year = Integer.toString(cal.get(Calendar.YEAR));
            this.expirationYearMonth = new TempString(year + "/" + month);
        }
    }

    /**
     * Expiration date parse year and month. (int format)
     * @param year  Year
     * @param month Month
     */
    public ExpirationDateObject(final int year, final int month) {
        this.expirationYearMonth = new TempString(year + "/" + month);
    }

    /**
     * Overloaded endpoint
     * @param rawExpirationDate
     */
    public ExpirationDateObject(final String rawExpirationDate) {

        if(StringUtilities.isNullOrEmpty(rawExpirationDate)){
            this.expirationYearMonth = new TempString("");
            return;
        }
        char[] characters = rawExpirationDate.toCharArray();
        if(characters == null){
            this.expirationYearMonth = new TempString("");
            return;
        }
        if(characters.length != 4){
            this.expirationYearMonth = new TempString("");
            return;
        }
        String month = (characters[2] + "") + (characters[3] + "");
        String year = (characters[0] + "") + (characters[1] + "");
        this.expirationYearMonth = new TempString(year + "/" + month);
    }

    /**
     * Dispose / clear the temp string data
     */
    @Override
    public void clearTempString() {
        this.expirationYearMonth.disposeData();

    }

    /**
     * Getter for the string
     * @return
     */
    @Override
    public String getTempString() {
        return this.expirationYearMonth.getTempStringData();
    }

    /**
     * Checking if the string is empty or not
     * @return
     */
    @Override
    public boolean tempStringHasData() {
        return this.expirationYearMonth != null;
    }

    /**
     * Checks if the expiration is too long (longer than MM/YY)
     * @return
     */
    @Override
    public boolean tempStringTooLong() {
        String str = getTempString();
        str = StringUtilities.removeSpaces(str);
        str = keepNumbersOnly(str);
        if(isNullOrEmpty(str)){
            return false;
        } else {
            return (str.length() > 4);
        }
    }

    /**
     * Gets the card expiration date, as a Date object. If there is no date or it
     * cannot be parsed, will return null.
     *
     * @return Date expiration date {@link Date}.
     */
    public Date getExpirationDateAsDate() {
        if(this.expirationYearMonth == null){
            return null;
        }
        String str = this.expirationYearMonth.getTempStringData();
        if(StringUtilities.isNullOrEmpty(str)){
            return null;
        }
        String[] exp = str.split("/");
        if(exp == null){
            return null;
        }
        if(exp.length != 2){
            return null;
        }
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.set(
                    Integer.parseInt("20" + exp[0]),
                    (Integer.parseInt(exp[1])),
                    0, 23, 59, 59);
            //Remember that setting 0 as the day defaults it to the end of the month
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }

        return calendar.getTime();
    }

    /**
     * Check if the expiration date is even set
     * @return
     */
    public boolean hasExpirationDate() {
        return this.expirationYearMonth != null;
    }

    /**
     * Whether or not the card has expired
     * @return boolean, True if the the card has expired, false if not
     */
    public boolean isExpired() {
        if (!hasExpirationDate()) {
            return true;
        } else {
            try {
                Date exp = getExpirationDateAsDate();
                if (exp == null) {
                    return true;
                }
                Date now = new Date();
                if (exp.before(now)) {
                    return true;
                } else {
                    return false;
                }
            } catch (Exception e){
                return true;
            }
        }
    }


}
