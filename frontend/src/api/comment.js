import request from '@/utils/request'

/**
 * 提交评价（学生对已完成的报修进行评分）
 * @param {Object} data - { repairId, score(1-5), content }
 * @returns {Promise}
 */
export function add(data) {
  return request({ url: '/comment/add', method: 'post', data })
}

/**
 * 获取评价列表（管理员查看评价）
 * @param {Object} params - { repairId }
 * @returns {Promise}
 */
export function getList(params) {
  return request({ url: '/comment/list', method: 'get', params })
}
