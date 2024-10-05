package com.artwork;

import com.author.AuthorRepository;
import com.author.AuthorVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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

    @Value(value = "#{systemProperties['upload.path']}")
    private String uploadPath;


    @Value(value = "#{systemProperties['upload.url']}")
    private String uploadUrl;

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

        if (images != null && !images.isEmpty()) {
            for (MultipartFile image : images) {
                if (!image.isEmpty()) {
                    String fileName = UUID.randomUUID().toString() + "_" + image.getOriginalFilename();
                    String uploadDir = uploadPath;

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
        if (!imagePaths.isEmpty()) {
            String imagePathsString = String.join(",", imagePaths);
            artwork.setImagePaths(imagePathsString);
            System.out.println("All Image Paths: " + imagePathsString);
            System.out.println("Image paths saved:" + imagePathsString);
        } else {
            artwork.setImagePaths("");
            System.out.println("No images uploaded");
        }

        return artworkRepository.save(artwork);
    }

    public List<ArtworkVO> getArtworksByCategory(String category) {
        List<ArtworkVO> artworks;

        if (category == null || category.isEmpty()) {
            artworks = artworkRepository.findAll();
        } else {
            artworks = artworkRepository.findByCategory(category);
        }

        for (ArtworkVO artwork : artworks) {
            AuthorVO author = authorRepository.findById(artwork.getAuthorId()).orElse(null);
            if (author != null) {
                artwork.setAuthorName(author.getAuthorName());
            }
        }

        return artworks;
    }

    public ArtworkVO getArtworkById(String id) {
        ArtworkVO artwork = artworkRepository.findById(id).orElse(null);

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

    public List<ArtworkVO> searchArtworks(String query) {
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
