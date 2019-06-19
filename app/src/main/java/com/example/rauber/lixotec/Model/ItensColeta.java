package com.example.rauber.lixotec.Model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "ItensColeta",
    foreignKeys = {@ForeignKey(entity = Coleta.class, parentColumns = "id_coleta", childColumns = "id_coleta"),
                    @ForeignKey(entity = Item.class, parentColumns = "id_item", childColumns = "id_item")})
public class ItensColeta {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_itens_coleta")
    private int idItensColeta;

    @ColumnInfo(name = "id_item")
    private int idItem;

    @ColumnInfo(name = "id_coleta")
    private int idColeta;

    public ItensColeta(int idItensColeta, int idItem, int idColeta) {
        this.idItensColeta = idItensColeta;
        this.idItem = idItem;
        this.idColeta = idColeta;
    }

    public int getIdItensColeta() {
        return idItensColeta;
    }

    public void setIdItensColeta(int idItensColeta) {
        this.idItensColeta = idItensColeta;
    }

    public int getIdItem() {
        return idItem;
    }

    public void setIdItem(int idItem) {
        this.idItem = idItem;
    }

    public int getIdColeta() {
        return idColeta;
    }

    public void setIdColeta(int idColeta) {
        this.idColeta = idColeta;
    }
}
