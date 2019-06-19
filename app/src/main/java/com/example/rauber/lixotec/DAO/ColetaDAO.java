package com.example.rauber.lixotec.DAO;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.example.rauber.lixotec.Model.Coleta;

import java.util.List;

@Dao
public interface ColetaDAO {

    @Insert
    void insert(Coleta coleta);

    @Update
    void update(Coleta coleta);

    @Delete
    void delete(Coleta coleta);

    @Query("SELECT * FROM COLETA WHERE realizada = 0")
    LiveData<List<Coleta>> allColetas();

    @Query("SELECT * FROM COLETA where id_usuario = :id_usuario")
    LiveData<List<Coleta>> allColetaByUser(int id_usuario);

}