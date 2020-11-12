package com.viasoft.petroshow.data.local.cliente;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ClienteDB extends SQLiteOpenHelper {
    public static int VERSION = 1;
    public static String CLIENTE_DB = "cliente_db";
    public static String CLIENTE_TB = "cliente";

    public ClienteDB(@Nullable Context context) {
        super(context, CLIENTE_DB, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + CLIENTE_TB + " (id INTEGER PRIMARY KEY, nome TEXT, email TEXT, telefone TEXT, dataNascimento TEXT, foto BLOB);";
        try {
            db.execSQL( sql );
            Log.i("INFO DB", "Sucesso ao criar a tabela" );
        }catch (Exception e){
            Log.i("INFO DB", "Erro ao criar a tabela" + e.getMessage() );
        }    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql = "DROP TABLE IF EXISTS " + CLIENTE_TB + ";";
        try {
            db.execSQL( sql );
            onCreate(db);
            Log.i("INFO DB", "Sucesso ao atualizar App" );
        }catch (Exception e){
            Log.i("INFO DB", "Erro ao atualizar App" + e.getMessage() );
        }
    }
}
