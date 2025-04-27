// src/main/java/com/example/demo/config/SecurityConfig.java
package com.example.demo.config;

import com.example.demo.security.JwtAuthenticationFilter;
import com.example.demo.service.CustomUserDetailsService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.*;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.*;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Autowired
	private CustomUserDetailsService uds;
	@Autowired
	private JwtAuthenticationFilter jwtFilter;

	/*
	 * DaoAuthenticationProvider Bean: Configures a DaoAuthenticationProvider to use
	 * the custom UserDetailsService (uds) for loading user details. Uses a
	 * BCryptPasswordEncoder (provided by the passwordEncoder() method) to encode
	 * and verify passwords. AuthenticationManager Bean: Retrieves and provides an
	 * AuthenticationManager from the AuthenticationConfiguration. This is required
	 * for handling authentication in the application. SecurityFilterChain Bean:
	 * Configures the HTTP security settings: CSRF: Disabled for stateless
	 * authentication. Authorization Rules: /auth/**: Publicly accessible. GET
	 * /employees/**: Accessible to users with roles ADMIN or USER. Other
	 * /employees/** endpoints: Restricted to the ADMIN role. All other requests:
	 * Require authentication. Session Management: Configured to be stateless (no
	 * sessions). JWT Filter: Adds the JwtAuthenticationFilter before the
	 * UsernamePasswordAuthenticationFilter. PasswordEncoder Bean: Provides a
	 * BCryptPasswordEncoder for securely hashing passwords.
	 */
	/*
	 * DaoAuthenticationProvider Bean: Configures a DaoAuthenticationProvider to use
	 * the custom UserDetailsService (uds) for loading user details. Uses a
	 * BCryptPasswordEncoder (provided by the passwordEncoder() method) to encode
	 * and verify passwords.
	 */
	@Bean
	DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider p = new DaoAuthenticationProvider();
		p.setUserDetailsService(uds);
		p.setPasswordEncoder(passwordEncoder());
		return p;
	}

	/*
	 * AuthenticationManager Bean: Retrieves and provides an AuthenticationManager
	 * from the AuthenticationConfiguration. This is required for handling
	 * authentication in the application.
	 */
	@Bean
	AuthenticationManager authenticationManager(AuthenticationConfiguration cfg) throws Exception {
		return cfg.getAuthenticationManager();
	}

	/*
	 * SecurityFilterChain Bean: Configures the HTTP security settings: CSRF:
	 * Disabled for stateless authentication. Authorization Rules: /auth/**:
	 * Publicly accessible. GET /employees/**: Accessible to users with roles ADMIN
	 * or USER. Other /employees/** endpoints: Restricted to the ADMIN role. All
	 * other requests: Require authentication. Session Management: Configured to be
	 * stateless (no sessions).JWT Filter: Adds the JwtAuthenticationFilter before
	 * the UsernamePasswordAuthenticationFilter
	 */
	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.csrf(cs -> cs.disable()).authenticationProvider(authenticationProvider())
				.authorizeHttpRequests(auth -> auth.requestMatchers("/auth/**").permitAll()
						.requestMatchers(HttpMethod.GET, "/employees/**").hasAnyRole("ADMIN", "USER")
						.requestMatchers("/employees/**").hasRole("ADMIN").anyRequest().authenticated())
				.sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
