package com.project.project.api.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod; // NEW: Import HttpMethod for specific HTTP verb matching
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter; // CRUCIAL: Correct filter class for JWT

@EnableMethodSecurity // Keep this to enable @PreAuthorize annotations on controller methods
@Configuration
public class Websecurityconfig {
    private JWTRequestFilter jwtRequestFilter;

    public Websecurityconfig(JWTRequestFilter jwtRequestFilter) {
        this.jwtRequestFilter = jwtRequestFilter;
    }

    @Bean
    public SecurityFilterChain FilterChain(HttpSecurity http) throws Exception {
        // Modern way to configure CSRF and CORS
        http.csrf(csrf -> csrf.disable())
            .cors(cors -> cors.disable()); // Consider configuring CORS properly for production

        // CRUCIAL: Add JWT filter BEFORE Spring's default UsernamePasswordAuthenticationFilter
        // This ensures the JWT is processed for authentication before other authentication attempts.
        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        // Configure authorization rules
        http.authorizeHttpRequests(authorize -> authorize
                // Public endpoints (no authentication required)
                .requestMatchers("/auth/register", "/auth/login", "/auth/verify").permitAll()

                // GET /api/places: Allow all to view the feed of places
                .requestMatchers(HttpMethod.GET, "/api/places").permitAll()

                // NEW CRUCIAL RULE: POST /api/places/create requires OWNER role
                .requestMatchers(HttpMethod.POST, "/api/places/create").hasRole("OWNER")

                // GET /api/feed/posts: Assuming this is a public feed for all posts
                .requestMatchers(HttpMethod.GET, "/api/feed/posts").permitAll()

                // NEW CRUCIAL RULE: POST /api/feed/user/posts requires USER or OWNER role
                // This aligns with @PreAuthorize("hasAnyRole('USER', 'OWNER')") in FeedController
                .requestMatchers(HttpMethod.POST, "/api/feed/user/posts").hasAnyRole("USER", "OWNER")

                // Existing specific feed rules (ensure these are still relevant for your design)
                .requestMatchers("/api/feed/owner/**").hasRole("OWNER")
                .requestMatchers("/api/feed/user/**").hasRole("USER") // For other GET/PUT/DELETE under /user

                // Review endpoints (assuming these are user-specific)
                .requestMatchers("/api/reviews", "/api/reviews/my").hasRole("USER")
                .requestMatchers("/api/places/*/reviews").hasRole("USER")

                // All other requests require authentication by default
                .anyRequest().authenticated()
            );
        return http.build();
    }
}