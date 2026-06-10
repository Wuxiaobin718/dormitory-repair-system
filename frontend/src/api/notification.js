import request from '@/utils/request'

/**
 * 获取当前用户的通知列表（分页）
 */
export function getList({ page = 1, size = 20, isRead } = {}) {
  const url = `/notification/list?page=${page}&size=${size}` + (isRead !== undefined ? `&isRead=${isRead}` : '')
  return request({ url, method: 'get' })
}

/**
 * 获取未读通知数
 */
export function getUnreadCount() {
  return request({ url: '/notification/unread-count', method: 'get' })
}

/**
 * 标记单条通知为已读
 */
export function markRead(id) {
  return request({ url: `/notification/read/${id}`, method: 'post' })
}

/**
 * 标记所有通知为已读
 */
export function markAllRead() {
  return request({ url: '/notification/read-all', method: 'post' })
}
