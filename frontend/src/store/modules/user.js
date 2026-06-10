import api from '@/api'

/**
 * Vuex 用户模块
 * 管理用户登录状态、用户信息，并同步到 localStorage 保持持久化
 */
export default {
  namespaced: true,  // 启用命名空间，通过 store.state.user.xxx 访问
  state: {
    // 从 localStorage 恢复登录状态（页面刷新后不丢失）
    userInfo: localStorage.getItem('userInfo') ? JSON.parse(localStorage.getItem('userInfo')) : null,
    token: localStorage.getItem('token') || null
  },
  getters: {
    hasUserInfo: state => !!state.userInfo,
    isLoggedIn: state => !!state.token,
    username: state => state.userInfo?.username || '',
    name: state => state.userInfo?.name || '',
    role: state => state.userInfo?.role || 0,
    isAdmin: state => state.userInfo?.role === 1  // 判断是否为管理员
  },
  mutations: {
    SET_TOKEN(state, token) { state.token = token },
    SET_USER_INFO(state, userInfo) { state.userInfo = userInfo },
    CLEAR_USER_INFO(state) {
      state.userInfo = null
      state.token = null
    }
  },
  actions: {
    /**
     * 登录：调后端接口 → 存 token → 存用户信息
     */
    async login({ commit }, loginData) {
      const res = await api.auth.login(loginData)
      if (res.code === 200) {
        commit('SET_TOKEN', res.data.token)
        localStorage.setItem('token', res.data.token)       // 持久化 token
        commit('SET_USER_INFO', res.data)
        localStorage.setItem('userInfo', JSON.stringify(res.data))  // 持久化用户信息
      }
      return res
    },
    /**
     * 注册：调用注册接口，只返回结果不存状态
     */
    async register({ commit }, registerData) {
      return await api.auth.register(registerData)
    },
    /**
     * 获取当前用户信息：刷新用户状态
     */
    async getUserInfo({ commit, state }) {
      if (!state.token) return null
      try {
        const res = await api.auth.getUserInfo()
        if (res.code === 200) {
          commit('SET_USER_INFO', res.data)
          localStorage.setItem('userInfo', JSON.stringify(res.data))
        }
        return res
      } catch {
        // 获取失败（如 token 过期），清除登录状态
        commit('CLEAR_USER_INFO')
        localStorage.removeItem('token')
        localStorage.removeItem('userInfo')
        throw new Error('获取用户信息失败')
      }
    },
    /**
     * 退出登录：清除所有登录状态
     */
    logout({ commit }) {
      localStorage.removeItem('token')
      localStorage.removeItem('userInfo')
      commit('CLEAR_USER_INFO')
    }
  }
}
