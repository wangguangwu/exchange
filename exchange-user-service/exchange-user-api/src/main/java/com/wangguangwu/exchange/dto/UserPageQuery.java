package com.wangguangwu.exchange.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author wangguangwu
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class UserPageQuery extends PageQuery {

    /**
     * 用户名关键字（可选）
     */
    private String username;

    /**
     * 用户邮箱关键字（可选）
     */
    private String email;

    /**
     * 手机号关键字（可选）
     */
    private String phone;

}
