/**
 * Axios请求配置
 */

import axios from 'axios'

// 创建axios实例
const request = axios.create({
  baseURL: '/api', // 后端接口基础路径，需要后端配置好后修改
  timeout: 10000
})

// 请求拦截器
request.interceptors.request.use(
  config => {
    // 从localStorage获取token
    const token = localStorage.getItem('token')
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`
    }
    return config
  },
  error => {
    console.error('请求错误:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
request.interceptors.response.use(
  response => {
    const res = response.data
    // 根据后端返回的code进行判断
    if (res.code !== 200) {
      // 处理错误
      console.error('接口错误:', res.message)
      return Promise.reject(new Error(res.message || 'Error'))
    }
    return res
  },
  error => {
    console.error('响应错误:', error)
    return Promise.reject(error)
  }
)

export default request
