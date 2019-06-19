package com.example.rauber.lixotec.Model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "Item",
        foreignKeys = @ForeignKey(entity = Metragem.class, parentColumns = "id_metragem", childColumns = "id_metragem"))
public class Item {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_item")
    private int idItem;

    @ColumnInfo(name = "id_metragem")
    private int idMetragem;

    @ColumnInfo(name = "nome")
    private String nome;

    @ColumnInfo(name = "valor")
    private Double valor;

    public Item(int idItem, int idMetragem, String nome, Double valor) {
        this.idItem = idItem;
        this.idMetragem = idMetragem;
        this.nome = nome;
        this.valor = valor;
    }

    public int getIdItem() {
        return idItem;
    }

    public void setIdItem(int idItem) {
        this.idItem = idItem;
    }

    public int getIdMetragem() {
        return idMetragem;
    }

    public void setIdMetragem(int idMetragem) {
        this.idMetragem = idMetragem;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Double getValor() {
        return valor;
    }

    public void setValor(Double valor) {
        this.valor = valor;
    }
}
