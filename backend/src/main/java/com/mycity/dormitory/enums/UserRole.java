package com.mycity.dormitory.enums;

import lombok.Getter;

/**
 * 用户角色枚举：0=学生，1=管理员
 */
@Getter
public enum UserRole {
    STUDENT(0, "学生"),
    ADMIN(1, "管理员");

    private final int code;
    private final String desc;

    UserRole(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
