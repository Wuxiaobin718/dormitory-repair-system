package com.mycity.dormitory.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mycity.dormitory.dto.RepairSubmitDTO;
import com.mycity.dormitory.entity.Repair;
import com.mycity.dormitory.enums.RepairStatus;
import com.mycity.dormitory.mapper.RepairMapper;
import com.mycity.dormitory.service.RepairService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 报修业务实现：创建报修单，初始状态为"待处理"
 */
@Service
@RequiredArgsConstructor
public class RepairServiceImpl
        extends ServiceImpl<RepairMapper, Repair>
        implements RepairService {

    private final RepairMapper repairMapper;

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
    }
}
