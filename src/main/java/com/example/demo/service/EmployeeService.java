package com.example.demo.service;

import com.example.demo.entity.Employee;
import com.example.demo.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EmployeeService {

	@Autowired
	private EmployeeRepository employeeRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	/**
	 * ADMIN → all employees USER → only the employee whose username == current
	 * principal
	 */
	public List<Employee> getAllEmployeesWithAccess() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		boolean isAdmin = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

		if (isAdmin) {
			return employeeRepository.findAll();
		}

		// non‑admin: lookup by username
		String me = auth.getName();
		return employeeRepository.findByUsername(me).map(List::of).orElse(List.of());
	}


	public Optional<Employee> getEmployeeById(String id) {
	    // Get authentication details
	    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	    String currentUsername = auth.getName();

	    // Find employee by ID
	    Optional<Employee> optionalEmp = employeeRepository.findById(id);
	    if (optionalEmp.isEmpty()) {
	        return Optional.empty();
	    }

	    Employee emp = optionalEmp.get();

	    // Allow access only if the ID belongs to the logged-in user
	    if (emp.getUsername().equals(currentUsername)) {
	        return Optional.of(emp);
	    }

	    return Optional.empty(); // Access denied
	}



	public Employee createEmployee(Employee employee) {
		employee.setPassword(passwordEncoder.encode(employee.getPassword()));
		return employeeRepository.save(employee);
	}

	public Employee updateEmployee(String id, Employee updatedEmployee) {
		updatedEmployee.setEmpId(id);
		updatedEmployee.setPassword(passwordEncoder.encode(updatedEmployee.getPassword()));
		return employeeRepository.save(updatedEmployee);
	}

	public void deleteEmployee(String id) {
		employeeRepository.deleteById(id);
	}

	public List<Employee> createEmployees(List<Employee> employees) {
		for (Employee employee : employees) {
			employee.setPassword(passwordEncoder.encode(employee.getPassword()));
		}
		return employeeRepository.saveAll(employees);
	}
}
