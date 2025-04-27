package com.example.demo.service;

import com.example.demo.entity.Employee;
import com.example.demo.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {

	@Autowired
	private EmployeeRepository employeeRepository;

	/*
	 * Purpose: This method retrieves an Employee entity from the database using the
	 * provided username. It converts the Employee entity into a User object, which
	 * is a Spring Security representation of a user. Steps: Retrieve Employee: The
	 * method uses employeeRepository.findByUsername(username) to fetch the Employee
	 * entity. If no employee is found, it throws a UsernameNotFoundException with a
	 * descriptive message. Steps: The userRoles of the Employee are mapped to
	 * SimpleGrantedAuthority objects, which are required by Spring Security. Each
	 * role is prefixed with "ROLE_" and converted to uppercase for consistency with
	 * Spring Security's role naming conventions. Return User: A User object is
	 * created with the employee's username, password, and authorities (roles). This
	 * User object is returned to Spring Security for authentication and
	 * authorization purposes.Steps: This method is invoked automatically by Spring
	 * Security during the authentication process when a user attempts to log in.
	 * This implementation ensures that user credentials and roles are correctly
	 * loaded and processed for authentication.
	 */
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Employee employee = employeeRepository.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

		return new User(employee.getUsername(), employee.getPassword(), employee.getUserRoles().stream()
				.map(role -> new SimpleGrantedAuthority("ROLE_" + role.toUpperCase())).collect(Collectors.toList()));
	}
}
