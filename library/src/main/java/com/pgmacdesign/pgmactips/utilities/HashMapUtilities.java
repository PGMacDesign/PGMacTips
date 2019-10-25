package com.pgmacdesign.pgmactips.utilities;

import android.os.Build;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by pmacdowell on 2017-11-28.
 */

public class HashMapUtilities {

    /**
     * Sort a map via the keys
     * @param myMap Map value can be of any time, but keys must be ints for sorting
     * @return A {@link LinkedHashMap} that matches the erasure type, but is sorted
     */
    public static LinkedHashMap sortMapKeys(Map<Integer, ?> myMap){
        return sortMapKeys(myMap, false);
    }

    /**
     * Sort a map via the keys
     * @param myMap Map value can be of any time, but keys must be ints for sorting
     * @param reverseOrder if true, will reverse the sort order
     * @return A {@link LinkedHashMap} that matches the erasure type, but is sorted
     */
    public static LinkedHashMap sortMapKeys(Map<Integer, ?> myMap, final boolean reverseOrder){
        if(myMap == null){
            return null;
        }
        List<Map.Entry<Integer,?>> entries = new ArrayList<Map.Entry<Integer,?>>(
                myMap.entrySet());
        Collections.sort(entries,
                new Comparator<Map.Entry<Integer,?>>() {
                    public int compare(Map.Entry<Integer,?> a, Map.Entry<Integer,?> b) {
                        int inta = NumberUtilities.getInt(a.getKey());
                        int intb = NumberUtilities.getInt(b.getKey());
                        if(Build.VERSION.SDK_INT >= 19) {
                            if (!reverseOrder) {
                                return Integer.compare(inta, intb);
                            } else {
                                return Integer.compare(intb, inta);
                            }
                        } else {
                            if (!reverseOrder) {
                                return (inta < intb) ? -1 : ((inta == intb) ? 0 : 1);
                            } else {
                                return (intb < inta) ? -1 : ((intb == inta) ? 0 : 1);
                            }
                        }
                    }
                }
        );
        LinkedHashMap toReturn = new LinkedHashMap<>();
        for(Map.Entry<Integer,?> map : entries){
            if(map != null){
                toReturn.put(map.getKey(), map.getValue());
            }
        }
        return toReturn;
    }

    /**
     * Sort a map via the values
     * @param myMap Map key can be of any time, but values must be ints for sorting
     * @return A {@link LinkedHashMap} that matches the erasure type, but is sorted
     */
    public static LinkedHashMap sortMapValues(Map<?, Integer> myMap){
        return sortMapValues(myMap, false);
    }

    /**
     * Sort a map via the values
     * @param myMap Map key can be of any time, but values must be ints for sorting
     * @param reverseOrder if true, will reverse the sort order
     * @return A {@link LinkedHashMap} that matches the erasure type, but is sorted
     */
    public static LinkedHashMap sortMapValues(Map<?, Integer> myMap, final boolean reverseOrder){
        if(myMap == null){
            return null;
        }
        List<Map.Entry<?,Integer>> entries = new ArrayList<Map.Entry<?,Integer>>(
                myMap.entrySet());
        Collections.sort(entries,
                new Comparator<Map.Entry<?,Integer>>() {
                    public int compare(Map.Entry<?,Integer> a, Map.Entry<?,Integer> b) {
                        int inta = NumberUtilities.getInt(a.getValue());
                        int intb = NumberUtilities.getInt(b.getValue());
                        if(Build.VERSION.SDK_INT >= 19) {
                            if (!reverseOrder) {
                                return Integer.compare(inta, intb);
                            } else {
                                return Integer.compare(intb, inta);
                            }
                        } else {
                            if (!reverseOrder) {
                                return (inta < intb) ? -1 : ((inta == intb) ? 0 : 1);
                            } else {
                                return (intb < inta) ? -1 : ((intb == inta) ? 0 : 1);
                            }
                        }
                    }
                }
        );
        LinkedHashMap toReturn = new LinkedHashMap<>();
        for(Map.Entry<?,Integer> map : entries){
            if(map != null){
                toReturn.put(map.getKey(), map.getValue());
            }
        }
        return toReturn;
    }

    /**
     * {@link MiscUtilities#isMapNullOrEmpty(Map)}
     */
    public static boolean isMapNullOrEmpty(Map<?, ?> myMap){
        return MiscUtilities.isMapNullOrEmpty(myMap);
    }

    /**
     * {@link MiscUtilities#printOutHashMap(Map)}
     */
    public static void printOutHashMap(Map<?,?> myMap){
        MiscUtilities.printOutHashMap(myMap);
    }

}
