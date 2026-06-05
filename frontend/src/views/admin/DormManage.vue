<template>
  <div class="dorm-manage">
    <div class="page-header">
      <h2 class="section-title">宿舍管理</h2>
      <el-button type="primary" icon="el-icon-plus" @click="showAddDialog">
        添加宿舍
      </el-button>
    </div>

    <!-- Stats overview -->
    <el-row :gutter="20" class="dorm-stats">
      <el-col :span="8">
        <div class="dorm-stat-card">
          <div class="dorm-stat-icon" style="background: linear-gradient(135deg, #409EFF, #66B1FF)">
            <i class="el-icon-office-building"></i>
          </div>
          <div class="dorm-stat-info">
            <div class="dorm-stat-number">{{ buildingCount }}</div>
            <div class="dorm-stat-label">楼栋数</div>
          </div>
        </div>
      </el-col>
      <el-col :span="8">
        <div class="dorm-stat-card">
          <div class="dorm-stat-icon" style="background: linear-gradient(135deg, #5B8FF9, #89BAFF)">
            <i class="el-icon-s-operation"></i>
          </div>
          <div class="dorm-stat-info">
            <div class="dorm-stat-number">{{ floorCount }}</div>
            <div class="dorm-stat-label">楼层数</div>
          </div>
        </div>
      </el-col>
      <el-col :span="8">
        <div class="dorm-stat-card">
          <div class="dorm-stat-icon" style="background: linear-gradient(135deg, #67C23A, #95D97E)">
            <i class="el-icon-s-home"></i>
          </div>
          <div class="dorm-stat-info">
            <div class="dorm-stat-number">{{ allData.length }}</div>
            <div class="dorm-stat-label">总房间数</div>
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- Table -->
    <div class="table-card">
      <el-table :data="pageData" border v-loading="loading" style="width:100%">
        <el-table-column prop="id" label="编号" width="70" />
        <el-table-column label="楼栋" width="150">
          <template slot-scope="scope">
            <div class="dorm-cell"><i class="el-icon-office-building"></i> {{ scope.row.building }}</div>
          </template>
        </el-table-column>
        <el-table-column label="楼层" width="120">
          <template slot-scope="scope">
            <el-tag size="small" class="floor-tag">{{ scope.row.floor }} 楼</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="房间号">
          <template slot-scope="scope">
            <div class="dorm-cell"><i class="el-icon-s-home"></i> {{ scope.row.room }} 室</div>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrap" v-if="allData.length > pageSize">
        <el-pagination
          :current-page="page" :page-size="pageSize" :total="allData.length"
          layout="total, prev, pager, next"
          @current-change="handlePageChange" />
      </div>
    </div>

    <!-- Add Dialog -->
    <el-dialog title="添加宿舍" :visible.sync="dialogVisible" width="420px" top="15vh"
      :close-on-click-modal="false">
      <el-form :model="form" ref="formRef" label-width="80px">
        <el-form-item label="楼栋" prop="building" :rules="[{ required: true, message: '请输入楼栋' }]">
          <el-input v-model="form.building" placeholder="如：1栋、A栋" prefix-icon="el-icon-office-building" />
        </el-form-item>
        <el-form-item label="楼层" prop="floor" :rules="[{ required: true, message: '请输入楼层' }]">
          <el-input-number v-model="form.floor" :min="1" :max="20" style="width:100%" />
        </el-form-item>
        <el-form-item label="房间号" prop="room" :rules="[{ required: true, message: '请输入房间号' }]">
          <el-input v-model="form.room" placeholder="如：101" prefix-icon="el-icon-s-home" />
        </el-form-item>
      </el-form>
      <span slot="footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleAdd">确认添加</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import api from '@/api'

export default {
  data() {
    return {
      allData: [], page: 1, pageSize: 10, loading: false, dialogVisible: false,
      form: { building: '', floor: 1, room: '' }
    }
  },
  computed: {
    pageData() {
      const start = (this.page - 1) * this.pageSize
      return this.allData.slice(start, start + this.pageSize)
    },
    buildingCount() {
      return new Set(this.allData.map(i => i.building)).size
    },
    floorCount() {
      return new Set(this.allData.map(i => i.floor)).size
    }
  },
  mounted() { this.loadData() },
  methods: {
    async loadData() {
      this.loading = true
      try {
        const res = await api.dorm.getList()
        if (res.code === 200) this.allData = res.data
      } finally { this.loading = false }
    },
    handlePageChange(p) {
      this.page = p
    },
    showAddDialog() {
      this.form = { building: '', floor: 1, room: '' }
      this.$nextTick(() => { if (this.$refs.formRef) this.$refs.formRef.clearValidate() })
      this.dialogVisible = true
    },
    async handleAdd() {
      try {
        await this.$refs.formRef.validate()
        const res = await api.dorm.add(this.form)
        if (res.code === 200) {
          this.$message.success('宿舍添加成功')
          this.dialogVisible = false
          this.loadData()
        }
      } catch {}
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
.page-header .section-title { margin: 0; }

/* Stats */
.dorm-stats { margin-bottom: 24px; }
.dorm-stat-card {
  display: flex;
  align-items: center;
  gap: 16px;
  background: #fff;
  padding: 20px 24px;
  border-radius: var(--r-lg);
  box-shadow: var(--shadow-card);
  transition: all 0.3s ease;
}
.dorm-stat-card:hover {
  transform: translateY(-3px);
  box-shadow: var(--shadow-md);
}
.dorm-stat-icon {
  width: 48px; height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}
.dorm-stat-icon i { font-size: 22px; color: #fff; }
.dorm-stat-number {
  font-size: 28px;
  font-weight: 800;
  color: var(--c-text);
  line-height: 1;
}
.dorm-stat-label {
  font-size: 13px;
  color: var(--c-text-secondary);
  margin-top: 4px;
}

/* Table */
.table-card {
  background: #fff;
  border-radius: var(--r-lg);
  padding: 20px;
  box-shadow: var(--shadow-card);
}
.dorm-cell { display: flex; align-items: center; gap: 6px; }
.dorm-cell i { color: var(--c-primary); }
.floor-tag { border-radius: 6px; }

.pagination-wrap {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}
</style>
