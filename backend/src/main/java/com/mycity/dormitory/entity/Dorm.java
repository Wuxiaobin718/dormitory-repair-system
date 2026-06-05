package com.mycity.dormitory.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 宿舍实体类，对应数据库 dorm 表
 */
@Data
@TableName("dorm")
public class Dorm {
    @TableId(type = IdType.AUTO)
    private Long id;           // 主键ID
    private String building;   // 楼栋（如 "A栋"、"B栋"）
    private Integer floor;     // 楼层
    private String room;       // 房间号（如 "101"、"202A"）
}
