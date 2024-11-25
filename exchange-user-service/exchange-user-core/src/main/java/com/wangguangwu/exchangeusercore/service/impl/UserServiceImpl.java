package com.wangguangwu.exchangeusercore.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wangguangwu.exchange.builder.UserInfoDOBuilder;
import com.wangguangwu.exchange.entity.UserInfoDO;
import com.wangguangwu.exchange.exception.UserException;
import com.wangguangwu.exchange.request.RegisterRequest;
import com.wangguangwu.exchange.request.ResetPasswordRequest;
import com.wangguangwu.exchange.request.UpdatePasswordRequest;
import com.wangguangwu.exchange.service.UserInfoService;
import com.wangguangwu.exchange.utils.PasswordUtil;
import com.wangguangwu.exchangeusercore.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author wangguangwu
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserInfoService userInfoService;

    @Override
    public void registerUser(RegisterRequest request) {
        String username = request.getUsername();
        if (isUsernameExists(username)) {
            throw new UserException("用户名已存在: " + username);
        }

        UserInfoDO user = UserInfoDOBuilder.build(username, request.getPassword());

        boolean success = userInfoService.save(user);
        if (success) {
            log.info("用户注册成功: {}", username);
        } else {
            log.error("用户注册失败: {}", username);
            throw new UserException("用户注册失败: " + username);
        }
    }

    @Override
    public void updatePassword(UpdatePasswordRequest request) {
        // 检查用户是否存在
        UserInfoDO user = validateUserExistence(request.getUsername());

        // 验证旧密码是否正确
        validatePassword(request.getOldPassword(), user.getPasswordHash(), request.getUsername());

        // 更新密码
        updatePasswordForUser(user, request.getNewPassword(), request.getUsername());
    }

    @Override
    public void resetPassword(ResetPasswordRequest request) {
        // 检查用户是否存在
        UserInfoDO user = validateUserExistence(request.getUsername());

        // 更新密码
        updatePasswordForUser(user, request.getNewPassword(), request.getUsername());
    }

    //=================================================私有方法============================================================

    private boolean isUsernameExists(String username) {
        boolean exists = userInfoService.count(new LambdaQueryWrapper<UserInfoDO>().eq(UserInfoDO::getUsername, username)) > 0;
        if (exists) {
            log.info("用户名已存在: {}", username);
        }
        return exists;
    }

    /**
     * 检查用户是否存在
     *
     * @param username 用户名
     * @return 用户信息对象
     */
    private UserInfoDO validateUserExistence(String username) {
        log.info("开始检查用户是否存在，用户名: {}", username);
        UserInfoDO user = userInfoService.getOne(new LambdaQueryWrapper<UserInfoDO>()
                .eq(UserInfoDO::getUsername, username));
        if (user == null) {
            log.warn("用户不存在，用户名: {}", username);
            throw new UserException("用户不存在，用户名: " + username);
        }
        return user;
    }

    /**
     * 验证密码是否正确
     *
     * @param inputPassword 输入的密码
     * @param storedPasswordHash 存储的密码哈希
     * @param username 用户名
     */
    private void validatePassword(String inputPassword, String storedPasswordHash, String username) {
        log.info("开始验证用户密码，用户名: {}", username);
        String inputPasswordHash = PasswordUtil.encryptPassword(inputPassword);
        if (!inputPasswordHash.equals(storedPasswordHash)) {
            log.warn("密码验证失败，用户名: {}", username);
            throw new UserException("当前密码错误，请重试！");
        }
    }

    /**
     * 更新用户密码
     *
     * @param user 用户信息对象
     * @param newPassword 新密码
     * @param username 用户名
     */
    private void updatePasswordForUser(UserInfoDO user, String newPassword, String username) {
        log.info("开始更新用户密码，用户名: {}", username);
        String newPasswordHash = PasswordUtil.encryptPassword(newPassword);
        user.setPasswordHash(newPasswordHash);

        boolean success = userInfoService.updateById(user);
        if (success) {
            log.info("用户密码更新成功，用户名: {}", username);
        } else {
            log.error("用户密码更新失败，用户名: {}", username);
            throw new UserException("用户密码更新失败，用户名: " + username);
        }
    }
}