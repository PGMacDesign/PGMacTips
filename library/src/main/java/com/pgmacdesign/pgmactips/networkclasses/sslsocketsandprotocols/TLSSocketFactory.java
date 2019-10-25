package com.pgmacdesign.pgmactips.networkclasses.sslsocketsandprotocols;

import android.os.Build;

import androidx.annotation.Nullable;

import com.pgmacdesign.pgmactips.networkclasses.sslsocketsandprotocols.SSLProtocolOptions;
import com.pgmacdesign.pgmactips.utilities.L;
import com.pgmacdesign.pgmactips.utilities.MiscUtilities;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class TLSSocketFactory extends SSLSocketFactory {
	private SSLSocketFactory internalSSLSocketFactory;
	
	/**
	 * Constructor for this class.
	 * @param sslProtocolOptions {@link SSLProtocolOptions} If null is passed, will default to
	 *                                                     {@link SSLProtocolOptions#TLS}
	 * @throws KeyManagementException
	 * @throws NoSuchAlgorithmException
	 */
	public TLSSocketFactory(@Nullable SSLProtocolOptions sslProtocolOptions) throws KeyManagementException, NoSuchAlgorithmException {
		SSLContext context = SSLContext.getInstance((sslProtocolOptions == null)
				? SSLProtocolOptions.TLS.name : sslProtocolOptions.name);
		context.init(null, null, null);
		this.internalSSLSocketFactory = context.getSocketFactory();
	}
	
	@Override
	public String[] getDefaultCipherSuites() {
		return this.internalSSLSocketFactory.getDefaultCipherSuites();
	}
	
	@Override
	public String[] getSupportedCipherSuites() {
		return this.internalSSLSocketFactory.getSupportedCipherSuites();
	}
	
	@Override
	public Socket createSocket() throws IOException {
		return enableTLSOnSocket(this.internalSSLSocketFactory.createSocket());
	}
	
	@Override
	public Socket createSocket(Socket s, String host, int port, boolean autoClose) throws IOException {
		return enableTLSOnSocket(this.internalSSLSocketFactory.createSocket(s, host, port, autoClose));
	}
	
	@Override
	public Socket createSocket(String host, int port) throws IOException, UnknownHostException {
		return enableTLSOnSocket(this.internalSSLSocketFactory.createSocket(host, port));
	}
	
	@Override
	public Socket createSocket(String host, int port, InetAddress localHost, int localPort) throws IOException, UnknownHostException {
		return enableTLSOnSocket(this.internalSSLSocketFactory.createSocket(host, port, localHost, localPort));
	}
	
	@Override
	public Socket createSocket(InetAddress host, int port) throws IOException {
		return enableTLSOnSocket(this.internalSSLSocketFactory.createSocket(host, port));
	}
	
	@Override
	public Socket createSocket(InetAddress address, int port, InetAddress localAddress, int localPort) throws IOException {
		return enableTLSOnSocket(this.internalSSLSocketFactory.createSocket(address, port, localAddress, localPort));
	}
	
	private Socket enableTLSOnSocket(Socket socket) {
		if(socket != null && (socket instanceof SSLSocket)) {
			Set<String> tlsStrs = new HashSet<>();
			int currentAPILevel = Build.VERSION.SDK_INT;
			//As per {@link javax.net.ssl.SSLSocket.java}
			for(SSLProtocolOptions s : SSLProtocolOptions.values()){
				if(s == SSLProtocolOptions.Default){
//					tlsStrs.add(SSLProtocolOptions.TLS.name);
					continue;
				}
				if(currentAPILevel >= s.minimumAPISupportLevel && currentAPILevel <= s.maximumAPISupportLevel) {
					switch (s){
						case TLSv1:
						case TLS:
							tlsStrs.add(SSLProtocolOptions.TLSv1.name);
							break;
							
						//Note, @Deprecated as per docs in {@link javax.net.ssl.SSLSocket.java}
						case SSLv3:
							tlsStrs.add(SSLProtocolOptions.SSLv3.name);
							break;
							
						case TLSv1dot1:
							tlsStrs.add(SSLProtocolOptions.TLSv1dot1.name);
							break;
							
						case TLSv1dot2:
							tlsStrs.add(SSLProtocolOptions.TLSv1dot2.name);
							break;
					}
				}
			}
			((SSLSocket)socket).setEnabledProtocols(tlsStrs.toArray(new String[tlsStrs.size()]));
		}
		return socket;
	}
	
}
