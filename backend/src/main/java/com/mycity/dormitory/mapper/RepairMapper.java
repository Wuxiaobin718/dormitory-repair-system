package com.mycity.dormitory.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mycity.dormitory.entity.Repair;
import org.apache.ibatis.annotations.Mapper;

/**
 * 报修单 Mapper：继承 BaseMapper 获得基础 CRUD，无需额外方法
 */
@Mapper
public interface RepairMapper extends BaseMapper<Repair> {
}
