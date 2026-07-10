package com.agri.platform.service.impl;

import com.agri.platform.dto.OrderRequest;
import com.agri.platform.entity.*;
import com.agri.platform.mapper.*;
import com.agri.platform.service.OrderService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

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
}