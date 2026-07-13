<template>
  <div class="question-detail-page">
    <div class="container">
      <!-- 返回按钮 -->
      <el-button @click="handleBack" class="back-btn" link>
        <el-icon><ArrowLeft /></el-icon>
        返回列表
      </el-button>

      <!-- 加载状态 -->
      <el-skeleton v-if="loading" :rows="10" animated />

      <!-- 问题详情 -->
      <div v-else-if="question.id" class="question-detail">
        <!-- 问题内容 -->
        <article class="question-article">
          <header class="question-header">
            <div class="status-badge" :class="`status-${question.status}`">
              {{ getStatusText(question.status) }}
            </div>
            <h1 class="question-title">{{ question.title }}</h1>
            <div class="question-meta">
              <span class="meta-item">
                <el-icon><User /></el-icon>
                提问者：{{ question.questioner }}
              </span>
              <span class="meta-item" v-if="question.plantName">
                <el-icon><ChatDotRound /></el-icon>
                农作物：{{ question.plantName }}
              </span>
              <span class="meta-item" v-if="question.phone">
                <el-icon><Phone /></el-icon>
                {{ question.phone }}
              </span>
              <span class="meta-item">
                <el-icon><Calendar /></el-icon>
                {{ formatDate(question.createTime) }}
              </span>
            </div>
          </header>

          <div class="question-content">
            {{ question.question }}
          </div>

          <!-- 操作按钮 -->
          <div class="question-actions" v-if="canClose || canDelete">
            <el-button
              v-if="canClose"
              type="warning"
              @click="handleCloseQuestion"
              :disabled="question.status === 2"
            >
              <el-icon><Close /></el-icon>
              关闭问题
            </el-button>
            <el-button v-if="canDelete" type="danger" @click="handleDeleteQuestion">
              <el-icon><Delete /></el-icon>
              删除问题
            </el-button>
          </div>
        </article>

        <!-- 回答区 -->
        <div class="answers-section">
          <h3 class="section-title">专家解答</h3>

          <!-- 专家回答表单 -->
          <div v-if="canAnswer" class="answer-form">
            <el-input
              v-model="answerContent"
              type="textarea"
              :rows="5"
              placeholder="请输入您的专业解答..."
              maxlength="2000"
              show-word-limit
            />
            <el-button
              type="primary"
              @click="handleSubmitAnswer"
              :loading="submitting"
              :disabled="!answerContent.trim()"
            >
              提交回答
            </el-button>
          </div>

          <!-- 已有回答 -->
          <div v-if="question.answer" class="answer-item">
            <div class="answer-header">
              <div class="expert-info">
                <el-avatar :size="40">{{ question.expertName?.charAt(0) || '专' }}</el-avatar>
                <div class="expert-detail">
                  <div class="expert-name">{{ question.expertName }}</div>
                  <div class="expert-title">农业专家</div>
                </div>
              </div>
              <span class="answer-time" v-if="question.updateTime">{{ formatDate(question.updateTime) }}</span>
            </div>
            <div class="answer-content">{{ question.answer }}</div>
          </div>
          <el-empty
            v-else
            :description="canAnswer ? '暂无回答，快来解答吧！' : '暂无回答，等待专家解答中...'"
          />
        </div>
      </div>

      <!-- 空状态 -->
      <el-empty v-else description="问题不存在或已被删除" />
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ArrowLeft, User, Calendar, Close, Delete, Phone, ChatDotRound } from '@element-plus/icons-vue'
import { getQuestionDetail, answerQuestion, closeQuestion, deleteQuestion } from '@/api/question'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const loading = ref(true)
const submitting = ref(false)
const question = ref({})
const answerContent = ref('')

const currentUserName = computed(() => userStore.userInfo?.userName || '')

// 是否可以回答（分配给该专家且未回答；或未指派专家时任意专家可答）
const canAnswer = computed(() => {
  if (!userStore.isLoggedIn() || !userStore.hasRole(['expert'])) return false
  if (question.value.status !== 0) return false
  const assigned = question.value.expertName
  return !assigned || assigned === currentUserName.value
})

// 是否可以关闭问题（提问者本人）
const canClose = computed(() => {
  return question.value.questioner === currentUserName.value && question.value.status !== 2
})

// 是否可以删除（提问者本人）
const canDelete = computed(() => {
  return question.value.questioner === currentUserName.value
})

// 获取状态文本（后端为整数：0未回答 1已回答 2已关闭）
const getStatusText = (status) => {
  const map = { 0: '待回答', 1: '已回答', 2: '已关闭' }
  return map[status] || '未知'
}

// 格式化日期
const formatDate = (dateStr) => {
  if (!dateStr) return ''
  return new Date(dateStr).toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// 返回列表
const handleBack = () => {
  router.push('/question')
}

// 关闭问题
const handleCloseQuestion = async () => {
  try {
    await ElMessageBox.confirm('确定要关闭这个问题吗？关闭后将无法继续回答。', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await closeQuestion(question.value.id)
    ElMessage.success('问题已关闭')
    await loadQuestion()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('操作失败')
    }
  }
}

// 删除问题
const handleDeleteQuestion = async () => {
  try {
    await ElMessageBox.confirm('确定要删除这个问题吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteQuestion(question.value.id)
    ElMessage.success('删除成功')
    router.push('/question')
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('操作失败')
    }
  }
}

// 提交回答
const handleSubmitAnswer = async () => {
  if (!answerContent.value.trim()) {
    ElMessage.warning('请输入回答内容')
    return
  }
  submitting.value = true
  try {
    await answerQuestion(question.value.id, { answer: answerContent.value.trim() })
    ElMessage.success('回答成功')
    answerContent.value = ''
    await loadQuestion()
  } catch (error) {
    ElMessage.error(error.message || '回答失败')
  } finally {
    submitting.value = false
  }
}

// 加载问题详情
const loadQuestion = async () => {
  loading.value = true
  try {
    const res = await getQuestionDetail(route.params.id)
    question.value = res.data || {}
  } catch (error) {
    ElMessage.error('加载失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadQuestion()
})
</script>

<style scoped>
.question-detail-page {
  min-height: 100vh;
  background: var(--color-bg-page, #f5f3ef);
  padding: var(--spacing-6, 24px) 0;
}

.container {
  max-width: 900px;
  margin: 0 auto;
  padding: 0 var(--spacing-6, 24px);
}

.back-btn {
  margin-bottom: var(--spacing-4, 16px);
  font-size: var(--font-size-base, 16px);
}

/* ===== 问题内容 ===== */
.question-detail {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-6, 24px);
}

.question-article {
  background: var(--color-bg-primary, #ffffff);
  border-radius: var(--radius-lg, 12px);
  padding: var(--spacing-8, 32px);
  box-shadow: var(--shadow-sm, 0 2px 8px rgba(0,0,0,0.05));
}

.question-header {
  margin-bottom: var(--spacing-6, 24px);
  padding-bottom: var(--spacing-6, 24px);
  border-bottom: 1px solid var(--color-border, #e5e0d8);
}

.status-badge {
  display: inline-block;
  padding: var(--spacing-1, 4px) var(--spacing-3, 12px);
  font-size: var(--font-size-xs, 12px);
  font-weight: var(--font-weight-medium, 500);
  border-radius: var(--radius-full, 9999px);
  margin-bottom: var(--spacing-3, 12px);
}

.status-0 {
  color: #e6a23c;
  background: #fdf6ec;
}

.status-1 {
  color: #67c23a;
  background: #f0f9ff;
}

.status-2 {
  color: #909399;
  background: #f4f4f5;
}

.question-title {
  font-family: var(--font-family-display, 'Noto Serif SC', serif);
  font-size: var(--font-size-3xl, 38px);
  font-weight: var(--font-weight-bold, 700);
  color: var(--color-text-primary, #1f2923);
  margin: 0 0 var(--spacing-4, 16px);
  line-height: 1.4;
}

.question-meta {
  display: flex;
  gap: var(--spacing-4, 16px);
  flex-wrap: wrap;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: var(--spacing-1, 4px);
  font-size: var(--font-size-sm, 14px);
  color: var(--color-text-tertiary, #6b7280);
}

.question-content {
  font-size: var(--font-size-lg, 20px);
  line-height: 1.8;
  color: var(--color-text-primary, #1f2923);
  white-space: pre-wrap;
}

.question-actions {
  margin-top: var(--spacing-6, 24px);
  padding-top: var(--spacing-6, 24px);
  border-top: 1px solid var(--color-border, #e5e0d8);
  display: flex;
  gap: var(--spacing-3, 12px);
}

/* ===== 回答区 ===== */
.answers-section {
  background: var(--color-bg-primary, #ffffff);
  border-radius: var(--radius-lg, 12px);
  padding: var(--spacing-6, 24px);
  box-shadow: var(--shadow-sm, 0 2px 8px rgba(0,0,0,0.05));
}

.section-title {
  font-size: var(--font-size-xl, 24px);
  font-weight: var(--font-weight-semibold, 600);
  color: var(--color-text-primary, #1f2923);
  margin: 0 0 var(--spacing-4, 16px);
}

.answer-form {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-3, 12px);
  margin-bottom: var(--spacing-6, 24px);
  padding: var(--spacing-4, 16px);
  background: var(--color-bg-secondary, #f7f5f0);
  border-radius: var(--radius-base, 8px);
}

.answer-item {
  padding: var(--spacing-5, 20px);
  background: var(--color-bg-secondary, #f7f5f0);
  border-radius: var(--radius-base, 8px);
}

.answer-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--spacing-3, 12px);
}

.expert-info {
  display: flex;
  align-items: center;
  gap: var(--spacing-3, 12px);
}

.expert-detail {
  display: flex;
  flex-direction: column;
}

.expert-name {
  font-weight: var(--font-weight-semibold, 600);
  color: var(--color-text-primary, #1f2923);
}

.expert-title {
  font-size: var(--font-size-xs, 12px);
  color: var(--color-primary, #2d5a3d);
}

.answer-time {
  font-size: var(--font-size-xs, 12px);
  color: var(--color-text-tertiary, #6b7280);
}

.answer-content {
  font-size: var(--font-size-base, 16px);
  line-height: 1.6;
  color: var(--color-text-primary, #1f2923);
  white-space: pre-wrap;
}

/* ===== 响应式 ===== */
@media (max-width: 768px) {
  .question-title {
    font-size: var(--font-size-2xl, 28px);
  }

  .question-article,
  .answers-section {
    padding: var(--spacing-5, 20px);
  }

  .answer-header {
    flex-direction: column;
    align-items: flex-start;
    gap: var(--spacing-2, 8px);
  }
}
</style>
