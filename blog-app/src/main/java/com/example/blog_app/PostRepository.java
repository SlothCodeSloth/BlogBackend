package com.example.blog_app;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * Repository interface for Post entity.
 * Provides built-in CRUD operations and custom queries for pagination and filtering by category.
 */
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByCategory(String category);
    Page<Post> findByCategory(String category, Pageable pageable);

    // Fetch all posts ordered by creation date (descending) with pagination
    Page<Post> findAllByOrderByCreatedAtDesc(Pageable pageable);

    // Fetch posts by category ordered by creation date (descending) with pagination
    Page<Post> findByCategoryOrderByCreatedAtDesc(String category, Pageable pageable);
}

