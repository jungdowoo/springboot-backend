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
      
    @GetMapping("/{id}") 
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
            @RequestParam("content") String content,
            @RequestParam("category") String category,
            @RequestParam("subCategory") String subCategory,
            @RequestParam("price") Double price,
            @RequestParam("deadline") String deadline,
            @RequestParam("completionDate") String completionDate,
            @RequestParam(value="images", required = false) MultipartFile[] images,
            HttpServletRequest request) {
        
        String token = jwtTokenProvider.resolveToken(request);
        if (token == null || !jwtTokenProvider.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); 
        }
        String authorId = jwtTokenProvider.getUsername(token);  

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
    @PostMapping("/uploadImage")
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
        try {
            String uploadDir = "C:/temp/uploads/";
            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            File uploadPath = new File(uploadDir);
            
            if (!uploadPath.exists()) {
                boolean isCreated = uploadPath.mkdirs();
                if (isCreated) {
                } else {
                }
            } else {
            }

            file.transferTo(new File(uploadDir + fileName));

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















