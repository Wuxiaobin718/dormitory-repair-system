import axios from 'axios'
import { Message } from 'element-ui'
import router from '@/router'

const service = axios.create({
  baseURL: 'http://localhost:8080/api',
  timeout: 15000,
  headers: { 'Content-Type': 'application/json;charset=utf-8' }
})

service.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  error => Promise.reject(error)
)

service.interceptors.response.use(
  response => {
    const res = response.data
    if (res.code !== 200) {
      Message({ message: res.message || '请求失败', type: 'error', duration: 3000 })
      if (res.code === 401) {
        localStorage.removeItem('token')
        router.push('/login')
      }
    }
    return res
  },
  error => {
    if (error.message.includes('timeout')) {
      Message({ message: '请求超时，请重试', type: 'warning', duration: 3000 })
    } else if (!window.navigator.onLine) {
      Message({ message: '网络异常', type: 'error' })
    } else {
      if (error.response && error.response.status === 401) {
        localStorage.removeItem('token')
        router.push('/login')
        Message.warning('登录已过期，请重新登录')
      } else {
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
