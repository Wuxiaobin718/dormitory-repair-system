package com.mycity.dormitory.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mycity.dormitory.common.Result;
import com.mycity.dormitory.dto.RepairSubmitDTO;
import com.mycity.dormitory.dto.RepairUpdateDTO;
import com.mycity.dormitory.entity.Repair;
import com.mycity.dormitory.enums.RepairStatus;
import com.mycity.dormitory.service.RepairService;
import com.mycity.dormitory.service.WebSocketPushService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 报修 Controller：学生提交/查看报修，管理员查看全部/更新状态
 */
@RestController
@RequestMapping("/api/repair")
@RequiredArgsConstructor
public class RepairController {

    private final RepairService repairService;
    private final HttpServletRequest request;
    private final WebSocketPushService webSocketPushService;

    /** POST /api/repair/submit — 学生提交报修单 */
    @PostMapping("/submit")
    public Result<Void> submit(@Valid @RequestBody RepairSubmitDTO dto) {
        Long userId = (Long) request.getAttribute("userId");
        repairService.submit(userId, dto);
        return Result.success();
    }

    /** GET /api/repair/my — 学生查看自己的报修记录（分页，含学生/宿舍信息） */
    @GetMapping("/my")
    public Result<Page<Repair>> my(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer status) {
        Long userId = (Long) request.getAttribute("userId");
        Page<Repair> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Repair> wrapper = new LambdaQueryWrapper<Repair>()
                .eq(Repair::getUserId, userId)
                .orderByDesc(Repair::getCreateTime);
        if (status != null) {
            wrapper.eq(Repair::getStatus, status);
        }
        return Result.success(repairService.pageWithDetail(pageParam, wrapper, null));
    }

    /** GET /api/repair/list — 管理员查看全部报修（含学生/宿舍信息，可搜索筛选） */
    @GetMapping("/list")
    public Result<Page<Repair>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String studentName) {
        // 仅管理员可查看全部报修
        Integer role = (Integer) request.getAttribute("role");
        if (role == null || role != 1) {
            return Result.error("无权限：仅管理员可查看全部报修");
        }
        Page<Repair> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Repair> wrapper = new LambdaQueryWrapper<>();
        if (status != null) {
            wrapper.eq(Repair::getStatus, status);  // 按状态筛选
        }
        wrapper.orderByDesc(Repair::getCreateTime);
        return Result.success(repairService.pageWithDetail(pageParam, wrapper, studentName));
    }

    /** GET /api/repair/stats — 获取当前用户各状态报修数量（用于 Tabs 计数） */
    @GetMapping("/stats")
    public Result<Map<String, Long>> stats() {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(repairService.getStatusCounts(userId));
    }

    /** POST /api/repair/update — 管理员更新报修状态（接单→维修→完成） */
    @PostMapping("/update")
    public Result<Void> update(@Valid @RequestBody RepairUpdateDTO dto) {
        // 仅管理员可操作状态变更
        Integer role = (Integer) request.getAttribute("role");
        if (role == null || role != 1) {
            return Result.error("无权限：仅管理员可处理报修");
        }
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

        // 状态变更后通过 WebSocket 向学生推送实时通知
        webSocketPushService.pushStatusUpdate(repair);
        return Result.success();
    }
}
