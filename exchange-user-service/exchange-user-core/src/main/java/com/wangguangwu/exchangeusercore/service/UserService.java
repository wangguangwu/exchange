package com.wangguangwu.exchangeusercore.service;

import com.wangguangwu.exchange.dto.UserDTO;
import com.wangguangwu.exchange.dto.UserPageQuery;

import java.util.List;

/**
 * @author wangguangwu
 */
public interface UserService {

    /**
     * 根据用户 ID 获取用户信息
     */
    UserDTO getUserById(Long uid);

    /**
     * 注册新用户
     */
    void registerUser(UserDTO userDTO);

    /**
     * 验证登录
     */
    void validateLogin(String username, String password);

    /**
     * 更新用户信息
     */
    void updateUser(UserDTO userDTO);

    /**
     * 分页查询用户
     */
    List<UserDTO> listUsers(UserPageQuery query);
}
