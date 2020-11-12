package com.viasoft.petroshow.data.remoto.endereco;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface EnderecoService {

    @GET("{cep}/json/")
    Call<EnderecoResp> pesquisarPorCEP(@Path("cep") String cep);
}
