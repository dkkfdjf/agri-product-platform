package com.agri.platform.service;

import com.agri.platform.dto.OrderRequest;
import com.agri.platform.entity.Purchase;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

public interface OrderService {
    Integer submitOrder(String userName, OrderRequest request);
    Page<Purchase> getOrderList(String userName, int page, int pageSize);
    Purchase getOrderDetail(Integer purchaseId, String userName);
    void updateOrderStatus(Integer purchaseId, Integer status);
    void cancelOrder(Integer purchaseId, String userName);
}