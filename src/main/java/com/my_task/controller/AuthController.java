package com.my_task.controller;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.my_task.service.UserRequest;
import com.my_task.service.UserService;

import lombok.AllArgsConstructor;

@RequestMapping("api/auth")
@AllArgsConstructor
@Controller
public class AuthController {
	private final UserService userService;
	
	@PostMapping("register")
	public ResponseEntity<?> register(@RequestBody UserRequest userRequest){
		return ResponseEntity.created(URI.create("/")).body(userService.create(userRequest));
	}
}
