package com.guido.test.springboot.app.springboot_test.repositories;

import com.guido.test.springboot.app.springboot_test.models.Banco;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IBancoRepository extends JpaRepository<Banco, Long> {
}
