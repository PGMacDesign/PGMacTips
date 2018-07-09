package com.pgmacdesign.pgmactips.biometricprintutilities;

/**
 * Simple class for Exceptions. Leaving here for expanding it in the future
 * Created by pmacdowell on 7/6/2018.
 */
public class FingerprintException extends Exception {
    public FingerprintException(Exception e){
        super(e);
    }
    public FingerprintException(String e){
        super(e);
    }
}
