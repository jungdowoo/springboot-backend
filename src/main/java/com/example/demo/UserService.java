package com.example.demo;

import com.example.demo.UserVO;
import com.fileupload.FileUploadService;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.author.AuthorRepository;

@Service
public class UserService {

	@Value("${file.upload-dir}")
	private String uploadDir;
	
	@Value("${jwt.secret}")
    private String jwtSecret;
	
	 @Value("${jwt.expiration}")
    private long jwtExpiration;
	 
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private AuthorRepository authorRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private FileUploadService fileUploadService;
    
    private static final Logger logger = LoggerFactory.getLogger(UserService.class); 
    
    public String generateToken(UserVO user) {
        return Jwts.builder()
                .setSubject(user.getUserId())
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpiration))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    public List<UserVO> getAllUsers(){
        return userRepository.findAll();
    }
    
    public void registerUser(String userId, String rawPassword, String userName, String phoneNum, String roles) {
        String encodedPassword = passwordEncoder.encode(rawPassword); 
        UserVO user = new UserVO();
        user.setUserId(userId);
        user.setUserPwd(encodedPassword);
        user.setUserName(userName);
        user.setPhoneNum(phoneNum);
        user.setRoles(roles);
        userRepository.save(user);
    }
    
    public boolean checkPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword); 
    }
    
    public boolean userExists(String userId) {
        return userRepository.existsByUserId(userId);
    }
   
    public boolean userExistsByName(String userName) {
        return userRepository.existsByUserName(userName);
    }
    
    public UserVO getUserById(String id) {
        return userRepository.findById(id).orElse(null);
    }
    
    public UserVO updateUser(UserVO user) {
        return userRepository.save(user);
    }
    
    public UserVO login(String userId, String rawPassword) {
        UserVO user = userRepository.findByUserId(userId);
        if (user == null) {
            logger.info("로그인 실패 - 사용자 ID: {} 존재하지 않음", userId);
            return null; 
        }

        logger.info("로그인 시도 - 사용자 ID: {}", userId);
        boolean isPasswordValid = checkPassword(rawPassword, user.getUserPwd());
        if (isPasswordValid) {
            logger.info("로그인 성공 - 사용자 ID: {}", userId);
            return user;
        } else {
            logger.info("로그인 실패 - 비밀번호 불일치, 사용자 ID: {}", userId);
            return null; 
        }
    }
    
    public void deleteUser(String id) {
        userRepository.deleteById(id);
    }
    
    public UserVO findByUserId(String userId) {
        return userRepository.findByUserId(userId); 
    }
    public UserVO getUserProfile(String userId) {
    	return userRepository.findByUserId(userId);
    }
    
    public UserVO getCurrentUserProfile() {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        logger.info("Extracted Username from SecurityContextHolder: " + userId);
        
        logger.info("Attaempting to find user with username:" + userId);
        
        UserVO user = userRepository.findByUserId(userId);
        if (user == null) {
            logger.error("User not found for userId: " + userId);
        } else {
        	logger.info ("Fetched User Profile:" + user.toString());
        }
        return user;
    }
    
    public UserVO updateUserProfile(String username, UserVO updatedUser) {
    	UserVO user = userRepository.findByUserId(username);
    	if(user != null) {
    		user.setUserName(updatedUser.getUserName());
    		user.setDescription(updatedUser.getDescription());
    		return userRepository.save(user);
    	} else {
    		return null;
    	}
    }
    public String uploadProfileImage(MultipartFile image) throws IOException {
       
        String fileName = System.currentTimeMillis() + "_" + image.getOriginalFilename();
        
        java.nio.file.Path filePath = Paths.get(uploadDir, fileName);
        try {
            Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            String userId = SecurityContextHolder.getContext().getAuthentication().getName();
            
            UserVO user = userRepository.findByUserId(userId);
            if (user != null) {
                String absoluteImageUrl = "/uploads/" + fileName;
                user.setProfileImage(absoluteImageUrl);
                userRepository.save(user);
                return absoluteImageUrl;
            } else {
               logger.error("User not found for userId:" + userId);
                throw new IOException("User not found for userId: " + userId);
            }
        } catch (IOException e) {
           logger.error("Error occurred while saving file:" + e.getMessage());
            throw e;
        }
    }

    public String deleteProfileImage(String userId) throws IOException {
        String currentImage = getUserProfileImage(userId);
        if (currentImage != null) {
            java.nio.file.Path filePath = Paths.get(uploadDir,   currentImage);
            Files.delete(filePath);
            return "Image deleted successfully";
        } else {
            throw new IOException("Image not found for user: " + userId);
        }
    }
    
    private String getUserProfileImage(String userId) {
        UserVO user = userRepository.findByUserId(userId);
        if (user != null) {
            return user.getProfileImage();
        }
        return null;
    }
    public void updateUserProfileImage(String userId, String imageUrl) {
    	
    	UserVO user = userRepository.findByUserId(userId);
    	if(user != null) {
    		user.setProfileImage(imageUrl);
    		userRepository.save(user);
    	} else {
    		logger.error("User not found for userId:" + userId);
    	}
    }
    
    public UserVO updateUserName(String userId, String newUserName) {
        UserVO user = userRepository.findByUserId(userId);
        if (user != null) {
            user.setUserName(newUserName);
            return userRepository.save(user); 
        }
        return null; 
    }
    
    public UserVO updateUserId(String currentUserId, String newUserId) {
    	
    	if (userRepository.existsById(newUserId)) {
    		throw new IllegalArgumentException("이미 존재하는 아이디입니다.");
    	}
    	UserVO user = userRepository.findByUserId(currentUserId);
    	if(user != null) {
    		user.setUserId(newUserId);
    		return userRepository.save(user);
    	}
    	return null;
    }
    
    public UserVO updatePassword(String userId, String newPassword) {
    	UserVO user = userRepository.findByUserId(userId);
    	if(user != null) {
    		String encodedPassword = passwordEncoder.encode(newPassword);
    		user.setUserPwd(encodedPassword);
    		return userRepository.save(user);
    	}
    	return null;
    }
    public UserVO updatePhoneNum(String userId, String newPhoneNum) {
    	UserVO user = userRepository.findByUserId(userId);
    	if (user != null) {
    		user.setPhoneNum(newPhoneNum);
    		return userRepository.save(user);
    	}
    	return null;
    }
    
    
    
}






















