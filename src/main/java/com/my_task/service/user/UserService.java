package com.my_task.service.user;

import java.util.List;
import java.util.function.Predicate;

import org.apache.logging.log4j.util.Strings;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.my_task.model.User;
import com.my_task.repository.TaskRepository;
import com.my_task.repository.TaskStatistics;
import com.my_task.repository.UserRepository;
import com.my_task.service.exception.ResourceAlreadyExistedException;
import com.my_task.service.exception.ResourceNotFoundException;
import com.my_task.utils.UserUtils;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserService implements UserDetailsService {

	private final UserRepository userRepository;

	private final TaskRepository taskRepository;

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

	public UsersPageResponse getUsers(String query, int pageSize, int pageNum, String sortDir) {
		Sort sort = Sort.by(Direction.fromString(sortDir), "firstName", "lastName", "email");
		PageRequest pageRequest = PageRequest.of(pageNum, pageSize, sort);

		Page<User> userPage;
		if (Strings.isNotBlank(query)) {
			userPage = userRepository.findBySearchQuery(query, pageRequest);
		} else {
			userPage = userRepository.findAll(pageRequest);
		}

		var content = userPage.getContent().stream().filter(Predicate.not(UserUtils::isAdmin)).map(user -> {
			TaskStatistics statistics = taskRepository.getTaskStatisticsByOwnerId(user.getId());
			return UserResponse.builder()
					.id(user.getId())
					.email(user.getEmail())
					.firstName(user.getFirstName())
					.lastName(user.getLastName())
					.avatarUrl(user.getAvatarUrl())
					.enabled(user.isEnabled())
					.numOfCompleted(statistics.getCompletedTasks())
					.numOfTodo(statistics.getTodoTasks())
					.totalTasks(statistics.getTotalTasks())
					.build();
		}).toList();

		var totalPages = userPage.getTotalPages();
		var currentPage = userPage.getNumber();
		var totalElements = userPage.getTotalElements();

		return new UsersPageResponse(content, totalPages, currentPage, totalElements);
	}

	private record UsersPageResponse(List<UserResponse> content, int totalPages, int currentPage, long totalElements) {

	}

	private UserResponse changeStatus(String userId, boolean newStatus) {
		var user = userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("There's no user with given id"));
		if (UserUtils.isAdmin(user)) {
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
