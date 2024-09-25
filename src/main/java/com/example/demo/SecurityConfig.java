package com.example.demo;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.security.web.firewall.StrictHttpFirewall;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.security.JwtAuthenticationFilter;
import com.security.JwtTokenProvider;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	private final JwtTokenProvider jwtTokenProvider;
	
	@Autowired
	public SecurityConfig(JwtTokenProvider jwtTokenProvider) {
		this.jwtTokenProvider = jwtTokenProvider;
	}

	 @Bean
	    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
	        http
	            .cors().configurationSource(corsConfigurationSource())
	            .and().csrf().disable()
	            .authorizeRequests((requests) -> requests
	                .antMatchers("/api/users/login", "/api/author/login", "/api/user/profile/**", "/api/users/register", "/api/author/create", "/api/users/check-duplicate", "/api/users/check-name-duplicate", "/api/posts", "/api/posts/**", "/api/upload", "/api/users/profile/current","/api/artworks/**","/uploads/**").permitAll()
	                
	                .anyRequest().authenticated()
	            )
	                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class)
	           
	            .formLogin((form) -> form
	                .loginProcessingUrl("/api/login")
	                .defaultSuccessUrl("/home", true)
	                .failureUrl("/login?error=true")
	                .usernameParameter("userId")
	                .passwordParameter("userPwd")
	            )
	            .logout((logout) -> logout
	                .logoutUrl("/perform_logout")
	                .deleteCookies("JSESSIONID")
	                .logoutSuccessUrl("/login")
	            );

	        return http.build();
	    }

	    @Bean
	    public CorsConfigurationSource corsConfigurationSource() {
	        CorsConfiguration configuration = new CorsConfiguration();
	        configuration.setAllowedOrigins(List.of("http://localhost:3000")); 
	        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
	        configuration.setAllowedHeaders(List.of("*")); 
	        configuration.setAllowCredentials(true); 

	        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
	        source.registerCorsConfiguration("/**", configuration);
	        return source;
	    }

	    @Bean
	    public PasswordEncoder passwordEncoder() {
	        return new BCryptPasswordEncoder();
	    }

	    @Bean
	    public HttpFirewall allowUrlEncodedSlashHttpFirewall() {
	        StrictHttpFirewall firewall = new StrictHttpFirewall();
	        firewall.setAllowUrlEncodedSlash(true);
	        firewall.setAllowSemicolon(true);
	        return firewall;
	    }
}









