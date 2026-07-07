<template>
  <div class="login-container">
    <div class="login-box">
      <div class="login-header">
        <h1>农产品融销一体平台</h1>
        <p> Agricultural Product Financing and Trading Platform</p>
      </div>

      <el-form
        ref="loginFormRef"
        :model="loginForm"
        :rules="loginRules"
        class="login-form"
      >
        <el-form-item prop="username">
          <el-input
            v-model="loginForm.username"
            placeholder="请输入账号"
            prefix-icon="User"
            size="large"
          />
        </el-form-item>

        <el-form-item prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="请输入密码"
            prefix-icon="Lock"
            size="large"
            show-password
          />
        </el-form-item>

        <el-form-item prop="role">
          <el-select
            v-model="loginForm.role"
            placeholder="请选择角色"
            size="large"
            style="width: 100%"
          >
            <el-option label="农户" value="farmer" />
            <el-option label="专家" value="expert" />
            <el-option label="银行" value="bank" />
            <el-option label="管理员" value="admin" />
          </el-select>
        </el-form-item>

        <el-form-item>
          <el-button
            type="primary"
            size="large"
            style="width: 100%"
            :loading="loading"
            @click="handleLogin"
          >
            登录
          </el-button>
        </el-form-item>

        <div class="login-tips">
          <p>测试账号：角色代码（如：farmer、expert、bank、admin）</p>
          <p>测试密码：123456</p>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const loginFormRef = ref(null)
const loading = ref(false)

// 表单数据
const loginForm = reactive({
  username: '',
  password: '',
  role: ''
})

// 表单验证规则
const loginRules = {
  username: [
    { required: true, message: '请输入账号', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能小于6位', trigger: 'blur' }
  ],
  role: [
    { required: true, message: '请选择角色', trigger: 'change' }
  ]
}

// 登录处理
const handleLogin = async () => {
  const valid = await loginFormRef.value?.validate().catch(() => false)
  if (!valid) return

  loading.value = true

  try {
    // 模拟登录API调用 - 等待后端接口完成后替换为真实API调用
    // const res = await userApi.login(loginForm)

    // 模拟登录验证
    if (loginForm.password === '123456') {
      // 模拟token和用户信息
      const mockToken = 'mock-token-' + Date.now()
      const mockUserInfo = {
        userName: loginForm.username,
        role: loginForm.role,
        // 根据角色设置不同的用户信息
        ...(loginForm.role === 'farmer' && {
          farmName: '示例农场',
          phone: '13800138000',
          address: '示例地址'
        }),
        ...(loginForm.role === 'expert' && {
          expertName: '示例专家',
          specialty: '农业技术',
          title: '高级农艺师'
        }),
        ...(loginForm.role === 'bank' && {
          bankName: '示例银行',
          branch: '农业贷款部'
        }),
        ...(loginForm.role === 'admin' && {
          adminName: '系统管理员'
        })
      }

      // 保存到store
      userStore.setToken(mockToken)
      userStore.setUserInfo(mockUserInfo)
      userStore.setRole(loginForm.role)

      ElMessage.success('登录成功')

      // 跳转到对应角色的首页或重定向页面
      const redirect = route.query.redirect || getDefaultRoute(loginForm.role)
      router.push(redirect)
    } else {
      ElMessage.error('密码错误，请输入正确的密码')
    }
  } catch (error) {
    ElMessage.error(error.message || '登录失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

// 根据角色获取默认路由
const getDefaultRoute = (role) => {
  const routeMap = {
    farmer: '/farmer/dashboard',
    expert: '/expert/dashboard',
    bank: '/bank/dashboard',
    admin: '/admin/dashboard'
  }
  return routeMap[role] || '/'
}
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #4CAF50 0%, #2E7D32 100%);
}

.login-box {
  width: 420px;
  padding: 40px;
  background: #fff;
  border-radius: 10px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
}

.login-header {
  text-align: center;
  margin-bottom: 30px;
}

.login-header h1 {
  font-size: 26px;
  color: #4CAF50;
  margin-bottom: 10px;
}

.login-header p {
  font-size: 14px;
  color: #999;
}

.login-form {
  margin-top: 20px;
}

.login-tips {
  text-align: center;
  margin-top: 20px;
  padding: 15px;
  background: #f5f7fa;
  border-radius: 4px;
}

.login-tips p {
  margin: 5px 0;
  font-size: 12px;
  color: #666;
}

:deep(.el-form-item) {
  margin-bottom: 22px;
}

:deep(.el-button--primary) {
  background-color: #4CAF50;
  border-color: #4CAF50;
}

:deep(.el-button--primary:hover) {
  background-color: #45a049;
  border-color: #45a049;
}
</style>
