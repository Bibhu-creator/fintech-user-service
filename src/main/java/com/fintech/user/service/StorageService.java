package com.fintech.user.service;

import java.io.InputStream;

public interface StorageService {

    /**
     * Upload a file and return the file key
     * fileKey is used to retrieve or delete the file later
     */
    String upload(String folder, String fileName,
                  InputStream data, long fileSize);

    /**
     * Delete a file by its key
     */
    void delete(String fileKey);

    /**
     * Get the URL to access the file
     */
    String getFileUrl(String fileKey);
}
