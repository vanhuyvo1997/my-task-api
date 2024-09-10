package com.my_task.filter;

import java.io.IOException;
import java.security.KeyPair;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.my_task.service.user.UserService;
import com.my_task.utils.TokenUtils;

import io.jsonwebtoken.Claims;
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

		if (token == null) {
			filterChain.doFilter(request, response);
			return;
		}

		var optJws = TokenUtils.parseToken(token, keypair.getPublic());

		if (!optJws.isPresent()) {
			filterChain.doFilter(request, response);
			return;
		}

		Claims claims = optJws.get().getPayload();
		if (claims.get("type").equals("access")) {
			String email = claims.getSubject();
			var user = userService.loadUserByUsername(email);
			Authentication authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
			SecurityContextHolder.getContext().setAuthentication(authentication);
		}

		filterChain.doFilter(request, response);

	}

}
