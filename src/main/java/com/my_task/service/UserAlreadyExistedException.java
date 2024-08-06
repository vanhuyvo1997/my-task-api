package com.my_task.service;

public class UserAlreadyExistedException extends RuntimeException{

	public UserAlreadyExistedException(String message) {
		super(message);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -1152493163181425466L;

}
