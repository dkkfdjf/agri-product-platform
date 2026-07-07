<template>
  <div class="farmer-products">
    <div class="page-header">
      <h2>我的发布</h2>
      <el-button type="primary" @click="openPublishDialog">
        <el-icon><Plus /></el-icon>
        发布商品
      </el-button>
    </div>

    <!-- 商品列表 -->
    <el-card>
      <el-table :data="productList" stripe>
        <el-table-column prop="orderId" label="商品ID" width="100" />
        <el-table-column prop="productName" label="商品名称" />
        <el-table-column prop="category" label="类别" width="120" />
        <el-table-column prop="price" label="价格" width="120">
          <template #default="{ row }">
            ¥{{ row.price }}/{{ row.unit }}
          </template>
        </el-table-column>
        <el-table-column prop="stock" label="库存" width="100" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">
              {{ row.status === 1 ? '在售' : '已下架' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="发布时间" width="180" />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" link @click="handleEdit(row)">
              编辑
            </el-button>
            <el-button type="danger" size="small" link @click="handleDelete(row)">
              {{ row.status === 1 ? '下架' : '删除' }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :total="total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="loadProducts"
          @current-change="loadProducts"
        />
      </div>
    </el-card>

    <!-- 发布/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑商品' : '发布商品'"
      width="600px"
    >
      <el-form :model="productForm" :rules="formRules" ref="formRef" label-width="100px">
        <el-form-item label="商品名称" prop="productName">
          <el-input v-model="productForm.productName" placeholder="请输入商品名称" />
        </el-form-item>
        <el-form-item label="商品类别" prop="category">
          <el-select v-model="productForm.category" placeholder="请选择类别" style="width: 100%">
            <el-option label="粮食" value="粮食" />
            <el-option label="蔬菜" value="蔬菜" />
            <el-option label="水果" value="水果" />
            <el-option label="畜禽" value="畜禽" />
            <el-option label="水产" value="水产" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>
        <el-form-item label="价格" prop="price">
          <el-input-number v-model="productForm.price" :min="0" :precision="2" />
          <el-select v-model="productForm.unit" style="width: 100px; margin-left: 10px">
            <el-option label="元/斤" value="斤" />
            <el-option label="元/公斤" value="公斤" />
            <el-option label="元/吨" value="吨" />
            <el-option label="元/个" value="个" />
          </el-select>
        </el-form-item>
        <el-form-item label="库存数量" prop="stock">
          <el-input-number v-model="productForm.stock" :min="0" />
        </el-form-item>
        <el-form-item label="产地" prop="origin">
          <el-input v-model="productForm.origin" placeholder="请输入产地" />
        </el-form-item>
        <el-form-item label="商品描述" prop="description">
          <el-input
            v-model="productForm.description"
            type="textarea"
            :rows="4"
            placeholder="请输入商品描述"
          />
        </el-form-item>
        <el-form-item label="商品图片" prop="images">
          <el-upload
            v-model:file-list="fileList"
            action="#"
            list-type="picture-card"
            :auto-upload="false"
          >
            <el-icon><Plus /></el-icon>
          </el-upload>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">
          {{ isEdit ? '保存' : '发布' }}
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { productApi } from '@/api'

const dialogVisible = ref(false)
const isEdit = ref(false)
const submitting = ref(false)
const formRef = ref(null)
const fileList = ref([])

// 商品列表
const productList = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 商品表单
const productForm = reactive({
  orderId: null,
  productName: '',
  category: '',
  price: 0,
  unit: '斤',
  stock: 0,
  origin: '',
  description: '',
  images: []
})

// 表单验证规则
const formRules = {
  productName: [{ required: true, message: '请输入商品名称', trigger: 'blur' }],
  category: [{ required: true, message: '请选择商品类别', trigger: 'change' }],
  price: [{ required: true, message: '请输入价格', trigger: 'blur' }],
  stock: [{ required: true, message: '请输入库存数量', trigger: 'blur' }],
  origin: [{ required: true, message: '请输入产地', trigger: 'blur' }],
  description: [{ required: true, message: '请输入商品描述', trigger: 'blur' }]
}

// 加载商品列表
const loadProducts = async () => {
  try {
    // TODO: 调用后端接口
    // const res = await productApi.getMyProducts({
    //   page: currentPage.value,
    //   size: pageSize.value
    // })

    // 模拟数据
    productList.value = [
      {
        orderId: 1,
        productName: '有机大米',
        category: '粮食',
        price: 5.8,
        unit: '斤',
        stock: 5000,
        origin: '黑龙江五常',
        status: 1,
        createTime: '2024-01-15 10:30:00'
      },
      {
        orderId: 2,
        productName: '新鲜蔬菜',
        category: '蔬菜',
        price: 3.5,
        unit: '斤',
        stock: 2000,
        origin: '山东寿光',
        status: 1,
        createTime: '2024-01-14 14:20:00'
      }
    ]
    total.value = 2
  } catch (error) {
    ElMessage.error('加载商品列表失败')
  }
}

// 打开发布对话框
const openPublishDialog = () => {
  isEdit.value = false
  resetForm()
  dialogVisible.value = true
}

// 编辑商品
const handleEdit = (row) => {
  isEdit.value = true
  Object.assign(productForm, row)
  dialogVisible.value = true
}

// 删除/下架商品
const handleDelete = (row) => {
  const action = row.status === 1 ? '下架' : '删除'
  ElMessageBox.confirm(`确定要${action}该商品吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      // TODO: 调用后端接口
      // await productApi.delete(row.orderId)
      ElMessage.success(`${action}成功`)
      loadProducts()
    } catch (error) {
      ElMessage.error(`${action}失败`)
    }
  })
}

// 提交表单
const handleSubmit = async () => {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  submitting.value = true
  try {
    // TODO: 调用后端接口
    // if (isEdit.value) {
    //   await productApi.update(productForm.orderId, productForm)
    // } else {
    //   await productApi.publish(productForm)
    // }

    ElMessage.success(isEdit.value ? '修改成功' : '发布成功')
    dialogVisible.value = false
    loadProducts()
  } catch (error) {
    ElMessage.error('操作失败')
  } finally {
    submitting.value = false
  }
}

// 重置表单
const resetForm = () => {
  Object.assign(productForm, {
    orderId: null,
    productName: '',
    category: '',
    price: 0,
    unit: '斤',
    stock: 0,
    origin: '',
    description: '',
    images: []
  })
  fileList.value = []
  formRef.value?.resetFields()
}

onMounted(() => {
  loadProducts()
})
</script>

<style scoped>
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
