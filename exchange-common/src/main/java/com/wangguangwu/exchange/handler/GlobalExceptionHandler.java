package com.wangguangwu.exchange.handler;

import com.wangguangwu.exchange.response.Response;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 全局异常处理
 *
 * @author wangguangwu
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理校验失败的异常
     *
     * @param ex MethodArgumentNotValidException
     * @return 包含详细错误信息的响应
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Response<List<String>> handleValidationException(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> String.format("字段 [%s] 错误: %s", error.getField(), error.getDefaultMessage()))
                .collect(Collectors.toList());
        return Response.error("参数校验失败", errors);
    }

    /**
     * 处理其他异常
     *
     * @param ex Exception
     * @return 错误响应
     */
    @ExceptionHandler(Exception.class)
    public Response<String> handleException(Exception ex) {
        return Response.error("系统异常", ex.getMessage());
    }
}
