<template>
  <div class="expert-help-page">
    <!-- 页面标题区 -->
    <div class="page-header">
      <h1 class="page-title">专家助力</h1>
      <p class="page-subtitle">农业专家在线，为您答疑解惑</p>
    </div>

    <!-- 内容区 -->
    <div class="content-grid">
      <!-- 专家列表 -->
      <div class="experts-section">
        <h2 class="section-title">认证专家</h2>
        <div class="experts-list">
          <div v-for="expert in experts" :key="expert.id" class="expert-card">
            <div class="expert-avatar">
              <img v-if="expert.avatar" :src="expert.avatar" :alt="expert.name">
              <span v-else>{{ expert.name.charAt(0) }}</span>
            </div>
            <div class="expert-info">
              <div class="expert-name">{{ expert.name }}</div>
              <div class="expert-title">{{ expert.title }}</div>
              <div class="expert-specialty">{{ expert.specialty }}</div>
              <div class="expert-stats">
                <span class="stat-item">{{ expert.answersCount }} 回答</span>
                <span class="stat-item">满意度 {{ expert.satisfactionRate }}%</span>
              </div>
            </div>
            <div class="expert-actions">
              <el-button type="primary" size="small" @click="handleAskExpert(expert)">提问</el-button>
            </div>
          </div>
        </div>
      </div>

      <!-- 热门问题 -->
      <div class="questions-section">
        <h2 class="section-title">热门问答</h2>
        <div class="questions-list">
          <div v-for="question in hotQuestions" :key="question.id" class="question-item">
            <div class="question-icon">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
                <circle cx="12" cy="12" r="10"/>
                <path d="M9.09 9a3 3 0 0 1 5.83 1c0 2-3 3-3 3"/>
                <line x1="12" y1="17" x2="12.01" y2="17"/>
              </svg>
            </div>
            <div class="question-content">
              <div class="question-title">{{ question.title }}</div>
              <div class="question-meta">
                <span>{{ question.answersCount }} 个回答</span>
              </div>
            </div>
          </div>
        </div>

        <!-- 知识推荐 -->
        <h2 class="section-title" style="margin-top: var(--spacing-6, 24px)">知识推荐</h2>
        <div class="knowledge-list">
          <div v-for="knowledge in knowledgeList" :key="knowledge.id" class="knowledge-item">
            <div class="knowledge-icon">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
                <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/>
                <path d="M14 2v6h6M16 13H8M16 17H8M10 9H8"/>
              </svg>
            </div>
            <div class="knowledge-content">
              <div class="knowledge-title">{{ knowledge.title }}</div>
              <div class="knowledge-author">{{ knowledge.author }}</div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()

// 处理向专家提问
const handleAskExpert = (expert) => {
  if (!userStore.isLoggedIn()) {
    ElMessage.info('请先登录后向专家提问')
    router.push('/login')
    return
  }
  // TODO: 跳转到提问页面或打开提问对话框
  ElMessage.info('向专家提问：' + expert.name)
}

const experts = ref([
  {
    id: 1,
    name: '张农学',
    title: '高级农艺师',
    specialty: '水稻种植、病虫害防治',
    answersCount: 156,
    satisfactionRate: 98
  },
  {
    id: 2,
    name: '李种植',
    title: '农业技术推广专家',
    specialty: '设施农业、大棚技术',
    answersCount: 89,
    satisfactionRate: 95
  },
  {
    id: 3,
    name: '王土壤',
    title: '土壤肥料专家',
    specialty: '土壤改良、科学施肥',
    answersCount: 67,
    satisfactionRate: 96
  }
])

const hotQuestions = ref([
  { id: 1, title: '水稻病虫害如何有效防治？', answersCount: 23 },
  { id: 2, title: '大棚蔬菜种植需要注意什么？', answersCount: 18 },
  { id: 3, title: '如何提高农作物产量？', answersCount: 15 },
  { id: 4, title: '有机农业种植技术有哪些？', answersCount: 12 }
])

const knowledgeList = ref([
  { id: 1, title: '夏季水稻病虫害综合防治技术', author: '张农学' },
  { id: 2, title: '大棚蔬菜标准化种植指南', author: '李种植' },
  { id: 3, title: '科学施肥提高农产品品质', author: '王土壤' }
])
</script>

<style scoped>
.expert-help-page {
  max-width: 1200px;
  margin: 0 auto;
  padding: var(--spacing-8, 32px) var(--spacing-6, 24px);
}

.page-header {
  text-align: center;
  margin-bottom: var(--spacing-8, 32px);
}

.page-title {
  font-family: var(--font-family-display, 'Noto Serif SC', serif);
  font-size: var(--font-size-4xl, 48px);
  font-weight: var(--font-weight-bold, 700);
  color: var(--color-text-primary, #1f2923);
  margin: 0 0 var(--spacing-2, 8px);
}

.page-subtitle {
  font-size: var(--font-size-lg, 20px);
  color: var(--color-text-tertiary, #6b7280);
  margin: 0;
}

/* ===== 内容网格 ===== */
.content-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: var(--spacing-6, 24px);
}

.section-title {
  font-size: var(--font-size-xl, 24px);
  font-weight: var(--font-weight-bold, 700);
  color: var(--color-text-primary, #1f2923);
  margin: 0 0 var(--spacing-4, 16px);
}

/* ===== 专家列表 ===== */
.experts-list {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-3, 12px);
}

.expert-card {
  display: flex;
  align-items: center;
  gap: var(--spacing-3, 12px);
  padding: var(--spacing-4, 16px);
  background: var(--color-bg-primary, #ffffff);
  border: 1px solid var(--color-border, #e5e0d8);
  border-radius: var(--radius-lg, 12px);
  transition: all var(--transition-fast, 150ms ease);
}

.expert-card:hover {
  box-shadow: var(--shadow-sm, 0 2px 4px rgba(31, 41, 35, 0.06));
  border-color: var(--color-primary, #2d5a3d);
}

.expert-avatar {
  width: 56px;
  height: 56px;
  border-radius: var(--radius-full, 9999px);
  background: var(--color-expert, #b85c38);
  color: var(--color-text-inverse, #ffffff);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: var(--font-size-xl, 24px);
  font-weight: var(--font-weight-bold, 700);
  flex-shrink: 0;
  overflow: hidden;
}

.expert-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.expert-info {
  flex: 1;
  min-width: 0;
}

.expert-name {
  font-size: var(--font-size-base, 16px);
  font-weight: var(--font-weight-semibold, 600);
  color: var(--color-text-primary, #1f2923);
  margin-bottom: var(--spacing-1, 4px);
}

.expert-title {
  font-size: var(--font-size-sm, 14px);
  color: var(--color-expert, #b85c38);
  margin-bottom: var(--spacing-1, 4px);
}

.expert-specialty {
  font-size: var(--font-size-sm, 14px);
  color: var(--color-text-secondary, #4a5249);
  margin-bottom: var(--spacing-2, 8px);
}

.expert-stats {
  display: flex;
  gap: var(--spacing-3, 12px);
}

.stat-item {
  font-size: var(--font-size-xs, 12px);
  color: var(--color-text-tertiary, #6b7280);
}

/* ===== 问题列表 ===== */
.questions-list {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-2, 8px);
}

.question-item {
  display: flex;
  align-items: center;
  gap: var(--spacing-3, 12px);
  padding: var(--spacing-3, 12px);
  background: var(--color-bg-primary, #ffffff);
  border: 1px solid var(--color-border, #e5e0d8);
  border-radius: var(--radius-base, 8px);
  cursor: pointer;
  transition: all var(--transition-fast, 150ms ease);
}

.question-item:hover {
  background: var(--color-bg-secondary, #f7f5f0);
  border-color: var(--color-expert, #b85c38);
}

.question-icon {
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(184, 92, 56, 0.1);
  border-radius: var(--radius-base, 8px);
  color: var(--color-expert, #b85c38);
  flex-shrink: 0;
}

.question-icon svg {
  width: 18px;
  height: 18px;
}

.question-content {
  flex: 1;
  min-width: 0;
}

.question-title {
  font-size: var(--font-size-sm, 14px);
  font-weight: var(--font-weight-medium, 500);
  color: var(--color-text-primary, #1f2923);
  margin-bottom: var(--spacing-1, 4px);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.question-meta {
  font-size: var(--font-size-xs, 12px);
  color: var(--color-text-tertiary, #6b7280);
}

/* ===== 知识列表 ===== */
.knowledge-list {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-2, 8px);
}

.knowledge-item {
  display: flex;
  align-items: center;
  gap: var(--spacing-3, 12px);
  padding: var(--spacing-3, 12px);
  background: var(--color-bg-primary, #ffffff);
  border: 1px solid var(--color-border, #e5e0d8);
  border-radius: var(--radius-base, 8px);
  cursor: pointer;
  transition: all var(--transition-fast, 150ms ease);
}

.knowledge-item:hover {
  background: var(--color-bg-secondary, #f7f5f0);
  border-color: var(--color-expert, #b85c38);
}

.knowledge-icon {
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(45, 90, 61, 0.1);
  border-radius: var(--radius-base, 8px);
  color: var(--color-primary, #2d5a3d);
  flex-shrink: 0;
}

.knowledge-icon svg {
  width: 18px;
  height: 18px;
}

.knowledge-content {
  flex: 1;
  min-width: 0;
}

.knowledge-title {
  font-size: var(--font-size-sm, 14px);
  font-weight: var(--font-weight-medium, 500);
  color: var(--color-text-primary, #1f2923);
  margin-bottom: var(--spacing-1, 4px);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.knowledge-author {
  font-size: var(--font-size-xs, 12px);
  color: var(--color-text-tertiary, #6b7280);
}

/* ===== 响应式 ===== */
@media (max-width: 768px) {
  .content-grid {
    grid-template-columns: 1fr;
  }

  .page-title {
    font-size: var(--font-size-3xl, 38px);
  }
}
</style>
