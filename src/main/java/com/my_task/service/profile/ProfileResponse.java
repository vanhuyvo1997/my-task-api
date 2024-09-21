package com.my_task.service.profile;

import com.my_task.model.User;
import lombok.Builder;

@Builder
public record ProfileResponse(String firstName, String lastName, String email, String role, String avatarUrl) {
	public static ProfileResponse from(User user) {
		return builder().firstName(user.getFirstName()).lastName(user.getLastName()).email(user.getEmail())
				.role(user.getRole().name()).avatarUrl(user.getAvatarUrl()).build();
	}
}
