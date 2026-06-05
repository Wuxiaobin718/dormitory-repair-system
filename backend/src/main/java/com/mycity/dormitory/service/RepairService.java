package com.mycity.dormitory.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mycity.dormitory.dto.RepairSubmitDTO;
import com.mycity.dormitory.entity.Repair;

/**
 * 报修业务接口：提交报修单
 */
public interface RepairService extends IService<Repair> {
    void submit(Long userId, RepairSubmitDTO dto);  // 学生提交报修
}
