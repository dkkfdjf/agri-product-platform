<template>
  <div class="farmer-dashboard">
    <!-- 欢迎横幅 -->
    <div class="welcome-banner">
      <div class="banner-content">
        <div class="banner-text">
          <h1 class="banner-title">欢迎回来，{{ userStore.userInfo?.farmName || userStore.userInfo?.realName }}</h1>
          <p class="banner-subtitle">管理您的农产品、订单和融资，开启智慧农业之旅</p>
        </div>
        <button class="btn-primary" @click="goTo('/farmer/products')">
          <svg width="18" height="18" viewBox="0 0 18 18" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M9 3v10M3 9h12"/>
          </svg>
          发布商品
        </button>
      </div>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-grid">
      <div class="stat-card" @click="goTo('/farmer/products')">
        <div class="stat-icon stat-icon-products">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
            <path d="M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4"/>
          </svg>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ stats.products }}</div>
          <div class="stat-label">已发布商品</div>
        </div>
        <div class="stat-arrow">
          <svg width="16" height="16" viewBox="0 0 16 16" fill="currentColor">
            <path d="M6 3l5 5-5 5"/>
          </svg>
        </div>
      </div>

      <div class="stat-card" @click="goTo('/farmer/orders')">
        <div class="stat-icon stat-icon-orders">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
            <path d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2"/>
          </svg>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ stats.orders }}</div>
          <div class="stat-label">订单数量</div>
        </div>
        <div class="stat-arrow">
          <svg width="16" height="16" viewBox="0 0 16 16" fill="currentColor">
            <path d="M6 3l5 5-5 5"/>
          </svg>
        </div>
      </div>

      <div class="stat-card" @click="goTo('/farmer/finance')">
        <div class="stat-icon stat-icon-finance">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
            <path d="M12 2v20M17 5H9.5a3.5 3.5 0 000 7h5a3.5 3.5 0 010 7H6"/>
            <circle cx="12" cy="12" r="2" fill="currentColor"/>
          </svg>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ stats.finances }}</div>
          <div class="stat-label">融资申请</div>
        </div>
        <div class="stat-arrow">
          <svg width="16" height="16" viewBox="0 0 16 16" fill="currentColor">
            <path d="M6 3l5 5-5 5"/>
          </svg>
        </div>
      </div>

      <div class="stat-card" @click="goTo('/farmer/expert')">
        <div class="stat-icon stat-icon-expert">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
            <circle cx="12" cy="8" r="3"/>
            <path d="M12 11v4M12 15a3 3 0 00-3 3v2h6v-2a3 3 0 00-3-3z"/>
          </svg>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ stats.questions }}</div>
          <div class="stat-label">咨询数量</div>
        </div>
        <div class="stat-arrow">
          <svg width="16" height="16" viewBox="0 0 16 16" fill="currentColor">
            <path d="M6 3l5 5-5 5"/>
          </svg>
        </div>
      </div>
    </div>

    <!-- 快速入口 -->
    <div class="section-card">
      <div class="section-header">
        <h3 class="section-title">快速入口</h3>
      </div>
      <div class="quick-actions">
        <div class="action-item" @click="goTo('/farmer/products')">
          <div class="action-icon action-icon-primary">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
              <path d="M12 5v14M5 12h14"/>
            </svg>
          </div>
          <div class="action-info">
            <div class="action-title">发布商品</div>
            <div class="action-desc">添加新的农产品</div>
          </div>
          <svg class="action-arrow" width="16" height="16" viewBox="0 0 16 16" fill="currentColor">
            <path d="M6 3l5 5-5 5"/>
          </svg>
        </div>

        <div class="action-item" @click="goTo('/farmer/orders')">
          <div class="action-icon action-icon-success">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
              <path d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2"/>
            </svg>
          </div>
          <div class="action-info">
            <div class="action-title">查看订单</div>
            <div class="action-desc">管理订单状态</div>
          </div>
          <svg class="action-arrow" width="16" height="16" viewBox="0 0 16 16" fill="currentColor">
            <path d="M6 3l5 5-5 5"/>
          </svg>
        </div>

        <div class="action-item" @click="goTo('/farmer/finance')">
          <div class="action-icon action-icon-secondary">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
              <path d="M12 2v20M17 5H9.5a3.5 3.5 0 000 7h5a3.5 3.5 0 010 7H6"/>
            </svg>
          </div>
          <div class="action-info">
            <div class="action-title">申请融资</div>
            <div class="action-desc">获取资金支持</div>
          </div>
          <svg class="action-arrow" width="16" height="16" viewBox="0 0 16 16" fill="currentColor">
            <path d="M6 3l5 5-5 5"/>
          </svg>
        </div>

        <div class="action-item" @click="goTo('/farmer/expert')">
          <div class="action-icon action-icon-accent">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
              <circle cx="12" cy="8" r="3"/>
              <path d="M12 11v4M12 15a3 3 0 00-3 3v2h6v-2a3 3 0 00-3-3z"/>
            </svg>
          </div>
          <div class="action-info">
            <div class="action-title">专家咨询</div>
            <div class="action-desc">获取专业指导</div>
          </div>
          <svg class="action-arrow" width="16" height="16" viewBox="0 0 16 16" fill="currentColor">
            <path d="M6 3l5 5-5 5"/>
          </svg>
        </div>

        <div class="action-item" @click="goTo('/market')">
          <div class="action-icon action-icon-info">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
              <path d="M3 3h2l.4 2M7 13h10l4-8H5.4M7 13L5.4 5M7 13l-2.293 2.293c-.63.63-.184 1.707.707 1.707H17m0 0a2 2 0 100 4 2 2 0 000-4zm-8 2a2 2 0 11-4 0 2 2 0 014 0z"/>
            </svg>
          </div>
          <div class="action-info">
            <div class="action-title">农产品交易</div>
            <div class="action-desc">浏览市场商品</div>
          </div>
          <svg class="action-arrow" width="16" height="16" viewBox="0 0 16 16" fill="currentColor">
            <path d="M6 3l5 5-5 5"/>
          </svg>
        </div>

        <div class="action-item" @click="goTo('/farmer/profile')">
          <div class="action-icon action-icon-neutral">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
              <path d="M10.325 4.317c.426-1.756 2.924-1.756 3.35 0a1.724 1.724 0 002.573 1.066c1.543-.94 3.31.826 2.37 2.37a1.724 1.724 0 001.065 2.572c1.756.426 1.756 2.924 0 3.35a1.724 1.724 0 00-1.066 2.573c.94 1.543-.826 3.31-2.37 2.37a1.724 1.724 0 00-2.572 1.065c-.426 1.756-2.924 1.756-3.35 0a1.724 1.724 0 00-2.573-1.066c-1.543.94-3.31-.826-2.37-2.37a1.724 1.724 0 00-1.065-2.572c-1.756-.426-1.756-2.924 0-3.35a1.724 1.724 0 001.066-2.573c-.94-1.543.826-3.31 2.37-2.37.996.608 2.296.07 2.572-1.065z"/>
              <path d="M15 12a3 3 0 11-6 0 3 3 0 016 0z"/>
            </svg>
          </div>
          <div class="action-info">
            <div class="action-title">账户设置</div>
            <div class="action-desc">管理个人信息</div>
          </div>
          <svg class="action-arrow" width="16" height="16" viewBox="0 0 16 16" fill="currentColor">
            <path d="M6 3l5 5-5 5"/>
          </svg>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

const stats = reactive({
  products: 0,
  orders: 0,
  finances: 0,
  questions: 0
})

const goTo = (path) => {
  router.push(path)
}

const loadStats = async () => {
  try {
    // TODO: 调用后端接口获取统计数据
    stats.products = 12
    stats.orders = 8
    stats.finances = 3
    stats.questions = 5
  } catch (error) {
    console.error('加载统计数据失败:', error)
  }
}

onMounted(() => {
  loadStats()
})
</script>

<style scoped>
/* ===== 全局变量 ===== */
.farmer-dashboard {
  --color-primary: #2D5A3D;
  --color-primary-light: #4A7C59;
  --color-primary-dark: #1F4229;
  --color-secondary: #C9A661;
  --color-accent: #B85C38;
  --color-bg: #F7F5F0;
  --color-text: #1F2923;
  --color-text-light: #6B7280;
  --color-border: #E5E0D8;
  --color-white: #FFFFFF;
  --font-display: 'Noto Serif SC', serif;
  --font-body: 'Noto Sans SC', sans-serif;
}

/* ===== 欢迎横幅 ===== */
.welcome-banner {
  background: linear-gradient(135deg, var(--color-primary) 0%, var(--color-primary-dark) 100%);
  border-radius: 16px;
  margin-bottom: 24px;
  position: relative;
  overflow: hidden;
}

.welcome-banner::before {
  content: '';
  position: absolute;
  top: 0;
  right: 0;
  width: 40%;
  height: 100%;
  background: radial-gradient(circle at 80% 20%, rgba(201, 166, 97, 0.15) 0%, transparent 60%);
}

.banner-content {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 32px;
  z-index: 1;
}

.banner-text {
  color: var(--color-white);
}

.banner-title {
  font-family: var(--font-display);
  font-size: 24px;
  font-weight: 700;
  margin: 0 0 8px;
}

.banner-subtitle {
  font-size: 14px;
  opacity: 0.9;
  margin: 0;
}

.btn-primary {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 24px;
  font-size: 14px;
  font-weight: 500;
  color: var(--color-primary);
  background: var(--color-white);
  border: none;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-primary:hover {
  background: rgba(255, 255, 255, 0.9);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

/* ===== 统计卡片 ===== */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  margin-bottom: 24px;
}

.stat-card {
  position: relative;
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px;
  background: var(--color-white);
  border-radius: 12px;
  border: 1px solid var(--color-border);
  cursor: pointer;
  transition: all 0.3s;
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.08);
}

.stat-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 48px;
  height: 48px;
  border-radius: 10px;
  flex-shrink: 0;
}

.stat-icon svg {
  width: 24px;
  height: 24px;
}

.stat-icon-products {
  background: rgba(45, 90, 61, 0.1);
  color: var(--color-primary);
}

.stat-icon-orders {
  background: rgba(74, 124, 89, 0.1);
  color: var(--color-primary-light);
}

.stat-icon-finance {
  background: rgba(201, 166, 97, 0.15);
  color: var(--color-secondary);
}

.stat-icon-expert {
  background: rgba(184, 92, 56, 0.1);
  color: var(--color-accent);
}

.stat-content {
  flex: 1;
}

.stat-value {
  font-family: var(--font-display);
  font-size: 24px;
  font-weight: 700;
  color: var(--color-text);
  line-height: 1;
}

.stat-label {
  font-size: 13px;
  color: var(--color-text-light);
  margin-top: 4px;
}

.stat-arrow {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 28px;
  height: 28px;
  background: var(--color-bg);
  border-radius: 8px;
  color: var(--color-text-light);
  opacity: 0;
  transition: all 0.2s;
}

.stat-card:hover .stat-arrow {
  opacity: 1;
}

/* ===== 区域卡片 ===== */
.section-card {
  background: var(--color-white);
  border-radius: 12px;
  border: 1px solid var(--color-border);
  overflow: hidden;
}

.section-header {
  padding: 16px 20px;
  border-bottom: 1px solid var(--color-border);
}

.section-title {
  font-family: var(--font-display);
  font-size: 16px;
  font-weight: 600;
  color: var(--color-text);
  margin: 0;
}

/* ===== 快速入口 ===== */
.quick-actions {
  display: flex;
  flex-direction: column;
}

.action-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px 20px;
  border-bottom: 1px solid var(--color-border);
  cursor: pointer;
  transition: all 0.2s;
}

.action-item:last-child {
  border-bottom: none;
}

.action-item:hover {
  background: var(--color-bg);
}

.action-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 40px;
  height: 40px;
  border-radius: 10px;
  flex-shrink: 0;
}

.action-icon svg {
  width: 20px;
  height: 20px;
}

.action-icon-primary {
  background: rgba(45, 90, 61, 0.1);
  color: var(--color-primary);
}

.action-icon-success {
  background: rgba(74, 124, 89, 0.1);
  color: var(--color-primary-light);
}

.action-icon-secondary {
  background: rgba(201, 166, 97, 0.15);
  color: var(--color-secondary);
}

.action-icon-accent {
  background: rgba(184, 92, 56, 0.1);
  color: var(--color-accent);
}

.action-icon-info {
  background: rgba(59, 130, 246, 0.1);
  color: #3B82F6;
}

.action-icon-neutral {
  background: rgba(107, 114, 128, 0.1);
  color: var(--color-text-light);
}

.action-info {
  flex: 1;
}

.action-title {
  font-size: 14px;
  font-weight: 500;
  color: var(--color-text);
  margin-bottom: 2px;
}

.action-desc {
  font-size: 12px;
  color: var(--color-text-light);
}

.action-arrow {
  color: var(--color-text-light);
  opacity: 0;
  transition: all 0.2s;
}

.action-item:hover .action-arrow {
  opacity: 1;
}

/* ===== 响应式 ===== */
@media (max-width: 1200px) {
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .banner-content {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }
}

@media (max-width: 768px) {
  .stats-grid {
    grid-template-columns: 1fr;
  }

  .banner-content {
    padding: 20px;
  }

  .banner-title {
    font-size: 20px;
  }
}
</style>
