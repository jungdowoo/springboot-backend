package com.fileupload;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;


@Service("fileUploadStorageService")
public class FileStorageService {

    private static final Logger logger = LoggerFactory.getLogger(FileStorageService.class);

    @Value("${file.upload-dir}")
    private String fileStorageLocation;

    public String storeFile(MultipartFile file) {
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());

        try {
            Path fileStoragePath = Paths.get(fileStorageLocation).toAbsolutePath().normalize();
            Files.createDirectories(fileStoragePath);
            logger.info("File storage path created: {}", fileStoragePath.toString());

            Path targetLocation = fileStoragePath.resolve(fileName);
            logger.info("Target location for file storage: {}", targetLocation.toString());
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            logger.info("Stored file at: {}", targetLocation.toString());
            return fileName;
        } catch (IOException ex) {
            logger.error("Could not store file {}. Error: {}", fileName, ex.getMessage());
            throw new RuntimeException("Could not store file " + fileName + ". Please try again!", ex);
        }
    }
}
