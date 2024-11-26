package com.wangguangwu.exchangeusercore.service;

import com.wangguangwu.exchange.request.LoginRequest;
import com.wangguangwu.exchange.response.LoginResponse;
import jakarta.servlet.http.HttpServletRequest;

/**
 * 登录服务接口
 * 提供用户登录相关功能
 *
 * @author wangguangwu
 */
public interface LoginService {

    /**
     * 用户登录方法
     *
     * @param request        包含用户名和密码的登录请求对象
     * @param servletRequest 当前 HTTP 请求对象，用于获取客户端相关信息（如 IP 地址、设备信息等）
     * @return 登录成功后生成的 Token，用于用户后续的身份认证
     * @throws com.wangguangwu.exchange.exception.UserException 如果用户名或密码错误，或其他验证失败的场景
     */
    LoginResponse login(LoginRequest request, HttpServletRequest servletRequest);

}
