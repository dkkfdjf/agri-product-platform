<template>
  <div class="reserve-expert-page">
    <div class="container">
      <!-- 页面标题 -->
      <div class="page-header">
        <h1 class="page-title">预约专家</h1>
        <p class="page-subtitle">与农业专家一对一咨询，获取专业指导</p>
      </div>

      <!-- Tab切换 -->
      <el-tabs v-model="activeTab" class="expert-tabs">
        <!-- 专家列表 -->
        <el-tab-pane label="专家列表" name="experts">
          <div v-loading="loadingExperts" class="experts-section">
            <!-- 分类筛选 -->
            <div class="filter-section">
              <div class="filter-tabs">
                <button
                  v-for="cat in categories"
                  :key="cat.value"
                  class="filter-tab"
                  :class="{ active: selectedCategory === cat.value }"
                  @click="selectedCategory = cat.value"
                >
                  {{ cat.label }}
                </button>
              </div>
            </div>

            <!-- 专家列表 -->
            <div v-if="filteredExperts.length > 0" class="experts-grid">
              <div
                v-for="expert in filteredExperts"
                :key="expert.expertId"
                class="expert-card"
              >
                <div class="expert-avatar">
                  <el-avatar :size="80" :src="expert.avatar">
                    {{ expert.realName?.charAt(0) }}
                  </el-avatar>
                  <div class="expert-status" v-if="expert.onlineStatus === 'online'">
                    在线
                  </div>
                </div>
                <div class="expert-info">
                  <h3 class="expert-name">{{ expert.realName }}</h3>
                  <div class="expert-title">{{ expert.specialty || '农业专家' }}</div>
                  <div class="expert-desc">{{ expert.description || '资深农业专家，为您提供专业指导' }}</div>
                  <div class="expert-stats">
                    <span>
                      <el-icon><ChatDotRound /></el-icon>
                      {{ expert.consultCount || 0 }} 次咨询
                    </span>
                    <span>
                      <el-icon><Star /></el-icon>
                      {{ expert.rating || 5.0 }} 分
                    </span>
                  </div>
                </div>
                <div class="expert-actions">
                  <el-button
                    type="primary"
                    @click="handleReserve(expert)"
                    :disabled="!userStore.isLoggedIn()"
                  >
                    立即预约
                  </el-button>
                  <el-button @click="handleViewDetail(expert)">
                    查看详情
                  </el-button>
                </div>
              </div>
            </div>
            <el-empty v-else description="暂无专家信息" />
          </div>
        </el-tab-pane>

        <!-- 我的预约 -->
        <el-tab-pane label="我的预约" name="my-reserves">
          <div v-loading="loadingReserves" class="reserves-section">
            <div v-if="userStore.isLoggedIn()">
              <div v-if="myReserves.length > 0" class="reserves-list">
                <div
                  v-for="reserve in myReserves"
                  :key="reserve.reserveId"
                  class="reserve-card"
                >
                  <div class="reserve-header">
                    <div class="expert-mini">
                      <el-avatar :size="40" :src="reserve.expertAvatar">
                        {{ reserve.expertName?.charAt(0) }}
                      </el-avatar>
                      <span class="expert-name">{{ reserve.expertName }}</span>
                    </div>
                    <div
                      class="status-badge"
                      :class="`status-${reserve.status}`"
                    >
                      {{ getReserveStatusText(reserve.status) }}
                    </div>
                  </div>
                  <div class="reserve-info">
                    <div class="info-item">
                      <el-icon><Calendar /></el-icon>
                      预约时间：{{ formatDateTime(reserve.reserveTime) }}
                    </div>
                    <div class="info-item" v-if="reserve.description">
                      <el-icon><Document /></el-icon>
                      咨询内容：{{ reserve.description }}
                    </div>
                    <div class="info-item" v-if="reserve.status === 'confirmed' && reserve.meetingLink">
                      <el-icon><VideoCamera /></el-icon>
                      会议链接：<a :href="reserve.meetingLink" target="_blank">点击进入</a>
                    </div>
                  </div>
                  <div class="reserve-actions">
                    <el-button
                      v-if="reserve.status === 'pending'"
                      type="danger"
                      size="small"
                      @click="handleCancelReserve(reserve)"
                    >
                      取消预约
                    </el-button>
                    <el-button
                      v-if="reserve.status === 'confirmed' && reserve.meetingLink"
                      type="primary"
                      size="small"
                      @click="handleEnterMeeting(reserve)"
                    >
                      进入会议
                    </el-button>
                  </div>
                </div>
              </div>
              <el-empty v-else description="您还没有预约记录，快去预约专家吧！" />
            </div>
            <div v-else class="login-prompt">
              <el-empty description="请先登录后查看预约记录">
                <el-button type="primary" @click="handleLogin">立即登录</el-button>
              </el-empty>
            </div>
          </div>
        </el-tab-pane>
      </el-tabs>
    </div>

    <!-- 预约对话框 -->
    <el-dialog
      v-model="reserveDialogVisible"
      title="预约专家"
      width="500px"
      :before-close="handleCloseDialog"
    >
      <el-form
        ref="reserveFormRef"
        :model="reserveForm"
        :rules="reserveFormRules"
        label-width="100px"
      >
        <el-form-item label="专家姓名">
          <el-input v-model="selectedExpert.realName" disabled />
        </el-form-item>
        <el-form-item label="预约时间" prop="reserveTime">
          <el-date-picker
            v-model="reserveForm.reserveTime"
            type="datetime"
            placeholder="选择预约时间"
            :disabled-date="disabledDate"
            :disabled-hours="disabledHours"
            :disabled-minutes="disabledMinutes"
            format="YYYY-MM-DD HH:mm"
            value-format="YYYY-MM-DD HH:mm:ss"
          />
        </el-form-item>
        <el-form-item label="咨询内容" prop="description">
          <el-input
            v-model="reserveForm.description"
            type="textarea"
            :rows="4"
            placeholder="请简要描述您想咨询的问题"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="handleCloseDialog">取消</el-button>
        <el-button type="primary" @click="handleSubmitReserve" :loading="submitting">
          确认预约
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  ChatDotRound,
  Star,
  Calendar,
  Document,
  VideoCamera
} from '@element-plus/icons-vue'
import { getExpertList } from '@/api/expert'
import { makeReservation, getMyReserves, cancelReserve } from '@/api/reserve'

const router = useRouter()
const userStore = useUserStore()

const activeTab = ref('experts')
const loadingExperts = ref(false)
const loadingReserves = ref(false)
const selectedCategory = ref('all')

const experts = ref([])
const myReserves = ref([])
const submitting = ref(false)
const reserveDialogVisible = ref(false)
const selectedExpert = ref({})
const reserveFormRef = ref(null)

const reserveForm = reactive({
  expertId: '',
  reserveTime: '',
  description: ''
})

const reserveFormRules = {
  reserveTime: [
    { required: true, message: '请选择预约时间', trigger: 'change' }
  ],
  description: [
    { required: true, message: '请输入咨询内容', trigger: 'blur' },
    { min: 10, max: 500, message: '咨询内容长度应在10-500个字符之间', trigger: 'blur' }
  ]
}

// 分类选项
const categories = [
  { value: 'all', label: '全部' },
  { value: 'grain', label: '粮食作物' },
  { value: 'vegetable', label: '蔬菜种植' },
  { value: 'fruit', label: '果树栽培' },
  { value: 'soil', label: '土壤肥料' },
  { value: 'pest', label: '病虫害防治' },
  { value: 'breeding', label: '畜牧养殖' }
]

// 过滤后的专家列表
const filteredExperts = computed(() => {
  if (selectedCategory.value === 'all') {
    return experts.value
  }
  return experts.value.filter(expert => expert.specialty === selectedCategory.value)
})

// 获取预约状态文本
const getReserveStatusText = (status) => {
  const map = {
    pending: '待确认',
    confirmed: '已确认',
    rejected: '已拒绝',
    completed: '已完成',
    cancelled: '已取消'
  }
  return map[status] || '未知'
}

// 格式化日期时间
const formatDateTime = (dateStr) => {
  if (!dateStr) return ''
  return new Date(dateStr).toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// 登录
const handleLogin = () => {
  router.push({ path: '/login', query: { redirect: '/reserve' } })
}

// 查看专家详情
const handleViewDetail = (expert) => {
  ElMessageBox.alert(
    `
    <div style="text-align: left;">
      <p><strong>姓名：</strong>${expert.realName}</p>
      <p><strong>专长：</strong>${expert.specialty || '农业专家'}</p>
      <p><strong>简介：</strong>${expert.description || '暂无简介'}</p>
      <p><strong>咨询次数：</strong>${expert.consultCount || 0} 次</p>
      <p><strong>评分：</strong>${expert.rating || 5.0} 分</p>
    </div>
    `,
    '专家详情',
    { dangerouslyUseHTMLString: true }
  )
}

// 预约专家
const handleReserve = (expert) => {
  if (!userStore.isLoggedIn()) {
    ElMessage.info('请先登录后预约')
    router.push({ path: '/login', query: { redirect: '/reserve' } })
    return
  }

  selectedExpert.value = expert
  reserveForm.expertId = expert.expertId
  reserveForm.reserveTime = ''
  reserveForm.description = ''
  reserveDialogVisible.value = true
}

// 关闭对话框
const handleCloseDialog = () => {
  reserveDialogVisible.value = false
  reserveFormRef.value?.resetFields()
}

// 禁用过去的日期
const disabledDate = (time) => {
  return time.getTime() < Date.now() - 24 * 60 * 60 * 1000
}

// 禁用非工作时间（9:00-18:00）
const disabledHours = () => {
  const hours = []
  for (let i = 0; i < 9; i++) {
    hours.push(i)
  }
  for (let i = 18; i < 24; i++) {
    hours.push(i)
  }
  return hours
}

// 禁用非整点或半点
const disabledMinutes = (hour) => {
  if (hour >= 9 && hour < 18) {
    const minutes = []
    for (let i = 0; i < 60; i++) {
      if (i !== 0 && i !== 30) {
        minutes.push(i)
      }
    }
    return minutes
  }
  return []
}

// 提交预约
const handleSubmitReserve = async () => {
  try {
    await reserveFormRef.value.validate()

    submitting.value = true

    await makeReservation({
      expertId: reserveForm.expertId,
      reserveTime: reserveForm.reserveTime,
      description: reserveForm.description
    })

    ElMessage.success('预约成功，请等待专家确认')
    reserveDialogVisible.value = false
    activeTab.value = 'my-reserves'
    await loadMyReserves()
  } catch (error) {
    if (error !== false) {
      ElMessage.error(error.message || '预约失败')
    }
  } finally {
    submitting.value = false
  }
}

// 取消预约
const handleCancelReserve = async (reserve) => {
  try {
    await ElMessageBox.confirm('确定要取消这个预约吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await cancelReserve(reserve.reserveId)
    ElMessage.success('已取消预约')
    await loadMyReserves()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('取消失败')
    }
  }
}

// 进入会议
const handleEnterMeeting = (reserve) => {
  if (reserve.meetingLink) {
    window.open(reserve.meetingLink, '_blank')
  }
}

// 加载专家列表
const loadExperts = async () => {
  loadingExperts.value = true
  try {
    const res = await getExpertList()
    experts.value = res.data || []
  } catch (error) {
    ElMessage.error('加载专家列表失败')
  } finally {
    loadingExperts.value = false
  }
}

// 加载我的预约
const loadMyReserves = async () => {
  if (!userStore.isLoggedIn()) return

  loadingReserves.value = true
  try {
    const res = await getMyReserves()
    myReserves.value = res.data || []
  } catch (error) {
    ElMessage.error('加载预约记录失败')
  } finally {
    loadingReserves.value = false
  }
}

// 监听Tab切换
watch(activeTab, (newVal) => {
  if (newVal === 'my-reserves' && userStore.isLoggedIn()) {
    loadMyReserves()
  }
})

onMounted(() => {
  loadExperts()
  if (userStore.isLoggedIn()) {
    loadMyReserves()
  }
})
</script>

<style scoped>
.reserve-expert-page {
  min-height: 100vh;
  background: var(--color-bg-page, #f5f3ef);
  padding: var(--spacing-6, 24px) 0;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 var(--spacing-6, 24px);
}

.page-header {
  text-align: center;
  margin-bottom: var(--spacing-6, 24px);
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

.expert-tabs {
  background: var(--color-bg-primary, #ffffff);
  border-radius: var(--radius-lg, 12px);
  padding: var(--spacing-6, 24px);
  box-shadow: var(--shadow-sm, 0 2px 8px rgba(0,0,0,0.05));
}

/* ===== 筛选区 ===== */
.filter-section {
  margin-bottom: var(--spacing-6, 24px);
}

.filter-tabs {
  display: flex;
  gap: var(--spacing-2, 8px);
  flex-wrap: wrap;
}

.filter-tab {
  padding: var(--spacing-2, 8px) var(--spacing-4, 16px);
  font-size: var(--font-size-sm, 14px);
  font-weight: var(--font-weight-medium, 500);
  color: var(--color-text-secondary, #4a5249);
  background: transparent;
  border: 1px solid var(--color-border, #e5e0d8);
  border-radius: var(--radius-full, 9999px);
  cursor: pointer;
  transition: all var(--transition-fast, 150ms ease);
}

.filter-tab:hover {
  color: var(--color-primary, #2d5a3d);
  border-color: var(--color-primary, #2d5a3d);
}

.filter-tab.active {
  color: var(--color-text-inverse, #ffffff);
  background: var(--color-primary, #2d5a3d);
  border-color: var(--color-primary, #2d5a3d);
}

/* ===== 专家列表 ===== */
.experts-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: var(--spacing-5, 20px);
}

.expert-card {
  border: 1px solid var(--color-border, #e5e0d8);
  border-radius: var(--radius-lg, 12px);
  padding: var(--spacing-5, 20px);
  text-align: center;
  transition: all var(--transition-fast, 150ms ease);
}

.expert-card:hover {
  box-shadow: var(--shadow-base, 0 4px 12px rgba(0,0,0,0.1));
}

.expert-avatar {
  position: relative;
  display: inline-block;
  margin-bottom: var(--spacing-3, 12px);
}

.expert-status {
  position: absolute;
  bottom: 0;
  right: 0;
  padding: var(--spacing-1, 4px) var(--spacing-2, 8px);
  font-size: var(--font-size-xs, 10px);
  color: var(--color-text-inverse, #ffffff);
  background: #67c23a;
  border-radius: var(--radius-full, 9999px);
}

.expert-name {
  font-size: var(--font-size-lg, 20px);
  font-weight: var(--font-weight-semibold, 600);
  color: var(--color-text-primary, #1f2923);
  margin: 0 0 var(--spacing-1, 4px);
}

.expert-title {
  font-size: var(--font-size-sm, 14px);
  color: var(--color-primary, #2d5a3d);
  margin-bottom: var(--spacing-3, 12px);
}

.expert-desc {
  font-size: var(--font-size-sm, 14px);
  color: var(--color-text-secondary, #4a5249);
  line-height: 1.6;
  margin-bottom: var(--spacing-3, 12px);
  min-height: 60px;
}

.expert-stats {
  display: flex;
  justify-content: center;
  gap: var(--spacing-4, 16px);
  font-size: var(--font-size-sm, 14px);
  color: var(--color-text-tertiary, #6b7280);
  margin-bottom: var(--spacing-4, 16px);
}

.expert-stats span {
  display: flex;
  align-items: center;
  gap: var(--spacing-1, 4px);
}

.expert-actions {
  display: flex;
  gap: var(--spacing-2, 8px);
  justify-content: center;
}

/* ===== 预约列表 ===== */
.reserves-list {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-4, 16px);
}

.reserve-card {
  border: 1px solid var(--color-border, #e5e0d8);
  border-radius: var(--radius-base, 8px);
  padding: var(--spacing-4, 16px);
}

.reserve-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--spacing-3, 12px);
}

.expert-mini {
  display: flex;
  align-items: center;
  gap: var(--spacing-2, 8px);
}

.expert-mini .expert-name {
  font-weight: var(--font-weight-semibold, 600);
  color: var(--color-text-primary, #1f2923);
}

.status-badge {
  padding: var(--spacing-1, 4px) var(--spacing-3, 12px);
  font-size: var(--font-size-xs, 12px);
  font-weight: var(--font-weight-medium, 500);
  border-radius: var(--radius-full, 9999px);
}

.status-pending {
  color: #e6a23c;
  background: #fdf6ec;
}

.status-confirmed {
  color: #67c23a;
  background: #f0f9ff;
}

.status-rejected {
  color: #f56c6c;
  background: #fef0f0;
}

.status-completed {
  color: #909399;
  background: #f4f4f5;
}

.status-cancelled {
  color: #909399;
  background: #f4f4f5;
}

.reserve-info {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-2, 8px);
  margin-bottom: var(--spacing-3, 12px);
}

.info-item {
  display: flex;
  align-items: center;
  gap: var(--spacing-1, 4px);
  font-size: var(--font-size-sm, 14px);
  color: var(--color-text-secondary, #4a5249);
}

.info-item a {
  color: var(--color-primary, #2d5a3d);
  text-decoration: none;
}

.reserve-actions {
  display: flex;
  gap: var(--spacing-2, 8px);
}

/* ===== 响应式 ===== */
@media (max-width: 1024px) {
  .experts-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .experts-grid {
    grid-template-columns: 1fr;
  }

  .filter-tabs {
    overflow-x: auto;
    flex-wrap: nowrap;
  }

  .page-title {
    font-size: var(--font-size-3xl, 38px);
  }

  .expert-tabs {
    padding: var(--spacing-4, 16px);
  }
}
</style>
