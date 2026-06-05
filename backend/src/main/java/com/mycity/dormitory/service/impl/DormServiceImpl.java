package com.mycity.dormitory.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mycity.dormitory.entity.Dorm;
import com.mycity.dormitory.mapper.DormMapper;
import com.mycity.dormitory.service.DormService;
import org.springframework.stereotype.Service;

/**
 * 宿舍业务实现：继承 ServiceImpl 获得 MyBatis-Plus 通用 CRUD
 */
@Service
public class DormServiceImpl
        extends ServiceImpl<DormMapper, Dorm>
        implements DormService {
}
