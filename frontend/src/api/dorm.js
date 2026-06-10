import request from '@/utils/request'

/**
 * 获取宿舍列表（学生选择宿舍时用）
 * @returns {Promise} 返回宿舍列表数组
 */
export function getList() {
  return request({ url: '/dorm/list', method: 'get' })
}

/**
 * 添加宿舍（管理员）
 * @param {Object} data - { building, floor, room }
 * @returns {Promise}
 */
export function add(data) {
  return request({ url: '/dorm/add', method: 'post', data })
}
