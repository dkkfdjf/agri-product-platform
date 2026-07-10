/**
 * 商品 API 模块
 */
import request from '@/utils/request'

/**
 * 获取商品列表
 * @param {Object} params - 查询参数 { page, pageSize, category, keyword }
 */
export function getProducts(params) {
  return request({
    url: '/product/list',
    method: 'get',
    params
  })
}

/**
 * 获取商品分页列表
 * @param {Object} params - 查询参数 { page, pageSize, category, keyword }
 */
export function getProductPage(params) {
  return request({
    url: '/product/page',
    method: 'get',
    params
  })
}

/**
 * 获取商品详情
 * @param {String|Number} id - 商品ID
 */
export function getProductDetail(id) {
  return request({
    url: `/product/${id}`,
    method: 'get'
  })
}

/**
 * 发布商品（农户专用）
 * @param {Object} data - 商品信息 { name, description, price, stock, picPath }
 */
export function publishProduct(data) {
  return request({
    url: '/product',
    method: 'post',
    data
  })
}

/**
 * 更新商品信息
 * @param {String|Number} id - 商品ID
 * @param {Object} data - 更新的商品信息
 */
export function updateProduct(id, data) {
  return request({
    url: `/product/${id}`,
    method: 'put',
    data
  })
}

/**
 * 删除商品
 * @param {String|Number} id - 商品ID
 */
export function deleteProduct(id) {
  return request({
    url: `/product/${id}`,
    method: 'delete'
  })
}
