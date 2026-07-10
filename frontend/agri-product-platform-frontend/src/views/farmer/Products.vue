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
        <el-table-column prop="name" label="商品名称" width="200" />
        <el-table-column prop="description" label="描述" show-overflow-tooltip />
        <el-table-column prop="price" label="价格" width="100">
          <template #default="{ row }">
            ¥{{ row.price }}
          </template>
        </el-table-column>
        <el-table-column prop="stock" label="库存" width="80" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">
              {{ row.status === 1 ? '已上架' : '已下架' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="editProduct(row)">编辑</el-button>
            <el-button
              link
              :type="row.status === 1 ? 'warning' : 'success'"
              @click="toggleStatus(row)"
            >
              {{ row.status === 1 ? '下架' : '上架' }}
            </el-button>
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
        <el-form-item label="商品名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入商品名称" />
        </el-form-item>
        <el-form-item label="商品描述" prop="description">
          <el-input
            v-model="form.description"
            type="textarea"
            :rows="3"
            placeholder="请输入商品描述"
          />
        </el-form-item>
        <el-form-item label="价格" prop="price">
          <el-input-number v-model="form.price" :min="0" :precision="2" :step="0.01" />
        </el-form-item>
        <el-form-item label="库存" prop="stock">
          <el-input-number v-model="form.stock" :min="0" :step="1" />
        </el-form-item>
        <el-form-item label="图片URL" prop="picPath">
          <el-input v-model="form.picPath" placeholder="请输入图片URL" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :value="1">上架</el-radio>
            <el-radio :value="0">下架</el-radio>
          </el-radio-group>
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
import { getProducts, publishProduct, updateProduct, deleteProduct as deleteProductApi } from '@/api/product'

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
  name: '',
  description: '',
  price: 0,
  stock: 0,
  picPath: '',
  status: 1
})

const rules = {
  name: [{ required: true, message: '请输入商品名称', trigger: 'blur' }],
  description: [{ required: true, message: '请输入商品描述', trigger: 'blur' }],
  price: [{ required: true, message: '请输入价格', trigger: 'blur' }],
  stock: [{ required: true, message: '请输入库存', trigger: 'blur' }],
  picPath: [{ required: true, message: '请输入图片URL', trigger: 'blur' }]
}

// 加载商品列表
const loadProducts = async () => {
  loading.value = true
  try {
    const res = await getProducts({
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
  editingProductId.value = product.productId || product.id
  Object.assign(form, {
    name: product.name,
    description: product.description,
    price: product.price,
    stock: product.stock,
    picPath: product.picPath,
    status: product.status
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
  const newStatus = product.status === 1 ? 0 : 1
  const statusText = newStatus === 1 ? '上架' : '下架'

  try {
    await updateProduct(product.productId || product.id, { status: newStatus })
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
      `确定要删除商品"${product.name}"吗？`,
      '提示',
      { type: 'warning' }
    )
    await deleteProductApi(product.productId || product.id)
    ElMessage.success('删除成功')
    await loadProducts()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

// 重置表单
const resetForm = () => {
  if (formRef.value) {
    formRef.value.resetFields()
  }
  Object.assign(form, {
    name: '',
    description: '',
    price: 0,
    stock: 0,
    picPath: '',
    status: 1
  })
  editingProductId.value = null
}

// 处理图片加载错误
const handleImageError = (e) => {
  e.target.src = 'https://via.placeholder.com/60x60?text=No+Image'
}

onMounted(() => {
  loadProducts()
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
</style>
