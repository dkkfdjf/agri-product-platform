/**
 * 地址 API 模块
 */
import request from '@/utils/request'

/**
 * 获取地址列表
 */
export function getAddressList() {
  return request({
    url: '/address/list',
    method: 'get'
  })
}

/**
 * 添加地址
 * @param {Object} data - 地址信息
 */
export function addAddress(data) {
  return request({
    url: '/address',
    method: 'post',
    data
  })
}

/**
 * 更新地址
 * @param {String|Number} id - 地址ID
 * @param {Object} data - 地址信息
 */
export function updateAddress(id, data) {
  return request({
    url: `/address/${id}`,
    method: 'put',
    data
  })
}

/**
 * 删除地址
 * @param {String|Number} id - 地址ID
 */
export function deleteAddress(id) {
  return request({
    url: `/address/${id}`,
    method: 'delete'
  })
}

/**
 * 设置默认地址
 * @param {String|Number} id - 地址ID
 */
export function setDefaultAddress(id) {
  return request({
    url: `/address/${id}/default`,
    method: 'put'
  })
}
