<template>
  <div class="order-detail-container">
    <div class="page-header">
      <h2>订单详情</h2>
      <el-button @click="goBack" class="back-btn">返回列表</el-button>
    </div>

    <el-card v-if="orderDetail" class="detail-card">
      <!-- 订单状态 -->
      <div class="order-status">
        <el-tag :type="getStatusType(orderDetail.status)" size="large">
          {{ getStatusText(orderDetail.status) }}
        </el-tag>
        <p class="order-id">订单号: {{ orderDetail.purchaseId }}</p>
      </div>

      <!-- 订单信息 -->
      <div class="section">
        <h3>订单信息</h3>
        <div class="info-row">
          <span class="label">创建时间:</span>
          <span>{{ orderDetail.createTime }}</span>
        </div>
        <div class="info-row">
          <span class="label">订单总额:</span>
          <span class="amount">¥{{ orderDetail.totalAmount }}</span>
        </div>
      </div>

      <!-- 商品列表 -->
      <div class="section">
        <h3>商品清单</h3>
        <el-table :data="orderDetail.details" style="width: 100%">
          <el-table-column label="商品">
            <template #default="{ row }">
              <div class="product-cell">
                <img :src="row.picPath" class="product-img" />
                <span>{{ row.name }}</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="单价" prop="price" width="120">
            <template #default="{ row }">¥{{ row.price }}</template>
          </el-table-column>
          <el-table-column label="数量" prop="quantity" width="100" />
          <el-table-column label="小计" width="120">
            <template #default="{ row }">¥{{ (row.price * row.quantity).toFixed(2) }}</template>
          </el-table-column>
        </el-table>
      </div>

      <!-- 操作按钮 -->
      <div class="actions">
        <el-button v-if="orderDetail.status === 'pending'" type="primary" @click="handlePay">
          立即支付
        </el-button>
        <el-button v-if="orderDetail.status === 'pending'" type="danger" plain @click="handleCancel">
          取消订单
        </el-button>
        <el-button v-if="orderDetail.status === 'shipped'" type="success" plain @click="handleConfirm">
          确认收货
        </el-button>
      </div>
    </el-card>

    <el-empty v-else description="订单不存在" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getOrderDetail } from '@/api/order'

const router = useRouter()
const route = useRoute()

const orderDetail = ref(null)

onMounted(async () => {
  await loadOrderDetail()
})

const loadOrderDetail = async () => {
  try {
    const orderId = route.params.id
    const res = await getOrderDetail(orderId)
    orderDetail.value = res.data
  } catch (error) {
    ElMessage.error('加载订单详情失败')
  }
}

const getStatusType = (status) => {
  const map = { pending: 'warning', shipped: 'primary', completed: 'success' }
  return map[status] || 'info'
}

const getStatusText = (status) => {
  const map = { pending: '待付款', shipped: '已发货', completed: '已完成' }
  return map[status] || status
}

const goBack = () => {
  router.push('/order')
}

const handlePay = () => {
  ElMessage.info('跳转到支付页面...')
  // 这里可以跳转到实际的支付页面
}

const handleCancel = async () => {
  try {
    await ElMessageBox.confirm('确定取消该订单吗？', '提示', {
      type: 'warning'
    })
    ElMessage.success('订单已取消')
    await loadOrderDetail()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('取消订单失败')
    }
  }
}

const handleConfirm = () => {
  ElMessage.success('确认收货成功')
  // 这里可以调用确认收货的API
  loadOrderDetail()
}
</script>

<style scoped>
.order-detail-container {
  max-width: 1000px;
  margin: 20px auto;
  padding: 0 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-header h2 {
  margin: 0;
}

.detail-card {
  margin-bottom: 20px;
}

.order-status {
  display: flex;
  align-items: center;
  gap: 15px;
  padding: 20px;
  background: #f5f7fa;
  border-radius: 4px;
  margin-bottom: 20px;
}

.order-id {
  color: #606266;
  font-size: 14px;
  margin: 0;
}

.section {
  margin-bottom: 30px;
}

.section h3 {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 15px;
  padding-bottom: 10px;
  border-bottom: 1px solid #eee;
}

.info-row {
  display: flex;
  padding: 8px 0;
}

.info-row .label {
  width: 100px;
  color: #909399;
}

.info-row .amount {
  color: #f56c6c;
  font-size: 18px;
  font-weight: bold;
}

.product-cell {
  display: flex;
  align-items: center;
  gap: 10px;
}

.product-img {
  width: 50px;
  height: 50px;
  object-fit: cover;
  border-radius: 4px;
}

.actions {
  display: flex;
  gap: 10px;
  padding-top: 20px;
  border-top: 1px solid #eee;
}
</style>
