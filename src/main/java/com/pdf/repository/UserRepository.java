package com.pdf.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pdf.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	// Since email is unique, we'll find users by email
	Optional<User> findByEmail(String email);
}