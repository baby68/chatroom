package com.example.chatroom.repository;

import com.example.chatroom.entity.UserEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserRepository {

    UserEntity selectByUsername(@Param("username") String username);

    int countByUsername(@Param("username") String username);

    int insert(UserEntity entity);

    int updatePassword(UserEntity entity);
}
