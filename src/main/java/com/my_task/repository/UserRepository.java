package com.my_task.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.my_task.model.User;

public interface UserRepository extends JpaRepository<User, String> {

	Optional<User> findByEmail(String email);

	@Query("select u from TaskUser u where (CONCAT(u.firstName,' ', u.lastName) ilike %:query% or CONCAT(u.lastName,' ', u.firstName) ilike %:query% or u.email ilike %:query%) and u.role=USER")
	Page<User> findBySearchQuery(@Param("query") String query, Pageable pageable);

	@Query("select u from TaskUser u where u.role=USER")
	Page<User> findAll(Pageable pageable);

	@Query("select count(u) as totalUsers, count(case when u.enabled = true then 1 end) as enabledUsers, count(case when u.enabled = false then 1 end) as disabledUsers from TaskUser u")
	UserStatistics getUserStatistics();

	@Query("""
			select u.id as id, u.firstName as firstName, u.lastName as lastName,
			u.avatarUrl as avatarUrl,
			count(t.id) as totalTasks,
			count(case when t.status=COMPLETED then 1 end) as numOfCompletedTasks,
			count(case when t.status=TO_DO then 1 end) as numOfTodoTasks
			 from TaskUser u join Task t on u.id=t.owner.id
			 where u.enabled=true
			 group by u.id
			 order by totalTasks, numOfCompletedTasks, numOfTodoTasks desc
			 fetch first :topNum rows only""")
	List<UserDetailsData> getTopActiveUser(int topNum);
}
