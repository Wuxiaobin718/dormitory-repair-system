<template>
  <div id="app">
    <!-- 根组件，只渲染 BasicLayout 布局组件 -->
    <BasicLayout />
  </div>
</template>

<script>
import BasicLayout from './components/BasicLayout.vue'

export default {
  name: 'App',
  components: { BasicLayout }
}
</script>

<style>
/* 全局基础字体样式 */
#app {
  font-family: "Helvetica Neue", Helvetica, "PingFang SC", "Microsoft YaHei", Arial, sans-serif;
  margin: 0;
  padding: 0;
}
</style>
