import Vue from 'vue'
import VueRouter from 'vue-router'
import NProgress from 'nprogress'
import 'nprogress/nprogress.css'

Vue.use(VueRouter)

const routes = [
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
  // 学生端
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
  // 管理员端
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
  {
    path: '/:pathMatch(.*)*',
    name: 'not-found',
    component: () => import('@/views/NotFoundView.vue'),
    meta: { title: '页面不存在', requiresAuth: false }
  }
]

const router = new VueRouter({
  mode: 'history',
  base: process.env.BASE_URL,
  routes,
  scrollBehavior() {
    return { top: 0 }
  }
})

NProgress.configure({ showSpinner: false })

const whiteList = ['/login', '/register', '/']

router.beforeEach(async (to, from, next) => {
  NProgress.start()
  document.title = to.meta.title
    ? `${to.meta.title} - 校园宿舍报修维修管理系统`
    : '校园宿舍报修维修管理系统'

  if (to.meta.requiresAuth === false) {
    next()
    return
  }

  const token = localStorage.getItem('token')
  if (!token) {
    next(`/login?redirect=${encodeURIComponent(to.fullPath)}`)
    NProgress.done()
    return
  }

  next()
})

router.afterEach(() => {
  NProgress.done()
})

export default router
