package com.pgmacdesign.pgmacutilities.networkclasses.retrofitutilities;

import com.google.gson.reflect.TypeToken;
import com.pgmacdesign.pgmacutilities.TESTINGPOJO;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.GsonConverterFactory;
import retrofit2.Retrofit;


/**
 * Created by pmacdowell on 11/7/2016.
 */

public class CustomConverterFactory extends Converter.Factory  {

    private static final Type TYPE_BOOLEAN = Boolean.TYPE;
    private static final Type TYPE_DOUBLE = Double.TYPE;
    private static final Type TYPE_INTEGER = Integer.TYPE;
    private static final Type TYPE_STRING = new TypeToken<String>(){}.getType();

    //How to make custom type converters
    private static final Type TYPE_EVENT = new TypeToken<TESTINGPOJO>(){}.getType();
    private static final Type TYPE_LIST_OF_EVENTS = new TypeToken<ArrayList<TESTINGPOJO>>(){}.getType();


    public CustomConverterFactory() {
        super();
    }

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        if(type ==TYPE_BOOLEAN){
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

        } else if(type == TYPE_DOUBLE){
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

        } else if(type == TYPE_INTEGER){
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

        } else if(type == (TYPE_STRING)){
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
            Converter<ResponseBody, ?> gsonConverter = GsonConverterFactory
                    .create().responseBodyConverter(type, annotations, retrofit);
            return gsonConverter;
        }

        //Else
        return super.responseBodyConverter(type, annotations, retrofit);
    }

    @Override
    public Converter<?, RequestBody> requestBodyConverter(Type type, Annotation[] parameterAnnotations, Annotation[] methodAnnotations, Retrofit retrofit) {
        return super.requestBodyConverter(type, parameterAnnotations, methodAnnotations, retrofit);
    }

    @Override
    public Converter<?, String> stringConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        return super.stringConverter(type, annotations, retrofit);
    }
}