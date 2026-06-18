package com.example.chatroom.controller;

import com.example.chatroom.model.ChatMessage;
import com.example.chatroom.service.ChatMessagePersistenceService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class MessageHistoryController {

    private final ChatMessagePersistenceService chatMessagePersistenceService;

    public MessageHistoryController(ChatMessagePersistenceService chatMessagePersistenceService) {
        this.chatMessagePersistenceService = chatMessagePersistenceService;
    }

    @GetMapping("/api/messages/history")
    public List<ChatMessage> history(@RequestParam(name = "limit", defaultValue = "50") int limit) {
        return chatMessagePersistenceService.loadRecentMessages(limit);
    }
}
