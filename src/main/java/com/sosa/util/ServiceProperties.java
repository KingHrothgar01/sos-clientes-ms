package com.sosa.util;

import javax.validation.constraints.NotNull;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@ConfigurationProperties(prefix = "cliente.service", ignoreUnknownFields = false)
@Component
@Getter 
@Setter
public class ServiceProperties {

	@NotNull
	private String name = "Cliente Spring Data JPA Service";
	
	@NotNull
	private String description = "Cliente Spring Data JPA Database Features";
	
}