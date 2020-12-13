package com.viasoft.petroshow.data.remoto.endereco;

import javax.inject.Inject;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class EnderecoRetrofit {
    private Retrofit retrofit;

    @Inject
    public EnderecoRetrofit() {
         retrofit = new Retrofit.Builder()
                .baseUrl("https://viacep.com.br/ws/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public Retrofit getEndereco() {
        return retrofit;
    }
}
