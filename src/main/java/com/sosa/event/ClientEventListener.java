package com.sosa.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class ClientEventListener implements ApplicationListener<ClienteServiceEvent> {
	private static final Logger LOGGER = LoggerFactory.getLogger(ClientEventListener.class);

	public void onApplicationEvent(ClienteServiceEvent event) {
		ClienteServiceEvent clienteEvent = event;
		LOGGER.info("Cliente {} with details : {}", event.getEventType(), clienteEvent.getCliente());
	}
}