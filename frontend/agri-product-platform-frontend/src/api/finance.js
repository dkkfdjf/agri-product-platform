/**
 * 融资 API 模块
 */
import request from '@/utils/request'

/**
 * 获取融资产品列表
 * @param {Object} params - 查询参数 { page, pageSize }
 */
export function getFinanceProducts(params) {
  return request({
    url: '/finance/product/list',
    method: 'get',
    params
  })
}

/**
 * 获取融资产品详情
 * @param {String|Number} productId - 产品ID
 */
export function getFinanceProductDetail(productId) {
  return request({
    url: `/finance/product/${productId}`,
    method: 'get'
  })
}

/**
 * 申请融资
 * @param {Object} data - 申请数据
 */
export function applyFinance(data) {
  return request({
    url: '/finance/apply',
    method: 'post',
    data
  })
}

/**
 * 获取我的融资申请列表
 * @param {Object} params - 查询参数
 */
export function getMyFinanceApplications(params) {
  return request({
    url: '/finance/apply/list',
    method: 'get',
    params
  })
}

/**
 * 获取融资申请列表（管理员/银行）
 * @param {Object} params - 查询参数
 */
export function getFinanceList(params) {
  return request({
    url: '/finance/list',
    method: 'get',
    params
  })
}

/**
 * 审核融资申请
 * @param {Object} data - 审核数据 { id, status, remark }
 */
export function approveFinance(data) {
  return request({
    url: '/finance/approve',
    method: 'post',
    data
  })
}

/**
 * 获取融资意向匹配列表
 * @param {Object} params - 查询参数
 */
export function getFinanceIntentionMatch(params) {
  return request({
    url: '/finance/intention/match',
    method: 'get',
    params
  })
}
