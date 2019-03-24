package com.company.JitHub.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.company.JitHub.Model.Usuario;

public class UsuarioDAO{

    private Context _context;

    public UsuarioDAO(Context context){
        _context = context;
    }

    public Usuario GetUsuario(String login){

        String selectQuery = "SELECT * FROM Logins WHERE Login = '" + login + "'";

        DatabaseHelper db = new DatabaseHelper(_context);
        SQLiteDatabase database = db.getWritableDatabase();

        Cursor cursor = database.rawQuery(selectQuery, null);

        if (cursor.getCount() > 0){
            cursor.moveToFirst();

            String nomeRetorno = cursor.getString(cursor.getColumnIndex("Login"));
            String senhaRetorno = cursor.getString(cursor.getColumnIndex("Senha"));

            db.close();
            database.close();

            if (nomeRetorno != null){
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

    public void UpdateUsuario(Usuario usuario){
        DatabaseHelper databaseHelper = new DatabaseHelper(_context);
        SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("Senha", usuario.get_senha());
        sqLiteDatabase.update("Logins",values, "Login = ?", new String[]{String.valueOf(usuario.get_nome())});

        databaseHelper.close();
        sqLiteDatabase.close();
    }

    public void InsertUsuario(Usuario usuario){
        DatabaseHelper databaseHelper = new DatabaseHelper(_context);
        SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("Login", usuario.get_nome());
        values.put("senha", usuario.get_senha());
        values.put("email", usuario.get_email());
        values.put("grupo", usuario.get_grupo());
        sqLiteDatabase.insert("Logins",null,values);

        databaseHelper.close();
        sqLiteDatabase.close();

    }
}
