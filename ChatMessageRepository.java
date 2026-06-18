package com.example.chatroom.repository;

import com.example.chatroom.entity.ChatMessageEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ChatMessageRepository {

    int insert(ChatMessageEntity entity);

    List<ChatMessageEntity> selectAllOrderByCreatedAtDesc(@Param("limit") int limit);

    List<ChatMessageEntity> selectAttachmentsOrderByCreatedAtDesc(@Param("limit") int limit);
}
