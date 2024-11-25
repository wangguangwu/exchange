package com.wangguangwu.exchangeusercore.service;

import com.wangguangwu.exchange.dto.UserDTO;
import com.wangguangwu.exchange.request.ResetPasswordRequest;
import com.wangguangwu.exchange.request.UpdatePasswordRequest;

/**
 * @author wangguangwu
 */
public interface UserService {

    /**
     * 注册新用户
     */
    void registerUser(UserDTO userDTO);

    /**
     * 更新用户信息
     */
    void updateUser(UpdatePasswordRequest request);

    /**
     * 找回密码
     *
     * @param request request
     */
    void resetPassword(ResetPasswordRequest request);


}
