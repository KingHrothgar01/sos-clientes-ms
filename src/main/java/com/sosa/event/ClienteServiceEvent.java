package com.sosa.event;

import org.springframework.context.ApplicationEvent;

import com.sosa.model.dto.ClienteDTO;

import lombok.Getter;
import lombok.Setter;

@Getter 
@Setter
public class ClienteServiceEvent extends ApplicationEvent {

	private static final long serialVersionUID = -6121624308782503383L;
	
	private ClienteDTO cliente;
	private String eventType;

	public ClienteServiceEvent(Object source, String eventType, ClienteDTO eventCliente) {
		super(source);
		this.eventType = eventType;
		this.cliente = eventCliente;
	}

	@Override
	public String toString() {
		return String.format("ClienteServiceEvent [evento = %s, eventType = %s]", cliente.getIdCliente(), eventType);
	}
}
