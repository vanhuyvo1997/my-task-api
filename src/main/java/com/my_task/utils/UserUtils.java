package com.my_task.utils;

import java.util.Optional;

import org.springframework.security.core.context.SecurityContextHolder;

import com.my_task.model.User;

public class UserUtils {
	public static Optional<User> getAuthenticatedUser() {
		var authentication = SecurityContextHolder.getContext().getAuthentication();
		var principal = authentication.getPrincipal();
		if (principal instanceof User user) {
			return Optional.of(user);
		}
		return Optional.empty();
	}
}
