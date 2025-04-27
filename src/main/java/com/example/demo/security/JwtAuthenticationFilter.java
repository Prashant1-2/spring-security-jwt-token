package com.example.demo.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.*;
import org.springframework.security.core.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	@Autowired
	private JwtUtil jwtUtil;

	/*
	 * Extract Authorization Header: The method retrieves the Authorization header
	 * from the HTTP request. It checks if the header is present and starts with the
	 * prefix Bearer, which indicates a JWT token. Validate Token: If a token is
	 * found, it is extracted by removing the Bearer prefix. The
	 * jwtUtil.validateToken(token) method is called to ensure the token is valid.
	 * Extract User Information: If the token is valid, the username is extracted
	 * using jwtUtil.getUsername(token). The roles associated with the token are
	 * retrieved using jwtUtil.getRoles(token) and converted into a list of
	 * GrantedAuthority objects. Set Authentication: A
	 * UsernamePasswordAuthenticationToken is created using the username and roles.
	 * This token is set in the SecurityContextHolder, which makes the
	 * authentication information available for the current request. Continue Filter
	 * Chain: The chain.doFilter(req, res) method is called to pass the request and
	 * response to the next filter in the chain. This filter ensures that requests
	 * with valid JWT tokens are authenticated and authorized based on the roles
	 * embedded in the token.
	 */
	@Override
	protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
			throws ServletException, IOException {
		String header = req.getHeader("Authorization");
		if (header != null && header.startsWith("Bearer ")) {
			String token = header.substring(7);
			if (jwtUtil.validateToken(token)) {
				String username = jwtUtil.getUsername(token);
				// load UserDetails or just build auth from token
				List<GrantedAuthority> auths = jwtUtil.getRoles(token).stream()
						.map(r -> new SimpleGrantedAuthority("ROLE_" + r)).collect(Collectors.toList());

				UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(username, null,
						auths);
				SecurityContextHolder.getContext().setAuthentication(auth);
			}
		}
		chain.doFilter(req, res);
	}
}
