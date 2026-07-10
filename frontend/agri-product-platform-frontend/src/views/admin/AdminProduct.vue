<template>
  <div class="admin-product">
    <div class="header">
      <h2>商品管理</h2>
      <el-input
        v-model="searchKeyword"
        placeholder="搜索商品"
        style="width: 250px"
        clearable
        @clear="loadProducts"
      >
        <template #prefix>
          <el-icon><Search /></el-icon>
        </template>
      </el-input>
    </div>

    <el-table :data="products" style="width: 100%" v-loading="loading">
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

    <el-dialog v-model="dialogVisible" title="编辑商品" width="600px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="商品名称">
          <el-input v-model="form.name" placeholder="输入商品名称" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="输入商品描述" />
        </el-form-item>
        <el-form-item label="价格">
          <el-input-number v-model="form.price" :min="0" :precision="2" />
        </el-form-item>
        <el-form-item label="库存">
          <el-input-number v-model="form.stock" :min="0" />
        </el-form-item>
        <el-form-item label="图片URL">
          <el-input v-model="form.picPath" placeholder="输入图片URL" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio :value="1">上架</el-radio>
            <el-radio :value="0">下架</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="save" :loading="saving">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import { getAdminProducts, updateAdminProduct, deleteProduct as deleteProductApi } from '@/api/admin'

const loading = ref(false)
const saving = ref(false)
const products = ref([])
const searchKeyword = ref('')
const dialogVisible = ref(false)
const editingProduct = ref(null)

const form = reactive({
  name: '',
  description: '',
  price: 0,
  stock: 0,
  picPath: '',
  status: 1
})

onMounted(() => {
  loadProducts()
})

const loadProducts = async () => {
  loading.value = true
  try {
    const res = await getAdminProducts({ keyword: searchKeyword.value })
    products.value = res.data || []
  } catch (error) {
    ElMessage.error('加载商品列表失败')
  } finally {
    loading.value = false
  }
}

const editProduct = (product) => {
  editingProduct.value = product
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

const save = async () => {
  if (!form.name) {
    ElMessage.warning('请输入商品名称')
    return
  }

  saving.value = true
  try {
    await updateAdminProduct(editingProduct.value.orderId || editingProduct.value.productId, form)
    ElMessage.success('更新成功')
    dialogVisible.value = false
    await loadProducts()
  } catch (error) {
    ElMessage.error('保存失败')
  } finally {
    saving.value = false
  }
}

const toggleStatus = async (product) => {
  try {
    const newStatus = product.status === 1 ? 0 : 1
    await updateAdminProduct(product.orderId || product.productId, { status: newStatus })
    ElMessage.success(newStatus === 1 ? '上架成功' : '下架成功')
    await loadProducts()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const deleteProduct = async (product) => {
  try {
    await ElMessageBox.confirm('确定删除此商品吗？', '提示', { type: 'warning' })
    await deleteProductApi(product.orderId || product.productId)
    ElMessage.success('删除成功')
    await loadProducts()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const handleImageError = (e) => {
  e.target.src = 'https://via.placeholder.com/80x80?text=No+Image'
}
</script>

<style scoped>
.admin-product {
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

.product-preview {
  width: 60px;
  height: 60px;
  object-fit: cover;
  border-radius: 4px;
}
</style>
