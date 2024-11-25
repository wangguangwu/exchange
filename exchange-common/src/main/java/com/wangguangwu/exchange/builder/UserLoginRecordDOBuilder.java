package com.wangguangwu.exchange.builder;

import cn.hutool.core.util.StrUtil;
import com.wangguangwu.exchange.entity.UserInfoDO;
import com.wangguangwu.exchange.entity.UserLoginRecordDO;

import java.time.LocalDateTime;

/**
 * @author wangguangwu
 */
public final class UserLoginRecordDOBuilder {

    private UserLoginRecordDOBuilder() {
    }

    /**
     * 构造登录记录
     *
     * @param user       用户信息
     * @param ipAddress  IP 地址
     * @param deviceInfo 设备信息
     * @param errorMsg   错误信息
     * @return 构造好的登录记录对象
     */
    public static UserLoginRecordDO buildLoginRecord(UserInfoDO user, String ipAddress, String deviceInfo, String errorMsg) {
        UserLoginRecordDO loginRecord = new UserLoginRecordDO();
        loginRecord.setUserId(user.getId());
        loginRecord.setUserName(user.getUsername());
        loginRecord.setLoginTime(LocalDateTime.now());
        loginRecord.setLoginIp(ipAddress);
        loginRecord.setDevice(deviceInfo);
        loginRecord.setIsSuccessful(StrUtil.isBlank(errorMsg));
        loginRecord.setRemark(errorMsg);
        loginRecord.setIsDeleted(false);
        return loginRecord;
    }
}
