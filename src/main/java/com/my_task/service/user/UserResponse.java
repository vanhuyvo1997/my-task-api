package com.my_task.service.user;

import com.my_task.model.User;

import lombok.Builder;

@Builder
public record UserResponse(String id, String firstName, String lastName, String email, boolean enabled, long numOfTodo,
		long numOfCompleted, long totalTasks) {
	public static UserResponse form(User user) {
		return UserResponse.builder().id(user.getId()).email(user.getEmail()).firstName(user.getFirstName())
				.lastName(user.getLastName()).enabled(user.isEnabled()).build();
	}
}
