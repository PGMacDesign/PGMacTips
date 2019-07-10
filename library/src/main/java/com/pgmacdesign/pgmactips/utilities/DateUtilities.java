package com.pgmacdesign.pgmactips.utilities;


import com.pgmacdesign.pgmactips.misc.PGMacTipsConstants;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import androidx.annotation.FloatRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Date Utilities for converting and whatnot
 * Created by pmacdowell on 11/12/2015.
 */
public class DateUtilities {

    private static final long SECOND_MILLIS = PGMacTipsConstants.ONE_SECOND;
    private static final long MINUTE_MILLIS = PGMacTipsConstants.ONE_MINUTE;
    private static final long HOUR_MILLIS = PGMacTipsConstants.ONE_HOUR;
    private static final long DAY_MILLIS = PGMacTipsConstants.ONE_DAY;
    private static final long MONTH_MILLIS = PGMacTipsConstants.ONE_MONTH;
	
    //region Simple Millisecond Converters
	
	/**
	 *
	 * @param milliseconds Milliseconds to convert
	 * @param useCommasToSeparate Use commas to separate, will use space otherwise
	 * @param useTitleCaseForStrings Use title case (First letter is capitalized, IE Year vs year)
	 * @return String formatted like this: "49 Years, 35 Weeks, 5 Days, 21 Hours, 16 Minutes, 53 Seconds" or
	 *         String formatted like this: "1 Year 3 Weeks 1 Hour 16 Minutes"
	 */
	public static String convertMillisecondsToTimeString(long milliseconds, boolean useCommasToSeparate, boolean useTitleCaseForStrings){
		StringBuilder sb = new StringBuilder();
		long seconds = milliseconds / 1000;
		long minutes = seconds / 60;
		long hours = minutes / 60;
		long days = hours / 24;
		long weeks = days / 7;
		long years = weeks / 52;
		
		//Years
		if(years > 0){
			sb.append((years));
			sb.append(" ");
			sb.append((useTitleCaseForStrings) ? "Year" : "year");
			if(years > 1){
				sb.append("s");
			}
			sb.append((useCommasToSeparate) ? ", " : " ");
		}
		
		//Weeks
		if((weeks % 52) > 0){
			sb.append((weeks % 52));
			sb.append(" ");
			sb.append((useTitleCaseForStrings) ? "Week" : "week");
			if((weeks % 52) > 1){
				sb.append("s");
			}
			sb.append((useCommasToSeparate) ? ", " : " ");
		}
		
		//Days
		if((days % 7) > 0){
			sb.append((days % 7));
			sb.append(" ");
			sb.append((useTitleCaseForStrings) ? "Day" : "day");
			if((days % 7) > 1){
				sb.append("s");
			}
			sb.append((useCommasToSeparate) ? ", " : " ");
		}
		
		//Hours
		if((hours % 24) > 0){
			sb.append((hours % 24));
			sb.append(" ");
			sb.append((useTitleCaseForStrings) ? "Hour" : "hour");
			if((hours % 24) > 1){
				sb.append("s");
			}
			sb.append((useCommasToSeparate) ? ", " : " ");
		}
		
		//Minutes
		if((minutes % 60) > 0){
			sb.append((minutes % 60));
			sb.append(" ");
			sb.append((useTitleCaseForStrings) ? "Minute" : "minute");
			if((minutes % 60) > 1){
				sb.append("s");
			}
			sb.append((useCommasToSeparate) ? ", " : " ");
		}
		
		//Seconds
		if((seconds % 60) > 0){
			sb.append((seconds % 60));
			sb.append(" ");
			sb.append((useTitleCaseForStrings) ? "Second" : "second");
			if((seconds % 60) > 1){
				sb.append("s");
			}
			sb.append((useCommasToSeparate) ? ", " : " ");
		}
		
		String timeString = sb.toString();
		if(StringUtilities.isNullOrEmpty(timeString)){
			timeString = "";
		}
		if(timeString.endsWith(", ")){
			timeString = timeString.substring(0, (timeString.length()-2));
		}
		return timeString.trim();
	}
	
	/**
	 * Build and return a String representing the number of milliseconds left matching the variable
	 * passed. Ideally used for countdown timers or the like
	 * @param milliseconds Number of milliseconds to convert
	 * @return String formatted like this: "49 Years, 35 Weeks, 5 Days, 21 Hours, 16 Minutes, 53 Seconds"
	 */
	public static String convertMillisecondsToTimeString(long milliseconds){
    	return DateUtilities.convertMillisecondsToTimeString(milliseconds, true, true);
    }
    
	//endregion
	
	//region Time Estimates
	
	/**
	 * Calculate the estimated time remaining based on the percent complete and the time elapsed using proportions
	 * @param startTime
	 * @param percentComplete
	 * @return
	 */
	public static long estimateTimeRemaining(long startTime, @FloatRange(from = 0.0, to = 1.0) float percentComplete){
		
		long now = System.currentTimeMillis();
		long timeSinceStart = now - startTime;
		if(percentComplete <= 0 || percentComplete >= 1 || startTime >= now){
			return 0;
		}
		float lengthOfTimeToComplete = (((float)timeSinceStart) / percentComplete);
		return (startTime + (long)lengthOfTimeToComplete - now);
	}
	
	//endregion
	
	
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
        } else if (diff < 120 * MINUTE_MILLIS) {
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
     * @param formatType The format type to do (See PGMacTipsConstants, IE DATE_YYYY_MM_DD)
     * @param delimiter  delimiter to separate them. (IE / or - or ,). If null, will use nothing
     * @param locale     Locale object {@link Locale} . If null, defaults to United States (US)
     * @return SimpleDateFormat
     */
    public static SimpleDateFormat getSimpleDateFormat(int formatType, @Nullable String delimiter,
                                                       @Nullable Locale locale) {

        //Object to return
        SimpleDateFormat simpleDateFormat = null;

        //Get rid of nulls
        if (delimiter == null) {
            delimiter = "/";
        }
        if (locale == null) {
            locale = Locale.US;
        }

        try {
            //Format
            if (formatType == PGMacTipsConstants.DATE_MM_DD_YYYY) {
                simpleDateFormat = new SimpleDateFormat("MM" + delimiter + "dd" + delimiter + "yyyy", locale);
            }
            if (formatType == PGMacTipsConstants.DATE_MM_DD_YY) {
                simpleDateFormat = new SimpleDateFormat("MM" + delimiter + "dd" + delimiter + "yy", locale);
            }
            if (formatType == PGMacTipsConstants.DATE_YYYY_MM_DD) {
                simpleDateFormat = new SimpleDateFormat("yyyy" + delimiter + "MM" + delimiter + "dd", locale);
            }
            if (formatType == PGMacTipsConstants.DATE_MM_DD) {
                simpleDateFormat = new SimpleDateFormat("MM" + delimiter + "dd", locale);
            }
            if (formatType == PGMacTipsConstants.DATE_MM_YY) {
                simpleDateFormat = new SimpleDateFormat("MM" + delimiter + "yy", locale);
            }
            if (formatType == PGMacTipsConstants.DATE_MM_YYYY) {
                simpleDateFormat = new SimpleDateFormat("MM" + delimiter + "yyyy", locale);
            }
            if (formatType == PGMacTipsConstants.DATE_MM_DD_YYYY_HH_MM) {
                simpleDateFormat = new SimpleDateFormat("MM" + delimiter + "dd" + delimiter + "yyyy" + " HH:mm", locale);
            }
            if (formatType == PGMacTipsConstants.DATE_YYYY_MM_DD_T_HH_MM_SS_SSS_Z) {
                simpleDateFormat = new SimpleDateFormat("yyyy" + delimiter + "MM" + delimiter + "dd" + "'T'" + " HH:mm:ss.SSS'Z", locale);
            }
            if (formatType == PGMacTipsConstants.DATE_EEEE_MMM_dd_HH_mm_ss_z_yyyy) {
                simpleDateFormat = new SimpleDateFormat("EEEE MMM dd HH:mm:ss z yyyy", locale);
            }
            if (formatType == PGMacTipsConstants.DATE_YYYY_MM_DD_T_HH_MM_SS_Z) {
                simpleDateFormat = new SimpleDateFormat("yyyy" + delimiter + "MM" + delimiter + "dd" + "'T'" + " HH:mm:ss'Z", locale);
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        return simpleDateFormat;
    }
    
    /**
     * Get the SimpleDateFormat to be used without a manually specified Delimiter.
     * Note, uses "/" by default
     *
     * @param formatType The format type to do (See PGMacTipsConstants, IE DATE_YYYY_MM_DD)
     * @param locale     Locale object {@link Locale} . If null, defaults to United States (US)
     * @return SimpleDateFormat
     */
    public static SimpleDateFormat getSimpleDateFormat(int formatType, @Nullable Locale locale) {
        return getSimpleDateFormat(formatType, "/", locale);
    }

    /**
     * Formats a date into a String
     *
     * @param date       The date to be converted
     * @param formatType The format type to do (See PGMacTipsConstants, IE DATE_YYYY_MM_DD)
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
            switch (formatType){
                case PGMacTipsConstants.DATE_MILLISECONDS:
                    long millis = date.getTime();
                    convertedString = Long.toString(millis);
                    break;
                    
                case PGMacTipsConstants.DATE_MM_DD_YYYY:
                case PGMacTipsConstants.DATE_MM_DD_YY:
                case PGMacTipsConstants.DATE_YYYY_MM_DD:
                case PGMacTipsConstants.DATE_MM_DD:
                case PGMacTipsConstants.DATE_MM_YY:
                case PGMacTipsConstants.DATE_MM_YYYY:
                case PGMacTipsConstants.DATE_MM_DD_YYYY_HH_MM:
                case PGMacTipsConstants.DATE_EEEE_MMM_dd_HH_mm_ss_z_yyyy:
                default:
                    convertedString = simpleDateFormat.format(date);
                    break;

                    
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
     * Attempts to convert local time to UTC with passed String date
     * @param localDateString String to attempt to convert
     * @return Converted string if successful, otherwise, will return null
     */
    public static String convertLocalTimeToUTC(@NonNull String localDateString){
        TimeZone tz = TimeZone.getDefault();
        TimeZone utc = TimeZone.getTimeZone("UTC");
        String[] delimTypes = {"-", "/", " ", null, "*", "_"};
        for(int pos : PGMacTipsConstants.ALL_DATE_TYPES){
            for(String delimiter : delimTypes) {
                try {
                    SimpleDateFormat sdf = DateUtilities.getSimpleDateFormat(pos, delimiter, null);
                    sdf.setTimeZone(tz);
                    Date date = sdf.parse(localDateString);
                    sdf.setTimeZone(utc);
                    if(sdf.format(date) != null){
                        return sdf.format(date);
                    }
                } catch (Exception e) {
                    //Do nothing failed parsing attempt
                }
            }
        }
        L.m("Could not convert local time to UTC");
        return null;
    }

    /**
     * Attempts to convert UTC to local time with passed String date
     * @param localDateString String to attempt to convert
     * @return Converted string if successful, otherwise, will return null
     */
    public static String convertUTCToLocalTime(@NonNull String localDateString){
        TimeZone tz = TimeZone.getDefault();
        TimeZone utc = TimeZone.getTimeZone("UTC");
        String[] delimTypes = {"-", "/", " ", null, "*", "_"};
        for(int pos : PGMacTipsConstants.ALL_DATE_TYPES){
            for(String delimiter : delimTypes) {
                try {
                    SimpleDateFormat sdf = DateUtilities.getSimpleDateFormat(pos, delimiter, null);
                    sdf.setTimeZone(utc);
                    Date date = sdf.parse(localDateString);
                    sdf.setTimeZone(tz);
                    if(sdf.format(date) != null){
                        return sdf.format(date);
                    }
                } catch (Exception e) {
                    //Do nothing failed parsing attempt
                }
            }
        }
        L.m("Could not convert local time to UTC");
        return null;
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
     * Simple method for getting the YYYY/MM/DD String returned
     * @return String in format YYYY/MM/DD
     */
    public static String getSimpleDate(){
        return getSimpleDate(Calendar.getInstance());
    }

    /**
     * Simple method for getting the YYYY/MM/DD String returned
     * @param date If null, will initialize new instance
     * @return String in format YYYY/MM/DD
     */
    public static String getSimpleDate(Date date){
        if(date == null){
            date = new Date();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return getSimpleDate(calendar);
    }

    /**
     * Simple method for getting the YYYY/MM/DD String returned
     * @param calendar If null, will initialize new instance
     * @return String in format YYYY/MM/DD
     */
    public static String getSimpleDate(@Nullable Calendar calendar){
        if(calendar == null){
            calendar = Calendar.getInstance();
        }
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        return year + "/" + month + "/" + day;
    }
    
    /**
     * Simple method for getting the YYYY/MM/DD String returned
     * @return String in format YYYY/MM/DD
     */
    public static String getSimpleDate2(){
        return getSimpleDate2(Calendar.getInstance());
    }
    
    /**
     * Simple method for getting the date String returned like this:
     * "Fri, 17 May 2019 21:37:48 GMT"
     * @param date If null, will initialize new instance
     * @return String in format "Fri, 17 May 2019 21:37:48 GMT"
     */
    public static String getSimpleDate2(Date date){
        if(date == null){
            date = new Date();
        }
        return getSimpleDateFormat(PGMacTipsConstants.DATE_EEEE_MMM_dd_HH_mm_ss_z_yyyy,
                Locale.getDefault()).format(date);
    }
    
    
    /**
     * Simple method for getting the YYYY/MM/DD String returned
     * @param calendar If null, will initialize new instance
     * @return String in format YYYY/MM/DD
     */
    public static String getSimpleDate2(@Nullable Calendar calendar){
        if(calendar == null){
            calendar = Calendar.getInstance();
        }
        Date date = calendar.getTime();
        return getSimpleDate2(date);
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
     * Get the year, 18 years ago
     * @return Int of year
     */
    public static int get18YearsAgoYear(){
        Calendar cal = Calendar.getInstance(TimeZone.getDefault());
        cal.add(Calendar.YEAR, -18);
        return cal.get(Calendar.YEAR);
    }

    /**
     * Get the month, 18 years ago
     * @return Int of month
     */
    public static int get18YearsAgoMonth(){
        Calendar cal = Calendar.getInstance(TimeZone.getDefault());
        cal.add(Calendar.YEAR, -18);
        return (1 + cal.get(Calendar.MONTH));
    }

    /**
     * Get the day, 18 years ago
     * @return Int of day
     */
    public static int get18YearsAgoDay(){
        Calendar cal = Calendar.getInstance(TimeZone.getDefault());
        cal.add(Calendar.YEAR, -18);
        return cal.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * Get the weekday in the past. IE, You want to know what day it was 4 years, 3
     * months and 2 days ago (--> 'Tuesday')
     * @param yearsAgo # of years ago (Positive number)
     * @param monthsAgo # of months ago (Positive number)
     * @param daysAgo # of days ago (Positive number)
     * @return String day of week. {@link DateUtilities#getDayOfWeek()}
     */
    public static String getWeekdayInPast(int yearsAgo, int monthsAgo, int daysAgo){
        Calendar cal = Calendar.getInstance(TimeZone.getDefault());
        cal.add(Calendar.YEAR, -yearsAgo);
        cal.add(Calendar.MONTH, -monthsAgo);
        cal.add(Calendar.DAY_OF_MONTH, -daysAgo);
        int weekday = cal.get(Calendar.DAY_OF_WEEK);
        return DateUtilities.getDayOfWeek(weekday);
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
     * Used to compare the start and end dates
     *
     * @param startDateStr start date of event in Date object format
     * @param endDateStr   end date of event in Date object format
     * @return Returns true if the first date is
     * BEFORE second date OR second date is NULL
     */
    public static boolean after(String startDateStr, String endDateStr, int formatType, String delimiter, Locale locale) {
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

        return after(startDate, endDate);
    }

    /**
     * Checks start and end dates to compare which came first
     *
     * @param startDate start date of event in Date object format
     * @param endDate   end date of event in Date object format
     * @return True if first date BEFORE second date OR second date is NULL
     */
    public static boolean after(Date startDate, Date endDate) {
        if ((startDate == null) && (endDate == null))
            return true;
        if ((startDate == null) && (endDate != null))
            return false;
        if ((startDate != null) && (endDate == null))
            return true;
        return startDate.after(endDate);
    }

    /**
     * Convert 24 hour to 12 hour. IE:
     * (14:11) --> (2:11 pm)
     * @param hour
     * @param minute
     * @return
     */
    public static String convert24HourTo12Hour(int hour, int minute){
        return convert24HourTo12Hour(hour + "", minute + "");
    }

    /**
     * Convert 12 hour to 24 hour. IE:
     * (2:11 pm)--> (14:11)
     * @param hour
     * @param minute
     * @param isAm
     * @return
     */
    public static String convert12HourTo24Hour(int hour, int minute, boolean isAm){
        return convert12HourTo24Hour(hour + "", minute + "", isAm);
    }

    /**
     * Convert 24 hour to 12 hour. IE:
     * (14:11) --> (2:11 pm)
     * @param hour
     * @param minute
     * @return
     */
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

    /**
     * Convert 12 hour to 24 hour. IE:
     * (2:11 pm)--> (14:11)
     * @param hour
     * @param minute
     * @param isAm
     * @return
     */
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

    /**
     * Get the first millisecond of the current month
     * @return First millisecond of month, 0 if null is passed
     */
    public static long getFirstMillisecondOfMonth(){
        return getFirstMillisecondOfMonth(Calendar.getInstance());
    }

    /**
     * Get the first millisecond of the month passed in
     * @param cal {@link Calendar}
     * @return First millisecond of month, 0 if null is passed
     */
    public static long getFirstMillisecondOfMonth(@NonNull Calendar cal){
        if(cal == null){
            return 0;
        }
        return getFirstDateTimeOfMonth(cal).getTime();
    }

    /**
     * Get the last millisecond of the current month
     * @return last millisecond of month, 0 if null is passed
     */
    public static long getLastMillisecondOfMonth(){
        return getLastMillisecondOfMonth(Calendar.getInstance());
    }

    /**
     * Get the last millisecond of the month passed in
     * @param cal {@link Calendar}
     * @return last millisecond of month, 0 if null is passed
     */
    public static long getLastMillisecondOfMonth(@NonNull Calendar cal){
        if(cal == null){
            return 0;
        }
        return getLastDateTimeOfMonth(cal).getTime();
    }

    /**
     * Get the first DateTime of the current month
     * @return First DateTime of month, new initialized date if null is passed
     */
    public static Date getFirstDateTimeOfMonth(){
        return getFirstDateTimeOfMonth(Calendar.getInstance());
    }

    /**
     * Get the first DateTime of the month passed in
     * @param cal {@link Calendar}
     * @return First DateTime of month, new initialized date if null is passed
     */
    public static Date getFirstDateTimeOfMonth(@NonNull Calendar cal){
        if(cal == null){
            return new Date();
        }
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    /**
     * Get the last DateTime of the current month
     * @return last DateTime of month, new initialized date if null is passed
     */
    public static Date getLastDateTimeOfMonth(){
        return getLastDateTimeOfMonth(Calendar.getInstance());
    }

    /**
     * Get the last DateTime of the month passed in
     * @param cal {@link Calendar}
     * @return last DateTime of month, new initialized date if null is passed
     */
    public static Date getLastDateTimeOfMonth(@NonNull Calendar cal){
        if(cal == null){
            return new Date();
        }
        int lastDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        cal.set(Calendar.DAY_OF_MONTH, lastDay);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        return cal.getTime();
    }
}
