<template>
  <div class="repair-manage">
    <div class="page-header">
      <h2 class="section-title">报修管理</h2>
    </div>


    <!-- Filter bar -->
    <div class="filter-card">
      <el-form :inline="true" class="filter-form">
        <el-form-item label="状态筛选">
          <el-select v-model="filterStatus" placeholder="全部状态" clearable @change="loadData(1)" size="medium">
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
        <el-form-item label="报修人">
          <el-input v-model="searchName" placeholder="输入学生姓名搜索" clearable
            prefix-icon="el-icon-search" size="medium" style="width:180px"
            @keyup.enter.native="loadData()" @clear="loadData()" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" size="medium" icon="el-icon-search" @click="loadData()">搜索</el-button>
          <el-button size="medium" @click="resetFilter">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- Table -->
    <div class="table-card">
      <el-table :data="list" border v-loading="loading" style="width:100%">
        <el-table-column prop="id" label="编号" min-width="50" />
        <el-table-column label="报修人" min-width="80">
          <template slot-scope="scope">
            {{ scope.row.studentName || '未知' }}
          </template>
        </el-table-column>
        <el-table-column label="宿舍" min-width="120">
          <template slot-scope="scope">
            <span v-if="scope.row.building">
              {{ scope.row.building }} {{ scope.row.floor }}楼 {{ scope.row.room }}室
            </span>
            <span v-else class="no-img">-</span>
          </template>
        </el-table-column>
        <el-table-column prop="type" label="故障类型" min-width="90" />
        <el-table-column label="状态" width="100" align="center">
          <template slot-scope="scope">
            <el-tag :type="statusType(scope.row.status)" size="small" class="status-tag">
              <i :class="statusIcon(scope.row.status)"></i>
              {{ statusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="报修时间" width="160" />
        <el-table-column label="操作" width="170" align="center">
          <template slot-scope="scope">
            <el-button type="text" size="small" @click="showDetail(scope.row)" class="detail-btn">
              <i class="el-icon-document"></i> 详情
            </el-button>
            <el-button v-if="scope.row.status === 0" type="text" size="small" style="color:#409EFF"
              @click="handleStatus(scope.row.id, 1)">
              接单
            </el-button>
            <el-button v-if="scope.row.status === 1" type="text" size="small" style="color:#67C23A"
              @click="handleStatus(scope.row.id, 2)">
              完成
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

    <!-- Detail Dialog -->
    <el-dialog title="报修详情" :visible.sync="detailDialog" width="620px" top="8vh"
      :close-on-click-modal="false">
      <div v-if="detailData" class="detail-body">

        <!-- Header: ID + Status -->
        <div class="detail-header">
          <span class="detail-id">报修单 #{{ detailData.id }}</span>
          <el-tag :type="statusType(detailData.status)" size="medium">
            <i :class="statusIcon(detailData.status)"></i>
            {{ statusText(detailData.status) }}
          </el-tag>
        </div>

        <!-- Info Grid -->
        <el-row :gutter="20" class="detail-grid">
          <el-col :span="12">
            <div class="detail-field">
              <span class="field-label">报修人</span>
              <span class="field-value"><i class="el-icon-user"></i> {{ detailData.studentName || '未知' }}</span>
            </div>
          </el-col>
          <el-col :span="12">
            <div class="detail-field">
              <span class="field-label">手机号</span>
              <span class="field-value"><i class="el-icon-mobile-phone"></i> {{ detailData.studentPhone || '-' }}</span>
            </div>
          </el-col>
          <el-col :span="12">
            <div class="detail-field">
              <span class="field-label">宿舍</span>
              <span class="field-value" v-if="detailData.building">
                <i class="el-icon-s-home"></i> {{ detailData.building }} {{ detailData.floor }}楼 {{ detailData.room }}室
              </span>
              <span v-else class="field-value">-</span>
            </div>
          </el-col>
          <el-col :span="12">
            <div class="detail-field">
              <span class="field-label">故障类型</span>
              <span class="field-value"><i class="el-icon-warning"></i> {{ detailData.type }}</span>
            </div>
          </el-col>
        </el-row>

        <!-- Description -->
        <div class="detail-section">
          <h4 class="section-label">故障描述</h4>
          <p class="detail-desc">{{ detailData.content || '无描述' }}</p>
        </div>

        <!-- Image -->
        <div class="detail-section" v-if="detailData.img">
          <h4 class="section-label">现场图片</h4>
          <div class="detail-img-wrap">
            <el-image :src="getImageUrl(detailData.img)" style="max-width:100%;max-height:300px;border-radius:8px"
              :preview-src-list="[getImageUrl(detailData.img)]" fit="contain" />
          </div>
        </div>

        <!-- Timeline -->
        <div class="detail-section">
          <h4 class="section-label">处理时间线</h4>
          <div class="timeline">
            <div class="timeline-item">
              <div class="tl-dot tl-dot-info"></div>
              <div class="tl-content">
                <div class="tl-title">提交报修</div>
                <div class="tl-time">{{ detailData.createTime }}</div>
              </div>
            </div>
            <div class="timeline-item" v-if="detailData.status >= 1">
              <div class="tl-dot tl-dot-primary"></div>
              <div class="tl-content">
                <div class="tl-title">管理员已接单</div>
                <div class="tl-time">{{ detailData.adminId ? '已分配管理员' : '-' }}</div>
              </div>
            </div>
            <div class="timeline-item" v-if="detailData.status === 2">
              <div class="tl-dot tl-dot-success"></div>
              <div class="tl-content">
                <div class="tl-title">维修完成</div>
                <div class="tl-time">{{ detailData.finishTime || '-' }}</div>
              </div>
            </div>
            <div class="timeline-item" v-if="detailData.status === 2 && detailComment">
              <div class="tl-dot tl-dot-warning"></div>
              <div class="tl-content">
                <div class="tl-title">学生评价</div>
                <div class="tl-comment">
                  <el-rate :value="detailComment.score" disabled size="small" class="tl-rate" />
                  <span v-if="detailComment.content">— {{ detailComment.content }}</span>
                </div>
                <div class="tl-time">{{ detailComment.createTime }}</div>
              </div>
            </div>
          </div>
        </div>

      </div>
    </el-dialog>
  </div>
</template>

<script>
import api from '@/api'
import ws from '@/utils/websocket'

export default {
  data() {
    return {
      list: [], page: 1, size: 10, total: 0, loading: false,
      filterStatus: null,     // 状态筛选：null=全部, 0=待处理, 1=维修中, 2=已完成
      searchName: '',         // 按学生姓名搜索
      detailDialog: false,    // 详情弹窗
      detailData: null,       // 当前查看的报修详情
      detailComment: null,    // 当前报修的评价
      wsHandler: null         // WebSocket 监听器引用
    }
  },
  mounted() {
    this.loadData()
    // 监听新报修通知，自动刷新列表
    this.wsHandler = (data) => {
      console.log('[RepairManage] 收到 NEW_REPAIR，刷新列表')
      if (this.filterStatus === null || this.filterStatus === 0 || this.filterStatus === '') {
        this.loadData(this.page)
      }
    }
    ws.on('NEW_REPAIR', this.wsHandler)
  },
  beforeDestroy() {
    if (this.wsHandler) ws.off('NEW_REPAIR', this.wsHandler)
  },
  methods: {
    // 加载报修列表
    async loadData(p = 1) {
      this.page = p
      this.loading = true
      try {
        const params = { page: this.page, size: this.size }
        if (this.filterStatus !== null && this.filterStatus !== '') params.status = this.filterStatus
        if (this.searchName) params.studentName = this.searchName
        const res = await api.repair.getRepairList(params)
        if (res.code === 200) {
          this.list = res.data.records
          this.total = res.data.total
        }
      } finally { this.loading = false }
    },
    // 重置筛选
    resetFilter() {
      this.filterStatus = null
      this.searchName = ''
      this.loadData()
    },
    // 管理员接单/完成
    async handleStatus(id, status) {
      const action = status === 1 ? '确认接单？' : '确认完成维修？'
      try {
        await this.$confirm(action, '提示', { type: 'warning', confirmButtonText: '确认', cancelButtonText: '取消' })
        const res = await api.repair.updateStatus({ id, status })
        if (res.code === 200) {
          this.$message.success('状态更新成功')
          this.loadData(this.page)
        }
      } catch {}
    },
    // 打开详情弹窗
    async showDetail(row) {
      this.detailData = row
      this.detailComment = null
      this.detailDialog = true
      // 如果已完成，加载评价
      if (row.status === 2) {
        try {
          const res = await api.comment.getList({ repairId: row.id })
          if (res.code === 200 && res.data && res.data.length > 0) {
            this.detailComment = res.data[0]
          }
        } catch {}
      }
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
  display: inline-block; width: 8px; height: 8px;
  border-radius: 50%; margin-right: 6px;
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
.detail-btn { padding: 0 4px; }
.pagination-wrap { margin-top: 20px; display: flex; justify-content: center; }

/* Detail Dialog */
.detail-body { padding: 4px 0; }

.detail-header {
  display: flex; align-items: center; justify-content: space-between;
  padding-bottom: 20px; border-bottom: 1px solid var(--c-border-light); margin-bottom: 20px;
}
.detail-id { font-size: 18px; font-weight: 700; color: var(--c-text); }

.detail-grid { margin-bottom: 16px; }
.detail-field {
  margin-bottom: 16px;
}
.field-label {
  display: block; font-size: 12px; color: var(--c-text-muted); margin-bottom: 4px;
}
.field-value {
  font-size: 14px; color: var(--c-text); font-weight: 600;
}
.field-value i { margin-right: 4px; color: var(--c-primary); }

.detail-section { margin-bottom: 20px; }
.section-label {
  font-size: 14px; font-weight: 700; color: var(--c-text);
  margin: 0 0 10px; padding-bottom: 8px;
  border-bottom: 1px dashed var(--c-border-light);
}
.detail-desc {
  font-size: 14px; color: var(--c-text-secondary); line-height: 1.7;
  margin: 0; padding: 8px 12px; background: var(--c-bg); border-radius: 8px;
}
.detail-img-wrap {
  background: var(--c-bg); border-radius: 8px; padding: 12px;
  display: flex; justify-content: center;
}

/* Timeline */
.timeline {
  position: relative; padding-left: 24px;
}
.timeline::before {
  content: ''; position: absolute; left: 7px; top: 4px; bottom: 4px;
  width: 2px; background: var(--c-border-light);
}
.timeline-item {
  position: relative; padding-bottom: 20px;
}
.timeline-item:last-child { padding-bottom: 0; }
.tl-dot {
  position: absolute; left: -20px; top: 4px;
  width: 14px; height: 14px; border-radius: 50%; border: 2px solid #fff;
  z-index: 1;
}
.tl-dot-info { background: #909399; }
.tl-dot-primary { background: #409EFF; }
.tl-dot-success { background: #67C23A; }
.tl-dot-warning { background: #E6A23C; }
.tl-content {}
.tl-title { font-size: 14px; font-weight: 600; color: var(--c-text); }
.tl-time { font-size: 12px; color: var(--c-text-muted); margin-top: 2px; }
.tl-comment {
  font-size: 13px; color: var(--c-text-secondary); margin-top: 4px;
  display: flex; align-items: center; gap: 8px;
}
.tl-rate { display: inline-flex; }
</style>
