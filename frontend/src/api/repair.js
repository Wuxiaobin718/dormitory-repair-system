import request from '@/utils/request'

/**
 * 提交报修单（学生）
 * @param {Object} data - { dormId, type, content, img }
 * @returns {Promise}
 */
export function submit(data) {
  return request({ url: '/repair/submit', method: 'post', data })
}

/**
 * 获取我的报修列表（学生）
 * @param {Object} params - { page, size, status? }
 * @returns {Promise} 分页数据
 */
export function getMyRepairs(params) {
  return request({ url: '/repair/my', method: 'get', params })
}

/**
 * 获取全部报修列表（管理员）
 * @param {Object} params - { page, size, status? }
 * @returns {Promise} 分页数据
 */
export function getRepairList(params) {
  return request({ url: '/repair/list', method: 'get', params })
}

/**
 * 更新报修单状态（管理员接单/完成）
 * @param {Object} data - { id, status } status: 1=接单, 2=完成
 * @returns {Promise}
 */
export function updateStatus(data) {
  return request({ url: '/repair/update', method: 'post', data })
}

/**
 * 获取当前用户各状态报修数量（用于 Tabs 计数）
 * @returns {Promise} { total, pending, inProgress, completed }
 */
export function getStats() {
  return request({ url: '/repair/stats', method: 'get' })
}
