package com.wangguangwu.exchange.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 响应 {@link  com.wangguangwu.exchange.response.Response} 结果枚举，用于定义通用的响应状态和信息
 * <p>
 *
 * @author wangguangwu
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum ResponseEnum {

    /**
     * 成功
     */
    SUCCESS(0, "成功"),

    /**
     * 失败
     */
    FAIL(-1, "失败"),

    /**
     * 通用业务异常
     */
    COMMON_SERVICE_EXCEPTION(1000, "通用业务异常"),

    /**
     * 入参校验异常
     */
    METHOD_ARGUMENT_NOT_VALID(1001, "入参校验异常"),

    /**
     * 系统未知异常
     */
    SYSTEM_UNKNOWN(1002, "系统未知异常"),

    /**
     * 用户服务异常
     */
    USER_SERVER_EXCEPTION(1003, "用户服务异常"),

    /**
     * 产品服务异常
     */
    PRODUCT_SERVER_EXCEPTION(1004, "产品服务异常"),

    /**
     * 订单服务异常
     */
    ORDER_SERVER_EXCEPTION(1005, "订单服务异常"),

    /**
     * 资产服务异常
     */
    ASSETS_SERVER_EXCEPTION(1006, "资产服务异常");

    /**
     * 状态码
     */
    private final int code;

    /**
     * 状态信息
     */
    private final String message;

}