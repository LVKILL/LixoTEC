package com.example.rauber.lixotec.ViewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.example.rauber.lixotec.Model.Endereco;
import com.example.rauber.lixotec.Repository.EnderecoRepository;

import java.util.List;

public class EnderecoViewModel extends AndroidViewModel {

    private EnderecoRepository enderecoRepository;
    private List<Endereco> addressList;
    private LiveData<List<Endereco>> adressListDatabase;

    public EnderecoViewModel(@NonNull Application application) {
        super(application);
        enderecoRepository = new EnderecoRepository(application);
        adressListDatabase = enderecoRepository.getAllAddressDatabase();
    }

    public LiveData<List<Endereco>> getAllAdressListDatabase(){
        return this.adressListDatabase;
    }

    public LiveData<List<Endereco>> getAllAdressByUser(int userId){
        return enderecoRepository.getAllAddressByUser(userId);
    }

    public void deleteColetaPoint(String json){
        enderecoRepository.deleteColetaPoint(json);
    }

    public LiveData<String> getDeleteColetaPointMessage(){
        return enderecoRepository.getDeleteColetaPointMessage();
    }


    public void loadColetaPoints(String json){
        enderecoRepository.loadColetaPoints(json);
    }

    public LiveData<String> observeColetaPoints(){
        return enderecoRepository.observableColetaPoints();
    }
//
//    public LiveData<Long> observeIds(){
//        return enderecoRepository.observeIds();
//    }

    public void insert(Endereco endereco){
        enderecoRepository.insert(endereco);
    }

    public void update(Endereco endereco){
        enderecoRepository.update(endereco);
    }

    public void delete(Endereco endereco){
        enderecoRepository.delete(endereco);
    }

    public void loadAddress(String json){
        enderecoRepository.loadUserAddress(json);
    }

    public LiveData<String> getUserAddresses(){
        return enderecoRepository.getUserAddresses();
    }
}
