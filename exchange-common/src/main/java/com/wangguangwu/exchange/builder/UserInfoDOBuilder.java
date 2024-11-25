package com.wangguangwu.exchange.builder;

import com.wangguangwu.exchange.entity.UserInfoDO;
import com.wangguangwu.exchange.utils.PasswordUtil;

/**
 * @author wangguangwu
 */
public final class UserInfoDOBuilder {

    private UserInfoDOBuilder() {
    }

    public static UserInfoDO build(String userName, String password) {
        UserInfoDO user = new UserInfoDO();
        user.setUsername(userName);
        user.setPasswordHash(PasswordUtil.encryptPassword(password));
        return user;
    }
}
