package com.mycity.dormitory.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mycity.dormitory.entity.Dorm;
import org.apache.ibatis.annotations.Mapper;

/**
 * 宿舍 Mapper：继承 BaseMapper 获得基础 CRUD，无需额外方法
 */
@Mapper
public interface DormMapper extends BaseMapper<Dorm> {
}
