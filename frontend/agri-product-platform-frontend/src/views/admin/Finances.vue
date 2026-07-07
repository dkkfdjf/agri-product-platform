<template>
  <div class="admin-finances">
    <h2>融资管理</h2>

    <el-card>
      <el-table :data="financeList" stripe>
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
            <el-button type="primary" size="small" link @click="handleView(row)">
              查看
            </el-button>
            <el-button type="warning" size="small" link @click="handleEdit(row)">
              修改状态
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="loadFinanceList"
          @current-change="loadFinanceList"
        />
      </div>
    </el-card>

    <!-- 详情对话框 -->
    <el-dialog v-model="detailVisible" title="融资详情" width="600px">
      <el-descriptions :column="2" border v-if="currentFinance">
        <el-descriptions-item label="申请编号">{{ currentFinance.financeId }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusType(currentFinance.status)">
            {{ getStatusText(currentFinance.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="申请人">{{ currentFinance.farmerName }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ currentFinance.phone }}</el-descriptions-item>
        <el-descriptions-item label="融资产品">{{ currentFinance.productName }}</el-descriptions-item>
        <el-descriptions-item label="申请金额">¥{{ currentFinance.amount }}</el-descriptions-item>
        <el-descriptions-item label="贷款期限">{{ currentFinance.term }}个月</el-descriptions-item>
        <el-descriptions-item label="申请时间">{{ currentFinance.createTime }}</el-descriptions-item>
        <el-descriptions-item label="借款用途" span="2">{{ currentFinance.purpose }}</el-descriptions-item>
        <el-descriptions-item label="还款来源" span="2">{{ currentFinance.source }}</el-descriptions-item>
        <el-descriptions-item label="审批意见" span="2" v-if="currentFinance.status !== 0">
          {{ currentFinance.remark || '-' }}
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>

    <!-- 修改状态对话框 -->
    <el-dialog v-model="editVisible" title="修改融资状态" width="500px">
      <el-form :model="editForm" ref="editFormRef" label-width="100px">
        <el-form-item label="当前状态">
          <el-tag :type="getStatusType(currentFinance?.status)">
            {{ getStatusText(currentFinance?.status) }}
          </el-tag>
        </el-form-item>
        <el-form-item label="修改为">
          <el-radio-group v-model="editForm.status">
            <el-radio :value="1">已通过</el-radio>
            <el-radio :value="2">已驳回</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="审批意见">
          <el-input v-model="editForm.remark" type="textarea" :rows="3" placeholder="请输入审批意见" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitEdit" :loading="submitting">
          保存
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'

const financeList = ref([])
const detailVisible = ref(false)
const editVisible = ref(false)
const submitting = ref(false)
const editFormRef = ref(null)
const currentFinance = ref(null)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 编辑表单
const editForm = reactive({
  status: 1,
  remark: ''
})

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

// 加载融资列表
const loadFinanceList = async () => {
  try {
    // TODO: 调用后端接口
    // const res = await adminApi.getFinanceList({
    //   page: currentPage.value,
    //   size: pageSize.value
    // })

    // 模拟数据
    financeList.value = [
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
        createTime: '2024-01-10 14:30:00',
        remark: '审批通过'
      }
    ]
    total.value = 2
  } catch (error) {
    ElMessage.error('加载融资列表失败')
  }
}

// 查看详情
const handleView = (row) => {
  currentFinance.value = row
  detailVisible.value = true
}

// 修改状态
const handleEdit = (row) => {
  currentFinance.value = row
  editForm.status = row.status === 0 ? 1 : row.status
  editForm.remark = row.remark || ''
  editVisible.value = true
}

// 提交编辑
const handleSubmitEdit = async () => {
  submitting.value = true
  try {
    // TODO: 调用后端接口
    // await adminApi.updateFinance(currentFinance.value.financeId, editForm)

    ElMessage.success('修改成功')
    editVisible.value = false
    loadFinanceList()
  } catch (error) {
    ElMessage.error('修改失败')
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  loadFinanceList()
})
</script>

<style scoped>
.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
