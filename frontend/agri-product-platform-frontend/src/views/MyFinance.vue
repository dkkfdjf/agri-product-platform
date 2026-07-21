<template>
  <div class="my-finance">
    <!-- 页面标题 -->
    <div class="page-header">
      <div>
        <h1 class="page-title">我的融资</h1>
        <p class="page-subtitle">查看我提交的全部融资申请及审批进度</p>
      </div>
      <el-button type="primary" @click="$router.push('/farmer/finance')">
        <el-icon><Plus /></el-icon>
        申请新融资
      </el-button>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-grid">
      <div class="stat-card warning">
        <div class="stat-icon"><el-icon><Clock /></el-icon></div>
        <div class="stat-content">
          <div class="stat-value">{{ stats.pending }}</div>
          <div class="stat-label">待审核</div>
        </div>
      </div>
      <div class="stat-card success">
        <div class="stat-icon"><el-icon><Select /></el-icon></div>
        <div class="stat-content">
          <div class="stat-value">{{ stats.approved }}</div>
          <div class="stat-label">已通过</div>
        </div>
      </div>
      <div class="stat-card error">
        <div class="stat-icon"><el-icon><Close /></el-icon></div>
        <div class="stat-content">
          <div class="stat-value">{{ stats.rejected }}</div>
          <div class="stat-label">已拒绝</div>
        </div>
      </div>
      <div class="stat-card primary">
        <div class="stat-icon"><el-icon><Document /></el-icon></div>
        <div class="stat-content">
          <div class="stat-value">{{ stats.total }}</div>
          <div class="stat-label">累计申请</div>
        </div>
      </div>
    </div>

    <!-- 申请记录 -->
    <div v-loading="loading" class="content-card">
      <div class="card-header">
        <h3 class="card-title">申请记录</h3>
        <div class="filter-bar">
          <el-select
            v-model="filterStatus"
            placeholder="全部状态"
            clearable
            style="width: 130px"
            @change="onFilterChange"
          >
            <el-option label="待审核" :value="0" />
            <el-option label="已通过" :value="1" />
            <el-option label="已拒绝" :value="2" />
          </el-select>
          <el-button plain @click="resetFilter">重置</el-button>
        </div>
      </div>

      <div class="table-wrapper">
        <el-table :data="pagedList" stripe style="width: 100%">
          <el-table-column label="融资产品" width="150" show-overflow-tooltip>
            <template #default="{ row }">{{ resolveProductName(row) }}</template>
          </el-table-column>
          <el-table-column label="申请金额" width="130" align="right">
            <template #default="{ row }">¥ {{ formatNumber(row.money) }}</template>
          </el-table-column>
          <el-table-column prop="purpose" label="融资用途" min-width="160" show-overflow-tooltip />
          <el-table-column label="还款期限" width="100" align="right">
            <template #default="{ row }">{{ row.repayment ? row.repayment + ' 个月' : '—' }}</template>
          </el-table-column>
          <el-table-column label="状态" width="100" align="center">
            <template #default="{ row }">
              <el-tag :type="statusType(row.status)" size="small">{{ statusText(row.status) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="申请时间" width="160">
            <template #default="{ row }">{{ formatTime(row.createTime) }}</template>
          </el-table-column>
          <el-table-column label="操作" width="100" fixed="right">
            <template #default="{ row }">
              <el-button type="primary" link size="small" @click="viewDetail(row)">详情</el-button>
            </template>
          </el-table-column>
          <template #empty>
            <el-empty description="暂无融资申请记录" />
          </template>
        </el-table>

        <div v-if="filteredList.length > pageSize" class="pagination-wrapper">
          <el-pagination
            v-model:current-page="page"
            :page-size="pageSize"
            :total="filteredList.length"
            layout="total, prev, pager, next"
          />
        </div>
      </div>
    </div>

    <!-- 详情对话框 -->
    <el-dialog v-model="detailVisible" title="融资申请详情" width="680px">
      <el-descriptions v-if="currentDetail" :column="2" border>
        <el-descriptions-item label="申请人">{{ currentDetail.realName || currentDetail.ownName }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ currentDetail.phone || '—' }}</el-descriptions-item>
        <el-descriptions-item label="身份证号">{{ currentDetail.idNum || '—' }}</el-descriptions-item>
        <el-descriptions-item label="融资产品">
          <span>{{ resolveProductName(currentDetail) }}</span>
          <div v-if="currentProduct?.introduce" class="product-introduce">{{ currentProduct.introduce }}</div>
        </el-descriptions-item>
        <el-descriptions-item label="申请金额">¥ {{ formatNumber(currentDetail.money) }}</el-descriptions-item>
        <el-descriptions-item label="年利率">{{ currentDetail.rate != null ? currentDetail.rate + '%' : '—' }}</el-descriptions-item>
        <el-descriptions-item label="还款期限">{{ currentDetail.repayment ? currentDetail.repayment + ' 个月' : '—' }}</el-descriptions-item>
        <el-descriptions-item label="申请状态">
          <el-tag :type="statusType(currentDetail.status)" size="small">{{ statusText(currentDetail.status) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="融资用途" :span="2">{{ currentDetail.purpose || '—' }}</el-descriptions-item>
        <el-descriptions-item label="还款来源" :span="2">{{ currentDetail.repaymentSource || '—' }}</el-descriptions-item>
        <el-descriptions-item v-if="currentInvitations.length" label="联合贷款人" :span="2">
          <div class="combination-block">
            <div v-for="inv in currentInvitations" :key="inv.id" class="combination-person">
              <span class="cp-label">联合人{{ inv.slot }}：</span>
              {{ inv.jointRealName || inv.jointUserName }} · {{ inv.jointPhone || '—' }}
              <el-tag size="small" :type="inviteStatusType(inv.status)" style="margin-left: 8px">
                {{ inviteStatusText(inv.status) }}
              </el-tag>
            </div>
          </div>
        </el-descriptions-item>
        <el-descriptions-item v-else-if="hasCombination(currentDetail)" label="联合贷款人" :span="2">
          <div class="combination-block">
            <div v-if="currentDetail.combinationName1 || currentDetail.combinationPhone1 || currentDetail.combinationIdnum1" class="combination-person">
              <span class="cp-label">联合人1：</span>{{ currentDetail.combinationName1 || '—' }} · {{ currentDetail.combinationPhone1 || '—' }} · {{ currentDetail.combinationIdnum1 || '—' }}
            </div>
            <div v-if="currentDetail.combinationName2 || currentDetail.combinationPhone2 || currentDetail.combinationIdnum2" class="combination-person">
              <span class="cp-label">联合人2：</span>{{ currentDetail.combinationName2 || '—' }} · {{ currentDetail.combinationPhone2 || '—' }} · {{ currentDetail.combinationIdnum2 || '—' }}
            </div>
          </div>
        </el-descriptions-item>
        <el-descriptions-item label="申请时间" :span="2">{{ formatTime(currentDetail.createTime) }}</el-descriptions-item>
        <el-descriptions-item v-if="currentDetail.remark" label="审批备注" :span="2">{{ currentDetail.remark }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus, Clock, Select, Close, Document } from '@element-plus/icons-vue'
import { getMyFinanceApplications, getFinanceProducts, getFinanceJointInvitations } from '@/api/finance'

const loading = ref(false)
const detailVisible = ref(false)
const currentDetail = ref(null)
// 当前详情对应的联合贷款人邀请（按 slot 排序），用于展示各联合人确认状态
const currentInvitations = ref([])

// 个人申请数量通常较少，一次性拉取后在前端做统计/筛选/分页
const allList = ref([])
const filterStatus = ref(undefined)
const page = ref(1)
const pageSize = 10

// 融资产品映射表（productId -> 产品对象），用于前端补全“融资产品”名称
const productMap = ref({})

const stats = reactive({
  pending: 0,
  approved: 0,
  rejected: 0,
  total: 0
})

// 按状态筛选（前端）
const filteredList = computed(() => {
  if (filterStatus.value === undefined || filterStatus.value === null) return allList.value
  return allList.value.filter(a => a.status === filterStatus.value)
})

// 前端分页
const pagedList = computed(() => {
  const start = (page.value - 1) * pageSize
  return filteredList.value.slice(start, start + pageSize)
})

onMounted(() => {
  loadApplications()
  loadProducts()
})

// 拉取融资产品列表，建立 productId -> 产品 映射，便于前端补全产品名称
const loadProducts = async () => {
  try {
    const res = await getFinanceProducts({ page: 1, pageSize: 1000 })
    const records = res.data?.records || []
    const map = {}
    records.forEach(p => {
      if (p.productId != null) map[p.productId] = p
    })
    productMap.value = map
  } catch (e) {
    // 忽略：加载失败时仍可回退到后端回填的 productName
  }
}

// 解析融资产品名称：优先用后端回填的 productName，缺失则按 productId 在前端补全
const resolveProductName = (row) => {
  if (!row) return '—'
  if (row.productName) return row.productName
  const p = productMap.value[row.productId]
  return p ? p.bankName : '—'
}

// 当前详情对应的产品对象（用于展示产品介绍）
const currentProduct = computed(() => productMap.value[currentDetail.value?.productId] || null)

const loadApplications = async () => {
  loading.value = true
  try {
    const res = await getMyFinanceApplications({ page: 1, pageSize: 1000 })
    allList.value = res.data?.records || []
    computeStats()
  } catch (error) {
    ElMessage.error(error?.message || '加载融资申请失败')
  } finally {
    loading.value = false
  }
}

const computeStats = () => {
  const all = allList.value
  stats.pending = all.filter(a => a.status === 0).length
  stats.approved = all.filter(a => a.status === 1).length
  stats.rejected = all.filter(a => a.status === 2).length
  stats.total = all.length
}

const onFilterChange = () => {
  page.value = 1
}

const resetFilter = () => {
  filterStatus.value = undefined
  page.value = 1
}

const viewDetail = async (row) => {
  currentDetail.value = row
  currentInvitations.value = []
  detailVisible.value = true
  // 拉取该申请下的联合贷款人邀请，展示各联合人确认状态（待确认/已同意/已拒绝）
  try {
    const res = await getFinanceJointInvitations(row.financeId)
    currentInvitations.value = res.data || []
  } catch (e) {
    // 忽略：老申请可能没有邀请记录
  }
}

// 是否含联合贷款人信息（任一字段非空即认为填写了）
const hasCombination = (d) => !!(d && (d.combinationName1 || d.combinationPhone1 || d.combinationIdnum1 || d.combinationName2 || d.combinationPhone2 || d.combinationIdnum2))

const statusType = (s) => ({ 0: 'warning', 1: 'success', 2: 'danger' }[s] || 'info')
const statusText = (s) => ({ 0: '待审核', 1: '已通过', 2: '已拒绝' }[s] || '未知')

// 联合贷款人邀请状态
const inviteStatusType = (s) => ({ 0: 'warning', 1: 'success', 2: 'danger' }[s] || 'info')
const inviteStatusText = (s) => ({ 0: '待确认', 1: '已同意', 2: '已拒绝' }[s] || '未知')

const formatNumber = (num) => {
  if (num === null || num === undefined || num === '') return '0'
  return Number(num).toLocaleString()
}

const formatTime = (t) => {
  if (!t) return '—'
  const d = new Date(t)
  if (isNaN(d.getTime())) return t
  const p = (n) => String(n).padStart(2, '0')
  return `${d.getFullYear()}-${p(d.getMonth() + 1)}-${p(d.getDate())} ${p(d.getHours())}:${p(d.getMinutes())}`
}
</script>

<style scoped>
.my-finance {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-6, 24px);
}

/* 页面标题 */
.page-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: var(--spacing-4, 16px);
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

/* 统计卡片 */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: var(--spacing-4, 16px);
}
.stat-card {
  display: flex;
  align-items: center;
  gap: var(--spacing-4, 16px);
  padding: var(--spacing-5, 20px);
  background: var(--color-bg-primary, #ffffff);
  border-radius: var(--radius-lg, 12px);
  border: 1px solid var(--color-border, #e5e0d8);
  box-shadow: var(--shadow-sm, 0 2px 4px rgba(31, 41, 35, 0.06));
  transition: all var(--transition-fast, 150ms ease);
}
.stat-card:hover {
  box-shadow: var(--shadow-base, 0 4px 12px rgba(31, 41, 35, 0.08));
  transform: translateY(-2px);
}
.stat-icon {
  width: 48px;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: var(--radius-lg, 12px);
  font-size: 24px;
  flex-shrink: 0;
}
.stat-card.warning .stat-icon { background: var(--color-warning-light, #fdf8e8); color: var(--color-warning, #c9a661); }
.stat-card.success .stat-icon { background: var(--color-success-light, #e8f0ec); color: var(--color-success, #4a7c59); }
.stat-card.error .stat-icon { background: var(--color-error-light, #fce8e0); color: var(--color-error, #b85c38); }
.stat-card.primary .stat-icon { background: rgba(74, 124, 89, 0.1); color: var(--color-farmer, #4a7c59); }
.stat-value {
  font-size: var(--font-size-2xl, 30px);
  font-weight: var(--font-weight-bold, 700);
  color: var(--color-text-primary, #1f2923);
  line-height: var(--line-height-tight, 1.25);
}
.stat-label {
  font-size: var(--font-size-sm, 14px);
  color: var(--color-text-tertiary, #6b7280);
  margin-top: var(--spacing-1, 4px);
}

/* 内容卡片 */
.content-card {
  background: var(--color-bg-primary, #ffffff);
  border-radius: var(--radius-lg, 12px);
  border: 1px solid var(--color-border, #e5e0d8);
  box-shadow: var(--shadow-sm, 0 2px 4px rgba(31, 41, 35, 0.06));
  overflow: hidden;
}
.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: var(--spacing-3, 12px);
  padding: var(--spacing-4, 16px) var(--spacing-5, 20px);
  border-bottom: 1px solid var(--color-divider, #edebe6);
  flex-wrap: wrap;
}
.card-title {
  font-size: var(--font-size-lg, 20px);
  font-weight: var(--font-weight-semibold, 600);
  color: var(--color-text-primary, #1f2923);
  margin: 0;
}
.filter-bar {
  display: flex;
  align-items: center;
  gap: var(--spacing-2, 8px);
}
.product-introduce {
  margin-top: var(--spacing-1, 4px);
  font-size: var(--font-size-sm, 14px);
  color: var(--color-text-tertiary, #6b7280);
  line-height: 1.5;
}
.combination-block {
  display: flex;
  flex-direction: column;
  gap: 6px;
}
.combination-person {
  font-size: var(--font-size-sm, 14px);
  color: var(--color-text-secondary, #4a5249);
  line-height: 1.5;
}
.cp-label {
  color: var(--color-text-tertiary, #6b7280);
  margin-right: 4px;
}
.table-wrapper {
  padding: var(--spacing-4, 16px);
}
.pagination-wrapper {
  display: flex;
  justify-content: flex-end;
  padding-top: var(--spacing-3, 12px);
}

@media (max-width: 1024px) {
  .stats-grid { grid-template-columns: repeat(2, 1fr); }
}
@media (max-width: 640px) {
  .stats-grid { grid-template-columns: 1fr; }
  .page-header { flex-direction: column; align-items: stretch; }
}
</style>
