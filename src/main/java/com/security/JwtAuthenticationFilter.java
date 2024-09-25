package com.security;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String token = jwtTokenProvider.resolveToken(request);
        System.out.println("Received JWT Token:" + token); // 토큰이 올바르게 수신확인

        if (StringUtils.hasText(token) && jwtTokenProvider.validateToken(token)) {
        	System.out.println("Valid JWT Token found"); // 토큰이 유효한지 확인
            SecurityContextHolder.getContext().setAuthentication(jwtTokenProvider.getAuthentication(token));
        } else {
        	System.out.println("InValid or no JWT Token found"); // 토큰이 없거나 유효하지않으면 이 메시지 출력
        }

        chain.doFilter(request, response);
    }
}
