<template>
  <div class="home">
    <el-container>
      <!-- 顶部导航栏 -->
      <el-header class="header">
        <div class="header-content">
          <h1>农产品融销一体平台</h1>
        </div>
      </el-header>

      <!-- 主内容区 -->
      <el-main class="main">
        <el-card class="welcome-card">
          <template #header>
            <div class="card-header">
              <span>欢迎使用</span>
            </div>
          </template>
          <div class="welcome-content">
            <h2>🌾 农产品融销一体平台</h2>
            <p>基于数字经济的农产品交易平台</p>
            <el-divider />
            <el-space direction="vertical" :size="20">
              <div class="feature-item">
                <el-icon><Goods /></el-icon>
                <span>农产品交易</span>
              </div>
              <div class="feature-item">
                <el-icon><Wallet /></el-icon>
                <span>融资服务</span>
              </div>
              <div class="feature-item">
                <el-icon><User /></el-icon>
                <span>专家助力</span>
              </div>
            </el-space>
          </div>
        </el-card>
      </el-main>
    </el-container>
  </div>
</template>

<script setup>
import { Goods, Wallet, User } from '@element-plus/icons-vue'
</script>

<style scoped>
.home {
  min-height: 100vh;
}

.header {
  background-color: #4CAF50;
  color: white;
  display: flex;
  align-items: center;
  padding: 0 20px;
}

.header-content h1 {
  font-size: 24px;
  margin: 0;
}

.main {
  padding: 20px;
}

.welcome-card {
  max-width: 800px;
  margin: 0 auto;
}

.card-header {
  font-size: 18px;
  font-weight: bold;
}

.welcome-content {
  text-align: center;
  padding: 20px;
}

.welcome-content h2 {
  color: #4CAF50;
  margin-bottom: 10px;
}

.welcome-content p {
  color: #666;
  margin-bottom: 30px;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 16px;
}

.feature-item .el-icon {
  font-size: 24px;
  color: #4CAF50;
}
</style>
