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
}
