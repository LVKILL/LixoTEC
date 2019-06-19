package com.example.rauber.lixotec.Repository;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.LiveDataReactiveStreams;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.Observer;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.rauber.lixotec.DAO.EnderecoDAO;
import com.example.rauber.lixotec.Database.LixoTecDatabase;
import com.example.rauber.lixotec.Model.Endereco;
import com.example.rauber.lixotec.RetrofitAPIs.LixotecAPI;
import com.example.rauber.lixotec.RetrofitAPIs.RetroClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class EnderecoRepository {

    private EnderecoDAO enderecoDAO;
    private RetroClass retroClass;
    private List<Endereco> addressList;
    private LiveData<List<Endereco>> allAddressDatabase;
    private LiveData<Endereco> uniqueAddress;
    private MediatorLiveData<Long> lastInsert = new MediatorLiveData<>();
    private MediatorLiveData<String> coletaPoints = new MediatorLiveData<>();
    private MediatorLiveData<String> deleteColetaPointMessage = new MediatorLiveData<>();
    private MediatorLiveData<String> userAddresses = new MediatorLiveData<>();
    private LixotecAPI lixotecAPI;

    public EnderecoRepository(Application application) {
        LixoTecDatabase lixoTecDatabase = LixoTecDatabase.getInstance(application);
        this.enderecoDAO = lixoTecDatabase.enderecoDAO();
        retroClass = new RetroClass();
        allAddressDatabase = enderecoDAO.allAddress();
        retroClass = new RetroClass();
        lixotecAPI = retroClass.getRetroClass().create(LixotecAPI.class);
    }

    public void deleteColetaPoint(String json) {
        final LiveData<String> source = LiveDataReactiveStreams.fromPublisher(
                lixotecAPI.excluirPontoColeta(json)
                .subscribeOn(Schedulers.io())
                .onErrorReturn(throwable -> createErrorJson())
        );

        deleteColetaPointMessage.addSource(source, (String s) -> {
            if(s != null){
                deleteColetaPointMessage.setValue(s);
                deleteColetaPointMessage.removeSource(source);
            }
        });

    }

    public LiveData<String> getDeleteColetaPointMessage(){
        return this.deleteColetaPointMessage;
    }

    public void loadColetaPoints(String json){
        final LiveData<String> source = LiveDataReactiveStreams.fromPublisher(
                lixotecAPI.consultarColetaPoints(json)
                .subscribeOn(Schedulers.io())
                .onErrorReturn(throwable -> createErrorJson())
        );

        coletaPoints.addSource(source, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                if(s != null){
                   coletaPoints.setValue(s);
                   coletaPoints.removeSource(source);
                }
            }
        });

    }

    public LiveData<String> observableColetaPoints(){
        return this.coletaPoints;
    }

    public LiveData<List<Endereco>> getAllAddressDatabase(){
        return this.allAddressDatabase;
    }

    public LiveData<List<Endereco>> getAllAddressByUser(int id){
        return enderecoDAO.allAddressByUser(id);
    }

    public void insert(Endereco endereco){
        InsertAsyncTask insertAsyncTask = new InsertAsyncTask(enderecoDAO);
        insertAsyncTask.execute(endereco);
    }

    public LiveData<Long> observeIds(){
        return lastInsert;
    }

     public void update(Endereco endereco){
        UpdateAsyncTask updateAsyncTask = new UpdateAsyncTask(enderecoDAO);
        updateAsyncTask.execute(endereco);
    }

    public void delete(Endereco endereco){
        DeleteAsyncTask deleteAsyncTask = new DeleteAsyncTask(enderecoDAO);
        deleteAsyncTask.execute(endereco);
    }

    public void loadUserAddress(String json){

        final LiveData<String> source = LiveDataReactiveStreams.fromPublisher(
                lixotecAPI.getUserAddress(json)
                .subscribeOn(Schedulers.io())
                .onErrorReturn(throwable -> createErrorJson())
        );

        userAddresses.addSource(source, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                if(s != null){
                    //convertJsonAddress(s);
                    userAddresses.setValue(s);
                    userAddresses.removeSource(source);
                }
            }
        });
   }

   public LiveData<String> getUserAddresses(){
        return this.userAddresses;
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

    class InsertAsyncTask extends AsyncTask<Endereco, Void, Void>{

        private EnderecoDAO enderecoDAO;

        public InsertAsyncTask(EnderecoDAO enderecoDAO) {
            this.enderecoDAO = enderecoDAO;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Endereco... enderecos) {
            Log.v("tag","esta inserindo endereço do mano");
            Log.v("tag", "endereco dados : "+enderecos[0].getBairro()  +  " " + enderecos[0].getIdUsuario() + " " + enderecos[0].getIdEndereco());
            enderecoDAO.insert(enderecos[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }

    class UpdateAsyncTask extends AsyncTask<Endereco, Void, Void>{

        private EnderecoDAO enderecoDAO;

        public UpdateAsyncTask(EnderecoDAO enderecoDAO) {
            this.enderecoDAO = enderecoDAO;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }

        @Override
        protected Void doInBackground(Endereco... enderecos) {
            enderecoDAO.update(enderecos[0]);
            return null;
        }
    }

    class DeleteAsyncTask extends AsyncTask<Endereco, Void, Void>{

        private EnderecoDAO enderecoDAO;

        public DeleteAsyncTask(EnderecoDAO enderecoDAO) {
            this.enderecoDAO = enderecoDAO;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Endereco... enderecos) {
            enderecoDAO.delete(enderecos[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }
}