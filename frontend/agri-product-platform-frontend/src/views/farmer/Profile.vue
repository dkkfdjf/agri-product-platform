<template>
  <div class="farmer-profile">
    <h2>账户设置</h2>

    <el-tabs v-model="activeTab">
      <!-- 基本信息 -->
      <el-tab-pane label="基本信息" name="basic">
        <el-card>
          <el-form :model="userForm" :rules="userRules" ref="userFormRef" label-width="120px">
            <el-form-item label="账号">
              <el-input v-model="userForm.userName" disabled />
            </el-form-item>
            <el-form-item label="农场名称" prop="farmName">
              <el-input v-model="userForm.farmName" placeholder="请输入农场名称" />
            </el-form-item>
            <el-form-item label="联系人" prop="contact">
              <el-input v-model="userForm.contact" placeholder="请输入联系人姓名" />
            </el-form-item>
            <el-form-item label="联系电话" prop="phone">
              <el-input v-model="userForm.phone" placeholder="请输入联系电话" />
            </el-form-item>
            <el-form-item label="农场地址" prop="address">
              <el-input v-model="userForm.address" placeholder="请输入农场地址" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleUpdateInfo" :loading="updating">
                保存修改
              </el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-tab-pane>

      <!-- 收货地址 -->
      <el-tab-pane label="收货地址" name="address">
        <div class="address-actions">
          <el-button type="primary" @click="openAddressDialog">
            <el-icon><Plus /></el-icon>
            添加地址
          </el-button>
        </div>
        <el-card>
          <el-table :data="addressList" stripe>
            <el-table-column prop="receiver" label="收货人" width="120" />
            <el-table-column prop="phone" label="联系电话" width="130" />
            <el-table-column prop="province" label="省/市/区" width="180">
              <template #default="{ row }">
                {{ row.province }} {{ row.city }} {{ row.district }}
              </template>
            </el-table-column>
            <el-table-column prop="detail" label="详细地址" />
            <el-table-column prop="isDefault" label="默认" width="80">
              <template #default="{ row }">
                <el-tag v-if="row.isDefault" type="success">默认</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="180" fixed="right">
              <template #default="{ row }">
                <el-button type="primary" size="small" link @click="handleEditAddress(row)">
                  编辑
                </el-button>
                <el-button
                  v-if="!row.isDefault"
                  type="success"
                  size="small"
                  link
                  @click="handleSetDefault(row)"
                >
                  设为默认
                </el-button>
                <el-button type="danger" size="small" link @click="handleDeleteAddress(row)">
                  删除
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-tab-pane>

      <!-- 修改密码 -->
      <el-tab-pane label="修改密码" name="password">
        <el-card>
          <el-form :model="passwordForm" :rules="passwordRules" ref="passwordFormRef" label-width="120px">
            <el-form-item label="原密码" prop="oldPassword">
              <el-input v-model="passwordForm.oldPassword" type="password" show-password placeholder="请输入原密码" />
            </el-form-item>
            <el-form-item label="新密码" prop="newPassword">
              <el-input v-model="passwordForm.newPassword" type="password" show-password placeholder="请输入新密码" />
            </el-form-item>
            <el-form-item label="确认密码" prop="confirmPassword">
              <el-input v-model="passwordForm.confirmPassword" type="password" show-password placeholder="请再次输入新密码" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleUpdatePassword" :loading="updating">
                修改密码
              </el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-tab-pane>
    </el-tabs>

    <!-- 地址对话框 -->
    <el-dialog v-model="addressDialogVisible" :title="isEditAddress ? '编辑地址' : '添加地址'" width="500px">
      <el-form :model="addressForm" :rules="addressRules" ref="addressFormRef" label-width="100px">
        <el-form-item label="收货人" prop="receiver">
          <el-input v-model="addressForm.receiver" placeholder="请输入收货人姓名" />
        </el-form-item>
        <el-form-item label="联系电话" prop="phone">
          <el-input v-model="addressForm.phone" placeholder="请输入联系电话" />
        </el-form-item>
        <el-form-item label="所在地区" prop="region">
          <el-cascader
            v-model="addressForm.region"
            :options="regionOptions"
            placeholder="请选择省/市/区"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="详细地址" prop="detail">
          <el-input v-model="addressForm.detail" type="textarea" :rows="2" placeholder="请输入详细地址" />
        </el-form-item>
        <el-form-item label="设为默认">
          <el-switch v-model="addressForm.isDefault" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="addressDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSaveAddress" :loading="updating">
          保存
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
const activeTab = ref('basic')
const updating = ref(false)
const userFormRef = ref(null)
const passwordFormRef = ref(null)
const addressFormRef = ref(null)
const addressDialogVisible = ref(false)
const isEditAddress = ref(false)

// 用户信息表单
const userForm = reactive({
  userName: '',
  farmName: '',
  contact: '',
  phone: '',
  address: ''
})

// 密码表单
const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

// 地址列表
const addressList = ref([])

// 地址表单
const addressForm = reactive({
  id: null,
  receiver: '',
  phone: '',
  region: [],
  province: '',
  city: '',
  district: '',
  detail: '',
  isDefault: false
})

// 表单验证规则
const userRules = {
  farmName: [{ required: true, message: '请输入农场名称', trigger: 'blur' }],
  contact: [{ required: true, message: '请输入联系人姓名', trigger: 'blur' }],
  phone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ],
  address: [{ required: true, message: '请输入农场地址', trigger: 'blur' }]
}

const passwordRules = {
  oldPassword: [{ required: true, message: '请输入原密码', trigger: 'blur' }],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能小于6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入新密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== passwordForm.newPassword) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

const addressRules = {
  receiver: [{ required: true, message: '请输入收货人姓名', trigger: 'blur' }],
  phone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ],
  region: [{ required: true, message: '请选择所在地区', trigger: 'change' }],
  detail: [{ required: true, message: '请输入详细地址', trigger: 'blur' }]
}

// 地区选项（简化版）
const regionOptions = [
  {
    value: '北京市',
    label: '北京市',
    children: [
      {
        value: '朝阳区',
        label: '朝阳区',
        children: [
          { value: '望京街道', label: '望京街道' },
          { value: '三里屯街道', label: '三里屯街道' }
        ]
      }
    ]
  },
  {
    value: '上海市',
    label: '上海市',
    children: [
      {
        value: '浦东新区',
        label: '浦东新区',
        children: [
          { value: '陆家嘴街道', label: '陆家嘴街道' },
          { value: '潍坊新村街道', label: '潍坊新村街道' }
        ]
      }
    ]
  }
]

// 加载用户信息
const loadUserInfo = () => {
  const userInfo = userStore.userInfo
  if (userInfo) {
    Object.assign(userForm, {
      userName: userInfo.userName || '',
      farmName: userInfo.farmName || '',
      contact: userInfo.contact || '',
      phone: userInfo.phone || '',
      address: userInfo.address || ''
    })
  }
}

// 加载地址列表
const loadAddressList = async () => {
  try {
    // TODO: 调用后端接口
    // const res = await addressApi.list()

    // 模拟数据
    addressList.value = [
      {
        id: 1,
        receiver: '张三',
        phone: '13800138000',
        province: '北京市',
        city: '朝阳区',
        district: '望京街道',
        detail: '某某小区1号楼101',
        isDefault: true
      }
    ]
  } catch (error) {
    ElMessage.error('加载地址列表失败')
  }
}

// 更新基本信息
const handleUpdateInfo = async () => {
  const valid = await userFormRef.value?.validate().catch(() => false)
  if (!valid) return

  updating.value = true
  try {
    // TODO: 调用后端接口
    // await userApi.updateInfo(userForm)

    // 更新本地store
    userStore.setUserInfo({ ...userStore.userInfo, ...userForm })

    ElMessage.success('保存成功')
  } catch (error) {
    ElMessage.error('保存失败')
  } finally {
    updating.value = false
  }
}

// 修改密码
const handleUpdatePassword = async () => {
  const valid = await passwordFormRef.value?.validate().catch(() => false)
  if (!valid) return

  updating.value = true
  try {
    // TODO: 调用后端接口
    // await userApi.updatePassword({
    //   oldPassword: passwordForm.oldPassword,
    //   newPassword: passwordForm.newPassword
    // })

    ElMessage.success('密码修改成功，请重新登录')
    userStore.logout()
    window.location.href = '/login'
  } catch (error) {
    ElMessage.error('密码修改失败')
  } finally {
    updating.value = false
  }
}

// 打开地址对话框
const openAddressDialog = () => {
  isEditAddress.value = false
  resetAddressForm()
  addressDialogVisible.value = true
}

// 编辑地址
const handleEditAddress = (row) => {
  isEditAddress.value = true
  Object.assign(addressForm, {
    ...row,
    region: [row.province, row.city, row.district]
  })
  addressDialogVisible.value = true
}

// 删除地址
const handleDeleteAddress = (row) => {
  ElMessageBox.confirm('确定要删除该地址吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      // TODO: 调用后端接口
      // await addressApi.delete(row.id)

      ElMessage.success('删除成功')
      loadAddressList()
    } catch (error) {
      ElMessage.error('删除失败')
    }
  })
}

// 设为默认地址
const handleSetDefault = async (row) => {
  try {
    // TODO: 调用后端接口
    // await addressApi.update(row.id, { isDefault: true })

    ElMessage.success('设置成功')
    loadAddressList()
  } catch (error) {
    ElMessage.error('设置失败')
  }
}

// 保存地址
const handleSaveAddress = async () => {
  const valid = await addressFormRef.value?.validate().catch(() => false)
  if (!valid) return

  const [province, city, district] = addressForm.region
  const data = {
    ...addressForm,
    province,
    city,
    district
  }

  updating.value = true
  try {
    // TODO: 调用后端接口
    // if (isEditAddress.value) {
    //   await addressApi.update(addressForm.id, data)
    // } else {
    //   await addressApi.add(data)
    // }

    ElMessage.success(isEditAddress.value ? '修改成功' : '添加成功')
    addressDialogVisible.value = false
    loadAddressList()
  } catch (error) {
    ElMessage.error('保存失败')
  } finally {
    updating.value = false
  }
}

// 重置地址表单
const resetAddressForm = () => {
  Object.assign(addressForm, {
    id: null,
    receiver: '',
    phone: '',
    region: [],
    province: '',
    city: '',
    district: '',
    detail: '',
    isDefault: false
  })
  addressFormRef.value?.resetFields()
}

onMounted(() => {
  loadUserInfo()
  loadAddressList()
})
</script>

<style scoped>
.address-actions {
  margin-bottom: 20px;
}
</style>
