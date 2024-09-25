	package com.artwork;
	
	import org.springframework.data.jpa.repository.JpaRepository;
	import java.util.List;
	
	public interface ArtworkRepository extends JpaRepository<ArtworkVO, String> {
	    List<ArtworkVO> findByCategory(String category);
	}
