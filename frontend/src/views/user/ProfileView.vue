<template>
  <div class="profile-page">
    <div class="profile-card">
      <div class="profile-cover">
        <!-- Avatar upload trigger -->
        <div class="avatar-wrapper" @click="triggerUpload">
          <div v-if="uploading" class="avatar-uploading">
            <i class="el-icon-loading"></i>
          </div>
          <img v-else-if="avatarUrl" :src="avatarUrl" class="avatar-img" />
          <div v-else class="avatar-letter">
            {{ avatarChar }}
          </div>
          <div class="avatar-overlay">
            <i class="el-icon-camera"></i>
            <span>更换头像</span>
          </div>
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
  </div>
</template>

<script>
import api from '@/api'

export default {
  data() {
    return {
      uploading: false
    }
  },
  computed: {
    userInfo() {
      return this.$store.state.user.userInfo || {}
    },
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
    triggerUpload() {
      if (this.uploading) return
      this.$refs.fileInput.click()
    },
    handleFileChange(e) {
      const file = e.target.files[0]
      if (!file) return

      // Validate file type
      const validTypes = ['image/jpeg', 'image/png', 'image/gif', 'image/webp']
      if (!validTypes.includes(file.type)) {
        this.$message.error('请上传 JPG、PNG、GIF 或 WebP 格式的图片')
        this.$refs.fileInput.value = ''
        return
      }

      // Validate file size (5MB)
      if (file.size > 5 * 1024 * 1024) {
        this.$message.error('图片大小不能超过 5MB')
        this.$refs.fileInput.value = ''
        return
      }

      this.uploadAvatar(file)
    },
    async uploadAvatar(file) {
      this.uploading = true
      try {
        const uploadRes = await api.upload.uploadImage(file)
        if (uploadRes.code !== 200) {
          this.$message.error('头像上传失败')
          return
        }

        const avatarPath = uploadRes.data.url
        const res = await api.auth.updateProfile({ avatar: avatarPath })
        if (res.code === 200) {
          // Update store with new user info (includes avatar)
          this.$store.commit('user/SET_USER_INFO', res.data)
          localStorage.setItem('userInfo', JSON.stringify(res.data))
          this.$message.success('头像更新成功')
        }
      } catch {
        this.$message.error('头像上传失败，请重试')
      } finally {
        this.uploading = false
        this.$refs.fileInput.value = ''
      }
    }
  }
}
</script>

<style scoped>
.profile-page {
  max-width: 480px;
  margin: 40px auto;
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

/* Avatar */
.avatar-wrapper {
  width: 88px;
  height: 88px;
  border-radius: 50%;
  background: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 36px;
  font-weight: 700;
  color: var(--c-primary);
  box-shadow: 0 4px 16px rgba(0,0,0,0.15);
  transform: translateY(44px);
  border: 4px solid #fff;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: transform 0.25s ease;
}
.avatar-wrapper:hover {
  transform: translateY(44px) scale(1.05);
}

.avatar-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}

.avatar-letter {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #fff;
  color: var(--c-primary);
}

.avatar-uploading {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(0,0,0,0.05);
}
.avatar-uploading i {
  font-size: 28px;
  color: var(--c-primary);
}

.avatar-overlay {
  position: absolute;
  inset: 0;
  background: rgba(0, 0, 0, 0.45);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 4px;
  color: #fff;
  font-size: 12px;
  opacity: 0;
  transition: opacity 0.25s ease;
}
.avatar-wrapper:hover .avatar-overlay {
  opacity: 1;
}
.avatar-overlay i {
  font-size: 20px;
}

/* Info */
.profile-info {
  text-align: center;
  padding: 52px 24px 24px;
}
.profile-info h2 {
  font-size: 22px;
  font-weight: 700;
  color: var(--c-text);
  margin: 0 0 8px;
}

.profile-role {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 4px 16px;
  border-radius: 20px;
  font-size: 13px;
  font-weight: 600;
}
.role-admin { background: #ECF5FF; color: #409EFF; }
.role-student { background: #F0F9EB; color: #67C23A; }

/* Details */
.profile-details {
  padding: 20px 24px 28px;
}
.detail-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 14px 0;
  border-bottom: 1px solid var(--c-border-light);
}
.detail-item:last-child { border-bottom: none; }
.detail-label {
  font-size: 14px;
  color: var(--c-text-secondary);
  font-weight: 500;
}
.detail-value {
  font-size: 14px;
  color: var(--c-text);
  font-weight: 600;
}
</style>
