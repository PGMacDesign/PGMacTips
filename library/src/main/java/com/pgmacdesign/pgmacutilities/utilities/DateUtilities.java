package com.pgmacdesign.pgmacutilities.utilities;


import com.pgmacdesign.pgmacutilities.misc.PGMacUtilitiesConstants;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Date Utilities for converting and whatnot
 * Created by pmacdowell on 11/12/2015.
 */
public class DateUtilities {


    /**
     * Get the SimpleDateFormat to be used
     * @param formatType The format type to do (See PGMacUtilitiesConstants, IE DATE_YYYY_MM_DD)
     * @param delimiter delimiter to separate them. (IE / or - or ,). If null, will use nothing
     * @param locale Locale object {@link Locale} . If null, defaults to United States (US)
     * @return SimpleDateFormat
     */
    public static SimpleDateFormat getSimpleDateFormat(int formatType, String delimiter, Locale locale){

        //Object to return
        SimpleDateFormat simpleDateFormat = null;

        //Get rid of nulls
        if(delimiter == null){
            delimiter = "";
        }
        if(locale == null){
            locale = Locale.US;
        }

        //Format
        if (formatType == PGMacUtilitiesConstants.DATE_MM_DD_YYYY) {
            simpleDateFormat = new SimpleDateFormat("MM" + delimiter + "dd" + delimiter + "yyyy", locale);
        }
        if (formatType == PGMacUtilitiesConstants.DATE_MM_DD_YY) {
            simpleDateFormat = new SimpleDateFormat("MM" + delimiter + "dd" + delimiter + "yy", locale);
        }
        if (formatType == PGMacUtilitiesConstants.DATE_YYYY_MM_DD) {
            simpleDateFormat = new SimpleDateFormat("yyyy" + delimiter + "MM" + delimiter + "dd", locale);
        }
        if (formatType == PGMacUtilitiesConstants.DATE_MM_DD) {
            simpleDateFormat = new SimpleDateFormat("MM" + delimiter + "dd", locale);
        }
        if (formatType == PGMacUtilitiesConstants.DATE_MM_YY) {
            simpleDateFormat = new SimpleDateFormat("MM" + delimiter + "yy", locale);
        }
        if (formatType == PGMacUtilitiesConstants.DATE_MM_YYYY) {
            simpleDateFormat = new SimpleDateFormat("MM" + delimiter + "yyyy", locale);
        }
        if (formatType == PGMacUtilitiesConstants.DATE_MM_DD_YYYY_HH_MM) {
            simpleDateFormat = new SimpleDateFormat("MM" + delimiter + "dd" + delimiter + "yyyy" + " HH:mm", locale);
        }
        if(formatType == PGMacUtilitiesConstants.DATE_YYYY_MM_DD_T_HH_MM_SS_SSS_Z){
            simpleDateFormat = new SimpleDateFormat("yyyy" + delimiter + "MM" + delimiter + "dd" + "'T'" + " HH:mm:ss.SSS'Z", locale);
        }
        if(formatType == PGMacUtilitiesConstants.DATE_YYYY_MM_DD_T_HH_MM_SS_Z){
            simpleDateFormat = new SimpleDateFormat("yyyy" + delimiter + "MM" + delimiter + "dd" + "'T'" + " HH:mm:ss'Z", locale);
        }

        return simpleDateFormat;
    }
    
    /**
     * Formats a date into a String
     * @param date The date to be converted
     * @param formatType The format type to do (See PGMacUtilitiesConstants, IE DATE_YYYY_MM_DD)
     * @param delimiter delimiter to separate them. (IE / or - or ,). If null, will use /
     * @param locale Locale object {@link Locale} . If null, defaults to United States (US)
     * @return Return a String of the converted date
     */
    public static String convertDateToString(Date date, int formatType, String delimiter, Locale locale){
        if(date == null){
            return null;
        }
        if(delimiter == null){
            delimiter = "/";
        }
        if(locale == null){
            locale = Locale.US;
        }
        SimpleDateFormat simpleDateFormat = getSimpleDateFormat(formatType, delimiter, locale);
        String convertedString = null;
        //simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
        try {
            if (formatType == PGMacUtilitiesConstants.DATE_MM_DD_YYYY) {
                convertedString = simpleDateFormat.format(date);
            }
            if (formatType == PGMacUtilitiesConstants.DATE_MM_DD_YY) {
                convertedString = simpleDateFormat.format(date);
            }
            if (formatType == PGMacUtilitiesConstants.DATE_YYYY_MM_DD) {
                convertedString = simpleDateFormat.format(date);
            }
            if (formatType == PGMacUtilitiesConstants.DATE_MM_DD) {
                convertedString = simpleDateFormat.format(date);
            }
            if (formatType == PGMacUtilitiesConstants.DATE_MM_YY) {
                convertedString = simpleDateFormat.format(date);
            }
            if (formatType == PGMacUtilitiesConstants.DATE_MM_YYYY) {
                convertedString = simpleDateFormat.format(date);
            }
            if (formatType == PGMacUtilitiesConstants.DATE_MM_DD_YYYY_HH_MM) {
                convertedString = simpleDateFormat.format(date);
            }
            if (formatType == PGMacUtilitiesConstants.DATE_MILLISECONDS) {
                long millis = date.getTime();
                convertedString = Long.toString(millis);
            }
            return convertedString;
        //} catch (ParseException e1){
            //return "Unable to Parse Date";
        } catch (Exception e){
            e.printStackTrace();
        }
        //If nothing, return the date to String
        return date.toString();
    }

    /**
     * Converts a String object into a Date object by parsing
     * @param strDate The string to be converted
     * @return A date object determined by parsing the String
     */
    public static Date convertStringToDate(String strDate, int formatType, String delimiter, Locale locale) {
        if(locale == null){
            locale = Locale.US;
        }
        SimpleDateFormat simpleDateFormat = getSimpleDateFormat(formatType, delimiter, locale);
        try {
            return simpleDateFormat.parse(strDate);
        } catch(ParseException e) {
            e.printStackTrace();
            return null;
        } catch(Exception e) {
            return null;
        }
    }


    /**
     * Get the current Date
     * @return Date object
     */
    public static Date getCurrentDate(){
        return Calendar.getInstance().getTime();
    }

    /**
     * Returns the date
     * @param timestamp This is the long milliseconds date since epoch
     * @return Returns a date object
     */
    public static Date getDate(Long timestamp) {
        if(timestamp == null)
            return null;
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timestamp);
        return cal.getTime();
    }

    /**
     * Get the age of the user. Takes in their birthday and calculates it according to today's date
     * @param birthday Date Object
     * @return Returns an int of their age (IE 20, 55, 18). If the date is in the future, it will
     *         return -1 instead.
     */
    public static int getAge(Date birthday){

        Calendar now = Calendar.getInstance();
        Calendar dob = Calendar.getInstance();
        dob.setTime(birthday);

        //First check for in the future:
        if (dob.after(now)) {
            return -1;
        }

        int year1 = now.get(Calendar.YEAR);
        int year2 = dob.get(Calendar.YEAR);

        int age = year1 - year2;

        int month1 = now.get(Calendar.MONTH);
        int month2 = dob.get(Calendar.MONTH);

        if (month2 > month1) {
            age--;

        } else if (month1 == month2) {
            int day1 = now.get(Calendar.DAY_OF_MONTH);
            int day2 = dob.get(Calendar.DAY_OF_MONTH);
            if (day2 > day1) {
                age--;
            }
        }

        return age;
    }

    /**
     * @return Return current day timestamp with YY, MM, dd info ONLY
     */
    public static Long getCurrentDayTS() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }

    /**
     * @return Return current day timestamp with YY, MM, dd info ONLY
     */
    public static Long getCurrentDateLong() {
        Date date = new Date();
        long ss = date.getTime();
        return ss;
    }

    /**
     * @return Return current year (IE 2015, 2016)
     */
    public static int getCurrentMonth() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.MONTH);
        year += 1; //January returns 0, December returns 11. Add one to return
        return year;
    }

    /**
     * @return Return current year (IE 2015, 2016)
     */
    public static int getCurrentYear() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        return year;
    }

    /**
     * Calculates the current day of the year and returns an int
     * @return Day - Day of the year. No time info.
     */
    public static int getCurrentDay() {
        return Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
    }

    /**
     * Same as the getCurrentDay() method but uses the passed in argument
     * as a long milliseconds timestamp to calculate vs a day object
     * @parma timestamp This is the time (in milliseconds from the epoch date)
     * used to calculate the current day of the year. (IE 365 = Dec 31st)
     * @return Day Day of the year based on timestamp.
     */
    public static Integer getCurrentDay(long timestamp) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timestamp);
        return cal.get(Calendar.DAY_OF_YEAR);
    }

    /**
     * Returns the day of the year (IE 365 = Dec 31st)
     * @param date This is the date to use to calculate the day of the year
     * @return Day - Day of the year based on date.
     */
    public static Integer getCurrentDay(Date date) {
        Calendar cal = Calendar.getInstance();
        if(date != null)
            cal.setTime(date);
        return cal.get(Calendar.DAY_OF_YEAR);
    }

    /**
     * Determines the Date "date" adjusted by the number of hours passed in.
     * A Negative int will go back a few hours. This always uses the current
     * date/ time as opposed to the getDateAdjustedByDays() method below which
     * uses the passed in Date argument as the base to use.
     * @param hrs Number of hours to adjust by
     * @return Returns a date object
     */
    public static Date getDateAdjustedByHours(int hrs) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.HOUR_OF_DAY, hrs);
        return cal.getTime();
    }

    /**
     * Determines the Date "date" adjusted by "days".
     * A negative value will return a date in the past.
     * @param date The date to be adjusted
     * @param days The number of days to adjust by
     * @return Returns a date object
     */
    public static Date getDateAdjustedByDays(Date date, int days) {
        Calendar cal = Calendar.getInstance();
        if(date != null)
            cal.setTime(date);
        cal.add(Calendar.DAY_OF_YEAR, days);
        return cal.getTime();
    }

    /**
     * Return the difference between 2 dates (dayTwo - dayOne) in days
     * @param dayTwo The first day to compare against
     * @param dayOne The second day to compare against
     * @return an int of the difference between the 2 days
     */
    public static int diffDays(Date dayTwo, Date dayOne) {
        Calendar calOne = Calendar.getInstance();
        calOne.setTime(dayOne);
        long msOne = calOne.getTimeInMillis();

        Calendar calTwo = Calendar.getInstance();
        calTwo.setTime(dayTwo);
        long msTwo = calTwo.getTimeInMillis();

        return (int) ((msTwo - msOne) / (1000L * 60L * 60L * 24L));
    }

    /**
     * Checks start and end dates to compare which came first
     * @param startDate start date of event in Date object format
     * @param endDate end date of event in Date object format
     * @return True if first date BEFORE second date OR second date is NULL
     */
    public static boolean before(Date startDate, Date endDate) {
        if((startDate == null) && (endDate == null))
            return true;
        if((startDate == null) && (endDate != null))
            return false;
        if((startDate != null) && (endDate == null))
            return true;
        return startDate.before(endDate);
    }

    /**
     * For when I need a quick date in the year 1985. Don't judge me, I get lazy and don't
     * want to type my birthday multiple times.
     * @return Date object
     */
    public static Date get1985(){
        Calendar calendar = Calendar.getInstance();
        calendar.set(1985, 10, 8, 11, 11, 11);
        Date date = calendar.getTime();
        return date;
    }

    /**
     * My son's birthday! This serves no purpose OTHER than a shoutout to him, love ya kid.
     * @return Date object
     */
    public static Date getLiamsBirthday(){
        Calendar calendar = Calendar.getInstance();
        calendar.set(2016, 7, 4, 12, 29, 30);
        Date date = calendar.getTime();
        return date;
    }

    /**
     * Used to compare the start and end dates
     * @param startDateStr start date of event in Date object format
     * @param endDateStr end date of event in Date object format
     * @return Returns true if the first date is
     * BEFORE second date OR second date is NULL
     */
    public static boolean before(String startDateStr, String endDateStr, int formatType, String delimiter, Locale locale){
        //Get rid of nulls
        if(delimiter == null){
            delimiter = "/";
        }
        if(locale == null){
            locale = Locale.US;
        }
        if((startDateStr == null) || (startDateStr.isEmpty()))
            return false;

        if((endDateStr == null) || (endDateStr.isEmpty()))
            return true;

        Date startDate = convertStringToDate(startDateStr, formatType, delimiter, locale);
        Date endDate = convertStringToDate(endDateStr, formatType, delimiter, locale);

        return before(startDate, endDate);
    }
}
