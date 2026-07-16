package com.agri.platform.dto;

import lombok.Data;

/**
 * 用户注册请求
 */
@Data
public class UserRegisterRequest {
    private String userName;
    private String password;
    private String realName;
    private String phone;
    private String role;  // farmer, buyer
    private String cfTurnstileToken;  // Cloudflare Turnstile 人机验证 token（前端验证后带回）
}
