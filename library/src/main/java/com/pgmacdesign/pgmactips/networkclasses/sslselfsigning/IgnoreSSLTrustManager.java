package com.pgmacdesign.pgmactips.networkclasses.sslselfsigning;


import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.X509TrustManager;
/**
 * From: https://github.com/erickok/transdroid/blob/abe1121da8c5a15f2b3ff8a4a1c4d2aa0876a1e3/app/src/main/java/org/transdroid/daemon/util/IgnoreSSLTrustManager.java
 * Created by pmacdowell on 10/3/2018.
 */
public class IgnoreSSLTrustManager implements X509TrustManager {

    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        // Perform no check whatsoever on the validity of the SSL certificate
    }

    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        // Perform no check whatsoever on the validity of the SSL certificate
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
	    return new X509Certificate[]{};
    }

}