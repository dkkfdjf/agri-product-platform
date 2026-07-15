package com.agri.platform.service.impl;

import com.agri.platform.dto.ProductRequest;
import com.agri.platform.entity.Product;
import com.agri.platform.mapper.ProductMapper;
import com.agri.platform.service.ProductService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductMapper productMapper;

    @Override
    public void publishProduct(String userName, ProductRequest request) {
        // Validate type
        if (request.getType() == null || (!request.getType().equals("goods") && !request.getType().equals("demand"))) {
            throw new RuntimeException("商品类型必须是goods或demand");
        }

        Product product = new Product();
        product.setTitle(request.getTitle());
        product.setPrice(request.getPrice());
        product.setContent(request.getContent());
        product.setPicPath(request.getPicPath());
        product.setType(request.getType());
        product.setOwnName(userName);
        product.setOrderStatus(0); // 默认待交易
        productMapper.insert(product);
    }

    @Override
    public Page<Product> getProductPage(int page, int pageSize, String type) {
        Page<Product> pageParam = new Page<>(page, pageSize);
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        if (type != null && !type.isEmpty()) {
            wrapper.eq(Product::getType, type);
        }
        wrapper.orderByDesc(Product::getCreateTime);
        return productMapper.selectPage(pageParam, wrapper);
    }

    @Override
    public Product getProductById(Integer orderId) {
        return productMapper.selectById(orderId);
    }

    @Override
    public void updateProduct(Integer orderId, String userName, ProductRequest request) {
        Product product = productMapper.selectById(orderId);
        if (product == null) {
            throw new RuntimeException("商品不存在");
        }
        if (!product.getOwnName().equals(userName)) {
            throw new RuntimeException("无权限修改此商品");
        }
        if (request.getTitle() != null) product.setTitle(request.getTitle());
        if (request.getPrice() != null) product.setPrice(request.getPrice());
        if (request.getContent() != null) product.setContent(request.getContent());
        if (request.getPicPath() != null) product.setPicPath(request.getPicPath());
        if (request.getOrderStatus() != null) product.setOrderStatus(request.getOrderStatus());
        productMapper.updateById(product);
    }

    @Override
    public void deleteProduct(Integer orderId, String userName) {
        Product product = productMapper.selectById(orderId);
        if (product == null) {
            throw new RuntimeException("商品不存在");
        }
        if (!product.getOwnName().equals(userName)) {
            throw new RuntimeException("无权限删除此商品");
        }
        productMapper.deleteById(orderId);
    }

    @Override
    public Page<Product> getUserProducts(String userName, int page, int pageSize) {
        Page<Product> pageParam = new Page<>(page, pageSize);
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Product::getOwnName, userName);
        wrapper.orderByDesc(Product::getCreateTime);
        return productMapper.selectPage(pageParam, wrapper);
    }
}