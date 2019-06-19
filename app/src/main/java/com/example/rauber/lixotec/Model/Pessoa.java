package com.example.rauber.lixotec.Model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "Pessoa",
        foreignKeys = @ForeignKey(entity = Usuario.class, parentColumns = "id_usuario", childColumns = "id_usuario"))
public class Pessoa {

    @PrimaryKey
    @ColumnInfo(name = "id_pessoa")
    private int idPessoa;

    @ColumnInfo(name = "nome_completo")
    private String nomeCompleto;

    @ColumnInfo(name = "cpf")
    private String CPF;

    @ColumnInfo(name = "id_usuario")
    private int idUsuario;

    public Pessoa(int idPessoa, String nomeCompleto, String CPF, int idUsuario) {
        this.idPessoa = idPessoa;
        this.nomeCompleto = nomeCompleto;
        this.CPF = CPF;
        this.idUsuario = idUsuario;
    }

    public int getIdPessoa() {
        return idPessoa;
    }

    public void setIdPessoa(int idPessoa) {
        this.idPessoa = idPessoa;
    }

    public String getNomeCompleto() {
        return nomeCompleto;
    }

    public void setNomeCompleto(String nomeCompleto) {
        this.nomeCompleto = nomeCompleto;
    }

    public String getCPF() {
        return CPF;
    }

    public void setCPF(String CPF) {
        this.CPF = CPF;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }
}