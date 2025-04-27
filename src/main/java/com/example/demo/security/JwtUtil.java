package com.example.demo.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.*;

@Component
public class JwtUtil {

	/*
	 * Key Generation: private final Key key: A secret key is generated using the
	 * Keys.secretKeyFor(SignatureAlgorithm.HS256) method. This key is used to sign
	 * and verify JWTs using the HS256 algorithm. Token Expiration: private final
	 * long expirationMs: Defines the token's expiration time in milliseconds (1
	 * hour in this case). Token Generation: generateToken(String username,
	 * List<String> roles): Creates a JWT with the following claims: sub (subject):
	 * The username. roles: A list of roles associated with the user. iat (issued
	 * at): The current timestamp. exp (expiration): The expiration timestamp. Signs
	 * the token using the secret key and returns the compact JWT string. Token
	 * Validation: validateToken(String token): Parses the token using the secret
	 * key to ensure it is valid. Returns true if the token is valid; otherwise,
	 * returns false if an exception occurs (e.g., expired or malformed token).
	 * Extract Username: getUsername(String token): Extracts and returns the sub
	 * (subject) claim, which represents the username, from the token. Extract
	 * Roles: getRoles(String token): Extracts and returns the roles claim as a list
	 * of strings from the token. This class is essential for generating,
	 * validating, and extracting information from JWTs in a Spring Security-based
	 * application.
	 */
	private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
	private final long expirationMs = 1000 * 60 * 60; // 1h

	public String generateToken(String username, List<String> roles) {
		return Jwts.builder().setSubject(username).claim("roles", roles).setIssuedAt(new Date())
				.setExpiration(new Date(System.currentTimeMillis() + expirationMs)).signWith(key).compact();
	}

	public boolean validateToken(String token) {
		try {
			Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
			return true;
		} catch (JwtException ex) {
			return false;
		}
	}

	public String getUsername(String token) {
		return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().getSubject();
	}

	@SuppressWarnings("unchecked")
	public List<String> getRoles(String token) {
		return (List<String>) Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody()
				.get("roles");
	}
}
