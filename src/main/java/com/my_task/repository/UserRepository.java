package com.my_task.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.my_task.model.User;

public interface UserRepository extends JpaRepository<User, String> {

	Optional<User> findByEmail(String email);

	@Query("select u from TaskUser u where (u.firstName ilike %:query% or u.lastName ilike %:query% or email ilike %:query%) and u.role!=ADMIN ")
	Page<User> findBySearchQuery(@Param("query") String query, Pageable pageable);

	@Query("select count(u) as totalUsers, count(case when u.enabled = true then 1 end) as enabledUsers, count(case when u.enabled = false then 1 end) as disabledUsers from TaskUser u")
	UserStatistics getUserStatistics();
}
