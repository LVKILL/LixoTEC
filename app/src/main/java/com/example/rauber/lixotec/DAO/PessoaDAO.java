package com.example.rauber.lixotec.DAO;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.example.rauber.lixotec.Model.Pessoa;
import com.example.rauber.lixotec.Model.Usuario;

import io.reactivex.Flowable;

@Dao
public interface PessoaDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insert(Pessoa pessoa);

    @Query("SELECT * FROM PESSOA WHERE id_usuario = :id_usuario")
    Flowable<Pessoa> getUserById(int id_usuario);


    @Update
    void update(Pessoa pessoa);

    @Delete
    void delete(Pessoa pessoa);


}
