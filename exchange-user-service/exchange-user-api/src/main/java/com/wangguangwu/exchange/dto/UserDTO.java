package com.wangguangwu.exchange.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author wangguangwu
 */
@Data
public class UserDTO {

    /**
     * 用户唯一标识
     */
    private Long id;

    /**
     * 用户名（唯一）
     */
    private String username;

    /**
     * 加密后的密码
     */
    private String password;

    /**
     * 用户邮箱（可选）
     */
    private String email;

    /**
     * 用户手机号（可选）
     */
    private String phone;

    /**
     * 最近登录时间
     */
    private LocalDateTime lastLogin;

    /**
     * 用户创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 用户信息更新时间
     */
    private LocalDateTime updatedAt;

}
