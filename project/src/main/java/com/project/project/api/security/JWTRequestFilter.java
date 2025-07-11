package com.project.project.api.security;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
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
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        String method = request.getMethod();

        System.out.println("DEBUG: JWTFilter processing path: " + path + ", method: " + method);

        boolean skipFilter = 
            // 1. Common non-authenticated paths
            path.equals("/error") ||
            path.startsWith("/.well-known/") || 
            path.equals("/favicon.ico") ||
            
            // 2. All HTML pages (must include owner_restaurant.html and exploring.html)
            path.equals("/") ||
            path.equals("/x.html") ||
            path.equals("/res.html") ||
            path.equals("/addreview.html") ||
            path.equals("/login.html") ||
            path.equals("/sign-up.html") ||
            path.equals("/create-post.html") ||
            path.equals("/Add place.html") ||
            path.equals("/resp.html") ||
            path.equals("/new.html") ||
            path.equals("/profile.html") ||
            path.equals("/Aboutus.html") ||
            path.equals("/user,p.html") ||
            path.equals("/p.html") ||
            path.equals("/op.html") ||
            path.equals("/upload_profile_pic.html") ||
            path.equals("/owner_restaurant.html") || // CONFIRMED: This path is here
            path.equals("/exploring.html") || // CONFIRMED: This path is correctly listed here
            
            // 3. Public API endpoints (GET requests for places and explore feed)
            (method.equals("GET") && path.equals("/api/places")) ||
            (method.equals("GET") && path.startsWith("/api/places/")) || 
            (method.equals("GET") && path.equals("/api/restaurants")) ||
            (method.equals("GET") && path.startsWith("/api/restaurants/")) ||
            (method.equals("GET") && path.equals("/api/posts/feed/explore")) ||

            // 4. Specific public POST endpoints (e.g., register, interests)
            (method.equals("POST") && path.equals("/api/users/interests")) ||
            (method.equals("POST") && path.equals("/auth/register")) ||
            (method.equals("POST") && path.equals("/auth/login")) || 

            // 5. Static resources
            path.startsWith("/project_01_img/") ||
            path.startsWith("/css/") ||
            path.startsWith("/js/") ||
            path.endsWith(".css") ||
            path.endsWith(".js");
        
        if (skipFilter) {
            System.out.println("DEBUG: JWTFilter SKIPPING (Matched public path): " + path);
        } else {
            System.out.println("DEBUG: JWTFilter PROCESSING (NO SKIP MATCH - expecting JWT): " + path + ", method: " + method);
        }
        return skipFilter; 
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterchain)
            throws ServletException, IOException {
        String tokenHeader = request.getHeader("Authorization");
        
        System.out.println("--- JWTFilter: doFilterInternal for path: " + request.getRequestURI() + " ---");
        if (tokenHeader == null) {
            System.out.println("JWTFilter: No Authorization header found for " + request.getRequestURI());
        } else {
            System.out.println("JWTFilter: Authorization header found for " + request.getRequestURI() + ". Token starts with: " + tokenHeader.substring(0, Math.min(tokenHeader.length(), 30)) + "...");
        }

        if (tokenHeader != null && tokenHeader.startsWith("Bearer ")) {
            String token = tokenHeader.substring(7);
            try {
                String username = jwtService.getUsername(token);
                System.out.println("JWTFilter: Extracted username from token: " + username);

                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    Optional<MyUser> opUser = myUserDAO.findByUsernameIgnoreCase(username);
                    if (opUser.isPresent()) {
                        MyUser user = opUser.get();

                        List<GrantedAuthority> authorities = Collections.singletonList(
                            new SimpleGrantedAuthority("ROLE_" + user.getRole().toUpperCase())
                        );

                        UsernamePasswordAuthenticationToken authentication =
                                new UsernamePasswordAuthenticationToken(user, null, authorities);
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        System.out.println("JWTFilter: Successfully authenticated user: " + username + " for " + request.getRequestURI() + " with roles: " + authorities);
                    } else {
                        System.out.println("JWTFilter: User not found in DB for username: " + username + ". Authentication failed for " + request.getRequestURI());
                    }
                } else if (SecurityContextHolder.getContext().getAuthentication() != null) {
                    System.out.println("JWTFilter: SecurityContext already has authentication for: " + SecurityContextHolder.getContext().getAuthentication().getName() + " for " + request.getRequestURI());
                } else {
                    System.out.println("JWTFilter: Username extracted was null, or SecurityContext already had authentication for " + request.getRequestURI());
                }
            } catch (JWTDecodeException ex) {
                System.err.println("JWTFilter ERROR: JWT Decode Exception for path " + request.getRequestURI() + ": " + ex.getMessage());
            } catch (Exception e) {
                System.err.println("JWTFilter ERROR: Generic error during JWT authentication filter for path " + request.getRequestURI() + ": " + e.getMessage());
                e.printStackTrace();
            }

        } else {
            System.out.println("JWTFilter: No Bearer token found in Authorization header for " + request.getRequestURI());
        }
        filterchain.doFilter(request, response);
    }
}
