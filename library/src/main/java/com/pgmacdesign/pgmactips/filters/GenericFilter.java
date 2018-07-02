package com.pgmacdesign.pgmactips.filters;

import android.support.annotation.NonNull;
import android.widget.Filter;

import com.pgmacdesign.pgmactips.adaptersandlisteners.OnTaskCompleteListener;
import com.pgmacdesign.pgmactips.misc.PGMacTipsConstants;
import com.pgmacdesign.pgmactips.utilities.L;
import com.pgmacdesign.pgmactips.utilities.MiscUtilities;
import com.pgmacdesign.pgmactips.utilities.StringUtilities;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * A Generic Filter
 * Created by pmacdowell on 2017-11-20.
 */
public class GenericFilter <T extends FilterInterface> {

    private List<T> objectsToFilter, filteredList;
    private OnTaskCompleteListener listener;
    private boolean ignorecaseForStrings;
    private Pattern pattern;

    /**
     * Constructor
     * @param objectsToFilter List of objects to filter. The class being sent in must
     *                        implement {@link FilterInterface}
     * @param listener Listener to send data back on. Returned data will always be a list of
     *                 T Objects (if none, will be an empty list) and the int tag will always be:
     *                 {@link PGMacTipsConstants#TAG_GENERIC_FILTER_RESULTS}
     */
    public GenericFilter(@NonNull List<T> objectsToFilter,
                         @NonNull OnTaskCompleteListener listener){
        this.objectsToFilter = objectsToFilter;
        this.listener = listener;
        this.ignorecaseForStrings = true;
        this.setCustomRegularExpression(null);
    }

    /**
     * Decide whether or not the String parsed should ignore the case being compared.
     * This will default to true unless set otherwise.
     * @param bool If true, case "pat" will be equal to "PAT".
     *             If false, case "pat" will NOT be equal to "PAT"
     */
    public void shouldIgnoreCaseForStrings(boolean bool){
        this.ignorecaseForStrings = bool;
    }

    /**
     * Custom regular expression to use in the String comparison
     * Note! It attempts to compare identical(ness) first before running the regex. If two
     * Strings are identical (dependendent upon {@link GenericFilter#ignorecaseForStrings}),
     * then it will be added to the list. If the strings are not identical, it will move on
     * to check the regex and check for passing there. This would be used if (IE) you wanted
     * to compare against someone typing in an address that can vary slightly in spelling.
     * @param regex Regular expression to use {@link java.util.regex.Pattern}
     */
    public void setCustomRegularExpression(String regex){
        if(!StringUtilities.isNullOrEmpty(regex)){
            try {
                pattern = Pattern.compile(regex);
            } catch (PatternSyntaxException e){
                L.m(regex +" it not a proper regular expression: " + e.getMessage());
                pattern = null;
            }
        } else {
            pattern = null;
        }
    }

    /**
     * Get the filter to be used in the adapter / class
     * @return {@link Filter}
     */
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    filteredList = objectsToFilter;
                } else {
                    List<T> myFilteredList = new ArrayList<>();
                    for (T t: objectsToFilter) {
                        if(t == null){
                            continue;
                        }

                        //Check Boolean
                        Boolean bool = t.getBooleanFilter();
                        if(bool != null){
                            try {
                                Boolean searchBool = Boolean.parseBoolean(charString);
                                if(searchBool == bool){
                                    filteredList.add(t);
                                    continue;
                                }
                            } catch (Exception e){}
                        }

                        //Check String
                        String str = t.getStringFilter();
                        if(!StringUtilities.isNullOrEmpty(str)){
                            try {
                                if(ignorecaseForStrings){
                                    if(str.equalsIgnoreCase(charString)){
                                        filteredList.add(t);
                                        continue;
                                    }
                                } else {
                                    if(str.equals(charString)){
                                        filteredList.add(t);
                                        continue;
                                    }
                                }
                            } catch (Exception e){}
                        }

                        //Check Regex
                        if(pattern != null) {
                            try {
                                Matcher matcher = pattern.matcher(charString);
                                if(matcher.matches()){
                                    filteredList.add(t);
                                    continue;
                                }
                            } catch (Exception e) {}
                        }

                        //Check List of Strings
                        List<String> strs = t.getStringsFilter();
                        if(!MiscUtilities.isListNullOrEmpty(strs)) {
                            outerloop:
                            for(String str2 : strs) {
                                if (!StringUtilities.isNullOrEmpty(str2)) {
                                    try {
                                        if (ignorecaseForStrings) {
                                            if (str2.equalsIgnoreCase(charString)) {
                                                filteredList.add(t);
                                                break outerloop;
                                            }
                                        } else {
                                            if (str2.equals(charString)) {
                                                filteredList.add(t);
                                                break outerloop;
                                            }
                                        }
                                    } catch (Exception e) {}
                                }
                                //Check Regex
                                if(pattern != null) {
                                    try {
                                        Matcher matcher = pattern.matcher(charString);
                                        if(matcher.matches()){
                                            filteredList.add(t);
                                            break outerloop;
                                        }
                                    } catch (Exception e) {}
                                }
                            }
                        }

                        //Check Double
                        Double d = t.getDoubleFilter();
                        if(d != null){
                            try {
                                Double dbl = Double.parseDouble(charString);
                                if(Double.compare(dbl, d) == 0){
                                    filteredList.add(t);
                                    continue;
                                }
                            } catch (Exception e){}
                        }

                        //Check Integer
                        Integer x = t.getIntegerFilter();
                        if(x != null){
                            try {
                                Integer intx = Integer.parseInt(charString);
                                if(Integer.compare(x, intx) == 0){
                                    filteredList.add(t);
                                    continue;
                                }
                            } catch (Exception e){}
                        }
                    }
                    filteredList = myFilteredList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                List<T> results;
                try {
                    results = (ArrayList<T>) filterResults.values;
                } catch (Exception e){
                    results = new ArrayList<>();
                }
                listener.onTaskComplete(results,
                        PGMacTipsConstants.TAG_GENERIC_FILTER_RESULTS);
            }
        };
    }
}
