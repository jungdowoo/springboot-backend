package com.fileupload;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.board.demo.FileStorageService;





@RestController
@RequestMapping("/api")
public class FileController {
	
	private static final Logger logger = LoggerFactory.getLogger(FileController.class);
	
	@Autowired
	private FileStorageService fileStorageService;
	
	
	@PostMapping("/upload")
	public ResponseEntity<Map<String, String>> uploadFile(@RequestParam("file") MultipartFile file) {
		logger.info("Received file: {}", file.getOriginalFilename());
		
		String fileName;
		try {
			fileName = fileStorageService.storeFile(file);
		} catch (Exception e) {
			logger.error("Error storing file: {}", e.getMessage());
			return ResponseEntity.status(500).body(Map.of("error", "failed to store file"));
		}
		
		String fileDownloadUri = "/uploads/" + fileName;
		
		logger.info("Stored file: {}", fileName); 
        logger.info("File download URI: {}", fileDownloadUri); 
		
		return ResponseEntity.ok(Map.of("filename", fileDownloadUri));
	}

}
