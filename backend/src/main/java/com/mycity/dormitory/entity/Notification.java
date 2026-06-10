package com.mycity.dormitory.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 通知实体类，对应数据库 notification 表
 */
@Data
@TableName("notification")
public class Notification {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;            // 接收通知的用户ID
    private String type;            // 通知类型：STATUS_UPDATE / NEW_REPAIR / NEW_COMMENT
    private String title;           // 通知标题
    private String message;         // 通知内容
    private Long repairId;          // 关联报修单ID（可选）
    private Integer isRead;         // 是否已读：0=未读，1=已读
    private LocalDateTime createTime;
}
