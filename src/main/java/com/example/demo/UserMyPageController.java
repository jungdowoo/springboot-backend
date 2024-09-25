package com.example.demo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.Map;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/user/profile")
public class UserMyPageController {

    @Autowired
    private UserService userService;
    
    @Value("${file.upload-dir}")
    private String uploadDir;

    @GetMapping("/{username}")
    public ResponseEntity<UserVO> getProfile(@PathVariable String username) {
        UserVO user = userService.getUserProfile(username);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/current")
    public ResponseEntity<UserVO> getCurrentUserProfile() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println("Current username:" + username);
        UserVO user = userService.getUserProfile(username);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(user);
    }


    
    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> uploadImage(@RequestParam("image") MultipartFile image) {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            String fileName = System.currentTimeMillis() + "_" + image.getOriginalFilename();
            Path imagePath = Paths.get(uploadDir, fileName);

            if (Files.notExists(imagePath.getParent())) {
                Files.createDirectories(imagePath.getParent());
            }

            Files.copy(image.getInputStream(), imagePath, StandardCopyOption.REPLACE_EXISTING);
            
            String imageUrl = "/uploads/" + fileName;
            userService.updateUserProfileImage(username, imageUrl);

            Map<String, String> response = Map.of("imageUrl", imageUrl);
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body(Map.of("error", "Error uploading image: " + e.getMessage()));
        }
    }

    @PostMapping("/delete-image")
    public ResponseEntity<String> deleteProfileImage(@RequestBody Map<String, String> payload) {
        String userId = payload.get("userId");
        try {
            String result = userService.deleteProfileImage(userId);
            return ResponseEntity.ok(result);
        } catch (IOException e) {
        	e.printStackTrace();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
        	e.printStackTrace();
        	return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error occurred while deleting image");
        }
    }

    private String getUserProfileImage(String userId) {
       
        return "currentImage.jpg";
    }
    
   
    
}
















