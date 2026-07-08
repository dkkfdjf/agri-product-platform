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
}
