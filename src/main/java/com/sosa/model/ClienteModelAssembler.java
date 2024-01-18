package com.sosa.model;

import org.modelmapper.ModelMapper;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import com.sosa.controller.ClienteController;
import com.sosa.model.dto.ClienteDTO;

@Component
public class ClienteModelAssembler extends RepresentationModelAssemblerSupport<ClienteDTO, ClienteDTO> {
	
	public ClienteModelAssembler() {
		super(ClienteController.class, ClienteDTO.class);
	}

	@Override
	public ClienteDTO toModel(ClienteDTO entity) {
		ModelMapper mapper = new ModelMapper();
		return mapper.map(entity, ClienteDTO.class);
	}

}
