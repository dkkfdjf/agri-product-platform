<template>
  <el-container class="bank-layout">
    <el-header class="layout-header">
      <div class="header-left">
        <h2>农产品融销一体平台 - 银行中心</h2>
      </div>
      <div class="header-right">
        <span class="welcome-text">欢迎，{{ userStore.userInfo?.bankName || userStore.userInfo?.userName }}</span>
        <el-button type="primary" plain @click="goHome">返回首页</el-button>
        <el-button type="danger" plain @click="handleLogout">退出登录</el-button>
      </div>
    </el-header>

    <el-container>
      <el-aside width="200px" class="layout-aside">
        <el-menu
          :default-active="activeMenu"
          router
          background-color="#545c64"
          text-color="#fff"
          active-text-color="#4CAF50"
        >
          <el-menu-item index="/bank/dashboard">
            <el-icon><Odometer /></el-icon>
            <span>数据概览</span>
          </el-menu-item>
          <el-menu-item index="/bank/approvals">
            <el-icon><DocumentChecked /></el-icon>
            <span>融资审批</span>
          </el-menu-item>
          <el-menu-item index="/bank/matching">
            <el-icon><Connection /></el-icon>
            <span>智能匹配</span>
          </el-menu-item>
          <el-menu-item index="/bank/products">
            <el-icon><List /></el-icon>
            <span>产品管理</span>
          </el-menu-item>
        </el-menu>
      </el-aside>

      <el-main class="layout-main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessageBox } from 'element-plus'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const activeMenu = computed(() => route.path)

const goHome = () => {
  router.push('/')
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
.bank-layout {
  min-height: 100vh;
}

.layout-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background-color: #4CAF50;
  color: #fff;
  padding: 0 20px;
}

.layout-header h2 {
  margin: 0;
  font-size: 20px;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 15px;
}

.welcome-text {
  color: #fff;
  font-size: 14px;
}

.layout-aside {
  background-color: #545c64;
  height: calc(100vh - 60px);
}

.layout-main {
  background-color: #f5f7fa;
  padding: 20px;
  height: calc(100vh - 60px);
  overflow-y: auto;
}

:deep(.el-menu) {
  border-right: none;
}
</style>
