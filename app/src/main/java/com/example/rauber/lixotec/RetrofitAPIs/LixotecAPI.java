package com.example.rauber.lixotec.RetrofitAPIs;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface LixotecAPI {

    @POST("/lixotec/Cadastros.php")
    Call<Void> cadastrarUsuario(@Body String json);

    @POST("/lixotec/Cadastros.php")
    Flowable<String> registrarUsuario(@Body String json);

    @POST("/lixotec/Login.php")
    Flowable<String> realizarLogin(@Body String json);

    @POST("/lixotec/Consultas.php")
    Flowable<String> getUserAddress(@Body String json);

    @POST("/lixotec/Coletas.php")
    Flowable<String> consultaHorarios(@Body String json);

    @POST("/lixotec/Coletas.php")
    Flowable<String> registrarColeta(@Body String json);

    @POST("/lixotec/Cadastros.php")
    Call<Void> alterarDadosUsuario(@Body String json);

    @POST("/lixotec/Coletas.php")
    Flowable<String> buscarColeta(@Body String json);

    @POST("/lixotec/Coletas.php")
    Call<Void> alterarCaminhoImagem(@Body String json);

    @Multipart
    @POST("/lixotec/uploadImage.php")
    Call<String> uploadImage(@Part MultipartBody.Part image, @Part("json") RequestBody idColeta);

    @POST("/lixotec/Consultas.php")
    Flowable<String> consultarColetaPoints(@Body String JSON);

    @POST("/lixotec/Coletas.php")
    Flowable<String> consultarColetasDiaEspecifico(@Body String json);

    @POST("/lixotec/Consultas.php")
    Flowable<String> consultarUsername(@Body String json);

    @POST("/lixotec/Coletas.php")
    Flowable<String> realizarColeta(@Body String json);

    @POST("/lixotec/Exclusao.php")
    Flowable<String> excluirPontoColeta(@Body String json);

    @POST("/lixotec/Consultas.php")
    Flowable<String> consultarHistoricoDeColetas(@Body String json);
}