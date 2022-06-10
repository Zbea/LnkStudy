package com.bll.lnkstudy.net;

import com.google.gson.Gson;

import retrofit2.Converter;

public class GsonDConverterFactory extends Converter.Factory {

    public static GsonDConverterFactory create() {
        return create(new Gson());
    }

    public static GsonDConverterFactory create(Gson gson) {
        return new GsonDConverterFactory(gson);
    }

    private final Gson gson;

    private GsonDConverterFactory(Gson gson) {
        if (gson == null) throw new NullPointerException("gson == null");
        this.gson = gson;
    }
//
//    @Override public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
//        return new GsonResponseBodyConverter < >(gson, type);
//    }
//
//    @Override public Converter < ?, RequestBody> requestBodyConverter(Type type, Annotation[] parameterAnnotations, Annotation[] methodAnnotations, Retrofit retrofit) {
//        TypeAdapter< ?> adapter = gson.getAdapter(TypeToken.get(type));
//        return new GsonRequestBodyConverter < >(gson, adapter);
//    }

}
