package com.viasoft.petroshow.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.viasoft.petroshow.R;
import com.viasoft.petroshow.data.local.cliente.Cliente;
import com.viasoft.petroshow.data.local.cliente.ClienteDAO;
import com.viasoft.petroshow.data.local.endereco.Endereco;
import com.viasoft.petroshow.data.local.endereco.EnderecoDAO;
import com.viasoft.petroshow.ui.adapter.EnderecoAdapter;
import com.viasoft.petroshow.ui.util.Constants;
import com.viasoft.petroshow.ui.util.RecyclerItemClickListener;

import java.util.ArrayList;
import java.util.List;

public class ClienteActivity extends AppCompatActivity {
    private static final String ENDEREÇO_APAGADO_COM_SUCESSO = "Endereço apagado com sucesso.";
    private static final String APAGAR_O_ENDEREÇO = "Apagar o endereço?";
    private static final int REQUEST_CODE_CAMERA = 100;
    private static final int REQUEST_CODE_GALERIA = 200;
    private static final String NOME_E_EMAIL_SÃO_OBRIGATÓRIOS = "Nome e Email são obrigatórios!";
    private TextInputEditText nome;
    private TextInputEditText email;
    private TextInputEditText telefone;
    private TextInputEditText dataNascimento;
    private Button buttonSalvarCliente;
    private ClienteDAO clienteDAO;
    private EnderecoDAO enderecoDAO;
    private RecyclerView recyclerViewEndereco;
    private List<Endereco> enderecos;
    private ImageButton imagemCliente;
    private Bitmap imagemClienteBitmap;

    @Override
    protected void onStart() {
        super.onStart();
        this.clienteDAO = new ClienteDAO(getApplicationContext());
        this.enderecoDAO = new EnderecoDAO(getApplicationContext());
        this.enderecos = new ArrayList<>();
        Bundle dados = getIntent().getExtras();
        if (dados != null) {
            Cliente c = (Cliente) dados.getSerializable(Constants.CLIENTE);
            preencherCliente(c);
            carregarEnderecos(c.getId());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cliente);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        nome = findViewById(R.id.nome);
        email = findViewById(R.id.email);
        telefone = findViewById(R.id.telefone);
        dataNascimento = findViewById(R.id.dataNascimento);
        imagemCliente = findViewById(R.id.imageCliente);
        imagemCliente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarDialogOpcoes(v);
            }
        });
        buttonSalvarCliente = findViewById(R.id.buttonSalvarCliente);
        buttonSalvarCliente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                salvarCliente();
            }
        });
        recyclerViewEndereco = findViewById(R.id.recyclerViewEndereco);
        recyclerViewEndereco.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        getApplicationContext(),
                        recyclerViewEndereco,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                mostrarDialogOpcoes(view, enderecos.get(position));
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            imagemClienteBitmap = null;
            try {
                switch (requestCode) {
                    case REQUEST_CODE_CAMERA:
                        imagemClienteBitmap = (Bitmap) data.getExtras().get("data");
                        break;
                    case REQUEST_CODE_GALERIA:
                        Uri uriImgGaleria = data.getData();
                        imagemClienteBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uriImgGaleria);
                        break;
                }
                if (imagemClienteBitmap != null) {
                    imagemCliente.setImageBitmap(imagemClienteBitmap);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onDestroy() {
        this.clienteDAO = null;
        this.enderecoDAO = null;
        super.onDestroy();
    }

    public void mostrarDialogOpcoes(View view) {
        String[] options = {Constants.CAMERA, Constants.GALERIA};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(Constants.CLIENTE);
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                switch (i) {
                    case 0:
                        Intent iCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        if (iCamera.resolveActivity(getPackageManager()) != null) {
                            startActivityForResult(iCamera, REQUEST_CODE_CAMERA);
                        }
                        break;
                    case 1:
                        Intent iGaleria = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        if (iGaleria.resolveActivity(getPackageManager()) != null) {
                            startActivityForResult(iGaleria, REQUEST_CODE_GALERIA);
                        }
                        break;
                }
            }
        });
        builder.show();
    }

    private void carregarEnderecos(Long idCliente) {
        this.enderecos = this.enderecoDAO.getAllByCliente(idCliente);
        EnderecoAdapter adapter = new EnderecoAdapter(this.enderecos);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerViewEndereco.setLayoutManager(layoutManager);
        recyclerViewEndereco.setHasFixedSize(true);
        recyclerViewEndereco.addItemDecoration(new DividerItemDecoration(this, LinearLayout.VERTICAL));
        recyclerViewEndereco.setAdapter(adapter);
    }

    private void mostrarMsg(String msg) {
        Toast.makeText(
                getApplicationContext(),
                msg,
                Toast.LENGTH_SHORT
        ).show();
    }

    private boolean validarDados() {
        return nome.getText().toString().isEmpty() || email.getText().toString().isEmpty() ? false : true;
    }

    private void salvarCliente() {
        if (!validarDados()) {
            mostrarMsg(NOME_E_EMAIL_SÃO_OBRIGATÓRIOS);
            return;
        }
        Cliente c = new Cliente();
        c.setNome(nome.getText().toString());
        c.setEmail(email.getText().toString());
        c.setTelefone(telefone.getText().toString());
        c.setDataNascimento(dataNascimento.getText().toString());
        /*
        if (imagemClienteBitmap != null) {
            ByteArrayOutputStream saida = new ByteArrayOutputStream();
            imagemClienteBitmap.compress(Bitmap.CompressFormat.PNG,100,saida);
            c.setFoto(saida.toByteArray());
        }*/
        this.clienteDAO.insert(c);
    }

    private void preencherCliente(Cliente c) {
        nome.setText(c.getNome());
        email.setText(c.getEmail());
        telefone.setText(c.getTelefone());
        dataNascimento.setText(c.getDataNascimento());
        /*
        if (c.getFoto() != null)
            imagemCliente.setImageBitmap(BitmapFactory.decodeByteArray(c.getFoto(),0,c.getFoto().length));
         */
    }

    public void mostrarDialogOpcoes(View view, Endereco endereco) {
        String[] options = {Constants.EDITAR, Constants.APAGAR};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(endereco.getNome());
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                switch (i) {
                    case 0:
                        Intent intentEditar = new Intent(getApplicationContext(), EnderecoActivity.class);
                        intentEditar.putExtra(Constants.ID_CLIENTE, 0);
                        intentEditar.putExtra(Constants.ENDERECO_PARA_EDITAR, endereco);
                        startActivity(intentEditar);
                        break;
                    case 1:
                        mostrarDialogApagarEndereco(view, endereco);
                        break;
                }
            }
        });
        builder.show();
    }

    public void mostrarDialogApagarEndereco(View view, Endereco endereco) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(endereco.getNome());
        builder.setMessage(APAGAR_O_ENDEREÇO);
        builder.setPositiveButton(Constants.SIM, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                apagarEndereco(endereco);
            }
        });
        builder.setNegativeButton(Constants.NÃO, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.show();
    }

    private void apagarEndereco(Endereco endereco) {
        if (this.enderecoDAO.delete(endereco)) {
            Toast.makeText(
                    getApplicationContext(),
                    ENDEREÇO_APAGADO_COM_SUCESSO,
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