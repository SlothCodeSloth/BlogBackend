package com.example.blog_app;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import java.time.LocalDateTime;

/**
 * Entity representing a blog post
 * Stores all the necessary information for a post; content, author, category, subject, and image URL.
 */
@Entity
@Table(name = "posts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100, nullable = false)
    @Size(max = 100, message = "Title must be less than 100 characters.")
    private String title;

    @Column(length = 3000, nullable = false)
    @Size(max = 3000, message = "Content must be less than 3000 characters")
    private String content;

    @Column(length = 50, nullable = false)
    @Size(max = 50, message = "Author must be less than 50 characters")
    private String author;

    @Column(length = 300)
    @Size(max = 300, message = "Subject must be less than 300 characters")
    private String subject;

    private String imageUrl;

    @Getter
    @Setter
    private String category;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    /**
     * Automatically set creation date before persisting.
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}