package com.pgmacdesign.pgmactips.magreaderutils;

import com.pgmacdesign.pgmactips.misc.TempString;

/**
 * Interface for linking with the {@link TempString} class.
 * Temp Strings are not persistable and can be fully deleted by calling {@link TempString#disposeData()}
 * Created by pmacdowell on 2017-08-03.
 */
public interface TempStringInterface {

    public void clearTempString();
    public String getTempString();
    public boolean tempStringHasData();
    public boolean tempStringTooLong();
}
