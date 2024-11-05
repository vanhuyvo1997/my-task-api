package com.my_task.service.task;

import java.time.LocalDateTime;

import com.my_task.model.Priority;
import com.my_task.model.TaskStatus;

public record TaskRequest(
		String name,
		TaskStatus status,
		LocalDateTime createdAt,
		LocalDateTime completedAt,
		Priority priority,
		String ownerId) {
}
