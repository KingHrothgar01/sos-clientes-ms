package com.sosa.service;

import static com.sosa.util.Constants.APPLICATION_MESSAGE_002;
import static com.sosa.util.Constants.APPLICATION_PARAMETER_LARGE_PAGE;
import static com.sosa.util.Constants.BUSINESS_MSG_ERR_C_001;
import static com.sosa.util.Constants.BUSINESS_MSG_ERR_C_003;
import static com.sosa.util.Constants.BUSINESS_MSG_ERR_C_006;
import static com.sosa.util.Constants.BUSINESS_MSG_ERR_C_009;
import static com.sosa.util.Constants.REGISTRO_ACTIVO;
import static com.sosa.util.Constants.REGISTRO_INACTIVO;

import static com.sosa.util.Constants.USER_ALTA;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.sosa.exception.HTTP400Exception;
import com.sosa.exception.HTTP404Exception;
import com.sosa.model.Cliente;
import com.sosa.model.dto.ClienteDTO;
import com.sosa.model.dto.PagingDTO;
import com.sosa.repository.ClienteRepository;

import io.micrometer.core.instrument.Metrics;

@Service
public class ClienteService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ClienteService.class);
	
	@Autowired
	private ClienteRepository clienteRepository;

	public Optional<ClienteDTO> saveCliente(ClienteDTO cliente) {
		Optional<Cliente> saved = clienteRepository.findById(cliente.getIdCliente());
	
		if (saved.isPresent())
			throw new HTTP400Exception(BUSINESS_MSG_ERR_C_001);
	
		ModelMapper mapper = new ModelMapper();
		Cliente model = mapper.map(cliente, Cliente.class);
		model.setFechaRegistro(new Date());
		model.setUsuarioRegistra(USER_ALTA);
		model.setActivo(REGISTRO_ACTIVO);
		return Optional.ofNullable(mapper.map(clienteRepository.save(model), ClienteDTO.class));
	}

	public Optional<ClienteDTO> findCliente(String id) {
		Optional<Cliente> model = clienteRepository.findById(id);
		
		if (model.isPresent()) {
			ModelMapper mapper = new ModelMapper();
			return Optional.ofNullable(mapper.map(model.get(), ClienteDTO.class));
		}else
			throw new HTTP404Exception(BUSINESS_MSG_ERR_C_003);
	}

	public Optional<ClienteDTO> updateCliente(ClienteDTO cliente) {
		Optional<Cliente> saved = clienteRepository.findById(cliente.getIdCliente());
		
		if (saved.isEmpty())
			throw new HTTP404Exception(BUSINESS_MSG_ERR_C_006);
		
		ModelMapper mapper = new ModelMapper();
		Cliente model = mapper.map(cliente, Cliente.class);
		model.setFechaRegistro(saved.get().getFechaRegistro());
		model.setUsuarioRegistra(saved.get().getUsuarioRegistra());
		model.setFechaActualizacion(new Date());
		model.setUsuarioActualiza(USER_ALTA);
		model.setActivo(saved.get().getActivo());
		return Optional.of(mapper.map(clienteRepository.save(model), ClienteDTO.class));
	}

	public Optional<ClienteDTO> deleteCliente(String idCliente) {
		Optional<Cliente> saved = clienteRepository.findById(idCliente);
		
		if (saved.isEmpty())
			throw new HTTP404Exception(BUSINESS_MSG_ERR_C_009);
		
		ModelMapper mapper = new ModelMapper();
		saved.get().setActivo(REGISTRO_INACTIVO);
		saved.get().setFechaActualizacion(new Date());
		saved.get().setUsuarioActualiza(USER_ALTA);
		Cliente model = mapper.map(saved, Cliente.class);
		return Optional.of(mapper.map(clienteRepository.save(model), ClienteDTO.class));
	}

	public Page<ClienteDTO> findAllClientes(PagingDTO dto) {
		Page<Cliente> pageOfClients = null;
		
		if (dto.getOrder().isAscending())
			pageOfClients = clienteRepository.findAll(PageRequest.of(
					dto.getPage(), dto.getSize(), Sort.by(dto.getProperty()).ascending()));
		else
			pageOfClients = clienteRepository.findAll(PageRequest.of(
					dto.getPage(), dto.getSize(), Sort.by(dto.getProperty()).descending()));
		
		if (dto.getSize() > APPLICATION_PARAMETER_LARGE_PAGE) {
			LOGGER.info(APPLICATION_MESSAGE_002);
			Metrics.counter("large_payload").increment();
		}
		return transform(pageOfClients);
	}
	
	private Page<ClienteDTO> transform (Page<Cliente> cliente) {
		return new PageImpl<>(
				getListClientes(cliente), cliente.getPageable(), cliente.getTotalElements());
	}
	
	private List<ClienteDTO> getListClientes(Page<Cliente> page){
		ModelMapper mapper = new ModelMapper();
		return page.getContent().stream().map(
				n -> mapper.map(n, ClienteDTO.class)).collect(Collectors.toList());
	}
}
