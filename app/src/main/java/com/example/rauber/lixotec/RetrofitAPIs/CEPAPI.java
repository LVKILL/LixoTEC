package com.example.rauber.lixotec.RetrofitAPIs;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface CEPAPI {

    @GET("/ws/{CEP}/json/")
    Call<String> validateCEP(@Path("CEP") String cep);

}
