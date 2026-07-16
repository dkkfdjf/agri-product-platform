<template>
  <div class="products-page">
    <div class="page-header">
      <h1 class="page-title">我的商品</h1>
      <el-button type="primary" @click="showAddDialog">
        <el-icon><Plus /></el-icon>
        发布商品
      </el-button>
    </div>

    <!-- 商品列表 -->
    <el-card class="products-card" v-loading="loading">
      <el-table :data="products" style="width: 100%">
        <el-table-column label="商品图片" width="100">
          <template #default="{ row }">
            <img :src="row.picPath" class="product-preview" @error="handleImageError" />
          </template>
        </el-table-column>
        <el-table-column prop="title" label="商品名称" width="200" />
        <el-table-column prop="content" label="描述" show-overflow-tooltip />
        <el-table-column prop="price" label="价格" width="100">
          <template #default="{ row }">
            ¥{{ row.price }}
          </template>
        </el-table-column>
        <el-table-column prop="type" label="类型" width="80">
          <template #default="{ row }">
            <el-tag :type="row.type === 'goods' ? 'success' : 'warning'" size="small">
              {{ row.type === 'goods' ? '货源' : '需求' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="orderStatus" label="交易状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getOrderStatusType(row.orderStatus)" size="small">
              {{ getOrderStatusText(row.orderStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="发布时间" width="170" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="editProduct(row)">编辑</el-button>
            <el-button link type="danger" @click="deleteProduct(row)">删除</el-button>
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
          @size-change="loadProducts"
          @current-change="loadProducts"
        />
      </div>
    </el-card>

    <!-- 添加/编辑商品对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogMode === 'add' ? '发布商品' : '编辑商品'"
      width="600px"
      @close="resetForm"
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="商品名称" prop="title">
          <el-input v-model="form.title" placeholder="请输入商品名称" />
        </el-form-item>
        <el-form-item label="商品描述" prop="content">
          <el-input
            v-model="form.content"
            type="textarea"
            :rows="3"
            placeholder="请输入商品描述"
          />
        </el-form-item>
        <el-form-item label="价格" prop="price">
          <el-input-number v-model="form.price" :min="0" :precision="2" :step="0.01" />
        </el-form-item>
        <el-form-item label="商品类型" prop="type">
          <el-radio-group v-model="form.type">
            <el-radio value="goods">货源（出售）</el-radio>
            <el-radio value="demand">需求（求购）</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="商品图片" prop="picPath">
          <div class="product-image-upload">
            <div class="image-preview">
              <img v-if="form.picPath" :src="form.picPath" alt="预览" />
              <div v-else class="no-image">暂无图片</div>
            </div>
            <div class="image-actions">
              <el-upload
                action="/api/upload"
                :before-upload="beforeImageUpload"
                :on-success="handleImageSuccess"
                :show-file-list="false"
                accept="image/*"
              >
                <el-button type="primary" size="small">上传图片</el-button>
              </el-upload>
              <span class="divider">或</span>
              <el-select
                v-model="form.picPath"
                placeholder="选择本地图片"
                clearable
                filterable
                size="small"
              >
                <el-option
                  v-for="img in localImages"
                  :key="img.path"
                  :label="img.name"
                  :value="img.path"
                >
                  <div class="image-option">
                    <img :src="img.path" :alt="img.name" />
                    <span>{{ img.name }}</span>
                  </div>
                </el-option>
              </el-select>
            </div>
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveProduct" :loading="saving">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { getMyProducts, publishProduct, updateProduct, deleteProduct as deleteProductApi } from '@/api/product'
import { LOCAL_PRODUCT_IMAGES } from '@/config/localImages'

// 本地图片列表
const localImages = LOCAL_PRODUCT_IMAGES

const loading = ref(false)
const saving = ref(false)
const products = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const dialogVisible = ref(false)
const dialogMode = ref('add')
const formRef = ref(null)
const editingProductId = ref(null)

const form = reactive({
  title: '',
  content: '',
  price: 0,
  picPath: '',
  type: 'goods'
})

const rules = {
  title: [{ required: true, message: '请输入商品名称', trigger: 'blur' }],
  content: [{ required: true, message: '请输入商品描述', trigger: 'blur' }],
  price: [{ required: true, message: '请输入价格', trigger: 'blur' }],
  type: [{ required: true, message: '请选择商品类型', trigger: 'change' }]
  // picPath 现在是可选的，可以从下拉列表中选择
}

// 加载商品列表
const loadProducts = async () => {
  loading.value = true
  try {
    const res = await getMyProducts({
      page: currentPage.value,
      pageSize: pageSize.value
    })
    if (res.code === 200) {
      products.value = res.data.records || []
      total.value = res.data.total || 0
    }
  } catch (error) {
    ElMessage.error('加载商品列表失败')
  } finally {
    loading.value = false
  }
}

// 显示添加对话框
const showAddDialog = () => {
  dialogMode.value = 'add'
  dialogVisible.value = true
}

// 编辑商品
const editProduct = (product) => {
  dialogMode.value = 'edit'
  editingProductId.value = product.productId || product.orderId
  Object.assign(form, {
    title: product.title,
    content: product.content,
    price: product.price,
    picPath: product.picPath,
    type: product.type || 'goods'
  })
  dialogVisible.value = true
}

// 保存商品
const saveProduct = async () => {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (!valid) return

    saving.value = true
    try {
      if (dialogMode.value === 'add') {
        await publishProduct(form)
        ElMessage.success('商品发布成功')
      } else {
        await updateProduct(editingProductId.value, form)
        ElMessage.success('商品更新成功')
      }
      dialogVisible.value = false
      await loadProducts()
    } catch (error) {
      ElMessage.error(dialogMode.value === 'add' ? '发布失败' : '更新失败')
    } finally {
      saving.value = false
    }
  })
}

// 切换商品状态
const toggleStatus = async (product) => {
  const newStatus = product.orderStatus === 2 ? 0 : 2
  const statusText = newStatus === 2 ? '完成交易' : '重新开始交易'

  try {
    await updateProduct(product.productId || product.orderId, { orderStatus: newStatus })
    ElMessage.success(`${statusText}成功`)
    await loadProducts()
  } catch (error) {
    ElMessage.error(`${statusText}失败`)
  }
}

// 删除商品
const deleteProduct = async (product) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除商品"${product.title}"吗？`,
      '提示',
      { type: 'warning' }
    )
    await deleteProductApi(product.productId || product.orderId)
    ElMessage.success('删除成功')
    await loadProducts()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

// 获取订单状态文本
const getOrderStatusText = (status) => {
  const statusMap = {
    0: '待交易',
    1: '交易中',
    2: '已完成'
  }
  return statusMap[status] || '未知'
}

// 获取订单状态类型
const getOrderStatusType = (status) => {
  const typeMap = {
    0: 'warning',
    1: 'primary',
    2: 'success'
  }
  return typeMap[status] || 'info'
}

// 重置表单
const resetForm = () => {
  if (formRef.value) {
    formRef.value.resetFields()
  }
  Object.assign(form, {
    title: '',
    content: '',
    price: 0,
    picPath: '',
    type: 'goods'
  })
  editingProductId.value = null
}

// 处理图片加载错误
const handleImageError = (e) => {
  e.target.style.display = 'none'
}

// 图片上传前的验证
const beforeImageUpload = (raw) => {
  const isImg = ['image/jpeg', 'image/png', 'image/jpg', 'image/webp'].includes(raw.type)
  if (!isImg) {
    ElMessage.error('只能上传图片')
    return false
  }
  if (raw.size / 1024 / 1024 > 5) {
    ElMessage.error('图片不能超过 5MB')
    return false
  }
  return true
}

// 图片上传成功回调
const handleImageSuccess = (response) => {
  if (response.code === 200) {
    form.picPath = response.data.url
    ElMessage.success('图片上传成功')
  } else {
    ElMessage.error(response.message || '上传失败')
  }
}

onMounted(() => {
  loadProducts()

  // 检查是否需要编辑特定商品（从订单列表跳转过来）
  const editProductId = sessionStorage.getItem('editProductId')
  if (editProductId) {
    sessionStorage.removeItem('editProductId')
    // 加载产品列表后找到该产品并打开编辑
    setTimeout(() => {
      const productToEdit = products.value.find(p => p.orderId == editProductId || p.productId == editProductId)
      if (productToEdit) {
        editProduct(productToEdit)
      }
    }, 300)
  }
})
</script>

<style scoped>
.products-page {
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

.products-card {
  flex: 1;
}

.product-preview {
  width: 60px;
  height: 60px;
  object-fit: cover;
  border-radius: 4px;
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

/* 图片选择器选项样式 */
.image-option {
  display: flex;
  align-items: center;
  gap: var(--spacing-2, 8px);
}

.image-option img {
  width: 32px;
  height: 32px;
  object-fit: cover;
  border-radius: var(--radius-sm, 4px);
}

/* 商品图片上传区域 */
.product-image-upload {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-3, 12px);
}

.image-preview {
  width: 120px;
  height: 120px;
  border: 2px dashed var(--color-border, #e5e0d8);
  border-radius: var(--radius-base, 8px);
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--color-bg-secondary, #f7f5f0);
}

.image-preview img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.no-image {
  color: var(--color-text-tertiary, #6b7280);
  font-size: var(--font-size-sm, 14px);
}

.image-actions {
  display: flex;
  align-items: center;
  gap: var(--spacing-2, 8px);
  flex-wrap: wrap;
}

.divider {
  color: var(--color-text-tertiary, #6b7280);
  font-size: var(--font-size-sm, 14px);
}
</style>
