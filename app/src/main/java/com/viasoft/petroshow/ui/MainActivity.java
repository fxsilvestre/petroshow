package com.viasoft.petroshow.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.viasoft.petroshow.R;
import com.viasoft.petroshow.data.local.cliente.Cliente;
import com.viasoft.petroshow.data.local.cliente.ClienteDAO;
import com.viasoft.petroshow.ui.adapter.ClienteAdapter;
import com.viasoft.petroshow.ui.util.Constants;
import com.viasoft.petroshow.ui.util.RecyclerItemClickListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String ADICIONAR_ENDERECO = "Adicionar endereço";
    private static final String APAGAR_O_CLIENTE = "Apagar o cliente?";
    private static final String CLIENTE_APAGADO_COM_SUCESSO = "Cliente apagado com sucesso.";
    private RecyclerView recyclerViewCliente;
    private List<Cliente> clientes;
    private ClienteDAO clienteDAO;

    @Override
    protected void onStart() {
        super.onStart();
        this.clienteDAO = new ClienteDAO(getApplicationContext());
        this.clientes = new ArrayList<>();
        carregarClientes();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton fabAddCliente = findViewById(R.id.fabAddCliente);
        fabAddCliente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ClienteActivity.class);
                startActivity(intent);
            }
        });

        recyclerViewCliente = findViewById(R.id.recyclerViewCliente);
        recyclerViewCliente.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        getApplicationContext(),
                        recyclerViewCliente,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                mostrarDialogOpcoes(view, clientes.get(position));
                            }

                            @Override
                            public void onLongItemClick(View view, int position) {
                            }

                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            }
                        }
                )
        );
    }

    @Override
    protected void onDestroy() {
        this.clienteDAO = null;
        super.onDestroy();
    }

    private void carregarClientes() {
        this.clientes = this.clienteDAO.getAll();
        ClienteAdapter adapter = new ClienteAdapter(this.clientes);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerViewCliente.setLayoutManager(layoutManager);
        recyclerViewCliente.setHasFixedSize(true);
        recyclerViewCliente.addItemDecoration(new DividerItemDecoration(this, LinearLayout.VERTICAL));
        recyclerViewCliente.setAdapter(adapter);
    }

    public void mostrarDialogOpcoes(View view, Cliente cliente) {
        String[] options = {Constants.EDITAR, Constants.APAGAR, ADICIONAR_ENDERECO};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(cliente.getNome());
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                switch (i) {
                    case 0:
                        Intent intentEditar = new Intent(getApplicationContext(), ClienteActivity.class);
                        intentEditar.putExtra(Constants.CLIENTE, cliente);
                        startActivity(intentEditar);
                        break;
                    case 1:
                        mostrarDialogApagarCliente(view, cliente);
                        break;
                    case 2:
                        Intent intentAdicionarEndereco = new Intent(getApplicationContext(), EnderecoActivity.class);
                        intentAdicionarEndereco.putExtra(Constants.ID_CLIENTE, cliente.getId());
                        startActivity(intentAdicionarEndereco);
                        break;
                }
            }
        });
        builder.show();
    }

    public void mostrarDialogApagarCliente(View view, Cliente cliente) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(cliente.getNome());
        builder.setMessage(APAGAR_O_CLIENTE);
        builder.setPositiveButton(Constants.SIM, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                apagarCliente(cliente);
            }
        });
        builder.setNegativeButton(Constants.NÃO, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.show();
    }

    private void apagarCliente(Cliente cliente) {
        if (this.clienteDAO.delete(cliente)) {
            Toast.makeText(
                    getApplicationContext(),
                    CLIENTE_APAGADO_COM_SUCESSO,
                    Toast.LENGTH_SHORT
            ).show();
            onStart();
        } else {
            Toast.makeText(
                    getApplicationContext(),
                    Constants.ERRO,
                    Toast.LENGTH_SHORT
            ).show();
        }
    }
}