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
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//            .cors().and().csrf().disable()
//            .authorizeRequests((requests) -> requests
//                .antMatchers("/api/users/login","/api/author/login", "/api/user/profile/**","/api/users/register", "/api/author/create","/api/users/check-duplicate", "/api/users/check-name-duplicate", "/api/posts", "/api/posts/**", "/api/upload", "/uploads/**").permitAll()
//                .antMatchers("/api/user/profile/current").authenticated() 
//               .anyRequest().authenticated()
//            )
//            .formLogin((form) -> form
//                .loginProcessingUrl("/api/login")
//                .defaultSuccessUrl("/home", true)
//                .failureUrl("/login?error=true")
//                .usernameParameter("userId")
//                .passwordParameter("userPwd")
//            )
//            .logout((logout) -> logout
//                .logoutUrl("/perform_logout")
//                .deleteCookies("JSESSIONID")
//                .logoutSuccessUrl("/login")
//            );
//
//        return http.build();
//    }
//
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//    @Bean
//    public HttpFirewall allowUrlEncodedSlashHttpFirewall() {
//    	StrictHttpFirewall firewall = new StrictHttpFirewall();
//    	firewall.setAllowUrlEncodedSlash(true);
//    	firewall.setAllowSemicolon(true);
//    	return firewall;
//    }
	 @Bean
	    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
	        http
	            .cors().configurationSource(corsConfigurationSource())  // CORS 설정 추가
	            .and().csrf().disable()
	            .authorizeRequests((requests) -> requests
	                .antMatchers("/api/users/login", "/api/author/login", "/api/user/profile/**", "/api/users/register", "/api/author/create", "/api/users/check-duplicate", "/api/users/check-name-duplicate", "/api/posts", "/api/posts/**", "/api/upload", "/api/users/profile/current","/api/artworks/**","/uploads/**").permitAll()
	                
	                .anyRequest().authenticated()
	            )
	                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class)
	           
	            // `formLogin`과 관련된 설정을 추가했으나, API 기반 인증에서는 필요하지 않을 수 있습니다.
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

	    // CORS 설정을 Spring Security에 직접 추가
	    @Bean
	    public CorsConfigurationSource corsConfigurationSource() {
	        CorsConfiguration configuration = new CorsConfiguration();
	        configuration.setAllowedOrigins(List.of("http://localhost:3000")); // 프론트엔드 도메인 설정
	        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS")); // 허용된 HTTP 메서드
	        configuration.setAllowedHeaders(List.of("*")); // 모든 헤더 허용
	        configuration.setAllowCredentials(true); // 자격 증명 허용

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









