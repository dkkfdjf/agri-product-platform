<template>
  <div class="buyer-dashboard">
    <h2>个人中心</h2>

    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :span="6">
        <el-card class="stats-card">
          <div class="stats-content">
            <div class="stats-icon" style="background: #4CAF50">
              <el-icon :size="30"><ShoppingCart /></el-icon>
            </div>
            <div class="stats-info">
              <div class="stats-value">{{ stats.cartItems }}</div>
              <div class="stats-label">购物车商品</div>
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
              <el-icon :size="30"><ChatDotRound /></el-icon>
            </div>
            <div class="stats-info">
              <div class="stats-value">{{ stats.questions }}</div>
              <div class="stats-label">咨询数量</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stats-card">
          <div class="stats-content">
            <div class="stats-icon" style="background: #9C27B0">
              <el-icon :size="30"><Coin /></el-icon>
            </div>
            <div class="stats-info">
              <div class="stats-value">{{ userStore.userInfo?.integral || 0 }}</div>
              <div class="stats-label">我的积分</div>
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
          <div class="action-item" @click="goTo('/buyer/cart')">
            <el-icon :size="40" color="#4CAF50"><ShoppingCart /></el-icon>
            <span>购物车</span>
          </div>
        </el-col>
        <el-col :span="4">
          <div class="action-item" @click="goTo('/buyer/orders')">
            <el-icon :size="40" color="#2196F3"><List /></el-icon>
            <span>我的订单</span>
          </div>
        </el-col>
        <el-col :span="4">
          <div class="action-item" @click="goTo('/buyer/expert')">
            <el-icon :size="40" color="#FF9800"><User /></el-icon>
            <span>专家咨询</span>
          </div>
        </el-col>
        <el-col :span="4">
          <div class="action-item" @click="goTo('/market')">
            <el-icon :size="40" color="#F44336"><Goods /></el-icon>
            <span>农产品交易</span>
          </div>
        </el-col>
        <el-col :span="4">
          <div class="action-item" @click="goTo('/finance-products')">
            <el-icon :size="40" color="#009688"><Money /></el-icon>
            <span>融资产品</span>
          </div>
        </el-col>
        <el-col :span="4">
          <div class="action-item" @click="goTo('/buyer/profile')">
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
        <el-descriptions-item label="姓名">{{ userStore.userInfo?.realName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ userStore.userInfo?.phone || '-' }}</el-descriptions-item>
        <el-descriptions-item label="信誉分">{{ userStore.userInfo?.credit || 5 }}/5</el-descriptions-item>
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
  cartItems: 0,
  orders: 0,
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
    stats.cartItems = 5
    stats.orders = 12
    stats.questions = 3
  } catch (error) {
    console.error('加载统计数据失败:', error)
  }
}

loadStats()
</script>

<style scoped>
.buyer-dashboard {
  padding: 0;
}

.buyer-dashboard > h2 {
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
