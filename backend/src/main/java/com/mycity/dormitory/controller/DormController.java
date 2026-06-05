package com.mycity.dormitory.controller;

import com.mycity.dormitory.common.Result;
import com.mycity.dormitory.entity.Dorm;
import com.mycity.dormitory.service.DormService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 宿舍 Controller：宿舍列表查询（学生用）、宿舍添加（管理员用）
 */
@RestController
@RequestMapping("/api/dorm")
@RequiredArgsConstructor
public class DormController {

    private final DormService dormService;

    /** GET /api/dorm/list — 获取全部宿舍列表（用于注册时选择宿舍） */
    @GetMapping("/list")
    public Result<List<Dorm>> list() {
        return Result.success(dormService.list());
    }

    /** POST /api/dorm/add — 管理员添加宿舍 */
    @PostMapping("/add")
    public Result<Void> add(@RequestBody Dorm dorm) {
        dormService.save(dorm);
        return Result.success();
    }
}
