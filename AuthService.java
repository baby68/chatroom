package com.example.chatroom.service;

import com.example.chatroom.entity.LoginLogEntity;
import com.example.chatroom.entity.UserEntity;
import com.example.chatroom.repository.LoginLogRepository;
import com.example.chatroom.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final LoginLogRepository loginLogRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, LoginLogRepository loginLogRepository) {
        this.userRepository = userRepository;
        this.loginLogRepository = loginLogRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public Map<String, Object> login(String username, String password, HttpServletRequest request) {
        Map<String, Object> result = new HashMap<String, Object>();
        String ip = getClientIp(request);

        UserEntity user = userRepository.selectByUsername(username);
        if (user == null) {
            recordLoginLog(username, ip, false, "用户不存在");
            result.put("success", false);
            result.put("message", "用户名或密码错误");
            return result;
        }

        String storedPassword = user.getPassword();
        boolean passwordMatch;

        if (storedPassword != null && storedPassword.startsWith("$2a$")) {
            // BCrypt 密码，正常校验
            passwordMatch = passwordEncoder.matches(password, storedPassword);
        } else {
            // 明文密码兼容：直接比较，匹配后自动升级为 BCrypt
            passwordMatch = password != null && password.equals(storedPassword);
            if (passwordMatch) {
                user.setPassword(passwordEncoder.encode(password));
                userRepository.updatePassword(user);
            }
        }

        if (!passwordMatch) {
            recordLoginLog(username, ip, false, "密码错误");
            result.put("success", false);
            result.put("message", "用户名或密码错误");
            return result;
        }

        recordLoginLog(username, ip, true, null);
        result.put("success", true);
        result.put("username", user.getUsername());
        result.put("nickname", user.getNickname());
        return result;
    }

    public Map<String, Object> register(String username, String password, String nickname) {
        Map<String, Object> result = new HashMap<String, Object>();

        if (userRepository.countByUsername(username) > 0) {
            result.put("success", false);
            result.put("message", "用户名已存在");
            return result;
        }

        UserEntity user = new UserEntity();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setNickname(nickname != null && !nickname.trim().isEmpty() ? nickname.trim() : username);
        userRepository.insert(user);

        result.put("success", true);
        result.put("username", user.getUsername());
        result.put("nickname", user.getNickname());
        return result;
    }

    private void recordLoginLog(String username, String ip, boolean success, String failReason) {
        LoginLogEntity log = new LoginLogEntity();
        log.setUsername(username);
        log.setIp(ip);
        log.setSuccess(success);
        log.setFailReason(failReason);
        loginLogRepository.insert(log);
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.trim().isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.trim().isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
