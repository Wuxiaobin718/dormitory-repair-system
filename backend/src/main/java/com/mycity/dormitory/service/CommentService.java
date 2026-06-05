package com.mycity.dormitory.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mycity.dormitory.dto.CommentAddDTO;
import com.mycity.dormitory.entity.Comment;

/**
 * 评价业务接口：添加评价（需校验报修单状态和所有权）
 */
public interface CommentService extends IService<Comment> {
    void add(Long userId, CommentAddDTO dto);  // 学生对已完成的报修单进行评价
}
