package com.artwork;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.security.JwtTokenProvider;



@RestController
@RequestMapping("/api/artworks")
public class ArtworkController {

    @Autowired
    private ArtworkService artworkService;
    
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
      

    // 작품 등록 상세 페이지
    @GetMapping("/{id}") // 작품의 id로 작품 조회
    public ResponseEntity<ArtworkVO> getArtworkById(@PathVariable String id) {
        ArtworkVO artwork = artworkService.getArtworkById(id);
        if (artwork != null) {
            return ResponseEntity.ok(artwork);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
    @PostMapping("/artupload")
    public ResponseEntity<?> uploadArtwork(
            @RequestParam("title") String title,
            //@RequestParam("description") String description,
            @RequestParam("content") String content,
            @RequestParam("category") String category,
            @RequestParam("subCategory") String subCategory,
            @RequestParam("price") Double price,
            @RequestParam("deadline") String deadline,
            @RequestParam("completionDate") String completionDate,
            @RequestParam(value="images", required = false) MultipartFile[] images,
            HttpServletRequest request) {
        
        // JWT 토큰에서 author_id를 추출
        String token = jwtTokenProvider.resolveToken(request);
        if (token == null || !jwtTokenProvider.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // 토큰이 없거나 유효하지 않으면 에러 반환
        }
        String authorId = jwtTokenProvider.getUsername(token);  // JWT에서 사용자 ID 추출

        try{
        	List<MultipartFile> imageList = (images != null) ? Arrays.asList(images) : Collections.emptyList();
        	 
        	 ArtworkVO newArtwork = artworkService.saveArtwork(
        			 title, content, category, subCategory, price, deadline, completionDate, imageList, authorId);
             return ResponseEntity.ok(newArtwork);
        } catch (Exception e) {
        	e.printStackTrace();
        	return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while uploading the artwork");
        	 	
        }
       
    }
 // 이미지 업로드 처리 API
    @PostMapping("/uploadImage")
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
        try {
            // 이미지 파일 저장 디렉토리
            String uploadDir = "C:/temp/uploads/";
            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            File uploadPath = new File(uploadDir);
            // 디렉토리가 존재하지 않으면 생성
            if (!uploadPath.exists()) {
                boolean isCreated = uploadPath.mkdirs();
                if (isCreated) {
                    System.out.println("디렉토리 생성 성공: " + uploadDir);
                } else {
                    System.out.println("디렉토리 생성 실패: " + uploadDir);
                }
            } else {
                System.out.println("디렉토리 이미 존재함: " + uploadDir);
            }

            // 파일을 저장
            file.transferTo(new File(uploadDir + fileName));
            System.out.println("File 저장 성공:" + fileName);

            // 파일 URL을 반환
            String fileUrl = "http://localhost:8080/uploads/" + fileName;
            return ResponseEntity.ok(Collections.singletonMap("url", fileUrl));
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Image upload failed");
        }
    }

    @GetMapping(produces = "application/json")
    public ResponseEntity<List<ArtworkVO>> getArtworks(@RequestParam(value = "category", required = false) String category) {
        List<ArtworkVO> artworks = artworkService.getArtworksByCategory(category);
        return ResponseEntity.ok(artworks);
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<ArtworkVO>> searchArtworks(@RequestParam("query") String query) {
        try {
            List<ArtworkVO> searchResults = artworkService.searchArtworks(query);
            if (searchResults.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            return ResponseEntity.ok(searchResults);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    } 
}















