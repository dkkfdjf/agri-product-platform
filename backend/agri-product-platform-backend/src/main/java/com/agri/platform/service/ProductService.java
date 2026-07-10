package com.agri.platform.service;

import com.agri.platform.dto.ProductRequest;
import com.agri.platform.entity.Product;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

public interface ProductService {
    void publishProduct(String userName, ProductRequest request);
    Page<Product> getProductPage(int page, int pageSize, String type);
    Product getProductById(Integer orderId);
    void updateProduct(Integer orderId, String userName, ProductRequest request);
    void deleteProduct(Integer orderId, String userName);
    Page<Product> getUserProducts(String userName, int page, int pageSize);
}