package com.company.JitHub.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    static final String dbName = "JitHubDB";
    static final String loginTable = "Logins";
    static final String colLoginID = "LoginID";
    static final String colLogin = "Login";
    static final String colSenha = "Senha";


    public DatabaseHelper(Context context){
        super(context, dbName, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE "+loginTable+" ("+colLoginID+ " INTEGER PRIMARY KEY , "+colLogin+ " TEXT , "+colSenha+" TEXT)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
