package com.my_task.service.task;

import java.time.LocalDateTime;

import com.my_task.model.Task;
import com.my_task.model.TaskStatus;

import lombok.Builder;

@Builder
public record TaskResponse (Long id, String name, LocalDateTime createdAt, LocalDateTime completedAt, TaskStatus status){

	public static TaskResponse from(Task task) {
		return builder()
				.id(task.getId())
				.name(task.getName())
				.createdAt(task.getCreatedAt())
				.completedAt(task.getCompletedAt())
				.status(task.getStatus())
				.build(); 
	}

}
