package com.my_task.service.user;

import com.my_task.model.User;

public record UserRequest(String firstName, String lastName, String email, String password, boolean enabled) {

	public User toUser() {
		return User.builder().firstName(firstName).lastName(lastName).email(email).password(password).enabled(enabled)
				.build();
	}

}
