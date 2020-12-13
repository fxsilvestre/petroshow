package com.viasoft.petroshow.data.local.endereco;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.qualifiers.ActivityContext;

public class EnderecoDAO implements EnderecoIDAO {

    private SQLiteDatabase write;
    private SQLiteDatabase read;

    @Inject
    EnderecoDAO(@ActivityContext Context context) {
        EnderecoDB db = new EnderecoDB(context);
        write = db.getWritableDatabase();
        read = db.getReadableDatabase();
    }

    @Override
    public boolean insert(Endereco e) {
        try {
            write.insert(EnderecoDB.ENDERECO_TB, null, getContentValue(e));
            Log.i("INFO", "Endereco salvo com sucesso!");
        } catch (Exception ex) {
            Log.e("INFO", "Erro ao salvar Endereco " + ex.getMessage());
            return false;
        }
        return true;
    }

    @Override
    public boolean update(Endereco e) {
        try {
            String[] args = {e.getId().toString()};
            write.update(EnderecoDB.ENDERECO_TB, getContentValue(e), "id = ?", args);
            Log.i("INFO", "Endereco atualizado com sucesso!");
        } catch (Exception ex) {
            Log.e("INFO", "Erro ao atualizar endereco " + ex.getMessage());
            return false;
        }
        return true;
    }

    @Override
    public boolean delete(Endereco e) {
        try {
            String[] args = {e.getId().toString()};
            write.delete(EnderecoDB.ENDERECO_TB, "id = ?", args);
            Log.i("INFO", "Endereco removido com sucesso!");
        } catch (Exception ex) {
            Log.e("INFO", "Erro ao remover endereco " + ex.getMessage());
            return false;
        }
        return true;
    }

    @Override
    public List<Endereco> getAllByCliente(Long idCliente) {
        String[] args = {idCliente.toString()};
        String sql = "SELECT * FROM " + EnderecoDB.ENDERECO_TB + " WHERE idCliente=?;";
        Cursor c = read.rawQuery(sql, args);
        List<Endereco> enderecos = new ArrayList<>();
        while (c.moveToNext()) {
            Endereco endereco = new Endereco();
            endereco.setId(c.getLong(c.getColumnIndex("id")));
            endereco.setCep(c.getString(c.getColumnIndex("cep")));
            endereco.setNome(c.getString(c.getColumnIndex("nome")));
            endereco.setNumero(c.getString(c.getColumnIndex("numero")));
            endereco.setComplemento(c.getString(c.getColumnIndex("complemento")));
            endereco.setBairro(c.getString(c.getColumnIndex("bairro")));
            endereco.setCidade(c.getString(c.getColumnIndex("cidade")));
            endereco.setUf(c.getString(c.getColumnIndex("uf")));
            endereco.setIdCliente(c.getLong(c.getColumnIndex("idCliente")));
            enderecos.add(endereco);
        }
        c.close();
        return enderecos;
    }

    private ContentValues getContentValue(Endereco e) {
        ContentValues cv = new ContentValues();
        if (e.getId() != null) {
            cv.put("id", e.getId());
        }
        cv.put("cep", e.getCep());
        cv.put("nome", e.getNome());
        cv.put("numero", e.getNumero());
        cv.put("complemento", e.getComplemento());
        cv.put("bairro", e.getBairro());
        cv.put("cidade", e.getCidade());
        cv.put("uf", e.getUf());
        cv.put("idCliente", e.getIdCliente());
        return cv;
    }
}
