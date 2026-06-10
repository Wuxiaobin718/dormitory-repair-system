package com.mycity.dormitory.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mycity.dormitory.common.Result;
import com.mycity.dormitory.entity.Notification;
import com.mycity.dormitory.service.NotificationService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 通知 Controller：通知列表、未读数、标记已读
 */
@RestController
@RequestMapping("/api/notification")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final HttpServletRequest request;

    /** GET /api/notification/list — 获取当前用户的通知列表（分页，支持筛选） */
    @GetMapping("/list")
    public Result<Page<Notification>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size,
            @RequestParam(required = false) Integer isRead) {
        Long userId = (Long) request.getAttribute("userId");
        Page<Notification> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Notification> wrapper = new LambdaQueryWrapper<Notification>()
                .eq(Notification::getUserId, userId)
                .eq(isRead != null, Notification::getIsRead, isRead)
                .orderByDesc(Notification::getCreateTime);
        return Result.success(notificationService.page(pageParam, wrapper));
    }

    /** GET /api/notification/unread-count — 获取未读通知数 */
    @GetMapping("/unread-count")
    public Result<Map<String, Long>> unreadCount() {
        Long userId = (Long) request.getAttribute("userId");
        Long count = notificationService.getUnreadCount(userId);
        return Result.success(Map.of("count", count));
    }

    /** POST /api/notification/read/{id} — 标记单条通知为已读 */
    @PostMapping("/read/{id}")
    public Result<Void> markRead(@PathVariable Long id) {
        Long userId = (Long) request.getAttribute("userId");
        notificationService.markAsRead(id, userId);
        return Result.success();
    }

    /** POST /api/notification/read-all — 标记所有通知为已读 */
    @PostMapping("/read-all")
    public Result<Void> markAllRead() {
        Long userId = (Long) request.getAttribute("userId");
        notificationService.markAllAsRead(userId);
        return Result.success();
    }
}
