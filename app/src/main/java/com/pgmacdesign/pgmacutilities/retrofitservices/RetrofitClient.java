package com.pgmacdesign.pgmacutilities.retrofitservices;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import com.pgmacdesign.pgmacutilities.utilities.MiscUtilities;
import com.pgmacdesign.pgmacutilities.utilities.StringUtilities;

import java.io.IOException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;

/**
 * Created by pmacdowell on 8/25/2016.
 */
public class RetrofitClient {

    //This log level is used to log in the logcat what is being sent and received via the body
    @SerializedName("httpLogLevel")
    private static HttpLoggingInterceptor.Level httpLogLevel = HttpLoggingInterceptor.Level.BODY;
    private String urlEndpoint;
    private Map<String, String> headers;
    private HttpLoggingInterceptor.Level logLevel;
    private int readTimeout, writeTimeout;

    /*
    Todo refactor in check for
    "Content-Type", "application/json"
     */

    /**
     *
     * @param urlEndpoint
     * @param headers
     * @param logLevel {@link okhttp3.logging.HttpLoggingInterceptor.Level}
     */
    public RetrofitClient(String urlEndpoint, Map<String, String> headers, HttpLoggingInterceptor.Level logLevel) {
        this.urlEndpoint = urlEndpoint;
        this.headers = headers;
        this.logLevel = logLevel;
        if(logLevel == null){
            logLevel = HttpLoggingInterceptor.Level.NONE;
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









    //Service Client used for our endpoint calls
    private static GenericRetrofitService serviceClient;
    //Service Client used for image multi-part upload calls
    private static RetrofitService serviceMultipartClient;




    //This static method is called when the class is created and runs on its own
    static {
        buildAClient();
    }

    /**
     * Simple getter method. When this is called, the static method is initiated and the client is
     * built and returned.
     * @return Fully built RetrofitService client that is ready for outbound calls
     */
    public static RetrofitService getServiceClient(){
        return serviceClient;
    }
    /**
     * Simple getter method. When this is called, the static method is initiated and the client is
     * built and returned.
     * @return Fully built RetrofitService client that is ready for outbound calls (Multi-part)
     */
    public static RetrofitService getServiceMultipartClientClient(){
        return serviceMultipartClient;
    }

    /**
     * This builds a client that will be used for outbound calls
     */
    public void buildAClient(){

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

        builder.addInterceptor(interceptor);
        builder.addInterceptor(logging);

        client.interceptors().add(interceptor);
        client.interceptors().add(logging);

        OkHttpClient client = builder.build();
        


        //Next, we are making a Gson object that will be used for parsing the response from the server
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                .create();

        //Lastly, create the retrofit object, which will use the variables/ objects we have created above
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client)
                .build();

        //Now that it is built, create the service client, which references the interface we made
        serviceClient = retrofit.create(RetrofitService.class);
    }

    /**
     * This builds a client that will be used for outbound calls
     */
    private static void buildImageClient(){

        //First create the interceptor, which will be used in the Retrofit call
        Interceptor interceptor = new Interceptor() {
            @Override
            public Response intercept(Interceptor.Chain chain) throws IOException {
                Request newRequest = chain.request().newBuilder()
                        //This is where you would add headers if need be. An example would be:
                        //.addHeader("Authorization", "Token:" + someAPIToken)
                        .addHeader("Content-Type", "multipart/form-data")
                        .build(); //Finally, build it
                return chain.proceed(newRequest);
            }
        };

        //Next, we set the logging level
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(httpLogLevel);

        //Next, create the OkHttpClient
        OkHttpClient client = RetrofitClient.configureClient(new OkHttpClient());
        client.interceptors().add(interceptor);
        client.interceptors().add(logging);
        //Set the timeout to 1 minute for now. We can set it to more, but for simplicity, keep it at 1 min
        client.setWriteTimeout(1, TimeUnit.MINUTES);
        client.setReadTimeout(1, TimeUnit.MINUTES);

        //Next, we are making a Gson object that will be used for parsing the response from the server
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                .create();

        //Lastly, create the retrofit object, which will use the variables/ objects we have created above
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client)
                .build();

        //Now that it is built, create the service client, which references the interface we made
        serviceMultipartClient = retrofit.create(RetrofitService.class);
    }

    /**
     * This class will configure the OkHttpClient to add things like SSL, certs, etc.
     * @param client The client object being changes
     * @return an Altered OkHttpClient with these new features added
     */
    public static OkHttpClient configureClient(final OkHttpClient client) {
        final TrustManager[] certs = new TrustManager[]{new X509TrustManager() {
            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }
            @Override
            public void checkServerTrusted(final X509Certificate[] chain,
                                           final String authType)
                    throws CertificateException {
            }

            @Override
            public void checkClientTrusted(final X509Certificate[] chain,
                                           final String authType)
                    throws CertificateException {
            }
        }};

        SSLContext ssl = null;
        try {
            ssl = SSLContext.getInstance("TLS");
            ssl.init(null, certs, new SecureRandom());
        } catch (final java.security.GeneralSecurityException ex) {
        }

        try {
            final HostnameVerifier hostnameVerifier = new HostnameVerifier() {
                @Override
                public boolean verify(final String hostname,
                                      final SSLSession session) {
                    return true;
                }
            };
            client.setHostnameVerifier(hostnameVerifier);
            client.setSslSocketFactory(ssl.getSocketFactory());
        } catch (final Exception e) {
        }

        return client;
    }
}


