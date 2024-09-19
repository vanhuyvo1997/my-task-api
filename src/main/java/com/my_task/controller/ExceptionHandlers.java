package com.my_task.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import com.my_task.service.exception.InvalidImageExcetption;
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

	@ExceptionHandler({ AccessDeniedException.class })
	public ResponseEntity<?> handleAccessDeniedException(AccessDeniedException ex) {
		return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(ex.getMessage()));
	}
	
	@ExceptionHandler({ InvalidImageExcetption.class })
	public ResponseEntity<?> handleInvalidImageException(InvalidImageExcetption ex) {
		return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(new ErrorResponse(ex.getMessage()));
	}
	
	@ExceptionHandler({ MaxUploadSizeExceededException.class })
	public ResponseEntity<?> handleInvalidImageException(MaxUploadSizeExceededException ex) {
		return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body(new ErrorResponse(ex.getMessage()));
	}
	
	private record ErrorResponse(String error) {}

}
