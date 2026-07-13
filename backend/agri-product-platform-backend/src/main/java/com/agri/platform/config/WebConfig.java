package com.agri.platform.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web配置
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private JwtInterceptor jwtInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/**")  // 拦截所有路径
                .excludePathPatterns(
                        "/user/login",       // 登录接口
                        "/user/register",    // 注册接口
                        "/home/**",          // 首页
                        "/banner/**",        // 轮播图
                        "/product/**",       // 商品相关（公开）
                        "/finance/product/**",  // 融资产品（公开）
                        "/expert/list",      // 专家列表（公开）
                        "/expert/*",         // 专家详情（公开，单段路径；/expert/my/info 与 /expert/reserve/* 受保护）
                        "/knowledge/list",   // 知识列表（公开）
                        "/knowledge/*",      // 知识详情（公开）
                        "/knowledge/*/discuss/list", // 知识评论列表（公开）
                        "/question/list"     // 问答列表（公开）
                );
    }
}
