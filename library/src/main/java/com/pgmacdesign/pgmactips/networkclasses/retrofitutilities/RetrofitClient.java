package com.pgmacdesign.pgmactips.networkclasses.retrofitutilities;

import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.util.Log;

import com.pgmacdesign.pgmactips.misc.CustomAnnotationsBase;
import com.pgmacdesign.pgmactips.networkclasses.sslsocketsandprotocols.SSLProtocolOptions;
import com.pgmacdesign.pgmactips.networkclasses.sslsocketsandprotocols.Tls12SocketFactory;
import com.pgmacdesign.pgmactips.utilities.L;
import com.pgmacdesign.pgmactips.utilities.MiscUtilities;
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
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

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

    private static final String APPLICATION_JSON = "application/json";
    private static final String MULTIPART_FORM_DATA = "multipart/form-data";
    private static final String CONTENT_TYPE = "Content-Type";

    public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    public static final String DEFAULT_DATE_FORMAT_WITHOUT_MILLISECONDS = "yyyy-MM-dd'T'HH:mm:ss'Z'";

    private String urlBase;
    private Map<String, String> headers;
    private HttpLoggingInterceptor.Level logLevel;
    private Interceptor retryInterceptor;
    private long readTimeout, writeTimeout;
    private String dateFormat;
    private Class serviceInterface;
    private Converter.Factory customConverterFactory;
    private CallAdapter.Factory customCallAdapterFactory;
    private SSLProtocolOptions sslProtocolOption;
    private boolean forceAcceptAllCertificates;

    /**
     * Constructor
     */
    private RetrofitClient(RetrofitClient.Builder builder) {
        this.urlBase = builder.builder_urlBase;
        this.headers = builder.builder_headers;
        this.logLevel = builder.builder_logLevel;
        this.dateFormat = builder.builder_dateFormat;
        this.readTimeout = builder.builder_readTimeout;
        this.writeTimeout = builder.builder_writeTimeout;
        this.retryInterceptor = builder.retryInterceptor;
        this.sslProtocolOption = builder.sslProtocolOption;
        this.serviceInterface = builder.builder_serviceInterface;
        this.customConverterFactory = builder.customConverterFactory;
        this.customCallAdapterFactory = builder.customCallAdapterFactory;
        this.forceAcceptAllCertificates = builder.forceAcceptAllCertificates;
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
        Headers.Builder builder = new Headers.Builder();
        if(MiscUtilities.isMapNullOrEmpty(headers)){
            return builder.build();
        } else {
            for (Map.Entry<String, String> myMap : headers.entrySet()) {
                String key = myMap.getKey();
                String value = myMap.getValue();
                if (!StringUtilities.isNullOrEmpty(key) &&
                        !StringUtilities.isNullOrEmpty(value)) {
                    builder.add(key, value);
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
                Request.Builder builder = chain.request().newBuilder();
                if(headers != null){
                    if(headers.size() > 0){
                        Headers headers = buildHeaders();
                        builder.headers(headers);
                    }
                }
                Request newRequest = builder.build();
                return chain.proceed(newRequest);
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
        if(logLevel == null){
            logLevel = HttpLoggingInterceptor.Level.NONE;
        }
        logging.setLevel(logLevel);

        //Next, create the OkHttpClient
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        if(readTimeout < 0){
            readTimeout = Builder.SIXTY_SECONDS;
        }
        if(writeTimeout < 0){
            writeTimeout = Builder.SIXTY_SECONDS;
        }
        if(readTimeout > 0){
            builder.readTimeout(readTimeout, TimeUnit.MILLISECONDS);
        }
        if(writeTimeout > 0){
            builder.writeTimeout(writeTimeout, TimeUnit.MILLISECONDS);
        }

        //Add logging and interceptor
        builder.addInterceptor(interceptor);
        builder.addInterceptor(logging);
        if(this.retryInterceptor != null){
            builder.addInterceptor(this.retryInterceptor);
        }
        //Configure SSL
        builder = configureClient(builder);

        //Build the client
        OkHttpClient client = builder.build();

        //Create the retrofit object, which will use the variables/ objects we have created above
        Retrofit.Builder myBuilder = new Retrofit.Builder();
        myBuilder.baseUrl(urlBase);

        if(customConverterFactory != null){
            myBuilder.addConverterFactory(customConverterFactory);
        } else {
            myBuilder.addConverterFactory(new CustomConverterFactory());
        }
        if(customCallAdapterFactory != null) {
            myBuilder.addCallAdapterFactory(customCallAdapterFactory);
            //IE: RxJava2CallAdapterFactory.create()
        }

        myBuilder.client(client);
        Retrofit retrofit = myBuilder.build();

        //Now that it is built, create the service client, which references the interface we made
        Object obj = retrofit.create(serviceInterface);
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
                        return null;
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

            sslContext = SSLContext.getInstance(this.sslProtocolOption.name);
            sslContext.init(null, new TrustManager[]{trustManager}, null);
            sslSocketFactory = sslContext.getSocketFactory();
            builder.sslSocketFactory(sslSocketFactory, trustManager);

            if(true){
                return builder;
            }

            //todo remove this if not needed. Keeping here for reference
            //Check on SSL Protocol to use
            boolean needToForce1dot2 = SSLProtocolOptions.requiresForcedTLS1dot2();
            if(needToForce1dot2 && this.sslProtocolOption == SSLProtocolOptions.TLSv1dot2){
                try {
                    SSLContext sc = SSLContext.getInstance(SSLProtocolOptions.TLSv1dot2.name);
                    sc.init(null, null, null);
                    builder.sslSocketFactory(new Tls12SocketFactory(sc.getSocketFactory()), trustManager);
                    ConnectionSpec cs = new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                            .tlsVersions(TlsVersion.TLS_1_2)
                            .build();
                    List<ConnectionSpec> specs = new ArrayList<>();
                    specs.add(cs);
                    specs.add(ConnectionSpec.COMPATIBLE_TLS);
                    specs.add(ConnectionSpec.CLEARTEXT);
                    builder.connectionSpecs(specs);
                } catch (Exception exc) {
                    L.m("Error while setting TLS 1.2");
                    sslContext = SSLContext.getInstance(this.sslProtocolOption.name);
                    sslContext.init(null, new TrustManager[]{trustManager}, null);
                    sslSocketFactory = sslContext.getSocketFactory();
                    builder.sslSocketFactory(sslSocketFactory, trustManager);
                }
            } else {
                sslContext = SSLContext.getInstance(this.sslProtocolOption.name);
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
    public static final class Builder <T> {

        String builder_urlBase;
        String builder_dateFormat;
        Interceptor retryInterceptor;
        Class<T> builder_serviceInterface;
        Map<String, String> builder_headers;
        HttpLoggingInterceptor.Level builder_logLevel;
        long builder_readTimeout, builder_writeTimeout;
        Converter.Factory customConverterFactory;
        CallAdapter.Factory customCallAdapterFactory;
        SSLProtocolOptions sslProtocolOption;
        boolean forceAcceptAllCertificates;

        static final int SIXTY_SECONDS = (int)(1000*60);

        /**
         * Constructor visible to the outside
         * @param serviceInterface Service interface class. This should be like the ones shown here:
         *                         https://guides.codepath.com/android/Consuming-APIs-with-Retrofit#define-the-endpoints
         * @param urlBase String url base to use, IE, http://www.myapi.com
         *                This excludes any paths and any versioning here (IE /V1 and no /users/...)
         */
        public Builder(@NonNull final Class<T> serviceInterface,
                       @NonNull String urlBase){
            this.builder_headers = null;
            this.retryInterceptor = null;
            this.builder_urlBase = urlBase;
            this.forceAcceptAllCertificates = false;
            this.setDateFormat(DEFAULT_DATE_FORMAT);
            this.setTimeouts(SIXTY_SECONDS, SIXTY_SECONDS);
            this.sslProtocolOption = SSLProtocolOptions.TLS;
            this.builder_serviceInterface = serviceInterface;
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
            this.forceAcceptAllCertificates = (passTrueToEnable && !passFalseToEnable);
            return this;
        }

        /**
         * Add a retry interceptor that will attempt the call again should it fail.
         * @param retryCount
         * @return
         */
        public Builder setRetryCount(@IntRange(from = 1) final int retryCount){
            if(retryCount < 1){
                this.retryInterceptor = null;
                return this;
            }
            retryInterceptor = new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    if(chain == null){
                        return null;
                    }
                    Request request = chain.request();
                    if(request == null){
                        return null;
                    }

                    Response response = chain.proceed(request);

                    if(response != null){
                        int tryCount = 0;
                        while (!response.isSuccessful() && tryCount < retryCount) {
                            tryCount++;
                            L.m("Call failed, retry triggered. Retry #" + tryCount);
                            // retry the request
                            response = chain.proceed(request);
                        }
                    }
                    return response;
                }
            };
            return this;
        }

        /**
         * Set a custom factory in case you want to add a special one (IE RX Java)
         * Note! If left out or not set, will default to {@link CustomConverterFactory}
         * @param factory {@link Converter.Factory}
         * @return this
         */
        public Builder setCustomConverterFactory(Converter.Factory factory){
            this.customConverterFactory = factory;
            return this;
        }

        /**
         * Set a custom Adapter factory. If this is ignored or not set, this will NOT
         * be included in the final output
         * @param factory An example of this would be an RX Java call adapter
         * @return this
         */
        public Builder setCustomAdapterFactory(CallAdapter.Factory factory){
            this.customCallAdapterFactory = factory;
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
            this.sslProtocolOption = (sslProtocolOption != null) ? sslProtocolOption : SSLProtocolOptions.TLS;
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


}



