package com.mycity.dormitory.util;

import com.mycity.dormitory.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT 工具类：生成 token、解析 token、校验 token 有效期
 */
@Component
@RequiredArgsConstructor
public class JwtUtil {

    private final JwtProperties jwtProperties;

    /** 生成 JWT token，包含 userId、username 和 role，过期时间从配置读取 */
    public String generateToken(Long userId, String username, Integer role) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + jwtProperties.getExpiration() * 60 * 60 * 1000);

        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("username", username);
        claims.put("role", role);

        SecretKey key = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));

        return Jwts.builder()
                .setClaims(claims)       // 存放自定义信息
                .setSubject(username)    // 设置主题（用户名）
                .setIssuedAt(now)        // 签发时间
                .setExpiration(expiration) // 过期时间
                .signWith(key, SignatureAlgorithm.HS256)  // HS256 签名
                .compact();
    }

    /** 从 token 中提取角色 */
    public Integer getRole(String token) {
        return (Integer) parseToken(token).get("role");
    }

    /** 解析 token，返回 Claims（包含所有声明信息） */
    public Claims parseToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
        return Jwts.parser()
                .setSigningKey(key)    // 设置签名密钥
                .build()
                .parseClaimsJws(token) // 解析 token
                .getBody();            // 获取载荷
    }

    /** 从 token 中提取用户ID */
    public Long getUserId(String token) {
        return Long.parseLong(parseToken(token).get("userId").toString());
    }

    /** 从 token 中提取用户名 */
    public String getUsername(String token) {
        return parseToken(token).getSubject();
    }

    /** 校验 token 是否有效（是否过期） */
    public boolean validateToken(String token) {
        try {
            return !parseToken(token).getExpiration().before(new Date());  // 未过期
        } catch (Exception e) {
            return false;  // 解析失败或已过期
        }
    }
}
