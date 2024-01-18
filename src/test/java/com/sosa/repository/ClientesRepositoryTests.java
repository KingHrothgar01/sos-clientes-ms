package com.sosa.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.sosa.model.Cliente;

import static com.sosa.util.Constants.USER_ALTA;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureDataJpa
@AutoConfigureTestDatabase
@AutoConfigureTestEntityManager
@Transactional
class ClientesRepositoryTests {
	
	@Autowired
	ClienteRepository clienteRepository;
	
	private Cliente clienteCero;

	@BeforeEach
	void setUp() throws Exception {
		clienteCero = Cliente.builder()
				.idCliente("0000000001")
				.nombre("Jose Juan")
				.apellidoPaterno("Sosa")
				.apellidoMaterno("Romero")
				.rfc("SORJ8204085P0")
				.curp("SORJ820408HDF")
				.fechaRegistro(new Date())
				.fechaActualizacion(null)
				.usuarioRegistra(USER_ALTA)
				.usuarioActualiza(null)
				.activo(true)
				.build();
	}
	
	@Test
	@DisplayName("Test para guardar un cliente.")
	void test_guardar_cliente() {
		// given
		// clienteCero

		// when
		Cliente clienteGuardado = clienteRepository.save(clienteCero);
		
		// then
		assertThat(clienteGuardado).isNotNull();
		assertThat(clienteGuardado.getIdCliente()).isEqualTo("0000000001");
	}
	
	@Test
	@DisplayName("Test para actualizar un cliente.")
	void test_actualizar_cliente() {
		Date fechaActualizacion = new Date();
		//given
		clienteRepository.save(clienteCero);
		
		//when
		Cliente clienteGuardado = clienteRepository.findById(clienteCero.getIdCliente()).get();
		clienteGuardado.setCurp("SORJ820408HDFSMN07");
		clienteGuardado.setFechaActualizacion(fechaActualizacion);
		clienteGuardado.setUsuarioActualiza("impersonator");
		Cliente clienteActualizado = clienteRepository.save(clienteGuardado);
		
		//then
		assertThat(clienteActualizado.getCurp()).isNotNull();
		assertThat(clienteActualizado.getCurp()).isEqualTo("SORJ820408HDFSMN07");
		assertThat(clienteActualizado.getFechaActualizacion()).isEqualTo(fechaActualizacion);
		assertThat(clienteActualizado.getUsuarioActualiza()).isEqualTo("impersonator");
	}
	
	@Test
	@DisplayName("Test para eliminar un cliente.")
	void test_eliminar_cliente() {
		Date fechaActualizacion = new Date();
		//given
		clienteRepository.save(clienteCero);
		
		//when
		Cliente clienteGuardado = clienteRepository.findById(clienteCero.getIdCliente()).get();
		clienteGuardado.setActivo(false);
		clienteGuardado.setFechaActualizacion(fechaActualizacion);
		clienteGuardado.setUsuarioActualiza("impersonator");
		Cliente clienteActualizado = clienteRepository.save(clienteGuardado);
		
		//then
		assertThat(clienteActualizado.getCurp()).isNotNull();
		assertThat(clienteActualizado.getActivo()).isFalse();
		assertThat(clienteActualizado.getFechaActualizacion()).isEqualTo(fechaActualizacion);
		assertThat(clienteActualizado.getUsuarioActualiza()).isEqualTo("impersonator");
	}
	
	@Test
	@DisplayName("Test para listar los clientes.")
	void test_listar_clientes() {
		//given
		Cliente clienteUno = Cliente.builder()
				.idCliente("0000000003")
				.nombre("Maria Fernanda")
				.apellidoPaterno("Sosa")
				.apellidoMaterno("Vazquez")
				.rfc("SOVF081221")
				.curp("SOVF081221MEM")
				.fechaRegistro(new Date())
				.fechaActualizacion(null)
				.usuarioRegistra(USER_ALTA)
				.usuarioActualiza(null)
				.activo(true)
				.build();
		
		Cliente clienteDos = Cliente.builder()
				.idCliente("0000000002")
				.nombre("Maria Jose")
				.apellidoPaterno("Sosa")
				.apellidoMaterno("Vazquez")
				.rfc("SOVM100423")
				.curp("SOVM1004236T0")
				.fechaRegistro(new Date())
				.fechaActualizacion(null)
				.usuarioRegistra(USER_ALTA)
				.usuarioActualiza(null)
				.activo(true)
				.build();
		
		clienteRepository.save(clienteCero);
		clienteRepository.save(clienteUno);
		clienteRepository.save(clienteDos);
		
		//when
		Page<Cliente> listaClientes = clienteRepository.findAll(PageRequest.of(0, 2, Sort.by("idCliente").ascending()));
		
		//then
		assertThat(listaClientes).isNotNull();
		assertThat(listaClientes.getSize()).isEqualTo(2);
		assertThat(listaClientes.getNumber()).isZero();
		assertThat(listaClientes.getNumberOfElements()).isEqualTo(2);
		assertThat(listaClientes.getSort().toString()).hasToString("idCliente: ASC");
		assertThat(listaClientes.getTotalElements()).isEqualTo(3);
		assertThat(listaClientes.getTotalPages()).isEqualTo(2);
		assertThat(listaClientes.getContent().get(0).getIdCliente()).isEqualTo("0000000001");
	}
	
	@Test
	@DisplayName("Test para obtener un cliente.")
	void test_obtener_cliente_por_id() {
		//given
		clienteRepository.save(clienteCero);
		
		//when
		Cliente clienteGuardado = clienteRepository.findById(clienteCero.getIdCliente()).orElse(null);
		
		//then
		assertThat(clienteGuardado).isNotNull();
		assertThat(clienteGuardado.getIdCliente()).isEqualTo("0000000001");
		assertThat(clienteGuardado.getActivo()).isTrue();
	}
}
