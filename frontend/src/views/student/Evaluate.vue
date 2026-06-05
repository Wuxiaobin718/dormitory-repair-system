<template>
  <div class="evaluate-page">
    <div class="evaluate-card">
      <div class="evaluate-header">
        <div class="evaluate-icon">
          <i class="el-icon-star-on"></i>
        </div>
        <h2>服务评价</h2>
        <p>请对本次维修服务进行评分</p>
      </div>

      <div class="rate-section">
        <el-rate v-model="form.score" :max="5" :colors="['#F7C948', '#F7C948', '#F7C948']"
          :icon-classes="['el-icon-star-on', 'el-icon-star-on', 'el-icon-star-on']"
          void-icon-class="el-icon-star-off"
          :texts="['非常差', '差', '一般', '好', '非常好']"
          show-text class="big-rate">
        </el-rate>
      </div>

      <el-form :model="form" :rules="rules" ref="formRef" class="evaluate-form">
        <el-form-item prop="content">
          <el-input v-model="form.content" type="textarea" :rows="4"
            placeholder="分享您的维修体验...（选填）" maxlength="300" show-word-limit />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" size="large" :loading="loading" @click="handleSubmit" class="submit-eval-btn">
            <i class="el-icon-circle-check"></i> 提交评价
          </el-button>
          <el-button size="large" @click="$router.push('/repair/my')">稍后再说</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script>
import api from '@/api'

export default {
  data() {
    return {
      form: { repairId: Number(this.$route.params.id), score: 0, content: '' },
      loading: false,
      rules: {
        score: [{ required: true, message: '请评分', trigger: 'change',
          validator: (rule, value, cb) => value > 0 ? cb() : cb(new Error('请评分')) }]
      }
    }
  },
  methods: {
    handleSubmit() {
      this.$refs.formRef.validate(async valid => {
        if (!valid) return
        this.loading = true
        try {
          const res = await api.comment.add(this.form)
          if (res.code === 200) {
            this.$message.success('评价提交成功，感谢您的反馈！')
            this.$router.push('/repair/my')
          }
        } finally {
          this.loading = false
        }
      })
    }
  }
}
</script>

<style scoped>
.evaluate-page {
  max-width: 560px;
  margin: 40px auto;
}

.evaluate-card {
  background: #fff;
  border-radius: var(--r-xl);
  padding: 48px 40px;
  box-shadow: var(--shadow-card);
  text-align: center;
}

.evaluate-header {
  margin-bottom: 32px;
}
.evaluate-icon {
  font-size: 56px;
  color: #F7C948;
  margin-bottom: 12px;
}
.evaluate-header h2 {
  font-size: 24px;
  font-weight: 700;
  color: var(--c-text);
  margin: 0 0 8px;
}
.evaluate-header p {
  font-size: 15px;
  color: var(--c-text-secondary);
  margin: 0;
}

/* Big star rating */
.rate-section {
  margin-bottom: 28px;
  padding: 20px;
  background: var(--c-bg);
  border-radius: var(--r-md);
}
.big-rate {
  display: flex;
  justify-content: center;
  gap: 8px;
}
.big-rate .el-rate__item {
  font-size: 36px !important;
}
.big-rate .el-rate__icon {
  font-size: 36px !important;
  transition: transform 0.2s ease;
}
.big-rate .el-rate__icon.hover {
  transform: scale(1.3);
}
.el-rate__text {
  font-size: 18px;
  font-weight: 600;
  color: var(--c-primary);
  margin-left: 12px;
}

.evaluate-form {
  text-align: left;
}
.evaluate-form .el-textarea__inner {
  border-radius: var(--r-md);
}

.submit-eval-btn {
  width: 100%;
  margin-bottom: 8px;
}
</style>
