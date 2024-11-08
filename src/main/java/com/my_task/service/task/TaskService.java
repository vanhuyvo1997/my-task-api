package com.my_task.service.task;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import com.my_task.model.Priority;
import com.my_task.model.Role;
import com.my_task.model.Task;
import com.my_task.model.TaskStatus;
import com.my_task.model.User;
import com.my_task.repository.TaskRepository;
import com.my_task.service.exception.ResourceNotFoundException;
import com.my_task.utils.UserUtils;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class TaskService {
	private final TaskRepository taskRepository;

	public TaskResponse createTask(TaskRequest taskRequest) {
		var owner = getOwner();
		var newTask = Task.builder()
				.name(taskRequest.name())
				.createdAt(LocalDateTime.now())
				.owner(owner)
				.status(TaskStatus.TO_DO)
				.priority(taskRequest.priority())
				.build();
		return TaskResponse.from(taskRepository.save(newTask));
	}

	private User getOwner() {
		var optOwner = UserUtils.getAuthenticatedUser()
				.filter(user -> user.getAuthorities().contains(new SimpleGrantedAuthority(Role.USER.name())));
		return optOwner.orElseThrow(() -> new AccessDeniedException("Permission denied"));
	}

	public Optional<List<TaskResponse>> getAll(List<String> sortProps, String sortDirection, String query,
			Priority priority) {
		Direction direction = Direction.fromOptionalString(sortDirection).orElse(Direction.DESC);
		if (sortProps == null || sortProps.isEmpty()) {
			sortProps = Arrays.asList("createdAt");
		}
		var sort = Sort.by(direction, sortProps.toArray(new String[sortProps.size()]));
		var owner = getOwner();
		query = query == null ? "" : query;
		Set<Priority> priorities = priority == null ? Set.of(Priority.values()) : Set.of(priority);

		List<Task> tasks;
		tasks = taskRepository.findByOwnerIdAndPriorityInAndNameContainingIgnoreCase(owner.getId(),
				priorities,
				query,
				sort);
		var tasksResponse = tasks.stream().map(TaskResponse::from).toList();
		return Optional.of(tasksResponse).filter(trs -> !trs.isEmpty());
	}

	public Optional<TaskResponse> findById(Long id) {
		var owner = getOwner();
		return taskRepository.findByOwnerIdAndId(owner.getId(), id).map(TaskResponse::from);
	}

	public void delete(Long id) {
		var owner = getOwner();
		var task = taskRepository.findByOwnerIdAndId(owner.getId(), id)
				.orElseThrow(() -> new ResourceNotFoundException("Task id not found: " + id));
		taskRepository.delete(task);
	}

	public TaskResponse changeStatus(Long id, TaskStatus nextStatus) {
		var owner = getOwner();
		var task = getTaskByOwner(id, owner.getId());

		if (task.getStatus() != nextStatus) {
			var completedDate = nextStatus.equals(TaskStatus.COMPLETED) ? LocalDateTime.now() : null;
			task.setCompletedAt(completedDate);
			task.setStatus(nextStatus);
			task = taskRepository.save(task);
		} else if (nextStatus.equals(TaskStatus.COMPLETED)) {
			task.setCompletedAt(LocalDateTime.now());
			task = taskRepository.save(task);
		}

		return TaskResponse.from(task);
	}

	public TaskResponse updateTask(Long id, TaskRequest taskPartialUpdateRequest) {
		var owner = getOwner();
		var task = getTaskByOwner(id, owner.getId());
		task = partialUpdate(task, taskPartialUpdateRequest);
		return TaskResponse.from(taskRepository.save(task));
	}

	private Task partialUpdate(Task task, TaskRequest taskPartialUpdateRequest) {
		if (taskPartialUpdateRequest.name() != null) {
			task.setName(taskPartialUpdateRequest.name());
		}

		if (taskPartialUpdateRequest.completedAt() != null) {
			task.setCompletedAt(taskPartialUpdateRequest.completedAt());
		}

		if (taskPartialUpdateRequest.createdAt() != null) {
			task.setCreatedAt(taskPartialUpdateRequest.createdAt());
		}

		if (taskPartialUpdateRequest.priority() != null) {
			task.setPriority(taskPartialUpdateRequest.priority());
		}

		return task;
	}

	private Task getTaskByOwner(Long id, String ownerId) {
		return taskRepository.findByOwnerIdAndId(ownerId, id)
				.orElseThrow(() -> new ResourceNotFoundException("Not found task " + id + " for user " + ownerId));
	}
}
