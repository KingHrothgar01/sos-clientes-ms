package com.sosa.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name="prestamo")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class Prestamo {
	
	@Id
	@Column(name="id_prestamo")
	private String idPrestamo;
	
	@Column(name="monto", nullable = false)
	private Double monto;
	
	@Column(name="saldo_insoluto", nullable = false)
	private Double saldoInsoluto;
	
	@ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = {CascadeType.ALL})
	@JoinColumn(name="id_cliente", nullable = false)
	private Cliente cliente;
	
	@OneToMany(mappedBy = "prestamo", fetch = FetchType.LAZY, cascade = {CascadeType.ALL}, orphanRemoval = true)
	private final List<Movimiento> movimientos = new ArrayList<>();
	
	@Column(name = "fecha_registro", nullable = false)
	private Date fechaRegistro;
	
	@Column(name = "fecha_actualizacion", nullable = true)
	private Date fechaActualizacion;

	@Column(name = "usuario_registra", nullable = false)
	private String usuarioRegistra;
	
	@Column(name = "usuario_actualiza", nullable = true)
	private String usuarioActualiza;
	
	@Column(name="activo")
	private Boolean activo;

}
