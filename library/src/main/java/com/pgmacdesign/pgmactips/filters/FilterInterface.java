package com.pgmacdesign.pgmactips.filters;

import java.util.List;

/**
 * Classes must implement this and subsequently return the data they
 * want to be compared against (filtered)
 * Created by pmacdowell on 2017-11-20.
 */
public interface FilterInterface {
    public String getStringFilter();
    public List<String> getStringsFilter();
    public Double getDoubleFilter();
    public Boolean getBooleanFilter();
    public Integer getIntegerFilter();
}
