<template>
  <div class="buyer-cart">
    <h2>购物车</h2>

    <el-card>
      <el-empty v-if="cartList.length === 0" description="购物车空空如也" />

      <div v-else>
        <el-table :data="cartList" stripe>
          <el-table-column type="selection" width="55" />
          <el-table-column label="商品信息" min-width="300">
            <template #default="{ row }">
              <div class="product-info">
                <el-image
                  :src="row.picture || '/placeholder.jpg'"
                  fit="cover"
                  class="product-image"
                />
                <div class="product-detail">
                  <div class="product-title">{{ row.title }}</div>
                  <div class="product-price">¥{{ row.price }}</div>
                </div>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="数量" width="150">
            <template #default="{ row }">
              <el-input-number
                v-model="row.count"
                :min="1"
                :max="99"
                @change="handleCountChange(row)"
              />
            </template>
          </el-table-column>
          <el-table-column label="小计" width="120">
            <template #default="{ row }">
              <span class="subtotal">¥{{ (row.price * row.count).toFixed(2) }}</span>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="100" fixed="right">
            <template #default="{ row }">
              <el-button type="danger" size="small" link @click="handleDelete(row)">
                删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>

        <div class="cart-footer">
          <div class="total-section">
            <span class="total-label">已选商品</span>
            <span class="total-price">总计：¥{{ totalPrice }}</span>
          </div>
          <el-button type="primary" size="large" @click="handleCheckout">
            结算
          </el-button>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useRouter } from 'vue-router'

const router = useRouter()

// 购物车列表
const cartList = ref([])

// 总价
const totalPrice = computed(() => {
  return cartList.value
    .reduce((sum, item) => sum + item.price * item.count, 0)
    .toFixed(2)
})

// 加载购物车
const loadCartList = async () => {
  try {
    // TODO: 调用后端接口
    // const res = await cartApi.list()

    // 模拟数据
    cartList.value = [
      {
        shoppingId: 1,
        orderId: 101,
        title: '优质大米',
        price: 5.5,
        count: 2,
        picture: ''
      },
      {
        shoppingId: 2,
        orderId: 102,
        title: '新鲜蔬菜',
        price: 3.0,
        count: 5,
        picture: ''
      }
    ]
  } catch (error) {
    ElMessage.error('加载购物车失败')
  }
}

// 修改数量
const handleCountChange = async (row) => {
  try {
    // TODO: 调用后端接口
    // await cartApi.update(row.shoppingId, { count: row.count })

    ElMessage.success('数量修改成功')
  } catch (error) {
    ElMessage.error('修改失败')
  }
}

// 删除商品
const handleDelete = (row) => {
  ElMessageBox.confirm('确定要从购物车删除该商品吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      // TODO: 调用后端接口
      // await cartApi.delete(row.shoppingId)

      cartList.value = cartList.value.filter(item => item.shoppingId !== row.shoppingId)
      ElMessage.success('删除成功')
    } catch (error) {
      ElMessage.error('删除失败')
    }
  })
}

// 结算
const handleCheckout = () => {
  router.push('/buyer/orders')
}

onMounted(() => {
  loadCartList()
})
</script>

<style scoped>
.buyer-cart {
  padding: 0;
}

.buyer-cart > h2 {
  margin-bottom: 20px;
  color: #333;
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
  margin-bottom: 8px;
}

.product-price {
  font-size: 16px;
  color: #f56c6c;
  font-weight: bold;
}

.subtotal {
  font-size: 16px;
  color: #f56c6c;
  font-weight: bold;
}

.cart-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #eee;
}

.total-section {
  display: flex;
  align-items: center;
  gap: 20px;
}

.total-label {
  font-size: 14px;
  color: #666;
}

.total-price {
  font-size: 20px;
  color: #f56c6c;
  font-weight: bold;
}
</style>
