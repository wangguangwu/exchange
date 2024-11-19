package com.wangguangwu.exchangeusercore.controller;

import cn.hutool.json.JSONUtil;
import com.wangguangwu.exchange.api.UserController;
import com.wangguangwu.exchange.dto.UserDTO;
import com.wangguangwu.exchange.dto.UserPageQuery;
import com.wangguangwu.exchange.response.Response;
import com.wangguangwu.exchangeusercore.service.UserService;
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

    private final UserService userService;

    @Override
    public Response<UserDTO> getUser(Long uid) {
        try {
            log.info("开始获取用户信息，用户ID: {}", uid);
            UserDTO user = userService.getUserById(uid);
            log.debug("成功获取用户信息: {}", JSONUtil.toJsonStr(user));
            return Response.success(user);
        } catch (Exception e) {
            log.error("获取用户信息失败，用户ID: {}, 错误信息: {}", uid, e.getMessage(), e);
            return Response.error("获取用户信息失败");
        }
    }

    @Override
    public Response<String> register(UserDTO userDTO) {
        try {
            log.info("开始注册用户，用户名: {}", userDTO.getUsername());
            userService.registerUser(userDTO);
            log.info("用户注册成功，用户名: {}", userDTO.getUsername());
            return Response.success("注册成功");
        } catch (Exception e) {
            log.error("用户注册失败，用户名: {}, 错误信息: {}", userDTO.getUsername(), e.getMessage(), e);
            return Response.error("注册失败: " + e.getMessage());
        }
    }

    @Override
    public Response<String> login(String username, String password) {
        try {
            log.info("用户登录请求，用户名: {}", username);
            userService.validateLogin(username, password);
            log.info("用户登录成功，用户名: {}", username);
            return Response.success("登录成功");
        } catch (Exception e) {
            log.error("用户登录失败，用户名: {}, 错误信息: {}", username, e.getMessage(), e);
            return Response.error("登录失败: " + e.getMessage());
        }
    }

    @Override
    public Response<String> updateUser(UserDTO userDTO) {
        try {
            log.info("开始更新用户信息，用户ID: {}", userDTO.getId());
            userService.updateUser(userDTO);
            log.info("用户信息更新成功，用户ID: {}", userDTO.getId());
            return Response.success("更新成功");
        } catch (Exception e) {
            log.error("更新用户信息失败，用户ID: {}, 错误信息: {}", userDTO.getId(), e.getMessage(), e);
            return Response.error("更新失败: " + e.getMessage());
        }
    }

    @Override
    public Response<List<UserDTO>> listUsers(UserPageQuery query) {
        try {
            log.info("查询用户列表，查询条件: {}", JSONUtil.toJsonStr(query));
            List<UserDTO> users = userService.listUsers(query);
            return Response.success(users);
        } catch (Exception e) {
            log.error("查询用户列表失败，查询条件: {}, 错误信息: {}", JSONUtil.toJsonStr(query), e.getMessage(), e);
            return Response.error("查询用户列表失败: " + e.getMessage());
        }
    }
}
