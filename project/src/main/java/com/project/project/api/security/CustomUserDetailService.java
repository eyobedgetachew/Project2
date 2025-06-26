package com.project.project.api.security;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.project.project.model.MyUser;
import com.project.project.model.dao.MyUserDAO;

@Service
public class CustomUserDetailService implements UserDetailsService {

    private final MyUserDAO myUserDAO;

    @Autowired
    public CustomUserDetailService(MyUserDAO myUserDAO) {
        this.myUserDAO = myUserDAO;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        MyUser user = myUserDAO.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                Collections.singletonList(
                    new SimpleGrantedAuthority("ROLE_" + user.getRole().toUpperCase())
                )
        );
    }
}
