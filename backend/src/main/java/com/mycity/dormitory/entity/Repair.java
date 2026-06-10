package com.mycity.dormitory.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 报修单实体类，对应数据库 repair 表
 */
@Data
@TableName("repair")
public class Repair {
    @TableId(type = IdType.AUTO)
    private Long id;                // 主键ID
    private Long userId;            // 报修人ID（关联 user 表）
    private Long dormId;            // 宿舍ID（关联 dorm 表）
    private String type;            // 故障类型（如 "水龙头漏水"、"灯管损坏"）
    private String content;         // 故障详细描述
    private String img;             // 现场图片路径
    private Integer status;         // 状态：0=待处理，1=维修中，2=已完成
    private Long adminId;           // 接单管理员ID
    private LocalDateTime createTime;   // 提交时间
    private LocalDateTime finishTime;   // 完成时间

    // ====== 以下字段为联表查询填充，表中无对应列 ======

    /** 报修学生姓名（关联 user 表） */
    @TableField(exist = false)
    private String studentName;

    /** 报修学生手机号（关联 user 表） */
    @TableField(exist = false)
    private String studentPhone;

    /** 宿舍楼栋（关联 dorm 表） */
    @TableField(exist = false)
    private String building;

    /** 宿舍楼层（关联 dorm 表） */
    @TableField(exist = false)
    private Integer floor;

    /** 宿舍房间号（关联 dorm 表） */
    @TableField(exist = false)
    private String room;
}
