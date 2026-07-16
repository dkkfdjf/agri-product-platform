<template>
  <div class="bank-finance-container">
    <div class="page-header">
      <h2>融资申请管理</h2>
      <el-button type="primary" @click="loadIntentionMatch" :loading="loadingMatch">
        <el-icon><Refresh /></el-icon>
        刷新匹配
      </el-button>
    </div>

    <!-- 智能匹配提示 -->
    <el-card v-if="matchCount > 0" class="match-card">
      <div class="match-header">
        <el-icon class="match-icon"><Bell /></el-icon>
        <span>有 {{ matchCount }} 个新匹配的融资意向</span>
        <el-button type="primary" link @click="showMatchDialog = true">查看详情</el-button>
      </div>
    </el-card>

    <!-- 统计卡片 -->
    <div class="stats-row">
      <div class="stat-card">
        <div class="stat-icon pending">
          <el-icon><Clock /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ stats.pending }}</div>
          <div class="stat-label">待审核</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon approved">
          <el-icon><Select /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ stats.approved }}</div>
          <div class="stat-label">已通过</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon rejected">
          <el-icon><Close /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ stats.rejected }}</div>
          <div class="stat-label">已拒绝</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon amount">
          <el-icon><Money /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ formatAmount(stats.totalAmount) }}</div>
          <div class="stat-label">申请总额(万)</div>
        </div>
      </div>
    </div>

    <!-- 筛选区域 -->
    <div class="filter-section">
      <el-select v-model="filterStatus" placeholder="申请状态" clearable style="width: 140px" @change="loadApplications">
        <el-option label="全部" value="" />
        <el-option label="待审核" value="0" />
        <el-option label="已通过" value="1" />
        <el-option label="已拒绝" value="2" />
      </el-select>
      <el-input
        v-model="filterKeyword"
        placeholder="搜索农户姓名/手机号"
        style="width: 200px"
        clearable
        @change="loadApplications"
      />
      <el-date-picker
        v-model="filterDateRange"
        type="daterange"
        range-separator="至"
        start-placeholder="开始日期"
        end-placeholder="结束日期"
        style="width: 280px"
        @change="loadApplications"
      />
    </div>

    <!-- 申请列表 -->
    <el-card v-loading="loading" class="table-card">
      <el-table :data="applications" stripe>
        <el-table-column prop="farmerName" label="农户姓名" width="100" />
        <el-table-column prop="phone" label="联系电话" width="130" />
        <el-table-column prop="productName" label="产品名称" width="140" />
        <el-table-column prop="amount" label="申请金额" width="100">
          <template #default="{ row }">
            {{ row.amount }} 万元
          </template>
        </el-table-column>
        <el-table-column prop="application" label="融资用途" show-overflow-tooltip min-width="150" />
        <el-table-column prop="item" label="农作物" width="90" />
        <el-table-column prop="area" label="种植面积" width="90">
          <template #default="{ row }">
            {{ row.area }} 亩
          </template>
        </el-table-column>
        <el-table-column prop="repaymentPeriod" label="还款期限" width="90">
          <template #default="{ row }">
            {{ row.repaymentPeriod }} 个月
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="申请时间" width="150">
          <template #default="{ row }">
            {{ formatDate(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button
              v-if="row.status === 0"
              type="success"
              link
              size="small"
              @click="handleApprove(row, 1)"
            >
              通过
            </el-button>
            <el-button
              v-if="row.status === 0"
              type="danger"
              link
              size="small"
              @click="handleApprove(row, 2)"
            >
              拒绝
            </el-button>
            <el-button type="primary" link size="small" @click="viewDetail(row)">
              详情
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div v-if="pagination.total > 0" class="pagination-wrapper">
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.pageSize"
          :total="pagination.total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next"
          @current-change="loadApplications"
          @size-change="loadApplications"
        />
      </div>

      <el-empty v-if="!loading && applications.length === 0" description="暂无融资申请记录" />
    </el-card>

    <!-- 匹配详情对话框 -->
    <el-dialog v-model="showMatchDialog" title="融资意向智能匹配" width="800px">
      <el-table :data="matchList" max-height="400">
        <el-table-column prop="farmerName" label="农户" width="100" />
        <el-table-column prop="productNeed" label="资金需求" width="100" />
        <el-table-column prop="cropType" label="种植作物" width="100" />
        <el-table-column prop="creditScore" label="信用评分" width="90">
          <template #default="{ row }">
            <el-tag :type="row.creditScore >= 80 ? 'success' : row.creditScore >= 60 ? 'warning' : 'danger'">
              {{ row.creditScore }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="matchReason" label="匹配理由" show-overflow-tooltip />
        <el-table-column label="操作" width="100">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="contactFarmer(row)">
              联系
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>

    <!-- 审核对话框 -->
    <el-dialog v-model="approveDialogVisible" :title="approveTitle" width="500px">
      <el-form :model="approveForm" label-width="100px">
        <el-form-item label="申请信息">
          <div class="approve-info">
            <p><strong>农户：</strong>{{ currentApplication?.farmerName }}</p>
            <p><strong>产品：</strong>{{ currentApplication?.productName }}</p>
            <p><strong>金额：</strong>{{ currentApplication?.amount }} 万元</p>
            <p><strong>用途：</strong>{{ currentApplication?.application }}</p>
          </div>
        </el-form-item>
        <el-form-item label="审核意见" prop="remark">
          <el-input
            v-model="approveForm.remark"
            type="textarea"
            :rows="4"
            :placeholder="approveForm.status === 1 ? '请填写通过理由（选填）' : '请填写拒绝原因'"
            maxlength="200"
            show-word-limit
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="approveDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="approving" @click="submitApprove">
          确认{{ approveForm.status === 1 ? '通过' : '拒绝' }}
        </el-button>
      </template>
    </el-dialog>

    <!-- 详情对话框 -->
    <el-dialog v-model="detailVisible" title="融资申请详情" width="700px">
      <div v-if="currentDetail" class="detail-content">
        <div class="detail-section">
          <h4>农户信息</h4>
          <div class="detail-grid">
            <div class="detail-item">
              <span class="label">农户姓名</span>
              <span class="value">{{ currentDetail.farmerName }}</span>
            </div>
            <div class="detail-item">
              <span class="label">联系电话</span>
              <span class="value">{{ currentDetail.phone }}</span>
            </div>
            <div v-if="currentDetail.address" class="detail-item full-width">
              <span class="label">种植地址</span>
              <span class="value">{{ currentDetail.address }}</span>
            </div>
          </div>
        </div>

        <div class="detail-section">
          <h4>申请信息</h4>
          <div class="detail-grid">
            <div class="detail-item">
              <span class="label">产品名称</span>
              <span class="value">{{ currentDetail.productName }}</span>
            </div>
            <div class="detail-item">
              <span class="label">申请金额</span>
              <span class="value">{{ currentDetail.amount }} 万元</span>
            </div>
            <div class="detail-item full-width">
              <span class="label">融资用途</span>
              <span class="value">{{ currentDetail.application }}</span>
            </div>
            <div class="detail-item">
              <span class="label">农作物类型</span>
              <span class="value">{{ currentDetail.item }}</span>
            </div>
            <div class="detail-item">
              <span class="label">种植面积</span>
              <span class="value">{{ currentDetail.area }} 亩</span>
            </div>
            <div class="detail-item">
              <span class="label">还款期限</span>
              <span class="value">{{ currentDetail.repaymentPeriod }} 个月</span>
            </div>
            <div class="detail-item">
              <span class="label">申请状态</span>
              <el-tag :type="getStatusType(currentDetail.status)">
                {{ getStatusText(currentDetail.status) }}
              </el-tag>
            </div>
            <div class="detail-item">
              <span class="label">申请时间</span>
              <span class="value">{{ formatDate(currentDetail.createTime) }}</span>
            </div>
          </div>
        </div>

        <div v-if="currentDetail.status !== 0" class="detail-section">
          <h4>审核信息</h4>
          <div class="detail-grid">
            <div class="detail-item">
              <span class="label">审核结果</span>
              <el-tag :type="getStatusType(currentDetail.status)">
                {{ getStatusText(currentDetail.status) }}
              </el-tag>
            </div>
            <div v-if="currentDetail.reviewTime" class="detail-item">
              <span class="label">审核时间</span>
              <span class="value">{{ formatDate(currentDetail.reviewTime) }}</span>
            </div>
            <div v-if="currentDetail.remark" class="detail-item full-width">
              <span class="label">审核说明</span>
              <span class="value">{{ currentDetail.remark }}</span>
            </div>
          </div>
        </div>
      </div>

      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
        <el-button
          v-if="currentDetail?.status === 0"
          type="success"
          @click="handleApprove(currentDetail, 1); detailVisible = false"
        >
          审核通过
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Refresh, Bell, Clock, Select, Close, Money } from '@element-plus/icons-vue'
import { getFinanceList, approveFinance, getFinanceIntentionMatch } from '@/api/finance'

const loading = ref(false)
const loadingMatch = ref(false)
const filterStatus = ref('')
const filterKeyword = ref('')
const filterDateRange = ref([])
const showMatchDialog = ref(false)
const approveDialogVisible = ref(false)
const detailVisible = ref(false)
const approving = ref(false)

const currentApplication = ref(null)
const currentDetail = ref(null)
const matchList = ref([])
const matchCount = ref(0)

const applications = ref([])
const pagination = reactive({
  page: 1,
  pageSize: 20,
  total: 0
})

const approveForm = reactive({
  id: null,
  status: 1,
  remark: ''
})

const stats = reactive({
  pending: 0,
  approved: 0,
  rejected: 0,
  totalAmount: 0
})

const approveTitle = computed(() => {
  return approveForm.status === 1 ? '审核通过' : '审核拒绝'
})

onMounted(() => {
  loadApplications()
  loadMatchCount()
})

const loadApplications = async () => {
  try {
    loading.value = true
    const params = {
      page: pagination.page,
      pageSize: pagination.pageSize,
      status: filterStatus.value,
      keyword: filterKeyword.value
    }

    if (filterDateRange.value && filterDateRange.value.length === 2) {
      params.startDate = formatDate(filterDateRange.value[0])
      params.endDate = formatDate(filterDateRange.value[1])
    }

    const res = await getFinanceList(params)
    applications.value = res.data.records || []
    pagination.total = res.data.total || 0

    updateStats(res.data.stats || {})
  } catch (error) {
    ElMessage.error('加载融资申请失败')
  } finally {
    loading.value = false
  }
}

const loadMatchCount = async () => {
  try {
    const res = await getFinanceIntentionMatch()
    matchList.value = res.data || []
    matchCount.value = matchList.value.length
  } catch (error) {
    console.error('获取匹配数据失败', error)
  }
}

const loadIntentionMatch = async () => {
  try {
    loadingMatch.value = true
    await loadMatchCount()
    if (matchCount.value > 0) {
      showMatchDialog.value = true
    } else {
      ElMessage.info('暂无新的匹配意向')
    }
  } catch (error) {
    ElMessage.error('获取匹配数据失败')
  } finally {
    loadingMatch.value = false
  }
}

const updateStats = (data) => {
  if (data.pending !== undefined) stats.pending = data.pending
  if (data.approved !== undefined) stats.approved = data.approved
  if (data.rejected !== undefined) stats.rejected = data.rejected
  if (data.totalAmount !== undefined) stats.totalAmount = data.totalAmount
}

const handleApprove = (row, status) => {
  currentApplication.value = row
  approveForm.id = row.id
  approveForm.status = status
  approveForm.remark = ''
  approveDialogVisible.value = true
}

const submitApprove = async () => {
  try {
    approving.value = true
    await approveFinance({
      id: approveForm.id,
      status: approveForm.status,
      remark: approveForm.remark
    })

    ElMessage.success(approveForm.status === 1 ? '审核通过' : '已拒绝申请')
    approveDialogVisible.value = false
    loadApplications()
  } catch (error) {
    ElMessage.error('操作失败，请重试')
  } finally {
    approving.value = false
  }
}

const viewDetail = (row) => {
  currentDetail.value = row
  detailVisible.value = true
}

const contactFarmer = (row) => {
  ElMessageBox.alert(
    `联系电话：${row.phone}\n\n农户姓名：${row.farmerName}\n资金需求：${row.productNeed}万元`,
    '联系农户',
    {
      confirmButtonText: '关闭',
      type: 'info'
    }
  )
}

const getStatusType = (status) => {
  const types = { 0: 'warning', 1: 'success', 2: 'danger' }
  return types[status] || 'info'
}

const getStatusText = (status) => {
  const texts = { 0: '待审核', 1: '已通过', 2: '已拒绝' }
  return texts[status] || '未知'
}

const formatAmount = (amount) => {
  if (!amount) return '0'
  return amount >= 10000 ? `${(amount / 10000).toFixed(1)}万` : amount.toFixed(0)
}

const formatDate = (date) => {
  if (!date) return '-'
  const d = new Date(date)
  const year = d.getFullYear()
  const month = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  const hours = String(d.getHours()).padStart(2, '0')
  const minutes = String(d.getMinutes()).padStart(2, '0')
  return `${year}-${month}-${day} ${hours}:${minutes}`
}
</script>

<style scoped>
.bank-finance-container {
  max-width: 1400px;
  margin: 20px auto;
  padding: 0 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.page-header h2 {
  font-size: 24px;
  font-weight: 600;
  color: #1f2923;
  margin: 0;
}

.match-card {
  margin-bottom: 20px;
  background: linear-gradient(135deg, #fff8e6 0%, #ffffff 100%);
  border: 1px solid #ffe4b5;
}

.match-header {
  display: flex;
  align-items: center;
  gap: 10px;
  color: #d48806;
  font-weight: 500;
}

.match-icon {
  font-size: 20px;
  animation: ring 2s ease-in-out infinite;
}

@keyframes ring {
  0%, 100% { transform: rotate(0); }
  10%, 30% { transform: rotate(-10deg); }
  20%, 40% { transform: rotate(10deg); }
}

.stats-row {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  margin-bottom: 24px;
}

.stat-card {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px;
  background: white;
  border-radius: 12px;
  border: 1px solid #e5e0d8;
  box-shadow: 0 2px 8px rgba(31, 41, 35, 0.06);
}

.stat-icon {
  width: 48px;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 12px;
  font-size: 24px;
}

.stat-icon.pending {
  background: #fef3e7;
  color: #d48806;
}

.stat-icon.approved {
  background: #e8f5e9;
  color: #2d5a3d;
}

.stat-icon.rejected {
  background: #fee;
  color: #b85c38;
}

.stat-icon.amount {
  background: #fce4ec;
  color: #c2185b;
}

.stat-content {
  flex: 1;
}

.stat-value {
  font-size: 24px;
  font-weight: 700;
  color: #1f2923;
  line-height: 1.2;
}

.stat-label {
  font-size: 13px;
  color: #6b7280;
  margin-top: 4px;
}

.filter-section {
  display: flex;
  gap: 12px;
  margin-bottom: 16px;
  flex-wrap: wrap;
}

.table-card {
  margin-bottom: 20px;
}

.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

.approve-info {
  padding: 12px;
  background: #f7f5f0;
  border-radius: 8px;
}

.approve-info p {
  margin: 4px 0;
  font-size: 14px;
}

.detail-content {
  padding: 10px 0;
}

.detail-section {
  margin-bottom: 24px;
}

.detail-section:last-child {
  margin-bottom: 0;
}

.detail-section h4 {
  font-size: 16px;
  font-weight: 600;
  color: #1f2923;
  margin: 0 0 16px;
  padding-bottom: 8px;
  border-bottom: 1px solid #edebe6;
}

.detail-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

.detail-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.detail-item.full-width {
  grid-column: 1 / -1;
}

.detail-item .label {
  font-size: 13px;
  color: #6b7280;
}

.detail-item .value {
  font-size: 14px;
  color: #1f2923;
  font-weight: 500;
}

@media (max-width: 1024px) {
  .stats-row {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 640px) {
  .stats-row {
    grid-template-columns: 1fr;
  }

  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }

  .filter-section {
    flex-direction: column;
  }

  .filter-section .el-select,
  .filter-section .el-input,
  .filter-section .el-date-picker {
    width: 100% !important;
  }

  .detail-grid {
    grid-template-columns: 1fr;
  }
}
</style>
