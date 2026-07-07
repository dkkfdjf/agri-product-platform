<template>
  <div class="test-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>后端连接测试</span>
        </div>
      </template>

      <el-space direction="vertical" :size="20" style="width: 100%">
        <div class="test-item">
          <el-button type="primary" @click="testHello" :loading="loading">
            测试后端连接
          </el-button>
          <el-button @click="testInfo" :loading="loading">
            获取项目信息
          </el-button>
        </div>

        <el-alert
          v-if="result"
          :title="result.message"
          :type="result.code === 200 ? 'success' : 'error'"
          show-icon
        >
          <template #default>
            <pre>{{ JSON.stringify(result.data, null, 2) }}</pre>
          </template>
        </el-alert>

        <el-divider />

        <div class="status-info">
          <el-descriptions title="项目状态" :column="2" border>
            <el-descriptions-item label="前端地址">
              <el-tag type="success">http://localhost:3000</el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="后端地址">
              <el-tag type="success">http://localhost:8080/api</el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="Vue版本">
              <el-tag>Vue 3</el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="UI组件">
              <el-tag>Element Plus</el-tag>
            </el-descriptions-item>
          </el-descriptions>
        </div>
      </el-space>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import axios from 'axios'

const loading = ref(false)
const result = ref(null)

const testHello = async () => {
  loading.value = true
  try {
    const res = await axios.get('/api/test/hello')
    result.value = res.data
  } catch (error) {
    result.value = {
      code: 500,
      message: '连接失败：' + error.message
    }
  } finally {
    loading.value = false
  }
}

const testInfo = async () => {
  loading.value = true
  try {
    const res = await axios.get('/api/test/info')
    result.value = res.data
  } catch (error) {
    result.value = {
      code: 500,
      message: '连接失败：' + error.message
    }
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.test-page {
  max-width: 800px;
  margin: 20px auto;
  padding: 0 20px;
}

.test-item {
  display: flex;
  gap: 10px;
}

pre {
  background: #f5f5f5;
  padding: 10px;
  border-radius: 4px;
  overflow-x: auto;
}

.card-header {
  font-size: 18px;
  font-weight: bold;
}

.status-info {
  margin-top: 20px;
}
</style>
