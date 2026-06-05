package com.mycity.dormitory.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mycity.dormitory.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * 用户 Mapper：继承 BaseMapper 获得基础 CRUD，额外提供根据学号查询
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
    /** 根据学号查询用户（用于登录校验） */
    @Select("SELECT * FROM user WHERE username = #{username}")
    User findByUsername(String username);
}
