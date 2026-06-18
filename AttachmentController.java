package com.example.chatroom.controller;

import com.example.chatroom.model.ChatMessage;
import com.example.chatroom.service.ChatMessagePersistenceService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
public class AttachmentController {

    private final ChatMessagePersistenceService chatMessagePersistenceService;

    public AttachmentController(ChatMessagePersistenceService chatMessagePersistenceService) {
        this.chatMessagePersistenceService = chatMessagePersistenceService;
    }

    @GetMapping("/attachments")
    public List<ChatMessage> attachments(@RequestParam(name = "limit", defaultValue = "50") int limit) {
        return chatMessagePersistenceService.loadRecentAttachments(limit);
    }
}
