package com.example.demo.repository;

import com.example.demo.entity.Employee;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface EmployeeRepository extends MongoRepository<Employee, String> {
	Optional<Employee> findByUsername(String username);
}
