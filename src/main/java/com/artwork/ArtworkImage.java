package com.artwork;

import javax.persistence.*;

@Entity
@Table(name = "artwork_images")
public class ArtworkImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "artwork_id")
    private String artworkId;

    @Column(name = "image_path", columnDefinition = "TEXT")
    private String imagePath;

    // Getter and Setter methods
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getArtworkId() {
        return artworkId;
    }

    public void setArtworkId(String artworkId) {
        this.artworkId = artworkId;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}
