package com.my_task.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.my_task.service.exception.ResourceAlreadyExistedException;
import com.my_task.service.exception.ResourceNotFoundException;

@ControllerAdvice
public class ExceptionHandlers {

	@ExceptionHandler({ ResourceAlreadyExistedException.class })
	public ResponseEntity<?> handleResourceAlreadyEisted(Exception e) {
		return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
	}

	@ExceptionHandler({ ResourceNotFoundException.class })
	public ResponseEntity<?> handleResourceNotFound(Exception e) {
		return ResponseEntity.notFound().build();
	}

}