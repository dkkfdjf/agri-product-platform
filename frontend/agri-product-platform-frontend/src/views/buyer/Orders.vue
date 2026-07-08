<template>
  <div class="buyer-orders">
    <h2>我的订单</h2>

    <el-card>
      <el-tabs v-model="activeTab" @tab-change="handleTabChange">
        <el-tab-pane label="全部" name="all" />
        <el-tab-pane label="待付款" name="1" />
        <el-tab-pane label="待发货" name="2" />
        <el-tab-pane label="待收货" name="3" />
        <el-tab-pane label="已完成" name="4" />
      </el-tabs>

      <el-empty v-if="orderList.length === 0" description="暂无订单" />

      <div v-else class="order-list">
        <div v-for="order in orderList" :key="order.purchaseId" class="order-item">
          <div class="order-header">
            <span class="order-no">订单号：{{ order.purchaseId }}</span>
            <el-tag :type="getStatusType(order.purchaseStatus)">
              {{ getStatusText(order.purchaseStatus) }}
            </el-tag>
          </div>

          <div class="order-content">
            <div class="product-list">
              <div v-for="item in order.details" :key="item.detailId" class="product-item">
                <div class="product-info">
                  <el-image
                    :src="item.picture || '/placeholder.jpg'"
                    fit="cover"
                    class="product-image"
                  />
                  <div class="product-detail">
                    <div class="product-title">{{ item.title }}</div>
                    <div class="product-spec">¥{{ item.uninPrice }} × {{ item.count }}</div>
                  </div>
                </div>
                <div class="product-sum">¥{{ item.sumPrice }}</div>
              </div>
            </div>

            <div class="order-footer">
              <div class="order-total">
                共{{ getTotalCount(order) }}件商品 总价：<span class="price">¥{{ order.totalPrice }}</span>
              </div>
              <div class="order-actions">
                <el-button
                  v-if="order.purchaseStatus === 1"
                  type="primary"
                  size="small"
                  @click="handlePay(order)"
                >
                  去支付
                </el-button>
                <el-button
                  v-if="order.purchaseStatus === 1"
                  type="danger"
                  size="small"
                  link
                  @click="handleCancel(order)"
                >
                  取消订单
                </el-button>
                <el-button
                  v-if="order.purchaseStatus === 3"
                  type="primary"
                  size="small"
                  @click="handleConfirm(order)"
                >
                  确认收货
                </el-button>
                <el-button
                  v-if="order.purchaseStatus >= 4"
                  type="info"
                  size="small"
                  link
                >
                  查看详情
                </el-button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const activeTab = ref('all')
const orderList = ref([])

// 订单状态映射
const statusMap = {
  1: { text: '待付款', type: 'warning' },
  2: { text: '待发货', type: 'primary' },
  3: { text: '待收货', type: 'success' },
  4: { text: '已完成', type: 'info' },
  5: { text: '已取消', type: 'danger' }
}

// 获取状态类型
const getStatusType = (status) => {
  return statusMap[status]?.type || 'info'
}

// 获取状态文本
const getStatusText = (status) => {
  return statusMap[status]?.text || '未知'
}

// 获取订单商品总数
const getTotalCount = (order) => {
  return order.details?.reduce((sum, item) => sum + item.count, 0) || 0
}

// 加载订单列表
const loadOrderList = async () => {
  try {
    // TODO: 调用后端接口
    // const res = await orderApi.list({ status: activeTab.value === 'all' ? null : activeTab.value })

    // 模拟数据
    orderList.value = [
      {
        purchaseId: 1001,
        totalPrice: 88.00,
        purchaseStatus: 2,
        details: [
          {
            detailId: 1,
            title: '优质大米',
            uninPrice: 5.5,
            count: 10,
            sumPrice: 55.00,
            picture: ''
          },
          {
            detailId: 2,
            title: '新鲜蔬菜',
            uninPrice: 3.0,
            count: 11,
            sumPrice: 33.00,
            picture: ''
          }
        ]
      },
      {
        purchaseId: 1002,
        totalPrice: 45.00,
        purchaseStatus: 4,
        details: [
          {
            detailId: 3,
            title: '水果礼盒',
            uninPrice: 45.0,
            count: 1,
            sumPrice: 45.00,
            picture: ''
          }
        ]
      }
    ]
  } catch (error) {
    ElMessage.error('加载订单列表失败')
  }
}

// 切换标签
const handleTabChange = () => {
  loadOrderList()
}

// 去支付
const handlePay = (order) => {
  ElMessage.info('跳转到支付页面')
  // TODO: 实现支付逻辑
}

// 取消订单
const handleCancel = (order) => {
  ElMessageBox.confirm('确定要取消该订单吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      // TODO: 调用后端接口
      // await orderApi.cancel(order.purchaseId)

      order.purchaseStatus = 5
      ElMessage.success('订单已取消')
    } catch (error) {
      ElMessage.error('取消失败')
    }
  })
}

// 确认收货
const handleConfirm = (order) => {
  ElMessageBox.confirm('确认已收到商品？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      // TODO: 调用后端接口
      // await orderApi.confirm(order.purchaseId)

      order.purchaseStatus = 4
      ElMessage.success('已确认收货')
    } catch (error) {
      ElMessage.error('操作失败')
    }
  })
}

onMounted(() => {
  loadOrderList()
})
</script>

<style scoped>
.buyer-orders {
  padding: 0;
}

.buyer-orders > h2 {
  margin-bottom: 20px;
  color: #333;
}

.order-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.order-item {
  border: 1px solid #eee;
  border-radius: 8px;
  overflow: hidden;
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 15px;
  background: #f8f9fa;
  border-bottom: 1px solid #eee;
}

.order-no {
  font-size: 14px;
  color: #666;
}

.order-content {
  padding: 15px;
}

.product-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.product-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.product-info {
  display: flex;
  align-items: center;
  gap: 15px;
}

.product-image {
  width: 60px;
  height: 60px;
  border-radius: 4px;
}

.product-detail {
  flex: 1;
}

.product-title {
  font-size: 14px;
  color: #333;
  margin-bottom: 5px;
}

.product-spec {
  font-size: 12px;
  color: #999;
}

.product-sum {
  font-size: 14px;
  color: #f56c6c;
  font-weight: bold;
}

.order-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 15px;
  padding-top: 15px;
  border-top: 1px solid #eee;
}

.order-total {
  font-size: 14px;
  color: #666;
}

.order-total .price {
  font-size: 18px;
  color: #f56c6c;
  font-weight: bold;
}

.order-actions {
  display: flex;
  gap: 10px;
}
</style>
