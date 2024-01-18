package com.sosa.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

import io.micrometer.core.instrument.Metrics;

@Aspect
@Configuration
public class ControllerLayerAspects {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ControllerLayerAspects.class);

	@Before("execution (public * com.sosa.controller.*Controller.*(..))")
	public void logBeforeRestCall(JoinPoint join) {
		LOGGER.info("AOP Before REST call.: {}.", join);
	}

	@AfterReturning("execution(public * com.sosa.controller.ClienteController.createCliente(..))")
	public void afterCallingCreateClient(JoinPoint join) {
		LOGGER.info("AOP @AfterReturning Create REST call.: {}", join);
		Metrics.counter("create_client").increment();
	}

	@AfterReturning("execution(public * com.sosa.controller.ClienteController.getAllClientes(..))")
	public void afterCallingGetAllClient(JoinPoint join) {
		LOGGER.info("AOP @AfterReturning get clients REST call.: {}", join);
		Metrics.counter("get_clients").increment();
	}

	@AfterReturning("execution(public * com.sosa.controller.ClienteController.getCliente(..))")
	public void afterCallingGetClient(JoinPoint join) {
		LOGGER.info("AOP @AfterReturning get client REST call.: {}", join);
		Metrics.counter("get_client").increment();
	}

	@AfterReturning("execution(public * com.sosa.controller.ClienteController.updateCliente(..))")
	public void afterCallingUpdateClient(JoinPoint join) {
		LOGGER.info("AOP @AfterReturning update client REST call.: {}", join);
		Metrics.counter("update_client").increment();
	}
	
	@AfterReturning("execution(public * com.sosa.controller.ClienteController.deleteCliente(..))")
	public void afterCallingDeleteClient(JoinPoint join) {
		LOGGER.info("AOP @AfterReturning delete client REST call.: {}", join);
		Metrics.counter("delete_client").increment();
	}

	@AfterThrowing(pointcut = "execution(public * com.sosa.controller.ClienteController.*(..))", throwing = "e")
	public void afterGetGreetingThrowsException(Exception e) {
		Metrics.counter("client_errors").increment();
	}
}
