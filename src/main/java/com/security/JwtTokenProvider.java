package com.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import java.util.Base64;
import java.util.Date;

@Component
public class JwtTokenProvider {

	@Value("${jwt.secret}")
    private  String secretKey = "YfrMNmAK2IXw1ZN2rMFaEKNGOLrSMyErj+bBBeEqtvs=";  // 이 키는 반드시 외부로 노출되지 않도록 해야 합니다.
	
    private final long validityInMilliseconds = 3600000; // 1시간

    @PostConstruct
    public void init() {
    	
        System.out.println("Encoded Secret Key after initialization: " + secretKey);
    }
    
    
    public String createToken(String username, String role) {
        Claims claims = Jwts.claims().setSubject(username);
        claims.put("role", role);

        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);

        
        System.out.println("Secret Key used for token creation:" + secretKey);
        
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(SignatureAlgorithm.HS256,  Base64.getDecoder().decode(secretKey)) 
                .compact();
    }

    public Authentication getAuthentication(String token) {
        UserDetails userDetails = loadUserByUsername(getUsername(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public String getUsername(String token) {
        return Jwts.parser()
        			.setSigningKey(Base64.getDecoder().decode(secretKey))
        			.parseClaimsJws(token)
        			.getBody()
        			.getSubject();
    }

    public String resolveToken(HttpServletRequest req) {
        String bearerToken = req.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    public boolean validateToken(String token) {
        try {
            System.out.println("Secret Key used for token validation: " + secretKey);
        	
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            return true;
        } catch (Exception e) {
        	e.printStackTrace();
            return false;
        }
    }

    // 실제로는 UserDetailsService를 통해 사용자 정보를 로드해야 합니다.
    public UserDetails loadUserByUsername(String username) {
        // 예시용 코드. 실제로는 DB 또는 다른 데이터 소스에서 사용자 정보를 가져와야 합니다.
        return org.springframework.security.core.userdetails.User
                .withUsername(username)
                .password("") // JWT는 이미 인증된 사용자를 위한 것이므로 비밀번호는 필요 없음
                .roles("USER")
                .build();
    }
   
}
