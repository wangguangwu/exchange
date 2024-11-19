package com.wangguangwu.exchangeusercore.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wangguangwu.exchange.dto.UserDTO;
import com.wangguangwu.exchange.dto.UserPageQuery;
import com.wangguangwu.exchange.entity.UserInfoDO;
import com.wangguangwu.exchange.entity.UserLoginRecordDO;
import com.wangguangwu.exchange.exception.UserException;
import com.wangguangwu.exchange.service.UserInfoService;
import com.wangguangwu.exchange.service.UserLoginRecordService;
import com.wangguangwu.exchange.utils.MappingUtils;
import com.wangguangwu.exchangeusercore.service.UserService;
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
    private final UserLoginRecordService userLoginRecordService;

    @Override
    public UserDTO getUserById(Long uid) {
        UserInfoDO user = userInfoService.getById(uid);
        if (user == null) {
            throw new UserException("用户不存在，用户ID: " + uid);
        }
        return MappingUtils.map(user, UserDTO.class);
    }

    @Override
    public void registerUser(UserDTO userDTO) {
        if (isUsernameExists(userDTO.getUsername())) {
            throw new UserException("用户名已存在: " + userDTO.getUsername());
        }

        UserInfoDO user = new UserInfoDO();
        user.setUsername(userDTO.getUsername());
        user.setPasswordHash(encryptPassword(userDTO.getPassword()));
        user.setEmail(userDTO.getEmail());
        user.setPhone(userDTO.getPhone());

        boolean success = userInfoService.save(user);
        if (success) {
            log.info("用户注册成功: {}", userDTO.getUsername());
        } else {
            log.error("用户注册失败: {}", userDTO.getUsername());
            throw new UserException("用户注册失败: " + userDTO.getUsername());
        }
    }

    @Override
    public String validateAndLogLogin(String username, String password, String ipAddress, String deviceInfo) {
        log.info("尝试登录，用户名: {}, IP: {}, 设备信息: {}", username, ipAddress, deviceInfo);

        // 查询用户信息
        UserInfoDO user = userInfoService.getOne(new QueryWrapper<UserInfoDO>().eq("username", username));
        // 用户不存在不用记录原因
        if (user == null) {
            log.warn("登录失败，用户不存在: {}, IP: {}, 设备信息: {}", username, ipAddress, deviceInfo);
            return "用户不存在";
        }

        // 验证用户信息
        String errorMsg = validateUserCredentials(user, password, username, ipAddress, deviceInfo);

        // 是否验证成功
        boolean isSuccess = StrUtil.isBlank(errorMsg);

        // 构造登录记录
        UserLoginRecordDO loginRecord = buildLoginRecord(user, isSuccess, ipAddress, deviceInfo, errorMsg);

        // 保存登录记录
        userLoginRecordService.save(loginRecord);

        if (!isSuccess) {
            log.warn("登录失败原因: {}", errorMsg);
        }

        return errorMsg;
    }

    @Override
    public void updateUser(UserDTO userDTO) {
        log.info("开始更新用户信息，用户ID: {}", userDTO.getId());
        UserInfoDO user = userInfoService.getById(userDTO.getId());
        if (user == null) {
            log.warn("更新失败，用户不存在: {}", userDTO.getId());
            throw new UserException("用户不存在，用户ID: " + userDTO.getId());
        }

        user.setEmail(userDTO.getEmail());
        user.setPhone(userDTO.getPhone());
        user.setPasswordHash(encryptPassword(userDTO.getPassword()));
        boolean success = userInfoService.updateById(user);
        if (success) {
            log.info("用户信息更新成功，用户ID: {}", userDTO.getId());
        } else {
            log.error("用户信息更新失败，用户ID: {}", userDTO.getId());
            throw new UserException("用户信息更新失败，用户ID: " + userDTO.getId());
        }
    }

    @Override
    public List<UserDTO> listUsers(UserPageQuery query) {
        // 使用 LambdaQueryWrapper 构建查询条件
        LambdaQueryWrapper<UserInfoDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(query.getUsername() != null && !query.getUsername().isEmpty(), UserInfoDO::getUsername, query.getUsername());
        wrapper.like(query.getEmail() != null && !query.getEmail().isEmpty(), UserInfoDO::getEmail, query.getEmail());
        wrapper.like(query.getPhone() != null && !query.getPhone().isEmpty(), UserInfoDO::getPhone, query.getPhone());

        // 分页查询
        Page<UserInfoDO> userPage = userInfoService.page(new Page<>(query.getPage(), query.getSize()), wrapper);
        List<UserDTO> userList = userPage.getRecords()
                .stream()
                .map(data -> MappingUtils.map(data, UserDTO.class))
                .collect(Collectors.toList());

        log.info("分页查询完成，查询到用户数量: {}", userList.size());
        return userList;
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