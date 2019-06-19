package com.example.rauber.lixotec.Repository;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.LiveDataReactiveStreams;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.Observer;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.rauber.lixotec.DAO.ColetaDAO;
import com.example.rauber.lixotec.Database.LixoTecDatabase;
import com.example.rauber.lixotec.Model.Coleta;
import com.example.rauber.lixotec.RetrofitAPIs.LixotecAPI;
import com.example.rauber.lixotec.RetrofitAPIs.RetroClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ColetaRepository {

    private ColetaDAO coletaDAO;
    private LiveData<List<Coleta>> allColetaByUser;
    private LiveData<List<Coleta>> allColetas;
    private RetroClass retroClass;
    private LixotecAPI lixotecAPI;
    private MediatorLiveData<String> coletaResponse = new MediatorLiveData<>();
    private MediatorLiveData<String> freeHours = new MediatorLiveData<>();
    private MediatorLiveData<String> registerResponse = new MediatorLiveData<>();
    private MediatorLiveData<String> coletaDiaEspecifico = new MediatorLiveData<>();
    private MediatorLiveData<Throwable> throwableMediatorLiveData = new MediatorLiveData<>();
    private MediatorLiveData<String> retornoColetaRealizada = new MediatorLiveData<>();
    private MediatorLiveData<String> coletasHistory = new MediatorLiveData<>();

    public ColetaRepository(Application application) {
        LixoTecDatabase lixoTecDatabase = LixoTecDatabase.getInstance(application);
        coletaDAO = lixoTecDatabase.coletaDAO();
        allColetas = coletaDAO.allColetas();
        retroClass = new RetroClass();
        lixotecAPI = retroClass.getRetroClass().create(LixotecAPI.class);

    }


    public void loadColetasHistory(String json){
        final LiveData<String> source = LiveDataReactiveStreams.fromPublisher(
                lixotecAPI.consultarHistoricoDeColetas(json)
                .subscribeOn(Schedulers.io())
                .onErrorReturn(throwable -> createErrorJson())

        );

        coletasHistory.addSource(source, (String s) -> {
            if(s != null) {
                coletasHistory.setValue(s);
                coletasHistory.removeSource(source);
            }
        });
    }

    public LiveData<String> getColetasHistory(){
        return this.coletasHistory;
    }

    public void performColeta(String json) {
        final LiveData<String> source = LiveDataReactiveStreams.fromPublisher(
                lixotecAPI.realizarColeta(json)
                .subscribeOn(Schedulers.io())
                .onErrorReturn(throwable -> createErrorJson())
        );

        retornoColetaRealizada.addSource(source, (@Nullable String s) -> {
                 if( s != null ){
                    retornoColetaRealizada.setValue(s);
                    retornoColetaRealizada.removeSource(source);
                }
        });

    }

    public LiveData<String> getColetaRealizadaResponse(){
        return this.retornoColetaRealizada;
    }



    public void searchColetaByDay(String json){
        final LiveData<String> source = LiveDataReactiveStreams.fromPublisher(
                lixotecAPI.consultarColetasDiaEspecifico(json)
                .subscribeOn(Schedulers.io())
                .onErrorReturn(throwable -> createErrorJson())
        );


        coletaDiaEspecifico.addSource(source, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                if (s != null) {
                    coletaDiaEspecifico.setValue(s);
                    coletaDiaEspecifico.removeSource(source);
                }
            }
        });
    }


    public LiveData<String> getColetasByDay(){
        return coletaDiaEspecifico;
    }

    public void sincronizarColeta(String json){
        final LiveData<String> source = LiveDataReactiveStreams.fromPublisher(
                lixotecAPI.buscarColeta(json)
                .subscribeOn(Schedulers.io())
                .onErrorReturn(throwable -> createErrorJson())

        );

        coletaResponse.addSource(source, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                if ( s != null){
                    convertJsonColeta(s);
                    coletaResponse.setValue(s);
                    coletaResponse.removeSource(source);
                }else{
                    Log.v("tag","Erro de requisição");
                }
            }

        });
    }

    private String createErrorJson() {
        String resultTest = "";

        try{
            JSONArray jsonArray = new JSONArray();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("codigo","600");
            jsonObject.put("mensagem","Erro ao conectar com o webservice");
            jsonObject.put("quantidade", "0");
            jsonArray.put(jsonObject);
            resultTest = jsonArray.toString();
        }catch (JSONException je){
            Log.v("tag","Error in createErrorJson() in ColetaRepository " +je.getMessage());
        }
        return resultTest;
    }

    public void uploadImage(File file, final String jsonIdColeta)throws IOException {

        MultipartBody.Part partImage = MultipartBody.Part.createFormData("imageupload", file.getName(), RequestBody.create(MediaType.parse("multipart/form-file"), file));
        RequestBody reqBody = RequestBody.create(MediaType.parse("multipart/form-data"), jsonIdColeta);
        Log.v("tag"," requestBody in uploadImage  = "+reqBody.toString()+ reqBody.contentLength() + " "+reqBody.contentType());
        lixotecAPI.uploadImage(partImage, reqBody).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.v("tag","full response " +response);
                Log.v("tag","onResponse "+response.isSuccessful()+ " in uploadImage on ColetaRepository.uploadImage()");
                Log.v("tag","Response from api "+response.body());
              if(response.isSuccessful())
                    convertImageResponse(response.body(), jsonIdColeta);
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.v("tag"," error in uploadImage() in ColetaRepository "+t.getMessage());
            }
        });

    }

    private void convertImageResponse(String jsonFromResponse, String jsonWithId){
        String jsonResponse = "";

        try{
            JSONObject jsonObject = new JSONObject(jsonFromResponse);
            int codigo = jsonObject.getInt("codigo");
            if(codigo == 0){
                jsonResponse = "erro";
             }else {
                JSONObject jsonFromApp = new JSONObject(jsonWithId);

                String idColeta = jsonFromApp.getString("idColeta");
                String imagePath = jsonObject.getString("nomeArquivo");

                JSONObject jsonToUpdatePath = new JSONObject();
                jsonToUpdatePath.put("idColeta",idColeta);
                jsonToUpdatePath.put("nomeArquivo", imagePath);
                jsonToUpdatePath.put("acao","alterarCaminhoImagem");
                jsonResponse = jsonToUpdatePath.toString();
            }
        }catch(JSONException je){
            Log.v("TAG","Error on convertImageResponse() in ColetaRepository "+je.getMessage());
        }

        alterColetaImgPath(jsonResponse);
    }

    private void alterColetaImgPath(final String jsonPath){
        if(!jsonPath.equals("erro")){
            lixotecAPI.alterarCaminhoImagem(jsonPath).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if(response.isSuccessful()){
                        Log.v("tag","jsonpath = "+jsonPath);
                        Log.v("tag","Sucess on updating image path "+response.isSuccessful());
                }else{
                        Log.v("tag","alterColetaImgPath not successfull "+response.body());
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Log.v("tag","Error on alterColetaImgPath() "+t.getMessage());
                }
            });
        }
    }

    private void convertJsonColeta(String coletaJson) {
        try{
            JSONArray jsonArray = new JSONArray(coletaJson);

            JSONObject jsonObject = jsonArray.getJSONObject(0);
            int idColeta = Integer.parseInt(jsonObject.getString("id_coleta"));
            int idUsuario = Integer.parseInt(jsonObject.getString("id_usuario"));
            String datahora = jsonObject.getString("data_hora");
            boolean realizada = (jsonObject.getString("realizada").equals("1"));
            String coletaImagem = jsonObject.getString("coleta_imagem");

            Coleta coleta = new Coleta(idColeta, idUsuario, datahora, realizada, coletaImagem);
            insert(coleta);

        }catch (JSONException je){
            Log.v("TAG","Error on convertJsonColeta() in ColetaRepository.class "+je.getMessage());
        }

    }

    public void registerColeta(String json){
        final LiveData<String> source = LiveDataReactiveStreams.fromPublisher(
                lixotecAPI.registrarColeta(json)
                .subscribeOn(Schedulers.io())
                .onErrorReturn(throwable -> createErrorJson())

        );

        registerResponse.addSource(source, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                if(s != null){
                    registerResponse.setValue(s);
                    registerResponse.removeSource(source);
                }
            }
        });
    }

    public void seekHours(String json){
        final LiveData<String> source = LiveDataReactiveStreams.fromPublisher(
                lixotecAPI.consultaHorarios(json)
                .subscribeOn(Schedulers.io())
                .onErrorReturn(throwable -> createErrorJson())
        );

        freeHours.addSource(source, new android.arch.lifecycle.Observer<String>(){
            @Override
            public void onChanged(@Nullable String s) {
                if(s != null) {
                    freeHours.setValue(s);
                    freeHours.removeSource(source);
                }
            }
        });

    }

    public LiveData<String> observeColetaResponse(){
        return registerResponse;
    }

    public LiveData<String> observeHours(){
        return freeHours;
    }

    public void insert(Coleta coleta){
        InsertAsyncTask insertAsyncTask = new InsertAsyncTask(coletaDAO);
        insertAsyncTask.execute(coleta);
    }

    public void update(Coleta coleta){
        UpdateAsyncTask updateAsyncTask = new UpdateAsyncTask(coletaDAO);
        updateAsyncTask.execute(coleta);
    }

    public void delete(Coleta coleta){
        DeleteAsyncTask deleteAsyncTask = new DeleteAsyncTask(coletaDAO);
        deleteAsyncTask.execute(coleta);
    }

    public LiveData<List<Coleta>> getAllColetaByUser(int id){
        allColetaByUser = coletaDAO.allColetaByUser(id);
        return allColetaByUser;
    }

    public LiveData<List<Coleta>> getAllColetas() {
        return allColetas;
    }


    public

    class InsertAsyncTask extends AsyncTask<Coleta, Void, Void>{

        private ColetaDAO coletaDAO;

        public InsertAsyncTask(ColetaDAO coletaDAO) {
            this.coletaDAO = coletaDAO;
        }

        @Override
        protected Void doInBackground(Coleta... coletas) {
            coletaDAO.insert(coletas[0]);
            return null;
        }
    }

    class UpdateAsyncTask extends AsyncTask<Coleta, Void, Void>{

        private ColetaDAO coletaDAO;

        public UpdateAsyncTask(ColetaDAO coletaDAO) {
            this.coletaDAO = coletaDAO;
        }

        @Override
        protected Void doInBackground(Coleta... coletas) {
            coletaDAO.update(coletas[0]);
            return null;
        }
    }

    class DeleteAsyncTask extends AsyncTask<Coleta, Void, Void>{

        private ColetaDAO coletaDAO;

        public DeleteAsyncTask(ColetaDAO coletaDAO) {
            this.coletaDAO = coletaDAO;
        }

        @Override
        protected Void doInBackground(Coleta... coletas) {
            coletaDAO.delete(coletas[0]);
            return null;
        }
    }


}
