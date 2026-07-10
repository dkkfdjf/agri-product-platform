<template>
  <div class="my-finance-container">
    <div class="page-header">
      <h2>我的融资申请</h2>
      <el-button type="primary" @click="$router.push('/finance')">
        <el-icon><Plus /></el-icon>
        申请新融资
      </el-button>
    </div>

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
        <div class="stat-icon total">
          <el-icon><Document /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ stats.total }}</div>
          <div class="stat-label">总申请</div>
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
        <el-table-column prop="productName" label="产品名称" width="160" />
        <el-table-column prop="amount" label="申请金额" width="120">
          <template #default="{ row }">
            {{ row.amount }} 万元
          </template>
        </el-table-column>
        <el-table-column prop="application" label="融资用途" show-overflow-tooltip />
        <el-table-column prop="item" label="农作物" width="100" />
        <el-table-column prop="area" label="种植面积" width="100">
          <template #default="{ row }">
            {{ row.area }} 亩
          </template>
        </el-table-column>
        <el-table-column prop="repaymentPeriod" label="还款期限" width="100">
          <template #default="{ row }">
            {{ row.repaymentPeriod }} 个月
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="申请时间" width="160">
          <template #default="{ row }">
            {{ formatDate(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
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
          layout="total, prev, pager, next"
          @current-change="loadApplications"
        />
      </div>

      <el-empty v-if="!loading && applications.length === 0" description="暂无融资申请记录" />
    </el-card>

    <!-- 详情对话框 -->
    <el-dialog v-model="detailVisible" title="融资申请详情" width="600px">
      <div v-if="currentDetail" class="detail-content">
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
            <div class="detail-item">
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
              <span class="label">联系电话</span>
              <span class="value">{{ currentDetail.phone }}</span>
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
            <div v-if="currentDetail.remark" class="detail-item full-width">
              <span class="label">审核说明</span>
              <span class="value">{{ currentDetail.remark }}</span>
            </div>
            <div v-if="currentDetail.reviewTime" class="detail-item">
              <span class="label">审核时间</span>
              <span class="value">{{ formatDate(currentDetail.reviewTime) }}</span>
            </div>
          </div>
        </div>
      </div>

      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus, Clock, Select, Close, Document } from '@element-plus/icons-vue'
import { getMyFinanceApplications } from '@/api/finance'

const loading = ref(false)
const filterStatus = ref('')
const filterDateRange = ref([])
const detailVisible = ref(false)
const currentDetail = ref(null)

const applications = ref([])
const pagination = reactive({
  page: 1,
  pageSize: 10,
  total: 0
})

const stats = reactive({
  pending: 0,
  approved: 0,
  rejected: 0,
  total: 0
})

onMounted(() => {
  loadApplications()
})

const loadApplications = async () => {
  try {
    loading.value = true
    const params = {
      page: pagination.page,
      pageSize: pagination.pageSize,
      status: filterStatus.value
    }

    if (filterDateRange.value && filterDateRange.value.length === 2) {
      params.startDate = formatDate(filterDateRange.value[0])
      params.endDate = formatDate(filterDateRange.value[1])
    }

    const res = await getMyFinanceApplications(params)
    applications.value = res.data.records || []
    pagination.total = res.data.total || 0

    // 更新统计数据
    updateStats()
  } catch (error) {
    ElMessage.error('加载融资申请失败')
  } finally {
    loading.value = false
  }
}

const updateStats = () => {
  const all = applications.value
  stats.pending = all.filter(a => a.status === 0).length
  stats.approved = all.filter(a => a.status === 1).length
  stats.rejected = all.filter(a => a.status === 2).length
  stats.total = pagination.total
}

const viewDetail = (row) => {
  currentDetail.value = row
  detailVisible.value = true
}

const getStatusType = (status) => {
  const types = { 0: 'warning', 1: 'success', 2: 'danger' }
  return types[status] || 'info'
}

const getStatusText = (status) => {
  const texts = { 0: '待审核', 1: '已通过', 2: '已拒绝' }
  return texts[status] || '未知'
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
.my-finance-container {
  max-width: 1200px;
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

.stat-icon.total {
  background: #e3f2fd;
  color: #1976d2;
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
}

.table-card {
  margin-bottom: 20px;
}

.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 20px;
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
  .filter-section .el-date-picker {
    width: 100% !important;
  }

  .detail-grid {
    grid-template-columns: 1fr;
  }
}
</style>
