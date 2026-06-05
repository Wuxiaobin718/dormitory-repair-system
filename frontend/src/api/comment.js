import request from '@/utils/request'

export function add(data) {
  return request({ url: '/comment/add', method: 'post', data })
}

export function getList(params) {
  return request({ url: '/comment/list', method: 'get', params })
}
