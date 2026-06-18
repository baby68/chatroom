package com.example.chatroom.event;

import com.example.chatroom.model.ChatMessage;
import com.example.chatroom.service.ChatMessagePersistenceService;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
public class WebSocketEventListener {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatMessagePersistenceService chatMessagePersistenceService;

    public WebSocketEventListener(SimpMessagingTemplate messagingTemplate,
                                  ChatMessagePersistenceService chatMessagePersistenceService) {
        this.messagingTemplate = messagingTemplate;
        this.chatMessagePersistenceService = chatMessagePersistenceService;
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        Object username = headerAccessor.getSessionAttributes().get("username");
        if (username == null) {
            return;
        }

        ChatMessage message = new ChatMessage();
        message.setSender(username.toString());
        message.setType(ChatMessage.MessageType.LEAVE);
        message.setContent(username + " disconnected");
        message = chatMessagePersistenceService.save(message);
        messagingTemplate.convertAndSend("/topic/public", message);
    }
}
