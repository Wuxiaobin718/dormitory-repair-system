package com.mycity.dormitory.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mycity.dormitory.entity.Notification;

/**
 * 通知业务接口
 */
public interface NotificationService extends IService<Notification> {

    /** 创建通知并保存到数据库 */
    void create(Long userId, String type, String title, String message, Long repairId);

    /** 获取当前用户未读通知数 */
    Long getUnreadCount(Long userId);

    /** 标记单条通知为已读 */
    void markAsRead(Long id, Long userId);

    /** 标记当前用户所有通知为已读 */
    void markAllAsRead(Long userId);
}
