package com.company.JitHub.Model;

import android.content.Context;

import com.company.JitHub.DAO.UsuarioDAO;
import com.google.gson.annotations.SerializedName;

public class Usuario {

    @SerializedName("id")
    private int _id;
    @SerializedName("nome")
    private String _nome;
    @SerializedName("senha")
    private String _senha;
    @SerializedName("email")
    public String _email;
    @SerializedName("grupo")
    public String _grupo;

    public int get_id() {
        return _id;
    }

    public String get_nome() {
        return _nome;
    }

    public String get_senha() {
        return _senha;
    }

    public String get_email() {
        return _email;
    }

    public String get_grupo() {
        return _grupo;
    }

    public Usuario(String login, String senha){
        _nome = login;
        _senha = senha;
    }

    public Usuario Get(Context contexto){

        Usuario usuario = new UsuarioDAO(contexto).GetUsuario(_nome, _senha);

        return usuario;
    }
}
