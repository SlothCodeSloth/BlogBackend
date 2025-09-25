package com.example.blog_app;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Security filter that runs once per request.
 * Extracts JWT from the Authorization header and validates it.
 */
@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public JwtFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    /**
     * Determine which requests should bypass this filter.
     * - Skips all auth endpoints (/api/auth/**)
     * - Skips GET requests to posts (publicly accessible)
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        String method = request.getMethod();

        // ✅ Skip all auth endpoints (login, register, refresh, etc.)
        if (path.startsWith("/api/auth/")) {
            return true; // Allow auth endpoints
        }

        // ✅ Skip GET requests to posts
        if ("GET".equalsIgnoreCase(method) && path.startsWith("/api/posts")) {
            return true; // Allow public GET posts
        }

        return false;
    }

    /**
     * Apply the JWT validation filter.
     * If token is valid, set authentication in SecurityContext.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException {

        logger.info("JwtFilter applied to: " + request.getMethod() + " " + request.getRequestURI());

        final String authorizationHeader = request.getHeader("Authorization");
        String jwt = null;
        String username = null;

        // Extract JWT if present
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            try {
                username = jwtUtil.getUsernameFromToken(jwt);
            } catch (Exception e) {
                logger.warn("JWT token validation failed: " + e.getMessage());
            }
        }

        // If valid token & no existing authentication, set SecurityContext
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            if (jwtUtil.validateToken(jwt)) {
                UserDetails userDetails = new User(username, "", new ArrayList<>());
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        // Continue filter chain
        chain.doFilter(request, response);
    }
}
