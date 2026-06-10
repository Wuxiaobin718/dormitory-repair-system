package com.mycity.dormitory.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.mycity.dormitory.dto.RepairSubmitDTO;
import com.mycity.dormitory.entity.Repair;

import java.util.Map;

/**
 * 报修业务接口：提交报修单
 */
public interface RepairService extends IService<Repair> {
    void submit(Long userId, RepairSubmitDTO dto);  // 学生提交报修

    /**
     * 分页查询报修列表，并填充学生姓名、宿舍信息
     * @param studentName 可选，按学生姓名模糊搜索
     */
    Page<Repair> pageWithDetail(Page<Repair> page, LambdaQueryWrapper<Repair> wrapper, String studentName);

    /**
     * 获取当前用户各状态报修数量（用于前端 Tabs 显示计数）
     * @return map 如 { "total": 10, "pending": 3, "inProgress": 2, "completed": 5 }
     */
    Map<String, Long> getStatusCounts(Long userId);
}
