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
        <el-tab-pane label="全部" name="all"></el-tab-pane>
        <el-tab-pane label="待处理" name="0"></el-tab-pane>
        <el-tab-pane label="维修中" name="1"></el-tab-pane>
        <el-tab-pane label="已完成" name="2"></el-tab-pane>
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

export default {
  data() {
    return {
      list: [], page: 1, size: 10, total: 0, loading: false,
      activeTab: 'all'
    }
  },
  mounted() {
    this.loadData()
  },
  methods: {
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
    handleTabChange() {
      this.loadData()
    },
    statusType(status) {
      return ['warning', 'primary', 'success'][status]
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
.repair-time {
  font-size: 13px;
  color: var(--c-text-muted);
  display: flex;
  align-items: center;
  gap: 4px;
}

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
