package com.wangguangwu.exchange.api;

import com.wangguangwu.exchange.dto.UserDTO;
import com.wangguangwu.exchange.request.LoginRequest;
import com.wangguangwu.exchange.request.ResetPasswordRequest;
import com.wangguangwu.exchange.request.UpdatePasswordRequest;
import com.wangguangwu.exchange.response.Response;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 用户服务 API 接口定义
 *
 * @author wangguangwu
 */
@RequestMapping("/user")
@CrossOrigin(origins = "*", allowCredentials = "true")
public interface UserController {

    /**
     * 用户登录
     *
     * @param request        登录
     * @param servletRequest 请求
     * @return token
     */
    @PostMapping("/login")
    Response<String> login(@Validated @RequestBody LoginRequest request, HttpServletRequest servletRequest);

    /**
     * 注册新用户
     *
     * @param userDTO 用户数据传输对象
     */
    @PostMapping("/register")
    Response<Void> register(@Validated(UserDTO.Create.class) @RequestBody UserDTO userDTO);

    /**
     * 找回密码
     *
     * @param request 包含用户名和新密码的请求体
     * @return 响应结果
     */
    @PostMapping("/resetPassword")
    Response<Void> resetPassword(@Validated @RequestBody ResetPasswordRequest request);

    /**
     * 更新密码
     *
     * @param request request
     */
    @PostMapping("/updatePassword")
    Response<Void> updatePassword(@Validated @RequestBody UpdatePasswordRequest request);

}
