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
    public Response<String> handleValidationException(MethodArgumentNotValidException ex) {
        // 收集所有字段错误信息
        List<String> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> String.format("[%s]: %s", error.getField(), error.getDefaultMessage()))
                .collect(Collectors.toList());

        // 将错误信息拼接为单个字符串
        String errorDetails = String.join("; ", errors);

        // 返回包含具体错误信息的响应
        return Response.error(String.format("参数校验失败: %s", errorDetails));
    }

    /**
     * 处理其他异常
     *
     * @param ex Exception
     * @return 错误响应
     */
    @ExceptionHandler(Exception.class)
    public Response<String> handleException(Exception ex) {
        return Response.error("系统异常:" + ex.getMessage());
    }
}
