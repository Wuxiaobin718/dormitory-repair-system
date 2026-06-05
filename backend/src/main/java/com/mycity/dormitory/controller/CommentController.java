package com.mycity.dormitory.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mycity.dormitory.common.Result;
import com.mycity.dormitory.dto.CommentAddDTO;
import com.mycity.dormitory.entity.Comment;
import com.mycity.dormitory.service.CommentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 评价 Controller：学生对已完成的报修单进行评价，查看评价列表
 */
@RestController
@RequestMapping("/api/comment")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;
    private final HttpServletRequest request;

    /** POST /api/comment/add — 添加评价（需校验：报修已完结、本人报修） */
    @PostMapping("/add")
    public Result<Void> add(@Valid @RequestBody CommentAddDTO dto) {
        Long userId = (Long) request.getAttribute("userId");
        commentService.add(userId, dto);
        return Result.success();
    }

    /** GET /api/comment/list — 查看某条报修单的所有评价 */
    @GetMapping("/list")
    public Result<List<Comment>> list(@RequestParam Long repairId) {
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<Comment>()
                .eq(Comment::getRepairId, repairId)
                .orderByDesc(Comment::getCreateTime);
        return Result.success(commentService.list(wrapper));
    }
}
