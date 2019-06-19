package com.example.rauber.lixotec.Model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "Coleta",
    foreignKeys = @ForeignKey(entity = Usuario.class, parentColumns = "id_usuario", childColumns = "id_usuario"))
public class Coleta {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_coleta")
    private int idColeta;

    @ColumnInfo(name = "id_usuario")
    private int idUsuario;

    @ColumnInfo(name = "data_hora")
    private String dataHora;

    @ColumnInfo(name = "realizada")
    private Boolean realizada;

    @ColumnInfo(name = "coleta_imagem")
    private String coletaImagem;

    @Ignore
    private String nome;

    @Ignore
    private Endereco endereco;


    public Coleta(int idColeta, int idUsuario, String dataHora, Boolean realizada, String coletaImagem) {
        this.idColeta = idColeta;
        this.idUsuario = idUsuario;
        this.dataHora = dataHora;
        this.realizada = realizada;
        this.coletaImagem = coletaImagem;
    }

    public String getColetaImagem() {
        return coletaImagem;
    }

    public void setColetaImagem(String coletaImagem) {
        this.coletaImagem = coletaImagem;
    }

    public int getIdColeta() {
        return idColeta;
    }

    public void setIdColeta(int idColeta) {
        this.idColeta = idColeta;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getDataHora() {
        return dataHora;
    }

    public void setDataHora(String dataHora) {
        this.dataHora = dataHora;
    }

    public Boolean getRealizada() {
        return realizada;
    }

    public void setRealizada(Boolean realizada) {
        this.realizada = realizada;
    }

    @Ignore
    public void setEndereco(Endereco end){
        this.endereco = end;
    }
    @Ignore
    public Endereco getEndereco(){
        return this.endereco;
    }

    @Ignore
    public void setNome(String nome){
        this.nome = nome;
    }

    @Ignore
    public String getNome(){
        return this.nome;
    }
}
