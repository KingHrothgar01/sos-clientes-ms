package com.sosa.controller;


import static com.sosa.util.Constants.BUSINESS_MSG_ERR_C_002;
import static com.sosa.util.Constants.BUSINESS_MSG_ERR_C_004;
import static com.sosa.util.Constants.BUSINESS_MSG_ERR_C_005;
import static com.sosa.util.Constants.BUSINESS_MSG_ERR_C_008;
import static com.sosa.util.Constants.BUSINESS_MSG_ERR_C_010;
import static com.sosa.util.Constants.BUSINESS_MSG_ERR_C_012;
import static com.sosa.util.Constants.BUSINESS_MSG_ERR_C_013;
import static com.sosa.util.Constants.BUSINESS_MSG_ERR_C_014;
import static com.sosa.util.Constants.HTTP_MSG_400;
import static com.sosa.util.Constants.HTTP_MSG_404;
import static com.sosa.util.Constants.HTTP_MSG_500;
import static com.sosa.util.Constants.USER_ALTA;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.validation.ConstraintViolationException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.PagedModel.PageMetadata;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.CannotCreateTransactionException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sosa.event.GlobalExceptionHandler;
import com.sosa.exception.HTTP400Exception;
import com.sosa.exception.HTTP404Exception;
import com.sosa.exception.HTTP500Exception;
import com.sosa.model.ClienteModelAssembler;
import com.sosa.model.dto.ClienteDTO;
import com.sosa.model.dto.PagingDTO;
import com.sosa.service.ClienteService;

@WebMvcTest(controllers = ClientesControllerTests.class)
@ActiveProfiles("test")
@ContextConfiguration(classes = {ClienteController.class, GlobalExceptionHandler.class, ClienteModelAssembler.class})
class ClientesControllerTests {
	
	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
	private ClienteService clienteService;
	
	@MockBean
	private PagedResourcesAssembler<ClienteDTO> pagedResourcesAssembler;
	
	private ClienteDTO dto;
	
	@BeforeEach
	void setup() {
		LocalDateTime localDateTime = LocalDateTime.of(2024, Month.JANUARY, 1, 10, 10, 30);
		Instant instant = localDateTime.atZone(ZoneId.systemDefault()).toInstant();
		Date date = Date.from(instant);
		
		dto = ClienteDTO.builder()
				.idCliente("000001")
				.nombre("Juan")
				.apellidoPaterno("Sosa")
				.apellidoMaterno("Romero")
				.rfc("SORJ8204085P0")
				.curp("SORJ820408HDF")
				.fechaRegistro(date)
				.fechaActualizacion(null)
				.usuarioRegistra(USER_ALTA)
				.usuarioActualiza(null)
				.activo(true)
				.build();
	}

	@Test
	@DisplayName("Test para guardar un cliente.")
	void test_crear_cliente() throws Exception{
		// given
		given(clienteService.saveCliente(any(ClienteDTO.class)))
			.willAnswer((invocation) -> Optional.ofNullable(invocation.getArgument(0)));
		
		// when
		ObjectMapper mapper = new ObjectMapper();
		ResultActions response = mockMvc.perform(post("/prestamos/v1/clientes")
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(dto)));
		
		// then
		response.andDo(print()).andExpect(status().isCreated())
			.andExpect(content().contentType("application/json"))
			.andExpect(header().exists("Location"))
			.andExpect(jsonPath("$.id-cliente", is("000001")));
	}
	
	@Test
	@DisplayName("Test para guardar un cliente, DTO nulo - Escenario de error 1.")
	void test_crear_cliente_error_1() throws Exception {
		// given

		// when
		ResultActions response = mockMvc.perform(post("/prestamos/v1/clientes")
			   .contentType(MediaType.APPLICATION_JSON));
		
		// then
		response.andDo(print())
		        .andExpect(status().isBadRequest())
			    .andExpect(result -> assertTrue(result.getResolvedException() instanceof HttpMessageNotReadableException))
			    .andExpect(result -> assertEquals("Required request body is missing: public com.sosa.model.dto.ClienteDTO com.sosa.controller.ClienteController.createCliente(com.sosa.model.dto.ClienteDTO,javax.servlet.http.HttpServletRequest,javax.servlet.http.HttpServletResponse)", result.getResolvedException().getMessage()))
			    .andExpect(jsonPath("$.error-detail", is(BUSINESS_MSG_ERR_C_002)))
			    .andExpect(jsonPath("$.error-message", is(HTTP_MSG_400)));
	}
	
	@Test
	@DisplayName("Test para guardar un cliente, Nombre nulo - Escenario de error 2.")
	void test_crear_cliente_error_2() throws Exception {
		// given
		dto.setNombre(null);

		// when
		ObjectMapper mapper = new ObjectMapper();
		ResultActions response = mockMvc.perform(post("/prestamos/v1/clientes")
			   .contentType(MediaType.APPLICATION_JSON)
			   .content(mapper.writeValueAsString(dto)));
		
		// then
		response.andDo(print())
		        .andExpect(status().isBadRequest())
			    .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException))
			    .andExpect(result -> assertEquals("Validation failed for argument [0] in public com.sosa.model.dto.ClienteDTO com.sosa.controller.ClienteController.createCliente(com.sosa.model.dto.ClienteDTO,javax.servlet.http.HttpServletRequest,javax.servlet.http.HttpServletResponse): [Field error in object 'clienteDTO' on field 'nombre': rejected value [null]; codes [NotBlank.clienteDTO.nombre,NotBlank.nombre,NotBlank.java.lang.String,NotBlank]; arguments [org.springframework.context.support.DefaultMessageSourceResolvable: codes [clienteDTO.nombre,nombre]; arguments []; default message [nombre]]; default message [El nombre es requerido.]] ", result.getResolvedException().getMessage()))
			    .andExpect(jsonPath("$.error-detail", is(BUSINESS_MSG_ERR_C_002)))
			    .andExpect(jsonPath("$.error-message", is(HTTP_MSG_400)));
	}
	
	@Test
	@DisplayName("Test para guardar un cliente, Nombre vacío - Escenario de error 3.")
	void test_crear_cliente_error_3() throws Exception {
		// given
		dto.setNombre(" ");

		// when
		ObjectMapper mapper = new ObjectMapper();
		ResultActions response = mockMvc.perform(post("/prestamos/v1/clientes")
			   .contentType(MediaType.APPLICATION_JSON)
			   .content(mapper.writeValueAsString(dto)));
		
		// then
		response.andDo(print())
		        .andExpect(status().isBadRequest())
			    .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException))
			    .andExpect(result -> assertEquals("Validation failed for argument [0] in public com.sosa.model.dto.ClienteDTO com.sosa.controller.ClienteController.createCliente(com.sosa.model.dto.ClienteDTO,javax.servlet.http.HttpServletRequest,javax.servlet.http.HttpServletResponse): [Field error in object 'clienteDTO' on field 'nombre': rejected value [ ]; codes [NotBlank.clienteDTO.nombre,NotBlank.nombre,NotBlank.java.lang.String,NotBlank]; arguments [org.springframework.context.support.DefaultMessageSourceResolvable: codes [clienteDTO.nombre,nombre]; arguments []; default message [nombre]]; default message [El nombre es requerido.]] ", result.getResolvedException().getMessage()))
			    .andExpect(jsonPath("$.error-detail", is(BUSINESS_MSG_ERR_C_002)))
			    .andExpect(jsonPath("$.error-message", is(HTTP_MSG_400)));
	}
	
	@Test
	@DisplayName("Test para guardar un cliente, Apellido Paterno nulo - Escenario de error 4.")
	void test_crear_cliente_error_4() throws Exception {
		// given
		dto.setApellidoPaterno(null);

		// when
		ObjectMapper mapper = new ObjectMapper();
		ResultActions response = mockMvc.perform(post("/prestamos/v1/clientes")
			   .contentType(MediaType.APPLICATION_JSON)
			   .content(mapper.writeValueAsString(dto)));
		
		// then
		response.andDo(print())
		        .andExpect(status().isBadRequest())
			    .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException))
			    .andExpect(result -> assertEquals("Validation failed for argument [0] in public com.sosa.model.dto.ClienteDTO com.sosa.controller.ClienteController.createCliente(com.sosa.model.dto.ClienteDTO,javax.servlet.http.HttpServletRequest,javax.servlet.http.HttpServletResponse): [Field error in object 'clienteDTO' on field 'apellidoPaterno': rejected value [null]; codes [NotBlank.clienteDTO.apellidoPaterno,NotBlank.apellidoPaterno,NotBlank.java.lang.String,NotBlank]; arguments [org.springframework.context.support.DefaultMessageSourceResolvable: codes [clienteDTO.apellidoPaterno,apellidoPaterno]; arguments []; default message [apellidoPaterno]]; default message [El apellido paterno es requerido.]] ", result.getResolvedException().getMessage()))
			    .andExpect(jsonPath("$.error-detail", is(BUSINESS_MSG_ERR_C_002)))
			    .andExpect(jsonPath("$.error-message", is(HTTP_MSG_400)));
	}
	
	@Test
	@DisplayName("Test para guardar un cliente, Apellido Paterno vacío - Escenario de error 5.")
	void test_crear_cliente_error_5() throws Exception {
		// given
		dto.setApellidoPaterno(" ");

		// when
		ObjectMapper mapper = new ObjectMapper();
		ResultActions response = mockMvc.perform(post("/prestamos/v1/clientes")
			   .contentType(MediaType.APPLICATION_JSON)
			   .content(mapper.writeValueAsString(dto)));
		
		// then
		response.andDo(print())
		        .andExpect(status().isBadRequest())
			    .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException))
			    .andExpect(result -> assertEquals("Validation failed for argument [0] in public com.sosa.model.dto.ClienteDTO com.sosa.controller.ClienteController.createCliente(com.sosa.model.dto.ClienteDTO,javax.servlet.http.HttpServletRequest,javax.servlet.http.HttpServletResponse): [Field error in object 'clienteDTO' on field 'apellidoPaterno': rejected value [ ]; codes [NotBlank.clienteDTO.apellidoPaterno,NotBlank.apellidoPaterno,NotBlank.java.lang.String,NotBlank]; arguments [org.springframework.context.support.DefaultMessageSourceResolvable: codes [clienteDTO.apellidoPaterno,apellidoPaterno]; arguments []; default message [apellidoPaterno]]; default message [El apellido paterno es requerido.]] ", result.getResolvedException().getMessage()))
			    .andExpect(jsonPath("$.error-detail", is(BUSINESS_MSG_ERR_C_002)))
			    .andExpect(jsonPath("$.error-message", is(HTTP_MSG_400)));
	}
	
	@Test
	@DisplayName("Test para guardar un cliente, RFC nulo - Escenario de error 6.")
	void test_crear_cliente_error_6() throws Exception {
		// given
		dto.setRfc(null);

		// when
		ObjectMapper mapper = new ObjectMapper();
		ResultActions response = mockMvc.perform(post("/prestamos/v1/clientes")
			   .contentType(MediaType.APPLICATION_JSON)
			   .content(mapper.writeValueAsString(dto)));
		
		// then
		response.andDo(print())
		        .andExpect(status().isBadRequest())
			    .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException))
			    .andExpect(result -> assertEquals("Validation failed for argument [0] in public com.sosa.model.dto.ClienteDTO com.sosa.controller.ClienteController.createCliente(com.sosa.model.dto.ClienteDTO,javax.servlet.http.HttpServletRequest,javax.servlet.http.HttpServletResponse): [Field error in object 'clienteDTO' on field 'rfc': rejected value [null]; codes [NotBlank.clienteDTO.rfc,NotBlank.rfc,NotBlank.java.lang.String,NotBlank]; arguments [org.springframework.context.support.DefaultMessageSourceResolvable: codes [clienteDTO.rfc,rfc]; arguments []; default message [rfc]]; default message [El RFC es requerido.]] ", result.getResolvedException().getMessage()))
			    .andExpect(jsonPath("$.error-detail", is(BUSINESS_MSG_ERR_C_002)))
			    .andExpect(jsonPath("$.error-message", is(HTTP_MSG_400)));
	}
	
	@Test
	@DisplayName("Test para guardar un cliente, RFC vacío - Escenario de error 7.")
	void test_crear_cliente_error_7() throws Exception {
		// given
		dto.setRfc(" ");

		// when
		ObjectMapper mapper = new ObjectMapper();
		ResultActions response = mockMvc.perform(post("/prestamos/v1/clientes")
			   .contentType(MediaType.APPLICATION_JSON)
			   .content(mapper.writeValueAsString(dto)));
		
		// then
		response.andDo(print())
		        .andExpect(status().isBadRequest())
			    .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException))
			    .andExpect(result -> assertEquals("Validation failed for argument [0] in public com.sosa.model.dto.ClienteDTO com.sosa.controller.ClienteController.createCliente(com.sosa.model.dto.ClienteDTO,javax.servlet.http.HttpServletRequest,javax.servlet.http.HttpServletResponse): [Field error in object 'clienteDTO' on field 'rfc': rejected value [ ]; codes [NotBlank.clienteDTO.rfc,NotBlank.rfc,NotBlank.java.lang.String,NotBlank]; arguments [org.springframework.context.support.DefaultMessageSourceResolvable: codes [clienteDTO.rfc,rfc]; arguments []; default message [rfc]]; default message [El RFC es requerido.]] ", result.getResolvedException().getMessage()))
			    .andExpect(jsonPath("$.error-detail", is(BUSINESS_MSG_ERR_C_002)))
			    .andExpect(jsonPath("$.error-message", is(HTTP_MSG_400)));
	}
	
	@Test
	@DisplayName("Test para guardar un cliente, CURP nulo - Escenario de error 8.")
	void test_crear_cliente_error_8() throws Exception {
		// given
		dto.setCurp(null);

		// when
		ObjectMapper mapper = new ObjectMapper();
		ResultActions response = mockMvc.perform(post("/prestamos/v1/clientes")
			   .contentType(MediaType.APPLICATION_JSON)
			   .content(mapper.writeValueAsString(dto)));
		
		// then
		response.andDo(print())
		        .andExpect(status().isBadRequest())
			    .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException))
			    .andExpect(result -> assertEquals("Validation failed for argument [0] in public com.sosa.model.dto.ClienteDTO com.sosa.controller.ClienteController.createCliente(com.sosa.model.dto.ClienteDTO,javax.servlet.http.HttpServletRequest,javax.servlet.http.HttpServletResponse): [Field error in object 'clienteDTO' on field 'curp': rejected value [null]; codes [NotBlank.clienteDTO.curp,NotBlank.curp,NotBlank.java.lang.String,NotBlank]; arguments [org.springframework.context.support.DefaultMessageSourceResolvable: codes [clienteDTO.curp,curp]; arguments []; default message [curp]]; default message [El CURP es requerido.]] ", result.getResolvedException().getMessage()))
			    .andExpect(jsonPath("$.error-detail", is(BUSINESS_MSG_ERR_C_002)))
			    .andExpect(jsonPath("$.error-message", is(HTTP_MSG_400)));
	}
	
	@Test
	@DisplayName("Test para guardar un cliente, CURP vacío - Escenario de error 9.")
	void test_crear_cliente_error_9() throws Exception {
		// given
		dto.setCurp(" ");

		// when
		ObjectMapper mapper = new ObjectMapper();
		ResultActions response = mockMvc.perform(post("/prestamos/v1/clientes")
			   .contentType(MediaType.APPLICATION_JSON)
			   .content(mapper.writeValueAsString(dto)));
		
		// then
		response.andDo(print())
		        .andExpect(status().isBadRequest())
			    .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException))
			    .andExpect(result -> assertEquals("Validation failed for argument [0] in public com.sosa.model.dto.ClienteDTO com.sosa.controller.ClienteController.createCliente(com.sosa.model.dto.ClienteDTO,javax.servlet.http.HttpServletRequest,javax.servlet.http.HttpServletResponse): [Field error in object 'clienteDTO' on field 'curp': rejected value [ ]; codes [NotBlank.clienteDTO.curp,NotBlank.curp,NotBlank.java.lang.String,NotBlank]; arguments [org.springframework.context.support.DefaultMessageSourceResolvable: codes [clienteDTO.curp,curp]; arguments []; default message [curp]]; default message [El CURP es requerido.]] ", result.getResolvedException().getMessage()))
			    .andExpect(jsonPath("$.error-detail", is(BUSINESS_MSG_ERR_C_002)))
			    .andExpect(jsonPath("$.error-message", is(HTTP_MSG_400)));
	}
	
	@Test
	@DisplayName("Test para guardar un cliente, Error en capa de servicio - Escenario de error 10.")
	void test_crear_cliente_error_10() throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		
		// given
		given(clienteService.saveCliente(any(ClienteDTO.class))).willReturn(Optional.empty());

		// when
		ResultActions response = mockMvc.perform(post("/prestamos/v1/clientes")
			   .contentType(MediaType.APPLICATION_JSON)
			   .content(mapper.writeValueAsString(dto)));
		
		// then
		response.andDo(print())
		        .andExpect(status().is5xxServerError())
			    .andExpect(result -> assertTrue(result.getResolvedException() instanceof HTTP500Exception))
			    .andExpect(result -> assertEquals(BUSINESS_MSG_ERR_C_012, result.getResolvedException().getMessage()))
			    .andExpect(jsonPath("$.error-detail", is(BUSINESS_MSG_ERR_C_012)))
			    .andExpect(jsonPath("$.error-message", is(HTTP_MSG_500)));
	}
	
	@Test
	@DisplayName("Test para manejar la excepción cuándo el cliente ya existe.")
	void test_crear_cliente_error_11() throws Exception{
		ObjectMapper mapper = new ObjectMapper();
		
		when(clienteService.saveCliente(any(ClienteDTO.class))).thenThrow(HTTP400Exception.class);
		
		ResultActions response = mockMvc.perform(post("/prestamos/v1/clientes", 1)
			    .contentType(MediaType.APPLICATION_JSON)
			    .content(mapper.writeValueAsString(dto)));
		
		response.andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(content().contentType("application/json"))
			.andExpect(jsonPath("$.error-message", is(HTTP_MSG_400)));
	}
	
	@Test
	@DisplayName("Test para manejar la excepción cuándo la DB es inalcanzable.")
	void test_crear_cliente_error_12() throws Exception{
		ObjectMapper mapper = new ObjectMapper();
		
		when(clienteService.saveCliente(any(ClienteDTO.class))).thenThrow(CannotCreateTransactionException.class);
		
		ResultActions response = mockMvc.perform(post("/prestamos/v1/clientes")
			    .contentType(MediaType.APPLICATION_JSON)
			    .content(mapper.writeValueAsString(dto)));
		
		response.andDo(print())
			.andExpect(status().is5xxServerError())
			.andExpect(result -> assertTrue(result.getResolvedException() instanceof CannotCreateTransactionException))
		    .andExpect(jsonPath("$.error-message", is(HTTP_MSG_500)));
	}
	
	@Test
	@DisplayName("Test para actualizar un cliente.")
	void test_actualizar_cliente() throws Exception{
		// given
		given(clienteService.updateCliente(any(ClienteDTO.class)))
			.willAnswer((invocation) -> Optional.ofNullable(invocation.getArgument(0)));
		
		// when
		ObjectMapper mapper = new ObjectMapper();
		ResultActions response = mockMvc.perform(put("/prestamos/v1/clientes/{id}", "000001")
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(dto)));
		
		// then
		response.andDo(print())
			.andExpect(status().isOk())
			.andExpect(content().contentType("application/json"))
			.andExpect(jsonPath("$.id-cliente", is("000001")));
	}
	
	@Test
	@DisplayName("Test para actualizar un cliente, DTO nulo - Escenario de error 1.")
	void test_actualizar_cliente_error_1() throws Exception {
		// given

		// when
		ResultActions response = mockMvc.perform(put("/prestamos/v1/clientes/{id}", "000001")
			   .contentType(MediaType.APPLICATION_JSON));
		
		// then
		response.andDo(print())
		        .andExpect(status().isBadRequest())
			    .andExpect(result -> assertTrue(result.getResolvedException() instanceof HttpMessageNotReadableException))
			    .andExpect(result -> assertEquals("Required request body is missing: public com.sosa.model.dto.ClienteDTO com.sosa.controller.ClienteController.updateCliente(java.lang.String,com.sosa.model.dto.ClienteDTO,javax.servlet.http.HttpServletRequest,javax.servlet.http.HttpServletResponse)", result.getResolvedException().getMessage()))
			    .andExpect(jsonPath("$.error-detail", is(BUSINESS_MSG_ERR_C_005)))
			    .andExpect(jsonPath("$.error-message", is(HTTP_MSG_400)));
	}
	
	@Test
	@DisplayName("Test para actualizar un cliente, Nombre nulo - Escenario de error 2.")
	void test_actualizar_cliente_error_2() throws Exception {
		// given
		dto.setNombre(null);

		// when
		ObjectMapper mapper = new ObjectMapper();
		ResultActions response = mockMvc.perform(put("/prestamos/v1/clientes/{id}", "000001")
			   .contentType(MediaType.APPLICATION_JSON)
			   .content(mapper.writeValueAsString(dto)));
		
		// then
		response.andDo(print())
		        .andExpect(status().isBadRequest())
			    .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException))
			    .andExpect(result -> assertEquals("Validation failed for argument [1] in public com.sosa.model.dto.ClienteDTO com.sosa.controller.ClienteController.updateCliente(java.lang.String,com.sosa.model.dto.ClienteDTO,javax.servlet.http.HttpServletRequest,javax.servlet.http.HttpServletResponse): [Field error in object 'clienteDTO' on field 'nombre': rejected value [null]; codes [NotBlank.clienteDTO.nombre,NotBlank.nombre,NotBlank.java.lang.String,NotBlank]; arguments [org.springframework.context.support.DefaultMessageSourceResolvable: codes [clienteDTO.nombre,nombre]; arguments []; default message [nombre]]; default message [El nombre es requerido.]] ", result.getResolvedException().getMessage()))
			    .andExpect(jsonPath("$.error-detail", is(BUSINESS_MSG_ERR_C_005)))
			    .andExpect(jsonPath("$.error-message", is(HTTP_MSG_400)));
	}
	
	@Test
	@DisplayName("Test para actualizar un cliente, Nombre vacío - Escenario de error 3.")
	void test_actualizar_cliente_error_3() throws Exception {
		// given
		dto.setNombre(" ");

		// when
		ObjectMapper mapper = new ObjectMapper();
		ResultActions response = mockMvc.perform(put("/prestamos/v1/clientes/{id}", "000001")
			   .contentType(MediaType.APPLICATION_JSON)
			   .content(mapper.writeValueAsString(dto)));
		
		// then
		response.andDo(print())
		        .andExpect(status().isBadRequest())
			    .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException))
			    .andExpect(result -> assertEquals("Validation failed for argument [1] in public com.sosa.model.dto.ClienteDTO com.sosa.controller.ClienteController.updateCliente(java.lang.String,com.sosa.model.dto.ClienteDTO,javax.servlet.http.HttpServletRequest,javax.servlet.http.HttpServletResponse): [Field error in object 'clienteDTO' on field 'nombre': rejected value [ ]; codes [NotBlank.clienteDTO.nombre,NotBlank.nombre,NotBlank.java.lang.String,NotBlank]; arguments [org.springframework.context.support.DefaultMessageSourceResolvable: codes [clienteDTO.nombre,nombre]; arguments []; default message [nombre]]; default message [El nombre es requerido.]] ", result.getResolvedException().getMessage()))
			    .andExpect(jsonPath("$.error-detail", is(BUSINESS_MSG_ERR_C_005)))
			    .andExpect(jsonPath("$.error-message", is(HTTP_MSG_400)));
	}
	
	@Test
	@DisplayName("Test para actualizar un cliente, Apellido Paterno nulo - Escenario de error 4.")
	void test_actualizar_cliente_error_4() throws Exception {
		// given
		dto.setApellidoPaterno(null);

		// when
		ObjectMapper mapper = new ObjectMapper();
		ResultActions response = mockMvc.perform(put("/prestamos/v1/clientes/{id}", "000001")
			   .contentType(MediaType.APPLICATION_JSON)
			   .content(mapper.writeValueAsString(dto)));
		
		// then
		response.andDo(print())
		        .andExpect(status().isBadRequest())
			    .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException))
			    .andExpect(result -> assertEquals("Validation failed for argument [1] in public com.sosa.model.dto.ClienteDTO com.sosa.controller.ClienteController.updateCliente(java.lang.String,com.sosa.model.dto.ClienteDTO,javax.servlet.http.HttpServletRequest,javax.servlet.http.HttpServletResponse): [Field error in object 'clienteDTO' on field 'apellidoPaterno': rejected value [null]; codes [NotBlank.clienteDTO.apellidoPaterno,NotBlank.apellidoPaterno,NotBlank.java.lang.String,NotBlank]; arguments [org.springframework.context.support.DefaultMessageSourceResolvable: codes [clienteDTO.apellidoPaterno,apellidoPaterno]; arguments []; default message [apellidoPaterno]]; default message [El apellido paterno es requerido.]] ", result.getResolvedException().getMessage()))
			    .andExpect(jsonPath("$.error-detail", is(BUSINESS_MSG_ERR_C_005)))
			    .andExpect(jsonPath("$.error-message", is(HTTP_MSG_400)));
	}
	
	@Test
	@DisplayName("Test para actualizar un cliente, Apellido Paterno vacío - Escenario de error 5.")
	void test_actualizar_cliente_error_5() throws Exception {
		// given
		dto.setApellidoPaterno(" ");

		// when
		ObjectMapper mapper = new ObjectMapper();
		ResultActions response = mockMvc.perform(put("/prestamos/v1/clientes/{id}", "000001")
			   .contentType(MediaType.APPLICATION_JSON)
			   .content(mapper.writeValueAsString(dto)));
		
		// then
		response.andDo(print())
		        .andExpect(status().isBadRequest())
			    .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException))
			    .andExpect(result -> assertEquals("Validation failed for argument [1] in public com.sosa.model.dto.ClienteDTO com.sosa.controller.ClienteController.updateCliente(java.lang.String,com.sosa.model.dto.ClienteDTO,javax.servlet.http.HttpServletRequest,javax.servlet.http.HttpServletResponse): [Field error in object 'clienteDTO' on field 'apellidoPaterno': rejected value [ ]; codes [NotBlank.clienteDTO.apellidoPaterno,NotBlank.apellidoPaterno,NotBlank.java.lang.String,NotBlank]; arguments [org.springframework.context.support.DefaultMessageSourceResolvable: codes [clienteDTO.apellidoPaterno,apellidoPaterno]; arguments []; default message [apellidoPaterno]]; default message [El apellido paterno es requerido.]] ", result.getResolvedException().getMessage()))
			    .andExpect(jsonPath("$.error-detail", is(BUSINESS_MSG_ERR_C_005)))
			    .andExpect(jsonPath("$.error-message", is(HTTP_MSG_400)));
	}
	
	@Test
	@DisplayName("Test para actualizar un cliente, RFC nulo - Escenario de error 6.")
	void test_actualizar_cliente_error_6() throws Exception {
		// given
		dto.setRfc(null);

		// when
		ObjectMapper mapper = new ObjectMapper();
		ResultActions response = mockMvc.perform(put("/prestamos/v1/clientes/{id}", "000001")
			   .contentType(MediaType.APPLICATION_JSON)
			   .content(mapper.writeValueAsString(dto)));
		
		// then
		response.andDo(print())
		        .andExpect(status().isBadRequest())
			    .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException))
			    .andExpect(result -> assertEquals("Validation failed for argument [1] in public com.sosa.model.dto.ClienteDTO com.sosa.controller.ClienteController.updateCliente(java.lang.String,com.sosa.model.dto.ClienteDTO,javax.servlet.http.HttpServletRequest,javax.servlet.http.HttpServletResponse): [Field error in object 'clienteDTO' on field 'rfc': rejected value [null]; codes [NotBlank.clienteDTO.rfc,NotBlank.rfc,NotBlank.java.lang.String,NotBlank]; arguments [org.springframework.context.support.DefaultMessageSourceResolvable: codes [clienteDTO.rfc,rfc]; arguments []; default message [rfc]]; default message [El RFC es requerido.]] ", result.getResolvedException().getMessage()))
			    .andExpect(jsonPath("$.error-detail", is(BUSINESS_MSG_ERR_C_005)))
			    .andExpect(jsonPath("$.error-message", is(HTTP_MSG_400)));
	}
	
	@Test
	@DisplayName("Test para actualizar un cliente, RFC vacío - Escenario de error 7.")
	void test_actualizar_cliente_error_7() throws Exception {
		// given
		dto.setRfc(" ");

		// when
		ObjectMapper mapper = new ObjectMapper();
		ResultActions response = mockMvc.perform(put("/prestamos/v1/clientes/{id}", "000001")
			   .contentType(MediaType.APPLICATION_JSON)
			   .content(mapper.writeValueAsString(dto)));
		
		// then
		response.andDo(print())
		        .andExpect(status().isBadRequest())
			    .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException))
			    .andExpect(result -> assertEquals("Validation failed for argument [1] in public com.sosa.model.dto.ClienteDTO com.sosa.controller.ClienteController.updateCliente(java.lang.String,com.sosa.model.dto.ClienteDTO,javax.servlet.http.HttpServletRequest,javax.servlet.http.HttpServletResponse): [Field error in object 'clienteDTO' on field 'rfc': rejected value [ ]; codes [NotBlank.clienteDTO.rfc,NotBlank.rfc,NotBlank.java.lang.String,NotBlank]; arguments [org.springframework.context.support.DefaultMessageSourceResolvable: codes [clienteDTO.rfc,rfc]; arguments []; default message [rfc]]; default message [El RFC es requerido.]] ", result.getResolvedException().getMessage()))
			    .andExpect(jsonPath("$.error-detail", is(BUSINESS_MSG_ERR_C_005)))
			    .andExpect(jsonPath("$.error-message", is(HTTP_MSG_400)));
	}
	
	@Test
	@DisplayName("Test para actualizar un cliente, CURP nulo - Escenario de error 8.")
	void test_actualizar_cliente_error_8() throws Exception {
		// given
		dto.setCurp(null);

		// when
		ObjectMapper mapper = new ObjectMapper();
		ResultActions response = mockMvc.perform(put("/prestamos/v1/clientes/{id}", "000001")
			   .contentType(MediaType.APPLICATION_JSON)
			   .content(mapper.writeValueAsString(dto)));
		
		// then
		response.andDo(print())
		        .andExpect(status().isBadRequest())
			    .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException))
			    .andExpect(result -> assertEquals("Validation failed for argument [1] in public com.sosa.model.dto.ClienteDTO com.sosa.controller.ClienteController.updateCliente(java.lang.String,com.sosa.model.dto.ClienteDTO,javax.servlet.http.HttpServletRequest,javax.servlet.http.HttpServletResponse): [Field error in object 'clienteDTO' on field 'curp': rejected value [null]; codes [NotBlank.clienteDTO.curp,NotBlank.curp,NotBlank.java.lang.String,NotBlank]; arguments [org.springframework.context.support.DefaultMessageSourceResolvable: codes [clienteDTO.curp,curp]; arguments []; default message [curp]]; default message [El CURP es requerido.]] ", result.getResolvedException().getMessage()))
			    .andExpect(jsonPath("$.error-detail", is(BUSINESS_MSG_ERR_C_005)))
			    .andExpect(jsonPath("$.error-message", is(HTTP_MSG_400)));
	}
	
	@Test
	@DisplayName("Test para actualizar un cliente, CURP vacío - Escenario de error 9.")
	void test_actualizar_cliente_error_9() throws Exception {
		// given
		dto.setCurp(" ");

		// when
		ObjectMapper mapper = new ObjectMapper();
		ResultActions response = mockMvc.perform(put("/prestamos/v1/clientes/{id}", "000001")
			   .contentType(MediaType.APPLICATION_JSON)
			   .content(mapper.writeValueAsString(dto)));
		
		// then
		response.andDo(print())
		        .andExpect(status().isBadRequest())
			    .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException))
			    .andExpect(result -> assertEquals("Validation failed for argument [1] in public com.sosa.model.dto.ClienteDTO com.sosa.controller.ClienteController.updateCliente(java.lang.String,com.sosa.model.dto.ClienteDTO,javax.servlet.http.HttpServletRequest,javax.servlet.http.HttpServletResponse): [Field error in object 'clienteDTO' on field 'curp': rejected value [ ]; codes [NotBlank.clienteDTO.curp,NotBlank.curp,NotBlank.java.lang.String,NotBlank]; arguments [org.springframework.context.support.DefaultMessageSourceResolvable: codes [clienteDTO.curp,curp]; arguments []; default message [curp]]; default message [El CURP es requerido.]] ", result.getResolvedException().getMessage()))
			    .andExpect(jsonPath("$.error-detail", is(BUSINESS_MSG_ERR_C_005)))
			    .andExpect(jsonPath("$.error-message", is(HTTP_MSG_400)));
	}
	
	@Test
	@DisplayName("Test para actualizar un cliente, Objeto actualizado vacío - Escenario de error 10.")
	void test_actualizar_cliente_error_10() throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		
		// given
		given(clienteService.updateCliente(any(ClienteDTO.class))).willReturn(Optional.empty());

		// when
		ResultActions response = mockMvc.perform(put("/prestamos/v1/clientes/{id}", "000001")
			   .contentType(MediaType.APPLICATION_JSON)
			   .content(mapper.writeValueAsString(dto)));
		
		// then
		response.andDo(print())
		        .andExpect(status().is5xxServerError())
			    .andExpect(result -> assertTrue(result.getResolvedException() instanceof HTTP500Exception))
			    .andExpect(result -> assertEquals(BUSINESS_MSG_ERR_C_013, result.getResolvedException().getMessage()))
			    .andExpect(jsonPath("$.error-detail", is(BUSINESS_MSG_ERR_C_013)))
			    .andExpect(jsonPath("$.error-message", is(HTTP_MSG_500)));
	}
	
	@Test
	@DisplayName("Test para manejar la excepción cuándo no coincide el identificador del objeto a actualizar.")
	void test_actualizar_cliente_error_11() throws Exception{
		ObjectMapper mapper = new ObjectMapper();
		// given
		
		// when
		ResultActions response = mockMvc.perform(put("/prestamos/v1/clientes/{id}", "000002")
			    .contentType(MediaType.APPLICATION_JSON)
			    .content(mapper.writeValueAsString(dto)));
		
		// then
		response.andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(content().contentType("application/json"))
			.andExpect(result -> assertTrue(result.getResolvedException() instanceof HTTP400Exception))
		    .andExpect(result -> assertEquals(BUSINESS_MSG_ERR_C_005, result.getResolvedException().getMessage()))
		    .andExpect(jsonPath("$.error-detail", is(BUSINESS_MSG_ERR_C_005)))
			.andExpect(jsonPath("$.error-message", is(HTTP_MSG_400)));
	}
	
	@Test
	@DisplayName("Test para actualizar un cliente, id excede longitud permitida - Escenario de error 12.")
	void test_actualizar_cliente_error_12() throws Exception {
		// given

		// when
		ObjectMapper mapper = new ObjectMapper();
		ResultActions response = mockMvc.perform(put("/prestamos/v1/clientes/{id}", "1000000000001")
			    .contentType(MediaType.APPLICATION_JSON)
			    .content(mapper.writeValueAsString(dto)));
		
		// then
		response.andDo(print())
		        .andExpect(status().isBadRequest())
			    .andExpect(result -> assertTrue(result.getResolvedException() instanceof ConstraintViolationException))
			    .andExpect(result -> assertEquals("updateCliente.id: El campo no debe exceder de 10 car\u00E1cteres.", result.getResolvedException().getMessage()))
			    .andExpect(jsonPath("$.error-message", is(HTTP_MSG_400)))
			    .andExpect(jsonPath("$.error-detail", is(BUSINESS_MSG_ERR_C_005)));
	}
	
	@Test
	@DisplayName("Test para actualizar un cliente, No existe el cliente - Escenario de error 13.")
	void test_actualizar_cliente_error_13() throws Exception{
		// given
		when(clienteService.updateCliente(any(ClienteDTO.class))).thenThrow(HTTP404Exception.class);
		
		// when
		ObjectMapper mapper = new ObjectMapper();
		ResultActions response = mockMvc.perform(put("/prestamos/v1/clientes/{id}", "000001")
			    .contentType(MediaType.APPLICATION_JSON)
			    .content(mapper.writeValueAsString(dto)));
		
		//then
		response.andDo(print())
			.andExpect(status().isNotFound())
			.andExpect(content().contentType("application/json"))
			.andExpect(result -> assertTrue(result.getResolvedException() instanceof HTTP404Exception))
			.andExpect(jsonPath("$.error-message", is(HTTP_MSG_404)));
	}
	
	@Test
	@DisplayName("Test para manejar la excepción de tipo HTTP400Exception - intento de modificación de datos inmutables.")
	void test_actualizar_cliente_error_14() throws Exception{
		ObjectMapper mapper = new ObjectMapper();
		
		when(clienteService.updateCliente(any(ClienteDTO.class))).thenThrow(HTTP400Exception.class);
		
		ResultActions response = mockMvc.perform(put("/prestamos/v1/clientes/{id}", "000001")
			    .contentType(MediaType.APPLICATION_JSON)
			    .content(mapper.writeValueAsString(dto)));
		
		response.andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(content().contentType("application/json"))
			.andExpect(jsonPath("$.error-message", is(HTTP_MSG_400)));
	}
	
	@Test
	@DisplayName("Test para manejar la excepción cuándo la DB es inalcanzable.")
	void test_actualizar_cliente_error_15() throws Exception{
		ObjectMapper mapper = new ObjectMapper();
		
		when(clienteService.updateCliente(any(ClienteDTO.class))).thenThrow(CannotCreateTransactionException.class);
		
		ResultActions response = mockMvc.perform(put("/prestamos/v1/clientes/{id}", "000001")
			    .contentType(MediaType.APPLICATION_JSON)
			    .content(mapper.writeValueAsString(dto)));
		
		response.andDo(print())
			.andExpect(status().is5xxServerError())
			.andExpect(content().contentType("application/json"))
			.andExpect(result -> assertTrue(result.getResolvedException() instanceof CannotCreateTransactionException))
		    .andExpect(jsonPath("$.error-message", is(HTTP_MSG_500)));
	}

	@Test
	@SuppressWarnings("unchecked")
	@DisplayName("Test para listar todos los clientes.")
	void test_listar_clientes() throws Exception{
		PagingDTO dto = new PagingDTO();
			dto.setPage(0);
			dto.setSize(2);
			dto.setOrder(Direction.ASC);
			dto.setProperty("idCliente");
		
		// given
		List<ClienteDTO> listaClientes = new ArrayList<>();
		listaClientes.add(ClienteDTO.builder().idCliente("000001").nombre("Juan").apellidoPaterno("Sosa").apellidoMaterno("Romero").rfc("SORJ8204085P0").curp("SORJ820408HDF").fechaRegistro(new Date()).usuarioRegistra(USER_ALTA).build());
		listaClientes.add(ClienteDTO.builder().idCliente("000002").nombre("Joselyne").apellidoPaterno("Vazquez").apellidoMaterno("Espinosa").rfc("VAEA840401").curp("VAEA840401MDF").fechaRegistro(new Date()).usuarioRegistra(USER_ALTA).build());
		listaClientes.add(ClienteDTO.builder().idCliente("000003").nombre("Mar\u00E1a Fernanda").apellidoPaterno("Sosa").apellidoMaterno("Vazquez").rfc("SOVF081221").curp("SOVF081221MEM").fechaRegistro(new Date()).usuarioRegistra(USER_ALTA).build());
		listaClientes.add(ClienteDTO.builder().idCliente("000004").nombre("Sonia").apellidoPaterno("Sosa").apellidoMaterno("Romero").rfc("SORS790926").curp("SORS790926MDF").fechaRegistro(new Date()).usuarioRegistra(USER_ALTA).build());
		listaClientes.add(ClienteDTO.builder().idCliente("000005").nombre("Mar\u00E1a").apellidoPaterno("Sosa").apellidoMaterno("Romero").rfc("SORG931026").curp("SORG931026MDF").fechaRegistro(new Date()).usuarioRegistra(USER_ALTA).build());
		PageImpl<ClienteDTO> page = new PageImpl<ClienteDTO>(listaClientes, PageRequest.of(dto.getPage(), dto.getSize(), Sort.by(dto.getProperty()).ascending()), 5);
		
		Link l[] = new Link[] {
				Link.of("http://192.168.100.65:8092/prestamos/v1/clientes?page=0&size=2&sort=idCliente,asc"),
				Link.of("http://192.168.100.65:8092/prestamos/v1/clientes?page=0&size=2&sort=idCliente,asc"),
				Link.of("http://192.168.100.65:8092/prestamos/v1/clientes?page=1&size=2&sort=idCliente,asc"),
				Link.of("http://192.168.100.65:8092/prestamos/v1/clientes?page=2&size=2&sort=idCliente,asc")};
		PagedModel<ClienteDTO> model = PagedModel.of(listaClientes, new PageMetadata(2, 0, 5, 3), l);
		
		given(clienteService.findAllClientes(any(PagingDTO.class))).willReturn(page);
		given(pagedResourcesAssembler.toModel(any(Page.class), any(ClienteModelAssembler.class))).willReturn(model);
		
		// when
		ResultActions response = mockMvc.perform(get("/prestamos/v1/clientes")
				.param("page", dto.getPage().toString())
				.param("size", dto.getSize().toString())
				.param("property", dto.getProperty())
				.param("sortOrder", dto.getOrder().toString()));
		
		// then
		response.andDo(print())
		        .andExpect(status().isOk())
		        .andExpect(jsonPath("$.page.size", is(2)))
		        .andExpect(jsonPath("$.page.number", is(0)))
		        .andExpect(jsonPath("$.page.totalElements", is(5)))
		        .andExpect(jsonPath("$.page.totalPages", is(3)));
	}
	
	@Test
	@SuppressWarnings("unchecked")
	@DisplayName("Test para listar clientes - lista vacía.")
	void test_listar_clientes_error_1() throws Exception{
		PagingDTO dto = new PagingDTO();
			dto.setPage(0);
			dto.setSize(2);
			dto.setOrder(Direction.ASC);
			dto.setProperty("idCliente");
		
		// given
		List<ClienteDTO> listaClientes = new ArrayList<>();
		PageImpl<ClienteDTO> page = new PageImpl<ClienteDTO>(listaClientes, PageRequest.of(dto.getPage(), dto.getSize(), Sort.by(dto.getProperty()).ascending()), 0);
		
		Link l[] = new Link[] {
				Link.of("http://192.168.100.65:8092/prestamos/v1/clientes?page=0&size=2&sort=idCliente,asc")};
		PagedModel<ClienteDTO> model = PagedModel.of(listaClientes, new PageMetadata(2, 0, 0, 0), l);
		
		given(clienteService.findAllClientes(any(PagingDTO.class))).willReturn(page);
		given(pagedResourcesAssembler.toModel(any(Page.class), any(ClienteModelAssembler.class))).willReturn(model);
		
		// when
		ResultActions response = mockMvc.perform(get("/prestamos/v1/clientes")
				.param("page", dto.getPage().toString())
				.param("size", dto.getSize().toString())
				.param("property", dto.getProperty())
				.param("sortOrder", dto.getOrder().toString()));
		
		// then
		response.andDo(print())
		        .andExpect(status().isOk())
		        .andExpect(jsonPath("$.page.size", is(2)))
		        .andExpect(jsonPath("$.page.number", is(0)))
		        .andExpect(jsonPath("$.page.totalElements", is(0)))
		        .andExpect(jsonPath("$.page.totalPages", is(0)));
	}
	
	@Test
	@DisplayName("Test para manejar una excepción en la capa de servicio.")
	void test_listar_clientes_error_2() throws Exception{
		// given
		when(clienteService.findAllClientes(any(PagingDTO.class))).thenThrow(RuntimeException.class);
		
		// when
		ResultActions response = mockMvc.perform(get("/prestamos/v1/clientes")
			.param("page", "0")
			.param("size", "2")
			.param("property", "idCliente")
			.param("sortOrder", Direction.ASC.toString()));
		
		// then
		response.andDo(print())
			.andExpect(status().is5xxServerError())
			.andExpect(result -> assertTrue(result.getResolvedException() instanceof RuntimeException))
		    .andExpect(jsonPath("$.error-message", is(HTTP_MSG_500)));
	}
	
	@Test
	@DisplayName("Test para listar clientes, número de p\u00E1gina menor a 0 - Escenario de error 3.")
	void test_listar_clientes_error_3() throws Exception {
		// given

		// when
		ResultActions response = mockMvc.perform(get("/prestamos/v1/clientes")
			.param("page", "-1")
			.param("size", "2")
			.param("property", "idCliente")
			.param("sortOrder", Direction.ASC.toString()));
		
		// then
		response.andDo(print())
	        .andExpect(status().isBadRequest())
		    .andExpect(result -> assertTrue(result.getResolvedException() instanceof ConstraintViolationException))
		    .andExpect(result -> assertEquals("getAllClientes.page: El numero de p\u00E1gina debe ser mayor o igual a cero.", result.getResolvedException().getMessage()))
		    .andExpect(jsonPath("$.error-message", is(HTTP_MSG_400)))
		    .andExpect(jsonPath("$.error-detail", is(BUSINESS_MSG_ERR_C_010)));
	}
	
	@Test
	@DisplayName("Test para listar clientes, tama\u00E1o de p\u00E1gina menor a 0 - Escenario de error 4.")
	void test_listar_clientes_error_4() throws Exception {
		// given

		// when
		ResultActions response = mockMvc.perform(get("/prestamos/v1/clientes")
				.param("page", "0")
				.param("size", "-1")
				.param("property", "idCliente")
				.param("sortOrder", Direction.ASC.toString()));
		
		// then
		response.andDo(print())
		        .andExpect(status().isBadRequest())
			    .andExpect(result -> assertTrue(result.getResolvedException() instanceof ConstraintViolationException))
			    .andExpect(result -> assertEquals("getAllClientes.size: El tama\u00F1o de p\u00E1gina debe ser mayor o igual a cero.", result.getResolvedException().getMessage()))
			    .andExpect(jsonPath("$.error-message", is(HTTP_MSG_400)))
			    .andExpect(jsonPath("$.error-detail", is(BUSINESS_MSG_ERR_C_010)));
	}
	
	@Test
	@DisplayName("Test para obtener un cliente.")
	void test_obtener_cliente() throws Exception {
		// given
		given(clienteService.findCliente(anyString())).willReturn(Optional.of(dto));
		
		// when
		ResultActions response = mockMvc.perform(get("/prestamos/v1/clientes/{id}", "000001")
				.contentType(MediaType.APPLICATION_JSON));
		
		// then
		response.andDo(print())
		        .andExpect(status().isOk())
		        .andExpect(jsonPath("$.curp", is("SORJ820408HDF")))
		        .andExpect(jsonPath("$.usuario-registra", is(USER_ALTA)))
		        .andExpect(jsonPath("$.fecha-registro").value("2024-01-01T16:10:30"))
		        .andExpect(jsonPath("$.activo").value("true"));
	}
	
	@Test
	@DisplayName("Test para obtener un cliente, id excede longitud permitida - Escenario de error 1.")
	void test_obtener_cliente_error_1() throws Exception {
		// given

		// when
		ResultActions response = mockMvc.perform(get("/prestamos/v1/clientes/{id}", "1000000000001"));
		
		// then
		response.andDo(print())
		        .andExpect(status().isBadRequest())
			    .andExpect(result -> assertTrue(result.getResolvedException() instanceof ConstraintViolationException))
			    .andExpect(result -> assertEquals("getCliente.id: El campo no debe exceder de 10 car\u00E1cteres.", result.getResolvedException().getMessage()))
			    .andExpect(jsonPath("$.error-message", is(HTTP_MSG_400)))
			    .andExpect(jsonPath("$.error-detail", is(BUSINESS_MSG_ERR_C_004)));
	}
	
	@Test
	@DisplayName("Test para obtener un cliente, no existe el cliente - Escenario de error 2.")
	void test_obtener_cliente_error_2() throws Exception{
		// given
		when(clienteService.findCliente(anyString())).thenThrow(HTTP404Exception.class);
		
		// when
		ResultActions response = mockMvc.perform(get("/prestamos/v1/clientes/{id}", "000001"));
		
		// then
		response.andDo(print())
			.andExpect(status().isNotFound())
			.andExpect(content().contentType("application/json"))
			.andExpect(result -> assertTrue(result.getResolvedException() instanceof HTTP404Exception))
			.andExpect(jsonPath("$.error-message", is(HTTP_MSG_404)));
	}
	
	@Test
	@DisplayName("Test para obtener un cliente, no existe el cliente - Escenario de error 3.")
	void test_obtener_cliente_error_3() throws Exception{
		// given
		given(clienteService.findCliente(anyString())).willReturn(Optional.empty());

		// when
		ResultActions response = mockMvc.perform(get("/prestamos/v1/clientes/{id}", "000001"));
		
		// then
		response.andDo(print())
			.andExpect(status().isNotFound())
			.andExpect(content().contentType("application/json"))
			.andExpect(result -> assertTrue(result.getResolvedException() instanceof HTTP404Exception))
			.andExpect(jsonPath("$.error-message", is(HTTP_MSG_404)));
	}
	
	@Test
	@DisplayName("Test para manejar la excepción cuándo la DB es inalcanzable.")
	void test_obtener_cliente_error_4() throws Exception{
		// given
		when(clienteService.findCliente(anyString())).thenThrow(CannotCreateTransactionException.class);
		
		// when
		ResultActions response = mockMvc.perform(get("/prestamos/v1/clientes/{id}", "000001"));
		
		// then
		response.andDo(print())
			.andExpect(status().is5xxServerError())
			.andExpect(result -> assertTrue(result.getResolvedException() instanceof CannotCreateTransactionException))
		    .andExpect(jsonPath("$.error-message", is(HTTP_MSG_500)));
	}
	
	@Test
	@DisplayName("Test para eliminar un cliente.")
	void test_eliminar_cliente() throws Exception{
		// given
		dto.setActivo(false);
		given(clienteService.deleteCliente("000001")).willReturn(Optional.of(dto));
		
		// when
		ResultActions response = mockMvc.perform(delete("/prestamos/v1/clientes/{id}", "000001"));
		
		// then
		response.andDo(print()).andExpect(status().isOk())
			.andExpect(content().contentType("application/json"))
			.andExpect(jsonPath("$.id-cliente", is("000001")))
			.andExpect(jsonPath("$.activo", is(false)));
	}
	
	@Test
	@DisplayName("Test para eliminar un cliente, id nulo - Escenario de error 1.")
	void test_eliminar_cliente_error_1() throws Exception {
		// given

		// when
		ResultActions response = mockMvc.perform(delete("/prestamos/v1/clientes"));
		
		// then
		response.andDo(print())
		        .andExpect(status().isBadRequest())
			    .andExpect(result -> assertTrue(result.getResolvedException() instanceof HttpRequestMethodNotSupportedException))
			    .andExpect(result -> assertEquals("Request method 'DELETE' not supported", result.getResolvedException().getMessage()))
			    .andExpect(jsonPath("$.error-message", is(HTTP_MSG_400)))
			    .andExpect(jsonPath("$.error-detail", is(BUSINESS_MSG_ERR_C_008)));
	}
	
	@Test
	@DisplayName("Test para eliminar un cliente, id vacío - Escenario de error 2.")
	void test_eliminar_cliente_error_2() throws Exception {
		// given

		// when
		ResultActions response = mockMvc.perform(delete("/prestamos/v1/clientes/{id}", ""));
		
		// then
		response.andDo(print())
		        .andExpect(status().isBadRequest())
			    .andExpect(result -> assertTrue(result.getResolvedException() instanceof HttpRequestMethodNotSupportedException))
			    .andExpect(result -> assertEquals("Request method 'DELETE' not supported", result.getResolvedException().getMessage()))
			    .andExpect(jsonPath("$.error-message", is(HTTP_MSG_400)))
			    .andExpect(jsonPath("$.error-detail", is(BUSINESS_MSG_ERR_C_008)));
	}
	
	@Test
	@DisplayName("Test para eliminar un cliente, id excede longitud permitida - Escenario de error 3.")
	void test_eliminar_cliente_error_3() throws Exception {
		// given

		// when
		ResultActions response = mockMvc.perform(delete("/prestamos/v1/clientes/{id}", "1000000000001"));
		
		// then
		response.andDo(print())
		        .andExpect(status().isBadRequest())
			    .andExpect(result -> assertTrue(result.getResolvedException() instanceof ConstraintViolationException))
			    .andExpect(result -> assertEquals("deleteCliente.id: El campo no debe exceder de 10 car\u00E1cteres.", result.getResolvedException().getMessage()))
			    .andExpect(jsonPath("$.error-message", is(HTTP_MSG_400)))
			    .andExpect(jsonPath("$.error-detail", is(BUSINESS_MSG_ERR_C_008)));
	}
	
	@Test
	@DisplayName("Test para eliminar un cliente, error en capa de servicio - Escenario de error 4.")
	void test_eliminar_cliente_error_4() throws Exception {
		// given
		given(clienteService.deleteCliente(anyString())).willReturn(Optional.empty());

		// when
		ObjectMapper mapper = new ObjectMapper();
		ResultActions response = mockMvc.perform(delete("/prestamos/v1/clientes/{id}", "000001")
			   .contentType(MediaType.APPLICATION_JSON)
			   .content(mapper.writeValueAsString(dto)));
		
		// then
		response.andDo(print())
        .andExpect(status().is5xxServerError())
	    .andExpect(result -> assertTrue(result.getResolvedException() instanceof HTTP500Exception))
	    .andExpect(result -> assertEquals(BUSINESS_MSG_ERR_C_014, result.getResolvedException().getMessage()))
	    .andExpect(jsonPath("$.error-detail", is(BUSINESS_MSG_ERR_C_014)))
	    .andExpect(jsonPath("$.error-message", is(HTTP_MSG_500)));
	}
	
	@Test
	@DisplayName("Test para eliminar un cliente, no existe el cliente - Escenario de error 5.")
	void test_eliminar_cliente_error_5() throws Exception{
		// given
		when(clienteService.deleteCliente(anyString())).thenThrow(HTTP404Exception.class);
		
		// when
		ResultActions response = mockMvc.perform(delete("/prestamos/v1/clientes/{id}", "000001"));
		
		// then
		response.andDo(print())
			.andExpect(status().isNotFound())
			.andExpect(content().contentType("application/json"))
			.andExpect(result -> assertTrue(result.getResolvedException() instanceof HTTP404Exception))
			.andExpect(jsonPath("$.error-message", is(HTTP_MSG_404)));
	}
	
	@Test
	@DisplayName("Test para manejar la excepción cuándo la DB es inalcanzable.")
	void test_eliminar_cliente_error_6() throws Exception{
		// given
		when(clienteService.deleteCliente(anyString())).thenThrow(CannotCreateTransactionException.class);
		
		// when
		ResultActions response = mockMvc.perform(delete("/prestamos/v1/clientes/{id}", "000001"));
		
		// then
		response.andDo(print())
			.andExpect(status().is5xxServerError())
			.andExpect(result -> assertTrue(result.getResolvedException() instanceof CannotCreateTransactionException))
		    .andExpect(jsonPath("$.error-message", is(HTTP_MSG_500)));
	}

}
