<template>
  <div class="question-ask-page">
    <div class="container">
      <!-- 页面标题 -->
      <div class="page-header">
        <h2 class="page-title">向专家提问</h2>
        <p class="page-subtitle">描述您的农业问题，专家将为您解答</p>
      </div>

      <!-- 提问表单 -->
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="100px"
        class="ask-form"
      >
        <!-- 问题标题 -->
        <el-form-item label="问题标题" prop="title">
          <el-input
            v-model="formData.title"
            placeholder="请简要描述您的问题，如：小麦叶片发黄是什么原因？"
            maxlength="100"
            show-word-limit
          />
        </el-form-item>

        <!-- 问题分类 -->
        <el-form-item label="问题分类" prop="category">
          <el-select v-model="formData.category" placeholder="请选择问题分类">
            <el-option
              v-for="cat in categories"
              :key="cat.value"
              :label="cat.label"
              :value="cat.value"
            />
          </el-select>
        </el-form-item>

        <!-- 问题详情 -->
        <el-form-item label="问题描述" prop="content">
          <el-input
            v-model="formData.content"
            type="textarea"
            :rows="10"
            placeholder="请详细描述您的问题，包括：&#10;1. 具体情况（种植什么作物、面积、生长阶段等）&#10;2. 出现的问题（症状、发生时间等）&#10;3. 已尝试的解决方法&#10;4. 其他相关信息"
            maxlength="2000"
            show-word-limit
          />
        </el-form-item>

        <!-- 图片上传（可选） -->
        <el-form-item label="相关图片">
          <el-upload
            class="image-uploader"
            :show-file-list="true"
            :file-list="imageList"
            :on-preview="handlePicturePreview"
            :on-success="handleImageSuccess"
            :on-remove="handleImageRemove"
            :before-upload="beforeImageUpload"
            list-type="picture-card"
            action="/api/upload"
            :limit="3"
          >
            <el-icon><Plus /></el-icon>
          </el-upload>
          <div class="upload-tip">最多上传3张图片，帮助专家更好地理解您的问题</div>
        </el-form-item>

        <!-- 提交按钮 -->
        <el-form-item>
          <el-button @click="handleBack">取消</el-button>
          <el-button type="primary" @click="handleSubmit" :loading="submitting">
            提交问题
          </el-button>
        </el-form-item>
      </el-form>

      <!-- 提示信息 -->
      <el-card class="tips-card">
        <template #header>
          <div class="tips-header">
            <el-icon><InfoFilled /></el-icon>
            提问须知
          </div>
        </template>
        <ul class="tips-list">
          <li>请详细描述您的问题，以便专家给出准确的解答</li>
          <li>上传相关照片可以更好地帮助专家诊断问题</li>
          <li>专家将在24小时内回复您的问题</li>
          <li>提问后可在"我的问题"中查看回答状态</li>
          <li>请勿重复提交相同的问题</li>
        </ul>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'
import { Plus, InfoFilled } from '@element-plus/icons-vue'
import { askQuestion } from '@/api/question'

const router = useRouter()
const userStore = useUserStore()

const formRef = ref(null)
const submitting = ref(false)
const imageList = ref([])

// 表单数据
const formData = reactive({
  title: '',
  category: '',
  content: '',
  images: []
})

// 分类选项
const categories = [
  { value: 'grain', label: '粮食作物' },
  { value: 'vegetable', label: '蔬菜种植' },
  { value: 'fruit', label: '果树栽培' },
  { value: 'soil', label: '土壤肥料' },
  { value: 'pest', label: '病虫害防治' },
  { value: 'technology', label: '农业技术' },
  { value: 'breeding', label: '养殖问题' },
  { value: 'other', label: '其他问题' }
]

// 表单验证规则
const formRules = {
  title: [
    { required: true, message: '请输入问题标题', trigger: 'blur' },
    { min: 5, max: 100, message: '标题长度应在5-100个字符之间', trigger: 'blur' }
  ],
  category: [
    { required: true, message: '请选择问题分类', trigger: 'change' }
  ],
  content: [
    { required: true, message: '请输入问题描述', trigger: 'blur' },
    { min: 20, max: 2000, message: '描述长度应在20-2000个字符之间', trigger: 'blur' }
  ]
}

// 图片上传前校验
const beforeImageUpload = (rawFile) => {
  const isImage = ['image/jpeg', 'image/png', 'image/jpg'].includes(rawFile.type)
  const isLt5M = rawFile.size / 1024 / 1024 < 5

  if (!isImage) {
    ElMessage.error('只能上传JPG/PNG格式的图片')
    return false
  }
  if (!isLt5M) {
    ElMessage.error('图片大小不能超过5MB')
    return false
  }
  return true
}

// 图片上传成功
const handleImageSuccess = (response) => {
  if (response.code === 200) {
    formData.images.push(response.data.url)
    ElMessage.success('图片上传成功')
  } else {
    ElMessage.error('图片上传失败')
  }
}

// 删除图片
const handleImageRemove = (file) => {
  const index = formData.images.indexOf(file.url)
  if (index > -1) {
    formData.images.splice(index, 1)
  }
}

// 预览图片
const handlePicturePreview = (file) => {
  window.open(file.url, '_blank')
}

// 返回
const handleBack = () => {
  router.back()
}

// 提交表单
const handleSubmit = async () => {
  try {
    await formRef.value.validate()

    submitting.value = true

    const submitData = {
      title: formData.title,
      category: formData.category,
      content: formData.content,
      images: formData.images.join(',')
    }

    await askQuestion(submitData)
    ElMessage.success('提问成功，专家将尽快为您解答')
    router.push('/question')
  } catch (error) {
    if (error !== false) {
      ElMessage.error(error.message || '提交失败')
    }
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  // 检查登录
  if (!userStore.isLoggedIn()) {
    ElMessage.info('请先登录后提问')
    router.push({ path: '/login', query: { redirect: '/question/ask' } })
  }
})
</script>

<style scoped>
.question-ask-page {
  min-height: 100vh;
  background: var(--color-bg-page, #f5f3ef);
  padding: var(--spacing-6, 24px) 0;
}

.container {
  max-width: 800px;
  margin: 0 auto;
  padding: 0 var(--spacing-6, 24px);
  display: flex;
  flex-direction: column;
  gap: var(--spacing-6, 24px);
}

.page-header {
  text-align: center;
}

.page-title {
  font-family: var(--font-family-display, 'Noto Serif SC', serif);
  font-size: var(--font-size-3xl, 38px);
  font-weight: var(--font-weight-bold, 700);
  color: var(--color-text-primary, #1f2923);
  margin: 0 0 var(--spacing-2, 8px);
}

.page-subtitle {
  font-size: var(--font-size-base, 16px);
  color: var(--color-text-tertiary, #6b7280);
  margin: 0;
}

.ask-form {
  background: var(--color-bg-primary, #ffffff);
  border-radius: var(--radius-lg, 12px);
  padding: var(--spacing-8, 32px);
  box-shadow: var(--shadow-sm, 0 2px 8px rgba(0,0,0,0.05));
}

.ask-form :deep(.el-form-item__label) {
  font-weight: var(--font-weight-medium, 500);
  color: var(--color-text-primary, #1f2923);
}

.ask-form :deep(.el-input__inner),
.ask-form :deep(.el-textarea__inner) {
  font-size: var(--font-size-base, 16px);
}

.ask-form :deep(.el-textarea__inner) {
  line-height: 1.6;
}

/* ===== 图片上传 ===== */
.image-uploader {
  width: 100%;
}

.upload-tip {
  margin-top: var(--spacing-2, 8px);
  font-size: var(--font-size-xs, 12px);
  color: var(--color-text-tertiary, #6b7280);
}

/* ===== 提示卡片 ===== */
.tips-card {
  background: linear-gradient(135deg, #f0f9ff 0%, #e6f4ea 100%);
  border: 1px solid #d4e7f0;
}

.tips-header {
  display: flex;
  align-items: center;
  gap: var(--spacing-2, 8px);
  font-weight: var(--font-weight-semibold, 600);
  color: var(--color-primary, #2d5a3d);
}

.tips-list {
  margin: 0;
  padding-left: var(--spacing-5, 20px);
  color: var(--color-text-secondary, #4a5249);
  line-height: 1.8;
}

.tips-list li {
  margin-bottom: var(--spacing-2, 8px);
}

/* ===== 响应式 ===== */
@media (max-width: 768px) {
  .ask-form {
    padding: var(--spacing-5, 20px);
  }

  .ask-form :deep(.el-form-item__label) {
    width: 80px !important;
  }
}
</style>
