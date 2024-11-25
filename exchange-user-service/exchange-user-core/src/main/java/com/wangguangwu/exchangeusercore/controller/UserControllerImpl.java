package com.wangguangwu.exchangeusercore.controller;

import com.wangguangwu.exchange.api.UserController;
import com.wangguangwu.exchange.dto.UserDTO;
import com.wangguangwu.exchange.request.LoginRequest;
import com.wangguangwu.exchange.request.ResetPasswordRequest;
import com.wangguangwu.exchange.request.UpdatePasswordRequest;
import com.wangguangwu.exchange.response.Response;
import com.wangguangwu.exchangeusercore.service.LoginService;
import com.wangguangwu.exchangeusercore.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户服务 API 实现类
 *
 * @author wangguangwu
 */
@RestController
@RequiredArgsConstructor
@Slf4j
public class UserControllerImpl implements UserController {

    private final UserService userService;
    private final LoginService loginService;

    @Override
    public Response<String> login(LoginRequest request, HttpServletRequest servletRequest) {
        String username = request.getUsername();
        try {
            log.info("用户[{}]开始登录", username);
            String token = loginService.login(request, servletRequest);
            log.info("用户[{}]登录成功", username);
            return Response.success(token);
        } catch (Exception e) {
            // 捕获所有系统异常，记录日志并返回错误响应
            log.error("用户[{}]登录失败， 错误信息: {}", username, e.getMessage(), e);
            return Response.error(e.getMessage());
        }
    }

    @Override
    public Response<Void> register(UserDTO userDTO) {
        try {
            log.info("开始注册用户，用户名: {}", userDTO.getUsername());
            userService.registerUser(userDTO);
            log.info("用户注册成功，用户名: {}", userDTO.getUsername());
            return Response.success();
        } catch (Exception e) {
            log.error("用户注册失败，用户名: {}, 错误信息: {}", userDTO.getUsername(), e.getMessage(), e);
            return Response.error(e.getMessage());
        }
    }


    @Override
    public Response<Void> updatePassword(UpdatePasswordRequest request) {
        String username = request.getUsername();
        try {
            log.info("开始更新用户信息，用户名: {}", username);
            userService.updateUser(request);
            log.info("用户信息更新成功，用户ID: {}", username);
            return Response.success();
        } catch (Exception e) {
            log.error("更新用户信息失败，用户名: {}, 错误信息: {}", username, e.getMessage(), e);
            return Response.error(e.getMessage());
        }
    }

    @Override
    public Response<Void> resetPassword(ResetPasswordRequest request) {
        String username = request.getUsername();
        try {
            log.info("开始找回密码操作，用户名: {}", username);
            userService.resetPassword(request);
            return Response.success();
        } catch (Exception e) {
            log.error("找回密码操作失败，用户名: {}, 错误信息: {}", username, e.getMessage(), e);
            return Response.error(e.getMessage());
        }
    }
}
