package com.board.demo;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;




import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

@RestController
@RequestMapping("/api/posts")
public class BoardController {

    @Autowired
    private BoardService boardService;

    @Autowired
    private FileStorageService fileStorageService;
    
    @Autowired
    private CommentService commentService;

    @GetMapping
    public List<BoardVO> getAllPosts() {
        return boardService.getAllPosts();
    }

    @GetMapping("/{id}")
    public ResponseEntity<BoardVO> getPostById(@PathVariable String id) {
        Optional<BoardVO> post = boardService.getPostById(id);
        return post.map(ResponseEntity::ok)
                   .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }

    @PostMapping
    public ResponseEntity<?> createPost(
            @RequestPart("title") String title,
            @RequestPart("content") String content,
            @RequestPart("category") String category,
            @RequestPart("subCategory") String subCategory,
            @RequestPart("userId") String userId,
            @RequestPart("dueDate") String dueDate,
            @RequestPart("deadline") String deadline, 
            @RequestPart(value = "file", required = false) MultipartFile file) {
        try {
        	 SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

            
	         Date parsedDueDate = formatter.parse(dueDate);
	         Date parsedDeadline = formatter.parse(deadline);
	         
            BoardVO post = new BoardVO();
            post.setTitle(title);
            post.setContent(content);
            post.setCategory(category);
            post.setUserId(userId);
            post.setDueDate(parsedDueDate);
            post.setDeadline(parsedDeadline);
            post.setCategory(category);

            String fileName = file != null ? fileStorageService.storeFile(file) : null;
            post.setFileName(fileName);

            BoardVO savedPost = boardService.createPost(post);
            return ResponseEntity.ok(savedPost);
        } catch (Exception e) {
            e.printStackTrace(); // 예외를 콘솔에 출력
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("{\"error\":\"Error creating post: " + e.getMessage() + "\"}");
        }
    }
    // 게시물 수정기능
    @PutMapping("/{id}")
    public ResponseEntity<?> updatePost(
            @PathVariable String id,
            @RequestBody BoardVO updatedPost,
            @RequestHeader("Authorization") String token) {
        try {
            Optional<BoardVO> postOptional = boardService.getPostById(id);
            if (postOptional.isPresent()) {
                BoardVO post = postOptional.get();
                String userId = getUserIdFromToken(token);  // 토큰에서 사용자 ID 추출

                if (post.getUserId().equals(userId)) {
                    post.setTitle(updatedPost.getTitle());
                    post.setContent(updatedPost.getContent());
                    post.setCategory(updatedPost.getCategory());
                    post.setDueDate(updatedPost.getDueDate());
                    post.setDeadline(updatedPost.getDeadline());
                    post.setBudget(updatedPost.getBudget());

                    BoardVO savedPost = boardService.updatePost(post);
                    return ResponseEntity.ok(savedPost);
                } else {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                         .body("수정 권한이 없습니다.");
                }
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("게시물을 찾을 수 없습니다.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Error updating post: " + e.getMessage());
        }
    }
    // 게시물 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePost(
            @PathVariable String id,
            @RequestHeader("Authorization") String token) {
        try {
            Optional<BoardVO> postOptional = boardService.getPostById(id);
            if (postOptional.isPresent()) {
                BoardVO post = postOptional.get();
                String userId = getUserIdFromToken(token);  // 토큰에서 사용자 ID 추출

                if (post.getUserId().equals(userId)) {
                    boardService.deletePost(post);
                    return ResponseEntity.ok("게시물이 삭제되었습니다.");
                } else {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                         .body("삭제 권한이 없습니다.");
                }
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("게시물을 찾을 수 없습니다.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Error deleting post: " + e.getMessage());
        }
    }

    // 토큰에서 사용자 ID를 추출하는 메서드 (임의로 작성, 실제 구현에 맞게 수정 필요)
    private String getUserIdFromToken(String token) {
      
    	String  secretKey="YfrMNmAK2IXw1ZN2rMFaEKNGOLrSMyErj+bBBeEqtvs=";
    	Claims claims = Jwts.parser()
    								.setSigningKey(secretKey)
    								.parseClaimsJws(token.replace("Bearer", ""))
    								.getBody();
    	return claims.getSubject();
    }
    
    // 특정 게시물의 댓글 가져오기
    @GetMapping("/{id}/comments")
    public ResponseEntity<List<Comment>> getCommentsByPostId(@PathVariable String id) {
        Optional<BoardVO> postOptional = boardService.getPostById(id);
        if (postOptional.isPresent()) {
            List<Comment> comments = commentService.getCommentsByPostId(id);
            return ResponseEntity.ok(comments);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
    // 댓글생성
    @PostMapping("/{id}/comments")
    public ResponseEntity<?> createComment(
            @PathVariable String id,
            @RequestBody CommentDTO commentRequest) {
        try {
        	System.out.println("Received userName: " + commentRequest.getUserName());
            System.out.println("Received userId: " + commentRequest.getUserId());
            System.out.println("Received content: " + commentRequest.getContent());
            Optional<BoardVO> postOptional = boardService.getPostById(id);
            if (postOptional.isPresent()) {
                BoardVO post = postOptional.get();
                Comment comment = new Comment();
                comment.setUserId(commentRequest.getUserId());
                comment.setUserName(commentRequest.getUserName());
                comment.setProfileImageUrl(commentRequest.getProfileImageUrl());
                comment.setContent(commentRequest.getContent());
                comment.setCreatedAt(new Date());
                comment.setPost(post);

                Comment savedComment = commentService.createComment(comment);
                return ResponseEntity.ok(savedComment);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("게시물을 찾을 수 없습니다.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Error creating comment: " + e.getMessage());
        }
    }

 // 댓글 수정 기능 추가
    @PutMapping("/{postId}/comments/{commentId}")
    public ResponseEntity<?> updateComment(
            @PathVariable String postId,
            @PathVariable Long commentId,
            @RequestBody CommentDTO commentRequest) {
        try {
            Optional<Comment> commentOptional = commentService.getCommentById(commentId);
            if (commentOptional.isPresent()) {
                Comment comment = commentOptional.get();
                if (comment.getUserId().equals(commentRequest.getUserId())) {
                    comment.setContent(commentRequest.getContent());
                    Comment updatedComment = commentService.updateComment(comment);
                    return ResponseEntity.ok(updatedComment);
                } else {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                         .body("수정 권한이 없습니다.");
                }
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("댓글을 찾을 수 없습니다.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Error updating comment: " + e.getMessage());
        }
    }

    // 댓글 삭제 기능 추가
    @DeleteMapping("/{postId}/comments/{commentId}")
    public ResponseEntity<?> deleteComment(
            @PathVariable String postId,
            @PathVariable Long commentId,
            @RequestBody CommentDTO commentRequest) {
        try {
            Optional<Comment> commentOptional = commentService.getCommentById(commentId);
            if (commentOptional.isPresent()) {
                Comment comment = commentOptional.get();
                if (comment.getUserId().equals(commentRequest.getUserId())) {
                    commentService.deleteComment(comment);
                    return ResponseEntity.ok("댓글이 삭제되었습니다.");
                } else {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                         .body("삭제 권한이 없습니다.");
                }
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("댓글을 찾을 수 없습니다.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Error deleting comment: " + e.getMessage());
        }
    }	
    
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception e) {
        e.printStackTrace(); // 예외를 콘솔에 출력
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                             .body("{\"error\":\"Error: " + e.getMessage() + "\"}");
    }
}
