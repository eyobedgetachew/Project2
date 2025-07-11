package com.project.project.api.security;

import java.io.IOException;
import java.util.Collections;
import java.util.List; // Import Collection
import java.util.Optional;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority; // Import Collections for singletonList
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder; // Import GrantedAuthority
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource; // Import SimpleGrantedAuthority
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.auth0.jwt.exceptions.JWTDecodeException;
import com.project.project.model.MyUser;
import com.project.project.model.dao.MyUserDAO;
import com.project.project.service.JWTService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JWTRequestFilter extends OncePerRequestFilter {

    private JWTService jwtService;
    private MyUserDAO myUserDAO;

    public JWTRequestFilter(JWTService jwtService, MyUserDAO myUserDAO) {
        this.jwtService = jwtService;
        this.myUserDAO = myUserDAO;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterchain)
            throws ServletException, IOException {
        String path = request.getRequestURI();

        // Skip JWT processing for public authentication endpoints
        if (path.equals("/auth/register") || path.equals("/auth/login") || path.equals("/auth/verify") || path.equals("/api/places")) { // Added /api/places to permAll from Websecurityconfig
            filterchain.doFilter(request, response);
            return;
        }

        String tokenHeader = request.getHeader("Authorization");
        if (tokenHeader != null && tokenHeader.startsWith("Bearer ")) {
            String token = tokenHeader.substring(7);
            try {
                String username = jwtService.getUsername(token);
                // Important: Only set authentication if no authentication is already present in the context
                // This prevents overwriting if, for example, a previous filter already authenticated
                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    Optional<MyUser> opUser = myUserDAO.findByUsernameIgnoreCase(username);
                    if (opUser.isPresent()) {
                        MyUser user = opUser.get();

                        // --- THE CRITICAL FIX IS HERE ---
                        // Create a list of GrantedAuthority based on the user's role from the database
                        List<GrantedAuthority> authorities = Collections.singletonList(
                            new SimpleGrantedAuthority("ROLE_" + user.getRole().toUpperCase())
                        );

                        // Create the authentication token with the retrieved user and their authorities
                        UsernamePasswordAuthenticationToken authentication =
                                new UsernamePasswordAuthenticationToken(user, null, authorities); // <-- Pass the authorities here!

                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                }
            } catch (JWTDecodeException ex) {
                // Log the exception if needed for debugging, but don't rethrow to avoid 500 errors for invalid tokens
                System.err.println("JWT Decode Exception: " + ex.getMessage());
            } catch (Exception e) {
                 // Catch other potential exceptions during user lookup/authentication setup
                 System.err.println("Error during JWT authentication filter: " + e.getMessage());
            }

        }
        filterchain.doFilter(request, response);
    }
}