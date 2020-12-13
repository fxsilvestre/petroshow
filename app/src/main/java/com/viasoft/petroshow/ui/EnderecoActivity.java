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
import com.viasoft.petroshow.data.remoto.endereco.EnderecoRetrofit;
import com.viasoft.petroshow.data.remoto.endereco.EnderecoService;
import com.viasoft.petroshow.ui.util.Constants;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@AndroidEntryPoint
public class EnderecoActivity extends AppCompatActivity {
    private static final String O_CEP_TEM_QUE_TER_8_DÍGITOS = "O CEP tem que ter 8 dígitos!";
    public static final String NOME_E_EMAIL_SAO_OBRIGATORIOS = "Nome e Email são obrigatórios!";
    private TextInputEditText cep;
    private TextInputEditText nome;
    private TextInputEditText numero;
    private TextInputEditText complemento;
    private TextInputEditText bairro;
    private TextInputEditText cidade;
    private TextInputEditText uf;
    private Button buttonSalvarEndereco;
    private Button buttonPesquisaCep;
    private Endereco enderecoParaEditar;
    @Inject
    EnderecoRetrofit enderecoRetrofit;
    @Inject
    EnderecoDAO enderecoDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_endereco);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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
            enderecoParaEditar = (Endereco) dados.getSerializable(Constants.ENDERECO_PARA_EDITAR);
            if (enderecoParaEditar != null) {
                if (enderecoParaEditar.getId() != null) {
                    setarTelaEndereco(enderecoParaEditar);
                }
            }
        }
    }

    private void pesquisarPorCEP(String cpf) {
        if (!validarCpf(cpf)) {
            mostrarMsg(O_CEP_TEM_QUE_TER_8_DÍGITOS);
            return;
        }

        EnderecoService service = enderecoRetrofit.getEndereco().create(EnderecoService.class);
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
            mostrarMsg(NOME_E_EMAIL_SAO_OBRIGATORIOS);
            return;
        }
        if (enderecoParaEditar.getId() == null) {
            enderecoParaEditar = setarEndereco(enderecoParaEditar);
            enderecoDAO.insert(enderecoParaEditar);
        } else {
            enderecoParaEditar = setarEndereco(enderecoParaEditar);
            enderecoDAO.update(setarEndereco(enderecoParaEditar));
        }
    }

    private Endereco setarEndereco(Endereco endereco) {
        endereco.setCep(cep.getText().toString());
        endereco.setNome(nome.getText().toString());
        endereco.setNumero(numero.getText().toString());
        endereco.setComplemento(complemento.getText().toString());
        endereco.setBairro(bairro.getText().toString());
        endereco.setCidade(cidade.getText().toString());
        endereco.setUf(uf.getText().toString());
        return endereco;
    }

    private void setarTelaEndereco(Endereco e) {
        cep.setText(e.getCep());
        nome.setText(e.getNome());
        numero.setText(e.getNumero());
        complemento.setText(e.getComplemento());
        bairro.setText(e.getBairro());
        cidade.setText(e.getCidade());
        uf.setText(e.getUf());
    }
}