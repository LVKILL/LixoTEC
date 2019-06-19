package com.example.rauber.lixotec.Model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "Usuario")
public class Usuario {

    @PrimaryKey
    @ColumnInfo(name = "id_usuario")
    private int idUsuario;

    @ColumnInfo(name = "login")
    private String login;

    @ColumnInfo(name = "senha")
    private String senha;

    @ColumnInfo(name = "email")
    private String email;

    @ColumnInfo(name = "telefone")
    private String phone;

    @ColumnInfo(name = "tipo_usuario")
    private int tipoUsuario;

    public Usuario(int idUsuario, String login, String senha, String email, String phone, int tipoUsuario) {
        this.idUsuario = idUsuario;
        this.login = login;
        this.senha = senha;
        this.email = email;
        this.phone = phone;
        this.tipoUsuario = tipoUsuario;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getTipoUsuario() {
        return tipoUsuario;
    }

    public void setTipoUsuario(int tipoUsuario) {
        this.tipoUsuario = tipoUsuario;
    }
}
