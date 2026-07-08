import axios from 'axios'

const api = axios.create({
  baseURL: '/api',
  timeout: 10000
})

// 请求拦截器
api.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 响应拦截器
api.interceptors.response.use(
  response => {
    return response.data
  },
  error => {
    if (error.response?.status === 401) {
      // Token过期，清除本地存储并跳转登录
      localStorage.removeItem('token')
      localStorage.removeItem('userInfo')
      window.location.href = '/login'
    }
    return Promise.reject(error)
  }
)

/**
 * 用户注册
 */
export function register(data) {
  return api.post('/user/register', data)
}

/**
 * 用户登录
 */
export function login(data) {
  return api.post('/user/login', data)
}

/**
 * 获取用户信息
 */
export function getUserInfo() {
  return api.get('/user/info')
}

/**
 * 修改用户信息
 */
export function updateUserInfo(data) {
  return api.put('/user/info', data)
}

/**
 * 修改密码
 */
export function updatePassword(data) {
  return api.put('/user/password', data)
}

export default api
