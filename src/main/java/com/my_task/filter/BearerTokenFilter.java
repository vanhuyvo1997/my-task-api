package com.my_task.filter;

import java.io.IOException;
import java.security.KeyPair;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.my_task.service.user.UserService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class BearerTokenFilter extends OncePerRequestFilter {

	private final KeyPair keypair;
	private UserService userService;
	

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		String token = (String) request.getHeader("Authorization");
		Jws<?> result = parseToken(token);
		Claims claims = null;
		if(result != null) claims = (Claims) result.getPayload();

		if (claims != null) {
			String email = claims.getSubject();
			var user = userService.loadUserByUsername(email);
			Authentication authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
			SecurityContextHolder.getContext().setAuthentication(authentication);
		}
		filterChain.doFilter(request, response);
	}

	private Jws<?> parseToken(String token) {
		if (token == null)
			return null;
		try {
			token = token.replaceAll("Bearer", "").trim();
			var parser = Jwts.parser().verifyWith(keypair.getPublic()).build();
			return parser.parseSignedClaims(token);
		} catch (JwtException ex) {
			return null;
		}
	}

}
