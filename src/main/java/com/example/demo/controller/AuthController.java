// src/main/java/com/example/demo/controller/AuthController.java
package com.example.demo.controller;

import com.example.demo.dto.AuthRequest;
import com.example.demo.security.JwtUtil;
import com.example.demo.service.CustomUserDetailsService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

	@Autowired
	private AuthenticationManager authManager;
	@Autowired
	private CustomUserDetailsService uds;
	@Autowired
	private JwtUtil jwtUtil;

	/*
	 * Authentication: The authManager.authenticate() method is called with a
	 * UsernamePasswordAuthenticationToken created using the username and password
	 * from the AuthRequest object. If the credentials are invalid, an exception is
	 * thrown, and the request fails. Load User Details: The
	 * uds.loadUserByUsername() method retrieves the UserDetails object for the
	 * authenticated user. The user's roles are extracted from the UserDetails
	 * object, and the "ROLE_" prefix is removed for simplicity. Generate JWT Token:
	 * The jwtUtil.generateToken() method generates a JWT token using the username
	 * and roles. The token is returned in the response body as a key-value pair
	 * ("token": <generated_token>). This method ensures secure authentication and
	 * provides a JWT token for subsequent requests.
	 */
	@PostMapping("/login")
	public ResponseEntity<Map<String, String>> login(@RequestBody AuthRequest req) {
		// 1) Throws if invalid
		authManager.authenticate(new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword()));

		// 2) Load user roles
		UserDetails ud = uds.loadUserByUsername(req.getUsername());
		List<String> roles = ud.getAuthorities().stream().map(a -> a.getAuthority().replace("ROLE_", "")).toList();

		// 3) Issue token
		String token = jwtUtil.generateToken(req.getUsername(), roles);
		return ResponseEntity.ok(Map.of("token", token));
	}
}
