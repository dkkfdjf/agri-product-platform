<template>
  <div class="order-list-container">
    <h2>我的订单</h2>

    <el-tabs v-model="activeTab" @tab-change="loadOrders">
      <el-tab-pane label="全部订单" name="all" />
      <el-tab-pane label="待付款" name="pending" />
      <el-tab-pane label="已发货" name="shipped" />
      <el-tab-pane label="已完成" name="completed" />
    </el-tabs>

    <el-empty v-if="orders.length === 0" description="暂无订单" />

    <div v-else class="order-list">
      <el-card v-for="order in orders" :key="order.purchaseId" class="order-card">
        <div class="order-header">
          <span>订单号: {{ order.purchaseId }}</span>
          <span>{{ order.createTime }}</span>
          <el-tag :type="getStatusType(order.status)">{{ getStatusText(order.status) }}</el-tag>
        </div>
        <div class="order-items">
          <div v-for="item in order.details" :key="item.detailId" class="order-item">
            <img :src="item.picPath" />
            <div class="item-info">
              <h4>{{ item.name }}</h4>
              <p>数量: {{ item.quantity }}</p>
            </div>
            <span class="item-price">¥{{ item.price }}</span>
          </div>
        </div>
        <div class="order-footer">
          <div class="order-total">
            订单总额: <span>¥{{ order.totalAmount }}</span>
          </div>
          <div class="order-actions">
            <el-button @click="viewDetail(order.purchaseId)">查看详情</el-button>
          </div>
        </div>
      </el-card>
    </div>

    <el-pagination
      v-if="pagination.total > 0"
      v-model:current-page="pagination.page"
      v-model:page-size="pagination.pageSize"
      :total="pagination.total"
      @current-change="loadOrders"
    />
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getOrders } from '@/api/order'

const router = useRouter()

const activeTab = ref('all')
const orders = ref([])
const pagination = reactive({
  page: 1,
  pageSize: 10,
  total: 0
})

onMounted(() => {
  loadOrders()
})

const loadOrders = async () => {
  try {
    const params = {
      page: pagination.page,
      pageSize: pagination.pageSize,
      status: activeTab.value === 'all' ? null : activeTab.value
    }
    const res = await getOrders(params)
    orders.value = res.data.records || []
    pagination.total = res.data.total || 0
  } catch (error) {
    ElMessage.error('加载订单失败')
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

const viewDetail = (id) => {
  router.push(`/order/${id}`)
}
</script>

<style scoped>
.order-list-container {
  max-width: 1000px;
  margin: 20px auto;
  padding: 0 20px;
}

.order-card {
  margin-bottom: 20px;
}

.order-header {
  display: flex;
  justify-content: space-between;
  padding-bottom: 15px;
  border-bottom: 1px solid #eee;
}

.order-items {
  padding: 15px 0;
}

.order-item {
  display: flex;
  gap: 15px;
  padding: 10px 0;
}

.order-item img {
  width: 80px;
  height: 80px;
  object-fit: cover;
}

.item-info {
  flex: 1;
}

.item-price {
  font-weight: bold;
}

.order-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 15px;
  border-top: 1px solid #eee;
}

.order-total span {
  color: #f56c6c;
  font-size: 18px;
  font-weight: bold;
}

.el-pagination {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}
</style>
