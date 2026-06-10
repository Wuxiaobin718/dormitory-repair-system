import axios from 'axios'
import { Message } from 'element-ui'
import router from '@/router'

/**
 * Axios 实例 — 统一配置
 * baseURL 指向后端 API 地址，所有请求自动拼接 /api 前缀
 */
const service = axios.create({
  baseURL: 'http://localhost:8080/api',  // 后端接口基础地址
  timeout: 15000,                        // 请求超时时间 15 秒
  headers: { 'Content-Type': 'application/json;charset=utf-8' }
})

/**
 * 请求拦截器 — 自动携带 Token
 * 每次请求前从 localStorage 读取 token，加到 Authorization 请求头
 */
service.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`  // JWT Bearer 格式
    }
    return config
  },
  error => Promise.reject(error)
)

/**
 * 响应拦截器 — 统一处理后端返回结果
 * 处理业务错误（code !== 200）、401 未授权自动跳登录、网络超时等
 */
service.interceptors.response.use(
  response => {
    const res = response.data
    if (res.code !== 200) {
      // 业务错误（code 500），弹出错误提示
      Message({ message: res.message || '请求失败', type: 'error', duration: 3000 })
      if (res.code === 401) {
        // 未授权，清除 token 并跳转到登录页
        localStorage.removeItem('token')
        router.push('/login')
      }
    }
    return res  // 直接返回 response.data，调用方拿到的就是 { code, message, data }
  },
  error => {
    // 网络层面的错误处理
    if (error.message.includes('timeout')) {
      Message({ message: '请求超时，请重试', type: 'warning', duration: 3000 })
    } else if (!window.navigator.onLine) {
      Message({ message: '网络异常', type: 'error' })
    } else {
      if (error.response && error.response.status === 401) {
        // HTTP 401：token 过期或无效
        localStorage.removeItem('token')
        router.push('/login')
        Message.warning('登录已过期，请重新登录')
      } else {
        // 其他 HTTP 错误
        Message({
          message: error.response?.data?.message || '系统错误',
          type: 'error',
          duration: 3000
        })
      }
    }
    return Promise.reject(error)
  }
)

export default service
