<template>
  <div class="address-container">
    <div class="header">
      <h2>地址管理</h2>
      <el-button type="primary" @click="showAddDialog">
        <el-icon><Plus /></el-icon>
        添加新地址
      </el-button>
    </div>

    <div class="address-list" v-if="addresses.length > 0">
      <el-card v-for="address in addresses" :key="address.addressId" class="address-card">
        <div class="address-content">
          <div class="address-info">
            <div class="name-phone">
              <span class="name">{{ address.realName }}</span>
              <span class="phone">{{ address.phone }}</span>
              <el-tag v-if="address.isDefault" type="success" size="small">默认</el-tag>
            </div>
            <div class="detail">
              {{ address.province }} {{ address.city }} {{ address.area }} {{ address.detail }}
            </div>
          </div>
          <div class="address-actions">
            <el-button link type="primary" @click="editAddress(address)">编辑</el-button>
            <el-button link type="danger" @click="deleteAddress(address)">删除</el-button>
            <el-button
              v-if="!address.isDefault"
              link
              type="warning"
              @click="setDefault(address)"
            >
              设为默认
            </el-button>
          </div>
        </div>
      </el-card>
    </div>

    <el-empty v-else description="暂无地址，请添加" />

    <!-- 添加/编辑地址对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="editingAddress ? '编辑地址' : '添加地址'"
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="姓名" prop="realName">
          <el-input v-model="form.realName" placeholder="请输入收货人姓名" />
        </el-form-item>
        <el-form-item label="电话" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入手机号码" />
        </el-form-item>
        <el-form-item label="地区" prop="region">
          <el-cascader
            v-model="region"
            :options="regionOptions"
            :props="{ expandTrigger: 'hover' }"
            placeholder="请选择省/市/区"
            @change="handleRegionChange"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="详细地址" prop="detail">
          <el-input
            v-model="form.detail"
            type="textarea"
            :rows="3"
            placeholder="请输入详细地址，如街道、楼栋号、门牌号等"
          />
        </el-form-item>
        <el-form-item label="默认地址">
          <el-switch v-model="form.isDefault" />
          <span class="tip">设为默认地址后，下单时将自动选择此地址</span>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="save" :loading="saving">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import {
  getAddressList,
  addAddress,
  updateAddress,
  deleteAddress as deleteAddr,
  setDefaultAddress
} from '@/api/address'

const addresses = ref([])
const dialogVisible = ref(false)
const editingAddress = ref(null)
const saving = ref(false)
const formRef = ref()
const region = ref([])

const form = reactive({
  realName: '',
  phone: '',
  province: '',
  city: '',
  area: '',
  detail: '',
  isDefault: false
})

const validatePhone = (rule, value, callback) => {
  if (!/^1[3-9]\d{9}$/.test(value)) {
    callback(new Error('请输入正确的手机号码'))
  } else {
    callback()
  }
}

const validateRegion = (rule, value, callback) => {
  if (!region.value || region.value.length === 0) {
    callback(new Error('请选择地区'))
  } else {
    callback()
  }
}

const rules = {
  realName: [{ required: true, message: '请输入收货人姓名', trigger: 'blur' }],
  phone: [
    { required: true, message: '请输入手机号码', trigger: 'blur' },
    { validator: validatePhone, trigger: 'blur' }
  ],
  region: [{ required: true, validator: validateRegion, trigger: 'change' }],
  detail: [{ required: true, message: '请输入详细地址', trigger: 'blur' }]
}

// 中国省市区数据（简化版）
const regionOptions = [
  {
    value: '北京市',
    label: '北京市',
    children: [
      {
        value: '北京市',
        label: '北京市',
        children: [
          { value: '东城区', label: '东城区' },
          { value: '西城区', label: '西城区' },
          { value: '朝阳区', label: '朝阳区' },
          { value: '海淀区', label: '海淀区' },
          { value: '丰台区', label: '丰台区' },
          { value: '石景山区', label: '石景山区' },
          { value: '通州区', label: '通州区' },
          { value: '顺义区', label: '顺义区' },
          { value: '大兴区', label: '大兴区' },
          { value: '昌平区', label: '昌平区' }
        ]
      }
    ]
  },
  {
    value: '上海市',
    label: '上海市',
    children: [
      {
        value: '上海市',
        label: '上海市',
        children: [
          { value: '黄浦区', label: '黄浦区' },
          { value: '徐汇区', label: '徐汇区' },
          { value: '长宁区', label: '长宁区' },
          { value: '静安区', label: '静安区' },
          { value: '浦东新区', label: '浦东新区' },
          { value: '普陀区', label: '普陀区' },
          { value: '虹口区', label: '虹口区' },
          { value: '杨浦区', label: '杨浦区' },
          { value: '闵行区', label: '闵行区' },
          { value: '宝山区', label: '宝山区' }
        ]
      }
    ]
  },
  {
    value: '广东省',
    label: '广东省',
    children: [
      {
        value: '广州市',
        label: '广州市',
        children: [
          { value: '荔湾区', label: '荔湾区' },
          { value: '越秀区', label: '越秀区' },
          { value: '海珠区', label: '海珠区' },
          { value: '天河区', label: '天河区' },
          { value: '白云区', label: '白云区' },
          { value: '番禺区', label: '番禺区' }
        ]
      },
      {
        value: '深圳市',
        label: '深圳市',
        children: [
          { value: '罗湖区', label: '罗湖区' },
          { value: '福田区', label: '福田区' },
          { value: '南山区', label: '南山区' },
          { value: '宝安区', label: '宝安区' },
          { value: '龙岗区', label: '龙岗区' },
          { value: '龙华区', label: '龙华区' }
        ]
      }
    ]
  },
  {
    value: '浙江省',
    label: '浙江省',
    children: [
      {
        value: '杭州市',
        label: '杭州市',
        children: [
          { value: '上城区', label: '上城区' },
          { value: '拱墅区', label: '拱墅区' },
          { value: '西湖区', label: '西湖区' },
          { value: '滨江区', label: '滨江区' },
          { value: '萧山区', label: '萧山区' },
          { value: '余杭区', label: '余杭区' }
        ]
      }
    ]
  },
  {
    value: '江苏省',
    label: '江苏省',
    children: [
      {
        value: '南京市',
        label: '南京市',
        children: [
          { value: '玄武区', label: '玄武区' },
          { value: '秦淮区', label: '秦淮区' },
          { value: '建邺区', label: '建邺区' },
          { value: '鼓楼区', label: '鼓楼区' },
          { value: '浦口区', label: '浦口区' },
          { value: '栖霞区', label: '栖霞区' }
        ]
      },
      {
        value: '苏州市',
        label: '苏州市',
        children: [
          { value: '虎丘区', label: '虎丘区' },
          { value: '吴中区', label: '吴中区' },
          { value: '相城区', label: '相城区' },
          { value: '姑苏区', label: '姑苏区' },
          { value: '吴江区', label: '吴江区' }
        ]
      }
    ]
  },
  {
    value: '四川省',
    label: '四川省',
    children: [
      {
        value: '成都市',
        label: '成都市',
        children: [
          { value: '锦江区', label: '锦江区' },
          { value: '青羊区', label: '青羊区' },
          { value: '金牛区', label: '金牛区' },
          { value: '武侯区', label: '武侯区' },
          { value: '成华区', label: '成华区' },
          { value: '龙泉驿区', label: '龙泉驿区' }
        ]
      }
    ]
  }
]

onMounted(() => {
  loadAddresses()
})

const loadAddresses = async () => {
  try {
    const res = await getAddressList()
    addresses.value = res.data || []
  } catch (error) {
    ElMessage.error('加载地址失败')
  }
}

const showAddDialog = () => {
  editingAddress.value = null
  Object.assign(form, {
    realName: '',
    phone: '',
    province: '',
    city: '',
    area: '',
    detail: '',
    isDefault: false
  })
  region.value = []
  dialogVisible.value = true
}

const editAddress = (address) => {
  editingAddress.value = address
  Object.assign(form, {
    realName: address.realName,
    phone: address.phone,
    province: address.province,
    city: address.city,
    area: address.area,
    detail: address.detail,
    isDefault: address.isDefault
  })
  region.value = [address.province, address.city, address.area]
  dialogVisible.value = true
}

const handleRegionChange = (value) => {
  if (value && value.length === 3) {
    form.province = value[0]
    form.city = value[1]
    form.area = value[2]
  }
}

const save = async () => {
  try {
    await formRef.value.validate()
    saving.value = true
    if (editingAddress.value) {
      await updateAddress(editingAddress.value.addressId, form)
      ElMessage.success('更新成功')
    } else {
      await addAddress(form)
      ElMessage.success('添加成功')
    }
    dialogVisible.value = false
    await loadAddresses()
  } catch (error) {
    if (error !== false) {
      ElMessage.error(error.message || '保存失败')
    }
  } finally {
    saving.value = false
  }
}

const deleteAddress = async (address) => {
  try {
    await ElMessageBox.confirm('确定删除此地址吗？', '提示', {
      type: 'warning',
      confirmButtonText: '确定',
      cancelButtonText: '取消'
    })
    await deleteAddr(address.addressId)
    ElMessage.success('删除成功')
    await loadAddresses()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const setDefault = async (address) => {
  try {
    await setDefaultAddress(address.addressId)
    ElMessage.success('设置成功')
    await loadAddresses()
  } catch (error) {
    ElMessage.error('设置失败')
  }
}
</script>

<style scoped>
.address-container {
  max-width: 800px;
  margin: 20px auto;
  padding: 0 20px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.header h2 {
  margin: 0;
  color: #303133;
}

.address-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.address-card {
  margin-bottom: 0;
}

.address-content {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}

.address-info {
  flex: 1;
}

.name-phone {
  display: flex;
  align-items: center;
  gap: 15px;
  margin-bottom: 10px;
}

.name {
  font-weight: bold;
  font-size: 16px;
  color: #303133;
}

.phone {
  color: #606266;
}

.detail {
  color: #909399;
  line-height: 1.5;
}

.address-actions {
  display: flex;
  gap: 10px;
  padding-left: 20px;
  border-left: 1px solid #f0f0f0;
}

.tip {
  margin-left: 10px;
  font-size: 12px;
  color: #909399;
}
</style>
