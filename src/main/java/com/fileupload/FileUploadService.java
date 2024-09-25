package com.fileupload;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Service
public class FileUploadService {

	 private static final Logger logger = LoggerFactory.getLogger(FileUploadService.class);

	    // 이미지 파일을 지정된 경로에 저장하는 메서드
	    public String handleFileUpload(MultipartFile file) throws IOException {
	        String uploadDir = "C:/temp/uploads/";
	        File uploadDirFile = new File(uploadDir);
	        if (!uploadDirFile.exists()) {
	            uploadDirFile.mkdirs(); // 디렉토리가 없으면 생성
	        }

	        // 고유한 파일 이름 생성
	        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
	        File destFile = new File(uploadDir + fileName);
	        
	        try {
	            // 파일을 지정된 경로에 저장
	            file.transferTo(destFile);
	            return "/uploads/" + fileName; // 파일 저장 경로 반환
	        } catch (IOException e) {
	            throw new RuntimeException("Failed to upload file", e);
	        }
	    }
}

