package com.wangguangwu.exchange.controller;

import com.wangguangwu.exchange.response.Response;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Supplier;

/**
 * 控制器基类
 * 提供模板方法用于统一处理日志记录和异常捕获
 *
 * @author wangguangwu
 */
@Slf4j
public abstract class BaseController {

    /**
     * 模板方法，用于执行带日志记录和异常处理的业务逻辑
     *
     * @param action  业务逻辑
     * @param logInfo 日志描述信息
     * @param <T>     返回值类型
     * @return 包装后的响应对象
     */
    protected <T> Response<T> execute(Supplier<T> action, String logInfo) {
        try {
            log.info("{} - 开始", logInfo);
            T result = action.get();
            log.info("{} - 成功", logInfo);
            return Response.success(result);
        } catch (Exception e) {
            log.error("{} - 失败，错误信息: {}", logInfo, e.getMessage(), e);
            return Response.error(e.getMessage());
        }
    }
}
