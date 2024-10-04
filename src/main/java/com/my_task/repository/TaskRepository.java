package com.my_task.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.my_task.model.Task;

public interface TaskRepository extends JpaRepository<Task, Long> {

	List<Task> findByOwnerId(String id, Sort sort);

	Optional<Task> findByOwnerIdAndId(String ownerId, Long id);

	List<Task> findByOwnerIdAndNameContainingIgnoreCase(String id, String query, Sort sort);

	@Query("select count(t) as totalTasks, count(case when t.status=COMPLETED then 1 end) as completedTasks, count(case when t.status=TO_DO then 1 end) as todoTasks from Task t")
	TaskStatistics getTaskStatistics();

	@Query("select count(t) as totalTasks, count(case when t.status=TO_DO then 1 end) as todoTasks, count(case when t.status=COMPLETED then 1 end) as completedTasks from Task t where t.owner.id=:ownerId")
	TaskStatistics getTaskStatisticsByOwnerId(String ownerId);
}
