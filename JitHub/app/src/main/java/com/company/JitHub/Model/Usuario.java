package com.company.JitHub.Model;

import android.content.Context;

import com.company.JitHub.DAO.UsuarioDAO;

public class Usuario {

    private String _login;
    private String _senha;

    public Usuario(String login, String senha){
        _login = login;
        _senha = senha;
    }

    public Usuario Get(Context contexto){

        Usuario usuario = new UsuarioDAO(contexto).GetUsuario(_login, _senha);

        return usuario;
    }
}
