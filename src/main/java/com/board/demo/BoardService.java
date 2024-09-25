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
	// 게시글 수정기능
	public BoardVO updatePost(BoardVO post) {
		return boardRepository.save(post);
	}
	// 게시글 삭제 기능
	public void deletePost(BoardVO post) {
		boardRepository.delete(post);
	}
}
