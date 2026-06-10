package com.mycity.dormitory.service;

import com.mycity.dormitory.entity.Repair;

/**
 * WebSocket 消息推送服务接口
 */
public interface WebSocketPushService {

    /**
     * 报修状态变更时，向报修学生推送状态更新通知
     */
    void pushStatusUpdate(Repair repair);

    /**
     * 新报修提交时，向所有在线管理员广播通知
     */
    void pushNewRepair(Repair repair);

    /**
     * 新评价提交时，向处理该报修的管理员推送通知
     */
    void pushNewComment(Long repairId, Long adminId);
}
