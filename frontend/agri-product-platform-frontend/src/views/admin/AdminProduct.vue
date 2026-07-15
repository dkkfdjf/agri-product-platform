<template>
  <div class="admin-product">
    <div class="header">
      <h2>商品管理</h2>
      <el-input
        v-model="searchKeyword"
        placeholder="按标题搜索商品"
        style="width: 250px"
        clearable
        @clear="loadProducts"
        @keyup.enter="loadProducts"
      >
        <template #prefix>
          <el-icon><Search /></el-icon>
        </template>
        <template #append>
          <el-button @click="loadProducts">搜索</el-button>
        </template>
      </el-input>
    </div>

    <el-table :data="products" style="width: 100%" v-loading="loading" border>
      <el-table-column label="图片" width="90">
        <template #default="{ row }">
          <img v-if="row.picPath" :src="row.picPath" class="product-preview" @error="handleImageError" />
          <span v-else class="no-img">无图</span>
        </template>
      </el-table-column>
      <el-table-column prop="title" label="标题" min-width="180" show-overflow-tooltip />
      <el-table-column label="类型" width="90">
        <template #default="{ row }">
          <el-tag :type="row.type === 'demand' ? 'warning' : 'success'" size="small">
            {{ typeText(row.type) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="价格" width="110">
        <template #default="{ row }">¥{{ row.price }}</template>
      </el-table-column>
      <el-table-column label="交易状态" width="100">
        <template #default="{ row }">
          <el-tag :type="orderStatusType(row.orderStatus)" size="small">
            {{ orderStatusText(row.orderStatus) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="ownName" label="发布人" width="120" />
      <el-table-column prop="createTime" label="发布时间" width="170" />
      <el-table-column label="操作" width="150" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" @click="editProduct(row)">编辑</el-button>
          <el-button link type="danger" @click="deleteProduct(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" title="编辑商品" width="600px">
      <el-form :model="form" label-width="90px">
        <el-form-item label="标题">
          <el-input v-model="form.title" placeholder="输入商品标题" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.content" type="textarea" :rows="3" placeholder="输入商品描述" />
        </el-form-item>
        <el-form-item label="价格">
          <el-input-number v-model="form.price" :min="0" :precision="2" />
        </el-form-item>
        <el-form-item label="类型">
          <el-select v-model="form.type" style="width: 100%">
            <el-option label="货源（出售）" value="goods" />
            <el-option label="需求（求购）" value="demand" />
          </el-select>
        </el-form-item>
        <el-form-item label="交易状态">
          <el-select v-model="form.orderStatus" style="width: 100%">
            <el-option label="待交易" :value="0" />
            <el-option label="交易中" :value="1" />
            <el-option label="已完成" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="图片地址">
          <el-input v-model="form.picPath" placeholder="输入图片URL，如 /images/青苹果.jpg" />
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
const editingId = ref(null)

const form = reactive({
  title: '',
  content: '',
  price: 0,
  picPath: '',
  type: 'goods',
  orderStatus: 0
})

const typeText = (t) => ({ goods: '货源', demand: '需求' }[t] || t || '—')
const orderStatusText = (s) => ({ 0: '待交易', 1: '交易中', 2: '已完成' }[s] ?? '—')
const orderStatusType = (s) => ({ 0: 'warning', 1: 'primary', 2: 'success' }[s] || 'info')

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
  editingId.value = product.orderId
  Object.assign(form, {
    title: product.title,
    content: product.content,
    price: product.price,
    picPath: product.picPath,
    type: product.type || 'goods',
    orderStatus: product.orderStatus ?? 0
  })
  dialogVisible.value = true
}

const save = async () => {
  if (!form.title) {
    ElMessage.warning('请输入标题')
    return
  }
  saving.value = true
  try {
    await updateAdminProduct(editingId.value, { ...form })
    ElMessage.success('更新成功')
    dialogVisible.value = false
    await loadProducts()
  } catch (error) {
    ElMessage.error(error.message || '保存失败')
  } finally {
    saving.value = false
  }
}

const deleteProduct = async (product) => {
  try {
    await ElMessageBox.confirm(`确定删除商品「${product.title}」吗？`, '提示', { type: 'warning' })
    await deleteProductApi(product.orderId)
    ElMessage.success('删除成功')
    await loadProducts()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '删除失败')
    }
  }
}

const handleImageError = (e) => {
  e.target.style.display = 'none'
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

.no-img {
  display: inline-block;
  width: 60px;
  height: 60px;
  line-height: 60px;
  text-align: center;
  font-size: 12px;
  color: #999;
  background: #f5f5f5;
  border-radius: 4px;
}
</style>
