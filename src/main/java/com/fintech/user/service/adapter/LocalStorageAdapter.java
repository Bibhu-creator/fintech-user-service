package com.fintech.user.service.adapter;

import com.fintech.user.service.StorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * Local disk storage adapter — used in development.
 * No AWS account needed. Files saved to local disk.
 * In production — swap with S3StorageAdapter.
 *
 * @Primary — Spring uses this adapter by default.
 * When we add S3StorageAdapter in production,
 * we remove @Primary from here and add it there.
 */
@Slf4j
@Service
@Primary
public class LocalStorageAdapter implements StorageService {

    @Value("${storage.local.base-path:C:/fintech-uploads}")
    private String basePath;

    @Override
    public String upload(String folder, String fileName,
                         InputStream data, long fileSize) {

        try {
            // Create folder if it doesn't exist
            Path folderPath = Paths.get(basePath, folder);
            Files.createDirectories(folderPath);

            // Save the file
            Path filePath = folderPath.resolve(fileName);
            Files.copy(data, filePath, StandardCopyOption.REPLACE_EXISTING);

            // Return the file key
            String fileKey = folder + "/" + fileName;
            log.info("File uploaded to local storage: {}", fileKey);
            return fileKey;

        } catch (IOException e) {
            log.error("Failed to upload file: {}/{}", folder, fileName, e);
            throw new RuntimeException("File upload failed: " + e.getMessage());
        }
    }

    @Override
    public void delete(String fileKey) {
        try {
            Path filePath = Paths.get(basePath, fileKey);
            Files.deleteIfExists(filePath);
            log.info("File deleted from local storage: {}", fileKey);
        } catch (IOException e) {
            log.error("Failed to delete file: {}", fileKey, e);
            throw new RuntimeException("File deletion failed: " + e.getMessage());
        }
    }

    @Override
    public String getFileUrl(String fileKey) {
        // In local storage — return the file path as URL
        return "file://" + basePath + "/" + fileKey;
    }
}
