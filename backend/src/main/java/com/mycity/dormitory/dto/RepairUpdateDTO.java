package com.mycity.dormitory.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 更新报修单状态请求 DTO
 * 管理员使用，变更报修单的处理进度
 */
@Data
public class RepairUpdateDTO {
    @NotNull(message = "报修单ID不能为空")
    private Long id;           // 报修单ID
    @NotNull(message = "状态不能为空")
    private Integer status;    // 目标状态：1=维修中，2=已完成
}
