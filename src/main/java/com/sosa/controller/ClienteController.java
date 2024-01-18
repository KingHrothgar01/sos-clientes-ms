package com.sosa.controller;

import static com.sosa.util.Constants.BUSINESS_MSG_ERR_C_012;
import static com.sosa.util.Constants.BUSINESS_MSG_ERR_C_013;
import static com.sosa.util.Constants.BUSINESS_MSG_ERR_C_014;
import static com.sosa.util.Constants.BUSINESS_MSG_ERR_C_015;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.sosa.event.AbstractRestHandler;
import com.sosa.event.ClienteServiceEvent;
import com.sosa.exception.HTTP400Exception;
import com.sosa.exception.HTTP500Exception;
import com.sosa.model.ClienteModelAssembler;
import com.sosa.model.dto.ClienteDTO;
import com.sosa.model.dto.PagingDTO;
import com.sosa.service.ClienteService;

@RestController
@Validated
public class ClienteController extends AbstractRestHandler {

	@Autowired
	private ClienteService clienteService;
	
	@Autowired
	private ClienteModelAssembler clienteModelAssembler;
	
	@Autowired
	private PagedResourcesAssembler<ClienteDTO> pagedResourcesAssembler;

	@PostMapping(value = "/prestamos/v1/clientes", consumes = { "application/json", "application/xml" }, produces = { "application/json", "application/xml" })
	@ResponseStatus(HttpStatus.CREATED)
	public ClienteDTO createCliente(@Valid @RequestBody ClienteDTO dto, HttpServletRequest request, HttpServletResponse response) {
		dto = (this.clienteService.saveCliente(dto)).orElseThrow(
				() -> new HTTP500Exception(BUSINESS_MSG_ERR_C_012));
		eventPublisher.publishEvent(new ClienteServiceEvent(this, "ClienteCreado", dto));
		response.setHeader("Location", request.getRequestURL().append("/").append(dto.getIdCliente()).toString());
		return dto;
	}

	@GetMapping(value = "/prestamos/v1/clientes",  produces = "application/hal+json")
	@ResponseStatus(HttpStatus.OK)
	public @ResponseBody PagedModel<ClienteDTO> getAllClientes(
			@Valid @RequestParam(value = "page", defaultValue = DEFAULT_PAGE_NUM) @Min(value = 0, message = "El numero de p\u00E1gina debe ser mayor o igual a cero.") Integer page,
			@Valid @RequestParam(value = "size", defaultValue = DEFAULT_PAGE_SIZE) @Min(value = 0, message = "El tama\u00F1o de p\u00E1gina debe ser mayor o igual a cero.") Integer size,
			@Valid @RequestParam(value = "property", defaultValue = DEFAULT_PAGE_PROPERTY) String prop,
			@Valid @RequestParam(value = "sortOrder", defaultValue = DEFAULT_PAGE_ORDER) String sortOrder,
			HttpServletRequest request, HttpServletResponse response) {
		Page<ClienteDTO> clientePage = this.clienteService.findAllClientes(
				new PagingDTO(page, size, Direction.fromString(sortOrder.toUpperCase()), prop));
		return pagedResourcesAssembler.toModel(clientePage, clienteModelAssembler);
	}

	@GetMapping(value = "/prestamos/v1/clientes/{id}", produces = { "application/json", "application/xml" })
	@ResponseStatus(HttpStatus.OK)
	public @ResponseBody Optional<ClienteDTO> getCliente(@Valid @PathVariable("id") @NotBlank @Size(max =10, message = "El campo no debe exceder de 10 car\u00E1cteres.") String id, 
			HttpServletRequest request, HttpServletResponse response) {
		Optional <ClienteDTO> cliente = this.clienteService.findCliente(id);
		return checkResourceFound(cliente);
	}

	@PutMapping(value = "/prestamos/v1/clientes/{id}", consumes = { "application/json", "application/xml" }, produces = { "application/json", "application/xml" })
	@ResponseStatus(HttpStatus.OK)
	public ClienteDTO updateCliente(@Valid @PathVariable("id") @NotBlank @Size(max =10, message = "El campo no debe exceder de 10 car\u00E1cteres.") String id, 
			@Valid @RequestBody ClienteDTO dto, HttpServletRequest request, HttpServletResponse response) {
		if (!id.equals(dto.getIdCliente()))
			throw new HTTP400Exception(BUSINESS_MSG_ERR_C_015);
		ClienteDTO objetoActualizado = this.clienteService.updateCliente(dto).orElseThrow(
				() -> new HTTP500Exception(BUSINESS_MSG_ERR_C_013));
		eventPublisher.publishEvent(new ClienteServiceEvent(this, "ClienteActualizado", objetoActualizado));
		return objetoActualizado;
	}

	@DeleteMapping(value = "/prestamos/v1/clientes/{id}", produces = { "application/json", "application/xml" })
	@ResponseStatus(HttpStatus.OK)
	public ClienteDTO deleteCliente(@Valid @PathVariable("id") @NotBlank @Size(max =10, message = "El campo no debe exceder de 10 car\u00E1cteres.") String id, 
			HttpServletRequest request, HttpServletResponse response) {
		ClienteDTO objetoEliminado = this.clienteService.deleteCliente(id).orElseThrow(
				() -> new HTTP500Exception(BUSINESS_MSG_ERR_C_014));
		eventPublisher.publishEvent(new ClienteServiceEvent(this, "ClienteEliminado", objetoEliminado));
		return objetoEliminado;
	}
}
