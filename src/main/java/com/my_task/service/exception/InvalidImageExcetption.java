package com.my_task.service.exception;

public class InvalidImageExcetption extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InvalidImageExcetption() {
		super();
	}

	public InvalidImageExcetption(String message) {
		super(message);
	}
	
}
