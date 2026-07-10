<template>
  <div class="farmer-expert-page">
    <div class="page-header">
      <h1>专家咨询</h1>
      <p class="subtitle">向农业专家提问，获取专业解答</p>
    </div>

    <el-tabs v-model="activeTab" class="content-tabs">
      <el-tab-pane label="我的问题" name="questions">
        <div class="list-section">
          <div class="section-header">
            <h3>我的提问</h3>
            <el-button type="primary" size="small" @click="goToAsk">
              <el-icon><Plus /></el-icon>
              提问
            </el-button>
          </div>
          <div class="list-container" v-if="questionList.length > 0" v-loading="loading">
            <div v-for="item in questionList" :key="item.id" class="list-item" @click="goToQuestion(item.id)">
              <div class="item-header">
                <h4 class="item-title">{{ item.title }}</h4>
                <el-tag :type="item.status === 1 ? 'success' : 'warning'" size="small">
                  {{ item.status === 1 ? '已回答' : '待回答' }}
                </el-tag>
              </div>
              <p class="item-content">{{ item.question }}</p>
              <div class="item-meta">
                <span class="meta-item">
                  <el-icon><Calendar /></el-icon>
                  {{ formatDate(item.createTime) }}
                </span>
                <span class="meta-item" v-if="item.expertName">
                  <el-icon><User /></el-icon>
                  专家：{{ item.expertName }}
                </span>
              </div>
            </div>
          </div>
          <el-empty v-else description="暂无提问记录">
            <el-button type="primary" @click="goToAsk">去提问</el-button>
          </el-empty>
        </div>
      </el-tab-pane>

      <el-tab-pane label="我的预约" name="reservations">
        <div class="list-section">
          <div class="section-header">
            <h3>我的预约</h3>
            <el-button type="primary" size="small" @click="goToReserve">
              <el-icon><Plus /></el-icon>
              预约专家
            </el-button>
          </div>
          <div class="list-container" v-if="reserveList.length > 0" v-loading="loading">
            <div v-for="item in reserveList" :key="item.id" class="list-item reserve-item">
              <div class="item-header">
                <h4 class="item-title">预约 {{ item.expertName }}</h4>
                <el-tag :type="getStatusType(item.status)" size="small">
                  {{ getStatusText(item.status) }}
                </el-tag>
              </div>
              <div class="reserve-details">
                <div class="detail-row">
                  <span class="detail-label">作物：</span>
                  <span>{{ item.plantName }}</span>
                </div>
                <div class="detail-row">
                  <span class="detail-label">面积：</span>
                  <span>{{ item.area }}</span>
                </div>
                <div class="detail-row">
                  <span class="detail-label">地址：</span>
                  <span>{{ item.address }}</span>
                </div>
                <div class="detail-row" v-if="item.message">
                  <span class="detail-label">留言：</span>
                  <span>{{ item.message }}</span>
                </div>
                <div class="detail-row" v-if="item.answer && item.status === 1">
                  <span class="detail-label">专家回复：</span>
                  <span class="answer-text">{{ item.answer }}</span>
                </div>
              </div>
              <div class="item-meta">
                <span class="meta-item">
                  <el-icon><Calendar /></el-icon>
                  {{ formatDate(item.createTime) }}
                </span>
              </div>
            </div>
          </div>
          <el-empty v-else description="暂无预约记录">
            <el-button type="primary" @click="goToReserve">去预约</el-button>
          </el-empty>
        </div>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Plus, Calendar, User } from '@element-plus/icons-vue'
import { getQuestionList } from '@/api/question'
import { getMyReserveList } from '@/api/reserve'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

const activeTab = ref('questions')
const questionList = ref([])
const reserveList = ref([])
const loading = ref(false)

// 加载我的问题
const loadMyQuestions = async () => {
  loading.value = true
  try {
    const userName = userStore.userInfo?.userName || userStore.userName
    const res = await getQuestionList({ page: 1, pageSize: 100, questioner: userName })
    if (res.code === 200) {
      questionList.value = (res.data.records || []).map(q => ({
        id: q.questionId || q.id,
        title: q.title,
        question: q.question,
        status: q.status,
        expertName: q.expertName,
        createTime: q.createTime
      }))
    }
  } catch (error) {
    console.error('加载问题失败:', error)
  } finally {
    loading.value = false
  }
}

// 加载我的预约
const loadMyReservations = async () => {
  loading.value = true
  try {
    const res = await getMyReserveList({ page: 1, pageSize: 100 })
    if (res.code === 200) {
      reserveList.value = (res.data.records || []).map(r => ({
        id: r.id,
        expertName: r.expertName,
        plantName: r.plantName,
        area: r.area,
        address: r.address,
        message: r.message,
        answer: r.answer,
        status: r.status,
        createTime: r.createTime
      }))
    }
  } catch (error) {
    console.error('加载预约失败:', error)
  } finally {
    loading.value = false
  }
}

const goToQuestion = (id) => {
  router.push(`/question/${id}`)
}

const goToAsk = () => {
  router.push('/question/ask')
}

const goToReserve = () => {
  router.push('/reserve')
}

const formatDate = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return date.toLocaleDateString()
}

const getStatusType = (status) => {
  const types = { 0: 'warning', 1: 'success', 2: 'danger' }
  return types[status] || 'info'
}

const getStatusText = (status) => {
  const texts = { 0: '待处理', 1: '已完成', 2: '已拒绝' }
  return texts[status] || '未知'
}

onMounted(() => {
  loadMyQuestions()
  loadMyReservations()
})
</script>

<style scoped>
.farmer-expert-page {
  padding: var(--spacing-6, 24px);
  max-width: 1000px;
  margin: 0 auto;
}

.page-header {
  margin-bottom: var(--spacing-6, 24px);
}

.page-header h1 {
  font-size: var(--font-size-3xl, 38px);
  font-weight: var(--font-weight-bold, 700);
  color: var(--color-text-primary, #1f2923);
  margin: 0 0 var(--spacing-2, 8px);
}

.subtitle {
  font-size: var(--font-size-base, 16px);
  color: var(--color-text-tertiary, #6b7280);
  margin: 0;
}

.content-tabs {
  background: var(--color-bg-primary, #ffffff);
  border-radius: var(--radius-lg, 12px);
  padding: var(--spacing-6, 24px);
  box-shadow: var(--shadow-sm, 0 2px 4px rgba(31, 41, 35, 0.06));
}

.list-section {
  padding: var(--spacing-4, 16px) 0;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--spacing-4, 16px);
}

.section-header h3 {
  font-size: var(--font-size-xl, 24px);
  font-weight: var(--font-weight-semibold, 600);
  color: var(--color-text-primary, #1f2923);
  margin: 0;
}

.list-container {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-3, 12px);
}

.list-item {
  padding: var(--spacing-4, 16px);
  background: var(--color-bg-secondary, #f7f5f0);
  border: 1px solid var(--color-border, #e5e0d8);
  border-radius: var(--radius-base, 8px);
  cursor: pointer;
  transition: all var(--transition-fast, 150ms ease);
}

.list-item:hover {
  border-color: var(--color-primary, #2d5a3d);
  box-shadow: var(--shadow-sm, 0 2px 8px rgba(45, 90, 61, 0.1));
}

.reserve-item {
  cursor: default;
}

.item-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: var(--spacing-3, 12px);
}

.item-title {
  font-size: var(--font-size-lg, 18px);
  font-weight: var(--font-weight-semibold, 600);
  color: var(--color-text-primary, #1f2923);
  margin: 0;
}

.item-content {
  color: var(--color-text-secondary, #4a5249);
  line-height: 1.6;
  margin-bottom: var(--spacing-3, 12px);
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.reserve-details {
  margin-bottom: var(--spacing-3, 12px);
}

.detail-row {
  display: flex;
  gap: var(--spacing-2, 8px);
  margin-bottom: var(--spacing-1, 4px);
  color: var(--color-text-secondary, #4a5249);
  line-height: 1.6;
}

.detail-label {
  font-weight: var(--font-weight-medium, 500);
  color: var(--color-text-tertiary, #6b7280);
  min-width: 70px;
}

.answer-text {
  color: var(--color-success, #4a7c59);
  font-weight: var(--font-weight-medium, 500);
}

.item-meta {
  display: flex;
  gap: var(--spacing-4, 16px);
  padding-top: var(--spacing-2, 8px);
  border-top: 1px solid var(--color-divider, #edebe6);
}

.meta-item {
  display: flex;
  align-items: center;
  gap: var(--spacing-1, 4px);
  color: var(--color-text-tertiary, #6b7280);
  font-size: var(--font-size-sm, 14px);
}
</style>
