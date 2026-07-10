package com.agri.platform.controller;

import com.agri.platform.common.Result;
import com.agri.platform.dto.ProductRequest;
import com.agri.platform.entity.Product;
import com.agri.platform.service.ProductService;
import com.agri.platform.util.JwtUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping
    public Result<String> publish(@RequestHeader("Authorization") String token,
                                   @RequestBody ProductRequest request) {
        String userName = jwtUtil.getUsernameFromToken(token.replace("Bearer ", ""));
        productService.publishProduct(userName, request);
        return Result.success("发布成功");
    }

    @GetMapping("/page")
    public Result<Page<Product>> page(@RequestParam(defaultValue = "1") int page,
                                       @RequestParam(defaultValue = "10") int pageSize,
                                       @RequestParam(required = false) String type) {
        return Result.success(productService.getProductPage(page, pageSize, type));
    }

    @GetMapping("/{orderId}")
    public Result<Product> getById(@PathVariable Integer orderId) {
        return Result.success(productService.getProductById(orderId));
    }

    @PutMapping("/{orderId}")
    public Result<String> update(@PathVariable Integer orderId,
                                  @RequestHeader("Authorization") String token,
                                  @RequestBody ProductRequest request) {
        String userName = jwtUtil.getUsernameFromToken(token.replace("Bearer ", ""));
        productService.updateProduct(orderId, userName, request);
        return Result.success("修改成功");
    }

    @DeleteMapping("/{orderId}")
    public Result<String> delete(@PathVariable Integer orderId,
                                  @RequestHeader("Authorization") String token) {
        String userName = jwtUtil.getUsernameFromToken(token.replace("Bearer ", ""));
        productService.deleteProduct(orderId, userName);
        return Result.success("下架成功");
    }

    @GetMapping("/user")
    public Result<Page<Product>> myProducts(@RequestHeader("Authorization") String token,
                                            @RequestParam(defaultValue = "1") int page,
                                            @RequestParam(defaultValue = "10") int pageSize) {
        String userName = jwtUtil.getUsernameFromToken(token.replace("Bearer ", ""));
        return Result.success(productService.getUserProducts(userName, page, pageSize));
    }
}