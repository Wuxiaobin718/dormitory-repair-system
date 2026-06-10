<template>
  <div class="profile-page">
    <!-- Profile Card -->
    <div class="profile-card">
      <div class="profile-cover">
        <div class="avatar-wrapper" @click="triggerUpload">
          <div v-if="uploading" class="avatar-uploading"><i class="el-icon-loading"></i></div>
          <img v-else-if="avatarUrl" :src="avatarUrl" class="avatar-img" />
          <div v-else class="avatar-letter">{{ avatarChar }}</div>
          <div class="avatar-overlay"><i class="el-icon-camera"></i><span>更换头像</span></div>
        </div>
        <input ref="fileInput" type="file" accept="image/jpeg,image/png,image/gif,image/webp"
          style="display:none" @change="handleFileChange" />
      </div>
      <div class="profile-info">
        <h2>{{ userInfo.name || userInfo.username }}</h2>
        <span class="profile-role" :class="userInfo.role === 1 ? 'role-admin' : 'role-student'">
          <i :class="userInfo.role === 1 ? 'el-icon-setting' : 'el-icon-user'"></i>
          {{ userInfo.role === 1 ? '管理员' : '学生' }}
        </span>
      </div>
      <div class="profile-details">
        <div class="detail-item">
          <span class="detail-label">学号</span>
          <span class="detail-value">{{ userInfo.username }}</span>
        </div>
        <div class="detail-item">
          <span class="detail-label">姓名</span>
          <span class="detail-value">{{ userInfo.name || '未填写' }}</span>
        </div>
        <div class="detail-item">
          <span class="detail-label">手机号</span>
          <span class="detail-value">{{ userInfo.phone || '未填写' }}</span>
        </div>
        <div class="detail-item">
          <span class="detail-label">注册时间</span>
          <span class="detail-value">{{ userInfo.createTime || '-' }}</span>
        </div>
      </div>
    </div>

    <!-- Change Password Card -->
    <div class="card-change-pwd">
      <h3 class="card-title"><i class="el-icon-key"></i> 修改密码</h3>
      <el-form :model="pwdForm" :rules="pwdRules" ref="pwdFormRef" label-width="100px">
        <el-form-item label="旧密码" prop="oldPassword">
          <el-input v-model="pwdForm.oldPassword" type="password" show-password placeholder="请输入旧密码" />
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input v-model="pwdForm.newPassword" type="password" show-password placeholder="至少 6 位" />
        </el-form-item>
        <el-form-item label="确认新密码" prop="confirmPassword">
          <el-input v-model="pwdForm.confirmPassword" type="password" show-password placeholder="再次输入新密码" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="pwdLoading" @click="handleChangePwd">确认修改</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- Admin Reset Password Card -->
    <div class="card-reset-pwd" v-if="isAdmin">
      <h3 class="card-title"><i class="el-icon-setting"></i> 重置学生密码</h3>
      <p class="reset-hint">输入要重置密码的学号，密码将重置为 <strong>123456</strong></p>
      <el-form :model="resetForm" :rules="resetRules" ref="resetFormRef" label-width="100px">
        <el-form-item label="学号" prop="username">
          <el-input v-model="resetForm.username" placeholder="输入学生学号">
            <el-button slot="append" icon="el-icon-search" @click="handleSearchStudent">查询</el-button>
          </el-input>
        </el-form-item>
        <div v-if="foundStudent" class="found-student">
          <span><i class="el-icon-user"></i> {{ foundStudent.name }}（{{ foundStudent.username }}）</span>
          <el-button type="warning" size="small" :loading="resetLoading" @click="handleResetPwd">
            重置为 123456
          </el-button>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script>
import api from '@/api'

export default {
  data() {
    return {
      // 头像
      uploading: false,
      // 修改密码
      pwdForm: { oldPassword: '', newPassword: '', confirmPassword: '' },
      pwdLoading: false,
      pwdRules: {
        oldPassword: [{ required: true, message: '请输入旧密码', trigger: 'blur' }],
        newPassword: [
          { required: true, message: '请输入新密码', trigger: 'blur' },
          { min: 6, message: '密码至少 6 位', trigger: 'blur' }
        ],
        confirmPassword: [
          { required: true, message: '请确认新密码', trigger: 'blur' },
          { validator: (rule, value, cb) => value === this.pwdForm.newPassword ? cb() : cb(new Error('两次密码不一致')), trigger: 'blur' }
        ]
      },
      // 管理员重置
      resetForm: { username: '' },
      resetRules: { username: [{ required: true, message: '请输入学号', trigger: 'blur' }] },
      resetLoading: false,
      foundStudent: null
    }
  },
  computed: {
    userInfo() { return this.$store.state.user.userInfo || {} },
    isAdmin() { return this.$store.getters['user/isAdmin'] },
    avatarUrl() {
      const url = this.userInfo.avatar
      if (!url) return ''
      if (url.startsWith('http')) return url
      return 'http://localhost:8080' + url
    },
    avatarChar() {
      return (this.userInfo.name || this.userInfo.username || '?').charAt(0).toUpperCase()
    }
  },
  mounted() {
    this.$store.dispatch('user/getUserInfo')
  },
  methods: {
    // ========== 头像 ==========
    triggerUpload() {
      if (this.uploading) return
      this.$refs.fileInput.click()
    },
    handleFileChange(e) {
      const file = e.target.files[0]
      if (!file) return
      const validTypes = ['image/jpeg', 'image/png', 'image/gif', 'image/webp']
      if (!validTypes.includes(file.type)) { this.$message.error('格式不支持'); this.$refs.fileInput.value = ''; return }
      if (file.size > 5 * 1024 * 1024) { this.$message.error('图片不超过 5MB'); this.$refs.fileInput.value = ''; return }
      this.uploadAvatar(file)
    },
    async uploadAvatar(file) {
      this.uploading = true
      try {
        const uploadRes = await api.upload.uploadImage(file)
        if (uploadRes.code !== 200) { this.$message.error('上传失败'); return }
        const res = await api.auth.updateProfile({ avatar: uploadRes.data.url })
        if (res.code === 200) {
          this.$store.commit('user/SET_USER_INFO', res.data)
          localStorage.setItem('userInfo', JSON.stringify(res.data))
          this.$message.success('头像更新成功')
        }
      } catch { this.$message.error('头像上传失败') } finally { this.uploading = false; this.$refs.fileInput.value = '' }
    },
    // ========== 修改密码 ==========
    async handleChangePwd() {
      try {
        await this.$refs.pwdFormRef.validate()
      } catch { return }
      this.pwdLoading = true
      try {
        const res = await api.auth.changePassword({
          oldPassword: this.pwdForm.oldPassword,
          newPassword: this.pwdForm.newPassword
        })
        if (res.code === 200) {
          this.$message.success('密码修改成功，下次登录请使用新密码')
          this.pwdForm = { oldPassword: '', newPassword: '', confirmPassword: '' }
          this.$refs.pwdFormRef.clearValidate()
        }
      } finally { this.pwdLoading = false }
    },
    // ========== 管理员重置密码 ==========
    async handleSearchStudent() {
      try {
        await this.$refs.resetFormRef.validate()
      } catch { return }
      try {
        const res = await api.auth.searchUser(this.resetForm.username)
        if (res.code === 200) {
          this.foundStudent = res.data
        } else {
          this.foundStudent = null
          this.$message.error(res.message || '用户不存在')
        }
      } catch { this.foundStudent = null; this.$message.error('查询失败') }
    },
    async handleResetPwd() {
      if (!this.foundStudent) return
      this.resetLoading = true
      try {
        const res = await api.auth.resetPassword(this.foundStudent.id)
        if (res.code === 200) {
          this.$message.success(`已将 ${this.foundStudent.name} 的密码重置为 123456`)
          this.foundStudent = null
          this.resetForm.username = ''
        }
      } finally { this.resetLoading = false }
    }
  }
}
</script>

<style scoped>
.profile-page {
  max-width: 520px;
  margin: 40px auto;
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.profile-card {
  background: #fff;
  border-radius: var(--r-xl);
  box-shadow: var(--shadow-card);
  overflow: hidden;
}
.profile-cover {
  height: 140px;
  background: linear-gradient(135deg, #409EFF, #66B1FF);
  display: flex;
  align-items: flex-end;
  justify-content: center;
  position: relative;
}
.avatar-wrapper {
  width: 88px; height: 88px; border-radius: 50%; background: #fff;
  display: flex; align-items: center; justify-content: center;
  font-size: 36px; font-weight: 700; color: var(--c-primary);
  box-shadow: 0 4px 16px rgba(0,0,0,0.15); transform: translateY(44px);
  border: 4px solid #fff; cursor: pointer; position: relative; overflow: hidden;
  transition: transform 0.25s ease;
}
.avatar-wrapper:hover { transform: translateY(44px) scale(1.05); }
.avatar-img { width: 100%; height: 100%; object-fit: cover; display: block; }
.avatar-letter { width: 100%; height: 100%; display: flex; align-items: center; justify-content: center; background: #fff; color: var(--c-primary); }
.avatar-uploading { width: 100%; height: 100%; display: flex; align-items: center; justify-content: center; background: rgba(0,0,0,0.05); }
.avatar-uploading i { font-size: 28px; color: var(--c-primary); }
.avatar-overlay { position: absolute; inset: 0; background: rgba(0,0,0,0.45); display: flex; flex-direction: column; align-items: center; justify-content: center; gap: 4px; color: #fff; font-size: 12px; opacity: 0; transition: opacity 0.25s ease; }
.avatar-wrapper:hover .avatar-overlay { opacity: 1; }
.avatar-overlay i { font-size: 20px; }

.profile-info { text-align: center; padding: 52px 24px 24px; }
.profile-info h2 { font-size: 22px; font-weight: 700; color: var(--c-text); margin: 0 0 8px; }
.profile-role { display: inline-flex; align-items: center; gap: 6px; padding: 4px 16px; border-radius: 20px; font-size: 13px; font-weight: 600; }
.role-admin { background: #ECF5FF; color: #409EFF; }
.role-student { background: #F0F9EB; color: #67C23A; }

.profile-details { padding: 20px 24px 28px; }
.detail-item { display: flex; justify-content: space-between; align-items: center; padding: 14px 0; border-bottom: 1px solid var(--c-border-light); }
.detail-item:last-child { border-bottom: none; }
.detail-label { font-size: 14px; color: var(--c-text-secondary); font-weight: 500; }
.detail-value { font-size: 14px; color: var(--c-text); font-weight: 600; }

/* Password cards */
.card-change-pwd, .card-reset-pwd {
  background: #fff;
  border-radius: var(--r-xl);
  box-shadow: var(--shadow-card);
  padding: 28px 32px;
}
.card-title {
  font-size: 16px; font-weight: 700; color: var(--c-text);
  margin: 0 0 20px; padding-bottom: 12px;
  border-bottom: 1px solid var(--c-border-light);
}
.card-title i { margin-right: 8px; color: var(--c-primary); }

.reset-hint {
  font-size: 13px; color: var(--c-text-secondary);
  margin: -8px 0 16px;
}
.reset-hint strong { color: #E6A23C; }

.found-student {
  display: flex; align-items: center; justify-content: space-between;
  padding: 12px 16px; background: #F0F9EB; border-radius: 8px;
  margin-top: 12px;
}
.found-student i { color: var(--c-primary); margin-right: 6px; }
</style>
