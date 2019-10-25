package com.pgmacdesign.pgmactips.networkclasses.sslsocketsandprotocols;

import android.os.Build;

/**
 * This is a wrapper to link to the Secure Socket (SSL) {@link javax.net.ssl} algorithms which in turn is
 * utilizing data from this url: https://docs.oracle.com/javase/8/docs/technotes/guides/security/StandardNames.html
 * Created by pmacdowell on 10/2/2018.
 */
public enum SSLProtocolOptions {
    Default("Default", Build.VERSION_CODES.GINGERBREAD_MR1, Build.VERSION_CODES.CUR_DEVELOPMENT),
    SSL("SSL", Build.VERSION_CODES.GINGERBREAD_MR1, Build.VERSION_CODES.CUR_DEVELOPMENT),
    SSLv3("SSLv3", Build.VERSION_CODES.GINGERBREAD_MR1, Build.VERSION_CODES.N_MR1),
    TLS("TLS", Build.VERSION_CODES.BASE, Build.VERSION_CODES.CUR_DEVELOPMENT),
    TLSv1("TLSv1", Build.VERSION_CODES.GINGERBREAD_MR1, Build.VERSION_CODES.CUR_DEVELOPMENT),
    TLSv1dot1("TLSv1.1", Build.VERSION_CODES.JELLY_BEAN, Build.VERSION_CODES.CUR_DEVELOPMENT),
    TLSv1dot2("TLSv1.2", Build.VERSION_CODES.JELLY_BEAN, Build.VERSION_CODES.CUR_DEVELOPMENT)
//    TLSv1dot2("TLSv1.3", Build.VERSION_CODES.JELLY_BEAN, Build.VERSION_CODES.CUR_DEVELOPMENT) //todo
    ;

    public String name;
    public int minimumAPISupportLevel, maximumAPISupportLevel;

    SSLProtocolOptions(String name, int minimumAPISupportLevel,
                       int maximumAPISupportLevel){
        this.name = name;
        this.minimumAPISupportLevel = minimumAPISupportLevel;
        this.maximumAPISupportLevel = maximumAPISupportLevel;
    }

    public static boolean isSupported(SSLProtocolOptions versionToCheck){
        if(versionToCheck == null){
            return false;
        }
        return ((Build.VERSION.SDK_INT >= versionToCheck.minimumAPISupportLevel) &&
                (Build.VERSION.SDK_INT <= versionToCheck.maximumAPISupportLevel));
    }

    /**
     * Checks if TLS1.2 needs to be forced. This should only be called if the SSLProtocolOption
     * being used is {@link SSLProtocolOptions#TLSv1dot2}.
     * @return False if it does not need to be forced, True if it does. (See {@link Tls12SocketFactory})
     */
    public static boolean requiresForcedTLS1dot2(){
        if((Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) &&
                (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT)){
            return true;
        }
        return false;
    }
}
