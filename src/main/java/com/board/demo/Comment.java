package com.board.demo;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class Comment {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String userId;
	private String userName;
	private String profileImageUrl;
	
	
	public String getProfileImageUrl() {
		return profileImageUrl;
	}



	public void setProfileImageUrl(String profileImageUrl) {
		this.profileImageUrl = profileImageUrl;
	}



	public String getUserName() {
		return userName;
	}



	public void setUserName(String userName) {
		this.userName = userName;
	}

	private String content;
	private Date createdAt;
	
	@ManyToOne
	@JsonIgnore
	private BoardVO post;

	@PrePersist
	protected void onCreate() {
		this.createdAt = new Date();
	}
	
	
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public BoardVO getPost() {
		return post;
	}

	public void setPost(BoardVO post) {
		this.post = post;
	}
	
}
