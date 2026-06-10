import Vue from 'vue'
import VueRouter from 'vue-router'
import NProgress from 'nprogress'       // 页面顶部进度条
import 'nprogress/nprogress.css'

Vue.use(VueRouter)

/**
 * 路由配置
 * meta 字段说明：
 *   requiresAuth - true=需要登录，false=无需登录
 *   title        - 页面标题，会在 beforeEach 中设置 document.title
 *   isFull       - true=全屏页面（不显示导航栏和页脚，如登录页）
 *   role         - 可选，限制角色访问（1=管理员）
 */
const routes = [
  // ── 公共页面 ──
  {
    path: '/',
    name: 'home',
    component: () => import('@/views/HomeView.vue'),
    meta: { title: '首页', requiresAuth: false }
  },
  {
    path: '/login',
    name: 'login',
    component: () => import('@/views/user/LoginView.vue'),
    meta: { requiresAuth: false, title: '登录', isFull: true }
  },
  {
    path: '/register',
    name: 'register',
    component: () => import('@/views/user/LoginView.vue'),
    meta: { requiresAuth: false, title: '注册', isFull: true }
  },
  {
    path: '/profile',
    name: 'profile',
    component: () => import('@/views/user/ProfileView.vue'),
    meta: { requiresAuth: true, title: '个人中心' }
  },
  // ── 学生端 ──
  {
    path: '/repair/submit',
    name: 'submit-repair',
    component: () => import('@/views/student/SubmitRepair.vue'),
    meta: { requiresAuth: true, title: '我要报修' }
  },
  {
    path: '/repair/my',
    name: 'my-repairs',
    component: () => import('@/views/student/MyRepairs.vue'),
    meta: { requiresAuth: true, title: '我的报修' }
  },
  {
    path: '/repair/:id/evaluate',
    name: 'evaluate',
    component: () => import('@/views/student/Evaluate.vue'),
    meta: { requiresAuth: true, title: '服务评价' }
  },
  // ── 管理员端 ──
  {
    path: '/admin/repairs',
    name: 'admin-repairs',
    component: () => import('@/views/admin/RepairManage.vue'),
    meta: { requiresAuth: true, title: '报修管理', role: 1 }
  },
  {
    path: '/admin/dorms',
    name: 'admin-dorms',
    component: () => import('@/views/admin/DormManage.vue'),
    meta: { requiresAuth: true, title: '宿舍管理', role: 1 }
  },
  {
    path: '/admin/stats',
    name: 'admin-stats',
    component: () => import('@/views/admin/Stats.vue'),
    meta: { requiresAuth: true, title: '数据统计', role: 1 }
  },
  // ── 通知中心 ──
  {
    path: '/notifications',
    name: 'notifications',
    component: () => import('@/views/NotificationView.vue'),
    meta: { requiresAuth: true, title: '消息通知' }
  },
  // ── 404 页面 ──
  {
    path: '/:pathMatch(.*)*',
    name: 'not-found',
    component: () => import('@/views/NotFoundView.vue'),
    meta: { title: '页面不存在', requiresAuth: false }
  }
]

const router = new VueRouter({
  mode: 'history',           // HTML5 History 模式，URL 不带 #
  base: process.env.BASE_URL,
  routes,
  scrollBehavior() {
    return { top: 0 }        // 页面切换时滚动到顶部
  }
})

NProgress.configure({ showSpinner: false })  // 进度条不显示加载转圈

/**
 * 路由守卫 — 登录验证
 * 白名单（/login, /register, /）不需要 token
 * 其他页面如果没有 token 则跳转到登录页，并记录来源路径
 */
const whiteList = ['/login', '/register', '/']

router.beforeEach(async (to, from, next) => {
  NProgress.start()
  // 设置页面标题
  document.title = to.meta.title
    ? `${to.meta.title} - 校园宿舍报修维修管理系统`
    : '校园宿舍报修维修管理系统'

  // 不需要登录的页面直接放行
  if (to.meta.requiresAuth === false) {
    next()
    return
  }

  // 需要登录但没 token → 跳登录页，带上当前页面路径作为 redirect
  const token = localStorage.getItem('token')
  if (!token) {
    next(`/login?redirect=${encodeURIComponent(to.fullPath)}`)
    NProgress.done()
    return
  }

  // 角色校验：如果路由限制了角色（如管理页 role:1），检查用户身份
  if (to.meta.role !== undefined) {
    const userInfo = localStorage.getItem('userInfo')
    if (userInfo) {
      const role = JSON.parse(userInfo).role
      if (role !== to.meta.role) {
        // 角色不匹配，跳转到首页
        next('/')
        NProgress.done()
        return
      }
    }
  }

  next()  // 有 token 且角色匹配，放行
})

router.afterEach(() => {
  NProgress.done()  // 进度条结束
})

export default router
