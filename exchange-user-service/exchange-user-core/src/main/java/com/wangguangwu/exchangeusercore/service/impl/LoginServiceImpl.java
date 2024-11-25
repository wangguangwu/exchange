package com.wangguangwu.exchangeusercore.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wangguangwu.exchange.entity.UserInfoDO;
import com.wangguangwu.exchange.entity.UserLoginRecordDO;
import com.wangguangwu.exchange.exception.UserException;
import com.wangguangwu.exchange.request.LoginRequest;
import com.wangguangwu.exchange.service.UserInfoService;
import com.wangguangwu.exchange.service.UserLoginRecordService;
import com.wangguangwu.exchange.utils.IpUtil;
import com.wangguangwu.exchange.utils.PasswordUtil;
import com.wangguangwu.exchangeusercore.builder.UserLoginRecordDOBuilder;
import com.wangguangwu.exchangeusercore.service.LoginService;
import com.wangguangwu.exchangeusercore.util.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 登录服务实现类
 * 负责处理用户登录的业务逻辑
 *
 * @author wangguangwu
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class LoginServiceImpl implements LoginService {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserInfoService userInfoService;
    private final UserLoginRecordService userLoginRecordService;

    @Override
    public String login(LoginRequest request, HttpServletRequest servletRequest) {
        // 提取用户名和密码
        String username = request.getUsername();
        String password = request.getPassword();

        // 获取客户端 IP 地址和设备信息
        String ipAddress = IpUtil.getClientIp(servletRequest);
        String deviceInfo = servletRequest.getHeader("User-Agent");

        // 查询用户信息
        UserInfoDO user = userInfoService.getOne(new LambdaQueryWrapper<UserInfoDO>()
                .eq(UserInfoDO::getUsername, username));

        // 验证用户名和密码
        String errorMsg = validateUserCredentials(user, password);

        // 构造登录记录
        UserLoginRecordDO loginRecord = UserLoginRecordDOBuilder.buildLoginRecord(user, ipAddress, deviceInfo, errorMsg);

        // 保存登录记录
        userLoginRecordService.save(loginRecord);

        // 如果验证失败，抛出异常
        if (StrUtil.isNotBlank(errorMsg)) {
            log.warn("用户登录失败，用户名: {}, 错误信息: {}", username, errorMsg);
            throw new UserException(errorMsg);
        }

        // 验证成功，生成 Token
        String token = jwtTokenProvider.generateToken(username);
        log.info("用户登录成功，用户名: {}, IP: {}, 设备信息: {}", username, ipAddress, deviceInfo);

        return token;
    }

    /**
     * 验证用户登录凭据
     *
     * @param user     用户信息
     * @param password 输入的密码
     * @return 错误信息（如果验证失败）
     */
    private String validateUserCredentials(UserInfoDO user, String password) {
        if (user == null) {
            return "用户不存在";
        }
        if (!PasswordUtil.validatePassword(password, user.getPasswordHash())) {
            return "密码错误";
        }
        // 验证通过
        return "";
    }
}
