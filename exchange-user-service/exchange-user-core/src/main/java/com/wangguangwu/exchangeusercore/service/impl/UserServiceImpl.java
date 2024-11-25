package com.wangguangwu.exchangeusercore.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wangguangwu.exchange.request.LoginRequest;
import com.wangguangwu.exchange.request.ResetPasswordRequest;
import com.wangguangwu.exchange.request.UpdatePasswordRequest;
import com.wangguangwu.exchange.dto.UserDTO;
import com.wangguangwu.exchange.dto.UserPageQuery;
import com.wangguangwu.exchange.entity.UserInfoDO;
import com.wangguangwu.exchange.entity.UserLoginRecordDO;
import com.wangguangwu.exchange.exception.UserException;
import com.wangguangwu.exchange.response.Response;
import com.wangguangwu.exchange.service.UserInfoService;
import com.wangguangwu.exchange.service.UserLoginRecordService;
import com.wangguangwu.exchange.utils.IpUtil;
import com.wangguangwu.exchange.utils.MappingUtils;
import com.wangguangwu.exchangeusercore.service.LoginService;
import com.wangguangwu.exchangeusercore.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author wangguangwu
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserInfoService userInfoService;

    @Override
    public void registerUser(UserDTO userDTO) {
        if (isUsernameExists(userDTO.getUsername())) {
            throw new UserException("用户名已存在: " + userDTO.getUsername());
        }

        UserInfoDO user = new UserInfoDO();
        user.setUsername(userDTO.getUsername());
        user.setPasswordHash(encryptPassword(userDTO.getPassword()));

        boolean success = userInfoService.save(user);
        if (success) {
            log.info("用户注册成功: {}", userDTO.getUsername());
        } else {
            log.error("用户注册失败: {}", userDTO.getUsername());
            throw new UserException("用户注册失败: " + userDTO.getUsername());
        }
    }

    @Override
    public void updateUser(UpdatePasswordRequest request) {
        // Step 1: 判断账户是否存在
        log.info("开始检查用户是否存在，用户名: {}", request.getUsername());
        UserInfoDO user = userInfoService.getOne(new LambdaQueryWrapper<UserInfoDO>()
                .eq(UserInfoDO::getUsername, request.getUsername()));

        if (user == null) {
            log.warn("用户不存在，用户名: {}", request.getUsername());
            throw new UserException("用户不存在，用户名: " + request.getUsername());
        }

        // Step 2: 判断密码是否正确
        log.info("开始验证用户密码，用户名: {}", request.getUsername());
        String currentPasswordHash = encryptPassword(request.getOldPassword());
        if (!currentPasswordHash.equals(user.getPasswordHash())) {
            log.warn("密码验证失败，用户名: {}", request.getUsername());
            throw new UserException("当前密码错误，请重试！");
        }

        // Step 3: 更新密码
        log.info("开始更新用户密码，用户名: {}", request.getUsername());
        String newPasswordHash = encryptPassword(request.getNewPassword());
        user.setPasswordHash(newPasswordHash);

        boolean success = userInfoService.updateById(user);
        if (success) {
            log.info("用户密码更新成功，用户名: {}", request.getUsername());
        } else {
            log.error("用户密码更新失败，用户名: {}", request.getUsername());
            throw new UserException("用户密码更新失败，用户名: " + request.getUsername());
        }
    }

    @Override
    public void resetPassword(ResetPasswordRequest request) {
        log.info("开始检查用户是否存在，用户名: {}", request.getUsername());
        UserInfoDO user = userInfoService.getOne(new LambdaQueryWrapper<UserInfoDO>()
                .eq(UserInfoDO::getUsername, request.getUsername()));
        if (user == null) {
            log.warn("用户不存在，用户名: {}", request.getUsername());
            throw new UserException("用户不存在，用户名: " + request.getUsername());
        }

        // Step 2: 加密新密码
        String newPasswordHash = encryptPassword(request.getNewPassword());
        user.setPasswordHash(newPasswordHash);

        // Step 3: 更新用户信息
        boolean success = userInfoService.updateById(user);
        if (success) {
            log.info("用户密码更新成功，用户名: {}", request.getUsername());
        } else {
            log.error("用户密码更新失败，用户名: {}", request.getUsername());
            throw new UserException("用户密码更新失败，用户名: " + request.getUsername());
        }
    }

    //=================================================私有方法============================================================

    private boolean isUsernameExists(String username) {
        boolean exists = userInfoService.count(new LambdaQueryWrapper<UserInfoDO>().eq(UserInfoDO::getUsername, username)) > 0;
        if (exists) {
            log.info("用户名已存在: {}", username);
        }
        return exists;
    }

    private String encryptPassword(String rawPassword) {
        log.debug("开始加密密码");
        return BCrypt.hashpw(rawPassword, BCrypt.gensalt());
    }

    private boolean validatePassword(String rawPassword, String encodedPassword) {
        log.debug("校验密码");
        return BCrypt.checkpw(rawPassword, encodedPassword);
    }

    /**
     * 验证用户凭据
     *
     * @param user       用户信息
     * @param password   输入的密码
     * @param username   用户名
     * @param ipAddress  IP 地址
     * @param deviceInfo 设备信息
     * @return 错误信息，如果验证成功返回 null
     */
    private String validateUserCredentials(UserInfoDO user, String password, String username, String ipAddress, String deviceInfo) {
        if (!validatePassword(password, user.getPasswordHash())) {
            log.warn("登录失败，密码错误: {}, IP: {}, 设备信息: {}", username, ipAddress, deviceInfo);
            return "密码错误";
        }

        log.info("用户验证成功: {}, IP: {}, 设备信息: {}", username, ipAddress, deviceInfo);
        // 验证成功
        return null;
    }

    /**
     * 构造登录记录
     *
     * @param user       用户信息
     * @param isSuccess  是否登录成功
     * @param ipAddress  IP 地址
     * @param deviceInfo 设备信息
     * @param errorMsg   错误信息
     * @return 构造好的登录记录对象
     */
    private UserLoginRecordDO buildLoginRecord(UserInfoDO user, boolean isSuccess, String ipAddress, String deviceInfo, String errorMsg) {
        UserLoginRecordDO loginRecord = new UserLoginRecordDO();
        loginRecord.setUserId(user.getId());
        loginRecord.setUserName(user.getUsername());
        loginRecord.setLoginTime(LocalDateTime.now());
        loginRecord.setLoginIp(ipAddress);
        loginRecord.setDevice(deviceInfo);
        loginRecord.setIsSuccessful(isSuccess);
        loginRecord.setRemark(errorMsg);
        loginRecord.setIsDeleted(false);
        return loginRecord;
    }
}