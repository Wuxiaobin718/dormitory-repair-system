package com.mycity.dormitory.config;

import com.mycity.dormitory.handler.RepairWebSocketHandler;
import com.mycity.dormitory.interceptor.WebSocketInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * WebSocket 配置：注册 /ws/repair 端点，添加 JWT 认证拦截器
 */
@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {

    private final RepairWebSocketHandler repairWebSocketHandler;
    private final WebSocketInterceptor webSocketInterceptor;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(repairWebSocketHandler, "/ws/repair")
                .addInterceptors(webSocketInterceptor)
                .setAllowedOrigins("*");
    }
}
