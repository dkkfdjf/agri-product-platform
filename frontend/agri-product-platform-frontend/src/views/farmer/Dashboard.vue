<template>
  <div class="farmer-dashboard">
    <h2>个人中心</h2>

    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :span="6">
        <el-card class="stats-card">
          <div class="stats-content">
            <div class="stats-icon" style="background: #4CAF50">
              <el-icon :size="30"><Goods /></el-icon>
            </div>
            <div class="stats-info">
              <div class="stats-value">{{ stats.products }}</div>
              <div class="stats-label">已发布商品</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stats-card">
          <div class="stats-content">
            <div class="stats-icon" style="background: #2196F3">
              <el-icon :size="30"><List /></el-icon>
            </div>
            <div class="stats-info">
              <div class="stats-value">{{ stats.orders }}</div>
              <div class="stats-label">订单数量</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stats-card">
          <div class="stats-content">
            <div class="stats-icon" style="background: #FF9800">
              <el-icon :size="30"><Money /></el-icon>
            </div>
            <div class="stats-info">
              <div class="stats-value">{{ stats.finances }}</div>
              <div class="stats-label">融资申请</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stats-card">
          <div class="stats-content">
            <div class="stats-icon" style="background: #9C27B0">
              <el-icon :size="30"><ChatDotRound /></el-icon>
            </div>
            <div class="stats-info">
              <div class="stats-value">{{ stats.questions }}</div>
              <div class="stats-label">咨询数量</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 快速入口 -->
    <el-card class="quick-actions">
      <template #header>
        <span>快速入口</span>
      </template>
      <el-row :gutter="15">
        <el-col :span="4">
          <div class="action-item" @click="goTo('/farmer/products')">
            <el-icon :size="40" color="#4CAF50"><Goods /></el-icon>
            <span>发布商品</span>
          </div>
        </el-col>
        <el-col :span="4">
          <div class="action-item" @click="goTo('/farmer/orders')">
            <el-icon :size="40" color="#2196F3"><List /></el-icon>
            <span>查看订单</span>
          </div>
        </el-col>
        <el-col :span="4">
          <div class="action-item" @click="goTo('/farmer/finance')">
            <el-icon :size="40" color="#FF9800"><Money /></el-icon>
            <span>申请融资</span>
          </div>
        </el-col>
        <el-col :span="4">
          <div class="action-item" @click="goTo('/farmer/expert')">
            <el-icon :size="40" color="#9C27B0"><User /></el-icon>
            <span>专家咨询</span>
          </div>
        </el-col>
        <el-col :span="4">
          <div class="action-item" @click="goTo('/market')">
            <el-icon :size="40" color="#F44336"><ShoppingCart /></el-icon>
            <span>农产品交易</span>
          </div>
        </el-col>
        <el-col :span="4">
          <div class="action-item" @click="goTo('/farmer/profile')">
            <el-icon :size="40" color="#607D8B"><Setting /></el-icon>
            <span>账户设置</span>
          </div>
        </el-col>
      </el-row>
    </el-card>

    <!-- 基本信息 -->
    <el-card class="user-info">
      <template #header>
        <span>基本信息</span>
      </template>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="账号">{{ userStore.userInfo?.userName }}</el-descriptions-item>
        <el-descriptions-item label="农场名称">{{ userStore.userInfo?.farmName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ userStore.userInfo?.phone || '-' }}</el-descriptions-item>
        <el-descriptions-item label="地址">{{ userStore.userInfo?.address || '-' }}</el-descriptions-item>
      </el-descriptions>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

// 统计数据
const stats = reactive({
  products: 0,
  orders: 0,
  finances: 0,
  questions: 0
})

// 跳转
const goTo = (path) => {
  router.push(path)
}

// 加载统计数据（等待后端接口完成后实现）
const loadStats = async () => {
  try {
    // TODO: 调用后端接口获取统计数据
    // const res = await dashboardApi.getStats()
    // Object.assign(stats, res.data)

    // 模拟数据
    stats.products = 12
    stats.orders = 8
    stats.finances = 3
    stats.questions = 5
  } catch (error) {
    console.error('加载统计数据失败:', error)
  }
}

loadStats()
</script>

<style scoped>
.farmer-dashboard {
  padding: 0;
}

.farmer-dashboard > h2 {
  margin-bottom: 20px;
  color: #333;
}

.stats-row {
  margin-bottom: 20px;
}

.stats-card {
  cursor: pointer;
  transition: transform 0.2s;
}

.stats-card:hover {
  transform: translateY(-5px);
}

.stats-content {
  display: flex;
  align-items: center;
  gap: 20px;
}

.stats-icon {
  width: 60px;
  height: 60px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
}

.stats-info {
  flex: 1;
}

.stats-value {
  font-size: 24px;
  font-weight: bold;
  color: #333;
}

.stats-label {
  font-size: 14px;
  color: #999;
  margin-top: 5px;
}

.quick-actions {
  margin-bottom: 20px;
}

.action-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 20px;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;
  background: #fff;
}

.action-item:hover {
  border-color: #4CAF50;
  box-shadow: 0 2px 8px rgba(76, 175, 80, 0.2);
}

.action-item span {
  margin-top: 10px;
  font-size: 14px;
  color: #666;
}

.user-info {
  margin-bottom: 20px;
}
</style>
