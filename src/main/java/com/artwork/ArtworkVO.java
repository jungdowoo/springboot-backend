package com.artwork;

import java.util.List;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "artworktbl")
public class ArtworkVO {

	@Id
	private String artworkId;
	@Column(name="author_id", nullable=false)
	private String authorId;
	private String authorName;
	private String authorDescription;
	
	public String getAuthorDescription() {
		return authorDescription;
	}

	public void setAuthorDescription(String authorDescription) {
		this.authorDescription = authorDescription;
	}

	private String title;
	//@Column(columnDefinition = "TEXT")  
    //private String description;
	
	 @Column(columnDefinition = "LONGTEXT")
	   private String content; 
	
	 public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	private String category;
	private String subCategory;
	private Double price;
	private String deadline;
	private String completionDate;
	
	@Column(name = "image_path")  
    private String imagePaths;
	
	
	
	

	public String getImagePaths() {
		return imagePaths;
	}

	public void setImagePaths(String imagePaths) {
		this.imagePaths = imagePaths;
	}

	public ArtworkVO() {
		this.artworkId =UUID.randomUUID().toString();
	}

	public String getArtworkId() {
		return artworkId;
	}

	public void setArtworkId(String artworkId) {
		this.artworkId = artworkId;
	}

	public String getAuthorId() {
		return authorId;
	}

	public void setAuthorId(String authorId) {
		this.authorId = authorId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	/*public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}*/

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getSubCategory() {
		return subCategory;
	}

	public void setSubCategory(String subCategory) {
		this.subCategory = subCategory;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public String getDeadline() {
		return deadline;
	}

	public void setDeadline(String deadline) {
		this.deadline = deadline;
	}

	public String getCompletionDate() {
		return completionDate;
	}

	public void setCompletionDate(String completionDate) {
		this.completionDate = completionDate;
	}

	
	public String getAuthorName() {
		return authorName;
	}

	public void setAuthorName(String authorName) {
		this.authorName = authorName;
	}

	
	
}
