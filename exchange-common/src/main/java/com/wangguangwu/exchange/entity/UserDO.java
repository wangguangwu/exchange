package com.wangguangwu.exchange.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 用户表
 * </p>
 *
 * @author wangguangwu
 * @since 2024-11-15
 */
@Getter
@Setter
@TableName("user")
public class UserDO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户唯一标识
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户名（唯一）
     */
    @TableField("username")
    private String username;

    /**
     * 加密后的密码
     */
    @TableField("password")
    private String password;

    /**
     * 用户邮箱（可选）
     */
    @TableField("email")
    private String email;

    /**
     * 用户手机号（可选）
     */
    @TableField("phone")
    private String phone;

    /**
     * 最近登录时间
     */
    @TableField("last_login")
    private LocalDateTime lastLogin;

    /**
     * 软删除标识：0=未删除，1=已删除
     */
    @TableField("is_deleted")
    @TableLogic
    private Boolean isDeleted;

    /**
     * 乐观锁版本号
     */
    @TableField("version")
    @Version
    private Long version;

    /**
     * 用户创建时间
     */
    @TableField("created_at")
    private LocalDateTime createdAt;

    /**
     * 用户信息更新时间
     */
    @TableField("updated_at")
    private LocalDateTime updatedAt;
}
