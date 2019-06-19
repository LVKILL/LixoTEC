package com.example.rauber.lixotec.Model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "Metragem")
public class Metragem {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_metragem")
    private int idMetragem;

    @ColumnInfo(name = "descricao")
    private String descricao;

    public Metragem(int idMetragem, String descricao) {
        this.idMetragem = idMetragem;
        this.descricao = descricao;
    }

    public int getIdMetragem() {
        return idMetragem;
    }

    public void setIdMetragem(int idMetragem) {
        this.idMetragem = idMetragem;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
