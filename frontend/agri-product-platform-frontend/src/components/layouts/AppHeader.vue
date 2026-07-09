<template>
  <header class="app-header">
    <div class="header-inner">
      <!-- Logo -->
      <div class="logo" @click="goTo('/')">
        <div class="logo-icon">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
            <path d="M12 2L12 12M12 12L8 8M12 12L16 8M12 12L12 22M12 22L8 18M12 22L16 18" />
            <circle cx="12" cy="3" r="1.5" fill="currentColor"/>
          </svg>
        </div>
        <span class="logo-text">农融汇</span>
      </div>

      <!-- 导航菜单 -->
      <nav class="main-nav" v-if="showNav">
        <a href="/home" @click.prevent="scrollToSection('')">首页</a>
        <a href="/market" @click.prevent="navigateTo('/market')">农产品</a>
        <a href="/finance-products" @click.prevent="navigateTo('/finance-products')">融资服务</a>
        <a href="/expert-help" @click.prevent="navigateTo('/expert-help')">专家助力</a>
      </nav>

      <!-- 右侧操作区 -->
      <div class="header-actions">
        <template v-if="!isLoggedIn">
          <button class="btn-text" @click="navigateTo('/login')">登录</button>
          <button class="btn-primary" @click="navigateTo('/register')">免费注册</button>
        </template>
        <template v-else>
          <el-dropdown @command="handleUserCommand" trigger="click">
            <div class="user-chip">
              <span class="user-name">{{ getUserDisplayName() }}</span>
              <svg width="16" height="16" viewBox="0 0 16 16" fill="currentColor">
                <path d="M4 6l4 4 4-4"/>
              </svg>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item :command="getDashboardPath()">
                  个人中心
                </el-dropdown-item>
                <el-dropdown-item divided command="logout">
                  退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </template>
      </div>
    </div>
  </header>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'

const props = defineProps({
  showNav: {
    type: Boolean,
    default: true
  }
})

const router = useRouter()
const userStore = useUserStore()

const isLoggedIn = computed(() => userStore.isLoggedIn())

// 获取用户显示名称
const getUserDisplayName = () => {
  const userInfo = userStore.userInfo
  if (!userInfo) return ''
  const role = userStore.role
  switch (role) {
    case 'farmer':
      return userInfo.farmName || userInfo.realName || userInfo.userName
    case 'expert':
      return userInfo.expertName || userInfo.realName || userInfo.userName
    case 'bank':
      return userInfo.bankName || userInfo.realName || userInfo.userName
    case 'admin':
      return '管理员'
    default:
      return userInfo.realName || userInfo.userName
  }
}

// 获取用户主页路径
const getDashboardPath = () => {
  const role = userStore.role
  switch (role) {
    case 'farmer':
      return '/farmer/dashboard'
    case 'expert':
      return '/expert/dashboard'
    case 'bank':
      return '/bank/dashboard'
    case 'admin':
      return '/admin/dashboard'
    default:
      return '/login'
  }
}

// 处理用户命令
const handleUserCommand = (command) => {
  if (command === 'logout') {
    userStore.logout()
    ElMessage.success('已退出登录')
    navigateTo('/login')
  } else {
    router.push(command)
  }
}

// 页面跳转
const navigateTo = (path) => {
  router.push(path)
}

// 滚动到指定区域
const scrollToSection = () => {
  router.push('/')
}
</script>

<style scoped>
.app-header {
  position: relative;
  height: 72px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-bottom: 1px solid var(--color-border);
  z-index: var(--z-sticky);
}

.header-inner {
  max-width: 1280px;
  margin: 0 auto;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 32px;
}

.logo {
  display: flex;
  align-items: center;
  gap: 12px;
  cursor: pointer;
  transition: opacity var(--transition-base);
}

.logo:hover {
  opacity: 0.8;
}

.logo-icon {
  width: 36px;
  height: 36px;
  color: var(--color-primary);
}

.logo-text {
  font-family: var(--font-display);
  font-size: 20px;
  font-weight: var(--font-semibold);
  color: var(--color-primary);
}

.main-nav {
  display: flex;
  gap: 40px;
}

.main-nav a {
  font-size: 15px;
  font-weight: var(--font-medium);
  color: var(--color-text-secondary);
  transition: color var(--transition-base);
  position: relative;
  text-decoration: none;
}

.main-nav a:hover,
.main-nav a.router-link-active {
  color: var(--color-primary);
}

.main-nav a::after {
  content: '';
  position: absolute;
  bottom: -28px;
  left: 0;
  right: 0;
  height: 2px;
  background: var(--color-primary);
  transform: scaleX(0);
  transition: transform var(--transition-base);
}

.main-nav a:hover::after {
  transform: scaleX(1);
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 16px;
}

.btn-text {
  padding: 8px 20px;
  font-size: 15px;
  font-weight: var(--font-medium);
  color: var(--color-text);
  background: transparent;
  border: none;
  cursor: pointer;
  transition: color var(--transition-base);
}

.btn-text:hover {
  color: var(--color-primary);
}

.btn-primary {
  padding: 10px 24px;
  font-size: 15px;
  font-weight: var(--font-medium);
  color: var(--color-white);
  background: var(--color-primary);
  border: none;
  border-radius: var(--radius-md);
  cursor: pointer;
  transition: background var(--transition-base);
}

.btn-primary:hover {
  background: var(--color-primary-dark);
}

.user-chip {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  background: var(--color-bg);
  border-radius: var(--radius-full);
  cursor: pointer;
  transition: background var(--transition-base);
}

.user-chip:hover {
  background: var(--color-border);
}

.user-name {
  font-size: 14px;
  font-weight: var(--font-medium);
  color: var(--color-text);
}

@media (max-width: 768px) {
  .header-inner {
    padding: 0 20px;
  }

  .main-nav {
    display: none;
  }
}
</style>
