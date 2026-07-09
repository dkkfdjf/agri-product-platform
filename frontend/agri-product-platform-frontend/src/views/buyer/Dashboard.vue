<template>
  <div class="buyer-dashboard">
    <!-- 页面标题区 -->
    <div class="page-header">
      <h1 class="page-title">{{ greeting }}，{{ userStore.displayName() }}</h1>
      <p class="page-subtitle">欢迎来到买家工作台，探索优质农产品，轻松采购</p>
    </div>

    <!-- 核心指标卡片 -->
    <div class="stats-grid">
      <div class="stat-card primary">
        <div class="stat-icon">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
            <circle cx="9" cy="21" r="1"/>
            <circle cx="20" cy="21" r="1"/>
            <path d="M1 1h4l2.68 13.39a2 2 0 002 1.61h9.72a2 2 0 002-1.61L23 6H6"/>
          </svg>
        </div>
        <div class="stat-content">
          <div class="stat-value">23</div>
          <div class="stat-label">累计订单</div>
          <div class="stat-trend positive">
            <el-icon><Top /></el-icon>
            <span>本月新增 5 笔</span>
          </div>
        </div>
      </div>

      <div class="stat-card success">
        <div class="stat-icon">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
            <path d="M12 2v20M17 5H9.5a3.5 3.5 0 000 7h5a3.5 3.5 0 010 7H6"/>
            <circle cx="12" cy="12" r="2" fill="currentColor"/>
          </svg>
        </div>
        <div class="stat-content">
          <div class="stat-value">¥ 45,200</div>
          <div class="stat-label">累计消费</div>
        </div>
      </div>

      <div class="stat-card warning">
        <div class="stat-icon">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
            <path d="M9 20a1 1 0 100 2 1 1 0 000-2zM7 4h10a2 2 0 012 2v1H5V6a2 2 0 012-2zM5 8h14v10a2 2 0 01-2 2H7a2 2 0 01-2-2V8z"/>
          </svg>
        </div>
        <div class="stat-content">
          <div class="stat-value">8</div>
          <div class="stat-label">购物车商品</div>
          <div class="stat-action">
            <router-link to="/buyer/cart">去结算</router-link>
          </div>
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
          <div class="stat-value">2</div>
          <div class="stat-label">专家咨询</div>
          <div class="stat-action">
            <router-link to="/buyer/expert">继续咨询</router-link>
          </div>
        </div>
      </div>
    </div>

    <!-- 主要内容区 -->
    <div class="content-grid">
      <!-- 推荐商品 -->
      <div class="content-card">
        <div class="card-header">
          <h3 class="card-title">推荐商品</h3>
          <router-link to="/market" class="card-link">查看更多</router-link>
        </div>
        <div class="product-grid">
          <div v-for="product in recommendedProducts" :key="product.id" class="product-card">
            <div class="product-image">
              <div class="image-placeholder">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
                  <path d="M21 12c0 4.97-4.03 9-9 9s-9-4.03-9-9 4.03-9 9-9 9 4.03 9 9z"/>
                  <path d="M12 7v5l3 3"/>
                </svg>
              </div>
            </div>
            <div class="product-info">
              <div class="product-name">{{ product.name }}</div>
              <div class="product-origin">{{ product.origin }}</div>
              <div class="product-price">¥{{ product.price }}/{{ product.unit }}</div>
            </div>
          </div>
        </div>
      </div>

      <!-- 快捷操作 -->
      <div class="content-card">
        <div class="card-header">
          <h3 class="card-title">快捷操作</h3>
        </div>
        <div class="quick-actions">
          <router-link to="/market" class="quick-action">
            <div class="action-icon">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
                <circle cx="11" cy="11" r="8"/>
                <path d="m21 21-4.35-4.35M11 8v6M8 11h6"/>
              </svg>
            </div>
            <span>逛市场</span>
          </router-link>
          <router-link to="/buyer/cart" class="quick-action">
            <div class="action-icon">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
                <path d="M9 20a1 1 0 100 2 1 1 0 000-2zM7 4h10a2 2 0 012 2v1H5V6a2 2 0 012-2zM5 8h14v10a2 2 0 01-2 2H7a2 2 0 01-2-2V8z"/>
              </svg>
            </div>
            <span>购物车</span>
          </router-link>
          <router-link to="/buyer/orders" class="quick-action">
            <div class="action-icon">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
                <path d="M14 2H6a2 2 0 00-2 2v16a2 2 0 002 2h12a2 2 0 002-2V8z"/>
                <path d="M14 2v6h6M16 13H8M16 17H8M10 9H8"/>
              </svg>
            </div>
            <span>我的订单</span>
          </router-link>
          <router-link to="/buyer/expert" class="quick-action">
            <div class="action-icon">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
                <circle cx="12" cy="8" r="3"/>
                <path d="M8 21v-5a4 4 0 014-4M8 21h8M12 12v5M8 10l2-2M16 10l-2-2"/>
              </svg>
            </div>
            <span>专家咨询</span>
          </router-link>
        </div>
      </div>
    </div>

    <!-- 最近订单 -->
    <div class="content-card">
      <div class="card-header">
        <h3 class="card-title">最近订单</h3>
        <router-link to="/buyer/orders" class="card-link">查看全部</router-link>
      </div>
      <div class="order-list">
        <div v-for="order in recentOrders" :key="order.id" class="order-item">
          <div class="order-info">
            <div class="order-products">{{ order.products }}</div>
            <div class="order-meta">
              <span class="order-id">订单号：{{ order.id }}</span>
              <span class="order-time">{{ order.time }}</span>
            </div>
          </div>
          <div class="order-amount">¥{{ order.amount }}</div>
          <div class="order-status" :class="order.statusClass">{{ order.status }}</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()

const greeting = computed(() => {
  const hour = new Date().getHours()
  if (hour < 12) return '早上好'
  if (hour < 18) return '下午好'
  return '晚上好'
})

const recommendedProducts = ref([
  { id: 1, name: '有机东北大米', origin: '黑龙江·五常', price: '8.50', unit: 'kg' },
  { id: 2, name: '红富士苹果', origin: '陕西·洛川', price: '12.00', unit: 'kg' },
  { id: 3, name: '散养土鸡蛋', origin: '湖北·荆门', price: '2.50', unit: '个' },
  { id: 4, name: '新鲜时蔬套餐', origin: '山东·寿光', price: '45.00', unit: '份' }
])

const recentOrders = ref([
  {
    id: 'ORD20240709003',
    products: '有机大米 50kg × 2',
    time: '昨天',
    amount: '850.00',
    status: '待收货',
    statusClass: 'pending'
  },
  {
    id: 'ORD20240708005',
    products: '红富士苹果 30kg',
    time: '3天前',
    amount: '360.00',
    status: '已完成',
    statusClass: 'success'
  },
  {
    id: 'ORD20240705012',
    products: '土鸡蛋 100个',
    time: '1周前',
    amount: '250.00',
    status: '已完成',
    statusClass: 'success'
  }
])
</script>

<style scoped>
.buyer-dashboard {
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
  background: rgba(91, 138, 158, 0.1);
  color: var(--color-buyer, #5b8a9e);
}

.stat-card.success .stat-icon {
  background: rgba(74, 124, 89, 0.1);
  color: var(--color-success, #4a7c59);
}

.stat-card.warning .stat-icon {
  background: rgba(201, 166, 97, 0.1);
  color: var(--color-warning, #c9a661);
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

.stat-action {
  margin-top: var(--spacing-2, 8px);
}

.stat-action a {
  font-size: var(--font-size-xs, 12px);
  color: var(--color-buyer, #5b8a9e);
  text-decoration: none;
  font-weight: var(--font-weight-medium, 500);
}

.stat-action a:hover {
  text-decoration: underline;
}

/* ===== 内容网格 ===== */
.content-grid {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: var(--spacing-4, 16px);
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

.card-link {
  font-size: var(--font-size-sm, 14px);
  color: var(--color-buyer, #5b8a9e);
  text-decoration: none;
  font-weight: var(--font-weight-medium, 500);
}

.card-link:hover {
  text-decoration: underline;
}

/* ===== 商品网格 ===== */
.product-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: var(--spacing-3, 12px);
  padding: var(--spacing-4, 16px);
}

.product-card {
  cursor: pointer;
  transition: transform var(--transition-fast, 150ms ease);
}

.product-card:hover {
  transform: translateY(-4px);
}

.product-image {
  aspect-ratio: 1;
  background: var(--color-bg-secondary, #f7f5f0);
  border-radius: var(--radius-base, 8px);
  margin-bottom: var(--spacing-2, 8px);
  overflow: hidden;
}

.image-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--color-text-tertiary, #6b7280);
}

.image-placeholder svg {
  width: 32px;
  height: 32px;
}

.product-name {
  font-size: var(--font-size-sm, 14px);
  font-weight: var(--font-weight-medium, 500);
  color: var(--color-text-primary, #1f2923);
  margin-bottom: var(--spacing-1, 4px);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.product-origin {
  font-size: var(--font-size-xs, 12px);
  color: var(--color-text-tertiary, #6b7280);
  margin-bottom: var(--spacing-1, 4px);
}

.product-price {
  font-size: var(--font-size-sm, 14px);
  font-weight: var(--font-weight-bold, 700);
  color: var(--color-error, #b85c38);
}

/* ===== 快捷操作 ===== */
.quick-actions {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: var(--spacing-3, 12px);
  padding: var(--spacing-4, 16px);
}

.quick-action {
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

.quick-action:hover {
  background: rgba(91, 138, 158, 0.08);
  transform: translateY(-2px);
}

.action-icon {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--color-bg-primary, #ffffff);
  border-radius: var(--radius-base, 8px);
  color: var(--color-buyer, #5b8a9e);
}

.action-icon svg {
  width: 20px;
  height: 20px;
}

.quick-action span {
  font-size: var(--font-size-sm, 14px);
  font-weight: var(--font-weight-medium, 500);
  color: var(--color-text-primary, #1f2923);
}

/* ===== 订单列表 ===== */
.order-list {
  padding: var(--spacing-3, 12px);
}

.order-item {
  display: flex;
  align-items: center;
  gap: var(--spacing-4, 16px);
  padding: var(--spacing-3, 12px) var(--spacing-4, 16px);
  border-bottom: 1px solid var(--color-divider, #edebe6);
}

.order-item:last-child {
  border-bottom: none;
}

.order-item:hover {
  background: var(--color-bg-secondary, #f7f5f0);
}

.order-info {
  flex: 1;
  min-width: 0;
}

.order-products {
  font-size: var(--font-size-sm, 14px);
  font-weight: var(--font-weight-medium, 500);
  color: var(--color-text-primary, #1f2923);
  margin-bottom: var(--spacing-1, 4px);
}

.order-meta {
  display: flex;
  gap: var(--spacing-3, 12px);
  font-size: var(--font-size-xs, 12px);
  color: var(--color-text-tertiary, #6b7280);
}

.order-amount {
  font-size: var(--font-size-base, 16px);
  font-weight: var(--font-weight-bold, 700);
  color: var(--color-text-primary, #1f2923);
}

.order-status {
  padding: var(--spacing-1, 4px) var(--spacing-3, 12px);
  font-size: var(--font-size-xs, 12px);
  font-weight: var(--font-weight-medium, 500);
  border-radius: var(--radius-full, 9999px);
  flex-shrink: 0;
}

.order-status.pending {
  background: rgba(201, 166, 97, 0.1);
  color: var(--color-warning, #c9a661);
}

.order-status.success {
  background: rgba(74, 124, 89, 0.1);
  color: var(--color-success, #4a7c59);
}

/* ===== 响应式 ===== */
@media (max-width: 1024px) {
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .content-grid {
    grid-template-columns: 1fr;
  }

  .product-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .stats-grid {
    grid-template-columns: 1fr;
  }

  .product-grid {
    grid-template-columns: 1fr;
  }

  .page-title {
    font-size: var(--font-size-2xl, 30px);
  }
}
</style>
