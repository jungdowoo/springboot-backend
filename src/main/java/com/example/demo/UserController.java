package com.example.demo;

import com.example.demo.UserService;
import com.example.demo.UserVO;
import com.fileupload.FileUploadService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private FileUploadService fileUploadService;

    @PostMapping("/register")
    public ResponseEntity<?> createUser(@RequestBody Map<String, String> payload) {
    	System.out.println("Received payload: " + payload);
        String userId = payload.get("userId");
        String rawPassword = payload.get("userPwd");
        String userName = payload.get("userName");
        String phoneNum = payload.get("phoneNum");
        String roles = payload.getOrDefault("roles", "USER");

        userService.registerUser(userId, rawPassword, userName, phoneNum, roles);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> payload) {
        String userId = payload.get("userId");
        String rawPassword = payload.get("userPwd");

        UserVO user = userService.login(userId, rawPassword);
        if (user != null) {
            String token = userService.generateToken(user);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", new HashMap<String, Object>() {{
                put("userId", user.getUserId());
                put("userName", user.getUserName());
                put("token", token);
            }});
            System.out.println("로그인 성공:" + response);
            return ResponseEntity.ok(response);
        } else {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Invalid credentials.");
            System.out.println("로그인 실패:" + response);
            return ResponseEntity.status(401).body(response);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable("id") String id) {
        UserVO user = userService.getUserById(id);
        if (user != null) {
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable("id") String id, @RequestBody Map<String, String> payload) {
        UserVO user = userService.getUserById(id);
        if (user != null) {

            if (payload.containsKey("userName")) {
                user.setUserName(payload.get("userName"));
            }
            if (payload.containsKey("phoneNum")) {
                user.setPhoneNum(payload.get("phoneNum"));
            }
            userService.updateUser(user);
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable("id") String id) {
        if (userService.userExists(id)) {
            userService.deleteUser(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/check-duplicate")
    public ResponseEntity<Map<String, Object>> checkDuplicate(@RequestBody Map<String, String> payload) {
        String userId = payload.get("userId");
        Map<String, Object> response = new HashMap<>();
        boolean isDuplicate = userService.userExists(userId);
        response.put("isDuplicate", isDuplicate);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/check-name-duplicate")
    public ResponseEntity<Map<String, Object>> checkNameDuplicate(@RequestBody Map<String, String> payload) {
        String userName = payload.get("userName");
        Map<String, Object> response = new HashMap<>();
        boolean isDuplicate = userService.userExistsByName(userName);
        response.put("isDuplicate", isDuplicate);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/profile/current")
    public ResponseEntity<?> getCurrentUserProfile() {
        UserVO user = userService.getCurrentUserProfile();
        System.out.println("Fetched User Profile:" + user);
        if (user != null) {
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User profile not found");
        }
}
    @PostMapping("/profile/upload")
    public ResponseEntity<?> uploadProfileImage(@RequestParam("image") MultipartFile image) {
        if (image.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("없는 이미지 요청");
        }
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            System.out.println("Authenticated username:" + username);

            UserVO user = userService.getUserProfile(username);
            if (user == null) {
            	System.out.println("User not found for username:" + username);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("사용자를 찾을 수 없습니다.");
            }

            String imageUrl = fileUploadService.handleFileUpload(image);
            userService.updateUserProfileImage(username, imageUrl);

            return ResponseEntity.ok().body(Map.of("imageUrl", imageUrl));
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload image");
        }
    }


    @PutMapping("/profile/{userId}/nickname")
    public ResponseEntity<?> updateNickname(@PathVariable("userId") String userId, @RequestBody Map<String, String> payload) {
        String newUserName = payload.get("userName");

        if (userService.userExistsByName(newUserName)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("이미 존재하는 닉네임입니다.");
        }

        UserVO updatedUser = userService.updateUserName(userId, newUserName);
        if (updatedUser != null) {
            return ResponseEntity.ok(updatedUser);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("사용자를 찾을 수 없습니다.");
        }
    }
    @PutMapping("/profile/{userId}/id")
    public ResponseEntity<?> updateUserId(@PathVariable("userId") String currentUserId, @RequestBody Map<String, String> payload) {
        String newUserId = payload.get("userId");

        try {
            UserVO updatedUser = userService.updateUserId(currentUserId, newUserId);
            if (updatedUser != null) {
                return ResponseEntity.ok(updatedUser);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("사용자를 찾을 수 없습니다.");
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }
    @PutMapping("/profile/{userId}/password")
    public ResponseEntity<?> updatePassword(@PathVariable("userId") String userId, @RequestBody Map<String, String> payload) {
    	String newPassword = payload.get("password");
    	try {
    		UserVO updatedUser = userService.updatePassword(userId, newPassword);
    		if(updatedUser != null) {
    			return ResponseEntity.ok("비밀번호 변경이 완료되었습니다.");
    		} else {
    			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("사용자를 찾을 수 없습니다.");
    		}
    	} catch (Exception e) {
    		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("비밀번호 변경 중 오류 발생");
    	}
    }
    @PutMapping("/profile/{userId}/phone")
    public ResponseEntity<?> updatePhoneNum(@PathVariable("userId") String userId, @RequestBody Map<String, String> payload){
    	String newPhoneNum = payload.get("phoneNum");
    	try {
    		UserVO updatedUser = userService.updatePhoneNum(userId, newPhoneNum);
    		if(updatedUser != null) {
    			return ResponseEntity.ok("휴대폰 번호가 성공적으로 변경되었습니다.");
    		} else {
    			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("사용자를 찾을 수 없습니다.");
    		}
    	} catch (Exception e) {
    		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("휴대폰 번호 변경 중 오류 발생");
    	}
    }





}












