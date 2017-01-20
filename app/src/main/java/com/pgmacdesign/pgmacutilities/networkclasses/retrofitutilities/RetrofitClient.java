package com.pgmacdesign.pgmacutilities.networkclasses.retrofitutilities;

import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pgmacdesign.pgmacutilities.nonutilities.PGMacUtilitiesConstants;
import com.pgmacdesign.pgmacutilities.networkclasses.retrofitutilities.serviceapiinterfaces.SampleRetrofitService;
import com.pgmacdesign.pgmacutilities.utilities.L;
import com.pgmacdesign.pgmacutilities.utilities.MiscUtilities;
import com.pgmacdesign.pgmacutilities.utilities.StringUtilities;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Converter;
import retrofit2.GsonConverterFactory;
import retrofit2.Retrofit;

/**
 * Created by pmacdowell on 8/25/2016.
 */
public class RetrofitClient {

    // TODO: 8/26/2016 add link to http://square.github.io/retrofit/ into build.gradle

    private String urlBase;
    private Map<String, String> headers;
    private HttpLoggingInterceptor.Level logLevel;
    private int readTimeout, writeTimeout;
    private boolean isSerializableResponse;
    private String dateFormat;
    private Class serviceInterface;
    private static final int SIXTY_SECONDS = (int)(PGMacUtilitiesConstants.ONE_MINUTE / 1000);

    //Service Client used for web calls
    //private T serviceClient;

    /**
     /**
     * Factory builder method for quick access. This will build the class and set some
     * defaults. More can be customized if
     * @param serviceInterface The class represented by a retrofit service {@link SampleRetrofitService}
     * @param urlBase The string URL base to use IE: dddd
     * @param headers Headers. EX = "Content-Type", "application/json"
     * @param <T> T extends Retrofit Service Interface {@link SampleRetrofitService}
     * @return RetrofitClient object
     * todo eventually refactor this into a builder style
     */

    public static <T> T RetrofitFactoryBuilder(@NonNull final Class serviceInterface,
                                                        @NonNull String urlBase,
                                                        Map<String, String> headers){
        L.m("urlbase = " + urlBase);
        try {
            RetrofitClient myClient = new RetrofitClient(serviceInterface, urlBase);
            myClient.setDateFormat(PGMacUtilitiesConstants.DEFAULT_DATE_FORMAT);
            myClient.setLogLevel(HttpLoggingInterceptor.Level.BODY);
            myClient.setHeaders(headers);
            myClient.setTimeouts(SIXTY_SECONDS, SIXTY_SECONDS);

            T t = myClient.buildRetrofitClient();

            return t;
        } catch (IllegalStateException e){
            e.printStackTrace();
            return null;
        }
    }
    /**
     * Constructor
     * @param serviceInterface Service Client. For example, see: {@link SampleRetrofitService}
     * @param urlBase The String URL base. An example would be:
     */
    public <T> RetrofitClient(@NonNull final Class<T> serviceInterface, @NonNull String urlBase) {
        this.urlBase = urlBase;
        this.serviceInterface = serviceInterface;
        initDefaults();
    }

    /**
     * init defaults in case the user doesn't set any default log level, date format, or timeouts
     */
    private void initDefaults(){
        this.headers = null;
        this.setLogLevel(HttpLoggingInterceptor.Level.BODY);
        this.setDateFormat(PGMacUtilitiesConstants.DEFAULT_DATE_FORMAT);
        this.setTimeouts(SIXTY_SECONDS, SIXTY_SECONDS);
        this.setIsSerializableResponse(true);
    }

    /**
     * This is for setting type converter factory for response data. This is set to true by default,
     * which means that it is assuming the code can be serialized by Gson. If not, IE, you are
     * receiving a String response of no formatting, just "true" or "false" and nothing else,
     * send this as false. If sent as null, it will default to true
     * @param bool
     */
    public void setIsSerializableResponse(Boolean bool){
        if(bool == null){
            bool = true;
        }
        this.isSerializableResponse = bool;
    }
    /**
     * Simple setter method for those who are too lazy to make a hashmap containing
     * "Content-Type", "application/json" as headers. Just call this and it will set it
     */
    public void callIsJsonFormat(){
        Map<String, String> myMap = new HashMap<>();
        myMap.put("Content-Type", "application/json");
        this.headers = myMap;
    }

    /**
     * Simple setter method for those who are too lazy to make a hashmap containing
     * "Content-Type", "multipart/form-data" as headers. Just call this and it will set it
     */
    public void callIsMultipartFormat(){
        Map<String, String> myMap = new HashMap<>();
        myMap.put("Content-Type", "multipart/form-data");
        this.headers = myMap;
    }

    /**
     * Shortcut for getting a map of the content-type application json strings.
     * @return Map<String, String> containing one set of "Content-Type", "application/json"
     */
    public static Map<String, String> getApplicationJSONMap(){
        Map<String, String> myMap = new HashMap<>();
        myMap.put("Content-Type", "application/json");
        return myMap;
    }
    /**
     * Set the logging level. Log level is text displayed in the logcat for testing / debugging
     * For more info, see {@link okhttp3.logging.HttpLoggingInterceptor.Level}
     * @param logLevel
     */
    public void setLogLevel(HttpLoggingInterceptor.Level logLevel){
        if(logLevel != null){
            this.logLevel = logLevel;
        }
    }

    /**
     * Set the headers. This would be where you would send in a map with header Strings.
     * Samples would be a map containing types like these:
     * <"Authentication", "password123">
     * <"Content-Type", "application/json">
     * <"Content-Type", "multipart/form-data">
     * @param headers
     */
    public void setHeaders(Map<String, String> headers){
        if(MiscUtilities.isMapNullOrEmpty(headers)) {
            this.headers = headers;
        }
    }

    /**
     * Set the read and write timeout IN SECONDS
     * @param readTimeoutInSeconds Read timeout (60 == 1 minute)
     * @param writeTimeoutInSeconds Write timeout (60 == 1 minute)
     */
    public void setTimeouts(int readTimeoutInSeconds, int writeTimeoutInSeconds){
        readTimeout = readTimeoutInSeconds;
        writeTimeout = writeTimeoutInSeconds;
    }

    /**
     * Set the data format.
     * @param dateFormat Date format String structured similar to this:
     *                   "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
     *                   If not set or null passed, it will be set to default listed above.
     */
    public void setDateFormat(String dateFormat){
        if(!StringUtilities.isNullOrEmpty(dateFormat)) {
            this.dateFormat = dateFormat;
        }
    }

    /**
     * Build a Workable service client Client
     * This should be used after the class is initialized and the setters are all set
     * @return Service client for making calls. Will be directly linked to the Interface passed in
     *         as well as to its calls. For an example, see {@link SampleRetrofitService}
     */
    public <T> T buildServiceClient(){
        T t = this.buildRetrofitClient();
        return t;
    }

    /**
     * This builds a client that will be used for network calls
     */
    public <T> T buildRetrofitClient(){
        //First create the interceptor, which will be used in the Retrofit call
        Interceptor interceptor = new Interceptor() {
            @Override
            public Response intercept(Interceptor.Chain chain) throws IOException {
                Request.Builder builder = new Request.Builder();
                if(!MiscUtilities.isMapNullOrEmpty(headers)){
                    for(Map.Entry<String, String> myMap : headers.entrySet()){
                        String key = myMap.getKey();
                        String value = myMap.getValue();
                        if(!StringUtilities.isNullOrEmpty(key) &&
                                !StringUtilities.isNullOrEmpty(value)){
                            builder.addHeader(key, value);
                        }
                    }
                }
                Request newRequest = builder.build();
                return chain.proceed(newRequest);
            }
        };

        //Next, we set the logging level
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(logLevel);

        //Next, create the OkHttpClient
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        if(readTimeout == 0){
            readTimeout = 60;
        }
        if(writeTimeout == 0){
            writeTimeout = 60;
        }
        if(readTimeout > 0){
            builder.readTimeout(readTimeout, TimeUnit.SECONDS);
        }
        if(writeTimeout > 0){
            builder.writeTimeout(writeTimeout, TimeUnit.SECONDS);
        }

        //Add logging and interceptor
        builder.addInterceptor(interceptor);
        builder.addInterceptor(logging);

        //Configure SSL
        builder = configureClient(builder);

        //Build the client
        OkHttpClient client = builder.build();

        //Old way of adding interceptors, refactored into builder now
        //client.interceptors().add(interceptor);
        //client.interceptors().add(logging);

        //Create the retrofit object, which will use the variables/ objects we have created above
        Retrofit.Builder myBuilder = new Retrofit.Builder();
        myBuilder.baseUrl(urlBase);
        myBuilder.addConverterFactory(new CustomConverterFactory());
        if(isSerializableResponse){
            //Gson object that will be used for parsing the response from the server
            Gson gson = new GsonBuilder()
                    .setLenient() //This is to allow the server to send back things without proper formatting
                    .setPrettyPrinting() //Makes it easier to read in the logcat
                    .setDateFormat(dateFormat)
                    .create();
            GsonConverterFactory factory = GsonConverterFactory.create(gson);
            myBuilder.addConverterFactory(factory);

        } else {
            ToStringConverterFactory factory2 = new ToStringConverterFactory();
            myBuilder.addConverterFactory(factory2);
        }
        myBuilder.client(client);
        Retrofit retrofit = myBuilder.build();

        //Now that it is built, create the service client, which references the interface we made
        Object obj = retrofit.create(serviceInterface);
        T serviceClient = null;
        try {
            serviceClient = (T) obj;
        } catch (ClassCastException e){
            L.m("If you are getting back null here, make sure your interface class passed in matches" +
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
    private static OkHttpClient.Builder configureClient(final OkHttpClient.Builder builder) {

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

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[]{trustManager}, null);
            SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            builder.sslSocketFactory(sslSocketFactory, trustManager);

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
     * Custom String converter factory for APIs returning Strings only. Modified after pulling from link below:
     * https://github.com/square/retrofit/blob/07d1f3de5e0eb11fb537464bd14fdbacfc9e55a7/retrofit/src/test/java/retrofit/ToStringConverterFactory.java
     * which was references here by Wharton:
     * https://github.com/square/retrofit/issues/1151
     */
    class ToStringConverterFactory extends Converter.Factory {

        public ToStringConverterFactory() {
            super();
        }

        @Override
        public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
            Converter<ResponseBody, ?> converter = new Converter<ResponseBody, Object>() {
                @Override
                public Object convert(ResponseBody value) throws IOException {
                    try {
                        return value.string();
                    } catch (Exception e) {
                        return null;
                    }
                }
            };
            return converter;
        }

        @Override
        public Converter<?, RequestBody> requestBodyConverter(Type type, Annotation[] parameterAnnotations, Annotation[] methodAnnotations, Retrofit retrofit) {
            Converter<?, RequestBody> converter = new Converter() {
                @Override
                public String convert(Object value) throws IOException {
                    try {
                        return value.toString();
                    } catch (Exception e){
                        return null;
                    }
                }
            };
            return converter;
        }
    }
}


