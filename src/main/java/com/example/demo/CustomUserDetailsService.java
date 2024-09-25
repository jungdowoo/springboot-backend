package com.example.demo;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        UserVO user = userRepository.findByUserId(userId);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with userId: " + userId);
        }

        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
        String[] roles = user.getRoles().split(",");
        for (String role : roles) {
            grantedAuthorities.add(new SimpleGrantedAuthority(role));
        }

        return new User(user.getUserId(), user.getUserPwd(), grantedAuthorities);
    }
}