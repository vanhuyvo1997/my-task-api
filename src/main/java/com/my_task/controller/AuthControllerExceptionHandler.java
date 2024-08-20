package com.my_task.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.my_task.service.user.UserAlreadyExistedException;

@ControllerAdvice
public class AuthControllerExceptionHandler {

	@ExceptionHandler({ UserAlreadyExistedException.class })
	public ResponseEntity<?> handleUserAlreadyEisted(Exception e) {
		return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
	}

}
