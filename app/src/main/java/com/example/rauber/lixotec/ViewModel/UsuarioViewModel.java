package com.example.rauber.lixotec.ViewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.example.rauber.lixotec.Model.Empresa;
import com.example.rauber.lixotec.Model.Pessoa;
import com.example.rauber.lixotec.Model.Usuario;
import com.example.rauber.lixotec.Repository.UsuarioRepository;

import java.util.List;

public class UsuarioViewModel extends AndroidViewModel {

    private UsuarioRepository usuarioRepository;
    private LiveData<List<Usuario>> allUser;

    public UsuarioViewModel(@NonNull Application application) {
        super(application);
        usuarioRepository = new UsuarioRepository(application);
        allUser = usuarioRepository.getAllUsuario();
    }

    public void checkUsername(String json){
        usuarioRepository.checkUsername(json);
    }

    public LiveData<String> getCheckUsername(){
        return usuarioRepository.getCheckUsername();
    }

    public void registerWithJson(String json){
        usuarioRepository.registerWithJson(json);
    }

    public LiveData<String> observeRegisterResponse(){
        return usuarioRepository.observeRegisterResponse();
    }

    public void loadUser(int id){
        usuarioRepository.loadUser(id);
    }

    public LiveData<Usuario> getUser(){
        return usuarioRepository.getUser();
    }

    public void loadUserPerson(int id){
        usuarioRepository.loadUserPerson(id);
    }

    public LiveData<Pessoa> getUserPessoa(){
        return usuarioRepository.getUserPessoaById();
    }

    public void loadCompany(int id){
        usuarioRepository.loadUserCompany(id);
    }

    public LiveData<Empresa> getCompany(){
        return usuarioRepository.getCompany();
    }

    public void registerUser(String json){
        usuarioRepository.registerUser(json);
    }

    public void authenticateWithJson(String json){
        usuarioRepository.authenticateWithJson(json);
    }

    public LiveData<String> observeLogin(){
        return usuarioRepository.observeLogin();
    }

    public void insert(Usuario usuario){
      usuarioRepository.insertUser(usuario);
    }

    public void delete(Usuario usuario){
        usuarioRepository.delete(usuario);
    }

    public LiveData<List<Usuario>> getAllUser() {
        return allUser;
    }

    public void updateUser(Usuario usuario){
        usuarioRepository.updateUser(usuario);
    }

    public void updateUserOnWebservice(String json){
        usuarioRepository.updateUserOnWebservice(json);
    }

}
