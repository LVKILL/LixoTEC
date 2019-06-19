package com.example.rauber.lixotec.DAO;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.example.rauber.lixotec.Model.Endereco;

import java.util.List;

import io.reactivex.Flowable;

@Dao
public interface EnderecoDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Endereco endereco);

    @Update
    void update(Endereco endereco);

    @Delete
    void delete(Endereco endereco);

    @Query("SELECT * FROM Endereco WHERE id_usuario = :id_usuario")
    LiveData<List<Endereco>> allAddressByUser(int id_usuario);

    @Query("SELECT * FROM Endereco")
    LiveData<List<Endereco>> allAddress();

}