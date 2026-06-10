package com.mycity.dormitory.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mycity.dormitory.entity.Notification;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * 通知 Mapper
 */
@Mapper
public interface NotificationMapper extends BaseMapper<Notification> {
    /** 统计当前用户未读通知数 */
    @Select("SELECT COUNT(*) FROM notification WHERE user_id = #{userId} AND is_read = 0")
    Long countUnread(Long userId);
}
