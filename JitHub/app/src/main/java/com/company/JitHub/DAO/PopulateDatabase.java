package com.company.JitHub.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class PopulateDatabase {

    private Context contexto;

    public PopulateDatabase(Context context){

        contexto = context;
    }

    public void populate(){

        DatabaseHelper dbHelper = new DatabaseHelper(contexto);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
            cv.put("LoginID", 1);
            cv.put("Login", "caio");
            cv.put("Senha", "caio");
            db.insert("Logins", "LoginID", cv);
        db.close();
        dbHelper.close();
    }
}
