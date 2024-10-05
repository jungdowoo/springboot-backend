package com.fileupload;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Service
public class FileUploadService {

    private static final Logger logger = LoggerFactory.getLogger(FileUploadService.class);

    @Value(value = "#{systemProperties['upload.path']}")
    private String uploadPath;


    @Value(value = "#{systemProperties['upload.url']}")
    private String uploadUrl;

    public String handleFileUpload(MultipartFile file) throws IOException {
        String uploadDir = uploadPath;
        File uploadDirFile = new File(uploadDir);
        if (!uploadDirFile.exists()) {
            uploadDirFile.mkdirs();
        }

        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        File destFile = new File(uploadDir + fileName);

        try {
            file.transferTo(destFile);
            return "/uploads/" + fileName;
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload file", e);
        }
    }
}

