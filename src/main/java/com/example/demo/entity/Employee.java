package com.example.demo.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;

@Document(collection = "employees")
public class Employee {
	@Id
	private String empId;
	private String username;
	private String password;
	private List<String> userRoles;

	// Constructors
	public Employee() {
	}

	public Employee(String empId, String username, String password, List<String> userRoles) {
		this.empId = empId;
		this.username = username;
		this.password = password;
		this.userRoles = userRoles;
	}

	// Getters and Setters
	public String getEmpId() {
		return empId;
	}

	public void setEmpId(String empId) {
		this.empId = empId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public List<String> getUserRoles() {
		return userRoles;
	}

	public void setUserRoles(List<String> userRoles) {
		this.userRoles = userRoles;
	}
}
