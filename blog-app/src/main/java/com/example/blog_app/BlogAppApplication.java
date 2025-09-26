package com.example.blog_app;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Entry point for the Blog Application.
 * Configures CORS and can run startup logic (via CommandLineRunner).
 */
@SpringBootApplication
public class BlogAppApplication {
	public static void main(String[] args) {
		SpringApplication.run(BlogAppApplication.class, args);
	}

	/**
	 * Startup runner that executes when the application starts.
	 * Prints all posts in the repository.
	 * Commented Out - Creates a new "First Post"
	 */
	@Bean
	CommandLineRunner run(PostRepository repo) {
		return args -> {
			//repo.save(new Post(null, "Hello World!", "My first blog post!", "Alan", "https://i.ytimg.com/vi/x7RAbKDNJSw/maxresdefault.jpg", "blog", "Simple Test", null));
			repo.findAll().forEach(System.out::println);
		};
	}

	/* Import Local and Deployed Frontend */
	@Value("${CORS_ORIGINS}")
	private String corsOrigins; // comma-separated

	/**
	 * Configure CORS settings for API endpoints.
	 * Allows frontend (local dev + deployed domain) to access backend.
	 */
	@Bean
	public WebMvcConfigurer corsConfigurer() {
		String[] originsArray = corsOrigins.split(",");
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				// WebMvcConfigurer.super.addCorsMappings(registry);
				registry.addMapping("/api/**")
						.allowedOrigins(originsArray)
						.allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
						.allowCredentials(true);
			}
		};
	}
}
