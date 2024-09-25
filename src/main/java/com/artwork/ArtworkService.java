package com.artwork;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.author.AuthorRepository;
import com.author.AuthorVO;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class ArtworkService {

    @Autowired
    private ArtworkRepository artworkRepository;
    
    @Autowired
    private ArtworkImageRepository artworkImageRepository;
    
    @Autowired
    private AuthorRepository authorRepository;

    public ArtworkVO saveArtwork(String title, String content, String category, String subCategory, Double price, String deadline, String completionDate, List<MultipartFile> images, String authorId) {
    	System.out.println("Received authorId from JWT:" + authorId);
    	
    	AuthorVO author = authorRepository.findById(authorId)
    			.orElseThrow(() -> new RuntimeException("Invalid author ID"));
    	
        ArtworkVO artwork = new ArtworkVO();
        artwork.setTitle(title);
        //artwork.setDescription(description);
        artwork.setContent(content);
        artwork.setCategory(category);
        artwork.setSubCategory(subCategory);
        artwork.setPrice(price);
        artwork.setDeadline(deadline);
        artwork.setCompletionDate(completionDate);
        artwork.setAuthorId(authorId);

        List<String> imagePaths = new ArrayList<>();
        
        
        
        //  다중 이미지 업로드 처리
        if (images != null && !images.isEmpty()) {
            for (MultipartFile image : images) {
                if (!image.isEmpty()) {
                    String fileName = UUID.randomUUID().toString() + "_" + image.getOriginalFilename();
                    String uploadDir = "C:/temp/uploads/";

                    File uploadPath = new File(uploadDir);
                    if (!uploadPath.exists()) {
                        uploadPath.mkdirs(); 
                    }

                    try {
                        image.transferTo(new File(uploadDir + fileName));
                        System.out.println("File saved successfully" + fileName);
                       imagePaths.add(fileName);
                       System.out.println("Saved Image Path: " + fileName);
                    } catch (IOException e) {
                    	System.out.println("Failed to save file:" + fileName);
                        e.printStackTrace();
                    }
                }
        }
    }
       
        //artworkVO에 이미지 경로 리스트 설정
        if (!imagePaths.isEmpty()) {
            String imagePathsString = String.join(",", imagePaths); 
            artwork.setImagePaths(imagePathsString);
            System.out.println("All Image Paths: " + imagePathsString);
            System.out.println("Image paths saved:" + imagePathsString);
        } else {
            artwork.setImagePaths("");
            System.out.println("No images uploaded");
        }

        // Artwork 저장
        return artworkRepository.save(artwork);
    }
    
    public List<ArtworkVO> getArtworksByCategory(String category) {
    	List<ArtworkVO> artworks;
    	
        if (category == null || category.isEmpty()) {
            artworks = artworkRepository.findAll();
        } else {
            artworks = artworkRepository.findByCategory(category);
        }
    
    // 작품 리스트에 각 작품의 authorName설정
        for (ArtworkVO artwork : artworks) {
            AuthorVO author = authorRepository.findById(artwork.getAuthorId()).orElse(null);
            if (author != null) {
                artwork.setAuthorName(author.getAuthorName());  // 작가 이름 설정
            }
        }

        return artworks;  // List 반환
    }
    
    // 작품 상세페이지
   /* public ArtworkVO getArtworkById(String id) {
    	ArtworkVO artwork = artworkRepository.findById(id).orElse(null);
    	if (artwork != null) {
    		AuthorVO author = authorRepository.findById(artwork.getAuthorId()).orElse(null);
    		if(author != null) {
    			artwork.setAuthorName(author.getAuthorName());
    		}
    	}
    	return artwork;
    }*/
    public ArtworkVO getArtworkById(String id) {
        ArtworkVO artwork = artworkRepository.findById(id).orElse(null);
        
        // Check if artwork is found
        if (artwork != null) {
            System.out.println("Artwork found: " + artwork.getTitle());
            
            
            AuthorVO author = authorRepository.findById(artwork.getAuthorId()).orElse(null);
            
            
            if (author != null) {
                System.out.println("Author found: " + author.getAuthorName());
                artwork.setAuthorName(author.getAuthorName());
            } else {
                System.out.println("Author not found for ID: " + artwork.getAuthorId());
            }
        } else {
            System.out.println("Artwork not found for ID: " + id);
            artwork = new ArtworkVO();  
            artwork.setArtworkId(id);
            artwork.setTitle("Unknown Artwork");
        }
        
        return artwork;
    }
    // 검색 기능 추가
    public List<ArtworkVO> searchArtworks(String query) {
        // 제목 또는 내용에서 검색어가 포함된 작품을 필터링
        List<ArtworkVO> artworks = artworkRepository.findAll();
        List<ArtworkVO> filteredArtworks = new ArrayList<>();
        
        for (ArtworkVO artwork : artworks) {
            if (artwork.getTitle().toLowerCase().contains(query.toLowerCase()) ||
                artwork.getContent().toLowerCase().contains(query.toLowerCase())) {
                AuthorVO author = authorRepository.findById(artwork.getAuthorId()).orElse(null);
                if (author != null) {
                    artwork.setAuthorName(author.getAuthorName());
                }
                filteredArtworks.add(artwork);
            }
        }

        return filteredArtworks;
    }
    
    
    
    
}
