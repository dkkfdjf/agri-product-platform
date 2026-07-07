<template>
  <div class="farmer-orders">
    <h2>我的订单</h2>

    <el-card>
      <!-- 订单列表 -->
      <el-table :data="orderList" stripe>
        <el-table-column prop="purchaseId" label="订单号" width="100" />
        <el-table-column prop="productName" label="商品名称" />
        <el-table-column prop="quantity" label="数量" width="100" />
        <el-table-column prop="totalPrice" label="总价" width="120">
          <template #default="{ row }">
            ¥{{ row.totalPrice }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="下单时间" width="180" />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" link @click="handleDetail(row)">
              详情
            </el-button>
            <el-button
              v-if="row.status === 1"
              type="warning"
              size="small"
              link
              @click="handleCancel(row)"
            >
              取消
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
          @size-change="loadOrders"
          @current-change="loadOrders"
        />
      </div>
    </el-card>

    <!-- 订单详情对话框 -->
    <el-dialog v-model="detailVisible" title="订单详情" width="600px">
      <el-descriptions :column="2" border v-if="currentOrder">
        <el-descriptions-item label="订单号">{{ currentOrder.purchaseId }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusType(currentOrder.status)">
            {{ getStatusText(currentOrder.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="商品名称">{{ currentOrder.productName }}</el-descriptions-item>
        <el-descriptions-item label="数量">{{ currentOrder.quantity }}</el-descriptions-item>
        <el-descriptions-item label="单价">¥{{ currentOrder.price }}</el-descriptions-item>
        <el-descriptions-item label="总价">¥{{ currentOrder.totalPrice }}</el-descriptions-item>
        <el-descriptions-item label="收货人" span="2">{{ currentOrder.receiver }}</el-descriptions-item>
        <el-descriptions-item label="联系电话" span="2">{{ currentOrder.phone }}</el-descriptions-item>
        <el-descriptions-item label="收货地址" span="2">{{ currentOrder.address }}</el-descriptions-item>
        <el-descriptions-item label="下单时间" span="2">{{ currentOrder.createTime }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const orderList = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const detailVisible = ref(false)
const currentOrder = ref(null)

// 状态类型
const getStatusType = (status) => {
  const types = {
    1: 'warning',
    2: 'primary',
    3: 'info',
    4: 'success',
    5: 'danger'
  }
  return types[status] || 'info'
}

// 状态文本
const getStatusText = (status) => {
  const texts = {
    1: '待付款',
    2: '待发货',
    3: '待收货',
    4: '已完成',
    5: '已取消'
  }
  return texts[status] || '未知'
}

// 加载订单列表
const loadOrders = async () => {
  try {
    // TODO: 调用后端接口
    // const res = await orderApi.list({
    //   page: currentPage.value,
    //   size: pageSize.value
    // })

    // 模拟数据
    orderList.value = [
      {
        purchaseId: 1001,
        productName: '有机大米',
        quantity: 100,
        price: 5.8,
        totalPrice: 580,
        status: 2,
        receiver: '张三',
        phone: '13800138000',
        address: '北京市朝阳区某某街道',
        createTime: '2024-01-15 10:30:00'
      },
      {
        purchaseId: 1002,
        productName: '新鲜蔬菜',
        quantity: 50,
        price: 3.5,
        totalPrice: 175,
        status: 4,
        receiver: '李四',
        phone: '13900139000',
        address: '上海市浦东新区某某路',
        createTime: '2024-01-10 14:20:00'
      }
    ]
    total.value = 2
  } catch (error) {
    ElMessage.error('加载订单列表失败')
  }
}

// 查看详情
const handleDetail = (row) => {
  currentOrder.value = row
  detailVisible.value = true
}

// 取消订单
const handleCancel = (row) => {
  ElMessageBox.confirm('确定要取消该订单吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      // TODO: 调用后端接口
      // await orderApi.cancel(row.purchaseId)
      ElMessage.success('取消成功')
      loadOrders()
    } catch (error) {
      ElMessage.error('取消失败')
    }
  })
}

onMounted(() => {
  loadOrders()
})
</script>

<style scoped>
.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
