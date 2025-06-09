package com.project.project.api.security;



import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFilter;

@Configuration
public class Websecurityconfig {
    private JWTRequestFilter jwtRequestFilter;
    public Websecurityconfig(JWTRequestFilter jwtRequestFilter) {
        this.jwtRequestFilter = jwtRequestFilter;
    }
    @Bean  
    public SecurityFilterChain FilterChain(HttpSecurity http) throws Exception {
        http.csrf().disable().cors().disable();
        http.addFilterBefore(jwtRequestFilter, AuthenticationFilter.class);
        http.authorizeHttpRequests()
        .requestMatchers("/api/places").permitAll()
        .requestMatchers("/auth/register").permitAll()
        .requestMatchers("/auth/login","/auth/verify").permitAll()
        .anyRequest().authenticated();
        return http.build();
    }

}
