package com.wangguangwu.exchange.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author wangguangwu
 */
@Data
public class UserDTO {

    /**
     * 校验分组接口：创建用户
     */
    public interface Create {}

    /**
     * 校验分组接口：更新用户
     */
    public interface Update {}

    /**
     * 用户唯一标识
     */
    @NotNull(groups = Update.class, message = "更新用户时，用户ID不能为空")
    private Long id;

    /**
     * 用户名（唯一）
     */
    @NotBlank(groups = {Create.class, Update.class}, message = "用户名不能为空")
    @Size(max = 50, groups = {Create.class, Update.class}, message = "用户名长度不能超过50字符")
    private String username;

    /**
     * 加密后的密码
     */
    @NotBlank(groups = Create.class, message = "创建用户时，密码不能为空")
    @Size(min = 8, max = 20, groups = {Create.class, Update.class}, message = "密码长度必须在8到20字符之间")
    private String password;

    /**
     * 用户邮箱（可选）
     */
    @Email(groups = {Create.class, Update.class}, message = "邮箱格式不正确")
    private String email;

    /**
     * 用户手机号（可选）
     */
    private String phone;

    /**
     * 最近登录时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastLogin;

    /**
     * 用户创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    /**
     * 用户信息更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

}
