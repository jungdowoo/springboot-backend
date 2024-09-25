package com.author;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthorService {

	
	@Autowired
	private AuthorRepository authorRepository;
	// 테스트 메서드 
	public void testFindByAuthorId(String authorId) {
        Optional<AuthorVO> authorOpt = authorRepository.findByAuthorId(authorId);
        if (authorOpt.isPresent()) {
            System.out.println("Author found: " + authorOpt.get().getAuthorName());
        } else {
            System.out.println("Author not found for ID: " + authorId);
        }
    }
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	// 모든 작가 조회
	public List<AuthorVO> getAllAuthors(){
		return authorRepository.findAll();
	}
	
	// 작가등록
	public AuthorVO registerAuthor(String authorId, String rawPassword, String authorName, String authorPhoneNum, String authorBio) {
		String encodedPassword = passwordEncoder.encode(rawPassword);
		AuthorVO author = new AuthorVO();
		author.setAuthorId(authorId);
		author.setAuthorPwd(encodedPassword);
		author.setAuthorName(authorName);
		author.setAuthorPhoneNum(authorPhoneNum);
		author.setAuthorBio(authorBio);
		return authorRepository.save(author);
	}
	// 비밀번호 검증
	public boolean checkPassword(String rawPassword, String encodedPassword) {
		return passwordEncoder.matches(rawPassword,  encodedPassword);
	}
	
	// 작가 존재 여부 확인
	public boolean authorExists(String authorId) {
		return authorRepository.existsByAuthorId(authorId);
	}
	
	// 닉네임 존재 여부 확인
	public boolean authorExistsByName(String authorName) {
		return authorRepository.existsByAuthorName(authorName);
	}
	
	// 작가 ID로 작가 정보 조회
	public AuthorVO getAuthorById(String id) {
		return authorRepository.findById(id).orElse(null);
	}
	
	//작가 정보 업데이트
	public AuthorVO updateAuthor(AuthorVO author) {
		return authorRepository.save(author);
	}
	
	// 작가 삭제
	public void deleteAuthor(String id) {
		authorRepository.deleteById(id);
	}
	
	// 작가 로그인
//	public AuthorVO login(String authorId, String rawPassword) {
//	    AuthorVO author = authorRepository.findByAuthorId(authorId);
//	    if (author != null) {
//	        if (checkPassword(rawPassword, author.getAuthorPwd())) {
//	            return author;
//	        }
//	    }
//	    return null;
//	}
//}
	public AuthorVO login(String authorId, String rawPassword) {
	    // findByAuthorId 메서드가 Optional<AuthorVO>를 반환하도록 수정되었으므로 Optional 처리 필요
	    return authorRepository.findByAuthorId(authorId)
	        .filter(author -> checkPassword(rawPassword, author.getAuthorPwd()))
	        .orElse(null);
	}
}






















