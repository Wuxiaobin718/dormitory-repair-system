package com.mycity.dormitory.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * JWT 配置：密钥、过期时间、请求头等（可在 application.yml 中覆盖）
 */
@Data
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {
    private String secret = "campus_dormitory_secret_key_2026";
    private Long expiration = 24L;
    private String tokenPrefix = "Bearer ";
    private String header = "Authorization";
}
