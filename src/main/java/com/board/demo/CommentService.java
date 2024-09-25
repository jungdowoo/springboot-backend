package com.board.demo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class CommentService {

	@Autowired BoardService boardService;
	
    @Autowired
    private CommentRepository commentRepository;

    public List<Comment> getCommentsByPostId(String postId) {
        Optional<BoardVO> postOptional = boardService.getPostById(postId);
        if (postOptional.isPresent()) {
            return commentRepository.findByPost(postOptional.get());
        } else {
            return new ArrayList<>(); // 게시물이 없을 경우 빈 리스트 반환
        }
    }

    public Comment createComment(Comment comment) {
        return commentRepository.save(comment);
    }
    
    // 댓글 수정기능 
    public Comment updateComment(Comment comment) {
    	return commentRepository.save(comment);
    }
    // 댓글 삭제 기능
    public void deleteComment(Comment comment) {
    	commentRepository.delete(comment);
    }
    // 댓글 ID로 댓글을 조회하는 기능
    public Optional<Comment> getCommentById(Long id) {
    	return commentRepository.findById(id);
    }
}




