package com.sosa.model.dto;

import java.io.Serializable;
import java.util.Date;

import javax.validation.constraints.NotBlank;

import org.springframework.hateoas.RepresentationModel;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClienteDTO extends RepresentationModel<ClienteDTO> implements Serializable{
	private static final long serialVersionUID = -3518185069453765848L;

	@JsonProperty("id-cliente")
	@JsonSetter("id-cliente")
	private String idCliente;
	
	@NotBlank(message = "El nombre es requerido.")
	@JsonProperty("nombre")
	@JsonSetter("nombre")
	private String nombre;
	
	@NotBlank(message = "El apellido paterno es requerido.")
	@JsonProperty("apellido-paterno")
	@JsonSetter("apellido-paterno")
	private String apellidoPaterno;
	
	@JsonProperty("apellido-materno")
	@JsonSetter("apellido-materno")
	private String apellidoMaterno;
	
	@NotBlank(message = "El RFC es requerido.")
	@JsonProperty("rfc")
	@JsonSetter("rfc")
	private String rfc;
	
	@NotBlank(message = "El CURP es requerido.")
	@JsonProperty("curp")
	@JsonSetter("curp")
	private String curp;

	@JsonProperty(value = "fecha-registro", access = JsonProperty.Access.READ_ONLY)
	@JsonSetter("fecha-registro")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
	private Date fechaRegistro;
	
	@JsonProperty(value = "fecha-actualizacion", access = JsonProperty.Access.READ_ONLY)
	@JsonSetter("fecha-actualizacion")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
	private Date fechaActualizacion;

	@JsonProperty(value = "usuario-registra", access = JsonProperty.Access.READ_ONLY)
	@JsonSetter("usuario-registra")
	private String usuarioRegistra;
	
	@JsonProperty(value = "usuario-actualiza", access = JsonProperty.Access.READ_ONLY)
	@JsonSetter("usuario-actualiza")
	private String usuarioActualiza;
	
	@JsonProperty(value = "activo", access = JsonProperty.Access.READ_ONLY)
	@JsonSetter("activo")
	private Boolean activo;
}
