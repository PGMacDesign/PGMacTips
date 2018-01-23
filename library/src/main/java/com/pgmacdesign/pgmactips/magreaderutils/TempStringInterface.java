package com.pgmacdesign.pgmactips.magreaderutils;

import com.pgmacdesign.pgmactips.misc.TempString;

/**
 * Interface for linking with the {@link TempString} class
 * Created by pmacdowell on 2017-08-03.
 */
public interface TempStringInterface {

    public void clearTempString();
    public String getTempString();
    public boolean tempStringHasData();
    public boolean tempStringTooLong();
}
