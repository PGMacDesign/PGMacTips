package com.pgmacdesign.pgmactips.biometricutilities;

/**
 * Simple class for Exceptions. Leaving here for expanding it in the future
 * Created by pmacdowell on 7/6/2018.
 */
public class BiometricException extends Exception {
    public BiometricException(Exception e){
        super(e);
    }
    public BiometricException(String e){
        super(e);
    }
}
