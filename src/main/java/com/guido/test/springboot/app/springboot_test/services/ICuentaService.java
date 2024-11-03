package com.guido.test.springboot.app.springboot_test.services;

import com.guido.test.springboot.app.springboot_test.models.Cuenta;

import java.math.BigDecimal;
import java.util.List;

public interface ICuentaService {
    List<Cuenta> findAll();
    Cuenta findById(Long id);
    Cuenta save(Cuenta cuenta);
    void deleteById(Long id);
    int revisarTotalTransferencias(Long bancoId);
    BigDecimal revisarSaldo(Long cuentaId);
    void transferir(Long numCuentaOrigen, Long numCuentaDestino, BigDecimal monto, Long bancoId);
}
