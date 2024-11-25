package com.wangguangwu.exchange.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author wangguangwu
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePasswordRequest {

    /**
     * 用户名
     * 必填项，不能为空
     */
    @NotBlank(message = "用户名不能为空")
    private String username;

    /**
     * 当前密码（旧密码）
     * 必填项，不能为空
     */
    @NotBlank(message = "当前密码不能为空")
    private String oldPassword;

    /**
     * 新密码
     * 必填项，不能为空
     */
    @NotBlank(message = "新密码不能为空")
    private String newPassword;

}
