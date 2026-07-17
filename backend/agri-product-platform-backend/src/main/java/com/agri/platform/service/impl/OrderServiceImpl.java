package com.agri.platform.service.impl;

import com.agri.platform.dto.OrderRequest;
import com.agri.platform.dto.OrderResponse;
import com.agri.platform.entity.*;
import com.agri.platform.mapper.*;
import com.agri.platform.service.OrderService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private PurchaseMapper purchaseMapper;

    @Autowired
    private PurchaseDetailMapper detailMapper;

    @Autowired
    private ShoppingCartMapper cartMapper;

    @Autowired
    private ProductMapper productMapper;

    // 订单状态文本映射
    private static final Map<Integer, String> STATUS_TEXT_MAP = Map.of(
        1, "待付款",
        2, "待发货",
        3, "待收货",
        4, "已完成",
        5, "已取消"
    );

    @Override
    @Transactional
    public Integer submitOrder(String userName, OrderRequest request) {
        // 创建订单主记录
        Purchase purchase = new Purchase();
        purchase.setOwnName(userName);
        purchase.setAddress(request.getAddress());
        purchase.setPurchaseType(request.getPurchaseType());
        purchase.setPurchaseStatus(1); // 待付款
        purchaseMapper.insert(purchase);

        BigDecimal totalPrice = BigDecimal.ZERO;

        if (request.getPurchaseType() == 1) {
            // 从购物车获取
            List<ShoppingCart> cartItems = cartMapper.selectList(
                new LambdaQueryWrapper<ShoppingCart>()
                    .eq(ShoppingCart::getOwnName, userName)
            );

            for (ShoppingCart cart : cartItems) {
                Product product = productMapper.selectById(cart.getOrderId());
                if (product == null) continue;

                BigDecimal lineTotal = product.getPrice().multiply(new BigDecimal(cart.getCount()));
                totalPrice = totalPrice.add(lineTotal);

                // 创建订单详情
                PurchaseDetail detail = new PurchaseDetail();
                detail.setPurchaseId(purchase.getPurchaseId());
                detail.setOrderId(cart.getOrderId());
                detail.setUninPrice(product.getPrice());
                detail.setCount(cart.getCount());
                detail.setSumPrice(lineTotal);
                detailMapper.insert(detail);

                // 清空购物车
                cartMapper.deleteById(cart.getShoppingId());
            }
        } else {
            // 直接购买
            for (OrderRequest.OrderItem item : request.getItems()) {
                Product product = productMapper.selectById(item.getOrderId());
                if (product == null) continue;

                BigDecimal lineTotal = product.getPrice().multiply(new BigDecimal(item.getCount()));
                totalPrice = totalPrice.add(lineTotal);

                // 创建订单详情
                PurchaseDetail detail = new PurchaseDetail();
                detail.setPurchaseId(purchase.getPurchaseId());
                detail.setOrderId(item.getOrderId());
                detail.setUninPrice(product.getPrice());
                detail.setCount(item.getCount());
                detail.setSumPrice(lineTotal);
                detailMapper.insert(detail);
            }
        }

        // 更新订单总价
        purchase.setTotalPrice(totalPrice);
        purchaseMapper.updateById(purchase);

        return purchase.getPurchaseId();
    }

    @Override
    public Page<Purchase> getOrderList(String userName, int page, int pageSize) {
        Page<Purchase> pageParam = new Page<>(page, pageSize);
        LambdaQueryWrapper<Purchase> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Purchase::getOwnName, userName);
        wrapper.orderByDesc(Purchase::getCreateTime);
        return purchaseMapper.selectPage(pageParam, wrapper);
    }

    @Override
    public Purchase getOrderDetail(Integer purchaseId, String userName) {
        Purchase purchase = purchaseMapper.selectById(purchaseId);
        if (purchase == null) {
            throw new RuntimeException("订单不存在");
        }
        if (!purchase.getOwnName().equals(userName)) {
            throw new RuntimeException("无权限查看此订单");
        }
        return purchase;
    }

    @Override
    public void markOrderPaid(Integer purchaseId) {
        Purchase purchase = purchaseMapper.selectById(purchaseId);
        if (purchase == null) {
            return; // 订单不存在，忽略（幂等）
        }
        // 仅待付款订单推进，避免重复回调把已发货/已完成订单改回去
        if (purchase.getPurchaseStatus() != null && purchase.getPurchaseStatus() == 1) {
            purchase.setPurchaseStatus(2); // 待发货
            purchaseMapper.updateById(purchase);
        }
    }

    @Override
    public void updateOrderStatus(Integer purchaseId, Integer status) {
        Purchase purchase = purchaseMapper.selectById(purchaseId);
        if (purchase == null) {
            throw new RuntimeException("订单不存在");
        }
        purchase.setPurchaseStatus(status);
        purchaseMapper.updateById(purchase);
    }

    @Override
    public void cancelOrder(Integer purchaseId, String userName) {
        Purchase purchase = purchaseMapper.selectById(purchaseId);
        if (purchase == null) {
            throw new RuntimeException("订单不存在");
        }
        if (!purchase.getOwnName().equals(userName)) {
            throw new RuntimeException("无权限取消此订单");
        }
        if (purchase.getPurchaseStatus() != 1) {
            throw new RuntimeException("只有待付款订单可以取消");
        }
        purchase.setPurchaseStatus(5); // 已取消
        purchaseMapper.updateById(purchase);
    }

    @Override
    public List<OrderResponse> getOrderListWithDetails(String userName, int page, int pageSize) {
        // 获取订单列表
        Page<Purchase> pageParam = new Page<>(page, pageSize);
        LambdaQueryWrapper<Purchase> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Purchase::getOwnName, userName);
        wrapper.orderByDesc(Purchase::getCreateTime);
        Page<Purchase> purchasePage = purchaseMapper.selectPage(pageParam, wrapper);

        // 获取所有订单ID
        List<Integer> purchaseIds = purchasePage.getRecords().stream()
                .map(Purchase::getPurchaseId)
                .collect(Collectors.toList());

        if (purchaseIds.isEmpty()) {
            return new ArrayList<>();
        }

        // 获取订单详情
        LambdaQueryWrapper<PurchaseDetail> detailWrapper = new LambdaQueryWrapper<>();
        detailWrapper.in(PurchaseDetail::getPurchaseId, purchaseIds);
        List<PurchaseDetail> details = detailMapper.selectList(detailWrapper);

        // 获取商品信息
        List<Integer> productIds = details.stream()
                .map(PurchaseDetail::getOrderId)
                .distinct()
                .collect(Collectors.toList());

        LambdaQueryWrapper<Product> productWrapper = new LambdaQueryWrapper<>();
        productWrapper.in(Product::getOrderId, productIds);
        List<Product> products = productMapper.selectList(productWrapper);

        Map<Integer, Product> productMap = products.stream()
                .collect(Collectors.toMap(Product::getOrderId, p -> p));

        // 按订单ID分组详情
        Map<Integer, List<PurchaseDetail>> detailMap = details.stream()
                .collect(Collectors.groupingBy(PurchaseDetail::getPurchaseId));

        // 构建响应
        List<OrderResponse> responses = new ArrayList<>();
        for (Purchase purchase : purchasePage.getRecords()) {
            OrderResponse response = new OrderResponse();
            response.setPurchaseId(purchase.getPurchaseId());
            response.setOwnName(purchase.getOwnName());
            response.setTotalPrice(purchase.getTotalPrice());
            response.setAddress(purchase.getAddress());
            response.setPurchaseStatus(purchase.getPurchaseStatus());
            response.setStatusText(STATUS_TEXT_MAP.getOrDefault(purchase.getPurchaseStatus(), "未知"));
            response.setPurchaseType(purchase.getPurchaseType());
            response.setCreateTime(purchase.getCreateTime());
            response.setUpdateTime(purchase.getUpdateTime());

            // 添加商品详情
            List<PurchaseDetail> orderDetails = detailMap.getOrDefault(purchase.getPurchaseId(), new ArrayList<>());
            List<OrderResponse.OrderItemResponse> items = new ArrayList<>();
            for (PurchaseDetail detail : orderDetails) {
                OrderResponse.OrderItemResponse item = new OrderResponse.OrderItemResponse();
                Product product = productMap.get(detail.getOrderId());
                if (product != null) {
                    item.setProductId(product.getOrderId());
                    item.setProductName(product.getTitle());
                    item.setProductPic(product.getPicPath());
                    item.setPrice(detail.getUninPrice());
                    item.setCount(detail.getCount());
                    item.setTotalPrice(detail.getSumPrice());
                }
                items.add(item);
            }
            response.setItems(items);
            responses.add(response);
        }

        return responses;
    }
}