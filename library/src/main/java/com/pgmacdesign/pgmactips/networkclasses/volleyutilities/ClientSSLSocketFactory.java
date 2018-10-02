package com.pgmacdesign.pgmactips.networkclasses.volleyutilities;


import android.content.Context;
import android.net.SSLCertificateSocketFactory;
import android.net.SSLSessionCache;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.IOException;
import java.net.Socket;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Created by pmacdowell on 10/2/2018.
 */

public class ClientSSLSocketFactory extends SSLCertificateSocketFactory {

    private static SSLContext sslContext;

    /**
     * @param handshakeTimeoutMillis
     * @deprecated Use {@link #getDefault(int)} instead.
     */
    private ClientSSLSocketFactory(int handshakeTimeoutMillis) {
        super(handshakeTimeoutMillis);
    }

    /**
     * Build SSLSocket Factory
     * @param context
     * @return {@link SSLSocketFactory}
     */
    public static SSLSocketFactory getSocketFactory(@NonNull Context context, @Nullable Integer handshakeTimeoutMilliseconds){
        try {
            X509TrustManager tm = new X509TrustManager() {
                public void checkClientTrusted(X509Certificate[] xcs, String string) throws CertificateException {}

                public void checkServerTrusted(X509Certificate[] xcs, String string) throws CertificateException {}

                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            };
            sslContext = SSLContext.getInstance("TLS");
//            sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, new TrustManager[] { tm }, new java.security.SecureRandom());
            SSLSocketFactory ssf = ClientSSLSocketFactory.getDefault(
                    (handshakeTimeoutMilliseconds != null) ? handshakeTimeoutMilliseconds : 10000,
                    new SSLSessionCache(context));
            return ssf;
        } catch (Exception ex) {
            return null;
        }
    }

    @Override
    public Socket createSocket(Socket socket, String host, int port, boolean autoClose) {
        try {
            return sslContext.getSocketFactory().createSocket(socket, host, port, autoClose);
        } catch (IOException ioe){
            ioe.printStackTrace();
            return null;
        }
    }

    @Override
    public Socket createSocket() throws IOException {
        try {
            return sslContext.getSocketFactory().createSocket();
        } catch (IOException ioe){
            ioe.printStackTrace();
            return null;
        }
    }
}