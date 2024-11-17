package com.wangguangwu.exchangeusercore.controller;

import com.alibaba.fastjson2.JSON;
import com.wangguangwu.exchange.api.UserController;
import com.wangguangwu.exchange.dto.UserDTO;
import com.wangguangwu.exchange.dto.UserPageQuery;
import com.wangguangwu.exchange.response.Response;
import com.wangguangwu.exchangeusercore.service.CustomUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 用户服务 API 实现类
 *
 * @author wangguangwu
 */
@RestController
@RequiredArgsConstructor
@Slf4j
public class UserControllerImpl implements UserController {

    private final CustomUserService customUserService;

    @Override
    public Response<UserDTO> getUser(Long uid) {
        try {
            log.info("开始获取用户信息，用户ID: {}", uid);
            UserDTO user = customUserService.getUserById(uid);
            log.info("成功获取用户信息: {}", JSON.toJSONString(user));
            return Response.success(user);
        } catch (Exception e) {
            log.error("获取用户信息失败，用户ID: {}, 错误信息: {}", uid, e.getMessage(), e);
            return Response.error("获取用户信息失败");
        }
    }

    @Override
    public Response<String> register(UserDTO userDTO) {
        try {
            log.info("开始注册用户: {}", JSON.toJSONString(userDTO));
            boolean result = customUserService.registerUser(userDTO);
            log.info("用户注册成功: {}", JSON.toJSONString(userDTO));
            return result ? Response.success("注册成功") : Response.error("注册失败");
        } catch (Exception e) {
            log.error("用户注册失败: {}, 错误信息: {}", JSON.toJSONString(userDTO), e.getMessage(), e);
            return Response.error("注册失败: " + e.getMessage());
        }
    }

    @Override
    public Response<String> login(String username, String password) {
        try {
            log.info("开始用户登录，用户名: {}", username);
            boolean result = customUserService.validateLogin(username, password);
            log.info("用户登录结果，用户名: {}, 成功: {}", username, result);
            return result ? Response.success("登录成功") : Response.error("用户名或密码错误");
        } catch (Exception e) {
            log.error("用户登录失败，用户名: {}, 错误信息: {}", username, e.getMessage(), e);
            return Response.error("登录失败: " + e.getMessage());
        }
    }

    @Override
    public Response<String> updateUser(UserDTO userDTO) {
        try {
            log.info("开始更新用户信息: {}", JSON.toJSONString(userDTO));
            boolean result = customUserService.updateUser(userDTO);
            log.info("用户更新结果，用户ID: {}, 成功: {}", userDTO.getId(), result);
            return result ? Response.success("更新成功") : Response.error("更新失败");
        } catch (Exception e) {
            log.error("更新用户信息失败: {}, 错误信息: {}", JSON.toJSONString(userDTO), e.getMessage(), e);
            return Response.error("更新失败: " + e.getMessage());
        }
    }

    @Override
    public Response<List<UserDTO>> listUsers(UserPageQuery query) {
        try {
            List<UserDTO> users = customUserService.listUsers(query);
            return Response.success(users);
        } catch (Exception e) {
            log.error("分页查询用户列表失败，查询信息: {}，异常信息：{}", JSON.toJSON(query), e.getMessage(), e);
            return Response.error("查询用户列表失败: " + e.getMessage());
        }
    }
}
