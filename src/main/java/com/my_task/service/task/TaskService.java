package com.my_task.service.task;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import javax.security.sasl.AuthenticationException;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.my_task.model.Role;
import com.my_task.model.Task;
import com.my_task.model.TaskStatus;
import com.my_task.model.User;
import com.my_task.repository.TaskRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class TaskService {
	private final TaskRepository taskRepository;

	public TaskResponse createTask(TaskRequest taskRequest) throws AuthenticationException {
		var owner = getOwner();
		var newTask = Task.builder()
				.name(taskRequest.name())
				.createdAt(LocalDateTime.now())
				.owner(owner)
				.status(TaskStatus.TO_DO)
				.build();
		return TaskResponse.from(taskRepository.save(newTask));
	}
	
	
	private Optional<User> getAuthenticatedUser(){
		var authentication = SecurityContextHolder.getContext().getAuthentication();
		var principal = authentication.getPrincipal();
		if(principal instanceof User user) {
			return Optional.of(user);
		}
		return Optional.empty();
	}
	
	private User getOwner() throws AuthenticationException{
		var optOwner = getAuthenticatedUser()
				.filter(user -> user.getAuthorities().contains(new SimpleGrantedAuthority(Role.USER.name())));
		return optOwner.orElseThrow(() -> new AuthenticationException("Permission denied"));
	}

	public Optional<List<TaskResponse>> getAll() throws AuthenticationException {
		var owner = getOwner();
		var result = taskRepository.findByOwnerId(owner.getId()).stream().map(TaskResponse::from).toList();
		return Optional.of(result).filter(tasks -> tasks.size() > 0);
	}
}
