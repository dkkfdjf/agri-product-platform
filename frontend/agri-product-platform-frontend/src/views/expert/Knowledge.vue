<template>
  <div class="expert-knowledge">
    <div class="page-header">
      <h2>知识管理</h2>
      <el-button type="primary" @click="openPublishDialog">
        <el-icon><Plus /></el-icon>
        发布知识
      </el-button>
    </div>

    <el-card>
      <el-table :data="knowledgeList" stripe>
        <el-table-column prop="knowledgeId" label="知识编号" width="100" />
        <el-table-column prop="title" label="标题" />
        <el-table-column prop="category" label="分类" width="120" />
        <el-table-column prop="views" label="浏览量" width="100" />
        <el-table-column prop="comments" label="评论数" width="100" />
        <el-table-column prop="createTime" label="发布时间" width="180" />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" link @click="handleView(row)">
              查看
            </el-button>
            <el-button type="warning" size="small" link @click="handleEdit(row)">
              编辑
            </el-button>
            <el-button type="danger" size="small" link @click="handleDelete(row)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 发布/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑知识' : '发布知识'"
      width="800px"
    >
      <el-form :model="knowledgeForm" :rules="formRules" ref="formRef" label-width="100px">
        <el-form-item label="标题" prop="title">
          <el-input v-model="knowledgeForm.title" placeholder="请输入标题" />
        </el-form-item>
        <el-form-item label="分类" prop="category">
          <el-select v-model="knowledgeForm.category" placeholder="请选择分类" style="width: 100%">
            <el-option label="种植技术" value="种植技术" />
            <el-option label="病虫害防治" value="病虫害防治" />
            <el-option label="施肥技术" value="施肥技术" />
            <el-option label="灌溉技术" value="灌溉技术" />
            <el-option label="农机使用" value="农机使用" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <el-input
            v-model="knowledgeForm.content"
            type="textarea"
            :rows="10"
            placeholder="请输入内容"
          />
        </el-form-item>
        <el-form-item label="封面图片" prop="cover">
          <el-upload
            v-model:file-list="fileList"
            action="#"
            list-type="picture-card"
            :limit="1"
            :auto-upload="false"
          >
            <el-icon><Plus /></el-icon>
          </el-upload>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">
          {{ isEdit ? '保存' : '发布' }}
        </el-button>
      </template>
    </el-dialog>

    <!-- 查看详情对话框 -->
    <el-dialog v-model="viewVisible" title="知识详情" width="800px">
      <div v-if="currentKnowledge" class="knowledge-detail">
        <h2>{{ currentKnowledge.title }}</h2>
        <div class="meta">
          <el-tag>{{ currentKnowledge.category }}</el-tag>
          <span>发布时间：{{ currentKnowledge.createTime }}</span>
          <span>浏览量：{{ currentKnowledge.views }}</span>
        </div>
        <div class="content">{{ currentKnowledge.content }}</div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const dialogVisible = ref(false)
const viewVisible = ref(false)
const isEdit = ref(false)
const submitting = ref(false)
const formRef = ref(null)
const fileList = ref([])
const knowledgeList = ref([])
const currentKnowledge = ref(null)

// 知识表单
const knowledgeForm = reactive({
  knowledgeId: null,
  title: '',
  category: '',
  content: '',
  cover: ''
})

// 表单验证规则
const formRules = {
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  category: [{ required: true, message: '请选择分类', trigger: 'change' }],
  content: [{ required: true, message: '请输入内容', trigger: 'blur' }]
}

// 加载知识列表
const loadKnowledgeList = async () => {
  try {
    // TODO: 调用后端接口
    // const res = await knowledgeApi.list()

    // 模拟数据
    knowledgeList.value = [
      {
        knowledgeId: 1,
        title: '水稻育苗技术要点',
        category: '种植技术',
        content: '水稻育苗是水稻种植的重要环节，需要注意以下要点...',
        views: 128,
        comments: 15,
        createTime: '2024-01-10 10:00:00'
      },
      {
        knowledgeId: 2,
        title: '常见水稻病虫害防治',
        category: '病虫害防治',
        content: '水稻种植过程中常见的病虫害及其防治方法...',
        views: 256,
        comments: 32,
        createTime: '2024-01-12 14:30:00'
      }
    ]
  } catch (error) {
    ElMessage.error('加载知识列表失败')
  }
}

// 打开发布对话框
const openPublishDialog = () => {
  isEdit.value = false
  resetForm()
  dialogVisible.value = true
}

// 查看详情
const handleView = (row) => {
  currentKnowledge.value = row
  viewVisible.value = true
}

// 编辑知识
const handleEdit = (row) => {
  isEdit.value = true
  Object.assign(knowledgeForm, row)
  dialogVisible.value = true
}

// 删除知识
const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该知识吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      // TODO: 调用后端接口
      // await knowledgeApi.delete(row.knowledgeId)
      ElMessage.success('删除成功')
      loadKnowledgeList()
    } catch (error) {
      ElMessage.error('删除失败')
    }
  })
}

// 提交表单
const handleSubmit = async () => {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  submitting.value = true
  try {
    // TODO: 调用后端接口
    // if (isEdit.value) {
    //   await knowledgeApi.update(knowledgeForm.knowledgeId, knowledgeForm)
    // } else {
    //   await knowledgeApi.publish(knowledgeForm)
    // }

    ElMessage.success(isEdit.value ? '修改成功' : '发布成功')
    dialogVisible.value = false
    loadKnowledgeList()
  } catch (error) {
    ElMessage.error('操作失败')
  } finally {
    submitting.value = false
  }
}

// 重置表单
const resetForm = () => {
  Object.assign(knowledgeForm, {
    knowledgeId: null,
    title: '',
    category: '',
    content: '',
    cover: ''
  })
  fileList.value = []
  formRef.value?.resetFields()
}

onMounted(() => {
  loadKnowledgeList()
})
</script>

<style scoped>
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.knowledge-detail h2 {
  margin-bottom: 15px;
  color: #333;
}

.knowledge-detail .meta {
  margin-bottom: 20px;
  color: #999;
  font-size: 14px;
}

.knowledge-detail .meta span {
  margin-right: 20px;
}

.knowledge-detail .content {
  line-height: 1.8;
  color: #666;
  white-space: pre-wrap;
}
</style>
