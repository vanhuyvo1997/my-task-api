package com.my_task.controller;

import java.net.URI;

import javax.security.sasl.AuthenticationException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.my_task.service.task.TaskRequest;
import com.my_task.service.task.TaskService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("api/tasks")
@AllArgsConstructor
public class TaskController {
	
	private final TaskService taskService;

	@PostMapping
	public ResponseEntity<?> create(@RequestBody TaskRequest taskRequest) throws AuthenticationException{
		return ResponseEntity.created(URI.create("/api/tasks")).body(taskService.createTask(taskRequest));
	}
	
	@GetMapping
	public ResponseEntity<?> gettAll() throws AuthenticationException{
		return ResponseEntity.of(taskService.getAll());
	}
	
}
