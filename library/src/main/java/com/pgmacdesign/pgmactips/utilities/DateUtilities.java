package com.pgmacdesign.pgmactips.utilities;


import com.pgmacdesign.pgmactips.misc.PGMacUtilitiesConstants;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Date Utilities for converting and whatnot
 * Created by pmacdowell on 11/12/2015.
 */
public class DateUtilities {

    private static final long SECOND_MILLIS = PGMacUtilitiesConstants.ONE_SECOND;
    private static final long MINUTE_MILLIS = PGMacUtilitiesConstants.ONE_MINUTE;
    private static final long HOUR_MILLIS = PGMacUtilitiesConstants.ONE_HOUR;
    private static final long DAY_MILLIS = PGMacUtilitiesConstants.ONE_DAY;
    private static final long MONTH_MILLIS = PGMacUtilitiesConstants.ONE_MONTH;


    /**
     * Copyright 2012 Google Inc. (Apache License)
     * From: https://stackoverflow.com/a/13018647/2480714
     * @param time Time
     * @return
     */
    public static String getTimeAgo(long time) {
        if (time < 1000000000000L) {
            // if timestamp given in seconds, convert to millis
            time *= 1000;
        }

        long now = System.currentTimeMillis();
        if (time > now || time <= 0) {
            return null;
        }

        final long diff = now - time;
        if (diff < MINUTE_MILLIS) {
            return "just now";
        } else if (diff < 2 * MINUTE_MILLIS) {
            return "a minute ago";
        } else if (diff < 50 * MINUTE_MILLIS) {
            return diff / MINUTE_MILLIS + " minutes ago";
        } else if (diff < 90 * MINUTE_MILLIS) {
            return "an hour ago";
        } else if (diff < 24 * HOUR_MILLIS) {
            return diff / HOUR_MILLIS + " hours ago";
        } else if (diff < 48 * HOUR_MILLIS) {
            return "yesterday";
        } else if((diff < 30 * DAY_MILLIS)){
            return diff / DAY_MILLIS + " days ago";
        } else if (diff >= 30 * DAY_MILLIS && diff < 60 * DAY_MILLIS){
            return "a month ago";
        } else if (diff >= 60 * DAY_MILLIS && diff < 365 * DAY_MILLIS){
            return diff / MONTH_MILLIS + " months ago";
        } else {
            return "years ago";
        }
    }

    /**
     * Get the SimpleDateFormat to be used
     *
     * @param formatType The format type to do (See PGMacUtilitiesConstants, IE DATE_YYYY_MM_DD)
     * @param delimiter  delimiter to separate them. (IE / or - or ,). If null, will use nothing
     * @param locale     Locale object {@link Locale} . If null, defaults to United States (US)
     * @return SimpleDateFormat
     */
    public static SimpleDateFormat getSimpleDateFormat(int formatType, String delimiter, Locale locale) {

        //Object to return
        SimpleDateFormat simpleDateFormat = null;

        //Get rid of nulls
        if (delimiter == null) {
            delimiter = "";
        }
        if (locale == null) {
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
        if (formatType == PGMacUtilitiesConstants.DATE_YYYY_MM_DD_T_HH_MM_SS_SSS_Z) {
            simpleDateFormat = new SimpleDateFormat("yyyy" + delimiter + "MM" + delimiter + "dd" + "'T'" + " HH:mm:ss.SSS'Z", locale);
        }
        if (formatType == PGMacUtilitiesConstants.DATE_YYYY_MM_DD_T_HH_MM_SS_Z) {
            simpleDateFormat = new SimpleDateFormat("yyyy" + delimiter + "MM" + delimiter + "dd" + "'T'" + " HH:mm:ss'Z", locale);
        }

        return simpleDateFormat;
    }

    /**
     * Formats a date into a String
     *
     * @param date       The date to be converted
     * @param formatType The format type to do (See PGMacUtilitiesConstants, IE DATE_YYYY_MM_DD)
     * @param delimiter  delimiter to separate them. (IE / or - or ,). If null, will use /
     * @param locale     Locale object {@link Locale} . If null, defaults to United States (US)
     * @return Return a String of the converted date
     */
    public static String convertDateToString(Date date, int formatType, String delimiter, Locale locale) {
        if (date == null) {
            return null;
        }
        if (delimiter == null) {
            delimiter = "/";
        }
        if (locale == null) {
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        //If nothing, return the date to String
        return date.toString();
    }

    /**
     * Converts a String object into a Date object by parsing
     *
     * @param strDate The string to be converted
     * @return A date object determined by parsing the String
     */
    public static Date convertStringToDate(String strDate, int formatType, String delimiter, Locale locale) {
        if (locale == null) {
            locale = Locale.US;
        }
        SimpleDateFormat simpleDateFormat = getSimpleDateFormat(formatType, delimiter, locale);
        try {
            return simpleDateFormat.parse(strDate);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            return null;
        }
    }


    /**
     * Get the current Date
     *
     * @return Date object
     */
    public static Date getCurrentDate() {
        return Calendar.getInstance().getTime();
    }

    /**
     * Returns the date
     *
     * @param timestamp This is the long milliseconds date since epoch
     * @return Returns a date object
     */
    public static Date getDate(Long timestamp) {
        if (timestamp == null)
            return null;
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timestamp);
        return cal.getTime();
    }

    /**
     * Get the age of the user. Takes in their birthday and calculates it according to today's date
     *
     * @param birthday Date Object
     * @return Returns an int of their age (IE 20, 55, 18). If the date is in the future, it will
     * return -1 instead.
     */
    public static int getAge(Date birthday) {

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
     * Quick calculator to determine if person is >= age of 18
     *
     * @param dob Date of birth
     * @return
     */
    public static boolean isAge18OrOlder(Date dob) {
        int age = getAge(dob);
        if (age >= 18) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Quick calculator to determine if person is >= age of 18
     *
     * @param dob Date of birth
     * @return
     */
    public static boolean isAge18OrOlder(Calendar dob) {
        int age = getAge(dob);
        if (age >= 18) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Quick calculator to determine if person is >= age of 21
     *
     * @param dob Date of birth
     * @return
     */
    public static boolean isAge21OrOlder(Date dob) {
        int age = getAge(dob);
        if (age >= 21) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Quick calculator to determine if person is >= age of 21
     *
     * @param dob Date of birth
     * @return
     */
    public static boolean isAge21OrOlder(Calendar dob) {
        int age = getAge(dob);
        if (age >= 21) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Quick calculator to get a person's age (as an int)
     *
     * @param dob Date of birth
     * @return
     */
    public static int getAge(Calendar dob) {
        Calendar now = Calendar.getInstance();
        int age = 0;
        int year1 = now.get(Calendar.YEAR);
        int year2 = dob.get(Calendar.YEAR);
        age = year1 - year2;
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
     *
     * @return Day - Day of the year. No time info.
     */
    public static int getCurrentDay() {
        return Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
    }

    /**
     * Same as the getCurrentDay() method but uses the passed in argument
     * as a long milliseconds timestamp to calculate vs a day object
     *
     * @return Day Day of the year based on timestamp.
     * @parma timestamp This is the time (in milliseconds from the epoch date)
     * used to calculate the current day of the year. (IE 365 = Dec 31st)
     */
    public static Integer getCurrentDay(long timestamp) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timestamp);
        return cal.get(Calendar.DAY_OF_YEAR);
    }

    /**
     * Returns the day of the year (IE 365 = Dec 31st)
     *
     * @param date This is the date to use to calculate the day of the year
     * @return Day - Day of the year based on date.
     */
    public static Integer getCurrentDay(Date date) {
        Calendar cal = Calendar.getInstance();
        if (date != null)
            cal.setTime(date);
        return cal.get(Calendar.DAY_OF_YEAR);
    }

    /**
     * Determines the Date "date" adjusted by the number of hours passed in.
     * A Negative int will go back a few hours. This always uses the current
     * date/ time as opposed to the getDateAdjustedByDays() method below which
     * uses the passed in Date argument as the base to use.
     *
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
     *
     * @param date The date to be adjusted
     * @param days The number of days to adjust by
     * @return Returns a date object
     */
    public static Date getDateAdjustedByDays(Date date, int days) {
        Calendar cal = Calendar.getInstance();
        if (date != null)
            cal.setTime(date);
        cal.add(Calendar.DAY_OF_YEAR, days);
        return cal.getTime();
    }

    /**
     * Return the difference between 2 dates (dayTwo - dayOne) in days
     *
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
     *
     * @param startDate start date of event in Date object format
     * @param endDate   end date of event in Date object format
     * @return True if first date BEFORE second date OR second date is NULL
     */
    public static boolean before(Date startDate, Date endDate) {
        if ((startDate == null) && (endDate == null))
            return true;
        if ((startDate == null) && (endDate != null))
            return false;
        if ((startDate != null) && (endDate == null))
            return true;
        return startDate.before(endDate);
    }

    /**
     * Get Day of the week (IE: Monday, Tuesday, Wednesday, Thursday, Friday, Saturday, Sunday)
     * @return
     */
    public static String getDayOfWeek(){
        Calendar calendar = Calendar.getInstance();
        if(calendar != null) {
            return getDayOfWeek(calendar.get(Calendar.DAY_OF_WEEK));
        } else {
            return null;
        }
    }

    /**
     * Get the day of the week short (IE: Mon, Tues, Wed, Thur, Fri, Sat, Sun
     * @return String
     */
    public static String getDayOfWeekShort(){
        Calendar calendar = Calendar.getInstance();
        if(calendar != null) {
            return getDayOfWeekShort(calendar.get(Calendar.DAY_OF_WEEK));
        } else {
            return null;
        }
    }

    /**
     * Get the day of the week from a calendar instance int
     * @param calendarDayOfWeek {@link Calendar#DAY_OF_WEEK}
     * @return String, day of the week
     */
    public static String getDayOfWeek(int calendarDayOfWeek){
        String dayOfWeek;
        switch (calendarDayOfWeek){
            case Calendar.MONDAY:
                dayOfWeek = "Monday";
                break;
            case Calendar.TUESDAY:
                dayOfWeek = "Tuesday";
                break;
            case Calendar.WEDNESDAY:
                dayOfWeek = "Wednesday";
                break;
            case Calendar.THURSDAY:
                dayOfWeek = "Thursday";
                break;
            case Calendar.FRIDAY:
                dayOfWeek = "Friday";
                break;
            case Calendar.SATURDAY:
                dayOfWeek = "Saturday";
                break;
            case Calendar.SUNDAY:
                dayOfWeek = "Sunday";
                break;
            default:
                dayOfWeek = "Unknown";
        }
        return dayOfWeek;
    }

    /**
     * Get the day of the week from a calendar instance int
     * @param calendarDayOfWeek {@link Calendar#DAY_OF_WEEK}
     * @return String, day of the week
     */
    private static String getDayOfWeekShort(int calendarDayOfWeek){
        String dayOfWeek;
        switch (calendarDayOfWeek){
            case Calendar.MONDAY:
                dayOfWeek = "Mon";
                break;
            case Calendar.TUESDAY:
                dayOfWeek = "Tues";
                break;
            case Calendar.WEDNESDAY:
                dayOfWeek = "Wed";
                break;
            case Calendar.THURSDAY:
                dayOfWeek = "Thur";
                break;
            case Calendar.FRIDAY:
                dayOfWeek = "Fri";
                break;
            case Calendar.SATURDAY:
                dayOfWeek = "Sat";
                break;
            case Calendar.SUNDAY:
                dayOfWeek = "Sun";
                break;
            default:
                dayOfWeek = "Unknown";
        }
        return dayOfWeek;
    }

    /**
     * Get the month of the year from a calendar instance
     * @return String, month of the year (IE: January, February, March, April, May, June,
     *          July, August, September, October, November, December)
     */
    public static String getMonthOfYear(){
        Calendar calendar = Calendar.getInstance();
        if(calendar != null){
            return getMonthOfYear(calendar.get(Calendar.MONTH));
        } else {
            return null;
        }
    }

    /**
     * Get the month of the year from a calendar instance int
     * @param calendarMonth {@link Calendar#MONTH}
     * @return String, month of the year (IE: January, February, March, April, May, June,
     *          July, August, September, October, November, December)
     */
    public static String getMonthOfYear(int calendarMonth){
        String monthOfTheYear;
        switch (calendarMonth){
            case Calendar.JANUARY:
                monthOfTheYear = "January";
                break;
            case Calendar.FEBRUARY:
                monthOfTheYear = "February";
                break;
            case Calendar.MARCH:
                monthOfTheYear = "March";
                break;
            case Calendar.APRIL:
                monthOfTheYear = "April";
                break;
            case Calendar.MAY:
                monthOfTheYear = "May";
                break;
            case Calendar.JUNE:
                monthOfTheYear = "June";
                break;
            case Calendar.JULY:
                monthOfTheYear = "July";
                break;
            case Calendar.AUGUST:
                monthOfTheYear = "August";
                break;
            case Calendar.SEPTEMBER:
                monthOfTheYear = "September";
                break;
            case Calendar.OCTOBER:
                monthOfTheYear = "October";
                break;
            case Calendar.NOVEMBER:
                monthOfTheYear = "November";
                break;
            case Calendar.DECEMBER:
                monthOfTheYear = "December";
                break;
            default:
                monthOfTheYear = "Unknown";
                break;
        }
        return monthOfTheYear;
    }

    /**
     * Get the month of the year from a calendar instance int
     * @return String, month of the year (IE: Jan, Feb, Mar, Apr, May, Jun, Jul, Aug,
     *          Sep, Oct, Nov, Dec)
     */
    public static String getMonthOfYearShort(){
        Calendar calendar = Calendar.getInstance();
        if(calendar != null){
            return getMonthOfYearShort(calendar.get(Calendar.MONTH));
        } else {
            return null;
        }
    }

    /**
     * Get the month of the year from a calendar instance int
     * @param calendarMonth {@link Calendar#MONTH}
     * @return String, month of the year (IE: Jan, Feb, Mar, Apr, May, Jun, Jul, Aug,
     *          Sep, Oct, Nov, Dec)
     */
    private static String getMonthOfYearShort(int calendarMonth){
        String monthOfTheYear;
        switch (calendarMonth){
            case Calendar.JANUARY:
                monthOfTheYear = "Jan";
                break;
            case Calendar.FEBRUARY:
                monthOfTheYear = "Feb";
                break;
            case Calendar.MARCH:
                monthOfTheYear = "Mar";
                break;
            case Calendar.APRIL:
                monthOfTheYear = "Apr";
                break;
            case Calendar.MAY:
                monthOfTheYear = "May";
                break;
            case Calendar.JUNE:
                monthOfTheYear = "Jun";
                break;
            case Calendar.JULY:
                monthOfTheYear = "Jul";
                break;
            case Calendar.AUGUST:
                monthOfTheYear = "Aug";
                break;
            case Calendar.SEPTEMBER:
                monthOfTheYear = "Sep";
                break;
            case Calendar.OCTOBER:
                monthOfTheYear = "Oct";
                break;
            case Calendar.NOVEMBER:
                monthOfTheYear = "Nov";
                break;
            case Calendar.DECEMBER:
                monthOfTheYear = "Dec";
                break;
            default:
                monthOfTheYear = "Unknown";
                break;
        }
        return monthOfTheYear;
    }

    /**
     * Builds list of Strings like this: "Tuesday October 25" and increment to the next
     * one of "Wednesday October 26".
     * @param cal Calendar to use for incrementing. If left null, it will
     *                 create a new calendar instance at the time of this call
     * @param numberOfDaysToIncrement Number of times / days to increment
     * @return
     */
    public static List<String> buildSequentialDateStrings(Calendar cal,
                                                          int numberOfDaysToIncrement){
        if(numberOfDaysToIncrement <= 0){
            return null;
        }
        List<String> toReturn = new ArrayList<>();
        if(cal == null) {
            cal = Calendar.getInstance();
        }
        for(int i = 0; i < numberOfDaysToIncrement; i++){
            int dayOfWeekInt = cal.get(Calendar.DAY_OF_WEEK);
            int dayOfMonthInt = cal.get(Calendar.DAY_OF_MONTH);
            int monthOfYearInt = cal.get(Calendar.MONTH);
            String monthOfYear = getMonthOfYear(monthOfYearInt);
            String dayOfWeek = getDayOfWeek(dayOfWeekInt);
            String str = dayOfWeek + " " + monthOfYear + " " + dayOfMonthInt;
            toReturn.add(str);
            cal.add(Calendar.DAY_OF_MONTH, 1);
        }
        return toReturn;
    }

    /**
     * For when I need a quick date in the year 1985. Don't judge me, I get lazy and don't
     * want to type my birthday multiple times.
     *
     * @return Date object
     */
    public static Date get1985() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(1985, 10, 8, 11, 11, 11);
        return calendar.getTime();
    }

    /**
     * My son's birthday! This serves no purpose OTHER than a shoutout to him, love ya kid.
     *
     * @return Date object
     */
    public static Date getLiamsBirthday() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2016, 7, 4, 12, 29, 30);
        return calendar.getTime();
    }

    /**
     * Used to compare the start and end dates
     *
     * @param startDateStr start date of event in Date object format
     * @param endDateStr   end date of event in Date object format
     * @return Returns true if the first date is
     * BEFORE second date OR second date is NULL
     */
    public static boolean before(String startDateStr, String endDateStr, int formatType, String delimiter, Locale locale) {
        //Get rid of nulls
        if (delimiter == null) {
            delimiter = "/";
        }
        if (locale == null) {
            locale = Locale.US;
        }
        if ((startDateStr == null) || (startDateStr.isEmpty()))
            return false;

        if ((endDateStr == null) || (endDateStr.isEmpty()))
            return true;

        Date startDate = convertStringToDate(startDateStr, formatType, delimiter, locale);
        Date endDate = convertStringToDate(endDateStr, formatType, delimiter, locale);

        return before(startDate, endDate);
    }

    public static String convert24HourTo12Hour(int hour, int minute){
        return convert24HourTo12Hour(hour + "", minute + "");
    }

    public static String convert12HourTo24Hour(int hour, int minute, boolean isAm){
        return convert12HourTo24Hour(hour + "", minute + "", isAm);
    }

    public static String convert24HourTo12Hour(String hour, String minute){
        String str = hour + ":" + minute;
        SimpleDateFormat time24 = new SimpleDateFormat("HH:mm");
        SimpleDateFormat time12 = new SimpleDateFormat("hh:mm a");
        try {
            Date date = time24.parse(str);
            return time12.format(date);
        } catch (Exception e){
            return null;
        }
    }

    public static String convert12HourTo24Hour(String hour, String minute, boolean isAm){
        String ampm;
        if(isAm){
            ampm = "AM";
        } else {
            ampm = "PM";
        }
        String str = hour + ":" + minute + " " + ampm;
        SimpleDateFormat time24 = new SimpleDateFormat("HH:mm");
        SimpleDateFormat time12 = new SimpleDateFormat("hh:mm a");
        try {
            Date date = time12.parse(str);
            return time24.format(date);
        } catch (Exception e){
            return null;
        }
    }

}
