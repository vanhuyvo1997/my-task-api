package com.my_task.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import com.my_task.model.Task;

public interface TaskRepository extends JpaRepository<Task, Long> {

	List<Task> findByOwnerId(String id, Sort sort);
	
	Optional<Task> findByOwnerIdAndId(String ownerId, Long id);

}
