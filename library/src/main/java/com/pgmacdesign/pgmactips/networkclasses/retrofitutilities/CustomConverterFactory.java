package com.pgmacdesign.pgmactips.networkclasses.retrofitutilities;

import com.google.gson.reflect.TypeToken;
import com.pgmacdesign.pgmactips.datamodels.SamplePojo;
import com.pgmacdesign.pgmactips.misc.PGMacTipsConstants;
import com.pgmacdesign.pgmactips.utilities.L;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * Created by pmacdowell on 11/7/2016.
 */
public class CustomConverterFactory extends Converter.Factory  {

    //How to make custom type converters
    private static final Type TYPE_TESTINGPOJO = new TypeToken<SamplePojo>(){}.getType();
    private static final Type TYPE_LIST_OF_TESTINGPOJO = new TypeToken<ArrayList<SamplePojo>>(){}.getType();

    private static final String SERIALIZED_STRING_ERROR =
            "Make sure you don't have the same '@Serialized' string name declaration over 2 different variables. This will cause an exception. See this for more details: https://stackoverflow.com/questions/32367469/unable-to-create-converter-for-my-class-in-android-retrofit-library/42517143#42517143";


    public CustomConverterFactory() {
        super();
    }

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        if(type == PGMacTipsConstants.TYPE_BOOLEAN){
            //Boolean
            try {
                Converter<ResponseBody, ?> converter = new Converter<ResponseBody, Boolean>() {
                    @Override
                    public Boolean convert(ResponseBody value) throws IOException {
                        return Boolean.parseBoolean(value.string());
                    }
                };
                return converter;
            } catch (Exception e){
                e.printStackTrace();
            }

        } else if(type == PGMacTipsConstants.TYPE_DOUBLE){
            //Double
            try {
                Converter<ResponseBody, ?> converter = new Converter<ResponseBody, Double>() {
                    @Override
                    public Double convert(ResponseBody value) throws IOException {
                        return Double.parseDouble(value.string());
                    }
                };
                return converter;
            } catch (Exception e){
                e.printStackTrace();
            }

        } else if(type == PGMacTipsConstants.TYPE_INTEGER){
            //Integer
            try {
                Converter<ResponseBody, ?> converter = new Converter<ResponseBody, Integer>() {
                    @Override
                    public Integer convert(ResponseBody value) throws IOException {
                        return Integer.parseInt(value.string());
                    }
                };
                return converter;
            } catch (Exception e){
                e.printStackTrace();
            }

        } else if(type == (PGMacTipsConstants.TYPE_STRING)){
            //String
            try {
                Converter<ResponseBody, ?> converter = new Converter<ResponseBody, String>() {
                    @Override
                    public String convert(ResponseBody value) throws IOException {
                        return value.string();
                    }
                };
                return converter;
            } catch (Exception e){
                e.printStackTrace();
            }
        } else {
            try {
                Converter<ResponseBody, ?> gsonConverter = GsonConverterFactory
                        .create().responseBodyConverter(type, annotations, retrofit);
                return gsonConverter;
            } catch (Exception e){
                L.m(SERIALIZED_STRING_ERROR);
                e.printStackTrace();
            }
        }

        //If a catch gets hit
        return super.responseBodyConverter(type, annotations, retrofit);
    }

    @Override
    public Converter<?, RequestBody> requestBodyConverter(Type type,
                                                          Annotation[] parameterAnnotations,
                                                          Annotation[] methodAnnotations,
                                                          Retrofit retrofit) {
        Converter<?, RequestBody> gsonConverter = GsonConverterFactory
                .create().requestBodyConverter(type, parameterAnnotations,
                        methodAnnotations, retrofit);
        return gsonConverter;
    }

    @Override
    public Converter<?, String> stringConverter(Type type, Annotation[] annotations,
                                                Retrofit retrofit) {
        return super.stringConverter(type, annotations, retrofit);
    }
}
