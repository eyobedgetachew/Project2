package com.project.project.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Objects; // For Objects.requireNonNull

@Service
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public CloudinaryService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    /**
     * Uploads a MultipartFile to Cloudinary.
     * @param file The MultipartFile to upload.
     * @return The URL of the uploaded file on Cloudinary.
     * @throws IOException If there's an error converting or uploading the file.
     */
    public String uploadFile(MultipartFile file) throws IOException {
        // Convert MultipartFile to File (Cloudinary's upload method works better with File or InputStream)
        File uploadedFile = convertMultiPartToFile(file);
        try {
            // Upload to Cloudinary.
            // "resource_type": "auto" lets Cloudinary detect if it's an image or video
            Map uploadResult = cloudinary.uploader().upload(uploadedFile, ObjectUtils.asMap("resource_type", "auto"));
            return uploadResult.get("url").toString();
        } finally {
            // Ensure temporary file is deleted even if upload fails
            if (uploadedFile.exists() && !uploadedFile.delete()) {
                System.err.println("Failed to delete temporary file: " + uploadedFile.getAbsolutePath());
            }
        }
    }

    /**
     * Helper method to convert MultipartFile to File.
     * This creates a temporary file on the server.
     */
    private File convertMultiPartToFile(MultipartFile file) throws IOException {
        File convFile = new File(Objects.requireNonNull(file.getOriginalFilename()));
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(file.getBytes());
        fos.close();
        return convFile;
    }
}