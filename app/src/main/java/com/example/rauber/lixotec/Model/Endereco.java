package com.example.rauber.lixotec.Model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "Endereco",
    foreignKeys = @ForeignKey(entity = Usuario.class, parentColumns = "id_usuario",childColumns = "id_usuario"))
public class Endereco {

    @PrimaryKey
    @ColumnInfo(name = "id_endereco")
    private int idEndereco;

    @ColumnInfo(name = "id_usuario")
    private int idUsuario;

    @ColumnInfo(name = "logradouro")
    private String logradouro;

    @ColumnInfo(name = "numero")
    private String numero;

    @ColumnInfo(name = "cep")
    private String CEP;

    @ColumnInfo(name = "bairro")
    private String bairro;

    @ColumnInfo(name = "is_ponto_coleta")
    private boolean isPontoColeta;

    @ColumnInfo(name = "complemento")
    private String complemento;



    public Endereco(int idEndereco, int idUsuario, String logradouro, String numero, String CEP, String bairro, boolean isPontoColeta, String complemento) {
        this.idEndereco = idEndereco;
        this.idUsuario = idUsuario;
        this.logradouro = logradouro;
        this.numero = numero;
        this.CEP = CEP;
        this.bairro = bairro;
        this.isPontoColeta = isPontoColeta;
        this.complemento = complemento;
    }

    public int getIdEndereco() {
        return idEndereco;
    }

    public void setIdEndereco(int idEndereco) {
        this.idEndereco = idEndereco;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getLogradouro() {
        return logradouro;
    }

    public void setLogradouro(String logradouro) {
        this.logradouro = logradouro;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getCEP() {
        return CEP;
    }

    public void setCEP(String CEP) {
        this.CEP = CEP;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public boolean isPontoColeta() {
        return isPontoColeta;
    }

    public void setPontoColeta(boolean pontoColeta) {
        isPontoColeta = pontoColeta;
    }

    public String getComplemento() {
        return complemento;
    }

    public void setComplemento(String complemento) {
        this.complemento = complemento;
    }
}
