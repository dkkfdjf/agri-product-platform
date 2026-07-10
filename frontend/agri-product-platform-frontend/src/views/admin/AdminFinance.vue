<template>
  <div class="admin-finance">
    <div class="header">
      <h2>融资管理</h2>
      <div class="filters">
        <el-select v-model="statusFilter" placeholder="状态筛选" clearable @change="loadFinances" style="width: 150px">
          <el-option label="待审核" value="pending" />
          <el-option label="已通过" value="approved" />
          <el-option label="已拒绝" value="rejected" />
        </el-select>
      </div>
    </div>

    <el-table :data="finances" style="width: 100%" v-loading="loading">
      <el-table-column prop="applicantName" label="申请人" width="120" />
      <el-table-column prop="amount" label="申请金额" width="120">
        <template #default="{ row }">
          ¥{{ row.amount }}
        </template>
      </el-table-column>
      <el-table-column prop="purpose" label="用途" show-overflow-tooltip />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="getStatusType(row.status)">
            {{ getStatusLabel(row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="applyTime" label="申请时间" width="180" />
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" @click="viewDetail(row)">详情</el-button>
          <el-button
            v-if="row.status === 'pending'"
            link
            type="success"
            @click="approve(row)"
          >
            审核
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- Detail Dialog -->
    <el-dialog v-model="detailVisible" title="融资申请详情" width="600px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="申请人">{{ currentFinance.applicantName }}</el-descriptions-item>
        <el-descriptions-item label="申请金额">¥{{ currentFinance.amount }}</el-descriptions-item>
        <el-descriptions-item label="用途" :span="2">{{ currentFinance.purpose }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusType(currentFinance.status)">
            {{ getStatusLabel(currentFinance.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="申请时间">{{ currentFinance.applyTime }}</el-descriptions-item>
        <el-descriptions-item v-if="currentFinance.remark" label="审核备注" :span="2">
          {{ currentFinance.remark }}
        </el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
      </template>
    </el-dialog>

    <!-- Approval Dialog -->
    <el-dialog v-model="approveVisible" title="审核融资申请" width="500px">
      <el-form :model="approveForm" label-width="80px">
        <el-form-item label="审核结果">
          <el-radio-group v-model="approveForm.status">
            <el-radio value="approved">通过</el-radio>
            <el-radio value="rejected">拒绝</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="审核备注">
          <el-input
            v-model="approveForm.remark"
            type="textarea"
            :rows="3"
            placeholder="请输入审核备注（选填）"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="approveVisible = false">取消</el-button>
        <el-button type="primary" @click="submitApprove" :loading="approving">提交</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getAdminFinances, approveFinance } from '@/api/admin'

const loading = ref(false)
const approving = ref(false)
const finances = ref([])
const statusFilter = ref('')
const detailVisible = ref(false)
const approveVisible = ref(false)
const currentFinance = ref({})
const currentApproveId = ref(null)

const approveForm = reactive({
  status: 'approved',
  remark: ''
})

onMounted(() => {
  loadFinances()
})

const loadFinances = async () => {
  loading.value = true
  try {
    const res = await getAdminFinances({ status: statusFilter.value })
    finances.value = res.data || []
  } catch (error) {
    ElMessage.error('加载融资申请失败')
  } finally {
    loading.value = false
  }
}

const getStatusType = (status) => {
  const types = {
    pending: 'warning',
    approved: 'success',
    rejected: 'danger'
  }
  return types[status] || ''
}

const getStatusLabel = (status) => {
  const labels = {
    pending: '待审核',
    approved: '已通过',
    rejected: '已拒绝'
  }
  return labels[status] || status
}

const viewDetail = (finance) => {
  currentFinance.value = finance
  detailVisible.value = true
}

const approve = (finance) => {
  currentApproveId.value = finance.id
  approveForm.status = 'approved'
  approveForm.remark = ''
  approveVisible.value = true
}

const submitApprove = async () => {
  approving.value = true
  try {
    await approveFinance({
      id: currentApproveId.value,
      status: approveForm.status,
      remark: approveForm.remark
    })
    ElMessage.success('审核成功')
    approveVisible.value = false
    await loadFinances()
  } catch (error) {
    ElMessage.error('审核失败')
  } finally {
    approving.value = false
  }
}
</script>

<style scoped>
.admin-finance {
  max-width: 1200px;
  margin: 20px auto;
  padding: 0 20px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.header h2 {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
}

.filters {
  display: flex;
  gap: 10px;
}
</style>
