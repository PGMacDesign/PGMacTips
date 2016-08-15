package com.pgmacdesign.pgmacutilities.utilities;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;

/**
 * Created by pmacdowell on 8/12/2016.
 */
public class NumberUtilities {


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
     *                      a $ to the begining
     * @return A String, formatted correctly. Will look like this: 104.44 or $99.40
     */
    public static String convertDoubleToStringAddZeroNoRemove(double value, Boolean addDollarSign){
        String str = Double.toString(value);
        String ending = str.substring((str.length()-2), (str.length()-1));
        if(ending.equalsIgnoreCase(".")){
            str = str + "0";
        }
        if(addDollarSign != null){
            if(addDollarSign){
                str = "$"+str;
            }
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


}
