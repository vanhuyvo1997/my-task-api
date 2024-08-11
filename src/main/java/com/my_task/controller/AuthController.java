package com.my_task.controller;

import java.net.URI;
import java.time.Instant;

import org.apache.tomcat.websocket.AuthenticationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.my_task.model.User;
import com.my_task.service.UserRequest;
import com.my_task.service.UserService;
import com.my_task.utils.TokenUtils;

import lombok.AllArgsConstructor;

@RequestMapping("api/auth")
@AllArgsConstructor
@Controller
public class AuthController {
	private final UserService userService;
	private final AuthenticationManager authenticationManager;
	
	@PostMapping("register")
	public ResponseEntity<?> register(@RequestBody UserRequest userRequest){
		return ResponseEntity.created(URI.create("/")).body(userService.create(userRequest));
	}
	
	@PostMapping("login")
	public ResponseEntity<?> login(@RequestBody LoginRequest request) throws AuthenticationException{
		
		UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(request.email(), request.password());
		var authentication = authenticationManager.authenticate(token);
		if(!authentication.isAuthenticated()) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Can not log in with " + request.email());
		}
		
		User user = (User)authentication.getPrincipal();
		Instant now = Instant.now();
		String accessToken = TokenUtils.generateAccesToken(user, now);
		String refreshToken  = TokenUtils.generateRefreshToken(user,now);
		
		return ResponseEntity.ok(LoginResponse.builder().refreshToken(refreshToken).accessToken(accessToken).build());
	}
}
