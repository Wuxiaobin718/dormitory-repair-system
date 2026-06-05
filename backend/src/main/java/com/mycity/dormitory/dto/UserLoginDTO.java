package com.mycity.dormitory.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 用户登录请求 DTO
 */
@Data
public class UserLoginDTO {
    @NotBlank(message = "学号不能为空")
    private String username;   // 学号
    @NotBlank(message = "密码不能为空")
    private String password;   // 密码
}
