package com.viasoft.petroshow.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.viasoft.petroshow.R;
import com.viasoft.petroshow.data.local.endereco.Endereco;

import java.util.List;

public class EnderecoAdapter extends RecyclerView.Adapter<EnderecoAdapter.MyViewHolder> {
    private List<Endereco> enderecos;

    public EnderecoAdapter(List<Endereco> enderecos) {
        this.enderecos = enderecos;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_enderecos, parent, false);
        return new MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Endereco c = this.enderecos.get(position);
        holder.nome.setText(c.getNome());
        holder.cep.setText(c.getCep());
    }

    @Override
    public int getItemCount() {
        return this.enderecos.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView nome;
        TextView cep;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            nome = itemView.findViewById(R.id.adapterEnderecoNome);
            cep = itemView.findViewById(R.id.adapterEnderecoCep);
        }
    }
}
