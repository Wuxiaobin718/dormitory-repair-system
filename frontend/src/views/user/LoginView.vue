<template>
  <div class="login-page">
    <div class="login-container">
      <!-- Title above card -->
      <div class="login-title">
        <i class="el-icon-s-tools"></i>
        <h1>校园宿舍报修系统</h1>
      </div>
      <!-- Card -->
      <div class="login-card">
        <el-form :model="form" :rules="rules" ref="formRef" label-position="top" class="login-form">
          <el-form-item label="学号" prop="username">
            <el-input v-model="form.username" placeholder="请输入学号"
              prefix-icon="el-icon-user" size="large" />
          </el-form-item>
          <el-form-item label="密码" prop="password">
            <el-input v-model="form.password" type="password" placeholder="请输入密码"
              prefix-icon="el-icon-lock" size="large" show-password />
          </el-form-item>
          <el-form-item v-if="isRegister" label="确认密码" prop="confirmPassword">
            <el-input v-model="form.confirmPassword" type="password" placeholder="请确认密码"
              prefix-icon="el-icon-lock" size="large" show-password />
          </el-form-item>
          <el-form-item v-if="isRegister" label="姓名" prop="name">
            <el-input v-model="form.name" placeholder="请输入姓名"
              prefix-icon="el-icon-user-solid" size="large" />
          </el-form-item>
          <el-form-item v-if="isRegister" label="手机号" prop="phone">
            <el-input v-model="form.phone" placeholder="请输入手机号（选填）"
              prefix-icon="el-icon-mobile-phone" size="large" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" :loading="loading" size="large" class="login-btn"
              @click="handleSubmit">
              {{ isRegister ? '注册' : '登录' }}
            </el-button>
          </el-form-item>
        </el-form>
        <div class="login-footer">
          <span v-if="!isRegister">
            还没有账号？
            <el-link type="primary" :underline="false" @click="toggleMode">立即注册</el-link>
          </span>
          <span v-else>
            已有账号？
            <el-link type="primary" :underline="false" @click="toggleMode">去登录</el-link>
          </span>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import api from '@/api'

export default {
  data() {
    return {
      isRegister: this.$route.path === '/register',  // 根据路径判断是登录还是注册
      loading: false,
      form: { username: '', password: '', confirmPassword: '', name: '', phone: '' },
      rules: {
        username: [{ required: true, message: '请输入学号', trigger: 'blur' }],
        password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
        confirmPassword: [{ required: true, message: '请确认密码', trigger: 'blur' }],
        name: [{ required: true, message: '请输入姓名', trigger: 'blur' }]
      }
    }
  },
  methods: {
    toggleMode() {
      // 登录/注册模式切换
      this.isRegister = !this.isRegister
      this.$router.replace(this.isRegister ? '/register' : '/login')
    },
    handleSubmit() {
      // 表单校验通过后才提交
      this.$refs.formRef.validate(async valid => {
        if (!valid) return
        this.loading = true
        try {
          if (this.isRegister) {
            // 注册：校验两次密码一致 → 调注册接口 → 切换到登录页
            if (this.form.password !== this.form.confirmPassword) {
              this.$message.error('两次输入的密码不一致')
              this.loading = false
              return
            }
            const res = await this.$store.dispatch('user/register', this.form)
            if (res.code === 200) {
              this.$message.success('注册成功，请登录')
              this.isRegister = false
              this.$router.replace('/login')
              this.form.password = ''
              this.form.confirmPassword = ''
            }
          } else {
            // 登录：调登录接口 → Vuex 存 token → 跳转首页（或来源页）
            await this.$store.dispatch('user/login', {
              username: this.form.username,
              password: this.form.password
            })
            this.$message.success('登录成功')
            const redirect = this.$route.query.redirect || '/'  // 登录前访问的页面
            this.$router.push(redirect)
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
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #409EFF 0%, #66B1FF 50%, #79B8FF 100%);
  position: relative;
  overflow: hidden;
}

.login-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 32px;
  width: 400px;
}

/* Title */
.login-title {
  text-align: center;
  color: #fff;
}
.login-title i {
  font-size: 48px;
  margin-bottom: 12px;
}
.login-title h1 {
  font-size: 28px;
  font-weight: 800;
  margin: 0;
  letter-spacing: 2px;
  text-shadow: 0 2px 8px rgba(0,0,0,0.1);
}

/* Card */
.login-card {
  width: 100%;
  background: #fff;
  border-radius: 16px;
  padding: 40px 36px;
  box-shadow: 0 16px 48px rgba(0, 0, 0, 0.12);
}

.login-form .el-form-item {
  margin-bottom: 22px;
}
.login-form .el-form-item__label {
  font-weight: 600;
  color: var(--c-text);
  padding-bottom: 6px;
}
.login-form .el-input__inner {
  height: 44px;
  font-size: 15px;
}

.login-btn {
  width: 100%;
  height: 44px;
  font-size: 16px;
  font-weight: 600;
  border-radius: 10px;
  margin-top: 4px;
}

.login-footer {
  text-align: center;
  margin-top: 16px;
  font-size: 14px;
  color: var(--c-text-secondary);
}
.login-footer .el-link {
  font-weight: 600;
  font-size: 14px;
}
</style>
