package com.wangguangwu.exchangeusercore.controller;

import com.wangguangwu.exchange.api.UserController;
import com.wangguangwu.exchange.controller.BaseController;
import com.wangguangwu.exchange.request.LoginRequest;
import com.wangguangwu.exchange.request.RegisterRequest;
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
public class UserControllerImpl extends BaseController implements UserController {

    private final UserService userService;
    private final LoginService loginService;

    @Override
    public Response<String> login(LoginRequest request, HttpServletRequest servletRequest) {
        String username = request.getUsername();
        return execute(() -> {
            log.info("用户[{}]尝试登录", username);
            return loginService.login(request, servletRequest);
        }, "用户登录");
    }

    @Override
    public Response<Void> register(RegisterRequest request) {
        String username = request.getUsername();
        return execute(() -> {
            log.info("用户[{}]尝试注册", username);
            userService.registerUser(request);
            return null;
        }, "用户注册");
    }

    @Override
    public Response<Void> resetPassword(ResetPasswordRequest request) {
        String username = request.getUsername();
        return execute(() -> {
            log.info("用户[{}]尝试找回密码", username);
            userService.resetPassword(request);
            return null;
        }, "找回密码");
    }

    @Override
    public Response<Void> updatePassword(UpdatePasswordRequest request) {
        String username = request.getUsername();
        return execute(() -> {
            log.info("用户[{}]尝试更新密码", username);
            userService.updatePassword(request);
            return null;
        }, "更新密码");
    }
}
