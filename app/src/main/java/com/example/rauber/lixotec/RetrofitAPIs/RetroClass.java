package com.example.rauber.lixotec.RetrofitAPIs;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class RetroClass {

//    private String URL = "http://192.168.25.56/webtec/";
    private String URL = "https://ambientesvirtuais.com/lixotec/";
    private String URLCEP = "https://viacep.com.br/";

    public Retrofit getRetroClass(){
        OkHttpClient okHttpClient = new OkHttpClient();
        Retrofit.Builder retroBuilder = new Retrofit.Builder()
                .baseUrl(URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(okHttpClient);

        Retrofit retrofit = retroBuilder.build();
        return retrofit;
    }

    public Retrofit getRetroClassCep(){
        OkHttpClient okHttpClient = new OkHttpClient();
        Retrofit.Builder retroBuilder = new Retrofit.Builder()
                .baseUrl(URLCEP)
                .addConverterFactory(ScalarsConverterFactory.create())
                .client(okHttpClient);

        Retrofit retrofit = retroBuilder.build();
        return retrofit;
    }
}
