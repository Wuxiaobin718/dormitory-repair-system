<template>
  <div class="repair-manage">
    <div class="page-header">
      <h2 class="section-title">报修管理</h2>
    </div>

    <!-- Filter bar -->
    <div class="filter-card">
      <el-form :inline="true" class="filter-form">
        <el-form-item label="状态筛选">
          <el-select v-model="filterStatus" placeholder="全部状态" clearable @change="loadData" size="medium">
            <el-option label="待处理" :value="0">
              <span><span class="dot dot-warning"></span> 待处理</span>
            </el-option>
            <el-option label="维修中" :value="1">
              <span><span class="dot dot-primary"></span> 维修中</span>
            </el-option>
            <el-option label="已完成" :value="2">
              <span><span class="dot dot-success"></span> 已完成</span>
            </el-option>
          </el-select>
        </el-form-item>
      </el-form>
    </div>

    <!-- Table -->
    <div class="table-card">
      <el-table :data="list" border v-loading="loading" style="width:100%">
        <el-table-column prop="id" label="编号" width="70" />
        <el-table-column prop="type" label="故障类型" width="110" />
        <el-table-column prop="content" label="故障描述" show-overflow-tooltip />
        <el-table-column label="图片" width="70" align="center">
          <template slot-scope="scope">
            <el-image v-if="scope.row.img" :src="getImageUrl(scope.row.img)" style="width:36px;height:36px;border-radius:6px"
              :preview-src-list="[getImageUrl(scope.row.img)]" />
            <span v-else class="no-img">-</span>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="110" align="center">
          <template slot-scope="scope">
            <el-tag :type="statusType(scope.row.status)" size="medium" class="status-tag">
              <i :class="statusIcon(scope.row.status)"></i>
              {{ statusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="报修时间" width="175" />
        <el-table-column label="操作" width="170" align="center">
          <template slot-scope="scope">
            <el-button v-if="scope.row.status === 0" type="primary" size="small" @click="handleStatus(scope.row.id, 1)"
              class="action-btn">
              <i class="el-icon-check"></i> 接单
            </el-button>
            <el-button v-if="scope.row.status === 1" type="success" size="small" @click="handleStatus(scope.row.id, 2)"
              class="action-btn">
              <i class="el-icon-circle-check"></i> 完成
            </el-button>
            <span v-if="scope.row.status === 2" class="done-label">
              <i class="el-icon-success"></i> 已完成
            </span>
            <el-button v-if="scope.row.status === 2" type="warning" size="small" plain
              @click="showComment(scope.row.id)" class="action-btn">
              <i class="el-icon-star-on"></i> 评价
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrap" v-if="total > size">
        <el-pagination
          :current-page="page" :page-size="size" :total="total"
          layout="total, prev, pager, next" @current-change="loadData" />
      </div>
    </div>

    <!-- Comment Dialog -->
    <el-dialog title="服务评价" :visible.sync="commentDialog" width="480px" top="20vh">
      <div v-if="commentData" class="comment-display">
        <div class="comment-score">
          <span class="comment-label">评分：</span>
          <el-rate v-model="commentData.score" disabled :max="5"
            :colors="['#F7C948', '#F7C948', '#F7C948']"
            :icon-classes="['el-icon-star-on', 'el-icon-star-on', 'el-icon-star-on']"
            void-icon-class="el-icon-star-off">
          </el-rate>
        </div>
        <div class="comment-content" v-if="commentData.content">
          <span class="comment-label">评价内容：</span>
          <p>{{ commentData.content }}</p>
        </div>
        <div class="comment-time">
          <span class="comment-label">评价时间：</span>
          <span>{{ commentData.createTime }}</span>
        </div>
      </div>
      <div v-else class="comment-empty">
        <i class="el-icon-star-off"></i>
        <p>暂无评价</p>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import api from '@/api'

export default {
  data() {
    return {
      list: [], page: 1, size: 10, total: 0, loading: false, filterStatus: null,
      commentDialog: false,
      commentData: null
    }
  },
  mounted() { this.loadData() },
  methods: {
    async loadData(p = 1) {
      this.page = p
      this.loading = true
      try {
        const params = { page: this.page, size: this.size }
        if (this.filterStatus !== null && this.filterStatus !== '') params.status = this.filterStatus
        const res = await api.repair.getRepairList(params)
        if (res.code === 200) {
          this.list = res.data.records
          this.total = res.data.total
        }
      } finally { this.loading = false }
    },
    async handleStatus(id, status) {
      const action = status === 1 ? '确认接单？' : '确认完成维修？'
      try {
        await this.$confirm(action, '提示', { type: 'warning', confirmButtonText: '确认', cancelButtonText: '取消' })
        const res = await api.repair.updateStatus({ id, status })
        if (res.code === 200) {
          this.$message.success('状态更新成功')
          this.loadData(this.page)
        }
      } catch { /* cancelled */ }
    },
    async showComment(repairId) {
      this.commentData = null
      this.commentDialog = true
      try {
        const res = await api.comment.getList({ repairId })
        if (res.code === 200 && res.data && res.data.length > 0) {
          this.commentData = res.data[0]
        }
      } catch {}
    },
    statusType(status) { return ['warning', 'primary', 'success'][status] },
    statusText(status) { return ['待处理', '维修中', '已完成'][status] },
    statusIcon(status) { return ['el-icon-time', 'el-icon-loading', 'el-icon-circle-check'][status] },
    getImageUrl(img) {
      if (!img) return ''
      if (img.startsWith('http')) return img
      return 'http://localhost:8080' + img
    }
  }
}
</script>

<style scoped>
.page-header { margin-bottom: 20px; }

.filter-card {
  background: #fff;
  border-radius: var(--r-lg);
  padding: 16px 24px;
  margin-bottom: 20px;
  box-shadow: var(--shadow-card);
}
.filter-form .el-form-item { margin-bottom: 0; }

.dot {
  display: inline-block;
  width: 8px; height: 8px;
  border-radius: 50%;
  margin-right: 6px;
}
.dot-warning { background: #E6A23C; }
.dot-primary { background: #409EFF; }
.dot-success { background: #67C23A; }

.table-card {
  background: #fff;
  border-radius: var(--r-lg);
  padding: 20px;
  box-shadow: var(--shadow-card);
}

.no-img { color: var(--c-text-muted); }

.status-tag i { margin-right: 4px; }

.action-btn { border-radius: 6px; }

.done-label {
  font-size: 13px;
  color: var(--c-text-muted);
  font-weight: 500;
  margin-right: 6px;
}
.done-label i { margin-right: 4px; }

.pagination-wrap {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

/* Comment Dialog */
.comment-display {
  padding: 8px 0;
}
.comment-score {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 20px;
}
.comment-label {
  font-size: 14px;
  font-weight: 600;
  color: var(--c-text);
  white-space: nowrap;
}
.comment-content {
  margin-bottom: 16px;
}
.comment-content p {
  font-size: 14px;
  color: var(--c-text-secondary);
  line-height: 1.6;
  margin: 8px 0 0;
  padding: 12px 16px;
  background: var(--c-bg);
  border-radius: 8px;
}
.comment-time {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 13px;
  color: var(--c-text-muted);
}
.comment-empty {
  text-align: center;
  padding: 40px 0;
  color: var(--c-text-muted);
}
.comment-empty i {
  font-size: 40px;
  margin-bottom: 12px;
}
.comment-empty p {
  margin: 0;
  font-size: 14px;
}
</style>
