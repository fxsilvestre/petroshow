package com.viasoft.petroshow.data.local.endereco;


import java.util.List;

public interface EnderecoIDAO {
    boolean insert(Endereco e);
    boolean update(Endereco e);
    boolean delete(Endereco e);
    List<Endereco> getAllByCliente(Long idCliente);
}
