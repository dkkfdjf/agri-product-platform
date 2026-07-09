package com.agri.platform.controller;

import com.agri.platform.common.Result;
import com.agri.platform.dto.UserLoginRequest;
import com.agri.platform.dto.UserRegisterRequest;
import com.agri.platform.entity.User;
import com.agri.platform.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 用户控制器
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public Result<String> register(@RequestBody UserRegisterRequest request) {
        try {
            // 验证参数
            if (request.getUserName() == null || request.getUserName().trim().isEmpty()) {
                return Result.error("用户名不能为空");
            }
            if (request.getPassword() == null || request.getPassword().length() < 6) {
                return Result.error("密码长度不能少于6位");
            }
            if (request.getRole() == null) {
                return Result.error("请选择角色类型");
            }
            // 验证角色类型
            if (!request.getRole().matches("farmer|buyer")) {
                return Result.error("角色类型错误");
            }

            userService.register(request);
            return Result.success("注册成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody UserLoginRequest request) {
        try {
            // 验证参数
            if (request.getUserName() == null || request.getUserName().trim().isEmpty()) {
                return Result.error("用户名不能为空");
            }
            if (request.getPassword() == null || request.getPassword().length() < 6) {
                return Result.error("密码长度不能少于6位");
            }
            if (request.getRole() == null || request.getRole().trim().isEmpty()) {
                return Result.error("请选择登录角色");
            }

            Map<String, Object> result = userService.login(request);
            return Result.success("登录成功", result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/info")
    public Result<Map<String, Object>> getUserInfo(@RequestHeader("Authorization") String token) {
        try {
            String userName = userService.getUserByUserNameFromToken(token);
            User user = userService.getUserByUserName(userName);
            if (user == null) {
                return Result.error("用户不存在");
            }
            return Result.success(userService.getUserInfo(user));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 修改用户信息
     */
    @PutMapping("/info")
    public Result<String> updateInfo(@RequestHeader("Authorization") String token,
                                     @RequestBody User userInfo) {
        try {
            String userName = userService.getUserByUserNameFromToken(token);
            User user = userService.getUserByUserName(userName);
            if (user == null) {
                return Result.error("用户不存在");
            }

            // 更新允许修改的字段
            if (userInfo.getRealName() != null) {
                user.setRealName(userInfo.getRealName());
            }
            if (userInfo.getPhone() != null) {
                user.setPhone(userInfo.getPhone());
            }
            if (userInfo.getAddress() != null) {
                user.setAddress(userInfo.getAddress());
            }
            if (userInfo.getAvatar() != null) {
                user.setAvatar(userInfo.getAvatar());
            }

            userService.updateUser(user);
            return Result.success("更新成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 修改密码
     */
    @PutMapping("/password")
    public Result<String> updatePassword(@RequestHeader("Authorization") String token,
                                        @RequestBody Map<String, String> request) {
        try {
            String userName = userService.getUserByUserNameFromToken(token);
            User user = userService.getUserByUserName(userName);
            if (user == null) {
                return Result.error("用户不存在");
            }

            String oldPassword = request.get("oldPassword");
            String newPassword = request.get("newPassword");

            if (oldPassword == null || newPassword == null) {
                return Result.error("请输入旧密码和新密码");
            }
            if (newPassword.length() < 6) {
                return Result.error("新密码长度不能少于6位");
            }

            userService.updatePassword(user, oldPassword, newPassword);
            return Result.success("密码修改成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
