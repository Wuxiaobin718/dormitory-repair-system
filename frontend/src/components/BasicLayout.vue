<template>
  <div>
    <!-- Main layout with header -->
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

    <!-- Full-screen pages (login/register) -->
    <router-view v-else />
  </div>
</template>

<script>
export default {
  data() {
    return {}
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
      return 'http://localhost:8080' + url
    },
    avatarChar() {
      return (this.userInfo.name || this.userInfo.username || 'U').charAt(0).toUpperCase()
    }
  },
  methods: {
    handleUserCommand(command) {
      if (command === 'profile') {
        this.$router.push('/profile')
      } else if (command === 'logout') {
        this.$store.dispatch('user/logout')
        this.$router.push('/login')
        this.$message.success('已安全退出')
      }
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
