package com.agri.platform.service.impl;

import com.agri.platform.dto.ProductRequest;
import com.agri.platform.entity.Product;
import com.agri.platform.entity.User;
import com.agri.platform.mapper.ProductMapper;
import com.agri.platform.mapper.UserMapper;
import com.agri.platform.service.ProductService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    public void publishProduct(String userName, ProductRequest request) {
        // Validate type
        if (request.getType() == null || (!request.getType().equals("goods") && !request.getType().equals("demand"))) {
            throw new RuntimeException("商品类型必须是goods或demand");
        }

        // 获取用户真实姓名
        User user = userMapper.selectById(userName);
        String publisherName = user != null && user.getRealName() != null ? user.getRealName() : userName;

        Product product = new Product();
        product.setTitle(request.getTitle());
        product.setPrice(request.getPrice());
        product.setContent(request.getContent());
        product.setPicPath(request.getPicPath());
        product.setType(request.getType());
        product.setOwnName(publisherName);
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

        // 获取用户真实姓名进行权限验证
        User user = userMapper.selectById(userName);
        String userRealName = user != null && user.getRealName() != null ? user.getRealName() : userName;

        if (!product.getOwnName().equals(userRealName)) {
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

        // 获取用户真实姓名进行权限验证
        User user = userMapper.selectById(userName);
        String userRealName = user != null && user.getRealName() != null ? user.getRealName() : userName;

        if (!product.getOwnName().equals(userRealName)) {
            throw new RuntimeException("无权限删除此商品");
        }
        productMapper.deleteById(orderId);
    }

    @Override
    public Page<Product> getUserProducts(String userName, int page, int pageSize, String type) {
        // 获取用户真实姓名进行查询
        User user = userMapper.selectById(userName);
        String userRealName = user != null && user.getRealName() != null ? user.getRealName() : userName;

        Page<Product> pageParam = new Page<>(page, pageSize);
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Product::getOwnName, userRealName);
        // 按类型过滤
        if (type != null && !type.isEmpty()) {
            wrapper.eq(Product::getType, type);
        }
        wrapper.orderByDesc(Product::getCreateTime);
        return productMapper.selectPage(pageParam, wrapper);
    }

    /**
     * 批量迁移已发布商品的发布方名称为真实姓名
     * @return 更新的记录数
     */
    @Override
    @Transactional
    public int migratePublisherNamesToRealNames() {
        // 获取所有产品
        List<Product> allProducts = productMapper.selectList(null);
        int updateCount = 0;
        int skippedCount = 0;

        for (Product product : allProducts) {
            // 根据当前 ownName（可能是用户名或真实姓名）查找对应的用户
            LambdaQueryWrapper<User> userWrapper = new LambdaQueryWrapper<>();
            userWrapper.eq(User::getUserName, product.getOwnName())
                      .or()
                      .eq(User::getRealName, product.getOwnName());
            User user = userMapper.selectOne(userWrapper);

            if (user != null && user.getRealName() != null && !user.getRealName().isEmpty()) {
                // 如果产品的 ownName 不是真实姓名，则更新
                if (!user.getRealName().equals(product.getOwnName())) {
                    product.setOwnName(user.getRealName());
                    productMapper.updateById(product);
                    updateCount++;
                    System.out.println("更新商品: " + product.getTitle() + " 发布方: " + product.getOwnName() + " -> " + user.getRealName());
                }
            } else {
                skippedCount++;
                System.out.println("跳过商品: " + product.getTitle() + " 发布方: " + product.getOwnName() + " (用户不存在或无真实姓名)");
            }
        }

        System.out.println("迁移完成: 更新 " + updateCount + " 条，跳过 " + skippedCount + " 条");
        return updateCount;
    }
}