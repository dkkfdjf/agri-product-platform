<template>
  <div class="admin-dashboard">
    <h2>数据概览</h2>

    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :span="6">
        <el-card class="stats-card">
          <div class="stats-content">
            <div class="stats-icon" style="background: #409EFF">
              <el-icon :size="30"><User /></el-icon>
            </div>
            <div class="stats-info">
              <div class="stats-value">{{ stats.users }}</div>
              <div class="stats-label">总用户数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stats-card">
          <div class="stats-content">
            <div class="stats-icon" style="background: #67C23A">
              <el-icon :size="30"><Goods /></el-icon>
            </div>
            <div class="stats-info">
              <div class="stats-value">{{ stats.products }}</div>
              <div class="stats-label">商品总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stats-card">
          <div class="stats-content">
            <div class="stats-icon" style="background: #E6A23C">
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
            <div class="stats-icon" style="background: #909399">
              <el-icon :size="30"><UserFilled /></el-icon>
            </div>
            <div class="stats-info">
              <div class="stats-value">{{ stats.experts }}</div>
              <div class="stats-label">专家数量</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'

// 统计数据
const stats = reactive({
  users: 0,
  products: 0,
  finances: 0,
  experts: 0
})

// 加载统计数据
const loadStats = async () => {
  try {
    // TODO: 调用后端接口获取统计数据
    // const res = await dashboardApi.getStats()
    // Object.assign(stats, res.data)

    // 模拟数据
    stats.users = 1250
    stats.products = 356
    stats.finances = 89
    stats.experts = 25
  } catch (error) {
    console.error('加载统计数据失败:', error)
  }
}

onMounted(() => {
  loadStats()
})
</script>

<style scoped>
.admin-dashboard {
  padding: 0;
}

.admin-dashboard > h2 {
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
</style>
