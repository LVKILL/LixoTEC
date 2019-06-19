package com.example.rauber.lixotec.DAO;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Update;

import com.example.rauber.lixotec.Model.Administrador;

@Dao
public interface AdministradorDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Administrador administrador);

    @Update
    void update(Administrador administrador);

    @Delete
    void delete(Administrador administrador);
}
