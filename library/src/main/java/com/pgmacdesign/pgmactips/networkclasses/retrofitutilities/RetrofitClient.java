package com.pgmacdesign.pgmactips.networkclasses.retrofitutilities;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import android.util.Log;

import com.pgmacdesign.pgmactips.adaptersandlisteners.OnTaskCompleteListener;
import com.pgmacdesign.pgmactips.misc.CustomAnnotationsBase;
import com.pgmacdesign.pgmactips.misc.PGMacTipsConfig;
import com.pgmacdesign.pgmactips.misc.PGMacTipsConstants;
import com.pgmacdesign.pgmactips.networkclasses.sslsocketsandprotocols.SSLProtocolOptions;
import com.pgmacdesign.pgmactips.networkclasses.sslsocketsandprotocols.TLSSocketFactory;
import com.pgmacdesign.pgmactips.utilities.L;
import com.pgmacdesign.pgmactips.utilities.MiscUtilities;
import com.pgmacdesign.pgmactips.utilities.NumberUtilities;
import com.pgmacdesign.pgmactips.utilities.StringUtilities;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnull;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import androidx.annotation.Nullable;

import okhttp3.ConnectionSpec;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.TlsVersion;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.CallAdapter;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * Retrofit Client that is used for Retrofit calls. The builder method allows for more customized
 * uses and cases rather than using the regular {@link Retrofit}
 * todo: Look into retry if not functioning properly: https://medium.com/shuttl/easily-retrying-network-requests-on-android-with-retrofit-2-ee4b4b379eb7
 * Created by pmacdowell on 8/25/2016.
 */
@CustomAnnotationsBase.RequiresDependency(requiresDependencies = {CustomAnnotationsBase.Dependencies.Retrofit2,
        CustomAnnotationsBase.Dependencies.Retrofit2GSONConverter, CustomAnnotationsBase.Dependencies.OkHttp3,
        CustomAnnotationsBase.Dependencies.GSON, CustomAnnotationsBase.Dependencies.OkHttp3LoggingInterceptor,
        CustomAnnotationsBase.Dependencies.Okio})
public class RetrofitClient {

    //region Local Strings
    private static final String APPLICATION_JSON = "application/json";
    private static final String MULTIPART_FORM_DATA = "multipart/form-data";
    private static final String CONTENT_TYPE = "Content-Type";
    
    //endregion
    
    //region Public Strings

    public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    public static final String DEFAULT_DATE_FORMAT_WITHOUT_MILLISECONDS = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    
    //endregion
    
    //region Public Tags
    /**
     * Sent back along the {@link OnTaskCompleteListener} with a HashSet<String> of cookies received
     */
    public static final int TAG_COOKIES_HASHSET_RESPONSE = PGMacTipsConstants.TAG_COOKIES_HASHSET_RESPONSE;
    //endregion
    
    private String urlBase;
    private Map<String, String> headers;
    private HttpLoggingInterceptor.Level logLevel;
    private int retryCount;
    private int[] retryOnStatusCodes;
    private long readTimeout, writeTimeout;
    private ConnectionSpec connectionSpec;
    private String dateFormat;
    private String[] customCookieHeaderStrings;
    private Class serviceInterface;
    private Converter.Factory customConverterFactory;
    private CallAdapter.Factory customCallAdapterFactory;
    private SSLProtocolOptions sslProtocolOption;
    private boolean forceAcceptAllCertificates, shouldSaveResponseCookies;
    private OnTaskCompleteListener cookieResponseListener;

    /**
     * Constructor
     */
    private RetrofitClient(RetrofitClient.Builder builder) {
        this.urlBase = builder.builder_urlBase;
        this.headers = builder.builder_headers;
        this.logLevel = builder.builder_logLevel;
        this.retryCount = builder.builder_retryCount;
        this.dateFormat = builder.builder_dateFormat;
        this.readTimeout = builder.builder_readTimeout;
        this.writeTimeout = builder.builder_writeTimeout;
	    this.connectionSpec = builder.builder_connectionSpec;
	    this.sslProtocolOption = builder.builder_sslProtocolOption;
	    this.serviceInterface = builder.builder_serviceInterface;
	    this.retryOnStatusCodes = builder.builder_retryOnStatusCodes;
	    this.customConverterFactory = builder.builder_customConverterFactory;
	    this.cookieResponseListener = builder.builder_cookieResponseListener;
	    this.customCallAdapterFactory = builder.builder_customCallAdapterFactory;
	    this.customCookieHeaderStrings = builder.builder_customCookieHeaderString;
        this.shouldSaveResponseCookies = builder.builder_shouldSaveResponseCookies;
        this.forceAcceptAllCertificates = builder.builder_forceAcceptAllCertificates;
    }

    /**
     * Build a Workable service client Client
     * This should be used after the class is initialized and the setters are all set
     * @param <T> Service Interface class
     * @return Service client for making calls. Will be directly linked to the Interface passed in
     *         as well as to its calls.
     * @throws IllegalArgumentException If the BASE_URL String does not end in a forward slash
     *                                  (/), this will throw an illegal argument exception.
     */
    @SuppressWarnings("unchecked")
    @CustomAnnotationsBase.RequiresDependency(requiresDependencies = {CustomAnnotationsBase.Dependencies.Retrofit2,
            CustomAnnotationsBase.Dependencies.Retrofit2GSONConverter, CustomAnnotationsBase.Dependencies.OkHttp3,
            CustomAnnotationsBase.Dependencies.GSON, CustomAnnotationsBase.Dependencies.OkHttp3LoggingInterceptor,
            CustomAnnotationsBase.Dependencies.Okio})
    public <T> T buildServiceClient() throws IllegalArgumentException{
        T t = this.buildRetrofitClient();
        return t;
    }

    /**
     * Build headers from the headers map.
     * {@link Headers}
     * @return
     */
    private Headers buildHeaders(){
        return buildHeaders(null);
    }
    
    /**
     * Build headers from the headers map + the original headers.
     * Note that the custom {@link RetrofitClient#headers} set will overwrite the original ones
     * {@link Headers}
     * @return
     */
    private Headers buildHeaders(@Nullable Request request){
        Headers.Builder builder = new Headers.Builder();
        Headers originalHeaders = null;
        Set<String> originalHeaderKeys = null;
        if(request != null){
            originalHeaders = request.headers();
            originalHeaderKeys = originalHeaders.names();
        }
        if(MiscUtilities.isMapNullOrEmpty(headers) && MiscUtilities.isSetNullOrEmpty(originalHeaderKeys)){
            return builder.build();
        } else {
            if(originalHeaders != null){
                for (String key : originalHeaderKeys) {
                    if(StringUtilities.isNullOrEmpty(key)){
                        continue;
                    }
                    String value = originalHeaders.get(key);
                    if(!StringUtilities.isNullOrEmpty(value)){
                        builder.add(key, value);
                    }
                }
            }
            if(!MiscUtilities.isMapNullOrEmpty(headers)) {
                for (Map.Entry<String, String> myMap : headers.entrySet()) {
                    String key = myMap.getKey();
                    String value = myMap.getValue();
                    if (!StringUtilities.isNullOrEmpty(key) &&
                            !StringUtilities.isNullOrEmpty(value)) {
                        builder.add(key, value);
                    }
                }
            }
            return builder.build();
        }
    }

    /**
     * This builds a client that will be used for network calls
     */
    @SuppressWarnings("unchecked")
    private  <T> T buildRetrofitClient(){
        //First create the interceptor, which will be used in the Retrofit call
        Interceptor interceptor = new Interceptor() {
            @Override
            public Response intercept(Interceptor.Chain chain) throws IOException {
                //First Set the headers
                Request originalRequest = chain.request();
                Request.Builder builder = chain.request().newBuilder();
                if(headers != null){
                    if(headers.size() > 0){
                        Headers headers = buildHeaders(originalRequest);
                        builder.headers(headers);
                    }
                }
                //Next make the outbound call
//                Response originalResponse = null;
//                try {
//                    originalResponse = chain.proceed(chain.request());
//                } catch (IOException ioe){
//                    ioe.printStackTrace();
//                }
//                Response originalResponse = chain.proceed(chain.request());
                Response originalResponse = chain.proceed(builder.build());
                if(originalResponse != null) {
                    if(shouldSaveResponseCookies && cookieResponseListener != null) {
                        HashSet<String> cookies = new HashSet<>();
                        if (!MiscUtilities.isListNullOrEmpty(originalResponse.headers(PGMacTipsConstants.COOKIE_1))) {
                            try {
                                cookies.addAll(originalResponse.headers(PGMacTipsConstants.COOKIE_1));
                            } catch (Exception e) {
                                //Catching in the event of custom cookie responses
                            }
                        }
                        if (!MiscUtilities.isListNullOrEmpty(originalResponse.headers(PGMacTipsConstants.COOKIE_2))) {
                            try {
                                cookies.addAll(originalResponse.headers(PGMacTipsConstants.COOKIE_2));
                            } catch (Exception e) {
                                //Catching in the event of custom cookie responses
                            }
                        }
                        if(!MiscUtilities.isArrayNullOrEmpty(customCookieHeaderStrings)){
                        	for(String str : customCookieHeaderStrings){
                        		if(StringUtilities.isNullOrEmpty(str)){
                        			continue;
		                        }
		                        if (!MiscUtilities.isListNullOrEmpty(originalResponse.headers(str))) {
			                        try {
				                        cookies.addAll(originalResponse.headers(str));
			                        } catch (Exception e) {
				                        //Catching in the event of custom cookie responses
			                        }
		                        }
	                        }

                        }
                        cookieResponseListener.onTaskComplete(cookies, RetrofitClient.TAG_COOKIES_HASHSET_RESPONSE);
                    }
                    
                    if(false){
	                    // TODO: 4/14/20 Removing for now to experiment with RetryCallAdapterFactory
	                    if(retryCount > 0){
		                    if(!originalResponse.isSuccessful()) {
			                    boolean shouldRetry;
			                    int localRetryCount = retryCount;
			                    int retryNumber = 0;
			                    int responseCode = originalResponse.code();
			                    if (retryOnStatusCodes == null) {
				                    shouldRetry = (responseCode != 200);
			                    } else {
				                    shouldRetry = (NumberUtilities.doesArrayContain(retryOnStatusCodes, responseCode));
			                    }
			                    if(shouldRetry) {
				                    Response myNewResponse = null;
				                    while (localRetryCount > 0) {
					                    //I am aware keeping 2 is wasteful, but I have an idea for future reference, so keeping both for now
					                    localRetryCount--;
					                    retryNumber++;
					                    try {
						                    if (!PGMacTipsConfig.getInstance().getIsLiveBuild()){
							                    L.m("Call Failed, retry number " + retryNumber);
						                    }
					                    } catch (Exception e){
						                    //Catching in case user manually blocks PGMacTipsConfig on setup
					                    }
					                    myNewResponse = chain.proceed(chain.request());
					                    if (myNewResponse != null) {
						                    if (!myNewResponse.isSuccessful()){
							                    int responseCodeX = myNewResponse.code();
							                    if (retryOnStatusCodes == null) {
								                    shouldRetry = (responseCodeX != 200);
							                    } else {
								                    shouldRetry = (NumberUtilities.doesArrayContain(retryOnStatusCodes, responseCodeX));
							                    }
							                    if (!shouldRetry) {
								                    //Return the response if this one was successful
								                    return myNewResponse;
							                    }
						                    }
					                    }
				                    }
				                    if(myNewResponse != null){
					                    //Return the last response if multiple failed
					                    return myNewResponse;
				                    }
			                    }
		                    }
	                    }
                    }
                }
                return originalResponse;
            }
        };
        /*
        //Hard-coded sample
        Interceptor interceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request newRequest = chain.request().newBuilder()
                        //This is where you would add headers if need be. An example would be:
                        .addHeader("Content-Type", "application/json")
                        .addHeader("some-other-header", "more headers")
                        .build(); //Finally, build it
                return chain.proceed(newRequest);
            }
         };
         */

        //Next, we set the logging level
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        if(this.logLevel == null){
	        this.logLevel = HttpLoggingInterceptor.Level.NONE;
        }
        logging.setLevel(this.logLevel);

        //Next, create the OkHttpClient
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        if(this.readTimeout < 0){
	        this.readTimeout = Builder.SIXTY_SECONDS;
        }
        if(this.writeTimeout < 0){
	        this.writeTimeout = Builder.SIXTY_SECONDS;
        }
        if(this.readTimeout > 0){
            builder.readTimeout(this.readTimeout, TimeUnit.MILLISECONDS);
        }
        if(this.writeTimeout > 0){
            builder.writeTimeout(this.writeTimeout, TimeUnit.MILLISECONDS);
        }

        //Add logging and interceptor
        builder.addInterceptor(interceptor);
        builder.addInterceptor(logging);
        
        //Configure SSL
        builder = configureClient(builder);

        //Build the client
        OkHttpClient client = builder.build();

        //Create the retrofit object, which will use the variables/ objects we have created above
        Retrofit.Builder myBuilder = new Retrofit.Builder();
        myBuilder.baseUrl(this.urlBase);

        //Converter Factory
        if(this.customConverterFactory != null){
            myBuilder.addConverterFactory(this.customConverterFactory);
        } else {
            myBuilder.addConverterFactory(new CustomConverterFactory());
        }
	    //Call Adapter Factory
        if(this.customCallAdapterFactory == null) {
        	//Will set either 0 or the user-set retry count, whichever is higher
        	myBuilder.addCallAdapterFactory(RetryCallAdapterFactory.create(Math.max(this.retryCount, 0)));
        } else {
	        //IE: RxJava2CallAdapterFactory.create()
	        myBuilder.addCallAdapterFactory(this.customCallAdapterFactory);
        }

        myBuilder.client(client);
        Retrofit retrofit = myBuilder.build();

        //Now that it is built, create the service client, which references the interface we made
        Object obj = retrofit.create(this.serviceInterface);
        T serviceClient = null;
        try {
            serviceClient = (T) obj;
        } catch (ClassCastException e){
            Log.d("RetrofitClient Error:" ,"If you are getting back null here, make sure your interface class passed in matches" +
                    "the style outlined in the Retrofit documentation. See this class' documentation" +
                    "for a link to a sample.");
        }

        return serviceClient;
    }

    /**
     * This class will configure the OkHttpClient to add things like SSL, certs, etc.
     * @param builder The builder that will be altered and returned
     * @return Altered builder method.
     * For more information on this, please see
     * {@link okhttp3.OkHttpClient.Builder} <-- sslSocketFactory
     */
    private OkHttpClient.Builder configureClient(final OkHttpClient.Builder builder) {

        try {
            X509TrustManager trustManager;
            if(this.forceAcceptAllCertificates){
                trustManager = new X509TrustManager() {
                    public void checkClientTrusted(X509Certificate[] xcs, String string)
                            throws CertificateException {}
                    public void checkServerTrusted(X509Certificate[] xcs, String string)
                            throws CertificateException {}
                    public X509Certificate[] getAcceptedIssuers() {
                        return new java.security.cert.X509Certificate[]{};
                    }
                };
//                trustManager = new X509TrustManager() {
//                    public void checkClientTrusted(X509Certificate[] xcs, String string)
//                            throws CertificateException {}
//                    public void checkServerTrusted(X509Certificate[] xcs, String string)
//                            throws CertificateException {}
//                    public X509Certificate[] getAcceptedIssuers() {
//                        return new java.security.cert.X509Certificate[]{};
//                    }
//                };
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

            SSLContext sslContext;
            SSLSocketFactory sslSocketFactory;
            if(this.sslProtocolOption != null){
                try {
                	TlsVersion v;
                	switch (this.sslProtocolOption){
		                case TLSv1:
		                	v = TlsVersion.TLS_1_0;
		                	break;
		                case TLSv1dot1:
		                	v = TlsVersion.TLS_1_1;
		                	break;
		                case TLSv1dot2:
		                	v = TlsVersion.TLS_1_1;
		                	break;
		                case TLS:
		                	v = TlsVersion.TLS_1_0;
		                	break;
		                case SSLv3:
		                	v = TlsVersion.SSL_3_0;
		                	break;
		                default:
		                	v = TlsVersion.TLS_1_2;
		                	break;
	                }
                    builder.sslSocketFactory(new TLSSocketFactory(this.sslProtocolOption), trustManager);
                    ConnectionSpec cs = new ConnectionSpec.Builder((this.connectionSpec == null)
		                    ? ConnectionSpec.MODERN_TLS : this.connectionSpec)
                            .tlsVersions(v).build();
                    List<ConnectionSpec> specs = new ArrayList<>();
                    specs.add(cs);
	                if(this.forceAcceptAllCertificates) {
	                	//Unsure if needed, will test more
//		                specs.add(ConnectionSpec.CLEARTEXT);
//                      specs.add(ConnectionSpec.COMPATIBLE_TLS);
	                }
                    builder.connectionSpecs(specs);
                } catch (Exception exc) {
                    L.m("Error while setting TLS / Connection Spec, using defaults. \nError == " + exc.getMessage());
                    sslContext = SSLContext.getInstance(this.sslProtocolOption.name);
                    sslContext.init(null, new TrustManager[]{trustManager}, null);
                    sslSocketFactory = sslContext.getSocketFactory();
                    builder.sslSocketFactory(sslSocketFactory, trustManager);
                }
            } else {
                sslContext = SSLContext.getInstance(SSLProtocolOptions.TLS.name);
                sslContext.init(null, new TrustManager[]{trustManager}, null);
                sslSocketFactory = sslContext.getSocketFactory();
                builder.sslSocketFactory(sslSocketFactory, trustManager);
            }
        } catch (KeyManagementException kme){
            kme.printStackTrace();
        } catch (NoSuchAlgorithmException nsa){
            nsa.printStackTrace();
        } catch (KeyStoreException kse){
            kse.printStackTrace();
        } catch (IllegalStateException ise){
            ise.printStackTrace();
        }

        return builder;
    }
    
	
    /**
     * Get an instance of the {@link RetrofitClient.Builder} class
     */
    @CustomAnnotationsBase.RequiresDependency(requiresDependencies = {CustomAnnotationsBase.Dependencies.Retrofit2,
            CustomAnnotationsBase.Dependencies.Retrofit2GSONConverter, CustomAnnotationsBase.Dependencies.OkHttp3,
            CustomAnnotationsBase.Dependencies.GSON, CustomAnnotationsBase.Dependencies.OkHttp3LoggingInterceptor,
            CustomAnnotationsBase.Dependencies.Okio})
    public <T> Builder newBuilder(@NonNull final Class<T> serviceInterface,
                                  @NonNull String urlBase){
        return (new Builder(serviceInterface, urlBase)); //(T)
    }

    //Builder class below
    @CustomAnnotationsBase.RequiresDependency(requiresDependencies = {CustomAnnotationsBase.Dependencies.Retrofit2,
            CustomAnnotationsBase.Dependencies.Retrofit2GSONConverter, CustomAnnotationsBase.Dependencies.OkHttp3,
            CustomAnnotationsBase.Dependencies.GSON, CustomAnnotationsBase.Dependencies.OkHttp3LoggingInterceptor,
            CustomAnnotationsBase.Dependencies.Okio})
    public static final class Builder <G> {

        int builder_retryCount;
        int[] builder_retryOnStatusCodes;
        String builder_urlBase;
        String builder_dateFormat;
        String[] builder_customCookieHeaderString;
        Class<G> builder_serviceInterface;
        Map<String, String> builder_headers;
        HttpLoggingInterceptor.Level builder_logLevel;
        long builder_readTimeout, builder_writeTimeout;
        Converter.Factory builder_customConverterFactory;
        CallAdapter.Factory builder_customCallAdapterFactory;
        SSLProtocolOptions builder_sslProtocolOption;
        OnTaskCompleteListener builder_cookieResponseListener;
        boolean builder_shouldSaveResponseCookies;
        boolean builder_forceAcceptAllCertificates;
	    ConnectionSpec builder_connectionSpec;

        static final int SIXTY_SECONDS = (int)(1000*60);

        /**
         * Constructor visible to the outside
         * @param serviceInterface Service interface class. This should be like the ones shown here:
         *                         https://guides.codepath.com/android/Consuming-APIs-with-Retrofit#define-the-endpoints
         * @param urlBase String url base to use, IE, http://www.myapi.com
         *                This excludes any paths and any versioning here (IE /V1 and no /users/...)
         */
        public Builder(@NonNull final Class<G> serviceInterface,
                       @NonNull String urlBase){
            this.builder_headers = null;
            this.builder_urlBase = urlBase;
            this.builder_forceAcceptAllCertificates = false;
            this.setDateFormat(DEFAULT_DATE_FORMAT);
            this.setTimeouts(SIXTY_SECONDS, SIXTY_SECONDS);
            this.builder_sslProtocolOption = SSLProtocolOptions.TLS;
            this.builder_serviceInterface = serviceInterface;
            this.builder_retryCount = 0;
            this.builder_retryOnStatusCodes = null;
            this.builder_shouldSaveResponseCookies = false;
            this.builder_cookieResponseListener = null;
            this.builder_customCookieHeaderString = null;
            this.builder_connectionSpec = ConnectionSpec.MODERN_TLS;
        }

        /**
         * Set the logging level. Log level is text displayed in the logcat for testing / debugging
         * For more info, see {@link okhttp3.logging.HttpLoggingInterceptor.Level}
         * @param logLevel
        */
        public Builder setLogLevel(HttpLoggingInterceptor.Level logLevel){
            if(logLevel != null){
                this.builder_logLevel = logLevel;
            }
            return this;
        }

        /**
         * Set the chosen Connection spec. Defaults to {@link ConnectionSpec#MODERN_TLS}
         * @param connectionSpec The {@link ConnectionSpec} you wish to use
        */
        public Builder setConnectionSpec(ConnectionSpec connectionSpec){
            if(connectionSpec != null){
                this.builder_connectionSpec = connectionSpec;
            }
            return this;
        }

        /**
         * Force all SSL Certificates to be accepted.
         * WARNING! THIS IS DANGEROUS AND CAN LEAD YOUR APP OPEN TO MALICIOUS ATTACKS!
         * The main reason this option is available is because of API levels 16-19 and the
         * subsequent issue with regards to TrustManagers not working properly. For more info, see
         * this link: https://stackoverflow.com/questions/52630694/how-to-get-trust-anchors-to-work-properly-on-android-api-levels-16-19
         * @param passTrueToEnable Pass true to force acceptance of all certificates &&
         * @param passFalseToEnable Pass false to force acceptance of all certificates
         * @return this
         */
        public Builder forceAcceptAllCertificates(boolean passTrueToEnable, boolean passFalseToEnable){
            this.builder_forceAcceptAllCertificates = (passTrueToEnable && !passFalseToEnable);
            return this;
        }
    
        /**
         * Set the response to return cookies (as a HashSet of Strings) along the passed listener.
         * You can also use this to parse other headers if you want.
         * Note that this will trigger a response if the following header values are received:
         *     "Cookie"  or   "Set-Cookie";
         * If you want to add in another custom cookie header to check for, use the overloaded method:
         * {@link Builder#shouldSaveResponseCookies(OnTaskCompleteListener, String[])}
         * Note that only cookies from the first call (not retries) will trigger this callback
         * {@link OnTaskCompleteListener}
         * @param cookieResponseListener listener HashSet of cookies are sent back on this listener.
         *                               If null is passed, listener is removed and cookies will
         *                               not be sent back
         * @return this
         */
        public Builder shouldSaveResponseCookies(OnTaskCompleteListener cookieResponseListener){
            return shouldSaveResponseCookies(cookieResponseListener, null);
        }
        
        /**
         * Set the response to return cookies (as a HashSet of Strings) along the passed listener.
         * Note that this will trigger a response if the following header values are received:
         *     "Cookie"  or   "Set-Cookie"  or    {whatever_value_you_send}
         * Note that only cookies from the first call (not retries) will trigger this callback
         * {@link OnTaskCompleteListener}
         * @param cookieResponseListener listener HashSet of cookies are sent back on this listener.
         *                               If null is passed, listener is removed and cookies will
         *                               not be sent back
         * @param customCookieHeaderStrings Array of Strings to parse on headers
         * @return this
         */
        public Builder shouldSaveResponseCookies(OnTaskCompleteListener cookieResponseListener,
                                                 String[] customCookieHeaderStrings){
            if(cookieResponseListener != null) {
                this.builder_shouldSaveResponseCookies = true;
                this.builder_cookieResponseListener = cookieResponseListener;
            } else {
                this.builder_shouldSaveResponseCookies = false;
                this.builder_cookieResponseListener = null;
            }
            this.builder_customCookieHeaderString = customCookieHeaderStrings;
            return this;
        }
        
        /**
         * Add a retry interceptor that will attempt the call again should it fail.
         * @param retryCount Number of attempts to retry. Defaults to 0
         * @return
         */
        public Builder setRetryCount(@IntRange(from = 0) final int retryCount){
            return setRetryCount(retryCount, null);
        }
    
    
        /**
         * Add a retry interceptor that will attempt the call again should it fail. Defaults
         * to code != 200, but specific retry code integers can be set here too
         * @param retryCount Number of attempts to retry. Defaults to 0
         * @param retryOnStatusCodes int status codes you wish to allow retries on. If nothing is
         *                           passed, will default to anything not == 200.
         * @return
         */
        public Builder setRetryCount(@IntRange(from = 0) final int retryCount, @Nullable int[] retryOnStatusCodes){
            if(retryCount >= 1){
                this.builder_retryCount = retryCount;
            } else {
                this.builder_retryCount = 0;
            }
            this.builder_retryOnStatusCodes = retryOnStatusCodes;
            return this;
        }
        
        /**
         * Set a custom factory in case you want to add a special one (IE RX Java)
         * Note! If left out or not set, will default to {@link CustomConverterFactory}
         * @param factory {@link Converter.Factory}
         * @return this
         */
        public Builder setCustomConverterFactory(@Nonnull Converter.Factory factory){
            this.builder_customConverterFactory = factory;
            return this;
        }

        /**
         * Set a custom Adapter factory. If this is ignored or not set, it will default to
         * the {@link RetryCallAdapterFactory}
         * @param factory An example of this would be an RX Java call adapter
         * @return this
         */
        public Builder setCustomAdapterFactory(@Nonnull CallAdapter.Factory factory){
            this.builder_customCallAdapterFactory = factory;
            return this;
        }

        /**
         * todo note! this is breaking something where using this ignores the headers listed within the interface method!
         * Set the headers. This would be where you would send in a map with header Strings.
         * Samples would be a map containing types like these:
         * <"Authentication", "password123">
         * <"Content-Type", "application/json">
         * <"Content-Type", "multipart/form-data">
         * @param headers
         */
        public Builder setHeaders(Map<String, String> headers){
            if(!MiscUtilities.isMapNullOrEmpty(headers)) {
                this.builder_headers = headers;
            }
            return this;
        }

        /**
         * This is the SSL protocol level to be used in the call. Will default to TLS if null
         * is passed or this setter is never called.
         * @param sslProtocolOption {@link SSLProtocolOptions}
         * @return this
         */
        public Builder setSSLProtocolOption(SSLProtocolOptions sslProtocolOption){
            this.builder_sslProtocolOption = (sslProtocolOption != null) ? sslProtocolOption : SSLProtocolOptions.TLS;
            return this;
        }

        /**
         * Set the read and write timeout IN MILLISECONDS
         * @param readTimeoutInMilliseconds Read timeout (60000 == 1 minute). Pass 0 for no timeout
         * @param writeTimeoutInMilliseconds Write timeout (60000 == 1 minute). Pass 0 for no timeout
         */
        public Builder setTimeouts(long readTimeoutInMilliseconds, long writeTimeoutInMilliseconds){
            builder_readTimeout = readTimeoutInMilliseconds;
            builder_writeTimeout = writeTimeoutInMilliseconds;
            return this;
        }

        /**
         * Set the data format.
         * @param dateFormat Date format String structured similar to this:
         *                   "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
         *                   If not set or null passed, it will be set to default listed above.
         */
        public Builder setDateFormat(String dateFormat){
            if(!StringUtilities.isNullOrEmpty(dateFormat)) {
                this.builder_dateFormat = dateFormat;
            }
            return this;
        }

        /**
         * todo note! this is breaking something where using this ignores the headers listed within the interface method!
         * Simple setter method for those who are too lazy to make a hashmap containing
         * "Content-Type", "application/json" as headers. Just call this and it will set it.
         * Keep in mind this will replace other headers you have in place so call this before
         * you make any other header alterations (IE with a client id or auth token)
         */
        public Builder callIsJSONFormat(){
            Map<String, String> myMap = new HashMap<>();
            myMap.put(CONTENT_TYPE, APPLICATION_JSON);
            this.builder_headers = myMap;
            return this;
        }

        /**
         * todo note! this is breaking something where using this ignores the headers listed within the interface method!
         * Simple setter method for those who are too lazy to make a hashmap containing
         * "Content-Type", "multipart/form-data" as headers. Just call this and it will set it.
         * Keep in mind this will replace other headers you have in place so call this before
         * you make any other header alterations (IE with a client id or auth token)
         */
        public Builder callIsMultipartFormat(){
            Map<String, String> myMap = new HashMap<>();
            myMap.put(CONTENT_TYPE, MULTIPART_FORM_DATA);
            this.builder_headers = myMap;
            return this;
        }

        /**
         * Shortcut for getting a map of the content-type application json strings.
         * @return Map<String, String> containing one set of "Content-Type", "application/json"
         */
        public static Map<String, String> getApplicationJSONMap(){
            Map<String, String> myMap = new HashMap<>();
            myMap.put(CONTENT_TYPE, APPLICATION_JSON);
            return myMap;
        }

        /**
         * Simple setter method for those who are too lazy to make a hashmap containing
         * "Content-Type", "multipart/form-data" as headers. Just call this and it will set it
         */
        public static Map<String, String> getMultipartFormat(){
            Map<String, String> myMap = new HashMap<>();
            myMap.put(CONTENT_TYPE, MULTIPART_FORM_DATA);
            return myMap;
        }

        /**
         * Builds a Retrofit client and returns it
         * @return
         */
        public RetrofitClient build(){
            return new RetrofitClient(this);
        }
    }
    
    //region Error Response Codes
    
    private static final int[] ERROR_CODES_4XX = {
            400, 401, 402, 403, 404, 405, 406, 407, 408, 409, 410, 411, 412, 413, 414, 415,
            417, 418, 420, 422, 423, 424, 425, 426, 428, 429, 431, 444, 449, 450, 451, 499
    };
    private static final int[] ERROR_CODES_5XX = {
            500, 501, 502, 503, 504, 505, 506, 507, 508, 509, 510, 511, 598, 599
    };
    
    /**
     * Get all 4xx response codes
     * Reference: https://www.restapitutorial.com/httpstatuscodes.html
     * @return int array of the 4xx error codes (IE 404)
     */
    public static int[] getAll4xxErrorCodes(){
        return ERROR_CODES_4XX;
    }
    
    /**
     * Get all 5xx response codes
     * Reference: https://www.restapitutorial.com/httpstatuscodes.html
     * @return int array of the 5xx error codes (IE 503)
     */
    public static int[] getAll5xxErrorCodes(){
        return ERROR_CODES_5XX;
    }
    
    /**
     * Get all of the 4xx and 5xx error codes
     * Reference: https://www.restapitutorial.com/httpstatuscodes.html
     * @return int array with the combined 4xx and 5xx error codes (IE 401 and 500)
     */
    public static int[] getAll4xx5xxErrorCodes(){
        int[] toReturn = new int[getAll4xxErrorCodes().length + getAll5xxErrorCodes().length];
        System.arraycopy(getAll4xxErrorCodes(), 0, toReturn, 0, getAll4xxErrorCodes().length);
        System.arraycopy(getAll5xxErrorCodes(), 0, toReturn, getAll4xxErrorCodes().length, getAll5xxErrorCodes().length);
        return toReturn;
    }
    //endregion
}



