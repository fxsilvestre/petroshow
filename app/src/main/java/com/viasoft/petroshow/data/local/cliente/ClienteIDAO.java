package com.viasoft.petroshow.data.local.cliente;

import java.util.List;

public interface ClienteIDAO {
    boolean insert(Cliente c);
    boolean update(Cliente c);
    boolean delete(Cliente c);
    List<Cliente> getAll();
}
