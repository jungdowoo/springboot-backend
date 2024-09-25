package com.author;

import java.util.UUID;

import javax.persistence.Entity;

import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Table;




@Entity
@Table(name = "authortbl")
public class AuthorVO {

    @Id
    private String id;
    private String authorName;
    private String authorId;
    private String authorPwd;
    private String authorPhoneNum;
    private String authorBio;

    @PrePersist
    public void prePersist() {
        if (id == null) {
            id = UUID.randomUUID().toString();
        }
    }
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public String getAuthorPwd() {
        return authorPwd;
    }

    public void setAuthorPwd(String authorPwd) {
        this.authorPwd = authorPwd;
    }

    public String getAuthorPhoneNum() {
        return authorPhoneNum;
    }

    public void setAuthorPhoneNum(String authorPhoneNum) {
        this.authorPhoneNum = authorPhoneNum;
    }

    public String getAuthorBio() {
        return authorBio;
    }

    public void setAuthorBio(String authorBio) {
        this.authorBio = authorBio;
    }

    @Override
    public String toString() {
        return "AuthorVO{" +
                "id='" + id + '\'' +
                ", authorName='" + authorName + '\'' +
                ", authorId='" + authorId + '\'' +
                ", authorPwd='" + authorPwd + '\'' +
                ", authorPhoneNum='" + authorPhoneNum + '\'' +
                ", authorBio='" + authorBio + '\'' +
                '}';
    }
}