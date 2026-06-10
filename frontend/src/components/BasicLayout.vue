<template>
  <div>
    <!--
      主布局：导航栏 + 内容区 + 页脚
      登录/注册页（$route.meta.isFull === true）不显示导航栏和页脚，全屏展示
    -->
    <el-container v-if="!$route.meta.isFull" class="main-container">
      <el-header height="70px" class="custom-header">
        <div class="header-inner">
          <!-- Logo -->
          <div class="logo-area" @click="$router.push('/')">
            <i class="el-icon-s-tools logo-icon"></i>
            <span class="logo-text">宿修系统</span>
          </div>

          <!-- Navigation -->
          <nav class="nav-menu">
            <el-link :underline="false" @click="$router.push('/')"
              :class="{ active: $route.path === '/' }">
              首页
            </el-link>
            <template v-if="token">
              <el-link v-if="!isAdmin" :underline="false" @click="$router.push('/repair/submit')"
                :class="{ active: $route.path === '/repair/submit' }">
                我要报修
              </el-link>
              <el-link v-if="!isAdmin" :underline="false" @click="$router.push('/repair/my')"
                :class="{ active: $route.path === '/repair/my' }">
                我的报修
              </el-link>
              <el-link v-if="isAdmin" :underline="false" @click="$router.push('/admin/repairs')"
                :class="{ active: $route.path === '/admin/repairs' }">
                报修管理
              </el-link>
              <el-link v-if="isAdmin" :underline="false" @click="$router.push('/admin/dorms')"
                :class="{ active: $route.path === '/admin/dorms' }">
                宿舍管理
              </el-link>
              <el-link v-if="isAdmin" :underline="false" @click="$router.push('/admin/stats')"
                :class="{ active: $route.path === '/admin/stats' }">
                数据统计
              </el-link>
            </template>
          </nav>

          <!-- User area -->
          <div class="user-area">
            <template v-if="token">
              <el-dropdown trigger="click" @command="handleUserCommand">
                <span class="user-trigger">
                  <span class="user-avatar">
                    <img v-if="avatarUrl" :src="avatarUrl" class="avatar-img" />
                    <span v-else>{{ avatarChar }}</span>
                  </span>
                  <span class="user-name">{{ userInfo.name || userInfo.username }}</span>
                  <i class="el-icon-arrow-down el-icon--right"></i>
                </span>
                <el-dropdown-menu slot="dropdown">
                  <el-dropdown-item command="profile">
                    <i class="el-icon-user"></i> 个人中心
                  </el-dropdown-item>
                  <el-dropdown-item command="logout" divided>
                    <i class="el-icon-switch-button"></i> 退出登录
                  </el-dropdown-item>
                </el-dropdown-menu>
              </el-dropdown>
              <!-- Notification bell -->
              <div class="notif-area">
                <el-popover placement="bottom-end" width="360" trigger="click"
                  :visible-arrow="false" popper-class="notif-popover"
                  @show="loadNotifList">
                  <el-badge :value="unreadCount" :hidden="unreadCount === 0" slot="reference"
                    class="notif-badge">
                    <i class="el-icon-bell notif-bell" :class="{ 'has-unread': unreadCount > 0 }"></i>
                  </el-badge>
                  <div class="notif-header">
                    <span class="notif-title">消息通知</span>
                    <el-button type="text" size="mini" @click="handleMarkAllRead"
                      v-if="unreadCount > 0">全部标为已读</el-button>
                  </div>
                  <div class="notif-list" v-loading="notifLoading">
                    <div v-if="notifList.length === 0" class="notif-empty">
                      <i class="el-icon-bell"></i>
                      <p>暂无通知</p>
                    </div>
                    <div v-for="n in notifList" :key="n.id" class="notif-item"
                      :class="{ 'notif-unread': !n.isRead }"
                      @click="handleNotifClick(n)">
                      <div class="notif-dot" v-if="!n.isRead"></div>
                      <div class="notif-body">
                        <div class="notif-item-title">{{ n.title }}</div>
                        <div class="notif-item-msg">{{ n.message }}</div>
                        <div class="notif-item-time">{{ n.createTime }}</div>
                      </div>
                    </div>
                  </div>
                  <div class="notif-footer">
                    <el-button type="text" size="mini" @click="$router.push('/notifications')">
                      查看全部通知
                    </el-button>
                  </div>
                </el-popover>
              </div>
            </template>
            <template v-else>
              <el-button plain size="small" @click="$router.push('/login')">登录</el-button>
              <el-button type="primary" size="small" @click="$router.push('/register')">注册</el-button>
            </template>
          </div>
        </div>
      </el-header>

      <el-main class="custom-main">
        <div class="page-content">
          <router-view />
        </div>
      </el-main>

      <el-footer height="auto" class="custom-footer">
        <div class="footer-inner">
          <div class="footer-links">
            <span>校园宿舍报修维修管理系统</span>
          </div>
          <p class="footer-copy">&copy; 2026 校园宿舍报修系统 | 在线报修 · 进度跟踪 · 服务评价</p>
        </div>
      </el-footer>
    </el-container>

    <!-- 全屏页面（登录/注册）不显示导航栏 -->
    <router-view v-else />
  </div>
</template>

<script>
import api from '@/api'
import ws from '@/utils/websocket'

export default {
  data() {
    return {
      notifyHandlers: {},  // 存储 WS 通知监听器，用于清理
      // 消息通知
      unreadCount: 0,
      notifList: [],
      notifLoading: false,
      notifTimer: null      // 定时轮询未读数
    }
  },
  computed: {
    token() {
      return this.$store.state.user.token || localStorage.getItem('token')
    },
    userInfo() {
      return this.$store.state.user.userInfo || {}
    },
    isAdmin() {
      return this.$store.getters['user/isAdmin']
    },
    avatarUrl() {
      const url = this.userInfo.avatar
      if (!url) return ''
      if (url.startsWith('http')) return url
      return 'http://localhost:8080' + url  // 拼接后端图片地址
    },
    avatarChar() {
      // 取用户名首字母作为头像占位符
      return (this.userInfo.name || this.userInfo.username || 'U').charAt(0).toUpperCase()
    }
  },
  watch: {
    // 登录/退出时自动连接/断开 WebSocket
    token(newVal) {
      if (newVal) {
        ws.connect()
      } else {
        ws.disconnect()
      }
    }
  },
  mounted() {
    if (this.token) {
      ws.connect()
    }

    // ========== 注册全局 WebSocket 通知弹窗 ==========
    this.notifyHandlers.newRepair = (data) => {
      this.$notify({ title: '新报修通知', message: data.message, type: 'info', duration: 5000,
        onClick: () => { this.$router.push('/admin/repairs').catch(() => {}) } })
      this.fetchUnreadCount()
    }
    ws.on('NEW_REPAIR', this.notifyHandlers.newRepair)

    this.notifyHandlers.statusUpdate = (data) => {
      this.$notify({ title: '报修进度更新', message: data.message, type: data.status === 2 ? 'success' : 'warning', duration: 5000,
        onClick: () => { this.$router.push('/repair/my').catch(() => {}) } })
      this.fetchUnreadCount()
    }
    ws.on('STATUS_UPDATE', this.notifyHandlers.statusUpdate)

    this.notifyHandlers.newComment = (data) => {
      this.$notify({ title: '新评价通知', message: data.message, type: 'success', duration: 5000,
        onClick: () => { this.$router.push('/admin/repairs').catch(() => {}) } })
      this.fetchUnreadCount()
    }
    ws.on('NEW_COMMENT', this.notifyHandlers.newComment)

    // 首次加载未读数 + 定时轮询（每 60 秒）
    this.fetchUnreadCount()
    this.notifTimer = setInterval(() => { this.fetchUnreadCount() }, 60000)
  },
  beforeDestroy() {
    ws.off('NEW_REPAIR', this.notifyHandlers.newRepair)
    ws.off('STATUS_UPDATE', this.notifyHandlers.statusUpdate)
    ws.off('NEW_COMMENT', this.notifyHandlers.newComment)
    ws.disconnect()
    clearInterval(this.notifTimer)
  },
  methods: {
    handleUserCommand(command) {
      if (command === 'profile') {
        this.$router.push('/profile')
      } else if (command === 'logout') {
        ws.disconnect()
        this.$store.dispatch('user/logout')
        this.$router.push('/login')
        this.$message.success('已安全退出')
      }
    },
    // ========== 消息通知 ==========
    async fetchUnreadCount() {
      try {
        const res = await api.notification.getUnreadCount()
        if (res.code === 200) this.unreadCount = res.data.count || 0
      } catch {}
    },
    async loadNotifList() {
      if (this.notifList.length > 0) return
      this.notifLoading = true
      try {
        const res = await api.notification.getList({ page: 1, size: 10 })
        if (res.code === 200) this.notifList = res.data.records
      } finally { this.notifLoading = false }
    },
    async handleNotifClick(n) {
      if (!n.isRead) {
        await api.notification.markRead(n.id)
        n.isRead = 1
        this.unreadCount = Math.max(0, this.unreadCount - 1)
      }
      // 根据通知类型跳转
      if (n.type === 'NEW_REPAIR' || n.type === 'NEW_COMMENT') {
        this.$router.push('/admin/repairs').catch(() => {})
      } else if (n.type === 'STATUS_UPDATE') {
        this.$router.push('/repair/my').catch(() => {})
      }
    },
    async handleMarkAllRead() {
      try {
        await api.notification.markAllRead()
        this.notifList.forEach(n => { n.isRead = 1 })
        this.unreadCount = 0
        this.$message.success('已全部标为已读')
      } catch {}
    }
  }
}
</script>

<style>
/* ---- Global ---- */
body {
  margin: 0;
  font-family: "Helvetica Neue", Helvetica, "PingFang SC", "Hiragino Sans GB",
    "Microsoft YaHei", "微软雅黑", Arial, sans-serif;
  background-color: #f8fafc;
  color: #303133;
}

/* ---- Header ---- */
.custom-header {
  background: #fff;
  height: 70px !important;
  display: flex;
  align-items: center;
  position: sticky;
  top: 0;
  z-index: 1000;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}

.header-inner {
  width: 1200px;
  margin: 0 auto;
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 100%;
}

/* Logo */
.logo-area {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  flex-shrink: 0;
}
.logo-icon {
  font-size: 28px;
  color: #409EFF;
}
.logo-text {
  font-size: 22px;
  font-weight: 800;
  color: #1e293b;
  letter-spacing: 1px;
}

/* Navigation */
.nav-menu {
  display: flex;
  align-items: center;
  gap: 25px;
  margin: 0 40px;
}
.nav-menu .el-link {
  font-size: 15px;
  font-weight: 500;
  color: #64748b;
  padding: 8px 0;
  position: relative;
  transition: color 0.25s ease;
}
.nav-menu .el-link:hover {
  color: #409EFF;
}
.nav-menu .el-link.active {
  color: #409EFF;
  font-weight: 700;
}
.nav-menu .el-link.active::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 2px;
  background: #409EFF;
}

/* User area */
.user-area {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-shrink: 0;
}

.notif-area {
  display: flex; align-items: center;
}
.notif-badge { cursor: pointer; }
.notif-bell {
  font-size: 22px; color: #64748b; transition: color 0.2s;
  vertical-align: middle;
}
.notif-bell:hover, .notif-bell.has-unread { color: #409EFF; }
.user-area .el-button--default.is-plain {
  background: transparent;
  border: 1px solid var(--c-border);
  color: #64748b;
  border-radius: 18px;
  padding: 7px 18px;
  font-weight: 500;
  transition: all 0.25s ease;
}
.user-area .el-button--default.is-plain:hover {
  border-color: #409EFF;
  color: #409EFF;
  background: #ECF5FF;
}
.user-area .el-button--primary {
  border-radius: 18px;
  padding: 7px 18px;
}

/* User dropdown trigger */
.user-trigger {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  color: #334155;
  font-size: 14px;
  font-weight: 600;
  padding: 4px 12px 4px 4px;
  border-radius: 20px;
  transition: all 0.25s ease;
}
.user-trigger:hover {
  background: #f1f5f9;
}
.user-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: #409EFF;
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  font-weight: 700;
  flex-shrink: 0;
  overflow: hidden;
}
.user-avatar .avatar-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}
.user-name {
  max-width: 80px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.el-dropdown-menu {
  border: none;
  border-radius: 10px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
  padding: 6px;
}
.el-dropdown-menu__item {
  border-radius: 6px;
  padding: 8px 16px;
  font-size: 14px;
}
.el-dropdown-menu__item i {
  margin-right: 6px;
}
.el-dropdown-menu__item:hover {
  background: #ECF5FF;
  color: #409EFF;
}

/* ---- Main ---- */
.custom-main {
  padding: 0;
  min-height: calc(100vh - 70px - 150px);
}
.page-content {
  width: 1200px;
  margin: 30px auto;
  min-height: 600px;
}

/* ---- Footer ---- */
.custom-footer {
  background: #1e293b;
  padding: 40px 0 24px !important;
  margin-top: 60px;
}
.footer-inner {
  width: 1200px;
  margin: 0 auto;
  text-align: center;
  border-top: 1px solid #334155;
  padding-top: 24px;
}
.footer-links {
  font-size: 14px;
  color: #cbd5e1;
  margin-bottom: 8px;
}
.footer-copy {
  font-size: 12px;
  color: #64748b;
  margin: 0;
}

/* Transition */
.fade-enter-active, .fade-leave-active {
  transition: opacity 0.3s;
}
.fade-enter, .fade-leave-to {
  opacity: 0;
}
</style>

<!-- 通知弹窗全局样式（el-popover 渲染在 body 下） -->
<style>
.notif-popover { padding: 0 !important; border-radius: 12px !important; }
.notif-popover .el-popover__title { display: none; }
.notif-header {
  display: flex; justify-content: space-between; align-items: center;
  padding: 16px 20px 12px; border-bottom: 1px solid #f0f2f5;
}
.notif-title { font-size: 15px; font-weight: 700; color: #1e293b; }
.notif-list { max-height: 360px; overflow-y: auto; }
.notif-empty { text-align: center; padding: 40px 0; color: #94a3b8; }
.notif-empty i { font-size: 32px; display: block; margin-bottom: 8px; }
.notif-empty p { margin: 0; font-size: 13px; }

.notif-item {
  display: flex; align-items: flex-start; gap: 10px;
  padding: 12px 20px; cursor: pointer; transition: background 0.15s;
  border-bottom: 1px solid #f8fafc;
}
.notif-item:last-child { border-bottom: none; }
.notif-item:hover { background: #f8fafc; }
.notif-unread { background: #F0F7FF; }
.notif-unread:hover { background: #E3F2FD; }

.notif-dot {
  flex-shrink: 0; width: 8px; height: 8px; border-radius: 50%;
  background: #409EFF; margin-top: 6px;
}
.notif-body { flex: 1; min-width: 0; }
.notif-item-title { font-size: 13px; font-weight: 700; color: #1e293b; }
.notif-item-msg { font-size: 12px; color: #64748b; margin: 2px 0; line-height: 1.4; }
.notif-item-time { font-size: 11px; color: #94a3b8; }

.notif-footer {
  text-align: center; padding: 10px 20px;
  border-top: 1px solid #f0f2f5;
}
</style>
