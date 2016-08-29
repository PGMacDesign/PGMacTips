package com.pgmacdesign.pgmacutilities.retrofitservices;

import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pgmacdesign.pgmacutilities.nonutilities.PGMacUtilitiesConstants;
import com.pgmacdesign.pgmacutilities.utilities.MiscUtilities;
import com.pgmacdesign.pgmacutilities.utilities.StringUtilities;

import java.io.IOException;
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
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.GsonConverterFactory;
import retrofit2.Retrofit;

/**
 * Created by pmacdowell on 8/25/2016.
 */
public class RetrofitClient <T> {

    // TODO: 8/26/2016 add link to http://square.github.io/retrofit/ into build.gradle

    private String urlBase;
    private Map<String, String> headers;
    private HttpLoggingInterceptor.Level logLevel;
    private int readTimeout, writeTimeout;
    private String dateFormat;
    private Class<T> serviceInterface;
    private static final int SIXTY_SECONDS = (int)(PGMacUtilitiesConstants.ONE_MINUTE / 1000);

    //Service Client used for web calls
    private T serviceClient;

    /**
     * Factory builder method for quick access. This will build the class and set some
     * defaults. More can be customized if
     * @param serviceInterface The class represented by a retrofit service {@link SampleRetrofitService}
     * @param urlBase The string URL base to use IE: dddd
     * @param headers Headers. EX = "Content-Type", "application/json"
     * @return RetrofitClient object
     * todo eventually refactor this into a builder style
     */
    public static RetrofitClient RetrofitFactoryBuilder(@NonNull final Class serviceInterface,
                                           @NonNull String urlBase,
                                           Map<String, String> headers){
        RetrofitClient myClient = new RetrofitClient(serviceInterface, urlBase);
        myClient.setDateFormat(PGMacUtilitiesConstants.DEFAULT_DATE_FORMAT);
        myClient.setLogLevel(HttpLoggingInterceptor.Level.BODY);
        myClient.setHeaders(headers);
        myClient.setTimeouts(SIXTY_SECONDS, SIXTY_SECONDS);

        return myClient;
    }
    /**
     * Constructor
     * @param serviceInterface Service Client. For example, see: {@link SampleRetrofitService}
     * @param urlBase The String URL base. An example would be:
     */
    public RetrofitClient(@NonNull final Class<T> serviceInterface, @NonNull String urlBase) {
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
     * @param dateFormat Date format String structured like this:
     *                   "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
     *                   If not set or null passed, it will be set to default listed above.
     */
    public void setDateFormat(String dateFormat){
        if(!StringUtilities.isNullOrEmpty(dateFormat)) {
            this.dateFormat = dateFormat;
        }
    }

    /**
     * Build a Client
     * @return
     */
    public T buildClient(){
        return buildAClient();
    }

    /**
     * This builds a client that will be used for network calls
     */
    private T buildAClient(){
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

        //Gson object that will be used for parsing the response from the server
        Gson gson = new GsonBuilder()
                .setDateFormat(dateFormat)
                .create();

        //Create the retrofit object, which will use the variables/ objects we have created above
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(urlBase)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client)
                .build();

        //Now that it is built, create the service client, which references the interface we made
        serviceClient = retrofit.create(serviceInterface);
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



}


