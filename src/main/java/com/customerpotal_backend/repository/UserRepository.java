package com.customerpotal_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.customerpotal_backend.entity.UserDetail;

import jakarta.transaction.Transactional;

public interface UserRepository extends JpaRepository<UserDetail, Long>{
	
	@Query(value = "SELECT * FROM user_detail WHERE user_email = :email", nativeQuery = true)
	UserDetail getUserDetail(@Param("email") String email);

	@Modifying
	@Transactional
	@Query(value = "UPDATE user_detail set user_password = :userPassword where user_email = :userEmail", nativeQuery = true)
	void updateUserPassword(String userPassword, String userEmail);
}
