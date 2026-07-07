<template>
  <div class="expert-questions">
    <div class="page-header">
      <h2>问答管理</h2>
    </div>

    <el-tabs v-model="activeTab">
      <!-- 待回答 -->
      <el-tab-pane label="待回答" name="pending">
        <el-card>
          <el-table :data="pendingList" stripe>
            <el-table-column prop="questionId" label="问题编号" width="100" />
            <el-table-column prop="title" label="问题标题" />
            <el-table-column prop="farmerName" label="提问人" width="120" />
            <el-table-column prop="createTime" label="提问时间" width="180" />
            <el-table-column label="操作" width="150" fixed="right">
              <template #default="{ row }">
                <el-button type="primary" size="small" link @click="handleAnswer(row)">
                  回答
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-tab-pane>

      <!-- 已回答 -->
      <el-tab-pane label="已回答" name="answered">
        <el-card>
          <el-table :data="answeredList" stripe>
            <el-table-column prop="questionId" label="问题编号" width="100" />
            <el-table-column prop="title" label="问题标题" />
            <el-table-column prop="farmerName" label="提问人" width="120" />
            <el-table-column prop="answerTime" label="回答时间" width="180" />
            <el-table-column label="操作" width="150" fixed="right">
              <template #default="{ row }">
                <el-button type="primary" size="small" link @click="handleView(row)">
                  查看
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-tab-pane>
    </el-tabs>

    <!-- 回答对话框 -->
    <el-dialog v-model="answerVisible" title="回答问题" width="600px">
      <div v-if="currentQuestion" class="question-info">
        <el-descriptions title="问题信息" :column="2" border>
          <el-descriptions-item label="问题标题">{{ currentQuestion.title }}</el-descriptions-item>
          <el-descriptions-item label="提问人">{{ currentQuestion.farmerName }}</el-descriptions-item>
          <el-descriptions-item label="提问时间" span="2">{{ currentQuestion.createTime }}</el-descriptions-item>
          <el-descriptions-item label="问题内容" span="2">{{ currentQuestion.content }}</el-descriptions-item>
        </el-descriptions>
      </div>
      <el-form :model="answerForm" :rules="answerRules" ref="answerFormRef" label-width="100px" style="margin-top: 20px">
        <el-form-item label="回答内容" prop="answer">
          <el-input v-model="answerForm.answer" type="textarea" :rows="6" placeholder="请输入您的回答" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="answerVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitAnswer" :loading="submitting">
          提交回答
        </el-button>
      </template>
    </el-dialog>

    <!-- 查看详情对话框 -->
    <el-dialog v-model="viewVisible" title="问答详情" width="600px">
      <div v-if="currentQuestion" class="answer-detail">
        <el-descriptions title="问题信息" :column="2" border>
          <el-descriptions-item label="问题标题">{{ currentQuestion.title }}</el-descriptions-item>
          <el-descriptions-item label="提问人">{{ currentQuestion.farmerName }}</el-descriptions-item>
          <el-descriptions-item label="提问时间">{{ currentQuestion.createTime }}</el-descriptions-item>
          <el-descriptions-item label="回答时间">{{ currentQuestion.answerTime }}</el-descriptions-item>
          <el-descriptions-item label="问题内容" span="2">{{ currentQuestion.content }}</el-descriptions-item>
          <el-descriptions-item label="回答内容" span="2">{{ currentQuestion.answer }}</el-descriptions-item>
        </el-descriptions>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'

const activeTab = ref('pending')
const pendingList = ref([])
const answeredList = ref([])
const answerVisible = ref(false)
const viewVisible = ref(false)
const submitting = ref(false)
const answerFormRef = ref(null)
const currentQuestion = ref(null)

// 回答表单
const answerForm = reactive({
  answer: ''
})

// 表单验证规则
const answerRules = {
  answer: [{ required: true, message: '请输入回答内容', trigger: 'blur' }]
}

// 加载待回答列表
const loadPendingList = async () => {
  try {
    // TODO: 调用后端接口
    // const res = await questionApi.list({ status: 0 })

    // 模拟数据
    pendingList.value = [
      {
        questionId: 1,
        title: '水稻育苗期需要注意什么？',
        content: '请问水稻育苗期间需要注意哪些事项？',
        farmerName: '张三',
        createTime: '2024-01-15 10:00:00'
      },
      {
        questionId: 2,
        title: '如何防治水稻稻飞虱？',
        content: '我的水稻田里发现了稻飞虱，应该怎么防治？',
        farmerName: '李四',
        createTime: '2024-01-14 14:30:00'
      }
    ]
  } catch (error) {
    ElMessage.error('加载待回答列表失败')
  }
}

// 加载已回答列表
const loadAnsweredList = async () => {
  try {
    // TODO: 调用后端接口
    // const res = await questionApi.list({ status: 1 })

    // 模拟数据
    answeredList.value = [
      {
        questionId: 3,
        title: '小麦施肥的最佳时间？',
        content: '请问小麦在什么时候施肥效果最好？',
        farmerName: '王五',
        answer: '小麦施肥的最佳时间是在分蘖期和拔节期...',
        answerTime: '2024-01-13 16:00:00',
        createTime: '2024-01-12 09:00:00'
      }
    ]
  } catch (error) {
    ElMessage.error('加载已回答列表失败')
  }
}

// 回答问题
const handleAnswer = (row) => {
  currentQuestion.value = row
  answerForm.answer = ''
  answerVisible.value = true
}

// 查看详情
const handleView = (row) => {
  currentQuestion.value = row
  viewVisible.value = true
}

// 提交回答
const handleSubmitAnswer = async () => {
  const valid = await answerFormRef.value?.validate().catch(() => false)
  if (!valid) return

  submitting.value = true
  try {
    // TODO: 调用后端接口
    // await questionApi.answer(currentQuestion.value.questionId, {
    //   answer: answerForm.answer
    // })

    ElMessage.success('回答提交成功')
    answerVisible.value = false
    loadPendingList()
    loadAnsweredList()
  } catch (error) {
    ElMessage.error('回答提交失败')
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  loadPendingList()
  loadAnsweredList()
})
</script>

<style scoped>
.page-header {
  margin-bottom: 20px;
}

.question-info {
  margin-bottom: 20px;
}
</style>
