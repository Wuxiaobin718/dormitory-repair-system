package com.mycity.dormitory.interceptor;

import com.mycity.dormitory.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.net.URI;
import java.util.Map;

/**
 * WebSocket 握手拦截器：从 URL 查询参数中提取 JWT token 并校验身份
 * 客户端连接格式：ws://localhost:8080/ws/repair?token=xxx
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketInterceptor implements HandshakeInterceptor {

    private final JwtUtil jwtUtil;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) {
        URI uri = request.getURI();
        String query = uri.getRawQuery();
        if (query == null || !query.contains("token=")) {
            log.warn("WebSocket 握手失败：缺少 token 参数");
            return false;
        }

        String token = null;
        for (String param : query.split("&")) {
            String[] pair = param.split("=", 2);
            if (pair.length == 2 && "token".equals(pair[0])) {
                token = pair[1];
                break;
            }
        }

        if (token == null || !jwtUtil.validateToken(token)) {
            log.warn("WebSocket 握手失败：token 无效或已过期");
            return false;
        }

        // 将用户信息存入 WebSocket 会话属性，供 Handler 使用
        attributes.put("userId", jwtUtil.getUserId(token));
        attributes.put("role", jwtUtil.getRole(token));

        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {
        // 握手完成后无需额外操作
    }
}
