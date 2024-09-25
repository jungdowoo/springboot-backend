package com.author;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.security.JwtTokenProvider;

@RestController
@RequestMapping("/api/author")
public class AuthorController {

    @Autowired
    private AuthorService authorService;
    
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    // 닉네임 중복 체크
    @PostMapping("/check-name-duplicate")
    public Map<String, Object> checkNameDuplicate(@RequestBody Map<String, String> payload) {
        String authorName = payload.get("userName");

        // 닉네임 유효성 검사 로직
        String namePattern = "^[a-zA-Z0-9가-힣]{2,10}$";
        Map<String, Object> response = new HashMap<>();

        if (!Pattern.matches(namePattern, authorName)) {
            response.put("isValid", false);
            response.put("error", "닉네임은 2-10자의 영문, 숫자, 한글이어야 합니다.");
            return response;
        }

        boolean isDuplicate = authorService.authorExistsByName(authorName);
        response.put("isValid", true);
        response.put("isDuplicate", isDuplicate);
        return response;
    }

    // 아이디 중복 체크 
    @PostMapping("/check-id-duplicate")
    public Map<String, Object> checkIdDuplicate(@RequestBody Map<String, String> payload) {
        String authorId = payload.get("userId");

        // ID 유효성 검사 로직
        String idPattern = "^(?=.*[0-9]).{4,12}$";
        Map<String, Object> response = new HashMap<>();

        if (!Pattern.matches(idPattern, authorId)) {
            response.put("isValid", false);
            response.put("error", "아이디는 4-12자리의 숫자를 포함해야 합니다.");
            return response;
        }

        boolean isDuplicate = authorService.authorExists(authorId); // existsByAuthorId로 수정
        response.put("isValid", true);
        response.put("isDuplicate", isDuplicate);
        return response;
    }

    // 작가 회원가입
    @PostMapping("/create")
    public ResponseEntity<?> createAuthor(@RequestBody Map<String, String> payload) {
        String authorId = payload.get("authorId");
        String rawPassword = payload.get("authorPwd");
        String authorName = payload.get("authorName");
        String authorPhoneNum = payload.get("authorPhoneNum");
        String authorBio = payload.get("authorBio");

        try {
            AuthorVO newAuthor = authorService.registerAuthor(authorId, rawPassword, authorName, authorPhoneNum, authorBio);
            return ResponseEntity.ok(newAuthor);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> response = new HashMap<>();
            response.put("error", "회원가입 중 오류가 발생했습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
 // 작가 로그인 추가
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> payload) {
        String authorId = payload.get("authorId");
        String rawPassword = payload.get("authorPwd");

        AuthorVO author = authorService.login(authorId, rawPassword);
        if (author != null) {
        	String token = jwtTokenProvider.createToken(author.getId(), "AUTHOR");
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            
            
            Map<String, Object> data = new HashMap<>();
            data.put("authorId", author.getId());
            data.put("userName", author.getAuthorName());
            data.put("token", token);
            data.put("isAuthor", true);
            
            response.put("data", data);
            
            System.out.println("Author login successful: isAuthor true returned.");
            return ResponseEntity.ok(response);
        } else {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", "Invalid author ID or password");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }
}





