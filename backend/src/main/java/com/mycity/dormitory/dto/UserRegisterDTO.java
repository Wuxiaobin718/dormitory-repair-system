package com.mycity.dormitory.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 用户注册请求 DTO
 */
@Data
public class UserRegisterDTO {
    @NotBlank(message = "学号不能为空")
    private String username;           // 学号
    @NotBlank(message = "密码不能为空")
    private String password;           // 密码
    @NotBlank(message = "确认密码不能为空")
    private String confirmPassword;    // 确认密码（需与 password 一致）
    @NotBlank(message = "姓名不能为空")
    private String name;               // 姓名
    private String phone;              // 手机号（可选）
    private Long dormId;               // 宿舍ID（可选）
    private Integer role;              // 角色（默认 0=学生）
}
