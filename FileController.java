package com.example.chatroom.controller;

import com.example.chatroom.dto.FileUploadResponse;
import com.example.chatroom.service.FileStorageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/api/files")
public class FileController {

    private final FileStorageService fileStorageService;

    public FileController(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public FileUploadResponse upload(@RequestParam("file") MultipartFile file) throws IOException {
        return fileStorageService.store(file);
    }

    @ExceptionHandler(IOException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleIoException(IOException exception) {
        return Collections.singletonMap("message", exception.getMessage());
    }
}
