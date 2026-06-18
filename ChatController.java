package com.example.chatroom.controller;

import com.example.chatroom.model.ChatMessage;
import com.example.chatroom.service.ChatMessagePersistenceService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatMessagePersistenceService chatMessagePersistenceService;

    public ChatController(SimpMessagingTemplate messagingTemplate,
                          ChatMessagePersistenceService chatMessagePersistenceService) {
        this.messagingTemplate = messagingTemplate;
        this.chatMessagePersistenceService = chatMessagePersistenceService;
    }

    @MessageMapping("/chat.addUser")
    public void addUser(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {
        headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
        chatMessage.setType(ChatMessage.MessageType.JOIN);
        chatMessage.setContent(chatMessage.getSender() + " joined the room");
        chatMessage = chatMessagePersistenceService.save(chatMessage);
        messagingTemplate.convertAndSend("/topic/public", chatMessage);
    }

    @MessageMapping("/chat.send")
    public void sendMessage(@Payload ChatMessage chatMessage) {
        if (chatMessage.getType() == null) {
            chatMessage.setType(ChatMessage.MessageType.CHAT);
        }
        chatMessage = chatMessagePersistenceService.save(chatMessage);
        messagingTemplate.convertAndSend("/topic/public", chatMessage);
    }

    @MessageMapping("/chat.leave")
    public void leave(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {
        Object username = headerAccessor.getSessionAttributes().remove("username");
        String sender = username == null ? chatMessage.getSender() : username.toString();
        chatMessage.setSender(sender);
        chatMessage.setType(ChatMessage.MessageType.LEAVE);
        chatMessage.setContent(sender + " left the room");
        chatMessage = chatMessagePersistenceService.save(chatMessage);
        messagingTemplate.convertAndSend("/topic/public", chatMessage);
    }
}
