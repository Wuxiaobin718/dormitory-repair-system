package com.mycity.dormitory.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 密码加密配置：注册 BCrypt 密码编码器
 */
@Configuration
public class SecurityConfig {
    /**
     * BCrypt 不可逆加密：注册时加密存库，登录时用 matches() 比对
     * 即使数据库泄露，也无法还原出原始密码
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
