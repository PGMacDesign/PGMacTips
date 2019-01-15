package com.pgmacdesign.pgmactips.networkclasses.sslsocketsandprotocols;


import android.content.Context;
import android.net.SSLCertificateSocketFactory;
import android.net.SSLSessionCache;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.pgmacdesign.pgmactips.utilities.L;

import java.io.IOException;
import java.net.Socket;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

/**
 * ClientSSLSocket Factory for use in building sockets to use
 * Note: Test with this URL to confirm that TLS1.2 is working properly:
 *      https://www.ssllabs.com/ssltest/viewMyClient.html
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
     * Overloaded to allow for omission of params
     */
    public static SSLSocketFactory getSocketFactory(@NonNull Context context) {
        return ClientSSLSocketFactory.getSocketFactory(context,
                null, SSLProtocolOptions.TLS, false);
    }

    /**
     * Overloaded to allow for omission of params
     */
    public static SSLSocketFactory getSocketFactory(@NonNull Context context,
                                                    @Nullable Integer handshakeTimeoutMilliseconds) {
        return ClientSSLSocketFactory.getSocketFactory(context,
                handshakeTimeoutMilliseconds, SSLProtocolOptions.TLS, false);
    }


    /**
     * Build SSLSocket Factory
     *
     * @param context                      {@link Context}
     * @param handshakeTimeoutMilliseconds Handshake timeout in milliseconds
     * @param sslProtocolOption            {@link SSLProtocolOptions}
     * @param forceAcceptAllCertificates   Force to accept ALL SSL handshakes.
     *                                     WARNING! THIS IS DANGEROUS AND CAN LEAD YOUR APP OPEN TO MALICIOUS ATTACKS!
     *                                     The main reason this option is available is because of API levels 16-19 and the
     *                                     subsequent issue with regards to TrustManagers not working properly. For more info, see
     *                                     this link: https://stackoverflow.com/questions/52630694/how-to-get-trust-anchors-to-work-properly-on-android-api-levels-16-19
     * @return {@link SSLSocketFactory}
     */
    public static SSLSocketFactory getSocketFactory(@NonNull Context context,
                                                    @Nullable Integer handshakeTimeoutMilliseconds,
                                                    @Nullable SSLProtocolOptions sslProtocolOption,
                                                    @Nullable Boolean forceAcceptAllCertificates) {
        if (sslProtocolOption == null) {
            sslProtocolOption = SSLProtocolOptions.TLS;
        }
        if (forceAcceptAllCertificates == null) {
            forceAcceptAllCertificates = false;
        }
        SSLSocketFactory ssf = null;
        try {
            X509TrustManager trustManager;
            if (forceAcceptAllCertificates) {
                trustManager = new X509TrustManager() {
                    public void checkClientTrusted(X509Certificate[] xcs, String string)
                            throws CertificateException {
                    }

                    public void checkServerTrusted(X509Certificate[] xcs, String string)
                            throws CertificateException {
                    }

                    public X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }
                };
            } else {
                TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(
                        TrustManagerFactory.getDefaultAlgorithm());
                trustManagerFactory.init((KeyStore) null);
                TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
                if (trustManagers.length != 1 || !(trustManagers[0] instanceof X509TrustManager)) {
                    throw new IllegalStateException("Unexpected default trust managers:"
                            + Arrays.toString(trustManagers));
                }
                trustManager = (X509TrustManager) trustManagers[0];
            }

            //Get SSL Protocol set first
            sslContext = SSLContext.getInstance(sslProtocolOption.name);
            sslContext.init(null, new TrustManager[]{trustManager}, null);

            //Determine if forced 1.2 is necessary
            boolean needToForce1dot2 = SSLProtocolOptions.requiresForcedTLS1dot2();
            if (needToForce1dot2 && sslProtocolOption == SSLProtocolOptions.TLSv1dot2) {
                try {
                    SSLContext sc = SSLContext.getInstance(SSLProtocolOptions.TLSv1dot2.name);
                    sc.init(null, new TrustManager[]{trustManager}, null);
                    ssf = new Tls12SocketFactory(sc.getSocketFactory());
                } catch (Exception exc) {
                    exc.printStackTrace();
                    ssf = ClientSSLSocketFactory.getDefault(
                            (handshakeTimeoutMilliseconds != null)
                                    ? handshakeTimeoutMilliseconds : 10000,
                            new SSLSessionCache(context));
                }
            } else {
                ssf = ClientSSLSocketFactory.getDefault(
                        (handshakeTimeoutMilliseconds != null) ? handshakeTimeoutMilliseconds : 10000,
                        new SSLSessionCache(context));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
        if (ssf != null) {
            HttpsURLConnection.setDefaultSSLSocketFactory(ssf);
        }
        return ssf;
    }

    @Override
    public Socket createSocket(Socket socket, String host, int port, boolean autoClose) {
        try {
            if (socket != null) {
                if (socket instanceof SSLSocket) {
                    if (isTLSServerEnabled((SSLSocket) socket)) {
                        ((SSLSocket) socket).setEnabledProtocols(new String[]{
                                SSLProtocolOptions.TLSv1dot1.name, SSLProtocolOptions.TLSv1dot2.name});
                    }
                }
            }
            return sslContext.getSocketFactory().createSocket(socket, host, port, autoClose);
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return null;
        }
    }

    @Override
    public Socket createSocket() throws IOException {
        try {
            return sslContext.getSocketFactory().createSocket();
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return null;
        }
    }

    private static boolean isTLSServerEnabled(SSLSocket sslSocket) {
        L.m("SUPPORTED PROTOCOLS: " + sslSocket.getSupportedProtocols().toString());
        for (String protocol : sslSocket.getSupportedProtocols()) {
            if (protocol.equals(SSLProtocolOptions.TLSv1dot1.name) ||
                    protocol.equals(SSLProtocolOptions.TLSv1dot2.name)) {
                return true;
            }
        }
        return false;
    }
}