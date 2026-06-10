# 校园宿舍报修维修管理系统

基于 Spring Boot + Vue 的校园宿舍报修维修管理系统，支持学生提交报修、管理员派单维修、实时通知推送等功能。

## 功能模块

### 学生端
- **提交报修** — 选择宿舍、故障类型，上传图片，描述问题
- **我的报修** — 查看报修进度，追踪处理状态
- **服务评价** — 维修完成后对服务进行评分和评价

### 管理员端
- **报修管理** — 查看全部报修，派单/处理/完成状态流转
- **宿舍管理** — 维护楼栋、楼层、房间信息
- **数据统计** — 报修数据可视化分析
- **实时通知** — WebSocket 实时推送报修状态变更

### 通用
- 用户注册/登录（JWT 认证）
- 个人资料管理
- 实时消息通知

## 技术栈

| 层级 | 技术 |
|------|------|
| 后端 | JDK 17, Spring Boot 3.5.11, MyBatis-Plus 3.5.9 |
| 数据库 | MySQL 8.0 |
| 认证 | JWT (jjwt 0.12.x), BCrypt |
| 实时推送 | WebSocket |
| 前端 | Vue 2, Element UI 2.x, Vue Router 3, Vuex 3, Axios |
| 构建 | Maven (后端), Vue CLI 5 (前端) |

## 快速开始

### 环境要求

- JDK 17+
- Maven 3.8+
- MySQL 8.0+
- Node.js 16+
- npm 8+

### 1. 克隆项目

```bash
git clone https://github.com/Wuxiaobin718/dormitory-repair-system.git
```

### 2. 初始化数据库

```sql
source database/init.sql
```

或导入完整数据：

```bash
mysql -u root -p campus_dormitory < database/campus_dormitory.sql
```

### 3. 启动后端

```bash
cd backend
mvn spring-boot:run
```

后端默认运行在 `http://localhost:8080`

### 4. 启动前端

```bash
cd frontend
npm install
npm run serve
```

前端默认运行在 `http://localhost:8081`

## 数据库设计

| 表 | 说明 | 关键字段 |
|----|------|---------|
| `user` | 用户表 | username(学号), password(BCrypt), role(0=学生/1=管理员) |
| `dorm` | 宿舍表 | building(楼栋), floor(楼层), room(房间号) |
| `repair` | 报修表 | type(故障类型), status(0=待处理/1=维修中/2=已完成) |
| `comment` | 评价表 | score(1-5), content |
| `notification` | 通知表 | repair_id, content, status(0=未读/1=已读) |

## API 概览

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/user/register` | 注册 |
| POST | `/api/user/login` | 登录 |
| GET | `/api/user/info` | 当前用户信息 |
| GET | `/api/dorm/list` | 宿舍列表 |
| POST | `/api/dorm/add` | 添加宿舍(管理员) |
| POST | `/api/repair/submit` | 提交报修 |
| GET | `/api/repair/my` | 我的报修 |
| GET | `/api/repair/list` | 全部报修(管理员) |
| POST | `/api/repair/update` | 修改状态 |
| POST | `/api/comment/add` | 添加评价 |
| GET | `/api/comment/list` | 评价列表 |
| GET | `/api/notification/list` | 通知列表 |
| PUT | `/api/notification/read` | 标记已读 |
| WS | `/ws/repair` | WebSocket 通知推送 |

## 项目结构

```
├── backend/                        # Spring Boot 后端
│   └── src/main/java/com/mycity/dormitory/
│       ├── common/                 # 统一响应 Result<T>
│       ├── config/                 # CORS、拦截器、MyBatis-Plus、WebSocket 配置
│       ├── controller/             # REST 控制器
│       ├── dto/                    # 请求对象
│       ├── entity/                 # 数据实体
│       ├── enums/                  # 枚举（报修状态、用户角色）
│       ├── exception/              # 全局异常处理
│       ├── handler/                # WebSocket 处理器
│       ├── interceptor/            # JWT 拦截器、WebSocket 拦截器
│       ├── mapper/                 # MyBatis-Plus 映射器
│       ├── service/                # 业务逻辑
│       └── util/                   # 工具类（JWT、文件）
├── frontend/                       # Vue 2 前端
│   └── src/
│       ├── api/                    # Axios API 模块
│       ├── components/             # 公共组件
│       ├── router/                 # 路由配置
│       ├── store/                  # Vuex 状态管理
│       ├── utils/                  # 工具（请求实例、WebSocket）
│       └── views/                  # 页面（student/ admin/ user/）
└── database/                       # SQL 脚本
```
