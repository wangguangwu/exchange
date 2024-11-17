package com.wangguangwu.exchange.api;

import com.wangguangwu.exchange.dto.UserDTO;
import com.wangguangwu.exchange.dto.UserPageQuery;
import com.wangguangwu.exchange.response.Response;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户服务 API 接口定义
 *
 * @author wangguangwu
 */
@RequestMapping("/user")
public interface UserController {

    /**
     * 获取用户信息接口定义
     *
     * @param uid 用户ID
     * @return 用户信息
     */
    @GetMapping("/get/{uid}")
    Response<UserDTO> getUser(@PathVariable("uid") Long uid);

    /**
     * 注册新用户
     *
     * @param userDTO 用户数据传输对象
     * @return 用户ID
     */
    @PostMapping("/register")
    Response<String> register(@Validated(UserDTO.Create.class) @RequestBody UserDTO userDTO);

    /**
     * 用户登录
     *
     * @param username 用户名
     * @param password 密码
     * @return 登录结果
     */
    @PostMapping("/login")
    Response<String> login(@RequestParam("username") String username, @RequestParam("password") String password);

    /**
     * 更新用户信息
     *
     * @param userDTO 用户数据传输对象
     * @return 更新结果
     */
    @PutMapping("/update")
    Response<String> updateUser(@Validated(UserDTO.Update.class) @RequestBody UserDTO userDTO);

    /**
     * 分页查询用户
     *
     * @param query@return 用户列表
     */
    @GetMapping("/list")
    Response<List<UserDTO>> listUsers(@Validated UserPageQuery query);
}
