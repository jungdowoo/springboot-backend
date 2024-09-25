package com.board.demo;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service("boardFileStorageService")
public class FileStorageService {

    private final Path fileStorageLocation;

    public FileStorageService() {
        this.fileStorageLocation = Paths.get("C:/temp/uploads")
                .toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }

    public String storeFile(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        try {
            if (fileName.contains("..")) {
                throw new RuntimeException("Sorry! Filename contains invalid path sequence " + fileName);
            }
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, targetLocation);
            }
            return fileName;
        } catch (IOException ex) {
            throw new RuntimeException("Could not store file " + fileName + ". Please try again!", ex);
        }
    }
}
