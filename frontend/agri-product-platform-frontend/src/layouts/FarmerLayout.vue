<template>
  <div class="farmer-dashboard-layout">
    <!-- 顶部导航栏 -->
    <header class="dashboard-header">
      <div class="header-left">
        <div class="logo" @click="goTo('/home')">
          <div class="logo-icon">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
              <path d="M12 2L12 12M12 12L8 8M12 12L16 8M12 12L12 22M12 22L8 18M12 22L16 18" />
              <circle cx="12" cy="3" r="1.5" fill="currentColor"/>
            </svg>
          </div>
          <span class="logo-text">农融汇</span>
        </div>
        <nav class="header-nav">
          <a @click="goTo('/home')">首页</a>
          <a @click="goTo('/market')">农产品</a>
          <a @click="goTo('/finance-products')">融资</a>
          <a @click="goTo('/expert-help')">专家</a>
        </nav>
      </div>
      <div class="header-right">
        <div class="user-chip" @click="toggleUserMenu">
          <span class="user-avatar">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
              <circle cx="12" cy="8" r="3"/>
              <path d="M12 11v4M12 15a3 3 0 00-3 3v2h6v-2a3 3 0 00-3-3z"/>
            </svg>
          </span>
          <span class="user-name">{{ userStore.userInfo?.farmName || userStore.userInfo?.realName || '农户' }}</span>
          <svg class="dropdown-icon" width="16" height="16" viewBox="0 0 16 16" fill="currentColor">
            <path d="M4 6l4 4 4-4"/>
          </svg>
        </div>
      </div>
    </header>

    <!-- 用户下拉菜单 -->
    <transition name="fade">
      <div v-if="userMenuOpen" class="user-dropdown" @click="userMenuOpen = false">
        <div class="dropdown-menu" @click.stop>
          <div class="dropdown-item" @click="goTo('/farmer/profile')">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
              <circle cx="12" cy="8" r="3"/>
              <path d="M12 11v4M12 15a3 3 0 00-3 3v2h6v-2a3 3 0 00-3-3z"/>
            </svg>
            <span>个人中心</span>
          </div>
          <div class="dropdown-divider"></div>
          <div class="dropdown-item danger" @click="handleLogout">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
              <path d="M9 21H5a2 2 0 01-2-2V5a2 2 0 012-2h4"/>
              <polyline points="16 17 21 12 16 7"/>
              <line x1="21" y1="12" x2="9" y2="12"/>
            </svg>
            <span>退出登录</span>
          </div>
        </div>
      </div>
    </transition>

    <!-- 主内容区 -->
    <div class="dashboard-main">
      <!-- 侧边栏 -->
      <aside class="dashboard-sidebar">
        <nav class="sidebar-nav">
          <router-link
            v-for="item in sidebarItems"
            :key="item.path"
            :to="item.path"
            :class="['nav-item', { active: isActive(item.path) }]"
          >
            <span class="nav-icon">
              <component :is="item.icon" />
            </span>
            <span class="nav-label">{{ item.label }}</span>
            <span v-if="item.badge" class="nav-badge">{{ item.badge }}</span>
          </router-link>
        </nav>
      </aside>

      <!-- 内容区域 -->
      <main class="dashboard-content">
        <router-view />
      </main>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessageBox } from 'element-plus'
import { useUserStore } from '@/stores/user'

// Icons
const OdometerIcon = () => `
  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
    <circle cx="12" cy="12" r="3"/>
    <path d="M12 1v6m0 6v6"/>
    <path d="m5 12 6-6m0 12 6-6"/>
    <circle cx="12" cy="12" r="8"/>
  </svg>
`

const GoodsIcon = () => `
  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
    <path d="M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4"/>
  </svg>
`

const ListIcon = () => `
  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
    <path d="M8 6h13M8 12h13M8 18h13M3 6h.01M3 12h.01M3 18h.01"/>
  </svg>
`

const MoneyIcon = () => `
  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
    <path d="M12 2v20M17 5H9.5a3.5 3.5 0 000 7h5a3.5 3.5 0 010 7H6"/>
    <circle cx="12" cy="12" r="2" fill="currentColor"/>
  </svg>
`

const UserIcon = () => `
  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
    <circle cx="12" cy="8" r="3"/>
    <path d="M12 11v4M12 15a3 3 0 00-3 3v2h6v-2a3 3 0 00-3-3z"/>
  </svg>
`

const SettingIcon = () => `
  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
    <circle cx="12" cy="12" r="3"/>
    <path d="M12 1v6m0 6v6"/>
    <path d="m5 12 6-6m0 12 6-6"/>
    <circle cx="12" cy="12" r="8"/>
  </svg>
`

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const userMenuOpen = ref(false)

const sidebarItems = [
  { path: '/farmer/dashboard', label: '个人中心', icon: OdometerIcon },
  { path: '/farmer/products', label: '我的发布', icon: GoodsIcon },
  { path: '/farmer/orders', label: '我的订单', icon: ListIcon },
  { path: '/farmer/finance', label: '我的融资', icon: MoneyIcon },
  { path: '/farmer/expert', label: '专家咨询', icon: UserIcon },
  { path: '/farmer/profile', label: '账户设置', icon: SettingIcon }
]

const isActive = (path) => {
  return route.path === path || route.path.startsWith(path + '/')
}

const toggleUserMenu = () => {
  userMenuOpen.value = !userMenuOpen.value
}

const goTo = (path) => {
  router.push(path)
}

const handleLogout = () => {
  ElMessageBox.confirm('确定要退出登录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    userStore.logout()
    router.push('/login')
  }).catch(() => {})
}
</script>

<style scoped>
/* ===== 全局变量 ===== */
.farmer-dashboard-layout {
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

  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background: var(--color-bg);
}

/* ===== 顶部导航栏 ===== */
.dashboard-header {
  height: 64px;
  background: var(--color-white);
  border-bottom: 1px solid var(--color-border);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 100;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 48px;
}

.logo {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  transition: opacity 0.2s;
}

.logo:hover {
  opacity: 0.8;
}

.logo-icon {
  width: 32px;
  height: 32px;
  color: var(--color-primary);
  display: flex;
  align-items: center;
  justify-content: center;
}

.logo-icon svg {
  width: 24px;
  height: 24px;
}

.logo-text {
  font-family: var(--font-display);
  font-size: 18px;
  font-weight: 600;
  color: var(--color-primary);
}

.header-nav {
  display: flex;
  gap: 32px;
}

.header-nav a {
  font-size: 14px;
  font-weight: 500;
  color: var(--color-text-light);
  text-decoration: none;
  cursor: pointer;
  transition: color 0.2s;
  position: relative;
  padding: 20px 0;
}

.header-nav a:hover {
  color: var(--color-primary);
}

.header-right {
  display: flex;
  align-items: center;
}

.user-chip {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 12px;
  background: var(--color-bg);
  border-radius: 20px;
  cursor: pointer;
  transition: all 0.2s;
}

.user-chip:hover {
  background: var(--color-border);
}

.user-avatar {
  width: 28px;
  height: 28px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--color-primary);
  background: var(--color-white);
  border-radius: 50%;
}

.user-avatar svg {
  width: 16px;
  height: 16px;
}

.user-name {
  font-size: 14px;
  font-weight: 500;
  color: var(--color-text);
}

.dropdown-icon {
  color: var(--color-text-light);
  transition: transform 0.2s;
}

/* ===== 用户下拉菜单 ===== */
.user-dropdown {
  position: fixed;
  top: 64px;
  right: 24px;
  z-index: 99;
}

.dropdown-menu {
  background: var(--color-white);
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  border: 1px solid var(--color-border);
  padding: 8px;
  min-width: 180px;
}

.dropdown-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;
  color: var(--color-text);
  font-size: 14px;
}

.dropdown-item:hover {
  background: var(--color-bg);
}

.dropdown-item svg {
  width: 18px;
  height: 18px;
  color: var(--color-text-light);
}

.dropdown-item.danger {
  color: var(--color-accent);
}

.dropdown-item.danger svg {
  color: var(--color-accent);
}

.dropdown-divider {
  height: 1px;
  background: var(--color-border);
  margin: 4px 0;
}

/* ===== 主内容区 ===== */
.dashboard-main {
  display: flex;
  margin-top: 64px;
  min-height: calc(100vh - 64px);
}

/* ===== 侧边栏 ===== */
.dashboard-sidebar {
  width: 200px;
  background: linear-gradient(180deg, var(--color-primary) 0%, var(--color-primary-dark) 100%);
  padding: 24px 12px;
  position: fixed;
  left: 0;
  top: 64px;
  bottom: 0;
}

.sidebar-nav {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  border-radius: 10px;
  color: rgba(255, 255, 255, 0.75);
  text-decoration: none;
  transition: all 0.2s;
  position: relative;
  font-size: 14px;
  font-weight: 500;
}

.nav-item:hover {
  color: rgba(255, 255, 255, 0.95);
  background: rgba(255, 255, 255, 0.1);
}

.nav-item.active {
  color: var(--color-white);
  background: rgba(255, 255, 255, 0.15);
  font-weight: 600;
}

.nav-icon {
  width: 20px;
  height: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.nav-icon :deep(svg) {
  width: 18px;
  height: 18px;
}

.nav-label {
  flex: 1;
}

.nav-badge {
  background: var(--color-accent);
  color: var(--color-white);
  font-size: 11px;
  font-weight: 600;
  padding: 2px 8px;
  border-radius: 10px;
  min-width: 20px;
  text-align: center;
}

/* ===== 内容区域 ===== */
.dashboard-content {
  flex: 1;
  margin-left: 200px;
  padding: 24px;
  background: var(--color-bg);
  min-height: calc(100vh - 64px);
}

/* ===== 过渡动画 ===== */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

/* ===== 响应式 ===== */
@media (max-width: 1024px) {
  .header-nav {
    display: none;
  }

  .dashboard-sidebar {
    width: 64px;
    padding: 24px 8px;
  }

  .nav-label {
    display: none;
  }

  .dashboard-content {
    margin-left: 64px;
    padding: 16px;
  }
}

@media (max-width: 768px) {
  .dashboard-header {
    padding: 0 16px;
  }

  .header-left {
    gap: 16px;
  }

  .logo-text {
    display: none;
  }

  .dashboard-sidebar {
    display: none;
  }

  .dashboard-content {
    margin-left: 0;
    padding: 12px;
  }
}
</style>
