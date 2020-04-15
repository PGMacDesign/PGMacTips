package com.pgmacdesign.pgmactips.networkclasses;

import androidx.annotation.NonNull;

import com.pgmacdesign.pgmactips.misc.PGMacTipsConstants;
import com.pgmacdesign.pgmactips.utilities.StringUtilities;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Class for testing network configurations and statuses.
 * Sample would be for checking DNS
 */
public class NetworkingUtilities {
	
	//region DNS
	
	/**
	 * Test that the DNS is successfully resolving a hostname. This is useful for detecting the
	 * "Unable to resolve host "{my.api.website}": No address associated with hostname" error
	 * @param hostname hostname to check, IE "{my.api.website}"
	 * @return true if it can resolve it, false if it cannot
	 */
	public static boolean testDNS(String hostname) {
		return testDNS(hostname, PGMacTipsConstants.ONE_SECOND);
	}
	
	/**
	 * Test that the DNS is successfully resolving a hostname. This is useful for detecting the
	 * "Unable to resolve host "{my.api.website}": No address associated with hostname" error
	 * @param hostname hostname to check, IE "{my.api.website}"
	 * @param customTimeoutCheckInMilliseconds Custom timeout for this check to occur. If this is
	 *                                         omitted, defaults to 1,000 milliseconds (1 second)
	 * @return true if it can resolve it, false if it cannot
	 */
	public static boolean testDNS(String hostname, long customTimeoutCheckInMilliseconds) {
		if (StringUtilities.isNullOrEmpty(hostname)) {
			return false;
		}
		try {
			DNSResolver dnsRes = new DNSResolver(hostname);
			Thread t = new Thread(dnsRes);
			t.start();
			t.join((customTimeoutCheckInMilliseconds > 0)
					? customTimeoutCheckInMilliseconds : PGMacTipsConstants.ONE_SECOND);
			InetAddress inetAddr = dnsRes.get();
			return inetAddr != null;
		} catch(Exception e) {
			return false;
		}
	}
	
	/**
	 * DNS Resolver. Code pulled from:
	 * https://stackoverflow.com/questions/18217335/can-i-set-the-getaddrinfo-timeout-in-android-for-defaulthttpclient
	 */
	private static class DNSResolver implements Runnable {
		private String domain;
		private InetAddress inetAddr;
		public DNSResolver(String domain) {
			this.domain = domain;
		}
		
		public void run() {
			try {
				InetAddress addr = InetAddress.getByName(domain);
				set(addr);
			} catch (UnknownHostException e) {}
		}
		
		public synchronized void set(InetAddress inetAddr) {
			this.inetAddr = inetAddr;
		}
		public synchronized InetAddress get() {
			return inetAddr;
		}
	}
	
	//endregion
	
}
