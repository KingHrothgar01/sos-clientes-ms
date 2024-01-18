package com.sosa.util;

public final class Constants {
	
	private Constants() {
		throw new IllegalStateException(Constants.APPLICATION_MESSAGE_003);
	}
	
	public static final String USER_ALTA = "admin";
	
	public static final String HTTP_MSG_400 = "Error en petici\u00F3n cliente.";
	public static final String HTTP_MSG_401 = "Error en la autenticaci\u00F3n.";
	public static final String HTTP_MSG_403 = "Permisos insuficientes.";
	public static final String HTTP_MSG_404 = "No se encontr\u00F3 el recurso solicitado.";
	public static final String HTTP_MSG_500 = "Error en servidor.";
	
	public static final String DATE_FORMAT_1 = "dd/MM/yyyy";
	
	public static final String APPLICATION_MESSAGE_000 = "";
	public static final String APPLICATION_MESSAGE_001 = "Application can not save Tasa.";
	public static final String APPLICATION_MESSAGE_002 = "Large page size for transactions.";
	public static final String APPLICATION_MESSAGE_003 = "Utility Class.";
	public static final String APPLICATION_MESSAGE_004 = "Ocurrio un error en el envio del correo electronico {0}";
	
	public static final String BUSINESS_MSG_ERR_C_001 = "No fue posible dar de alta cliente.";
	public static final String BUSINESS_MSG_ERR_C_002 = "No fue posible dar de alta cliente.";
	public static final String BUSINESS_MSG_ERR_C_003 = "No se encontró cliente.";
	public static final String BUSINESS_MSG_ERR_C_004 = "No se encontró cliente.";
	public static final String BUSINESS_MSG_ERR_C_005 = "No fue posible actualizar cliente.";
	public static final String BUSINESS_MSG_ERR_C_006 = "No fue posible actualizar cliente.";
	public static final String BUSINESS_MSG_ERR_C_007 = "No fue posible actualizar cliente.";
	public static final String BUSINESS_MSG_ERR_C_008 = "No fue posible dar de baja cliente.";
	public static final String BUSINESS_MSG_ERR_C_009 = "No fue posible dar de baja cliente.";
	public static final String BUSINESS_MSG_ERR_C_010 = "No se encontraron resultados.";
	public static final String BUSINESS_MSG_ERR_C_011 = "No se encontraron resultados.";
	public static final String BUSINESS_MSG_ERR_C_012 = "No fue posible dar de alta cliente.";
	public static final String BUSINESS_MSG_ERR_C_013 = "No fue posible actualizar cliente.";
	public static final String BUSINESS_MSG_ERR_C_014 = "No fue posible dar de baja cliente.";
	public static final String BUSINESS_MSG_ERR_C_015 = "No fue posible actualizar cliente.";
	
	public static final String SPECIFICATION_FIELD_ID = "id";
	public static final String SPECIFICATION_FIELD_NUMANIO = "anio";
	public static final String SPECIFICATION_FIELD_NUMMES = "mes";
	public static final String SPECIFICATION_FIELD_INDACTIVO = "activo";

	public static final Integer APPLICATION_PARAMETER_LARGE_PAGE = 50;
	
	public static final Boolean REGISTRO_INACTIVO = false;
	public static final Boolean REGISTRO_ACTIVO = true;
}