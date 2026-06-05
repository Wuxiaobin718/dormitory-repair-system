package com.mycity.dormitory.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 添加评价请求 DTO
 */
@Data
public class CommentAddDTO {
    @NotNull(message = "报修单ID不能为空")
    private Long repairId;                      // 要评价的报修单ID
    @NotNull(message = "评分不能为空")
    @Min(value = 1, message = "评分最低1分")
    @Max(value = 5, message = "评分最高5分")
    private Integer score;                      // 评分：1~5 星
    private String content;                     // 评价内容（可选）
}
