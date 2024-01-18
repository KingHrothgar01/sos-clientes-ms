package com.sosa.service;

import static com.sosa.util.Constants.BUSINESS_MSG_ERR_C_001;
import static com.sosa.util.Constants.BUSINESS_MSG_ERR_C_003;
import static com.sosa.util.Constants.BUSINESS_MSG_ERR_C_009;
import static com.sosa.util.Constants.USER_ALTA;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.BDDAssertions.then;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

import com.sosa.exception.HTTP400Exception;
import com.sosa.exception.HTTP404Exception;
import com.sosa.model.Cliente;
import com.sosa.model.dto.ClienteDTO;
import com.sosa.model.dto.PagingDTO;
import com.sosa.repository.ClienteRepository;

@ExtendWith(MockitoExtension.class)
class ClientesServiceTests {
	
	@Mock
	ClienteRepository clienteRepository;
	
	@InjectMocks
	ClienteService clienteService;
	
	private ClienteDTO dto;
	
	private Cliente model;
	
	private PagingDTO paging;

	@BeforeEach
	void setUp() throws Exception {
		dto = ClienteDTO.builder()
				.idCliente("000001")
				.nombre("Juan")
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
		
		ModelMapper mapper = new ModelMapper();
		model = mapper.map(dto, Cliente.class);
	}
	
	@Test
	@DisplayName("Test para guardar un cliente.")
	void test_guardar_cliente() {
		//given
		given(clienteRepository.findById(dto.getIdCliente())).willReturn(Optional.empty());
		given(clienteRepository.save(any(Cliente.class))).willReturn(model);
		
		//when
		ClienteDTO clienteGuardado = (clienteService.saveCliente(dto)).orElse(null);
		
		//then
		then(clienteGuardado).isNotNull();
		then(clienteGuardado.getIdCliente()).isEqualTo("000001");
		then(clienteGuardado.getActivo()).isTrue();
	}
	
	@Test
	@DisplayName("Test para guardar un cliente que ha sido guardado previamente - Escenario de error 1.")
	void test_guardar_cliente_error_1() {
		//given
		given(clienteRepository.findById(anyString())).willReturn(Optional.of(model));
		
		//when
		HTTP400Exception thrown = assertThrows(HTTP400Exception.class, () -> {
			clienteService.saveCliente(dto);
		});
		
		//then
		assertTrue(thrown.getMessage().contains(BUSINESS_MSG_ERR_C_001));
		verify(clienteRepository, never()).save(any(Cliente.class));
	}
	
	@Test
	@DisplayName("Test para obtener cliente por id.")
	void test_obtener_cliente() {
		//given
		given(clienteRepository.findById(model.getIdCliente())).willReturn(Optional.of(model));
		
		//when
		Optional<ClienteDTO> clientes = clienteService.findCliente(model.getIdCliente());
		
		//then
		assertThat(clientes).isPresent();
		assertThat(clientes.get().getIdCliente()).isEqualTo("000001");
	}
	
	@Test
	@DisplayName("Test para obtener cliente por id, cliente no encontrado - Escenario de error 1.")
	void test_obtener_cliente_error_1() {
		//given
		given(clienteRepository.findById(model.getIdCliente())).willReturn(Optional.empty());
		
		//when
		HTTP404Exception thrown = assertThrows(HTTP404Exception.class, () -> {
			clienteService.findCliente("000001");
		});
		
		//then
		assertTrue(thrown.getMessage().contains(BUSINESS_MSG_ERR_C_003));
	}
	
	@Test
	@DisplayName("Test para eliminar un cliente por id.")
	void test_eliminar_cliente() {
		//given
		given(clienteRepository.findById(anyString())).willReturn(Optional.of(model));
		given(clienteRepository.save(any(Cliente.class))).willReturn(model);
		
		//when
		Optional<ClienteDTO> clienteBorrado = clienteService.deleteCliente("000001");
		
		//then
		verify(clienteRepository, times(1)).save(any(Cliente.class));
		then(clienteBorrado).isNotNull();
		then(clienteBorrado).isPresent();
		then(clienteBorrado.get().getActivo()).isFalse();
	}
	
	@Test
	@DisplayName("Test para eliminar un cliente por id, cliente no encontrado - Escenario de error 1.")
	void test_eliminar_cliente_error_1() {
		//given
		given(clienteRepository.findById(anyString())).willReturn(Optional.empty());
		
		//when
		HTTP404Exception thrown = assertThrows(HTTP404Exception.class, () -> {
			clienteService.deleteCliente("000001");
		});
		
		//then
		assertTrue(thrown.getMessage().contains(BUSINESS_MSG_ERR_C_009));
	}

	@Test
	@DisplayName("Test para actualizar un cliente por id.")
	void test_actualiza_cliente() {
		//given
		given(clienteRepository.findById(anyString())).willReturn(Optional.of(model));
		given(clienteRepository.save(any(Cliente.class))).willReturn(model);
		
		//when
		Optional<ClienteDTO> tasaActualizada = clienteService.updateCliente(dto);
		
		//then
		assertThat(tasaActualizada).isPresent();
	}
	
	@Test
	@DisplayName("Test para actualizar un cliente por id, cliente no encontrado - Escenario de error 1.")
	void test_actualiza_cliente_error_1() {
		//given
		given(clienteRepository.findById(anyString())).willReturn(Optional.empty());
		
		//when
		assertThrows(HTTP404Exception.class, () -> {
			clienteService.updateCliente(dto);
		});
		
		//then
		verify(clienteRepository, never()).save(any(Cliente.class));
	}

	@Test
	@DisplayName("Test para listar todos los clientes existentes de forma ascendente.")
	void test_listar_clientes_ascendente() {
		//given
		paging = new PagingDTO();
		paging.setPage(0);
		paging.setSize(2);
		paging.setOrder(Direction.ASC);
		paging.setProperty("idCliente");
		
		Cliente clienteNumberOne = Cliente.builder()
				.idCliente("000002")
				.nombre("Sonia")
				.apellidoPaterno("Sosa")
				.apellidoMaterno("Romero")
				.rfc("SORS790926")
				.curp("SORS790926MDF")
				.fechaRegistro(new Date())
				.fechaActualizacion(null)
				.usuarioRegistra(USER_ALTA)
				.usuarioActualiza(null)
				.activo(true)
				.build();
		
		Cliente clienteNumberTwo = Cliente.builder()
				.idCliente("000003")
				.nombre("Maria")
				.apellidoPaterno("Sosa")
				.apellidoMaterno("Romero")
				.rfc("SORG931026")
				.curp("SORG931026MDF")
				.fechaRegistro(new Date())
				.fechaActualizacion(null)
				.usuarioRegistra(USER_ALTA)
				.usuarioActualiza(null)
				.activo(true)
				.build();
		
		PageImpl<Cliente> pagina = new PageImpl<Cliente>(
				List.of(clienteNumberOne, clienteNumberTwo),PageRequest.of(paging.getPage(), paging.getSize(), Sort.by(paging.getProperty()).ascending()), 3);
		
		given(clienteRepository.findAll(PageRequest.of(paging.getPage(), paging.getSize(), Sort.by(paging.getProperty()).ascending()))).willReturn(pagina);
		
		//when
		Page<ClienteDTO> clientes = clienteService.findAllClientes(paging);
		
		//then
		assertThat(clientes).isNotNull();
		assertThat(clientes.getNumberOfElements()).isEqualTo(2);
		assertThat(clientes.getTotalPages()).isEqualTo(2);
		assertThat(clientes.getNumber()).isZero();
		assertThat(clientes.getTotalElements()).isEqualTo(3);
		assertThat(clientes.getSort()).isEqualTo(Sort.by("idCliente").ascending());
	}
	
	@Test
	@DisplayName("Test para listar todos los clientes existentes de forma descendente.")
	void test_listar_clientes_descendente() {
		//given
		paging = new PagingDTO();
		paging.setPage(0);
		paging.setSize(2);
		paging.setOrder(Direction.DESC);
		paging.setProperty("idCliente");
		
		Cliente clienteNumberOne = Cliente.builder()
				.idCliente("000002")
				.nombre("Sonia")
				.apellidoPaterno("Sosa")
				.apellidoMaterno("Romero")
				.rfc("SORS790926")
				.curp("SORS790926MDF")
				.fechaRegistro(new Date())
				.fechaActualizacion(null)
				.usuarioRegistra(USER_ALTA)
				.usuarioActualiza(null)
				.activo(true)
				.build();
		
		Cliente clienteNumberTwo = Cliente.builder()
				.idCliente("000003")
				.nombre("Maria")
				.apellidoPaterno("Sosa")
				.apellidoMaterno("Romero")
				.rfc("SORG931026")
				.curp("SORG931026MDF")
				.fechaRegistro(new Date())
				.fechaActualizacion(null)
				.usuarioRegistra(USER_ALTA)
				.usuarioActualiza(null)
				.activo(true)
				.build();
		
		PageImpl<Cliente> pagina = new PageImpl<Cliente>(
				List.of(clienteNumberTwo, clienteNumberOne),PageRequest.of(paging.getPage(), paging.getSize(), Sort.by(paging.getProperty()).descending()), 3);
		
		given(clienteRepository.findAll(PageRequest.of(paging.getPage(), paging.getSize(), Sort.by(paging.getProperty()).descending()))).willReturn(pagina);
		
		//when
		Page<ClienteDTO> clientes = clienteService.findAllClientes(paging);
		
		//then
		assertThat(clientes).isNotNull();
		assertThat(clientes.getNumberOfElements()).isEqualTo(2);
		assertThat(clientes.getTotalPages()).isEqualTo(2);
		assertThat(clientes.getNumber()).isZero();
		assertThat(clientes.getTotalElements()).isEqualTo(3);
		assertThat(clientes.getSort()).isEqualTo(Sort.by("idCliente").descending());
	}
	
	@Test
	@DisplayName("Test para listar todos los clientes existentes de forma ascendente con numero de pagina mayor a 50.")
	void test_listar_clientes_ascendente_large_page() {
		//given
		paging = new PagingDTO();
		paging.setPage(0);
		paging.setSize(52);
		paging.setOrder(Direction.ASC);
		paging.setProperty("idCliente");
		
		Cliente clienteNumberOne = Cliente.builder()
				.idCliente("000002")
				.nombre("Sonia")
				.apellidoPaterno("Sosa")
				.apellidoMaterno("Romero")
				.rfc("SORS790926")
				.curp("SORS790926MDF")
				.fechaRegistro(new Date())
				.fechaActualizacion(null)
				.usuarioRegistra(USER_ALTA)
				.usuarioActualiza(null)
				.activo(true)
				.build();
		
		Cliente clienteNumberTwo = Cliente.builder()
				.idCliente("000003")
				.nombre("Maria")
				.apellidoPaterno("Sosa")
				.apellidoMaterno("Romero")
				.rfc("SORG931026")
				.curp("SORG931026MDF")
				.fechaRegistro(new Date())
				.fechaActualizacion(null)
				.usuarioRegistra(USER_ALTA)
				.usuarioActualiza(null)
				.activo(true)
				.build();
		
		PageImpl<Cliente> pagina = new PageImpl<Cliente>(
				List.of(clienteNumberOne, clienteNumberTwo),PageRequest.of(paging.getPage(), paging.getSize(), Sort.by(paging.getProperty()).ascending()), 2);
		
		given(clienteRepository.findAll(PageRequest.of(paging.getPage(), paging.getSize(), Sort.by(paging.getProperty()).ascending()))).willReturn(pagina);
		
		//when
		Page<ClienteDTO> clientes = clienteService.findAllClientes(paging);
		
		//then
		assertThat(clientes).isNotNull();
		assertThat(clientes.getNumberOfElements()).isEqualTo(2);
		assertThat(clientes.getTotalPages()).isEqualTo(1);
		assertThat(clientes.getNumber()).isZero();
		assertThat(clientes.getTotalElements()).isEqualTo(2);
		assertThat(clientes.getSort()).isEqualTo(Sort.by("idCliente").ascending());
	}
	
	@Test
	@DisplayName("Test para listar todos los clientes existentes, lista vacía - Escenario de error 1.")
	void test_listar_clientes_error_1() {
		//given
		paging = new PagingDTO();
		paging.setPage(0);
		paging.setSize(2);
		paging.setOrder(Direction.DESC);
		paging.setProperty("idCliente");
		
		PageImpl<Cliente> pagina = new PageImpl<Cliente>(
				List.of(),PageRequest.of(paging.getPage(), paging.getSize(), Sort.by(paging.getProperty()).descending()), 0);
		
		given(clienteRepository.findAll(PageRequest.of(paging.getPage(), paging.getSize(), Sort.by(paging.getProperty()).descending()))).willReturn(pagina);
		
		//when
		Page<ClienteDTO> clientes = clienteService.findAllClientes(paging);
		
		//then
		assertThat(clientes).isNotNull();
		assertThat(clientes).isEmpty();
		assertThat(clientes.getNumberOfElements()).isZero();
		assertThat(clientes.getTotalPages()).isZero();
		assertThat(clientes.getNumber()).isZero();
		assertThat(clientes.getTotalElements()).isZero();
		assertThat(clientes.getSort()).isEqualTo(Sort.by("idCliente").descending());
	}

}