package com.agri.platform.controller;

import com.agri.platform.common.Result;
import com.agri.platform.dto.CartRequest;
import com.agri.platform.entity.ShoppingCart;
import com.agri.platform.service.CartService;
import com.agri.platform.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping
    public Result<String> add(@RequestHeader("Authorization") String token,
                              @RequestBody CartRequest request) {
        String userName = jwtUtil.getUsernameFromToken(token.replace("Bearer ", ""));
        cartService.addToCart(userName, request);
        return Result.success("加入购物车成功");
    }

    @GetMapping("/list")
    public Result<List<ShoppingCart>> list(@RequestHeader("Authorization") String token) {
        String userName = jwtUtil.getUsernameFromToken(token.replace("Bearer ", ""));
        return Result.success(cartService.getCartList(userName));
    }

    @PutMapping("/{shoppingId}")
    public Result<String> update(@PathVariable Integer shoppingId,
                                  @RequestHeader("Authorization") String token,
                                  @RequestBody Map<String, Integer> request) {
        String userName = jwtUtil.getUsernameFromToken(token.replace("Bearer ", ""));
        Integer count = request.get("count");
        cartService.updateCartCount(shoppingId, userName, count);
        return Result.success("修改成功");
    }

    @DeleteMapping("/{shoppingId}")
    public Result<String> delete(@PathVariable Integer shoppingId,
                                  @RequestHeader("Authorization") String token) {
        String userName = jwtUtil.getUsernameFromToken(token.replace("Bearer ", ""));
        cartService.deleteFromCart(shoppingId, userName);
        return Result.success("删除成功");
    }
}