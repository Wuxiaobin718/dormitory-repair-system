import Vue from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import ElementUI from 'element-ui'                       // Element UI 组件库
import 'element-ui/lib/theme-chalk/index.css'             // Element UI 默认样式
import './styles/theme.css'                               // 全局自定义样式

Vue.use(ElementUI)  // 全局注册 Element UI 所有组件
Vue.config.productionTip = false

/**
 * 应用入口
 * 挂载 Vue 实例，注入 router（路由）和 store（状态管理）
 */
new Vue({
  router,
  store,
  render: h => h(App)
}).$mount('#app')
