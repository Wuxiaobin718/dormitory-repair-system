/*
MySQL Data Transfer
Source Host: localhost
Source Database: campus_dormitory
Target Host: localhost
Target Database: campus_dormitory
Date: 2026/6/8 22:13:21
*/

SET FOREIGN_KEY_CHECKS=0;
-- ----------------------------
-- Table structure for comment
-- ----------------------------
DROP TABLE IF EXISTS `comment`;
CREATE TABLE `comment` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `repair_id` bigint NOT NULL COMMENT '报修单ID',
  `score` int NOT NULL COMMENT '评分：1-5',
  `content` varchar(255) DEFAULT NULL COMMENT '评价内容',
  `create_time` datetime DEFAULT NULL COMMENT '评价时间',
  PRIMARY KEY (`id`),
  KEY `idx_repair_id` (`repair_id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='评价表';

-- ----------------------------
-- Table structure for dorm
-- ----------------------------
DROP TABLE IF EXISTS `dorm`;
CREATE TABLE `dorm` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `building` varchar(20) NOT NULL COMMENT '楼栋',
  `floor` int NOT NULL COMMENT '楼层',
  `room` varchar(20) NOT NULL COMMENT '房间号',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=82 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='宿舍表';

-- ----------------------------
-- Table structure for notification
-- ----------------------------
DROP TABLE IF EXISTS `notification`;
CREATE TABLE `notification` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` bigint NOT NULL COMMENT '接收通知的用户ID',
  `type` varchar(30) NOT NULL COMMENT '通知类型',
  `title` varchar(100) NOT NULL COMMENT '通知标题',
  `message` varchar(500) DEFAULT NULL COMMENT '通知内容',
  `repair_id` bigint DEFAULT NULL COMMENT '关联报修单ID',
  `is_read` tinyint NOT NULL DEFAULT '0' COMMENT '是否已读 0=未读 1=已读',
  `create_time` datetime DEFAULT NULL COMMENT '通知时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_user_read` (`user_id`,`is_read`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='通知表';

-- ----------------------------
-- Table structure for repair
-- ----------------------------
DROP TABLE IF EXISTS `repair`;
CREATE TABLE `repair` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` bigint NOT NULL COMMENT '学生ID',
  `dorm_id` bigint NOT NULL COMMENT '宿舍ID',
  `type` varchar(30) NOT NULL COMMENT '故障类型',
  `content` varchar(500) DEFAULT NULL COMMENT '故障描述',
  `img` varchar(255) DEFAULT NULL COMMENT '图片路径',
  `status` int NOT NULL DEFAULT '0' COMMENT '状态：0=待处理 1=维修中 2=已完成',
  `admin_id` bigint DEFAULT NULL COMMENT '处理管理员ID',
  `create_time` datetime DEFAULT NULL COMMENT '报修时间',
  `finish_time` datetime DEFAULT NULL COMMENT '完成时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=63 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='报修表';

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `username` varchar(50) NOT NULL COMMENT '学号（账号）',
  `password` varchar(100) NOT NULL COMMENT '密码（BCrypt加密）',
  `name` varchar(20) DEFAULT NULL COMMENT '姓名',
  `phone` varchar(20) DEFAULT NULL COMMENT '联系电话',
  `dorm_id` bigint DEFAULT NULL COMMENT '宿舍ID',
  `role` int NOT NULL DEFAULT '0' COMMENT '角色：0=学生 1=管理员',
  `avatar` varchar(255) DEFAULT NULL COMMENT '头像URL',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户表';

-- ----------------------------
-- Records 
-- ----------------------------
INSERT INTO `comment` VALUES ('1', '1', '5', '维修效率快，师傅人也很好', '2026-06-01 08:48:00');
INSERT INTO `comment` VALUES ('2', '27', '3', '还是有问题', '2026-06-06 19:45:20');
INSERT INTO `comment` VALUES ('3', '59', '5', '维修的非常快，效率高', '2026-06-07 12:28:45');
INSERT INTO `comment` VALUES ('4', '52', '4', '很好', '2026-06-07 23:49:47');
INSERT INTO `comment` VALUES ('5', '60', '5', '', '2026-06-08 11:42:12');
INSERT INTO `comment` VALUES ('6', '60', '5', '111', '2026-06-08 11:42:55');
INSERT INTO `dorm` VALUES ('1', '1栋', '1', '101');
INSERT INTO `dorm` VALUES ('2', '1栋', '1', '102');
INSERT INTO `dorm` VALUES ('3', '1栋', '1', '103');
INSERT INTO `dorm` VALUES ('4', '1栋', '2', '201');
INSERT INTO `dorm` VALUES ('5', '1栋', '2', '202');
INSERT INTO `dorm` VALUES ('6', '2栋', '1', '101');
INSERT INTO `dorm` VALUES ('7', '2栋', '1', '102');
INSERT INTO `dorm` VALUES ('8', '2栋', '2', '201');
INSERT INTO `dorm` VALUES ('9', '2栋', '2', '202');
INSERT INTO `dorm` VALUES ('10', '3栋', '1', '101');
INSERT INTO `dorm` VALUES ('11', '3栋', '1', '102');
INSERT INTO `dorm` VALUES ('12', '3栋', '2', '201');
INSERT INTO `dorm` VALUES ('13', '3栋', '2', '202');
INSERT INTO `dorm` VALUES ('14', '1栋', '1', '101');
INSERT INTO `dorm` VALUES ('15', '1栋', '1', '102');
INSERT INTO `dorm` VALUES ('16', '1栋', '1', '103');
INSERT INTO `dorm` VALUES ('17', '1栋', '2', '201');
INSERT INTO `dorm` VALUES ('18', '1栋', '2', '202');
INSERT INTO `dorm` VALUES ('19', '2栋', '1', '101');
INSERT INTO `dorm` VALUES ('20', '2栋', '1', '102');
INSERT INTO `dorm` VALUES ('21', '2栋', '2', '201');
INSERT INTO `dorm` VALUES ('22', '2栋', '2', '202');
INSERT INTO `dorm` VALUES ('23', '3栋', '1', '101');
INSERT INTO `dorm` VALUES ('24', '3栋', '1', '102');
INSERT INTO `dorm` VALUES ('25', '3栋', '2', '201');
INSERT INTO `dorm` VALUES ('26', '3栋', '2', '202');
INSERT INTO `dorm` VALUES ('27', '1栋', '1', '101');
INSERT INTO `dorm` VALUES ('28', '1栋', '1', '102');
INSERT INTO `dorm` VALUES ('29', '1栋', '1', '103');
INSERT INTO `dorm` VALUES ('30', '1栋', '2', '201');
INSERT INTO `dorm` VALUES ('31', '1栋', '2', '202');
INSERT INTO `dorm` VALUES ('32', '2栋', '1', '101');
INSERT INTO `dorm` VALUES ('33', '2栋', '1', '102');
INSERT INTO `dorm` VALUES ('34', '2栋', '2', '201');
INSERT INTO `dorm` VALUES ('35', '2栋', '2', '202');
INSERT INTO `dorm` VALUES ('36', '3栋', '1', '101');
INSERT INTO `dorm` VALUES ('37', '3栋', '1', '102');
INSERT INTO `dorm` VALUES ('38', '3栋', '2', '201');
INSERT INTO `dorm` VALUES ('39', '3栋', '2', '202');
INSERT INTO `dorm` VALUES ('40', '博华一', '9', '921');
INSERT INTO `dorm` VALUES ('41', '5栋', '1', '510');
INSERT INTO `dorm` VALUES ('42', '1栋', '1', '101');
INSERT INTO `dorm` VALUES ('43', '1栋', '1', '102');
INSERT INTO `dorm` VALUES ('44', '1栋', '1', '103');
INSERT INTO `dorm` VALUES ('45', '1栋', '2', '201');
INSERT INTO `dorm` VALUES ('46', '1栋', '2', '202');
INSERT INTO `dorm` VALUES ('47', '2栋', '1', '101');
INSERT INTO `dorm` VALUES ('48', '2栋', '1', '102');
INSERT INTO `dorm` VALUES ('49', '2栋', '2', '201');
INSERT INTO `dorm` VALUES ('50', '2栋', '2', '202');
INSERT INTO `dorm` VALUES ('51', '3栋', '1', '101');
INSERT INTO `dorm` VALUES ('52', '3栋', '1', '102');
INSERT INTO `dorm` VALUES ('53', '3栋', '2', '201');
INSERT INTO `dorm` VALUES ('54', '3栋', '2', '202');
INSERT INTO `dorm` VALUES ('55', '1栋', '1', '101');
INSERT INTO `dorm` VALUES ('56', '1栋', '1', '102');
INSERT INTO `dorm` VALUES ('57', '1栋', '1', '103');
INSERT INTO `dorm` VALUES ('58', '1栋', '2', '201');
INSERT INTO `dorm` VALUES ('59', '1栋', '2', '202');
INSERT INTO `dorm` VALUES ('60', '2栋', '1', '101');
INSERT INTO `dorm` VALUES ('61', '2栋', '1', '102');
INSERT INTO `dorm` VALUES ('62', '2栋', '2', '201');
INSERT INTO `dorm` VALUES ('63', '2栋', '2', '202');
INSERT INTO `dorm` VALUES ('64', '3栋', '1', '101');
INSERT INTO `dorm` VALUES ('65', '3栋', '1', '102');
INSERT INTO `dorm` VALUES ('66', '3栋', '2', '201');
INSERT INTO `dorm` VALUES ('67', '3栋', '2', '202');
INSERT INTO `dorm` VALUES ('68', '1栋', '1', '101');
INSERT INTO `dorm` VALUES ('69', '1栋', '1', '102');
INSERT INTO `dorm` VALUES ('70', '1栋', '1', '103');
INSERT INTO `dorm` VALUES ('71', '1栋', '2', '201');
INSERT INTO `dorm` VALUES ('72', '1栋', '2', '202');
INSERT INTO `dorm` VALUES ('73', '2栋', '1', '101');
INSERT INTO `dorm` VALUES ('74', '2栋', '1', '102');
INSERT INTO `dorm` VALUES ('75', '2栋', '2', '201');
INSERT INTO `dorm` VALUES ('76', '2栋', '2', '202');
INSERT INTO `dorm` VALUES ('77', '3栋', '1', '101');
INSERT INTO `dorm` VALUES ('78', '3栋', '1', '102');
INSERT INTO `dorm` VALUES ('79', '3栋', '2', '201');
INSERT INTO `dorm` VALUES ('80', '3栋', '2', '202');
INSERT INTO `dorm` VALUES ('81', '1', '1', '150');
INSERT INTO `notification` VALUES ('1', '2', 'NEW_REPAIR', '新报修通知', '有新的报修单 #58（水电故障）', '58', '1', '2026-06-07 12:20:40');
INSERT INTO `notification` VALUES ('2', '2', 'NEW_REPAIR', '新报修通知', '有新的报修单 #59（门窗故障）', '59', '1', '2026-06-07 12:21:21');
INSERT INTO `notification` VALUES ('3', '1', 'STATUS_UPDATE', '报修进度更新', '报修单 #59 状态已更新为【维修中】', '59', '1', '2026-06-07 12:28:07');
INSERT INTO `notification` VALUES ('4', '1', 'STATUS_UPDATE', '报修进度更新', '报修单 #59 状态已更新为【已完成】', '59', '1', '2026-06-07 12:28:09');
INSERT INTO `notification` VALUES ('5', '2', 'NEW_COMMENT', '新评价通知', '报修单 #59 收到新的评价', '59', '1', '2026-06-07 12:28:45');
INSERT INTO `notification` VALUES ('6', '2', 'NEW_COMMENT', '新评价通知', '报修单 #52 收到新的评价', '52', '1', '2026-06-07 23:49:47');
INSERT INTO `notification` VALUES ('7', '2', 'NEW_REPAIR', '新报修通知', '有新的报修单 #61（其他）', '61', '0', '2026-06-08 08:48:55');
INSERT INTO `notification` VALUES ('8', '3', 'STATUS_UPDATE', '报修进度更新', '报修单 #58 状态已更新为【维修中】', '58', '0', '2026-06-08 08:49:26');
INSERT INTO `notification` VALUES ('9', '1', 'STATUS_UPDATE', '报修进度更新', '报修单 #56 状态已更新为【维修中】', '56', '0', '2026-06-08 08:49:34');
INSERT INTO `notification` VALUES ('10', '1', 'STATUS_UPDATE', '报修进度更新', '报修单 #56 状态已更新为【已完成】', '56', '0', '2026-06-08 08:49:39');
INSERT INTO `notification` VALUES ('11', '1', 'STATUS_UPDATE', '报修进度更新', '报修单 #54 状态已更新为【已完成】', '54', '0', '2026-06-08 08:49:42');
INSERT INTO `notification` VALUES ('12', '2', 'NEW_REPAIR', '新报修通知', '有新的报修单 #62（水电故障）', '62', '0', '2026-06-08 11:38:11');
INSERT INTO `notification` VALUES ('13', '1', 'STATUS_UPDATE', '报修进度更新', '报修单 #60 状态已更新为【维修中】', '60', '0', '2026-06-08 11:41:53');
INSERT INTO `notification` VALUES ('14', '1', 'STATUS_UPDATE', '报修进度更新', '报修单 #60 状态已更新为【已完成】', '60', '0', '2026-06-08 11:42:00');
INSERT INTO `notification` VALUES ('15', '2', 'NEW_COMMENT', '新评价通知', '报修单 #60 收到新的评价', '60', '1', '2026-06-08 11:42:12');
INSERT INTO `notification` VALUES ('16', '2', 'NEW_COMMENT', '新评价通知', '报修单 #60 收到新的评价', '60', '0', '2026-06-08 11:42:55');
INSERT INTO `repair` VALUES ('1', '1', '1', '卫浴问题', '喷头损坏', '/uploads/eea105c5-e2c0-42a7-8801-6738b15656b1.jpg', '2', '2', '2026-05-25 11:51:26', '2026-06-01 08:42:05');
INSERT INTO `repair` VALUES ('2', '1', '3', '门窗故障', '门上的锁断了', '/uploads/8541926d-d4b3-4501-8bb2-5a79d447b79e.jpg', '0', null, '2026-05-26 11:36:56', null);
INSERT INTO `repair` VALUES ('3', '1', '1', '水电故障', '水龙头漏水，一直滴个不停，晚上影响休息', null, '2', '1', '2026-05-01 08:30:00', '2026-05-02 10:00:00');
INSERT INTO `repair` VALUES ('4', '1', '2', '门窗故障', '宿舍门锁坏了，关不上，钥匙插不进去', null, '2', '1', '2026-05-03 09:15:00', '2026-05-04 11:30:00');
INSERT INTO `repair` VALUES ('5', '1', '3', '家具损坏', '书桌抽屉拉手脱落，抽屉拉不出来', null, '2', '1', '2026-05-05 10:00:00', '2026-05-06 14:00:00');
INSERT INTO `repair` VALUES ('6', '1', '4', '卫浴问题', '卫生间马桶堵塞，排水缓慢', null, '2', '1', '2026-05-07 11:20:00', '2026-05-08 09:45:00');
INSERT INTO `repair` VALUES ('7', '1', '5', '墙体地面', '窗户旁边墙面渗水，墙皮脱落', null, '1', '1', '2026-05-09 14:00:00', '2026-05-10 16:00:00');
INSERT INTO `repair` VALUES ('8', '1', '6', '水电故障', '空调不制冷，开了半个小时还是热风', null, '1', '1', '2026-05-11 08:45:00', null);
INSERT INTO `repair` VALUES ('9', '1', '7', '水电故障', '走廊灯一闪一闪的，已经持续两天了', null, '1', '1', '2026-05-12 09:30:00', null);
INSERT INTO `repair` VALUES ('10', '1', '8', '门窗故障', '阳台门推拉困难，轨道变形卡住了', null, '1', '1', '2026-05-13 10:15:00', null);
INSERT INTO `repair` VALUES ('11', '1', '9', '家具损坏', '床板断裂一块，睡觉时塌下去了', null, '0', null, '2026-05-14 11:00:00', null);
INSERT INTO `repair` VALUES ('12', '1', '10', '卫浴问题', '花洒支架松动，洗澡时掉下来了', null, '0', null, '2026-05-15 14:30:00', null);
INSERT INTO `repair` VALUES ('13', '1', '11', '水电故障', '插座没电，插上电器没有反应', null, '0', null, '2026-05-16 08:00:00', null);
INSERT INTO `repair` VALUES ('14', '1', '12', '其他', '宿舍门牌号脱落，需要重新安装', null, '0', null, '2026-05-17 09:20:00', null);
INSERT INTO `repair` VALUES ('15', '1', '13', '墙体地面', '天花板有裂缝，担心安全问题', null, '0', null, '2026-05-18 10:40:00', null);
INSERT INTO `repair` VALUES ('16', '1', '1', '水电故障', '热水器不出热水，冷水正常出', null, '0', null, '2026-05-19 11:55:00', null);
INSERT INTO `repair` VALUES ('17', '1', '2', '门窗故障', '窗户关不严实，漏风严重', null, '0', null, '2026-05-20 13:10:00', null);
INSERT INTO `repair` VALUES ('18', '1', '3', '卫浴问题', '洗手台下水管漏水，柜子都泡了', null, '0', null, '2026-05-21 14:25:00', null);
INSERT INTO `repair` VALUES ('19', '1', '4', '家具损坏', '衣柜门合页坏了，门挂在那里', null, '0', null, '2026-05-22 15:40:00', null);
INSERT INTO `repair` VALUES ('20', '1', '5', '水电故障', '电风扇转不动了，启动有异响', null, '2', '2', '2026-05-23 08:50:00', '2026-06-01 10:36:02');
INSERT INTO `repair` VALUES ('21', '1', '6', '墙体地面', '地板砖翘起来了，容易绊倒人', null, '0', null, '2026-05-24 09:30:00', null);
INSERT INTO `repair` VALUES ('22', '1', '7', '其他', '需要加装一个挂钩在卫生间', null, '0', null, '2026-05-25 10:15:00', null);
INSERT INTO `repair` VALUES ('23', '1', '8', '水电故障', '宿舍总闸跳闸，一天跳了三次', null, '2', '2', '2026-05-26 11:00:00', '2026-06-01 10:35:38');
INSERT INTO `repair` VALUES ('24', '1', '9', '门窗故障', '门把手断了，从外面打不开', null, '0', null, '2026-05-27 14:00:00', null);
INSERT INTO `repair` VALUES ('25', '1', '10', '卫浴问题', '地漏返味，卫生间很臭', null, '2', '2', '2026-05-28 15:30:00', '2026-06-06 19:57:44');
INSERT INTO `repair` VALUES ('26', '1', '11', '家具损坏', '椅子螺丝松动，坐上去摇晃', null, '2', '2', '2026-05-29 08:20:00', '2026-06-01 10:35:32');
INSERT INTO `repair` VALUES ('27', '1', '12', '水电故障', '灯泡烧了，需要更换新的', null, '2', '2', '2026-05-30 09:45:00', '2026-06-01 10:35:35');
INSERT INTO `repair` VALUES ('28', '1', '1', '水电故障', '水龙头漏水，一直滴个不停，晚上影响休息', null, '2', '1', '2026-05-01 08:30:00', '2026-05-02 10:00:00');
INSERT INTO `repair` VALUES ('29', '1', '2', '门窗故障', '宿舍门锁坏了，关不上，钥匙插不进去', null, '2', '1', '2026-05-03 09:15:00', '2026-05-04 11:30:00');
INSERT INTO `repair` VALUES ('30', '1', '3', '家具损坏', '书桌抽屉拉手脱落，抽屉拉不出来', null, '2', '1', '2026-05-05 10:00:00', '2026-05-06 14:00:00');
INSERT INTO `repair` VALUES ('31', '1', '4', '卫浴问题', '卫生间马桶堵塞，排水缓慢', null, '2', '1', '2026-05-07 11:20:00', '2026-05-08 09:45:00');
INSERT INTO `repair` VALUES ('32', '1', '5', '墙体地面', '窗户旁边墙面渗水，墙皮脱落', null, '1', '1', '2026-05-09 14:00:00', '2026-05-10 16:00:00');
INSERT INTO `repair` VALUES ('33', '1', '6', '水电故障', '空调不制冷，开了半个小时还是热风', null, '1', '1', '2026-05-11 08:45:00', null);
INSERT INTO `repair` VALUES ('34', '1', '7', '水电故障', '走廊灯一闪一闪的，已经持续两天了', null, '1', '1', '2026-05-12 09:30:00', null);
INSERT INTO `repair` VALUES ('35', '1', '8', '门窗故障', '阳台门推拉困难，轨道变形卡住了', null, '1', '1', '2026-05-13 10:15:00', null);
INSERT INTO `repair` VALUES ('36', '1', '9', '家具损坏', '床板断裂一块，睡觉时塌下去了', null, '0', null, '2026-05-14 11:00:00', null);
INSERT INTO `repair` VALUES ('37', '1', '10', '卫浴问题', '花洒支架松动，洗澡时掉下来了', null, '0', null, '2026-05-15 14:30:00', null);
INSERT INTO `repair` VALUES ('38', '1', '11', '水电故障', '插座没电，插上电器没有反应', null, '0', null, '2026-05-16 08:00:00', null);
INSERT INTO `repair` VALUES ('39', '1', '12', '其他', '宿舍门牌号脱落，需要重新安装', null, '0', null, '2026-05-17 09:20:00', null);
INSERT INTO `repair` VALUES ('40', '1', '13', '墙体地面', '天花板有裂缝，担心安全问题', null, '0', null, '2026-05-18 10:40:00', null);
INSERT INTO `repair` VALUES ('41', '1', '1', '水电故障', '热水器不出热水，冷水正常出', null, '0', null, '2026-05-19 11:55:00', null);
INSERT INTO `repair` VALUES ('42', '1', '2', '门窗故障', '窗户关不严实，漏风严重', null, '0', null, '2026-05-20 13:10:00', null);
INSERT INTO `repair` VALUES ('43', '1', '3', '卫浴问题', '洗手台下水管漏水，柜子都泡了', null, '0', null, '2026-05-21 14:25:00', null);
INSERT INTO `repair` VALUES ('44', '1', '4', '家具损坏', '衣柜门合页坏了，门挂在那里', null, '2', '2', '2026-05-22 15:40:00', '2026-06-01 10:36:00');
INSERT INTO `repair` VALUES ('45', '1', '5', '水电故障', '电风扇转不动了，启动有异响', null, '0', null, '2026-05-23 08:50:00', null);
INSERT INTO `repair` VALUES ('46', '1', '6', '墙体地面', '地板砖翘起来了，容易绊倒人', null, '2', '2', '2026-05-24 09:30:00', '2026-06-01 10:36:03');
INSERT INTO `repair` VALUES ('47', '1', '7', '其他', '需要加装一个挂钩在卫生间', null, '0', null, '2026-05-25 10:15:00', null);
INSERT INTO `repair` VALUES ('48', '1', '8', '水电故障', '宿舍总闸跳闸，一天跳了三次', null, '0', null, '2026-05-26 11:00:00', null);
INSERT INTO `repair` VALUES ('49', '1', '9', '门窗故障', '门把手断了，从外面打不开', null, '0', null, '2026-05-27 14:00:00', null);
INSERT INTO `repair` VALUES ('50', '1', '10', '卫浴问题', '地漏返味，卫生间很臭', null, '2', '2', '2026-05-28 15:30:00', '2026-06-01 10:35:30');
INSERT INTO `repair` VALUES ('51', '1', '11', '家具损坏', '椅子螺丝松动，坐上去摇晃', null, '1', '2', '2026-05-29 08:20:00', null);
INSERT INTO `repair` VALUES ('52', '1', '12', '水电故障', '灯泡烧了，需要更换新的', null, '2', '2', '2026-05-30 09:45:00', '2026-06-01 10:35:33');
INSERT INTO `repair` VALUES ('53', '1', '13', '墙体地面', '墙壁裂开了', '', '2', '2', '2026-06-06 19:44:02', '2026-06-06 19:46:20');
INSERT INTO `repair` VALUES ('54', '1', '1', '水电故障', '不来水了', '', '2', '2', '2026-06-06 19:53:12', '2026-06-08 08:49:42');
INSERT INTO `repair` VALUES ('55', '1', '2', '水电故障', '跳闸了', '', '0', null, '2026-06-06 19:59:29', null);
INSERT INTO `repair` VALUES ('56', '1', '1', '水电故障', '喷洒断了', '', '2', '2', '2026-06-06 20:01:19', '2026-06-08 08:49:39');
INSERT INTO `repair` VALUES ('57', '3', '3', '家具损坏', '桌子烂了', '', '1', '2', '2026-06-06 20:10:08', null);
INSERT INTO `repair` VALUES ('58', '3', '41', '水电故障', '漏水', '', '1', '2', '2026-06-07 12:20:40', null);
INSERT INTO `repair` VALUES ('59', '1', '41', '门窗故障', '门坏了', '', '2', '2', '2026-06-07 12:21:21', '2026-06-07 12:28:09');
INSERT INTO `repair` VALUES ('60', '1', '1', '家具损坏', '桌子发霉了', '', '2', '2', '2026-06-07 23:48:20', '2026-06-08 11:42:00');
INSERT INTO `repair` VALUES ('61', '1', '3', '其他', '笔记本进水了', '/uploads/f3358b9b-4eea-498f-8acf-329b25573a76.jpg', '0', null, '2026-06-08 08:48:55', null);
INSERT INTO `repair` VALUES ('62', '1', '2', '水电故障', '11', '/uploads/59251359-16de-488d-9d78-8dfa9024ab57.jpg', '0', null, '2026-06-08 11:38:11', null);
INSERT INTO `user` VALUES ('1', '202301', '$2a$10$Dq7V4/QXAZ4qZKhNtTK4zOey/ktoo0O5JW72OKV8jdND7wM4BX3eq', 'wu', '12345678910', null, '0', '/uploads/c7b954d4-a341-410d-956c-8f70b6ba3388.jpg', null);
INSERT INTO `user` VALUES ('2', 'admin', '$2a$10$INjOF8PucrRE/eAc.y4xnubLR73ahv7B8gxaqVnvgqtVDH0GWajQW', 'BIN', '12345600000', null, '1', '/uploads/24b26d56-dbf4-4b04-b1e9-fc02a130ae71.jpg', null);
INSERT INTO `user` VALUES ('3', '202302', '$2a$10$7lYBqoMV63M7cXPX8CEReOaTDNwvaq.rucPl6WIE1sjqbQa2cAm0m', 'king', '', null, '0', null, null);
INSERT INTO `user` VALUES ('4', '2024001', '$2a$10$4lldxyHw/rBQpzReFvrb3OV2FoJGi.jJHn18uT8fD874rFF5PYm4S', '张三', '13800138000', '1', '0', null, null);
