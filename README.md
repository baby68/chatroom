# JDK8 Java WebSocket Chatroom

一个基于 `JDK8 + Spring Boot + MyBatis + WebSocket(STOMP/SockJS)` 的前后端聊天室示例。

## 运行要求

- JDK 8
- Maven 3.6+
- MySQL 8

## 数据库准备

项目启动前需要先建表（MyBatis 不会自动建表），执行以下 SQL：

```sql
CREATE DATABASE IF NOT EXISTS chatroom DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE chatroom;

CREATE TABLE chat_message (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    type VARCHAR(20) NOT NULL,
    sender VARCHAR(64) NOT NULL,
    content TEXT,
    file_name VARCHAR(255),
    file_url VARCHAR(512),
    file_type VARCHAR(128),
    file_size BIGINT,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE chat_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(64) NOT NULL UNIQUE,
    password VARCHAR(128) NOT NULL,
    nickname VARCHAR(64) NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE login_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(64) NOT NULL,
    ip VARCHAR(64),
    success TINYINT(1) NOT NULL,
    fail_reason VARCHAR(255),
    login_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

然后修改配置文件：

`src/main/resources/application.properties`

- `spring.datasource.url`
- `spring.datasource.username`
- `spring.datasource.password`

> 注意：使用 MyBatis，建表 SQL 见上方「数据库准备」，项目不会自动建表。

## 启动方式

```bash
mvn spring-boot:run
```

如果本机 Maven 仓库目录没有写权限，可以改用项目内仓库：

```bash
mvn "-Dmaven.repo.local=.m2repo" spring-boot:run
```

启动后打开：

```text
http://localhost:8080
```

## 功能说明

- 数据库用户名/密码登录，自动记录登录日志
- 支持注册新账号
- 支持多人实时聊天
- 支持发送图片、音频、视频和通用附件
- 支持 `Ctrl+V` 粘贴图片后直接发送
- 文件通过 HTTP 上传，消息通过 WebSocket 广播
- 聊天消息持久化到 MySQL 8，服务重启后可恢复最近历史记录
- 页面右侧附件列表面板，显示所有已发送文件
- 支持加入、离开、断线系统通知
- 前端页面由 Spring Boot 静态资源直接提供

## 主要路径

- 后端入口：`src/main/java/com/example/chatroom/ChatroomApplication.java`
- WebSocket 配置：`src/main/java/com/example/chatroom/config/WebSocketConfig.java`
- 聊天控制器：`src/main/java/com/example/chatroom/controller/ChatController.java`
- 认证控制器（登录/注册）：`src/main/java/com/example/chatroom/controller/AuthController.java`
- 历史消息接口：`src/main/java/com/example/chatroom/controller/MessageHistoryController.java`
- 附件列表接口：`src/main/java/com/example/chatroom/controller/AttachmentController.java`
- 文件上传接口：`src/main/java/com/example/chatroom/controller/FileController.java`
- 消息持久化服务：`src/main/java/com/example/chatroom/service/ChatMessagePersistenceService.java`
- 文件存储服务：`src/main/java/com/example/chatroom/service/FileStorageService.java`
- 认证服务：`src/main/java/com/example/chatroom/service/AuthService.java`
- 用户实体：`src/main/java/com/example/chatroom/entity/UserEntity.java`
- 登录日志实体：`src/main/java/com/example/chatroom/entity/LoginLogEntity.java`
- 前端页面：`src/main/resources/static/index.html`

## 数据库表

需要在数据库中手动创建以下表（建表 SQL 见上方「数据库准备」）：

| 表名 | 说明 |
|------|------|
| `chat_message` | 聊天消息（文本 + 附件） |
| `chat_user` | 用户账号（用户名 + BCrypt 加密密码） |
| `login_log` | 登录日志（用户名、IP、成功/失败、时间） |
