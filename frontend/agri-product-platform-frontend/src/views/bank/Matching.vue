<template>
  <div class="bank-matching">
    <!-- 页面标题 -->
    <div class="page-header">
      <h1 class="page-title">智能匹配</h1>
      <p class="page-subtitle">根据期望金额与种植作物，主动发现潜在融资客户</p>
    </div>

    <!-- 匹配规则提示 -->
    <div class="tip-card">
      <el-icon class="tip-icon"><InfoFilled /></el-icon>
      <div class="tip-text">
        匹配规则：在下方填写<span class="em">期望金额区间</span>与<span class="em">农作物类型</span>，
        系统将筛选出符合条件的农户融资意向，便于银行主动对接。
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
          <span class="filter-label">农作物</span>
          <el-input v-model="filter.item" placeholder="如：水稻 / 蔬菜" clearable style="width: 180px" />
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
        <div v-if="intentions.length > 0" class="result-grid">
          <div v-for="row in intentions" :key="row.id" class="result-card">
            <div class="result-top">
              <div class="result-name">{{ row.realName }}</div>
              <el-tag type="warning" effect="plain" size="small">{{ row.amount }} 万</el-tag>
            </div>
            <div class="result-meta">
              <span><el-icon><Crop /></el-icon>{{ row.item }}</span>
              <span v-if="row.area"><el-icon><Location /></el-icon>{{ row.area }} 亩</span>
              <span><el-icon><Calendar /></el-icon>{{ row.repaymentPeriod }} 个月</span>
            </div>
            <div class="result-row"><span class="label">融资用途</span><span class="value">{{ row.application }}</span></div>
            <div class="result-row"><span class="label">种植地址</span><span class="value">{{ row.address || '—' }}</span></div>
            <div class="result-actions">
              <el-button type="primary" link size="small" @click="contactFarmer(row)">联系农户</el-button>
            </div>
          </div>
        </div>

        <el-empty v-else description="暂无匹配的融资意向，调整筛选条件再试" />

        <div v-if="pagination.total > 0" class="pagination-wrapper">
          <el-pagination
            v-model:current-page="pagination.page"
            v-model:page-size="pagination.pageSize"
            :total="pagination.total"
            :page-sizes="[9, 18, 36]"
            layout="total, sizes, prev, pager, next"
            @current-change="loadIntentions"
            @size-change="loadIntentions"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { InfoFilled, Search, Crop, Location, Calendar } from '@element-plus/icons-vue'
import { getFinanceIntentionMatch } from '@/api/finance'

const loading = ref(false)
const intentions = ref([])

const filter = reactive({
  minAmount: undefined,
  maxAmount: undefined,
  item: ''
})

const pagination = reactive({
  page: 1,
  pageSize: 9,
  total: 0
})

onMounted(() => {
  loadIntentions()
})

const loadIntentions = async () => {
  loading.value = true
  try {
    const params = {
      page: pagination.page,
      pageSize: pagination.pageSize
    }
    if (filter.minAmount !== undefined && filter.minAmount !== null) {
      params.minAmount = filter.minAmount
    }
    if (filter.maxAmount !== undefined && filter.maxAmount !== null) {
      params.maxAmount = filter.maxAmount
    }
    if (filter.item && filter.item.trim()) {
      params.item = filter.item.trim()
    }
    const res = await getFinanceIntentionMatch(params)
    intentions.value = res.data?.records || []
    pagination.total = res.data?.total || 0
  } catch (error) {
    ElMessage.error(error?.message || '加载融资意向失败')
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
  loadIntentions()
}

const resetFilter = () => {
  filter.minAmount = undefined
  filter.maxAmount = undefined
  filter.item = ''
  pagination.page = 1
  loadIntentions()
}

const contactFarmer = (row) => {
  ElMessageBox.alert(
    `农户姓名：${row.realName}\n联系电话：${row.phone}\n资金需求：${row.amount} 万元\n种植作物：${row.item}`,
    '联系农户',
    { confirmButtonText: '我知道了', type: 'info' }
  )
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
.result-row .value { color: var(--color-text-primary, #1f2923); }
.result-actions {
  display: flex;
  justify-content: flex-end;
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
