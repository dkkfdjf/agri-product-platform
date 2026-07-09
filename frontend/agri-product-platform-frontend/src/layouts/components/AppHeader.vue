<template>
  <header class="app-header">
    <div class="header-left">
      <div class="logo" @click="goToHome">
        <svg viewBox="0 0 80 80" fill="none" stroke="currentColor" stroke-width="1.5">
          <path d="M40 12v40" stroke-linecap="round"/>
          <path d="M40 52l-6 6M40 52l6 6M40 48l-8 4M40 48l8 4" stroke-linecap="round" opacity="0.6"/>
          <path d="M40 20c-8 4-16 2-20-4" stroke-linecap="round"/>
          <path d="M40 28c-10 4-18 0-22-8" stroke-linecap="round"/>
          <path d="M40 36c-8 2-14 0-16-6" stroke-linecap="round"/>
          <path d="M40 20c8 4 16 2 20-4" stroke-linecap="round"/>
          <path d="M40 28c10 4 18 0 22-8" stroke-linecap="round"/>
          <path d="M40 36c8 2 14 0 16-6" stroke-linecap="round"/>
          <circle cx="40" cy="10" r="3" fill="currentColor" stroke="none"/>
        </svg>
        <span class="logo-text">农产融销</span>
      </div>
    </div>

    <div class="header-center">
      <nav class="header-nav">
        <template v-if="!userStore.isLoggedIn()">
          <router-link to="/market" class="nav-item">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
              <circle cx="9" cy="21" r="1"/>
              <circle cx="20" cy="21" r="1"/>
              <path d="M1 1h4l2.68 13.39a2 2 0 002 1.61h9.72a2 2 0 002-1.61L23 6H6"/>
            </svg>
            <span>农产品交易</span>
          </router-link>
          <router-link to="/finance-products" class="nav-item">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
              <path d="M12 2v20M17 5H9.5a3.5 3.5 0 000 7h5a3.5 3.5 0 010 7H6"/>
              <circle cx="12" cy="12" r="2" fill="currentColor"/>
            </svg>
            <span>融资服务</span>
          </router-link>
          <router-link to="/expert-help" class="nav-item">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
              <circle cx="12" cy="8" r="3"/>
              <path d="M8 21v-5a4 4 0 014-4M8 21h8M12 12v5M8 10l2-2M16 10l-2-2"/>
            </svg>
            <span>专家助力</span>
          </router-link>
        </template>
        <template v-else>
          <router-link to="/market" class="nav-item">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
              <circle cx="9" cy="21" r="1"/>
              <circle cx="20" cy="21" r="1"/>
              <path d="M1 1h4l2.68 13.39a2 2 0 002 1.61h9.72a2 2 0 002-1.61L23 6H6"/>
            </svg>
            <span>市场</span>
          </router-link>
        </template>
      </nav>
    </div>

    <div class="header-right">
      <div v-if="userStore.isLoggedIn()" class="user-actions">
        <el-badge :value="notificationCount" :hidden="notificationCount === 0" class="notification-badge">
          <el-button circle class="notification-btn" @click="showNotifications">
            <el-icon><Bell /></el-icon>
          </el-button>
        </el-badge>

        <el-dropdown trigger="click" @command="handleUserCommand">
          <div class="user-dropdown-trigger">
            <div class="user-avatar">
              <img v-if="userStore.userAvatar()" :src="userStore.userAvatar()" alt="avatar">
              <span v-else class="avatar-placeholder">{{ userStore.displayName()?.charAt(0) || 'U' }}</span>
            </div>
            <div class="user-info">
              <div class="user-name">{{ userStore.displayName() }}</div>
              <div class="user-role">{{ roleLabels[userStore.role] || '用户' }}</div>
            </div>
            <el-icon class="dropdown-arrow"><ArrowDown /></el-icon>
          </div>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="profile">
                <el-icon><User /></el-icon>
                个人中心
              </el-dropdown-item>
              <el-dropdown-item command="settings">
                <el-icon><Setting /></el-icon>
                设置
              </el-dropdown-item>
              <el-dropdown-item divided command="logout">
                <el-icon><SwitchButton /></el-icon>
                退出登录
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
      <div v-else class="auth-actions">
        <router-link to="/login" class="btn-login">登录</router-link>
        <router-link to="/register" class="btn-register">注册</router-link>
      </div>
    </div>
  </header>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const notificationCount = ref(0)

const roleLabels = {
  farmer: '农户',
  buyer: '买家',
  expert: '专家',
  bank: '银行',
  admin: '管理员'
}

const goToHome = () => {
  router.push('/home')
}

const showNotifications = () => {
  ElMessage.info('暂无新通知')
}

const handleUserCommand = (command) => {
  switch (command) {
    case 'profile':
      const profilePath = `/${userStore.role}/profile`
      router.push(profilePath)
      break
    case 'settings':
      ElMessage.info('设置功能开发中')
      break
    case 'logout':
      ElMessageBox.confirm('确定要退出登录吗？', '退出登录', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        userStore.logout()
        ElMessage.success('已退出登录')
        router.push('/home')
      }).catch(() => {})
      break
  }
}
</script>

<style scoped>
.app-header {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  height: var(--header-height, 64px);
  background: var(--color-bg-primary, #ffffff);
  border-bottom: 1px solid var(--color-border, #e5e0d8);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 var(--spacing-6, 24px);
  z-index: var(--z-fixed, 1030);
  backdrop-filter: blur(8px);
}

/* ===== 左侧 Logo ===== */
.header-left {
  flex-shrink: 0;
}

.logo {
  display: flex;
  align-items: center;
  gap: var(--spacing-3, 12px);
  cursor: pointer;
  transition: opacity var(--transition-fast, 150ms ease);
}

.logo:hover {
  opacity: 0.8;
}

.logo svg {
  width: 36px;
  height: 36px;
  color: var(--color-primary, #2d5a3d);
}

.logo-text {
  font-family: var(--font-family-display, 'Noto Serif SC', serif);
  font-size: var(--font-size-xl, 24px);
  font-weight: var(--font-weight-bold, 700);
  color: var(--color-text-primary, #1f2923);
}

/* ===== 中间导航 ===== */
.header-center {
  flex: 1;
  display: flex;
  justify-content: center;
}

.header-nav {
  display: flex;
  gap: var(--spacing-8, 32px);
}

.nav-item {
  display: flex;
  align-items: center;
  gap: var(--spacing-2, 8px);
  padding: var(--spacing-2, 8px) var(--spacing-3, 12px);
  font-size: var(--font-size-sm, 14px);
  font-weight: var(--font-weight-medium, 500);
  color: var(--color-text-secondary, #4a5249);
  border-radius: var(--radius-base, 8px);
  transition: all var(--transition-fast, 150ms ease);
  text-decoration: none;
}

.nav-item svg {
  width: 20px;
  height: 20px;
}

.nav-item:hover {
  color: var(--color-primary, #2d5a3d);
  background: var(--color-bg-secondary, #f7f5f0);
}

.nav-item.router-link-active {
  color: var(--color-primary, #2d5a3d);
  background: rgba(45, 90, 61, 0.08);
}

/* ===== 右侧用户区 ===== */
.header-right {
  flex-shrink: 0;
  display: flex;
  align-items: center;
  gap: var(--spacing-4, 16px);
}

/* 登录状态 */
.user-actions {
  display: flex;
  align-items: center;
  gap: var(--spacing-4, 16px);
}

.notification-badge :deep(.el-badge__content) {
  background: var(--color-error, #b85c38);
  border: 2px solid var(--color-bg-primary, #ffffff);
}

.notification-btn {
  width: 40px;
  height: 40px;
  border: none;
  background: var(--color-bg-secondary, #f7f5f0);
  color: var(--color-text-secondary, #4a5249);
}

.notification-btn:hover {
  background: var(--color-primary-light, #4a7c59);
  color: var(--color-text-inverse, #ffffff);
}

.user-dropdown-trigger {
  display: flex;
  align-items: center;
  gap: var(--spacing-3, 12px);
  padding: var(--spacing-2, 8px);
  cursor: pointer;
  border-radius: var(--radius-base, 8px);
  transition: background var(--transition-fast, 150ms ease);
}

.user-dropdown-trigger:hover {
  background: var(--color-bg-secondary, #f7f5f0);
}

.user-avatar {
  width: 40px;
  height: 40px;
  border-radius: var(--radius-full, 9999px);
  overflow: hidden;
  background: var(--color-primary, #2d5a3d);
  display: flex;
  align-items: center;
  justify-content: center;
}

.user-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.avatar-placeholder {
  font-size: var(--font-size-lg, 20px);
  font-weight: var(--font-weight-semibold, 600);
  color: var(--color-text-inverse, #ffffff);
}

.user-info {
  text-align: left;
}

.user-name {
  font-size: var(--font-size-sm, 14px);
  font-weight: var(--font-weight-medium, 500);
  color: var(--color-text-primary, #1f2923);
  line-height: var(--line-height-tight, 1.25);
}

.user-role {
  font-size: var(--font-size-xs, 12px);
  color: var(--color-text-tertiary, #6b7280);
  line-height: var(--line-height-tight, 1.25);
}

.dropdown-arrow {
  font-size: 12px;
  color: var(--color-text-tertiary, #6b7280);
}

/* 未登录状态 */
.auth-actions {
  display: flex;
  gap: var(--spacing-3, 12px);
}

.btn-login,
.btn-register {
  padding: var(--spacing-2, 8px) var(--spacing-4, 16px);
  font-size: var(--font-size-sm, 14px);
  font-weight: var(--font-weight-medium, 500);
  border-radius: var(--radius-base, 8px);
  text-decoration: none;
  transition: all var(--transition-fast, 150ms ease);
}

.btn-login {
  color: var(--color-primary, #2d5a3d);
  border: 1px solid var(--color-primary, #2d5a3d);
}

.btn-login:hover {
  background: var(--color-primary, #2d5a3d);
  color: var(--color-text-inverse, #ffffff);
}

.btn-register {
  background: var(--color-primary, #2d5a3d);
  color: var(--color-text-inverse, #ffffff);
}

.btn-register:hover {
  background: var(--color-primary-dark, #1f4229);
}

/* ===== 响应式 ===== */
@media (max-width: 768px) {
  .header-nav {
    display: none;
  }

  .user-info {
    display: none;
  }

  .logo-text {
    display: none;
  }
}
</style>
