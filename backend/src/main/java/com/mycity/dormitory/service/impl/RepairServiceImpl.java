package com.mycity.dormitory.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mycity.dormitory.dto.RepairSubmitDTO;
import com.mycity.dormitory.entity.Dorm;
import com.mycity.dormitory.entity.Repair;
import com.mycity.dormitory.entity.User;
import com.mycity.dormitory.enums.RepairStatus;
import com.mycity.dormitory.mapper.DormMapper;
import com.mycity.dormitory.mapper.RepairMapper;
import com.mycity.dormitory.mapper.UserMapper;
import com.mycity.dormitory.service.RepairService;
import com.mycity.dormitory.service.WebSocketPushService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 报修业务实现：创建报修单，初始状态为"待处理"
 */
@Service
@RequiredArgsConstructor
public class RepairServiceImpl
        extends ServiceImpl<RepairMapper, Repair>
        implements RepairService {

    private final RepairMapper repairMapper;
    private final UserMapper userMapper;
    private final DormMapper dormMapper;
    private final WebSocketPushService webSocketPushService;

    /** 提交报修：设置用户、宿舍、故障信息，初始状态 0=待处理 */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void submit(Long userId, RepairSubmitDTO dto) {
        Repair repair = new Repair();
        repair.setUserId(userId);
        repair.setDormId(dto.getDormId());
        repair.setType(dto.getType());
        repair.setContent(dto.getContent());
        repair.setImg(dto.getImg());
        repair.setStatus(RepairStatus.PENDING.getCode());  // 初始状态：待处理
        repair.setCreateTime(LocalDateTime.now());
        repairMapper.insert(repair);

        // 提交后通过 WebSocket 向所有在线管理员推送新报修通知
        webSocketPushService.pushNewRepair(repair);
    }

    /** 分页查询并填充学生姓名、宿舍信息 */
    @Override
    public Page<Repair> pageWithDetail(Page<Repair> page, LambdaQueryWrapper<Repair> wrapper, String studentName) {
        // 如果按学生姓名搜索，先查出匹配的用户 ID，再过滤报修
        if (studentName != null && !studentName.isBlank()) {
            List<Long> matchedUserIds = userMapper.selectList(
                    new LambdaQueryWrapper<User>()
                            .like(User::getName, studentName)
            ).stream().map(User::getId).collect(Collectors.toList());

            if (matchedUserIds.isEmpty()) {
                // 没有匹配的学生，返回空页
                page.setRecords(List.of());
                page.setTotal(0);
                return page;
            }
            wrapper.in(Repair::getUserId, matchedUserIds);
        }
        Page<Repair> result = repairMapper.selectPage(page, wrapper);
        fillDetail(result.getRecords());
        return result;
    }

    /** 获取当前用户各状态报修数量 */
    @Override
    public Map<String, Long> getStatusCounts(Long userId) {
        LambdaQueryWrapper<Repair> wrapper = new LambdaQueryWrapper<Repair>().eq(Repair::getUserId, userId);
        long total = repairMapper.selectCount(wrapper);

        wrapper.eq(Repair::getStatus, 0);
        long pending = repairMapper.selectCount(wrapper);
        wrapper.clear(); wrapper.eq(Repair::getUserId, userId).eq(Repair::getStatus, 1);
        long inProgress = repairMapper.selectCount(wrapper);
        wrapper.clear(); wrapper.eq(Repair::getUserId, userId).eq(Repair::getStatus, 2);
        long completed = repairMapper.selectCount(wrapper);

        Map<String, Long> map = new HashMap<>();
        map.put("total", total);
        map.put("pending", pending);
        map.put("inProgress", inProgress);
        map.put("completed", completed);
        return map;
    }

    /** 批量填充学生姓名、手机号、宿舍信息 */
    private void fillDetail(List<Repair> repairs) {
        if (repairs.isEmpty()) return;

        // 1. 批量查用户
        List<Long> userIds = repairs.stream().map(Repair::getUserId).distinct().collect(Collectors.toList());
        Map<Long, User> userMap = userMapper.selectBatchIds(userIds)
                .stream().collect(Collectors.toMap(User::getId, Function.identity()));

        // 2. 批量查宿舍
        List<Long> dormIds = repairs.stream().map(Repair::getDormId).distinct().collect(Collectors.toList());
        Map<Long, Dorm> dormMap = dormMapper.selectBatchIds(dormIds)
                .stream().collect(Collectors.toMap(Dorm::getId, Function.identity()));

        // 3. 填充
        for (Repair r : repairs) {
            User u = userMap.get(r.getUserId());
            if (u != null) {
                r.setStudentName(u.getName());
                r.setStudentPhone(u.getPhone());
            }
            Dorm d = dormMap.get(r.getDormId());
            if (d != null) {
                r.setBuilding(d.getBuilding());
                r.setFloor(d.getFloor());
                r.setRoom(d.getRoom());
            }
        }
    }
}
