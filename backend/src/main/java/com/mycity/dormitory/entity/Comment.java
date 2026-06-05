package com.mycity.dormitory.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 评价实体类，对应数据库 comment 表
 * 学生对已完成的报修单进行评分和评价
 */
@Data
@TableName("comment")
public class Comment {
    @TableId(type = IdType.AUTO)
    private Long id;                 // 主键ID
    private Long repairId;           // 关联的报修单ID
    private Integer score;           // 评分：1~5 星
    private String content;          // 评价文字内容
    private LocalDateTime createTime;    // 评价时间
}
