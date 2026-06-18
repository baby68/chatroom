package com.example.chatroom.config;

import com.example.chatroom.service.FileStorageService;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final FileStorageService fileStorageService;

    public WebMvcConfig(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(fileStorageService.getUploadPathUri());
    }
}
