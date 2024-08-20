package com.my_task.controller;

import java.io.IOException;
import java.net.URI;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

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
import com.my_task.service.auth.AuthService;
import com.my_task.service.auth.LoginRequest;
import com.my_task.service.user.UserRequest;
import com.my_task.service.user.UserService;

import lombok.AllArgsConstructor;

@RequestMapping("api/auth")
@AllArgsConstructor
@Controller
public class AuthController {
	private final UserService userService;
	private final AuthService authService;
	private final AuthenticationManager authenticationManager;
	
	@PostMapping("register")
	public ResponseEntity<?> register(@RequestBody UserRequest userRequest){
		return ResponseEntity.created(URI.create("/")).body(userService.create(userRequest));
	}
	
	@PostMapping("login")
	public ResponseEntity<?> login(@RequestBody LoginRequest request) throws AuthenticationException, NoSuchAlgorithmException, InvalidKeySpecException, IOException{
		
		UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(request.email(), request.password());
		var authentication = authenticationManager.authenticate(token);
		if(!authentication.isAuthenticated()) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Can not log in with " + request.email());
		}
		var user = (User) authentication.getPrincipal();
		return ResponseEntity.ok(authService.generateToken(user));
	}
}
