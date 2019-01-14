package com.company.JitHub.DAO;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.company.JitHub.Model.Usuario;

public class UsuarioDAO{

    private Context _contexto;

    public UsuarioDAO(Context context){
        _contexto = context;
    }

    public Usuario GetUsuario(String login, String senha){

        String selectQuery = "SELECT * FROM Logins WHERE Senha = '" + senha + "' AND Login = '" + login + "'";

        DatabaseHelper db = new DatabaseHelper(_contexto);
        SQLiteDatabase database = db.getWritableDatabase();

        Cursor cursor = database.rawQuery(selectQuery, null);

        if (cursor.getCount() > 0){
            cursor.moveToFirst();

            String nomeRetorno = cursor.getString(cursor.getColumnIndex("Login"));
            String senhaRetorno = cursor.getString(cursor.getColumnIndex("Senha"));

            db.close();
            database.close();

            if (senha.equals(senhaRetorno) && login.equals(nomeRetorno)){
                Usuario usuario = new Usuario(nomeRetorno, senhaRetorno);
                return usuario;
            } else {
                return null;
            }
        } else {
            db.close();
            database.close();
            return null;
        }
    }
}
