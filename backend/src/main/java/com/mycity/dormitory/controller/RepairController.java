package com.mycity.dormitory.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mycity.dormitory.common.Result;
import com.mycity.dormitory.dto.RepairSubmitDTO;
import com.mycity.dormitory.dto.RepairUpdateDTO;
import com.mycity.dormitory.entity.Repair;
import com.mycity.dormitory.enums.RepairStatus;
import com.mycity.dormitory.service.RepairService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * 报修 Controller：学生提交/查看报修，管理员查看全部/更新状态
 */
@RestController
@RequestMapping("/api/repair")
@RequiredArgsConstructor
public class RepairController {

    private final RepairService repairService;
    private final HttpServletRequest request;

    /** POST /api/repair/submit — 学生提交报修单 */
    @PostMapping("/submit")
    public Result<Void> submit(@Valid @RequestBody RepairSubmitDTO dto) {
        Long userId = (Long) request.getAttribute("userId");
        repairService.submit(userId, dto);
        return Result.success();
    }

    /** GET /api/repair/my — 学生查看自己的报修记录（分页） */
    @GetMapping("/my")
    public Result<Page<Repair>> my(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Long userId = (Long) request.getAttribute("userId");
        Page<Repair> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Repair> wrapper = new LambdaQueryWrapper<Repair>()
                .eq(Repair::getUserId, userId)
                .orderByDesc(Repair::getCreateTime);
        return Result.success(repairService.page(pageParam, wrapper));
    }

    /** GET /api/repair/list — 管理员查看全部报修（可按状态筛选，分页） */
    @GetMapping("/list")
    public Result<Page<Repair>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer status) {
        Page<Repair> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Repair> wrapper = new LambdaQueryWrapper<>();
        if (status != null) {
            wrapper.eq(Repair::getStatus, status);  // 按状态筛选
        }
        wrapper.orderByDesc(Repair::getCreateTime);
        return Result.success(repairService.page(pageParam, wrapper));
    }

    /** POST /api/repair/update — 管理员更新报修状态（接单→维修→完成） */
    @PostMapping("/update")
    public Result<Void> update(@Valid @RequestBody RepairUpdateDTO dto) {
        // ① 查找报修单
        Repair repair = repairService.getById(dto.getId());
        if (repair == null) {
            return Result.error("报修单不存在");
        }

        // ② 获取当前状态和目标状态（Integer → 枚举）
        RepairStatus currentStatus = RepairStatus.fromCode(repair.getStatus());
        RepairStatus targetStatus = RepairStatus.fromCode(dto.getStatus());

        // ③ 状态机校验：当前状态能否转换到目标状态
        if (!currentStatus.canTransitionTo(targetStatus)) {
            return Result.error(
                String.format("状态转换失败：%s 不能直接转为 %s（允许的流转：待处理→维修中→已完成，不可回退或跳步）",
                    currentStatus.getDesc(), targetStatus.getDesc())
            );
        }

        // ④ 执行状态更新
        repair.setStatus(targetStatus.getCode());

        // ⑤ 附加业务：根据目标状态处理额外逻辑
        if (targetStatus == RepairStatus.IN_PROGRESS) {
            // 接单时记录管理员ID
            Long adminId = (Long) request.getAttribute("userId");
            repair.setAdminId(adminId);
        } else if (targetStatus == RepairStatus.COMPLETED) {
            // 完成时记录完成时间
            repair.setFinishTime(LocalDateTime.now());
        }

        repairService.updateById(repair);
        return Result.success();
    }
}
