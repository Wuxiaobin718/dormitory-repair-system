package com.mycity.dormitory.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mycity.dormitory.dto.CommentAddDTO;
import com.mycity.dormitory.entity.Comment;
import com.mycity.dormitory.entity.Repair;
import com.mycity.dormitory.mapper.CommentMapper;
import com.mycity.dormitory.service.CommentService;
import com.mycity.dormitory.service.RepairService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 评价业务实现：只有报修单的主人且报修已完成后才能评价
 */
@Service
@RequiredArgsConstructor
public class CommentServiceImpl
        extends ServiceImpl<CommentMapper, Comment>
        implements CommentService {

    private final CommentMapper commentMapper;
    private final RepairService repairService;

    /** 添加评价，需要校验：报修单存在、归属当前用户、状态为已完成 */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void add(Long userId, CommentAddDTO dto) {
        Repair repair = repairService.getById(dto.getRepairId());
        if (repair == null) {
            throw new IllegalArgumentException("报修单不存在");
        }
        if (!repair.getUserId().equals(userId)) {
            throw new IllegalArgumentException("只能评价自己的报修单");
        }
        if (repair.getStatus() != 2) {
            throw new IllegalStateException("只能对已完成的报修单进行评价");
        }
        Comment comment = new Comment();
        comment.setRepairId(dto.getRepairId());
        comment.setScore(dto.getScore());
        comment.setContent(dto.getContent());
        comment.setCreateTime(LocalDateTime.now());
        commentMapper.insert(comment);
    }
}
