<template>
  <div class="buyer-expert">
    <h2>专家咨询</h2>

    <el-row :gutter="20">
      <!-- 左侧：问答列表 -->
      <el-col :span="14">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>我的提问</span>
              <el-button type="primary" size="small" @click="openQuestionDialog">
                <el-icon><Plus /></el-icon>
                提问
              </el-button>
            </div>
          </template>

          <el-empty v-if="questionList.length === 0" description="暂无提问记录" />

          <div v-else class="question-list">
            <div v-for="item in questionList" :key="item.id" class="question-item">
              <div class="question-header">
                <span class="question-title">{{ item.title }}</span>
                <el-tag :type="item.status === 1 ? 'success' : 'warning'" size="small">
                  {{ item.status === 1 ? '已回答' : '未回答' }}
                </el-tag>
              </div>
              <div class="question-content">
                <div class="question-label">问题：</div>
                <div class="question-text">{{ item.question }}</div>
              </div>
              <div v-if="item.answer" class="answer-content">
                <div class="answer-label">回答：</div>
                <div class="answer-text">{{ item.answer }}</div>
                <div class="answer-expert">专家：{{ item.expertName }}</div>
              </div>
              <div class="question-footer">
                <span class="question-time">{{ item.createTime }}</span>
                <el-button
                  v-if="item.status === 0"
                  type="danger"
                  size="small"
                  link
                  @click="handleDeleteQuestion(item)"
                >
                  删除
                </el-button>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>

      <!-- 右侧：预约列表 -->
      <el-col :span="10">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>我的预约</span>
              <el-button type="primary" size="small" @click="openReserveDialog">
                <el-icon><Plus /></el-icon>
                预约
              </el-button>
            </div>
          </template>

          <el-empty v-if="reserveList.length === 0" description="暂无预约记录" />

          <div v-else class="reserve-list">
            <div v-for="item in reserveList" :key="item.id" class="reserve-item">
              <div class="reserve-header">
                <span class="reserve-expert">专家：{{ item.expertName }}</span>
                <el-tag :type="getReserveStatusType(item.status)" size="small">
                  {{ getReserveStatusText(item.status) }}
                </el-tag>
              </div>
              <div class="reserve-content">
                <div class="reserve-row">
                  <span class="label">农作物：</span>
                  <span>{{ item.plantName }}</span>
                </div>
                <div class="reserve-row">
                  <span class="label">面积：</span>
                  <span>{{ item.area }}亩</span>
                </div>
                <div class="reserve-row">
                  <span class="label">地址：</span>
                  <span>{{ item.address }}</span>
                </div>
              </div>
              <div v-if="item.answer" class="reserve-answer">
                <span class="label">回复：</span>
                <span>{{ item.answer }}</span>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 提问对话框 -->
    <el-dialog v-model="questionDialogVisible" title="向专家提问" width="500px">
      <el-form :model="questionForm" :rules="questionRules" ref="questionFormRef" label-width="100px">
        <el-form-item label="专家" prop="expertName">
          <el-select v-model="questionForm.expertName" placeholder="请选择专家" style="width: 100%">
            <el-option
              v-for="expert in expertList"
              :key="expert.userName"
              :label="expert.realName"
              :value="expert.userName"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="农作物" prop="plantName">
          <el-input v-model="questionForm.plantName" placeholder="请输入农作物名称" />
        </el-form-item>
        <el-form-item label="问题标题" prop="title">
          <el-input v-model="questionForm.title" placeholder="请输入问题标题" />
        </el-form-item>
        <el-form-item label="问题内容" prop="question">
          <el-input v-model="questionForm.question" type="textarea" :rows="4" placeholder="请详细描述问题" />
        </el-form-item>
        <el-form-item label="联系电话" prop="phone">
          <el-input v-model="questionForm.phone" placeholder="请输入联系电话" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="questionDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitQuestion" :loading="submitting">
          提交
        </el-button>
      </template>
    </el-dialog>

    <!-- 预约对话框 -->
    <el-dialog v-model="reserveDialogVisible" title="预约专家" width="600px">
      <el-form :model="reserveForm" :rules="reserveRules" ref="reserveFormRef" label-width="100px">
        <el-form-item label="专家" prop="expertName">
          <el-select v-model="reserveForm.expertName" placeholder="请选择专家" style="width: 100%">
            <el-option
              v-for="expert in expertList"
              :key="expert.userName"
              :label="`${expert.realName} - ${expert.profession}`"
              :value="expert.userName"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="农作物" prop="plantName">
          <el-input v-model="reserveForm.plantName" placeholder="请输入农作物名称" />
        </el-form-item>
        <el-form-item label="面积" prop="area">
          <el-input-number v-model="reserveForm.area" :min="1" :max="10000" />
          <span style="margin-left: 10px">亩</span>
        </el-form-item>
        <el-form-item label="地址" prop="address">
          <el-input v-model="reserveForm.address" placeholder="请输入详细地址" />
        </el-form-item>
        <el-form-item label="土壤条件" prop="soilCondition">
          <el-input v-model="reserveForm.soilCondition" placeholder="请描述土壤条件" />
        </el-form-item>
        <el-form-item label="作物条件" prop="plantCondition">
          <el-input v-model="reserveForm.plantCondition" placeholder="请描述作物生长情况" />
        </el-form-item>
        <el-form-item label="作物详情" prop="plantDetail">
          <el-input v-model="reserveForm.plantDetail" type="textarea" :rows="3" placeholder="请详细描述作物情况" />
        </el-form-item>
        <el-form-item label="联系电话" prop="phone">
          <el-input v-model="reserveForm.phone" placeholder="请输入联系电话" />
        </el-form-item>
        <el-form-item label="留言">
          <el-input v-model="reserveForm.message" placeholder="可选填写" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="reserveDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitReserve" :loading="submitting">
          提交预约
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const questionDialogVisible = ref(false)
const reserveDialogVisible = ref(false)
const submitting = ref(false)
const questionFormRef = ref(null)
const reserveFormRef = ref(null)

// 问答列表
const questionList = ref([])

// 预约列表
const reserveList = ref([])

// 专家列表
const expertList = ref([])

// 预约状态映射
const reserveStatusMap = {
  0: { text: '待处理', type: 'warning' },
  1: { text: '已完成', type: 'success' },
  2: { text: '已拒绝', type: 'danger' }
}

// 获取预约状态类型
const getReserveStatusType = (status) => {
  return reserveStatusMap[status]?.type || 'info'
}

// 获取预约状态文本
const getReserveStatusText = (status) => {
  return reserveStatusMap[status]?.text || '未知'
}

// 提问表单
const questionForm = reactive({
  expertName: '',
  plantName: '',
  title: '',
  question: '',
  phone: ''
})

// 预约表单
const reserveForm = reactive({
  expertName: '',
  plantName: '',
  area: 1,
  address: '',
  soilCondition: '',
  plantCondition: '',
  plantDetail: '',
  phone: '',
  message: ''
})

// 表单验证规则
const questionRules = {
  expertName: [{ required: true, message: '请选择专家', trigger: 'change' }],
  plantName: [{ required: true, message: '请输入农作物名称', trigger: 'blur' }],
  title: [{ required: true, message: '请输入问题标题', trigger: 'blur' }],
  question: [{ required: true, message: '请输入问题内容', trigger: 'blur' }],
  phone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ]
}

const reserveRules = {
  expertName: [{ required: true, message: '请选择专家', trigger: 'change' }],
  plantName: [{ required: true, message: '请输入农作物名称', trigger: 'blur' }],
  area: [{ required: true, message: '请输入种植面积', trigger: 'blur' }],
  address: [{ required: true, message: '请输入地址', trigger: 'blur' }],
  soilCondition: [{ required: true, message: '请描述土壤条件', trigger: 'blur' }],
  plantCondition: [{ required: true, message: '请描述作物条件', trigger: 'blur' }],
  plantDetail: [{ required: true, message: '请详细描述作物情况', trigger: 'blur' }],
  phone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ]
}

// 加载问答列表
const loadQuestionList = async () => {
  try {
    // TODO: 调用后端接口
    // const res = await questionApi.myList()

    // 模拟数据
    questionList.value = [
      {
        id: 1,
        expertName: '王专家',
        plantName: '水稻',
        title: '水稻叶片发黄怎么办？',
        question: '近期发现水稻叶片发黄，不知道是什么原因，请教专家。',
        answer: '可能是缺氮或水分过多导致的，建议适当追施氮肥并控制水分。',
        status: 1,
        createTime: '2024-01-15 10:30'
      }
    ]
  } catch (error) {
    ElMessage.error('加载问答列表失败')
  }
}

// 加载预约列表
const loadReserveList = async () => {
  try {
    // TODO: 调用后端接口
    // const res = await reserveApi.myList()

    // 模拟数据
    reserveList.value = [
      {
        id: 1,
        expertName: '李专家',
        plantName: '小麦',
        area: 50,
        address: '某某村',
        soilCondition: '沙壤土',
        plantCondition: '生长良好',
        status: 1,
        answer: '已安排下周三上门指导'
      }
    ]
  } catch (error) {
    ElMessage.error('加载预约列表失败')
  }
}

// 加载专家列表
const loadExpertList = async () => {
  try {
    // TODO: 调用后端接口
    // const res = await expertApi.list()

    // 模拟数据
    expertList.value = [
      { userName: 'expert1', realName: '王专家', profession: '水稻种植专家' },
      { userName: 'expert2', realName: '李专家', profession: '小麦种植专家' },
      { userName: 'expert3', realName: '张专家', profession: '蔬菜种植专家' }
    ]
  } catch (error) {
    ElMessage.error('加载专家列表失败')
  }
}

// 打开提问对话框
const openQuestionDialog = () => {
  resetQuestionForm()
  questionDialogVisible.value = true
}

// 打开预约对话框
const openReserveDialog = () => {
  resetReserveForm()
  reserveDialogVisible.value = true
}

// 提交提问
const handleSubmitQuestion = async () => {
  const valid = await questionFormRef.value?.validate().catch(() => false)
  if (!valid) return

  submitting.value = true
  try {
    // TODO: 调用后端接口
    // await questionApi.add(questionForm)

    ElMessage.success('提问提交成功，专家会尽快回答')
    questionDialogVisible.value = false
    loadQuestionList()
  } catch (error) {
    ElMessage.error('提交失败')
  } finally {
    submitting.value = false
  }
}

// 提交预约
const handleSubmitReserve = async () => {
  const valid = await reserveFormRef.value?.validate().catch(() => false)
  if (!valid) return

  submitting.value = true
  try {
    // TODO: 调用后端接口
    // await reserveApi.add(reserveForm)

    ElMessage.success('预约提交成功，请耐心等待')
    reserveDialogVisible.value = false
    loadReserveList()
  } catch (error) {
    ElMessage.error('提交失败')
  } finally {
    submitting.value = false
  }
}

// 删除提问
const handleDeleteQuestion = (item) => {
  ElMessageBox.confirm('确定要删除该提问吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      // TODO: 调用后端接口
      // await questionApi.delete(item.id)

      questionList.value = questionList.value.filter(q => q.id !== item.id)
      ElMessage.success('删除成功')
    } catch (error) {
      ElMessage.error('删除失败')
    }
  })
}

// 重置提问表单
const resetQuestionForm = () => {
  Object.assign(questionForm, {
    expertName: '',
    plantName: '',
    title: '',
    question: '',
    phone: ''
  })
  questionFormRef.value?.resetFields()
}

// 重置预约表单
const resetReserveForm = () => {
  Object.assign(reserveForm, {
    expertName: '',
    plantName: '',
    area: 1,
    address: '',
    soilCondition: '',
    plantCondition: '',
    plantDetail: '',
    phone: '',
    message: ''
  })
  reserveFormRef.value?.resetFields()
}

onMounted(() => {
  loadQuestionList()
  loadReserveList()
  loadExpertList()
})
</script>

<style scoped>
.buyer-expert {
  padding: 0;
}

.buyer-expert > h2 {
  margin-bottom: 20px;
  color: #333;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.question-list,
.reserve-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.question-item,
.reserve-item {
  padding: 15px;
  border: 1px solid #eee;
  border-radius: 8px;
}

.question-header,
.reserve-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.question-title,
.reserve-expert {
  font-size: 16px;
  font-weight: bold;
  color: #333;
}

.question-content,
.answer-content,
.reserve-content {
  margin: 10px 0;
}

.question-label,
.answer-label,
.reserve-content .label {
  font-weight: bold;
  color: #666;
  margin-right: 5px;
}

.question-text,
.answer-text {
  color: #333;
  line-height: 1.6;
}

.answer-expert {
  margin-top: 5px;
  font-size: 14px;
  color: #999;
}

.question-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 10px;
  padding-top: 10px;
  border-top: 1px solid #eee;
}

.question-time {
  font-size: 12px;
  color: #999;
}

.reserve-content .reserve-row {
  display: flex;
  margin: 5px 0;
}

.reserve-content .label {
  width: 80px;
  flex-shrink: 0;
}

.reserve-answer {
  margin-top: 10px;
  padding: 10px;
  background: #f5f7fa;
  border-radius: 4px;
}

.reserve-answer .label {
  font-weight: bold;
  color: #666;
}
</style>
