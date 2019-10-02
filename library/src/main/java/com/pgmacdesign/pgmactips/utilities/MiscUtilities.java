package com.pgmacdesign.pgmactips.utilities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.Signature;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Looper;

import androidx.annotation.FloatRange;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.provider.Settings;
import android.text.format.DateFormat;
import android.util.Base64;
import android.util.Log;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import com.pgmacdesign.pgmactips.BuildConfig;
import com.pgmacdesign.pgmactips.adaptersandlisteners.OnTaskCompleteListener;
import com.pgmacdesign.pgmactips.misc.GenericComparator;
import com.pgmacdesign.pgmactips.misc.PGMacTipsConstants;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.Nonnull;

/**
 * Created by pmacdowell on 8/15/2016.
 */
public class MiscUtilities {

	//region Private Static Vars
    // https://docs.oracle.com/javase/7/docs/technotes/guides/security/StandardNames.html#MessageDigest
    private static final String SHA = "SHA";
    //endregion

	//region Misc Thread Management Utilities
	
    /**
     * This class will determine if the current loop being run is on the main thread or not
     * @return boolean, true if on main ui thread, false if not
     */
    public static boolean isRunningOnMainThread() {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            return false;
        } else {
            return true;
        }
    }
    
    //endregion
	
	//region Checking User Settings Preferences
	
    /**
     * Checks system preferences for if user has 24 hour (18:04 == 6:04 pm) in their settings
     * @param context {@link Context}
     * @return boolean, true if they prefer 24 hour, false if they prefer 12 hour
     */
    public static boolean userPrefers24HourTimeFormat(Context context){
        try {
            return DateFormat.is24HourFormat(context);
        } catch (Exception e){
            return false;
        }
    }
	
    //endregion
	
	//region Hash Key Utilities
	
	/**
	 * Print out your hashkey which is unique to your machine you are on
	 * @param context
	 * @param packageName
	 */
	public static void printOutMyHashKey(Context context, String packageName){
		try {
			PackageInfo info = context.getPackageManager().getPackageInfo(
					packageName, PackageManager.GET_SIGNATURES);
			for (Signature signature : info.signatures) {
				MessageDigest md = MessageDigest.getInstance(SHA);
				md.update(signature.toByteArray());
				Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
			}
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Return your hashkey which is unique to your machine you are on
	 * @param context
	 * @param packageName
	 */
	public static String getMyHashKey(Context context, String packageName){
		try {
			PackageInfo info = context.getPackageManager().getPackageInfo(
					packageName, PackageManager.GET_SIGNATURES);
			for (Signature signature : info.signatures) {
				MessageDigest md = MessageDigest.getInstance(SHA);
				md.update(signature.toByteArray());
				return Base64.encodeToString(md.digest(), Base64.DEFAULT);
			}
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	//endregion
	
	//region List Utilities
	
	/**
	 * Checks a list for either being empty or containing objects within it
	 * @param myList List to check
	 * @return Boolean, true if it is null or empty, false it if is not
	 */
	public static boolean isListNullOrEmpty(List<?> myList){
		if(myList == null){
			return true;
		}
		if(myList.size() <= 0){
			return true;
		}
		return false;
	}
	
	/**
	 * Overloaded for naming simplicity (and because I forget the names sometimes)
	 * Checks a list for whether or not the passed position is valid within it (IE, passing 10
	 * in a list that has a size of 4 would return false. Passing 2 in a list of size 3 would
	 * return true. Passing -1 would always return false.)
	 * @param myList List to check
	 * @param posToCheck int position to check
	 * @return Boolean, true if it is a valid position (won't throw {@link ArrayIndexOutOfBoundsException}).
	 *         Will also return false if the list is of size 0.
	 */
	public static boolean isValidPosition(List<?> myList, int posToCheck){
		return isValidPositionInList(myList, posToCheck);
	}
	
	/**
	 * Checks a list for whether or not the passed position is valid within it (IE, passing 10
	 * in a list that has a size of 4 would return false. Passing 2 in a list of size 3 would
	 * return true. Passing -1 would always return false.)
	 * @param myList List to check
	 * @param posToCheck int position to check
	 * @return Boolean, true if it is a valid position (won't throw {@link ArrayIndexOutOfBoundsException}).
	 *         Will also return false if the list is of size 0.
	 */
	public static boolean isValidPositionInList(List<?> myList, int posToCheck){
		if(myList == null){
			return false;
		}
		if(myList.size() <= 0){
			return false;
		}
		if(posToCheck < 0){
			return false;
		}
		if(posToCheck >= myList.size()){
			return false;
		}
		return true;
	}
	
	/**
     * Print out a list of objects
     * @param myList
     */
    public static void printOutList(List<?> myList){
        if(isListNullOrEmpty(myList)){
            return;
        }
        int x = 0;
        for(Object item : myList){
            try {
                L.m(item.toString());
            } catch (Exception e){
                L.m("Could not print position " + x);
            }
            x++;
        }
    }
	//endregion
	
    //region Array Utilities
	
	/**
	 * Checks a list for either being empty or containing objects within it
	 * @param myArray array to check
	 * @param <T> T extends object
	 * @return boolean, true if it is null or empty, false it if is not
	 */
	public static <T extends Object> boolean isArrayNullOrEmpty(T[] myArray){
		if(myArray == null){
			return true;
		}
		if(myArray.length <= 0){
			return true;
		}
		return false;
	}
	
	/**
	 * Overloaded for naming simplicity (and because I forget the names sometimes)
	 * Checks an array for whether or not the passed position is valid within it (IE, passing 10
	 * in a array that has a size of 4 would return false. Passing 2 in a array of size 3 would
	 * return true. Passing -1 would always return false.)
	 * @param myArray Array to check
	 * @param posToCheck int position to check
	 * @return Boolean, true if it is a valid position (won't throw {@link ArrayIndexOutOfBoundsException}).
	 *         Will also return false if the array is of size 0.
	 */
	public static <T extends Object> boolean isValidPosition(T[] myArray, int posToCheck){
		return isValidPositionInArray(myArray, posToCheck);
	}
	
	/**
	 * Checks an array for whether or not the passed position is valid within it (IE, passing 10
	 * in a array that has a size of 4 would return false. Passing 2 in a array of size 3 would
	 * return true. Passing -1 would always return false.)
	 * @param myArray Array to check
	 * @param posToCheck int position to check
	 * @return Boolean, true if it is a valid position (won't throw {@link ArrayIndexOutOfBoundsException}).
	 *         Will also return false if the array is of size 0.
	 */
	public static <T extends Object> boolean isValidPositionInArray(T[] myArray, int posToCheck){
		if(myArray == null){
			return false;
		}
		if(myArray.length <= 0){
			return false;
		}
		if(posToCheck < 0){
			return false;
		}
		if(posToCheck >= myArray.length){
			return false;
		}
		return true;
	}
	
	/**
     * Print out an array of objects
     * @param myArray Array of objects
     */
    public static void printOutArray(Object[] myArray){
        if(isArrayNullOrEmpty(myArray)){
            return;
        }
        int x = 0;
        for(Object item : myArray){
            try {
                L.m(item.toString());
            } catch (Exception e){
                L.m("Could not print position " + x);
            }
            x++;
        }
    }
    
    /**
     * Print out an array of bytes on the same line
     * @param myArray Array of bytes
     */
    public static void printOutArray(byte[] myArray, boolean separateByCommas){
        if(myArray == null){
            return;
        }
        if(myArray.length <= 0){
            return;
        }
        int x = 0;
        StringBuilder sb = new StringBuilder();
        for(byte b : myArray){
            try {
                if(separateByCommas) {
                    sb.append(b + ",");
                } else {
                    sb.append(b + "");
                }
            } catch (Exception e){
                L.m("Could not print position " + x);
            }
            x++;
        }
        L.m(sb.toString());
    }
    
    //endregion
	
	//region Comparators / Sorting
	
	//region Strings
	
	/**
	 * Sort a list of Strings. Defaults to sorting alphabetically ascending
	 * Null values and empty Strings will always move to the end of the list
	 * Note, you can use the {@link MiscUtilities#sortGenericList(List)} for this as well, but
	 * this will run faster due to not needing to use reflection
	 * @param lst List to sort
	 * @return Sorted list
	 */
	public static List<String> sortStringList(List<String> lst){
    	return MiscUtilities.sortStringList(lst, true);
	}
	
	/**
	 * Sort a list of Strings.
	 * Null values and empty Strings will always move to the end of the list
	 * Note, you can use the {@link MiscUtilities#sortGenericList(List)} for this as well, but
	 * this will run faster due to not needing to use reflection
	 * @param lst List to sort
	 * @param sortByAsc If true, sorts ascending, else, sorts descending
	 * @return Sorted list
	 */
	public static List<String> sortStringList(List<String> lst, final boolean sortByAsc){
    	if(MiscUtilities.isListNullOrEmpty(lst)){
    		return lst;
	    }
		Collections.sort(lst, new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				if(StringUtilities.isNullOrEmpty(o1)){
					return 1;
				}
				if(StringUtilities.isNullOrEmpty(o2)){
					return -1;
				}
				return (sortByAsc) ? o1.compareTo(o2) : o2.compareTo(o1);
			}
		});
    	return lst;
	}
	
	//endregion
	
	//region Integers
	
	/**
	 * Sort a list of Integers. Defaults to sorting ascending
	 * Null values will always move to the end of the list
	 * Note, you can use the {@link MiscUtilities#sortGenericList(List)} for this as well, but
	 * this will run faster due to not needing to use reflection
	 * @param lst List to sort
	 * @return Sorted list
	 */
	public static List<Integer> sortIntList(List<Integer> lst){
    	return MiscUtilities.sortIntList(lst, true);
	}
	
	/**
	 * Sort a list of Integers.
	 * Null values will always move to the end of the list
	 * Note, you can use the {@link MiscUtilities#sortGenericList(List)} for this as well, but
	 * this will run faster due to not needing to use reflection
	 * @param lst List to sort
	 * @param sortByAsc If true, sorts ascending, else, sorts descending
	 * @return Sorted list
	 */
	public static List<Integer> sortIntList(List<Integer> lst, final boolean sortByAsc){
    	if(MiscUtilities.isListNullOrEmpty(lst)){
    		return lst;
	    }
		Collections.sort(lst, new Comparator<Integer>() {
			@Override
			public int compare(Integer o1, Integer o2) {
				if(StringUtilities.isNullOrEmpty(o1)){
					return 1;
				}
				if(StringUtilities.isNullOrEmpty(o2)){
					return -1;
				}
				return (sortByAsc) ? o1.compareTo(o2) : o2.compareTo(o1);
			}
		});
    	return lst;
	}
	
	//endregion
	
	//region Doubles
	
	/**
	 * Sort a list of Integers. Defaults to sorting ascending
	 * Null values will always move to the end of the list
	 * Note, you can use the {@link MiscUtilities#sortGenericList(List)} for this as well, but
	 * this will run faster due to not needing to use reflection
	 * @param lst List to sort
	 * @return Sorted list
	 */
	public static List<Double> sortDoubleList(List<Double> lst){
		return MiscUtilities.sortDoubleList(lst, true);
	}
	
	/**
	 * Sort a list of Doubles.
	 * Null values will always move to the end of the list
	 * Note, you can use the {@link MiscUtilities#sortGenericList(List)} for this as well, but
	 * this will run faster due to not needing to use reflection
	 * @param lst List to sort
	 * @param sortByAsc If true, sorts ascending, else, sorts descending
	 * @return Sorted list
	 */
	public static List<Double> sortDoubleList(List<Double> lst, final boolean sortByAsc){
		if(MiscUtilities.isListNullOrEmpty(lst)){
			return lst;
		}
		Collections.sort(lst, new Comparator<Double>() {
			@Override
			public int compare(Double o1, Double o2) {
				if(StringUtilities.isNullOrEmpty(o1)){
					return 1;
				}
				if(StringUtilities.isNullOrEmpty(o2)){
					return -1;
				}
				return (sortByAsc) ? o1.compareTo(o2) : o2.compareTo(o1);
			}
		});
		return lst;
	}
	
	//endregion
	
	//region Floats
	
	/**
	 * Sort a list of Floats. Defaults to sorting ascending
	 * Null values will always move to the end of the list
	 * Note, you can use the {@link MiscUtilities#sortGenericList(List)} for this as well, but
	 * this will run faster due to not needing to use reflection
	 * @param lst List to sort
	 * @return Sorted list
	 */
	public static List<Float> sortFloatList(List<Float> lst){
		return MiscUtilities.sortFloatList(lst, true);
	}
	
	/**
	 * Sort a list of Floats.
	 * Null values will always move to the end of the list
	 * Note, you can use the {@link MiscUtilities#sortGenericList(List)} for this as well, but
	 * this will run faster due to not needing to use reflection
	 * @param lst List to sort
	 * @param sortByAsc If true, sorts ascending, else, sorts descending
	 * @return Sorted list
	 */
	public static List<Float> sortFloatList(List<Float> lst, final boolean sortByAsc){
		if(MiscUtilities.isListNullOrEmpty(lst)){
			return lst;
		}
		Collections.sort(lst, new Comparator<Float>() {
			@Override
			public int compare(Float o1, Float o2) {
				if(StringUtilities.isNullOrEmpty(o1)){
					return 1;
				}
				if(StringUtilities.isNullOrEmpty(o2)){
					return -1;
				}
				return (sortByAsc) ? o1.compareTo(o2) : o2.compareTo(o1);
			}
		});
		return lst;
	}
	
	//endregion
	
	//region Longs
	
	/**
	 * Sort a list of Longs. Defaults to sorting ascending
	 * Null values will always move to the end of the list
	 * Note, you can use the {@link MiscUtilities#sortGenericList(List)} for this as well, but
	 * this will run faster due to not needing to use reflection
	 * @param lst List to sort
	 * @return Sorted list
	 */
	public static List<Long> sortLongList(List<Long> lst){
		return MiscUtilities.sortLongList(lst, true);
	}
	
	/**
	 * Sort a list of Long.
	 * Null values will always move to the end of the list
	 * Note, you can use the {@link MiscUtilities#sortGenericList(List)} for this as well, but
	 * this will run faster due to not needing to use reflection
	 * @param lst List to sort
	 * @param sortByAsc If true, sorts ascending, else, sorts descending
	 * @return Sorted list
	 */
	public static List<Long> sortLongList(List<Long> lst, final boolean sortByAsc){
		if(MiscUtilities.isListNullOrEmpty(lst)){
			return lst;
		}
		Collections.sort(lst, new Comparator<Long>() {
			@Override
			public int compare(Long o1, Long o2) {
				if(StringUtilities.isNullOrEmpty(o1)){
					return 1;
				}
				if(StringUtilities.isNullOrEmpty(o2)){
					return -1;
				}
				return (sortByAsc) ? o1.compareTo(o2) : o2.compareTo(o1);
			}
		});
		return lst;
	}
	
	//endregion
	
	//region Generic Comparators (Can Handle Misc Object Types too)
	
	/**
	 * Sort a list of Generics. Defaults to sorting alphabetically / numerically ascending
	 * Null values and empty Strings will always move to the end of the list
	 * @param lst list to sort
	 * @param <T> Generic
	 * @return sorted list
	 */
	public static <T> List<T> sortGenericList(List<T> lst){
		return sortGenericList(lst, true);
	}
	
	/**
	 * Sort a list of Generics. Defaults to sorting alphabetically / numerically ascending
	 * Null values and empty Strings will always move to the end of the list
	 * @param lst list to sort
	 * @param sortAscending if true, sort ascending, else, sorts descending
	 * @param <T> Generic
	 * @return sorted list
	 */
	public static <T> List<T> sortGenericList(List<T> lst, boolean sortAscending){
		if(MiscUtilities.isListNullOrEmpty(lst)){
			return lst;
		}
		try {
			Collections.sort(lst, new GenericComparator(sortAscending));
			return lst;
		} catch (Exception e){
			e.getMessage();
			return lst;
		}
	}
	
	/**
	 * Sort a list of Generics. Defaults to sorting alphabetically / numerically ascending
	 * Null values and empty Strings will always move to the end of the list
	 * @param lst list to sort
	 * @param fieldName Field name you would like to use for the sorting
	 *                  Note that if the field passed gets an array or list, it will sort by the length / size of said collection
	 * @param <T> Generic
	 * @return sorted list
	 */
	public static <T> List<T> sortGenericList(List<T> lst, String fieldName){
		return sortGenericList(lst, fieldName, true);
	}
	
	/**
	 *
	 * @param lst list to sort
	 * @param fieldName Field name you would like to use for the sorting
	 *                  Note that if the field passed gets an array or list, it will sort by the length / size of said collection
	 * @param sortAscending boolean for sorting by ascending or descending
	 * @param <T> Generic
	 * @return sorted list
	 */
	public static <T> List<T> sortGenericList(List<T> lst, String fieldName, boolean sortAscending){
		if(MiscUtilities.isListNullOrEmpty(lst) || StringUtilities.isNullOrEmpty(fieldName)){
			return lst;
		}
		try {
			Collections.sort(lst, new GenericComparator(fieldName, sortAscending));
			return lst;
		} catch (Exception e){
			e.getMessage();
			return lst;
		}
	}
	
	/**
	 * Sort a list using the custom-defined method name as opposed to the field name
	 * @param lst list to sort
	 * @param customOverrideMethodName Custom override getter method name you are using in place
	 *                                 of the standard generated getters. This is useful if you have
	 *                                 a non-normal named getter for a var.
	 *                                 Note that if the type passed is an array or list, it will sort by the length / size of said collection
	 * @param sortAscending boolean for sorting by ascending or descending
	 * @param <T> Generic
	 * @return sorted list
	 */
	public static <T> List<T> sortGenericListCustom(List<T> lst, String customOverrideMethodName, boolean sortAscending){
		if(MiscUtilities.isListNullOrEmpty(lst) || StringUtilities.isNullOrEmpty(customOverrideMethodName)){
			return lst;
		}
		try {
			Collections.sort(lst, new GenericComparator(false, customOverrideMethodName, sortAscending));
			return lst;
		} catch (Exception e){
			e.getMessage();
			return lst;
		}
	}
	
	//endregion
	
	//endregion
	
    //region HashMap / Map Utilities
	
	/**
	 * Checks a map for either being empty or containing objects within it
	 * @param myMap map to check
	 * @return Boolean, true if it is null or empty, false it if is not
	 */
	public static boolean isMapNullOrEmpty(Map<?, ?> myMap){
		if(myMap == null){
			return true;
		}
		if(myMap.size() <= 0){
			return true;
		}
		return false;
	}
	
    /**
     * Print out a hashmap {@link java.util.HashMap}
     * @param myMap Map of type String, ?
     */
    public static void printOutHashMap(Map<?,?> myMap){
        if(myMap == null){
            return;
        }
        L.m("Printing out entire Hashmap:\n");
        for(Map.Entry<?,?> map : myMap.entrySet()){
            Object key = map.getKey();
            Object value = map.getValue();
            String keyStr = (key == null) ? "null" : key.toString();
            String valueStr = (value == null) ? "null" : value.toString();
            L.m(keyStr + ", " + valueStr);
        }
        L.m("\nEnd printing out Hashmap:");
    }
	
	/**
	 * Get the 'first' (only) key in a map. Useful for maps with only one thing in them
	 * @param myMap
	 * @return
	 */
	public static Object getOnlyKeyInMap(Map<?, ?> myMap){
		if(MiscUtilities.isMapNullOrEmpty(myMap)){
			return null;
		}
		try {
			return myMap.entrySet().iterator().next().getKey();
		} catch (Exception e){
			return null;
		}
	}
	
	/**
	 * Get the 'first' (only) value in a map. Useful for maps with only one thing in them
	 * @param myMap
	 * @return
	 */
	public static Object getOnlyValueInMap(Map<?, ?> myMap){
		if(MiscUtilities.isMapNullOrEmpty(myMap)){
			return null;
		}
		try {
			return myMap.entrySet().iterator().next().getValue();
		} catch (Exception e){
			return null;
		}
	}
	
	/**
	 * Get the Keys from a Map where the key is of type Generic
	 * @param map The Map to iterate through
	 * @param <T> Generic type to match the type passed in
	 * @return Will return a list of the keys from the map, if the original map passed in is null or empty, will return empty list
	 */
	public static <T> List<T> getKeysFromMap(Map<T, ?> map){
		return MiscUtilities.getKeysFromMap(map, false);
	}
	
	/**
	 * Get the Keys from a Map where the key is of type Generic
	 * @param map The Map to iterate through
	 * @param includeNullValues If true, will add null keys to the returned list, if false, will skip. Defaults to false
	 * @param <T> Generic type to match the type passed in
	 * @return Will return a list of the keys from the map, if the original map passed in is null or empty, will return empty list
	 * @return
	 */
	public static <T> List<T> getKeysFromMap(Map<T, ?> map, boolean includeNullValues){
		List<T> lst = new ArrayList<>();
		if(MiscUtilities.isMapNullOrEmpty(map)){
			return lst;
		}
		for(Map.Entry<T, ?> m : map.entrySet()){
			if(m != null){
				T t = m.getKey();
				if(t != null){
					lst.add(t);
				} else {
					if(includeNullValues){
						lst.add(t);
					}
				}
			}
		}
		return lst;
	}
		
	/**
	 * Get the Values from a Map where the Value is of type Generic
	 * @param map The Map to iterate through
	 * @param <T> Generic type to match the type passed in
	 * @return Will return a list of the values from the map, if the original map passed in is null or empty, will return empty list
	 */
	public static <T> List<T> getValuesFromMap(Map<?, T> map){
		return MiscUtilities.getValuesFromMap(map, false);
	}
	
	/**
	 * Get the Values from a Map where the Value is of type generic
	 * @param map The Map to iterate through
	 * @param includeNullValues If true, will add null values to the returned list, if false, will skip. Defaults to false
	 * @param <T> Generic type to match the type passed in
	 * @return Will return a list of the values from the map, if the original map passed in is null or empty, will return empty list
	 */
	public static <T> List<T> getValuesFromMap(Map<?, T> map, boolean includeNullValues){
		List<T> lst = new ArrayList<>();
		if(MiscUtilities.isMapNullOrEmpty(map)){
			return lst;
		}
		for(Map.Entry<?, T> m : map.entrySet()){
			if(m != null){
				T t = m.getValue();
				if(t != null){
					lst.add(t);
				} else {
					if(includeNullValues){
						lst.add(t);
					}
				}
			}
		}
		return lst;
	}
	
    //endregion
	
    //region HashSet / Set Utilities
	
	/**
	 * Checks a set for either being empty or containing objects within it
	 * @param mySet set to check
	 * @return Boolean, true if it is null or empty, false it if is not
	 */
	public static boolean isSetNullOrEmpty(Set<?> mySet){
		if(mySet == null){
			return true;
		}
		if(mySet.size() <= 0){
			return true;
		}
		return false;
	}
	
    /**
     * Print out a hashSet {@link HashSet}
     * @param hashSet HashSet of type ?
     */
    public static void printOutHashSet(HashSet<?> hashSet){
        if(hashSet == null){
            return;
        }
        L.m("Printing out entire HashSet:\n");
        for(Object o : hashSet){
            L.m(o);
        }
        L.m("\nEnd printing out HashSet:");
    }
  
    /**
     * Print out a Set {@link Set}
     * @param set Set of type ?
     */
    public static void printOutHashSet(Set<?> set){
        if(set == null){
            return;
        }
        L.m("Printing out entire Set:\n");
        for(Object o : set){
            L.m(o);
        }
        L.m("\nEnd printing out Set:");
    }

    //endregion
	
    //region Simple Utilities for Removing Nulls from Lists
	
    /**
     * Remove nulls from a list of list of objects
     * @param nestedListObject
     * @return remove the nulls and return objects
     */
    public static List<List<Object>> removeNullsFromLists(List<List<?>> nestedListObject){
        List<List<Object>> listsToReturn = new ArrayList<>();
        for(int i = 0; i < nestedListObject.size(); i++){
            try {
                List<Object> obj = listsToReturn.get(i);
                if(obj == null){
                    continue;
                }
                obj = removeNullsFromList(obj);
                if(obj != null){
                    listsToReturn.add(obj);
                }
            } catch (Exception e){}
        }
        return listsToReturn;
    }

    /**
     * Remove nulls from a list of objects
     * @param myList
     * @return remove the nulls and return objects
     */
    public static List<Object> removeNullsFromList (List<?> myList){
        if(myList == null){
            return null;
        }
        List<Object> listToReturn = new ArrayList<>();
        for(int i = 0; i < myList.size(); i++){
            try {
                Object obj = myList.get(i);
                if(obj != null){
                    listToReturn.add(obj);
                }
            } catch (Exception e){}
        }
        return listToReturn;
    }

    //endregion
	
    //region Manual Pause Callbacks
	
    /**
     * Simple class for pausing a certain number of milliseconds. Useful for interacting with the
     * Main UI Thread when running on things like timers and can't cross threads
     */
    public static class PauseForXSeconds extends AsyncTask<Void, Void, Void> {

        private long numberOfMillisecondsToWait;
        private OnTaskCompleteListener listener;
        /**
         * Pause for X seconds on background thread and then pass back word of finishing on a
         * listener. Used mainly for interacting with the UI when you need to update a field (IE
         * a textview) but need to wait X seconds. Usually this would cause an exception where
         * you are calling it NOT on the main thread. This alleviates that
         * @param numberOfMillisecondsToWait long number of milliseconds to wait. Minimum 10, no max
         * @param listener Listener to pass back word of completion
         */
        public PauseForXSeconds(long numberOfMillisecondsToWait, OnTaskCompleteListener listener){
            this.listener = listener;
            this.numberOfMillisecondsToWait = numberOfMillisecondsToWait;
            if(this.numberOfMillisecondsToWait < 10){
                //Minimum of 10 milliseconds in case 0 is sent by accident
                this.numberOfMillisecondsToWait = 10;
            }
        }
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Thread.sleep(numberOfMillisecondsToWait);
            } catch (InterruptedException e){
                return null;
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            listener.onTaskComplete(null, -1);
        }
    }

    //endregion
	
	//region Cookie Management
	
    /**
     * Clear cookies
     * @param context context
     */
    @SuppressWarnings("deprecation")
    public static void clearCookies(Context context) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            CookieManager.getInstance().removeAllCookies(null);
            CookieManager.getInstance().flush();
        } else {
            CookieSyncManager cookieSyncMngr = CookieSyncManager.createInstance(context);
            cookieSyncMngr.startSync();
            CookieManager cookieManager=CookieManager.getInstance();
            cookieManager.removeAllCookie();
            cookieManager.removeSessionCookie();
            cookieSyncMngr.stopSync();
            cookieSyncMngr.sync();
        }
    }

    //endregion
	
    //region Utilities for checking installed apps on the device
	
    /**
     * Checks if a user has the facebook application installed on their phone
     * @param context Context used to ge the package manager
     * @return boolean, true if they have it installed, false if they do not
     */
    public static boolean doesUserHaveFacebookAppInstalled(Context context){
        try{
            context.getPackageManager().getApplicationInfo("com.facebook.katana", 0 );
            return true;
        } catch( PackageManager.NameNotFoundException e ){
            return false;
        }
    }

    /**
     * Grab a list of all apps the user has installed and return them
     * @param context context
     * @param printResults Overloaded boolean, if true, will print out results in logcat
     * @return {@link ResolveInfo}
     */
    public static List<ResolveInfo> getAllInstalledApps(Context context, boolean printResults){
        try {
            Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
            mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
            List<ResolveInfo> pkgAppsList = context.getPackageManager()
                    .queryIntentActivities( mainIntent, 0);
            if(printResults){
                L.m("\nPrinting list of all installed apps:\n");
                for(ResolveInfo r : pkgAppsList){
                    if(r != null) {
                        L.m(r.toString());
                    }
                }
                L.m("\nFinished Printing list of all installed apps:\n");
            }
            return pkgAppsList;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Grab a list of all apps the user has installed and return them
     * @param context context
     * @return {@link ResolveInfo}
     */
    public static List<ResolveInfo> getAllInstalledApps(Context context){
        try {
            Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
            mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
            List<ResolveInfo> pkgAppsList = context.getPackageManager()
                    .queryIntentActivities( mainIntent, 0);
            return pkgAppsList;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //endregion
	
	//region AtomicInteger Utilities
	
    /**
     * Simple method to allow for less null checks in code
     * @param atomicInteger {@link AtomicInteger}
     * @return int. -1 if Atomic Integer is null, actual int else.
     */
    public static int getInt(AtomicInteger atomicInteger){
        if(atomicInteger == null){
            return -1;
        } else {
            return atomicInteger.get();
        }
    }

    //endregion
	
	//region Clipboard Utilities
	
    /**
     * copy something to the clipboard. Overloaded to allow for empty label
     * @param context Context for referencing system service
     * @param toCopy The actual text to copy
     * @return boolean, true if it successfully copied, false if it did not
     */
    public static boolean copyToClipboard(@NonNull Context context, @NonNull String toCopy){
        return copyToClipboard(context, null, toCopy);
    }

    /**
     * copy something to the clipboard
     * @param context Context for referencing system service
     * @param label A label to reference by. If null or empty, will be {@link PGMacTipsConstants#PGMACTIPS_STRING}
     * @param toCopy The actual text to copy
     * @return boolean, true if it successfully copied, false if it did not
     */
    public static boolean copyToClipboard(@NonNull Context context, @Nullable String label, @NonNull String toCopy){
        if(context == null || StringUtilities.isNullOrEmpty(toCopy)){
            return false;
        }
        if(StringUtilities.isNullOrEmpty(label)){
            label = PGMacTipsConstants.PGMACTIPS_STRING;
        }
        android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        android.content.ClipData clip = android.content.ClipData.newPlainText(label, toCopy);
        if(clipboard != null) {
            clipboard.setPrimaryClip(clip);
            return true;
        } else {
            return false;
        }
    }

    //endregion
	
    //region Enum Utils
	
    /**
     * Get all values from an enum type (list of all the values)
     * @param enumToGet The enum to get all of
     * @param <E> E Extends Enum
     * @return List of enum values, null if something fails
     */
    public static <E extends Enum> List<E> getAllEnumValues(E enumToGet){
        if(enumToGet == null){
            return null;
        }
        try {
            return new ArrayList<>(EnumSet.allOf(enumToGet.getDeclaringClass()));
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    //endregion
	
    //region Static Charset Getters
	
    /**
     * Simple getter for UTF-8 String since it is recommended to use StandardCharsets after SDK level 19,
     * but this library currently supports 15 as the minimum.
     * @return
     */
    public static String getUTF8(){
        if(Build.VERSION.SDK_INT >= 19){
            return StandardCharsets.UTF_8.toString();
        } else {
            return "UTF-8";
        }
    }

    /**
     * Simple getter for ISO_8859-1 String since it is recommended to use StandardCharsets after SDK level 19,
     * but this library currently supports 15 as the minimum.
     * @return
     */
    public static String getISO8859(){
        if(Build.VERSION.SDK_INT >= 19){
            return StandardCharsets.ISO_8859_1.toString();
        } else {
            return "ISO-8859-1";
        }
    }

    /**
     * Simple getter for US_ASCII String since it is recommended to use StandardCharsets after SDK level 19,
     * but this library currently supports 15 as the minimum.
     * @return
     */
    public static String getASCII(){
        if(Build.VERSION.SDK_INT >= 19){
            return StandardCharsets.US_ASCII.toString();
        } else {
            return "US-ASCII";
        }
    }

    /**
     * Simple getter for UTF-16 String since it is recommended to use StandardCharsets after SDK level 19,
     * but this library currently supports 15 as the minimum.
     * @return
     */
    public static String getUTF16(){
        if(Build.VERSION.SDK_INT >= 19){
            return StandardCharsets.UTF_16.toString();
        } else {
            return "UTF-16";
        }
    }

    /**
     * Simple getter for UTF-16BE String since it is recommended to use StandardCharsets after SDK level 19,
     * but this library currently supports 15 as the minimum.
     * @return
     */
    public static String getUTF16BE(){
        if(Build.VERSION.SDK_INT >= 19){
            return StandardCharsets.UTF_16BE.toString();
        } else {
            return "UTF-16BE";
        }
    }

    /**
     * Simple getter for UTF-16LE String since it is recommended to use StandardCharsets after SDK level 19,
     * but this library currently supports 15 as the minimum.
     * @return
     */
    public static String getUTF16LE(){
        if(Build.VERSION.SDK_INT >= 19){
            return StandardCharsets.UTF_16LE.toString();
        } else {
            return "UTF-16LE";
        }
    }

    //endregion
	
    //region Misc Screen Utilities
    
    
    /**
     * Calculates the % opacity I want to use. higher number means less see-through
     * @param percent % to convert, 0 means transparent, 100 means completely blocking background
     * @return int, used like this, view.getBackground().setAlpha(opacityPercent(55));
     */
    public static int opacityPercent(float percent){
        float x = 255;
        if(percent < 0 || percent > 100){
            return 255;
        } else {
            int y = (int) (x * (percent/100));
            return y;
        }
    }
    
    //region Screen Brightness
    
    /**
     * Get the current screen brightness
     * @param context {@link Context}
     * @return an int (range from 0 - 255) for brightness representation. If cannot be obtained,
     *         will return -1.
     */
    public static int getScreenBrightness(@Nonnull Context context){
        try {
            int x = Settings.System.getInt(context.getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS);
            return x;
        } catch (Settings.SettingNotFoundException e){
            e.printStackTrace();
            return -1;
        }
    }
    
    /**
     * Simple utility method
     */
    private static float convertIntAlphaToFloatPercent(int num){
        return ((float)num) / ((float)255);
    }
    
    /**
     * Get the current screen brightness
     * @param context {@link Context}
     * @return a float (range from 0 - 1) for brightness representation. If cannot be obtained,
     *         will return -1.
     */
    public static float getScreenBrightnessAsFloat(@Nonnull Context context){
        try {
            int x = Settings.System.getInt(context.getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS);
            float y = convertIntAlphaToFloatPercent(x);
            return y;
        } catch (Settings.SettingNotFoundException e){
            e.printStackTrace();
            return -1;
        }
    }
    
    /**
     * Set the screen brightness level
     * @param valueToSet value to set, must be within 0 to 255.
     */
    public static void setScreenBrightness(@Nonnull Activity activity,
                                           @IntRange(from = 0, to = 255) int valueToSet){
        if(valueToSet < 0 || valueToSet > 255){
            return;
        }
        float newBrightness = convertIntAlphaToFloatPercent(valueToSet);
        try {
            WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
            lp.screenBrightness = newBrightness;
            activity.getWindow().setAttributes(lp);
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    
    /**
     * Set the screen brightness level
     * @param valueToSet value to set, must be within 0 to 1.
     */
    public static void setScreenBrightness(@Nonnull Activity activity,
                                           @FloatRange(from = 0, to = 1) float valueToSet){
        if(valueToSet < 0 || valueToSet > 1){
            return;
        }
        try {
            WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
            lp.screenBrightness = valueToSet;
            activity.getWindow().setAttributes(lp);
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    
    //endregion
    
    //endregion
	
	//region Url Path Param Splitting
	
	
	/**
	 * Build and return a Map<String, String> from the path queryUrl passed in. IE:
	 * Convert this - "?name=pat&age=1&isMale=true&title=android_developer" into a map like this:
	 * [{"name", "Pat"}, {"age", "1"}, {"isMale", "true"}, "title", "android_developer"}]
	 * @param source String source to convert
	 * @return
	 */
	public static Map<String, String> convertQueryStringToHashMap(String source)  {
		Map<String, String> data = new HashMap<String, String>();
		
		if(StringUtilities.isNullOrEmpty(source)){
			return data;
		}
		if(source.startsWith("?")){
			source = source.replaceFirst("\\?", "&");
		}
		final String[] arrParameters = source.split("&");
		for (final String tempParameterString : arrParameters) {
			
			final String[] arrTempParameter = tempParameterString
					.split("=");
			if(arrTempParameter.length == 0){
				continue;
			}
			if (arrTempParameter.length >= 2) {
				final String parameterKey = arrTempParameter[0];
				final String parameterValue = arrTempParameter[1];
				if(StringUtilities.isNullOrEmpty(parameterKey) &&
						StringUtilities.isNullOrEmpty(parameterValue)){
					continue;
				}
				data.put(parameterKey, parameterValue);
			} else {
				final String parameterKey = arrTempParameter[0];
				if(!StringUtilities.isNullOrEmpty(parameterKey)){
					data.put(parameterKey, "");
				}
			}
		}
		
		return data;
	}
	
	//endregion
	
	//region Checking Rooted Devices
	
	/**
	 * Check if a device is rooted
	 * Overloaded so as to make it more easily accessible.
	 * @return True means the device is rooted, false means it is not
	 */
	public static boolean isDeviceRooted() {
		return SystemUtilities.isDeviceRooted();
	}
	
	//endregion
	
	//region Deprecated or Unused
	
	
	/**
	 * Checks a boolean for null (returns false if it is null) and then returns actual
	 * bool if not null
	 * @param bool boolean to check
	 * @return Boolean, true if it is null or empty, false it if is not
	 * @Deprecated moved to {@link BoolUtilities#isTrue(Boolean)}
	 */
	@Deprecated
	public static boolean isBooleanNullTrueFalse(Boolean bool){
		if(bool == null){
			return false;
		} else {
			return bool;
		}
	}
	
	/**
	 * Gets the package name. If null returned, send call again with context
	 * @return
	 * @deprecated Please redirect to {@link SystemUtilities#getPackageName()}
	 */
	@Deprecated
	public static String getPackageName(){
		try {
			return BuildConfig.APPLICATION_ID;
		} catch (Exception e){
			return null;
		}
	}
	
	/**
	 * Overloaded method in case getPackageName returns null
	 * @param context context
	 * @return
	 * @deprecated Please redirect to {@link SystemUtilities#getPackageName(Context)}
	 */
	@Deprecated
	public static String getPackageName(Context context){
		String packageName = null;
		try {
			packageName = context.getPackageManager().getPackageInfo(
					getPackageName(), 0).packageName;
			if(!StringUtilities.isNullOrEmpty(packageName)){
				return packageName;
			}
		} catch (Exception e){}
		try{
			packageName = context.getPackageName();
		} catch (Exception e){e.printStackTrace();}
		return packageName;
	}

    /*
    public static Class getClass(String classname) throws ClassNotFoundException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if(classLoader == null)
            classLoader = Singleton.class.getClassLoader();
        return (classLoader.loadClass(classname));
    }
    */
	
	//Not used atm. Re-working to fix exception issues. Supposed to work via reflection.
	//Link: http://stackoverflow.com/questions/6591665/merging-two-objects-in-java
	@Deprecated
	private Object mergeObjects(Object obj, Object update){
		if(!obj.getClass().isAssignableFrom(update.getClass())){
			return null;
		}
		
		Method[] methods = obj.getClass().getMethods();
		
		for(Method fromMethod: methods){
			if(fromMethod.getDeclaringClass().equals(obj.getClass())
					&& fromMethod.getName().startsWith("get")){
				
				String fromName = fromMethod.getName();
				String toName = fromName.replace("get", "set");
				
				try {
					Method toMetod = obj.getClass().getMethod(toName, fromMethod.getReturnType());
					Object value = fromMethod.invoke(update, (Object[])null);
					if(value != null){
						toMetod.invoke(obj, value);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}
	
	
	//endregion
}
