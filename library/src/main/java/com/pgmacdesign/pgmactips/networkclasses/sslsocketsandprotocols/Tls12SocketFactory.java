package com.pgmacdesign.pgmactips.networkclasses.sslsocketsandprotocols;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

/**
 * This is designed for API levels {@link android.os.Build.VERSION_CODES#JELLY_BEAN} to
 * {@link android.os.Build.VERSION_CODES#KITKAT} (16 -- 19) as TLS1.2 is supported, but
 * not enabled for those versions. This will force TLS1.2 for the SSLSocketFactory on
 * API levels 16-19. If the build version is not 16-19, it will be ignored
 *
 * Class pulled from: https://github.com/square/okhttp/issues/2372
 * Enables TLS v1.2 when creating SSLSockets.
 * <p/>
 * For some reason, android supports TLS v1.2 from API 16, but enables it by
 * default only from API 20.
 * @link https://developer.android.com/reference/javax/net/ssl/SSLSocket.html
 * @see SSLSocketFactory
 * Created by pmacdowell on 10/2/2018.
 */
public class Tls12SocketFactory extends SSLSocketFactory {
    private static final String[] TLS_V12_ONLY = {"TLSv1.2"};

    final SSLSocketFactory delegate;

    public Tls12SocketFactory(SSLSocketFactory base) {
        this.delegate = base;
    }

    @Override
    public String[] getDefaultCipherSuites() {
        return delegate.getDefaultCipherSuites();
    }

    @Override
    public String[] getSupportedCipherSuites() {
        return delegate.getSupportedCipherSuites();
    }

    @Override
    public Socket createSocket(Socket s, String host, int port, boolean autoClose) throws IOException {
        return patch(delegate.createSocket(s, host, port, autoClose));
    }

    @Override
    public Socket createSocket(String host, int port) throws IOException, UnknownHostException {
        return patch(delegate.createSocket(host, port));
    }

    @Override
    public Socket createSocket(String host, int port, InetAddress localHost, int localPort) throws IOException, UnknownHostException {
        return patch(delegate.createSocket(host, port, localHost, localPort));
    }

    @Override
    public Socket createSocket(InetAddress host, int port) throws IOException {
        return patch(delegate.createSocket(host, port));
    }

    @Override
    public Socket createSocket(InetAddress address, int port, InetAddress localAddress, int localPort) throws IOException {
        return patch(delegate.createSocket(address, port, localAddress, localPort));
    }

    private Socket patch(Socket s) {
        if (s instanceof SSLSocket) {
            ((SSLSocket) s).setEnabledProtocols(TLS_V12_ONLY);
        }
        return s;
    }

    @Override
    public Socket createSocket() throws IOException {
//        try {
//            return sslContext.getSocketFactory().createSocket(socket, host, port, autoClose);
//        } catch (IOException ioe){
//            ioe.printStackTrace();
//            return null;
//        }
        return super.createSocket();
    }
}