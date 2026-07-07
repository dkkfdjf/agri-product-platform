<template>
  <div class="bank-products">
    <div class="page-header">
      <h2>产品管理</h2>
      <el-button type="primary" @click="openProductDialog">
        <el-icon><Plus /></el-icon>
        添加产品
      </el-button>
    </div>

    <el-card>
      <el-table :data="productList" stripe>
        <el-table-column prop="productId" label="产品编号" width="100" />
        <el-table-column prop="productName" label="产品名称" />
        <el-table-column prop="bankName" label="银行" width="150" />
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
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">
              {{ row.status === 1 ? '上架' : '下架' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" link @click="handleEdit(row)">
              编辑
            </el-button>
            <el-button
              :type="row.status === 1 ? 'warning' : 'success'"
              size="small"
              link
              @click="handleToggleStatus(row)"
            >
              {{ row.status === 1 ? '下架' : '上架' }}
            </el-button>
            <el-button type="danger" size="small" link @click="handleDelete(row)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 产品对话框 -->
    <el-dialog
      v-model="productDialogVisible"
      :title="isEdit ? '编辑产品' : '添加产品'"
      width="600px"
    >
      <el-form :model="productForm" :rules="productRules" ref="productFormRef" label-width="120px">
        <el-form-item label="产品名称" prop="productName">
          <el-input v-model="productForm.productName" placeholder="请输入产品名称" />
        </el-form-item>
        <el-form-item label="银行名称" prop="bankName">
          <el-input v-model="productForm.bankName" placeholder="请输入银行名称" />
        </el-form-item>
        <el-form-item label="最高额度" prop="maxAmount">
          <el-input-number v-model="productForm.maxAmount" :min="1000" :step="1000" />
          <span style="margin-left: 10px">元</span>
        </el-form-item>
        <el-form-item label="年利率" prop="rate">
          <el-input-number v-model="productForm.rate" :min="0" :max="20" :precision="2" :step="0.1" />
          <span style="margin-left: 10px">%</span>
        </el-form-item>
        <el-form-item label="最长期限" prop="maxTerm">
          <el-input-number v-model="productForm.maxTerm" :min="1" :max="60" />
          <span style="margin-left: 10px">个月</span>
        </el-form-item>
        <el-form-item label="产品描述" prop="description">
          <el-input v-model="productForm.description" type="textarea" :rows="4" placeholder="请输入产品描述" />
        </el-form-item>
        <el-form-item label="申请条件" prop="conditions">
          <el-input v-model="productForm.conditions" type="textarea" :rows="3" placeholder="请输入申请条件" />
        </el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="productForm.status" :active-value="1" :inactive-value="0" active-text="上架" inactive-text="下架" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="productDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitProduct" :loading="submitting">
          保存
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const productList = ref([])
const productDialogVisible = ref(false)
const isEdit = ref(false)
const submitting = ref(false)
const productFormRef = ref(null)

// 产品表单
const productForm = reactive({
  productId: null,
  productName: '',
  bankName: '',
  maxAmount: 50000,
  rate: 4.35,
  maxTerm: 12,
  description: '',
  conditions: '',
  status: 1
})

// 表单验证规则
const productRules = {
  productName: [{ required: true, message: '请输入产品名称', trigger: 'blur' }],
  bankName: [{ required: true, message: '请输入银行名称', trigger: 'blur' }],
  maxAmount: [{ required: true, message: '请输入最高额度', trigger: 'blur' }],
  rate: [{ required: true, message: '请输入年利率', trigger: 'blur' }],
  maxTerm: [{ required: true, message: '请输入最长期限', trigger: 'blur' }],
  description: [{ required: true, message: '请输入产品描述', trigger: 'blur' }],
  conditions: [{ required: true, message: '请输入申请条件', trigger: 'blur' }]
}

// 加载产品列表
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
        maxTerm: 12,
        description: '面向农户的小额信用贷款',
        conditions: '具有农村户籍，从事农业生产',
        status: 1
      },
      {
        productId: 2,
        productName: '农业生产贷款',
        bankName: '中国农业银行',
        maxAmount: 200000,
        rate: 4.75,
        maxTerm: 36,
        description: '支持农业生产经营的中期贷款',
        conditions: '从事农业生产满1年',
        status: 1
      },
      {
        productId: 3,
        productName: '农机购置贷款',
        bankName: '中国工商银行',
        maxAmount: 300000,
        rate: 4.65,
        maxTerm: 24,
        description: '专门用于购买农机设备的贷款',
        conditions: '购买指定农机设备',
        status: 1
      }
    ]
  } catch (error) {
    ElMessage.error('加载产品列表失败')
  }
}

// 打开产品对话框
const openProductDialog = () => {
  isEdit.value = false
  resetProductForm()
  productDialogVisible.value = true
}

// 编辑产品
const handleEdit = (row) => {
  isEdit.value = true
  Object.assign(productForm, row)
  productDialogVisible.value = true
}

// 切换状态
const handleToggleStatus = (row) => {
  const action = row.status === 1 ? '下架' : '上架'
  ElMessageBox.confirm(`确定要${action}该产品吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      // TODO: 调用后端接口
      // await financeApi.updateProduct(row.productId, { status: row.status === 1 ? 0 : 1 })

      ElMessage.success(`${action}成功`)
      loadProductList()
    } catch (error) {
      ElMessage.error(`${action}失败`)
    }
  })
}

// 删除产品
const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该产品吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      // TODO: 调用后端接口
      // await financeApi.deleteProduct(row.productId)

      ElMessage.success('删除成功')
      loadProductList()
    } catch (error) {
      ElMessage.error('删除失败')
    }
  })
}

// 提交产品
const handleSubmitProduct = async () => {
  const valid = await productFormRef.value?.validate().catch(() => false)
  if (!valid) return

  submitting.value = true
  try {
    // TODO: 调用后端接口
    // if (isEdit.value) {
    //   await financeApi.updateProduct(productForm.productId, productForm)
    // } else {
    //   await financeApi.addProduct(productForm)
    // }

    ElMessage.success(isEdit.value ? '修改成功' : '添加成功')
    productDialogVisible.value = false
    loadProductList()
  } catch (error) {
    ElMessage.error('操作失败')
  } finally {
    submitting.value = false
  }
}

// 重置产品表单
const resetProductForm = () => {
  Object.assign(productForm, {
    productId: null,
    productName: '',
    bankName: '',
    maxAmount: 50000,
    rate: 4.35,
    maxTerm: 12,
    description: '',
    conditions: '',
    status: 1
  })
  productFormRef.value?.resetFields()
}

onMounted(() => {
  loadProductList()
})
</script>

<style scoped>
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}
</style>
