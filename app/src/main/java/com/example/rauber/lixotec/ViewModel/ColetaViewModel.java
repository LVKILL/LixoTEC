package com.example.rauber.lixotec.ViewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.rauber.lixotec.Model.Coleta;
import com.example.rauber.lixotec.Repository.ColetaRepository;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class ColetaViewModel extends AndroidViewModel {

    private ColetaRepository coletaRepository;
    private LiveData<List<Coleta>> allColetas;

    public ColetaViewModel(@NonNull Application application) {
        super(application);
        coletaRepository = new ColetaRepository(application);
        allColetas = coletaRepository.getAllColetas();
    }

    public void loadColetasHistory(String json){
        coletaRepository.loadColetasHistory(json);
    }

    public LiveData<String> getColetasHistory(){
        return coletaRepository.getColetasHistory();
    }

    public void performColeta(String json){
        coletaRepository.performColeta(json);
    }

    public LiveData<String> getPerformColetaResponse(){
        return coletaRepository.getColetaRealizadaResponse();
    }

    public void searchColetaByDay(String json){
        coletaRepository.searchColetaByDay(json);
    }

    public LiveData<String> getColetaByDay(){
        return coletaRepository.getColetasByDay();
    }

    public void registerColeta(String json){
        coletaRepository.registerColeta(json);
    }

    public LiveData<String> observeColetaResponse(){
        return coletaRepository.observeColetaResponse();
    }

    public void sincronizarColeta(String json){
        coletaRepository.sincronizarColeta(json);
    }

    public void uploadImage(File file, String jsonIdColeta)throws IOException {
        coletaRepository.uploadImage(file, jsonIdColeta);
    }

    public void seekHours(String json){
        coletaRepository.seekHours(json);
    }

    public LiveData<String> observeHours(){
        return coletaRepository.observeHours();
    }

    public void insert(Coleta coleta){
        coletaRepository.insert(coleta);
    }

    public void update(Coleta coleta){
        coletaRepository.update(coleta);
    }

    public void delete(Coleta coleta){
        coletaRepository.delete(coleta);
    }

    public LiveData<List<Coleta>> getAllColetas() {
        return allColetas;
    }

    public LiveData<List<Coleta>> getAllColetasByUser(int id) {
        return coletaRepository.getAllColetaByUser(id);
    }

}