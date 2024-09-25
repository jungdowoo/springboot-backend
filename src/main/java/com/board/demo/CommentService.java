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
            return new ArrayList<>(); 
        }
    }

    public Comment createComment(Comment comment) {
        return commentRepository.save(comment);
    }
    
    public Comment updateComment(Comment comment) {
    	return commentRepository.save(comment);
    }
    public void deleteComment(Comment comment) {
    	commentRepository.delete(comment);
    }
    public Optional<Comment> getCommentById(Long id) {
    	return commentRepository.findById(id);
    }
}




