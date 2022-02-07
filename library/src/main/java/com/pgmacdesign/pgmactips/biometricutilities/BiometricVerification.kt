package com.pgmacdesign.pgmactips.biometricutilities

import android.Manifest
import android.app.KeyguardManager
import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.hardware.biometrics.BiometricManager
import android.hardware.biometrics.BiometricPrompt
import android.hardware.fingerprint.FingerprintManager
import android.os.Build
import android.os.CancellationSignal
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyPermanentlyInvalidatedException
import android.security.keystore.KeyProperties
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import androidx.core.app.ActivityCompat
import com.pgmacdesign.pgmactips.adaptersandlisteners.OnTaskCompleteListener
import com.pgmacdesign.pgmactips.utilities.StringUtilities
import com.pgmacdesign.pgmactips.utilities.SystemUtilities
import java.io.IOException
import java.lang.Exception
import java.lang.IllegalStateException
import java.lang.NullPointerException
import java.security.*
import java.security.cert.CertificateException
import java.util.*
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.NoSuchPaddingException
import javax.crypto.SecretKey

@RequiresApi(api = Build.VERSION_CODES.M)
class BiometricVerification {

	//region Constructor


	//endregion

	//region Sample

	/*
    //Sample Usage after you have requested permission via the manifest
    if(Build.VERSION.SDK_INT >= 23) {
                //Initialize here
                BiometricVerification biometricVerification = new BiometricVerification(
                        new OnTaskCompleteListener() {
                            @Override
                            public void onTaskComplete(Object result, int customTag) {
                                //Switch Statement to handle one of the five possible responses
                                switch (customTag){
                                    case BiometricVerificationV2.TAG_AUTHENTICATION_FAIL:
                                        //Authentication failed / finger does not match
                                        boolean fail = (boolean) result;
                                        break;

                                    case BiometricVerificationV2.TAG_AUTHENTICATION_SUCCESS:
                                        //Authentication success / finger matches.
                                        //(NOTE! Stops fingerprint listener when this triggers)
                                        boolean success = (boolean) result;
                                        break;

                                    case BiometricVerificationV2.TAG_AUTHENTICATION_ERROR:
                                        //Error (IE called stopFingerprintAuth() or onStop() triggered)
                                        String knownAuthenticationError = (String) result;
                                        break;

                                    case BiometricVerificationV2.TAG_AUTHENTICATION_HELP:
                                        //Authentication did not work, help string passed
                                        String helpString = (String) result;
                                        break;

                                    case BiometricVerificationV2.TAG_GENERIC_ERROR:
                                        //Some unknown error has occurred
                                        String genericErrorString = (String) result;
                                        break;
                                }
                            }
                        }, mContext, "my_key_name");

                //Start auth here
                try {
                    if(BiometricVerificationV2.isCriteriaMet()){
                        this.BiometricVerificationV2.startFingerprintAuth();
                    }
                } catch (BiometricException e){
                    e.printStackTrace();
                }
    }
     */

	//endregion

	//region Sample
	/*
    //Sample Usage after you have requested permission via the manifest
    if(Build.VERSION.SDK_INT >= 23) {
                //Initialize here
                BiometricVerification biometricVerification = new BiometricVerification(
                        new OnTaskCompleteListener() {
                            @Override
                            public void onTaskComplete(Object result, int customTag) {
                                //Switch Statement to handle one of the five possible responses
                                switch (customTag){
                                    case BiometricVerificationV2.TAG_AUTHENTICATION_FAIL:
                                        //Authentication failed / finger does not match
                                        boolean fail = (boolean) result;
                                        break;

                                    case BiometricVerificationV2.TAG_AUTHENTICATION_SUCCESS:
                                        //Authentication success / finger matches.
                                        //(NOTE! Stops fingerprint listener when this triggers)
                                        boolean success = (boolean) result;
                                        break;

                                    case BiometricVerificationV2.TAG_AUTHENTICATION_ERROR:
                                        //Error (IE called stopFingerprintAuth() or onStop() triggered)
                                        String knownAuthenticationError = (String) result;
                                        break;

                                    case BiometricVerificationV2.TAG_AUTHENTICATION_HELP:
                                        //Authentication did not work, help string passed
                                        String helpString = (String) result;
                                        break;

                                    case BiometricVerificationV2.TAG_GENERIC_ERROR:
                                        //Some unknown error has occurred
                                        String genericErrorString = (String) result;
                                        break;
                                }
                            }
                        }, mContext, "my_key_name");

                //Start auth here
                try {
                    if(BiometricVerificationV2.isCriteriaMet()){
                        this.BiometricVerificationV2.startFingerprintAuth();
                    }
                } catch (BiometricException e){
                    e.printStackTrace();
                }
    }
     */
	//endregion
	//region Static Vars

	companion object{

		const val ANDROID_KEYSTORE = "AndroidKeyStore"
		const val FINGERPRINT_SUFFIX_STRING = ".fingerprint"
		const val ERROR_MISSING_PERMISSION =
			"Missing required permission [android.permission.USE_FINGERPRINT] or [android.permission.USE_BIOMETRIC]."
		const val MUST_CALL_START_BEFORE_STOP =
			"You must call startFingerprintAuth() before you can call stopFingerprintAuth()"
		const val UNKNOWN_ERROR = "An unknown error has occurred. Please try again"
		const val HARDWARE_UNAVAILABLE = "Fingerprint sensor hardware not available on this device."
		const val NO_STORED_FINGERPRINTS =
			"User does not have any enrolled fingerprints; must have at least one stored to use this method."
		const val LOCK_SCREEN_NOT_ENABLED =
			"User does not have a lock screen enabled. A lock screen is required before this feature can be used."

		//Used in API 28+
		const  val NO_STORED_FINGERPRINTS_OR_FACES =
			"User does not have any enrolled fingerprints or faces; must have at least one stored to use this method."
		const  val BIOMETRIC_HW_NOT_AVAILABLE = "Biometric Hardware is currently unavailable"
		const  val BIOMETRIC_SECURITY_UPDATE_REQUIRED =
			"A biometric security breach in older code has been detected and a user must run a security update (or wait for one to be pushed)"
		const  val BIOMETRIC_ERROR_NO_HARDWARE =
			"No biometric hardware is not installed on the device at all"
		const  val BIOMETRICS_READY_AND_AVAILABLE = "Biometrics are enabled and available for use"

		/**
		 * Will trigger upon making a call that requires fingerprint permission but does not have it.
		 * This can happen when a user uses the app, gives permission, and then goes into settings and
		 * removes the given permission.
		 */
		@Deprecated("Refactored into different method and will throw {@link BiometricException} instead.\n" + "      When using {@link OnTaskCompleteListener#onTaskComplete(Object, int)}, the object will always be passed as a String")
		const val TAG_MISSING_FINGERPRINT_PERMISSION = 9320

		/**
		 * Will trigger upon making an unknown error occurring.
		 * When using [OnTaskCompleteListener.onTaskComplete], the object will always be passed as a String
		 */
		const val TAG_GENERIC_ERROR = 9321

		/**
		 * Will trigger upon authentication success, IE, fingerprint / face matches those stored in phone.
		 * NOTE! When this is sent back, the system automatically calls [BiometricVerification.stopBiometricAuth]
		 * and will stop listening for fingerprint / face input. If you want to begin listening for input again, you will
		 * need to call [BiometricVerification.startBiometricAuth] ()} again.
		 * When using [OnTaskCompleteListener.onTaskComplete], the object will always be passed as a boolean
		 */
		const val TAG_AUTHENTICATION_SUCCESS = 9322

		/**
		 * Will trigger upon authentication fail, IE, fingerprint does not match any stored in phone.
		 * When using [OnTaskCompleteListener.onTaskComplete], the object will always be passed as a boolean
		 */
		const val TAG_AUTHENTICATION_FAIL = 9323

		/**
		 * Will trigger upon an error, IE, manual call to [FingerprintHandler.stopBiometricAuth] or
		 * when the app goes into the background unexpectedly.
		 * When using [OnTaskCompleteListener.onTaskComplete], the object will always be passed as a String
		 */
		const val TAG_AUTHENTICATION_ERROR = 9324

		/**
		 * Will trigger upon helpful hints, IE, if you move the finger too quickly, you will see this text:
		 * "Finger moved too fast. Please try again".
		 * When using [OnTaskCompleteListener.onTaskComplete], the object will always be passed as a String
		 */
		const  val TAG_AUTHENTICATION_HELP = 9325
		//API 28+
		//API 28+
		/**
		 * Hardware is currently unavailable
		 */
		const val TAG_BIOMETRIC_ERROR_HW_UNAVAILABLE = 9326

		/**
		 * A biometric security breach in older code has been detected and a user must run a
		 * security update (or wait for one to be pushed)
		 */
		const val TAG_BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED = 9327

		/**
		 * No biometrics enrolled (IE no fingerprints or face)
		 */
		const val TAG_BIOMETRIC_ERROR_NONE_ENROLLED = 9328

		/**
		 * No biometric hardware is not installed on the device at all
		 */
		const val TAG_BIOMETRIC_ERROR_NO_HARDWARE = 9329

		/**
		 * Biometrics are enabled and available for use
		 */
		const val TAG_BIOMETRICS_AVAILABLE_FOR_USE = 9330
	}

	//endregion

	//region Vars


	//endregion
	//region Vars
	//For FingerprintManager.AuthenticationCallback Extension:
	private var cancellationSignal: CancellationSignal? = null

	//Vars
	private var cryptoObject: FingerprintManager.CryptoObject? = null
	private var biometricCryptoObject: BiometricPrompt.CryptoObject? = null
	private var fingerprintManager: FingerprintManager? = null
	private var biometricManager: BiometricManager? = null
	private var biometricPrompt: BiometricPrompt? = null
	private var keyguardManager: KeyguardManager? = null
	private var cipher: Cipher? = null
	private var keyStore: KeyStore? = null
	private var keyGenerator: KeyGenerator? = null
	private var secretKey: SecretKey? = null

	//Standard Vars
	private var listener: OnTaskCompleteListener? = null
	private var context: Context? = null
	private var keyName: String? = null
	private var cipherInitialized = false
	private  var keystoreInitialized:kotlin.Boolean = false

	//endregion

	//region Constructor
	/**
	 * Fingerprint Verification Constructor
	 * @param listener [OnTaskCompleteListener] link to send back results
	 * @param context Context to be used in the class
	 * @param keyName String keyName desired to use. If null, will attempt to pull package name and
	 * use that as the name. If that fails, it will use random numbers plus the
	 * '.fingerprint' suffix String.
	 */
	constructor(
		listener: OnTaskCompleteListener,
		context: Context,
		keyName: String?
	) {
		this.keystoreInitialized = false
		cipherInitialized = this.keystoreInitialized
		this.context = context
		this.listener = listener
		if (!StringUtilities.isNullOrEmpty(keyName)) {
			this.keyName = keyName
		} else {
			val packageName = SystemUtilities.getPackageName(this.context)
			if (!StringUtilities.isNullOrEmpty(packageName)) {
				this.keyName = packageName + FINGERPRINT_SUFFIX_STRING
			} else {
				val random = Random().nextInt(8999) + 1000
				this.keyName = random.toString() + FINGERPRINT_SUFFIX_STRING
			}
		}
		if (keyguardManager == null) {
			keyguardManager = context
				.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
		}
		if (fingerprintManager == null) {
			fingerprintManager = context
				.getSystemService(Context.FINGERPRINT_SERVICE) as FingerprintManager
		}
		if (biometricManager == null) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
				biometricManager = context
					.getSystemService(Context.BIOMETRIC_SERVICE) as BiometricManager
			} else {
				biometricManager = null
			}
		}
	}

	//endregion

	//region Private Methods

	//endregion
	//region Private Methods
	/**
	 * Init method
	 * @throws BiometricException [BiometricException]
	 */
	@RequiresPermission(anyOf = [Manifest.permission.USE_FINGERPRINT, Manifest.permission.USE_BIOMETRIC])
	@Throws(
		BiometricException::class
	)
	private fun init() {
		try {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
				if (!doesHaveFingerprintPermission()) {
					throw BiometricException(ERROR_MISSING_PERMISSION)
				}
				if (!isFingerprintSensorAvailable()) {
					throw BiometricException(HARDWARE_UNAVAILABLE)
				}
				if (!doesUserHaveEnrolledFingerprints()) {
					throw BiometricException(NO_STORED_FINGERPRINTS)
				}
				if (!doesUserHaveLockEnabled()) {
					throw BiometricException(LOCK_SCREEN_NOT_ENABLED)
				}
				val res = canAuthenticate()
				when (res) {
					BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> throw BiometricException(
						BIOMETRIC_HW_NOT_AVAILABLE
					)
					BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED -> throw BiometricException(
						BIOMETRIC_SECURITY_UPDATE_REQUIRED
					)
					BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> throw BiometricException(
						NO_STORED_FINGERPRINTS_OR_FACES
					)
					BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> throw BiometricException(
						BIOMETRIC_ERROR_NO_HARDWARE
					)
				}
			} else {
				if (!doesHaveFingerprintPermission()) {
					throw BiometricException(ERROR_MISSING_PERMISSION)
				}
				if (!isFingerprintSensorAvailable()) {
					throw BiometricException(HARDWARE_UNAVAILABLE)
				}
				if (!doesUserHaveEnrolledFingerprints()) {
					throw BiometricException(NO_STORED_FINGERPRINTS)
				}
				if (!doesUserHaveLockEnabled()) {
					throw BiometricException(LOCK_SCREEN_NOT_ENABLED)
				}
			}

			//Finish the builders
			val didInitSuccessfully = initCipher()
			if (!didInitSuccessfully) {
				throw BiometricException(UNKNOWN_ERROR)
			}
			if (cryptoObject == null) {
				cryptoObject = FingerprintManager.CryptoObject(cipher!!)
			}
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
				biometricCryptoObject = BiometricPrompt.CryptoObject(
					cipher!!
				)
			} else {
				biometricCryptoObject = null
			}
		} catch (fe: BiometricException) {
			throw fe
		} catch (e: Exception) {
			e.printStackTrace()
			throw BiometricException(e)
		}
	}

	/**
	 * Generate the Key
	 * @return [SecretKey]
	 */
	private fun generateKey(): SecretKey? {
		try {
			if (secretKey != null) {
				return secretKey
			}

			// Obtain a reference to the Keystore using the standard Android keystore container identifier ("AndroidKeystore")//
			keyStore = KeyStore.getInstance(BiometricVerification.ANDROID_KEYSTORE)

			//Generate the key//
			keyGenerator = KeyGenerator.getInstance(
				KeyProperties.KEY_ALGORITHM_AES,
				BiometricVerification.ANDROID_KEYSTORE
			)

			//Initialize an empty KeyStore//
			keyStore!!.load(null)

			//Initialize the KeyGenerator//
			keyGenerator!!.init(
				KeyGenParameterSpec.Builder(
					keyName!!,
					KeyProperties.PURPOSE_ENCRYPT or
							KeyProperties.PURPOSE_DECRYPT
				)
					.setBlockModes(KeyProperties.BLOCK_MODE_CBC) //Configure this key so that the user has to confirm their identity with a fingerprint each time they want to use it//
					.setUserAuthenticationRequired(true)
					.setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
					.build()
			)

			//Generate the key//
			return keyGenerator!!.generateKey()
		} catch (exc: KeyStoreException) {
			exc.printStackTrace()
			return null
		} catch (exc: NoSuchAlgorithmException) {
			exc.printStackTrace()
			return null
		} catch (exc: NoSuchProviderException) {
			exc.printStackTrace()
			return null
		} catch (exc: IllegalStateException) {
			exc.printStackTrace()
			return null
		} catch (exc: InvalidAlgorithmParameterException) {
			exc.printStackTrace()
			return null
		} catch (exc: CertificateException) {
			exc.printStackTrace()
			return null
		} catch (exc: IOException) {
			exc.printStackTrace()
			return null
		}
	}

	/**
	 * Initialize the cipher
	 * @return true if it succeeded false if it did not
	 */
	private fun initCipher(): Boolean {
		try {
			//Obtain a cipher instance and configure it with the properties required for fingerprint authentication//
			if (cipher == null) {
				cipher = Cipher.getInstance(
					KeyProperties.KEY_ALGORITHM_AES + "/"
							+ KeyProperties.BLOCK_MODE_CBC + "/"
							+ KeyProperties.ENCRYPTION_PADDING_PKCS7
				)
			}
		} catch (e: NoSuchAlgorithmException) {
			e.printStackTrace()
			return false
		} catch (e: NoSuchPaddingException) {
			e.printStackTrace()
			return false
		}
		try {
			if (secretKey == null) {
				secretKey = generateKey()
			}
			if (keyStore != null) {
				if (!this.keystoreInitialized) {
					keyStore!!.load(null)
					this.keystoreInitialized = true
				}
			}
			//            key = (SecretKey) this.keyStore.getKey(keyName, null); todo needed?
			if (cipher != null) {
				if (!cipherInitialized) {
					cipher!!.init(Cipher.ENCRYPT_MODE, secretKey)
					cipherInitialized = true
				}
			}
			//Return true if the cipher has been initialized successfully//
			return true
		} catch (e: KeyPermanentlyInvalidatedException) {

			//Return false if cipher initialization failed//
			return false
		} catch (e: CertificateException) {
			e.printStackTrace()
			return false
		} catch (e: IOException) {
			e.printStackTrace()
			return false
		} catch (e: NullPointerException) {
			e.printStackTrace()
			return false
		} catch (e: NoSuchAlgorithmException) {
			e.printStackTrace()
			return false
		} catch (e: InvalidKeyException) {
			e.printStackTrace()
			return false
		}
	}

	//endregion

	//region Availability Checks


	//endregion
	//region Availability Checks
	/**
	 * Checks if the the fingerprint sensor hardware is available on the device
	 * @return boolean, if true sensor is available on the device, false if not
	 */
	@RequiresPermission(anyOf = [Manifest.permission.USE_FINGERPRINT, Manifest.permission.USE_BIOMETRIC])
	fun isFingerprintSensorAvailable(): Boolean {
		try {
			return fingerprintManager!!.isHardwareDetected
		} catch (e: Exception) {
			return false
		}
	}

	/**
	 * Checks if the the face unlock hardware is available on the device
	 * @return boolean, if true hardware / feature is available on the device, false if not
	 */
	@RequiresApi(api = Build.VERSION_CODES.Q)
	@RequiresPermission(anyOf = [Manifest.permission.USE_BIOMETRIC])
	fun isFaceSensorAvailable(): Boolean {
		try {
			return context!!.packageManager.hasSystemFeature(PackageManager.FEATURE_FACE)
		} catch (e: Exception) {
			return false
		}
	}

	/**
	 * Checks if the user has enrolled fingerprints in the phone itself
	 * @return boolean, true if they have, false if they have not
	 */
	@RequiresPermission(anyOf = [Manifest.permission.USE_FINGERPRINT, Manifest.permission.USE_BIOMETRIC])
	fun doesUserHaveEnrolledFingerprints(): Boolean {
		try {
			return fingerprintManager!!.hasEnrolledFingerprints()
		} catch (e: Exception) {
			return false
		}
	}

	/**
	 * Checks if the user has given permission to use Fingerprint Scanning
	 * @return boolean, true if they have, false if they have not
	 */
	fun doesHaveFingerprintPermission(): Boolean {
		try {
			val x =
				if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)) ActivityCompat.checkSelfPermission(
					(context)!!, Manifest.permission.USE_BIOMETRIC
				) else ActivityCompat.checkSelfPermission(
					(context)!!, Manifest.permission.USE_FINGERPRINT
				)
			return (x == PackageManager.PERMISSION_GRANTED)
		} catch (e: Exception) {
			return false
		}
	}

	/**
	 * Checks if the user has a phone lock available and enabled
	 * @return boolean, true if they have, false if they have not
	 */
	fun doesUserHaveLockEnabled(): Boolean {
		try {
			return (keyguardManager!!.isKeyguardSecure)
		} catch (e: Exception) {
			return false
		}
	}

	/**
	 * Simple method to combine all of of the checker methods into one so as to reduce code.
	 * @return boolean, will return true if all criteria has been met, false if not
	 */
	@RequiresPermission(anyOf = [Manifest.permission.USE_FINGERPRINT, Manifest.permission.USE_BIOMETRIC])
	fun isCriteriaMet(): Boolean {
		return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
			this.isCriteriaMet(null)
		} else {
			((doesHaveFingerprintPermission() &&
					doesUserHaveLockEnabled() &&
					doesUserHaveEnrolledFingerprints() &&
					isFingerprintSensorAvailable()))
		}
	}

	/**
	 * Simple method to combine all of of the checker methods into one so as to reduce code.
	 * @param specificProblemListener Callback listener to send back the exact item that caused it
	 * to return false should not all criteria be met.
	 * @return boolean, will return true if all criteria has been met, false if not
	 */
	@RequiresApi(api = Build.VERSION_CODES.Q)
	@RequiresPermission(anyOf = [Manifest.permission.USE_BIOMETRIC])
	fun isCriteriaMet(specificProblemListener: OnTaskCompleteListener?): Boolean {
		val res = canAuthenticate()
		if (specificProblemListener != null) {
			when (res) {
				BiometricManager.BIOMETRIC_SUCCESS -> specificProblemListener.onTaskComplete(
					BIOMETRICS_READY_AND_AVAILABLE,
					TAG_BIOMETRICS_AVAILABLE_FOR_USE
				)
				BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> specificProblemListener.onTaskComplete(
					BIOMETRIC_HW_NOT_AVAILABLE,
					TAG_BIOMETRIC_ERROR_HW_UNAVAILABLE
				)
				BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED -> specificProblemListener.onTaskComplete(
					BIOMETRIC_SECURITY_UPDATE_REQUIRED,
					TAG_BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED
				)
				BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> specificProblemListener.onTaskComplete(
					NO_STORED_FINGERPRINTS_OR_FACES,
					TAG_BIOMETRIC_ERROR_NONE_ENROLLED
				)
				BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> specificProblemListener.onTaskComplete(
					BIOMETRIC_ERROR_NO_HARDWARE,
					TAG_BIOMETRIC_ERROR_NO_HARDWARE
				)
			}
		}
		return (res == BiometricManager.BIOMETRIC_SUCCESS)
	}

	@RequiresApi(value = Build.VERSION_CODES.Q)
	@RequiresPermission(value = "android.permission.USE_BIOMETRIC")
	private fun canAuthenticate(): Int {
		return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
			biometricManager!!.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK)
		} else {
			biometricManager!!.canAuthenticate()
		}
	}


	//endregion

	//region Public Auth

	//endregion
	//region Public Auth
	/**
	 * Begins the Authentication session and starts listening for the finger to hit the sensor or
	 * the face to be viewed in the front-facing camera (if applicable).
	 * This is used in direct conjunction with the Google Fingerprint Authentication API
	 * [FingerprintManager] as well as the Biometric API [BiometricManager] to
	 * check against stored fingerprints / faces and ping back along the
	 * [OnTaskCompleteListener] when finished.
	 * This class assumes the following pre-requisites have been checked against:
	 * 1) The device is running Android 6.0 or higher.
	 * [android.os.Build.VERSION_CODES.M]
	 * 2) The device features a fingerprint sensor.
	 * [BiometricVerification.isFingerprintSensorAvailable]
	 * 3) The user has granted your app permission to access the fingerprint sensor.
	 * [BiometricVerification.doesHaveFingerprintPermission] && Permission in the manifest
	 * 4) The user has protected their lockscreen
	 * [BiometricVerification.doesUserHaveLockEnabled]
	 * 5) The user has registered at least one fingerprint on their device.
	 * [BiometricVerification.doesUserHaveEnrolledFingerprints]
	 * If any of these criteria are not met, a BiometricException will be thrown.
	 * @throws BiometricException [BiometricException]
	 */
	@RequiresPermission(anyOf = [Manifest.permission.USE_FINGERPRINT, Manifest.permission.USE_BIOMETRIC])
	@Throws(
		BiometricException::class
	)
	fun startBiometricAuth() {
		init()
		try {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
				this.startBiometricAuth(null, null, null, null)
			} else {
				if (cancellationSignal == null) {
					cancellationSignal = CancellationSignal()
				}
				if (cancellationSignal!!.isCanceled) {
					cancellationSignal = CancellationSignal()
				}
				fingerprintManager!!.authenticate(
					cryptoObject, cancellationSignal,
					0, object : FingerprintManager.AuthenticationCallback() {
						override fun onAuthenticationError(errMsgId: Int, errString: CharSequence) {
							//Authentication error
							listener?.onTaskComplete(errString, TAG_AUTHENTICATION_ERROR)
						}

						override fun onAuthenticationFailed() {
							//Authentication failed (Fingerprints don't match ones on device)
							listener?.onTaskComplete(false, TAG_AUTHENTICATION_FAIL)
						}

						override fun onAuthenticationHelp(helpMsgId: Int, helpString: CharSequence) {
							//Non-Fatal error (IE moved finger too quickly)
							listener?.onTaskComplete(
								helpString?.toString() ?: UNKNOWN_ERROR,
								BiometricVerification.TAG_AUTHENTICATION_HELP
							)
						}

						override fun onAuthenticationSucceeded(result: FingerprintManager.AuthenticationResult) {
							//Authentication Succeeded
							listener?.onTaskComplete(true, BiometricVerification.TAG_AUTHENTICATION_SUCCESS)
						}
					}, null
				)
			}
		} catch (e: Exception) {
			e.printStackTrace()
			listener!!.onTaskComplete(e.message, TAG_GENERIC_ERROR)
		}
	}


	/**
	 * Begins the Authentication session and starts listening for the finger to hit the sensor or
	 * the face to be viewed in the front-facing camera (if applicable).
	 * This is used in direct conjunction with the Google Fingerprint Authentication API
	 * [FingerprintManager] as well as the Biometric API [BiometricManager] to
	 * check against stored fingerprints / faces and ping back along the
	 * [OnTaskCompleteListener] when finished.
	 * This class assumes the following pre-requisites have been checked against:
	 * 1) The device is running Android 6.0 or higher.
	 * [android.os.Build.VERSION_CODES.M]
	 * 2) The device features a fingerprint sensor.
	 * [BiometricVerification.isFingerprintSensorAvailable]
	 * 3) The user has granted your app permission to access the fingerprint sensor.
	 * [BiometricVerification.doesHaveFingerprintPermission] && Permission in the manifest
	 * 4) The user has protected their lockscreen
	 * [BiometricVerification.doesUserHaveLockEnabled]
	 * 5) The user has registered at least one fingerprint on their device.
	 * [BiometricVerification.doesUserHaveEnrolledFingerprints]
	 * If any of these criteria are not met, a BiometricException will be thrown.
	 * In addition, this overloaded method pops up a [BiometricPrompt] overlay so that a
	 * user can verify by either face / camera or fingerprint depending on what they have in place.
	 * @param title Title of the Biometric Prompt
	 * @param description Description of the Biometric Prompt
	 * @param subtitle Smaller Subtitle text below the title of the Biometric Prompt
	 * @param cancelText The cancellation text (IE cancel, stop, dismiss) of the Biometric Prompt
	 * @throws BiometricException [BiometricException]
	 */
	@RequiresApi(value = Build.VERSION_CODES.Q)
	@RequiresPermission(anyOf = [Manifest.permission.USE_BIOMETRIC])
	@Throws(
		BiometricException::class
	)
	fun startBiometricAuth(
		title: String?, description: String?,
		subtitle: String?, cancelText: String?
	) {
		init()
		if (cancellationSignal == null) {
			cancellationSignal = CancellationSignal()
		}
		if (cancellationSignal!!.isCanceled) {
			cancellationSignal = CancellationSignal()
		}
		var builder = BiometricPrompt.Builder(context)
		if (title != null) {
			builder = builder.setTitle(title)
		}
		if (description != null) {
			builder = builder.setDescription(description)
		}
		if (subtitle != null) {
			builder = builder.setSubtitle(subtitle)
		}
		if (description != null) {
			builder = builder.setDescription(description)
		}
		builder = builder.setDeviceCredentialAllowed(false)
		builder = builder.setNegativeButton(
			cancelText ?: "Cancel",
			context!!.mainExecutor,
			DialogInterface.OnClickListener { dialog: DialogInterface?, which: Int ->
				//The case does not matter here as it can only represent 'cancel'
				listener!!.onTaskComplete(false, BiometricVerification.TAG_AUTHENTICATION_FAIL)
			})
		biometricPrompt = builder.build()
		val c: BiometricPrompt.AuthenticationCallback =
			object : BiometricPrompt.AuthenticationCallback() {
				override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
					//Authentication error
					listener!!.onTaskComplete(
						errString,
						BiometricVerification.TAG_AUTHENTICATION_ERROR
					)
				}

				override fun onAuthenticationHelp(helpCode: Int, helpString: CharSequence) {
					//Non-Fatal error (IE moved finger too quickly)
					listener!!.onTaskComplete(
						helpString?.toString() ?: UNKNOWN_ERROR,
						BiometricVerification.TAG_AUTHENTICATION_HELP
					)
				}

				override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
					//Authentication Succeeded
					listener!!.onTaskComplete(
						true,
						BiometricVerification.TAG_AUTHENTICATION_SUCCESS
					)
				}

				override fun onAuthenticationFailed() {
					//Authentication failed (Fingerprints don't match ones on device)
					listener!!.onTaskComplete(false, BiometricVerification.TAG_AUTHENTICATION_FAIL)
				}
			}
		if (biometricCryptoObject != null) {
			biometricPrompt!!.authenticate(
				biometricCryptoObject!!, cancellationSignal!!,
				context!!.mainExecutor, c
			)
		} else {
			biometricPrompt!!.authenticate(
				cancellationSignal!!,
				context!!.mainExecutor, c
			)
		}
	}

	/**
	 * Stops all active Auth. Call this if onStop is suddenly called in your app or if you want
	 * to manually dismiss any open Biometric / fingerprint auth dialogs
	 */
	fun stopBiometricAuth() {
		try {
			if (cancellationSignal != null) {
				cancellationSignal!!.cancel()
			} else {
				listener!!.onTaskComplete(
					BiometricVerification.MUST_CALL_START_BEFORE_STOP,
					TAG_GENERIC_ERROR
				)
			}
		} catch (e: Exception) {
			e.printStackTrace()
		}
	}

	//endregion

}