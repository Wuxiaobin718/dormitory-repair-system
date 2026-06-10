package com.mycity.dormitory.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mycity.dormitory.dto.UserRegisterDTO;
import com.mycity.dormitory.entity.User;

/**
 * 用户业务接口：注册、登录、查询用户信息
 */
public interface UserService extends IService<User> {
    void register(UserRegisterDTO dto);   // 注册新用户
    User login(String username, String password);  // 登录验证
    User findByUsername(String username);  // 根据学号查询用户

    /**
     * 修改当前用户密码（需要旧密码验证）
     * @param userId 当前用户ID
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     */
    void changePassword(Long userId, String oldPassword, String newPassword);

    /**
     * 管理员重置指定用户密码（无需旧密码）
     * @param adminId 管理员ID
     * @param targetUserId 目标用户ID
     * @param newPassword 新密码
     */
    void resetPassword(Long adminId, Long targetUserId, String newPassword);
}
