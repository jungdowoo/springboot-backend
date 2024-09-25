package com.author;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorRepository extends JpaRepository<AuthorVO, String> {
    boolean existsByAuthorName(String authorName);
    boolean existsByAuthorId(String authorId);  
    Optional<AuthorVO> findByAuthorId(String id);
}