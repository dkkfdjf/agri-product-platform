<template>
  <div class="orders-page">
    <div class="page-header">
      <h1 class="page-title">订单管理</h1>
      <el-select v-model="statusFilter" placeholder="筛选状态" style="width: 150px" clearable @change="loadOrders">
        <el-option label="全部" value="" />
        <el-option label="待付款" value="待付款" />
        <el-option label="待发货" value="待发货" />
        <el-option label="已完成" value="已完成" />
        <el-option label="已取消" value="已取消" />
      </el-select>
    </div>

    <!-- 订单列表 -->
    <el-card class="orders-card" v-loading="loading">
      <div v-for="order in orders" :key="order.id || order.purchaseId" class="order-item">
        <div class="order-header">
          <span class="order-id">订单号：{{ order.id || order.purchaseId }}</span>
          <span class="order-time">{{ formatTime(order.createTime) }}</span>
        </div>

        <!-- 商品列表（可能包含多个商品） -->
        <div class="products-section">
          <div v-for="item in (order.items || [])" :key="item.productId" class="product-item">
            <div class="product-info">
              <img :src="item.productPic || '/images/default-product.jpg'" class="product-image" />
              <div class="product-details">
                <div class="product-name">{{ item.productName }}</div>
                <div class="product-meta">
                  <span>数量：{{ item.count }}</span>
                  <span>单价：¥{{ item.price }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div class="order-content">
          <div class="order-info">
            <div class="order-total">
              <span class="label">订单总额：</span>
              <span class="amount">¥{{ order.totalPrice }}</span>
            </div>
            <div class="order-buyer">
              <span class="label">买家：</span>
              <span>{{ order.ownName }}</span>
            </div>
          </div>

          <div class="order-actions">
            <el-tag :type="getStatusType(order.statusText)" class="status-tag">
              {{ order.statusText }}
            </el-tag>

            <div class="action-buttons" v-if="order.statusText === '待发货'">
              <el-button type="primary" size="small" @click="shipOrder(order)">
                发货
              </el-button>
            </div>
            <div class="action-buttons" v-if="order.statusText === '待付款'">
              <el-button type="danger" size="small" plain @click="cancelOrder(order)">
                取消订单
              </el-button>
            </div>
          </div>
        </div>
      </div>

      <div class="pagination">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :total="total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="loadOrders"
          @current-change="loadOrders"
        />
      </div>

      <el-empty v-if="!loading && orders.length === 0" description="暂无订单数据" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getReceivedOrders, cancelOrder as cancelOrderApi, updateOrderStatus } from '@/api/order'

const loading = ref(false)
const orders = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const statusFilter = ref('')

// 加载订单列表
const loadOrders = async () => {
  loading.value = true
  try {
    const res = await getReceivedOrders({
      page: currentPage.value,
      pageSize: pageSize.value,
      status: statusFilter.value
    })
    if (res.code === 200) {
      orders.value = res.data.records || []
      total.value = res.data.total || 0
    }
  } catch (error) {
    ElMessage.error('加载订单列表失败')
  } finally {
    loading.value = false
  }
}

// 格式化时间
const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// 获取状态类型
const getStatusType = (status) => {
  const typeMap = {
    '待付款': 'warning',
    '待发货': 'primary',
    '已完成': 'success',
    '已取消': 'info'
  }
  return typeMap[status] || 'info'
}

// 发货
const shipOrder = async (order) => {
  try {
    await ElMessageBox.confirm(`确认订单"${order.id || order.purchaseId}"已发货？`, '提示', {
      type: 'info'
    })
    // 调用发货API，将状态从 2(待发货) 改为 3(待收货)
    await updateOrderStatus(order.id || order.purchaseId, { status: 3 })
    ElMessage.success('发货成功')
    await loadOrders()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('发货失败')
    }
  }
}

// 取消订单
const cancelOrder = async (order) => {
  try {
    await ElMessageBox.confirm(`确定要取消订单"${order.id || order.purchaseId}"吗？`, '提示', {
      type: 'warning'
    })
    await cancelOrderApi(order.id || order.purchaseId)
    ElMessage.success('订单已取消')
    await loadOrders()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('取消订单失败')
    }
  }
}

onMounted(() => {
  loadOrders()
})
</script>

<style scoped>
.orders-page {
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

.orders-card {
  flex: 1;
}

.order-item {
  padding: 20px;
  border-bottom: 1px solid #eee;
}

.order-item:last-child {
  border-bottom: none;
}

.order-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 15px;
  font-size: 14px;
  color: #666;
}

.order-id {
  font-weight: 500;
}

.products-section {
  margin-bottom: 15px;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.product-item {
  display: flex;
  gap: 15px;
}

.order-content {
  display: flex;
  gap: 20px;
}

.product-info {
  display: flex;
  gap: 15px;
  flex: 1;
}

.product-image {
  width: 80px;
  height: 80px;
  object-fit: cover;
  border-radius: 8px;
}

.product-details {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.product-name {
  font-weight: 500;
  font-size: 16px;
}

.product-meta {
  font-size: 14px;
  color: #666;
  display: flex;
  gap: 15px;
}

.order-info {
  display: flex;
  flex-direction: column;
  gap: 8px;
  min-width: 150px;
}

.order-total .amount {
  font-weight: 600;
  color: #f56c6c;
  font-size: 18px;
}

.order-actions {
  display: flex;
  flex-direction: column;
  gap: 10px;
  align-items: flex-end;
  min-width: 150px;
}

.action-buttons {
  display: flex;
  gap: 10px;
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}
</style>
