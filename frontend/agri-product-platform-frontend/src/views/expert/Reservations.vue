<template>
  <div class="expert-reservations">
    <h2>预约管理</h2>

    <el-tabs v-model="activeTab">
      <!-- 待处理 -->
      <el-tab-pane label="待处理" name="pending">
        <el-card>
          <el-table :data="pendingList" stripe>
            <el-table-column prop="reserveId" label="预约编号" width="100" />
            <el-table-column prop="farmerName" label="预约人" width="120" />
            <el-table-column prop="title" label="咨询主题" />
            <el-table-column prop="reserveTime" label="预约时间" width="180" />
            <el-table-column prop="createTime" label="预约时间" width="180" />
            <el-table-column label="操作" width="200" fixed="right">
              <template #default="{ row }">
                <el-button type="primary" size="small" link @click="handleView(row)">
                  查看
                </el-button>
                <el-button type="success" size="small" link @click="handleAccept(row)">
                  接受
                </el-button>
                <el-button type="danger" size="small" link @click="handleReject(row)">
                  拒绝
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-tab-pane>

      <!-- 已处理 -->
      <el-tab-pane label="已处理" name="processed">
        <el-card>
          <el-table :data="processedList" stripe>
            <el-table-column prop="reserveId" label="预约编号" width="100" />
            <el-table-column prop="farmerName" label="预约人" width="120" />
            <el-table-column prop="title" label="咨询主题" />
            <el-table-column prop="reserveTime" label="预约时间" width="180" />
            <el-table-column prop="status" label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="row.status === 1 ? 'success' : 'danger'">
                  {{ row.status === 1 ? '已完成' : '已拒绝' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="handleTime" label="处理时间" width="180" />
          </el-table>
        </el-card>
      </el-tab-pane>
    </el-tabs>

    <!-- 查看详情对话框 -->
    <el-dialog v-model="viewVisible" title="预约详情" width="600px">
      <el-descriptions :column="2" border v-if="currentReservation">
        <el-descriptions-item label="预约编号">{{ currentReservation.reserveId }}</el-descriptions-item>
        <el-descriptions-item label="预约人">{{ currentReservation.farmerName }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ currentReservation.phone }}</el-descriptions-item>
        <el-descriptions-item label="预约时间">{{ currentReservation.reserveTime }}</el-descriptions-item>
        <el-descriptions-item label="咨询主题" span="2">{{ currentReservation.title }}</el-descriptions-item>
        <el-descriptions-item label="问题描述" span="2">{{ currentReservation.description }}</el-descriptions-item>
        <el-descriptions-item label="预约提交时间">{{ currentReservation.createTime }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusType(currentReservation.status)">
            {{ getStatusText(currentReservation.status) }}
          </el-tag>
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const activeTab = ref('pending')
const pendingList = ref([])
const processedList = ref([])
const viewVisible = ref(false)
const currentReservation = ref(null)

// 状态类型
const getStatusType = (status) => {
  const types = {
    0: 'warning',
    1: 'success',
    2: 'danger'
  }
  return types[status] || 'info'
}

// 状态文本
const getStatusText = (status) => {
  const texts = {
    0: '待处理',
    1: '已完成',
    2: '已拒绝'
  }
  return texts[status] || '未知'
}

// 加载待处理列表
const loadPendingList = async () => {
  try {
    // TODO: 调用后端接口
    // const res = await expertApi.expertReserveList({ status: 0 })

    // 模拟数据
    pendingList.value = [
      {
        reserveId: 1,
        farmerName: '张三',
        phone: '13800138000',
        title: '水稻种植咨询',
        description: '想咨询水稻种植的技术问题',
        reserveTime: '2024-01-20 14:00',
        createTime: '2024-01-15 10:00:00',
        status: 0
      },
      {
        reserveId: 2,
        farmerName: '李四',
        phone: '13900139000',
        title: '病虫害防治咨询',
        description: '咨询病虫害防治的方法',
        reserveTime: '2024-01-21 10:00',
        createTime: '2024-01-14 16:30:00',
        status: 0
      }
    ]
  } catch (error) {
    ElMessage.error('加载待处理列表失败')
  }
}

// 加载已处理列表
const loadProcessedList = async () => {
  try {
    // TODO: 调用后端接口
    // const res = await expertApi.expertReserveList({ status: [1, 2] })

    // 模拟数据
    processedList.value = [
      {
        reserveId: 3,
        farmerName: '王五',
        phone: '13700137000',
        title: '施肥技术咨询',
        description: '咨询施肥技术',
        reserveTime: '2024-01-18 15:00',
        createTime: '2024-01-12 09:00:00',
        status: 1,
        handleTime: '2024-01-18 15:30:00'
      },
      {
        reserveId: 4,
        farmerName: '赵六',
        phone: '13600136000',
        title: '灌溉咨询',
        description: '咨询灌溉技术',
        reserveTime: '2024-01-19 10:00',
        createTime: '2024-01-13 11:00:00',
        status: 2,
        handleTime: '2024-01-13 14:00:00'
      }
    ]
  } catch (error) {
    ElMessage.error('加载已处理列表失败')
  }
}

// 查看详情
const handleView = (row) => {
  currentReservation.value = row
  viewVisible.value = true
}

// 接受预约
const handleAccept = (row) => {
  ElMessageBox.confirm('确定接受该预约吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'info'
  }).then(async () => {
    try {
      // TODO: 调用后端接口
      // await expertApi.handleReserve(row.reserveId, { status: 1 })

      ElMessage.success('已接受预约')
      loadPendingList()
      loadProcessedList()
    } catch (error) {
      ElMessage.error('操作失败')
    }
  })
}

// 拒绝预约
const handleReject = (row) => {
  ElMessageBox.prompt('请输入拒绝原因', '拒绝预约', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    inputPattern: /.+/,
    inputErrorMessage: '请输入拒绝原因'
  }).then(async ({ value }) => {
    try {
      // TODO: 调用后端接口
      // await expertApi.handleReserve(row.reserveId, { status: 2, remark: value })

      ElMessage.success('已拒绝预约')
      loadPendingList()
      loadProcessedList()
    } catch (error) {
      ElMessage.error('操作失败')
    }
  })
}

onMounted(() => {
  loadPendingList()
  loadProcessedList()
})
</script>

<style scoped>
.page-header {
  margin-bottom: 20px;
}
</style>
