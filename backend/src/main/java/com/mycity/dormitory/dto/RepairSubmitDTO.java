package com.mycity.dormitory.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 提交报修请求 DTO
 */
@Data
public class RepairSubmitDTO {
    @NotNull(message = "宿舍不能为空")
    private Long dormId;      // 宿舍ID
    @NotBlank(message = "故障类型不能为空")
    private String type;      // 故障类型
    @NotBlank(message = "故障描述不能为空")
    private String content;   // 故障描述
    private String img;       // 图片路径（上传后返回的路径）
}
