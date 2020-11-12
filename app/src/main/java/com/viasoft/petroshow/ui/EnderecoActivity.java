package com.viasoft.petroshow.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.viasoft.petroshow.R;
import com.viasoft.petroshow.data.local.endereco.Endereco;
import com.viasoft.petroshow.data.local.endereco.EnderecoDAO;
import com.viasoft.petroshow.data.remoto.endereco.EnderecoResp;
import com.viasoft.petroshow.data.remoto.endereco.EnderecoService;
import com.viasoft.petroshow.ui.util.Constants;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class EnderecoActivity extends AppCompatActivity {
    private static final String O_CEP_TEM_QUE_TER_8_DÍGITOS = "O CEP tem que ter 8 dígitos!";
    private TextInputEditText cep;
    private TextInputEditText nome;
    private TextInputEditText numero;
    private TextInputEditText complemento;
    private TextInputEditText bairro;
    private TextInputEditText cidade;
    private TextInputEditText uf;
    private Button buttonSalvarEndereco;
    private Button buttonPesquisaCep;
    private EnderecoDAO enderecoDAO;
    private Long idCliente;
    private Endereco enderecoParaEditar;
    private Retrofit retrofit;

    @Override
    protected void onStart() {
        super.onStart();
        this.enderecoDAO = new EnderecoDAO(getApplicationContext());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_endereco);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        retrofit = new Retrofit.Builder()
                .baseUrl("https://viacep.com.br/ws/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        cep = findViewById(R.id.cep);
        nome = findViewById(R.id.nome);
        numero = findViewById(R.id.numero);
        complemento = findViewById(R.id.complemento);
        bairro = findViewById(R.id.bairro);
        cidade = findViewById(R.id.cidade);
        uf = findViewById(R.id.uf);

        buttonSalvarEndereco = findViewById(R.id.buttonSalvarEndereco);
        buttonSalvarEndereco.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                salvarEndereco();
            }
        });
        buttonPesquisaCep = findViewById(R.id.buttonPesquisaCep);
        buttonPesquisaCep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pesquisarPorCEP(cep.getText().toString());
            }
        });
        Bundle dados = getIntent().getExtras();
        if (dados != null) {
            this.idCliente = (Long) dados.getLong(Constants.ID_CLIENTE);
            this.enderecoParaEditar = (Endereco) dados.getSerializable(Constants.ENDERECO_PARA_EDITAR);
            if (enderecoParaEditar != null) {
                preencherViewEndereco(enderecoParaEditar);
            }
        }
    }

    @Override
    protected void onDestroy() {
        this.enderecoDAO = null;
        super.onDestroy();
    }

    private void pesquisarPorCEP(String cpf) {
        if (!validarCpf(cpf)) {
            mostrarMsg(O_CEP_TEM_QUE_TER_8_DÍGITOS);
            return;
        }

        EnderecoService service = this.retrofit.create(EnderecoService.class);
        Call<EnderecoResp> call = service.pesquisarPorCEP(cpf);
        call.enqueue(new Callback<EnderecoResp>() {
            @Override
            public void onResponse(Call<EnderecoResp> call, Response<EnderecoResp> response) {
                if (response.isSuccessful()) {
                    EnderecoResp endereco = response.body();
                    if (endereco.getCep() == null) {
                        mostrarMsg(Constants.ERRO);
                        return;
                    }
                    nome.setText(endereco.getLogradouro());
                    numero.setText(endereco.getIbge());
                    complemento.setText(endereco.getComplemento());
                    bairro.setText(endereco.getBairro());
                    cidade.setText(endereco.getLocalidade());
                    uf.setText(endereco.getUf());
                }
            }

            @Override
            public void onFailure(Call<EnderecoResp> call, Throwable t) {
                mostrarMsg(Constants.ERRO);
            }
        });
    }

    private boolean validarCpf(String cpf) {
        return cpf.length() == 8 ? true : false;
    }

    private void mostrarMsg(String msg) {
        Toast.makeText(
                getApplicationContext(),
                msg,
                Toast.LENGTH_SHORT
        ).show();
    }

    private boolean validarDados() {
        return nome.getText().toString().isEmpty() || cep.getText().toString().isEmpty() ||
                numero.getText().toString().isEmpty() || cidade.getText().toString().isEmpty() ? false : true;
    }

    private void salvarEndereco() {
        if (!validarDados()) {
            mostrarMsg("Nome e Email são obrigatórios!");
            return;
        }
        Endereco endereco;
        if (idCliente != 0) {
            endereco = new Endereco();
            endereco = setEndereco(endereco);
            endereco.setIdCliente(idCliente);
            this.enderecoDAO.insert(endereco);
        } else {
            endereco = this.enderecoParaEditar;
            this.enderecoDAO.update(setEndereco(endereco));
        }
    }

    private Endereco setEndereco(Endereco endereco) {
        endereco.setCep(cep.getText().toString());
        endereco.setNome(nome.getText().toString());
        endereco.setNumero(numero.getText().toString());
        endereco.setComplemento(complemento.getText().toString());
        endereco.setBairro(bairro.getText().toString());
        endereco.setCidade(cidade.getText().toString());
        endereco.setUf(uf.getText().toString());
        return endereco;
    }

    private void preencherViewEndereco(Endereco e) {
        cep.setText(e.getCep());
        nome.setText(e.getNome());
        numero.setText(e.getNumero());
        complemento.setText(e.getComplemento());
        bairro.setText(e.getBairro());
        cidade.setText(e.getCidade());
        uf.setText(e.getUf());
    }
}