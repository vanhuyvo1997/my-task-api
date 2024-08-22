package com.my_task.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
	public ResponseEntity<?> create(@RequestBody TaskRequest taskRequest) {
		return ResponseEntity.created(URI.create("/api/tasks")).body(taskService.createTask(taskRequest));
	}

	@GetMapping
	public ResponseEntity<?> gettAll(@RequestParam(required = false) List<String> sortProps, @RequestParam(required = false) String sortDirection) {
		return ResponseEntity.of(taskService.getAll(sortProps, sortDirection));
	}
	
	@GetMapping("{id}")
	public ResponseEntity<?> getById(@PathVariable Long id){
		return ResponseEntity.of(taskService.findById(id));
	}

}