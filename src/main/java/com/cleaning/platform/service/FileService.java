// service/FileService.java

package com.cleaning.platform.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.UUID;

@Service
public class FileService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    private static final Set<String> ALLOWED_IMAGE_TYPES = Set.of(
            "image/jpeg", "image/jpg", "image/png",
            "image/gif", "image/webp", "image/bmp"
    );

    public String uploadFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("파일이 비어있습니다.");
        }

        validateImageFile(file);

        File uploadPathDir = new File(uploadDir);
        if (!uploadPathDir.exists()) {
            uploadPathDir.mkdirs();
        }

        String originalFilename = file.getOriginalFilename();
        String storedFilename = createStoredFilename(originalFilename);
        Path savePath = Paths.get(uploadDir, storedFilename);

        try {
            file.transferTo(savePath);
            System.out.println("✅ 파일 저장 성공: " + savePath);
        } catch (IOException e) {
            System.err.println("❌ 파일 저장 실패: " + savePath);
            e.printStackTrace();
            throw new RuntimeException("파일 저장에 실패했습니다.", e);
        }

        return storedFilename;
    }

    private void validateImageFile(MultipartFile file) {
        String contentType = file.getContentType();

        if (contentType == null || !ALLOWED_IMAGE_TYPES.contains(contentType.toLowerCase())) {
            throw new IllegalArgumentException(
                    "허용되지 않는 파일 형식입니다. (jpg, png, gif, webp만 허용)"
            );
        }

        long maxSize = 5 * 1024 * 1024; // 5MB
        if (file.getSize() > maxSize) {
            throw new IllegalArgumentException("파일 크기는 5MB를 초과할 수 없습니다.");
        }
    }

    private String createStoredFilename(String originalFilename) {
        if (originalFilename == null || originalFilename.isEmpty()) {
            return UUID.randomUUID().toString();
        }
        String ext = "";
        int pos = originalFilename.lastIndexOf(".");
        if (pos != -1) {
            ext = originalFilename.substring(pos);
        }
        return UUID.randomUUID().toString() + ext;
    }
}