package com.pgmacdesign.pgmactips.networkclasses.retrofitutilities

import android.content.Context
import android.os.Build
import androidx.annotation.RawRes
import androidx.annotation.RequiresApi
import okhttp3.OkHttpClient
import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.InputStream
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.security.KeyFactory
import java.security.KeyManagementException
import java.security.KeyStore
import java.security.KeyStoreException
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import java.security.UnrecoverableKeyException
import java.security.cert.CertificateException
import java.security.cert.CertificateFactory
import java.security.spec.InvalidKeySpecException
import java.security.spec.PKCS8EncodedKeySpec
import javax.net.ssl.KeyManagerFactory
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager

/**
 * This class houses some of the additional retrofit utilities needed to augment things like
 * mTLS / Certificates
 */
class RetrofitClientUtils {

	companion object {

		/**
		 * Configure an existing [OkHttpClient.Builder] to add in certificates for TLS / MTLS support
		 * @param okhttpBuilder The builder to augment
		 * @param privateKeyContent The content of the Private Key in String format
		 * @param privateCertContent The content of the Private Cert in String format
		 * @param secretString Secret String to use in conjunction with the keys. If null, it will just use "secret"
		 * @return If successful, an augmented OkHttpClient.Builder, otherwise, the original passed builder.
		 */
		@RequiresApi(Build.VERSION_CODES.KITKAT)
		public fun configureCerts(
			okhttpBuilder: OkHttpClient.Builder,
			privateKeyContent: String, privateCertContent: String,
			secretString: String?
		): OkHttpClient.Builder {
			var privateKeyContent = privateKeyContent
			return try {
				val certificateFactory = CertificateFactory.getInstance("X.509")

				// Get private key
				if(privateKeyContent.contains("-----BEGIN PRIVATE KEY-----") ||
					privateKeyContent.contains("-----BEGIN PRIVATE KEY-----")){
					privateKeyContent = privateKeyContent
						.replace("-----BEGIN PRIVATE KEY-----", "")
						.replace(System.lineSeparator().toRegex(), "")
						.replace("-----END PRIVATE KEY-----", "")
				}
				val rawPrivateKeyByteArray = android.util.Base64.decode(privateKeyContent, android.util.Base64.DEFAULT)
				val keyFactory = KeyFactory.getInstance("RSA")
				val keySpec = PKCS8EncodedKeySpec(rawPrivateKeyByteArray)

				// Get certificate
				var bArray: ByteArray = privateCertContent.toByteArray(StandardCharsets.UTF_8)
				val certificateInputStream = ByteArrayInputStream(bArray)
				val certificate = certificateFactory.generateCertificate(certificateInputStream)

				// Set up KeyStore
				val keyStore = KeyStore.getInstance(KeyStore.getDefaultType())
				val cc : CharArray = if(secretString.isNullOrEmpty()) "secret".toCharArray() else secretString.toCharArray()
				keyStore.load(null, cc)
				keyStore.setKeyEntry(
					"client",
					keyFactory.generatePrivate(keySpec),
					cc,
					arrayOf(certificate)
				)
				certificateInputStream.close()

				// Set up Trust Managers
				val trustManagerFactory =
					TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
				trustManagerFactory.init(null as KeyStore?)
				val trustManagers = trustManagerFactory.trustManagers

				// Set up Key Managers
				val keyManagerFactory =
					KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm())
				keyManagerFactory.init(keyStore, cc)
				val keyManagers = keyManagerFactory.keyManagers

				// Obtain SSL Socket Factory
				val sslContext = SSLContext.getInstance("TLS")
				sslContext.init(keyManagers, trustManagers, SecureRandom())
				val sslSocketFactory = sslContext.socketFactory

				// Finally, return the client, which will then be used to make HTTP calls.
				okhttpBuilder.sslSocketFactory(
					sslSocketFactory, trustManagers[0] as X509TrustManager
				)
			} catch (e: CertificateException) {
				e.printStackTrace()
				okhttpBuilder
			} catch (e: java.lang.IllegalArgumentException) {
				e.printStackTrace()
				okhttpBuilder
			} catch (e: IOException) {
				e.printStackTrace()
				okhttpBuilder
			} catch (e: NoSuchAlgorithmException) {
				e.printStackTrace()
				okhttpBuilder
			} catch (e: KeyStoreException) {
				e.printStackTrace()
				okhttpBuilder
			} catch (e: UnrecoverableKeyException) {
				e.printStackTrace()
				okhttpBuilder
			} catch (e: KeyManagementException) {
				e.printStackTrace()
				okhttpBuilder
			} catch (e: InvalidKeySpecException) {
				e.printStackTrace()
				okhttpBuilder
			}
		}

		/**
		 * Configure an existing [OkHttpClient.Builder] to add in certificates for TLS / MTLS support
		 * @param context Context (to pull from raw directory)
		 * @param okhttpBuilder The builder to augment
		 * @param privateKeyResource The raw resource file of the Private Key
		 * @param privateCertResource The raw resource file of the Private Cert
		 * @param secretString Secret String to use in conjunction with the keys. If null, it will just use "secret"
		 * @return If successful, an augmented OkHttpClient.Builder, otherwise, the original passed builder.
		 */
		@RequiresApi(Build.VERSION_CODES.KITKAT)
		public fun configureCertsRawFile(
			context: Context, okhttpBuilder: OkHttpClient.Builder,
			@RawRes privateKeyResource: Int, @RawRes privateCertResource: Int,
			secretString: String?
		): OkHttpClient.Builder {
			var privateKeyResource = privateKeyResource
			return try {
				val certificateFactory = CertificateFactory.getInstance("X.509")

				// Get private key
				val privateKeyInputStream = context.resources.openRawResource(privateKeyResource)
				val privateKeyByteArray = ByteArray(privateKeyInputStream.available())
				privateKeyInputStream.read(privateKeyByteArray)
				var privateKeyContent = String(privateKeyByteArray, Charset.defaultCharset())
					.replace("-----BEGIN PRIVATE KEY-----", "")
					.replace(System.lineSeparator().toRegex(), "")
					.replace("-----END PRIVATE KEY-----", "")
				val rawPrivateKeyByteArray = android.util.Base64.decode(privateKeyContent, android.util.Base64.DEFAULT)
				val keyFactory = KeyFactory.getInstance("RSA")
				val keySpec = PKCS8EncodedKeySpec(rawPrivateKeyByteArray)

				// Get certificate
				val certificateInputStream: InputStream = context.resources.openRawResource(privateCertResource)
				val certificate = certificateFactory.generateCertificate(certificateInputStream)

				// Set up KeyStore
				val cc : CharArray = if(secretString.isNullOrEmpty()) "secret".toCharArray() else secretString.toCharArray()
				val keyStore = KeyStore.getInstance(KeyStore.getDefaultType())
				keyStore.load(null, cc)
				keyStore.setKeyEntry(
					"client",
					keyFactory.generatePrivate(keySpec),
					cc,
					arrayOf(certificate)
				)
				certificateInputStream.close()

				// Set up Trust Managers
				val trustManagerFactory =
					TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
				trustManagerFactory.init(null as KeyStore?)
				val trustManagers = trustManagerFactory.trustManagers

				// Set up Key Managers
				val keyManagerFactory =
					KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm())
				keyManagerFactory.init(keyStore, cc)
				val keyManagers = keyManagerFactory.keyManagers

				// Obtain SSL Socket Factory
				val sslContext = SSLContext.getInstance("TLS")
				sslContext.init(keyManagers, trustManagers, SecureRandom())
				val sslSocketFactory = sslContext.socketFactory

				// Finally, return the client, which will then be used to make HTTP calls.
				okhttpBuilder.sslSocketFactory(
					sslSocketFactory, trustManagers[0] as X509TrustManager
				)
			} catch (e: CertificateException) {
				e.printStackTrace()
				okhttpBuilder
			} catch (e: java.lang.IllegalArgumentException) {
				e.printStackTrace()
				okhttpBuilder
			} catch (e: IOException) {
				e.printStackTrace()
				okhttpBuilder
			} catch (e: NoSuchAlgorithmException) {
				e.printStackTrace()
				okhttpBuilder
			} catch (e: KeyStoreException) {
				e.printStackTrace()
				okhttpBuilder
			} catch (e: UnrecoverableKeyException) {
				e.printStackTrace()
				okhttpBuilder
			} catch (e: KeyManagementException) {
				e.printStackTrace()
				okhttpBuilder
			} catch (e: InvalidKeySpecException) {
				e.printStackTrace()
				okhttpBuilder
			}
		}
	}


}