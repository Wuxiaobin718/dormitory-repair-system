<template>
  <div class="my-repairs">
    <div class="page-header">
      <h2 class="section-title">我的报修</h2>
      <el-button type="primary" @click="$router.push('/repair/submit')">
        <i class="el-icon-plus"></i> 新建报修
      </el-button>
    </div>

    <!-- Filter tabs -->
    <div class="filter-bar">
      <el-tabs v-model="activeTab" @tab-click="handleTabChange">
        <el-tab-pane name="all">
          <span slot="label">全部 <el-tag size="mini" type="info" class="tab-badge">{{ stats.total }}</el-tag></span>
        </el-tab-pane>
        <el-tab-pane name="0">
          <span slot="label">待处理 <el-tag size="mini" type="warning" class="tab-badge">{{ stats.pending }}</el-tag></span>
        </el-tab-pane>
        <el-tab-pane name="1">
          <span slot="label">维修中 <el-tag size="mini" type="primary" class="tab-badge">{{ stats.inProgress }}</el-tag></span>
        </el-tab-pane>
        <el-tab-pane name="2">
          <span slot="label">已完成 <el-tag size="mini" type="success" class="tab-badge">{{ stats.completed }}</el-tag></span>
        </el-tab-pane>
      </el-tabs>
    </div>

    <!-- Repair list as cards -->
    <div class="repair-list" v-loading="loading">
      <div v-if="!loading && list.length === 0" class="empty-state">
        <i class="el-icon-document"></i>
        <p>暂无报修记录</p>
        <el-button type="primary" @click="$router.push('/repair/submit')">去报修</el-button>
      </div>

      <div v-for="item in list" :key="item.id" class="repair-card fade-in-up">
        <div class="repair-card-top">
          <div class="repair-id">#{{ item.id }}</div>
          <el-tag :type="statusType(item.status)" size="medium" class="status-tag">
            <i :class="statusIcon(item.status)"></i>
            {{ statusText(item.status) }}
          </el-tag>
        </div>
        <div class="repair-card-body">
          <div class="repair-type">
            <i class="el-icon-warning"></i>
            {{ item.type }}
          </div>
          <p class="repair-desc">{{ item.content }}</p>
          <div class="repair-meta">
            <div class="repair-time">
              <i class="el-icon-time"></i>
              {{ item.createTime }}
            </div>
            <div class="repair-dorm" v-if="item.building">
              <i class="el-icon-s-home"></i>
              {{ item.building }} {{ item.floor }}楼 {{ item.room }}室
            </div>
          </div>
        </div>
        <div class="repair-card-actions" v-if="item.status === 2">
          <el-button type="success" size="small" plain
            @click="$router.push(`/repair/${item.id}/evaluate`)">
            <i class="el-icon-star-on"></i> 服务评价
          </el-button>
        </div>
      </div>
    </div>

    <!-- Pagination -->
    <div class="pagination-wrap" v-if="total > size">
      <el-pagination
        :current-page="page" :page-size="size" :total="total"
        layout="total, prev, pager, next" @current-change="loadData" />
    </div>
  </div>
</template>

<script>
import api from '@/api'
import ws from '@/utils/websocket'

export default {
  data() {
    return {
      list: [], page: 1, size: 10, total: 0, loading: false,
      activeTab: 'all',  // 当前筛选状态：all=全部, 0=待处理, 1=维修中, 2=已完成
      stats: { total: 0, pending: 0, inProgress: 0, completed: 0 },
      wsHandler: null    // WebSocket 监听器引用
    }
  },
  mounted() {
    this.loadData()
    this.loadStats()
    // 监听报修状态变更通知，自动刷新列表
    this.wsHandler = (data) => {
      console.log('[MyRepairs] 收到 STATUS_UPDATE，刷新列表')
      this.loadData(this.page)
      this.loadStats()
    }
    ws.on('STATUS_UPDATE', this.wsHandler)
  },
  beforeDestroy() {
    if (this.wsHandler) ws.off('STATUS_UPDATE', this.wsHandler)
  },
  methods: {
    // 加载报修列表（分页）
    async loadData(p = 1) {
      this.page = p
      this.loading = true
      try {
        const params = { page: this.page, size: this.size }
        if (this.activeTab !== 'all') params.status = Number(this.activeTab)
        const res = await api.repair.getMyRepairs(params)
        if (res.code === 200) {
          this.list = res.data.records
          this.total = res.data.total
        }
      } finally {
        this.loading = false
      }
    },
    // 加载各状态报修数量（Tabs 徽标）
    async loadStats() {
      try {
        const res = await api.repair.getStats()
        if (res.code === 200) this.stats = res.data
      } catch {}
    },
    // 切换筛选 Tab 时重新加载数据
    handleTabChange() {
      this.loadData()
    },
    // 状态对应的 Element UI Tag 类型
    statusType(status) {
      return ['warning', 'primary', 'success'][status]  // 待处理=黄色, 维修中=蓝色, 已完成=绿色
    },
    statusText(status) {
      return ['待处理', '维修中', '已完成'][status]
    },
    statusIcon(status) {
      return ['el-icon-time', 'el-icon-loading', 'el-icon-circle-check'][status]
    }
  }
}
</script>

<style scoped>
.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 24px;
}
.page-header .section-title {
  margin: 0;
}

/* Filter bar */
.filter-bar {
  background: #fff;
  border-radius: var(--r-lg);
  padding: 0 24px;
  margin-bottom: 20px;
  box-shadow: var(--shadow-card);
}
.filter-bar .el-tabs__header {
  margin: 0;
}
.filter-bar .el-tabs__item {
  font-size: 14px;
  font-weight: 500;
  padding: 16px 20px;
  height: auto;
}
.filter-bar .el-tabs__active-bar {
  background: var(--c-primary);
  height: 3px;
}
.filter-bar .el-tabs__item.is-active {
  color: var(--c-primary);
}
.tab-badge { margin-left: 4px; font-weight: 700; border-radius: 10px; }

/* Repair cards */
.repair-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.repair-card {
  background: #fff;
  border-radius: var(--r-lg);
  box-shadow: var(--shadow-card);
  overflow: hidden;
  transition: all 0.3s ease;
  border: 1px solid var(--c-border-light);
}
.repair-card:hover {
  box-shadow: var(--shadow-md);
  transform: translateY(-2px);
}

.repair-card-top {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 24px;
  background: var(--c-bg);
  border-bottom: 1px solid var(--c-border-light);
}
.repair-id {
  font-size: 13px;
  font-weight: 600;
  color: var(--c-text-muted);
}
.status-tag i {
  margin-right: 4px;
}

.repair-card-body {
  padding: 20px 24px;
}
.repair-type {
  font-size: 16px;
  font-weight: 600;
  color: var(--c-text);
  margin-bottom: 8px;
  display: flex;
  align-items: center;
  gap: 6px;
}
.repair-type i {
  color: var(--c-primary);
}
.repair-desc {
  font-size: 14px;
  color: var(--c-text-secondary);
  line-height: 1.6;
  margin: 0 0 12px;
}
.repair-meta {
  display: flex;
  gap: 20px;
}
.repair-time, .repair-dorm {
  font-size: 13px;
  color: var(--c-text-muted);
  display: flex;
  align-items: center;
  gap: 4px;
}
.repair-dorm i { color: var(--c-primary); }

.repair-card-actions {
  padding: 12px 24px;
  border-top: 1px solid var(--c-border-light);
  background: var(--c-bg);
  text-align: right;
}

/* Empty state */
.empty-state {
  text-align: center;
  padding: 80px 20px;
  background: #fff;
  border-radius: var(--r-lg);
  box-shadow: var(--shadow-card);
}
.empty-state i {
  font-size: 48px;
  color: var(--c-text-muted);
  margin-bottom: 16px;
}
.empty-state p {
  font-size: 15px;
  color: var(--c-text-secondary);
  margin: 0 0 20px;
}

.pagination-wrap {
  margin-top: 24px;
  display: flex;
  justify-content: center;
}
</style>
