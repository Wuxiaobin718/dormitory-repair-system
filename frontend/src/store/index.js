import Vue from 'vue'
import Vuex from 'vuex'
import user from './modules/user'  // 用户模块（登录状态、token、个人信息）

Vue.use(Vuex)

/**
 * Vuex 根 Store
 * 按功能拆分为多个模块（module），目前只有 user 模块
 * 后续可以添加 repair、dorm 等模块来管理各自的状态
 */
export default new Vuex.Store({
  state: {},
  getters: {},
  mutations: {},
  actions: {},
  modules: { user }
})
