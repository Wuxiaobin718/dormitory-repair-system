package com.mycity.dormitory.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * WebSocket 处理器：管理用户连接，支持按 userId 推送消息
 *
 * 连接映射关系：
 *   - studentSessions: 学生连接 (role=0)
 *   - adminSessions:   管理员连接 (role=1)
 * 通过 role 分流，方便广播给所有管理员
 */
@Slf4j
@Component
public class RepairWebSocketHandler extends TextWebSocketHandler {

    /** 学生连接池：userId → WebSocketSession */
    private final Map<Long, WebSocketSession> studentSessions = new ConcurrentHashMap<>();

    /** 管理员连接池：userId → WebSocketSession */
    private final Map<Long, WebSocketSession> adminSessions = new ConcurrentHashMap<>();

    // ==================== 连接生命周期 ====================

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        Long userId = getAttribute(session, "userId");
        Integer role = getAttribute(session, "role");

        if (role == 1) {
            adminSessions.put(userId, session);
            log.info("管理员 WS 连接: userId={}, sessionId={}", userId, session.getId());
        } else {
            studentSessions.put(userId, session);
            log.info("学生 WS 连接: userId={}, sessionId={}", userId, session.getId());
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        Long userId = getAttribute(session, "userId");
        Integer role = getAttribute(session, "role");

        if (role == 1) {
            adminSessions.remove(userId);
            log.info("管理员 WS 断开: userId={}, sessionId={}", userId, session.getId());
        } else {
            studentSessions.remove(userId);
            log.info("学生 WS 断开: userId={}, sessionId={}", userId, session.getId());
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        Long userId = getAttribute(session, "userId");
        log.error("WS 传输异常: userId={}, error={}", userId, exception.getMessage());
        // 异常后自动关闭，由 afterConnectionClosed 清理连接池
    }

    // ==================== 推送方法 ====================

    /**
     * 向指定学生推送消息
     */
    public void pushToStudent(Long userId, String message) {
        WebSocketSession session = studentSessions.get(userId);
        sendMessage(session, message);
    }

    /**
     * 向所有在线管理员广播消息
     */
    public void pushToAllAdmins(String message) {
        adminSessions.values().forEach(session -> sendMessage(session, message));
    }

    /**
     * 向指定管理员推送消息
     */
    public void pushToAdmin(Long adminId, String message) {
        WebSocketSession session = adminSessions.get(adminId);
        sendMessage(session, message);
    }

    // ==================== 内部工具 ====================

    private void sendMessage(WebSocketSession session, String message) {
        if (session != null && session.isOpen()) {
            try {
                session.sendMessage(new TextMessage(message));
            } catch (IOException e) {
                log.error("WS 发送消息失败: {}", e.getMessage());
            }
        }
    }

    @SuppressWarnings("unchecked")
    private <T> T getAttribute(WebSocketSession session, String key) {
        Map<String, Object> attrs = session.getAttributes();
        return (T) attrs.get(key);
    }

    /** 获取当前在线学生数 */
    public int getOnlineStudentCount() {
        return studentSessions.size();
    }

    /** 获取当前在线管理员数 */
    public int getOnlineAdminCount() {
        return adminSessions.size();
    }

    /** 获取当前所有在线管理员的 userId */
    public java.util.Set<Long> getOnlineAdminIds() {
        return adminSessions.keySet();
    }
}
