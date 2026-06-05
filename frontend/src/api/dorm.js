import request from '@/utils/request'

export function getList() {
  return request({ url: '/dorm/list', method: 'get' })
}

export function add(data) {
  return request({ url: '/dorm/add', method: 'post', data })
}
