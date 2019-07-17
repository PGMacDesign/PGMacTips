package com.pgmacdesign.pgmactips.utilities;

import androidx.annotation.Nullable;

/**
 * Created by pmacdowell on 2017-08-25.
 */

public class BoolUtilities {
	
	/**
	 * Simple checker for boolean as true or false without worry of a NPE being thrown on checking
	 * @param bool
	 * @return
	 */
    public static boolean isTrue(@Nullable Boolean bool){
        if(bool == null){
            return false;
        } else {
            return (boolean)bool;
        }
    }
	
	/**
	 * Safely parse a boolean String var without an exception being thrown
	 * @param bool input String
	 * @return parsed bool, Will return false if unparseable or String is null
	 */
	public static boolean parseBoolSafe(@Nullable String bool){
    	try {
    		return Boolean.parseBoolean(bool);
	    } catch (Exception e){
    		return false;
	    }
    }

}
