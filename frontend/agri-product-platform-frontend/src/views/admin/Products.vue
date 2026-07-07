<template>
  <div class="admin-products">
    <h2>商品管理</h2>

    <el-card>
      <el-table :data="productList" stripe>
        <el-table-column prop="orderId" label="商品ID" width="100" />
        <el-table-column prop="productName" label="商品名称" />
        <el-table-column prop="farmerName" label="发布农户" width="120" />
        <el-table-column prop="category" label="类别" width="100" />
        <el-table-column prop="price" label="价格" width="120">
          <template #default="{ row }">
            ¥{{ row.price }}/{{ row.unit }}
          </template>
        </el-table-column>
        <el-table-column prop="stock" label="库存" width="80" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">
              {{ row.status === 1 ? '在售' : '下架' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="发布时间" width="180" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button type="danger" size="small" link @click="handleDelete(row)">
              删除
            </el-button>
            <el-button type="warning" size="small" link @click="handleToggleStatus(row)">
              {{ row.status === 1 ? '下架' : '上架' }}
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
          @size-change="loadProductList"
          @current-change="loadProductList"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const productList = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 加载商品列表
const loadProductList = async () => {
  try {
    // TODO: 调用后端接口
    // const res = await adminApi.getProductList({
    //   page: currentPage.value,
    //   size: pageSize.value
    // })

    // 模拟数据
    productList.value = [
      {
        orderId: 1,
        productName: '有机大米',
        farmerName: '张三',
        category: '粮食',
        price: 5.8,
        unit: '斤',
        stock: 5000,
        status: 1,
        createTime: '2024-01-15 10:00:00'
      },
      {
        orderId: 2,
        productName: '新鲜蔬菜',
        farmerName: '李四',
        category: '蔬菜',
        price: 3.5,
        unit: '斤',
        stock: 2000,
        status: 1,
        createTime: '2024-01-14 14:30:00'
      }
    ]
    total.value = 2
  } catch (error) {
    ElMessage.error('加载商品列表失败')
  }
}

// 删除商品
const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该商品吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      // TODO: 调用后端接口
      // await adminApi.deleteProduct(row.orderId)

      ElMessage.success('删除成功')
      loadProductList()
    } catch (error) {
      ElMessage.error('删除失败')
    }
  })
}

// 切换状态
const handleToggleStatus = (row) => {
  const action = row.status === 1 ? '下架' : '上架'
  ElMessageBox.confirm(`确定要${action}该商品吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      // TODO: 调用后端接口
      // await adminApi.updateProduct(row.orderId, { status: row.status === 1 ? 0 : 1 })

      ElMessage.success(`${action}成功`)
      loadProductList()
    } catch (error) {
      ElMessage.error(`${action}失败`)
    }
  })
}

onMounted(() => {
  loadProductList()
})
</script>

<style scoped>
.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
