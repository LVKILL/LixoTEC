package com.example.rauber.lixotec.DAO;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.example.rauber.lixotec.Model.Usuario;

import java.util.List;

import io.reactivex.Flowable;

@Dao
public interface UsuarioDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertUser(Usuario usuario);

    @Update
    void update(Usuario usuario);

    @Delete
    void delete(Usuario usuario);

    @Query("SELECT * FROM Usuario")
    LiveData<List<Usuario>> allUsers();


    @Query("SELECT * FROM Usuario where id_usuario = :id")
    Flowable<Usuario> getUserById(int id);


    @Query("UPDATE usuario SET email = :email AND telefone = :phone WHERE id_usuario = :id_usuario")
    void updateUser(int id_usuario, String email, String phone);
}