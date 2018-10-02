package com.pgmacdesign.pgmactips.networkclasses.sslsocketsandprotocols;


import android.content.Context;
import android.net.SSLCertificateSocketFactory;
import android.net.SSLSessionCache;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.IOException;
import java.net.Socket;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.ConnectionSpec;
import okhttp3.TlsVersion;

/**
 * ClientSSLSocket Factory
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

    public static SSLSocketFactory getSocketFactory(@NonNull Context context,
                                                    @Nullable Integer handshakeTimeoutMilliseconds){
        return ClientSSLSocketFactory.getSocketFactory(context,
                handshakeTimeoutMilliseconds, SSLProtocolOptions.TLS);
    }
    /**
     * Build SSLSocket Factory
     * @param context
     * @return {@link SSLSocketFactory}
     */
    public static SSLSocketFactory getSocketFactory(@NonNull Context context,
                                                    @Nullable Integer handshakeTimeoutMilliseconds,
                                                    SSLProtocolOptions sslProtocolOption){
        if(sslProtocolOption == null){
            sslProtocolOption = SSLProtocolOptions.TLS;
        }
        try {
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(
                    TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init((KeyStore) null);
            TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
            if (trustManagers.length != 1 || !(trustManagers[0] instanceof X509TrustManager)) {
                throw new IllegalStateException("Unexpected default trust managers:"
                        + Arrays.toString(trustManagers));
            }
            X509TrustManager trustManager = (X509TrustManager) trustManagers[0];

//            X509TrustManager tm = new X509TrustManager() {
//                public void checkClientTrusted(X509Certificate[] xcs, String string) throws CertificateException {}
//                public void checkServerTrusted(X509Certificate[] xcs, String string) throws CertificateException {}
//                public X509Certificate[] getAcceptedIssuers() {
//                    return null;
//                }
//            };

            //Get SSL Protocol set first
            sslContext = SSLContext.getInstance(sslProtocolOption.name);
            sslContext.init(null, new TrustManager[] { trustManager }, null);

            //Determine if forced 1.2 is necessary
            boolean needToForce1dot2 = SSLProtocolOptions.requiresForcedTLS1dot2();
            SSLSocketFactory ssf;
            if(needToForce1dot2 && sslProtocolOption == SSLProtocolOptions.TLSv1dot2){
                try {
                    SSLContext sc = SSLContext.getInstance(SSLProtocolOptions.TLSv1dot2.name);
                    sc.init(null, null, null);
                    ssf = new Tls12SocketFactory(sc.getSocketFactory());

                    ConnectionSpec cs = new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                            .tlsVersions(TlsVersion.TLS_1_2)
                            .build();
                    List<ConnectionSpec> specs = new ArrayList<>();
                    specs.add(cs);
                    specs.add(ConnectionSpec.COMPATIBLE_TLS);
                    specs.add(ConnectionSpec.CLEARTEXT);
//                    builder.connectionSpecs(specs);
                    return ssf;


                } catch (Exception exc) {
                    ssf = ClientSSLSocketFactory.getDefault(
                            (handshakeTimeoutMilliseconds != null)
                                    ? handshakeTimeoutMilliseconds : 10000,
                            new SSLSessionCache(context));
                    return ssf;
                }
            } else {
                ssf = ClientSSLSocketFactory.getDefault(
                        (handshakeTimeoutMilliseconds != null) ? handshakeTimeoutMilliseconds : 10000,
                        new SSLSessionCache(context));
                return ssf;
            }
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