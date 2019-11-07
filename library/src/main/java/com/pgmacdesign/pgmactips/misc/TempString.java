package com.pgmacdesign.pgmactips.misc;

import com.pgmacdesign.pgmactips.utilities.MiscUtilities;
import com.pgmacdesign.pgmactips.utilities.StringUtilities;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import static com.pgmacdesign.pgmactips.utilities.StringUtilities.isNullOrEmpty;

/**
 * Temp String class used for Strings being wiped and not stored in memory.
 * Calling {@link TempString#disposeData()} will erase the String from memory
 * Created by pmacdowell on 2017-08-03.
 */
public class TempString implements CharSequence{
	
	/**
	 * The Temp String core component
	 */
    private transient final char[] tempStringData;
	
	/**
	 * Constructor with a String
	 * @param tempStringData
	 */
	public TempString(String tempStringData) {
        if (!StringUtilities.isNullOrEmpty(tempStringData)) {
            this.tempStringData = tempStringData.toCharArray();
        } else {
            this.tempStringData = new char[0];
        }
    }
	
//	/**
//	 * Overloaded constructor for a character array
//	 * @param tempStringData
//	 */
//	public TempString(char[] tempStringData) {
//        if (tempStringData != null) {
//            this.tempStringData = tempStringData;
//        } else {
//            this.tempStringData = new char[0];
//        }
//    }
//
//	/**
//	 * Overloaded Constructor to allow for a byte array
//	 * @param tempStringData
//	 */
//	public TempString(byte[] tempStringData) {
//    	if(tempStringData == null){
//		    this.tempStringData = new char[0];
//    		return;
//	    }
//    	String str = null;
//    	try {
//		    str = new String(tempStringData, MiscUtilities.getUTF8());
//	    } catch (UnsupportedEncodingException uee){}
//    	if(StringUtilities.isNullOrEmpty(str)) {
//		    try {
//			    str = new String(tempStringData, MiscUtilities.getUTF16());
//		    } catch (UnsupportedEncodingException uee) {
//		    }
//	    }
//    	if(StringUtilities.isNullOrEmpty(str)) {
//		    try {
//			    str = new String(tempStringData, MiscUtilities.getASCII());
//		    } catch (UnsupportedEncodingException uee) {
//		    }
//	    }
//    	if(StringUtilities.isNullOrEmpty(str)){
//		    this.tempStringData = new char[0];
//	    } else {
//		    char[] charArray = str.toCharArray();
//		    if(charArray != null){
//		    	if(charArray.length <= 0){
//				    this.tempStringData = new char[0];
//			    } else {
//				    this.tempStringData = charArray;
//			    }
//		    } else {
//			    this.tempStringData = new char[0];
//		    }
//	    }
//    }

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
	
	/**
	 * Get the actual data
	 * @return
	 */
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

    /**
     * Simple tool for checking if the temp string is empty or null
     * @param tempString
     * @return
     */
    public static boolean isTempStringEmptyOrNull(TempString tempString){
        if(tempString == null){
            return true;
        }
        if(isNullOrEmpty(tempString.getTempStringData())){
            return true;
        }
        if(tempString.getTempStringData().isEmpty()){
            return true;
        }
        return false;
    }


    /**
     * Simple checker for if they are equal
     * @param obj
     * @return
     */
    public boolean equals(TempString obj) {
        if(obj == null){
            return false;
        }
        try {
            String thisStr = this.getTempStringData();
            String otherStr = obj.getTempStringData();
            if(StringUtilities.isNullOrEmpty(thisStr) || StringUtilities.isNullOrEmpty(otherStr)){
                return false;
            }
            return StringUtilities.doesEqual(thisStr, otherStr);
        } catch (Exception e) {
            return false;
        }
    }
}
