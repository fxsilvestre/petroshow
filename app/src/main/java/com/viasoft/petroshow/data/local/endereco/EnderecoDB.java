package com.viasoft.petroshow.data.local.endereco;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

public class EnderecoDB extends SQLiteOpenHelper {
    public static int VERSION = 1;
    public static String ENDERECO_DB = "endereco_db";
    public static String ENDERECO_TB = "endereco";

    public EnderecoDB(@Nullable Context context) {
        super(context, ENDERECO_DB, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + ENDERECO_TB + " (id INTEGER PRIMARY KEY, cep TEXT, nome TEXT, numero TEXT, complemento TEXT, bairro TEXT, cidade TEXT, uf TEXT, idCliente INTEGER);";
        try {
            db.execSQL(sql);
            Log.i("INFO DB", "Sucesso ao criar a tabela");
        } catch (Exception e) {
            Log.i("INFO DB", "Erro ao criar a tabela" + e.getMessage());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql = "DROP TABLE IF EXISTS " + ENDERECO_TB + ";";
        try {
            db.execSQL(sql);
            onCreate(db);
            Log.i("INFO DB", "Sucesso ao atualizar App");
        } catch (Exception e) {
            Log.i("INFO DB", "Erro ao atualizar App" + e.getMessage());
        }
    }
}
