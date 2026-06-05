package com.mycity.dormitory.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 用户实体类，对应数据库 user 表
 */
@Data
@TableName("user")
public class User {
    @TableId(type = IdType.AUTO)
    private Long id;               // 主键ID
    private String username;       // 学号（登录账号）
    private String password;       // 密码（BCrypt 加密后）
    private String name;           // 姓名
    private String phone;          // 手机号
    private Long dormId;           // 宿舍ID（关联 dorm 表）
    private Integer role;          // 角色：0=学生，1=管理员
    private String avatar;         // 头像地址
    private LocalDateTime createTime;  // 注册时间
}
