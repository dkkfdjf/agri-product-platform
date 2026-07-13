/**
 * Admin API Module
 * Backend admin panel API endpoints
 */
import request from '@/utils/request'

/**
 * Get platform statistics
 */
export function getAdminStats() {
  return request({
    url: '/admin/stats',
    method: 'get'
  })
}

/**
 * Banner Management APIs
 */
export function getBanners() {
  return request({
    url: '/banner/all',
    method: 'get'
  })
}

export function addBanner(data) {
  return request({
    url: '/banner',
    method: 'post',
    data
  })
}

export function updateBanner(id, data) {
  return request({
    url: `/banner/${id}`,
    method: 'put',
    data
  })
}

export function deleteBanner(id) {
  return request({
    url: `/banner/${id}`,
    method: 'delete'
  })
}

/**
 * User Management APIs
 */
export function getUsers(params) {
  return request({
    url: '/admin/users',
    method: 'get',
    params
  })
}

export function createUser(data) {
  return request({
    url: '/admin/user',
    method: 'post',
    data
  })
}

export function updateUser(userName, data) {
  return request({
    url: `/admin/user/${userName}`,
    method: 'put',
    data
  })
}

export function deleteUser(userName) {
  return request({
    url: `/admin/user/${userName}`,
    method: 'delete'
  })
}

export function updateUserStatus(userName, data) {
  return request({
    url: `/admin/user/${userName}/status`,
    method: 'put',
    data
  })
}

/**
 * Role Application APIs
 */
export function getRoleApplications(params) {
  return request({
    url: '/admin/applications',
    method: 'get',
    params
  })
}

export function reviewRoleApplication(id, data) {
  return request({
    url: `/admin/application/${id}`,
    method: 'put',
    data
  })
}


/**
 * Product Management APIs
 */
export function getAdminProducts(params) {
  return request({
    url: '/admin/products',
    method: 'get',
    params
  })
}

export function updateAdminProduct(orderId, data) {
  return request({
    url: `/product/${orderId}`,
    method: 'put',
    data
  })
}

export function deleteProduct(orderId) {
  return request({
    url: `/product/${orderId}`,
    method: 'delete'
  })
}

/**
 * Finance Management APIs
 */
export function getAdminFinances(params) {
  return request({
    url: '/finance/list',
    method: 'get',
    params
  })
}

export function approveFinance(data) {
  return request({
    url: '/finance/approve',
    method: 'post',
    data
  })
}

/**
 * Expert Management APIs
 */
export function getAdminExperts(params) {
  return request({
    url: '/admin/experts',
    method: 'get',
    params
  })
}

export function createAdminExpert(data) {
  return request({
    url: '/admin/expert',
    method: 'post',
    data
  })
}

export function updateAdminExpert(userName, data) {
  return request({
    url: `/admin/expert/${userName}`,
    method: 'put',
    data
  })
}

export function deleteAdminExpert(userName) {
  return request({
    url: `/admin/expert/${userName}`,
    method: 'delete'
  })
}

