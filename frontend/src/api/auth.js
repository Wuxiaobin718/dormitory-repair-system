import request from '@/utils/request'

export function login(data) {
  return request({ url: '/user/login', method: 'post', data })
}

export function register(data) {
  return request({ url: '/user/register', method: 'post', data })
}

export function getUserInfo() {
  return request({ url: '/user/info', method: 'get' })
}

export function updateProfile(data) {
  return request({ url: '/user/profile', method: 'put', data })
}
