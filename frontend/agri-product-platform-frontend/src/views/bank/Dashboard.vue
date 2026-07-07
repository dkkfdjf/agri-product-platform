<template>
  <div class="bank-dashboard">
    <h2>数据概览</h2>

    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :span="6">
        <el-card class="stats-card">
          <div class="stats-content">
            <div class="stats-icon" style="background: #FF9800">
              <el-icon :size="30"><Document /></el-icon>
            </div>
            <div class="stats-info">
              <div class="stats-value">{{ stats.pending }}</div>
              <div class="stats-label">待审批申请</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stats-card">
          <div class="stats-content">
            <div class="stats-icon" style="background: #4CAF50">
              <el-icon :size="30"><Select /></el-icon>
            </div>
            <div class="stats-info">
              <div class="stats-value">{{ stats.approved }}</div>
              <div class="stats-label">已批准申请</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stats-card">
          <div class="stats-content">
            <div class="stats-icon" style="background: #F44336">
              <el-icon :size="30"><Close /></el-icon>
            </div>
            <div class="stats-info">
              <div class="stats-value">{{ stats.rejected }}</div>
              <div class="stats-label">已拒绝申请</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stats-card">
          <div class="stats-content">
            <div class="stats-icon" style="background: #2196F3">
              <el-icon :size="30"><Money /></el-icon>
            </div>
            <div class="stats-info">
              <div class="stats-value">¥{{ stats.amount }}</div>
              <div class="stats-label">放款总额（万）</div>
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
        <el-col :span="6">
          <div class="action-item" @click="goTo('/bank/approvals')">
            <el-icon :size="40" color="#FF9800"><DocumentChecked /></el-icon>
            <span>融资审批</span>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="action-item" @click="goTo('/bank/matching')">
            <el-icon :size="40" color="#4CAF50"><Connection /></el-icon>
            <span>智能匹配</span>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="action-item" @click="goTo('/bank/products')">
            <el-icon :size="40" color="#2196F3"><List /></el-icon>
            <span>产品管理</span>
          </div>
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

// 统计数据
const stats = reactive({
  pending: 0,
  approved: 0,
  rejected: 0,
  amount: 0
})

// 跳转
const goTo = (path) => {
  router.push(path)
}

// 加载统计数据
const loadStats = async () => {
  try {
    // TODO: 调用后端接口获取统计数据
    // const res = await dashboardApi.getStats()
    // Object.assign(stats, res.data)

    // 模拟数据
    stats.pending = 15
    stats.approved = 128
    stats.rejected = 12
    stats.amount = 2560
  } catch (error) {
    console.error('加载统计数据失败:', error)
  }
}

onMounted(() => {
  loadStats()
})
</script>

<style scoped>
.bank-dashboard {
  padding: 0;
}

.bank-dashboard > h2 {
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
</style>
