package com.mycity.dormitory.service.impl;

import com.mycity.dormitory.entity.Repair;
import com.mycity.dormitory.handler.RepairWebSocketHandler;
import com.mycity.dormitory.service.NotificationService;
import com.mycity.dormitory.service.WebSocketPushService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * WebSocket 消息推送实现：构造 JSON 消息，调用 Handler 发送，同步存库
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WebSocketPushServiceImpl implements WebSocketPushService {

    private final RepairWebSocketHandler wsHandler;
    private final NotificationService notificationService;

    @Override
    public void pushStatusUpdate(Repair repair) {
        String statusText = switch (repair.getStatus()) {
            case 0 -> "待处理";
            case 1 -> "维修中";
            case 2 -> "已完成";
            default -> "未知";
        };
        String msg = String.format("报修单 #%d 状态已更新为【%s】", repair.getId(), statusText);
        String json = String.format(
            "{\"type\":\"STATUS_UPDATE\",\"repairId\":%d,\"status\":%d,\"message\":\"%s\"}",
            repair.getId(), repair.getStatus(), msg
        );
        wsHandler.pushToStudent(repair.getUserId(), json);
        notificationService.create(repair.getUserId(), "STATUS_UPDATE",
                "报修进度更新", msg, repair.getId());
        log.info("WS推送状态更新 -> 学生 userId={}, repairId={}, status={}",
                repair.getUserId(), repair.getId(), repair.getStatus());
    }

    @Override
    public void pushNewRepair(Repair repair) {
        String msg = String.format("有新的报修单 #%d（%s）", repair.getId(), repair.getType());
        String json = String.format(
            "{\"type\":\"NEW_REPAIR\",\"repairId\":%d,\"message\":\"%s\"}",
            repair.getId(), msg
        );
        wsHandler.pushToAllAdmins(json);
        // 通知存库：给所有在线管理员各存一条
        for (Long adminId : wsHandler.getOnlineAdminIds()) {
            notificationService.create(adminId, "NEW_REPAIR", "新报修通知", msg, repair.getId());
        }
        log.info("WS推送新报修 -> 管理员, repairId={}", repair.getId());
    }

    @Override
    public void pushNewComment(Long repairId, Long adminId) {
        if (adminId == null) return;
        String msg = String.format("报修单 #%d 收到新的评价", repairId);
        String json = String.format(
            "{\"type\":\"NEW_COMMENT\",\"repairId\":%d,\"message\":\"%s\"}",
            repairId, msg
        );
        wsHandler.pushToAdmin(adminId, json);
        notificationService.create(adminId, "NEW_COMMENT", "新评价通知", msg, repairId);
        log.info("WS推送新评价 -> 管理员 userId={}, repairId={}", adminId, repairId);
    }
}
