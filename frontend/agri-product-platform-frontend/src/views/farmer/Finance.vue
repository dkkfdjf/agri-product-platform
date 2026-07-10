<template>
  <div class="finance-page">
    <div class="page-header">
      <h1 class="page-title">融资申请</h1>
      <el-button type="primary" @click="showApplyDialog">
        <el-icon><Plus /></el-icon>
        申请融资
      </el-button>
    </div>

    <!-- 融资产品推荐 -->
    <el-card class="products-card" v-if="financeProducts.length > 0">
      <template #header>
        <span>推荐融资产品</span>
      </template>
      <div class="products-grid">
        <div v-for="product in financeProducts" :key="product.productId" class="product-item">
          <div class="product-name">{{ product.name }}</div>
          <div class="product-desc">{{ product.description }}</div>
          <div class="product-terms">
            <div class="term">
              <span class="label">年利率：</span>
              <span class="value">{{ product.rate }}%</span>
            </div>
            <div class="term">
              <span class="label">期限：</span>
              <span class="value">{{ product.term }}个月</span>
            </div>
            <div class="term">
              <span class="label">额度：</span>
              <span class="value">¥{{ product.minLimit }}-{{ product.maxLimit }}万</span>
            </div>
          </div>
          <el-button type="primary" size="small" @click="applyForProduct(product)">
            立即申请
          </el-button>
        </div>
      </div>
    </el-card>

    <!-- 我的融资申请 -->
    <el-card class="applications-card" v-loading="loading">
      <template #header>
        <span>我的融资申请</span>
      </template>
      <el-table :data="applications" style="width: 100%">
        <el-table-column prop="productName" label="融资产品" width="150" />
        <el-table-column prop="amount" label="申请金额" width="100">
          <template #default="{ row }">
            ¥{{ row.amount }}
          </template>
        </el-table-column>
        <el-table-column prop="purpose" label="融资用途" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="申请时间" width="150">
          <template #default="{ row }">
            {{ formatTime(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button
              v-if="row.status === 'pending'"
              link
              type="primary"
              @click="editApplication(row)"
            >
              修改
            </el-button>
            <el-button
              v-if="row.status === 'pending'"
              link
              type="danger"
              @click="cancelApplication(row)"
            >
              撤销
            </el-button>
            <el-button v-else link type="info" @click="viewDetails(row)">
              详情
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :total="total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="loadApplications"
          @current-change="loadApplications"
        />
      </div>

      <el-empty v-if="!loading && applications.length === 0" description="暂无融资申请" />
    </el-card>

    <!-- 申请融资对话框 -->
    <el-dialog
      v-model="dialogVisible"
      title="申请融资"
      width="600px"
      @close="resetForm"
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-width="120px">
        <el-form-item label="融资产品" prop="productId">
          <el-select v-model="form.productId" placeholder="请选择融资产品" style="width: 100%">
            <el-option
              v-for="product in financeProducts"
              :key="product.productId"
              :label="product.name"
              :value="product.productId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="申请金额" prop="amount">
          <el-input-number
            v-model="form.amount"
            :min="1"
            :max="500"
            :step="1"
            :precision="2"
            style="width: 100%"
          />
          <span class="unit-text">万元</span>
        </el-form-item>
        <el-form-item label="融资用途" prop="purpose">
          <el-input
            v-model="form.purpose"
            type="textarea"
            :rows="4"
            placeholder="请详细说明融资用途，如：购买农机具、建设大棚、采购农资等"
          />
        </el-form-item>
        <el-form-item label="还款来源" prop="repaymentSource">
          <el-input
            v-model="form.repaymentSource"
            type="textarea"
            :rows="3"
            placeholder="请说明还款来源，如：农产品销售收入、其他收入等"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitApplication" :loading="submitting">
          提交申请
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { getFinanceProducts, applyFinance, getMyFinanceApplications } from '@/api/finance'

const loading = ref(false)
const submitting = ref(false)
const financeProducts = ref([])
const applications = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const dialogVisible = ref(false)
const formRef = ref(null)
const editingApplicationId = ref(null)

const form = reactive({
  productId: null,
  amount: 10,
  purpose: '',
  repaymentSource: ''
})

const rules = {
  productId: [{ required: true, message: '请选择融资产品', trigger: 'change' }],
  amount: [{ required: true, message: '请输入申请金额', trigger: 'blur' }],
  purpose: [{ required: true, message: '请输入融资用途', trigger: 'blur' }],
  repaymentSource: [{ required: true, message: '请输入还款来源', trigger: 'blur' }]
}

// 加载融资产品
const loadFinanceProducts = async () => {
  try {
    const res = await getFinanceProducts({ page: 1, pageSize: 10 })
    if (res.code === 200) {
      financeProducts.value = res.data.records || []
    }
  } catch (error) {
    ElMessage.error('加载融资产品失败')
  }
}

// 加载我的融资申请
const loadApplications = async () => {
  loading.value = true
  try {
    const res = await getMyFinanceApplications({
      page: currentPage.value,
      pageSize: pageSize.value
    })
    if (res.code === 200) {
      applications.value = res.data.records || []
      total.value = res.data.total || 0
    }
  } catch (error) {
    ElMessage.error('加载融资申请失败')
  } finally {
    loading.value = false
  }
}

// 显示申请对话框
const showApplyDialog = () => {
  dialogVisible.value = true
}

// 针对产品申请
const applyForProduct = (product) => {
  form.productId = product.productId
  dialogVisible.value = true
}

// 编辑申请
const editApplication = (application) => {
  editingApplicationId.value = application.financeId || application.id
  Object.assign(form, {
    productId: application.productId,
    amount: application.amount,
    purpose: application.purpose,
    repaymentSource: application.repaymentSource
  })
  dialogVisible.value = true
}

// 提交申请
const submitApplication = async () => {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (!valid) return

    submitting.value = true
    try {
      const data = {
        productId: form.productId,
        amount: form.amount * 10000, // 转换为元
        purpose: form.purpose,
        repaymentSource: form.repaymentSource
      }

      if (editingApplicationId.value) {
        // TODO: 更新申请API
        ElMessage.success('申请修改成功')
      } else {
        await applyFinance(data)
        ElMessage.success('融资申请已提交，请等待银行审核')
      }

      dialogVisible.value = false
      await loadApplications()
    } catch (error) {
      ElMessage.error('提交申请失败')
    } finally {
      submitting.value = false
    }
  })
}

// 撤销申请
const cancelApplication = async (application) => {
  try {
    await ElMessageBox.confirm('确定要撤销此融资申请吗？', '提示', {
      type: 'warning'
    })
    // TODO: 撤销申请API
    ElMessage.success('申请已撤销')
    await loadApplications()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('撤销申请失败')
    }
  }
}

// 查看详情
const viewDetails = (application) => {
  ElMessage.info('查看详情功能待实现')
}

// 格式化时间
const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  return date.toLocaleDateString('zh-CN')
}

// 获取状态类型
const getStatusType = (status) => {
  const typeMap = {
    'pending': 'warning',
    'approved': 'success',
    'rejected': 'danger'
  }
  return typeMap[status] || 'info'
}

// 获取状态文本
const getStatusText = (status) => {
  const textMap = {
    'pending': '待审核',
    'approved': '已批准',
    'rejected': '已拒绝'
  }
  return textMap[status] || status
}

// 重置表单
const resetForm = () => {
  if (formRef.value) {
    formRef.value.resetFields()
  }
  Object.assign(form, {
    productId: null,
    amount: 10,
    purpose: '',
    repaymentSource: ''
  })
  editingApplicationId.value = null
}

onMounted(() => {
  loadFinanceProducts()
  loadApplications()
})
</script>

<style scoped>
.finance-page {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.page-title {
  font-size: 24px;
  font-weight: 600;
  margin: 0;
}

.products-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
}

.product-item {
  padding: 20px;
  border: 1px solid #eee;
  border-radius: 8px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.product-name {
  font-weight: 600;
  font-size: 16px;
}

.product-desc {
  font-size: 14px;
  color: #666;
  flex: 1;
}

.product-terms {
  display: flex;
  flex-direction: column;
  gap: 6px;
  font-size: 14px;
}

.term .label {
  color: #666;
}

.term .value {
  font-weight: 500;
}

.unit-text {
  margin-left: 8px;
  color: #666;
  font-size: 14px;
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}
</style>
