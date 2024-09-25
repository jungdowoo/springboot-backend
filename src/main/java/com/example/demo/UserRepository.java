package com.example.demo;


import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

	@Repository
	public interface UserRepository extends JpaRepository<UserVO, String> {
		
		boolean existsByUserId(String userId);
		boolean existsByUserName(String userName);
		UserVO findByUserId(String userId);
		UserVO findByUserName(String userName);
}