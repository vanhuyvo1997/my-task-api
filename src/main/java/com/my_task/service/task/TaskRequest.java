package com.my_task.service.task;

import com.my_task.model.TaskStatus;

public record TaskRequest(String name, TaskStatus status) {
//	public toTask()

}
