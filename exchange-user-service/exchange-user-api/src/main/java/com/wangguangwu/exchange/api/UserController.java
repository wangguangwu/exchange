package com.wangguangwu.exchange.api;

import com.wangguangwu.exchange.request.LoginRequest;
import com.wangguangwu.exchange.request.RegisterRequest;
import com.wangguangwu.exchange.request.ResetPasswordRequest;
import com.wangguangwu.exchange.request.UpdatePasswordRequest;
import com.wangguangwu.exchange.response.Response;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 用户服务 API 接口定义
 *
 * @author wangguangwu
 */
@RequestMapping("/user")
public interface UserController {

    /**
     * 用户登录接口
     * <p>
     * 用于验证用户的用户名和密码，成功后返回一个 Token 用于后续认证。
     *
     * @param request        登录请求对象，包含用户名和密码
     * @param servletRequest HTTP 请求对象，用于获取客户端信息（如 IP 地址）
     * @return 成功时返回包含 Token 的响应，失败时返回错误信息
     */
    @PostMapping("/login")
    Response<String> login(@Validated @RequestBody LoginRequest request, HttpServletRequest servletRequest);

    /**
     * 用户注册接口
     * <p>
     * 用于创建一个新的用户账号。
     *
     * @param request 注册请求对象，包含用户名、密码等用户信息
     * @return 成功时返回空响应，失败时返回错误信息
     */
    @PostMapping("/register")
    Response<Void> register(@Validated @RequestBody RegisterRequest request);

    /**
     * 找回密码接口
     * <p>
     * 用于通过用户名重置用户的密码。
     *
     * @param request 找回密码请求对象，包含用户名和新密码
     * @return 成功时返回空响应，失败时返回错误信息
     */
    @PostMapping("/resetPassword")
    Response<Void> resetPassword(@Validated @RequestBody ResetPasswordRequest request);

    /**
     * 更新密码接口
     * <p>
     * 用于验证用户的旧密码并更新为新密码。
     *
     * @param request 更新密码请求对象，包含用户名、旧密码和新密码
     * @return 成功时返回空响应，失败时返回错误信息
     */
    @PostMapping("/updatePassword")
    Response<Void> updatePassword(@Validated @RequestBody UpdatePasswordRequest request);

}
