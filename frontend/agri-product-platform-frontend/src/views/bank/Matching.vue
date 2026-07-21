<template>
  <div class="bank-matching">
    <!-- 页面标题 -->
    <div class="page-header">
      <h1 class="page-title">智能匹配</h1>
      <p class="page-subtitle">按金额、期限与种植作物，从真实融资申请中主动发现并优先对接高信用农户</p>
    </div>

    <!-- 匹配规则提示 -->
    <div class="tip-card">
      <el-icon class="tip-icon"><InfoFilled /></el-icon>
      <div class="tip-text">
        匹配范围：状态为<span class="em">「申请中」</span>的真实融资申请。
        可按<span class="em">期望金额区间</span>、<span class="em">期限</span>、<span class="em">作物/用途关键字</span>筛选，
        结果按农户信用分优先排列，并展示其在售作物，便于银行评估与主动对接。
      </div>
    </div>

    <!-- 筛选 -->
    <div class="content-card">
      <div class="card-header">
        <h3 class="card-title">筛选条件</h3>
      </div>
      <div class="filter-body">
        <div class="filter-item">
          <span class="filter-label">期望金额（万）</span>
          <el-input-number v-model="filter.minAmount" :min="0" :step="1" controls-position="right" style="width: 130px" placeholder="最低" />
          <span class="dash">至</span>
          <el-input-number v-model="filter.maxAmount" :min="0" :step="1" controls-position="right" style="width: 130px" placeholder="最高" />
        </div>
        <div class="filter-item">
          <span class="filter-label">期限（月）</span>
          <el-input-number v-model="filter.repayment" :min="0" :step="1" :precision="0" controls-position="right" style="width: 130px" placeholder="如 12" />
        </div>
        <div class="filter-item">
          <span class="filter-label">作物/用途</span>
          <el-input v-model="filter.keyword" placeholder="如：水稻 / 购置农机" clearable style="width: 200px" />
        </div>
        <div class="filter-actions">
          <el-button type="primary" @click="onSearch">
            <el-icon><Search /></el-icon>
            搜索
          </el-button>
          <el-button plain @click="resetFilter">重置</el-button>
        </div>
      </div>
    </div>

    <!-- 结果列表 -->
    <div v-loading="loading" class="content-card">
      <div class="card-header">
        <h3 class="card-title">匹配结果（共 {{ pagination.total }} 条）</h3>
      </div>
      <div class="result-body">
        <div v-if="rows.length > 0" class="result-grid">
          <div v-for="row in rows" :key="row.financeId" class="result-card">
            <div class="result-top">
              <div class="result-name">{{ row.realName || '农户' }}</div>
              <el-tag type="warning" effect="plain" size="small">¥ {{ formatMoney(row.money) }}</el-tag>
            </div>
            <div class="result-meta">
              <span><el-icon><Calendar /></el-icon>{{ row.repayment ?? '—' }} 个月</span>
              <span><el-icon><Money /></el-icon>{{ row.productName || '—' }}</span>
              <el-tag :type="creditType(row.credit)" size="small" effect="plain">信用 {{ row.credit ?? '—' }}</el-tag>
            </div>
            <div class="result-row"><span class="label">融资用途</span><span class="value">{{ row.purpose || '—' }}</span></div>
            <div class="result-row"><span class="label">在售作物</span><span class="value">{{ row.productNames || '—' }}</span></div>
            <div class="result-row"><span class="label">联系电话</span><span class="value">{{ row.phone || '—' }}</span></div>
            <div class="result-actions">
              <el-button type="primary" link size="small" @click="contactFarmer(row)">联系农户</el-button>
              <el-button type="primary" link size="small" @click="goApprove(row)">去审批</el-button>
            </div>
          </div>
        </div>

        <el-empty v-else description="暂无匹配的融资申请，调整筛选条件再试" />

        <div v-if="pagination.total > 0" class="pagination-wrapper">
          <el-pagination
            v-model:current-page="pagination.page"
            v-model:page-size="pagination.pageSize"
            :total="pagination.total"
            :page-sizes="[9, 18, 36]"
            layout="total, sizes, prev, pager, next"
            @current-change="loadMatch"
            @size-change="loadMatch"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { InfoFilled, Search, Calendar, Money } from '@element-plus/icons-vue'
import { getBankMatch } from '@/api/finance'

const router = useRouter()
const loading = ref(false)
const rows = ref([])

const filter = reactive({
  minAmount: undefined,
  maxAmount: undefined,
  repayment: undefined,
  keyword: ''
})

const pagination = reactive({
  page: 1,
  pageSize: 9,
  total: 0
})

onMounted(() => {
  loadMatch()
})

// 金额单位换算：UI 以「万」录入，后端按「元」过滤
const WAN = 10000

const loadMatch = async () => {
  loading.value = true
  try {
    const params = {
      page: pagination.page,
      pageSize: pagination.pageSize
    }
    if (filter.minAmount !== undefined && filter.minAmount !== null) {
      params.minMoney = filter.minAmount * WAN
    }
    if (filter.maxAmount !== undefined && filter.maxAmount !== null) {
      params.maxMoney = filter.maxAmount * WAN
    }
    if (filter.repayment !== undefined && filter.repayment !== null && filter.repayment > 0) {
      params.repayment = filter.repayment
    }
    if (filter.keyword && filter.keyword.trim()) {
      params.keyword = filter.keyword.trim()
    }
    const res = await getBankMatch(params)
    rows.value = res.data?.records || []
    pagination.total = res.data?.total || 0
  } catch (error) {
    ElMessage.error(error?.message || '加载匹配结果失败')
  } finally {
    loading.value = false
  }
}

const onSearch = () => {
  // 校验金额区间
  if (
    filter.minAmount !== undefined && filter.minAmount !== null &&
    filter.maxAmount !== undefined && filter.maxAmount !== null &&
    filter.maxAmount < filter.minAmount
  ) {
    ElMessage.warning('金额上限不能低于下限')
    return
  }
  pagination.page = 1
  loadMatch()
}

const resetFilter = () => {
  filter.minAmount = undefined
  filter.maxAmount = undefined
  filter.repayment = undefined
  filter.keyword = ''
  pagination.page = 1
  loadMatch()
}

const formatMoney = (n) => {
  const num = Number(n || 0)
  return num.toLocaleString('zh-CN', { minimumFractionDigits: 0, maximumFractionDigits: 2 })
}

// 信用分配色：≥4 优、=3 中、其余低；无值用 info
const creditType = (c) => {
  if (c === null || c === undefined) return 'info'
  if (c >= 4) return 'success'
  if (c === 3) return 'warning'
  return 'danger'
}

const contactFarmer = (row) => {
  ElMessageBox.alert(
    `农户姓名：${row.realName || '—'}\n联系电话：${row.phone || '—'}\n资金需求：¥ ${formatMoney(row.money)}\n融资用途：${row.purpose || '—'}\n在售作物：${row.productNames || '—'}`,
    '联系农户',
    { confirmButtonText: '我知道了', type: 'info' }
  )
}

const goApprove = (row) => {
  // 跳到融资审批工作台（该申请在「申请中」列表里），由银行在此完成审批
  router.push('/bank/approvals')
}
</script>

<style scoped>
.bank-matching {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-6, 24px);
}

/* 页面标题 */
.page-header {
  margin-bottom: var(--spacing-1, 4px);
}
.page-title {
  font-family: var(--font-family-display, 'Noto Serif SC', serif);
  font-size: var(--font-size-3xl, 38px);
  font-weight: var(--font-weight-bold, 700);
  color: var(--color-text-primary, #1f2923);
  margin: 0 0 var(--spacing-2, 8px);
}
.page-subtitle {
  font-size: var(--font-size-base, 16px);
  color: var(--color-text-tertiary, #6b7280);
  margin: 0;
}

/* 提示卡 */
.tip-card {
  display: flex;
  align-items: center;
  gap: var(--spacing-3, 12px);
  padding: var(--spacing-4, 16px) var(--spacing-5, 20px);
  background: var(--color-warning-light, #fdf8e8);
  border: 1px solid #f0e1b8;
  border-radius: var(--radius-lg, 12px);
  color: var(--color-gold-dark, #a6873f);
}
.tip-icon { font-size: 22px; flex-shrink: 0; }
.tip-text { font-size: var(--font-size-sm, 14px); line-height: var(--line-height-normal, 1.5); }
.tip-text .em { font-weight: var(--font-weight-semibold, 600); }

/* 内容卡片 */
.content-card {
  background: var(--color-bg-primary, #ffffff);
  border-radius: var(--radius-lg, 12px);
  border: 1px solid var(--color-border, #e5e0d8);
  box-shadow: var(--shadow-sm, 0 2px 4px rgba(31, 41, 35, 0.06));
  overflow: hidden;
}
.card-header {
  padding: var(--spacing-4, 16px) var(--spacing-5, 20px);
  border-bottom: 1px solid var(--color-divider, #edebe6);
}
.card-title {
  font-size: var(--font-size-lg, 20px);
  font-weight: var(--font-weight-semibold, 600);
  color: var(--color-text-primary, #1f2923);
  margin: 0;
}

/* 筛选 */
.filter-body {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: var(--spacing-5, 20px);
  padding: var(--spacing-5, 20px);
}
.filter-item {
  display: flex;
  align-items: center;
  gap: var(--spacing-2, 8px);
}
.filter-label {
  font-size: var(--font-size-sm, 14px);
  color: var(--color-text-tertiary, #6b7280);
  white-space: nowrap;
}
.dash { color: var(--color-text-tertiary, #6b7280); }
.filter-actions { margin-left: auto; display: flex; gap: var(--spacing-2, 8px); }

/* 结果 */
.result-body { padding: var(--spacing-5, 20px); }
.result-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: var(--spacing-4, 16px);
}
.result-card {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-2, 8px);
  padding: var(--spacing-5, 20px);
  background: var(--color-bg-secondary, #f7f5f0);
  border-radius: var(--radius-lg, 12px);
  border: 1px solid var(--color-border, #e5e0d8);
  transition: all var(--transition-fast, 150ms ease);
}
.result-card:hover {
  box-shadow: var(--shadow-base, 0 4px 12px rgba(31, 41, 35, 0.08));
  transform: translateY(-2px);
}
.result-top {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: var(--spacing-2, 8px);
}
.result-name {
  font-size: var(--font-size-lg, 20px);
  font-weight: var(--font-weight-semibold, 600);
  color: var(--color-bank, #2d5a3d);
}
.result-meta {
  display: flex;
  flex-wrap: wrap;
  gap: var(--spacing-3, 12px);
  font-size: var(--font-size-xs, 12px);
  color: var(--color-text-tertiary, #6b7280);
  align-items: center;
}
.result-meta span {
  display: inline-flex;
  align-items: center;
  gap: 4px;
}
.result-row {
  display: flex;
  gap: var(--spacing-2, 8px);
  font-size: var(--font-size-sm, 14px);
  line-height: var(--line-height-normal, 1.5);
}
.result-row .label { color: var(--color-text-tertiary, #6b7280); flex-shrink: 0; width: 60px; }
.result-row .value { color: var(--color-text-primary, #1f2923); word-break: break-all; }
.result-actions {
  display: flex;
  justify-content: flex-end;
  gap: var(--spacing-2, 8px);
  margin-top: var(--spacing-1, 4px);
  padding-top: var(--spacing-2, 8px);
  border-top: 1px dashed var(--color-divider, #edebe6);
}

.pagination-wrapper {
  display: flex;
  justify-content: flex-end;
  padding-top: var(--spacing-5, 20px);
}

@media (max-width: 768px) {
  .filter-actions { margin-left: 0; }
}
</style>
