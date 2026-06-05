import api from '@/api'

export default {
  namespaced: true,
  state: {
    userInfo: localStorage.getItem('userInfo') ? JSON.parse(localStorage.getItem('userInfo')) : null,
    token: localStorage.getItem('token') || null
  },
  getters: {
    hasUserInfo: state => !!state.userInfo,
    isLoggedIn: state => !!state.token,
    username: state => state.userInfo?.username || '',
    name: state => state.userInfo?.name || '',
    role: state => state.userInfo?.role || 0,
    isAdmin: state => state.userInfo?.role === 1
  },
  mutations: {
    SET_TOKEN(state, token) {
      state.token = token
    },
    SET_USER_INFO(state, userInfo) {
      state.userInfo = userInfo
    },
    CLEAR_USER_INFO(state) {
      state.userInfo = null
      state.token = null
    }
  },
  actions: {
    async login({ commit, dispatch }, loginData) {
      const res = await api.auth.login(loginData)
      if (res.code === 200) {
        commit('SET_TOKEN', res.data.token)
        localStorage.setItem('token', res.data.token)
        commit('SET_USER_INFO', res.data)
        localStorage.setItem('userInfo', JSON.stringify(res.data))
      }
      return res
    },
    async register({ commit }, registerData) {
      return await api.auth.register(registerData)
    },
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
        commit('CLEAR_USER_INFO')
        localStorage.removeItem('token')
        localStorage.removeItem('userInfo')
        throw new Error('获取用户信息失败')
      }
    },
    logout({ commit }) {
      localStorage.removeItem('token')
      localStorage.removeItem('userInfo')
      commit('CLEAR_USER_INFO')
    }
  }
}
