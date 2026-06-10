<template>
  <div class="notif-page">
    <div class="page-header">
      <h2 class="section-title">消息通知</h2>
      <el-button type="text" icon="el-icon-check" @click="handleMarkAll" v-if="unreadCount > 0">
        全部标为已读
      </el-button>
    </div>

    <div class="notif-card">
      <el-tabs v-model="activeTab" @tab-click="loadData">
        <el-tab-pane label="全部" name="all"></el-tab-pane>
        <el-tab-pane :label="`未读${unreadCount ? '(' + unreadCount + ')' : ''}`" name="unread"></el-tab-pane>
      </el-tabs>

      <div v-loading="loading" class="notif-list">
        <div v-if="list.length === 0" class="notif-empty">
          <i class="el-icon-bell"></i>
          <p>暂无通知</p>
        </div>
        <div v-for="n in list" :key="n.id" class="notif-item"
          :class="{ 'notif-unread': !n.isRead }" @click="handleClick(n)">
          <div class="notif-left">
            <div class="notif-icon" :class="'notif-icon-' + typeClass(n.type)">
              <i :class="typeIcon(n.type)"></i>
            </div>
          </div>
          <div class="notif-body">
            <div class="notif-title-row">
              <span class="notif-item-title">{{ n.title }}</span>
              <el-tag v-if="!n.isRead" size="mini" type="danger" class="notif-tag">新</el-tag>
            </div>
            <div class="notif-item-msg">{{ n.message }}</div>
            <div class="notif-item-time">{{ n.createTime }}</div>
          </div>
        </div>
      </div>

      <div class="pagination-wrap" v-if="total > size">
        <el-pagination :current-page="page" :page-size="size" :total="total"
          layout="total, prev, pager, next" @current-change="loadData" />
      </div>
    </div>
  </div>
</template>

<script>
import api from '@/api'

export default {
  data() {
    return {
      list: [], page: 1, size: 20, total: 0, loading: false,
      activeTab: 'all', unreadCount: 0
    }
  },
  mounted() {
    this.loadData()
    this.fetchUnread()
  },
  methods: {
    async loadData(p) {
      if (typeof p === 'number') this.page = p
      this.loading = true
      try {
        const params = { page: this.page, size: this.size, isRead: this.activeTab === 'unread' ? 0 : undefined }
        const res = await api.notification.getList(params)
        if (res.code === 200) {
          this.list = res.data.records
          this.total = res.data.total
        }
      } finally { this.loading = false }
    },
    async fetchUnread() {
      const res = await api.notification.getUnreadCount()
      if (res.code === 200) this.unreadCount = res.data.count || 0
    },
    async handleClick(n) {
      if (!n.isRead) {
        await api.notification.markRead(n.id)
        n.isRead = 1
        this.unreadCount = Math.max(0, this.unreadCount - 1)
      }
      if (n.type === 'STATUS_UPDATE') this.$router.push('/repair/my').catch(() => {})
      else this.$router.push('/admin/repairs').catch(() => {})
    },
    async handleMarkAll() {
      await api.notification.markAllRead()
      this.list.forEach(n => { n.isRead = 1 })
      this.unreadCount = 0
      this.$message.success('已全部标为已读')
    },
    typeClass(type) {
      return { NEW_REPAIR: 'info', STATUS_UPDATE: 'primary', NEW_COMMENT: 'warning' }[type] || 'info'
    },
    typeIcon(type) {
      return { NEW_REPAIR: 'el-icon-document-copy', STATUS_UPDATE: 'el-icon-refresh', NEW_COMMENT: 'el-icon-star-on' }[type] || 'el-icon-bell'
    }
  }
}
</script>

<style scoped>
.notif-page { max-width: 720px; margin: 0 auto; }
.page-header { display: flex; align-items: center; justify-content: space-between; margin-bottom: 20px; }
.notif-card { background: #fff; border-radius: var(--r-lg); box-shadow: var(--shadow-card); padding: 0 24px 20px; }

.notif-list { min-height: 200px; }
.notif-empty { text-align: center; padding: 60px 0; color: var(--c-text-muted); }
.notif-empty i { font-size: 40px; margin-bottom: 12px; display: block; }
.notif-empty p { margin: 0; font-size: 14px; }

.notif-item {
  display: flex; gap: 14px; padding: 16px 0; cursor: pointer;
  border-bottom: 1px solid var(--c-border-light); transition: background 0.2s;
}
.notif-item:last-child { border-bottom: none; }
.notif-item:hover { background: #f8fafc; margin: 0 -24px; padding-left: 24px; padding-right: 24px; }
.notif-unread { background: #F0F7FF; margin: 0 -24px; padding-left: 24px; padding-right: 24px; }
.notif-unread:hover { background: #E3F2FD; }

.notif-left { flex-shrink: 0; }
.notif-icon {
  width: 36px; height: 36px; border-radius: 50%; display: flex;
  align-items: center; justify-content: center; font-size: 16px; color: #fff;
}
.notif-icon-info { background: #909399; }
.notif-icon-primary { background: #409EFF; }
.notif-icon-warning { background: #E6A23C; }

.notif-body { flex: 1; min-width: 0; }
.notif-title-row { display: flex; align-items: center; gap: 8px; }
.notif-item-title { font-size: 14px; font-weight: 700; color: var(--c-text); }
.notif-tag { flex-shrink: 0; }
.notif-item-msg { font-size: 13px; color: var(--c-text-secondary); margin: 4px 0; line-height: 1.4; }
.notif-item-time { font-size: 12px; color: var(--c-text-muted); }

.pagination-wrap { margin-top: 16px; display: flex; justify-content: center; }
</style>
