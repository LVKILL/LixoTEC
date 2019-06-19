package com.example.rauber.lixotec.Model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "Empresa",
        foreignKeys = @ForeignKey(entity = Usuario.class, parentColumns = "id_usuario", childColumns = "id_usuario"))
public class Empresa {

    @PrimaryKey
    @ColumnInfo(name = "id_empresa")
    private int idEmpresa;

    @ColumnInfo(name = "cnpj")
    private String CNPJ;

    @ColumnInfo(name = "razao_social")
    private String razaoSocial;

    @ColumnInfo(name = "nome_fantasia")
    private String nomeFantasia;

    @ColumnInfo(name = "id_usuario")
    private int idUsuario;

    public Empresa(int idEmpresa, String CNPJ, String razaoSocial, String nomeFantasia, int idUsuario) {
        this.idEmpresa = idEmpresa;
        this.CNPJ = CNPJ;
        this.razaoSocial = razaoSocial;
        this.nomeFantasia = nomeFantasia;
        this.idUsuario = idUsuario;
    }

    public int getIdEmpresa() {
        return idEmpresa;
    }

    public void setIdEmpresa(int idEmpresa) {
        this.idEmpresa = idEmpresa;
    }

    public String getCNPJ() {
        return CNPJ;
    }

    public void setCNPJ(String CNPJ) {
        this.CNPJ = CNPJ;
    }

    public String getRazaoSocial() {
        return razaoSocial;
    }

    public void setRazaoSocial(String razaoSocial) {
        this.razaoSocial = razaoSocial;
    }

    public String getNomeFantasia() {
        return nomeFantasia;
    }

    public void setNomeFantasia(String nomeFantasia) {
        this.nomeFantasia = nomeFantasia;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }
}
