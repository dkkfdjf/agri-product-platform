<template>
  <div class="bank-matching">
    <h2>智能匹配</h2>

    <el-card>
      <div class="matching-header">
        <p>根据农户提交的融资意向，智能匹配合适的融资产品</p>
        <el-button type="primary" @click="loadMatchingList">
          <el-icon><Refresh /></el-icon>
          刷新匹配
        </el-button>
      </div>

      <el-table :data="matchingList" stripe>
        <el-table-column prop="intentionId" label="意向编号" width="100" />
        <el-table-column prop="farmerName" label="农户姓名" width="120" />
        <el-table-column prop="amount" label="期望金额" width="120">
          <template #default="{ row }">
            ¥{{ row.amount }}
          </template>
        </el-table-column>
        <el-table-column prop="term" label="期望期限" width="100">
          <template #default="{ row }">
            {{ row.term }}个月
          </template>
        </el-table-column>
        <el-table-column prop="purpose" label="借款用途" show-overflow-tooltip />
        <el-table-column prop="matchedProduct" label="推荐产品" width="150" />
        <el-table-column prop="matchRate" label="匹配度" width="100">
          <template #default="{ row }">
            <el-progress :percentage="row.matchRate" :color="getMatchColor(row.matchRate)" />
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="提交时间" width="180" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" link @click="handleContact(row)">
              联系农户
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'

const matchingList = ref([])

// 获取匹配度颜色
const getMatchColor = (rate) => {
  if (rate >= 80) return '#67C23A'
  if (rate >= 60) return '#E6A23C'
  return '#F56C6C'
}

// 加载匹配列表
const loadMatchingList = async () => {
  try {
    // TODO: 调用后端接口
    // const res = await financeApi.matchIntention()

    // 模拟数据
    matchingList.value = [
      {
        intentionId: 1,
        farmerName: '张三',
        phone: '13800138000',
        amount: 50000,
        term: 12,
        purpose: '购买种子和化肥',
        matchedProduct: '农业小额贷款',
        matchRate: 95,
        createTime: '2024-01-15 10:00:00'
      },
      {
        intentionId: 2,
        farmerName: '李四',
        phone: '13900139000',
        amount: 150000,
        term: 24,
        purpose: '扩大种植规模',
        matchedProduct: '农业生产贷款',
        matchRate: 88,
        createTime: '2024-01-14 14:30:00'
      },
      {
        intentionId: 3,
        farmerName: '王五',
        phone: '13700137000',
        amount: 300000,
        term: 36,
        purpose: '购买农机设备',
        matchedProduct: '农机购置贷款',
        matchRate: 92,
        createTime: '2024-01-13 09:00:00'
      }
    ]

    ElMessage.success('匹配列表已更新')
  } catch (error) {
    ElMessage.error('加载匹配列表失败')
  }
}

// 联系农户
const handleContact = (row) => {
  ElMessage.success(`已获取农户联系方式：${row.phone}`)
}

onMounted(() => {
  loadMatchingList()
})
</script>

<style scoped>
.matching-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.matching-header p {
  margin: 0;
  color: #666;
}
</style>
