package com.my_task.service.user;

import java.util.ArrayList;
import java.util.List;

import javax.print.attribute.standard.PageRanges;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.domain.Sort.TypedSort;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.my_task.model.Role;
import com.my_task.model.User;
import com.my_task.repository.UserRepository;
import com.my_task.service.exception.ResourceAlreadyExistedException;
import com.my_task.service.exception.ResourceNotFoundException;
import com.my_task.utils.UserUtils;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserService implements UserDetailsService {

	private final UserRepository userRepository;

	private final PasswordEncoder passwordEncoder;

	public UserResponse create(UserRequest userRequest) {
		if (userRepository.findByEmail(userRequest.email()).isPresent()) {
			throw new ResourceAlreadyExistedException(userRequest.email() + " is existed.");
		}

		User newUser = userRequest.toUser();
		newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
		var createdUser = userRepository.save(newUser);
		return UserResponse.form(createdUser);
	}

	@Override
	public User loadUserByUsername(String username) throws UsernameNotFoundException {
		return userRepository.findByEmail(username)
				.orElseThrow(() -> new UsernameNotFoundException(username + " not found"));

	}

	public Object getUsers(String query, int pageSize, int pageNum, String sortDir) {
		Sort sort = Sort.by(Direction.fromString(sortDir), "firstName", "lastName", "email");
		PageRequest pageRequest = PageRequest.of(pageNum, pageSize, sort);
		return userRepository.findAll(pageRequest).stream().map(UserResponse::form).toList();
	}

	private UserResponse changeStatus(String userId, boolean newStatus) {
		var user = userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("There's no user with given id"));
		if (user.getAuthorities().contains(new SimpleGrantedAuthority(Role.ADMIN.name()))) {
			throw new AccessDeniedException("Can't change admin status");
		}
		if (user.isEnabled() != newStatus) {
			user.setEnabled(newStatus);
			user = userRepository.save(user);
		}
		return UserResponse.form(user);
	}

	public UserResponse enabled(String id) {
		return changeStatus(id, true);
	}

	public UserResponse disabled(String id) {
		return changeStatus(id, false);
	}

}
