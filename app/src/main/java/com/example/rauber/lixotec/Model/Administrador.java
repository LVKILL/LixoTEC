package com.example.rauber.lixotec.Model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "Administrador",
    foreignKeys = @ForeignKey(entity = Usuario.class, parentColumns = "id_usuario", childColumns = "id_usuario"))
public class Administrador {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_administrador")
    private int idAdministrador;

    @ColumnInfo(name = "is_sub_administrador")
    private boolean isSubAdministrador;

    @ColumnInfo(name = "id_usuario")
    private int idUsuario;

    public Administrador(int idAdministrador, boolean isSubAdministrador, int idUsuario) {
        this.idAdministrador = idAdministrador;
        this.isSubAdministrador = isSubAdministrador;
        this.idUsuario = idUsuario;
    }

    public int getIdAdministrador() {
        return idAdministrador;
    }

    public void setIdAdministrador(int idAdministrador) {
        this.idAdministrador = idAdministrador;
    }

    public boolean isSubAdministrador() {
        return isSubAdministrador;
    }

    public void setSubAdministrador(boolean subAdministrador) {
        isSubAdministrador = subAdministrador;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }
}
