package com.project.project.api.security;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterchain)
            throws ServletException, IOException {
        String path = request.getRequestURI();

    
    if (path.equals("/auth/register") || path.equals("/auth/login") || path.equals("/auth/verify")) {
        filterchain.doFilter(request, response);
        return;
    }
        
                String tokenHeader = request.getHeader("Authorization");
        if (tokenHeader != null && tokenHeader.startsWith("Bearer ")) {
            String token = tokenHeader.substring(7);
            try {
                String username = jwtService.getUsername(token);
                Optional<MyUser> opUser = myUserDAO.findByUsernameIgnoreCase(username);
                if (opUser.isPresent()) {
                    MyUser user = opUser.get();
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user, null, new ArrayList());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (JWTDecodeException ex) {

            }

        }
        filterchain.doFilter(request, response);

    }

}
