<template>
  <div class="farmer-finance">
    <h2>我的融资</h2>

    <!-- 操作按钮 -->
    <div class="action-buttons">
      <el-button type="primary" @click="openApplyDialog">
        <el-icon><Plus /></el-icon>
        申请融资
      </el-button>
      <el-button @click="showProducts = true">
        <el-icon><List /></el-icon>
        查看融资产品
      </el-button>
    </div>

    <!-- 融资申请列表 -->
    <el-card>
      <el-table :data="financeList" stripe>
        <el-table-column prop="financeId" label="申请编号" width="100" />
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
            <el-button type="primary" size="small" link @click="handleDetail(row)">
              详情
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 融资产品对话框 -->
    <el-dialog v-model="showProducts" title="融资产品" width="800px">
      <el-table :data="productList" stripe>
        <el-table-column prop="productName" label="产品名称" />
        <el-table-column prop="bankName" label="银行" />
        <el-table-column prop="maxAmount" label="最高额度" width="120">
          <template #default="{ row }">
            ¥{{ row.maxAmount }}
          </template>
        </el-table-column>
        <el-table-column prop="rate" label="年利率" width="100">
          <template #default="{ row }">
            {{ row.rate }}%
          </template>
        </el-table-column>
        <el-table-column prop="maxTerm" label="最长期限" width="100">
          <template #default="{ row }">
            {{ row.maxTerm }}个月
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100">
          <template #default="{ row }">
            <el-button type="primary" size="small" link @click="selectProduct(row)">
              申请
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>

    <!-- 申请融资对话框 -->
    <el-dialog v-model="applyVisible" title="申请融资" width="600px">
      <el-form :model="applyForm" :rules="applyRules" ref="applyFormRef" label-width="100px">
        <el-form-item label="融资产品" prop="productId">
          <el-select v-model="applyForm.productId" placeholder="请选择融资产品" style="width: 100%" @change="onProductChange">
            <el-option
              v-for="item in productList"
              :key="item.productId"
              :label="item.productName"
              :value="item.productId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="申请金额" prop="amount">
          <el-input-number v-model="applyForm.amount" :min="1000" :max="maxAmount" :step="1000" />
          <span style="margin-left: 10px; color: #999">元（最高{{ maxAmount }}元）</span>
        </el-form-item>
        <el-form-item label="贷款期限" prop="term">
          <el-select v-model="applyForm.term" placeholder="请选择期限">
            <el-option
              v-for="n in 12"
              :key="n"
              :label="n + '个月'"
              :value="n"
              :disabled="n > maxTerm"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="借款用途" prop="purpose">
          <el-input v-model="applyForm.purpose" type="textarea" :rows="3" placeholder="请说明借款用途" />
        </el-form-item>
        <el-form-item label="还款来源" prop="source">
          <el-input v-model="applyForm.source" type="textarea" :rows="2" placeholder="请说明还款来源" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="applyVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitApply" :loading="submitting">
          提交申请
        </el-button>
      </template>
    </el-dialog>

    <!-- 详情对话框 -->
    <el-dialog v-model="detailVisible" title="融资详情" width="600px">
      <el-descriptions :column="2" border v-if="currentFinance">
        <el-descriptions-item label="申请编号">{{ currentFinance.financeId }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusType(currentFinance.status)">
            {{ getStatusText(currentFinance.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="融资产品">{{ currentFinance.productName }}</el-descriptions-item>
        <el-descriptions-item label="申请金额">¥{{ currentFinance.amount }}</el-descriptions-item>
        <el-descriptions-item label="贷款期限">{{ currentFinance.term }}个月</el-descriptions-item>
        <el-descriptions-item label="申请时间">{{ currentFinance.createTime }}</el-descriptions-item>
        <el-descriptions-item label="借款用途" span="2">{{ currentFinance.purpose || '-' }}</el-descriptions-item>
        <el-descriptions-item label="还款来源" span="2">{{ currentFinance.source || '-' }}</el-descriptions-item>
        <el-descriptions-item label="审批意见" span="2" v-if="currentFinance.status !== 0">
          {{ currentFinance.remark || '-' }}
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'

const financeList = ref([])
const productList = ref([])
const showProducts = ref(false)
const applyVisible = ref(false)
const detailVisible = ref(false)
const submitting = ref(false)
const applyFormRef = ref(null)
const currentFinance = ref(null)

// 申请表单
const applyForm = reactive({
  productId: null,
  amount: 10000,
  term: 12,
  purpose: '',
  source: ''
})

// 表单验证规则
const applyRules = {
  productId: [{ required: true, message: '请选择融资产品', trigger: 'change' }],
  amount: [{ required: true, message: '请输入申请金额', trigger: 'blur' }],
  term: [{ required: true, message: '请选择贷款期限', trigger: 'change' }],
  purpose: [{ required: true, message: '请说明借款用途', trigger: 'blur' }],
  source: [{ required: true, message: '请说明还款来源', trigger: 'blur' }]
}

// 当前选择的产品信息
const selectedProduct = computed(() => {
  return productList.value.find(p => p.productId === applyForm.productId)
})

const maxAmount = computed(() => {
  return selectedProduct.value?.maxAmount || 500000
})

const maxTerm = computed(() => {
  return selectedProduct.value?.maxTerm || 36
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

// 加载融资申请列表
const loadFinanceList = async () => {
  try {
    // TODO: 调用后端接口
    // const res = await financeApi.myApplyList()

    // 模拟数据
    financeList.value = [
      {
        financeId: 1,
        productName: '农业小额贷款',
        amount: 50000,
        term: 12,
        status: 1,
        purpose: '购买种子和化肥',
        source: '农产品销售收入',
        createTime: '2024-01-10 10:00:00',
        remark: '审批通过'
      },
      {
        financeId: 2,
        productName: '农业生产贷款',
        amount: 100000,
        term: 24,
        status: 0,
        purpose: '扩大种植规模',
        source: '农产品销售收入',
        createTime: '2024-01-12 14:30:00'
      }
    ]
  } catch (error) {
    ElMessage.error('加载融资列表失败')
  }
}

// 加载融资产品列表
const loadProductList = async () => {
  try {
    // TODO: 调用后端接口
    // const res = await financeApi.productList()

    // 模拟数据
    productList.value = [
      {
        productId: 1,
        productName: '农业小额贷款',
        bankName: '中国农业银行',
        maxAmount: 50000,
        rate: 4.35,
        maxTerm: 12
      },
      {
        productId: 2,
        productName: '农业生产贷款',
        bankName: '中国农业银行',
        maxAmount: 200000,
        rate: 4.75,
        maxTerm: 36
      },
      {
        productId: 3,
        productName: '农机购置贷款',
        bankName: '中国工商银行',
        maxAmount: 300000,
        rate: 4.65,
        maxTerm: 24
      }
    ]
  } catch (error) {
    ElMessage.error('加载产品列表失败')
  }
}

// 打开申请对话框
const openApplyDialog = () => {
  loadProductList()
  applyVisible.value = true
}

// 选择产品
const selectProduct = (product) => {
  applyForm.productId = product.productId
  showProducts.value = false
  applyVisible.value = true
}

// 产品变化
const onProductChange = () => {
  const product = selectedProduct.value
  if (product) {
    applyForm.amount = Math.min(10000, product.maxAmount)
    applyForm.term = Math.min(12, product.maxTerm)
  }
}

// 提交申请
const handleSubmitApply = async () => {
  const valid = await applyFormRef.value?.validate().catch(() => false)
  if (!valid) return

  submitting.value = true
  try {
    // TODO: 调用后端接口
    // await financeApi.apply(applyForm)

    ElMessage.success('申请提交成功')
    applyVisible.value = false
    loadFinanceList()
  } catch (error) {
    ElMessage.error('申请提交失败')
  } finally {
    submitting.value = false
  }
}

// 查看详情
const handleDetail = (row) => {
  currentFinance.value = row
  detailVisible.value = true
}

onMounted(() => {
  loadFinanceList()
  loadProductList()
})
</script>

<style scoped>
.action-buttons {
  margin-bottom: 20px;
}
</style>
