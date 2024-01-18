package com.sosa.exception;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class CustomExceptionTests {
	
	@Test
	void test_http_400_exception() {
		try {
			throw new HTTP400Exception();
		}catch(HTTP400Exception e) {
			assertNull(e.getMessage());
		}
	}
	
	@Test
	void test_http_400_exception_message() {
		try {
			throw new HTTP400Exception("Testing bad request exception.");
		}catch(HTTP400Exception e) {
			assertNotNull(e.getMessage());
			assertTrue(e.getMessage().contains("Testing bad request exception."));
		}
	}
	
	@Test
	void test_http_400_exception_message_cause() {
		try {
			throw new HTTP400Exception("Testing bad request exception.", new Throwable("The client should not repeat this request without modification."));
		}catch(HTTP400Exception e) {
			assertNotNull(e.getMessage());
			assertTrue(e.getMessage().contains("Testing bad request exception."));
			assertNotNull(e.getCause());
			assertTrue(e.getCause().getMessage().contains("The client should not repeat this request without modification."));
		}
	}

	@Test
	void test_http_400_exception_cause() {
		try {
			throw new HTTP400Exception(new Throwable("The client should not repeat this request without modification."));
		}catch(HTTP400Exception e) {
			assertNotNull(e.getCause());
			assertTrue(e.getCause().getMessage().contains("The client should not repeat this request without modification."));
		}
	}

	@Test
	void test_http_401_exception() {
		try {
			throw new HTTP401Exception();
		}catch(HTTP401Exception e) {
			assertNull(e.getMessage());
		}
	}
	
	@Test
	void test_http_401_exception_message() {
		try {
			throw new HTTP401Exception("Testing unauthorized exception.");
		}catch(HTTP401Exception e) {
			assertNotNull(e.getMessage());
			assertTrue(e.getMessage().contains("Testing unauthorized exception."));
		}
	}
	
	@Test
	void test_http_401_exception_message_cause() {
		try {
			throw new HTTP401Exception("Testing unauthorized exception.", new Throwable("The user does not have enough privileges."));
		}catch(HTTP401Exception e) {
			assertNotNull(e.getMessage());
			assertTrue(e.getMessage().contains("Testing unauthorized exception."));
			assertNotNull(e.getCause());
			assertTrue(e.getCause().getMessage().contains("The user does not have enough privileges."));
		}
	}

	@Test
	void test_http_401_exception_cause() {
		try {
			throw new HTTP401Exception(new Throwable("The user does not have enough privileges."));
		}catch(HTTP401Exception e) {
			assertNotNull(e.getCause());
			assertTrue(e.getCause().getMessage().contains("The user does not have enough privileges."));
		}
	}
	
	@Test
	void test_http_403_exception() {
		try {
			throw new HTTP403Exception();
		}catch(HTTP403Exception e) {
			assertNull(e.getMessage());
		}
	}
	
	@Test
	void test_http_403_exception_message() {
		try {
			throw new HTTP403Exception("Testing forbidden exception.");
		}catch(HTTP403Exception e) {
			assertNotNull(e.getMessage());
			assertTrue(e.getMessage().contains("Testing forbidden exception."));
		}
	}
	
	@Test
	void test_http_403_exception_message_cause() {
		try {
			throw new HTTP403Exception("Testing forbidden exception.", new Throwable("The user does not have enough privileges."));
		}catch(HTTP403Exception e) {
			assertNotNull(e.getMessage());
			assertTrue(e.getMessage().contains("Testing forbidden exception."));
			assertNotNull(e.getCause());
			assertTrue(e.getCause().getMessage().contains("The user does not have enough privileges."));
		}
	}

	@Test
	void test_http_403_exception_cause() {
		try {
			throw new HTTP403Exception(new Throwable("The user does not have enough privileges."));
		}catch(HTTP403Exception e) {
			assertNotNull(e.getCause());
			assertTrue(e.getCause().getMessage().contains("The user does not have enough privileges."));
		}
	}
	
	@Test
	void test_http_404_exception() {
		try {
			throw new HTTP404Exception();
		}catch(HTTP404Exception e) {
			assertNull(e.getMessage());
		}
	}
	
	@Test
	void test_http_404_exception_message() {
		try {
			throw new HTTP404Exception("Testing not found exception.");
		}catch(HTTP404Exception e) {
			assertNotNull(e.getMessage());
			assertTrue(e.getMessage().contains("Testing not found exception."));
		}
	}
	
	@Test
	void test_http_404_exception_message_cause() {
		try {
			throw new HTTP404Exception("Testing not found exception.", new Throwable("The resource you are trying to reach is not located on this server."));
		}catch(HTTP404Exception e) {
			assertNotNull(e.getMessage());
			assertTrue(e.getMessage().contains("Testing not found exception."));
			assertNotNull(e.getCause());
			assertTrue(e.getCause().getMessage().contains("The resource you are trying to reach is not located on this server."));
		}
	}

	@Test
	void test_http_404_exception_cause() {
		try {
			throw new HTTP404Exception(new Throwable("The resource you are trying to reach is not located on this server."));
		}catch(HTTP404Exception e) {
			assertNotNull(e.getCause());
			assertTrue(e.getCause().getMessage().contains("The resource you are trying to reach is not located on this server."));
		}
	}
	
	@Test
	void test_http_500_exception() {
		try {
			throw new HTTP500Exception();
		}catch(HTTP500Exception e) {
			assertNull(e.getMessage());
		}
	}
	
	@Test
	void test_http_500_exception_message() {
		try {
			throw new HTTP500Exception("Testing internal server error exception.");
		}catch(HTTP500Exception e) {
			assertNotNull(e.getMessage());
			assertTrue(e.getMessage().contains("Testing internal server error exception."));
		}
	}
	
	@Test
	void test_http_500_exception_message_cause() {
		try {
			throw new HTTP500Exception("Testing internal server error exception.", new Throwable("What a shame, We have some problems..."));
		}catch(HTTP500Exception e) {
			assertNotNull(e.getMessage());
			assertTrue(e.getMessage().contains("Testing internal server error exception."));
			assertNotNull(e.getCause());
			assertTrue(e.getCause().getMessage().contains("What a shame, We have some problems..."));
		}
	}

	@Test
	void test_http_500_exception_cause() {
		try {
			throw new HTTP500Exception(new Throwable("What a shame, We have some problems..."));
		}catch(HTTP500Exception e) {
			assertNotNull(e.getCause());
			assertTrue(e.getCause().getMessage().contains("What a shame, We have some problems..."));
		}
	}
}
