<template>
  <div class="admin-dashboard">
    <!-- 页面标题区 -->
    <div class="page-header">
      <h1 class="page-title">{{ greeting }}，{{ userStore.displayName() }}</h1>
      <p class="page-subtitle">管理后台 - 平台数据总览与系统管理</p>
    </div>

    <!-- 核心指标卡片 -->
    <div class="stats-grid">
      <div class="stat-card primary">
        <div class="stat-icon">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
            <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/>
            <circle cx="9" cy="7" r="4"/>
            <path d="M23 21v-2a4 4 0 0 0-3-3.87M16 3.13a4 4 0 0 1 0 7.75"/>
          </svg>
        </div>
        <div class="stat-content">
          <div class="stat-value">10,245</div>
          <div class="stat-label">注册用户</div>
          <div class="stat-trend positive">
            <el-icon><Top /></el-icon>
            <span>+12.5%</span>
          </div>
        </div>
      </div>

      <div class="stat-card success">
        <div class="stat-icon">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
            <circle cx="9" cy="21" r="1"/>
            <circle cx="20" cy="21" r="1"/>
            <path d="M1 1h4l2.68 13.39a2 2 0 002 1.61h9.72a2 2 0 002-1.61L23 6H6"/>
          </svg>
        </div>
        <div class="stat-content">
          <div class="stat-value">5,678</div>
          <div class="stat-label">在售商品</div>
        </div>
      </div>

      <div class="stat-card warning">
        <div class="stat-icon">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
            <path d="M12 2v20M17 5H9.5a3.5 3.5 0 000 7h5a3.5 3.5 0 010 7H6"/>
            <circle cx="12" cy="12" r="2" fill="currentColor"/>
          </svg>
        </div>
        <div class="stat-content">
          <div class="stat-value">¥ 12.8M</div>
          <div class="stat-label">融资总额</div>
        </div>
      </div>

      <div class="stat-card info">
        <div class="stat-icon">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
            <circle cx="12" cy="8" r="3"/>
            <path d="M8 21v-5a4 4 0 014-4M8 21h8M12 12v5M8 10l2-2M16 10l-2-2"/>
          </svg>
        </div>
        <div class="stat-content">
          <div class="stat-value">528</div>
          <div class="stat-label">认证专家</div>
        </div>
      </div>
    </div>

    <!-- 数据概览图表 -->
    <div class="content-card">
      <div class="card-header">
        <h3 class="card-title">平台数据趋势</h3>
        <div class="time-filter">
          <el-button size="small" @click="handleTimeFilter('day')">日</el-button>
          <el-button size="small" type="primary" @click="handleTimeFilter('week')">周</el-button>
          <el-button size="small" @click="handleTimeFilter('month')">月</el-button>
        </div>
      </div>
      <div class="chart-placeholder">
        <div class="chart-info">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
            <path d="M3 3v18h18"/>
            <path d="M18.7 8l-5.1 5.2-2.8-2.7L7 14.3"/>
          </svg>
          <p>数据图表区域</p>
          <p class="chart-hint">展示平台核心指标趋势分析</p>
        </div>
      </div>
    </div>

    <!-- 主要内容区 -->
    <div class="content-grid">
      <!-- 待处理事项 -->
      <div class="content-card">
        <div class="card-header">
          <h3 class="card-title">待处理事项</h3>
          <span class="badge-count">12</span>
        </div>
        <div class="pending-list">
          <div class="pending-item urgent">
            <div class="pending-icon">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
                <circle cx="12" cy="12" r="10"/>
                <path d="M12 8v4M12 16h.01"/>
              </svg>
            </div>
            <div class="pending-content">
              <div class="pending-title">3条商品审核待处理</div>
              <div class="pending-time">紧急</div>
            </div>
            <el-button type="primary" size="small" @click="handlePendingAction('product')">处理</el-button>
          </div>
          <div class="pending-item warning">
            <div class="pending-icon">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
                <path d="M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z"/>
                <path d="M12 9v4M12 17h.01"/>
              </svg>
            </div>
            <div class="pending-content">
              <div class="pending-title">5个用户投诉待回复</div>
              <div class="pending-time">今天</div>
            </div>
            <el-button type="primary" size="small" @click="handlePendingAction('complaint')">查看</el-button>
          </div>
          <div class="pending-item">
            <div class="pending-icon">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
                <path d="M16 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/>
                <circle cx="8.5" cy="7" r="4"/>
                <path d="M20 8v6M23 11h-6"/>
              </svg>
            </div>
            <div class="pending-content">
              <div class="pending-title">4个专家申请待审核</div>
              <div class="pending-time">本周</div>
            </div>
            <el-button type="primary" size="small" @click="handlePendingAction('expert')">审核</el-button>
          </div>
        </div>
      </div>

      <!-- 快捷入口 -->
      <div class="content-card">
        <div class="card-header">
          <h3 class="card-title">管理入口</h3>
        </div>
        <div class="admin-entries">
          <router-link to="/admin/users" class="admin-entry">
            <div class="entry-icon">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
                <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/>
                <circle cx="9" cy="7" r="4"/>
                <path d="M23 21v-2a4 4 0 0 0-3-3.87M16 3.13a4 4 0 0 1 0 7.75"/>
              </svg>
            </div>
            <span>用户管理</span>
          </router-link>
          <router-link to="/admin/products" class="admin-entry">
            <div class="entry-icon">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
                <circle cx="9" cy="21" r="1"/>
                <circle cx="20" cy="21" r="1"/>
                <path d="M1 1h4l2.68 13.39a2 2 0 002 1.61h9.72a2 2 0 002-1.61L23 6H6"/>
              </svg>
            </div>
            <span>商品管理</span>
          </router-link>
          <router-link to="/admin/finances" class="admin-entry">
            <div class="entry-icon">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
                <path d="M12 2v20M17 5H9.5a3.5 3.5 0 000 7h5a3.5 3.5 0 010 7H6"/>
                <circle cx="12" cy="12" r="2" fill="currentColor"/>
              </svg>
            </div>
            <span>融资管理</span>
          </router-link>
          <router-link to="/admin/experts" class="admin-entry">
            <div class="entry-icon">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
                <circle cx="12" cy="8" r="3"/>
                <path d="M8 21v-5a4 4 0 014-4M8 21h8M12 12v5M8 10l2-2M16 10l-2-2"/>
              </svg>
            </div>
            <span>专家管理</span>
          </router-link>
          <router-link to="/admin/banners" class="admin-entry">
            <div class="entry-icon">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
                <rect x="3" y="3" width="18" height="18" rx="2"/>
                <circle cx="8.5" cy="8.5" r="1.5"/>
                <path d="M21 15l-5-5L5 21"/>
              </svg>
            </div>
            <span>轮播图</span>
          </router-link>
        </div>
      </div>
    </div>

    <!-- 最新活动 -->
    <div class="content-card">
      <div class="card-header">
        <h3 class="card-title">最新活动</h3>
      </div>
      <div class="activity-list">
        <div v-for="activity in recentActivities" :key="activity.id" class="activity-item">
          <div class="activity-time">{{ activity.time }}</div>
          <div class="activity-content">
            <span class="activity-user">{{ activity.user }}</span>
            <span class="activity-action">{{ activity.action }}</span>
            <span class="activity-target">{{ activity.target }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()

const greeting = computed(() => {
  const hour = new Date().getHours()
  if (hour < 12) return '早上好'
  if (hour < 18) return '下午好'
  return '晚上好'
})

// 处理时间筛选
const handleTimeFilter = (type) => {
  ElMessage.info('切换到' + (type === 'day' ? '日' : type === 'week' ? '周' : '月') + '视图')
  // TODO: 根据时间类型加载对应的数据
}

// 处理待处理事项
const handlePendingAction = (type) => {
  switch (type) {
    case 'product':
      router.push('/admin/products')
      break
    case 'complaint':
      ElMessage.info('跳转到投诉管理页面')
      break
    case 'expert':
      router.push('/admin/experts')
      break
  }
}

const recentActivities = ref([
  { id: 1, user: '张农场', action: '发布了新商品', target: '有机大米 50kg', time: '5分钟前' },
  { id: 2, user: '李买家', action: '提交了订单', target: '订单 #ORD20240709003', time: '15分钟前' },
  { id: 3, user: '王专家', action: '发布了知识', target: '夏季水稻病虫害防治', time: '1小时前' },
  { id: 4, user: '赵银行', action: '批准了融资申请', target: '¥500,000 农业生产贷款', time: '2小时前' },
  { id: 5, user: '新用户', action: '注册了账号', target: '成为农户用户', time: '3小时前' }
])
</script>

<style scoped>
.admin-dashboard {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-6, 24px);
}

/* ===== 页面标题 ===== */
.page-header {
  margin-bottom: var(--spacing-2, 8px);
}

.page-title {
  font-family: var(--font-family-display, 'Noto Serif SC', serif);
  font-size: var(--font-size-3xl, 38px);
  font-weight: var(--font-weight-bold, 700);
  color: var(--color-text-primary, #1f2923);
  margin: 0 0 var(--spacing-2, 8px);
}

.page-subtitle {
  font-size: var(--font-size-base, 16px);
  color: var(--color-text-tertiary, #6b7280);
  margin: 0;
}

/* ===== 指标卡片 ===== */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: var(--spacing-4, 16px);
}

.stat-card {
  display: flex;
  align-items: center;
  gap: var(--spacing-4, 16px);
  padding: var(--spacing-5, 20px);
  background: var(--color-bg-primary, #ffffff);
  border-radius: var(--radius-lg, 12px);
  border: 1px solid var(--color-border, #e5e0d8);
  box-shadow: var(--shadow-sm, 0 2px 4px rgba(31, 41, 35, 0.06));
  transition: all var(--transition-fast, 150ms ease);
}

.stat-card:hover {
  box-shadow: var(--shadow-base, 0 4px 12px rgba(31, 41, 35, 0.08));
  transform: translateY(-2px);
}

.stat-icon {
  width: 48px;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: var(--radius-lg, 12px);
  flex-shrink: 0;
}

.stat-card.primary .stat-icon {
  background: rgba(201, 166, 97, 0.1);
  color: var(--color-admin, #c9a661);
}

.stat-card.success .stat-icon {
  background: rgba(74, 124, 89, 0.1);
  color: var(--color-success, #4a7c59);
}

.stat-card.warning .stat-icon {
  background: rgba(45, 90, 61, 0.1);
  color: var(--color-primary, #2d5a3d);
}

.stat-card.info .stat-icon {
  background: rgba(184, 92, 56, 0.1);
  color: var(--color-error, #b85c38);
}

.stat-icon svg {
  width: 24px;
  height: 24px;
}

.stat-content {
  flex: 1;
  min-width: 0;
}

.stat-value {
  font-size: var(--font-size-2xl, 30px);
  font-weight: var(--font-weight-bold, 700);
  color: var(--color-text-primary, #1f2923);
  line-height: var(--line-height-tight, 1.25);
}

.stat-label {
  font-size: var(--font-size-sm, 14px);
  color: var(--color-text-tertiary, #6b7280);
  margin-top: var(--spacing-1, 4px);
}

.stat-trend {
  display: flex;
  align-items: center;
  gap: var(--spacing-1, 4px);
  margin-top: var(--spacing-2, 8px);
  font-size: var(--font-size-xs, 12px);
  font-weight: var(--font-weight-medium, 500);
}

.stat-trend.positive {
  color: var(--color-success, #4a7c59);
}

.stat-trend.negative {
  color: var(--color-error, #b85c38);
}

/* ===== 内容卡片 ===== */
.content-card {
  background: var(--color-bg-primary, #ffffff);
  border-radius: var(--radius-lg, 12px);
  border: 1px solid var(--color-border, #e5e0d8);
  box-shadow: var(--shadow-sm, 0 2px 4px rgba(31, 41, 35, 0.06));
  overflow: hidden;
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: var(--spacing-5, 20px);
  border-bottom: 1px solid var(--color-divider, #edebe6);
}

.card-title {
  font-size: var(--font-size-lg, 20px);
  font-weight: var(--font-weight-semibold, 600);
  color: var(--color-text-primary, #1f2923);
  margin: 0;
}

.time-filter {
  display: flex;
  gap: var(--spacing-2, 8px);
}

.badge-count {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 24px;
  height: 24px;
  padding: 0 var(--spacing-2, 8px);
  font-size: var(--font-size-xs, 12px);
  font-weight: var(--font-weight-bold, 700);
  color: var(--color-text-inverse, #ffffff);
  background: var(--color-error, #b85c38);
  border-radius: var(--radius-full, 9999px);
}

/* ===== 图表占位符 ===== */
.chart-placeholder {
  aspect-ratio: 21 / 9;
  background: var(--color-bg-secondary, #f7f5f0);
  display: flex;
  align-items: center;
  justify-content: center;
}

.chart-info {
  text-align: center;
  color: var(--color-text-tertiary, #6b7280);
}

.chart-info svg {
  width: 48px;
  height: 48px;
  margin: 0 auto var(--spacing-3, 12px);
}

.chart-info p {
  margin: var(--spacing-1, 4px) 0;
}

.chart-hint {
  font-size: var(--font-size-sm, 14px);
}

/* ===== 内容网格 ===== */
.content-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: var(--spacing-4, 16px);
}

/* ===== 待处理列表 ===== */
.pending-list {
  padding: var(--spacing-3, 12px);
}

.pending-item {
  display: flex;
  align-items: center;
  gap: var(--spacing-3, 12px);
  padding: var(--spacing-3, 12px) var(--spacing-4, 16px);
  border-bottom: 1px solid var(--color-divider, #edebe6);
}

.pending-item:last-child {
  border-bottom: none;
}

.pending-item:hover {
  background: var(--color-bg-secondary, #f7f5f0);
}

.pending-icon {
  width: 36px;
  height: 36px;
  border-radius: var(--radius-base, 8px);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.pending-item.urgent .pending-icon {
  background: rgba(184, 92, 56, 0.1);
  color: var(--color-error, #b85c38);
}

.pending-item.warning .pending-icon {
  background: rgba(201, 166, 97, 0.1);
  color: var(--color-warning, #c9a661);
}

.pending-item:not(.urgent):not(.warning) .pending-icon {
  background: rgba(74, 124, 89, 0.1);
  color: var(--color-success, #4a7c59);
}

.pending-icon svg {
  width: 18px;
  height: 18px;
}

.pending-content {
  flex: 1;
  min-width: 0;
}

.pending-title {
  font-size: var(--font-size-sm, 14px);
  font-weight: var(--font-weight-medium, 500);
  color: var(--color-text-primary, #1f2923);
  margin-bottom: var(--spacing-1, 4px);
}

.pending-time {
  font-size: var(--font-size-xs, 12px);
  color: var(--color-text-tertiary, #6b7280);
}

.pending-item.urgent .pending-time {
  color: var(--color-error, #b85c38);
}

/* ===== 管理入口 ===== */
.admin-entries {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: var(--spacing-3, 12px);
  padding: var(--spacing-4, 16px);
}

.admin-entry {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--spacing-2, 8px);
  padding: var(--spacing-4, 16px);
  background: var(--color-bg-secondary, #f7f5f0);
  border-radius: var(--radius-lg, 12px);
  text-decoration: none;
  transition: all var(--transition-fast, 150ms ease);
}

.admin-entry:hover {
  background: rgba(201, 166, 97, 0.08);
  transform: translateY(-2px);
}

.entry-icon {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--color-bg-primary, #ffffff);
  border-radius: var(--radius-base, 8px);
  color: var(--color-admin, #c9a661);
}

.entry-icon svg {
  width: 20px;
  height: 20px;
}

.admin-entry span {
  font-size: var(--font-size-sm, 14px);
  font-weight: var(--font-weight-medium, 500);
  color: var(--color-text-primary, #1f2923);
}

/* ===== 活动列表 ===== */
.activity-list {
  padding: var(--spacing-3, 12px);
}

.activity-item {
  display: flex;
  gap: var(--spacing-4, 16px);
  padding: var(--spacing-3, 12px) var(--spacing-4, 16px);
  border-bottom: 1px solid var(--color-divider, #edebe6);
}

.activity-item:last-child {
  border-bottom: none;
}

.activity-time {
  font-size: var(--font-size-xs, 12px);
  color: var(--color-text-tertiary, #6b7280);
  min-width: 80px;
  flex-shrink: 0;
}

.activity-content {
  flex: 1;
  font-size: var(--font-size-sm, 14px);
  color: var(--color-text-secondary, #4a5249);
}

.activity-user {
  font-weight: var(--font-weight-medium, 500);
  color: var(--color-text-primary, #1f2923);
}

.activity-action {
  margin: 0 var(--spacing-1, 4px);
}

.activity-target {
  color: var(--color-primary, #2d5a3d);
}

/* ===== 响应式 ===== */
@media (max-width: 1024px) {
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .content-grid {
    grid-template-columns: 1fr;
  }

  .admin-entries {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .stats-grid {
    grid-template-columns: 1fr;
  }

  .admin-entries {
    grid-template-columns: 1fr;
  }

  .page-title {
    font-size: var(--font-size-2xl, 30px);
  }
}
</style>
