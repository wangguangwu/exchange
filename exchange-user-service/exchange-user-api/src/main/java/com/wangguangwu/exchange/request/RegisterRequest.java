package com.wangguangwu.exchange.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author wangguangwu
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    @NotBlank(message = "用户名不能为空")
    @Size(max = 50, message = "用户名长度不能超过50字符")
    private String username;

    /**
     * 密码
     */
    @NotBlank(message = "创建用户时，密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度必须在6到20字符之间")
    private String password;

}
