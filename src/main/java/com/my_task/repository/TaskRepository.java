package com.my_task.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.my_task.model.Task;

public interface TaskRepository extends JpaRepository<Task, Long> {

	List<Task> findByOwnerId(String id);

}
