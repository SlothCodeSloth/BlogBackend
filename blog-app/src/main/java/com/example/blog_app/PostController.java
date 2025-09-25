package com.example.blog_app;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * REST controller for managing blog posts.
 * Provides CRUD operations and image upload functionality.
 */
@RestController
@RequestMapping("/api/posts")
public class PostController {
    private final JwtUtil jwtUtil;
    private final PostRepository repository;

    public PostController(PostRepository repository, JwtUtil jwtUtil) {
        this.repository = repository;
        this.jwtUtil = jwtUtil;
    }

    /**
     * Create a new blog post (requires valid JWT).
     */
    @PostMapping
    public ResponseEntity<Post> createPost(@Valid @RequestBody Post post, @RequestHeader("Authorization") String authHeader) {
        // System.out.println("Creating post with authorization: " + authHeader);
        validateToken(authHeader);
        // System.out.println("Token validated. Saving post: " + post.getTitle());
        return ResponseEntity.ok(repository.save(post));
    }

    // Delete an existing post by ID (requires valid JWT).
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePost(@PathVariable Long id, @RequestHeader("Authorization") String authHeader) {
        validateToken(authHeader);
        repository.deleteById(id);
        return ResponseEntity.ok("Deleted");
    }

    /**
     * Validate JWT token from Authorization header.
     * Throws RuntimeException if Invalid or Missing.
     */
    private void validateToken(String authHeader) {
        // System.out.println("Validating token: " + authHeader);
        if (authHeader == null || !authHeader.startsWith("Bearer")) {
            System.out.println("Missing or invalid token format.");
            throw new RuntimeException("Missing or invalid token");
        }
        String token = authHeader.substring(7);

        if (!jwtUtil.validateToken(token)) {
            // System.out.println("Token validation failed");
            throw new RuntimeException("Invalid token");
        }
        // System.out.println("Token validation succesfful.");
    }

    /**
     * Edit an existing post (requires valid JWT).
     */
    @PutMapping("/{id}")
    public ResponseEntity<Post> editPost(@PathVariable Long id, @Valid @RequestBody Post updatedPost, @RequestHeader("Authorization") String authHeader) {
        validateToken(authHeader);

        // Fetch existing post or throw if not found
        Post existing = repository.findById(id).orElseThrow();

        // Update the fields
        existing.setTitle(updatedPost.getTitle());
        existing.setContent(updatedPost.getContent());
        existing.setImageUrl(updatedPost.getImageUrl());
        existing.setSubject(updatedPost.getSubject());
        return ResponseEntity.ok(repository.save(existing));
    }

    /**
     * Fetch a paginated list of posts, potentially filtered by category (blog/project)
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getPosts(
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Post> postsPage;

        if (category != null && !category.isEmpty()) {
            postsPage = repository.findByCategoryOrderByCreatedAtDesc(category, pageable);
        } else {
            postsPage = repository.findAllByOrderByCreatedAtDesc(pageable);
        }

        // Build response
        Map<String, Object> response = new HashMap<>();
        response.put("posts", postsPage.getContent());
        response.put("currentPage", postsPage.getNumber());
        response.put("totalPages", postsPage.getTotalPages());
        response.put("totalElements", postsPage.getTotalElements());
        response.put("hasNext", postsPage.hasNext());
        response.put("hasPrevious", postsPage.hasPrevious());

        return ResponseEntity.ok(response);
    }

    /**
     * Upload an image file for posts (requires valid JWT).
     * Returns the URL where the image can be accessed
     */
    @PostMapping("/upload-image")
    public ResponseEntity<String> uploadImage(
            @RequestParam("image")MultipartFile file,
            @RequestHeader("Authorization") String authHeader
    ) {
        // System.out.println("Received image upload request");
        // System.out.println("File name: " + file.getOriginalFilename());
        // System.out.println("File size: " + file.getSize());
        // System.out.println("Content type: " + file.getContentType());
        validateToken(authHeader);
        try {
            // Prepare Upload Directory - Create if it doesn't exist
            Path uploadsDir = Paths.get("uploads");
            if (!Files.exists(uploadsDir)) {
                Files.createDirectories(uploadsDir);
                System.out.println("Created uploads directory");
            }

            // Generate unique filename
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename != null ?
                    originalFilename.substring(originalFilename.lastIndexOf(".")) : ".png";
            String filename = UUID.randomUUID().toString() + extension;

            // System.out.println("Saving file as: " + filename);

            // Save the file
            Path filePath = uploadsDir.resolve(filename);
            Files.copy(file.getInputStream(), filePath);

            // Return the URL
            return ResponseEntity.ok("http://localhost:8081/uploads/" + filename);
        }
        catch (IOException e) {
            // System.out.println("Failed to upload image: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body("Failed to upload image: " + e.getMessage());
        }
    }

    /**
     * Serve uploaded images
     */
    @GetMapping("/uploads/{filename}")
    public ResponseEntity<byte[]> getImage(@PathVariable String filename) {
        try {
            Path filePath = Paths.get("uploads").resolve(filename);
            byte[] imageBytes = Files.readAllBytes(filePath);
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(imageBytes);
        }
        catch (IOException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Fetch a single post by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Post> getPostById(@PathVariable Long id) {
        Post post = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + id));
        return ResponseEntity.ok(post);
    }
}