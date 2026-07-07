<template>
  <div class="expert-dashboard">
    <h2>个人中心</h2>

    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :span="6">
        <el-card class="stats-card">
          <div class="stats-content">
            <div class="stats-icon" style="background: #4CAF50">
              <el-icon :size="30"><Document /></el-icon>
            </div>
            <div class="stats-info">
              <div class="stats-value">{{ stats.knowledge }}</div>
              <div class="stats-label">发布知识</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stats-card">
          <div class="stats-content">
            <div class="stats-icon" style="background: #2196F3">
              <el-icon :size="30"><ChatDotRound /></el-icon>
            </div>
            <div class="stats-info">
              <div class="stats-value">{{ stats.answered }}</div>
              <div class="stats-label">已回答问题</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stats-card">
          <div class="stats-content">
            <div class="stats-icon" style="background: #FF9800">
              <el-icon :size="30"><Calendar /></el-icon>
            </div>
            <div class="stats-info">
              <div class="stats-value">{{ stats.reservations }}</div>
              <div class="stats-label">待处理预约</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stats-card">
          <div class="stats-content">
            <div class="stats-icon" style="background: #9C27B0">
              <el-icon :size="30"><ChatLineSquare /></el-icon>
            </div>
            <div class="stats-info">
              <div class="stats-value">{{ stats.pending }}</div>
              <div class="stats-label">待回答问题</div>
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
          <div class="action-item" @click="goTo('/expert/knowledge')">
            <el-icon :size="40" color="#4CAF50"><Document /></el-icon>
            <span>发布知识</span>
          </div>
        </el-col>
        <el-col :span="4">
          <div class="action-item" @click="goTo('/expert/questions')">
            <el-icon :size="40" color="#2196F3"><ChatDotRound /></el-icon>
            <span>回答问题</span>
          </div>
        </el-col>
        <el-col :span="4">
          <div class="action-item" @click="goTo('/expert/reservations')">
            <el-icon :size="40" color="#FF9800"><Calendar /></el-icon>
            <span>查看预约</span>
          </div>
        </el-col>
        <el-col :span="4">
          <div class="action-item" @click="goTo('/expert/profile')">
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
        <el-descriptions-item label="专家姓名">{{ userStore.userInfo?.expertName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="专业领域">{{ userStore.userInfo?.specialty || '-' }}</el-descriptions-item>
        <el-descriptions-item label="职称">{{ userStore.userInfo?.title || '-' }}</el-descriptions-item>
      </el-descriptions>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

// 统计数据
const stats = reactive({
  knowledge: 0,
  answered: 0,
  reservations: 0,
  pending: 0
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
    stats.knowledge = 15
    stats.answered = 32
    stats.reservations = 3
    stats.pending = 5
  } catch (error) {
    console.error('加载统计数据失败:', error)
  }
}

onMounted(() => {
  loadStats()
})
</script>

<style scoped>
.expert-dashboard {
  padding: 0;
}

.expert-dashboard > h2 {
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
