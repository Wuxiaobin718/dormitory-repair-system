import request from '@/utils/request'

/**
 * 用户登录
 * @param {Object} data - { username, password }
 * @returns {Promise} 返回 { code, message, data: { token, userInfo } }
 */
export function login(data) {
  return request({ url: '/user/login', method: 'post', data })
}

/**
 * 用户注册
 * @param {Object} data - { username, password, name, phone }
 * @returns {Promise} 返回 { code, message }
 */
export function register(data) {
  return request({ url: '/user/register', method: 'post', data })
}

/**
 * 获取当前登录用户信息
 * @returns {Promise} 返回用户信息
 */
export function getUserInfo() {
  return request({ url: '/user/info', method: 'get' })
}

/**
 * 更新个人资料（头像、手机号等）
 * @param {Object} data - 要更新的字段
 * @returns {Promise}
 */
export function updateProfile(data) {
  return request({ url: '/user/profile', method: 'put', data })
}

/**
 * 修改当前用户密码（需旧密码）
 * @param {Object} data - { oldPassword, newPassword }
 * @returns {Promise}
 */
export function changePassword(data) {
  return request({ url: '/user/change-password', method: 'post', data })
}

/**
 * 管理员重置指定用户密码（无需旧密码，重置为 123456）
 * @param {number} userId - 目标用户的ID
 * @returns {Promise}
 */
export function resetPassword(userId) {
  return request({ url: '/user/reset-password', method: 'post', data: { userId } })
}

/**
 * 管理员按学号查询用户
 * @param {string} username - 学号
 * @returns {Promise}
 */
export function searchUser(username) {
  return request({ url: '/user/search', method: 'get', params: { username } })
}
