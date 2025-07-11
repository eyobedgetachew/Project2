package com.project.project.api.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@EnableWebSecurity
@EnableMethodSecurity
@Configuration
public class Websecurityconfig {
    private JWTRequestFilter jwtRequestFilter;

    public Websecurityconfig(JWTRequestFilter jwtRequestFilter) {
        this.jwtRequestFilter = jwtRequestFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain FilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
            .cors(cors -> cors.disable());

        http.sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.anonymous(anonymous -> anonymous.disable()); 

        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        http.authorizeHttpRequests(authorize -> authorize
                // --- MOST PERMISSIVE RULES FIRST ---
                // Permit access to all HTML pages, error endpoint, well-known, and static assets
                .requestMatchers(
                    "/", "/x.html", "/res.html", "/addreview.html", 
                    "/login.html", "/sign-up.html", "/create-post.html", "/Add place.html",
                    "/resp.html", "/new.html", "/profile.html", "/Aboutus.html", 
                    "/user,p.html", "/p.html", "/op.html", "/upload_profile_pic.html",
                    "/owner_restaurant.html", // CONFIRMED: This path uses underscore
                    "/exploring.html", // Ensure exploring.html is also permitted
                    "/error", "/.well-known/**", 
                    "/project_01_img/**", "/css/**", "/js/**", "/*.css", "/*.js", "/favicon.ico"
                ).permitAll()

                // Public API endpoints (after HTML pages)
                .requestMatchers(HttpMethod.GET, "/api/places").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/places/**").permitAll() 
                .requestMatchers("/auth/register", "/auth/login").permitAll() 
                .requestMatchers(HttpMethod.GET, "/api/restaurants").permitAll() 
                .requestMatchers(HttpMethod.GET, "/api/restaurants/**").permitAll() 
                .requestMatchers(HttpMethod.GET, "/api/posts/feed/explore").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/users/interests").permitAll()


                // Authenticated/Role-based API endpoints (most restrictive last, before anyRequest())
                .requestMatchers(HttpMethod.GET, "/api/places/my-restaurant").hasRole("OWNER") 
                .requestMatchers(HttpMethod.PATCH, "/api/places/{id}").hasRole("OWNER")
                .requestMatchers(HttpMethod.POST, "/api/places/create").hasRole("OWNER") 
                .requestMatchers(HttpMethod.POST, "/api/posts/create").authenticated()
                .requestMatchers(HttpMethod.GET, "/api/posts/feed/for-you").authenticated()
                .requestMatchers(HttpMethod.GET, "/api/posts/me").authenticated()
                .requestMatchers(HttpMethod.GET, "/api/posts/user/{userId}").permitAll() 
                .requestMatchers(HttpMethod.PATCH, "/api/users/me/bio").authenticated()
                .requestMatchers("/api/feed/owner/**").hasRole("OWNER")
                .requestMatchers("/api/feed/user/**").hasRole("USER")
                .requestMatchers("/api/reviews", "/api/reviews/my").hasRole("USER")
                .requestMatchers("/api/places/*/reviews").hasRole("USER")
                .requestMatchers(HttpMethod.POST, "/api/users/me/complete-onboarding").authenticated()
                .requestMatchers(HttpMethod.POST, "/auth/profile-picture").authenticated()
                .requestMatchers(HttpMethod.DELETE, "/auth/profile-picture").authenticated()

                // All other requests require authentication by default (LAST)
                .anyRequest().authenticated()
            );
        return http.build();
    }
}
