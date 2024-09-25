package com.example.demo;

import java.util.Set;
import java.util.UUID;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.PrePersist;
import javax.persistence.Table;

@Entity
@Table(name = "usertbl")
public class UserVO {

    @Id
    private String id;
    
    @Column(name = "user_name")
    private String userName;
    
    @Column(name = "user_id")
    private String userId;
    
    @Column(name = "user_pwd")
    private String userPwd;
    
    @Column(name = "phone_num")
    private String phoneNum;
    
    @Column(name = "roles")
    private String roles;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "profile_image")
    private String profileImage;

    @PrePersist
    public void prePersist() {
    	if(id == null) {
    		id = UUID.randomUUID().toString();
    	}
    	if(userName == null) {
    		userName = "defaultUserName";
    	}
    }
    
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }
    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public String getUserPwd() {
        return userPwd;
    }
    public void setUserPwd(String userPwd) {
        this.userPwd = userPwd;
    }
    public String getPhoneNum() {
        return phoneNum;
    }
    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }
    public String getRoles() {
        return roles;
    }
    public void setRoles (String roles) {
        this.roles = roles;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) { 
        this.description = description;
    }
    public String getProfileImage() {
        return profileImage;
    }
    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    @Override
    public String toString() {
        return "UserVO{" +
                "id=" + id +
                ", userName='" + userName + '\'' +
                ", userId='" + userId + '\'' +
                ", userPwd='" + userPwd + '\'' +
                ", phoneNum='" + phoneNum + '\'' +
                ", roles=" + roles +
                ", description='" + description + '\'' +
                ", profileImage='" + profileImage + '\'' +
                '}';
    }
}
