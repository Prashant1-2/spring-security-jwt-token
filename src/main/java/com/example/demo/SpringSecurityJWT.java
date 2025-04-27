package com.example.demo;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.example.demo.entity.Employee;
import com.example.demo.repository.EmployeeRepository;

@SpringBootApplication
public class SpringSecurityJWT {

	public static void main(String[] args) {
		SpringApplication.run(SpringSecurityJWT.class, args);
	}

	/** On startup, ensure an ADMIN user exists */
	@Bean
	CommandLineRunner initAdmin(EmployeeRepository repo, BCryptPasswordEncoder encoder) {
		return args -> {
			if (repo.findByUsername("admin").isEmpty()) {
				Employee admin = new Employee();
				admin.setEmpId("EMP001");
				admin.setUsername("admin");
				admin.setPassword(encoder.encode("admin123"));
				admin.setUserRoles(List.of("ADMIN"));
				repo.save(admin);
				System.out.println("✅ Created default admin / password=admin123");
			}
		};
	}
}
