package com.pgmacdesign.pgmacutilities.creditcardutils;

import java.util.Arrays;

/**
 * Temp String class used for Strings being wiped and not stored in memory.
 * Created by pmacdowell on 2017-08-03.
 */
public class TempString implements CharSequence{

    private transient final char[] tempStringData;

    public TempString(String tempStringData) {
        if (tempStringData != null) {
            this.tempStringData = tempStringData.toCharArray();
        } else {
            this.tempStringData = new char[0];
        }
    }

    /**
     * Used to wipe tempStringData (following Java recommendations)
     * @param from start
     * @param to end
     */
    public void disposeData(int from, int to) {
        Arrays.fill(
                tempStringData,
                Math.max(0, from),
                Math.min(tempStringData.length, to),
                (char) 0
        );
    }

    /**
     * Used to wipe tempStringData (following Java recommendations)
     * Overloaded so as to allow not having the to int
     * @param from start
     */
    public void disposeData(final int from) {
        Arrays.fill(
                tempStringData,
                Math.max(0, from),
                tempStringData.length,
                (char) 0
        );
    }

    /**
     * Used to wipe tempStringData (following Java recommendations)
     * Overloaded so as to allow not having the parse int to the to int
     */
    public void disposeData() {
        Arrays.fill(
                tempStringData,
                (char) 0
        );
    }

    public String getTempStringData() {
        if (thereIsData()) {
            return new String(tempStringData);
        } else {
            return null;
        }
    }

    public boolean thereIsData() {
        boolean isThere;
        try {
            isThere = ((tempStringData[0] != 0)
                    && (tempStringData.length > 0)
                    && (tempStringData[tempStringData.length - 1] != 0)
            );
        } catch (Exception e){
            isThere = false;
        }
        return isThere;
    }

    @Override
    public char charAt(int i) {
        return tempStringData[i];
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode(tempStringData);
        return result;
    }

    @Override
    public int length() {
        if(tempStringData == null){
            return 0;
        }
        return tempStringData.length;
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        String sub = TempString.this.toString();
        CharSequence sub2 = null;
        try {
            sub2 = sub.subSequence(start, end);
        } catch (Exception e){
            e.printStackTrace();
            sub2 = "";
        }
        return sub2;
    }

    @Override
    public String toString() {
        if(thereIsData()){
            try {
                return new String(tempStringData);
            } catch (Exception e){
                e.printStackTrace();
                return "";
            }
        } else {
            return "";
        }
    }
}
