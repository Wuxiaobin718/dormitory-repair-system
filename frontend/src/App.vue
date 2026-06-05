<template>
  <div id="app">
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
#app {
  font-family: "Helvetica Neue", Helvetica, "PingFang SC", "Microsoft YaHei", Arial, sans-serif;
  margin: 0;
  padding: 0;
}
</style>
