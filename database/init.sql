-- 校园宿舍报修维修管理系统 - 数据库初始化脚本
-- 创建数据库
CREATE DATABASE IF NOT EXISTS campus_dormitory DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE campus_dormitory;

-- 1. 用户表
CREATE TABLE IF NOT EXISTS `user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `username` VARCHAR(50) NOT NULL COMMENT '学号（账号）',
    `password` VARCHAR(100) NOT NULL COMMENT '密码（BCrypt加密）',
    `name` VARCHAR(20) DEFAULT NULL COMMENT '姓名',
    `phone` VARCHAR(20) DEFAULT NULL COMMENT '联系电话',
    `dorm_id` BIGINT DEFAULT NULL COMMENT '宿舍ID',
    `role` INT NOT NULL DEFAULT 0 COMMENT '角色：0=学生 1=管理员',
    `avatar` VARCHAR(255) DEFAULT NULL COMMENT '头像URL',
    `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 2. 宿舍表
CREATE TABLE IF NOT EXISTS `dorm` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `building` VARCHAR(20) NOT NULL COMMENT '楼栋',
    `floor` INT NOT NULL COMMENT '楼层',
    `room` VARCHAR(20) NOT NULL COMMENT '房间号',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='宿舍表';

-- 3. 报修表
CREATE TABLE IF NOT EXISTS `repair` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `user_id` BIGINT NOT NULL COMMENT '学生ID',
    `dorm_id` BIGINT NOT NULL COMMENT '宿舍ID',
    `type` VARCHAR(30) NOT NULL COMMENT '故障类型',
    `content` VARCHAR(500) DEFAULT NULL COMMENT '故障描述',
    `img` VARCHAR(255) DEFAULT NULL COMMENT '图片路径',
    `status` INT NOT NULL DEFAULT 0 COMMENT '状态：0=待处理 1=维修中 2=已完成',
    `admin_id` BIGINT DEFAULT NULL COMMENT '处理管理员ID',
    `create_time` DATETIME DEFAULT NULL COMMENT '报修时间',
    `finish_time` DATETIME DEFAULT NULL COMMENT '完成时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='报修表';

-- 4. 评价表
CREATE TABLE IF NOT EXISTS `comment` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `repair_id` BIGINT NOT NULL COMMENT '报修单ID',
    `score` INT NOT NULL COMMENT '评分：1-5',
    `content` VARCHAR(255) DEFAULT NULL COMMENT '评价内容',
    `create_time` DATETIME DEFAULT NULL COMMENT '评价时间',
    PRIMARY KEY (`id`),
    KEY `idx_repair_id` (`repair_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评价表';

-- 5. 通知表
CREATE TABLE IF NOT EXISTS `notification` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `user_id` BIGINT NOT NULL COMMENT '接收通知的用户ID',
    `type` VARCHAR(30) NOT NULL COMMENT '通知类型：STATUS_UPDATE/NEW_REPAIR/NEW_COMMENT',
    `title` VARCHAR(100) NOT NULL COMMENT '通知标题',
    `message` VARCHAR(500) DEFAULT NULL COMMENT '通知内容',
    `repair_id` BIGINT DEFAULT NULL COMMENT '关联报修单ID',
    `is_read` TINYINT NOT NULL DEFAULT 0 COMMENT '是否已读：0=未读 1=已读',
    `create_time` DATETIME DEFAULT NULL COMMENT '通知时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_user_read` (`user_id`, `is_read`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='通知表';

-- 6. 示例数据：宿舍楼栋
INSERT INTO `dorm` (`building`, `floor`, `room`) VALUES
('1栋', 1, '101'), ('1栋', 1, '102'), ('1栋', 1, '103'),
('1栋', 2, '201'), ('1栋', 2, '202'),
('2栋', 1, '101'), ('2栋', 1, '102'),
('2栋', 2, '201'), ('2栋', 2, '202'),
('3栋', 1, '101'), ('3栋', 1, '102'),
('3栋', 2, '201'), ('3栋', 2, '202');

-- 6. 创建默认管理员账号（二选一）：
--    方法1（推荐）: 在前端注册学号 admin 密码 admin123，再执行：
--      UPDATE user SET role = 1 WHERE username = 'admin';
--    方法2: 如果已有学生账号，直接升级：UPDATE user SET role = 1 WHERE username = '你的学号';
