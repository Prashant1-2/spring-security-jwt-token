package com.example.demo.controller;

import com.example.demo.entity.Employee;
import com.example.demo.repository.EmployeeRepository;
import com.example.demo.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/employees")
public class EmployeeController {

    @Autowired
    private EmployeeService service;

    @Autowired
	private EmployeeRepository employeeRepository;

    private boolean isAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getAuthorities().stream()
                   .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    // --- READ ALL (ADMIN only) ---
    @GetMapping
    public ResponseEntity<List<Employee>> getAll() {
    	  List<Employee> list = service.getAllEmployeesWithAccess();
          return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Employee> getOne(@PathVariable String id) {
        return service.getEmployeeById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.FORBIDDEN).build());
    }

    // --- CREATE SINGLE (ADMIN only) ---
    @PostMapping
    public ResponseEntity<Employee> create(@RequestBody Employee e) {
        if (!isAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        Employee saved = service.createEmployee(e);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // --- CREATE BULK (ADMIN only) ---
    @PostMapping("/bulk")
    public ResponseEntity<List<Employee>> createBulk(@RequestBody List<Employee> list) {
        if (!isAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        List<Employee> saved = service.createEmployees(list);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // --- UPDATE (ADMIN only) ---
    @PutMapping("/{id}")
    public ResponseEntity<Employee> update(@PathVariable String id,
                                           @RequestBody Employee e) {
        if (!isAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        Employee updated = service.updateEmployee(id, e);
        return ResponseEntity.ok(updated);
    }

    // --- DELETE (ADMIN only) ---
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
    	Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = auth.getName();

        // Fetch the employee to be deleted
        Optional<Employee> emp = employeeRepository.findById(id);

        // Only allow if current user is ADMIN and not deleting their own record
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (emp.isEmpty() || !isAdmin || emp.get().getUsername().equals(currentUsername)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        service.deleteEmployee(id);
        return ResponseEntity.ok().build();
    }
}
