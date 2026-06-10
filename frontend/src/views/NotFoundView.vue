<template>
  <!-- 404 页面：路由匹配不到时显示 -->
  <div class="not-found-page">
    <div class="not-found-content">
      <div class="not-found-code">404</div>
      <div class="not-found-icon">
        <i class="el-icon-s-platform"></i>
      </div>
      <h2>页面不存在</h2>
      <p>您访问的页面可能已被移除或地址有误</p>
      <el-button type="primary" size="large" @click="$router.push('/')">
        <i class="el-icon-back"></i> 返回首页
      </el-button>
    </div>
  </div>
</template>

<style scoped>
.not-found-page {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 60vh;
}

.not-found-content {
  text-align: center;
  position: relative;
}

.not-found-code {
  font-size: 120px;
  font-weight: 900;
  color: var(--c-primary-lighter);
  line-height: 1;
  margin-bottom: -40px;
  position: relative;
  z-index: 0;
  letter-spacing: 8px;
}

.not-found-icon {
  position: relative;
  z-index: 1;
  margin-bottom: 16px;
}
.not-found-icon i {
  font-size: 56px;
  color: var(--c-primary);
  opacity: 0.7;
}

.not-found-content h2 {
  font-size: 24px;
  font-weight: 700;
  color: var(--c-text);
  margin: 0 0 8px;
}

.not-found-content p {
  font-size: 15px;
  color: var(--c-text-secondary);
  margin: 0 0 32px;
}
</style>
