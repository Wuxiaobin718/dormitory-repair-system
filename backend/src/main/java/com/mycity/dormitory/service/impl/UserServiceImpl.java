package com.mycity.dormitory.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mycity.dormitory.dto.UserRegisterDTO;
import com.mycity.dormitory.entity.User;
import com.mycity.dormitory.mapper.UserMapper;
import com.mycity.dormitory.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 用户业务实现：注册校验 + BCrypt 加密存库，登录时密码比对
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl
        extends ServiceImpl<UserMapper, User>
        implements UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    /** 注册：校验两次密码一致、学号唯一，密码加密后入库 */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void register(UserRegisterDTO dto) {
        // 校验两次密码一致性
        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            throw new IllegalArgumentException("两次输入的密码不一致");
        }
        // 校验学号是否已被注册
        User existingUser = userMapper.findByUsername(dto.getUsername());
        if (existingUser != null) {
            throw new IllegalArgumentException("学号已存在");
        }
        // 构建用户对象，密码用 BCrypt 加密
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setName(dto.getName());
        user.setPhone(dto.getPhone());
        user.setDormId(dto.getDormId());
        user.setRole(dto.getRole() != null ? dto.getRole() : 0);  // 默认学生
        userMapper.insert(user);
    }

    /** 登录：根据学号查用户，再用 BCrypt 比对密码 */
    @Override
    public User login(String username, String password) {
        User user = userMapper.findByUsername(username);
        if (user == null) {
            throw new IllegalArgumentException("学号或密码错误");  // 模糊提示防枚举
        }
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("学号或密码错误");
        }
        return user;
    }

    @Override
    public User findByUsername(String username) {
        return userMapper.findByUsername(username);
    }
}
