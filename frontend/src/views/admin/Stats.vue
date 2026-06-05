<template>
  <div class="stats-page">
    <div class="page-header">
      <h2 class="section-title">数据统计</h2>
    </div>

    <!-- Stats cards -->
    <el-row :gutter="24" class="stats-grid">
      <el-col :span="6">
        <div class="stat-card stat-total fade-in-up">
          <div class="stat-card-top">
            <div class="stat-icon-wrap">
              <i class="el-icon-document-copy"></i>
            </div>
            <div class="stat-change">总计</div>
          </div>
          <div class="stat-number">{{ stats.total }}</div>
          <div class="stat-label">总报修数</div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card stat-pending fade-in-up fade-in-up-d1">
          <div class="stat-card-top">
            <div class="stat-icon-wrap">
              <i class="el-icon-time"></i>
            </div>
            <div class="stat-change">待处理</div>
          </div>
          <div class="stat-number">{{ stats.pending }}</div>
          <div class="stat-label">等待维修</div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card stat-progress fade-in-up fade-in-up-d2">
          <div class="stat-card-top">
            <div class="stat-icon-wrap">
              <i class="el-icon-loading"></i>
            </div>
            <div class="stat-change">进行中</div>
          </div>
          <div class="stat-number">{{ stats.inProgress }}</div>
          <div class="stat-label">维修中</div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card stat-completed fade-in-up fade-in-up-d3">
          <div class="stat-card-top">
            <div class="stat-icon-wrap">
              <i class="el-icon-circle-check"></i>
            </div>
            <div class="stat-change">已完成</div>
          </div>
          <div class="stat-number">{{ stats.completed }}</div>
          <div class="stat-label">维修完成</div>
        </div>
      </el-col>
    </el-row>

    <!-- Status distribution -->
    <el-row :gutter="24" class="stats-grid">
      <el-col :span="24">
        <div class="chart-card">
          <h3 class="chart-title">维修状态分布</h3>
          <div class="progress-bars">
            <div class="progress-item">
              <div class="progress-label">
                <span class="progress-name">待处理</span>
                <span class="progress-count">{{ stats.pending }}</span>
              </div>
              <div class="progress-track">
                <div class="progress-fill fill-warning" :style="{ width: pendingPercent + '%' }"></div>
              </div>
            </div>
            <div class="progress-item">
              <div class="progress-label">
                <span class="progress-name">维修中</span>
                <span class="progress-count">{{ stats.inProgress }}</span>
              </div>
              <div class="progress-track">
                <div class="progress-fill fill-primary" :style="{ width: progressPercent + '%' }"></div>
              </div>
            </div>
            <div class="progress-item">
              <div class="progress-label">
                <span class="progress-name">已完成</span>
                <span class="progress-count">{{ stats.completed }}</span>
              </div>
              <div class="progress-track">
                <div class="progress-fill fill-success" :style="{ width: completedPercent + '%' }"></div>
              </div>
            </div>
          </div>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import api from '@/api'

export default {
  data() {
    return {
      stats: { total: 0, pending: 0, inProgress: 0, completed: 0 }
    }
  },
  computed: {
    total() { return this.stats.total || 1 },
    pendingPercent() { return (this.stats.pending / this.total * 100).toFixed(1) },
    progressPercent() { return (this.stats.inProgress / this.total * 100).toFixed(1) },
    completedPercent() { return (this.stats.completed / this.total * 100).toFixed(1) }
  },
  mounted() { this.loadStats() },
  methods: {
    async loadStats() {
      try {
        const [all, pending, progress, completed] = await Promise.all([
          api.repair.getRepairList({ page: 1, size: 1 }),
          api.repair.getRepairList({ page: 1, size: 1, status: 0 }),
          api.repair.getRepairList({ page: 1, size: 1, status: 1 }),
          api.repair.getRepairList({ page: 1, size: 1, status: 2 }),
        ])
        this.stats.total = all.data?.total || 0
        this.stats.pending = pending.data?.total || 0
        this.stats.inProgress = progress.data?.total || 0
        this.stats.completed = completed.data?.total || 0
      } catch {}
    }
  }
}
</script>

<style scoped>
.page-header { margin-bottom: 24px; }

.stats-grid { margin-bottom: 24px; }

/* Stat cards */
.stat-card {
  background: #fff;
  border-radius: var(--r-lg);
  padding: 24px;
  box-shadow: var(--shadow-card);
  transition: all 0.35s ease;
  position: relative;
  overflow: hidden;
}
.stat-card::before {
  content: '';
  position: absolute;
  top: 0; left: 0; right: 0;
  height: 4px;
}
.stat-card:hover {
  transform: translateY(-5px);
  box-shadow: var(--shadow-lg);
}
.stat-total::before { background: linear-gradient(90deg, #409EFF, #66B1FF); }
.stat-pending::before { background: linear-gradient(90deg, #E6A23C, #F5D06E); }
.stat-progress::before { background: linear-gradient(90deg, #5B8FF9, #89BAFF); }
.stat-completed::before { background: linear-gradient(90deg, #67C23A, #95D97E); }

.stat-card-top {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}
.stat-icon-wrap {
  width: 44px; height: 44px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
}
.stat-total .stat-icon-wrap { background: var(--c-primary-lighter); }
.stat-total .stat-icon-wrap i { color: var(--c-primary); font-size: 20px; }
.stat-pending .stat-icon-wrap { background: #FFF3D6; }
.stat-pending .stat-icon-wrap i { color: #D4890B; font-size: 20px; }
.stat-progress .stat-icon-wrap { background: #E3F2FD; }
.stat-progress .stat-icon-wrap i { color: #1976D2; font-size: 20px; }
.stat-completed .stat-icon-wrap { background: #E8FCF9; }
.stat-completed .stat-icon-wrap i { color: #0E9C8A; font-size: 20px; }

.stat-change {
  font-size: 12px;
  font-weight: 600;
  color: var(--c-text-muted);
}
.stat-number {
  font-size: 36px;
  font-weight: 800;
  color: var(--c-text);
  line-height: 1;
  margin-bottom: 4px;
}
.stat-label {
  font-size: 14px;
  color: var(--c-text-secondary);
}

/* Chart card */
.chart-card {
  background: #fff;
  border-radius: var(--r-lg);
  padding: 28px 32px;
  box-shadow: var(--shadow-card);
}
.chart-title {
  font-size: 16px;
  font-weight: 700;
  color: var(--c-text);
  margin: 0 0 24px;
}

.progress-bars {
  display: flex;
  flex-direction: column;
  gap: 20px;
}
.progress-item { }
.progress-label {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
  font-size: 14px;
}
.progress-name { color: var(--c-text); font-weight: 600; }
.progress-count { color: var(--c-text-secondary); font-weight: 500; }

.progress-track {
  height: 10px;
  background: var(--c-bg);
  border-radius: 5px;
  overflow: hidden;
}
.progress-fill {
  height: 100%;
  border-radius: 5px;
  transition: width 0.8s cubic-bezier(0.4, 0, 0.2, 1);
}
.fill-warning { background: linear-gradient(90deg, #E6A23C, #F5D06E); }
.fill-primary { background: linear-gradient(90deg, #409EFF, #89BAFF); }
.fill-success { background: linear-gradient(90deg, #67C23A, #95D97E); }
</style>
