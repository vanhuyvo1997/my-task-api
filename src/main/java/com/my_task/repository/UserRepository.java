package com.my_task.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.my_task.model.User;

public interface UserRepository extends JpaRepository<User, String> {

	Optional<User> findByEmail(String email);

	// Page<User>
	// findByFirstNameContainingOrLastNameContainingOrEmailContainingAllIgnoreCase(String
	// query,
	// Pageable pageable);

}
