package com.example.rauber.lixotec.Model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "TipoUsuario")
public class TipoUsuario {

    @PrimaryKey
    @ColumnInfo(name = "id_tipo_usuario")
    private int idTipoUsuario;

    @ColumnInfo(name = "tipo_usuario")
    private String tipoUsuario;

    public TipoUsuario(int idTipoUsuario, String tipoUsuario) {
        this.idTipoUsuario = idTipoUsuario;
        this.tipoUsuario = tipoUsuario;
    }

    public int getIdTipoUsuario() {
        return idTipoUsuario;
    }

    public void setIdTipoUsuario(int idTipoUsuario) {
        this.idTipoUsuario = idTipoUsuario;
    }

    public String getTipoUsuario() {
        return tipoUsuario;
    }

    public void setTipoUsuario(String tipoUsuario) {
        this.tipoUsuario = tipoUsuario;
    }
}
