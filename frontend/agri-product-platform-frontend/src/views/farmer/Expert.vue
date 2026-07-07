<template>
  <div class="farmer-expert">
    <h2>专家咨询</h2>

    <el-tabs v-model="activeTab">
      <!-- 专家列表 -->
      <el-tab-pane label="专家列表" name="experts">
        <el-card>
          <el-row :gutter="20">
            <el-col :span="8" v-for="expert in expertList" :key="expert.userName">
              <el-card class="expert-card" shadow="hover">
                <div class="expert-avatar">
                  <el-avatar :size="80" :src="expert.avatar">
                    {{ expert.expertName?.charAt(0) }}
                  </el-avatar>
                </div>
                <h3>{{ expert.expertName }}</h3>
                <p class="expert-title">{{ expert.title }}</p>
                <p class="expert-specialty">专长：{{ expert.specialty }}</p>
                <p class="expert-desc">{{ expert.description }}</p>
                <div class="expert-actions">
                  <el-button type="primary" size="small" @click="handleReserve(expert)">
                    预约咨询
                  </el-button>
                  <el-button size="small" @click="handleViewDetail(expert)">
                    查看详情
                  </el-button>
                </div>
              </el-card>
            </el-col>
          </el-row>
        </el-card>
      </el-tab-pane>

      <!-- 问答列表 -->
      <el-tab-pane label="我的问答" name="questions">
        <div class="action-buttons">
          <el-button type="primary" @click="openQuestionDialog">
            <el-icon><Plus /></el-icon>
            提问
          </el-button>
        </div>
        <el-card>
          <el-table :data="questionList" stripe>
            <el-table-column prop="questionId" label="问题编号" width="100" />
            <el-table-column prop="title" label="问题标题" />
            <el-table-column prop="status" label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="row.status === 1 ? 'success' : 'warning'">
                  {{ row.status === 1 ? '已回答' : '未回答' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="提问时间" width="180" />
            <el-table-column label="操作" width="150" fixed="right">
              <template #default="{ row }">
                <el-button type="primary" size="small" link @click="handleViewQuestion(row)">
                  查看
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-tab-pane>

      <!-- 我的预约 -->
      <el-tab-pane label="我的预约" name="reservations">
        <el-card>
          <el-table :data="reservationList" stripe>
            <el-table-column prop="reserveId" label="预约编号" width="100" />
            <el-table-column prop="expertName" label="专家姓名" />
            <el-table-column prop="reserveTime" label="预约时间" width="180" />
            <el-table-column prop="status" label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="getReserveStatusType(row.status)">
                  {{ getReserveStatusText(row.status) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="预约时间" width="180" />
            <el-table-column label="操作" width="100" fixed="right">
              <template #default="{ row }">
                <el-button type="danger" size="small" link @click="handleCancelReserve(row)" v-if="row.status === 0">
                  取消
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-tab-pane>
    </el-tabs>

    <!-- 预约对话框 -->
    <el-dialog v-model="reserveVisible" title="预约专家" width="500px">
      <el-form :model="reserveForm" :rules="reserveRules" ref="reserveFormRef" label-width="100px">
        <el-form-item label="专家姓名">
          <el-input v-model="currentExpert?.expertName" disabled />
        </el-form-item>
        <el-form-item label="预约时间" prop="reserveTime">
          <el-date-picker
            v-model="reserveForm.reserveTime"
            type="datetime"
            placeholder="请选择预约时间"
            format="YYYY-MM-DD HH:mm"
            value-format="YYYY-MM-DD HH:mm"
            style="width: 100%"
            :disabled-date="disabledDate"
          />
        </el-form-item>
        <el-form-item label="咨询主题" prop="title">
          <el-input v-model="reserveForm.title" placeholder="请输入咨询主题" />
        </el-form-item>
        <el-form-item label="问题描述" prop="description">
          <el-input v-model="reserveForm.description" type="textarea" :rows="4" placeholder="请详细描述您的问题" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="reserveVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitReserve" :loading="submitting">
          提交预约
        </el-button>
      </template>
    </el-dialog>

    <!-- 提问对话框 -->
    <el-dialog v-model="questionVisible" title="向专家提问" width="600px">
      <el-form :model="questionForm" :rules="questionRules" ref="questionFormRef" label-width="100px">
        <el-form-item label="问题标题" prop="title">
          <el-input v-model="questionForm.title" placeholder="请输入问题标题" />
        </el-form-item>
        <el-form-item label="问题内容" prop="content">
          <el-input v-model="questionForm.content" type="textarea" :rows="6" placeholder="请详细描述您的问题" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="questionVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitQuestion" :loading="submitting">
          提交问题
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const activeTab = ref('experts')
const expertList = ref([])
const questionList = ref([])
const reservationList = ref([])
const reserveVisible = ref(false)
const questionVisible = ref(false)
const submitting = ref(false)
const currentExpert = ref(null)
const reserveFormRef = ref(null)
const questionFormRef = ref(null)

// 预约表单
const reserveForm = reactive({
  expertUserName: '',
  reserveTime: '',
  title: '',
  description: ''
})

// 提问表单
const questionForm = reactive({
  title: '',
  content: ''
})

// 表单验证规则
const reserveRules = {
  reserveTime: [{ required: true, message: '请选择预约时间', trigger: 'change' }],
  title: [{ required: true, message: '请输入咨询主题', trigger: 'blur' }],
  description: [{ required: true, message: '请描述您的问题', trigger: 'blur' }]
}

const questionRules = {
  title: [{ required: true, message: '请输入问题标题', trigger: 'blur' }],
  content: [{ required: true, message, trigger: 'blur' }]
}

// 预约状态类型
const getReserveStatusType = (status) => {
  const types = {
    0: 'warning',
    1: 'success',
    2: 'danger'
  }
  return types[status] || 'info'
}

// 预约状态文本
const getReserveStatusText = (status) => {
  const texts = {
    0: '待处理',
    1: '已完成',
    2: '已拒绝'
  }
  return texts[status] || '未知'
}

// 禁用过去的日期
const disabledDate = (date) => {
  return date.getTime() < Date.now() - 24 * 60 * 60 * 1000
}

// 加载专家列表
const loadExpertList = async () => {
  try {
    // TODO: 调用后端接口
    // const res = await expertApi.list()

    // 模拟数据
    expertList.value = [
      {
        userName: 'expert1',
        expertName: '张教授',
        title: '高级农艺师',
        specialty: '水稻种植技术',
        description: '从事水稻种植研究20年，拥有丰富的实践经验',
        avatar: ''
      },
      {
        userName: 'expert2',
        expertName: '李研究员',
        title: '植物保护专家',
        specialty: '病虫害防治',
        description: '专注于农作物病虫害防治技术研究',
        avatar: ''
      },
      {
        userName: 'expert3',
        expertName: '王高级工程师',
        title: '农业机械化',
        specialty: '农机使用与维护',
        description: '熟悉各种农机的使用和维护',
        avatar: ''
      }
    ]
  } catch (error) {
    ElMessage.error('加载专家列表失败')
  }
}

// 加载问答列表
const loadQuestionList = async () => {
  try {
    // TODO: 调用后端接口
    // const res = await questionApi.list()

    // 模拟数据
    questionList.value = [
      {
        questionId: 1,
        title: '水稻育苗期需要注意什么？',
        status: 1,
        createTime: '2024-01-10 10:00:00'
      },
      {
        questionId: 2,
        title: '如何防治水稻病虫害？',
        status: 0,
        createTime: '2024-01-12 14:30:00'
      }
    ]
  } catch (error) {
    ElMessage.error('加载问答列表失败')
  }
}

// 加载预约列表
const loadReservationList = async () => {
  try {
    // TODO: 调用后端接口
    // const res = await expertApi.myReserveList()

    // 模拟数据
    reservationList.value = [
      {
        reserveId: 1,
        expertName: '张教授',
        reserveTime: '2024-01-20 14:00',
        status: 0,
        createTime: '2024-01-15 10:00:00'
      }
    ]
  } catch (error) {
    ElMessage.error('加载预约列表失败')
  }
}

// 预约专家
const handleReserve = (expert) => {
  currentExpert.value = expert
  reserveForm.expertUserName = expert.userName
  reserveVisible.value = true
}

// 查看专家详情
const handleViewDetail = (expert) => {
  ElMessage.info('专家详情功能开发中')
}

// 查看问题
const handleViewQuestion = (row) => {
  ElMessage.info('问题详情功能开发中')
}

// 取消预约
const handleCancelReserve = (row) => {
  ElMessageBox.confirm('确定要取消该预约吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      // TODO: 调用后端接口
      // await expertApi.handleReserve(row.reserveId, { status: 2 })
      ElMessage.success('取消成功')
      loadReservationList()
    } catch (error) {
      ElMessage.error('取消失败')
    }
  })
}

// 打开提问对话框
const openQuestionDialog = () => {
  questionVisible.value = true
}

// 提交预约
const handleSubmitReserve = async () => {
  const valid = await reserveFormRef.value?.validate().catch(() => false)
  if (!valid) return

  submitting.value = true
  try {
    // TODO: 调用后端接口
    // await expertApi.reserve(reserveForm)

    ElMessage.success('预约提交成功')
    reserveVisible.value = false
    loadReservationList()
  } catch (error) {
    ElMessage.error('预约提交失败')
  } finally {
    submitting.value = false
  }
}

// 提交问题
const handleSubmitQuestion = async () => {
  const valid = await questionFormRef.value?.validate().catch(() => false)
  if (!valid) return

  submitting.value = true
  try {
    // TODO: 调用后端接口
    // await questionApi.ask(questionForm)

    ElMessage.success('问题提交成功')
    questionVisible.value = false
    loadQuestionList()
  } catch (error) {
    ElMessage.error('问题提交失败')
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  loadExpertList()
  loadQuestionList()
  loadReservationList()
})
</script>

<style scoped>
.action-buttons {
  margin-bottom: 20px;
}

.expert-card {
  margin-bottom: 20px;
  text-align: center;
}

.expert-avatar {
  margin-bottom: 15px;
}

.expert-card h3 {
  margin: 10px 0;
  color: #333;
}

.expert-title {
  color: #4CAF50;
  font-weight: bold;
  margin-bottom: 10px;
}

.expert-specialty {
  color: #666;
  margin-bottom: 10px;
}

.expert-desc {
  color: #999;
  font-size: 13px;
  margin-bottom: 15px;
  min-height: 40px;
}

.expert-actions {
  display: flex;
  justify-content: center;
  gap: 10px;
}
</style>
