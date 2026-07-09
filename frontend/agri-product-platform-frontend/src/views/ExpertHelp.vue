<template>
  <div class="expert-help-page">
    <!-- 导航栏 -->
    <el-header class="header">
      <div class="header-content">
        <div class="logo" @click="goTo('/')">
          <div class="logo-icon">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
              <path d="M12 2L12 12M12 12L8 8M12 12L16 8M12 12L12 22M12 22L8 18M12 22L16 18" />
              <circle cx="12" cy="3" r="1.5" fill="currentColor"/>
            </svg>
          </div>
          <span>农融汇</span>
        </div>
        <el-menu
          :default-active="activeMenu"
          mode="horizontal"
          class="nav-menu"
          router
        >
          <el-menu-item index="/">首页</el-menu-item>
          <el-menu-item index="/finance-products">融资服务</el-menu-item>
          <el-menu-item index="/market">农产品交易</el-menu-item>
          <el-menu-item index="/expert-help">专家助力</el-menu-item>
        </el-menu>
        <div class="header-actions">
          <template v-if="!userStore.isLoggedIn()">
            <el-button type="success" plain @click="goToLogin">登录</el-button>
          </template>
          <template v-else>
            <el-dropdown @command="handleUserCommand">
              <span class="user-name">
                <el-icon><User /></el-icon>
                {{ getUserDisplayName() }}
                <el-icon class="el-icon--right"><ArrowDown /></el-icon>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item :command="getDashboardPath()">
                    <el-icon><Odometer /></el-icon>
                    个人中心
                  </el-dropdown-item>
                  <el-dropdown-item divided command="logout">
                    <el-icon><SwitchButton /></el-icon>
                    退出登录
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
        </div>
      </div>
    </el-header>

    <!-- 主内容 -->
    <el-main class="main">
      <div class="page-header">
        <h2 class="page-title">专家助力</h2>
        <p class="page-subtitle">农业专家在线答疑，为您提供专业的技术指导</p>
      </div>

      <el-tabs v-model="activeTab">
        <!-- 知识 -->
        <el-tab-pane label="农业知识" name="knowledge">
          <el-row :gutter="20">
            <el-col :span="8" v-for="item in knowledgeList" :key="item.knowledgeId">
              <el-card class="knowledge-card" shadow="hover" @click="handleViewKnowledge(item)">
                <div class="card-header">
                  <el-tag>{{ item.category }}</el-tag>
                  <span class="views">{{ item.views }}次浏览</span>
                </div>
                <h3>{{ item.title }}</h3>
                <p class="summary">{{ item.summary }}</p>
                <div class="card-footer">
                  <span>{{ item.expertName }}</span>
                  <span>{{ item.createTime }}</span>
                </div>
              </el-card>
            </el-col>
          </el-row>
        </el-tab-pane>

        <!-- 问答 -->
        <el-tab-pane label="农业问答" name="qa">
          <el-card>
            <el-table :data="questionList" stripe>
              <el-table-column prop="title" label="问题标题" />
              <el-table-column prop="farmerName" label="提问人" width="120" />
              <el-table-column prop="status" label="状态" width="100">
                <template #default="{ row }">
                  <el-tag :type="row.status === 1 ? 'success' : 'warning'">
                    {{ row.status === 1 ? '已回答' : '未回答' }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="createTime" label="提问时间" width="180" />
              <el-table-column label="操作" width="100">
                <template #default="{ row }">
                  <el-button type="primary" size="small" link @click="handleViewQuestion(row)">
                    查看
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-card>
        </el-tab-pane>

        <!-- 专家列表 -->
        <el-tab-pane label="专家列表" name="experts">
          <el-row :gutter="20">
            <el-col :span="6" v-for="expert in expertList" :key="expert.userName">
              <el-card class="expert-card" shadow="hover">
                <el-avatar :size="60">{{ expert.expertName?.charAt(0) }}</el-avatar>
                <h3>{{ expert.expertName }}</h3>
                <p class="title">{{ expert.title }}</p>
                <p class="specialty">专长：{{ expert.specialty }}</p>
                <el-button v-if="userStore.role === 'farmer'" type="primary" @click="handleReserve(expert)">
                  预约咨询
                </el-button>
              </el-card>
            </el-col>
          </el-row>
        </el-tab-pane>
      </el-tabs>
    </el-main>

    <!-- 知识详情对话框 -->
    <el-dialog v-model="knowledgeVisible" title="知识详情" width="700px">
      <div v-if="currentKnowledge" class="knowledge-detail">
        <h2>{{ currentKnowledge.title }}</h2>
        <div class="meta">
          <el-tag>{{ currentKnowledge.category }}</el-tag>
          <span>发布者：{{ currentKnowledge.expertName }}</span>
          <span>发布时间：{{ currentKnowledge.createTime }}</span>
          <span>浏览量：{{ currentKnowledge.views }}</span>
        </div>
        <div class="content">{{ currentKnowledge.content }}</div>
      </div>
    </el-dialog>

    <!-- 问答详情对话框 -->
    <el-dialog v-model="questionVisible" title="问答详情" width="600px">
      <div v-if="currentQuestion" class="question-detail">
        <h3>问题：{{ currentQuestion.title }}</h3>
        <el-descriptions :column="2" border style="margin-bottom: 20px">
          <el-descriptions-item label="提问人">{{ currentQuestion.farmerName }}</el-descriptions-item>
          <el-descriptions-item label="提问时间">{{ currentQuestion.createTime }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="currentQuestion.status === 1 ? 'success' : 'warning'">
              {{ currentQuestion.status === 1 ? '已回答' : '未回答' }}
            </el-tag>
          </el-descriptions-item>
        </el-descriptions>
        <div class="question-content">
          <p><strong>问题描述：</strong>{{ currentQuestion.content }}</p>
          <p v-if="currentQuestion.answer" class="answer">
            <strong>专家回答：</strong>{{ currentQuestion.answer }}
          </p>
          <p v-else class="no-answer">该问题尚未被回答</p>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const activeMenu = computed(() => route.path)
const activeTab = ref('knowledge')
const knowledgeList = ref([])
const questionList = ref([])
const expertList = ref([])
const knowledgeVisible = ref(false)
const questionVisible = ref(false)
const currentKnowledge = ref(null)
const currentQuestion = ref(null)

// 获取用户显示名称
const getUserDisplayName = () => {
  const userInfo = userStore.userInfo
  if (!userInfo) return ''
  const role = userStore.role
  switch (role) {
    case 'farmer':
      return userInfo.farmName || userInfo.userName
    case 'expert':
      return userInfo.expertName || userInfo.userName
    case 'bank':
      return userInfo.bankName || userInfo.userName
    case 'admin':
      return '管理员'
    default:
      return userInfo.userName
  }
}

// 获取用户主页路径
const getDashboardPath = () => {
  const role = userStore.role
  switch (role) {
    case 'farmer':
      return '/farmer/dashboard'
    case 'expert':
      return '/expert/dashboard'
    case 'bank':
      return '/bank/dashboard'
    case 'admin':
      return '/admin/dashboard'
    default:
      return '/'
  }
}

// 处理用户命令
const handleUserCommand = (command) => {
  if (command === 'logout') {
    userStore.logout()
    ElMessage.success('已退出登录')
    location.reload()
  } else {
    router.push(command)
  }
}

// 跳转登录
const goToLogin = () => {
  router.push('/login')
}

// 页面跳转
const goTo = (path) => {
  router.push(path)
}

// 加载知识列表
const loadKnowledgeList = async () => {
  try {
    // TODO: 调用后端接口
    // const res = await knowledgeApi.list()

    // 模拟数据
    knowledgeList.value = [
      {
        knowledgeId: 1,
        title: '水稻育苗技术要点',
        category: '种植技术',
        summary: '水稻育苗是水稻种植的重要环节，需要注意以下要点',
        content: '水稻育苗是水稻种植的重要环节。首先要选择合适的品种...',
        expertName: '张教授',
        views: 128,
        createTime: '2024-01-10'
      },
      {
        knowledgeId: 2,
        title: '常见水稻病虫害防治',
        category: '病虫害防治',
        summary: '水稻种植过程中常见的病虫害及其防治方法',
        content: '水稻种植过程中常见的病虫害包括稻飞虱、稻纵卷叶螟...',
        expertName: '李研究员',
        views: 256,
        createTime: '2024-01-12'
      },
      {
        knowledgeId: 3,
        title: '科学施肥提高产量',
        category: '施肥技术',
        summary: '科学合理的施肥方法可以有效提高作物产量',
        content: '科学施肥是提高作物产量的重要手段。要根据土壤状况...',
        expertName: '王高级工程师',
        views: 98,
        createTime: '2024-01-14'
      }
    ]
  } catch (error) {
    ElMessage.error('加载知识列表失败')
  }
}

// 加载问答列表
const loadQuestionList = async () => {
  try {
    // TODO: 调用后端接口
    // const res = await questionApi.list()

    // 模拟数据
    questionList.value = [
      {
        questionId: 1,
        title: '水稻育苗期需要注意什么？',
        content: '请问水稻育苗期间需要注意哪些事项？',
        farmerName: '张三',
        status: 1,
        answer: '水稻育苗期间要注意温度控制、水分管理、病虫害预防等...',
        createTime: '2024-01-15 10:00:00'
      },
      {
        questionId: 2,
        title: '如何防治水稻稻飞虱？',
        content: '我的水稻田里发现了稻飞虱，应该怎么防治？',
        farmerName: '李四',
        status: 0,
        createTime: '2024-01-14 14:30:00'
      }
    ]
  } catch (error) {
    ElMessage.error('加载问答列表失败')
  }
}

// 加载专家列表
const loadExpertList = async () => {
  try {
    // TODO: 调用后端接口
    // const res = await expertApi.list()

    // 模拟数据
    expertList.value = [
      {
        userName: 'expert1',
        expertName: '张教授',
        title: '高级农艺师',
        specialty: '水稻种植技术'
      },
      {
        userName: 'expert2',
        expertName: '李研究员',
        title: '研究员',
        specialty: '病虫害防治'
      },
      {
        userName: 'expert3',
        expertName: '王高级工程师',
        title: '高级工程师',
        specialty: '农业机械化'
      },
      {
        userName: 'expert4',
        expertName: '赵博士',
        title: '农学博士',
        specialty: '土壤改良'
      }
    ]
  } catch (error) {
    ElMessage.error('加载专家列表失败')
  }
}

// 查看知识详情
const handleViewKnowledge = (item) => {
  currentKnowledge.value = item
  knowledgeVisible.value = true
}

// 查看问答详情
const handleViewQuestion = (item) => {
  currentQuestion.value = item
  questionVisible.value = true
}

// 预约专家
const handleReserve = (expert) => {
  if (!userStore.isLoggedIn()) {
    ElMessage.warning('请先登录')
    goToLogin()
    return
  }
  if (userStore.role !== 'farmer') {
    ElMessage.warning('只有农户可以预约专家')
    return
  }
  // 跳转到专家咨询页面
  router.push('/farmer/expert')
}

onMounted(() => {
  loadKnowledgeList()
  loadQuestionList()
  loadExpertList()
})
</script>

<style scoped>
.expert-help-page {
  --color-primary: #2D5A3D;
  --color-primary-light: #4A7C59;
  --color-primary-dark: #1F4229;
  --color-secondary: #C9A661;
  --color-accent: #B85C38;
  --color-bg: #F7F5F0;
  --color-text: #1F2923;
  --color-text-light: #6B7280;
  --color-border: #E5E0D8;
  --color-white: #FFFFFF;
  --font-display: 'Noto Serif SC', serif;
  --font-body: 'Noto Sans SC', sans-serif;

  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background: var(--color-bg);
}

.header {
  background: linear-gradient(135deg, var(--color-primary) 0%, var(--color-primary-dark) 100%);
  padding: 0;
  height: 72px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.header-content {
  max-width: 1280px;
  margin: 0 auto;
  height: 100%;
  display: flex;
  align-items: center;
  padding: 0 32px;
  gap: 48px;
}

.logo {
  display: flex;
  align-items: center;
  gap: 12px;
  color: var(--color-white);
  font-size: 20px;
  font-weight: 600;
  font-family: var(--font-display);
  cursor: pointer;
  transition: opacity 0.2s;
}

.logo:hover {
  opacity: 0.9;
}

.logo-icon {
  width: 36px;
  height: 36px;
  color: var(--color-secondary);
}

.logo-icon svg {
  width: 100%;
  height: 100%;
}

.nav-menu {
  flex: 1;
  border: none;
  background: transparent;
}

.nav-menu :deep(.el-menu-item) {
  color: rgba(255, 255, 255, 0.8);
  border-bottom: 2px solid transparent;
  font-size: 15px;
  font-weight: 500;
}

.nav-menu :deep(.el-menu-item:hover),
.nav-menu :deep(.el-menu-item.is-active) {
  color: var(--color-white);
  background: transparent;
  border-bottom-color: var(--color-white);
}

.header-actions {
  display: flex;
  align-items: center;
}

.user-name {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  color: var(--color-white);
  background: rgba(255, 255, 255, 0.1);
  border-radius: 20px;
  cursor: pointer;
  transition: background 0.2s;
}

.user-name:hover {
  background: rgba(255, 255, 255, 0.2);
}

.main {
  flex: 1;
  padding: 48px 32px;
  max-width: 1280px;
  margin: 0 auto;
  width: 100%;
}

.page-header {
  text-align: center;
  margin-bottom: 48px;
}

.page-title {
  font-family: var(--font-display);
  font-size: 36px;
  font-weight: 700;
  color: var(--color-text);
  margin: 0 0 12px;
}

.page-subtitle {
  font-size: 16px;
  color: var(--color-text-light);
  margin: 0;
}

/* Tabs */
:deep(.el-tabs) {
  background: transparent;
}

:deep(.el-tabs__header) {
  margin: 0 0 32px;
  border-bottom: 2px solid var(--color-border);
}

:deep(.el-tabs__nav-wrap::after) {
  display: none;
}

:deep(.el-tabs__item) {
  font-size: 16px;
  font-weight: 500;
  color: var(--color-text-light);
  padding: 0 24px;
  height: 48px;
  line-height: 48px;
}

:deep(.el-tabs__item:hover) {
  color: var(--color-primary);
}

:deep(.el-tabs__item.is-active) {
  color: var(--color-primary);
}

:deep(.el-tabs__active-bar) {
  height: 3px;
  background: var(--color-primary);
}

/* Knowledge Cards */
.knowledge-card {
  margin-bottom: 20px;
  cursor: pointer;
  border-radius: 12px;
  border: 1px solid var(--color-border);
  transition: all 0.3s;
}

.knowledge-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(45, 90, 61, 0.12);
  border-color: var(--color-primary-light);
}

.knowledge-card :deep(.el-card__body) {
  padding: 24px;
}

.knowledge-card .card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.knowledge-card .views {
  font-size: 13px;
  color: var(--color-text-light);
}

.knowledge-card h3 {
  font-size: 18px;
  font-weight: 600;
  color: var(--color-text);
  margin: 0 0 12px;
}

.knowledge-card .summary {
  font-size: 14px;
  color: var(--color-text-light);
  line-height: 1.6;
  margin-bottom: 16px;
  min-height: 40px;
}

.knowledge-card .card-footer {
  display: flex;
  justify-content: space-between;
  color: var(--color-text-light);
  font-size: 13px;
}

/* Expert Cards */
.expert-card {
  margin-bottom: 20px;
  text-align: center;
  border-radius: 12px;
  border: 1px solid var(--color-border);
  transition: all 0.3s;
}

.expert-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(45, 90, 61, 0.12);
  border-color: var(--color-secondary);
}

.expert-card :deep(.el-card__body) {
  padding: 28px 20px;
}

.expert-card h3 {
  font-size: 18px;
  font-weight: 600;
  color: var(--color-text);
  margin: 16px 0 8px;
}

.expert-card .title {
  margin: 0 0 8px;
  color: var(--color-secondary);
  font-size: 14px;
  font-weight: 500;
}

.expert-card .specialty {
  margin: 0 0 20px;
  color: var(--color-text-light);
  font-size: 13px;
  line-height: 1.5;
}

.expert-card :deep(.el-button) {
  border-radius: 8px;
}

/* Table Styling */
:deep(.el-table) {
  border-radius: 12px;
  overflow: hidden;
}

:deep(.el-table th) {
  background: var(--color-bg);
  font-weight: 600;
  color: var(--color-text);
}

:deep(.el-table tr:hover > td) {
  background: rgba(45, 90, 61, 0.05);
}

/* Dialog Styling */
:deep(.el-dialog) {
  border-radius: 16px;
}

:deep(.el-dialog__header) {
  padding: 24px 24px 16px;
  border-bottom: 1px solid var(--color-border);
}

:deep(.el-dialog__title) {
  font-size: 20px;
  font-weight: 600;
  color: var(--color-text);
}

:deep(.el-dialog__body) {
  padding: 24px;
}

/* Knowledge Detail */
.knowledge-detail h2 {
  font-size: 24px;
  font-weight: 600;
  color: var(--color-text);
  margin: 0 0 16px;
}

.knowledge-detail .meta {
  margin-bottom: 24px;
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  color: var(--color-text-light);
  font-size: 14px;
}

.knowledge-detail .content {
  line-height: 1.8;
  color: var(--color-text);
  white-space: pre-wrap;
  font-size: 15px;
}

/* Question Detail */
.question-detail h3 {
  font-size: 18px;
  font-weight: 600;
  color: var(--color-text);
  margin: 0 0 16px;
}

.question-content {
  padding: 20px;
  background: var(--color-bg);
  border-radius: 8px;
}

.question-content p {
  margin-bottom: 16px;
  line-height: 1.6;
}

.question-content .answer {
  color: var(--color-primary);
  padding-top: 16px;
  border-top: 1px dashed var(--color-border);
}

.question-content .no-answer {
  color: var(--color-text-light);
  font-style: italic;
}

/* Responsive */
@media (max-width: 1024px) {
  .header-content {
    padding: 0 20px;
    gap: 24px;
  }

  .nav-menu {
    display: none;
  }

  .main {
    padding: 32px 20px;
  }

  .page-title {
    font-size: 28px;
  }
}

:deep(.el-menu--horizontal) {
  border-bottom: none;
}
</style>
