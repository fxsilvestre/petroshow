package com.viasoft.petroshow.data.local.cliente;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class ClienteDAO implements ClienteIDAO {

    private SQLiteDatabase write;
    private SQLiteDatabase read;

    public ClienteDAO(Context context) {
        ClienteDB db = new ClienteDB(context);
        write = db.getWritableDatabase();
        read = db.getReadableDatabase();
    }

    @Override
    public boolean insert(Cliente c) {
        try {
            write.insert(ClienteDB.CLIENTE_TB, null, getContentValue(c));
            Log.i("INFO", "Cliente salvo com sucesso!");
        } catch (Exception ex) {
            Log.e("INFO", "Erro ao salvar Cliente " + ex.getMessage());
            return false;
        }
        return true;
    }

    @Override
    public boolean update(Cliente c) {
        try {
            String[] args = {c.getId().toString()};
            write.update(ClienteDB.CLIENTE_TB, getContentValue(c), "id = ?", args);
            Log.i("INFO", "Cliente atualizado com sucesso!");
        } catch (Exception ex) {
            Log.e("INFO", "Erro ao atualizar Cliente " + ex.getMessage());
            return false;
        }
        return true;
    }

    @Override
    public boolean delete(Cliente c) {
        try {
            String[] args = {c.getId().toString()};
            write.delete(ClienteDB.CLIENTE_TB, "id = ?", args);
            Log.i("INFO", "Cliente atualizado com sucesso!");
        } catch (Exception ex) {
            Log.e("INFO", "Erro ao atualizar Cliente " + ex.getMessage());
            return false;
        }
        return true;
    }

    @Override
    public List<Cliente> getAll() {
        String sql = "SELECT * FROM " + ClienteDB.CLIENTE_TB + ";";
        Cursor c = read.rawQuery(sql, null);
        List<Cliente> clientes = new ArrayList<>();
        while (c.moveToNext()) {
            Cliente cliente = new Cliente();
            cliente.setId(c.getLong(c.getColumnIndex("id")));
            cliente.setNome(c.getString(c.getColumnIndex("nome")));
            cliente.setEmail(c.getString(c.getColumnIndex("email")));
            cliente.setTelefone(c.getString(c.getColumnIndex("telefone")));
            cliente.setDataNascimento(c.getString(c.getColumnIndex("dataNascimento")));
            cliente.setFoto(c.getBlob(c.getColumnIndex("foto")));
            clientes.add(cliente);
        }
        c.close();
        return clientes;
    }

    private ContentValues getContentValue(Cliente c) {
        ContentValues cv = new ContentValues();
        if (c.getId() != null) {
            cv.put("id", c.getId());
        }
        cv.put("nome", c.getNome());
        cv.put("email", c.getEmail());
        cv.put("telefone", c.getTelefone());
        cv.put("dataNascimento", c.getDataNascimento());
        cv.put("foto", c.getFoto());
        return cv;
    }
}
