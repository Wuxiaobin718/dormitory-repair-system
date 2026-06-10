/**
 * WebSocket 连接管理器
 *
 * 用法：
 *   import ws from '@/utils/websocket'
 *
 *   // 监听消息
 *   ws.on('STATUS_UPDATE', data => { ... })
 *   ws.on('NEW_REPAIR', data => { ... })
 *
 *   // 组件销毁时取消监听
 *   ws.off('STATUS_UPDATE', handler)
 *
 * 连接生命周期由 BasicLayout.vue 自动管理（登录时连接，退出时断开）
 */

const WS_URL = 'ws://localhost:8080/ws/repair'

let ws = null               // WebSocket 实例
let listeners = {}          // { type: [fn, fn, ...] }
let reconnectTimer = null   // 重连定时器
let reconnectAttempts = 0
const MAX_RECONNECT = 5     // 最大重连次数
const RECONNECT_INTERVAL = 3000  // 重连间隔(ms)

/**
 * 建立 WebSocket 连接
 * 从 localStorage 读取 token 进行认证
 */
function connect() {
  // 已连接或正在连接则不重复创建
  if (ws && (ws.readyState === WebSocket.OPEN || ws.readyState === WebSocket.CONNECTING)) {
    return
  }

  const token = localStorage.getItem('token')
  if (!token) return

  try {
    ws = new WebSocket(`${WS_URL}?token=${token}`)

    ws.onopen = () => {
      console.log('[WS] 连接已建立')
      reconnectAttempts = 0  // 重置重连计数
    }

    ws.onmessage = (event) => {
      try {
        const data = JSON.parse(event.data)
        console.log('[WS] 收到消息:', data)

        // 触发对应类型的监听器
        const handlers = listeners[data.type] || []
        handlers.forEach(fn => fn(data))
      } catch (e) {
        console.warn('[WS] 消息解析失败:', e)
      }
    }

    ws.onclose = (event) => {
      console.log('[WS] 连接已关闭, code:', event.code)
      ws = null
      // 非正常关闭时尝试重连
      if (event.code !== 1000 && event.code !== 1001) {
        scheduleReconnect()
      }
    }

    ws.onerror = (err) => {
      console.error('[WS] 连接错误:', err)
    }
  } catch (e) {
    console.error('[WS] 创建连接失败:', e)
    scheduleReconnect()
  }
}

/**
 * 断开 WebSocket 连接
 */
function disconnect() {
  clearTimeout(reconnectTimer)
  reconnectAttempts = 0
  if (ws) {
    ws.onclose = null  // 阻止触发重连
    ws.close(1000, '用户主动断开')
    ws = null
  }
}

/**
 * 注册消息监听器
 * @param {string} type 消息类型，如 'STATUS_UPDATE', 'NEW_REPAIR'
 * @param {Function} fn  回调函数
 */
function on(type, fn) {
  if (!listeners[type]) {
    listeners[type] = []
  }
  listeners[type].push(fn)
}

/**
 * 移除消息监听器
 * @param {string} type 消息类型
 * @param {Function} fn  之前注册的回调（不传则移除该类型所有监听器）
 */
function off(type, fn) {
  if (!listeners[type]) return
  if (fn) {
    listeners[type] = listeners[type].filter(f => f !== fn)
  } else {
    delete listeners[type]
  }
}

/**
 * 计划重连（指数退避：3s → 6s → 12s → 24s → 30s max）
 */
function scheduleReconnect() {
  if (reconnectAttempts >= MAX_RECONNECT) {
    console.log('[WS] 已达最大重连次数，停止重连')
    return
  }
  const delay = Math.min(RECONNECT_INTERVAL * Math.pow(2, reconnectAttempts), 30000)
  reconnectAttempts++
  console.log(`[WS] 将在 ${delay}ms 后尝试第 ${reconnectAttempts} 次重连`)
  clearTimeout(reconnectTimer)
  reconnectTimer = setTimeout(() => {
    connect()
  }, delay)
}

export default {
  connect,
  disconnect,
  on,
  off
}
