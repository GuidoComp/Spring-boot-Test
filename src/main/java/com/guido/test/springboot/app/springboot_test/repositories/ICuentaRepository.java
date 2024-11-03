package com.guido.test.springboot.app.springboot_test.repositories;

import com.guido.test.springboot.app.springboot_test.models.Cuenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ICuentaRepository extends JpaRepository<Cuenta, Long> {
    @Query("select c from Cuenta c where c.persona = ?1")
    Optional<Cuenta> findByPersona(String persona);
}
