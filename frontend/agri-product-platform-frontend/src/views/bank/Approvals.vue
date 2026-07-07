<template>
  <div class="bank-approvals">
    <h2>融资审批</h2>

    <el-card>
      <el-table :data="approvalList" stripe>
        <el-table-column prop="financeId" label="申请编号" width="100" />
        <el-table-column prop="farmerName" label="申请人" width="120" />
        <el-table-column prop="productName" label="融资产品" />
        <el-table-column prop="amount" label="申请金额" width="120">
          <template #default="{ row }">
            ¥{{ row.amount }}
          </template>
        </el-table-column>
        <el-table-column prop="term" label="期限" width="100">
          <template #default="{ row }">
            {{ row.term }}个月
          </template>
        </el-table-column>
        <el-table-column prop="purpose" label="借款用途" width="150" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="申请时间" width="180" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button
              v-if="row.status === 0"
              type="primary"
              size="small"
              link
              @click="handleApprove(row)"
            >
              审批
            </el-button>
            <el-button type="info" size="small" link @click="handleView(row)">
              详情
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 审批对话框 -->
    <el-dialog v-model="approveVisible" title="融资审批" width="600px">
      <div v-if="currentApproval" class="approval-info">
        <el-descriptions title="申请信息" :column="2" border>
          <el-descriptions-item label="申请编号">{{ currentApproval.financeId }}</el-descriptions-item>
          <el-descriptions-item label="申请人">{{ currentApproval.farmerName }}</el-descriptions-item>
          <el-descriptions-item label="联系电话">{{ currentApproval.phone }}</el-descriptions-item>
          <el-descriptions-item label="申请金额">¥{{ currentApproval.amount }}</el-descriptions-item>
          <el-descriptions-item label="贷款期限">{{ currentApproval.term }}个月</el-descriptions-item>
          <el-descriptions-item label="融资产品">{{ currentApproval.productName }}</el-descriptions-item>
          <el-descriptions-item label="借款用途" span="2">{{ currentApproval.purpose }}</el-descriptions-item>
          <el-descriptions-item label="还款来源" span="2">{{ currentApproval.source }}</el-descriptions-item>
          <el-descriptions-item label="申请时间" span="2">{{ currentApproval.createTime }}</el-descriptions-item>
        </el-descriptions>
      </div>
      <el-form :model="approveForm" :rules="approveRules" ref="approveFormRef" label-width="100px" style="margin-top: 20px">
        <el-form-item label="审批结果" prop="status">
          <el-radio-group v-model="approveForm.status">
            <el-radio :value="1">通过</el-radio>
            <el-radio :value="2">拒绝</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="审批意见" prop="remark">
          <el-input v-model="approveForm.remark" type="textarea" :rows="4" placeholder="请输入审批意见" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="approveVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitApprove" :loading="submitting">
          提交审批
        </el-button>
      </template>
    </el-dialog>

    <!-- 详情对话框 -->
    <el-dialog v-model="detailVisible" title="申请详情" width="600px">
      <el-descriptions :column="2" border v-if="currentApproval">
        <el-descriptions-item label="申请编号">{{ currentApproval.financeId }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusType(currentApproval.status)">
            {{ getStatusText(currentApproval.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="申请人">{{ currentApproval.farmerName }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ currentApproval.phone }}</el-descriptions-item>
        <el-descriptions-item label="申请金额">¥{{ currentApproval.amount }}</el-descriptions-item>
        <el-descriptions-item label="贷款期限">{{ currentApproval.term }}个月</el-descriptions-item>
        <el-descriptions-item label="融资产品" span="2">{{ currentApproval.productName }}</el-descriptions-item>
        <el-descriptions-item label="借款用途" span="2">{{ currentApproval.purpose }}</el-descriptions-item>
        <el-descriptions-item label="还款来源" span="2">{{ currentApproval.source }}</el-descriptions-item>
        <el-descriptions-item label="申请时间">{{ currentApproval.createTime }}</el-descriptions-item>
        <el-descriptions-item label="审批时间">{{ currentApproval.approveTime || '-' }}</el-descriptions-item>
        <el-descriptions-item label="审批意见" span="2" v-if="currentApproval.status !== 0">
          {{ currentApproval.remark || '-' }}
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'

const approvalList = ref([])
const approveVisible = ref(false)
const detailVisible = ref(false)
const submitting = ref(false)
const approveFormRef = ref(null)
const currentApproval = ref(null)

// 审批表单
const approveForm = reactive({
  status: 1,
  remark: ''
})

// 表单验证规则
const approveRules = {
  status: [{ required: true, message: '请选择审批结果', trigger: 'change' }],
  remark: [{ required: true, message: '请输入审批意见', trigger: 'blur' }]
}

// 状态类型
const getStatusType = (status) => {
  const types = {
    0: 'warning',
    1: 'success',
    2: 'danger'
  }
  return types[status] || 'info'
}

// 状态文本
const getStatusText = (status) => {
  const texts = {
    0: '申请中',
    1: '已通过',
    2: '已驳回'
  }
  return texts[status] || '未知'
}

// 加载待审批列表
const loadApprovalList = async () => {
  try {
    // TODO: 调用后端接口
    // const res = await financeApi.bankApplyList()

    // 模拟数据
    approvalList.value = [
      {
        financeId: 1,
        farmerName: '张三',
        phone: '13800138000',
        productName: '农业小额贷款',
        amount: 50000,
        term: 12,
        purpose: '购买种子和化肥',
        source: '农产品销售收入',
        status: 0,
        createTime: '2024-01-15 10:00:00'
      },
      {
        financeId: 2,
        farmerName: '李四',
        phone: '13900139000',
        productName: '农业生产贷款',
        amount: 100000,
        term: 24,
        purpose: '扩大种植规模',
        source: '农产品销售收入',
        status: 1,
        approveTime: '2024-01-14 15:30:00',
        remark: '审批通过'
      }
    ]
  } catch (error) {
    ElMessage.error('加载审批列表失败')
  }
}

// 审批
const handleApprove = (row) => {
  currentApproval.value = row
  approveForm.status = 1
  approveForm.remark = ''
  approveVisible.value = true
}

// 查看详情
const handleView = (row) => {
  currentApproval.value = row
  detailVisible.value = true
}

// 提交审批
const handleSubmitApprove = async () => {
  const valid = await approveFormRef.value?.validate().catch(() => false)
  if (!valid) return

  submitting.value = true
  try {
    // TODO: 调用后端接口
    // await financeApi.approve(currentApproval.value.financeId, approveForm)

    ElMessage.success('审批提交成功')
    approveVisible.value = false
    loadApprovalList()
  } catch (error) {
    ElMessage.error('审批提交失败')
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  loadApprovalList()
})
</script>

<style scoped>
.approval-info {
  margin-bottom: 20px;
}
</style>
