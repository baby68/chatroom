package com.example.chatroom.service;

import com.example.chatroom.dto.FileUploadResponse;
import com.example.chatroom.model.ChatMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path uploadPath;

    public FileStorageService(@Value("${chatroom.upload-dir:uploads}") String uploadDir) {
        this.uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
    }

    @PostConstruct
    public void init() throws IOException {
        Files.createDirectories(uploadPath);
    }

    public FileUploadResponse store(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IOException("上传文件不能为空");
        }

        String originalName = StringUtils.cleanPath(file.getOriginalFilename());
        String extension = "";
        int dotIndex = originalName.lastIndexOf('.');
        if (dotIndex >= 0) {
            extension = originalName.substring(dotIndex);
        }

        String storedFileName = UUID.randomUUID().toString().replace("-", "") + extension;
        Path targetFile = uploadPath.resolve(storedFileName);

        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, targetFile, StandardCopyOption.REPLACE_EXISTING);
        }

        FileUploadResponse response = new FileUploadResponse();
        response.setFileName(originalName);
        response.setFileUrl("/uploads/" + storedFileName);
        response.setFileType(resolveFileType(file));
        response.setFileSize(file.getSize());
        response.setMessageType(resolveMessageType(response.getFileType()));
        return response;
    }

    public String getUploadPathUri() {
        return uploadPath.toUri().toString();
    }

    private String resolveFileType(MultipartFile file) {
        String contentType = file.getContentType();
        if (contentType != null && !contentType.trim().isEmpty()) {
            return contentType;
        }

        String fileName = file.getOriginalFilename();
        if (fileName == null) {
            return "application/octet-stream";
        }
        String lowerName = fileName.toLowerCase(Locale.ENGLISH);
        if (lowerName.endsWith(".png")) {
            return "image/png";
        }
        if (lowerName.endsWith(".jpg") || lowerName.endsWith(".jpeg")) {
            return "image/jpeg";
        }
        if (lowerName.endsWith(".gif")) {
            return "image/gif";
        }
        if (lowerName.endsWith(".webp")) {
            return "image/webp";
        }
        if (lowerName.endsWith(".mp3")) {
            return "audio/mpeg";
        }
        if (lowerName.endsWith(".wav")) {
            return "audio/wav";
        }
        if (lowerName.endsWith(".mp4")) {
            return "video/mp4";
        }
        return "application/octet-stream";
    }

    private ChatMessage.MessageType resolveMessageType(String fileType) {
        if (fileType == null) {
            return ChatMessage.MessageType.FILE;
        }
        if (fileType.startsWith("image/")) {
            return ChatMessage.MessageType.IMAGE;
        }
        if (fileType.startsWith("audio/")) {
            return ChatMessage.MessageType.AUDIO;
        }
        if (fileType.startsWith("video/")) {
            return ChatMessage.MessageType.VIDEO;
        }
        return ChatMessage.MessageType.FILE;
    }
}
