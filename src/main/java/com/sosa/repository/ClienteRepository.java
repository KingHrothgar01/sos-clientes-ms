package com.sosa.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.sosa.model.Cliente;

@Repository
public interface ClienteRepository extends PagingAndSortingRepository<Cliente, String>{
	
}
