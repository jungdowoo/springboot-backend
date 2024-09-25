package com.board.demo;

import java.util.List;
import java.util.Optional;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BoardService {

	
	@Autowired
	private BoardRepository boardRepository;
	
	public List<BoardVO> getAllPosts() {
		return boardRepository.findAll();
	}
	
	public Optional<BoardVO> getPostById(String id) {
		return boardRepository.findById(id);
	}
	
	public BoardVO createPost(BoardVO post) {
		return boardRepository.save(post);
	}
	public BoardVO updatePost(BoardVO post) {
		return boardRepository.save(post);
	}
	public void deletePost(BoardVO post) {
		boardRepository.delete(post);
	}
}
