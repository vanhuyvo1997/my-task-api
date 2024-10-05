package com.my_task.utils;

import java.util.Optional;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import com.my_task.model.Role;
import com.my_task.model.User;

public class UserUtils {

	private UserUtils() {
		throw new IllegalStateException("Utility class");
	}

	public static Optional<User> getAuthenticatedUser() {
		var authentication = SecurityContextHolder.getContext().getAuthentication();
		var principal = authentication.getPrincipal();
		if (principal instanceof User user) {
			return Optional.of(user);
		}
		return Optional.empty();
	}

	public static boolean isAdmin(User user) {
		if (user == null)
			return false;
		return user.getAuthorities().contains(new SimpleGrantedAuthority(Role.ADMIN.name()));
	}
}
