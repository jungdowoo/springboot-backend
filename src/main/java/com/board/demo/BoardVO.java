package com.board.demo;



import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import com.board.demo.Comment;
import com.fasterxml.jackson.annotation.JsonFormat;




@Entity
@Table(name = "posts")
public class BoardVO {

    @Id
    private String id;
    private String title;
    private String content;
    private String fileName;
    private String category;
    private String userId;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern ="yyyy-MM-dd")
    private Date dueDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date deadline;
    private String budget;
    
    public String getBudget() {
		return budget;
	}
	public void setBudget(String budget) {
		this.budget = budget;
	}
	public List<Comment> getComments() {
		return comments;
	}
	public void setComments(List<Comment> comments) {
		this.comments = comments;
	}

	@OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments;
    
    public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public Date getDueDate() {
		return dueDate;
	}
	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}
	public Date getDeadline() {
		return deadline;
	}
	public void setDeadline(Date deadline) {
		this.deadline = deadline;
	}
	@PrePersist
    public void prepersist() {
    	if (id == null) {
    		id = UUID.randomUUID().toString().replace("-", "").substring(0,24);
    	}
    }
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    
    @Override
    public String toString() {
        return "BoardVO{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", fileName='" + fileName + '\'' +
                ", category='" + category + '\'' +
                ", userId='" + userId + '\'' +
                ", dueDate='" + dueDate + '\'' +
                ", deadline='" + deadline + '\'' +
                ", comments=" +comments +
                '}';
    }
    
}
