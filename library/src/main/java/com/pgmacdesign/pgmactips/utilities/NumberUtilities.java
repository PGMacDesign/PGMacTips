package com.pgmacdesign.pgmactips.utilities;

import android.os.Build;
import androidx.annotation.Nullable;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Created by pmacdowell on 8/12/2016.
 */
public class NumberUtilities {

    /**
     * Convert Inches to feet
     * @param i
     * @return
     */
    public static double convertInchesToFeet(double i){
        double dbl = i * 0.08333333;
        return dbl;
    }

    /**
     * Convert Feet into Inches
     * @param f
     * @return
     */
    public static double convertFeetToInches(double f){
        double dbl = f / 0.08333333;
        return dbl;
    }

    /**
     * Convert Miles to Kilometers
     * @param m
     * @return
     */
    public static double convertMilesToKilometers(double m){
        double dbl = (m * 1.609344);
        return dbl;
    }

    /**
     * Convert Kilometers to miles
     * @param k
     * @return
     */
    public static double convertKilometersToMiles(double k){
        double dbl = (k * 0.621371);
        return dbl;
    }

    /**
     * Convert feet to meters
     * @param f
     * @return
     */
    public static double convertFeetToMeters(double f){
        double dbl = f * 0.3048;
        return dbl;
    }

    /**
     * Convert meters to feet
     * @param m
     * @return
     */
    public static double convertMetersToFeet(double m){
        double dbl = m / 0.3048;
        return dbl;
    }

    /**
     * Convert feet to miles
     * @param f
     * @return
     */
    public static double convertFeetToMiles(double f){
        double dbl = (f * 0.00019);
        return dbl;
    }

    /**
     * Convert miles to feet
     * @param m
     * @return
     */
    public static double convertMilesToFeet(double m){
        double dbl = (m / 0.00019);
        return dbl;
    }

    /**
     * Convert Celsius to Fahrenheit
     * @param c
     * @return
     */
    public static double convertCelsiusToFahrenheit(double c){
        double dbl = ((c * 9) / 5) + 32;
        return dbl;
    }

    /**
     * Convert Fahrenheit to Celsius
     * @param f
     * @return
     */
    public static double convertFahrenheitToCelsius(double f){
        double dbl = ((f - 32) * 5) / 9;
        return dbl;
    }
    /**
     * Checks if a string passed in is a number (IE, "12345" would return true)
     * @param str String to check
     * @return true if number, false if not
     */
    public static boolean isNumber(String str){
        if(StringUtilities.isNullOrEmpty(str)){
            return false;
        }

        str = str.trim();
        for(int i = 0; i < str.length(); i++){
            boolean bool = Character.isDigit(str.charAt(i));
            if(!bool){
                return false;
            }
        }
        return true;
    }

    /**
     * Rounds a double via passed in amount and places
     * @param value
     * @param places
     * @return
     */
    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    /**
     * Rounds a double down via passed in amount and places
     * @param value
     * @param places
     * @return
     */
    public static double roundDown(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.FLOOR);
        return bd.doubleValue();
    }

    /**
     * Rounds a double up via passed in amount and places
     * @param value
     * @param places
     * @return
     */
    public static double roundUp(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.CEILING);
        return bd.doubleValue();
    }

    /**
     * Simple method for getting a Long and converting it to a non-null number
     * @param lngX
     * @return long value, 0 if it is null
     */
    public static long getLong(Long lngX){
        if(lngX == null){
            return 0;
        } else {
            return lngX;
        }
    }

    /**
     * Simple method for getting an Integer and converting it to a non-null number
     * @param intx
     * @return Int value, 0 if int is null
     */
    public static int getInt(Integer intx){
        if(intx == null){
            return 0;
        } else {
            return intx;
        }
    }

    /**
     * Simple method for preventing Null Pointer Exceptions when working with Floats
     * @param flt
     * @return Float value, 0 if float was null
     */
    public static float getFloat(@Nullable Float flt){
        if(flt == null){
            return 0;
        } else {
            return flt;
        }
    }

    /**
     * Simple method for preventing Null Pointer Exceptions when working with Doubles
     * @param dbl
     * @return Float value, 0 if float was null
     */
    public static double getDouble(@Nullable Double dbl){
        if(dbl == null){
            return 0;
        } else {
            return dbl;
        }
    }

    /**
     * Simple formatter to add in commas for formatting. Obtained from:
     * https://stackoverflow.com/questions/3672731/how-can-i-format-a-string-number-to-have-commas-and-round
     * @param number number to round. If unparseable, will return number
     * @return Formatted String. IE, 44234 would return as 44,234
     */
    public static String formatNumberAddCommas(long number){
        DecimalFormat formatter = new DecimalFormat("#,###");
        try {
            return formatter.format(number);
        } catch (Exception e){
            e.printStackTrace();
            return number + "";
        }
    }

    /**
     * Simple formatter to add in commas for formatting. Obtained from:
     * https://stackoverflow.com/questions/3672731/how-can-i-format-a-string-number-to-have-commas-and-round
     * @param number number to round. If unparseable, will return number
     * @return Formatted String. IE, 44234 would return as 44,234
     */
    public static String formatNumberAddCommas(double number){
        DecimalFormat formatter = new DecimalFormat("#,###.00");
        try {
            return formatter.format(number);
        } catch (Exception e){
            e.printStackTrace();
            return number + "";
        }
    }

    /**
     * This used when a double ends in 0 (IE 100.0) and you want to remove the significant figures
     * after the decimal point (assuming they end in zero)
     * @param value The double to convert
     * @param addDollarSign boolean, if null passed, nothing, if true passed, will add
     *                      a $ to the beginning
     * @return A String, formatted correctly. Will look like this: 104.44 or $99.
     *         if the last 2 are 00, it will remove the significant figures after
     *         the decimal as well as the decimal itself
     */
    public static String convertDoubleToStringAddZero(double value, Boolean addDollarSign){
        String str = Double.toString(value);
        if(str == null){
            return null;
        }
        //String ending = str.substring((str.length()-2));
        String ending = str.substring((str.length()-2), (str.length()-1));
        if(ending.equalsIgnoreCase(".")){
            str = str + "0";
        }
        if(addDollarSign != null){
            if(addDollarSign){
                str = "$"+str;
            }
        }
        ending = str.substring((str.length() - 3));
        if(ending.equalsIgnoreCase(".00")){
            str = str.replace(".00", "");
        }

        return str;
    }
    
    /**
     * This used when a double ends in 0 (IE 100.0) and you want 2 decimal places instead (IE 100.00)
     * @param value The double to convert
     * @param addDollarSign boolean, if null passed, nothing, if true passed, will add
     *                      a $ to the beginning
     * @return A String, formatted correctly. Will look like this: 104.44 or $99.40
     */
    public static String convertDoubleToStringAddZeroNoRemove(double value, Boolean addDollarSign){
        String str = Double.toString(value);
        String ending = str.substring((str.length()-2), (str.length()-1));
        if(ending.equalsIgnoreCase(".")){
            str = str + "0";
        }
        if(BoolUtilities.isTrue(addDollarSign)){
            str = "$"+str;
        }
        return str;
    }
    
    /**
     * Get a random number between a min and max
     * @param min lower end min
     * @param max higher end max
     * @return
     */
    public static int getRandomInt(int min, int max){
        Random rand = new Random();
        int randomNum = rand.nextInt((max - min) + 1) + min;
        return randomNum;
    }

    /**
     * Get the minimum value of a set of numbers
     * @param numbers variable number of numbers
     * @return minimum of the passed set. If cannot find min, will return {@link Integer#MAX_VALUE}
     */
    public static int getMinimum(int... numbers) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Arrays.stream(numbers).min().orElse(Integer.MAX_VALUE);
        } else {
            if(numbers.length > 0){
                Arrays.sort(numbers);
                return numbers[0];
            }
            return Integer.MAX_VALUE;
        }
    }

    /**
     * Get the Maximum value of a set of numbers
     * @param numbers variable number of numbers
     * @return maximum of the passed set. If cannot find max, will return {@link Integer#MIN_VALUE}
     */
    public static int getMaximum(int... numbers) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Arrays.stream(numbers).max().orElse(Integer.MIN_VALUE);
        } else {
            if(numbers.length > 0){
                Arrays.sort(numbers);
                return numbers[(numbers.length -1)];
            }
            return Integer.MIN_VALUE;
        }
    }

    /**
     * Get the minimum value of a set of numbers
     * @param numbers variable number of numbers
     * @return minimum of the passed set. If cannot find min, will return {@link Integer#MAX_VALUE}
     */
    public static int getMinimum(List<Integer> numbers) {
        try {
            return Collections.min(numbers);
        } catch (Exception e){
            return Integer.MAX_VALUE;
        }
    }

    /**
     * Get the Maximum value of a set of numbers
     * @param numbers variable number of numbers
     * @return maximum of the passed set. If cannot find max, will return {@link Integer#MIN_VALUE}
     */
    public static int getMaximum(List<Integer> numbers) {
        try {
            return Collections.max(numbers);
        } catch (Exception e){
            return Integer.MIN_VALUE;
        }
    }
    
    //region Check if an int is contained within an array
    
    /**
     * Simple utility to check if an array contains an int value. There are faster ways to do so,
     * see https://stackoverflow.com/questions/12020361/java-simplified-check-if-int-array-contains-int
     * but some require Java 8 while other require external libraries, which may pose a conflict
     * for those using the library. If you want a faster method, use the link above.
     * @param array int array to check the values in
     * @param toCheck int to check if it is in an array
     * @return boolean, true if it does contain it, false if not
     */
    public static boolean doesArrayContain(int[] array, int toCheck){
        if(array == null){
            return false;
        }
        for(int x : array){
            if(x == toCheck){
                return true;
            }
        }
        return false;
    }
    
    /**
     * Simple utility to check if an array contains an double value. There are faster ways to do so,
     * see https://stackoverflow.com/questions/12020361/java-simplified-check-if-int-array-contains-int
     * but some require Java 8 while other require external libraries, which may pose a conflict
     * for those using the library. If you want a faster method, use the link above.
     * @param array int array to check the values in
     * @param toCheck int to check if it is in an array
     * @return boolean, true if it does contain it, false if not
     */
    public static boolean doesArrayContain(double[] array, double toCheck){
        if(array == null){
            return false;
        }
        for(double x : array){
            if(x == toCheck){
                return true;
            }
        }
        return false;
    }
    
    /**
     * Simple utility to check if an array contains an float value. There are faster ways to do so,
     * see https://stackoverflow.com/questions/12020361/java-simplified-check-if-int-array-contains-int
     * but some require Java 8 while other require external libraries, which may pose a conflict
     * for those using the library. If you want a faster method, use the link above.
     * @param array int array to check the values in
     * @param toCheck int to check if it is in an array
     * @return boolean, true if it does contain it, false if not
     */
    public static boolean doesArrayContain(float[] array, float toCheck){
        if(array == null){
            return false;
        }
        for(float x : array){
            if(x == toCheck){
                return true;
            }
        }
        return false;
    }
    
    //endregion
    
    //region X.ParseX Methods
    
    /**
     * Parse and return an integer without worrying about null checks
     * @param intx String to parse
     * @param fallbackNumber Fallback number to return if it failed parsing
     * @return Parsed long or fallback number if it failed conversion
     */
    public static int parseIntegerSafe(String intx, int fallbackNumber){
        try {
            return Integer.parseInt(intx);
        } catch (Exception e){
            return fallbackNumber;
        }
    }
    
    /**
     * Parse and return a long without worrying about null checks
     * @param lng String to parse
     * @param fallbackNumber Fallback number to return if it failed parsing
     * @return Parsed long or fallback number if it failed conversion
     */
    public static long parseLongSafe(String lng, long fallbackNumber){
        try {
            return Long.parseLong(lng);
        } catch (Exception e){
            return fallbackNumber;
        }
    }
    
    /**
     * Parse and return a double without worrying about null checks
     * @param dbl String to parse
     * @param fallbackNumber Fallback number to return if it failed parsing
     * @return Parsed long or fallback number if it failed conversion
     */
    public static double parseDoubleSafe(String dbl, double fallbackNumber){
        try {
            return Double.parseDouble(dbl);
        } catch (Exception e){
            return fallbackNumber;
        }
    }
    
    /**
     * Parse and return a float without worrying about null checks
     * @param flt String to parse
     * @param fallbackNumber Fallback number to return if it failed parsing
     * @return Parsed long or fallback number if it failed conversion
     */
    public static float parseFloatSafe(String flt, float fallbackNumber){
        try {
            return Long.parseLong(flt);
        } catch (Exception e){
            return fallbackNumber;
        }
    }
    
    //endregion
    
}
