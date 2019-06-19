package com.example.rauber.lixotec.DAO;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.example.rauber.lixotec.Model.Empresa;
import com.example.rauber.lixotec.Model.Pessoa;

import io.reactivex.Flowable;

@Dao
public interface EmpresaDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insert(Empresa empresa);

    @Update
    void update(Empresa empresa);

    @Delete
    void delete(Empresa empresa);

    @Query("SELECT * FROM Empresa WHERE id_usuario = :id_usuario")
    Flowable<Empresa> getEmpresaById(int id_usuario);


}
