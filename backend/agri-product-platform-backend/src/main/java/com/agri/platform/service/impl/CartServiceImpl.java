package com.agri.platform.service.impl;

import com.agri.platform.dto.CartRequest;
import com.agri.platform.entity.Product;
import com.agri.platform.entity.ShoppingCart;
import com.agri.platform.mapper.ProductMapper;
import com.agri.platform.mapper.ShoppingCartMapper;
import com.agri.platform.service.CartService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private ShoppingCartMapper cartMapper;

    @Autowired
    private ProductMapper productMapper;

    @Override
    public void addToCart(String userName, CartRequest request) {
        // 检查商品是否存在
        Product product = productMapper.selectById(request.getOrderId());
        if (product == null) {
            throw new RuntimeException("商品不存在");
        }

        // 检查购物车中是否已有该商品
        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShoppingCart::getOwnName, userName)
               .eq(ShoppingCart::getOrderId, request.getOrderId());
        ShoppingCart existingCart = cartMapper.selectOne(wrapper);

        if (existingCart != null) {
            // 已存在，增加数量
            existingCart.setCount(existingCart.getCount() + request.getCount());
            cartMapper.updateById(existingCart);
        } else {
            // 不存在，新增
            ShoppingCart cart = new ShoppingCart();
            cart.setOrderId(request.getOrderId());
            cart.setCount(request.getCount());
            cart.setOwnName(userName);
            cartMapper.insert(cart);
        }
    }

    @Override
    public List<ShoppingCart> getCartList(String userName) {
        return cartMapper.selectList(
            new LambdaQueryWrapper<ShoppingCart>()
                .eq(ShoppingCart::getOwnName, userName)
        );
    }

    @Override
    public void updateCartCount(Integer shoppingId, String userName, Integer count) {
        ShoppingCart cart = cartMapper.selectById(shoppingId);
        if (cart == null) {
            throw new RuntimeException("购物车记录不存在");
        }
        if (!cart.getOwnName().equals(userName)) {
            throw new RuntimeException("无权限操作此购物车记录");
        }
        if (count <= 0) {
            throw new RuntimeException("数量必须大于0");
        }
        cart.setCount(count);
        cartMapper.updateById(cart);
    }

    @Override
    public void deleteFromCart(Integer shoppingId, String userName) {
        ShoppingCart cart = cartMapper.selectById(shoppingId);
        if (cart == null) {
            throw new RuntimeException("购物车记录不存在");
        }
        if (!cart.getOwnName().equals(userName)) {
            throw new RuntimeException("无权限操作此购物车记录");
        }
        cartMapper.deleteById(shoppingId);
    }
}