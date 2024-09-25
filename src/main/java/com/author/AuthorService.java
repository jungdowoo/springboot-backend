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
	
	public List<AuthorVO> getAllAuthors(){
		return authorRepository.findAll();
	}
	
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
	public boolean checkPassword(String rawPassword, String encodedPassword) {
		return passwordEncoder.matches(rawPassword,  encodedPassword);
	}
	
	public boolean authorExists(String authorId) {
		return authorRepository.existsByAuthorId(authorId);
	}
	
	public boolean authorExistsByName(String authorName) {
		return authorRepository.existsByAuthorName(authorName);
	}
	
	public AuthorVO getAuthorById(String id) {
		return authorRepository.findById(id).orElse(null);
	}
	
	public AuthorVO updateAuthor(AuthorVO author) {
		return authorRepository.save(author);
	}
	
	public void deleteAuthor(String id) {
		authorRepository.deleteById(id);
	}
	
	
	public AuthorVO login(String authorId, String rawPassword) {
	    return authorRepository.findByAuthorId(authorId)
	        .filter(author -> checkPassword(rawPassword, author.getAuthorPwd()))
	        .orElse(null);
	}
}






















