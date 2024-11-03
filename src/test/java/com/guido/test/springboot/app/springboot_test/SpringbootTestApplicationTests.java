package com.guido.test.springboot.app.springboot_test;

import static com.guido.test.springboot.app.springboot_test.data.Datos.*;

import com.guido.test.springboot.app.springboot_test.exceptions.DineroInsuficienteException;
import com.guido.test.springboot.app.springboot_test.models.Banco;
import com.guido.test.springboot.app.springboot_test.models.Cuenta;
import com.guido.test.springboot.app.springboot_test.repositories.IBancoRepository;
import com.guido.test.springboot.app.springboot_test.repositories.ICuentaRepository;
import com.guido.test.springboot.app.springboot_test.services.ICuentaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static com.guido.test.springboot.app.springboot_test.data.Datos.crearCuenta001;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class SpringbootTestApplicationTests {
	@MockBean
	ICuentaRepository cuentaRepository;
	@MockBean
	IBancoRepository bancoRepository;

	@Autowired
	ICuentaService service;

	@BeforeEach
	void setUp() {
//		cuentaRepository = mock(ICuentaRepository.class);
//		bancoRepository = mock(IBancoRepository.class);
//		service = new CuentaService(cuentaRepository, bancoRepository);
	}

	@Test
	void transferenciaTest() {
		when(cuentaRepository.findById(1L)).thenReturn(crearCuenta001());
		when(cuentaRepository.findById(2L)).thenReturn(crearCuenta002());
		when(bancoRepository.findById(1L)).thenReturn(crearBanco());

		BigDecimal saldoOrigen = service.revisarSaldo(1L);
		BigDecimal saldoDestino = service.revisarSaldo(2L);

		assertEquals("1000", saldoOrigen.toPlainString());
		assertEquals("2000", saldoDestino.toPlainString());

		service.transferir(1L, 2L, new BigDecimal("100"), 1L);

		saldoOrigen = service.revisarSaldo(1L);
		saldoDestino = service.revisarSaldo(2L);

		assertEquals("900", saldoOrigen.toPlainString());
		assertEquals("2100", saldoDestino.toPlainString());

		int total = service.revisarTotalTransferencias(1L);
		assertEquals(1, total);
		verify(cuentaRepository, times(3)).findById(1L);
		verify(cuentaRepository, times(3)).findById(2L);
		verify(cuentaRepository, times(2)).save(any(Cuenta.class));

		verify(bancoRepository, times(2)).findById(1L);
		verify(bancoRepository).save(any(Banco.class));

		verify(cuentaRepository, never()).findAll();
		verify(cuentaRepository, times(6)).findById(anyLong());
	}

	@Test
	void excepcionTransferenciaTest() {
		when(cuentaRepository.findById(1L)).thenReturn(crearCuenta001());
		when(cuentaRepository.findById(2L)).thenReturn(crearCuenta002());
		when(bancoRepository.findById(1L)).thenReturn(crearBanco());

		BigDecimal saldoOrigen = service.revisarSaldo(1L);
		BigDecimal saldoDestino = service.revisarSaldo(2L);

		assertEquals("1000", saldoOrigen.toPlainString());
		assertEquals("2000", saldoDestino.toPlainString());

		assertThrows(DineroInsuficienteException.class, () -> {
			service.transferir(1L, 2L, new BigDecimal("1200"), 1L);
		});

		saldoOrigen = service.revisarSaldo(1L);
		saldoDestino = service.revisarSaldo(2L);

		assertEquals("1000", saldoOrigen.toPlainString());
		assertEquals("2000", saldoDestino.toPlainString());

		int total = service.revisarTotalTransferencias(1L);
		assertEquals(0, total);
		verify(cuentaRepository, times(3)).findById(1L);
		verify(cuentaRepository, times(2)).findById(2L);
		verify(cuentaRepository, never()).save(any(Cuenta.class));

		verify(bancoRepository, times(1)).findById(1L);
		verify(bancoRepository, never()).save(any(Banco.class));
		
		verify(cuentaRepository, never()).findAll();
		verify(cuentaRepository, times(5)).findById(anyLong());
	}

	@Test
	void sameObjectTest() {
		when(cuentaRepository.findById(1L)).thenReturn(crearCuenta001());
		Cuenta cuenta1 = service.findById(1L);
		Cuenta cuenta2 = service.findById(1L);

		assertSame(cuenta1, cuenta2);
		assertTrue(cuenta1 == cuenta2);
		assertEquals("Andrés", cuenta1.getPersona());
		assertEquals("Andrés", cuenta2.getPersona());

		verify(cuentaRepository, times(2)).findById(1L);
	}

	@Test
	void testFindAll() {
		// Given
		List<Cuenta> cuentasMock = Arrays.asList(crearCuenta001().orElseThrow(), crearCuenta002().orElseThrow());
		when(cuentaRepository.findAll()).thenReturn(cuentasMock);

		// When
		List<Cuenta> cuentas = service.findAll();

		// Then
		assertFalse(cuentas.isEmpty());
		assertEquals(2, cuentas.size());
		assertTrue(cuentas.contains(crearCuenta002().orElseThrow()));

		verify(cuentaRepository).findAll();
	}

	@Test
	void testSave() {
		// Given
		Cuenta cuentaMock = new Cuenta(null, "Pepe", new BigDecimal("3000"));
		when(cuentaRepository.save(any())).then(invocation -> {
			Cuenta c = invocation.getArgument(0);
			c.setId(3L);
			return c;
		});

		// When
		Cuenta nuevaCuenta = service.save(cuentaMock);

		// Then
		assertEquals("Pepe", nuevaCuenta.getPersona());
		assertEquals(3L, nuevaCuenta.getId());
		assertEquals("3000", nuevaCuenta.getSaldo().toPlainString());

		verify(cuentaRepository).save(any());
	}
}
