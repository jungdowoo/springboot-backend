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
    
 // JWT 토큰 생성 메서드
    public String generateToken(UserVO user) {
        return Jwts.builder()
                .setSubject(user.getUserId())
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpiration))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    // 모든 사용자 조회
    public List<UserVO> getAllUsers(){
        return userRepository.findAll();
    }
    
    // 사용자 등록
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
    
    
    // 비밀번호 검증
    public boolean checkPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword); 
    }
    
    // 사용자 존재 여부 확인
    public boolean userExists(String userId) {
        return userRepository.existsByUserId(userId);
    }
    
    // 닉네임 존재 여부 확인
    public boolean userExistsByName(String userName) {
        return userRepository.existsByUserName(userName);
    }
    
    // 사용자 ID로 사용자 정보 조회
    public UserVO getUserById(String id) {
        return userRepository.findById(id).orElse(null);
    }
    
    // 사용자 정보 업데이트
    public UserVO updateUser(UserVO user) {
        return userRepository.save(user);
    }
    
    // 로그인 로직 
    public UserVO login(String userId, String rawPassword) {
        UserVO user = userRepository.findByUserId(userId);
        if (user == null) {
            logger.info("로그인 실패 - 사용자 ID: {} 존재하지 않음", userId);
            return null; // 사용자 존재하지 않음
        }

        logger.info("로그인 시도 - 사용자 ID: {}", userId);
        boolean isPasswordValid = checkPassword(rawPassword, user.getUserPwd());
        if (isPasswordValid) {
            logger.info("로그인 성공 - 사용자 ID: {}", userId);
            return user;
        } else {
            logger.info("로그인 실패 - 비밀번호 불일치, 사용자 ID: {}", userId);
            return null; // 비밀번호 불일치
        }
    }
    
    // 사용자 삭제
    public void deleteUser(String id) {
        userRepository.deleteById(id);
    }
    
    // 사용자 ID로 사용자 정보 조회
    public UserVO findByUserId(String userId) {
        return userRepository.findByUserId(userId); 
    }
    // 사용자 프로필 조회
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
    
    // 사용자 프로필 업데이트
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
 // 프로필 이미지 업로드
    public String uploadProfileImage(MultipartFile image) throws IOException {
    	logger.info("uploadProfileImage 메서드가 호출되었습니다.");
    	
        // 파일 이름 생성
        String fileName = System.currentTimeMillis() + "_" + image.getOriginalFilename();
        logger.info("Attempting to save file with name:" + fileName);

        // 파일 저장 경로
        java.nio.file.Path filePath = Paths.get(uploadDir, fileName);
        logger.info("Saveing file to path:" + filePath);
        
        try {
            // 파일 저장
            Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            logger.info("File successfully saved:" + filePath);

            // 사용자 이름 가져오기
            String userId = SecurityContextHolder.getContext().getAuthentication().getName();
            logger.info("Authenticated userId:" + userId);
            
            logger.info("Attempting to find user with userId:" + userId);
            
            UserVO user = userRepository.findByUserId(userId);
            if (user != null) {
                String absoluteImageUrl = "/uploads/" + fileName;
                user.setProfileImage(absoluteImageUrl);
                userRepository.save(user);
                logger.info("User profile image updated successfully:"+ absoluteImageUrl);
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
    // 프로필 이미지 업데이트 메서드 추가
    public void updateUserProfileImage(String userId, String imageUrl) {
    	logger.info("Attempting to find user with userId:" + userId);
    	
    	UserVO user = userRepository.findByUserId(userId);
    	if(user != null) {
    		user.setProfileImage(imageUrl);
    		userRepository.save(user);
    	} else {
    		logger.error("User not found for userId:" + userId);
    	}
    }
    
    // 닉네임변경
    public UserVO updateUserName(String userId, String newUserName) {
        UserVO user = userRepository.findByUserId(userId);
        if (user != null) {
            user.setUserName(newUserName);
            return userRepository.save(user); // 변경된 닉네임을 저장
        }
        return null; // 사용자를 찾을 수 없는 경우 null 반환
    }
    
    // 아이디 변경
    public UserVO updateUserId(String currentUserId, String newUserId) {
    	// 이미 사용중인 아디확인
    	if (userRepository.existsById(newUserId)) {
    		throw new IllegalArgumentException("이미 존재하는 아이디입니다.");
    	}
    	// 사용자 조회
    	UserVO user = userRepository.findByUserId(currentUserId);
    	if(user != null) {
    		user.setUserId(newUserId);
    		return userRepository.save(user);
    	}
    	return null;
    }
    // 비밀번호 변경
    public UserVO updatePassword(String userId, String newPassword) {
    	UserVO user = userRepository.findByUserId(userId);
    	if(user != null) {
    		String encodedPassword = passwordEncoder.encode(newPassword);
    		user.setUserPwd(encodedPassword);
    		return userRepository.save(user);
    	}
    	return null;
    }
    // 휴대폰 번호 변경
    public UserVO updatePhoneNum(String userId, String newPhoneNum) {
    	UserVO user = userRepository.findByUserId(userId);
    	if (user != null) {
    		user.setPhoneNum(newPhoneNum);
    		return userRepository.save(user);
    	}
    	return null;
    }
    
    
    
}






















