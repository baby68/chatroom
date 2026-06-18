package com.example.chatroom.service;

import com.example.chatroom.entity.ChatMessageEntity;
import com.example.chatroom.model.ChatMessage;
import com.example.chatroom.repository.ChatMessageRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class ChatMessagePersistenceService {

    private final ChatMessageRepository chatMessageRepository;

    public ChatMessagePersistenceService(ChatMessageRepository chatMessageRepository) {
        this.chatMessageRepository = chatMessageRepository;
    }

    public ChatMessage save(ChatMessage chatMessage) {
        ChatMessageEntity entity = new ChatMessageEntity();
        entity.setType(chatMessage.getType());
        entity.setSender(chatMessage.getSender());
        entity.setContent(chatMessage.getContent());
        entity.setFileName(chatMessage.getFileName());
        entity.setFileUrl(chatMessage.getFileUrl());
        entity.setFileType(chatMessage.getFileType());
        entity.setFileSize(chatMessage.getFileSize());

        chatMessageRepository.insert(entity);
        return toModel(entity);
    }

    public List<ChatMessage> loadRecentAttachments(int limit) {
        int safeLimit = Math.max(1, Math.min(limit, 200));
        List<ChatMessageEntity> entities = chatMessageRepository.selectAttachmentsOrderByCreatedAtDesc(safeLimit);
        List<ChatMessage> messages = new ArrayList<ChatMessage>(entities.size());
        for (ChatMessageEntity entity : entities) {
            messages.add(toModel(entity));
        }
        Collections.reverse(messages);
        return messages;
    }

    public List<ChatMessage> loadRecentMessages(int limit) {
        int safeLimit = Math.max(1, Math.min(limit, 200));
        List<ChatMessageEntity> entities = chatMessageRepository.selectAllOrderByCreatedAtDesc(safeLimit);
        List<ChatMessage> messages = new ArrayList<ChatMessage>(entities.size());
        for (ChatMessageEntity entity : entities) {
            messages.add(toModel(entity));
        }
        Collections.reverse(messages);
        return messages;
    }

    private ChatMessage toModel(ChatMessageEntity entity) {
        ChatMessage message = new ChatMessage();
        message.setId(entity.getId());
        message.setType(entity.getType());
        message.setSender(entity.getSender());
        message.setContent(entity.getContent());
        message.setFileName(entity.getFileName());
        message.setFileUrl(entity.getFileUrl());
        message.setFileType(entity.getFileType());
        message.setFileSize(entity.getFileSize());
        message.setCreatedAt(entity.getCreatedAt());
        return message;
    }
}
