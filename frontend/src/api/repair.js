import request from '@/utils/request'

export function submit(data) {
  return request({ url: '/repair/submit', method: 'post', data })
}

export function getMyRepairs(params) {
  return request({ url: '/repair/my', method: 'get', params })
}

export function getRepairList(params) {
  return request({ url: '/repair/list', method: 'get', params })
}

export function updateStatus(data) {
  return request({ url: '/repair/update', method: 'post', data })
}
