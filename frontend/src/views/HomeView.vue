<template>
  <div class="home">
    <!-- Hero Section -->
    <section class="hero-section">
      <div class="hero-content">
        <div class="hero-badge fade-in-up">校园服务 · 一键报修</div>
        <h1 class="hero-title fade-in-up fade-in-up-d1">
          宿舍报修维修<br><span class="highlight">管理系统</span>
        </h1>
        <p class="hero-desc fade-in-up fade-in-up-d2">
          在线报修、进度跟踪、服务评价 — 让宿舍维修更高效、更透明
        </p>
        <div class="hero-actions fade-in-up fade-in-up-d3">
          <template v-if="!token">
            <el-button type="primary" size="large" @click="$router.push('/register')" class="hero-btn">
              立即注册
              <i class="el-icon-right"></i>
            </el-button>
            <el-button plain size="large" @click="$router.push('/login')" class="hero-btn-secondary">登录系统</el-button>
          </template>
          <template v-else>
            <el-button v-if="!isAdmin" type="primary" size="large" @click="$router.push('/repair/submit')" class="hero-btn">
              我要报修
              <i class="el-icon-right"></i>
            </el-button>
            <el-button v-else type="primary" size="large" @click="$router.push('/admin/repairs')" class="hero-btn">
              进入管理
              <i class="el-icon-right"></i>
            </el-button>
          </template>
        </div>
      </div>
    </section>

    <!-- Feature Cards -->
    <section class="features-section">
      <h2 class="section-title">核心功能</h2>
      <div class="features-grid">
        <div class="feature-card fade-in-up" v-for="(item, idx) in features" :key="idx"
             :class="`fade-in-up-d${idx + 1}`">
          <div class="feature-icon" :style="{ background: item.bg }">
            <i :class="item.icon"></i>
          </div>
          <h3>{{ item.title }}</h3>
          <p>{{ item.desc }}</p>
        </div>
      </div>
    </section>

    <!-- Quick Stats -->
    <section class="stats-section" v-if="token">
      <el-row :gutter="24">
        <el-col :span="8" v-for="(stat, idx) in stats" :key="idx">
          <div class="stat-card fade-in-up" :class="`fade-in-up-d${idx + 1}`">
            <div class="stat-icon" :style="{ background: stat.bg }">
              <i :class="stat.icon"></i>
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ stat.value }}</div>
              <div class="stat-label">{{ stat.label }}</div>
            </div>
          </div>
        </el-col>
      </el-row>
    </section>
  </div>
</template>

<script>
import api from '@/api'

export default {
  data() {
    return {
      features: [
        { icon: 'el-icon-circle-plus-outline', title: '在线报修', desc: '填写故障信息，上传图片，一键提交报修申请',
          bg: 'linear-gradient(135deg, #409EFF, #66B1FF)' },
        { icon: 'el-icon-refresh', title: '进度跟踪', desc: '实时查看维修状态：待处理 → 维修中 → 已完成',
          bg: 'linear-gradient(135deg, #E6A23C, #F5D06E)' },
        { icon: 'el-icon-star-on', title: '服务评价', desc: '维修完成后对服务进行评分和反馈，持续提升质量',
          bg: 'linear-gradient(135deg, #67C23A, #95D97E)' },
      ],
      stats: [
        { icon: 'el-icon-document-copy', label: '总报修数', value: 0,
          bg: 'linear-gradient(135deg, #409EFF, #66B1FF)' },
        { icon: 'el-icon-time', label: '待处理', value: 0,
          bg: 'linear-gradient(135deg, #E6A23C, #F5D06E)' },
        { icon: 'el-icon-circle-check', label: '已完成', value: 0,
          bg: 'linear-gradient(135deg, #67C23A, #95D97E)' },
      ]
    }
  },
  computed: {
    token() { return this.$store.state.user.token },
    isAdmin() { return this.$store.getters['user/isAdmin'] }
  },
  mounted() {
    if (this.token) this.loadStats()
  },
  methods: {
    async loadStats() {
      try {
        const [all, pending, completed] = await Promise.all([
          api.repair.getRepairList({ page: 1, size: 1 }),
          api.repair.getRepairList({ page: 1, size: 1, status: 0 }),
          api.repair.getRepairList({ page: 1, size: 1, status: 2 }),
        ])
        this.stats[0].value = all.data?.total || 0
        this.stats[1].value = pending.data?.total || 0
        this.stats[2].value = completed.data?.total || 0
      } catch {}
    }
  }
}
</script>

<style scoped>
.home {
  padding: 0;
}

/* ===== Hero Section ===== */
.hero-section {
  position: relative;
  min-height: 400px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #f0f7ff 0%, #e8f4fd 50%, #f0f0ff 100%);
  border-radius: 20px;
  overflow: hidden;
  margin-bottom: 40px;
  padding: 60px 20px;
}

.hero-content {
  position: relative;
  text-align: center;
  z-index: 1;
  max-width: 700px;
}

.hero-badge {
  display: inline-block;
  background: #409EFF;
  padding: 6px 20px;
  border-radius: 20px;
  font-size: 14px;
  color: #fff;
  font-weight: 500;
  margin-bottom: 20px;
}

.hero-title {
  font-size: 44px;
  font-weight: 800;
  color: #1e293b;
  line-height: 1.2;
  margin: 0 0 16px;
}

.hero-title .highlight {
  color: #409EFF;
}

.hero-desc {
  font-size: 18px;
  color: #64748b;
  margin: 0 0 32px;
  line-height: 1.6;
}

.hero-actions {
  display: flex;
  gap: 16px;
  justify-content: center;
}

.hero-btn {
  padding: 12px 28px;
  font-size: 16px;
  border-radius: 10px;
}

.hero-btn-secondary {
  padding: 12px 28px;
  font-size: 16px;
  border-radius: 10px;
  border: 1.5px solid #dcdfe6;
  color: #64748b;
}
.hero-btn-secondary:hover {
  border-color: #409EFF;
  color: #409EFF;
}

/* ===== Features Section ===== */
.features-section {
  margin-bottom: 40px;
}
.features-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 24px;
}

.feature-card {
  background: #fff;
  border-radius: var(--r-lg);
  padding: 36px 28px;
  position: relative;
  overflow: hidden;
  transition: all 0.35s ease;
  box-shadow: var(--shadow-card);
  cursor: default;
}
.feature-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 5px 15px rgba(0,0,0,0.1);
}

.feature-icon {
  width: 56px;
  height: 56px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 20px;
}
.feature-icon i {
  font-size: 28px;
  color: #fff;
}

.feature-card h3 {
  font-size: 18px;
  font-weight: 700;
  color: var(--c-text);
  margin: 0 0 10px;
}
.feature-card p {
  font-size: 14px;
  color: var(--c-text-secondary);
  line-height: 1.6;
  margin: 0;
}

/* ===== Stats Section ===== */
.stats-section {
  margin-bottom: 40px;
}

.stat-card {
  display: flex;
  align-items: center;
  gap: 20px;
  background: #fff;
  padding: 24px;
  border-radius: var(--r-lg);
  box-shadow: var(--shadow-card);
  transition: all 0.35s ease;
}
.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-md);
}

.stat-icon {
  width: 52px;
  height: 52px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}
.stat-icon i {
  font-size: 24px;
  color: #fff;
}

.stat-number {
  font-size: 32px;
  font-weight: 800;
  color: var(--c-text);
  line-height: 1;
  margin-bottom: 4px;
}
.stat-label {
  font-size: 14px;
  color: var(--c-text-secondary);
}

/* ===== Responsive ===== */
@media (max-width: 768px) {
  .hero-title { font-size: 32px; }
  .features-grid { grid-template-columns: 1fr; }
  .stats-section .el-col { margin-bottom: 16px; }
}
</style>
