package com.example.chatroom.repository;

import com.example.chatroom.entity.LoginLogEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface LoginLogRepository {

    int insert(LoginLogEntity entity);

    List<LoginLogEntity> selectByUsernameOrderByLoginTimeDesc(@Param("username") String username, @Param("limit") int limit);
}
